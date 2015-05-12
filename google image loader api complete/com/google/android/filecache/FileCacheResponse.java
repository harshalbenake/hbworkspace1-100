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

import android.text.format.DateUtils;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CacheResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads a cached HTTP response from a {@link File}.
 */
class FileCacheResponse extends CacheResponse {

    private static final int BUFFER_SIZE = 8 * 1024;

    private static Map<String, List<String>> readHeaders(DataInput din) throws IOException {
        int keyCount = din.readInt();
        Map<String, List<String>> headers = new HashMap<String, List<String>>(keyCount);
        for (int i = 0; i < keyCount; i++) {
            String key = din.readUTF();
            int valueCount = din.readInt();
            List<String> values = new ArrayList<String>(valueCount);
            for (int j = 0; j < valueCount; j++) {
                String value = din.readUTF();
                values.add(value);
            }

            // All keys must be lower-case because
            // HttpURLConnection#getHeaderField(String key)
            // is implemented as: headers.get(key.toLowerCase())
            key = key.toLowerCase();

            if (key.equalsIgnoreCase("transfer-encoding")) {
                // Remove the transfer encoding because
                // the cached response is already decoded.
            } else {
                headers.put(key, values);
            }
        }
        return headers;
    }

    private final File mFile;

    private Map<String, List<String>> mHeaders;

    private InputStream mInputStream;

    public FileCacheResponse(File file) {
        if (file == null) {
            throw new NullPointerException();
        }
        mFile = file;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, List<String>> getHeaders() throws IOException {
        if (mHeaders == null) {
            if (mInputStream != null) {
                // It should not be possible to set mInputStream
                // without settings mHeaders as well.
                throw new IllegalStateException();
            }
            InputStream input = new FileInputStream(mFile);
            try {
                input = new BufferedInputStream(input, BUFFER_SIZE);

                DataInputStream din = new DataInputStream(input);
                mHeaders = readHeaders(din);

                // TODO: Have an HTTP expert verify that these
                // headers are being used correctly.

                long ageMillis = System.currentTimeMillis() - mFile.lastModified();
                long ageSeconds = ageMillis / DateUtils.SECOND_IN_MILLIS;
                mHeaders.put("age", Arrays.asList(String.valueOf(ageSeconds)));

                // Add localhost to Via header
                List<String> via = mHeaders.get("via");
                if (via != null) {
                    via.add("1.1 localhost");
                } else {
                    mHeaders.put("via", Arrays.asList("1.1 localhost"));
                }

                mInputStream = din;
                return mHeaders;
            } finally {
                if (mInputStream == null) {
                    mHeaders = null;
                    input.close();
                }
            }
        } else {
            return mHeaders;
        }
    }

    @Override
    public InputStream getBody() throws IOException {
        if (mInputStream != null) {
            return mInputStream;
        } else {
            // Calling getHeaders() has the side-effect of setting mInputStream
            getHeaders();
            if (mInputStream == null) {
                // The method getHeaders() must throw an IOException
                // if it does not set mInputStream.
                throw new IllegalStateException();
            }
            return mInputStream;
        }
    }
}
