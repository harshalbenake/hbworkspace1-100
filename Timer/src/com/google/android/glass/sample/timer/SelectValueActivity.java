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

import android.app.Activity;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.MotionEvent;

/**
 * Activity to select a timer component value.
 */
public class SelectValueActivity extends Activity implements GestureDetector.BaseListener {

    public static final String EXTRA_COUNT = "count";
    public static final String EXTRA_INITIAL_VALUE = "initial_value";
    public static final String EXTRA_SELECTED_VALUE = "selected_value";

    private static final int DEFAULT_COUNT = 60;

    private AudioManager mAudioManager;

    private GestureDetector mDetector;
    private CardScrollView mView;
    private SelectValueScrollAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mAdapter = new SelectValueScrollAdapter(
                this, getIntent().getIntExtra(EXTRA_COUNT, DEFAULT_COUNT));

        mView = new CardScrollView(this) {
            @Override
            public final boolean dispatchGenericFocusedEvent(MotionEvent event) {
                if (mDetector.onMotionEvent(event)) {
                    return true;
                }
                return super.dispatchGenericFocusedEvent(event);
            }
        };
        mView.setAdapter(mAdapter);
        setContentView(mView);

        mDetector = new GestureDetector(this).setBaseListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mView.activate();
        mView.setSelection(getIntent().getIntExtra(EXTRA_INITIAL_VALUE, 0));
    }

    @Override
    public void onPause() {
        super.onPause();
        mView.deactivate();
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mDetector.onMotionEvent(event);
    }

    @Override
    public boolean onGesture(Gesture gesture) {
        if (gesture == Gesture.TAP) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_SELECTED_VALUE, mView.getSelectedItemPosition());
            setResult(RESULT_OK, resultIntent);
            mAudioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
            finish();
            return true;
        }
        return false;
    }
}
