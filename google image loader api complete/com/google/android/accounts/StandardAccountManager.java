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
import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * An implementation of {@link AccountManager} that is just a thin wrapper
 * around {@link android.accounts.AccountManager}.
 */
class StandardAccountManager extends AccountManager {

    private static Account convertAccount(android.accounts.Account account) {
        return new Account(account.name, account.type);
    }

    private static Account[] convertAccountArray(android.accounts.Account[] accounts) {
        Account[] array = new Account[accounts.length];
        for (int i = 0; i < accounts.length; i++) {
            array[i] = convertAccount(accounts[i]);
        }
        return array;
    }

    private static android.accounts.Account convertAccount(Account account) {
        return new android.accounts.Account(account.name, account.type);
    }

    private static <V> AccountManagerFuture<V> convertFuture(
            final android.accounts.AccountManagerFuture<V> future) {
        return new AccountManagerFuture<V>() {
            /**
             * {@inheritDoc}
             */
            public boolean cancel(boolean mayInterruptIfRunning) {
                return future.cancel(mayInterruptIfRunning);
            }

            /**
             * {@inheritDoc}
             */
            public V getResult(long timeout, TimeUnit unit) throws OperationCanceledException,
                    IOException, AuthenticatorException {
                try {
                    return future.getResult(timeout, unit);
                } catch (android.accounts.AuthenticatorException e) {
                    throw new AuthenticatorException(e);
                } catch (android.accounts.OperationCanceledException e) {
                    throw new OperationCanceledException(e);
                }
            }

            /**
             * {@inheritDoc}
             */
            public boolean isCancelled() {
                return future.isCancelled();
            }

            /**
             * {@inheritDoc}
             */
            public boolean isDone() {
                return future.isDone();
            }

            /**
             * {@inheritDoc}
             */
            public V getResult() throws OperationCanceledException, IOException,
                    AuthenticatorException {
                try {
                    return future.getResult();
                } catch (android.accounts.AuthenticatorException e) {
                    throw new AuthenticatorException(e);
                } catch (android.accounts.OperationCanceledException e) {
                    throw new OperationCanceledException(e);
                }
            }
        };
    }

    private static AccountManagerFuture<Account[]> convertAccountArrayFuture(
            final android.accounts.AccountManagerFuture<android.accounts.Account[]> future) {
        return new AccountManagerFuture<Account[]>() {
            /**
             * {@inheritDoc}
             */
            public boolean cancel(boolean mayInterruptIfRunning) {
                return future.cancel(mayInterruptIfRunning);
            }

            /**
             * {@inheritDoc}
             */
            public Account[] getResult(long timeout, TimeUnit unit)
                    throws OperationCanceledException, IOException, AuthenticatorException {
                try {
                    return convertAccountArray(future.getResult(timeout, unit));
                } catch (android.accounts.AuthenticatorException e) {
                    throw new AuthenticatorException(e);
                } catch (android.accounts.OperationCanceledException e) {
                    throw new OperationCanceledException(e);
                }
            }

            /**
             * {@inheritDoc}
             */
            public boolean isCancelled() {
                return future.isCancelled();
            }

            /**
             * {@inheritDoc}
             */
            public boolean isDone() {
                return future.isDone();
            }

            /**
             * {@inheritDoc}
             */
            public Account[] getResult() throws OperationCanceledException, IOException,
                    AuthenticatorException {
                try {
                    return convertAccountArray(future.getResult());
                } catch (android.accounts.AuthenticatorException e) {
                    throw new AuthenticatorException(e);
                } catch (android.accounts.OperationCanceledException e) {
                    throw new OperationCanceledException(e);
                }
            }
        };
    }

    private static <V> android.accounts.AccountManagerCallback<V> convertCallback(
            final AccountManagerCallback<V> callback) {
        if (callback != null) {
            return new android.accounts.AccountManagerCallback<V>() {
                public void run(android.accounts.AccountManagerFuture<V> future) {
                    callback.run(convertFuture(future));
                }
            };
        } else {
            return null;
        }
    }

    private static android.accounts.AccountManagerCallback<android.accounts.Account[]> convertAccountArrayCallback(
            final AccountManagerCallback<Account[]> callback) {
        if (callback != null) {
            return new android.accounts.AccountManagerCallback<android.accounts.Account[]>() {
                public void run(
                        android.accounts.AccountManagerFuture<android.accounts.Account[]> future) {
                    callback.run(convertAccountArrayFuture(future));
                }
            };
        } else {
            return null;
        }
    }

    private final android.accounts.AccountManager mManager;

    public StandardAccountManager(Context context) {
        super(context);
        mManager = android.accounts.AccountManager.get(context);
    }

    @Override
    public Account[] getAccountsByType(String type) {
        return convertAccountArray(mManager.getAccountsByType(type));
    }

    @Override
    public AccountManagerFuture<Account[]> getAccountsByTypeAndFeatures(final String type,
            String[] features, AccountManagerCallback<Account[]> callback, Handler handler) {
        if (features == null || features.length == 0) {
            // AccountManagerService does not invoke the callback as it should
            return new Future2Task<Account[]>(new Callable<Account[]>() {
                public Account[] call() {
                    return getAccountsByType(type);
                }
            }, handler, callback).start();
        } else {
            return convertAccountArrayFuture(mManager.getAccountsByTypeAndFeatures(type, features,
                    convertAccountArrayCallback(callback), handler));
        }
    }

    @Override
    public AccountManagerFuture<Bundle> getAuthToken(Account account, String authTokenType,
            boolean notifyAuthFailure, AccountManagerCallback<Bundle> callback, Handler handler) {
        return convertFuture(mManager.getAuthToken(convertAccount(account), authTokenType,
                notifyAuthFailure, convertCallback(callback), handler));
    }

    @Override
    public AccountManagerFuture<Bundle> addAccount(String accountType, String authTokenType,
            String[] requiredFeatures, Bundle addAccountOptions, Void activity,
            AccountManagerCallback<Bundle> callback, Handler handler) {
        if (activity != null) {
            throw new RuntimeException("Activity parameter is not supported");
        }
        return convertFuture(mManager.addAccount(accountType, authTokenType, requiredFeatures,
                addAccountOptions, null, convertCallback(callback), handler));
    }

    @Override
    public boolean addAccountExplicitly(Account account, String password, Bundle extras) {
        return mManager.addAccountExplicitly(convertAccount(account), password, extras);
    }

    @Override
    public AccountManagerFuture<Boolean> removeAccount(Account account,
            AccountManagerCallback<Boolean> callback, Handler handler) {
        return convertFuture(mManager.removeAccount(convertAccount(account),
                convertCallback(callback), handler));
    }

    @Override
    public void invalidateAuthToken(String accountType, String authToken) {
        mManager.invalidateAuthToken(accountType, authToken);
    }
}
