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
 * {@link ContentSyncer} implementation for Froyo and later.
 * <p>
 * This class is just a thin wrapper around the {@link ContentResolver} APIs
 * introduced in Froyo. Although {@link FroyoContentSyncer} indirectly extends
 * {@link CupcakeContentSyncer}, it does not use any of the functionality
 * provided by {@link CupcakeContentSyncer}.
 */
class FroyoContentSyncer extends EclairContentSyncer {

    public FroyoContentSyncer(Context context) {
        super(context);
    }

    @Override
    public void addPeriodicSync(Account account, String authority, Bundle extras, long pollFrequency) {
        ContentResolver.addPeriodicSync(convertAccount(account), authority, extras, pollFrequency);
    }

    @Override
    public void removePeriodicSync(Account account, String authority, Bundle extras) {
        ContentResolver.removePeriodicSync(convertAccount(account), authority, extras);
    }
}
