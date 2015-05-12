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

package com.google.android.feeds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ContentHandler;
import java.net.URLConnection;

/**
 * A {@link ContentHandler} for reading data in JSON format.
 */
public abstract class JsonContentHandler extends ContentHandler {

    @Override
    public Object getContent(URLConnection connection) throws IOException {
        String json = ContentHandlerUtils.toString(connection);
        try {
            // Pass the JSON string to handler where it can be
            // interpreted as an object or an array.
            return getContent(json);
        } catch (JSONException e) {
            // Re-throw JSONException as IOException because
            // ContentHandler implementations are only allowed 
        	// to throw IOExceptions.
            IOException ioe = new IOException();
            ioe.initCause(e);
            throw ioe;
        }
    }

    /**
     * Parses the given JSON content.
     * 
     * @param source the JSON source.
     * @return the value to return from {@link #getContent(URLConnection)}.
     * @throws JSONException if the JSON is not well-formed.
     * @see JSONObject#JSONObject(String)
     * @see JSONArray#JSONArray(String)
     */
    protected abstract Object getContent(String source) throws JSONException;
}
