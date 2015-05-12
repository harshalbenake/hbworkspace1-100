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

import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.CrossProcessCursor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

/**
 * Creates {@link Cursor} wrappers that include error information and other
 * meta-data.
 */
public final class FeedProvider {

    /**
     * Ensures that the query has been evaluated.
     * <p>
     * Some versions of the Android platform do not evaluate the query until the
     * {@link Cursor} is used. This method helps ensure that the query is
     * evaluated on a background thread and not the main thread.
     */
    private static Cursor evaluate(Cursor cursor) {
        if (cursor != null) {
            cursor.getCount();
        }
        return cursor;
    }

    private static AnnotatedCursor createAnnotatedCursor(Cursor cursor, Bundle extras) {
        if (cursor instanceof CrossProcessCursor) {
            CrossProcessCursor crossProcessCursor = (CrossProcessCursor) cursor;
            return new AnnotatedCrossProcessCursor(crossProcessCursor, extras);
        } else if (cursor != null) {
            return new AnnotatedCursor(cursor, extras);
        } else {
            return null;
        }
    }

    /**
     * Creates a cursor with meta-data.
     * 
     * @param cursor the {@link Cursor} to wrap.
     * @param extras meta-data, such as {@link FeedExtras#EXTRA_MORE} or
     *            {@link FeedExtras#EXTRA_TIMESTAMP}.
     * @return the wrapped cursor.
     */
    public static Cursor feedCursor(Cursor cursor, Bundle extras) {
        return createAnnotatedCursor(evaluate(cursor), extras);
    }

    /**
     * Creates a cursor with error data.
     * 
     * @param cursor the cursor to wrap. When there is an error, the provider
     *            should generally return any data that is cached locally. For
     *            example, if the first 10 items are available locally, it is
     *            usually best to show them to the user even if the next 10
     *            items are unavailable.
     * @param extras meta-data, such as {@link FeedExtras#EXTRA_MORE}. The flag
     *            {@link FeedExtras#EXTRA_MORE} should be set if there are more
     *            items, even if there was an error so that the UI can retry the
     *            operation to load more items after the network connection has
     *            been restored.
     * @param t the exception that was thrown. The calling code should catch
     *            {@link Throwable} in
     *            {@link ContentProvider#query(Uri, String[], String, String[], String)}
     *            otherwise
     *            {@link ContentResolver#query(Uri, String[], String, String[], String)}
     *            will return {@code null} by default.
     * @param solution an {@link Intent} that starts an {@link Activity} to
     *            address the error (for example, opens the device network
     *            settings or a Wi-Fi hotspot login page), or {@code null}.
     * @return the wrapped cursor.
     * @see FeedExtras#EXTRA_ERROR
     * @see FeedExtras#EXTRA_SOLUTION
     */
    public static Cursor errorCursor(Cursor cursor, Bundle extras, Throwable t, Intent solution) {
        if (extras == null) {
            throw new NullPointerException("Bundle is null");
        }
        if (t == null) {
            throw new NullPointerException("Throwable is null");
        }
        extras.putSerializable(FeedExtras.EXTRA_ERROR, t);
        if (solution != null) {
            extras.putParcelable(FeedExtras.EXTRA_SOLUTION, solution);
        }
        return feedCursor(cursor, extras);
    }

    private FeedProvider() {
    }
}
