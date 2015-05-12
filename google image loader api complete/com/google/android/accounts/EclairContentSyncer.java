/*-
 * Copyright (C) 2010 Google Inc.
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

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

/**
 * {@link ContentSyncer} implementation for Eclair and later.
 * <p>
 * This class is just a thin wrapper around the {@link ContentResolver} APIs
 * introduced in Eclair. Period sync is supported through
 * {@link CupcakeContentSyncer}.
 */
class EclairContentSyncer extends CupcakeContentSyncer {

    protected static android.accounts.Account convertAccount(Account account) {
        return new android.accounts.Account(account.name, account.type);
    }

    protected static Account convertAccount(android.accounts.Account account) {
        return new Account(account.name, account.type);
    }

    public EclairContentSyncer(Context context) {
        super(context);
    }

    @Override
    public void setIsSyncable(Account account, String authority, int syncable) {
        ContentResolver.setIsSyncable(convertAccount(account), authority, syncable);
    }

    @Override
    public void setSyncAutomatically(Account account, String authority, boolean sync) {
        ContentResolver.setSyncAutomatically(convertAccount(account), authority, sync);
    }

    @Override
    public int getIsSyncable(Account account, String authority) {
        return ContentResolver.getIsSyncable(convertAccount(account), authority);
    }

    @Override
    public boolean getSyncAutomatically(Account account, String authority) {
        return ContentResolver.getSyncAutomatically(convertAccount(account), authority);
    }

    @Override
    public void requestSync(Account account, String authority, Bundle extras) {
        ContentResolver.requestSync(convertAccount(account), authority, extras);
    }
}
