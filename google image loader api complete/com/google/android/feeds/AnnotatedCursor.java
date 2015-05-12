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

import android.database.Cursor;
import android.database.CursorWrapper;
import android.os.Bundle;

/**
 * A {@link CursorWrapper} that overrides {@link Cursor#getExtras()} to include
 * additional key-value pairs.
 */
class AnnotatedCursor extends CursorWrapper {

    private final Bundle mExtras;

    /**
     * Constructs a {@link AnnotatedCursor}.
     *
     * @param cursor the {@link Cursor} to wrap.
     * @param extras a {@link Bundle} containing additional key-value pairs to
     *            include in the {@link Bundle} returned by
     *            {@link #getExtras()}.
     */
    public AnnotatedCursor(Cursor cursor, Bundle extras) {
        super(cursor);
        if (cursor == null) {
            throw new NullPointerException("Cursor is null");
        }
        if (extras == null) {
            throw new NullPointerException("Extras are null");
        }
        mExtras = extras;
    }

    @Override
    public Bundle getExtras() {
        Bundle extras = new Bundle();
        extras.putAll(super.getExtras());
        extras.putAll(mExtras);
        return extras;
    }
}
