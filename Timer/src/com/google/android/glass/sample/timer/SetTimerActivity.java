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
 * Activity to set the timer.
 */
public class SetTimerActivity extends Activity implements GestureDetector.BaseListener {

    public static final String EXTRA_DURATION_MILLIS = "duration_millis";

    private static final int SELECT_VALUE = 100;

    private AudioManager mAudioManager;

    private GestureDetector mDetector;
    private CardScrollView mView;
    private SetTimerScrollAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mAdapter = new SetTimerScrollAdapter(this);
        mAdapter.setDurationMillis(getIntent().getLongExtra(EXTRA_DURATION_MILLIS, 0));

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
            int position = mView.getSelectedItemPosition();
            SetTimerScrollAdapter.TimeComponents component =
                    (SetTimerScrollAdapter.TimeComponents) mAdapter.getItem(position);
            Intent selectValueIntent = new Intent(this, SelectValueActivity.class);

            selectValueIntent.putExtra(SelectValueActivity.EXTRA_COUNT, component.getMaxValue());
            selectValueIntent.putExtra(
                    SelectValueActivity.EXTRA_INITIAL_VALUE,
                    (int) mAdapter.getTimeComponent(component));
            startActivityForResult(selectValueIntent, SELECT_VALUE);
            mAudioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_DURATION_MILLIS, mAdapter.getDurationMillis());
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SELECT_VALUE) {
            int position = mView.getSelectedItemPosition();
            SetTimerScrollAdapter.TimeComponents component =
                    (SetTimerScrollAdapter.TimeComponents) mAdapter.getItem(position);

            mAdapter.setTimeComponent(
                    component, data.getIntExtra(SelectValueActivity.EXTRA_SELECTED_VALUE, 0));
            mView.updateViews(true);
        }
    }
}
