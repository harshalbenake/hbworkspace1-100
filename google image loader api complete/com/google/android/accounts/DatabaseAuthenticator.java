/*-
 * Copyright (C) 2009 The Android Open Source Project
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

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class for authenticators that store authentication tokens in a
 * database.
 * <p>
 * Subclasses generally only need to implement a constructor that accepts a
 * {@link Context} as an argument and {@link #getAuthTokenLabel(String)}. For
 * example,
 *
 * <pre>
 * public class AcmeAuthenticator extends DatabaseAuthenticator {
 *     public AcmeAuthenticator(Context context) {
 *         super(context, &quot;acme_accounts&quot;, AcmeAuthenticatorActivity.class);
 *     }
 *
 *     &#064;Override
 *     public String getAuthTokenLabel(String authTokenType) {
 *         Context context = getContext();
 *         return context.getString(R.string.auth_token_label);
 *     }
 * }
 * </pre>
 *
 * This class can be used to implement a standard
 * {@link android.accounts.AbstractAccountAuthenticator} with the help of
 * {@link DatabaseAuthenticatorAdapter} on devices running Eclair (API level 5).
 * <p>
 * Example {@code AndroidManifest.xml} entry:
 *
 * <pre>
 *         &lt;service android:name=&quot;.app.AcmeAuthenticatorService&quot;&gt;
 *             &lt;intent-filter&gt;
 *                 &lt;action android:name=&quot;android.accounts.AccountAuthenticator&quot; /&gt;
 *                 &lt;action android:name=&quot;com.google.android.accounts.DatabaseAuthenticator&quot; /&gt;
 *             &lt;/intent-filter&gt;
 *             &lt;meta-data android:name=&quot;android.accounts.AccountAuthenticator&quot;
 *                 android:resource=&quot;@xml/account_authenticator&quot; /&gt;
 *             &lt;meta-data
 *                 android:name=&quot;com.google.android.accounts.DatabaseAuthenticator&quot;
 *                 android:resource=&quot;@xml/database_authenticator&quot; /&gt;
 *         &lt;/service&gt;
 *
 * </pre>
 *
 * Example {@code database_authenticator.xml} file:
 *
 * <pre>
 * &lt;account-authenticator xmlns:android=&quot;http://schemas.android.com/apk/res/android&quot;
 *     android:accountType=&quot;com.acme&quot;
 *     android:name=&quot;.app.AcmeAuthenticator&quot; /&gt;
 * </pre>
 */
public abstract class DatabaseAuthenticator {

    private static final String LOG_TAG = "DatabaseAccountManager";

    private static final String KEY_DATABASE_AUTHENTICATOR = "com.google.android.accounts.DatabaseAuthenticator";

    private static final String NAMESPACE = "http://schemas.android.com/apk/res/android";

    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_ACCOUNTS = "accounts";

    private static final String TABLE_AUTH_TOKENS = "auth_tokens";

    private static final String TABLE_SYNC_AUTOMATICALLY = "sync_automatically";

    private static final String COLUMN_ACCOUNT_NAME = "account_name";

    private static final String COLUMN_ACCOUNT_TYPE = "account_type";

    private static final String COLUMN_PASSWORD = "password";

    private static final String COLUMN_AUTH_TOKEN_TYPE = "auth_token_type";

    private static final String COLUMN_AUTH_TOKEN = "auth_token";

    private static final String COLUMN_AUTHORITY = "authority";

    private static final String COLUMN_SYNC_AUTOMATICALLY = "sync_automatically";

    private class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper() {
            super(getContext(), mDatabaseName, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_ACCOUNTS + " ("
                    + (COLUMN_ACCOUNT_NAME + " TEXT NOT NULL, ")
                    + (COLUMN_ACCOUNT_TYPE + " TEXT NOT NULL, ")
                    + (COLUMN_PASSWORD + " TEXT")
                    + ")");
            db.execSQL("CREATE TABLE " + TABLE_AUTH_TOKENS + " ("
                    + (COLUMN_ACCOUNT_NAME + " TEXT NOT NULL, ")
                    + (COLUMN_ACCOUNT_TYPE + " TEXT NOT NULL, ")
                    + (COLUMN_AUTH_TOKEN_TYPE + " TEXT, ")
                    + (COLUMN_AUTH_TOKEN + " TEXT NOT NULL")
                    + ")");
            db.execSQL("CREATE TABLE " + TABLE_SYNC_AUTOMATICALLY + " ("
                    + (COLUMN_ACCOUNT_NAME + " TEXT NOT NULL, ")
                    + (COLUMN_ACCOUNT_TYPE + " TEXT NOT NULL, ")
                    + (COLUMN_AUTHORITY + " TEXT NOT NULL, ")
                    + (COLUMN_SYNC_AUTOMATICALLY + " INTEGER NOT NULL")
                    + ")");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTH_TOKENS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SYNC_AUTOMATICALLY);
            onCreate(db);
        }
    }

    private static boolean hasAccount(SQLiteDatabase db, Account account) {
        String[] projection = {};
        String selection = String.format("%s=? AND %s=?", COLUMN_ACCOUNT_NAME, COLUMN_ACCOUNT_TYPE,
                COLUMN_AUTH_TOKEN_TYPE);
        String[] selectionArgs = new String[] {
                account.name, account.type
        };
        String groupBy = null;
        String having = null;
        String orderBy = null;
        Cursor c = db.query(TABLE_ACCOUNTS, projection, selection, selectionArgs, groupBy, having,
                orderBy);
        try {
            return c.getCount() != 0;
        } finally {
            c.close();
        }
    }

    private static void addAccount(SQLiteDatabase db, Account account, String password) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACCOUNT_NAME, account.name);
        values.put(COLUMN_ACCOUNT_TYPE, account.type);
        if (password != null) {
            values.put(COLUMN_PASSWORD, password);
        }
        db.insert(TABLE_ACCOUNTS, null, values);
    }

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

    private static DatabaseAuthenticator newDatabaseAuthenticatorInstance(Context context,
            String packageName, String className) throws ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException, IllegalAccessException,
            InstantiationException {
        if (className.startsWith(".")) {
            className = packageName + className;
        }
        Class<?> authenticatorClass = Class.forName(className);
        Constructor<?> constructor = authenticatorClass.getConstructor(Context.class);
        return (DatabaseAuthenticator) constructor.newInstance(context);
    }

    static Map<String, DatabaseAuthenticator> createDatabaseAuthenticators(Context context) {
        Map<String, DatabaseAuthenticator> authenticators = new HashMap<String, DatabaseAuthenticator>();
        String packageName = context.getPackageName();
        PackageManager pm = context.getPackageManager();
        Resources resources = context.getResources();
        Intent intent = new Intent(KEY_DATABASE_AUTHENTICATOR);
        int flags = PackageManager.GET_META_DATA;
        for (ResolveInfo resolveInfo : pm.queryIntentServices(intent, flags)) {
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            Bundle metaData = serviceInfo.metaData;
            if (metaData != null && serviceInfo.packageName.equals(packageName)) {
                int resId = metaData.getInt(KEY_DATABASE_AUTHENTICATOR);
                if (resId != 0) {
                    try {
                        XmlResourceParser parser = resources.getXml(resId);
                        beginDocument(parser, "account-authenticator");
                        String name = parser.getAttributeValue(NAMESPACE, "name");
                        String accountType = parser.getAttributeValue(NAMESPACE, "accountType");
                        String maxSdkVersion = parser.getAttributeValue(NAMESPACE, "maxSdkVersion");
                        DatabaseAuthenticator authenticator = newDatabaseAuthenticatorInstance(
                                context, packageName, name);
                        if (maxSdkVersion != null) {
                            authenticator.setMaxSdkVersion(Integer.parseInt(maxSdkVersion));
                        }
                        authenticators.put(accountType, authenticator);
                    } catch (Exception e) {
                        Log.w(LOG_TAG, "Failed  to create authenticator", e);
                    }
                }
            }
        }
        if (authenticators.isEmpty()) {
            Log.w(LOG_TAG, "No authenticators found");
        }
        return authenticators;
    }

    private final Context mContext;

    private final String mDatabaseName;

    private final Class<? extends Activity> mAuthenticatorActivity;

    /**
     * The maximum SDK version for which this {@link DatabaseAuthenticator}
     * should be used. The default is API Level 4 (Donut) because starting in
     * API Level 5 (Eclair), the standard AccountManager API is available, but
     * in API Level 6 and 7, the AccountManager API has some bugs and in rare
     * circumstances it may be desirable for a {@link DatabaseAuthenticator} to
     * be used instead.
     */
    private int mMaxSdkVersion = 4;

    /**
     * Constructs a {@link DatabaseAuthenticator}.
     *
     * @param context the context.
     * @param databaseName the name of the database to store the authentication
     *            tokens and sync settings.
     * @param authenticatorActivity the class of the {@link Activity} to perform
     *            authentication.
     * @throws NullPointerException if any argument is {@code null}
     */
    protected DatabaseAuthenticator(Context context, String databaseName,
            Class<? extends Activity> authenticatorActivity) {
        if (context == null || databaseName == null || authenticatorActivity == null) {
            throw new NullPointerException();
        }
        mContext = context;
        mDatabaseName = databaseName;
        mAuthenticatorActivity = authenticatorActivity;
    }

    int getMaxSdkVersion() {
        return mMaxSdkVersion;
    }

    void setMaxSdkVersion(int version) {
        mMaxSdkVersion = version;
    }

    /**
     * Returns the context passed to the constructor.
     */
    public Context getContext() {
        return mContext;
    }

    private SQLiteDatabase getReadableDatabase() {
        return new DatabaseHelper().getReadableDatabase();
    }

    private SQLiteDatabase getWritableDatabase() {
        return new DatabaseHelper().getWritableDatabase();
    }

    /**
     * Mirrors
     * {@link android.accounts.AbstractAccountAuthenticator#addAccount(android.accounts.AccountAuthenticatorResponse, String, String, String[], Bundle)}
     */
    public Bundle addAccount(String accountType, String authTokenType, String[] requiredFeatures,
            Bundle options) {
        Bundle result = new Bundle();

        Intent intent = new Intent();
        intent.setClass(mContext, mAuthenticatorActivity);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);

        result.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
        result.putParcelable(AccountManager.KEY_INTENT, intent);

        return result;
    }

    /**
     * Mirrors
     * {@link android.accounts.AccountManager#addAccountExplicitly(android.accounts.Account, String, Bundle)}
     */
    public boolean addAccountExplicitly(Account account, String password, Bundle userdata) {
        if (account == null || account.type == null || account.name == null) {
            // The documentation specifies that this method should return false
            // instead of throwing an exception if the account is null.
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            try {
                if (hasAccount(db, account)) {
                    return false;
                } else {
                    addAccount(db, account, password);
                    db.setTransactionSuccessful();
                    return true;
                }
            } finally {
                db.endTransaction();
            }
        } finally {
            db.close();
        }
    }

    /**
     * Mirrors
     * {@link android.accounts.AbstractAccountAuthenticator#confirmCredentials(android.accounts.AccountAuthenticatorResponse, android.accounts.Account, Bundle)}
     */
    public Bundle confirmCredentials(Account account, Bundle options) {
        Bundle result = new Bundle();

        Intent intent = new Intent();
        intent.setClass(mContext, mAuthenticatorActivity);
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, account.type);

        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        result.putParcelable(AccountManager.KEY_INTENT, intent);

        return result;
    }

    /**
     * Mirrors
     * {@link android.accounts.AbstractAccountAuthenticator#editProperties(android.accounts.AccountAuthenticatorResponse, String)}
     */
    public Bundle editProperties(String accountType) {
        // Properties aren't supported
        return Bundle.EMPTY;
    }

    /**
     * Mirrors
     * {@link android.accounts.AbstractAccountAuthenticator#getAuthToken(android.accounts.AccountAuthenticatorResponse, android.accounts.Account, String, Bundle)}
     */
    public Bundle getAuthToken(Account account, String authTokenType, Bundle loginOptions) {
        String table = TABLE_AUTH_TOKENS;
        String[] projection = {
            COLUMN_AUTH_TOKEN
        };
        String selection;
        String[] selectionArgs;

        if (authTokenType != null) {
            selection = String.format("%s=? AND %s=? AND %s=?", COLUMN_ACCOUNT_NAME,
                    COLUMN_ACCOUNT_TYPE, COLUMN_AUTH_TOKEN_TYPE);
            selectionArgs = new String[] {
                    account.name, account.type, authTokenType
            };
        } else {
            selection = String.format("%s=? AND %s=? AND %s IS NULL", COLUMN_ACCOUNT_NAME,
                    COLUMN_ACCOUNT_TYPE, COLUMN_AUTH_TOKEN_TYPE);
            selectionArgs = new String[] {
                    account.name, account.type
            };
        }
        String groupBy = null;
        String having = null;
        String orderBy = null;
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor cursor = db.query(table, projection, selection, selectionArgs, groupBy, having,
                    orderBy);
            try {
                final Bundle result = new Bundle();
                result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(COLUMN_AUTH_TOKEN);
                    String authToken = cursor.getString(columnIndex);
                    result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(mContext, mAuthenticatorActivity);
                    intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name);
                    intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                    result.putParcelable(AccountManager.KEY_INTENT, intent);
                }
                return result;
            } finally {
                cursor.close();
            }
        } finally {
            db.close();
        }
    }

    /**
     * Mirrors
     * {@link android.accounts.AbstractAccountAuthenticator#getAuthTokenLabel(String)}
     */
    public abstract String getAuthTokenLabel(String authTokenType);

    /**
     * Mirrors
     * {@link android.accounts.AbstractAccountAuthenticator#hasFeatures(android.accounts.AccountAuthenticatorResponse, android.accounts.Account, String[])}
     *
     * @throws NetworkErrorException
     */
    public Bundle hasFeatures(Account account, String[] features) throws NetworkErrorException {
        Bundle bundle = new Bundle();
        boolean featuresEmpty = features == null || features.length == 0;
        bundle.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, featuresEmpty);
        return bundle;
    }

    /**
     * Mirrors
     * {@link android.accounts.AbstractAccountAuthenticator#updateCredentials(android.accounts.AccountAuthenticatorResponse, android.accounts.Account, String, Bundle)}
     */
    public Bundle updateCredentials(Account account, String authTokenType, Bundle loginOptions) {
        Bundle result = new Bundle();

        Intent intent = new Intent();
        intent.setClass(mContext, mAuthenticatorActivity);
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, account.type);

        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        result.putParcelable(AccountManager.KEY_INTENT, intent);

        return result;
    }

    /**
     * Sets the auth token of a specific type for a specific account, replacing
     * any existing tokens belonging to the same account of the same type.
     *
     * @param account the account to which the auth token belongs.
     * @param authTokenType the type of the authentication token, or {@code
     *            null}.
     * @param authToken the auth token.
     * @return {@code true} if the auth token was saved, {@code false}
     *         otherwise.
     * @throws NullPointerException if any of the arguments are {@code null}.
     */
    public boolean setAuthToken(Account account, String authTokenType, String authToken) {
        if (account == null || account.type == null || account.name == null || authToken == null) {
            throw new NullPointerException();
        }

        String whereClause;
        String[] whereArgs;
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACCOUNT_NAME, account.name);
        values.put(COLUMN_ACCOUNT_TYPE, account.type);
        if (authTokenType != null) {
            values.put(COLUMN_AUTH_TOKEN_TYPE, authTokenType);
            whereClause = String.format("%s=? AND %s=? AND %s=?", COLUMN_ACCOUNT_NAME,
                    COLUMN_ACCOUNT_TYPE, COLUMN_AUTH_TOKEN_TYPE);
            whereArgs = new String[] {
                    account.name, account.type, authTokenType
            };
        } else {
            whereClause = String.format("%s=? AND %s=? AND %s IS NULL", COLUMN_ACCOUNT_NAME,
                    COLUMN_ACCOUNT_TYPE, COLUMN_AUTH_TOKEN_TYPE);
            whereArgs = new String[] {
                    account.name, account.type
            };
        }
        values.put(COLUMN_AUTH_TOKEN, authToken);

        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            try {
                db.delete(TABLE_AUTH_TOKENS, whereClause, whereArgs);
                db.insert(TABLE_AUTH_TOKENS, null, values);
                db.setTransactionSuccessful();
                return true;
            } finally {
                db.endTransaction();
            }
        } finally {
            db.close();
        }
    }

    /**
     * Mirrors {@link android.accounts.AccountManager#getAccountsByType(String)}
     */
    public Account[] getAccountsByType(String type) {
        if (type == null) {
            throw new NullPointerException();
        }
        String table = TABLE_ACCOUNTS;
        String[] columns = {
            COLUMN_ACCOUNT_NAME
        };
        String selection = String.format("%s=?", COLUMN_ACCOUNT_TYPE);
        String[] selectionArgs = new String[] {
            type
        };
        String groupBy = null;
        String having = null;
        String orderBy = null;
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having,
                    orderBy);
            try {
                int count = cursor.getCount();
                Account[] accounts = new Account[count];
                int columnIndex = cursor.getColumnIndex(COLUMN_ACCOUNT_NAME);
                for (int pos = 0; pos < count; pos++) {
                    cursor.moveToPosition(pos);
                    String name = cursor.getString(columnIndex);
                    accounts[pos] = new Account(name, type);
                }
                return accounts;
            } finally {
                cursor.close();
            }
        } finally {
            db.close();
        }
    }

    /**
     * Mirrors
     * {@link android.accounts.AccountManager#getAccountsByTypeAndFeatures(String, String[], android.accounts.AccountManagerCallback, android.os.Handler)}
     */
    public Account[] getAccountsByTypeAndFeatures(String type, String[] features) {
        return getAccountsByType(type);
    }

    /**
     * Mirrors
     * {@link android.accounts.AccountManager#removeAccount(android.accounts.Account, android.accounts.AccountManagerCallback, android.os.Handler)}
     */
    public boolean removeAccount(Account account) {
        if (account == null || account.type == null || account.name == null) {
            throw new NullPointerException();
        }
        String whereClause = String.format("%s=? AND %s=?", COLUMN_ACCOUNT_NAME,
                COLUMN_ACCOUNT_TYPE);
        String[] whereArgs = new String[] {
                account.name, account.type
        };
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            try {
                db.delete(TABLE_AUTH_TOKENS, whereClause, whereArgs);
                db.delete(TABLE_SYNC_AUTOMATICALLY, whereClause, whereArgs);
                db.setTransactionSuccessful();
                return true;
            } finally {
                db.endTransaction();
            }
        } finally {
            db.close();
        }
    }

    /**
     * Mirrors
     * {@link android.accounts.AccountManager#invalidateAuthToken(String, String)}
     */
    public void invalidateAuthToken(String accountType, String authToken) {
        if (accountType == null || authToken == null) {
            throw new NullPointerException();
        }
        String whereClause = String.format("%s=? AND %s=?", COLUMN_ACCOUNT_TYPE, COLUMN_AUTH_TOKEN);
        String[] whereArgs = new String[] {
                accountType, authToken
        };
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            try {
                db.delete(TABLE_AUTH_TOKENS, whereClause, whereArgs);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } finally {
            db.close();
        }
    }

    /**
     * Mirrors
     * {@link ContentResolver#setSyncAutomatically(android.accounts.Account, String, boolean)}
     */
    public void setSyncAutomatically(Account account, String authority, boolean sync) {
        if (account == null || account.type == null || account.name == null || authority == null) {
            throw new NullPointerException();
        }
        SQLiteDatabase db = getWritableDatabase();
        try {
            if (sync && !getSyncAutomatically(account, authority)) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_ACCOUNT_NAME, account.name);
                values.put(COLUMN_ACCOUNT_TYPE, account.type);
                values.put(COLUMN_AUTHORITY, authority);
                values.put(COLUMN_SYNC_AUTOMATICALLY, 1);
                db.insert(TABLE_SYNC_AUTOMATICALLY, null, values);
            } else {
                String table = TABLE_SYNC_AUTOMATICALLY;
                String whereClause = String.format("%s=? AND %s=? AND %s=?", COLUMN_ACCOUNT_NAME,
                        COLUMN_ACCOUNT_TYPE, COLUMN_AUTHORITY);
                String[] whereArgs = {
                        account.name, account.type, authority
                };
                db.delete(table, whereClause, whereArgs);
            }
        } finally {
            db.close();
        }
    }

    /**
     * Mirrors
     * {@link ContentResolver#getSyncAutomatically(android.accounts.Account, String)}
     */
    public boolean getSyncAutomatically(Account account, String authority) {
        if (account == null || account.type == null || account.name == null || authority == null) {
            throw new NullPointerException();
        }
        String table = TABLE_SYNC_AUTOMATICALLY;
        String[] columns = {};
        String selection = String.format("%s=? AND %s=? AND %s=?", COLUMN_ACCOUNT_NAME,
                COLUMN_ACCOUNT_TYPE, COLUMN_AUTHORITY);
        String[] selectionArgs = {
                account.name, account.type, authority
        };
        String groupBy = null;
        String having = null;
        String orderBy = null;
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having,
                    orderBy);
            try {
                return cursor.moveToFirst();
            } finally {
                cursor.close();
            }
        } finally {
            db.close();
        }
    }

    /**
     * Updates the database to reflect a change in the set of logged-in
     * accounts.
     * <p>
     * This method should be called when the broadcast
     * {@link AccountManager#LOGIN_ACCOUNTS_CHANGED_ACTION} is received.
     *
     * @param accounts the current set of logged-in accounts as returned by
     *            {@link AccountManager#getAccountsByType(String)}.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public void onLoginAccountsChanged(Account[] accounts) {
        deleteAccountsNotIn(accounts);
    }

    private void deleteAccountsNotIn(Account[] accounts) {
        String whereClause = null;
        String[] whereArgs = null;
        if (accounts.length != 0) {
            StringBuilder where = new StringBuilder();
            whereArgs = new String[accounts.length * 2];
            where.append("NOT (");
            for (int i = 0; i < accounts.length; i++) {
                Account account = accounts[i];
                if (i != 0) {
                    where.append(" OR ");
                }
                where.append("(");
                where.append(COLUMN_ACCOUNT_NAME).append('=').append('?');
                where.append(" AND ");
                where.append(COLUMN_ACCOUNT_TYPE).append('=').append('?');
                where.append(")");
                whereArgs[i * 2] = account.name;
                whereArgs[i * 2 + 1] = account.type;
            }
            where.append(")");
            whereClause = where.toString();
        }

        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(TABLE_SYNC_AUTOMATICALLY, whereClause, whereArgs);
            db.delete(TABLE_AUTH_TOKENS, whereClause, whereArgs);
            db.delete(TABLE_ACCOUNTS, whereClause, whereArgs);
        } finally {
            db.close();
        }
    }
}
