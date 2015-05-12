/*-
 * Copyright (C) 2009 Google Inc.
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

package com.google.android.accounts;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

/**
 * Adapts a {@link DatabaseAuthenticator} to extend
 * {@link AbstractAccountAuthenticator}.
 */
public class DatabaseAuthenticatorAdapter extends AbstractAccountAuthenticator {

    private static com.google.android.accounts.Account convertAccount(
            android.accounts.Account account) {
        return new com.google.android.accounts.Account(account.name, account.type);
    }

    private static Bundle convertResponse(AccountAuthenticatorResponse response, Bundle bundle) {
        if (bundle != null && bundle.containsKey(AccountManager.KEY_INTENT)) {
            String key = AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE;
            Parcelable value = response;

            // Set the AccountAuthenticatorResponse Intent extra
            Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
            intent.putExtra(key, value);
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        }
        return bundle;
    }

    private final DatabaseAuthenticator mAuthenticator;

    public DatabaseAuthenticatorAdapter(DatabaseAuthenticator authenticator) {
        super(authenticator.getContext());
        mAuthenticator = authenticator;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
            String authTokenType, String[] requiredFeatures, Bundle options) {
        return convertResponse(response, mAuthenticator.addAccount(accountType, authTokenType,
                requiredFeatures, options));
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account,
            Bundle options) {
        return convertResponse(response, mAuthenticator.confirmCredentials(convertAccount(account),
                options));
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return convertResponse(response, mAuthenticator.editProperties(accountType));
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
            String authTokenType, Bundle loginOptions) {
        return convertResponse(response, mAuthenticator.getAuthToken(convertAccount(account),
                authTokenType, loginOptions));
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return mAuthenticator.getAuthTokenLabel(authTokenType);
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account,
            String[] features) throws NetworkErrorException {
        try {
            return convertResponse(response, mAuthenticator.hasFeatures(convertAccount(account),
                    features));
        } catch (com.google.android.accounts.NetworkErrorException e) {
            throw new NetworkErrorException(e);
        }
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
            String authTokenType, Bundle loginOptions) {
        return convertResponse(response, mAuthenticator.updateCredentials(convertAccount(account),
                authTokenType, loginOptions));
    }
}
