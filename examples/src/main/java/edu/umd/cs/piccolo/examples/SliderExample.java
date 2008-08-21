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

import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import edu.umd.cs.piccolox.swing.PScrollPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Tests a set of Sliders and Checkboxes in panels.
 * 
 * @author Martin Clifford
 */
public class SliderExample extends JFrame {
    private PSwingCanvas canvas;
    private PScrollPane scrollPane;
    private JTabbedPane tabbedPane;
    private PSwing swing;

    public SliderExample() {
        // Create main panel
        JPanel mainPanel = new JPanel(false);
        // Create a tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(700, 700));
        // Add tabbed pane to main panel
        mainPanel.add(tabbedPane);
        // Set the frame contents
        getContentPane().add(mainPanel);

        // Create a canvas
        canvas = new PSwingCanvas();
        canvas.setPreferredSize(new Dimension(700, 700));
        // Create a scroll pane for the canvas
        scrollPane = new PScrollPane(canvas);
        // Create a new tab for the tabbed pane
        tabbedPane.add("Tab 1", scrollPane);

        // Create the contents for "Tab 1"
        JPanel tabPanel = new JPanel(false);
        tabPanel.setLayout(null);
        tabPanel.setPreferredSize(new Dimension(700, 700));
        // Populate the tab panel with four instances of nested panel.
        JPanel panel;
        panel = createNestedPanel();
        panel.setSize(new Dimension(250, 250));
        panel.setLocation(0, 0);
        tabPanel.add(panel);
        panel = createNestedPanel();
        panel.setSize(new Dimension(250, 250));
        panel.setLocation(0, 350);
        tabPanel.add(panel);
        panel = createNestedPanel();
        panel.setSize(new Dimension(250, 250));
        panel.setLocation(350, 0);
        tabPanel.add(panel);
        panel = createNestedPanel();
        panel.setSize(new Dimension(250, 250));
        panel.setLocation(350, 350);
        tabPanel.add(panel);
        // Add the default zoom button
        JButton buttonPreset = new JButton("Zoom = 100%");
        buttonPreset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                canvas.getCamera().setViewScale(1.0);
                canvas.getCamera().setViewOffset(0, 0);
            }
        });
        buttonPreset.setSize(new Dimension(120, 25));
        buttonPreset.setLocation(240, 285);
        tabPanel.add(buttonPreset);
        // Create a pswing object for the tab panel
        swing = new PSwing(tabPanel);
        swing.translate(0, 0);
        // Add the pswing object to the canvas
        canvas.getLayer().addChild(swing);
        // Turn off default pan event handling
        canvas.setPanEventHandler(null);

        // Set up basic frame
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Slider Example");
        setResizable(true);
        setBackground(null);
        pack();
        setVisible(true);
    }

    private JPanel createNestedPanel() {
        // A panel MUST be created with double buffering off
        JPanel panel;
        JLabel label;
        panel = new JPanel(false);
        panel.setLayout(new BorderLayout());
        label = new JLabel("A Panel within a panel");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setForeground(Color.white);
        JLabel label2 = new JLabel("A Panel within a panel");
        label2.setHorizontalAlignment(SwingConstants.CENTER);
        JSlider slider = new JSlider();
        JCheckBox cbox1 = new JCheckBox("Checkbox 1");
        JCheckBox cbox2 = new JCheckBox("Checkbox 2");
        JPanel panel3 = new JPanel(false);
        panel3.setLayout(new BoxLayout(panel3, BoxLayout.PAGE_AXIS));
        panel3.setBorder(new EmptyBorder(3, 3, 3, 3));
        panel3.add(label2);
        panel3.add(slider);
        panel3.add(cbox1);
        panel3.add(cbox2);
        JPanel panel2 = new JPanel(false);
        panel2.setBackground(Color.blue);
        panel.setBorder(new EmptyBorder(1, 1, 1, 1));
        panel2.add(label);
        panel2.add(panel3);
        panel.setBackground(Color.red);
        panel.setSize(new Dimension(250, 250));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(panel2, "Center");
        panel.revalidate();
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SliderExample();
            }
        });
    }
}
