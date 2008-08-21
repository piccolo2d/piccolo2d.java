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
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.handles.PBoundsHandle;
import edu.umd.cs.piccolox.handles.PHandle;
import edu.umd.cs.piccolox.util.PBoundsLocator;

/**
 * This example shows another way to create sticky handles. These handles are
 * not added as children to the object that they manipulate. Instead they are
 * added to the camera the views that objects. This means that they will not be
 * affected by the cameras view transform, and so will stay the same size when
 * the view is zoomed. They will also be drawn on top of all other objects, even
 * if those objects overlap the object that they manipulate. For this setup we
 * need to add and updateHandles activity that makes sure to relocate the handle
 * after any change. Another way to do this would be to add change listeners to
 * the camera and the node that they manipulate and only update them then. But
 * this method is easier and should be plenty efficient for normal use.
 * 
 * @author jesse
 */
public class StickyHandleLayerExample extends PFrame {

    public StickyHandleLayerExample() {
        this(null);
    }

    public StickyHandleLayerExample(PCanvas aCanvas) {
        super("StickyHandleLayerExample", false, aCanvas);
    }

    public void initialize() {
        PCanvas c = getCanvas();

        PActivity updateHandles = new PActivity(-1, 0) {
            protected void activityStep(long elapsedTime) {
                super.activityStep(elapsedTime);

                PRoot root = getActivityScheduler().getRoot();

                if (root.getPaintInvalid() || root.getChildPaintInvalid()) {
                    Iterator i = getCanvas().getCamera().getChildrenIterator();
                    while (i.hasNext()) {
                        PNode each = (PNode) i.next();
                        if (each instanceof PHandle) {
                            PHandle handle = (PHandle) each;
                            handle.relocateHandle();
                        }
                    }
                }
            }
        };

        PPath rect = PPath.createRectangle(0, 0, 100, 100);
        rect.setPaint(Color.RED);
        c.getLayer().addChild(rect);

        c.getCamera().addChild(new PBoundsHandle(PBoundsLocator.createNorthEastLocator(rect)));
        c.getCamera().addChild(new PBoundsHandle(PBoundsLocator.createNorthWestLocator(rect)));
        c.getCamera().addChild(new PBoundsHandle(PBoundsLocator.createSouthEastLocator(rect)));
        c.getCamera().addChild(new PBoundsHandle(PBoundsLocator.createSouthWestLocator(rect)));

        c.getRoot().getActivityScheduler().addActivity(updateHandles, true);
    }

    public static void main(String[] args) {
        new StickyHandleLayerExample();
    }
}
