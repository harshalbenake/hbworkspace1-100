/*-
 * Copyright (C) 2011 Google Inc.
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

package com.google.android.callable;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import java.util.List;

/**
 * Implements
 * {@link android.content.ContentProvider#call(String, String, Bundle)} on
 * devices running API Level 10 and lower.
 * <p>
 * Use this mechanism when an application needs interprocess communication.
 */
public final class CallableProvider {

    private static final String PATH = "call";

    private CallableProvider() {
    }

    /**
     * Constructs a query URI that will invoke a call.
     */
    private static Uri uri(Uri baseUri, String method, String arg) {
        return baseUri.buildUpon().path(PATH).appendPath(method).appendPath(arg).build();
    }

    /**
     * Calls {@link CallableContentProvider#call(String, String, Bundle)} via
     * {@link Cursor#respond(Bundle)} on pre-Honeycomb devices.
     * <p>
     * The {@link ContentProvider} must implement
     * {@link CallableContentProvider} and use
     * {@link #query(CallableContentProvider, Uri)} to dispatch calls.
     */
    public static Bundle call(ContentResolver resolver, Uri uri, String method, String arg,
            Bundle extras) {
        if (Integer.parseInt(Build.VERSION.SDK) < 11) {
            Cursor cursor = resolver.query(uri(uri, method, arg), null, null, null, null);
            Bundle result = null;
            if (cursor != null) {
                try {
                    result = cursor.respond(extras);
                } finally {
                    cursor.close();
                }
            }
            return result;
        } else {
            return resolver.call(uri, method, arg, extras);
        }
    }

    /**
     * Dispatches calls made with
     * {@link #call(ContentResolver, Uri, String, String, Bundle)} to
     * {@link CallableContentProvider#call(String, String, Bundle)} via
     * {@link ContentProvider#query(Uri, String[], String, String[], String)}
     * and {@link Cursor#respond(Bundle)}.
     * <p>
     * Usage:
     *
     * <pre>
     * &#064;Override
     * public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
     *         String sortOrder) {
     *     Cursor callable = CallableProvider.query(this, uri);
     *     if (callable != null) {
     *         return callable;
     *     }
     *     ...
     * }
     * </pre>
     */
    public static Cursor query(CallableContentProvider provider, Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments.size() == 3 && segments.get(0).equals(PATH)) {
            String method = segments.get(1);
            String arg = segments.get(2);
            return new CallableCursor(provider, method, arg);
        } else {
            return null;
        }
    }
}
