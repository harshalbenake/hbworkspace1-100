/*-
 * Copyright (C) 2009 The Android Open Source Project
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
 *
 * Notes:
 * This implementation is copied from the Android 2.0 SDK so that it may be
 * used without triggering an exception on pre-2.0 devices.
 *
 * Modifications:
 * -Moved from android.accounts to com.google.android.accounts
 * -Added wrapper class for AccountAuthenticatorResponse
 * -Fixed javadoc
 */

package com.google.android.accounts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

/**
 * Base class for implementing an {@link Activity} that is used to help
 * implement an {@link android.accounts.AbstractAccountAuthenticator}. If the
 * {@link android.accounts.AbstractAccountAuthenticator} needs to use an
 * activity to handle the request then it can have the activity extend
 * {@link AccountAuthenticatorActivity}. The
 * {@link android.accounts.AbstractAccountAuthenticator} passes in the response
 * to the intent using the following:
 *
 * <pre>
 * intent.putExtra(AccountManager.ACCOUNT_AUTHENTICATOR_RESPONSE_KEY, response);
 * </pre>
 *
 * The activity then sets the result that is to be handed to the response via
 * {@link #setAccountAuthenticatorResult(android.os.Bundle)}. This result will
 * be sent as the result of the request when the activity finishes. If this is
 * never set or if it is set to null then error
 * {@link AccountManager#ERROR_CODE_CANCELED} will be called on the response.
 */
public class AccountAuthenticatorActivity extends Activity {
    private AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;

    private Bundle mResultBundle = null;

    /**
     * Set the result that is to be sent as the result of the request that
     * caused this Activity to be launched. If result is null or this method is
     * never called then the request will be canceled.
     *
     * @param result this is returned as the result of the
     *            AbstractAccountAuthenticator request
     */
    public final void setAccountAuthenticatorResult(Bundle result) {
        mResultBundle = result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String key = AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE;
        Parcelable response = intent.getParcelableExtra(key);
        if (response != null) {
            mAccountAuthenticatorResponse = new AccountAuthenticatorResponse(response);
        }

        if (mAccountAuthenticatorResponse != null) {
            mAccountAuthenticatorResponse.onRequestContinued();
        }
    }

    @Override
    public void finish() {
        if (mAccountAuthenticatorResponse != null) {
            // send the result bundle back if set, otherwise send an error.
            if (mResultBundle != null) {
                mAccountAuthenticatorResponse.onResult(mResultBundle);
            } else {
                mAccountAuthenticatorResponse.onError(AccountManager.ERROR_CODE_CANCELED,
                        "canceled");
            }
            mAccountAuthenticatorResponse = null;
        }
        super.finish();
    }
}

/**
 * Wraps {@link android.accounts.AccountAuthenticatorResponse} so that
 * {@link android.accounts.AccountAuthenticatorResponse} is only referenced when
 * the API level is greater than or equal to {@code 5} (to prevent
 * {@link VerifyError VerifyErrors}).
 */
class AccountAuthenticatorResponse {
    private final android.accounts.AccountAuthenticatorResponse mResponse;

    /**
     * Constructor.
     *
     * @param response the {@link android.accounts.AccountAuthenticatorResponse}
     */
    public AccountAuthenticatorResponse(Parcelable response) {
        if (response == null) {
            throw new NullPointerException();
        }
        mResponse = (android.accounts.AccountAuthenticatorResponse) response;
    }

    /**
     * Mirrors
     * {@link android.accounts.AccountAuthenticatorResponse#onRequestContinued()}
     */
    public void onRequestContinued() {
        mResponse.onRequestContinued();
    }

    /**
     * Mirrors
     * {@link android.accounts.AccountAuthenticatorResponse#onResult(Bundle)}
     */
    public void onResult(Bundle result) {
        mResponse.onResult(result);
    }

    /**
     * Mirrors
     * {@link android.accounts.AccountAuthenticatorResponse#onError(int, String)}
     */
    public void onError(int errorCode, String errorMessage) {
        mResponse.onError(errorCode, errorMessage);
    }
}
