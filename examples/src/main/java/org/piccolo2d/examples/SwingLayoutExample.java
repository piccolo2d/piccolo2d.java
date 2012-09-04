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
package org.piccolo2d.examples;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.piccolo2d.PCanvas;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.extras.pswing.PSwing;
import org.piccolo2d.extras.pswing.PSwingCanvas;
import org.piccolo2d.extras.swing.SwingLayoutNode;
import org.piccolo2d.extras.swing.SwingLayoutNode.Anchor;
import org.piccolo2d.nodes.PHtmlView;
import org.piccolo2d.nodes.PPath;
import org.piccolo2d.nodes.PText;


public class SwingLayoutExample {

    public static class MyPPath extends PPath.Float {
        public MyPPath(final Shape shape, final Color color, final Stroke stroke, final Color strokeColor) {
            super(shape, stroke);
            setPaint(color);
            setStrokePaint(strokeColor);
        }
    }

    public static void main(final String[] args) {

        final Dimension canvasSize = new Dimension(800, 600);
        final PCanvas canvas = new PSwingCanvas();
        canvas.setPreferredSize(canvasSize);

        final PNode rootNode = new PNode();
        canvas.getLayer().addChild(rootNode);
        rootNode.addInputEventListener(new PBasicInputEventHandler() {
            // Shift+Drag up/down will scale the node up/down
            public void mouseDragged(final PInputEvent event) {
                super.mouseDragged(event);
                if (event.isShiftDown()) {
                    event.getPickedNode().scale(event.getCanvasDelta().height > 0 ? 0.98 : 1.02);
                }
            }
        });

        final BorderLayout borderLayout = new BorderLayout();
        borderLayout.setHgap(10);
        borderLayout.setVgap(5);
        final SwingLayoutNode borderLayoutNode = new SwingLayoutNode(borderLayout);
        borderLayoutNode.addChild(new PText("North"), BorderLayout.NORTH);
        borderLayoutNode.setAnchor(Anchor.CENTER);
        borderLayoutNode.addChild(new PText("South"), BorderLayout.SOUTH);
        borderLayoutNode.setAnchor(Anchor.WEST);
        borderLayoutNode.addChild(new PText("East"), BorderLayout.EAST);
        borderLayoutNode.addChild(new PText("West"), BorderLayout.WEST);
        borderLayoutNode.addChild(new PText("CENTER"), BorderLayout.CENTER);
        borderLayoutNode.setOffset(100, 100);
        rootNode.addChild(borderLayoutNode);

        final SwingLayoutNode flowLayoutNode = new SwingLayoutNode(new FlowLayout());
        flowLayoutNode.addChild(new PText("1+1"));
        flowLayoutNode.addChild(new PText("2+2"));
        flowLayoutNode.setOffset(200, 200);
        rootNode.addChild(flowLayoutNode);

        final SwingLayoutNode gridBagLayoutNode = new SwingLayoutNode(new GridBagLayout());
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = GridBagConstraints.RELATIVE;
        gridBagLayoutNode.addChild(new PText("FirstNode"), gridBagConstraints);
        gridBagLayoutNode.addChild(new PText("SecondNode"), gridBagConstraints);
        gridBagConstraints.insets = new Insets(50, 50, 50, 50);
        gridBagLayoutNode.addChild(new PText("ThirdNode"), gridBagConstraints);
        gridBagLayoutNode.setOffset(400, 250);
        rootNode.addChild(gridBagLayoutNode);

        JPanel container = new JPanel();        
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        final SwingLayoutNode boxLayoutNode = new SwingLayoutNode(container);
        boxLayoutNode.addChild(new MyPPath(new Rectangle2D.Double(0, 0, 50, 50), Color.yellow, new BasicStroke(2),
                Color.red));
        boxLayoutNode.addChild(new MyPPath(new Rectangle2D.Double(0, 0, 100, 50), Color.orange, new BasicStroke(2),
                Color.blue));
        final SwingLayoutNode innerNode = new SwingLayoutNode(); // nested
        // layout
        innerNode.addChild(new PSwing(new JLabel("foo")));
        innerNode.addChild(new PSwing(new JLabel("bar")));
        boxLayoutNode.addChild(innerNode, Anchor.CENTER);
        boxLayoutNode.setOffset(300, 300);
        rootNode.addChild(boxLayoutNode);

        final SwingLayoutNode horizontalLayoutNode = new SwingLayoutNode(new GridBagLayout());
        horizontalLayoutNode.addChild(new PSwing(new JButton("Zero")));
        horizontalLayoutNode.addChild(new PSwing(new JButton("One")));
        horizontalLayoutNode.addChild(new PSwing(new JButton("Two")));
        horizontalLayoutNode.addChild(new PSwing(new JLabel("Three")));
        horizontalLayoutNode.addChild(new PSwing(new JSlider()));
        horizontalLayoutNode.addChild(new PSwing(new JTextField("Four")));
        final PHtmlView htmlNode = new PHtmlView("<html>Five</html>", new JLabel().getFont().deriveFont(15f),
                Color.blue);
        htmlNode.scale(3);
        horizontalLayoutNode.addChild(htmlNode);
        horizontalLayoutNode.setOffset(100, 450);
        rootNode.addChild(horizontalLayoutNode);

        // 3x2 grid of values, shapes and labels (similar to a layout in
        // acid-base-solutions)
        final SwingLayoutNode gridNode = new SwingLayoutNode(new GridBagLayout());
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);
        /*---- column of values, right justified ---*/
        constraints.gridy = 0; // row
        constraints.gridx = 0; // column
        constraints.anchor = GridBagConstraints.EAST;
        final PText dynamicNode = new PText("0"); // will be controlled by
        // dynamicSlider
        gridNode.addChild(dynamicNode, constraints);
        constraints.gridy++;
        gridNode.addChild(new PText("0"), constraints);
        /*---- column of shapes, center justified ---*/
        constraints.gridy = 0; // row
        constraints.gridx++; // column
        constraints.anchor = GridBagConstraints.CENTER;
        final PPath redCircle = new PPath.Double(new Ellipse2D.Double(0, 0, 25, 25));
        redCircle.setPaint(Color.RED);
        gridNode.addChild(redCircle, constraints);
        constraints.gridy++;
        final PPath greenCircle = new PPath.Double(new Ellipse2D.Double(0, 0, 25, 25));
        greenCircle.setPaint(Color.GREEN);
        gridNode.addChild(greenCircle, constraints);
        /*---- column of labels, left justified ---*/
        constraints.gridy = 0; // row
        constraints.gridx++; // column
        constraints.anchor = GridBagConstraints.WEST;
        gridNode.addChild(new PHtmlView("<html>H<sub>2</sub>O</html>"), constraints);
        constraints.gridy++;
        gridNode.addChild(new PHtmlView("<html>H<sub>3</sub>O<sup>+</sup></html>"), constraints);
        gridNode.scale(2.0);
        gridNode.setOffset(400, 50);
        rootNode.addChild(gridNode);

        final JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        final JSlider dynamicSlider = new JSlider(0, 1000, 0); // controls
        // dynamicNode
        dynamicSlider.setMajorTickSpacing(dynamicSlider.getMaximum());
        dynamicSlider.setPaintTicks(true);
        dynamicSlider.setPaintLabels(true);
        dynamicSlider.addChangeListener(new ChangeListener() {

            public void stateChanged(final ChangeEvent e) {
                dynamicNode.setText(String.valueOf(dynamicSlider.getValue()));
            }
        });
        controlPanel.add(dynamicSlider);

        final JPanel appPanel = new JPanel(new BorderLayout());
        appPanel.add(canvas, BorderLayout.CENTER);
        appPanel.add(controlPanel, BorderLayout.EAST);

        final JFrame frame = new JFrame();
        frame.setContentPane(appPanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
