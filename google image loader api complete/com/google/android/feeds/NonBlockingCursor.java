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

import android.database.ContentObserver;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.widget.CursorAdapter;

import java.util.HashSet;

/**
 * Executes {@link Cursor#close()} asynchronously to avoid blocking the main
 * thread when {@link CursorAdapter#changeCursor(Cursor)} is called.
 */
class NonBlockingCursor extends CursorWrapper {

    private final HashSet<DataSetObserver> mDataSetObservers;

    private final HashSet<ContentObserver> mContentObservers;

    private final CloseTask mCloseTask;

    private boolean mClosed;

    public NonBlockingCursor(Cursor cursor) {
        super(cursor);
        mDataSetObservers = new HashSet<DataSetObserver>(8);
        mContentObservers = new HashSet<ContentObserver>(8);
        mCloseTask = new CloseTask(cursor);
    }

    @Override
    public void registerContentObserver(ContentObserver observer) {
        super.registerContentObserver(observer);
        mContentObservers.add(observer);
    }

    @Override
    public void unregisterContentObserver(ContentObserver observer) {
        super.unregisterContentObserver(observer);
        mContentObservers.remove(observer);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
        mDataSetObservers.add(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        super.unregisterDataSetObserver(observer);
        mDataSetObservers.remove(observer);
    }

    @Override
    public void close() {
        if (mClosed) {
            return;
        }
        
        mClosed = true;

        // Deactivate synchronously to send onInvalidated callback to observers
        // on the thread that closed the cursor.
        deactivate();

        // DataSetObservers must be unregistered otherwise they will receive an
        // onInvalidated callback on the wrong thread when the cursor is closed.
        unregisterAllDataSetObservers();

        // Remove ContentObservers now to prevent content change notifications
        // that might happen between now and the time when the cursor is closed.
        unregisterAllContentObservers();

        // Close the Cursor asynchronously to avoid blocking
        // in case the database is locked by a background thread.
        mCloseTask.execute();
    }

    private void unregisterAllDataSetObservers() {
        for (DataSetObserver observer : mDataSetObservers) {
            super.unregisterDataSetObserver(observer);
        }
        mDataSetObservers.clear();
    }

    private void unregisterAllContentObservers() {
        for (ContentObserver observer : mContentObservers) {
            super.unregisterContentObserver(observer);
        }
        mContentObservers.clear();
    }

    @Override
    public boolean isClosed() {
        return mClosed || super.isClosed();
    }

    /**
     * Closes a {@link Cursor} asynchronously.
     */
    private static class CloseTask extends AsyncTask<Void, Void, Void> {
        private final Cursor mCursor;

        public CloseTask(Cursor cursor) {
            mCursor = cursor;
        }

        @Override
        protected Void doInBackground(Void... args) {
            mCursor.close();
            return null;
        }
    }
}
