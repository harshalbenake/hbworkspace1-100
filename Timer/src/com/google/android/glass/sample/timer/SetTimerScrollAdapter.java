/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.google.android.glass.sample.timer;

import com.google.android.glass.widget.CardScrollAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Adapter for the {@link CardScrollView} inside {@link SetTimerActivity}.
 */
public class SetTimerScrollAdapter extends CardScrollAdapter {

    public enum TimeComponents {
        HOURS(0, 24, R.string.hours),
        MINUTES(1, 60, R.string.minutes),
        SECONDS(2, 60, R.string.seconds);

        private final int mPosition;
        private final int mMaxValue;
        private final int mLabelResourceId;

        TimeComponents(int pos, int max, int resId) {
            mPosition = pos;
            mMaxValue = max;
            mLabelResourceId = resId;
        }

        /**
         * Gets the component's position in the values array.
         */
        public int getPosition() {
            return mPosition;
        }

        /**
         * Gets the maximum value that this component can hold.
         */
        public int getMaxValue() {
            return mMaxValue;
        }

        /**
         * Gets the resource ID for the component's label.
         */
        public int getLabelResourceId() {
            return mLabelResourceId;
        }
    }

    private final Context mContext;
    private final long[] mValues;

    public SetTimerScrollAdapter(Context context) {
        mContext = context;
        mValues = new long[3];
    }

    /**
     * Sets the total duration in milliseconds.
     */
    public void setDurationMillis(long durationMillis) {
        mValues[TimeComponents.HOURS.getPosition()] = TimeUnit.MILLISECONDS.toHours(durationMillis);
        durationMillis %= TimeUnit.HOURS.toMillis(1);
        mValues[TimeComponents.MINUTES.getPosition()] =
                TimeUnit.MILLISECONDS.toMinutes(durationMillis);
        durationMillis %= TimeUnit.MINUTES.toMillis(1);
        mValues[TimeComponents.SECONDS.getPosition()] =
                TimeUnit.MILLISECONDS.toSeconds(durationMillis);
    }

    /**
     * Get the total duration in milliseconds.
     */
    public long getDurationMillis() {
        return TimeUnit.HOURS.toMillis(mValues[TimeComponents.HOURS.getPosition()])
                + TimeUnit.MINUTES.toMillis(mValues[TimeComponents.MINUTES.getPosition()])
                + TimeUnit.SECONDS.toMillis(mValues[TimeComponents.SECONDS.getPosition()]);
    }

    /**
     * Set a specific time component value.
     */
    public void setTimeComponent(TimeComponents component, int value) {
        mValues[component.getPosition()] = value;
    }

    /**
     * Get a specific time component value.
     */
    public long getTimeComponent(TimeComponents component) {
        return mValues[component.getPosition()];
    }

    @Override
    public int getCount() {
        return mValues.length;
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < mValues.length) {
            return TimeComponents.values()[position];
        }
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.card_timer, parent);
        }
        final TextView[] views = new TextView[] {
            (TextView) convertView.findViewById(R.id.hours),
            (TextView) convertView.findViewById(R.id.minutes),
            (TextView) convertView.findViewById(R.id.seconds)
        };
        final TextView tipView = (TextView) convertView.findViewById(R.id.tip);
        final String tipLabel = mContext.getResources().getString(
                ((TimeComponents) getItem(position)).getLabelResourceId());

        tipView.setText(tipLabel);

        for (int i = 0; i < 3; ++i) {
            views[i].setText(String.format("%02d", mValues[i]));
            views[i].setTextColor(mContext.getResources().getColor(R.color.gray));
        }
        views[position].setTextColor(mContext.getResources().getColor(R.color.white));

        return setItemOnCard(this, convertView);
    }

    @Override
    public int findIdPosition(Object id) {
        if (id instanceof TimeComponents) {
            TimeComponents component = (TimeComponents) id;
            return component.getPosition();
        }
        return AdapterView.INVALID_POSITION;
    }

    @Override
    public int findItemPosition(Object item) {
        return findIdPosition(item);
    }
}
