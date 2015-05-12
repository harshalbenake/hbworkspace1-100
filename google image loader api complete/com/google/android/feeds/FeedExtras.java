/*-
 * Copyright (C) 2011 Google Inc.
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

package com.google.android.feeds;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

/**
 * Keys for {@link Cursor#getExtras()}.
 */
public interface FeedExtras {

    /**
     * Specifies an error that was encountered while loading the data.
     * <p>
     * An error does not necessarily imply that the data will be empty; for
     * example, if refreshing cached data fails, the cached data should still be
     * returned.
     * <p>
     * IMPORTANT: Avoid using custom exception classes that the caller might not
     * be able to deserialize.
     * 
     * @see Throwable
     * @see Bundle#getSerializable(String)
     */
    String EXTRA_ERROR = "com.google.feeds.cursor.extra.ERROR";

    /**
     * Specifies an {@link Intent} that starts an {@link Activity} to resolve
     * the error specified by {@link #EXTRA_ERROR}.
     * <p>
     * Examples:
     * <ul>
     * <li>Ask the user to re-enter their account password</li>
     * <li>Direct the user to a Wi-Fi hotspot login page</li>
     * <li>Instruct the user to upgrade their application to a newer version</li>
     * </ul>
     * 
     * @see Intent
     * @see Bundle#getParcelable(String)
     */
    String EXTRA_SOLUTION = "com.google.feeds.cursor.extra.SOLUTION";

    /**
     * Indicates if more items are available.
     * 
     * @see Bundle#getBoolean(String)
     */
    String EXTRA_MORE = "com.google.feeds.cursor.extra.MORE";

    /**
     * The cache timestamp of the {@link Cursor} data.
     * <p>
     * Use this value to decide if the cached data should be refreshed.
     * 
     * @see System#currentTimeMillis()
     * @see Bundle#getLong(String)
     */
    String EXTRA_TIMESTAMP = "com.google.feeds.cursor.extra.TIMESTAMP";
}
