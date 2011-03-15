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
package org.piccolo2d.examples.pswing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.piccolo2d.extras.pswing.PSwing;
import org.piccolo2d.extras.pswing.PSwingCanvas;


public class MultiplePSwingCanvasesExample extends JFrame {
    
    
    public static void main(final String[] args) {
        JFrame frame = new MultiplePSwingCanvasesExample();
        
        Container container = frame.getContentPane();
        container.setLayout(new BorderLayout());
        
        PSwingCanvas canvas1 = buildPSwingCanvas("Canvas 1", Color.RED);
        canvas1.setPreferredSize(new Dimension(350, 350));
        container.add(canvas1, BorderLayout.WEST);
        
        PSwingCanvas canvas2 = buildPSwingCanvas("Canvas 2", Color.BLUE);
        container.add(canvas2, BorderLayout.EAST);              
        canvas2.setPreferredSize(new Dimension(350, 350));
        
        frame.pack();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setVisible(true);
        
    }

    private static PSwingCanvas buildPSwingCanvas(String canvasName, Color rectangleColor) {
        PSwingCanvas canvas = new PSwingCanvas();
        canvas.setPreferredSize(new Dimension(350, 350));
        canvas.getLayer().addChild(new PSwing(new JLabel(canvasName)));
        
        PSwing rectNode = buildRectangleNode(rectangleColor);
        rectNode.setOffset(100, 100);
        canvas.getLayer().addChild(rectNode);
        
        PSwing buttonNode = buildTestButton();
        buttonNode.setOffset(50, 50);
        canvas.getLayer().addChild(buttonNode);
                
        return canvas;
    }

    private static PSwing buildRectangleNode(Color rectangleColor) {
        JPanel rectPanel = new JPanel();
        rectPanel.setPreferredSize(new Dimension((int)(Math.random()*50+50), (int)(Math.random()*50+50)));
        rectPanel.setBackground(rectangleColor);
        PSwing rect = new PSwing(rectPanel);
        return rect;
    }

    private static PSwing buildTestButton() {
        final JButton button = new JButton("Click Me");
        
        button.addActionListener(new AbstractAction() {

            public void actionPerformed(ActionEvent arg0) {
               button.setText("Thanks"); 
            }
            
        });
        
        return new PSwing(button);
    }
}
