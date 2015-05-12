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

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Mirrors {@link android.accounts.AccountManager}
 */
public abstract class AccountManager {

    // Determine the SDK version in a way that is compatible with API level 3.
    private static final int SDK = Integer.parseInt(Build.VERSION.SDK);

    public static final int ERROR_CODE_REMOTE_EXCEPTION = 1;

    public static final int ERROR_CODE_NETWORK_ERROR = 3;

    public static final int ERROR_CODE_CANCELED = 4;

    public static final int ERROR_CODE_INVALID_RESPONSE = 5;

    public static final int ERROR_CODE_UNSUPPORTED_OPERATION = 6;

    public static final int ERROR_CODE_BAD_ARGUMENTS = 7;

    public static final int ERROR_CODE_BAD_REQUEST = 8;

    public static final String KEY_ACCOUNTS = "accounts";

    public static final String KEY_AUTHENTICATOR_TYPES = "authenticator_types";

    public static final String KEY_USERDATA = "userdata";

    public static final String KEY_AUTHTOKEN = "authtoken";

    public static final String KEY_PASSWORD = "password";

    public static final String KEY_ACCOUNT_NAME = "authAccount";

    public static final String KEY_ACCOUNT_TYPE = "accountType";

    public static final String KEY_ERROR_CODE = "errorCode";

    public static final String KEY_ERROR_MESSAGE = "errorMessage";

    public static final String KEY_INTENT = "intent";

    public static final String KEY_BOOLEAN_RESULT = "booleanResult";

    public static final String KEY_ACCOUNT_AUTHENTICATOR_RESPONSE = "accountAuthenticatorResponse";

    public static final String KEY_ACCOUNT_MANAGER_RESPONSE = "accountManagerResponse";

    public static final String KEY_AUTH_FAILED_MESSAGE = "authFailedMessage";

    public static final String KEY_AUTH_TOKEN_LABEL = "authTokenLabelKey";

    public static final String ACTION_AUTHENTICATOR_INTENT = "android.accounts.AccountAuthenticator";

    public static final String AUTHENTICATOR_META_DATA_NAME = "android.accounts.AccountAuthenticator";

    public static final String AUTHENTICATOR_ATTRIBUTES_NAME = "account-authenticator";

    /**
     * Mirrors
     * {@link android.accounts.AccountManager#LOGIN_ACCOUNTS_CHANGED_ACTION}
     */
    public static final String LOGIN_ACCOUNTS_CHANGED_ACTION = "android.accounts.LOGIN_ACCOUNTS_CHANGED";

    /**
     * Mirrors {@link android.accounts.AccountManager#get(Context)}
     */
    public static AccountManager get(Context context) {
        if (SDK >= 7) {
            // The AccountManager does not have any bugs in API Level 7,
            // so always use it to avoid the performance cost of parsing
            // the manifest for database authenticators.
            return new StandardAccountManager(context);
        } else {
            // Use DatabaseAccountManager for earlier platform versions.
            // It will use a StandardAccountManager for API Level 5 and 6,
            // unless there is a DatabaseAuthenticator that has indicated
            // it should be used instead. DatabaseAuthenticators are always
            // used on API Level 4 and earlier because StandardAccountManager
            // is not supported.
            return new DatabaseAccountManager(context);
        }
    }

    private static class TaskExecutor implements Executor {
        public void execute(Runnable r) {
            new Thread(r).start();
        }
    }

    private class BaseFutureTask<T> extends FutureTask<T> {
        final Handler mHandler;

        public BaseFutureTask(Callable<T> callable, Handler handler) {
            super(callable);
            mHandler = handler;
        }

        protected void postRunnableToHandler(Runnable runnable) {
            Handler handler = (mHandler == null) ? mMainHandler : mHandler;
            handler.post(runnable);
        }

        protected void startTask() {
            new TaskExecutor().execute(this);
        }
    }

    class Future2Task<T> extends BaseFutureTask<T> implements AccountManagerFuture<T> {
        final AccountManagerCallback<T> mCallback;

        public Future2Task(Callable<T> callable, Handler handler, AccountManagerCallback<T> callback) {
            super(callable, handler);
            mCallback = callback;
        }

        @Override
        protected void done() {
            if (mCallback != null) {
                postRunnableToHandler(new Runnable() {
                    public void run() {
                        mCallback.run(Future2Task.this);
                    }
                });
            }
        }

        public Future2Task<T> start() {
            startTask();
            return this;
        }

        private T internalGetResult(Long timeout, TimeUnit unit) throws OperationCanceledException,
                IOException, AuthenticatorException {
            try {
                if (timeout == null) {
                    return get();
                } else {
                    return get(timeout, unit);
                }
            } catch (InterruptedException e) {
                // fall through and cancel
            } catch (TimeoutException e) {
                // fall through and cancel
            } catch (CancellationException e) {
                // fall through and cancel
            } catch (ExecutionException e) {
                final Throwable cause = e.getCause();
                if (cause instanceof IOException) {
                    throw (IOException) cause;
                } else if (cause instanceof UnsupportedOperationException) {
                    throw new AuthenticatorException(cause);
                } else if (cause instanceof AuthenticatorException) {
                    throw (AuthenticatorException) cause;
                } else if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                } else if (cause instanceof Error) {
                    throw (Error) cause;
                } else {
                    throw new IllegalStateException(cause);
                }
            } finally {
                boolean mayInterruptIfRunning = true;
                cancel(mayInterruptIfRunning);
            }
            throw new OperationCanceledException();
        }

        /**
         * {@inheritDoc}
         */
        public T getResult() throws OperationCanceledException, IOException, AuthenticatorException {
            return internalGetResult(null, null);
        }

        /**
         * {@inheritDoc}
         */
        public T getResult(long timeout, TimeUnit unit) throws OperationCanceledException,
                IOException, AuthenticatorException {
            return internalGetResult(timeout, unit);
        }
    }

    private Handler mMainHandler;

    public AccountManager(Context context) {
        mMainHandler = new Handler(context.getMainLooper());
    }

    /**
     * Mirrors {@link android.accounts.AccountManager#getAccountsByType(String)}
     */
    public abstract Account[] getAccountsByType(String type);

    /**
     * Mirrors
     * {@link android.accounts.AccountManager#getAccountsByTypeAndFeatures(String, String[], android.accounts.AccountManagerCallback, Handler)}
     */
    public abstract AccountManagerFuture<Account[]> getAccountsByTypeAndFeatures(String type,
            String[] features, AccountManagerCallback<Account[]> callback, Handler handler);

    /**
     * Mirrors
     * {@link android.accounts.AccountManager#addAccount(String, String, String[], Bundle, Activity, android.accounts.AccountManagerCallback, Handler)}
     * The activity parameter is not supported and the type has been changed to
     * {@link Void} to trigger a compile error when a caller attempts to use it.
     */
    public abstract AccountManagerFuture<Bundle> addAccount(String accountType,
            String authTokenType, String[] requiredFeatures, Bundle addAccountOptions,
            Void activity, AccountManagerCallback<Bundle> callback, Handler handler);

    /**
     * Mirrors
     * {@link android.accounts.AccountManager#addAccountExplicitly(android.accounts.Account, String, Bundle)}
     */
    public abstract boolean addAccountExplicitly(Account account, String password, Bundle extras);

    /**
     * Mirrors
     * {@link android.accounts.AccountManager#removeAccount(android.accounts.Account, android.accounts.AccountManagerCallback, Handler)}
     */
    public abstract AccountManagerFuture<Boolean> removeAccount(Account account,
            AccountManagerCallback<Boolean> callback, Handler handler);

    /**
     * Mirrors
     * {@link android.accounts.AccountManager#getAuthToken(android.accounts.Account, String, boolean, android.accounts.AccountManagerCallback, Handler)}
     */
    public abstract AccountManagerFuture<Bundle> getAuthToken(Account account,
            String authTokenType, boolean notifyAuthFailure,
            AccountManagerCallback<Bundle> callback, Handler handler);

    /**
     * Mirrors
     * {@link android.accounts.AccountManager#invalidateAuthToken(String, String)}
     */
    public abstract void invalidateAuthToken(String accountType, String authToken);

    /**
     * Mirrors
     * {@link android.accounts.AccountManager#blockingGetAuthToken(android.accounts.Account, String, boolean)}
     */
    public final String blockingGetAuthToken(Account account, String authTokenType,
            boolean notifyAuthFailure) throws AuthenticatorException, IOException,
            OperationCanceledException {
        AccountManagerCallback<Bundle> callback = null;
        Handler handler = null;
        Bundle bundle = getAuthToken(account, authTokenType, notifyAuthFailure, callback, handler)
                .getResult();
        return bundle.getString(KEY_AUTHTOKEN);
    }
}
