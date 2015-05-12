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

import org.xml.sax.SAXException;

import android.util.Xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.ContentHandler;
import java.net.URLConnection;

/**
 * {@link java.net.ContentHandler} implementation for reading XML documents.
 * <p>
 * Use {@link #parse(URLConnection, org.xml.sax.ContentHandler)} as a helper to
 * implement the abstract method {@link #getContent(URLConnection)}.
 */
public abstract class XmlContentHandler extends ContentHandler {

    /**
     * Parses a {@link URLConnection} with a SAX
     * {@link org.xml.sax.ContentHandler}, and re-throws any
     * {@link SAXException} as an {@link IOException}.
     * <p>
     * Use this helper to implement the abstract method
     * {@link #getContent(URLConnection)}.
     * 
     * @param connection the {@link URLConnection} to parse.
     * @param contentHandler the XML parser.
     * @throws IOException
     */
    protected final void parse(URLConnection connection, org.xml.sax.ContentHandler contentHandler)
            throws IOException {
        InputStream in = ContentHandlerUtils.getUncompressedInputStream(connection);
        try {
            String encoding = ContentHandlerUtils.getCharSet(connection);
            try {
                Xml.Encoding e = Xml.findEncodingByName(encoding);
                Xml.parse(in, e, contentHandler);
            } catch (SAXException e) {
                // Re-throw SAXException as IOException
                IOException ioe = new IOException();
                ioe.initCause(e);
                throw ioe;
            }
        } finally {
            in.close();
        }
    }
}
