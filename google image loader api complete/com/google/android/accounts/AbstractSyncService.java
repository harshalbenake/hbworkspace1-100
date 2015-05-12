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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Abstract base class for services that invoke
 * {@link AbstractSyncAdapter#onPerformSync(Account, Bundle, String)}.
 * <p>
 * Returns {@link AbstractThreadedSyncAdapter#getSyncAdapterBinder()} for
 * {@link Service#onBind(Intent)} on Android 2.0 and handles
 * {@link #ACTION_REQUEST_SYNC} intents dispatched by {@link ContentSyncer} on
 * earlier Android platform releases.
 */
public abstract class AbstractSyncService extends IntentService {

    // Use the action that is already required by the Android 2.0 specification
    static final String ACTION_REQUEST_SYNC = "android.content.SyncAdapter";

    static final String EXTRA_ACCOUNT_NAME = "com.google.android.accounts.intent.extra.ACCOUNT_NAME";

    static final String EXTRA_ACCOUNT_TYPE = "com.google.android.accounts.intent.extra.ACCOUNT_TYPE";

    static final String EXTRA_AUTHORITY = "com.google.android.accounts.intent.extra.AUTHORITY";

    static final String EXTRA_SUPPORTS_UPLOADING = "com.google.android.accounts.intent.extra.SUPPORTS_UPLOADING";

    static final String EXTRA_BUNDLE = "com.google.android.accounts.intent.extra.BUNDLE";

    // Determine the SDK version in a way that is compatible with API level 3.
    private static final int SDK = Integer.parseInt(Build.VERSION.SDK);

    private static final int ECLAIR = 5;

    private final String mTag;

    private final int mNotificationId;

    /**
     * Constructs an {@link AbstractSyncAdapter}.
     *
     * @param name the service name (for logging purposes)
     * @param priority the thread priority for the service (for example,
     *            {@link Process#THREAD_PRIORITY_BACKGROUND}).
     * @param notificationId the notification ID to use.
     */
    protected AbstractSyncService(String name, int priority, int notificationId) {
        super(name, priority);
        mTag = name;
        mNotificationId = notificationId;
    }

    /**
     * Creates the sync adapter for this service.
     */
    protected abstract AbstractSyncAdapter createSyncAdapter();

    /**
     * Returns the notification title.
     *
     * @see Notification#setLatestEventInfo(Context, CharSequence, CharSequence,
     *      PendingIntent)
     */
    protected abstract CharSequence createNotificationTitle();

    /**
     * Returns the notification text.
     *
     * @see Notification#setLatestEventInfo(Context, CharSequence, CharSequence,
     *      PendingIntent)
     */
    protected abstract CharSequence createNotificationText();

    /**
     * Returns the notification {@link Intent}.
     * <p>
     * The default implementation returns an {@link Intent} that does nothing.
     *
     * @see Notification#setLatestEventInfo(Context, CharSequence, CharSequence,
     *      PendingIntent)
     */
    protected PendingIntent createNotificationIntent() {
        Context context = this;
        int requestCode = 0;
        Intent intent = new Intent();
        int flags = 0;
        return PendingIntent.getBroadcast(context, requestCode, intent, flags);
    }

    private boolean isSyncEnabled() {
        // SystemProperties is not accessible to third-party applications,
        // so assume that sync is always enabled.
        return true;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (ACTION_REQUEST_SYNC.equals(action)) {
            String accountName = intent.getStringExtra(EXTRA_ACCOUNT_NAME);
            String accountType = intent.getStringExtra(EXTRA_ACCOUNT_TYPE);
            String authority = intent.getStringExtra(EXTRA_AUTHORITY);
            boolean supportsUploading = intent.getBooleanExtra(EXTRA_SUPPORTS_UPLOADING, false);
            Bundle extras = intent.getBundleExtra(EXTRA_BUNDLE);
            performSync(accountName, accountType, authority, supportsUploading, extras);
        }
    }

    // This code is based on android.content.SyncManager#scheduleSync(...)
    private void performSync(String accountName, String accountType, String authority,
            boolean supportsUploading, Bundle extras) {
        if (accountName == null || accountType == null) {
            Log.e(mTag, "not syncing because account was not specified");
            return;
        }
        if (authority == null) {
            Log.e(mTag, "not syncing because authority was not specified");
            return;
        }
        if (!isSyncEnabled()) {
            Log.v(mTag, "not syncing because sync is disabled");
            return;
        }

        Object service = getSystemService(CONNECTIVITY_SERVICE);
        ConnectivityManager cm = (ConnectivityManager) service;
        boolean backgroundDataUsageAllowed = cm.getBackgroundDataSetting();

        if (extras == null) {
            extras = new Bundle();
        }

        boolean uploadOnly = extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, false);
        boolean manualSync = extras.getBoolean(ContentResolver.SYNC_EXTRAS_FORCE, false);

        Context context = this;
        ContentSyncer cs = ContentSyncer.get(context);
        Account account = new Account(accountName, accountType);
        int isSyncable = cs.getIsSyncable(account, authority);
        if (isSyncable == 0) {
            Log.v(mTag, "not syncing because account is not syncable for authority");
            return;
        }

        if (!supportsUploading && uploadOnly) {
            Log.v(mTag, "not syncing because adapter does not support uploading");
            return;
        }

        boolean syncAutomatically = cs.getSyncAutomatically(account, authority);
        boolean syncAllowed = manualSync || (backgroundDataUsageAllowed && syncAutomatically);

        Bundle extrasCopy = extras;
        if (isSyncable < 0) {
            extrasCopy = new Bundle(extras);
            // ContentResolver.SYNC_EXTRAS_INITIALIZE
            // is not defined until API level 5
            final String SYNC_EXTRAS_INITIALIZE = "initialize";
            extrasCopy.putBoolean(SYNC_EXTRAS_INITIALIZE, true);
        } else {
            if (!syncAllowed) {
                if (Log.isLoggable(mTag, Log.DEBUG)) {
                    Log.v(mTag, "sync of " + account + ", " + authority
                            + " is not allowed, dropping request");
                } else {
                    Log.v(mTag, "sync of account is not allowed, dropping request");
                }
                return;
            }
        }

        // Prevent the system from killing the process while a notification is
        // shown, otherwise the notification will remain visible if the service
        // is killed in the middle of a sync operation.
        startForegroundCompat(mNotificationId, createNotification(authority));
        try {
            AbstractSyncAdapter syncAdapter = createSyncAdapter();
            syncAdapter.onPerformSync(account, extras, authority);
        } finally {
            stopForegroundCompat(mNotificationId);
        }
    }

    private Notification createNotification(String authority) {
        int icon = android.R.drawable.stat_notify_sync;
        String tickerText = null;
        long when = 0;
        Notification notification = new Notification(icon, tickerText, when);
        Context context = this;
        CharSequence contentTitle = createNotificationTitle();
        CharSequence contentText = createNotificationText();
        PendingIntent contentIntent = createNotificationIntent();
        notification.when = System.currentTimeMillis();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        return notification;
    }

    @Override
    public void onDestroy() {
        stopForegroundCompat(mNotificationId);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (SDK >= ECLAIR) {
            Context context = this;
            AbstractSyncAdapter adapter = createSyncAdapter();
            return new Adapter(context, adapter).getSyncAdapterBinder();
        } else {
            return null;
        }
    }
}

class Adapter {

    private static final boolean AUTO_INITIALIZE = true;

    private static Account convertAccount(android.accounts.Account account) {
        return new Account(account.name, account.type);
    }

    private final Context mContext;

    private final AbstractSyncAdapter mSyncAdapter;

    public Adapter(Context context, AbstractSyncAdapter syncAdapter) {
        mContext = context;
        mSyncAdapter = syncAdapter;
    }

    public IBinder getSyncAdapterBinder() {
        return new AbstractThreadedSyncAdapter(mContext, AUTO_INITIALIZE) {
            @Override
            public void onPerformSync(android.accounts.Account account, Bundle extras,
                    String authority, ContentProviderClient provider, SyncResult syncResult) {
                mSyncAdapter.onPerformSync(convertAccount(account), extras, authority);
            }
        }.getSyncAdapterBinder();
    }
}

// A copy of android.app.IntentService with added thread priority parameter.
abstract class IntentService extends CompatService {
    private volatile Looper mServiceLooper;

    private volatile ServiceHandler mServiceHandler;

    private final String mName;

    private final int mPriority;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            onHandleIntent((Intent) msg.obj);
            stopSelf(msg.arg1);
        }
    }

    public IntentService(String name) {
        mName = name;
        mPriority = Process.THREAD_PRIORITY_DEFAULT;
    }

    public IntentService(String name, int priority) {
        mName = name;
        mPriority = priority;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("IntentService[" + mName + "]", mPriority);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
    }

    @Override
    public void onDestroy() {
        mServiceLooper.quit();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Invoked on the Handler thread with the {@link Intent} that is passed to
     * {@link #onStart}. Note that this will be invoked from a different thread
     * than the one that handles the {@link #onStart} call.
     */
    protected abstract void onHandleIntent(Intent intent);
}


// http://developer.android.com/reference/android/app/Service.html#startForeground(int, android.app.Notification)
abstract class CompatService extends Service {

    private NotificationManager mNotificationManager;

    private Method mSetForeground;

    private Method mStartForeground;

    private Method mStopForeground;

    @Override
    public void onCreate() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mStartForeground = getMethod("startForeground", int.class, Notification.class);
        mStopForeground = getMethod("stopForeground", boolean.class);
        mSetForeground = getMethod("setForeground", boolean.class);
    }

    /**
     * This is a wrapper around {@link #startForeground(int, Notification)},
     * using the older APIs if it is not available.
     */
    public void startForegroundCompat(int id, Notification notification) {
        if (mStartForeground != null) {
            invokeMethod(mStartForeground, Integer.valueOf(id), notification);
        } else {
            invokeMethod(mSetForeground, Boolean.TRUE);
            mNotificationManager.notify(id, notification);
        }
    }

    /**
     * This is a wrapper around {@link #stopForeground(boolean)}, using the
     * older APIs if it is not available.
     */
    public void stopForegroundCompat(int id) {
        if (mStopForeground != null) {
            Boolean removeNotification = Boolean.TRUE;
            invokeMethod(mStopForeground, removeNotification);
        } else {
            mNotificationManager.cancel(id);
            invokeMethod(mSetForeground, Boolean.FALSE);
        }
    }

    private Method getMethod(String name, Class<?>... parameterTypes) {
        try {
            return Service.class.getMethod(name, parameterTypes);
        } catch (SecurityException e) {
            return null;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private void invokeMethod(Method method, Object... args) {
        try {
            Object receiver = this;
            method.invoke(receiver, args);
        } catch (InvocationTargetException e) {
            throw (Error) new AssertionError().initCause(e);
        } catch (IllegalAccessException e) {
            throw (Error) new AssertionError().initCause(e);
        }
    }
}
