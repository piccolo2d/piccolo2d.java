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

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;

/**
 * This example shows how a node can get the keyboard focus.
 */
public class KeyEventFocusExample extends PFrame {

    public KeyEventFocusExample() {
        this(null);
    }

    public KeyEventFocusExample(PCanvas aCanvas) {
        super("KeyEventFocusExample", false, aCanvas);
    }

    public void initialize() {
        // Create a green and red node and add them to canvas layer.
        PCanvas canvas = getCanvas();
        PNode nodeGreen = PPath.createRectangle(0, 0, 100, 100);
        PNode nodeRed = PPath.createRectangle(0, 0, 100, 100);
        nodeRed.translate(200, 0);
        nodeGreen.setPaint(Color.green);
        nodeRed.setPaint(Color.red);
        canvas.getLayer().addChild(nodeGreen);
        canvas.getLayer().addChild(nodeRed);

        // Add an event handler to the green node the prints
        // "green mousepressed"
        // when the mouse is pressed on the green node, and "green keypressed"
        // when
        // the key is pressed and the event listener has keyboard focus.
        nodeGreen.addInputEventListener(new PBasicInputEventHandler() {
            public void keyPressed(PInputEvent event) {
                System.out.println("green keypressed");
            }

            // Key board focus is managed by the PInputManager, accessible from
            // the root object, or from an incoming PInputEvent. In this case
            // when
            // the mouse is pressed in the green node, then the event handler
            // associated
            // with it will set the keyfocus to itself. Now it will receive key
            // events
            // until someone else gets the focus.
            public void mousePressed(PInputEvent event) {
                event.getInputManager().setKeyboardFocus(event.getPath());
                System.out.println("green mousepressed");
            }

            public void keyboardFocusGained(PInputEvent event) {
                System.out.println("green focus gained");
            }

            public void keyboardFocusLost(PInputEvent event) {
                System.out.println("green focus lost");
            }
        });

        // do the same thing with the red node.
        nodeRed.addInputEventListener(new PBasicInputEventHandler() {
            public void keyPressed(PInputEvent event) {
                System.out.println("red keypressed");
            }

            public void mousePressed(PInputEvent event) {
                event.getInputManager().setKeyboardFocus(event.getPath());
                System.out.println("red mousepressed");
            }

            public void keyboardFocusGained(PInputEvent event) {
                System.out.println("red focus gained");
            }

            public void keyboardFocusLost(PInputEvent event) {
                System.out.println("red focus lost");
            }
        });
    }

    public static void main(String[] args) {
        new KeyEventFocusExample();
    }
}
