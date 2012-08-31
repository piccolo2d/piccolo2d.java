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
package org.piccolo2d.examples.issues;

import org.piccolo2d.PLayer;
import org.piccolo2d.PNode;
import org.piccolo2d.activities.PActivity;
import org.piccolo2d.extras.PFrame;
import org.piccolo2d.nodes.PPath;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;

/**
 * Example that demonstrates the memory leak in Issue 185.
 * <p>
 * Memory leak - PActivityScheduler keeps processed activities in reference<br/>
 * <a href="http://code.google.com/p/piccolo2d/issues/detail?id=185">http://code.google.com/p/piccolo2d/issues/detail?id=185</a>
 * </p>
 */
public class ActivityMemoryLeakBugExample extends PFrame {

    public static void main(String[] args) {
        new ActivityMemoryLeakBugExample();
    }

    /** {@inheritDoc} */
    public void initialize() {
        final PLayer layer = getCanvas().getLayer();
        // Create the node that we expect to get garbage collected.
        PNode node = PPath.createEllipse(20, 20, 20, 20);
        layer.addChild(node);
        // Create a WeakReference to the node so we can detect if it is gc'd.
        final WeakReference ref = new WeakReference(layer.getChild(0));
        // Create and execute an activity.
        ((PNode) ref.get()).animateToPositionScaleRotation(0, 0, 5.0, 0, 1000);
        // Create a Timer that will start after the activity and repeat.
        new Timer(2000, new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                // Remove our reference to the node.
                layer.removeAllChildren();
                // Force garbage collection.
                System.gc();
                // This should print null if the node was successfully gc'd. (IT never does.)
                System.out.println(ref.get());
                // This prints 0 as expected.
                System.out.println(layer.getRoot().getActivityScheduler().getActivitiesReference().size());
            }
        }).start();
        // This will cause any previous activity references to clear.
        forceCleanupOfPriorActivities(layer);
    }

    private void forceCleanupOfPriorActivities(final PLayer layer) {
        new Thread() {
            /** {@inheritDoc} */
            public void run() {
                // Wait 6 seconds before doing the cleanup so the bug can be witnessed.
                try {
                    Thread.sleep(6000);
                }
                catch (InterruptedException e) {
                    // empty
                }

                SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            PActivity a = new PActivity(-1) {
                                    protected void activityStep(final long elapsedTime) {
                                        System.out.println("cleanup activity");
                                        terminate();
                                    }
                                };
                            layer.getRoot().addActivity(a);
                        }
                    });
            }
        }.start();
    }
}
