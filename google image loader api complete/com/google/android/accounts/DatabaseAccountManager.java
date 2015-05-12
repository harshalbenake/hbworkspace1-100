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

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * A Cupcake-compatibile implementation of {@link AccountManager} where accounts
 * and authentication tokens are stored in a database managed by a
 * {@link DatabaseAuthenticator} declared in the manifest.
 */
class DatabaseAccountManager extends AccountManager {

    // Determine the SDK version in a way that is compatible with API level 3.
    private static final int SDK = Integer.parseInt(Build.VERSION.SDK);

    /**
     * Returns a {@link Bundle} result for when the requested authenticator is
     * missing.
     */
    private static Bundle authenticatorMissing(String accountType) {
        Bundle result = new Bundle();
        result.putInt(KEY_ERROR_CODE, ERROR_CODE_BAD_ARGUMENTS);
        result.putString(KEY_ERROR_MESSAGE, "unknown account type: " + accountType);
        return result;
    }

    private final Map<String, DatabaseAuthenticator> mAuthenticators;

    private AccountManager mStandardAccountManager;

    public DatabaseAccountManager(Context context) {
        super(context);
        mAuthenticators = DatabaseAuthenticator.createDatabaseAuthenticators(context);
        if (SDK >= 5) {
            mStandardAccountManager = new StandardAccountManager(context);
        } else {
            // The standard AccountManager API
            // is not available on earlier releases
        }
    }

    private DatabaseAuthenticator getAuthenticator(String type) {
        DatabaseAuthenticator authenticator = mAuthenticators.get(type);
        if (authenticator != null && SDK <= authenticator.getMaxSdkVersion()) {
            return authenticator;
        } else {
            return null;
        }
    }

    @Override
    public Account[] getAccountsByType(String type) {
        DatabaseAuthenticator authenticator = getAuthenticator(type);
        if (authenticator != null) {
            return authenticator.getAccountsByType(type);
        } else if (mStandardAccountManager != null) {
            return mStandardAccountManager.getAccountsByType(type);
        } else {
            return new Account[0];
        }
    }

    @Override
    public AccountManagerFuture<Account[]> getAccountsByTypeAndFeatures(final String type,
            final String[] features, AccountManagerCallback<Account[]> callback, Handler handler) {
        final DatabaseAuthenticator authenticator = getAuthenticator(type);
        if (authenticator != null || mStandardAccountManager == null) {
            return new Future2Task<Account[]>(new Callable<Account[]>() {
                public Account[] call() {
                    if (authenticator != null) {
                        return authenticator.getAccountsByTypeAndFeatures(type, features);
                    } else {
                        return new Account[0];
                    }
                }
            }, handler, callback).start();
        } else {
            return mStandardAccountManager.getAccountsByTypeAndFeatures(type, features, callback,
                    handler);
        }
    }

    @Override
    public AccountManagerFuture<Bundle> getAuthToken(final Account account,
            final String authTokenType, boolean notifyAuthFailure,
            AccountManagerCallback<Bundle> callback, Handler handler) {
        final DatabaseAuthenticator authenticator = getAuthenticator(account.type);
        if (authenticator != null || mStandardAccountManager == null) {
            return new Future2Task<Bundle>(new Callable<Bundle>() {
                public Bundle call() {
                    if (authenticator != null) {
                        return authenticator.getAuthToken(account, authTokenType, null);
                    } else {
                        return authenticatorMissing(account.type);
                    }
                }
            }, handler, callback).start();
        } else {
            return mStandardAccountManager.getAuthToken(account, authTokenType, notifyAuthFailure,
                    callback, handler);
        }
    }

    @Override
    public AccountManagerFuture<Bundle> addAccount(final String accountType,
            final String authTokenType, final String[] requiredFeatures,
            final Bundle addAccountOptions, Void activity, AccountManagerCallback<Bundle> callback,
            Handler handler) {
        if (activity != null) {
            throw new RuntimeException("Activity parameter is not supported");
        }
        final DatabaseAuthenticator authenticator = getAuthenticator(accountType);
        if (authenticator != null || mStandardAccountManager == null) {
            return new Future2Task<Bundle>(new Callable<Bundle>() {
                public Bundle call() {
                    if (authenticator != null) {
                        return authenticator.addAccount(accountType, authTokenType,
                                requiredFeatures, addAccountOptions);
                    } else {
                        return authenticatorMissing(accountType);
                    }
                }
            }, handler, callback).start();
        } else {
            return mStandardAccountManager.addAccount(accountType, authTokenType, requiredFeatures,
                    addAccountOptions, activity, callback, handler);
        }
    }

    @Override
    public boolean addAccountExplicitly(Account account, String password, Bundle userdata) {
        DatabaseAuthenticator authenticator = getAuthenticator(account.type);
        if (authenticator != null) {
            return authenticator.addAccountExplicitly(account, password, userdata);
        } else if (mStandardAccountManager != null) {
            return mStandardAccountManager.addAccountExplicitly(account, password, userdata);
        } else {
            return false;
        }
    }

    @Override
    public AccountManagerFuture<Boolean> removeAccount(final Account account,
            AccountManagerCallback<Boolean> callback, Handler handler) {
        final DatabaseAuthenticator authenticator = getAuthenticator(account.type);
        if (authenticator != null || mStandardAccountManager == null) {
            return new Future2Task<Boolean>(new Callable<Boolean>() {
                public Boolean call() {
                    DatabaseAuthenticator authenticator = getAuthenticator(account.type);
                    if (authenticator != null) {
                        return authenticator.removeAccount(account);
                    } else {
                        return false;
                    }
                }
            }, handler, callback).start();
        } else {
            return mStandardAccountManager.removeAccount(account, callback, handler);
        }
    }

    @Override
    public void invalidateAuthToken(String accountType, String authToken) {
        DatabaseAuthenticator authenticator = getAuthenticator(accountType);
        if (authenticator != null) {
            authenticator.invalidateAuthToken(accountType, authToken);
        } else if (mStandardAccountManager != null) {
            mStandardAccountManager.invalidateAuthToken(accountType, authToken);
        } else {
            // Do nothing
        }
    }
}
