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

package com.google.android.filecache;

import java.net.URLConnection;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides case-insensitive access to the request properties of a
 * {@link URLConnection} via the {@link Map} interface.
 * <p>
 * The method {@link URLConnection#getRequestProperties()} cannot be called
 * after a connection is established because it is mutable, but
 * {@link URLConnection#getRequestProperty(String)} can be. This class provides
 * the same interface as the object returned by
 * {@link URLConnection#getRequestProperties()}, but calls through to
 * {@link URLConnection#getRequestProperty(String)} so that the request
 * properties can be accessed after a connection has been established.
 * <p>
 * The {@link Map} returned by {@link URLConnection#getRequestProperties()} on
 * Android is case-sensitive, while
 * {@link URLConnection#getRequestProperty(String)} is not.
 */
class RequestPropertiesMap implements Map<String, List<String>> {

    private final URLConnection mConnection;

    public RequestPropertiesMap(URLConnection connection) {
        mConnection = connection;
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsKey(Object key) {
        if (key instanceof String) {
            String field = (String) key;
            return mConnection.getRequestProperty(field) != null;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Set<Entry<String, List<String>>> entrySet() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public List<String> get(Object key) {
        if (key instanceof String) {
            String field = (String) key;
            String value = mConnection.getRequestProperty(field);
            return value != null ? Collections.singletonList(value) : null;
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> keySet() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public List<String> put(String key, List<String> value) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void putAll(Map<? extends String, ? extends List<String>> value) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public List<String> remove(Object key) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Collection<List<String>> values() {
        throw new UnsupportedOperationException();
    }
}
