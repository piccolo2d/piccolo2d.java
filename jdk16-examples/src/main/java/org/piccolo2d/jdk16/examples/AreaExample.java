/*
 * Copyright (c) 2008-2010, Piccolo2D project, http://piccolo2d.org
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
package org.piccolo2d.jdk16.examples;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import java.awt.geom.Area;

import org.piccolo2d.PCanvas;

import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PInputEventListener;

import org.piccolo2d.extras.PFrame;

import org.piccolo2d.jdk16.nodes.PArea;
import org.piccolo2d.jdk16.nodes.PPath;

import org.piccolo2d.util.PBounds;

/**
 * Area example.
 */
public final class AreaExample extends PFrame {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Path paint. */
    private static final Paint PAINT = new Color(20, 20, 20, 120);

    /** Area paint. */
    private static final Paint AREA_PAINT = new Color(0, 0, 0, 0);

    /** Stroke. */
    private static final Stroke STROKE = new BasicStroke(0.5f);

    /** Area stroke. */
    private static final Stroke AREA_STROKE = null;

    /** Stroke paint. */
    private static final Paint STROKE_PAINT = new Color(20, 20, 20, 200);

    /** Mouseover paint. */
    private static final Paint MOUSEOVER_PAINT = new Color(10, 10, 10);


    /**
     * Create a new area example.
     */
    public AreaExample() {
        this(null);
    }

    /**
     * Create a new area example with the specified canvas.
     *
     * @param canvas canvas for this area example
     */
    public AreaExample(final PCanvas canvas) {
        super("AreaExample", false, canvas);
    }


    /** {@inheritDoc} */
    public void initialize() {

        /*
          Example based on TertiaryVennNode.java, see
          http://www.dishevelled.org/piccolo-venn
         */

        PPath first = PPath.createEllipse(0.0d, 0.0d, 128.0d, 128.0d);
        PPath second = PPath.createEllipse((2.0d * 128.0d) / 3.0d, 0.0d, 128.0d, 128.0d);
        PPath third = PPath.createEllipse(128.0d / 3.0d, (2.0d * 128.0d) / 3.0d, 128.0d, 128.0d);

        first.setStroke(STROKE);
        second.setStroke(STROKE);
        third.setStroke(STROKE);

        Area f = new Area(first.getPathReference());
        Area s = new Area(second.getPathReference());
        Area t = new Area(third.getPathReference());

        PArea firstOnly = new PArea(AREA_STROKE);
        firstOnly.add(f);
        firstOnly.subtract(s);
        firstOnly.subtract(t);

        PArea secondOnly = new PArea(AREA_STROKE);
        secondOnly.add(s);
        secondOnly.subtract(f);
        secondOnly.subtract(t);

        PArea thirdOnly = new PArea(AREA_STROKE);
        thirdOnly.add(t);
        thirdOnly.subtract(f);
        thirdOnly.subtract(s);

        PArea firstSecond = new PArea(AREA_STROKE);
        firstSecond.add(f);
        firstSecond.intersect(s);
        firstSecond.subtract(t);

        PArea firstThird = new PArea(AREA_STROKE);
        firstThird.add(f);
        firstThird.intersect(t);
        firstThird.subtract(s);

        PArea secondThird = new PArea(AREA_STROKE);
        secondThird.add(s);
        secondThird.intersect(t);
        secondThird.subtract(f);

        PArea intersection = new PArea(AREA_STROKE);
        intersection.add(f);
        intersection.intersect(s);
        intersection.intersect(t);

        PInputEventListener mouseOver = new PBasicInputEventHandler() {

                /** {@inheritDoc} */
                public void mouseEntered(final PInputEvent event) {
                    event.getPickedNode().setPaint(MOUSEOVER_PAINT);
                }

                /** {@inheritDoc} */
                public void mouseExited(final PInputEvent event) {
                    event.getPickedNode().setPaint(PAINT);
                }
            };

        firstOnly.addInputEventListener(mouseOver);
        secondOnly.addInputEventListener(mouseOver);
        thirdOnly.addInputEventListener(mouseOver);
        firstSecond.addInputEventListener(mouseOver);
        firstThird.addInputEventListener(mouseOver);
        secondThird.addInputEventListener(mouseOver);
        intersection.addInputEventListener(mouseOver);

        first.setPaint(PAINT);
        first.setStrokePaint(STROKE_PAINT);
        second.setPaint(PAINT);
        second.setStrokePaint(STROKE_PAINT);
        third.setPaint(PAINT);
        third.setStrokePaint(STROKE_PAINT);
        firstOnly.setPaint(AREA_PAINT);
        secondOnly.setPaint(AREA_PAINT);
        thirdOnly.setPaint(AREA_PAINT);
        firstSecond.setPaint(AREA_PAINT);
        firstThird.setPaint(AREA_PAINT);
        secondThird.setPaint(AREA_PAINT);
        intersection.setPaint(AREA_PAINT);

        getCanvas().getLayer().addChild(first);
        getCanvas().getLayer().addChild(second);
        getCanvas().getLayer().addChild(third);
        getCanvas().getLayer().addChild(firstOnly);
        getCanvas().getLayer().addChild(secondOnly);
        getCanvas().getLayer().addChild(thirdOnly);
        getCanvas().getLayer().addChild(firstSecond);
        getCanvas().getLayer().addChild(firstThird);
        getCanvas().getLayer().addChild(secondThird);
        getCanvas().getLayer().addChild(intersection);

        PBounds center = getCanvas().getLayer().getFullBoundsReference();
        getCanvas().getCamera().animateViewToCenterBounds(center, false, 0L);
    }


    /**
     * Main.
     *
     * @param args command line arguments, ignored
     */
    public static void main(final String[] args) {
        new AreaExample();
    }
}