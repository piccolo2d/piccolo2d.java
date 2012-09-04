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
package org.piccolo2d.tutorial;

import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.geom.Ellipse2D;

import org.piccolo2d.PLayer;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PDragEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.extras.PFrame;
import org.piccolo2d.nodes.PImage;
import org.piccolo2d.nodes.PPath;
import org.piccolo2d.nodes.PText;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PPaintContext;


public class InterfaceFrame extends PFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public void initialize() {
        // Remove the Default pan event handler and add a drag event handler
        // so that we can drag the nodes around individually.
        getCanvas().setPanEventHandler(null);
        getCanvas().addInputEventListener(new PDragEventHandler());

        // Add Some Default Nodes

        // Create a node.
        final PNode aNode = new PNode();

        // A node will not be visible until its bounds and paint are set.
        aNode.setBounds(0, 0, 100, 80);
        aNode.setPaint(Color.RED);

        // A node needs to be a descendant of the root to be displayed.
        final PLayer layer = getCanvas().getLayer();
        layer.addChild(aNode);

        // A node can have child nodes added to it.
        final PNode anotherNode = new PNode();
        anotherNode.setBounds(0, 0, 100, 80);
        anotherNode.setPaint(Color.YELLOW);
        aNode.addChild(anotherNode);

        // The base bounds of a node are easy to change. Changing the bounds
        // of a node will not affect its children.
        aNode.setBounds(-10, -10, 200, 110);

        // Each node has a transform that can be used to modify the position,
        // scale or rotation of a node. Changing a node's transform will
        // transform all of its children as well.
        aNode.translate(100, 100);
        aNode.scale(1.5f);
        aNode.rotate(45);

        // Add a couple of PPath nodes and a PText node.
        layer.addChild(PPath.createEllipse(0, 0, 100, 100));
        layer.addChild(PPath.createRectangle(0, 100, 100, 100));
        layer.addChild(new PText("Hello World"));

        // Here we create a PImage node that displays a thumbnail image
        // of the root node. Then we add the new PImage to the main layer.
        final PImage image = new PImage(layer.toImage(300, 300, null));
        layer.addChild(image);

        // Create a New Node using Composition

        final PNode myCompositeFace = PPath.createRectangle(0, 0, 100, 80);

        // Create parts for the face.
        final PNode eye1 = PPath.createEllipse(0, 0, 20, 20);
        eye1.setPaint(Color.YELLOW);
        final PNode eye2 = (PNode) eye1.clone();
        final PNode mouth = PPath.createRectangle(0, 0, 40, 20);
        mouth.setPaint(Color.BLACK);

        // Add the face parts.
        myCompositeFace.addChild(eye1);
        myCompositeFace.addChild(eye2);
        myCompositeFace.addChild(mouth);

        // Don't want anyone grabbing out our eyes.
        myCompositeFace.setChildrenPickable(false);

        // Position the face parts.
        eye2.translate(25, 0);
        mouth.translate(0, 30);

        // Set the face bounds so that it neatly contains the face parts.
        final PBounds b = myCompositeFace.getUnionOfChildrenBounds(null);
        b.inset(-5, -5);
        myCompositeFace.setBounds(b);

        // Opps it's too small, so scale it up.
        myCompositeFace.scale(1.5);

        layer.addChild(myCompositeFace);

        // Create a New Node using Inheritance.
        final ToggleShape ts = new ToggleShape();
        ts.setPaint(Color.ORANGE);
        layer.addChild(ts);
    }

    class ToggleShape extends PPath.Float {

        private static final long serialVersionUID = 1L;
        private boolean isPressed = false;

        public ToggleShape() {
            reset();
            append(new Ellipse2D.Float(0.0f, 0.0f, 100.0f, 80.0f), false);
            closePath();

            addInputEventListener(new PBasicInputEventHandler() {
                public void mousePressed(final PInputEvent event) {
                    super.mousePressed(event);
                    isPressed = true;
                    repaint();
                }

                public void mouseReleased(final PInputEvent event) {
                    super.mouseReleased(event);
                    isPressed = false;
                    repaint();
                }
            });
        }

        protected void paint(final PPaintContext paintContext) {
            if (isPressed) {
                final Graphics2D g2 = paintContext.getGraphics();
                g2.setPaint(getPaint());
                g2.fill(getBoundsReference());
            }
            else {
                super.paint(paintContext);
            }
        }
    }

    public static void main(final String[] args) {
        new InterfaceFrame();
    }
}
