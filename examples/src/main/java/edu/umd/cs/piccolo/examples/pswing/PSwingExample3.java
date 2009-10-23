/*
 * Copyright (c) 2008-2009, Piccolo2D project, http://piccolo2d.org
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
package edu.umd.cs.piccolo.examples.pswing;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * User: Sam Reid Date: Jul 11, 2005 Time: 12:15:55 PM
 */
public class PSwingExample3 extends JFrame {   
    private static final long serialVersionUID = 1L;
    private ExampleList exampleList;

    public PSwingExample3() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        PSwingCanvas canvas;

        // Set up basic frame
        setBounds(50, 50, 750, 750);
        setResizable(true);
        setBackground(null);
        setVisible(true);
        canvas = new PSwingCanvas();
        canvas.setPanEventHandler(null);
        getContentPane().add(canvas);
        validate();       
                      
        exampleList = new ExampleList("Button Examples");
        
        addButtonExamples();
        
        canvas.getLayer().addChild(exampleList);
        
        canvas.getCamera().animateViewToCenterBounds(canvas.getLayer().getFullBounds(), true, 1200);
    }

    private void addButtonExamples() {
        addButtonAloneNoSizing();        
        addButtonAlone200x50();
        addButtonOnPanelNoSizing();
        addButtonOnPanel200x50();        
        addButtonAlone10x10();
    }

    private void addButtonAloneNoSizing() {
        JButton button = new JButton("Button");
        PSwing pButton = new PSwing(button);
        exampleList.addExample("Alone - No Sizing", pButton);
    }
    
    private void addButtonAlone200x50() {
        JButton button = new JButton("Button");
        button.setPreferredSize(new Dimension(200, 50));
        PSwing pButton = new PSwing(button);
        exampleList.addExample("Alone - 200x50", pButton);
    }
    
    private void addButtonAlone10x10() {
        JButton button = new JButton("Button");
        button.setPreferredSize(new Dimension(10, 10));
        PSwing pButton = new PSwing(button);
        exampleList.addExample("Alone - 10x10", pButton);
    }
    
    private void addButtonOnPanelNoSizing() {
        JButton button = new JButton("Button");
        JPanel panel = new JPanel();
        panel.add(button);        
        PSwing pPanel = new PSwing(panel);
        
        exampleList.addExample("On JPanel - No Sizing", pPanel);
    }
    
    private void addButtonOnPanel200x50() {
        JButton button = new JButton("Button");
        button.setPreferredSize(new Dimension(200, 50));
        
        JPanel panel = new JPanel();
        panel.add(button);
        PSwing pPanel = new PSwing(panel);
        
        exampleList.addExample("On JPanel - 200x50", pPanel);
    }   

    public static void main(final String[] args) {
        new PSwingExample3().setVisible(true);
    }          
    
    class ExampleList extends PText {
        ExampleList(String name) {
            super(name);
            setScale(2);
        }
        
        public void layoutChildren() {
            PNode node;
            double currentY = getHeight();
            for (int i=0; i<getChildrenCount(); i++) {
                node = getChild(i);
                node.setOffset(0, currentY);
                currentY += node.getFullBounds().getHeight() + 5;
            }           
        }
        
        public void addExample(String name, PNode example) {
            ExampleNode exampleNode = new ExampleNode(name, example);
            exampleNode.setScale(0.5);
            addChild(exampleNode);
        }
        
        class ExampleNode extends PText {
            ExampleNode(String name, PNode example) {
                super(name);
                
                addChild(example);
            }
            
            public void layoutChildren() {
                PNode example = getChild(0);
                example.setOffset(getWidth()+5, 0);
                //example.setScale(getHeight() / example.getHeight());
            }
        }              
    }
}
