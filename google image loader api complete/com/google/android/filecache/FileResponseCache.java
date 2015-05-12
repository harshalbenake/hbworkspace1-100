/*-
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.filecache;

import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.ContentHandler;
import java.net.HttpURLConnection;
import java.net.ResponseCache;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * File-based implementation of {@link ResponseCache}.
 * <p>
 * Usage:
 *
 * <pre>
 * // Step 1: Implement cache
 * class MyResponseCache extends FileResponseCache { ... }
 *
 * // Step 2: Install cache
 * ResponseCache.setDefault(new MyResponseCache(...));
 *
 * // Step 3: Use cache
 * ContentHandler handler = new MyParser(...);
 * handler = MyResponseCache.capture(handler, cookie);
 * handler.getContent(connection);
 * </pre>
 * <p>
 * Note: Due to a bug in the Android platform,
 * {@link HttpURLConnection#getResponseCode()} will return {@code -1} and
 * {@link HttpURLConnection#getResponseMessage()} will return {@code null} when
 * the response is cached, but you can get the status line by calling
 * {@link URLConnection#getHeaderField(String)} with the value {@code "status"}.
 * <p>
 * Please see <a
 * href="http://java.sun.com/j2se/1.5.0/docs/guide/net/http-cache.html">
 * http://java.sun.com/j2se/1.5.0/docs/guide/net/http-cache.html </a> for more
 * information about the {@link ResponseCache} API.
 */
public abstract class FileResponseCache extends ResponseCache {

    private static final String TAG = "FileResponseCache";

    private static final String MAX_AGE_PREFIX = "max-age=";

    /**
     * Logs an error message about a file.
     * <p>
     * The file name (which may contain personal information) is only logged if
     * the log level is set to {@link Log#DEBUG}.
     */
    private static void logFileError(String message, File file) {
        if (Log.isLoggable(TAG, Log.ERROR)) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                String path = file.getAbsolutePath();
                message += ": " + path;
            }
            Log.e(TAG, message);
        }
    }

    /**
     * Returns the response code, even if the response is cached.
     * <p>
     * Calling {@link HttpURLConnection#getResponseCode()} directly when the
     * response is cached will always return {@code -1}.
     */
    public static int getResponseCode(URLConnection connection) throws IOException {
        HttpURLConnection http = (HttpURLConnection) connection;
        int responseCode = http.getResponseCode();
        if (responseCode != -1) {
            return responseCode;
        } else {
            String response = connection.getHeaderField("status");
            if (response == null) {
                return -1;
            }
            response = response.trim();
            int index = response.indexOf(" ");
            if (index == -1) {
                return -1;
            }
            try {
                return Integer.parseInt(response.substring(0, index));
            } catch (NumberFormatException e) {
                return -1;
            }
        }
    }

    /**
     * Returns the response message, even if the response is cached.
     * <p>
     * Calling {@link HttpURLConnection#getResponseMessage()} directly when the
     * response is cached will always return {@code null}.
     */
    public static String getResponseMessage(URLConnection connection) throws IOException {
        HttpURLConnection http = (HttpURLConnection) connection;
        int responseCode = http.getResponseCode();
        if (responseCode != -1) {
            return http.getResponseMessage();
        } else {
            String response = connection.getHeaderField("status");
            if (response == null) {
                return null;
            }
            response = response.trim();
            int index = response.indexOf(" ");
            if (index != -1) {
                return response.substring(index + 1);
            } else {
                return null;
            }
        }
    }

    /**
     * Stores meta-information for each caching call to
     * {@link ContentHandler#getContent(URLConnection)}. This is necessary to
     * pass information between methods like {@link #get(URI, String, Map)} and
     * {@link #put(URI, URLConnection)} in order to implement work-arounds for
     * platform bugs.
     * <p>
     * The implementation assumes that a {@link URLConnection} is only accessed
     * by a single thread, and that nested calls to
     * {@link ContentHandler#getContent(URLConnection)} do not access the
     * {@link URLConnection} owned by the enclosing {@link ContentHandler}.
     */
    private final ThreadLocal<Stack<Frame>> mStack = new ThreadLocal<Stack<Frame>>();

    protected FileResponseCache() {
    }

    /**
     * Returns {@code true} if the given cache {@link File} is too stale to
     * satisfy the given request parameters.
     * <p>
     * The current implementation only considers the {@code max-age} value
     * specified in the {@code cache-control} header.
     *
     * @param file the cache file.
     * @param uri the request {@link URI}
     * @param requestMethod the HTTP request method.
     * @param requestHeaders the HTTP request headers. Keys should be specified
     *            in lower-case.
     * @return {@code true} if the given cache {@link File} is too stale to
     *         satisfy the given request parameters, {@code false} otherwise.
     */
    protected boolean isStale(File file, URI uri, String requestMethod,
            Map<String, List<String>> requestHeaders, Object cookie) {
        // TODO: Implement a more robust Cache-Control parser
        List<String> values = requestHeaders.get("cache-control");
        if (values != null) {
            for (String value : values) {
                if (value.startsWith(MAX_AGE_PREFIX)) {
                    try {
                        int start = MAX_AGE_PREFIX.length();
                        long maxAge = Long.parseLong(value.substring(start));
                        if (maxAge == 0L) {
                            return true;
                        }
                        long ageInMillis = System.currentTimeMillis() - file.lastModified();
                        long ageInSeconds = ageInMillis / 1000L;
                        if (ageInSeconds > maxAge) {
                            return true;
                        }
                    } catch (NumberFormatException e) {
                        if (Log.isLoggable(TAG, Log.ERROR)) {
                            Log.e(TAG, "Failed to parse Cache-Control: " + value, e);
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if the given {@link URLConnection} is cacheable,
     * {@code false} otherwise.
     * <p>
     * The default implementation returns {@code true} if and only if the
     * request method is {@code HTTP GET} and the response code is {@code HTTP
     * 200 OK}. Subclasses may override this method to implement different
     * behavior. For example, some applications may cache certain {@code 206
     * Partial Content} or {@code HTTP POST} responses.
     *
     * @param connection the {@link URLConnection} to be cached.
     * @return {@code true} if the given {@link URLConnection} is cacheable,
     *         {@code false} otherwise.
     * @throws IOException
     */
    protected boolean isCacheable(URLConnection connection) throws IOException {
        if (connection instanceof HttpURLConnection) {
            HttpURLConnection http = (HttpURLConnection) connection;
            String requestMethod = http.getRequestMethod();
            int responseCode = http.getResponseCode();
            return "GET".equals(requestMethod) && HttpURLConnection.HTTP_OK == responseCode;
        } else {
            return false;
        }
    }

    /**
     * Returns the {@link File} used for caching the given request, or {@code
     * null} if this type of request should not be cached.
     * <p>
     * It is not necessary to create the parent directories; this will be done
     * automatically before the file is written.
     *
     * @param uri the {@link URI} of the request.
     * @param requestMethod the HTTP request method.
     * @param requestHeaders the HTTP request headers.
     *            <p>
     *            Due to a bug in the platform, the current implementation is
     *            limited to one value per key.
     *            <p>
     *            Keys should be specified in lower-case.
     * @param cookie the cookie passed to
     *            {@link #getFile(URI, String, Map, Object)} or {@code null} if
     *            none was passed.
     * @return the {@link File} in which to cache the given request, or {@code
     *         null} to not cache this request.
     */
    protected abstract File getFile(URI uri, String requestMethod,
            Map<String, List<String>> requestHeaders, Object cookie);

    private File getFile(Frame frame) {
        try {
            HttpURLConnection http = (HttpURLConnection) frame.getConnection();
            String requestMethod = http.getRequestMethod();

            URL url = http.getURL();
            URI uri = url.toURI();

            // URLConnection#getRequestProperties() is not readable after the
            // connection has been opened so pass a wrapper class that returns
            // values from URLConnection#getRequestProperty(String)
            // (which is still readable after the connection has been opened).
            Map<String, List<String>> requestHeaders = new RequestPropertiesMap(http);

            Object cookie = frame.getCookie();

            return getFile(uri, requestMethod, requestHeaders, cookie);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Please see {@link ResponseCache#get(URI, String, Map)}.
     *
     * @throws IOException
     */
    @Override
    public CacheResponse get(URI uri, String requestMethod, Map<String, List<String>> requestHeaders)
            throws IOException {
        Stack<Frame> stack = mStack.get();
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        Frame frame = stack.peek();
        URLConnection connection = frame.getConnection();
        // There is a bug in Android's HttpURLConnection implementation
        // where the response headers are passed to this method instead of
        // the request headers.
        // Pass a RequestPropertiesMap instead of passing
        // URLConnection#getRequestProperies() to be consistent
        // with the values passed to getFile(...) from put(...)
        // (i.e., case-insensitive, max one value per key).
        requestHeaders = new RequestPropertiesMap(connection);
        Object cookie = frame.getCookie();
        File file = getFile(uri, requestMethod, requestHeaders, cookie);
        if (file != null && file.exists()
                && !isStale(file, uri, requestMethod, requestHeaders, cookie)) {
            return createCacheResponse(file);
        } else {
            return null;
        }
    }

    /**
     * Please see {@link ResponseCache#put(URI, URLConnection)}.
     */
    @Override
    public CacheRequest put(URI uri, URLConnection connection) throws IOException {
        if (!isCacheable(connection)) {
            return null;
        }
        Stack<Frame> stack = mStack.get();
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        Frame frame = stack.peek();
        // The Android implementation of HttpURLConnection
        // passes an incorrect URI to this method in some cases,
        // so let getFile(URLConnection) calculate the
        // correct value from URLConnection#getURL().
        File file = getFile(frame);
        if (file != null) {
            File parent = file.getParentFile();
            if (parent == null) {
                logFileError("File has no parent directory", file);
                return null;
            }
            if (!parent.exists() && !parent.mkdirs()) {
                logFileError("Unable to create parent directory", parent);
                return null;
            }
            if (parent.exists() && !parent.isDirectory()) {
                logFileError("Parent is not a directory", parent);
                return null;
            }
            if (file.exists() && file.isDirectory()) {
                logFileError("Destination file is a directory", file);
                return null;
            }

            CacheRequest cacheRequest = createCacheRequest(file, connection);
            frame.setCacheRequest(cacheRequest);
            // Disable CacheRequest#abort() because it is called by
            // the HttpURLConnection implementation when it should not be.
            // FileResponseCacheContentHandler is responsible for
            // aborting the CacheRequest instead.
            return new UnabortableCacheRequest(cacheRequest);
        } else {
            return null;
        }
    }

    /**
     * Creates a {@link CacheResponse} to be returned by
     * {@link #get(URI, String, Map)} for the given {@link File}.
     * <p>
     * The implementation should ensure that all keys in the map returned by
     * {@link CacheResponse#getHeaders()} are lower-case and should remove the
     * {@code Transfer-Encoding} header if it has not already removed by the
     * {@link CacheRequest} because the content written to
     * {@link CacheRequest#getBody()} is not encoded.
     *
     * @param file the file containing the cached response.
     * @return the {@link CacheResponse}.
     */
    private CacheResponse createCacheResponse(File file) {
        return new FileCacheResponse(file);
    }

    /**
     * Creates a {@link CacheRequest} to be returned by
     * {@link #put(URI, URLConnection)}.
     * <p>
     * The implementation should capture the response code and response message
     * for {@link HttpURLConnection HttpURLConnections} in a {@code Status}
     * header for completeness. The {@link CacheRequest} should remove the
     * {@code Transfer-Encoding} header if it is not removed by the
     * {@link CacheResponse} because the content written to
     * {@link CacheRequest#getBody()} is not encoded. Finally,
     * {@link CacheRequest#getBody()} may be called multiple times and it should
     * always return the same {@link OutputStream} instance.
     *
     * @param file the file in which to save the cached response.
     * @param connection the {@link URLConnection} to be cached.
     * @return the {@link CacheRequest}.
     * @throws IOException if there is a problem reading the
     *             {@link URLConnection}.
     */
    private CacheRequest createCacheRequest(File file, URLConnection connection)
            throws IOException {
        int responseCode = -1;
        String responseMessage = null;
        if (connection instanceof HttpURLConnection) {
            HttpURLConnection http = (HttpURLConnection) connection;
            responseCode = http.getResponseCode();
            responseMessage = http.getResponseMessage();
        } else if (Log.isLoggable(TAG, Log.WARN)) {
            Class<? extends URLConnection> type = connection.getClass();
            String className = type.getName();
            Log.w(TAG, "URLConnection is not an HttpURLConnection: " + className);
        }
        Map<String, List<String>> responseHeaders = connection.getHeaderFields();
        return new FileCacheRequest(file, responseCode, responseMessage, responseHeaders);
    }

    /**
     * Creates a {@link ContentHandler} wrapper to capture a
     * {@link URLConnection} using the currently installed
     * {@link FileResponseCache}.
     * <p>
     * A wrapper is required to work-around platform bugs and to guard against
     * cache corruption. It also provides a nice mechanism for passing
     * parameters to the cache on a per-resource basis.
     * <p>
     * Usage:
     *
     * <pre>
     * // Parses binary content from a URLConnection
     * class MyContentHandler extends java.net.ContentHandler { ... }
     *
     * // Parses binary content from a URL with caching
     * Object getContent(URL url) {
     *     ContentHandler handler = new MyContentHandler(...);
     *     handler = ResponseCache.capture(handler, cookie);
     *
     *     URLConnection connection = url.openConnection();
     *     return handler.getContent(connection);
     * }
     * </pre>
     * <p>
     * The wrapper ensures that the correct values are passed to
     * {@link #getFile(URI, String, Map, Object)}. Without the wrapper, the
     * {@link URI} and headers passed to
     * {@link #getFile(URI, String, Map, Object)} may be incorrect due to
     * platform bugs.
     * <p>
     * The wrapper will also delete the cache file associated with a
     * {@link URLConnection} passed to
     * {@link ContentHandler#getContent(URLConnection)} if any type of exception
     * is thrown.
     * <p>
     * The wrapper specifies the cookie value passed to
     * {@link #getFile(URI, String, Map, Object)}.
     * <p>
     * In general, the cookie contains information about a request that is
     * required to calculate the cache file location. This is especially
     * important when the information is difficult to extract from the
     * {@link URI} and headers passed to
     * {@link #getFile(URI, String, Map, Object)}.
     * <p>
     * Frequently, the cookie is an account identifier so that cached responses
     * are grouped by account. Grouping cached files by account makes it easy to
     * remove the cached content associated with an account when an account is
     * removed from the system. It is also common for a single {@link URL} to
     * provide different content depending on the account that was used to
     * access it, and separating the cached content ensures that one account
     * does not overwrite the content of another.
     * <p>
     * The cookie could also just be the {@link File} that should be returned by
     * {@link #getFile(URI, String, Map, Object)}.
     * <p>
     * <strong>Implementation Notes: </strong>It is important that the
     * {@link URLConnection} is only accessed by a single thread (the one that
     * calls {@link ContentHandler#getContent(URLConnection)}). Nesting is
     * allowed (i.e., caching a different {@link URLConnection} from within a
     * call to {@link ContentHandler#getContent(URLConnection)}), however the
     * {@link URLConnection} owned by the enclosing {@link ContentHandler} must
     * not be accessed while the nested request is in progress.
     *
     * @param handler a {@link ContentHandler}.
     * @param cookie a cookie to pass to
     *            {@link #getFile(URI, String, Map, Object)} or {@code null}.
     * @return a {@link ContentHandler} wrapper or the original
     *         {@link ContentHandler} if a {@link FileResponseCache} has not
     *         been installed.
     * @throws IllegalStateException if a {@link FileResponseCache} has not been
     *             installed using
     *             {@link ResponseCache#setDefault(ResponseCache)}.
     */
    public static ContentHandler capture(ContentHandler handler, Object cookie) {
        ResponseCache responseCache = ResponseCache.getDefault();
        if (responseCache instanceof FileResponseCache) {
            FileResponseCache fileResponseCache = (FileResponseCache) responseCache;
            return new FileResponseCacheContentHandler(handler, fileResponseCache, cookie);
        } else if (responseCache == null) {
            throw new IllegalStateException("ResponseCache not found");
        } else {
            Class<? extends ResponseCache> type = responseCache.getClass();
            String message = "Installed ResponseCache is not a FileResponseCache: " + type;
            throw new IllegalStateException(message);
        }
    }

    /**
     * Creates a {@link ContentHandler} that consumes the content of a
     * {@link URLConnection} so that the response data will be captured by a
     * {@link ResponseCache}.
     * <p>
     * The returned {@link ContentHandler} must be wrapped with
     * {@link #capture(ContentHandler, Object)} to enable caching.
     * <p>
     * The bytes of the input stream are not interpreted, which saves CPU
     * resources and makes this method ideal for pre-fetching data. The method
     * {@link ContentHandler#getContent(URLConnection)} will always return
     * {@code null}.
     * <p>
     * If the {@link URLConnection} is already being served from the local
     * cache, this {@link ContentHandler} does nothing.
     */
    public static ContentHandler sink() {
        return new SinkContentHandler();
    }

    private static class FileResponseCacheContentHandler extends ContentHandler {
        private final ContentHandler mContentHandler;

        private final Object mCookie;

        private final FileResponseCache mFileResponseCache;

        public FileResponseCacheContentHandler(ContentHandler contentHandler,
                FileResponseCache fileResponseCache, Object cookie) {
            mContentHandler = contentHandler;
            mFileResponseCache = fileResponseCache;
            mCookie = cookie;
        }

        @Override
        public Object getContent(URLConnection connection) throws IOException {
            if (connection == null) {
                throw new NullPointerException();
            }
            Frame frame = new Frame(connection, mCookie);
            Stack<Frame> stack = mFileResponseCache.mStack.get();
            if (stack == null) {
                stack = new Stack<Frame>();
                mFileResponseCache.mStack.set(stack);
            }
            stack.push(frame);
            try {
                Object content = mContentHandler.getContent(connection);
                frame.close();
                return content;
            } catch (IOException e) {
                frame.abort();
                throw e;
            } catch (RuntimeException e) {
                frame.abort();
                throw e;
            } catch (Error e) {
                frame.abort();
                throw e;
            } finally {
                stack.pop();
            }
        }
    }

    /**
     * Store information about a connection for the duration of
     * {@link ContentHandler#getContent(URLConnection)}.
     * <p>
     * This data structure may be placed in a stack so that nested calls to
     * {@link ContentHandler#getContent(URLConnection)} can be made.
     * <p>
     * For example, if an XML parser synchronously loaded images referenced by
     * the XML.
     */
    private static class Frame {

        private static final int SDK = Integer.parseInt(Build.VERSION.SDK);

        private static final int GINGERBREAD = 9;

        private final URLConnection mConnection;

        private final Object mCookie;

        private CacheRequest mCacheRequest;

        public Frame(URLConnection connection, Object cookie) {
            if (connection == null) {
                throw new NullPointerException();
            }
            mConnection = connection;
            mCookie = cookie;
        }

        public void setCacheRequest(CacheRequest cacheRequest) {
        	if (cacheRequest == null) {
        	  throw new NullPointerException();
        	}
            mCacheRequest = cacheRequest;
        }

        public void close() throws IOException {
            if (SDK < GINGERBREAD) {
                if (mCacheRequest != null) {
                    OutputStream output = mCacheRequest.getBody();
                    output.close();
                }
            } else {
                // The platform will close the output stream
            }
        }

        public void abort() {
            if (mCacheRequest != null) {
                mCacheRequest.abort();
            }
        }

        public URLConnection getConnection() {
            return mConnection;
        }

        public Object getCookie() {
            return mCookie;
        }
    }

    /**
     * A {@link CacheRequest} wrapper that drops all calls to
     * {@link CacheRequest#abort()}.
     */
    private static class UnabortableCacheRequest extends CacheRequest {
        private final CacheRequest mCacheRequest;

        public UnabortableCacheRequest(CacheRequest cacheRequest) {
            if (cacheRequest == null) {
                throw new NullPointerException();
            }
            mCacheRequest = cacheRequest;
        }

        @Override
        public OutputStream getBody() throws IOException {
            return mCacheRequest.getBody();
        }

        @Override
        public void abort() {
            // Drop all calls to abort() from the platform
            // because sometimes abort() is called even
            // when the request is successful.
            // The FileResponseCacheContentHandler will call abort()
            // on mCacheRequest directly when appropriate.
        }
    }
}