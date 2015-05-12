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

import android.os.Bundle;

import java.io.IOException;
import java.net.ContentHandler;
import java.net.URLConnection;

/**
 * An abstract class for building a {@link ContentHandler} that returns cached
 * content if the cached content is fresh, or returns remote content if the
 * cached content is stale.
 */
public abstract class AbstractCachedContentHandler extends ContentHandler {

    /**
     * Returns the current time, indicated by the local system clock.
     */
    private static long now() {
        return System.currentTimeMillis();
    }

    private final long mMaxAge;
    private final Bundle mExtras;

    /**
     * Constructor.
     * 
     * @param maxAge the maximum age for cached content.
     * @param extras the age of the cached content is written to this
     *            {@link Bundle} as {@link FeedExtras#EXTRA_TIMESTAMP}.
     */
    protected AbstractCachedContentHandler(long maxAge, Bundle extras) {
        if (maxAge < 0) {
            throw new IllegalArgumentException("Max age must be non-negative");
        }
        if (extras == null) {
            throw new NullPointerException("Bundle is null");
        }
        mMaxAge = maxAge;
        mExtras = extras;
    }

    /**
     * Returns {@code true} if the requested data is cached locally and meets
     * the requirements specified by the headers of the given
     * {@link URLConnection}, {@code false} otherwise.
     */
    public final boolean isLocal(URLConnection connection) {
        if (mMaxAge == 0L) {
            // Explicitly refresh when the max-age is zero
            // in case the system clock changed.
            return false;
        }
        long timestamp = getTimestamp(connection);
        if (timestamp < 0) {
            // Must refresh when timestamp is not set
            return false;
        }
        mExtras.putLong(FeedExtras.EXTRA_TIMESTAMP, timestamp);
        if (mMaxAge == Long.MAX_VALUE) {
            // Never refresh when max-age is set to maximum value
            return true;
        }
        long age = now() - timestamp;
        if (age < 0) {
            // The clock must have changed, so assume the worst:
            // the cached content might be really really old.
            return false;
        }
        if (age > mMaxAge) {
            return false;
        }
        return true;
    }

    @Override
    public Object getContent(URLConnection connection) throws IOException {
        if (isLocal(connection)) {
            return getLocalContent(connection);
        } else {
            // Record the timestamp before starting the request
            // so the freshness of the content is not over-estimated.
            // Use the local system clock time for consistency.
            long timestamp = now();

            Object content = getRemoteContent(connection);

            // Update the timestamp if no exception is thrown
            setTimestamp(connection, timestamp);
            mExtras.putLong(FeedExtras.EXTRA_TIMESTAMP, timestamp);

            return content;
        }
    }

    /**
     * Gets the timestamp of the cached content.
     *
     * @return the timestamp of the cached content, or {@code -1} if there is no
     *         cached content.
     */
    protected abstract long getTimestamp(URLConnection connection);

    /**
     * Updates the timestamp of the cached content.
     */
    protected abstract void setTimestamp(URLConnection connection, long value);

    /**
     * Gets the content from the cache.
     */
    protected abstract Object getLocalContent(URLConnection connection) throws IOException;

    /**
     * Gets the content from a {@link URLConnection} when the cached data is
     * stale.
     * <p>
     * If this method does not throw an exception, it is assumed that the cache
     * has been updated and {@link #setTimestamp(URLConnection, long)} will be
     * called with the current time.
     * <p>
     * If this method throws, it is assumed that the transaction to update the
     * cache has been rolled-back completely and that the previously cached
     * content still exists; the timestamp for the existing cache content will
     * not be cleared.
     */
    protected abstract Object getRemoteContent(URLConnection connection) throws IOException;
}
