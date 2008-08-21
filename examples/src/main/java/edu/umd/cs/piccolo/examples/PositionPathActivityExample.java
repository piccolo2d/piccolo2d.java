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

import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;

import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.activities.PPositionPathActivity;

/**
 * This example shows how create a simple acitivty to animate a node along a
 * general path.
 */
public class PositionPathActivityExample extends PFrame {

    public PositionPathActivityExample() {
        super();
    }

    public void initialize() {
        PLayer layer = getCanvas().getLayer();
        final PNode animatedNode = PPath.createRectangle(0, 0, 100, 80);
        layer.addChild(animatedNode);

        // create animation path
        GeneralPath path = new GeneralPath();
        path.moveTo(0, 0);
        path.lineTo(300, 300);
        path.lineTo(300, 0);
        path.append(new Arc2D.Float(0, 0, 300, 300, 90, -90, Arc2D.OPEN), true);
        path.closePath();

        // create node to display animation path
        PPath ppath = new PPath(path);
        layer.addChild(ppath);

        // create activity to run animation.
        PPositionPathActivity positionPathActivity = new PPositionPathActivity(5000, 0,
                new PPositionPathActivity.Target() {
                    public void setPosition(double x, double y) {
                        animatedNode.setOffset(x, y);
                    }
                });
        // positionPathActivity.setSlowInSlowOut(false);
        positionPathActivity.setPositions(path);
        positionPathActivity.setLoopCount(Integer.MAX_VALUE);

        // add the activity.
        animatedNode.addActivity(positionPathActivity);
    }

    public static void main(String[] args) {
        new PositionPathActivityExample();
    }
}