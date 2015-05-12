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

package com.google.android.feeds;

import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeaderValueParser;
import org.apache.http.message.HeaderValueParser;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ContentHandler;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Utility methods for {@link ContentHandler} implementations.
 */
public final class ContentHandlerUtils {

    private static final int DEFAULT_BUFFER_SIZE = 4096;

    /**
     * Returns the character set of the content provided by the given
     * {@link URLConnection}.
     *
     * @throws IOException if the character set cannot be determined.
     */
    public static String getCharSet(URLConnection connection) throws IOException {
        String contentType = connection.getContentType();
        if (contentType != null) {
            HeaderValueParser parser = new BasicHeaderValueParser();
            HeaderElement[] values = BasicHeaderValueParser.parseElements(contentType, parser);
            if (values.length > 0) {
                NameValuePair param = values[0].getParameterByName("charset");
                if (param != null) {
                    return param.getValue();
                }
            }
        }
        if (connection instanceof HttpURLConnection) {
            return HTTP.DEFAULT_CONTENT_CHARSET;
        } else {
            throw new IOException("Unabled to determine character encoding");
        }
    }

    /**
     * Returns the uncompressed {@link InputStream} for the given
     * {@link URLConnection}.
     */
    public static InputStream getUncompressedInputStream(URLConnection connection)
            throws IOException {
        InputStream source = connection.getInputStream();
        String encoding = connection.getContentEncoding();
        if ("gzip".equalsIgnoreCase(encoding)) {
            return new GZIPInputStream(source);
        } else if ("deflate".equalsIgnoreCase(encoding)) {
            boolean noHeader = true;
            Inflater inflater = new Inflater(noHeader);
            return new InflaterInputStream(source, inflater);
        } else {
            return source;
        }
    }

    /**
     * Returns the body of a {@link URLConnection} as a {@link String}.
     */
    public static String toString(URLConnection connection) throws IOException {
        if (connection == null) {
            throw new IllegalArgumentException("URLConnection is null");
        }
        int contentLength = connection.getContentLength();
        if (contentLength < 0) {
            contentLength = DEFAULT_BUFFER_SIZE;
        }
        String charset = getCharSet(connection);
        InputStream input = getUncompressedInputStream(connection);
        try {
            InputStreamReader reader = new InputStreamReader(input, charset);
            StringBuilder builder = new StringBuilder(contentLength);
            char[] buffer = new char[1024];
            for (int n = reader.read(buffer); n != -1; n = reader.read(buffer)) {
                builder.append(buffer, 0, n);
            }
            return builder.toString();
        } finally {
            input.close();
        }
    }

    private ContentHandlerUtils() {
    }
}
