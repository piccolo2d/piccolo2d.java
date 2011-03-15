/*
 * Copyright (c) 2008-2011, Piccolo2D project, http://piccolo2d.org
 * Copyright (c) 1998-2008, University of Maryland
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * None of the name of the University of Maryland, the name of the Piccolo2D project, or the names of its
 * contributors may be used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.piccolo2d.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * <b>PObjectOutputStream</b> is an extension of ObjectOutputStream to handle
 * optional elements. This is similar to the concept of Java's
 * "weak references", but applied to object serialization rather than garbage
 * collection. Here, PObjectOutputStream provides a method,
 * <code>writeConditionalObject</code>, which only serializes the specified
 * object to the stream if there is a strong reference (if it has been written
 * somewhere else using writeObject()) to that object elsewhere in the stream.
 * <p>
 * To discover strong references to objects, PObjectOutputStream uses a
 * two-phase writing process. First, a "discovery" phase is used to find out
 * what objects are about to be serialized. This works by effectively
 * serializing the object graph to /dev/null, recording which objects are
 * unconditionally written using the standard writeObject method. Then, in the
 * second "write" phase, ObjectOutputStream actually serializes the data to the
 * output stream. During this phase, calls to writeConditionalObject() will only
 * write the specified object if the object was found to be serialized during
 * the discovery stage. If the object was not recorded during the discovery
 * stage, a an optional null (the default) is unconditionally written in place
 * of the object. To skip writting out the null use
 * <code>writeConditionalObject(object, false)</code>
 * <p>
 * By careful implementation of readObject and writeObject methods, streams
 * serialized using PObjectOutputStream can be deserialized using the standard
 * ObjectInputStream.
 * <p>
 * 
 * @version 1.0
 * @author Jon Meyer
 * @author Jesse Grosjean
 */
public class PObjectOutputStream extends ObjectOutputStream {

    private boolean writingRoot;
    private final HashMap unconditionallyWritten;

    /**
     * Transform the given object into an array of bytes.
     * 
     * @param object the object to be transformed
     * @return array of bytes representing the given object
     * @throws IOException when serialization system throws one
     */
    public static byte[] toByteArray(final Object object) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PObjectOutputStream zout = new PObjectOutputStream(out);
        zout.writeObjectTree(object);
        return out.toByteArray();
    }

    /**
     * Constructs a PObjectOutputStream that wraps the provided OutputStream.
     * 
     * @param out underlying outputstream that will receive the serialized
     *            objects
     * 
     * @throws IOException when underlying subsystem throws one
     */
    public PObjectOutputStream(final OutputStream out) throws IOException {
        super(out);
        unconditionallyWritten = new HashMap();
    }

    /**
     * Writes the provided object to the underlying stream like an ordination
     * ObjectOutputStream except that it does not record duplicates at all.
     * 
     * @param object object to be serialized
     * 
     * @throws IOException when underlying subsystem throws one
     */
    public void writeObjectTree(final Object object) throws IOException {
        writingRoot = true;
        recordUnconditionallyWritten(object); // record pass
        writeObject(object); // write pass
        writingRoot = false;
    }

    /**
     * Writes the given object, but only if it was not in the object tree
     * multiple times.
     * 
     * @param object object to write to the stream.
     * @throws IOException when underlying subsystem throws one
     */
    public void writeConditionalObject(final Object object) throws IOException {
        if (!writingRoot) {
            throw new RuntimeException(
                    "writeConditionalObject() may only be called when a root object has been written.");
        }

        if (unconditionallyWritten.containsKey(object)) {
            writeObject(object);
        }
        else {
            writeObject(null);
        }
    }

    /**
     * Resets the ObjectOutputStream clearing any memory about objects already
     * being written while it's at it.
     * 
     * @throws IOException when underlying subsystem throws one
     */
    public void reset() throws IOException {
        super.reset();
        unconditionallyWritten.clear();
    }

    /**
     * Performs a scan of objects that can be serialized once.
     * 
     * @param aRoot Object from which to start the scan
     * @throws IOException when serialization fails
     */
    protected void recordUnconditionallyWritten(final Object aRoot) throws IOException {
        class ZMarkObjectOutputStream extends PObjectOutputStream {
            public ZMarkObjectOutputStream() throws IOException {
                super(NULL_OUTPUT_STREAM);
                enableReplaceObject(true);
            }

            public Object replaceObject(final Object object) {
                unconditionallyWritten.put(object, Boolean.TRUE);
                return object;
            }

            public void writeConditionalObject(final Object object) throws IOException {
            }
        }
        new ZMarkObjectOutputStream().writeObject(aRoot);
    }

    private static final OutputStream NULL_OUTPUT_STREAM = new OutputStream() {
        public void close() {
        }

        public void flush() {
        }

        public void write(final byte[] b) {
        }

        public void write(final byte[] b, final int off, final int len) {
        }

        public void write(final int b) {
        }
    };
}
