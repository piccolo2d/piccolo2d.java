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
package org.piccolo2d.nodes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import junit.framework.TestCase;

/**
 * Abstract unit test for subclasses of PShape.
 */
public abstract class AbstractPShapeTest extends TestCase {

    /** Mock property change listener. */
    protected MockPropertyChangeListener mockListener;


    /** {@inheritDoc} */
    protected void setUp() {
        mockListener = new MockPropertyChangeListener();
    }

    /**
     * Create a new instance of a subclass of PShape to test.
     *
     * @return a new instance of a subclass of PShape to test
     */
    protected abstract PShape createShapeNode();

    public void testCreateShapeNode() {
        assertNotNull(createShapeNode());
    }

    public void testDefaultPaint() {
        PShape shape = createShapeNode();
        assertEquals(PShape.DEFAULT_PAINT, shape.getPaint());
    }

    public void testDefaultStroke() {
        PShape shape = createShapeNode();
        assertEquals(PShape.DEFAULT_STROKE, shape.getStroke());
    }

    public void testDefaultStrokePaint() {
        PShape shape = createShapeNode();
        assertEquals(PShape.DEFAULT_STROKE_PAINT, shape.getStrokePaint());
    }

    public void testStroke() {
        PShape shape = createShapeNode();
        Stroke stroke = new BasicStroke(2.0f);
        shape.setStroke(stroke);
        assertEquals(stroke, shape.getStroke());
    }

    public void testStrokeBoundProperty() {
        PShape shape = createShapeNode();
        shape.addPropertyChangeListener("stroke", mockListener);
        Stroke stroke = new BasicStroke(2.0f);
        shape.setStroke(stroke);
        assertEquals(1, mockListener.getPropertyChangeCount());
    }

    public void testStrokePaint() {
        PShape shape = createShapeNode();
        Paint strokePaint = Color.RED;
        shape.setStrokePaint(strokePaint);
        assertEquals(strokePaint, shape.getStrokePaint());
    }

    public void testStrokePaintBoundProperty() {
        PShape shape = createShapeNode();
        shape.addPropertyChangeListener("strokePaint", mockListener);
        Paint strokePaint = Color.RED;
        shape.setStrokePaint(strokePaint);
        assertEquals(1, mockListener.getPropertyChangeCount());
    }
}