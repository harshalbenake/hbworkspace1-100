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

/**
 * Mirrors {@link android.accounts.AuthenticatorException}
 */
@SuppressWarnings("serial")
public class AuthenticatorException extends AccountsException {
    /**
     * Mirrors
     * {@link android.accounts.AuthenticatorException#AuthenticatorException(Throwable)}
     */
    public AuthenticatorException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Mirrors
     * {@link android.accounts.AuthenticatorException#AuthenticatorException()}
     */
    public AuthenticatorException() {
        super();
    }

    /**
     * Mirrors
     * {@link android.accounts.AuthenticatorException#AuthenticatorException(String, Throwable)}
     */
    public AuthenticatorException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    /**
     * Mirrors
     * {@link android.accounts.AuthenticatorException#AuthenticatorException(String)}
     */
    public AuthenticatorException(String detailMessage) {
        super(detailMessage);
    }
}
