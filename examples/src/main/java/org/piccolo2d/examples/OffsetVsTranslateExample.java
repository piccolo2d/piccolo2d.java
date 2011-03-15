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
package org.piccolo2d.examples;

import org.piccolo2d.PCanvas;
import org.piccolo2d.PNode;
import org.piccolo2d.activities.PActivity;
import org.piccolo2d.extras.PFrame;
import org.piccolo2d.nodes.PText;




/**
 * This example demonstrates the difference between
 * <code>offset(double, double)</code> and
 * <code>translate(double, double)</code>.
 * 
 * @see PNode#offset(double, double)
 * @see PNode#translate(double, double)
 */
public class OffsetVsTranslateExample
    extends PFrame {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;


    /**
     * Create a new offset vs. translate example.
     */
    public OffsetVsTranslateExample() {
        this(null);
    }

    /**
     * Create a new offset vs. translate example for the specified canvas.
     * 
     * @param canvas canvas for this offset vs. translate example
     */
    public OffsetVsTranslateExample(final PCanvas canvas) {
        super("OffsetVsTranslateExample", false, canvas);
    }


    /** {@inheritDoc} */
    public void initialize() {
        final PText offset = new PText("Offset node");
        final PText offsetRotated = new PText("Offset rotated node");
        final PText translate = new PText("Translated node");
        final PText translateRotated = new PText("Translated rotated node");

        offset.setScale(2.0d);
        offsetRotated.setScale(2.0d);
        translate.setScale(2.0d);
        translateRotated.setScale(2.0d);

        offsetRotated.setRotation(Math.PI / 8.0d);
        translateRotated.setRotation(Math.PI / 8.0d);
        offset.setOffset(15.0d, 100.0d);
        offsetRotated.setOffset(15.0d, 150.0d);
        translate.setOffset(15.0d, 200.0d);
        translateRotated.setOffset(15.0d, 250.0d);

        getCanvas().getLayer().addChild(offset);
        getCanvas().getLayer().addChild(offsetRotated);
        getCanvas().getLayer().addChild(translate);
        getCanvas().getLayer().addChild(translateRotated);

        offset.addActivity(new PActivity(-1L) {
            /** {@inheritDoc} */
            protected void activityStep(final long elapsedTime) {
                offset.offset(1.0d, 0.0d);
            }
        });
        offsetRotated.addActivity(new PActivity(-1L) {
            /** {@inheritDoc} */
            protected void activityStep(final long elapsedTime) {
                offsetRotated.offset(1.0d, 0.0d);
            }
        });
        translate.addActivity(new PActivity(-1L) {
            /** {@inheritDoc} */
            protected void activityStep(final long elapsedTime) {
                translate.translate(1.0d, 0.0d);
            }
        });
        translateRotated.addActivity(new PActivity(-1L) {
            /** {@inheritDoc} */
            protected void activityStep(final long elapsedTime) {
                translateRotated.translate(1.0d, 0.0d);
            }
        });
    }


    /**
     * Main.
     * 
     * @param args command line arguments, ignored
     */
    public static void main(final String[] args) {
        new OffsetVsTranslateExample();
    }
}
