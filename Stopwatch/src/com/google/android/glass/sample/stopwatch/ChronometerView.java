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

package com.google.android.glass.sample.stopwatch;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * View used to display draw a running Chronometer.
 *
 * This code is greatly inspired by the Android's Chronometer widget.
 */
public class ChronometerView extends FrameLayout {

    /**
     * Interface to listen for changes on the view layout.
     */
    public interface ChangeListener {
        /** Notified of a change in the view. */
        public void onChange();
    }

    // About 24 FPS.
    private static final long DELAY_MILLIS = 41;

    private final TextView mMinuteView;
    private final TextView mSecondView;
    private final TextView mCentiSecondView;

    private boolean mStarted;
    private boolean mForceStart;
    private boolean mVisible;
    private boolean mRunning;

    private long mBaseMillis;

    private ChangeListener mChangeListener;

    public ChronometerView(Context context) {
        this(context, null, 0);
    }

    public ChronometerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChronometerView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        LayoutInflater.from(context).inflate(R.layout.card_chronometer, this);

        mMinuteView = (TextView) findViewById(R.id.minute);
        mSecondView = (TextView) findViewById(R.id.second);
        mCentiSecondView = (TextView) findViewById(R.id.centi_second);

        setBaseMillis(SystemClock.elapsedRealtime());
    }

    /**
     * Set the base value of the chronometer in milliseconds.
     */
    public void setBaseMillis(long baseMillis) {
        mBaseMillis = baseMillis;
        updateText();
    }

    /**
     * Get the base value of the chronometer in milliseconds.
     */
    public long getBaseMillis() {
        return mBaseMillis;
    }

    /**
     * Set a {@link ChangeListener}.
     */
    public void setListener(ChangeListener listener) {
        mChangeListener = listener;
    }

    /**
     * Set whether or not to force the start of the chronometer when a window has not been attached
     * to the view.
     */
    public void setForceStart(boolean forceStart) {
        mForceStart = forceStart;
        updateRunning();
    }

    /**
     * Start the chronometer.
     */
    public void start() {
        mStarted = true;
        updateRunning();
    }

    /**
     * Stop the chronometer.
     */
    public void stop() {
        mStarted = false;
        updateRunning();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVisible = false;
        updateRunning();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mVisible = (visibility == VISIBLE);
        updateRunning();
    }


    private final Handler mHandler = new Handler();

    private final Runnable mUpdateTextRunnable = new Runnable() {
        @Override
        public void run() {
            if (mRunning) {
                updateText();
                mHandler.postDelayed(mUpdateTextRunnable, DELAY_MILLIS);
            }
        }
    };

    /**
     * Update the running state of the chronometer.
     */
    private void updateRunning() {
        boolean running = (mVisible || mForceStart) && mStarted;
        if (running != mRunning) {
            if (running) {
                mHandler.post(mUpdateTextRunnable);
            } else {
                mHandler.removeCallbacks(mUpdateTextRunnable);
            }
            mRunning = running;
        }
    }

    /**
     * Update the value of the chronometer.
     */
    private void updateText() {
        long millis = SystemClock.elapsedRealtime() - mBaseMillis;
        // Cap chronometer to one hour.
        millis %= TimeUnit.HOURS.toMillis(1);

        mMinuteView.setText(String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millis)));
        millis %= TimeUnit.MINUTES.toMillis(1);
        mSecondView.setText(String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millis)));
        millis = (millis % TimeUnit.SECONDS.toMillis(1)) / 10;
        mCentiSecondView.setText(String.format("%02d", millis));
        if (mChangeListener != null) {
            mChangeListener.onChange();
        }
    }
}
