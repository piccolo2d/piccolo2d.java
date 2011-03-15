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

import java.awt.BasicStroke;
import java.awt.Color;

import org.piccolo2d.PCanvas;
import org.piccolo2d.extras.PFrame;
import org.piccolo2d.extras.util.PFixedWidthStroke;
import org.piccolo2d.nodes.PPath;
import org.piccolo2d.nodes.PText;


/**
 * Stroke example.
 */
public final class StrokeExample extends PFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a new stroke example.
     */
    public StrokeExample() {
        this(null);
    }

    /**
     * Create a new stroke example with the specified canvas.
     * 
     * @param canvas canvas
     */
    public StrokeExample(final PCanvas canvas) {
        super("StrokeExample", false, canvas);
    }

    /** {@inheritDoc} */
    public void initialize() {
        final PText label = new PText("Stroke Example");
        label.setFont(label.getFont().deriveFont(24.0f));
        label.offset(20.0d, 20.0d);

        final PPath rect = PPath.createRectangle(50.0f, 50.0f, 300.0f, 300.0f);
        rect.setStroke(new BasicStroke(4.0f));
        rect.setStrokePaint(new Color(80, 80, 80));

        final PText fixedWidthLabel = new PText("PFixedWidthStrokes");
        fixedWidthLabel.setTextPaint(new Color(80, 0, 0));
        fixedWidthLabel.offset(100.0d, 80.0d);

        final PPath fixedWidthRect0 = PPath.createRectangle(100.0f, 100.0f, 200.0f, 50.0f);
        fixedWidthRect0.setStroke(new PFixedWidthStroke(2.0f));
        fixedWidthRect0.setStrokePaint(new Color(60, 60, 60));

        final PPath fixedWidthRect1 = PPath.createRectangle(100.0f, 175.0f, 200.0f, 50.0f);
        fixedWidthRect1.setStroke(new PFixedWidthStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f));
        // fixedWidthRect1.setStroke(new PFixedWidthStroke(1.5f,
        // PFixedWidthStroke.CAP_ROUND, PFixedWidthStroke.JOIN_MITER, 10.0f));
        fixedWidthRect1.setStrokePaint(new Color(40, 40, 40));

        final PPath fixedWidthRect2 = PPath.createRectangle(100.0f, 250.0f, 200.0f, 50.0f);
        fixedWidthRect2.setStroke(new PFixedWidthStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f,
                new float[] { 2.0f, 3.0f, 4.0f }, 1.0f));
        // fixedWidthRect2.setStroke(new PFixedWidthStroke(1.0f,
        // PFixedWidthStroke.CAP_ROUND, PFixedWidthStroke.JOIN_MITER, 10.0f, new
        // float[] { 2.0f, 3.0f, 4.0f }, 1.0f));
        fixedWidthRect2.setStrokePaint(new Color(20, 20, 20));

        getCanvas().getLayer().addChild(label);
        getCanvas().getLayer().addChild(rect);
        getCanvas().getLayer().addChild(fixedWidthLabel);
        getCanvas().getLayer().addChild(fixedWidthRect0);
        getCanvas().getLayer().addChild(fixedWidthRect1);
        getCanvas().getLayer().addChild(fixedWidthRect2);
    }

    /**
     * Main.
     * 
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        new StrokeExample();
    }
}
