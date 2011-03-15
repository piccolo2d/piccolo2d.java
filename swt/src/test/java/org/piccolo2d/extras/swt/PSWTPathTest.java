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
package org.piccolo2d.extras.swt;

import java.awt.geom.Point2D;

import org.piccolo2d.extras.swt.PSWTPath;
import org.piccolo2d.util.PBounds;

import junit.framework.TestCase;


/**
 * Unit test for PSWTPath.
 */
public class PSWTPathTest extends TestCase {

    public void testCenterEmpty() {
        PSWTPath path = new PSWTPath();

        PBounds bounds = path.getBoundsReference();
        assertEquals(0.0d, bounds.getX(), 0.1d);
        assertEquals(0.0d, bounds.getY(), 0.1d);
        assertEquals(0.0d, bounds.getHeight(), 0.1d);
        assertEquals(0.0d, bounds.getWidth(), 0.1d);

        Point2D center = path.getCenter();
        assertEquals(0.0d, center.getX(), 0.1d);
        assertEquals(0.0d, center.getY(), 0.1d);
    }

    public void testCenter() {
        PSWTPath path = PSWTPath.createRectangle(10.0f, 20.0f, 100.0f, 200.0f);

        PBounds bounds = path.getBoundsReference();
        // hard to believe the tolerance in SWT is this poor
        assertEquals(10.0d, bounds.getX(), 1.0d);
        assertEquals(20.0d, bounds.getY(), 1.0d);
        assertEquals(200.0d, bounds.getHeight(), 2.0d);
        assertEquals(100.0d, bounds.getWidth(), 2.0d);

        Point2D center = path.getCenter();
        assertEquals(60.0d, center.getX(), 0.1d);
        assertEquals(120.0d, center.getY(), 0.1d);
    }

    public void testCenterScale() {
        PSWTPath path = PSWTPath.createRectangle(10.0f, 20.0f, 100.0f, 200.0f);
        path.scale(10.0d);

        PBounds bounds = path.getBoundsReference();
        // hard to believe the tolerance in SWT is this poor
        assertEquals(10.0d, bounds.getX(), 1.0d);
        assertEquals(20.0d, bounds.getY(), 1.0d);
        assertEquals(200.0d, bounds.getHeight(), 2.0d);
        assertEquals(100.0d, bounds.getWidth(), 2.0d);

        // center is calculated in terms of local bounds, not full bounds
        Point2D center = path.getCenter();
        assertEquals(60.0d, center.getX(), 0.1d);
        assertEquals(120.0d, center.getY(), 0.1d);
    }

    public void testCenterRotate() {
        PSWTPath path = PSWTPath.createRectangle(10.0f, 20.0f, 100.0f, 200.0f);
        path.rotate(Math.PI / 8.0d);

        PBounds bounds = path.getBoundsReference();
        // hard to believe the tolerance in SWT is this poor
        assertEquals(10.0d, bounds.getX(), 1.0d);
        assertEquals(20.0d, bounds.getY(), 1.0d);
        assertEquals(200.0d, bounds.getHeight(), 2.0d);
        assertEquals(100.0d, bounds.getWidth(), 2.0d);

        // center is calculated in terms of local bounds, not full bounds
        Point2D center = path.getCenter();
        assertEquals(60.0d, center.getX(), 0.1d);
        assertEquals(120.0d, center.getY(), 0.1d);
    }
}
