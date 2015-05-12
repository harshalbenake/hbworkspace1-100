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

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import java.lang.reflect.Method;

/**
 * Decorates a {@link ListAdapter} to show a loading or error footer.
 * <p>
 * The {@link ListAdapter} should be a {@link CursorAdapter} and the
 * {@link Cursor} must have {@link FeedExtras#EXTRA_MORE} or
 * {@link FeedExtras#EXTRA_ERROR} set in {@link Cursor#getExtras()} for a footer
 * to be shown. Footers are never shown when the {@link Cursor} is empty.
 * <p>
 * When {@link FeedExtras#EXTRA_MORE} is {@code true}, the "Loading" footer will
 * be shown even if the content isn't actually being loaded. The activity or
 * fragment that loaded the initial data set is responsible for automatically
 * loading more items when the user scrolls to the end of the list.
 */
public abstract class ContentDecorator implements WrapperListAdapter {

    private static Class<?> SUPPORT_CURSOR_ADAPTER;
    private static Method SUPPORT_GET_CURSOR;

    static {
        try {
            SUPPORT_CURSOR_ADAPTER = Class.forName("android.support.v4.widget.CursorAdapter");
            SUPPORT_GET_CURSOR = SUPPORT_CURSOR_ADAPTER.getMethod("getCursor");
        } catch (Throwable t) {
            // Support library is not being used
            SUPPORT_CURSOR_ADAPTER = null;
            SUPPORT_GET_CURSOR = null;
        }
    }

    /**
     * Calls the equivalent of
     * <code>instanceof android.support.v4.widget.CursorAdapter</code>.
     */
    private static boolean isInstanceOfSupportCursorAdapter(ListAdapter adapter) {
        return SUPPORT_CURSOR_ADAPTER != null && SUPPORT_CURSOR_ADAPTER.isInstance(adapter);
    }

    /**
     * Invokes {@link android.support.v4.widget.CursorAdapter#getCursor()}.
     */
    private static Cursor supportGetCursor(ListAdapter adapter) {
        try {
            return (Cursor) SUPPORT_GET_CURSOR.invoke(adapter);
        } catch (Exception e) {
            throw new RuntimeException("Incompatible android.support.v4.widget.CursorAdapter", e);
        }
    }

    private final ListAdapter mAdapter;

    public ContentDecorator(ListAdapter adapter) {
        if (adapter == null) {
            throw new NullPointerException();
        }
        mAdapter = adapter;
    }

    /** {@inheritDoc} */
    public ListAdapter getWrappedAdapter() {
        return mAdapter;
    }

    private Bundle getCursorExtras() {
        if (mAdapter instanceof android.widget.CursorAdapter) {
            Cursor cursor = ((android.widget.CursorAdapter) mAdapter).getCursor();
            return cursor != null ? cursor.getExtras() : Bundle.EMPTY;
        } else if (isInstanceOfSupportCursorAdapter(mAdapter)) {
            Cursor cursor = supportGetCursor(mAdapter);
            return cursor != null ? cursor.getExtras() : Bundle.EMPTY;
        } else {
            return Bundle.EMPTY;
        }
    }

    private boolean hasMore() {
        return getCursorExtras().getBoolean(FeedExtras.EXTRA_MORE);
    }

    private boolean hasError() {
        return getCursorExtras().containsKey(FeedExtras.EXTRA_ERROR);
    }

    /** {@inheritDoc} */
    public final int getCount() {
        int count = mAdapter.getCount();
        if (count != 0) {
            if (hasError() || hasMore()) {
                count++;
            }
        } else {
            // Don't show footers when the list is empty.
        }
        return count;
    }

    /** {@inheritDoc} */
    public int getItemViewType(int position) {
        if (isItem(position)) {
            return mAdapter.getItemViewType(position);
        } else {
            return AdapterView.ITEM_VIEW_TYPE_IGNORE;
        }
    }

    /** {@inheritDoc} */
    public int getViewTypeCount() {
        return mAdapter.getViewTypeCount();
    }

    private boolean isItem(int position) {
        return position < mAdapter.getCount();
    }

    /** {@inheritDoc} */
    public View getView(int position, View convertView, ViewGroup parent) {
        if (isItem(position)) {
            return mAdapter.getView(position, convertView, parent);
        } else if (hasError()) {
            LayoutInflater inflater = getLayoutInflater(parent);
            return newErrorView(inflater, parent);
        } else if (hasMore()) {
            LayoutInflater inflater = getLayoutInflater(parent);
            return newLoadingView(inflater, parent);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Creates a view that is shown when there is an error.
     * 
     * @param inflater a layout inflater.
     * @param parent the parent view.
     * @return a new error view.
     * @see LayoutInflater#inflate(int, ViewGroup, boolean)
     */
    protected abstract View newErrorView(LayoutInflater inflater, ViewGroup parent);

    /**
     * Creates a view that is shown when more items can be loaded.
     * 
     * @param inflater a layout inflater.
     * @param parent the parent view.
     * @return a new loading view.
     * @see LayoutInflater#inflate(int, ViewGroup, boolean)
     */
    protected abstract View newLoadingView(LayoutInflater inflater, ViewGroup parent);

    /** {@inheritDoc} */
    public Object getItem(int position) {
        if (isItem(position)) {
            return mAdapter.getItem(position);
        } else {
            return null;
        }
    }

    /** {@inheritDoc} */
    public long getItemId(int position) {
        if (isItem(position)) {
            return mAdapter.getItemId(position);
        } else {
            return AdapterView.INVALID_ROW_ID;
        }
    }

    /** {@inheritDoc} */
    public boolean hasStableIds() {
        return mAdapter.hasStableIds();
    }

    /** {@inheritDoc} */
    public final boolean isEmpty() {
        // This method must not be overridden to ensure consistency
        return getCount() == 0;
    }

    /** {@inheritDoc} */
    public void registerDataSetObserver(DataSetObserver observer) {
        mAdapter.registerDataSetObserver(observer);
    }

    /** {@inheritDoc} */
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mAdapter.unregisterDataSetObserver(observer);
    }

    /** {@inheritDoc} */
    public boolean areAllItemsEnabled() {
        // The loading view cannot be clicked.
        // See also: http://code.google.com/p/android/issues/detail?id=8914
        return false;
    }

    /** {@inheritDoc} */
    public boolean isEnabled(int position) {
        if (isItem(position)) {
            return mAdapter.isEnabled(position);
        } else {
            return false;
        }
    }

    private static LayoutInflater getLayoutInflater(ViewGroup parent) {
        Context context = parent.getContext();
        return LayoutInflater.from(context);
    }
}
