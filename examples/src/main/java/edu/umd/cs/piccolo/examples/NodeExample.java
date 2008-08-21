/*
 * Copyright (c) 2008, Piccolo2D project, http://piccolo2d.org
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
package edu.umd.cs.piccolo.examples;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.PFrame;

/**
 * This example shows how to create and manipulate nodes.
 */
public class NodeExample extends PFrame {

    boolean fIsPressed = false;

    public NodeExample() {
        this(null);
    }

    public NodeExample(PCanvas aCanvas) {
        super("NodeExample", false, aCanvas);
    }

    public void initialize() {
        nodeDemo();
        createNodeUsingExistingClasses();
        subclassExistingClasses();
        composeOtherNodes();
        createCustomNode();

        // Last of all lets remove the default pan event handler, and add a
        // drag event handler instead. This way you will be able to drag the
        // nodes around with the mouse.
        getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
        getCanvas().addInputEventListener(new PDragEventHandler());
    }

    // This method demonstrates the kinds of things that can be done with any
    // node.
    public void nodeDemo() {
        PLayer layer = getCanvas().getLayer();
        PNode aNode = PPath.createRectangle(0, 0, 100, 80);

        // A node needs to be a descendent of the root to be displayed on the
        // screen.
        layer.addChild(aNode);

        // The default color for a node is blue, but you can change that with
        // the setPaint method.
        aNode.setPaint(Color.red);

        // A node can have children nodes added to it.
        aNode.addChild(PPath.createRectangle(0, 0, 100, 80));

        // The base bounds of a node is easy to change. Note that changing the
        // base
        // bounds of a node will not change it's children.
        aNode.setBounds(-10, -10, 200, 110);

        // Each node has a transform that can be used to transform the node, and
        // all its children on the screen.
        aNode.translate(100, 100);
        aNode.scale(1.5);
        aNode.rotate(45);

        // The transparency of any node can be set, this transparency will be
        // applied to any of the nodes children as well.
        aNode.setTransparency(0.75f);

        // Its easy to copy nodes.
        PNode aCopy = (PNode) aNode.clone();

        // Make is so that the copies children are not pickable. For this
        // example
        // that means you will not be able to grab the child and remove it from
        // its parent.
        aNode.setChildrenPickable(false);

        // Change the look of the copy
        aNode.setPaint(Color.GREEN);
        aNode.setTransparency(1.0f);

        // Let's add the copy to the root, and translate it so that it does not
        // cover the original node.
        layer.addChild(aCopy);
        aCopy.setOffset(0, 0);
        aCopy.rotate(-45);
    }

    // So far we have just been using PNode, but of course PNode has many
    // subclasses that you can try out to.
    public void createNodeUsingExistingClasses() {
        PLayer layer = getCanvas().getLayer();
        layer.addChild(PPath.createEllipse(0, 0, 100, 100));
        layer.addChild(PPath.createRectangle(0, 100, 100, 100));
        layer.addChild(new PText("Hello World"));

        // Here we create an image node that displays a thumbnail
        // image of the root node. Note that you can easily get a thumbnail
        // of any node by using PNode.toImage().
        PImage image = new PImage(layer.toImage(300, 300, null));
        layer.addChild(image);
    }

    // Another way to create nodes is to customize other nodes that already
    // exist. Here we create an ellipse, except when you press the mouse on
    // this ellipse it turns into a square, when you release the mouse it
    // goes back to being an ellipse.
    public void subclassExistingClasses() {
        final PNode n = new PPath(new Ellipse2D.Float(0, 0, 100, 80)) {

            public void paint(PPaintContext aPaintContext) {
                if (fIsPressed) {
                    // if mouse is pressed draw self as a square.
                    Graphics2D g2 = aPaintContext.getGraphics();
                    g2.setPaint(getPaint());
                    g2.fill(getBoundsReference());
                }
                else {
                    // if mouse is not pressed draw self normally.
                    super.paint(aPaintContext);
                }
            }
        };

        n.addInputEventListener(new PBasicInputEventHandler() {
            public void mousePressed(PInputEvent aEvent) {
                super.mousePressed(aEvent);
                fIsPressed = true;
                n.invalidatePaint(); // this tells the framework that the node
                                     // needs to be redisplayed.
            }

            public void mouseReleased(PInputEvent aEvent) {
                super.mousePressed(aEvent);
                fIsPressed = false;
                n.invalidatePaint(); // this tells the framework that the node
                                     // needs to be redisplayed.
            }
        });

        n.setPaint(Color.ORANGE);
        getCanvas().getLayer().addChild(n);
    }

    // Here a new "face" node is created. But instead of drawing the face
    // directly
    // using Graphics2D we compose the face from other nodes.
    public void composeOtherNodes() {
        PNode myCompositeFace = PPath.createRectangle(0, 0, 100, 80);

        // create parts for the face.
        PNode eye1 = PPath.createEllipse(0, 0, 20, 20);
        eye1.setPaint(Color.YELLOW);
        PNode eye2 = (PNode) eye1.clone();
        PNode mouth = PPath.createRectangle(0, 0, 40, 20);
        mouth.setPaint(Color.BLACK);

        // add the face parts
        myCompositeFace.addChild(eye1);
        myCompositeFace.addChild(eye2);
        myCompositeFace.addChild(mouth);

        // don't want anyone grabbing out our eye's.
        myCompositeFace.setChildrenPickable(false);

        // position the face parts.
        eye2.translate(25, 0);
        mouth.translate(0, 30);

        // set the face bounds so that it neatly contains the face parts.
        PBounds b = myCompositeFace.getUnionOfChildrenBounds(null);
        myCompositeFace.setBounds(b.inset(-5, -5));

        // opps it to small, so scale it up.
        myCompositeFace.scale(1.5);

        getCanvas().getLayer().addChild(myCompositeFace);
    }

    // Here a completely new kind of node, a grid node" is created. We do
    // all the drawing ourselves here instead of passing the work off to
    // other parts of the framework.
    public void createCustomNode() {
        PNode n = new PNode() {
            public void paint(PPaintContext aPaintContext) {
                double bx = getX();
                double by = getY();
                double rightBorder = bx + getWidth();
                double bottomBorder = by + getHeight();

                Line2D line = new Line2D.Double();
                Graphics2D g2 = aPaintContext.getGraphics();

                g2.setStroke(new BasicStroke(0));
                g2.setPaint(getPaint());

                // draw vertical lines
                for (double x = bx; x < rightBorder; x += 5) {
                    line.setLine(x, by, x, bottomBorder);
                    g2.draw(line);
                }

                for (double y = by; y < bottomBorder; y += 5) {
                    line.setLine(bx, y, rightBorder, y);
                    g2.draw(line);
                }
            }
        };
        n.setBounds(0, 0, 100, 80);
        n.setPaint(Color.black);
        getCanvas().getLayer().addChild(n);
    }

    public static void main(String[] args) {
        new NodeExample();
    }
}
