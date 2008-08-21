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
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;

/**
 * This example shows how create and schedule activities.
 */
public class ActivityExample extends PFrame {

    public ActivityExample() {
        this(null);
    }

    public ActivityExample(PCanvas aCanvas) {
        super("ActivityExample", false, aCanvas);
    }

    public void initialize() {
        long currentTime = System.currentTimeMillis();

        // Create a new node that we will apply different activities to, and
        // place that node at location 200, 200.
        final PNode aNode = PPath.createRectangle(0, 0, 100, 80);
        PLayer layer = getCanvas().getLayer();
        layer.addChild(aNode);
        aNode.setOffset(200, 200);

        // Create a new custom "flash" activity. This activity will start
        // running in five seconds, and while it runs it will flash aNode's
        // paint between red and green every half second.
        PActivity flash = new PActivity(-1, 500, currentTime + 5000) {
            boolean fRed = true;

            protected void activityStep(long elapsedTime) {
                super.activityStep(elapsedTime);

                if (fRed) {
                    aNode.setPaint(Color.red);
                }
                else {
                    aNode.setPaint(Color.green);
                }

                fRed = !fRed;
            }
        };

        // An activity will not run unless it is scheduled with the root. Once
        // it has been scheduled it will be given a chance to run during the
        // next PRoot.processInputs() call.
        getCanvas().getRoot().addActivity(flash);

        // Use the PNode animate methods to create three activities that animate
        // the node's position. Since our node already descends from the root
        // node the animate methods will automatically schedule these activities
        // for us.
        PActivity a1 = aNode.animateToPositionScaleRotation(0, 0, 0.5, 0, 5000);
        PActivity a2 = aNode.animateToPositionScaleRotation(100, 0, 1.5, Math.toRadians(110), 5000);
        PActivity a3 = aNode.animateToPositionScaleRotation(200, 100, 1, 0, 5000);
        PActivity a4 = aNode.animateToTransparency(0.25f, 3000);

        // the animate activities will start immediately (in the next call to
        // PRoot.processInputs) by default. Here we set their start times (in
        // PRoot global time) so that they start when the previous one has
        // finished.
        a1.setStartTime(currentTime);

        a2.startAfter(a1);
        a3.startAfter(a2);
        a4.startAfter(a3);

        // or the previous three lines could be replaced with these lines for
        // the same effect.
        // a2.setStartTime(currentTime + 5000);
        // a3.setStartTime(currentTime + 10000);
        // a4.setStartTime(currentTime + 15000);
    }

    public static void main(String[] args) {
        new ActivityExample();
    }
}
