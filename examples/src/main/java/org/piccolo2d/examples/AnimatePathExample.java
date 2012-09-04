/*
 * Copyright (c) 2008-2012, Piccolo2D project, http://piccolo2d.org
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
package org.piccolo2d.examples;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import org.piccolo2d.PCanvas;

import org.piccolo2d.activities.PInterpolatingActivity;

import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PInputEventListener;

import org.piccolo2d.extras.PFrame;

import org.piccolo2d.nodes.PPath;

/**
 * Animate path example.
 */
public final class AnimatePathExample extends PFrame {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Curve. */
    private final PPath curve = new PPath.Double(STROKE);

    /** Path paint. */
    private static final Paint PAINT = new Color(20, 20, 20, 120);

    /** Stroke. */
    private static final Stroke STROKE = new BasicStroke(0.5f);

    /** Stroke paint. */
    private static final Paint STROKE_PAINT = new Color(20, 20, 20, 120);


    /**
     * Create a new animate path example.
     */
    public AnimatePathExample() {
        this(null);
    }

    /**
     * Create a new animate path example with the specified canvas.
     *
     * @param canvas canvas for this animate path example
     */
    public AnimatePathExample(final PCanvas canvas) {
        super("AnimatePathExample", false, canvas);
    }


    /** {@inheritDoc} */
    public void initialize() {

        curve.moveTo(100.0d, 100.0d);
        curve.curveTo(150.0d, 135.0d, 250.0d, 155.0d, 300.0d, 300.0d);
        curve.closePath();

        curve.setPaint(PAINT);
        curve.setStrokePaint(STROKE_PAINT);

        PInputEventListener animateCurve = new PBasicInputEventHandler() {
                /** {@inheritDoc} */
                public void mousePressed(final PInputEvent event) {
                    animateCurve();
                }
            };

        curve.addInputEventListener(animateCurve);

        getCanvas().getLayer().addChild(curve);
    }

    /**
     * Animate curve.
     */
    private void animateCurve() {
        curve.addActivity(new PInterpolatingActivity(1000L) {
                /** {@inheritDoc} */
                public void setRelativeTargetValue(final float value) {
                    curve.reset();
                    curve.moveTo(100.0d, 100.0d);
                    curve.curveTo(150.0d, 135.0d - (135.0d * value), 250.0d, 155.0d - (155.0d * value), 300.0d, 300.0d);
                    curve.closePath();
                }
            });
    }


    /**
     * Main.
     *
     * @param args command line arguments, ignored
     */
    public static void main(final String[] args) {
        new AnimatePathExample();
    }
}