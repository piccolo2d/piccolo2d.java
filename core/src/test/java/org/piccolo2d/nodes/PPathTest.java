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

import java.awt.Color;
import java.awt.Shape;

import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.piccolo2d.MockPropertyChangeListener;
import org.piccolo2d.PiccoloAsserts;

import org.piccolo2d.nodes.PPath;

import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PObjectOutputStream;

import junit.framework.TestCase;

/**
 * Unit test for PPath.
 */
public class PPathTest extends TestCase {

    private static final double TOLERANCE = 0.0001d;
    private static final double LOW_TOLERANCE = 1.0d;

    private MockPropertyChangeListener mockListener;

    public void setUp() {
        mockListener = new MockPropertyChangeListener();
    }

    public void testStrokeIsNotNullByDefault() {
        final PPath path = new PPath.Double();
        assertNotNull(path.getStroke());
    }

    public void testStrokePaintIsBlackByDefault() {
        final PPath path = new PPath.Double();
        assertEquals(Color.BLACK, path.getStrokePaint());
    }

    public void testClone() {
        PPath p = PPath.createEllipse(0, 0, 100, 100);        
        PPath cloned = (PPath) p.clone();
        assertEquals(p.getBounds(), cloned.getBounds());
        //assertEquals(p.getPathReference()., cloned.getPathReference());
    }

    public void testSerialization() throws IOException, ClassNotFoundException {
        final PPath srcPath = PPath.createEllipse(0, 0, 100, 100);
        final PBounds srcBounds = srcPath.getBounds();

        final File file = File.createTempFile("test", "ser");

        serializeToFile(srcPath, file);
        final PPath resultPath = deserializeFromFile(srcBounds, file);
        file.deleteOnExit();

        assertEquals(resultPath.getBounds(), srcBounds);
    }

    private PPath deserializeFromFile(final PBounds b, final File file) throws FileNotFoundException, IOException,
            ClassNotFoundException {
        PPath path;
        final FileInputStream fin = new FileInputStream(file);
        final ObjectInputStream in = new ObjectInputStream(fin);
        path = (PPath) in.readObject();

        return path;
    }

    private void serializeToFile(final PPath p, final File file) throws FileNotFoundException, IOException {
        final FileOutputStream fout = new FileOutputStream(file);
        final PObjectOutputStream out = new PObjectOutputStream(fout);
        out.writeObjectTree(p);
        out.flush();
        out.close();
    }

    public void testCreateRectangleReturnsValidPPath() {
        final PPath path = PPath.createRectangle(0, 0, 100, 50);
        assertNotNull(path);

        // Seems like rounding is affecting the bounds greatly
        PiccoloAsserts.assertEquals(new PBounds(0, 0, 100, 50), path.getBounds(), 2.0d);
    }

    public void testCreateEllipseReturnsValidPPath() {
        final PPath path = PPath.createEllipse(0, 0, 100, 50);
        assertNotNull(path);

        // Seems like rounding is affecting the bounds greatly
        PiccoloAsserts.assertEquals(new PBounds(0, 0, 100, 50), path.getBounds(), 2.0d);
    }

    public void testCreateRoundedRectReturnsValidPPath() {
        final PPath path = PPath.createRoundRectangle(0, 0, 100, 50, 10, 10);
        assertNotNull(path);

        // Seems like rounding is affecting the bounds greatly
        PiccoloAsserts.assertEquals(new PBounds(0, 0, 100, 50), path.getBounds(), 2.0d);
    }

    public void testCreateLineReturnsValidPPath() {
        final PPath path = PPath.createLine(0, 0, 100, 0);
        assertNotNull(path);

        // Seems like rounding is affecting the bounds greatly
        PiccoloAsserts.assertEquals(new PBounds(0, 0, 100, 0), path.getBounds(), 2.0d);
    }

    /*
    public void testCreatePolyLinePoint2DReturnsValidPPath() {
        final PPath path = PPath.createPolyline(new Point2D[] { new Point2D.Double(0, 0), new Point2D.Double(100, 50),
                new Point2D.Double(100, 0) });
        assertNotNull(path);

        // Seems like rounding is affecting the bounds greatly
        PiccoloAsserts.assertEquals(new PBounds(0, 0, 100, 50), path.getBounds(), 2.0d);
    }

    public void testCreatePolyLineFloatsReturnsValidPPath() {
        final PPath path = PPath.createPolyline(new float[] { 0, 100, 100 }, new float[] { 0, 50, 0 });
        assertNotNull(path);

        // Seems like rounding is affecting the bounds greatly
        PiccoloAsserts.assertEquals(new PBounds(0, 0, 100, 50), path.getBounds(), 2.0d);
    }
    */

    public void testSetStrokePaintPersists() {
        final PPath path = new PPath.Double();
        path.setStrokePaint(Color.RED);
        assertEquals(Color.RED, path.getStrokePaint());
    }

    // todo:  move these to PShape test, add stroke
    public void testSetStrokeFiresPropertyChangeEvent() {
        final PPath path = new PPath.Double();
        path.addPropertyChangeListener("strokePaint", mockListener);
        path.setStrokePaint(Color.RED);
        assertEquals(1, mockListener.getPropertyChangeCount());
    }

    public void testChangingPathFiresPropertyChangeEvent() {
        final PPath path = new PPath.Double();
        path.addPropertyChangeListener("path", mockListener); // "shape"
        path.append(new Rectangle2D.Double(0, 0, 100, 50), true);
        assertEquals(1, mockListener.getPropertyChangeCount());
    }

    public void testCreateArcFloat() {
        assertNotNull(PPath.createArc(0.0f, 0.0f, 50.0f, 100.0f, 25.0f, 75.0f, Arc2D.OPEN));
    }

    public void testCreateCubicCurveFloat() {
        assertNotNull(PPath.createCubicCurve(0.0f, 0.0f, 25.0f, 75.0f, 75.0f, 25.0f, 50.0f, 100.0f));
    }

    public void testCreateEllipseFloat() {
        assertNotNull(PPath.createEllipse(0.0f, 0.0f, 50.0f, 100.0f));
    }

    public void testCreateLineFloat() {
        assertNotNull(PPath.createLine(0.0f, 0.0f, 50.0f, 100.0f));
    }

    public void testCreateQuadCurveFloat() {
        assertNotNull(PPath.createQuadCurve(0.0f, 0.0f, 25.0f, 75.0f, 50.0f, 100.0f));
    }

    public void testCreateRectangleFloat() {
        assertNotNull(PPath.createRectangle(0.0f, 0.0f, 50.0f, 100.0f));
    }

    public void testCreateRoundRectangleFloat() {
        assertNotNull(PPath.createRoundRectangle(0.0f, 0.0f, 50.0f, 100.0f, 4.0f, 8.0f));
    }

    public void testCreateArcDouble() {
        assertNotNull(PPath.createArc(0.0d, 0.0d, 50.0d, 100.0d, 25.0d, 75.0d, Arc2D.OPEN));
    }

    public void testCreateCubicCurveDouble() {
        assertNotNull(PPath.createCubicCurve(0.0d, 0.0d, 25.0d, 75.0d, 75.0d, 25.0d, 50.0d, 100.0d));
    }

    public void testCreateEllipseDouble() {
        assertNotNull(PPath.createEllipse(0.0d, 0.0d, 50.0d, 100.0d));
    }

    public void testCreateLineDouble() {
        assertNotNull(PPath.createLine(0.0d, 0.0d, 50.0d, 100.0d));
    }

    public void testCreateQuadCurveDouble() {
        assertNotNull(PPath.createQuadCurve(0.0d, 0.0d, 25.0d, 75.0d, 50.0d, 100.0d));
    }

    public void testCreateRectangleDouble() {
        assertNotNull(PPath.createRectangle(0.0d, 0.0d, 50.0d, 100.0d));
    }

    public void testCreateRoundRectangleDouble() {
        assertNotNull(PPath.createRoundRectangle(0.0d, 0.0d, 50.0d, 100.0d, 4.0d, 8.0d));
    }

    public void testAppendShape() {
        PPath path = PPath.createRectangle(0.0d, 0.0d, 50.0d, 100.0d);
        Rectangle2D rect = new Rectangle2D.Double(50.0d, 100.0d, 50.0d, 100.0d);
        path.append(rect, true);
        // todo:  shouldn't this be width + 2 * strokeWidth?
        assertEquals(101.0d, path.getWidth(), TOLERANCE);
        assertEquals(201.0d, path.getHeight(), TOLERANCE);
    }

    public void testAppendShapeNullArgument() {
        PPath path = PPath.createRectangle(0.0d, 0.0d, 50.0d, 100.0d);
        try {
            path.append((Shape) null, true);
            fail("append((Shape) null, true) expected NullPointerException");
        }
        catch (NullPointerException e) {
            // expected
        }
    }

    public void testAppendPathIterator() {
        PPath path = PPath.createRectangle(0.0d, 0.0d, 50.0d, 100.0d);
        Rectangle2D rect = new Rectangle2D.Double(50.0d, 100.0d, 50.0d, 100.0d);
        PathIterator pathIterator = rect.getPathIterator(new AffineTransform());
        path.append(pathIterator, true);
        assertEquals(101.0d, path.getWidth(), TOLERANCE);
        assertEquals(201.0d, path.getHeight(), TOLERANCE);
    }

    public void testAppendPathIteratorNullArgument() {
        PPath path = PPath.createRectangle(0.0d, 0.0d, 50.0d, 100.0d);
        try {
            path.append((PathIterator) null, true);
            fail("append((PathIterator) null, true) expected NullPointerException");
        }
        catch (NullPointerException e) {
            // expected
        }
    }

    public void testCurveTo() {
        PPath path = PPath.createRectangle(0.0d, 0.0d, 50.0d, 100.0d);
        path.curveTo(70.0d, 140.0d, 80.0d, 140.0d, 100.0d, 200.0d);
        assertEquals(101.0d, path.getWidth(), LOW_TOLERANCE);
        assertEquals(201.0d, path.getHeight(), LOW_TOLERANCE);
    }

    public void testLineTo() {
        PPath path = PPath.createRectangle(0.0d, 0.0d, 50.0d, 100.0d);
        path.lineTo(100.0d, 200.0d);
        assertEquals(101.0d, path.getWidth(), LOW_TOLERANCE);
        assertEquals(201.0d, path.getHeight(), LOW_TOLERANCE);
    }

    public void testMoveTo() {
        PPath path = PPath.createRectangle(0.0d, 0.0d, 50.0d, 100.0d);
        path.moveTo(100.0d, 200.0d);
        assertEquals(51.0d, path.getWidth(), TOLERANCE);
        assertEquals(101.0d, path.getHeight(), TOLERANCE);
    }

    public void testQuadTo() {
        PPath path = PPath.createRectangle(0.0d, 0.0d, 50.0d, 100.0d);
        path.quadTo(70.0d, 140.0d, 100.0d, 200.0d);
        assertEquals(101.0d, path.getWidth(), LOW_TOLERANCE);
        assertEquals(201.0d, path.getHeight(), LOW_TOLERANCE);
    }

    public void testClosePath() {
        PPath path = PPath.createRectangle(0.0d, 0.0d, 50.0d, 100.0d);
        path.lineTo(100.0d, 200.0d);
        path.closePath();
    }

    public void testClosePathAlreadyClosed() {
        PPath path = PPath.createRectangle(0.0d, 0.0d, 50.0d, 100.0d);
        path.lineTo(100.0d, 200.0d);
        path.closePath();
        path.closePath();
    }

    public void testIntersects() {
        PPath path = PPath.createRectangle(0.0d, 0.0d, 50.0d, 100.0d);
        assertTrue(path.intersects(new Rectangle2D.Double(0.0d, 0.0d, 2.0d, 2.0d)));
        assertTrue(path.intersects(new Rectangle2D.Double(25.0d, 50.0d, 2.0d, 2.0d)));
        assertTrue(path.intersects(new Rectangle2D.Double(49.0d, 99.0d, 2.0d, 2.0d)));
        assertFalse(path.intersects(new Rectangle2D.Double(-10.0d, -10.0d, 2.0d, 2.0d)));
        assertFalse(path.intersects(new Rectangle2D.Double(100.0d, 200.0d, 2.0d, 2.0d)));
    }

    public void testIntersectsNullStroke() {
        PPath path = PPath.createRectangle(0.0d, 0.0d, 50.0d, 100.0d);
        path.setStroke(null);
        assertTrue(path.intersects(new Rectangle2D.Double(0.0d, 0.0d, 2.0d, 2.0d)));
        assertTrue(path.intersects(new Rectangle2D.Double(25.0d, 50.0d, 2.0d, 2.0d)));
        assertTrue(path.intersects(new Rectangle2D.Double(49.0d, 99.0d, 2.0d, 2.0d)));
        assertFalse(path.intersects(new Rectangle2D.Double(-10.0d, -10.0d, 2.0d, 2.0d)));
        assertFalse(path.intersects(new Rectangle2D.Double(100.0d, 200.0d, 2.0d, 2.0d)));
    }

    public void testIntersectsNullPaint() {
        PPath path = PPath.createRectangle(0.0d, 0.0d, 50.0d, 100.0d);
        path.setPaint(null);
        assertTrue(path.intersects(new Rectangle2D.Double(0.0d, 0.0d, 2.0d, 2.0d)));
        assertFalse(path.intersects(new Rectangle2D.Double(25.0d, 50.0d, 2.0d, 2.0d)));
        assertTrue(path.intersects(new Rectangle2D.Double(49.0d, 99.0d, 2.0d, 2.0d)));
        assertFalse(path.intersects(new Rectangle2D.Double(-10.0d, -10.0d, 2.0d, 2.0d)));
        assertFalse(path.intersects(new Rectangle2D.Double(100.0d, 200.0d, 2.0d, 2.0d)));
    }

    public void testIntersectsNullPaintNullStroke() {
        PPath path = PPath.createRectangle(0.0d, 0.0d, 50.0d, 100.0d);
        path.setPaint(null);
        path.setStroke(null);
        assertFalse(path.intersects(new Rectangle2D.Double(0.0d, 0.0d, 2.0d, 2.0d)));
        assertFalse(path.intersects(new Rectangle2D.Double(25.0d, 50.0d, 2.0d, 2.0d)));
        assertFalse(path.intersects(new Rectangle2D.Double(49.0d, 99.0d, 2.0d, 2.0d)));
        assertFalse(path.intersects(new Rectangle2D.Double(-10.0d, -10.0d, 2.0d, 2.0d)));
        assertFalse(path.intersects(new Rectangle2D.Double(100.0d, 200.0d, 2.0d, 2.0d)));
    }

    public void testFullIntersects() {
        PPath path = PPath.createRectangle(0.0d, 0.0d, 50.0d, 100.0d);
        assertTrue(path.fullIntersects(new Rectangle2D.Double(0.0d, 0.0d, 2.0d, 2.0d)));
        assertTrue(path.fullIntersects(new Rectangle2D.Double(25.0d, 50.0d, 2.0d, 2.0d)));
        assertTrue(path.fullIntersects(new Rectangle2D.Double(49.0d, 99.0d, 2.0d, 2.0d)));
        assertFalse(path.fullIntersects(new Rectangle2D.Double(-10.0d, -10.0d, 2.0d, 2.0d)));
        assertFalse(path.fullIntersects(new Rectangle2D.Double(100.0d, 200.0d, 2.0d, 2.0d)));
    }

    public void testFullIntersectsNullStroke() {
        PPath path = PPath.createRectangle(0.0d, 0.0d, 50.0d, 100.0d);
        path.setStroke(null);
        assertTrue(path.fullIntersects(new Rectangle2D.Double(0.0d, 0.0d, 2.0d, 2.0d)));
        assertTrue(path.fullIntersects(new Rectangle2D.Double(25.0d, 50.0d, 2.0d, 2.0d)));
        assertTrue(path.fullIntersects(new Rectangle2D.Double(49.0d, 99.0d, 2.0d, 2.0d)));
        assertFalse(path.fullIntersects(new Rectangle2D.Double(-10.0d, -10.0d, 2.0d, 2.0d)));
        assertFalse(path.fullIntersects(new Rectangle2D.Double(100.0d, 200.0d, 2.0d, 2.0d)));
    }

    public void testFullIntersectsNullPaint() {
        PPath path = PPath.createRectangle(0.0d, 0.0d, 50.0d, 100.0d);
        path.setPaint(null);
        assertTrue(path.fullIntersects(new Rectangle2D.Double(0.0d, 0.0d, 2.0d, 2.0d)));
        assertTrue(path.fullIntersects(new Rectangle2D.Double(25.0d, 50.0d, 2.0d, 2.0d)));
        assertTrue(path.fullIntersects(new Rectangle2D.Double(49.0d, 99.0d, 2.0d, 2.0d)));
        assertFalse(path.fullIntersects(new Rectangle2D.Double(-10.0d, -10.0d, 2.0d, 2.0d)));
        assertFalse(path.fullIntersects(new Rectangle2D.Double(100.0d, 200.0d, 2.0d, 2.0d)));
    }

    public void testFullIntersectsNullPaintNullStroke() {
        PPath path = PPath.createRectangle(0.0d, 0.0d, 50.0d, 100.0d);
        path.setPaint(null);
        path.setStroke(null);
        assertTrue(path.fullIntersects(new Rectangle2D.Double(0.0d, 0.0d, 2.0d, 2.0d)));
        assertTrue(path.fullIntersects(new Rectangle2D.Double(25.0d, 50.0d, 2.0d, 2.0d)));
        assertTrue(path.fullIntersects(new Rectangle2D.Double(49.0d, 99.0d, 2.0d, 2.0d)));
        assertFalse(path.fullIntersects(new Rectangle2D.Double(-10.0d, -10.0d, 2.0d, 2.0d)));
        assertFalse(path.fullIntersects(new Rectangle2D.Double(100.0d, 200.0d, 2.0d, 2.0d)));
    }
}
