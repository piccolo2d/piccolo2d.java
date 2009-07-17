/*
 * Copyright (c) 2008-2009, Piccolo2D project, http://piccolo2d.org
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
package edu.umd.cs.piccolo;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;

import junit.framework.TestCase;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PPickPath;

public class PCameraTest extends TestCase {

    private PCamera camera;

    public PCameraTest(String name) {
        super(name);
    }

    public void setUp() {
        camera = new PCamera();
        PDebug.debugBounds = false;
        PDebug.debugFullBounds = false;
    }

    public void testClone() {
        PNode n = new PNode();

        PLayer layer1 = new PLayer();
        PLayer layer2 = new PLayer();

        PCamera camera1 = new PCamera();
        PCamera camera2 = new PCamera();

        n.addChild(layer1);
        n.addChild(layer2);
        n.addChild(camera1);
        n.addChild(camera2);

        camera1.addLayer(layer1);
        camera1.addLayer(layer2);
        camera2.addLayer(layer1);
        camera2.addLayer(layer2);

        // no layers should be written out since they are written conditionally.
        PCamera cameraCopy = (PCamera) camera1.clone();
        assertEquals(cameraCopy.getLayerCount(), 0);

        n.clone();
        assertEquals(((PCamera) n.getChildrenReference().get(2)).getLayerCount(), 2);
        assertEquals(((PLayer) n.getChildrenReference().get(1)).getCameraCount(), 2);
    }

    public void testCameraShouldHaveNullComponentUntilAssigned() {
        assertNull(camera.getComponent());

        MockPComponent component = new MockPComponent();
        camera.setComponent(component);

        assertNotNull(camera.getComponent());
        assertEquals(component, camera.getComponent());
    }

    public void testLayersReferenceIsNotNullByDefault() {
        assertNotNull(camera.getLayersReference());
    }

    public void testCameraHasNoLayersByDefault() {
        assertEquals(0, camera.getLayerCount());
    }

    public void testIndexOfLayerReturnsMinusOneWhenLayerNotFound() {
        PLayer orphanLayer = new PLayer();
        assertEquals(-1, camera.indexOfLayer(orphanLayer));

        camera.addLayer(new PLayer());
        assertEquals(-1, camera.indexOfLayer(orphanLayer));
    }

    public void testRemoveLayerByReferenceWorks() {
        PLayer layer = new PLayer();
        camera.addLayer(layer);
        camera.removeLayer(layer);
        assertEquals(0, camera.getLayerCount());
    }

    public void testRemoveLayerByReferenceDoesNothingWithStrangeLayerWorks() {
        PLayer strangeLayer = new PLayer();
        camera.removeLayer(strangeLayer);        
    }
    
    public void testRemoveLayerRemovesTheCameraFromTheLayer() {
        PLayer layer = new PLayer();
        camera.addLayer(layer);        
        camera.removeLayer(layer);
        assertEquals(0, layer.getCameraCount());
    }
    
    public void testAddingLayerAddCameraToLayer() {
        PLayer layer = new PLayer();
        camera.addLayer(layer);               
        assertSame(camera, layer.getCamera(0));
    }

    public void testGetFullUnionOfLayerFullBoundsWorks() {
        PLayer layer1 = new PLayer();
        layer1.setBounds(0, 0, 10, 10);
        camera.addLayer(layer1);

        PLayer layer2 = new PLayer();
        layer2.setBounds(10, 10, 10, 10);
        camera.addLayer(layer2);

        PBounds fullLayerBounds = camera.getUnionOfLayerFullBounds();
        assertEquals(new PBounds(0, 0, 20, 20), fullLayerBounds);
    }

    public void testPaintPaintsAllLayers() {
        PCanvas canvas = new PCanvas();
        PCamera camera = canvas.getCamera();

        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(img);

        PLayer layer1 = canvas.getLayer();
        PNode blueSquare = new PNode();
        blueSquare.setPaint(Color.BLUE);
        blueSquare.setBounds(0, 0, 10, 10);
        layer1.addChild(blueSquare);
        camera.addLayer(layer1);

        PLayer layer2 = new PLayer();
        canvas.getLayer().getRoot().addChild(layer2);
        layer2.setOffset(10, 10);
        PNode redSquare = new PNode();
        redSquare.setPaint(Color.RED);
        redSquare.setBounds(0, 0, 10, 10);
        layer2.addChild(redSquare);
        camera.addLayer(layer2);

        canvas.setBounds(0, 0, 20, 20);
        canvas.paint(g2);

        assertEquals(Color.BLUE.getRGB(), img.getRGB(5, 5));
        assertEquals(Color.RED.getRGB(), img.getRGB(15, 15));
    }

    public void testPickPackWorksInSimpleCases() {
        PLayer layer = new PLayer();
        camera.addChild(layer);

        PNode node1 = new PNode();
        node1.setBounds(0, 0, 10, 10);
        layer.addChild(node1);

        PNode node2 = new PNode();
        node2.setBounds(0, 0, 10, 10);
        node2.setOffset(10, 10);
        layer.addChild(node2);

        PPickPath path1 = camera.pick(5, 5, 1);
        assertEquals(node1, path1.getPickedNode());

        PPickPath path2 = camera.pick(15, 15, 1);
        assertEquals(node2, path2.getPickedNode());
    }

    public void testDefaultViewScaleIsOne() {
        assertEquals(1, camera.getViewScale(), 0.0001);
    }

    public void testGetViewBoundsTransformsCamerasBounds() {
        camera.setBounds(0, 0, 100, 100);
        camera.getViewTransformReference().scale(10, 10);
        assertEquals(new PBounds(0, 0, 10, 10), camera.getViewBounds());
    }

    public void testScaleViewIsCummulative() {
        camera.scaleView(2);
        assertEquals(2, camera.getViewScale(), 0.001);
        camera.scaleView(2);
        assertEquals(4, camera.getViewScale(), 0.001);
    }

    public void testSetViewScalePersists() {
        camera.setViewScale(2);
        assertEquals(2, camera.getViewScale(), 0.001);
        camera.setViewScale(2);
        assertEquals(2, camera.getViewScale(), 0.001);
    }

    public void testTranslateViewIsCummulative() {
        camera.translateView(100, 100);
        assertEquals(100, camera.getViewTransform().getTranslateX(), 0.001);
        camera.translateView(100, 100);
        assertEquals(200, camera.getViewTransform().getTranslateX(), 0.001);
    }

    public void testViewTransformedFiresChangeEvent() {
        MockPropertyChangeListener mockListener = new MockPropertyChangeListener();
        camera.addPropertyChangeListener(PCamera.PROPERTY_VIEW_TRANSFORM, mockListener);
        camera.setViewTransform(PAffineTransform.getScaleInstance(2, 2));
        assertEquals(1, mockListener.getPropertyChangeCount());
    }

    public void testAnimateViewToCenterBoundsIsImmediateWhenDurationIsZero() {
        camera.setViewBounds(new PBounds(0, 0, 10, 10));
        PBounds targetBounds = new PBounds(-5, -5, 10, 10);
        PActivity activity = camera.animateViewToCenterBounds(targetBounds, true, 0);
        assertNull(activity);

        assertEquals(-5, camera.getViewTransform().getTranslateX(), 0.001);
        assertEquals(-5, camera.getViewTransform().getTranslateY(), 0.001);
    }

    public void testAnimateViewToCenterBoundsCreatesValidActivity() {
        camera.setViewBounds(new PBounds(0, 0, 10, 10));
        PBounds targetBounds = new PBounds(-5, -5, 10, 10);
        PActivity activity = camera.animateViewToCenterBounds(targetBounds, true, 100);
        assertNotNull(activity);

        assertEquals(100, activity.getDuration());
        assertFalse(activity.isStepping());
    }

    public void testAnimateViewToPanToBoundsDoesNotAffectScale() {
        camera.setViewBounds(new PBounds(0, 0, 10, 10));
        camera.animateViewToPanToBounds(new PBounds(10, 10, 10, 30), 0);

        assertEquals(1, camera.getViewScale(), 0.0001);
    }

    public void testAnimateViewToPanToBoundsIsImmediateWhenDurationIsZero() {
        camera.setViewBounds(new PBounds(0, 0, 10, 10));
        PActivity activity = camera.animateViewToPanToBounds(new PBounds(10, 10, 10, 10), 0);

        assertNull(activity);
        assertEquals(PAffineTransform.getTranslateInstance(-15, -15), camera.getViewTransform());
    }

    public void testAnimateViewToPanToBoundsReturnsAppropriatelyConfiguredActivity() {
        camera.setViewBounds(new PBounds(0, 0, 10, 10));
        PTransformActivity activity = camera.animateViewToPanToBounds(new PBounds(10, 10, 10, 10), 100);

        assertNotNull(activity);
        assertEquals(100, activity.getDuration());
        assertFalse(activity.isStepping());
        assertEquals(PAffineTransform.getTranslateInstance(-15, -15), new PAffineTransform(activity
                .getDestinationTransform()));
    }

    public void testPDebugDebugBoundsPaintsBounds() throws IOException {
        PCanvas canvas = new PCanvas();

        PNode parent = new PNode();
        PNode child = new PNode();

        parent.addChild(child);
        parent.setBounds(0, 0, 10, 10);
        child.setBounds(20, 0, 10, 10);
        canvas.setBounds(0, 0, 100, 100);
        canvas.setSize(100, 100);
        canvas.getLayer().addChild(parent);

        parent.setPaint(Color.GREEN);
        child.setPaint(Color.GREEN);

        PDebug.debugBounds = true;
        PDebug.debugFullBounds = false;

        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(img);
        graphics.setPaint(Color.WHITE);
        graphics.fillRect(0, 0, 100, 100);
        PPaintContext pc = new PPaintContext(graphics);
        canvas.setDefaultRenderQuality(PPaintContext.LOW_QUALITY_RENDERING);
        canvas.getCamera().paint(pc);

        // First Square's Bounds
        assertPointColor(Color.RED, img, 0, 0);
        assertPointColor(Color.RED, img, 9, 0);
        assertPointColor(Color.RED, img, 10, 10);
        assertPointColor(Color.RED, img, 0, 10);

        // Second Square's Bounds
        assertPointColor(Color.RED, img, 20, 0);
        assertPointColor(Color.RED, img, 29, 0);
        assertPointColor(Color.RED, img, 29, 10);
        assertPointColor(Color.RED, img, 20, 10);

        // Ensure point between the squares on the full bounds is not drawn
        assertPointColor(Color.WHITE, img, 15, 10);
    }

    private void assertPointColor(Color expectedColor, BufferedImage img, int x, int y) {
        assertEquals(expectedColor.getRGB(), img.getRGB(x, y));
    }

    public void testSetViewOffsetIsNotCummulative() {
        camera.setViewOffset(100, 100);
        camera.setViewOffset(100, 100);
        assertEquals(100, camera.getViewTransform().getTranslateX(), 0.001);
        assertEquals(100, camera.getViewTransform().getTranslateY(), 0.001);

    }

    public void testDefaultViewConstraintsIsNone() {
        assertEquals(PCamera.VIEW_CONSTRAINT_NONE, camera.getViewConstraint());
    }

    public void testSetViewContraintsPersists() {
        camera.setViewConstraint(PCamera.VIEW_CONSTRAINT_ALL);
        assertEquals(PCamera.VIEW_CONSTRAINT_ALL, camera.getViewConstraint());
    }

    class MockPComponent implements PComponent {

        public void paintImmediately() {
        }

        public void popCursor() {
        }

        public void pushCursor(Cursor cursor) {
        }

        public void repaint(PBounds bounds) {
        }

        public void setInteracting(boolean interacting) {
        }
    }
}
