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
import java.util.Random;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;

/**
 * Click on a node and the camera will pan the minimum distance to bring that
 * node fully into the cameras view.
 */
public class PanToExample extends PFrame {

    public PanToExample() {
        this(null);
    }

    public PanToExample(PCanvas aCanvas) {
        super("PanToExample", false, aCanvas);
    }

    public void initialize() {

        PPath eacha = PPath.createRectangle(50, 50, 300, 300);
        eacha.setPaint(Color.red);
        getCanvas().getLayer().addChild(eacha);

        eacha = PPath.createRectangle(-50, -50, 100, 100);
        eacha.setPaint(Color.green);
        getCanvas().getLayer().addChild(eacha);

        eacha = PPath.createRectangle(350, 350, 100, 100);
        eacha.setPaint(Color.green);
        getCanvas().getLayer().addChild(eacha);

        getCanvas().getCamera().addInputEventListener(new PBasicInputEventHandler() {
            public void mousePressed(PInputEvent event) {
                if (!(event.getPickedNode() instanceof PCamera)) {
                    event.setHandled(true);
                    getCanvas().getCamera().animateViewToPanToBounds(event.getPickedNode().getGlobalFullBounds(), 500);
                }
            }
        });

        PLayer layer = getCanvas().getLayer();

        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            PPath each = PPath.createRectangle(0, 0, 100, 80);
            each.scale(random.nextFloat() * 2);
            each.offset(random.nextFloat() * 10000, random.nextFloat() * 10000);
            each.setPaint(new Color(random.nextFloat(), random.nextFloat(), random.nextFloat()));
            each.setStroke(new BasicStroke(1 + (10 * random.nextFloat())));
            each.setStrokePaint(new Color(random.nextFloat(), random.nextFloat(), random.nextFloat()));
            layer.addChild(each);
        }

        getCanvas().removeInputEventListener(getCanvas().getZoomEventHandler());
    }

    public static void main(String[] args) {
        new PanToExample();
    }
}
