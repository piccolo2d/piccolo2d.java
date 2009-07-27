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
package edu.umd.cs.piccolo.examples;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolox.PFrame;

public class ExampleRunner extends JFrame {

    public ExampleRunner() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Piccolo Example Runner");
        setSize(426, 335);
        getContentPane().setLayout(new BorderLayout());
        createExampleButtons();
        validate();
        pack();
        setVisible(true);
    }

    public void createExampleButtons() {
        Container c = getContentPane();
        JPanel panel = new JPanel(new GridLayout(0, 1));
        c.add(BorderLayout.NORTH, panel);

        panel.add(new JCheckBox(new AbstractAction("Print Frame Rates to Console") {
            public void actionPerformed(ActionEvent e) {
                PDebug.debugPrintFrameRate = !PDebug.debugPrintFrameRate;
            }
        }));

        panel.add(new JCheckBox(new AbstractAction("Show Region Managment") {
            public void actionPerformed(ActionEvent e) {
                PDebug.debugRegionManagement = !PDebug.debugRegionManagement;
            }
        }));

        panel.add(new JCheckBox(new AbstractAction("Show Full Bounds") {
            public void actionPerformed(ActionEvent e) {
                PDebug.debugFullBounds = !PDebug.debugFullBounds;
            }
        }));

        panel = new JPanel(new GridLayout(0, 2));
        c.add(BorderLayout.CENTER, panel);

        addExampleButtons(panel, new Class[] { ActivityExample.class, AngleNodeExample.class,
                BirdsEyeViewExample.class, CameraExample.class, CenterExample.class, ChartLabelExample.class,
                ClipExample.class, CompositeExample.class, DynamicExample.class, EventHandlerExample.class,
                FullScreenNodeExample.class, GraphEditorExample.class, GraphEditorExample.class, GridExample.class,
                GroupExample.class, HandleExample.class, HierarchyZoomExample.class, KeyEventFocusExample.class,
                LayoutExample.class, LensExample.class, NavigationExample.class, NodeCacheExample.class,
                NodeEventExample.class, NodeEventExample.class, NodeLinkExample.class, PanToExample.class,
                PathExample.class, PositionExample.class, PositionPathActivityExample.class, PulseExample.class,
                ScrollingExample.class, SelectionExample.class, SquiggleExample.class, StickyExample.class,
                StickyHandleLayerExample.class, StrokeExample.class, TextExample.class, TooltipExample.class,
                TwoCanvasExample.class, WaitForActivitiesExample.class });
    }

    private void addExampleButtons(JPanel panel, Class[] exampleClasses) {
        for (int i = 0; i < exampleClasses.length; i++) {
            panel.add(buildExampleButton(exampleClasses[i]));
        }
    }

    private JButton buildExampleButton(final Class exampleClass) {
        String fullClassName = exampleClass.getName();
        String simpleClassName = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
        return new JButton(new AbstractAction(simpleClassName) {
            public void actionPerformed(ActionEvent event) {
                try {
                    PFrame example = (PFrame) exampleClass.newInstance();
                    example.setDefaultCloseOperation(PFrame.DISPOSE_ON_CLOSE);
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(ExampleRunner.this, "A problem was encountered running the example");
                }
            }
        });
    }

    public static void main(String[] args) {
        new ExampleRunner();
    }
}