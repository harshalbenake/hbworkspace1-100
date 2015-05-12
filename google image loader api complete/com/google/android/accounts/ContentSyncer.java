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
import android.os.Build;
import android.os.Bundle;

/**
 * Mirrors the sync-related methods of {@link ContentResolver}.
 */
public abstract class ContentSyncer {

    // Determine the SDK version in a way that is compatible with API level 3.
    private static final int SDK = Integer.parseInt(Build.VERSION.SDK);

    private static final int CUPCAKE = 3;

    private static final int ECLAIR = 5;

    private static final int FROYO = 8;

    /**
     * Mirrors {@link ContentResolver#SYNC_EXTRAS_IGNORE_BACKOFF}.
     * <p>
     * Falls-back to {@link ContentResolver#SYNC_EXTRAS_FORCE} on older platform
     * versions.
     */
    public static final String SYNC_EXTRAS_IGNORE_BACKOFF = SDK >= FROYO ? "ignore_backoff"
            : "force";

    /**
     * Mirrors {@link ContentResolver#SYNC_EXTRAS_IGNORE_SETTINGS}.
     * <p>
     * Falls-back to {@link ContentResolver#SYNC_EXTRAS_FORCE} on older platform
     * versions.
     */
    public static final String SYNC_EXTRAS_IGNORE_SETTINGS = SDK >= FROYO ? "ignore_settings"
            : "force";

    /**
     * Mirrors {@link ContentResolver#SYNC_EXTRAS_FORCE}.
     */
    @Deprecated
    public static final String SYNC_EXTRAS_FORCE = "force";

    /**
     * Mirrors {@link ContentResolver#SYNC_EXTRAS_MANUAL}.
     */
    public static final String SYNC_EXTRAS_MANUAL = "force";

    /**
     * Mirrors {@link ContentResolver#SYNC_EXTRAS_UPLOAD}.
     */
    public static final String SYNC_EXTRAS_UPLOAD = "upload";

    /**
     * Mirrors {@link ContentResolver#SYNC_EXTRAS_OVERRIDE_TOO_MANY_DELETIONS}.
     */
    public static final String SYNC_EXTRAS_OVERRIDE_TOO_MANY_DELETIONS = "deletions_override";

    /**
     * Mirrors {@link ContentResolver#SYNC_EXTRAS_INITIALIZE}.
     */
    public static final String SYNC_EXTRAS_INITIALIZE = "initialize";

    /**
     * Mirrors {@link ContentResolver#SYNC_EXTRAS_EXPEDITED}.
     */
    public static final String SYNC_EXTRAS_EXPEDITED = "expedited";

    /**
     * Mirrors {@link ContentResolver#SYNC_EXTRAS_DO_NOT_RETRY}.
     */
    public static final String SYNC_EXTRAS_DO_NOT_RETRY = "do_not_retry";

    /**
     * Mirrors {@link ContentResolver#SYNC_EXTRAS_DISCARD_LOCAL_DELETIONS}.
     */
    public static final String SYNC_EXTRAS_DISCARD_LOCAL_DELETIONS = "discard_deletions";

    public static ContentSyncer get(Context context) {
        if (SDK >= FROYO) {
            return new FroyoContentSyncer(context);
        } else if (SDK >= ECLAIR) {
            return new EclairContentSyncer(context);
        } else if (SDK >= CUPCAKE) {
            return new CupcakeContentSyncer(context);
        } else {
            throw new IllegalStateException("API version not supported: " + SDK);
        }
    }

    /**
     * Mirrors
     * {@link ContentResolver#setIsSyncable(android.accounts.Account, String, int)}
     */
    public abstract void setIsSyncable(Account account, String authority, int syncable);

    /**
     * Mirrors
     * {@link ContentResolver#getIsSyncable(android.accounts.Account, String)}
     */
    public abstract int getIsSyncable(Account account, String authority);

    /**
     * Mirrors
     * {@link ContentResolver#setSyncAutomatically(android.accounts.Account, String, boolean)}
     */
    public abstract void setSyncAutomatically(Account account, String authority, boolean sync);

    /**
     * Mirrors
     * {@link ContentResolver#getSyncAutomatically(android.accounts.Account, String)}
     */
    public abstract boolean getSyncAutomatically(Account account, String authority);

    /**
     * Mirrors
     * {@link ContentResolver#requestSync(android.accounts.Account, String, Bundle)}
     */
    public abstract void requestSync(Account account, String authority, Bundle extras);

    /**
     * Mirrors
     * {@link ContentResolver#addPeriodicSync(android.accounts.Account, String, Bundle, long)}
     */
    public abstract void addPeriodicSync(Account account, String authority, Bundle extras,
            long pollFrequency);
    /**
     * Mirrors
     * {@link ContentResolver#removePeriodicSync(android.accounts.Account, String, Bundle)}
     */
    public abstract void removePeriodicSync(Account account, String authority, Bundle extras);
}
