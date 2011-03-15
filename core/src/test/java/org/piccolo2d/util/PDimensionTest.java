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
package org.piccolo2d.util;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import org.piccolo2d.util.PDimension;

import junit.framework.TestCase;

/**
 * Unit test for PDimension.
 */
public class PDimensionTest extends TestCase {
    public void testDefaultConstructorResultsInEmptyDimension() {
        final PDimension dimension = new PDimension();

        assertEquals(0, dimension.getWidth(), 0.00001);
        assertEquals(0, dimension.getHeight(), 0.00001);
    }

    public void testCloningConstructorDoesSo() {
        final Dimension2D src = new Dimension(100, 50);
        final PDimension copy = new PDimension(src);

        assertEquals(100, copy.getWidth(), 0.00001);
        assertEquals(50, copy.getHeight(), 0.00001);
    }

    public void testDimensionGetBuiltFromPoints() {
        final PDimension dimension = new PDimension(new Point2D.Double(-50, -25), new Point2D.Double(50, 25));
        assertEquals(100, dimension.getWidth(), 0.00001);
        assertEquals(50, dimension.getHeight(), 0.00001);
    }
}
