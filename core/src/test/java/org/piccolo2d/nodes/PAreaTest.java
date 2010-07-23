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
package org.piccolo2d.nodes;

import java.awt.Shape;

import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * Unit test for PArea.
 */
public class PAreaTest extends AbstractPShapeTest {

    private static final double TOLERANCE = 0.0001d;

    /** {@inheritDoc} */
    protected PShape createShapeNode() {
        return new PArea();
    }

    public void testNoArgConstructor() {
        assertNotNull(new PArea());
    }

    public void testShapeConstructor() {
        assertNotNull(new PArea(new Rectangle2D.Double(0.0d, 0.0d, 100.0d, 100.0d)));
    }

    public void testShapeConstructorNullArgument() {
        try {
            new PArea((Shape) null);
            fail("ctr((Shape) null) expected NullPointerException");
        }
        catch (NullPointerException e) {
            // expected
        }
    }

    public void testAreaConstructor() {
        assertNotNull(new PArea(new Area()));
    }

    public void testAreaConstructorNullArgument() {
        try {
            new PArea((Area) null);
            fail("ctr((Area) null) expected NullPointerException");
        }
        catch (NullPointerException e) {
            // expected
        }
    }

    public void testAdd() {
        PArea area = new PArea();
        assertEquals(0.0d, area.getWidth(), TOLERANCE);
        assertEquals(0.0d, area.getHeight(), TOLERANCE);

        Area rect0 = new Area(new Rectangle2D.Double(0.0d, 0.0d, 100.0d, 100.0d));
        area.add(rect0);
        Area rect1 = new Area(new Rectangle2D.Double(50.0d, 0.0d, 100.0d, 100.0d));
        area.add(rect1);

        // todo:  shouldn't this be width + 2 * strokeWidth?
        assertEquals(151.0d, area.getWidth(), TOLERANCE);
        assertEquals(101.0, area.getHeight(), TOLERANCE);
        assertTrue(area.intersects(new Rectangle2D.Double(10.0d, 95.0d, 10.0d, 10.0d)));
        assertTrue(area.intersects(new Rectangle2D.Double(60.0d, 95.0d, 10.0d, 10.0d)));
        assertTrue(area.intersects(new Rectangle2D.Double(110.0d, 95.0d, 10.0d, 10.0d)));
    }

    public void testAddNoStroke() {
        PArea area = new PArea();
        area.setStroke(null);
        assertEquals(0.0d, area.getWidth(), TOLERANCE);
        assertEquals(0.0d, area.getHeight(), TOLERANCE);

        Area rect0 = new Area(new Rectangle2D.Double(0.0d, 0.0d, 100.0d, 100.0d));
        area.add(rect0);
        Area rect1 = new Area(new Rectangle2D.Double(50.0d, 0.0d, 100.0d, 100.0d));
        area.add(rect1);

        assertEquals(150.0d, area.getWidth(), TOLERANCE);
        assertEquals(100.0, area.getHeight(), TOLERANCE);
        assertFalse(area.intersects(new Rectangle2D.Double(10.0d, 95.0d, 10.0d, 10.0d)));
        assertFalse(area.intersects(new Rectangle2D.Double(60.0d, 95.0d, 10.0d, 10.0d)));
        assertFalse(area.intersects(new Rectangle2D.Double(110.0d, 95.0d, 10.0d, 10.0d)));
    }

    public void testAddNullArgument() {
        PArea area = new PArea();
        try {
            area.add(null);
            fail("add(null) expected NullPointerException");
        }
        catch (NullPointerException e) {
            // expected
        }
    }

    public void testExclusiveOr() {
        PArea area = new PArea();
        assertEquals(0.0d, area.getWidth(), TOLERANCE);
        assertEquals(0.0d, area.getHeight(), TOLERANCE);

        Area rect0 = new Area(new Rectangle2D.Double(0.0d, 0.0d, 100.0d, 100.0d));
        area.add(rect0);
        Area rect1 = new Area(new Rectangle2D.Double(50.0d, 0.0d, 100.0d, 100.0d));
        area.exclusiveOr(rect1);

        assertEquals(151.0d, area.getWidth(), TOLERANCE);
        assertEquals(101.0, area.getHeight(), TOLERANCE);
        assertTrue(area.intersects(new Rectangle2D.Double(10.0d, 95.0d, 10.0d, 10.0d)));
        assertFalse(area.intersects(new Rectangle2D.Double(60.0d, 95.0d, 10.0d, 10.0d)));
        assertTrue(area.intersects(new Rectangle2D.Double(110.0d, 95.0d, 10.0d, 10.0d)));
    }

    public void testExclusiveOrNoStroke() {
        PArea area = new PArea();
        area.setStroke(null);
        assertEquals(0.0d, area.getWidth(), TOLERANCE);
        assertEquals(0.0d, area.getHeight(), TOLERANCE);

        Area rect0 = new Area(new Rectangle2D.Double(0.0d, 0.0d, 100.0d, 100.0d));
        area.add(rect0);
        Area rect1 = new Area(new Rectangle2D.Double(50.0d, 0.0d, 100.0d, 100.0d));
        area.exclusiveOr(rect1);

        assertEquals(150.0d, area.getWidth(), TOLERANCE);
        assertEquals(100.0, area.getHeight(), TOLERANCE);
        assertFalse(area.intersects(new Rectangle2D.Double(10.0d, 95.0d, 10.0d, 10.0d)));
        assertFalse(area.intersects(new Rectangle2D.Double(60.0d, 95.0d, 10.0d, 10.0d)));
        assertFalse(area.intersects(new Rectangle2D.Double(110.0d, 95.0d, 10.0d, 10.0d)));
    }

    public void testExclusiveOrNullArgument() {
        PArea area = new PArea();
        try {
            area.exclusiveOr(null);
            fail("exclusiveOr(null) expected NullPointerException");
        }
        catch (NullPointerException e) {
            // expected
        }
    }

    public void testIntersect() {
        PArea area = new PArea();
        assertEquals(0.0d, area.getWidth(), TOLERANCE);
        assertEquals(0.0d, area.getHeight(), TOLERANCE);

        Area rect0 = new Area(new Rectangle2D.Double(0.0d, 0.0d, 100.0d, 100.0d));
        area.add(rect0);
        Area rect1 = new Area(new Rectangle2D.Double(50.0d, 0.0d, 100.0d, 100.0d));
        area.intersect(rect1);        

        assertEquals(51.0d, area.getWidth(), TOLERANCE);
        assertEquals(101.0, area.getHeight(), TOLERANCE);
        assertFalse(area.intersects(new Rectangle2D.Double(10.0d, 95.0d, 10.0d, 10.0d)));
        assertTrue(area.intersects(new Rectangle2D.Double(60.0d, 95.0d, 10.0d, 10.0d)));
        assertFalse(area.intersects(new Rectangle2D.Double(110.0d, 95.0d, 10.0d, 10.0d)));
    }

    public void testIntersectNoStroke() {
        PArea area = new PArea();
        area.setStroke(null);
        assertEquals(0.0d, area.getWidth(), TOLERANCE);
        assertEquals(0.0d, area.getHeight(), TOLERANCE);

        Area rect0 = new Area(new Rectangle2D.Double(0.0d, 0.0d, 100.0d, 100.0d));
        area.add(rect0);
        Area rect1 = new Area(new Rectangle2D.Double(50.0d, 0.0d, 100.0d, 100.0d));
        area.intersect(rect1);        

        assertEquals(50.0d, area.getWidth(), TOLERANCE);
        assertEquals(100.0, area.getHeight(), TOLERANCE);
        assertFalse(area.intersects(new Rectangle2D.Double(10.0d, 95.0d, 10.0d, 10.0d)));
        assertFalse(area.intersects(new Rectangle2D.Double(60.0d, 95.0d, 10.0d, 10.0d)));
        assertFalse(area.intersects(new Rectangle2D.Double(110.0d, 95.0d, 10.0d, 10.0d)));
    }

    public void testIntersectNullArgument() {
        PArea area = new PArea();
        try {
            area.intersect(null);
            fail("intersect(null) expected NullPointerException");
        }
        catch (NullPointerException e) {
            // expected
        }
    }

    public void testSubtract() {
        PArea area = new PArea();
        assertEquals(0.0d, area.getWidth(), TOLERANCE);
        assertEquals(0.0d, area.getHeight(), TOLERANCE);

        Area rect0 = new Area(new Rectangle2D.Double(0.0d, 0.0d, 100.0d, 100.0d));
        area.add(rect0);
        Area rect1 = new Area(new Rectangle2D.Double(50.0d, 0.0d, 100.0d, 100.0d));
        area.subtract(rect1);        

        assertEquals(51.0d, area.getWidth(), TOLERANCE);
        assertEquals(101.0, area.getHeight(), TOLERANCE);
        assertTrue(area.intersects(new Rectangle2D.Double(10.0d, 95.0d, 10.0d, 10.0d)));
        assertFalse(area.intersects(new Rectangle2D.Double(60.0d, 95.0d, 10.0d, 10.0d)));
        assertFalse(area.intersects(new Rectangle2D.Double(110.0d, 95.0d, 10.0d, 10.0d)));
    }

    public void testSubtractNoStroke() {
        PArea area = new PArea();
        area.setStroke(null);
        assertEquals(0.0d, area.getWidth(), TOLERANCE);
        assertEquals(0.0d, area.getHeight(), TOLERANCE);

        Area rect0 = new Area(new Rectangle2D.Double(0.0d, 0.0d, 100.0d, 100.0d));
        area.add(rect0);
        Area rect1 = new Area(new Rectangle2D.Double(50.0d, 0.0d, 100.0d, 100.0d));
        area.subtract(rect1);        

        assertEquals(50.0d, area.getWidth(), TOLERANCE);
        assertEquals(100.0, area.getHeight(), TOLERANCE);
        assertFalse(area.intersects(new Rectangle2D.Double(10.0d, 95.0d, 10.0d, 10.0d)));
        assertFalse(area.intersects(new Rectangle2D.Double(60.0d, 95.0d, 10.0d, 10.0d)));
        assertFalse(area.intersects(new Rectangle2D.Double(110.0d, 95.0d, 10.0d, 10.0d)));
    }

    public void testSubtractNullArgument() {
        PArea area = new PArea();
        try {
            area.subtract(null);
            fail("subtract(null) expected NullPointerException");
        }
        catch (NullPointerException e) {
            // expected
        }
    }

    public void testIsPolygonal() {
        assertTrue(new PArea(new Rectangle2D.Double(0.0d, 0.0d, 50.0d, 100.0d)).isPolygonal());
        assertTrue(new PArea(new Line2D.Double(0.0d, 0.0d, 50.0d, 100.0d)).isPolygonal());
        assertFalse(new PArea(new Ellipse2D.Double(0.0d, 0.0d, 50.0d, 100.0d)).isPolygonal());
    }

    public void testIsRectangular() {
        assertTrue(new PArea(new Rectangle2D.Double(0.0d, 0.0d, 50.0d, 100.0d)).isRectangular());
        assertTrue(new PArea(new Line2D.Double(0.0d, 0.0d, 50.0d, 100.0d)).isRectangular());
        assertFalse(new PArea(new Ellipse2D.Double(0.0d, 0.0d, 50.0d, 100.0d)).isRectangular());
    }

    public void testIsSingular() {
        assertTrue(new PArea(new Rectangle2D.Double(0.0d, 0.0d, 50.0d, 100.0d)).isSingular());
        assertTrue(new PArea(new Line2D.Double(0.0d, 0.0d, 50.0d, 100.0d)).isSingular());
        assertTrue(new PArea(new Ellipse2D.Double(0.0d, 0.0d, 50.0d, 100.0d)).isSingular());

        PArea exclusiveOr = new PArea();
        Area rect0 = new Area(new Rectangle2D.Double(0.0d, 0.0d, 100.0d, 100.0d));
        exclusiveOr.add(rect0);
        Area rect1 = new Area(new Rectangle2D.Double(50.0d, 0.0d, 100.0d, 100.0d));
        exclusiveOr.exclusiveOr(rect1);

        assertFalse(exclusiveOr.isSingular());
    }
}