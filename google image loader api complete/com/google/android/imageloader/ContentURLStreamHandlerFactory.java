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

import android.content.ContentResolver;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * {@link URLStreamHandlerFactory} for {@code content://}, {@code file://}, and
 * {@code android.resource://} URIs.
 */
public class ContentURLStreamHandlerFactory implements URLStreamHandlerFactory {

    private final ContentResolver mResolver;

    public ContentURLStreamHandlerFactory(ContentResolver resolver) {
        if (resolver == null) {
            throw new NullPointerException();
        }
        mResolver = resolver;
    }

    /**
     * {@inheritDoc}
     */
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (ContentResolver.SCHEME_CONTENT.equals(protocol)
                || ContentResolver.SCHEME_FILE.equals(protocol)
                || ContentResolver.SCHEME_ANDROID_RESOURCE.equals(protocol)) {
            return new ContentURLStreamHandler(mResolver);
        } else {
            return null;
        }
    }
}
