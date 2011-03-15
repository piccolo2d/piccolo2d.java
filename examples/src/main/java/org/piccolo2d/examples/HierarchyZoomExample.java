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
package org.piccolo2d.examples;

import org.piccolo2d.PCanvas;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.extras.PFrame;
import org.piccolo2d.nodes.PPath;


/**
 * This example shows how to create and zoom over a node hierarchy.
 */
public class HierarchyZoomExample extends PFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public HierarchyZoomExample() {
        this(null);
    }

    public HierarchyZoomExample(final PCanvas aCanvas) {
        super("HierarchyZoomExample", false, aCanvas);
    }

    public void initialize() {
        final PNode root = createHierarchy(10);
        getCanvas().getLayer().addChild(root);
        getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
        getCanvas().addInputEventListener(new PBasicInputEventHandler() {
            public void mousePressed(final PInputEvent event) {
                getCanvas().getCamera().animateViewToCenterBounds(event.getPickedNode().getGlobalBounds(), true, 500);
            }
        });
    }

    public PNode createHierarchy(final int level) {
        final PPath result = PPath.createRectangle(0, 0, 100, 100);

        if (level > 0) {
            final PNode child = createHierarchy(level - 1);
            child.scale(0.5);
            result.addChild(child);
            child.offset(25, 25);
        }

        return result;
    }

    public static void main(final String[] args) {
        new HierarchyZoomExample();
    }
}