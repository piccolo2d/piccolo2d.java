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
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.event.PNotification;
import edu.umd.cs.piccolox.event.PNotificationCenter;
import edu.umd.cs.piccolox.event.PSelectionEventHandler;

/**
 * This example shows how the selection event handler works. It creates a bunch
 * of objects that can be selected.
 */
public class SelectionExample extends PFrame {

    public SelectionExample() {
        this(null);
    }

    public SelectionExample(PCanvas aCanvas) {
        super("SelectionExample", false, aCanvas);
    }

    public void initialize() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                PNode rect = PPath.createRectangle(i * 60, j * 60, 50, 50);
                rect.setPaint(Color.blue);
                getCanvas().getLayer().addChild(rect);
            }
        }

        // Turn off default navigation event handlers
        getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
        getCanvas().removeInputEventListener(getCanvas().getZoomEventHandler());

        // Create a selection event handler
        PSelectionEventHandler selectionEventHandler = new PSelectionEventHandler(getCanvas().getLayer(), getCanvas()
                .getLayer());
        getCanvas().addInputEventListener(selectionEventHandler);
        getCanvas().getRoot().getDefaultInputManager().setKeyboardFocus(selectionEventHandler);

        PNotificationCenter.defaultCenter().addListener(this, "selectionChanged",
                PSelectionEventHandler.SELECTION_CHANGED_NOTIFICATION, selectionEventHandler);
    }

    public void selectionChanged(PNotification notfication) {
        System.out.println("selection changed");
    }

    public static void main(String[] args) {
        new SelectionExample();
    }
}
