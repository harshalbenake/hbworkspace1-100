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

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;

/**
 * Dispatches calls to
 * {@link CallableContentProvider#call(String, String, Bundle)} via
 * {@link Cursor#respond(Bundle)} on platform versions that don't support
 * {@link ContentResolver#call(Uri, String, String, Bundle)}.
 */
final class CallableCursor extends MatrixCursor {
    private static final String[] PROJECTION = {};
    private final CallableContentProvider mProvider;
    private final String mMethod;
    private final String mArg;

    public CallableCursor(CallableContentProvider provider, String method, String arg) {
        super(PROJECTION);
        mProvider = provider;
        mMethod = method;
        mArg = arg;
    }

    @Override
    public Bundle respond(Bundle extras) {
        return mProvider.call(mMethod, mArg, extras);
    }
}
