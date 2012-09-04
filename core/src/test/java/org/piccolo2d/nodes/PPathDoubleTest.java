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
import java.awt.Shape;
import java.awt.Stroke;

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

/**
 * Unit test for PPath.Double.
 */
public class PPathDoubleTest extends AbstractPPathTest {

    /** {@inheritDoc} */
    protected PPath createPathNode() {
        return new PPath.Double();
    }

    public void testNoArgConstructor() {
        assertNotNull(new PPath.Double());
    }

    public void testStrokeConstructor() {
        assertNotNull(new PPath.Double((Stroke) null));
        assertNotNull(new PPath.Double(new BasicStroke(2.0f)));
    }

    public void testShapeConstructor() {
        assertNotNull(new PPath.Double(new Rectangle2D.Double(0.0d, 0.0d, 100.0d, 100.0d)));
    }

    public void testShapeStrokeConstructor() {
        assertNotNull(new PPath.Double(new Rectangle2D.Double(0.0d, 0.0d, 100.0d, 100.0d), null));
        assertNotNull(new PPath.Double(new Rectangle2D.Double(0.0d, 0.0d, 100.0d, 100.0d), new BasicStroke(2.0f)));
    }

    public void testShapeConstructorNullArgument() {
        try {
            new PPath.Double((Shape) null);
            fail("ctr((Shape) null) expected NullPointerException");
        }
        catch (NullPointerException e) {
            // expected
        }
    }

    public void testShapeStrokeConstructorNullArgument() {
        try {
            new PPath.Double((Shape) null, null);
            fail("ctr((Shape) null, ) expected NullPointerException");
        }
        catch (NullPointerException e) {
            // expected
        }
    }

    public void testPathConstructor() {
        assertNotNull(new PPath.Double(new Path2D.Double()));
    }

    public void testPathStrokeConstructor() {
        assertNotNull(new PPath.Double(new Path2D.Double(), null));
        assertNotNull(new PPath.Double(new Path2D.Double(), new BasicStroke(2.0f)));
    }

    public void testPathConstructorNullArgument() {
        try {
            new PPath.Double((Path2D) null);
            fail("ctr((Path2D) null) expected NullPointerException");
        }
        catch (NullPointerException e) {
            // expected
        }
    }

    public void testPathStrokeConstructorNullArgument() {
        try {
            new PPath.Double((Path2D) null, null);
            fail("ctr((Path2D) null, ) expected NullPointerException");
        }
        catch (NullPointerException e) {
            // expected
        }
    }
}