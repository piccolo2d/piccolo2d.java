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
import org.piccolo2d.PLayer;
import org.piccolo2d.PNode;
import org.piccolo2d.activities.PActivity;
import org.piccolo2d.extras.PFrame;
import org.piccolo2d.nodes.PPath;


/**
 * This example shows how to use setTriggerTime to synchronize the start and end
 * of two activities.
 */
public class WaitForActivitiesExample extends PFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public WaitForActivitiesExample() {
        this(null);
    }

    public WaitForActivitiesExample(final PCanvas aCanvas) {
        super("WaitForActivitiesExample", false, aCanvas);
    }

    public void initialize() {
        final PLayer layer = getCanvas().getLayer();

        final PNode a = PPath.createRectangle(0, 0, 100, 80);
        final PNode b = PPath.createRectangle(0, 0, 100, 80);

        layer.addChild(a);
        layer.addChild(b);

        final PActivity a1 = a.animateToPositionScaleRotation(200, 200, 1, 0, 5000);
        final PActivity a2 = b.animateToPositionScaleRotation(200, 200, 1, 0, 5000);

        a2.startAfter(a1);
    }

    public static void main(final String[] args) {
        new WaitForActivitiesExample();
    }
}
