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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.ListIterator;

import javax.swing.text.MutableAttributeSet;

import org.piccolo2d.PCanvas;
import org.piccolo2d.PLayer;
import org.piccolo2d.PNode;
import org.piccolo2d.activities.PActivity;
import org.piccolo2d.activities.PColorActivity;
import org.piccolo2d.activities.PInterpolatingActivity;
import org.piccolo2d.activities.PTransformActivity;
import org.piccolo2d.activities.PColorActivity.Target;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.util.PAffineTransform;
import org.piccolo2d.util.PAffineTransformException;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PDimension;
import org.piccolo2d.util.PNodeFilter;
import org.piccolo2d.util.PPaintContext;
import org.piccolo2d.util.PPickPath;
import org.piccolo2d.util.PUtil;

import junit.framework.TestCase;

/**
 * Unit test for PNode.
 */
public class PNodeTest extends TestCase {

    private MockPropertyChangeListener mockListener;
    private PNode node;

    public PNodeTest(final String name) {
        super(name);
    }

    public void setUp() {
        node = new PNode();
        mockListener = new MockPropertyChangeListener();
    }

    public void testCenterBaseBoundsOnPoint() {
        node.setBounds(100, 300, 100, 80);
        node.centerBoundsOnPoint(0, 0);
        assertEquals(-50, node.getBoundsReference().getX(), 0);
        assertEquals(-40, node.getBoundsReference().getY(), 0);
    }

    public void testClientProperties() {
        final PNode n = new PNode();

        assertNull(n.getAttribute(null));
        n.addAttribute("a", "b");
        assertEquals(n.getAttribute("a"), "b");
        assertNull(n.getAttribute(null));
        n.addAttribute("a", null);
        assertNull(n.getAttribute("a"));
    }

    public void testFullScale() {
        final PNode aParent = new PNode();
        final PNode aNode = new PNode();

        aParent.addChild(aNode);

        aParent.scale(2.0);
        aNode.scale(0.5);

        assertEquals(1.0, aNode.getGlobalScale(), 0);

        aParent.setScale(1.0);
        assertEquals(0.5, aNode.getGlobalScale(), 0);

        aNode.setScale(.75);
        assertEquals(0.75, aNode.getGlobalScale(), 0);
    }

    public void testReparent() {
        final PNode aParent = new PNode();
        final PNode aNode = new PNode();

        aParent.setOffset(400, 500);
        aParent.scale(0.5);
        aNode.reparent(aParent);

        assertEquals(0, aNode.getGlobalTranslation().getX(), 0);
        assertEquals(0, aNode.getGlobalTranslation().getY(), 0);
        assertEquals(2.0, aNode.getScale(), 0);

        aNode.setGlobalScale(0.25);
        aNode.setGlobalTranslation(new Point2D.Double(10, 10));

        assertEquals(10, aNode.getGlobalTranslation().getX(), 0);
        assertEquals(10, aNode.getGlobalTranslation().getY(), 0);
        assertEquals(0.25, aNode.getGlobalScale(), 0);
    }

    public void testFindIntersectingNodes() {
        final PNode n = new PNode();
        final PNode c = new PNode();

        n.addChild(c);
        n.setBounds(0, 0, 100, 100);
        c.setBounds(0, 0, 100, 100);
        c.scale(200);

        ArrayList found = new ArrayList();
        final Rectangle2D rect2d = new Rectangle2D.Double(50, 50, 10, 10);
        n.findIntersectingNodes(rect2d, found);

        assertEquals(found.size(), 2);
        assertEquals(rect2d.getHeight(), 10, 0);
        found = new ArrayList();

        final PBounds bounds = new PBounds(50, 50, 10, 10);
        n.findIntersectingNodes(bounds, found);

        assertEquals(found.size(), 2);
        assertEquals(bounds.getHeight(), 10, 0);
    }

    public void testRemoveNonexistantListener() {
        final PNode n = new PNode();
        n.removeInputEventListener(new PBasicInputEventHandler());
    }

    public void testAddChildHandleDuplicates() {
        final PNode parent = new PNode();
        parent.addChild(node);
        parent.addChild(new PNode());
        parent.addChild(node);
        assertEquals(1, parent.indexOfChild(node));
    }

    public void testAddChildCanSpecifyAnIndexAndDoesntReplace() {
        final PNode parent = new PNode();
        parent.addChild(new PNode());
        parent.addChild(0, node);
        assertEquals(0, parent.indexOfChild(node));
        assertEquals(2, parent.getChildrenCount());
    }

    public void testAddChildWithIndexMovesChildAround() {
        final PNode parent = new PNode();

        parent.addChild(new PNode());
        parent.addChild(new PNode());
        parent.addChild(node);

        parent.addChild(0, node);
        assertEquals(node, parent.getChild(0));

        parent.addChild(1, node);
        assertEquals(node, parent.getChild(1));

        parent.addChild(2, node);
        assertEquals(node, parent.getChild(2));
    }

    public void testCloneCopiesAllProperties() {
        node.setBounds(1, 2, 3, 4);
        node.setChildPaintInvalid(true);
        node.setChildrenPickable(false);
        node.setPaint(Color.yellow);
        node.setPaintInvalid(true);
        node.setPickable(false);
        node.setPropertyChangeParentMask(PNode.PROPERTY_CODE_PAINT);
        node.setVisible(false);

        final PNode clonedNode = (PNode) node.clone();

        assertEquals(1, clonedNode.getX(), Double.MIN_VALUE);
        assertEquals(2, clonedNode.getY(), Double.MIN_VALUE);
        assertEquals(3, clonedNode.getWidth(), Double.MIN_VALUE);
        assertEquals(4, clonedNode.getHeight(), Double.MIN_VALUE);
        assertTrue(clonedNode.getChildPaintInvalid());
        assertFalse(clonedNode.getChildrenPickable());
        assertEquals(Color.YELLOW, clonedNode.getPaint());

        assertFalse(clonedNode.getPickable());
        assertEquals(PNode.PROPERTY_CODE_PAINT, node.getPropertyChangeParentMask());
        assertFalse(clonedNode.getVisible());
    }

    public void testCloneCopiesTransforms() {
        node.setScale(0.5);
        node.setRotation(Math.PI / 8d);
        node.setOffset(5, 6);

        final PNode clonedNode = (PNode) node.clone();

        assertEquals(0.5, clonedNode.getScale(), 0.00001);
        assertEquals(Math.PI / 8d, clonedNode.getRotation(), 0.00001);
        assertEquals(5, clonedNode.getXOffset(), Double.MIN_VALUE);
        assertEquals(6, clonedNode.getYOffset(), Double.MIN_VALUE);
    }

    public void testCloneDoesNotCopyEventListeners() {
        node.addInputEventListener(new PBasicInputEventHandler() {});

        final PNode clonedNode = (PNode) node.clone();

        assertNull(clonedNode.getListenerList());      
    }
    
    public void testCloneClonesChildrenAswell() {
        final PNode child = new PNode();
        node.addChild(child);

        final PNode clonedNode = (PNode) node.clone();

        assertEquals(clonedNode.getChildrenCount(), 1);
        assertNotSame(child, clonedNode.getChild(0));
    }
    
    public void testCloneDoesADeepCopy() {
        final PNode child = new PNode();
        node.addChild(child);

        final PNode clonedNode = (PNode) node.clone();

        assertNotSame(node.getChildrenReference(), clonedNode.getChildrenReference());
        assertNotSame(node.getChild(0), clonedNode.getChild(0));
        
        assertNotSame(node.getBoundsReference(), clonedNode.getBoundsReference());
    }
    
    public void testCloneDoesNotCopyParent() {
        final PNode child = new PNode();
        node.addChild(child);

        final PNode clonedChild = (PNode) child.clone();

        assertNull(clonedChild.getParent());
    }

    public void testLocalToGlobal() {
        final PNode aParent = new PNode();
        final PNode aChild = new PNode();

        aParent.addChild(aChild);
        aChild.scale(0.5);

        // bounds
        final PBounds bnds = new PBounds(0, 0, 50, 50);

        aChild.localToGlobal(bnds);
        assertEquals(0, bnds.x, 0);
        assertEquals(0, bnds.y, 0);
        assertEquals(25, bnds.width, 0);
        assertEquals(25, bnds.height, 0);

        aChild.globalToLocal(bnds);
        assertEquals(0, bnds.x, 0);
        assertEquals(0, bnds.y, 0);
        assertEquals(50, bnds.width, 0);
        assertEquals(50, bnds.height, 0);

        aChild.getGlobalToLocalTransform(new PAffineTransform());
        aChild.getLocalToGlobalTransform(new PAffineTransform()).createTransformedShape(aChild.getBounds());

        // dimensions
        final PDimension dim = new PDimension(50, 50);

        aChild.localToGlobal(dim);
        assertEquals(25, dim.getHeight(), 0);
        assertEquals(25, dim.getWidth(), 0);

        aChild.globalToLocal(dim);
        assertEquals(50, dim.getHeight(), 0);
        assertEquals(50, dim.getWidth(), 0);
    }

    public void testToString() {
        final PNode a = new PNode();
        final PNode b = new PNode();
        final PNode c = new PNode();
        final PNode d = new PNode();
        final PNode e = new PNode();
        final PNode f = new PNode();

        a.translate(100, 100);
        a.getFullBoundsReference();

        a.addChild(b);
        b.addChild(c);
        c.addChild(d);
        d.addChild(e);
        e.addChild(f);

        assertNotNull(a.toString());
    }

    public void testRecursiveLayout() {
        final PNode layoutNode1 = new PNode() {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            protected void layoutChildren() {
                if (getChildrenCount() > 0) {
                    getChild(0).setOffset(1, 0);
                }
            }
        };

        final PNode layoutNode2 = new PNode() {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            protected void layoutChildren() {
                if (getChildrenCount() > 0) {
                    getChild(0).setOffset(1, 0);
                }
            }
        };

        layoutNode1.addChild(layoutNode2);

        final PNode n = new PNode();
        n.setBounds(0, 0, 100, 100);

        layoutNode2.addChild(n);

        n.setBounds(10, 10, 100, 100);

        layoutNode1.getFullBoundsReference();
    }

    public void testAnimateToBoundsWithDuration0IsImmediate() {
        node.setBounds(0, 0, 100, 100);

        final PActivity activity = node.animateToBounds(50, 50, 150, 150, 0);
        assertNull(activity);

        final PBounds resultBounds = node.getBounds();
        assertEquals(50.0, resultBounds.x, 0.001);
        assertEquals(50.0, resultBounds.y, 0.001);
        assertEquals(150.0, resultBounds.width, 0.001);
        assertEquals(150.0, resultBounds.height, 0.001);
    }

    public void testAnimateToBoundsHasProperSetup() {
        node.setBounds(0, 0, 100, 100);
        final PInterpolatingActivity activity = node.animateToBounds(50, 50, 150, 150, 50);

        assertEquals(50, activity.getDuration());
        assertEquals(PUtil.DEFAULT_ACTIVITY_STEP_RATE, activity.getStepRate());
        assertTrue(activity.getFirstLoop());
        assertFalse(activity.isStepping());
    }

    public void testAnimateTransformToBoundsWithDuration0IsImmediate() {
        node.setBounds(0, 0, 100, 100);
        final PActivity activity = node.animateTransformToBounds(0, 0, 10, 10, 0);

        assertNull(activity);

        final PAffineTransform transform = node.getTransform();
        assertEquals(0.1, transform.getScale(), 0.0001);
    }

    public void testAnimateTransformToBoundsHasProperSetup() {
        node.setBounds(0, 0, 100, 100);
        final PTransformActivity activity = node.animateTransformToBounds(0, 0, 10, 10, 50);

        assertEquals(50, activity.getDuration());
        assertEquals(PUtil.DEFAULT_ACTIVITY_STEP_RATE, activity.getStepRate());
        assertTrue(activity.getFirstLoop());
        assertFalse(activity.isStepping());

        final double[] resultTransform = activity.getDestinationTransform();

        assertEquals(0.1, resultTransform[0], 0.001);
        assertEquals(0, resultTransform[1], 0.001);
        assertEquals(0, resultTransform[2], 0.001);
        assertEquals(0.1, resultTransform[3], 0.001);
        assertEquals(0, resultTransform[4], 0.001);
        assertEquals(0, resultTransform[5], 0.001);
    }

    public void testAnimateToPositionScaleRotationWithDuration0IsImmediate() {
        node.setBounds(0, 0, 100, 100);
        final PActivity activity = node.animateToPositionScaleRotation(50, 50, 0.5, Math.PI, 0);

        assertNull(activity);

        final PAffineTransform resultTransform = node.getTransform();

        final PAffineTransform expected = new PAffineTransform();
        expected.translate(50, 50);
        expected.scale(0.5, 0.5);
        expected.rotate(Math.PI);

        assertEquals(expected, resultTransform);
    }

    public void testAnimateToPositionScaleRotationHasProperSetup() {
        node.setBounds(0, 0, 100, 100);
        final PTransformActivity activity = node.animateToPositionScaleRotation(50, 50, 0.5, Math.PI, 50);

        assertEquals(50, activity.getDuration());
        assertEquals(PUtil.DEFAULT_ACTIVITY_STEP_RATE, activity.getStepRate());
        assertTrue(activity.getFirstLoop());
        assertFalse(activity.isStepping());

        final double[] resultTransform = activity.getDestinationTransform();

        final PAffineTransform expected = new PAffineTransform();
        expected.translate(50, 50);
        expected.scale(0.5, 0.5);
        expected.rotate(Math.PI);

        assertEquals(-0.5, resultTransform[0], 0.001);
        assertEquals(0, resultTransform[1], 0.001);
        assertEquals(0, resultTransform[2], 0.001);
        assertEquals(-0.5, resultTransform[3], 0.001);
        assertEquals(50.0, resultTransform[4], 0.001);
        assertEquals(50.0, resultTransform[5], 0.001);
    }

    public void testAnimateToColorWithDuration0IsImmediate() {
        node.setPaint(Color.WHITE);

        final PActivity activity = node.animateToColor(Color.BLACK, 0);

        assertNull(activity);

        assertEquals(Color.BLACK, node.getPaint());
    }

    public void testAnimateToColorHasProperSetup() {
        node.setPaint(Color.WHITE);
        final PInterpolatingActivity activity = node.animateToColor(Color.BLACK, 50);

        assertEquals(50, activity.getDuration());
        assertEquals(PUtil.DEFAULT_ACTIVITY_STEP_RATE, activity.getStepRate());
        assertTrue(activity.getFirstLoop());
        assertFalse(activity.isStepping());
        // assertEquals(Color.BLACK, activity.getDestinationColor());
        assertEquals("Paint should not change immediately", Color.WHITE, node.getPaint());
    }

    public void testAddActivityAddsActivityToScheduler() {
        final PCanvas canvas = new PCanvas();
        node.setPaint(Color.WHITE);
        canvas.getLayer().addChild(node);

        final PColorActivity activity = buildTestActivity();

        node.addActivity(activity);

        assertEquals(1, node.getRoot().getActivityScheduler().getActivitiesReference().size());
    }

    private PColorActivity buildTestActivity() {
        final Target testTarget = new PColorActivity.Target() {

            public Color getColor() {
                return Color.BLACK;
            }

            public void setColor(final Color color) {

            }
        };

        final PColorActivity activity = new PColorActivity(1000, 0, testTarget, Color.BLACK);
        return activity;
    }

    public void testAnimateToTransparencyWithDuration0IsImmediate() {
        node.setPaint(Color.WHITE);

        final PActivity activity = node.animateToTransparency(0.5f, 0);

        assertNull(activity);

        assertEquals(0.5f, node.getTransparency(), 0.0001);
    }

    public void testAnimateToTransparencyHasProperSetup() {
        final PInterpolatingActivity activity = node.animateToTransparency(0f, 50);

        assertEquals(50, activity.getDuration());
        assertEquals(PUtil.DEFAULT_ACTIVITY_STEP_RATE, activity.getStepRate());
        assertTrue(activity.getFirstLoop());
        assertFalse(activity.isStepping());

        assertEquals("Transparency should not change immediately", 1.0f, node.getTransparency(), 0.0001);
    }

    public void testGetClientPropertiesShouldReturnSetEvenIfNonePresent() {
        final MutableAttributeSet properties = node.getClientProperties();
        assertNotNull(properties);
        assertEquals(0, properties.getAttributeCount());
    }

    public void testGetClientPropertiesShouldReturnSameCollectionAlways() {
        final MutableAttributeSet properties1 = node.getClientProperties();
        final MutableAttributeSet properties2 = node.getClientProperties();
        assertSame(properties1, properties2);
    }

    public void testGetClientPropertyKeysEnumerationShouldReturnEnumarationOnNewNode() {
        final Enumeration enumeration = node.getClientPropertyKeysEnumeration();
        assertNotNull(enumeration);
        assertFalse(enumeration.hasMoreElements());
    }

    public void testGetClientPropertyKeysEnumerationShouldReturnCorrectEnumWhenPropertiesExist() {
        node.addAttribute("Testing", "Hello");
        final Enumeration enumeration = node.getClientPropertyKeysEnumeration();
        assertNotNull(enumeration);
        assertTrue(enumeration.hasMoreElements());
        assertEquals("Testing", enumeration.nextElement());
        assertFalse(enumeration.hasMoreElements());
    }

    public void testGetAttributeReturnsNullWhenMissing() {
        assertNull(node.getAttribute("Testing"));
    }

    public void testGetAttributeReturnsValueWhenPresent() {
        node.addAttribute("Testing", "Hello");
        assertEquals("Hello", node.getAttribute("Testing"));
    }

    public void testGetAttributeReturnsDefaultWhenProvided() {
        assertEquals("Default", node.getAttribute("Missing", "Default"));
    }

    public void testGetAttributeReturnsValueIfFoundWhenDefaultProvided() {
        node.addAttribute("Found", "Hello");
        assertEquals("Hello", node.getAttribute("Found", "Default"));
    }

    public void testGetBooleanAttributeReturnsDefaultWhenProvided() {
        assertEquals(false, node.getBooleanAttribute("Missing", false));
    }

    public void testGetBooleanAttributeReturnsValueIfFoundWhenDefaultProvided() {
        node.addAttribute("Found", Boolean.TRUE);
        assertEquals(true, node.getBooleanAttribute("Found", false));
    }

    public void testGetIntegerAttributeReturnsDefaultWhenProvided() {
        assertEquals(10, node.getIntegerAttribute("Missing", 10));
    }

    public void testGetIntegerAttributeReturnsValueIfFoundWhenDefaultProvided() {
        node.addAttribute("Found", new Integer(5));
        assertEquals(5, node.getIntegerAttribute("Found", 10));
    }

    public void testGetDoubleAttributeReturnsDefaultWhenProvided() {
        assertEquals(10, node.getDoubleAttribute("Missing", 10), 0.001);
    }

    public void testGetDoubleAttributeReturnsValueIfFoundWhenDefaultProvided() {
        node.addAttribute("Found", new Double(5));
        assertEquals(5, node.getIntegerAttribute("Found", 10), 0.001);
    }

    public void testLocalToParentModifiesGivenPoint() {
        final PNode parent = new PNode();
        parent.addChild(node);

        node.scale(0.5);

        final Point2D point = new Point2D.Double(5, 6);
        node.localToParent(point);
        assertTrue(5 != point.getX());
        assertTrue(6 != point.getY());
    }

    public void testLocalToParentDoesWorkWithOrphanChildWhenTransformed() {
        node.scale(0.5);

        final Point2D point = new Point2D.Double(5, 6);
        node.localToParent(point);
        assertTrue(5 != point.getX());
        assertTrue(6 != point.getY());
    }

    public void testLocalToParentDoesNothingWithOrphanChildWhenNotTransformed() {
        final Point2D point = new Point2D.Double(5, 6);
        node.localToParent(point);
        assertEquals(5, point.getX(), 0.0001);
        assertEquals(6, point.getY(), 0.0001);
    }

    public void testParentToLocalModifiesGivenPoint() {
        final PNode parent = new PNode();
        parent.addChild(node);

        node.scale(0.5);

        final Point2D point = new Point2D.Double(5, 6);
        node.parentToLocal(point);
        assertTrue(5 != point.getX());
        assertTrue(6 != point.getY());
    }

    public void testParentToLocalTransformsOrphanChildWhenTransformed() {
        final PNode aChild = new PNode();
        aChild.scale(0.5);

        final Point2D point = new Point2D.Double(5, 6);
        aChild.parentToLocal(point);
        assertEquals(10, point.getX(), 0.0001);
        assertEquals(12, point.getY(), 0.0001);
    }

    public void testGlobalToLocalWorksUnTransformedNodes() {
        final PNode parent = new PNode();
        parent.addChild(node);

        final Point2D point = new Point2D.Double(10, 11);
        node.globalToLocal(point);
        assertEquals(10, point.getX(), 0.0001);
        assertEquals(11, point.getY(), 0.0001);
    }

    public void testRemoveEventListener() {
        final PBasicInputEventHandler eventListener = new PBasicInputEventHandler();
        node.addInputEventListener(eventListener);
        assertEquals(1, node.getListenerList().getListenerCount());
        node.removeInputEventListener(eventListener);
        assertNull(node.getListenerList());

    }

    public void testAddPropertyChangeListener() {
        node.addPropertyChangeListener(mockListener);
        node.setBounds(0, 0, 100, 100);
        assertEquals(1, mockListener.getPropertyChangeCount());
    }

    public void testAddPropertyChangeListenerForPropertyName() {
        node.addPropertyChangeListener(PNode.PROPERTY_BOUNDS, mockListener);
        node.setBounds(0, 0, 100, 100);
        assertEquals(1, mockListener.getPropertyChangeCount());
    }

    public void testRemovePropertyChangeListener() {
        node.addPropertyChangeListener(mockListener);
        node.removePropertyChangeListener(mockListener);
        node.setBounds(0, 0, 100, 100);
        assertEquals(0, mockListener.getPropertyChangeCount());
    }

    public void testRemovePropertyChangeListenerForPropertyName() {
        node.addPropertyChangeListener(PNode.PROPERTY_BOUNDS, mockListener);
        node.removePropertyChangeListener(PNode.PROPERTY_BOUNDS, mockListener);
        node.setBounds(0, 0, 100, 100);
        assertEquals(0, mockListener.getPropertyChangeCount());
    }

    public void testPropertyChangesCascadeToParent() {
        final PNode aParent = new PNode();
        aParent.addPropertyChangeListener(PNode.PROPERTY_BOUNDS, mockListener);

        final PNode aChild = new PNode();
        aChild.setPropertyChangeParentMask(PNode.PROPERTY_CODE_BOUNDS);
        aParent.addChild(aChild);

        aChild.setBounds(0, 0, 100, 100);
        assertEquals(1, mockListener.getPropertyChangeCount());
        final PropertyChangeEvent propEvent = mockListener.getPropertyChange(0);
        assertEquals(PNode.PROPERTY_BOUNDS, propEvent.getPropertyName());
        assertEquals(new PBounds(0, 0, 100, 100), propEvent.getNewValue());
    }

    public void testStartEndResizeBoundsCanBeCalledWithoutResizes() {
        node.startResizeBounds();
        node.endResizeBounds();
    }

    public void testSetXModifiesBounds() {
        node.setX(10);
        assertEquals(10, node.getBounds().getX(), 0.0001);
    }

    public void testSetYModifiesBounds() {
        node.setY(10);
        assertEquals(10, node.getBounds().getY(), 0.0001);
    }

    public void testSetHeightModifiesBounds() {
        node.setHeight(10);
        assertEquals(10, node.getBounds().getHeight(), 0.0001);
    }

    public void testSetWidthModifiesBounds() {
        node.setWidth(10);
        assertEquals(10, node.getBounds().getWidth(), 0.0001);
    }

    public void testResetBoundsDoesSo() {
        node.setBounds(10, 15, 100, 115);
        node.resetBounds();

        final PBounds zeroBounds = new PBounds();
        assertEquals(zeroBounds, node.getBounds());
    }

    public void testCenterBoundsOnPointWorksAsExpected() {
        node.setBounds(0, 0, 100, 100);
        node.centerBoundsOnPoint(0, 0);

        final PBounds expected = new PBounds(-50, -50, 100, 100);
        assertEquals(expected, node.getBounds());
    }

    public void testCenterFullBoundsOnPointWorksAsExpected() {
        final PNode aParent = buildComplexSquareNode();

        aParent.centerFullBoundsOnPoint(0, 0);

        final PBounds expected = new PBounds(-50, -50, 100, 100);
        assertEquals(expected, aParent.getFullBounds());
    }

    private PNode buildComplexSquareNode() {
        final PNode aParent = new PNode();
        aParent.setBounds(0, 0, 50, 100);

        final PNode child1 = new PNode();
        child1.setBounds(50, 0, 50, 50);
        aParent.addChild(child1);

        final PNode child2 = new PNode();
        child2.setBounds(50, 50, 50, 50);
        aParent.addChild(child2);

        return aParent;
    }

    public void testGetUnionOfChildrenBoundsAcceptsNull() {
        final PNode node = buildComplexSquareNode();

        final PBounds union = node.getUnionOfChildrenBounds(null);

        assertNotNull(union);
        assertEquals(new PBounds(50, 0, 50, 100), union);
    }

    public void testGetGlobalFullBoundsIsSameWhenNoTransforms() {
        final PNode parent = new PNode();
        final PNode child = new PNode();
        parent.addChild(child);
        final PNode grandChild = new PNode();
        child.addChild(grandChild);
        child.setBounds(50, 0, 50, 50);
        grandChild.setBounds(0, 50, 50, 50);

        final PBounds globalFullBounds = parent.getGlobalFullBounds();

        assertNotNull(globalFullBounds);
        assertEquals(new PBounds(0, 0, 100, 100), globalFullBounds);
    }

    public void testChildBoundsStayVolatile() {
        node.setChildBoundsVolatile(true);
        assertTrue(node.getChildBoundsVolatile());
    }

    public void testRotatingChangesRotation() {
        node.rotate(Math.PI);
        assertEquals(Math.PI, node.getRotation(), 0.0001);
    }

    public void testSetRotationIsNotCummulative() {
        node.setRotation(Math.PI);
        node.setRotation(Math.PI);
        assertEquals(Math.PI, node.getRotation(), 0.0001);
    }

    public void testRotateAboutPointDoesNotAffectBounds() {
        node.setBounds(25, 25, 50, 50);
        node.rotateAboutPoint(Math.PI, 50, 25); // It's top center point
        assertEquals(new PBounds(25, 25, 50, 50), node.getBounds());
    }

    public void testRotateAboutPointVersion1AffectsTransformAsItShould() {
        node.setBounds(25, 25, 50, 50);
        node.rotateAboutPoint(Math.PI, 50, 0); // It's top center point

        final PAffineTransform expectedTransform = new PAffineTransform();
        expectedTransform.translate(100, 0);
        expectedTransform.rotate(Math.PI);

        assertEquals(expectedTransform, node.getTransform());
    }

    public void testRotateAboutPointVersion2AffectsTransformAsItShould() {
        node.setBounds(25, 25, 50, 50);
        node.rotateAboutPoint(Math.PI, new Point2D.Double(50, 0)); // It's top
        // center
        // point

        final PAffineTransform expectedTransform = new PAffineTransform();
        expectedTransform.translate(100, 0);
        expectedTransform.rotate(Math.PI);

        assertEquals(expectedTransform, node.getTransform());
    }

    public void testScaleAboutPointWorksAsExpected() {
        node.setBounds(0, 0, 100, 100);
        node.scaleAboutPoint(2, new Point2D.Double(50, 50));
        final PAffineTransform expectedTransform = new PAffineTransform();
        expectedTransform.translate(-50, -50);
        expectedTransform.scale(2, 2);

        assertEquals(expectedTransform, node.getTransform());
    }

    public void testRotateInPlaneLeavesFullBoundsUntouched() {
        node.setBounds(25, 25, 50, 50);
        final PBounds boundsBefore = node.getFullBounds();

        node.rotateInPlace(Math.PI);
        assertEquals(boundsBefore, node.getFullBounds());
    }

    public void testSetGlobalScaleTakesParentsScaleIntoAccount() {
        final PNode aParent = new PNode();
        aParent.scale(2);

        final PNode aChild = new PNode();
        aParent.addChild(aChild);

        aChild.setGlobalScale(1);

        assertEquals(0.5, aChild.getScale(), 0.0001);
    }

    public void testOffsetDoesNotTakeBoundsIntoAccount() {
        node.setOffset(10, 20);
        node.setBounds(50, 50, 100, 100);
        assertEquals(10, node.getXOffset(), 0.001);
        assertEquals(20, node.getYOffset(), 0.001);
    }

    public void testTransformByIsCummulative() {
        node.transformBy(AffineTransform.getScaleInstance(2, 2));
        node.transformBy(AffineTransform.getScaleInstance(2, 2));

        assertEquals(AffineTransform.getScaleInstance(4, 4), node.getTransform());
    }

    public void testLerp() {
        assertEquals(5, PNode.lerp(0.5, 0, 10), 0.001);
        assertEquals(0, PNode.lerp(0, 0, 10), 0.001);
        assertEquals(10, PNode.lerp(1, 0, 10), 0.001);
    }

    public void testAnimateToRelativePositionResultsInProperTransform() {
        final PCanvas canvas = new PCanvas();
        final PNode A = new PNode();
        A.setBounds(0, 0, 50, 50);
        canvas.getLayer().addChild(A);
        final PNode B = new PNode();
        B.setBounds(0, 0, 100, 100);
        B.setOffset(100, 100);
        canvas.getLayer().addChild(B);

        final Point2D srcPt = new Point2D.Double(1.0, 0.0);
        final Point2D destPt = new Point2D.Double(0.0, 0.0);
        A.animateToRelativePosition(srcPt, destPt, B.getGlobalBounds(), 0);

        final PAffineTransform expectedTransform = new PAffineTransform();
        expectedTransform.translate(50, 100);

        assertEquals(expectedTransform, A.getTransform());
    }

    public void testGetInverseTransformWorks() {
        node.translate(50, 50);
        node.rotate(Math.PI);

        final PAffineTransform expectedTransform = new PAffineTransform();
        expectedTransform.rotate(-Math.PI);
        expectedTransform.translate(-50, -50);
        assertEquals(expectedTransform, node.getInverseTransform());
    }

    public void testGetInverseTransformThrowsExceptionWhenTransformIsNotInvertible() {
        node.setTransform(new AffineTransform(new double[] { 0, 0, 0, 0, 0, 0 }));

        try {
            node.getInverseTransform();
            fail("Exception not thrown");
        }
        catch (final PAffineTransformException e) {
            // expected
        }
    }

    public void testSetVisibleIsRespectedOnPaint() {
        final int[] paintCounts = new int[1];

        final PNode node = new PNode() {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            public void paint(final PPaintContext pc) {
                paintCounts[0]++;
            }
        };
        node.setBounds(0, 0, 100, 100);
        node.setVisible(true);

        final PCanvas canvas = buildCanvasContainingNode(node);

        final BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        final Graphics g = GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(img);

        canvas.paintComponent(g);

        assertEquals(1, paintCounts[0]);

        node.setVisible(false);
        node.invalidatePaint();
        canvas.paintComponent(g);
        assertEquals(1, paintCounts[0]);

        node.setVisible(true);
        node.invalidatePaint();
        canvas.paintComponent(g);
        assertEquals(2, paintCounts[0]);
    }

    public void testSetTransparency1MeansInvisible() {
        final PNode node = new PNode();
        node.setBounds(0, 0, 100, 100);
        node.setVisible(true);
        node.setPaint(Color.RED);

        final PCanvas canvas = buildCanvasContainingNode(node);

        final BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        final Graphics g = GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(img);

        canvas.paintComponent(g);
        node.setTransparency(1f);
        assertEquals(Color.RED.getRGB(), img.getRGB(10, 10));

        node.setTransparency(0f);
        canvas.paintComponent(g);
        assertEquals(Color.WHITE.getRGB(), img.getRGB(10, 10));

    }

    private PCanvas buildCanvasContainingNode(final PNode node) {
        final PCanvas canvas = new PCanvas();
        canvas.setSize(100, 100);
        canvas.getLayer().addChild(node);
        return canvas;
    }

    public void testPaintColourIsRespectedOnPaint() {
        final BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        final Graphics g = GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(img);

        node.setPaint(Color.RED);
        node.setBounds(0, 0, 100, 100);

        final PCanvas canvas = buildCanvasContainingNode(node);
        canvas.paintComponent(g);

        assertEquals(Color.RED.getRGB(), img.getRGB(0, 0));
    }

    public void testToImageReturnsValidImage() {
        node.setBounds(0, 0, 10, 10);
        node.setPaint(Color.RED);

        // Really don't like casting here, but... without changing the
        // interface, I don't see a choice
        final BufferedImage img = (BufferedImage) node.toImage();

        assertEquals(10, img.getHeight(null));
        assertEquals(10, img.getWidth(null));
        assertEquals(Color.RED.getRGB(), img.getRGB(0, 0));
        assertEquals(Color.RED.getRGB(), img.getRGB(9, 0));
        assertEquals(Color.RED.getRGB(), img.getRGB(0, 9));
        assertEquals(Color.RED.getRGB(), img.getRGB(9, 9));
    }

    public void testToImageUsesFullBoundsWhenConvertingImage() throws IOException {
        node.setBounds(0, 0, 50, 50);
        PNode child1 = new PNode();
        child1.setBounds(0, 0, 100, 50);
        child1.setPaint(Color.RED);
        node.addChild(child1);
        
        PNode child2 = new PNode();
        child2.setBounds(0, 0, 50, 100);
        child2.setPaint(Color.BLUE);
        node.addChild(child2);
        
        BufferedImage image = (BufferedImage) node.toImage();
        assertNotNull(image);
        assertEquals(100, image.getWidth());
        assertEquals(100, image.getHeight());           
        assertEquals(Color.RED.getRGB(), image.getRGB(99, 1));
        
        //This line fails if PNode.toImage uses getWidth() rather than getFullBounds().getWidth()
        assertEquals(Color.BLUE.getRGB(), image.getRGB(1, 99));
    }

    public void testToImageWillAcceptBackgroundPaint() {
        node.setBounds(0, 0, 10, 10);

        final BufferedImage img = (BufferedImage) node.toImage(10, 10, Color.BLUE);
        assertEquals(Color.BLUE.getRGB(), img.getRGB(5, 5));
    }

    public void testToImageResultsInDesiredSizeImage() {
        node.setBounds(0, 0, 10, 10);

        final BufferedImage img = (BufferedImage) node.toImage(20, 40, null);
        assertEquals(40, img.getHeight(null));
        assertEquals(20, img.getWidth(null));
    }

    public void testToImageWithBackgroundColorGivenReturnsValidImage() {
        node.setBounds(0, 0, 10, 10);
        node.setPaint(Color.RED);

        final BufferedImage img = (BufferedImage) node.toImage(20, 40, Color.BLUE);
        assertEquals(Color.RED.getRGB(), img.getRGB(0, 0));
        assertEquals(Color.BLUE.getRGB(), img.getRGB(15, 25));
    }

    public void testToImageScalesNodeAsBigAsCanBe() throws IOException {
        node.setBounds(0, 0, 10, 10);
        node.setPaint(Color.RED);

        final BufferedImage img = (BufferedImage) node.toImage(20, 40, Color.BLUE);

        assertEquals(Color.RED.getRGB(), img.getRGB(0, 0));
        assertEquals(Color.RED.getRGB(), img.getRGB(19, 0));
        assertEquals(Color.RED.getRGB(), img.getRGB(0, 19));
        assertEquals(Color.RED.getRGB(), img.getRGB(19, 19));
        assertEquals(Color.BLUE.getRGB(), img.getRGB(0, 20));
        assertEquals(Color.BLUE.getRGB(), img.getRGB(19, 20));
    }

    public void testToImageScalesAccordingToExactFitStrategy() throws IOException {
        node.setBounds(0, 0, 10, 10);
        node.setPaint(Color.RED);

        final BufferedImage img = (BufferedImage) node.toImage(new BufferedImage(20, 40, BufferedImage.TYPE_INT_RGB),
                Color.BLUE, PNode.FILL_STRATEGY_EXACT_FIT);

        assertEquals(Color.RED.getRGB(), img.getRGB(0, 0));
        assertEquals(Color.RED.getRGB(), img.getRGB(19, 0));
        assertEquals(Color.RED.getRGB(), img.getRGB(0, 39));
        assertEquals(Color.RED.getRGB(), img.getRGB(19, 39));

    }

    public void testToImageScalesAccordingToAspectCoverStrategy() throws IOException {
        node.setBounds(0, 0, 10, 10);
        node.setPaint(Color.RED);

        PNode blueSquare = new PNode();
        blueSquare.setPaint(Color.BLUE);
        blueSquare.setBounds(0, 0, 5, 5);
        node.addChild(blueSquare);

        PNode greenSquare = new PNode();
        greenSquare.setPaint(Color.GREEN);
        greenSquare.setBounds(5, 5, 5, 5);
        node.addChild(greenSquare);

        final BufferedImage img = (BufferedImage) node.toImage(new BufferedImage(20, 40, BufferedImage.TYPE_INT_RGB),
                Color.BLUE, PNode.FILL_STRATEGY_EXACT_FIT);

        assertEquals(Color.RED.getRGB(), img.getRGB(11, 19));
        assertEquals(Color.RED.getRGB(), img.getRGB(9, 20));
        assertEquals(Color.RED.getRGB(), img.getRGB(0, 20));
        assertEquals(Color.RED.getRGB(), img.getRGB(9, 39));

        assertEquals(Color.BLUE.getRGB(), img.getRGB(9, 19));
        assertEquals(Color.BLUE.getRGB(), img.getRGB(0, 0));
        assertEquals(Color.BLUE.getRGB(), img.getRGB(0, 19));
        assertEquals(Color.BLUE.getRGB(), img.getRGB(9, 0));

        assertEquals(Color.GREEN.getRGB(), img.getRGB(10, 20));
        assertEquals(Color.GREEN.getRGB(), img.getRGB(19, 20));
        assertEquals(Color.GREEN.getRGB(), img.getRGB(10, 39));
        assertEquals(Color.GREEN.getRGB(), img.getRGB(19, 39));
    }

    public void testGetPickableShouldDefaultToTrue() {
        assertTrue(node.getPickable());
    }

    public void testSetPickableFiresPropertyChange() {
        node.addPropertyChangeListener(mockListener);
        node.setPickable(false);
        assertEquals(1, mockListener.getPropertyChangeCount());
    }

    public void testChildrenShouldBePickableByDefault() {
        assertTrue(node.getChildrenPickable());
    }

    public void testSetChildrenPickableFiresPropertyChange() {
        node.addPropertyChangeListener(mockListener);
        node.setChildrenPickable(false);
        assertEquals(1, mockListener.getPropertyChangeCount());
    }

    public void testByDefaultNodesShouldNotPickThemselvesBeforeTheirChildren() {
        final PCanvas canvas = new PCanvas();
        final PPickPath pickPath = new PPickPath(canvas.getCamera(), new PBounds(0, 0, 100, 100));
        assertFalse(node.pick(pickPath));
    }

    public void testfullPickReturnsTrueWhenOverlapsWithChildNode() {
        final PCanvas canvas = new PCanvas();
        node.setBounds(0, 0, 10, 10);

        final PNode child = new PNode();
        child.setBounds(20, 0, 10, 10);
        node.addChild(child);

        final PPickPath pickPath = new PPickPath(canvas.getCamera(), new PBounds(20, 0, 10, 10));
        canvas.getLayer().addChild(node);
        assertTrue(node.fullPick(pickPath));
    }

    public void testfullPickReturnsFalseWhenNotOverlappingWithChildNode() {
        final PCanvas canvas = new PCanvas();
        node.setBounds(0, 0, 10, 10);

        final PNode child = new PNode();
        child.setBounds(10, 0, 10, 10);
        node.addChild(child);

        final PPickPath pickPath = new PPickPath(canvas.getCamera(), new PBounds(20, 0, 10, 10));
        canvas.getLayer().addChild(node);
        assertFalse(node.fullPick(pickPath));
    }

    public void testAddChildrenAddsAllChildren() {
        final Collection newChildren = new ArrayList();
        newChildren.add(new PNode());
        newChildren.add(new PNode());
        newChildren.add(new PNode());

        node.addChildren(newChildren);

        assertEquals(3, node.getChildrenCount());
    }

    public void testRemoveChildrenWorks() {
        final Collection newChildren = new ArrayList();
        newChildren.add(new PNode());
        newChildren.add(new PNode());
        newChildren.add(new PNode());
        node.addChildren(newChildren);
        node.addChild(new PNode());

        node.removeChildren(newChildren);
        assertEquals(1, node.getChildrenCount());
    }

    public void testGetAllNodesUnrollsTheNodeGraph() {
        final Collection newChildren = new ArrayList();
        newChildren.add(new PNode());
        newChildren.add(new PNode());
        newChildren.add(new PNode());

        node.addChildren(newChildren);

        assertEquals(4, node.getAllNodes().size());
    }

    public void testRemoveAllChildrenDoesntCrashWhenNoChidlren() {
        node.removeAllChildren();

        // And now for the case when there once was a child
        node.addChild(new PNode());
        node.removeAllChildren();
        node.removeAllChildren();
    }

    public void testRemoveFromParentDoesSo() {
        final PNode parent = new PNode();
        parent.addChild(node);

        node.removeFromParent();

        assertEquals(0, parent.getChildrenCount());
    }

    public void testReplaceWithSwapsParents() {
        final PNode parent = new PNode();
        parent.addChild(node);

        final PNode newNode = new PNode();
        node.replaceWith(newNode);
        assertNull(node.getParent());

        assertEquals(parent, newNode.getParent());
    }

    public void testGetChildrenIteratorReturnsIteratorEvenWithNoChildren() {
        final ListIterator iterator = node.getChildrenIterator();
        assertNotNull(iterator);
        assertFalse(iterator.hasNext());
    }

    public void testGetChildrenIteratorReturnsValidIteratorWhenHasChildren() {
        final PNode child = new PNode();
        node.addChild(child);

        final ListIterator iterator = node.getChildrenIterator();
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        assertEquals(child, iterator.next());
        assertFalse(iterator.hasNext());
    }

    public void testGetAllNodesDoesntIgnoreFilter() {
        final PNodeFilter nullFilter = new PNodeFilter() {

            public boolean accept(final PNode aNode) {
                return false;
            }

            public boolean acceptChildrenOf(final PNode aNode) {
                return true;
            }
        };

        node.addChild(new PNode());
        node.addChild(new PNode());
        node.addChild(new PNode());
        final Collection nodes = node.getAllNodes(nullFilter, null);
        assertNotNull(nodes);
        assertTrue(nodes.isEmpty());
    }

    public void testAncestryMethods() {
        final PNode child = new PNode();
        node.addChild(child);

        final PNode grandChild = new PNode();
        child.addChild(grandChild);

        final PNode unrelated = new PNode();

        assertTrue(node.isAncestorOf(child));
        assertTrue(node.isAncestorOf(grandChild));
        assertTrue(child.isDescendentOf(node));
        assertTrue(grandChild.isDescendentOf(node));

        assertFalse(node.isAncestorOf(unrelated));
        assertFalse(grandChild.isDescendentOf(unrelated));
    }

    public void testRaise() {
        final PNode parent = new PNode();
        parent.addChild(node);
        parent.addChild(new PNode());
        parent.addChild(new PNode());
        node.raise();
        assertEquals(1, parent.indexOfChild(node));
    }

    public void testRaiseOnly() {
        final PNode parent = new PNode();
        parent.addChild(node);
        node.raise();
        assertEquals(0, parent.indexOfChild(node));
    }

    public void testLower() {
        final PNode parent = new PNode();
        parent.addChild(new PNode());
        parent.addChild(new PNode());
        parent.addChild(node);
        node.lower();
        assertEquals(1, parent.indexOfChild(node));
    }

    public void testLowerOnly() {
        final PNode parent = new PNode();
        parent.addChild(node);
        node.lower();
        assertEquals(0, parent.indexOfChild(node));
    }

    public void testRaiseToTop() {
        final PNode parent = new PNode();
        parent.addChild(node);
        parent.addChild(new PNode());
        parent.addChild(new PNode());
        node.raiseToTop();
        assertEquals(2, parent.indexOfChild(node));
    }

    public void testRaiseToTopOnly() {
        final PNode parent = new PNode();
        parent.addChild(node);
        node.raiseToTop();
        assertEquals(0, parent.indexOfChild(node));
    }

    public void testLowerToBottom() {
        final PNode parent = new PNode();
        parent.addChild(new PNode());
        parent.addChild(new PNode());
        parent.addChild(node);
        node.lowerToBottom();
        assertEquals(0, parent.indexOfChild(node));
    }

    public void testLowerToBottomOnly() {
        final PNode parent = new PNode();
        parent.addChild(node);
        node.lowerToBottom();
        assertEquals(0, parent.indexOfChild(node));
    }

    public void testRaiseAbove() {
        final PNode parent = new PNode();
        parent.addChild(node);
        final PNode sibling = new PNode();
        parent.addChild(sibling);
        parent.addChild(new PNode());
        node.raiseAbove(sibling);
        assertEquals(1, parent.indexOfChild(node));
    }

    public void testLowerBelow() {
        final PNode parent = new PNode();
        parent.addChild(new PNode());
        final PNode sibling = new PNode();
        parent.addChild(sibling);
        parent.addChild(node);
        node.lowerBelow(sibling);
        assertEquals(1, parent.indexOfChild(node));
    }

    public void testRaiseChild() {
        final PNode child0 = new PNode();
        final PNode child1 = new PNode();
        final PNode child2 = new PNode();
        node.addChild(child0);
        node.addChild(child1);
        node.addChild(child2);
        node.raise(child0);
        assertEquals(1, node.indexOfChild(child0));
    }

    public void testLowerChild() {
        final PNode child0 = new PNode();
        final PNode child1 = new PNode();
        final PNode child2 = new PNode();
        node.addChild(child0);
        node.addChild(child1);
        node.addChild(child2);
        node.lower(child2);
        assertEquals(1, node.indexOfChild(child2));
    }

    public void testRaiseChildToTop() {
        final PNode child0 = new PNode();
        final PNode child1 = new PNode();
        final PNode child2 = new PNode();
        node.addChild(child0);
        node.addChild(child1);
        node.addChild(child2);
        node.raiseToTop(child0);
        assertEquals(2, node.indexOfChild(child0));
    }

    public void testLowerChildToBottom() {
        final PNode child0 = new PNode();
        final PNode child1 = new PNode();
        final PNode child2 = new PNode();
        node.addChild(child0);
        node.addChild(child1);
        node.addChild(child2);
        node.lowerToBottom(child2);
        assertEquals(0, node.indexOfChild(child2));
    }

    public void testLowerToBottomMovesNodeToBeFirstChild() {
        final PNode parent = new PNode();
        parent.addChild(new PNode());
        parent.addChild(new PNode());
        parent.addChild(node);
        node.lowerToBottom();
        assertEquals(0, parent.indexOfChild(node));
    }

    public void testRaiseToTopMovesNodeToBeLastChild() {
        final PNode parent = new PNode();
        parent.addChild(node);
        parent.addChild(new PNode());
        parent.addChild(new PNode());
        node.raiseToTop();
        assertEquals(2, parent.indexOfChild(node));
    }

    public void testLowerBelowMovesNodeToBeforeSibling() {
        final PNode parent = new PNode();
        final PNode sibling = new PNode();

        parent.addChild(node);
        parent.addChild(new PNode());
        parent.addChild(new PNode());
        parent.addChild(sibling);

        node.lowerBelow(sibling);
        assertEquals(2, parent.indexOfChild(node));
    }

    public void testRaiseAboveMovesNodeToAfterSibling() {
        final PNode parent = new PNode();
        final PNode sibling = new PNode();

        parent.addChild(node);
        parent.addChild(new PNode());
        parent.addChild(new PNode());
        parent.addChild(sibling);

        node.raiseAbove(sibling);
        assertEquals(3, parent.indexOfChild(node));
    }

    public void testRaiseAboveDoesNothingIfNotSibling() {
        final PNode parent = new PNode();
        final PNode stranger = new PNode();

        parent.addChild(node);
        parent.addChild(new PNode());
        parent.addChild(new PNode());

        node.raiseAbove(stranger);
        assertEquals(0, parent.indexOfChild(node));
    }

    public void testLowerBelowDoesNothingIfNotSibling() {
        final PNode parent = new PNode();
        final PNode stranger = new PNode();

        parent.addChild(node);
        parent.addChild(new PNode());
        parent.addChild(new PNode());

        node.lowerBelow(stranger);
        assertEquals(0, parent.indexOfChild(node));
    }

    public void testIsDescendentOfRootHandlesOrphans() {
        final PNode orphan = new PNode();

        assertFalse(orphan.isDescendentOfRoot());
        orphan.addChild(node);
        assertFalse(node.isDescendentOfRoot());
    }

    public void testIsDescendentOfRootHandlesDescendentsOfRoot() {
        final PCanvas canvas = new PCanvas();
        canvas.getLayer().addChild(node);

        assertTrue(node.isDescendentOfRoot());
    }

    public void testGetGlobalRationTakesParentsIntoAccount() {
        final PNode parent = new PNode();
        parent.rotate(Math.PI / 4d);
        parent.addChild(node);

        node.rotate(Math.PI / 4d);

        assertEquals(Math.PI / 2d, node.getGlobalRotation(), 0.001);
    }

    public void testSetGlobalRationTakesParentsIntoAccount() {
        final PNode parent = new PNode();
        parent.rotate(Math.PI / 4d);
        parent.addChild(node);

        node.setGlobalRotation(Math.PI / 2d);

        assertEquals(Math.PI / 4d, node.getRotation(), 0.001);
    }

    public void testSetGlobalRationWorksWhenNoParent() {
        node.setGlobalRotation(Math.PI / 2d);

        assertEquals(Math.PI / 2d, node.getRotation(), 0.001);
    }

    public void testSetOccludedPersistes() {
        node.setOccluded(true);
        assertTrue(node.getOccluded());
    }

    public void testHiddenNodesAreNotPickable() {
        final PCanvas canvas = new PCanvas();
        canvas.setBounds(0, 0, 400, 400);
        canvas.setPreferredSize(new Dimension(400, 400));
        final PNode node1 = new PNode();
        node1.setBounds(0, 0, 100, 100);
        node1.setPaint(Color.RED);
        canvas.getLayer().addChild(node1);

        final PNode node2 = (PNode) node1.clone();
        node2.setPaint(Color.BLUE);

        final PLayer layer2 = new PLayer();
        layer2.addChild(node2);
        layer2.setVisible(false);
        canvas.getCamera().addLayer(layer2);

        final PPickPath path = canvas.getCamera().pick(5, 5, 5);
        assertSame(node1, path.getPickedNode());
    }

    public void testGetPropertyChangeListenersEmpty() {
        PropertyChangeListener[] listeners = node.getPropertyChangeListeners();
        assertNotNull(listeners);
        assertEquals(0, listeners.length);
    }

    public void testGetPropertyChangeListeners() {
        node.addPropertyChangeListener(mockListener);
        PropertyChangeListener[] listeners = node.getPropertyChangeListeners();
        assertNotNull(listeners);
        assertEquals(1, listeners.length);
        assertEquals(mockListener, listeners[0]);
    }

    public void testGetPropertyChangeListenersStringNull() {
        PropertyChangeListener[] listeners = node.getPropertyChangeListeners(null);
        assertNotNull(listeners);
        assertEquals(0, listeners.length);
    }

    public void testGetPropertyChangeListenersStringEmpty() {
        PropertyChangeListener[] listeners = node.getPropertyChangeListeners("foo");
        assertNotNull(listeners);
        assertEquals(0, listeners.length);
    }

    public void testGetPropertyChangeListenersString() {
        node.addPropertyChangeListener("foo", mockListener);
        PropertyChangeListener[] listeners = node.getPropertyChangeListeners("foo");
        assertNotNull(listeners);
        assertEquals(1, listeners.length);
        assertEquals(mockListener, listeners[0]);
    }
}
