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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * A {@link BroadcastReceiver} that handles {@link AlarmManager} broadcasts to
 * perform periodic sync operations on platform versions before Froyo.
 * <p>
 * You must add this receiver to your manifest for
 * {@link ContentSyncer#addPeriodicSync(Account, String, Bundle, long)} to
 * function.
 */
public final class PeriodicSyncReceiver extends BroadcastReceiver {

    private static final String KEY_ACCOUNT_NAME = "authAccount";

    private static final String KEY_ACCOUNT_TYPE = "accountType";

    private static final String KEY_AUTHORITY = "authority";

    private static final String KEY_USERDATA = "userdata";

    static Intent createIntent(Context context, Account account, String authority, Bundle extras) {
        Intent intent = new Intent(context, PeriodicSyncReceiver.class);
        intent.putExtra(KEY_ACCOUNT_NAME, account.name);
        intent.putExtra(KEY_ACCOUNT_TYPE, account.type);
        intent.putExtra(KEY_AUTHORITY, authority);
        intent.putExtra(KEY_USERDATA, extras);
        return intent;
    }

    static PendingIntent createPendingIntent(Context context, Account account, String authority,
            Bundle extras) {
        int requestCode = 0;
        Intent intent = createIntent(context, account, authority, extras);
        int flags = 0;
        return PendingIntent.getBroadcast(context, requestCode, intent, flags);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String accountName = intent.getStringExtra(KEY_ACCOUNT_NAME);
        String accountType = intent.getStringExtra(KEY_ACCOUNT_TYPE);
        String authority = intent.getStringExtra(KEY_AUTHORITY);
        Bundle extras = intent.getBundleExtra(KEY_USERDATA);
        Account account = new Account(accountName, accountType);
        ContentSyncer syncer = ContentSyncer.get(context);
        if (syncer.getSyncAutomatically(account, authority)) {
            syncer.requestSync(account, authority, extras);
        } else {
            syncer.removePeriodicSync(account, authority, extras);
        }
    }
}
