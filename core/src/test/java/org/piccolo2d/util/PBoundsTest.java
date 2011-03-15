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

import java.awt.geom.Rectangle2D;

import org.piccolo2d.PiccoloAsserts;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PDimension;

import junit.framework.TestCase;

/**
 * Unit test for PBounds.
 */
public class PBoundsTest extends TestCase {
    public void testDefaultBoundsAreEmpty() {
        final PBounds b = new PBounds();
        PiccoloAsserts.assertEquals(new PBounds(0, 0, 0, 0), b, 0.0001);
    }

    public void testBoundsCloneConstructorWorks() {
        final PBounds b1 = new PBounds(10, 15, 100, 50);
        final PBounds b2 = new PBounds(b1);
        PiccoloAsserts.assertEquals(b1, b2, 0.00001);
    }

    public void testBoundsCanBeConstructedFromRectangle2D() {
        final Rectangle2D r = new Rectangle2D.Double(1, 2, 3, 4);
        final PBounds b = new PBounds(r);
        PiccoloAsserts.assertEquals(new PBounds(1, 2, 3, 4), b, 0.000001);
    }

    /*
     * public void testBoundsCreatedFromPointAndWidthIsCorrect() { PBounds b =
     * new PBounds(new Point2D.Double(), 10, 10);
     * PiccoloAsserts.assertEquals(new PBounds(-10, -10, 20, 20), b, 0.000001);
     * }
     */

    public void testResetToZeroClearsBounds() {
        final PBounds b = new PBounds(1, 2, 3, 4);
        b.resetToZero();
        assertTrue(b.isEmpty());
        PiccoloAsserts.assertEquals(new PBounds(), b, 0.0000001);
    }

    public void testAdding1PointToEmptyYieldsEmpty() {
        final PBounds b = new PBounds();
        b.add(-10, -10);
        PiccoloAsserts.assertEquals(new PDimension(0, 0), b.getSize(), 0.00001);
    }

    public void testAdding2PointsToEmptyYieldsNotEmpty() {
        final PBounds b = new PBounds();
        b.add(-10, -10);
        b.add(0, 0);
        PiccoloAsserts.assertEquals(new PDimension(10, 10), b.getSize(), 0.00001);
    }

    // TODO: This test should pass, but making it do so would break binary compatability
    /*
     * public void testWhenBoundsHas0HeightFullBoundsIsCorrectlyReturned() {
     * final PNode node = new PNode(); final PBounds testBounds = new
     * PBounds(10, 10, 10, 0); node.setBounds(testBounds);
     * assertEquals(testBounds, node.getFullBounds()); }
     */
}
