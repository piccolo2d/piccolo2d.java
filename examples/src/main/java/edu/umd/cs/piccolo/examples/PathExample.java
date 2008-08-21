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

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.handles.PStickyHandleManager;
import edu.umd.cs.piccolox.util.PFixedWidthStroke;

public class PathExample extends PFrame {

    public PathExample() {
        this(null);
    }

    public PathExample(PCanvas aCanvas) {
        super("PathExample", false, aCanvas);
    }

    public void initialize() {
        PPath n1 = PPath.createRectangle(0, 0, 100, 80);
        PPath n2 = PPath.createEllipse(100, 100, 200, 34);
        PPath n3 = new PPath();
        n3.moveTo(0, 0);
        n3.lineTo(20, 40);
        n3.lineTo(10, 200);
        n3.lineTo(155.444f, 33.232f);
        n3.closePath();
        n3.setPaint(Color.yellow);

        n1.setStroke(new BasicStroke(5));
        n1.setStrokePaint(Color.red);
        n2.setStroke(new PFixedWidthStroke());
        n3.setStroke(new PFixedWidthStroke());
        // n3.setStroke(null);

        getCanvas().getLayer().addChild(n1);
        getCanvas().getLayer().addChild(n2);
        getCanvas().getLayer().addChild(n3);

        // create a set of bounds handles for reshaping n3, and make them
        // sticky relative to the getCanvas().getCamera().
        new PStickyHandleManager(getCanvas().getCamera(), n3);

        getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
        getCanvas().addInputEventListener(new PDragEventHandler());
    }

    public static void main(String[] args) {
        new PathExample();
    }
}
