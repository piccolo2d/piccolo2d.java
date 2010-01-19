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
package edu.umd.cs.piccolo.nodes;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import junit.framework.TestCase;
import edu.umd.cs.piccolo.MockPropertyChangeListener;
import edu.umd.cs.piccolo.PiccoloAsserts;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PObjectOutputStream;

/**
 * Unit test for PPath.
 */
public class PPathTest extends TestCase {

    private MockPropertyChangeListener mockListener;

    public void setUp() {
        mockListener = new MockPropertyChangeListener();
    }

    public void testStrokeIsNotNullByDefault() {
        final PPath path = new PPath();
        assertNotNull(path.getStroke());
    }

    public void testStrokePaintIsBlackByDefault() {
        final PPath path = new PPath();
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

    public void testSetStrokePaintPersists() {
        final PPath path = new PPath();
        path.setStrokePaint(Color.RED);
        assertEquals(Color.RED, path.getStrokePaint());
    }

    public void testSetStrokeFiresPropertyChangeEvent() {
        final PPath path = new PPath();
        path.addPropertyChangeListener(PPath.PROPERTY_STROKE_PAINT, mockListener);
        path.setStrokePaint(Color.RED);
        assertEquals(1, mockListener.getPropertyChangeCount());
    }

    public void testChangingPathFiresPropertyChangeEvent() {
        final PPath path = new PPath();
        path.addPropertyChangeListener(PPath.PROPERTY_PATH, mockListener);
        path.append(new Rectangle2D.Double(0, 0, 100, 50), true);
        assertEquals(1, mockListener.getPropertyChangeCount());
    }

}
