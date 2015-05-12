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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A Cupcake-compatibile implementation of {@link ContentSyncer} where sync is
 * executed by a per-application {@link AbstractSyncService} and sync
 * preferences are stored in a database managed by a
 * {@link DatabaseAuthenticator} declared in the manifest.
 */
class CupcakeContentSyncer extends ContentSyncer {

    private static final String LOG_TAG = "CupcakeContentSyncer";

    private static final String NAMESPACE = "http://schemas.android.com/apk/res/android";

    // Adapted from com.android.internal.util.XmlUtils
    private static final void beginDocument(XmlPullParser parser, String firstElementName)
            throws XmlPullParserException, IOException {
        int type;
        while ((type = parser.next()) != XmlPullParser.START_TAG
                && type != XmlPullParser.END_DOCUMENT) {
        }

        if (type != XmlPullParser.START_TAG) {
            throw new XmlPullParserException("No start tag found");
        }

        if (!parser.getName().equals(firstElementName)) {
            throw new XmlPullParserException("Unexpected start tag: found " + parser.getName()
                    + ", expected " + firstElementName);
        }
    }

    private Context mContext;

    private Map<String, DatabaseAuthenticator> mAuthenticators;

    public CupcakeContentSyncer(Context context) {
        mContext = context;
        mAuthenticators = DatabaseAuthenticator.createDatabaseAuthenticators(context);
    }

    private DatabaseAuthenticator getAuthenticator(String type) {
        return mAuthenticators.get(type);
    }

    @Override
    public void setIsSyncable(Account account, String authority, int syncable) {
        if (account == null || authority == null) {
            throw new NullPointerException();
        }
        // All accounts are syncable by default
    }

    @Override
    public int getIsSyncable(Account account, String authority) {
        if (account == null || authority == null) {
            throw new NullPointerException();
        }
        // All accounts are syncable by default
        return 1;
    }

    @Override
    public void setSyncAutomatically(Account account, String authority, boolean sync) {
        if (account == null || authority == null) {
            throw new NullPointerException();
        }
        DatabaseAuthenticator authenticator = getAuthenticator(account.type);
        if (authenticator != null) {
            authenticator.setSyncAutomatically(account, authority, sync);
        }
    }

    @Override
    public boolean getSyncAutomatically(Account account, String authority) {
        if (account == null || authority == null) {
            throw new NullPointerException();
        }
        DatabaseAuthenticator authenticator = getAuthenticator(account.type);
        if (authenticator != null) {
            return authenticator.getSyncAutomatically(account, authority);
        } else {
            return false;
        }
    }

    private List<ResolveInfo> querySyncAdapterServices() {
        PackageManager pm = mContext.getPackageManager();
        Intent intent = new Intent("android.content.SyncAdapter");
        int flags = PackageManager.GET_META_DATA;
        return pm.queryIntentServices(intent, flags);
    }

    @Override
    public void requestSync(Account account, String authority, Bundle extras) {
        if (account == null || authority == null) {
            throw new NullPointerException();
        }
        for (ResolveInfo service : querySyncAdapterServices()) {
            try {
                ServiceInfo info = service.serviceInfo;

                PackageManager pm = mContext.getPackageManager();
                Resources resources = pm.getResourcesForApplication(info.packageName);

                Bundle metaData = info.metaData;
                int resId = metaData.getInt("android.content.SyncAdapter");

                XmlResourceParser xml = resources.getXml(resId);
                try {
                    beginDocument(xml, "sync-adapter");
                    String contentAuthority = xml.getAttributeValue(NAMESPACE, "contentAuthority");
                    String accountType = xml.getAttributeValue(NAMESPACE, "accountType");
                    String supportsUploadingValue = xml.getAttributeValue(NAMESPACE,
                            "supportsUploading");
                    boolean supportsUploading = "true".equals(supportsUploadingValue);
                    if (contentAuthority.equals(authority)) {
                        if (accountType.equals(account.type)) {
                            Intent serviceIntent = new Intent(
                                    AbstractSyncService.ACTION_REQUEST_SYNC);
                            serviceIntent.setClassName(info.packageName, info.name);
                            serviceIntent.putExtra(AbstractSyncService.EXTRA_ACCOUNT_NAME,
                                    account.name);
                            serviceIntent.putExtra(AbstractSyncService.EXTRA_ACCOUNT_TYPE,
                                    account.type);
                            serviceIntent.putExtra(AbstractSyncService.EXTRA_AUTHORITY, authority);
                            serviceIntent.putExtra(AbstractSyncService.EXTRA_BUNDLE, extras);
                            serviceIntent.putExtra(AbstractSyncService.EXTRA_SUPPORTS_UPLOADING,
                                    supportsUploading);
                            mContext.startService(serviceIntent);
                        }
                    }
                } finally {
                    xml.close();
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "Could not read SyncAdapter meta-data", e);
            }
        }
    }

    private AlarmManager getAlarmManager() {
        return (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    private PendingIntent createOperation(Account account, String authority, Bundle extras) {
        return PeriodicSyncReceiver.createPendingIntent(mContext, account, authority, extras);
    }

    @Override
    public void addPeriodicSync(Account account, String authority, Bundle extras, long pollFrequency) {
        long pollFrequencyMsec = pollFrequency * DateUtils.SECOND_IN_MILLIS;
        AlarmManager manager = getAlarmManager();
        int type = AlarmManager.ELAPSED_REALTIME_WAKEUP;
        long triggerAtTime = SystemClock.elapsedRealtime() + pollFrequencyMsec;
        long interval = pollFrequencyMsec;
        PendingIntent operation = createOperation(account, authority, extras);
        manager.setInexactRepeating(type, triggerAtTime, interval, operation);
    }

    @Override
    public void removePeriodicSync(Account account, String authority, Bundle extras) {
        AlarmManager manager = getAlarmManager();
        PendingIntent operation = createOperation(account, authority, extras);
        manager.cancel(operation);
    }
}
