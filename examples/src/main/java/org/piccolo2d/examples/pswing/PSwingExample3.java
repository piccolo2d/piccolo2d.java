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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import org.piccolo2d.PNode;
import org.piccolo2d.extras.pswing.PSwing;
import org.piccolo2d.extras.pswing.PSwingCanvas;
import org.piccolo2d.nodes.PText;


/**
 * User: Sam Reid Date: Jul 11, 2005 Time: 12:15:55 PM
 */
public class PSwingExample3 extends JFrame {
    private static final long serialVersionUID = 1L;

    public PSwingExample3() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set up basic frame
        setBounds(50, 50, 750, 750);
        setResizable(true);
        setBackground(null);
        setVisible(true);
        final PSwingCanvas canvas = new PSwingCanvas();
        getContentPane().add(canvas);
        validate();

        ExampleGrid exampleGrid = new ExampleGrid(3);
        exampleGrid.addChild(createButtonExamples());
        exampleGrid.addChild(createSimpleComponentExamples());
        canvas.getLayer().addChild(exampleGrid);
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                canvas.getCamera().animateViewToCenterBounds(canvas.getLayer().getFullBounds(), true, 1200);
            }

        });

    }

    private ExampleList createSimpleComponentExamples() {
        ExampleList exampleList = new ExampleList("Simple Components");
        exampleList.addExample("JLabel", new PSwing(new JLabel("JLabel Example")));
        exampleList.addExample("JCheckBox ", new PSwing(new JCheckBox()));        

        JRadioButton radio1 = new JRadioButton("Radio Option 1");
        JRadioButton radio2 = new JRadioButton("Radio Option 1");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(radio1);
        buttonGroup.add(radio2);
        exampleList.addExample("RadioButton 1", radio1);
        exampleList.addExample("RadioButton 2", radio2);

        JPanel examplePanel = new JPanel() {

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.RED);
                g.fillRect(0, 0, getWidth(), getHeight());
        
            }
        };
        examplePanel.setPreferredSize(new Dimension(50, 50));
        
        exampleList.addExample("Custom JPanel ", examplePanel);
        return exampleList;
    }

    private ExampleList createButtonExamples() {
        ExampleList exampleList = new ExampleList("Button Examples");
        addButtonAloneNoSizing(exampleList);
        addButtonAlone200x50(exampleList);
        addButtonOnPanelNoSizing(exampleList);
        addButtonOnPanel200x50(exampleList);
        addButtonAlone10x10(exampleList);
        return exampleList;
    }

    private void addButtonAloneNoSizing(ExampleList exampleList) {
        JButton button = new JButton("Button");
        PSwing pButton = new PSwing(button);
        exampleList.addExample("Alone - No Sizing", pButton);
    }

    private void addButtonAlone200x50(ExampleList exampleList) {
        JButton button = new JButton("Button");
        button.setPreferredSize(new Dimension(200, 50));
        PSwing pButton = new PSwing(button);
        exampleList.addExample("Alone - 200x50", pButton);
    }

    private void addButtonAlone10x10(ExampleList exampleList) {
        JButton button = new JButton("Button");
        button.setPreferredSize(new Dimension(10, 10));
        PSwing pButton = new PSwing(button);
        exampleList.addExample("Alone - 10x10", pButton);
    }

    private void addButtonOnPanelNoSizing(ExampleList exampleList) {
        JButton button = new JButton("Button");
        JPanel panel = new JPanel();
        panel.add(button);
        PSwing pPanel = new PSwing(panel);

        exampleList.addExample("On JPanel - No Sizing", pPanel);
    }

    private void addButtonOnPanel200x50(ExampleList exampleList) {
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

    class ExampleGrid extends PNode {
        private int columns;

        public ExampleGrid(int columns) {
            this.columns = columns;
        }

        public void layoutChildren() {
            double[] colWidths = calculateColumnWidths();

            double currentY = 0;
            for (int i = 0; i < getChildrenCount(); i++) {
                PNode child = getChild(i);
                child.setOffset(colWidths[i % columns] * 1.25, currentY * 1.25);
                if (i % columns == 0 && i > 0) {
                    currentY = getFullBounds().getHeight();
                }
            }
        }

        private double[] calculateColumnWidths() {
            double[] colWidths = new double[columns];
            for (int i = 0; i < getChildrenCount(); i++) {
                PNode child = getChild(i);
                colWidths[i % columns] = Math.max(colWidths[i % columns], child.getFullBounds().getWidth()
                        * child.getScale());
            }
            return colWidths;
        }
    }

    class ExampleList extends PText {
        ExampleList(String name) {
            super(name);
            setScale(2);
        }

        public void layoutChildren() {
            PNode node;
            double currentY = getHeight();
            for (int i = 0; i < getChildrenCount(); i++) {
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

        public void addExample(String name, JComponent example) {
            addExample(name, new PSwing(example));
        }

        class ExampleNode extends PText {
            ExampleNode(String name, PNode example) {
                super(name);

                addChild(example);
            }

            public void layoutChildren() {
                PNode example = getChild(0);
                example.setOffset(getWidth() + 5, 0);
                // example.setScale(getHeight() / example.getHeight());
            }
        }
    }
}
