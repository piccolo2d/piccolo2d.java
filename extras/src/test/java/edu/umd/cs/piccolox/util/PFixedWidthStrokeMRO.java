/*
 * Copyright (c) 2008, Piccolo2D project, http://piccolo2d.org
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
package edu.umd.cs.piccolox.util;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Arrays;

import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PPickPath;

/**
 * <b>PFixedWidthStrokeMRO</b> is the same as {@link BasicStroke} except that
 * PFixedWidthStrokeMRO has a fixed width on the screen so that even when the
 * canvas view is zooming its width stays the same in canvas coordinates.
 * <p>
 * {@link #createStrokedShape(Shape)} checks if the scale has changed since the
 * last usage and if that's the case calls {@link #newStroke(float)} to get a
 * new {@link Stroke} instance to delegate to.
 * 
 * @see edu.umd.cs.piccolo.nodes.PPath
 * @see BasicStroke
 * @version 1.3
 * @author Marcus Rohrmoser
 */
public class PFixedWidthStrokeMRO implements Stroke, Serializable {

    // make them public if required or delete when cleaning up for 2.0
    private static final int CAP_BUTT = BasicStroke.CAP_BUTT;
    private static final int CAP_ROUND = BasicStroke.CAP_ROUND;
    private static final int CAP_SQUARE = BasicStroke.CAP_SQUARE;
    private static final int JOIN_BEVEL = BasicStroke.JOIN_BEVEL;
    private static final int JOIN_MITER = BasicStroke.JOIN_MITER;
    private static final int JOIN_ROUND = BasicStroke.JOIN_ROUND;

    private static final long serialVersionUID = -2503357070350473610L;
    private static final double THRESHOLD = 1e-6;

    private static int hashCode(final float[] array) {
        final int prime = 31;
        if (array == null) {
            return 0;
        }
        int result = 1;
        for (int index = 0; index < array.length; index++) {
            result = prime * result + Float.floatToIntBits(array[index]);
        }
        return result;
    }

    private final int cap;
    private final float dash[];
    private final float dash_phase;
    private final int join;
    private final float miterlimit;
    private transient float recentScale;
    private transient Stroke recentStroke;
    private transient final float tmpDash[];
    private final float width;

    public PFixedWidthStrokeMRO() {
        this(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
    }

    public PFixedWidthStrokeMRO(final float width) {
        this(width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
    }

    public PFixedWidthStrokeMRO(final float width, final int cap, final int join) {
        this(width, cap, join, 10.0f, null, 0.0f);
    }

    public PFixedWidthStrokeMRO(final float width, final int cap, final int join, final float miterlimit) {
        this(width, cap, join, miterlimit, null, 0.0f);
    }

    public PFixedWidthStrokeMRO(final float width, final int cap, final int join, final float miterlimit,
            final float dash[], final float dash_phase) {
        this.width = width;
        this.cap = cap;
        this.join = join;
        this.miterlimit = miterlimit;
        this.dash = dash;
        this.dash_phase = dash_phase;
        // avoid instantiations at the cost of some bytes of memory:
        tmpDash = this.dash == null ? null : new float[this.dash.length];
        // Instantiate eagerly to benefit from ctor's argument checks.
        recentStroke = newStroke(recentScale = 1.0F);
    }

    public Object clone() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    protected float computeStrokeScale() {
        if (PDebug.getProcessingOutput()) {
            if (PPaintContext.CURRENT_PAINT_CONTEXT != null) {
                return 1.0f / (float) PPaintContext.CURRENT_PAINT_CONTEXT.getScale();
            }
        }
        else {
            if (PPickPath.CURRENT_PICK_PATH != null) {
                return 1.0f / (float) PPickPath.CURRENT_PICK_PATH.getScale();
            }
        }
        return 1.0F;
    }

    public Shape createStrokedShape(final Shape s) {
        final float currentScale = computeStrokeScale();
        if (Math.abs(currentScale - recentScale) > THRESHOLD) {
            recentStroke = newStroke(recentScale = currentScale);
        }
        return recentStroke.createStrokedShape(s);
    }

    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PFixedWidthStrokeMRO other = (PFixedWidthStrokeMRO) obj;
        if (cap != other.cap) {
            return false;
        }
        if (!Arrays.equals(dash, other.dash)) {
            return false;
        }
        if (Float.floatToIntBits(dash_phase) != Float.floatToIntBits(other.dash_phase)) {
            return false;
        }
        if (join != other.join) {
            return false;
        }
        if (Float.floatToIntBits(miterlimit) != Float.floatToIntBits(other.miterlimit)) {
            return false;
        }
        if (Float.floatToIntBits(width) != Float.floatToIntBits(other.width)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + cap;
        result = prime * result + PFixedWidthStrokeMRO.hashCode(dash);
        result = prime * result + Float.floatToIntBits(dash_phase);
        result = prime * result + join;
        result = prime * result + Float.floatToIntBits(miterlimit);
        result = prime * result + Float.floatToIntBits(width);
        return result;
    }

    protected Stroke newStroke(final float scale) {
        if (tmpDash != null) {
            for (int i = dash.length - 1; i >= 0; i--) {
                tmpDash[i] = dash[i] * scale;
            }
        }
        return new BasicStroke(width * scale, cap, join, miterlimit * scale, tmpDash, dash_phase * scale);
    }

    /** Is it really necessary to implement {@link Serializable}? */
    protected Object readResolve() throws ObjectStreamException {
        return new PFixedWidthStrokeMRO(width, cap, join, miterlimit, dash, dash_phase);
    }
}
