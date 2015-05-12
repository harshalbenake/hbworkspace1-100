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

package com.google.android.feeds;

import android.database.CrossProcessCursor;
import android.database.CursorWindow;

/**
 * Cross-process implementation of {@link NonBlockingCursor}.
 */
class NonBlockingCrossProcessCursor extends NonBlockingCursor implements CrossProcessCursor {

    private final CrossProcessCursor mCursor;

    public NonBlockingCrossProcessCursor(CrossProcessCursor cursor) {
        super(cursor);
        mCursor = cursor;
    }

    /**
     * {@inheritDoc}
     */
    public void fillWindow(int pos, CursorWindow winow) {
        mCursor.fillWindow(pos, winow);
    }

    /**
     * {@inheritDoc}
     */
    public CursorWindow getWindow() {
        return mCursor.getWindow();
    }

    /**
     * {@inheritDoc}
     */
    public boolean onMove(int oldPosition, int newPosition) {
        return mCursor.onMove(oldPosition, newPosition);
    }
}
