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
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * This example shows how to create and install a custom event listener that
 * draws rectangles.
 */
public class EventHandlerExample extends PFrame {

    public EventHandlerExample() {
        this(null);
    }

    public EventHandlerExample(PCanvas aCanvas) {
        super("EventHandlerExample", false, aCanvas);
    }

    public void initialize() {
        super.initialize();

        // Create a new event handler the creates new rectangles on
        // mouse pressed, dragged, release.
        PBasicInputEventHandler rectEventHandler = createRectangleEventHandler();

        // Make the event handler only work with BUTTON1 events, so that it does
        // not conflict with the zoom event handler that is installed by
        // default.
        rectEventHandler.setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));

        // Remove the pan event handler that is installed by default so that it
        // does not conflict with our new rectangle creation event handler.
        getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());

        // Register our new event handler.
        getCanvas().addInputEventListener(rectEventHandler);
    }

    public PBasicInputEventHandler createRectangleEventHandler() {

        // Create a new subclass of PBasicEventHandler that creates new PPath
        // nodes on mouse pressed, dragged, and released sequences. Not that
        // subclassing PDragSequenceEventHandler would make this class easier to
        // implement, but here you can see how to do it from scratch.
        return new PBasicInputEventHandler() {

            // The rectangle that is currently getting created.
            protected PPath rectangle;

            // The mouse press location for the current pressed, drag, release
            // sequence.
            protected Point2D pressPoint;

            // The current drag location.
            protected Point2D dragPoint;

            public void mousePressed(PInputEvent e) {
                super.mousePressed(e);

                PLayer layer = getCanvas().getLayer();

                // Initialize the locations.
                pressPoint = e.getPosition();
                dragPoint = pressPoint;

                // create a new rectangle and add it to the canvas layer so that
                // we can see it.
                rectangle = new PPath();
                rectangle.setStroke(new BasicStroke((float) (1 / e.getCamera().getViewScale())));
                layer.addChild(rectangle);

                // update the rectangle shape.
                updateRectangle();
            }

            public void mouseDragged(PInputEvent e) {
                super.mouseDragged(e);
                // update the drag point location.
                dragPoint = e.getPosition();

                // update the rectangle shape.
                updateRectangle();
            }

            public void mouseReleased(PInputEvent e) {
                super.mouseReleased(e);
                // update the rectangle shape.
                updateRectangle();
                rectangle = null;
            }

            public void updateRectangle() {
                // create a new bounds that contains both the press and current
                // drag point.
                PBounds b = new PBounds();
                b.add(pressPoint);
                b.add(dragPoint);

                // Set the rectangles bounds.
                rectangle.setPathTo(b);
            }
        };
    }

    public static void main(String[] args) {
        new EventHandlerExample();
    }
}
