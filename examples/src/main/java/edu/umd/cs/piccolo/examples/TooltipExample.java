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

import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.PFrame;

/**
 * Simple example of one way to add tooltips
 * 
 * @author jesse
 */
public class TooltipExample extends PFrame {

    public TooltipExample() {
        this(null);
    }

    public TooltipExample(PCanvas aCanvas) {
        super("TooltipExample", false, aCanvas);
    }

    public void initialize() {
        PNode n1 = PPath.createEllipse(0, 0, 100, 100);
        PNode n2 = PPath.createRectangle(300, 200, 100, 100);

        n1.addAttribute("tooltip", "node 1");
        n2.addAttribute("tooltip", "node 2");
        getCanvas().getLayer().addChild(n1);
        getCanvas().getLayer().addChild(n2);

        final PCamera camera = getCanvas().getCamera();
        final PText tooltipNode = new PText();

        tooltipNode.setPickable(false);
        camera.addChild(tooltipNode);

        camera.addInputEventListener(new PBasicInputEventHandler() {
            public void mouseMoved(PInputEvent event) {
                updateToolTip(event);
            }

            public void mouseDragged(PInputEvent event) {
                updateToolTip(event);
            }

            public void updateToolTip(PInputEvent event) {
                PNode n = event.getInputManager().getMouseOver().getPickedNode();
                String tooltipString = (String) n.getAttribute("tooltip");
                Point2D p = event.getCanvasPosition();

                event.getPath().canvasToLocal(p, camera);

                tooltipNode.setText(tooltipString);
                tooltipNode.setOffset(p.getX() + 8, p.getY() - 8);
            }
        });
    }

    public static void main(String[] argv) {
        new TooltipExample();
    }
}