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

import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PObjectOutputStream;

/**
 * Abstract unit test for subclasses of PPath.
 */
public abstract class AbstractPPathTest extends AbstractPShapeTest {

    private static final double TOLERANCE = 0.0001d;
    private static final double LOW_TOLERANCE = 1.0d;

    /** {@inheritDoc} */
    protected void setUp() {
        super.setUp();
    }

    /** {@inheritDoc} */
    protected PShape createShapeNode() {
        return createPathNode();
    }

    /**
     * Create a new instance of a subclass of PPath to test.
     *
     * @return a new instance of a subclass of PPath to test
     */
    protected abstract PPath createPathNode();

    // todo:  rewrite in terms of createPathNode()

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

    public void testCreatePolylineFloatArraysEmpty() {
        try {
            PPath.createPolyline(new float[0], new float[0]);
            fail("expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testCreatePolylineFloatArraysDifferentSizes() {
        try {
            PPath.createPolyline(new float[0], new float[] { 100.0f });
            fail("expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testCreatePolylineFloatArraysDifferentSingle() {
        assertNotNull(PPath.createPolyline(new float[] { 100.0f }, new float[] { 100.0f }));
    }

    public void testCreatePolylineFloatArrays() {
        assertNotNull(PPath.createPolyline(new float[] { 100.0f, 100.0f, 200.0f }, new float[] { 100.0f, 200.0f, 200.0f }));
    }

    public void testCreatePolylinePoint2DFloatArrayNull() {
        try {
            PPath.createPolyline((Point2D.Float[]) null);
            fail("createPolyline(null) expected NullPointerException");
        }
        catch (NullPointerException e) {
            // expected
        }
    }

    public void testCreatePolylinePoint2DFloatArrayEmpty() {
        try {
            PPath.createPolyline(new Point2D.Float[0]);
            fail("expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testCreatePolylinePoint2DFloatArraySingle() {
        assertNotNull(PPath.createPolyline(new Point2D.Float[] { new Point2D.Float(100.0f, 100.0f) }));
    }

    public void testCreatePolylinePoint2DFloatArray() {
        assertNotNull(PPath.createPolyline(new Point2D.Float[] { new Point2D.Float(100.0f, 100.0f), new Point2D.Float(100.0f, 200.0f), new Point2D.Float(200.0f, 200.0f) }));
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

    /*
    public void testPath() {
        PPath path = createPathNode();
        assertNotNull(path.getPath()); // or (Path) getShape(), or getPathReference() ?
        Path2D.Double rect = new Path2D.Double((new Rectangle2D.Double(0.0d, 0.0d, 100.0d, 100.0d)));
        path.setPath(rect);
        assertEquals(rect, path.getPath());
    }

    public void testPathNullArgument() {
        PPath path = createPathNode();
        try {
            path.setPath(null);
            fail("setPath(null) expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) { // or NPE?
            // expected
        }
    }

    public void testPathBoundProperty() {
        PPath path = createPathNode();
        path.addPropertyChangeListener("path", mockListener);
        Path2D.Double rect = new Path2D.Double((new Rectangle2D.Double(0.0d, 0.0d, 100.0d, 100.0d)));
        path.setPath(rect);
        assertEquals(1, mockListener.getPropertyChangeCount());
    }
    */

    public void testAppendShapeFiresPropertyChangeEvent() {
        PPath path = createPathNode();
        path.addPropertyChangeListener("path", mockListener);
        Rectangle2D rect = new Rectangle2D.Double(50.0d, 100.0d, 50.0d, 100.0d);
        path.append(rect, true);
        assertEquals(1, mockListener.getPropertyChangeCount());
    }

    public void testAppendPathIteratorFiresPropertyChangeEvent() {
        PPath path = createPathNode();
        path.moveTo(0.0d, 0.0d);
        path.addPropertyChangeListener("path", mockListener);
        Rectangle2D rect = new Rectangle2D.Double(50.0d, 100.0d, 50.0d, 100.0d);
        PathIterator pathIterator = rect.getPathIterator(new AffineTransform());
        path.append(pathIterator, true);
        assertEquals(1, mockListener.getPropertyChangeCount());
    }

    public void testCurveToFiresPropertyChangeEvent() {
        PPath path = createPathNode();
        path.moveTo(0.0d, 0.0d);
        path.addPropertyChangeListener("path", mockListener);
        path.curveTo(70.0d, 140.0d, 80.0d, 140.0d, 100.0d, 200.0d);
        assertEquals(1, mockListener.getPropertyChangeCount());
    }

    public void testLineToFiresPropertyChangeEvent() {
        PPath path = createPathNode();
        path.moveTo(0.0d, 0.0d);
        path.addPropertyChangeListener("path", mockListener);
        path.lineTo(100.0d, 200.0d);
        assertEquals(1, mockListener.getPropertyChangeCount());
    }

    public void testMoveToFiresPropertyChangeEvent() {
        PPath path = createPathNode();
        path.moveTo(0.0d, 0.0d);
        path.addPropertyChangeListener("path", mockListener);
        path.moveTo(100.0d, 200.0d);
        assertEquals(1, mockListener.getPropertyChangeCount());
    }

    public void testQuadToFiresPropertyChangeEvent() {
        PPath path = createPathNode();
        path.moveTo(0.0d, 0.0d);
        path.addPropertyChangeListener("path", mockListener);
        path.quadTo(70.0d, 140.0d, 100.0d, 200.0d);
        assertEquals(1, mockListener.getPropertyChangeCount());
    }

    public void testClosePathFiresPropertyChangeEvent() {
        PPath path = createPathNode();
        path.moveTo(0.0d, 0.0d);
        path.lineTo(100.0d, 200.0d);
        path.addPropertyChangeListener("path", mockListener);
        path.closePath();
        assertEquals(1, mockListener.getPropertyChangeCount());
    }
}
