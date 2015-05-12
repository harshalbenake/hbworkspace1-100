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

import java.lang.Long;
import java.util.concurrent.TimeUnit;

/**
 * Animated countdown going from {@code mTimeSeconds} to 0.
 *
 * The current animation for each second is as follow:
 *   1. From 0 to 500ms, move the TextView from {@code MAX_TRANSLATION_Y} to 0 and its alpha from
 *      {@code 0} to {@code ALPHA_DELIMITER}.
 *   2. From 500ms to 1000ms, update the TextView's alpha from {@code ALPHA_DELIMITER} to {@code 1}.
 * At each second change, update the TextView text.
 */
public class CountDownView extends FrameLayout {
    private static final String TAG = "CountDownView";

    /**
     * Interface to listen for changes in the countdown.
     */
    public interface CountDownListener {
        /**
         * Notified of a tick, indicating a layout change.
         */
        public void onTick(long millisUntilFinish);

        /**
         * Notified when the countdown is finished.
         */
        public void onFinish();
    }

    /** Time delimiter specifying when the second component is fully shown. */
    public static final float ANIMATION_DURATION_IN_MILLIS = 850.0f;

    // About 24 FPS.
    private static final long DELAY_MILLIS = 41;
    private static final int MAX_TRANSLATION_Y = 30;
    private static final float ALPHA_DELIMITER = 0.95f;
    private static final long SEC_TO_MILLIS = TimeUnit.SECONDS.toMillis(1);

    private final TextView mSecondsView;

    private long mTimeSeconds;
    private long mStopTimeInFuture;
    private CountDownListener mListener;
    private boolean mStarted;

    public CountDownView(Context context) {
        this(context, null, 0);
    }

    public CountDownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        LayoutInflater.from(context).inflate(R.layout.card_countdown, this);

        mSecondsView =  (TextView) findViewById(R.id.seconds_view);
    }

    public void setCountDown(long timeSeconds) {
        mTimeSeconds = timeSeconds;
    }

    public long getCountDown() {
        return mTimeSeconds;
    }

    /**
     * Set a {@link CountDownListener}.
     */
    public void setListener(CountDownListener listener) {
        mListener = listener;
    }

    private final Handler mHandler = new Handler();

    private final Runnable mUpdateViewRunnable = new Runnable() {
        @Override
        public void run() {
            final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();

            // Count down is done.
            if (millisLeft <= 0) {
                mStarted = false;
                if (mListener != null) {
                    mListener.onFinish();
                }
            } else {
                updateView(millisLeft);
                if (mListener != null) {
                    mListener.onTick(millisLeft);
                }
                mHandler.postDelayed(mUpdateViewRunnable, DELAY_MILLIS);
            }
        }
    };

    /**
     * Starts the countdown animation if not yet started.
     */
    public void start() {
        if (!mStarted) {
            mStopTimeInFuture =
                    TimeUnit.SECONDS.toMillis(mTimeSeconds) + SystemClock.elapsedRealtime();
            mStarted = true;
            mHandler.postDelayed(mUpdateViewRunnable, DELAY_MILLIS);
        }
    }

    /**
     * Updates the views to reflect the current state of animation.
     *
     * @params millisUntilFinish milliseconds until the countdown is done
     */
    private void updateView(long millisUntilFinish) {
        long currentTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinish) + 1;
        long frame = SEC_TO_MILLIS - (millisUntilFinish % SEC_TO_MILLIS);

        mSecondsView.setText(Long.toString(currentTimeSeconds));
        if (frame <= ANIMATION_DURATION_IN_MILLIS) {
            float factor = frame / ANIMATION_DURATION_IN_MILLIS;
            mSecondsView.setAlpha(factor * ALPHA_DELIMITER);
            mSecondsView.setTranslationY(MAX_TRANSLATION_Y * (1 - factor));
        } else {
            float factor = (frame - ANIMATION_DURATION_IN_MILLIS) / ANIMATION_DURATION_IN_MILLIS;
            mSecondsView.setAlpha(ALPHA_DELIMITER + factor * (1 - ALPHA_DELIMITER));
        }
    }


}
