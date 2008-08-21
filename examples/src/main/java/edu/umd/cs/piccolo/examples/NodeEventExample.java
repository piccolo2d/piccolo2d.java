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

import java.awt.Color;
import java.awt.geom.Dimension2D;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;

/**
 * This example shows how to make a node handle events.
 */
public class NodeEventExample extends PFrame {

    public NodeEventExample() {
        this(null);
    }

    public NodeEventExample(PCanvas aCanvas) {
        super("NodeEventExample", false, aCanvas);
    }

    public void initialize() {
        PLayer layer = getCanvas().getLayer();

        // create a new node and override some of the event handling
        // methods so that the node changes to orange when the mouse (Button 1)
        // is pressed on the node, and changes back to green when the mouse
        // is released. Also when the mouse is dragged the node updates its
        // position so that the node is "dragged". Note that this only serves
        // as a simple example, most of the time dragging nodes is best done
        // with the PDragEventHandler, but this shows another way to do it.
        //
        // Note that each of these methods marks the event as handled. This is
        // so that when the node is being dragged the zoom and pan event handles
        // (that are installed by default) do not also operate, but they will
        // still respond to events that are not handled by the node. (try to
        // uncomment the aEvent.setHandled() calls and see what happens.
        final PNode aNode = new PNode();
        aNode.addInputEventListener(new PBasicInputEventHandler() {
            public void mousePressed(PInputEvent aEvent) {
                aNode.setPaint(Color.orange);
                printEventCoords(aEvent);
                aEvent.setHandled(true);
            }

            public void mouseDragged(PInputEvent aEvent) {
                Dimension2D delta = aEvent.getDeltaRelativeTo(aNode);
                aNode.translate(delta.getWidth(), delta.getHeight());
                printEventCoords(aEvent);
                aEvent.setHandled(true);
            }

            public void mouseReleased(PInputEvent aEvent) {
                aNode.setPaint(Color.green);
                printEventCoords(aEvent);
                aEvent.setHandled(true);
            }

            // Note this slows things down a lot, comment it out to see how the
            // normal speed of things is.
            // 
            // For fun the coords of each event that the node handles are
            // printed out. This can help to understand how coordinate systems
            // work. Notice that when the example first starts all the values
            // for (canvas, global, and local) are equal. But once you drag the
            // node then the local coordinates become different then the screen
            // and global coordinates. When you pan or zoom then the screen
            // coordinates become different from the global coordinates.
            public void printEventCoords(PInputEvent aEvent) {
                System.out.println("Canvas Location: " + aEvent.getCanvasPosition());
                // System.out.println("Global Location: " +
                // aEvent.getGlobalLocation());
                System.out.println("Local Location: " + aEvent.getPositionRelativeTo(aNode));
                System.out.println("Canvas Delta: " + aEvent.getCanvasDelta());
                // System.out.println("Global Delta: " +
                // aEvent.getGlobalDelta());
                System.out.println("Local Delta: " + aEvent.getDeltaRelativeTo(aNode));
            }
        });
        aNode.setBounds(0, 0, 200, 200);
        aNode.setPaint(Color.green);

        // By default the filter accepts all events, but here we constrain the
        // kinds of events that aNode receives to button 1 events. Comment this
        // line out and then you will be able to drag the node with any mouse
        // button.
        // aNode.setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));

        // add another node to the canvas that does not handle events as a
        // reference point, so that we can make sure that our green node is
        // getting dragged.
        layer.addChild(PPath.createRectangle(0, 0, 100, 80));
        layer.addChild(aNode);
    }

    public static void main(String[] args) {
        new NodeEventExample();
    }
}
