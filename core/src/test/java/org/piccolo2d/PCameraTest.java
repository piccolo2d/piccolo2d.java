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
package org.piccolo2d;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;

import org.piccolo2d.PCamera;
import org.piccolo2d.PCanvas;
import org.piccolo2d.PComponent;
import org.piccolo2d.PLayer;
import org.piccolo2d.PNode;
import org.piccolo2d.activities.PActivity;
import org.piccolo2d.activities.PTransformActivity;
import org.piccolo2d.util.PAffineTransform;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PDebug;
import org.piccolo2d.util.PNodeFilter;
import org.piccolo2d.util.PPaintContext;
import org.piccolo2d.util.PPickPath;

import junit.framework.TestCase;

/**
 * Unit test for PCamera.
 */
public class PCameraTest extends TestCase {

    private PCamera camera;

    public PCameraTest(final String name) {
        super(name);
    }

    public void setUp() {
        camera = new PCamera();
        PDebug.debugBounds = false;
        PDebug.debugFullBounds = false;
    }

    public void testClone() throws CloneNotSupportedException {
        final PLayer layer1 = new PLayer();
        final PLayer layer2 = new PLayer();

        final PCamera camera1 = new PCamera();
        camera1.addLayer(layer1);
        camera1.addLayer(layer2);


        final PCamera cameraCopy = (PCamera) camera1.clone();
        //TODO: assertEquals(2, cameraCopy.getLayerCount());                       
    }

    public void testCameraShouldHaveNullComponentUntilAssigned() {
        assertNull(camera.getComponent());

        final MockPComponent component = new MockPComponent();
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
        final PLayer orphanLayer = new PLayer();
        assertEquals(-1, camera.indexOfLayer(orphanLayer));

        camera.addLayer(new PLayer());
        assertEquals(-1, camera.indexOfLayer(orphanLayer));
    }

    public void testRemoveLayerByReferenceWorks() {
        final PLayer layer = new PLayer();
        camera.addLayer(layer);
        camera.removeLayer(layer);
        assertEquals(0, camera.getLayerCount());
    }

    public void testRemoveLayerByReferenceDoesNothingWithStrangeLayerWorks() {
        final PLayer strangeLayer = new PLayer();
        camera.removeLayer(strangeLayer);
    }

    public void testRemoveLayerRemovesTheCameraFromTheLayer() {
        final PLayer layer = new PLayer();
        camera.addLayer(layer);
        camera.removeLayer(layer);
        assertEquals(0, layer.getCameraCount());
    }

    public void testAddingLayerAddCameraToLayer() {
        final PLayer layer = new PLayer();
        camera.addLayer(layer);
        assertSame(camera, layer.getCamera(0));
    }

    public void testGetFullUnionOfLayerFullBoundsWorks() {
        final PLayer layer1 = new PLayer();
        layer1.setBounds(0, 0, 10, 10);
        camera.addLayer(layer1);

        final PLayer layer2 = new PLayer();
        layer2.setBounds(10, 10, 10, 10);
        camera.addLayer(layer2);

        final PBounds fullLayerBounds = camera.getUnionOfLayerFullBounds();
        assertEquals(new PBounds(0, 0, 20, 20), fullLayerBounds);
    }

    public void testPaintPaintsAllLayers() {
        final PCanvas canvas = new PCanvas();
        final PCamera camera = canvas.getCamera();

        final BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g2 = GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(img);

        final PLayer layer1 = canvas.getLayer();
        final PNode blueSquare = new PNode();
        blueSquare.setPaint(Color.BLUE);
        blueSquare.setBounds(0, 0, 10, 10);
        layer1.addChild(blueSquare);
        camera.addLayer(layer1);

        final PLayer layer2 = new PLayer();
        canvas.getLayer().getRoot().addChild(layer2);
        layer2.setOffset(10, 10);
        final PNode redSquare = new PNode();
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
        final PLayer layer = new PLayer();
        camera.addChild(layer);

        final PNode node1 = new PNode();
        node1.setBounds(0, 0, 10, 10);
        layer.addChild(node1);

        final PNode node2 = new PNode();
        node2.setBounds(0, 0, 10, 10);
        node2.setOffset(10, 10);
        layer.addChild(node2);

        final PPickPath path1 = camera.pick(5, 5, 1);
        assertEquals(node1, path1.getPickedNode());

        final PPickPath path2 = camera.pick(15, 15, 1);
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
        final MockPropertyChangeListener mockListener = new MockPropertyChangeListener();
        camera.addPropertyChangeListener(PCamera.PROPERTY_VIEW_TRANSFORM, mockListener);
        camera.setViewTransform(AffineTransform.getScaleInstance(2, 2));
        assertEquals(1, mockListener.getPropertyChangeCount());
    }

    public void testAnimateViewToCenterBoundsIsImmediateWhenDurationIsZero() {
        camera.setViewBounds(new PBounds(0, 0, 10, 10));
        final PBounds targetBounds = new PBounds(-5, -5, 10, 10);
        final PActivity activity = camera.animateViewToCenterBounds(targetBounds, true, 0);
        assertNull(activity);

        assertEquals(-5, camera.getViewTransform().getTranslateX(), 0.001);
        assertEquals(-5, camera.getViewTransform().getTranslateY(), 0.001);
    }

    public void testAnimateViewToCenterBoundsCreatesValidActivity() {
        camera.setViewBounds(new PBounds(0, 0, 10, 10));
        final PBounds targetBounds = new PBounds(-5, -5, 10, 10);
        final PActivity activity = camera.animateViewToCenterBounds(targetBounds, true, 100);
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
        final PActivity activity = camera.animateViewToPanToBounds(new PBounds(10, 10, 10, 10), 0);

        assertNull(activity);
        assertEquals(AffineTransform.getTranslateInstance(-15, -15), camera.getViewTransform());
    }

    public void testAnimateViewToPanToBoundsReturnsAppropriatelyConfiguredActivity() {
        camera.setViewBounds(new PBounds(0, 0, 10, 10));
        final PTransformActivity activity = camera.animateViewToPanToBounds(new PBounds(10, 10, 10, 10), 100);

        assertNotNull(activity);
        assertEquals(100, activity.getDuration());
        assertFalse(activity.isStepping());
        assertEquals(AffineTransform.getTranslateInstance(-15, -15), new PAffineTransform(activity
                .getDestinationTransform()));
    }

    public void testPDebugDebugBoundsPaintsBounds() throws IOException {
        final PCanvas canvas = new PCanvas();

        final PNode parent = new PNode();
        final PNode child = new PNode();

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

        final BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics = GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(img);
        graphics.setPaint(Color.WHITE);
        graphics.fillRect(0, 0, 100, 100);
        final PPaintContext pc = new PPaintContext(graphics);
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

    private void assertPointColor(final Color expectedColor, final BufferedImage img, final int x, final int y) {
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
        camera.setViewConstraint(PCamera.VIEW_CONSTRAINT_CENTER);
        assertEquals(PCamera.VIEW_CONSTRAINT_CENTER, camera.getViewConstraint());
        camera.setViewConstraint(PCamera.VIEW_CONSTRAINT_NONE);
        assertEquals(PCamera.VIEW_CONSTRAINT_NONE, camera.getViewConstraint());
    }

    public void testSetViewConstraintsThrowsIllegalArgumentException() {
        try {
            camera.setViewConstraint(-1);
        }
        catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testTooFewLayersCamera() {
        PCamera tooFew = new TooFewLayersCamera();
        MockPLayer layer0 = new MockPLayer();
        MockPLayer layer1 = new MockPLayer();
        tooFew.addLayer(layer0);
        tooFew.addLayer(layer1);
        assertEquals(layer0, tooFew.getLayer(0));
        assertEquals(layer1, tooFew.getLayer(1));
        assertEquals(layer0, tooFew.getLayersReference().get(0));
        assertEquals(layer1, tooFew.getLayersReference().get(1));
        assertEquals(0, tooFew.indexOfLayer(layer0));
        assertEquals(0, tooFew.indexOfLayer(layer0));

        // pickCameraView
        PPickPath pickPath = new PPickPath(tooFew, new PBounds(0, 0, 400, 400));
        tooFew.pickCameraView(pickPath);
        assertTrue(layer0.fullPickCalled());
        assertTrue(layer1.fullPickCalled());

        // paintCameraView
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        PPaintContext paintContext = new PPaintContext(graphics);
        tooFew.paintCameraView(paintContext);
        assertTrue(layer0.fullPaintCalled());
        assertTrue(layer1.fullPaintCalled());

        // getUnionOfLayerFullBounds
        tooFew.getUnionOfLayerFullBounds();
        assertTrue(layer0.fullBoundsReferenceCalled());
        assertTrue(layer1.fullBoundsReferenceCalled());

        // paintDebugInfo
        PDebug.debugBounds = true;
        tooFew.paintDebugInfo(paintContext);
        assertTrue(layer0.getAllNodesCalled());
        assertTrue(layer1.getAllNodesCalled());
        PDebug.debugBounds = false;

        graphics.dispose();
    }

    public void testTooManyLayersCamera() {
        PCamera tooMany = new TooManyLayersCamera();
        MockPLayer layer0 = new MockPLayer();
        MockPLayer layer1 = new MockPLayer();
        tooMany.addLayer(layer0);
        tooMany.addLayer(layer1);
        assertEquals(layer0, tooMany.getLayer(0));
        assertEquals(layer1, tooMany.getLayer(1));
        assertEquals(layer0, tooMany.getLayersReference().get(0));
        assertEquals(layer1, tooMany.getLayersReference().get(1));
        assertEquals(0, tooMany.indexOfLayer(layer0));
        assertEquals(0, tooMany.indexOfLayer(layer0));

        // pickCameraView
        PPickPath pickPath = new PPickPath(tooMany, new PBounds(0, 0, 400, 400));
        tooMany.pickCameraView(pickPath);
        assertTrue(layer0.fullPickCalled());
        assertTrue(layer1.fullPickCalled());

        // paintCameraView
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        PPaintContext paintContext = new PPaintContext(graphics);
        tooMany.paintCameraView(paintContext);
        assertTrue(layer0.fullPaintCalled());
        assertTrue(layer1.fullPaintCalled());

        // getUnionOfLayerFullBounds
        tooMany.getUnionOfLayerFullBounds();
        assertTrue(layer0.fullBoundsReferenceCalled());
        assertTrue(layer1.fullBoundsReferenceCalled());

        // paintDebugInfo
        PDebug.debugBounds = true;
        tooMany.paintDebugInfo(paintContext);
        assertTrue(layer0.getAllNodesCalled());
        assertTrue(layer1.getAllNodesCalled());
        PDebug.debugBounds = false;

        graphics.dispose();
    }

    public void testRepaintFromNullParent() {
        camera.setParent(null);
        PCanvas canvas = new PCanvas();
        camera.setComponent(canvas);
        camera.repaintFrom(new PBounds(0, 0, 1, 1), camera);
    }

    public void testRepaintFromNullComponent() {
        PNode parent = new PNode();
        camera.setParent(parent);
        camera.setComponent(null);
        camera.repaintFrom(new PBounds(0, 0, 1, 1), camera);
    }

    public void testRepaintFromNullParentNullComponent() {
        camera.setParent(null);
        camera.setComponent(null);
        camera.repaintFrom(new PBounds(0, 0, 1, 1), camera);
    }

    public void testRepaintFromLayer() {
        PLayer layer = new PLayer();
        camera.addLayer(layer);
        camera.repaintFromLayer(new PBounds(0, 0, 1, 1), layer);
    }

    public void testRepaintFromLayerNotViewedByCamera() {
        PLayer layer = new PLayer();
        // todo:  layer is not contained in list of layers viewed by camera, should complain
        camera.repaintFromLayer(new PBounds(0, 0, 1, 1), layer);
    }

    public void testRemoveLayerAtIndex() {
        PLayer layer = new PLayer();
        camera.addLayer(layer);
        assertEquals(1, camera.getLayerCount());
        assertEquals(1, camera.getLayersReference().size());
        camera.removeLayer(0);
        assertEquals(0, camera.getLayerCount());
        assertEquals(0, camera.getLayersReference().size());
    }

    public void testRemoveLayerAtIndexIndexOutOfBounds() {
        PLayer layer = new PLayer();
        camera.addLayer(layer);
        try {
            camera.removeLayer(2);
            fail("removeLayer(2) expected IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testPaintDebugInfoDebugFullBounds() {
        PLayer layer = new PLayer();
        camera.addLayer(layer);
        PNode child = new PNode();
        child.setBounds(0.0d, 0.0d, 200.0d, 200.0d);
        layer.addChild(child);
        BufferedImage image = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        PPaintContext paintContext = new PPaintContext(graphics);
        PDebug.debugFullBounds = true;
        camera.paintDebugInfo(paintContext);
        PDebug.debugFullBounds = false;
        graphics.dispose();
    }

    public void testPaintDebugInfoDebugFullBoundsNoChildNodes() {
        PLayer layer = new PLayer();
        camera.addLayer(layer);
        BufferedImage image = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        PPaintContext paintContext = new PPaintContext(graphics);
        PDebug.debugFullBounds = true;
        camera.paintDebugInfo(paintContext);
        PDebug.debugFullBounds = false;
        graphics.dispose();
    }

    public void testPickAfterChildrenNotPicked() {
        PPickPath pickPath = new PPickPath(camera, new PBounds(-5, -5, 0, 0));
        assertFalse(camera.pickAfterChildren(pickPath));
    }

    public void testLocalToViewPoint2D() {
        Point2D local = new Point2D.Double(0.0d, 0.0d);
        camera.localToView(local);
        assertEquals(0.0d, local.getX(), 0.1d);
        assertEquals(0.0d, local.getY(), 0.1d);
    }

    public void testLocalToViewPoint2DTranslateView() {
        camera.translateView(10.0d, 20.0d);
        Point2D local = new Point2D.Double(0.0d, 0.0d);
        camera.localToView(local);
        assertEquals(-10.0d, local.getX(), 0.1d);
        assertEquals(-20.0d, local.getY(), 0.1d);
    }

    public void testLocalToViewPoint2DScaleView() {
        camera.scaleView(10.0d);
        Point2D local = new Point2D.Double(10.0d, 20.0d);
        camera.localToView(local);
        assertEquals(1.0d, local.getX(), 0.1d);
        assertEquals(2.0d, local.getY(), 0.1d);
    }

    public void testLocalToViewDimension2D() {
        Dimension2D local = new Dimension(0, 0);
        camera.localToView(local);
        assertEquals(0.0d, local.getWidth(), 0.1d);
        assertEquals(0.0d, local.getHeight(), 0.1d);
    }

    public void testLocalToViewDimension2DTranslateView() {
        camera.translateView(10.0d, 20.0d);
        Dimension2D local = new Dimension(0, 0);
        camera.localToView(local);
        assertEquals(0.0d, local.getWidth(), 0.1d);
        assertEquals(0.0d, local.getHeight(), 0.1d);
    }

    public void testLocalToViewDimension2DScaleView() {
        camera.scaleView(10.0d);
        Dimension2D local = new Dimension(10, 20);
        camera.localToView(local);
        assertEquals(1.0d, local.getWidth(), 0.1d);
        assertEquals(2.0d, local.getHeight(), 0.1d);
    }

    public void testViewToLocalPoint2D() {
        Point2D view = new Point2D.Double(0.0d, 0.0d);
        camera.viewToLocal(view);
        assertEquals(0.0d, view.getX(), 0.1d);
        assertEquals(0.0d, view.getY(), 0.1d);
    }

    public void testViewToLocalPoint2DTranslateView() {
        camera.translateView(10.0d, 20.0d);
        Point2D view = new Point2D.Double(0.0d, 0.0d);
        camera.viewToLocal(view);
        assertEquals(10.0d, view.getX(), 0.1d);
        assertEquals(20.0d, view.getY(), 0.1d);
    }

    public void testViewToLocalPoint2DScaleView() {
        camera.scaleView(10.0d);
        Point2D view = new Point2D.Double(10.0d, 20.0d);
        camera.viewToLocal(view);
        assertEquals(100.0d, view.getX(), 0.1d);
        assertEquals(200.0d, view.getY(), 0.1d);
    }

    public void testViewToLocalDimension2D() {
        Dimension2D view = new Dimension(0, 0);
        camera.viewToLocal(view);
        assertEquals(0.0d, view.getWidth(), 0.1d);
        assertEquals(0.0d, view.getHeight(), 0.1d);
    }

    public void testViewToLocalDimension2DTranslateView() {
        camera.translateView(10.0d, 20.0d);
        Dimension2D view = new Dimension(0, 0);
        camera.viewToLocal(view);
        assertEquals(0.0d, view.getWidth(), 0.1d);
        assertEquals(0.0d, view.getHeight(), 0.1d);
    }

    public void testViewToLocalDimension2DScaleView() {
        camera.scaleView(10.0d);
        Dimension2D view = new Dimension(10, 20);
        camera.viewToLocal(view);
        assertEquals(100.0d, view.getWidth(), 0.1d);
        assertEquals(200.0d, view.getHeight(), 0.1d);
    }

    public void testPickWithoutIntersectionStillContainsCamera() {
        camera.offset(10.0d, 10.0d);
        PPickPath pickPath = camera.pick(0.0d, 0.0d, 0.0d);
        // todo:  don't understand why this should be the case
        assertFalse(pickPath.getNodeStackReference().isEmpty());
        assertTrue(pickPath.getNodeStackReference().contains(camera));
    }

    /*
    public void testAnimateViewToTransformIdentity() {
        PRoot root = new PRoot();
        PLayer layer = new PLayer();
        root.addChild(camera);
        root.addChild(layer);
        camera.addChild(layer);

        AffineTransform identity = new AffineTransform();
        camera.animateViewToTransform(identity, System.currentTimeMillis());
        // todo:  throws NPE at PActivityScheduler.processActivities(PActivityScheduler.java:176)
        root.waitForActivities();

        assertSame(identity, camera.getViewTransformReference());
    }
    */


    static class MockPComponent implements PComponent {

        public void paintImmediately() {
        }

        public void popCursor() {
        }

        public void pushCursor(final Cursor cursor) {
        }

        public void repaint(final PBounds bounds) {
        }

        public void setInteracting(final boolean interacting) {
        }
    }

    /**
     * Mock PLayer.  Should consider using mock library in version 2.0.
     */
    private static final class MockPLayer extends PLayer {
        private static final long serialVersionUID = 1L;
        private boolean fullBoundsReferenceCalled = false;
        private boolean fullPaintCalled = false;
        private boolean getAllNodesCalled = false;
        private boolean fullPickCalled = false;

        /** {@inheritDoc} */
        public PBounds getFullBoundsReference() {
            fullBoundsReferenceCalled = true;
            return super.getFullBoundsReference();
        }

        /** {@inheritDoc} */
        public void fullPaint(final PPaintContext paintContext) {
            fullPaintCalled = true;
            super.fullPaint(paintContext);
        }

        /** {@inheritDoc} */
        public Collection getAllNodes(final PNodeFilter filter, final Collection nodes) {
            getAllNodesCalled = true;
            return super.getAllNodes(filter, nodes);
        }

        /** {@inheritDoc} */
        public boolean fullPick(final PPickPath pickPath) {
            fullPickCalled = true;
            return super.fullPick(pickPath);
        }

        private boolean fullBoundsReferenceCalled() {
            return fullBoundsReferenceCalled;
        }

        private boolean fullPaintCalled() {
            return fullPaintCalled;
        }

        private boolean getAllNodesCalled() {
            return getAllNodesCalled;
        }

        private boolean fullPickCalled() {
            return fullPickCalled;
        }
    }

    /**
     * Subclass of PCamera that advertises too few layers.
     */
    private static final class TooFewLayersCamera extends PCamera {
        private static final long serialVersionUID = 1L;

        /** {@inheritDoc} */
        public int getLayerCount() {
            return Math.max(0, super.getLayerCount() - 1);
        }
    }

    /**
     * Subclass of PCamera that advertises too many layers.
     */
    private static final class TooManyLayersCamera extends PCamera {
        private static final long serialVersionUID = 1L;

        /** {@inheritDoc} */
        public int getLayerCount() {
            return super.getLayerCount() + 1;
        }
    }
}
