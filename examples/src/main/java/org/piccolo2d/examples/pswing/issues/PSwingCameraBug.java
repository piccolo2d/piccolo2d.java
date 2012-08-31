/*
 * Copyright (c) 2008-2012, Piccolo2D project, http://piccolo2d.org
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
package org.piccolo2d.examples.pswing.issues;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTextField;

import org.piccolo2d.extras.pswing.PSwing;
import org.piccolo2d.extras.pswing.PSwingCanvas;

/**
 * Example that demonstrates the PSwing camera bug described in Issue 148.
 * <p>
 * PSwing nodes doen't handle interactions when added to the camera<br/>
 * <a href="http://code.google.com/p/piccolo2d/issues/detail?id=148">http://code.google.com/p/piccolo2d/issues/detail?id=148</a>
 * </p>
 */
public class PSwingCameraBug {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setPreferredSize(new Dimension(600, 600));

        frame.getContentPane().add(buildPSwingCanvas());        
        frame.pack();
        frame.setVisible(true);
    }

    private static PSwingCanvas buildPSwingCanvas() {
        PSwingCanvas canvas = new PSwingCanvas();

        addCameraPSwing(canvas);
        addLayerPSwing(canvas);

        return canvas;
    }

    private static void addCameraPSwing(PSwingCanvas canvas) {
        PSwing swingNode = buildPSwingNode();
        canvas.getCamera().addChild(swingNode);
    }

    private static void addLayerPSwing(PSwingCanvas canvas) {
        PSwing swingNode = buildPSwingNode();
        swingNode.setOffset(200, 0);
        canvas.getLayer().addChild(swingNode);
    }

    private static PSwing buildPSwingNode() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(100, 20));
        return new PSwing(textField);        
    }
}