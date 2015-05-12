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

package com.google.android.imageloader;

import java.util.LinkedHashMap;

/**
 * A LRU cache for holding image data or meta-data.
 */
class LruCache<K, V> extends LinkedHashMap<K, V> {

    private static final int INITIAL_CAPACITY = 32;

    // Hold information for at least a few pages full of thumbnails.
    private static final int MAX_CAPACITY = 256;

    private static final float LOAD_FACTOR = 0.75f;

    public LruCache() {
        super(INITIAL_CAPACITY, LOAD_FACTOR, true);
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
        return size() > MAX_CAPACITY;
    }
}
