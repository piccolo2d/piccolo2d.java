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
package edu.umd.cs.piccolo.tutorial;

import java.awt.Color;

import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.activities.*;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolox.*;

public class SpecialEffects extends PFrame {
    public void initialize() {
        // Create the Target for our Activities.

        // Create a new node that we will apply different activities to, and
        // place that node at location 200, 200.
        final PNode aNode = PPath.createRectangle(0, 0, 100, 80);
        PLayer layer = getCanvas().getLayer();
        layer.addChild(aNode);
        aNode.setOffset(200, 200);

        // Extend PActivity.

        // Store the current time in milliseconds for use below.
        long currentTime = System.currentTimeMillis();

        // Create a new custom "flash" activity. This activity will start
        // running in five seconds, and while it runs it will flash aNode's
        // paint between red and green every half second.
        PActivity flash = new PActivity(-1, 500, currentTime + 5000) {
            boolean fRed = true;

            protected void activityStep(long elapsedTime) {
                super.activityStep(elapsedTime);

                // Toggle the target node's brush color between red and green
                // each time the activity steps.
                if (fRed) {
                    aNode.setPaint(Color.red);
                }
                else {
                    aNode.setPaint(Color.green);
                }

                fRed = !fRed;
            }
        };

        // Schedule the activity.
        getCanvas().getRoot().addActivity(flash);

        // Create three activities that animate the node's position. Since our
        // node already descends from the root node the animate methods will
        // automatically schedule these activities for us.
        PActivity a1 = aNode.animateToPositionScaleRotation(0, 0, 0.5, 0, 5000);
        PActivity a2 = aNode.animateToPositionScaleRotation(100, 0, 1.5, Math.toRadians(110), 5000);
        PActivity a3 = aNode.animateToPositionScaleRotation(200, 100, 1, 0, 5000);

        // The animate activities will start immediately (in the next call to
        // PRoot.processInputs) by default. Here we set their start times (in
        // PRoot global time) so that they start when the previous one has
        // finished.
        a1.setStartTime(currentTime);
        a2.startAfter(a1);
        a3.startAfter(a2);

        a1.setDelegate(new PActivity.PActivityDelegate() {
            public void activityStarted(PActivity activity) {
                System.out.println("a1 started");
            }

            public void activityStepped(PActivity activity) {
            }

            public void activityFinished(PActivity activity) {
                System.out.println("a1 finished");
            }
        });
    }

    public static void main(String[] args) {
        new SpecialEffects();
    }
}
