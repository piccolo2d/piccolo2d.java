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
public class PNodeTest extends AbstractPNodeTest {

    /** {@inheritDoc} */
    protected PNode createNode() {
        return new PNode();
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

    public void testFindIntersectingNodes() {
        final PNode c = new PNode();

        node.addChild(c);
        node.setBounds(0, 0, 100, 100);
        c.setBounds(0, 0, 100, 100);
        c.scale(200);

        ArrayList found = new ArrayList();
        final Rectangle2D rect2d = new Rectangle2D.Double(50, 50, 10, 10);
        node.findIntersectingNodes(rect2d, found);

        assertEquals(found.size(), 2);
        assertEquals(rect2d.getHeight(), 10, 0);
        found = new ArrayList();

        final PBounds bounds = new PBounds(50, 50, 10, 10);
        node.findIntersectingNodes(bounds, found);

        assertEquals(found.size(), 2);
        assertEquals(bounds.getHeight(), 10, 0);
    }

    public void testCenterFullBoundsOnPointWorksAsExpected() {
        final PNode parent = buildComplexSquareNode();

        parent.centerFullBoundsOnPoint(0, 0);

        final PBounds expected = new PBounds(-50, -50, 100, 100);
        assertEquals(expected, parent.getFullBounds());
    }

    // todo:  not sure about this. . .
    private PNode buildComplexSquareNode() {
        final PNode parent = new PNode();
        parent.setBounds(0, 0, 50, 100);

        final PNode child1 = new PNode();
        child1.setBounds(50, 0, 50, 50);
        parent.addChild(child1);

        final PNode child2 = new PNode();
        child2.setBounds(50, 50, 50, 50);
        parent.addChild(child2);

        return parent;
    }

    public void testGetUnionOfChildrenBoundsAcceptsNull() {
        final PNode node = buildComplexSquareNode();

        final PBounds union = node.getUnionOfChildrenBounds(null);

        assertNotNull(union);
        assertEquals(new PBounds(50, 0, 50, 100), union);
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

    private PCanvas buildCanvasContainingNode(final PNode node) {
        final PCanvas canvas = new PCanvas();
        canvas.setSize(100, 100);
        canvas.getLayer().addChild(node);
        return canvas;
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

    public void testSetTransparency1MeansInvisible() {
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
}
