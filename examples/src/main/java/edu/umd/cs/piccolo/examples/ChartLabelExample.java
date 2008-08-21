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
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.PFrame;

/**
 * This example shows how to create a vertical and a horizontal bar which can
 * move with your graph and always stays on view.
 * 
 * @author Tao
 */
public class ChartLabelExample extends PFrame {
    final int nodeHeight = 15;
    final int nodeWidth = 30;

    // Row Bar
    PLayer rowBarLayer;

    // Colume Bar
    PLayer colBarLayer;

    public ChartLabelExample() {
        this(null);
    }

    public ChartLabelExample(PCanvas aCanvas) {
        super("ChartLabelExample", false, aCanvas);
    }

    public void initialize() {
        // create bar layers
        rowBarLayer = new PLayer();
        colBarLayer = new PLayer();

        // create bar nodes
        for (int i = 0; i < 10; i++) {
            // create row bar with node row1, row2,...row10
            PText p = new PText("Row " + i);
            p.setX(0);
            p.setY(nodeHeight * i + nodeHeight);
            p.setPaint(Color.white);
            colBarLayer.addChild(p);

            // create col bar with node col1, col2,...col10
            p = new PText("Col " + i);
            p.setX(nodeWidth * i + nodeWidth);
            p.setY(0);
            p.setPaint(Color.white);
            rowBarLayer.addChild(p);
        }

        // add bar layers to camera
        getCanvas().getCamera().addChild(rowBarLayer);
        getCanvas().getCamera().addChild(colBarLayer);

        // create matrix nodes
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                PPath path = PPath.createRectangle(nodeWidth * j + nodeWidth, nodeHeight * i + nodeHeight,
                        nodeWidth - 1, nodeHeight - 1);
                getCanvas().getLayer().addChild(path);
            }
        }

        // catch drag event and move bars corresponding
        getCanvas().addInputEventListener(new PDragSequenceEventHandler() {
            Point2D oldP, newP;

            public void mousePressed(PInputEvent aEvent) {
                oldP = getCanvas().getCamera().getViewBounds().getCenter2D();
            }

            public void mouseReleased(PInputEvent aEvent) {
                newP = getCanvas().getCamera().getViewBounds().getCenter2D();
                colBarLayer.translate(0, (oldP.getY() - newP.getY()) / getCanvas().getLayer().getScale());
                rowBarLayer.translate((oldP.getX() - newP.getX()) / getCanvas().getLayer().getScale(), 0);
            }
        });
    }

    public static void main(String[] args) {
        new ChartLabelExample();
    }
}