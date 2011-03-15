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

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;

import org.piccolo2d.PCamera;
import org.piccolo2d.PLayer;
import org.piccolo2d.PRoot;


/**
 * <b>PUtil</b> util methods for the Piccolo framework.
 * <P>
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PUtil {
    /**
     * PActivities are broken into steps, this is how many milliseconds should
     * pass between steps.
     */
    public static final long DEFAULT_ACTIVITY_STEP_RATE = 20;

    /** Rate in milliseconds at which the activity timer will get invoked. */
    public static final int ACTIVITY_SCHEDULER_FRAME_DELAY = 10;

    /** An iterator that iterates over an empty collection. */
    public static final Iterator NULL_ITERATOR = Collections.EMPTY_LIST.iterator();

    /**
     * Used when persisting paths to an object stream. Used to mark the end of
     * the path.
     */
    private static final int PATH_TERMINATOR = -1;

    /** A utility enumeration with no elements. */
    public static final Enumeration NULL_ENUMERATION = new Enumeration() {
        public boolean hasMoreElements() {
            return false;
        }

        public Object nextElement() {
            return null;
        }
    };

    /**
     * Creates the simplest possible scene graph. 1 Camera, 1 Layer, 1 Root
     * 
     * @return a basic scene with 1 camera, layer and root
     */
    public static PCamera createBasicScenegraph() {
        final PRoot root = new PRoot();
        final PLayer layer = new PLayer();
        final PCamera camera = new PCamera();

        root.addChild(camera);
        root.addChild(layer);
        camera.addLayer(layer);

        return camera;
    }

    /**
     * Serializes the given stroke object to the object output stream provided.
     * By default strokes are not serializable. This method solves that problem.
     * 
     * @param stroke stroke to be serialize
     * @param out stream to which the stroke is to be serialized
     * @throws IOException can occur if exception occurs with underlying output
     *             stream
     */
    public static void writeStroke(final Stroke stroke, final ObjectOutputStream out) throws IOException {
        if (stroke instanceof Serializable) {
            out.writeBoolean(true);
            out.writeBoolean(true);
            out.writeObject(stroke);
        }
        else if (stroke instanceof BasicStroke) {
            out.writeBoolean(true);
            out.writeBoolean(false);
            writeBasicStroke((BasicStroke) stroke, out);
        }
        else {
            out.writeBoolean(false);
        }
    }

    private static void writeBasicStroke(final BasicStroke basicStroke, final ObjectOutputStream out)
            throws IOException {
        final float[] dash = basicStroke.getDashArray();

        if (dash == null) {
            out.write(0);
        }
        else {
            out.write(dash.length);
            for (int i = 0; i < dash.length; i++) {
                out.writeFloat(dash[i]);
            }
        }

        out.writeFloat(basicStroke.getLineWidth());
        out.writeInt(basicStroke.getEndCap());
        out.writeInt(basicStroke.getLineJoin());
        out.writeFloat(basicStroke.getMiterLimit());
        out.writeFloat(basicStroke.getDashPhase());
    }

    /**
     * Reconstitutes a stroke from the provided Object Input Stream. According
     * to the scheme found in writeStroke. By default strokes are not
     * serializable.
     * 
     * @param in stream from which Stroke is to be read
     * @return a stroke object
     * @throws IOException occurs if an exception occurs reading from in stream
     * @throws ClassNotFoundException should never happen, but can if somehow
     *             the stroke class is not on the classpath
     */
    public static Stroke readStroke(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        final boolean wroteStroke = in.readBoolean();
        if (!wroteStroke) {
            return null;
        }

        final boolean serializedStroke = in.readBoolean();
        if (serializedStroke) {
            return (Stroke) in.readObject();
        }

        return readBasicStroke(in);
    }

    private static Stroke readBasicStroke(final ObjectInputStream in) throws IOException {
        float[] dash = null;
        final int dashLength = in.read();

        if (dashLength != 0) {
            dash = new float[dashLength];
            for (int i = 0; i < dashLength; i++) {
                dash[i] = in.readFloat();
            }
        }

        final float lineWidth = in.readFloat();
        final int endCap = in.readInt();
        final int lineJoin = in.readInt();
        final float miterLimit = in.readFloat();
        final float dashPhase = in.readFloat();

        return new BasicStroke(lineWidth, endCap, lineJoin, miterLimit, dash, dashPhase);
    }

    /**
     * Reads a path from the provided inputStream in accordance with the
     * serialization policy defined in writePath.
     * 
     * @param in stream from which to read the path.
     * @return reconstituted path
     * @throws IOException if an unknown path type is read from the stream
     * @throws ClassNotFoundException should never happen, but can if somehow
     *             the classpath is seriously messed up
     */
    public static GeneralPath readPath(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        final GeneralPath path = new GeneralPath();

        while (true) {
            final int segType = in.readInt();

            switch (segType) {
                case PathIterator.SEG_MOVETO:
                    path.moveTo(in.readFloat(), in.readFloat());
                    break;

                case PathIterator.SEG_LINETO:
                    path.lineTo(in.readFloat(), in.readFloat());
                    break;

                case PathIterator.SEG_QUADTO:
                    path.quadTo(in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat());
                    break;

                case PathIterator.SEG_CUBICTO:
                    path.curveTo(in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat(), in
                            .readFloat());
                    break;

                case PathIterator.SEG_CLOSE:
                    path.closePath();
                    break;

                case PATH_TERMINATOR:
                    return path;

                default:
                    throw new IOException("Unknown path type encountered while deserializing path.");
            }
        }
    }

    /**
     * Serializes the given path to the provided Object Output Stream.
     * 
     * @param path path to be serialized
     * @param out stream to which the path should be serialized
     * @throws IOException if unknown path segment type is encountered, or an
     *             exception occurs writing to the output stream
     */
    public static void writePath(final GeneralPath path, final ObjectOutputStream out) throws IOException {
        final PathIterator i = path.getPathIterator(null);
        final float[] data = new float[6];

        while (!i.isDone()) {
            switch (i.currentSegment(data)) {
                case PathIterator.SEG_MOVETO:
                    out.writeInt(PathIterator.SEG_MOVETO);
                    out.writeFloat(data[0]);
                    out.writeFloat(data[1]);
                    break;

                case PathIterator.SEG_LINETO:
                    out.writeInt(PathIterator.SEG_LINETO);
                    out.writeFloat(data[0]);
                    out.writeFloat(data[1]);
                    break;

                case PathIterator.SEG_QUADTO:
                    out.writeInt(PathIterator.SEG_QUADTO);
                    out.writeFloat(data[0]);
                    out.writeFloat(data[1]);
                    out.writeFloat(data[2]);
                    out.writeFloat(data[3]);
                    break;

                case PathIterator.SEG_CUBICTO:
                    out.writeInt(PathIterator.SEG_CUBICTO);
                    out.writeFloat(data[0]);
                    out.writeFloat(data[1]);
                    out.writeFloat(data[2]);
                    out.writeFloat(data[3]);
                    out.writeFloat(data[4]);
                    out.writeFloat(data[5]);
                    break;

                case PathIterator.SEG_CLOSE:
                    out.writeInt(PathIterator.SEG_CLOSE);
                    break;

                default:
                    throw new IOException("Unknown path type encountered while serializing path.");
            }

            i.next();
        }

        out.writeInt(PATH_TERMINATOR);
    }
}
