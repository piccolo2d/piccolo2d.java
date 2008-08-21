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
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.activities.PActivityScheduler;
import edu.umd.cs.piccolo.activities.PColorActivity;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;

/**
 * This example shows how to set up interpolating activities that repeat. For
 * example it shows how to create a rectangle whos color pulses.
 * 
 * @author jesse
 */
public class PulseExample extends PFrame {

    public PulseExample() {
        this(null);
    }

    public PulseExample(PCanvas aCanvas) {
        super("PulseExample", false, aCanvas);
    }

    public void initialize() {
        PRoot root = getCanvas().getRoot();
        PLayer layer = getCanvas().getLayer();
        PActivityScheduler scheduler = root.getActivityScheduler();

        final PNode singlePulse = PPath.createRectangle(0, 0, 100, 80);
        final PPath repeatePulse = PPath.createRectangle(100, 80, 100, 80);
        final PNode repeateReversePulse = PPath.createRectangle(200, 160, 100, 80);

        layer.addChild(singlePulse);
        layer.addChild(repeatePulse);
        layer.addChild(repeateReversePulse);

        // animate from source to destination color in one second,
        PColorActivity singlePulseActivity = new PColorActivity(1000, 0, 1,
                PInterpolatingActivity.SOURCE_TO_DESTINATION, new PColorActivity.Target() {
                    public Color getColor() {
                        return (Color) singlePulse.getPaint();
                    }

                    public void setColor(Color color) {
                        singlePulse.setPaint(color);
                    }
                }, Color.ORANGE);

        // animate from source to destination color in one second, loop 5 times
        PColorActivity repeatPulseActivity = new PColorActivity(1000, 0, 5,
                PInterpolatingActivity.SOURCE_TO_DESTINATION, new PColorActivity.Target() {
                    public Color getColor() {
                        return (Color) repeatePulse.getPaint();
                    }

                    public void setColor(Color color) {
                        repeatePulse.setPaint(color);
                    }
                }, Color.BLUE);

        // animate from source to destination to source color in one second,
        // loop 10 times
        PColorActivity repeatReversePulseActivity = new PColorActivity(500, 0, 10,
                PInterpolatingActivity.SOURCE_TO_DESTINATION_TO_SOURCE, new PColorActivity.Target() {
                    public Color getColor() {
                        return (Color) repeateReversePulse.getPaint();
                    }

                    public void setColor(Color color) {
                        repeateReversePulse.setPaint(color);
                    }
                }, Color.GREEN);

        scheduler.addActivity(singlePulseActivity);
        scheduler.addActivity(repeatPulseActivity);
        scheduler.addActivity(repeatReversePulseActivity);
    }

    public static void main(String[] args) {
        new PulseExample();
    }
}
