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
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;

import java.util.concurrent.TimeUnit;

/**
 * SurfaceHolder.Callback used to draw the chronometer on the timeline LiveCard.
 */
public class ChronometerDrawer implements SurfaceHolder.Callback {
    private static final String TAG = "ChronometerDrawer";

    private static final long SEC_TO_MILLIS = TimeUnit.SECONDS.toMillis(1);
    private static final int SOUND_PRIORITY = 1;
    private static final int MAX_STREAMS = 1;
    private static final int COUNT_DOWN_VALUE = 3;

    private final SoundPool mSoundPool;
    private final int mStartSoundId;
    private final int mCountDownSoundId;

    private final CountDownView mCountDownView;
    private final ChronometerView mChronometerView;

    private long mCurrentTimeSeconds;
    private boolean mCountDownSoundPlayed;

    private SurfaceHolder mHolder;
    private boolean mCountDownDone;

    public ChronometerDrawer(Context context) {
        mCountDownView = new CountDownView(context);
        mCountDownView.setCountDown(COUNT_DOWN_VALUE);
        mCountDownView.setListener(new CountDownView.CountDownListener() {

            @Override
            public void onTick(long millisUntilFinish) {
                maybePlaySound(millisUntilFinish);
                draw(mCountDownView);
            }

            @Override
            public void onFinish() {
                mCountDownDone = true;
                mChronometerView.setBaseMillis(SystemClock.elapsedRealtime());
                if (mHolder != null) {
                    mChronometerView.start();
                }
                playSound(mStartSoundId);
            }

            private void maybePlaySound(long millisUntilFinish) {
                long currentTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinish);
                long milliSecondsPart = millisUntilFinish % SEC_TO_MILLIS;

                if (currentTimeSeconds != mCurrentTimeSeconds) {
                    mCountDownSoundPlayed = false;
                    mCurrentTimeSeconds = currentTimeSeconds;
                }
                if (!mCountDownSoundPlayed
                    && (milliSecondsPart <= CountDownView.ANIMATION_DURATION_IN_MILLIS)) {
                    playSound(mCountDownSoundId);
                    mCountDownSoundPlayed = true;
                }
            }
        });

        mChronometerView = new ChronometerView(context);
        mChronometerView.setListener(new ChronometerView.ChangeListener() {

            @Override
            public void onChange() {
                draw(mChronometerView);
            }
        });
        mChronometerView.setForceStart(true);

        mSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        mStartSoundId = mSoundPool.load(context, R.raw.start, SOUND_PRIORITY);
        mCountDownSoundId = mSoundPool.load(context, R.raw.countdown_bip, SOUND_PRIORITY);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Measure and layout the view with the canvas dimensions.
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        mCountDownView.measure(measuredWidth, measuredHeight);
        mCountDownView.layout(
                0, 0, mCountDownView.getMeasuredWidth(), mCountDownView.getMeasuredHeight());

        mChronometerView.measure(measuredWidth, measuredHeight);
        mChronometerView.layout(
                0, 0, mChronometerView.getMeasuredWidth(), mChronometerView.getMeasuredHeight());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "Surface created");
        mHolder = holder;
        if (mCountDownDone) {
            mChronometerView.start();
        } else {
            mCountDownView.start();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Surface destroyed");
        mChronometerView.stop();
        mHolder = null;
    }

    /**
     * Draws the view in the SurfaceHolder's canvas.
     */
    private void draw(View view) {
        Canvas canvas;
        try {
            canvas = mHolder.lockCanvas();
        } catch (Exception e) {
            return;
        }
        if (canvas != null) {
            view.draw(canvas);
            mHolder.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * Plays the provided {@code soundId}.
     */
    private void playSound(int soundId) {
        mSoundPool.play(soundId,
                        1 /* leftVolume */,
                        1 /* rightVolume */,
                        SOUND_PRIORITY,
                        0 /* loop */,
                        1 /* rate */);
    }
}
