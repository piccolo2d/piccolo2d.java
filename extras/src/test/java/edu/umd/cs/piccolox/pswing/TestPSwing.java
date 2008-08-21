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
package edu.umd.cs.piccolox.pswing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * User: Sam Reid Date: Jul 11, 2005 Time: 12:15:55 PM
 */

public class TestPSwing {
    public static void main(String[] args) {
        PSwingCanvas pCanvas = new PSwingCanvas();
        final PText pText = new PText("PText");
        pCanvas.getLayer().addChild(pText);
        JFrame frame = new JFrame("Test Piccolo");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(pCanvas);
        frame.setSize(600, 800);
        frame.setVisible(true);

        PText text2 = new PText("Text2");
        text2.setFont(new Font("Lucida Sans", Font.BOLD, 18));
        pCanvas.getLayer().addChild(text2);
        text2.translate(100, 100);
        text2.addInputEventListener(new PZoomEventHandler());

        pCanvas.removeInputEventListener(pCanvas.getPanEventHandler());

        JButton jButton = new JButton("MyButton!");
        jButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("TestZSwing.actionPerformed!!!!!!!!!!!!!!*********************");
            }
        });
        final PSwing pSwing = new PSwing(jButton);
        pCanvas.getLayer().addChild(pSwing);
        pSwing.repaint();

        JSpinner jSpinner = new JSpinner();
        jSpinner.setPreferredSize(new Dimension(100, jSpinner.getPreferredSize().height));
        PSwing pSpinner = new PSwing(jSpinner);
        pCanvas.getLayer().addChild(pSpinner);
        pSpinner.translate(0, 150);

        JCheckBox jcb = new JCheckBox("CheckBox", true);
        jcb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("TestZSwing.JCheckBox.actionPerformed");
            }
        });
        jcb.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                System.out.println("TestPSwing.JChekbox.stateChanged@" + System.currentTimeMillis());
            }
        });
        PSwing pCheckBox = new PSwing(jcb);
        pCanvas.getLayer().addChild(pCheckBox);
        pCheckBox.translate(100, 0);

        // Growable JTextArea
        JTextArea textArea = new JTextArea("This is a growable TextArea.\nTry it out!");
        textArea.setBorder(new LineBorder(Color.blue, 3));
        PSwing swing = new PSwing(textArea);
        swing.translate(150, 150);
        pCanvas.getLayer().addChild(swing);

        // A Slider
        JSlider slider = new JSlider();
        PSwing pSlider = new PSwing(slider);
        pSlider.translate(200, 200);
        pCanvas.getLayer().addChild(pSlider);

        // A Scrollable JTree
        JTree tree = new JTree();
        tree.setEditable(true);
        JScrollPane p = new JScrollPane(tree);
        p.setPreferredSize(new Dimension(150, 150));
        PSwing pTree = new PSwing(p);
        pCanvas.getLayer().addChild(pTree);
        pTree.translate(0, 250);

        // A JColorChooser - also demonstrates JTabbedPane
        JColorChooser chooser = new JColorChooser();
        PSwing pChooser = new PSwing(chooser);
        pCanvas.getLayer().addChild(pChooser);
        pChooser.translate(100, 300);

        JPanel myPanel = new JPanel();
        myPanel.setBorder(BorderFactory.createTitledBorder("Titled Border"));
        myPanel.add(new JCheckBox("CheckBox"));
        PSwing panelSwing = new PSwing(myPanel);
        pCanvas.getLayer().addChild(panelSwing);
        panelSwing.translate(400, 50);

        // A Slider
        JSlider slider2 = new JSlider();
        PSwing pSlider2 = new PSwing(slider2);
        pSlider2.translate(200, 200);
        PNode root = new PNode();
        root.addChild(pSlider2);
        root.scale(1.5);
        root.rotate(Math.PI / 4);
        root.translate(300, 200);
        pCanvas.getLayer().addChild(root);

        String[] listItems = { "Summer Teeth", "Mermaid Avenue", "Being There", "A.M." };
        PComboBox box = new PComboBox(listItems);
        swing = new PSwing(box);
        swing.translate(200, 250);
        pCanvas.getLayer().addChild(swing);
        box.setEnvironment(swing, pCanvas);// has to be done manually at present

        // Revalidate and repaint
        pCanvas.revalidate();
        pCanvas.repaint();
    }

}
