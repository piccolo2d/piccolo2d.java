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
package edu.umd.cs.piccolo.util;

import junit.framework.TestCase;

import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PBounds;

public class PAffineTransformTest extends TestCase {

    public PAffineTransformTest(String aName) {
        super(aName);
    }

    public void testRotation() {
        PAffineTransform at = new PAffineTransform();
        at.rotate(Math.toRadians(45));
        assertEquals(at.getRotation(), Math.toRadians(45), 0.000000001);
        at.setRotation(Math.toRadians(90));
        assertEquals(at.getRotation(), Math.toRadians(90), 0.000000001);
    }

    public void testScale() {
        PAffineTransform at = new PAffineTransform();
        at.scaleAboutPoint(0.45, 0, 1);
        assertEquals(at.getScale(), 0.45, 0.000000001);
        at.setScale(0.11);
        assertEquals(at.getScale(), 0.11, 0.000000001);
    }

    public void testTransformRect() {
        PBounds b1 = new PBounds(0, 0, 100, 80);
        PBounds b2 = new PBounds(100, 100, 100, 80);

        PAffineTransform at = new PAffineTransform();
        at.scale(0.5, 0.5);
        at.translate(100, 50);

        at.transform(b1, b1);
        at.transform(b2, b2);

        PBounds b3 = new PBounds();
        PBounds b4 = new PBounds(0, 0, 100, 100);

        assertTrue(at.transform(b3, b4).isEmpty());

        assertEquals(b1.getX(), 50, 0.000000001);
        assertEquals(b1.getY(), 25, 0.000000001);
        assertEquals(b1.getWidth(), 50, 0.000000001);
        assertEquals(b1.getHeight(), 40, 0.000000001);

        assertEquals(b2.getX(), 100, 0.000000001);
        assertEquals(b2.getY(), 75, 0.000000001);
        assertEquals(b2.getWidth(), 50, 0.000000001);
        assertEquals(b2.getHeight(), 40, 0.000000001);

        at.inverseTransform(b1, b1);
        at.inverseTransform(b2, b2);

        assertEquals(b1.getX(), 0, 0.000000001);
        assertEquals(b1.getY(), 0, 0.000000001);
        assertEquals(b1.getWidth(), 100, 0.000000001);
        assertEquals(b1.getHeight(), 80, 0.000000001);

        assertEquals(b2.getX(), 100, 0.000000001);
        assertEquals(b2.getY(), 100, 0.000000001);
        assertEquals(b2.getWidth(), 100, 0.000000001);
        assertEquals(b2.getHeight(), 80, 0.000000001);
    }
}
