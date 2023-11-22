/*
 * Copyright (c) 2008-2019, Piccolo2D project, http://piccolo2d.org
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import org.piccolo2d.extras.PFrame;
import org.piccolo2d.util.PDebug;


public class ExampleRunner extends JFrame {
    private static final long serialVersionUID = 1L;

    public ExampleRunner() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Piccolo Example Runner");
        setSize(650, 600);
        getContentPane().setLayout(new BorderLayout());
        createExampleButtons();
        
        getContentPane().setBackground(new Color(200, 200, 200));
        validate();
        setVisible(true);
    }

    public void createExampleButtons() {
        final Container c = getContentPane();
        
        c.add(buildOptions(), BorderLayout.NORTH);
        JPanel panel= new JPanel(new GridLayout(0, 2));
        c.add(BorderLayout.CENTER, panel);

        addExampleButtons(panel, new Class<?>[] { ActivityExample.class, AngleNodeExample.class,
                BirdsEyeViewExample.class, CameraExample.class, CenterExample.class, ChartLabelExample.class,
                ClipExample.class, CompositeExample.class, DynamicExample.class, EventHandlerExample.class,
                FullScreenNodeExample.class, GraphEditorExample.class, GridExample.class, GroupExample.class,
                HandleExample.class, HelloWorldExample.class, HierarchyZoomExample.class, HtmlViewExample.class,
                KeyEventFocusExample.class, LayoutExample.class, LensExample.class, MouseWheelZoomExample.class,
                NavigationExample.class, NodeCacheExample.class, NodeEventExample.class, NodeExample.class,
                NodeLinkExample.class, P3DRectExample.class, PanToExample.class, PathExample.class,
                PositionExample.class, PositionPathActivityExample.class, PulseExample.class, ScrollingExample.class,
                SelectionExample.class, ShadowExample.class, SquiggleExample.class, StickyExample.class,
                StickyHandleLayerExample.class, StrokeExample.class, TextExample.class, TooltipExample.class,
                TwoCanvasExample.class, WaitForActivitiesExample.class });
    }

    /**
     * @param c
     */
    private JPanel buildOptions() {
        JPanel optionsPanel = new JPanel(new GridLayout(3, 1));        
        optionsPanel.setBorder(new TitledBorder("Display Options"));
               
        optionsPanel.add(new JCheckBox(new AbstractAction("Print Frame Rates to Console") {
        	
            private static final long serialVersionUID = 1L;

			public void actionPerformed(final ActionEvent e) {
                PDebug.debugPrintFrameRate = !PDebug.debugPrintFrameRate;
            }
        }));

        optionsPanel.add(new JCheckBox(new AbstractAction("Show Region Managment") {
        		
            private static final long serialVersionUID = 1L;

			public void actionPerformed(final ActionEvent e) {
                PDebug.debugRegionManagement = !PDebug.debugRegionManagement;
            }
        }));

        optionsPanel.add(new JCheckBox(new AbstractAction("Show Full Bounds") {
        	
        	private static final long serialVersionUID = 1L;
        	
            public void actionPerformed(final ActionEvent e) {
                PDebug.debugFullBounds = !PDebug.debugFullBounds;
            }
        }));
        
        return optionsPanel;
    } 

    private void addExampleButtons(final JPanel panel, final Class<?>[] exampleClasses) {
        for (int i = 0; i < exampleClasses.length; i++) {
            panel.add(buildExampleButton(exampleClasses[i]));
        }
    }

    private JButton buildExampleButton(final Class<?> exampleClass) {
        final String fullClassName = exampleClass.getName();
        final String simpleClassName = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
        final String exampleLabel = camelToProper(simpleClassName);
        JButton button = new JButton(new AbstractAction(exampleLabel) {
        	
            private static final long serialVersionUID = 1L;

			public void actionPerformed(final ActionEvent event) {
                try {
                    final PFrame example = (PFrame)exampleClass.getDeclaredConstructor().newInstance();
                    example.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                }
                catch (final Exception e) {
                    JOptionPane.showMessageDialog(ExampleRunner.this,
                            "A problem was encountered running the example.\n\n" + e.getMessage());
                }
            }
        });        
        button.setBackground(Color.WHITE);
        button.setHorizontalAlignment(JButton.LEFT);                
        return button;
    }

    private String camelToProper(String camel) {
        Pattern pattern = Pattern.compile("[a-z]([A-Z])");
        Matcher matcher = pattern.matcher(camel);
        StringBuffer result = new StringBuffer();
        int lastIndex = 0;
        while (matcher.find()) {
            int nextWord = matcher.start(1);
            result.append(camel.substring(lastIndex, nextWord));
            result.append(' ');
            lastIndex = nextWord;
        }
        result.append(camel.substring(lastIndex, camel.length()));
        return result.toString();
    }

    public static void main(final String[] args) {
        new ExampleRunner();
    }
}
