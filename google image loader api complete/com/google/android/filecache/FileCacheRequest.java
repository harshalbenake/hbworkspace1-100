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

import java.io.BufferedOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.SyncFailedException;
import java.net.CacheRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Saves an HTTP response to a file.
 */
class FileCacheRequest extends CacheRequest {

    private static final int BUFFER_SIZE = 8 * 1024;

    private static File createTempFile(File file) throws IOException {
        String prefix = file.getName();
        while (prefix.length() < 3) {
            // The prefix must be at least three characters long
            prefix += "_";
        }
        String suffix = null; // Use the default: .tmp
        File directory = file.getParentFile();
        File temp = File.createTempFile(prefix, suffix, directory);
        temp.deleteOnExit();
        return temp;
    }

    private final File mFile;

    private final int mResponseCode;

    private final String mResponseMessage;

    private final Map<String, List<String>> mHeaders;

    private TempFileOutputStream mOutputStream;

    public FileCacheRequest(File file, int responseCode, String responseMessage,
            Map<String, List<String>> headers) {
        if (file == null) {
            throw new NullPointerException();
        }
        if (headers == null) {
            throw new NullPointerException();
        }
        mFile = file;
        mResponseCode = responseCode;
        mResponseMessage = responseMessage;
        mHeaders = new TreeMap<String, List<String>>();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();
            if (key != null) {
                mHeaders.put(key, value);
            } else {
            	// Discard original status line
            }
        }

        // Store cgi-style status (no protocol version)
        String status = getStatus();
        if (status != null) {
            mHeaders.put("status", Collections.singletonList(status));
        }
    }

    /**
     * Returns the HTTP status (for example: {@code "200 OK"}), or {@code null}
     * if {@link #mResponseCode} is {@code -1}.
     * <p>
     * This format mirrors that used in CGI scripts, for example:
     *
     * <pre>
     * #!/usr/bin/env python
     * print 'Status: 404 Not Found';
     * </pre>
     *
     * @return the HTTP status code and message (but not the protocol version).
     */
    private String getStatus() {
        if (mResponseCode != -1) {
            int capactity = 3; // 3-digit response code
            if (mResponseMessage != null) {
                capactity += 1; // Space
                capactity += mResponseMessage.length();
            }
            StringBuilder builder = new StringBuilder(capactity);
            builder.append(mResponseCode);
            if (mResponseMessage != null) {
                builder.append(' ');
                builder.append(mResponseMessage);
            }
            return builder.toString();
        } else {
            return null;
        }
    }

    private void writeHeaders(DataOutput dout) throws IOException {
        int headerCount = mHeaders.size();
        dout.writeInt(headerCount);
        for (Map.Entry<String, List<String>> entry : mHeaders.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            dout.writeUTF(key);
            dout.writeInt(values.size());
            for (String value : values) {
                dout.writeUTF(value);
            }
        }
    }

    @Override
    public OutputStream getBody() throws IOException {
        if (mOutputStream != null) {
            return mOutputStream;
        }
        // Create a file Output stream
        File temp = createTempFile(mFile);
        try {
            FileOutputStream fileOutput = new FileOutputStream(temp);
            FileDescriptor fd = fileOutput.getFD();
            OutputStream output = fileOutput;
            try {
                output = new BufferedOutputStream(output, BUFFER_SIZE);

                // Write the HTTP headers
                DataOutputStream dout = new DataOutputStream(output);
                writeHeaders(dout);

                mOutputStream = new TempFileOutputStream(dout, fd, temp, mFile);
                return mOutputStream;
            } finally {
                if (mOutputStream == null) {
                    // The return-statement was not reached
                    output.close();
                }
            }
        } finally {
            if (mOutputStream == null) {
                // The return-statement was not reached
                temp.delete();
            }
        }
    }

    @Override
    public void abort() {
        if (mOutputStream != null) {
            mOutputStream.abort();
        }
    }

    /**
     * A {@link FilterOutputStream} that moves a temporary file to a destination
     * file when it is closed.
     */
    private static class TempFileOutputStream extends FilterOutputStream {

        private final FileDescriptor mFileDescriptor;
        
        private final File mTempFile;

        private final File mFile;

        /**
         * Constructor.
         *
         * @param out the {@link FileOutputStream} to decorate.
         * @param temp the temporary file.
         * @param file the destination file.
         */
        public TempFileOutputStream(OutputStream out, FileDescriptor fd, File temp, File file) {
            super(out);
            if (fd == null) {
                throw new NullPointerException("File descriptor is null");
            }
            if (temp == null) {
              throw new NullPointerException("Temporary file is null");
            }
            if (file == null) {
              throw new NullPointerException("Destination file is null");
            }
            mFileDescriptor = fd;
            mTempFile = temp;
            mFile = file;
        }

        private void moveTempFile() {
        	// Don't throw exceptions because caching
        	// is not critical.
        	//
        	// TODO: Add logging
            mFile.delete();
            mTempFile.renameTo(mFile);
        }

        @Override
        public void close() throws IOException {
            try {
                flush();
                fsync();
                super.close();
                moveTempFile();
            } finally {
                mTempFile.delete();
            }
        }

        private void fsync() throws SyncFailedException {
            mFileDescriptor.sync();
        }

        public void abort() {
            try {
                super.close();
            } catch (IOException e) {
                // Ignore
            } finally {
              mTempFile.delete();
            }
        }
    }
}
