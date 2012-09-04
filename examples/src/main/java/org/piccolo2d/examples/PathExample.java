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

import java.awt.geom.Arc2D;

import org.piccolo2d.PCanvas;

import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PInputEventListener;

import org.piccolo2d.extras.PFrame;

import org.piccolo2d.nodes.PPath;

/**
 * Path example.
 */
public final class PathExample extends PFrame {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Path paint. */
    private static final Paint PAINT = new Color(20, 20, 20, 120);

    /** Stroke. */
    private static final Stroke STROKE = new BasicStroke(0.5f);

    /** Stroke paint. */
    private static final Paint STROKE_PAINT = new Color(20, 20, 20, 120);

    /** Mouseover paint. */
    private static final Paint MOUSEOVER_PAINT = new Color(252, 233, 79);

    /** Mouseover stroke paint. */
    private static final Paint MOUSEOVER_STROKE_PAINT = new Color(237, 212, 0);


    /**
     * Create a new path example.
     */
    public PathExample() {
        this(null);
    }

    /**
     * Create a new path example with the specified canvas.
     *
     * @param canvas canvas for this path example
     */
    public PathExample(final PCanvas canvas) {
        super("PathExample", false, canvas);
    }


    /** {@inheritDoc} */
    public void initialize() {

        PPath arc = PPath.createArc(-75.0d, 25.0d, 200.0d, 200.0d, 30.0d, 60.0d, Arc2D.PIE);
        PPath cubicCurve = PPath.createCubicCurve(100.0d, 100.0d, 150.0d, 125.0d, 175.0d, 150.0d, 200.0d, 200.0d);
        PPath ellipse = PPath.createEllipse(250.0d, 250.0d, 90.0d, 90.0d);
        PPath line = PPath.createLine(10.0d, 390.0d, 200.0d, 200.0d); 
        PPath quadCurve = PPath.createQuadCurve(390.0d, 10.0d, 375.0d, 80.0d, 200.0d, 200.0d);
        PPath rectangle = PPath.createRectangle(180.0d, 300.0d, 40.0d, 60.0d);
        PPath roundRectangle = PPath.createRoundRectangle(280.0d, 180.0d, 60.0d, 40.0d, 4.0d, 8.0d);

        arc.setPaint(PAINT);
        arc.setStroke(STROKE);
        arc.setStrokePaint(STROKE_PAINT);
        cubicCurve.setPaint(PAINT);
        cubicCurve.setStroke(STROKE);
        cubicCurve.setStrokePaint(STROKE_PAINT);
        ellipse.setPaint(PAINT);
        ellipse.setStroke(STROKE);
        ellipse.setStrokePaint(STROKE_PAINT);
        line.setPaint(PAINT);
        line.setStroke(STROKE);
        line.setStrokePaint(STROKE_PAINT);
        quadCurve.setPaint(PAINT);
        quadCurve.setStroke(STROKE);
        quadCurve.setStrokePaint(STROKE_PAINT);
        rectangle.setPaint(PAINT);
        rectangle.setStroke(STROKE);
        rectangle.setStrokePaint(STROKE_PAINT);
        roundRectangle.setPaint(PAINT);
        roundRectangle.setStroke(STROKE);
        roundRectangle.setStrokePaint(STROKE_PAINT);

        PInputEventListener mouseOver = new PBasicInputEventHandler() {

                /** {@inheritDoc} */
                public void mouseEntered(final PInputEvent event) {
                    event.getPickedNode().setPaint(MOUSEOVER_PAINT);
                    ((PPath) event.getPickedNode()).setStrokePaint(MOUSEOVER_STROKE_PAINT);
                }

                /** {@inheritDoc} */
                public void mouseExited(final PInputEvent event) {
                    event.getPickedNode().setPaint(PAINT);
                    ((PPath) event.getPickedNode()).setStrokePaint(STROKE_PAINT);
                }
            };

        arc.addInputEventListener(mouseOver);
        cubicCurve.addInputEventListener(mouseOver);
        ellipse.addInputEventListener(mouseOver);
        line.addInputEventListener(mouseOver);
        quadCurve.addInputEventListener(mouseOver);
        rectangle.addInputEventListener(mouseOver);        
        roundRectangle.addInputEventListener(mouseOver);

        getCanvas().getLayer().addChild(arc);
        getCanvas().getLayer().addChild(cubicCurve);
        getCanvas().getLayer().addChild(ellipse);
        getCanvas().getLayer().addChild(line);
        getCanvas().getLayer().addChild(quadCurve);
        getCanvas().getLayer().addChild(rectangle);
        getCanvas().getLayer().addChild(roundRectangle);
    }


    /**
     * Main.
     *
     * @param args command line arguments, ignored
     */
    public static void main(final String[] args) {
        new PathExample();
    }
}