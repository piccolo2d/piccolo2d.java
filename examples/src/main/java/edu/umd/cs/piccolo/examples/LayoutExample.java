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
import java.util.Iterator;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.handles.PBoundsHandle;

/**
 * This example shows how to create a node that will automatically layout its
 * children.
 */
public class LayoutExample extends PFrame {

    public LayoutExample() {
        this(null);
    }

    public LayoutExample(PCanvas aCanvas) {
        super("LayoutExample", false, aCanvas);
    }

    public void initialize() {
        // Create a new node and override its validateLayoutAfterChildren method
        // so that it lays out its children in a row from left to right.

        final PNode layoutNode = new PNode() {
            public void layoutChildren() {
                double xOffset = 0;
                double yOffset = 0;

                Iterator i = getChildrenIterator();
                while (i.hasNext()) {
                    PNode each = (PNode) i.next();
                    each.setOffset(xOffset - each.getX(), yOffset);
                    xOffset += each.getWidth();
                }
            }
        };

        layoutNode.setPaint(Color.red);

        // add some children to the layout node.
        for (int i = 0; i < 1000; i++) {
            // create child to add to the layout node.
            PNode each = PPath.createRectangle(0, 0, 100, 80);

            // add the child to the layout node.
            layoutNode.addChild(each);
        }

        PBoundsHandle.addBoundsHandlesTo(layoutNode.getChild(0));

        // add layoutNode to the root so it will be displayed.
        getCanvas().getLayer().addChild(layoutNode);
    }

    public static void main(String[] args) {
        new LayoutExample();
    }
}
