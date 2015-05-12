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

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;

/**
 * SurfaceHolder.Callback used to draw the timer on the timeline {@link LiveCard}.
 */
public class TimerDrawer implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private TimerView mView;

    private TimerView.ChangeListener mListener = new TimerView.ChangeListener() {
        @Override
        public void onChange() {
            if (mHolder != null) {
                draw();
            }
        }
    };

    public TimerDrawer(Context context) {
        mView = new TimerView(context);
        mView.setListener(mListener);
    }

    public Timer getTimer() {
        return mView.getTimer();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Measure and layout the view with the canvas dimensions.
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        mView.measure(measuredWidth, measuredHeight);
        mView.layout(0, 0, mView.getMeasuredWidth(), mView.getMeasuredHeight());
        draw();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        draw();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder = null;
    }

    public void draw() {
        Canvas canvas;
        try {
            canvas = mHolder.lockCanvas();
        } catch (Exception e) {
            return;
        }
        if (canvas != null) {
            mView.draw(canvas);
            mHolder.unlockCanvasAndPost(canvas);
        }
    }
}
