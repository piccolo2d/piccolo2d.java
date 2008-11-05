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

/**
 * <b>PFixedWidthStroke</b> is the same as {@link BasicStroke} except that
 * PFixedWidthStroke has a fixed width on the screen so that even when the
 * canvas view is zooming its width stays the same in canvas coordinates.
 * <p>
 * {@link #createStrokedShape(Shape)} checks if the scale has changed since the
 * last usage and if that's the case calls {@link #newStroke(float)} to get a
 * new {@link Stroke} instance to delegate to.
 * <p>
 * <b>CAUTION!</b> this implementation falls short for large scaling factors -
 * the effective miterlimit might drop below 1.0 which isn't permitted by
 * {@link BasicStroke} and therefore limited to a minimal 1.0 by this
 * implementation. A more sophisticated implementation might use the approach
 * mentioned at http://code.google.com/p/piccolo2d/issues/detail?id=49
 * <p>
 * <b>CAUTION!</b> after extreme scaling this implementation seems to change to
 * internal state of the base stroke. Try PathExample with extreme zoom in and
 * zoom back to the original scale. The pickable circles disappear. Strange!
 * 
 * @see edu.umd.cs.piccolo.nodes.PPath
 * @see BasicStroke
 * @version 1.0
 * @author Jesse Grosjean
 * @author Marcus Rohrmoser
 */
public class PFixedWidthStroke extends PSemanticStroke implements Serializable {

    // make them public if required or delete when cleaning up for 2.0
    private static final int CAP_BUTT = BasicStroke.CAP_BUTT;
    private static final int CAP_ROUND = BasicStroke.CAP_ROUND;
    private static final int CAP_SQUARE = BasicStroke.CAP_SQUARE;
    private static final int JOIN_BEVEL = BasicStroke.JOIN_BEVEL;
    private static final int JOIN_MITER = BasicStroke.JOIN_MITER;
    private static final int JOIN_ROUND = BasicStroke.JOIN_ROUND;

    private static final long serialVersionUID = -2503357070350473610L;

    // avoid repeated cloning:
    private transient final float dash[];
    // avoid repeated instantiations:
    private transient final float tmpDash[];

    public PFixedWidthStroke() {
        this(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
    }

    /** This should be "public" and the "main" constructor. */
    private PFixedWidthStroke(final BasicStroke stroke) {
        super(stroke);
        dash = stroke.getDashArray();
        tmpDash = dash == null ? null : new float[dash.length];
    }

    public PFixedWidthStroke(final float width) {
        this(width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
    }

    public PFixedWidthStroke(final float width, final int cap, final int join) {
        this(width, cap, join, 10.0f, null, 0.0f);
    }

    public PFixedWidthStroke(final float width, final int cap, final int join, final float miterlimit) {
        this(width, cap, join, miterlimit, null, 0.0f);
    }

    public PFixedWidthStroke(final float width, final int cap, final int join, final float miterlimit,
            final float dash[], final float dash_phase) {
        this(new BasicStroke(width, cap, join, miterlimit, dash, dash_phase));
    }

    public Object clone() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public float[] getDashArray() {
        return ((BasicStroke) stroke).getDashArray();
    }

    public float getDashPhase() {
        return ((BasicStroke) stroke).getDashPhase();
    }

    public int getEndCap() {
        return ((BasicStroke) stroke).getEndCap();
    }

    public int getLineJoin() {
        return ((BasicStroke) stroke).getLineJoin();
    }

    public float getLineWidth() {
        return ((BasicStroke) stroke).getLineWidth();
    }

    public float getMiterLimit() {
        return ((BasicStroke) stroke).getMiterLimit();
    }

    protected Stroke newStroke(final float activeScale) {
        if (tmpDash != null) {
            for (int i = dash.length - 1; i >= 0; i--) {
                tmpDash[i] = dash[i] / activeScale;
            }
        }
        final float ml = getMiterLimit() / activeScale;
        return new BasicStroke(getLineWidth() / activeScale, getEndCap(), getLineJoin(), ml < 1.0f ? 1.0f : ml,
                tmpDash, getDashPhase() / activeScale);
    }

    /** Is it really necessary to implement {@link Serializable}? */
    protected Object readResolve() throws ObjectStreamException {
        return new PFixedWidthStroke((BasicStroke) stroke);
    }
}
