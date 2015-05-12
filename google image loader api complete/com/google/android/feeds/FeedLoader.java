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

import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import java.io.IOException;
import java.net.ContentHandler;
import java.net.URL;
import java.net.URLConnection;

/**
 * Loads feed content from one or more documents to satisfy a query.
 * <p>
 * For example, loads {@code http://example.com/feed?page=1},
 * {@code http://example.com/feed?page=2}, and
 * {@code http://example.com/feed?page=3} to get the first three pages of items
 * in a feed.
 * <p>
 * Parsers must implement the {@link ContentHandler} interface and return
 * information about the document (see method documentation for details).
 * <p>
 * The feed data is typically stored in a {@link MatrixCursor} or a
 * {@link SQLiteDatabase} passed to the {@link ContentHandler} constructor.
 */
public class FeedLoader {
    private static class DocumentInfo {

        private final int mItemCount;

        private final String mContinuationToken;

        public DocumentInfo() {
            mItemCount = -1;
            mContinuationToken = null;
        }

        public DocumentInfo(int itemCount) {
            mItemCount = itemCount;
            mContinuationToken = null;
        }

        public DocumentInfo(int itemCount, String continuationToken) {
            mItemCount = itemCount;
            mContinuationToken = continuationToken;
        }

        public int itemCount() {
            return mItemCount;
        }

        public String continuationToken() {
            return mContinuationToken;
        }
    }

    private static DocumentInfo loadDocument(ContentHandler handler, Uri uri) throws IOException {
        String spec = uri.toString();
        URL url = new URL(spec);
        URLConnection connection = url.openConnection();
        Object content = handler.getContent(connection);
        if (content instanceof DocumentInfo) {
            return (DocumentInfo) content;
        } else {
            String message = "ContentHandler must return FeedLoader.documentInfo(...)";
            throw new RuntimeException(message);
        }
    }

    /**
     * Loads a feed that is contained in a single document.
     *
     * @param handler a {@link ContentHandler} that parses a document and
     *            returns {@link FeedLoader#documentInfo()} or
     *            {@link FeedLoader#documentInfo(int)}. The feed data is
     *            typically stored in a {@link MatrixCursor} or a
     *            {@link SQLiteDatabase} passed to the {@link ContentHandler}
     *            constructor.
     * @param documentUri the URI of the document.
     * @throws IOException if there is an error loading one of the documents.
     */
    public static void loadFeed(ContentHandler handler, Uri documentUri) throws IOException {
        loadDocument(handler, documentUri);
    }

    /**
     * Loads a feed that is spread across multiple pages, where a page is
     * selected by specifying the index of the first element that should appear
     * on the page. For example:
     * <ul>
     * <li>{@code http://example.com/feed}</li>
     * <li>{@code http://example.com/feed?start=10}</li>
     * <li>{@code http://example.com/feed?start=20}</li>
     * <li>...</li>
     * </ul>
     *
     * @param handler a {@link ContentHandler} that parses each document and
     *            returns {@link FeedLoader#documentInfo(int)}. The feed data is
     *            typically stored in a {@link MatrixCursor} or a
     *            {@link SQLiteDatabase} passed to the {@link ContentHandler}
     *            constructor.
     * @param baseDocumentUri the base document URI.
     * @param indexParameter the query parameter to specify the index of the
     *            first desired item.
     * @param firstIndex the index of the first item in the feed (usually
     *            {@code 0} or {@code 1})
     * @param pageSize the expected number of items per page. If the received
     *            number of items is less than this value, the implementation
     *            will assume that there are no more items.
     * @param itemCount the target item count for the output. The actual number
     *            of items in the output may be more or less, depending on
     *            availability.
     * @param extras a {@link Bundle} to store meta-data like
     *            {@link FeedExtras#EXTRA_MORE}.
     * @throws IOException if there is an error loading one of the documents.
     */
    public static void loadIndexedFeed(ContentHandler handler, Uri baseDocumentUri,
            String indexParameter, int firstIndex, int pageSize, int itemCount, Bundle extras)
            throws IOException {
        int totalCount = 0;
        int index = firstIndex;
        boolean hasMore;
        do {
            // Get the rows for the current page
            Uri.Builder documentUri = baseDocumentUri.buildUpon();
            documentUri.appendQueryParameter(indexParameter, Integer.toString(index));
            DocumentInfo document = loadDocument(handler, documentUri.build());

            int documentItemCount = document.itemCount();
            if (documentItemCount < 0) {
                throw new RuntimeException("Invalid document info: item count is unset or invalid");
            }

            // If the page is full, there are probably more pages
            hasMore = documentItemCount >= pageSize;
            extras.putBoolean(FeedExtras.EXTRA_MORE, hasMore);

            // Update the current index
            index += documentItemCount;
            totalCount += documentItemCount;
        } while (totalCount < itemCount && hasMore);
    }

    /**
     * Loads a feed that is spread across multiple pages, where a page is
     * selected by specifying the page number. For example:
     * <ul>
     * <li>{@code http://example.com/feed}</li>
     * <li>{@code http://example.com/feed?page=2}</li>
     * <li>{@code http://example.com/feed?page=3}</li>
     * <li>...</li>
     * </ul>
     *
     * @param handler a {@link ContentHandler} that parses each document and
     *            returns {@link FeedLoader#documentInfo(int)}. The feed data is
     *            typically stored in a {@link MatrixCursor} or a
     *            {@link SQLiteDatabase} passed to the {@link ContentHandler}
     *            constructor.
     * @param baseDocumentUri the base document URI.
     * @param pageParameter the name of the query parameter that specifies the
     *            desired page number.
     * @param firstPage the number of the first page (usually {@code 1}, but may
     *            be {@code 0}).
     * @param pageSize the number of items on each page. If the number of rows
     *            found on a page is less than this number, it will be assumed
     *            that there are no more pages.
     * @param itemCount the target item count for the output. The actual number
     *            of items in the output may be more or less, depending on
     *            availability.
     * @param extras a {@link Bundle} to store meta-data like
     *            {@link FeedExtras#EXTRA_MORE}.
     * @throws IOException if there is an error loading one of the documents.
     */
    public static void loadPagedFeed(ContentHandler handler, Uri baseDocumentUri,
            String pageParameter, int firstPage, int pageSize, int itemCount, Bundle extras)
            throws IOException {
        int totalCount = 0;
        int page = firstPage;

        boolean morePages;
        do {
            // Get the rows for the current page
            Uri.Builder documentUri = baseDocumentUri.buildUpon();
            documentUri.appendQueryParameter(pageParameter, Integer.toString(page));
            DocumentInfo document = loadDocument(handler, documentUri.build());

            int documentItemCount = document.itemCount();
            if (documentItemCount < 0) {
                throw new RuntimeException("Invalid document info: item count is unset or invalid");
            }

            // If the page is full, there are probably more pages
            morePages = documentItemCount >= pageSize;
            extras.putBoolean(FeedExtras.EXTRA_MORE, morePages);

            // Set the next page number
            page += 1;
            totalCount += documentItemCount;
        } while (totalCount < itemCount && morePages);
    }

    /**
     * Loads a feed using continuation tokens. For example:
     * <ul>
     * <li>{@code http://example.com/feed} (response contains token a)</li>
     * <li>{@code http://example.com/feed?continuation=a} (response contains token b)</li>
     * <li>{@code http://example.com/feed?continuation=b} (response has no continuation token)</li>
     * </ul>
     *
     * @param handler a {@link ContentHandler} that parses each document and
     *            returns {@link FeedLoader#documentInfo(int, String)}.
     * @param baseDocumentUri the base URI for documents.
     * @param continuationParameter the URI query parameter to specify the
     *            continuation token.
     * @param itemCount the target item count for the output. The actual number
     *            of items in the output may be more or less, depending on
     *            availability.
     * @param extras a {@link Bundle} to store meta-data like
     *            {@link FeedExtras#EXTRA_MORE}.
     * @throws IOException if there is an error loading one of the documents.
     */
    public static void loadContinuedFeed(ContentHandler handler, Uri baseDocumentUri,
            String continuationParameter, int itemCount, Bundle extras) throws IOException {
        int totalCount = 0;
        String continuation = null;
        do {
            Uri.Builder documentUri = baseDocumentUri.buildUpon();
            if (continuation != null) {
                documentUri.appendQueryParameter(continuationParameter, continuation);
            }
            DocumentInfo document = loadDocument(handler, documentUri.build());

            continuation = document.continuationToken();
            extras.putBoolean(FeedExtras.EXTRA_MORE, continuation != null);

            int documentItemCount = document.itemCount();
            if (documentItemCount < 0) {
                throw new RuntimeException("Invalid document info: item count is unset or invalid");
            }
            totalCount += documentItemCount;
        } while (totalCount < itemCount && continuation != null);
    }

    /**
     * Returns an empty document info object.
     * <p>
     * This value may be returned by a {@link ContentHandler} when the number of
     * items is unknown or when the number of items is unnecessary and expensive
     * to compute. Otherwise {@link #documentInfo(int)} should be returned
     * instead.
     */
    public static Object documentInfo() {
        return new DocumentInfo();
    }

    /**
     * Returns a document info object that specifies an item count.
     * <p>
     * This type of document info object should be returned by
     * {@link ContentHandler ContentHandlers} passed to
     * {@link #loadFeed(ContentHandler, Uri)},
     * {@link #loadIndexedFeed(ContentHandler, Uri, String, int, int, int, Bundle)}
     * , and
     * {@link #loadPagedFeed(ContentHandler, Uri, String, int, int, int, Bundle)}
     * .
     *
     * @param itemCount the number of rows added to a {@link MatrixCursor} or
     *            {@link SQLiteDatabase} by the last call to
     *            {@link ContentHandler#getContent(URLConnection)}. Note that
     *            {@link ContentHandler#getContent(URLConnection)} can be called
     *            multiple times, so any counters should be reset at the
     *            beginning of each call.
     * @return an object describing the document.
     */
    public static Object documentInfo(int itemCount) {
        return new DocumentInfo(itemCount);
    }

    /**
     * Returns a document info object that specifies an item count and a
     * continuation token.
     * <p>
     * This type of document info object should be returned by
     * {@link ContentHandler ContentHandlers} passed to
     * {@link #loadContinuedFeed(ContentHandler, Uri, String, int, Bundle)}.
     *
     * @param itemCount the number of rows added to a {@link MatrixCursor} or
     *            {@link SQLiteDatabase} by the last call to
     *            {@link ContentHandler#getContent(URLConnection)}. Note that
     *            {@link ContentHandler#getContent(URLConnection)} can be called
     *            multiple times, so any counters should be reset at the
     *            beginning of each call.
     * @param continuationToken the continuation token, or {@code null} if there
     *            are no more items.
     * @return an object describing the document.
     */
    public static Object documentInfo(int itemCount, String continuationToken) {
        return new DocumentInfo(itemCount, continuationToken);
    }

    private FeedLoader() {
    }
}
