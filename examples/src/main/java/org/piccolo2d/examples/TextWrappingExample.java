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

import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JPanel;

import org.piccolo2d.PCanvas;

import org.piccolo2d.extras.PFrame;

import org.piccolo2d.nodes.PText;

/**
 * Text wrapping example.
 */
public class TextWrappingExample extends PFrame {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Wide text node. */
    private final PText wide = new PText();

    /** Wide text node bounds label. */
    private final PText wideLabel = new PText();

    /** Narrow text node. */
    private final PText narrow = new PText();

    /** Narrow text node bounds label. */
    private final PText narrowLabel = new PText();

    /** Toggle text action. */
    private final AbstractAction toggleText = new AbstractAction("Toggle text") {
            /** {@inheritDoc} */
            public void actionPerformed(final ActionEvent event) {
                wide.setText(wide.getText() == "" ? TEXT : "");
                narrow.setText(narrow.getText() == "" ? TEXT : "");
            }
        };

    /** Constrain height action. */
    private final AbstractAction constrainHeight = new AbstractAction("Constrain Height") {
            /** {@inheritDoc} */
            public void actionPerformed(final ActionEvent event) {
                wide.setConstrainHeightToTextHeight(!wide.isConstrainHeightToTextHeight());
                narrow.setConstrainHeightToTextHeight(!narrow.isConstrainHeightToTextHeight());
            }
        };

    /** Constrain width action. */
    private final AbstractAction constrainWidth = new AbstractAction("Constrain Width") {
            /** {@inheritDoc} */
            public void actionPerformed(final ActionEvent event) {
                wide.setConstrainWidthToTextWidth(!wide.isConstrainWidthToTextWidth());
                narrow.setConstrainWidthToTextWidth(!narrow.isConstrainWidthToTextWidth());
            }
        };

    /** Reset bounds action. */
    private final AbstractAction resetBoundsAction = new AbstractAction("Reset Bounds") {
            /** {@inheritDoc} */
            public void actionPerformed(final ActionEvent event) {
                wide.setBounds(10.0d, 10.0d, 400.0d, 100.0d);
                narrow.setBounds(10.0d, 210.0d, 100.0d, 400.0d);
            }
        };

    /** Text. */
    private static final String TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed et lacus eros, non auctor odio. Duis nibh dolor, consectetur ac sodales sit amet, hendrerit eget lorem. Donec sollicitudin quam vel ipsum accumsan hendrerit eget a dui. Vivamus iaculis sollicitudin faucibus. Integer dignissim facilisis est, tempor ultrices eros convallis vitae. In.";


    /**
     * Create a new text wrapping example.
     */
    public TextWrappingExample() {
        this(null);
    }

    /**
     * Create a new text wrapping example with the specified canvas.
     *
     * @param canvas canvas
     */
    public TextWrappingExample(final PCanvas canvas) {
        super("TextWrappingExample", false, canvas);
    }


    /** {@inheritDoc} */
    public void initialize() {
        JToggleButton toggleTextButton = new JToggleButton(toggleText);
        JToggleButton constrainHeightButton = new JToggleButton(constrainHeight);
        constrainHeightButton.setSelected(wide.isConstrainHeightToTextHeight());
        JToggleButton constrainWidthButton = new JToggleButton(constrainWidth);
        constrainWidthButton.setSelected(wide.isConstrainWidthToTextWidth());
        JToolBar toolBar = new JToolBar();
        toolBar.add(toggleTextButton);
        toolBar.add(constrainHeightButton);
        toolBar.add(constrainWidthButton);
        toolBar.addSeparator();
        toolBar.add(resetBoundsAction);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add("North", toolBar);
        panel.add("Center", getCanvas());
        setContentPane(panel);
        invalidate();
        validate();

        wide.addPropertyChangeListener("bounds", new PropertyChangeListener() {
                /** {@inheritDoc} */
                public void propertyChange(final PropertyChangeEvent event) {
                    wideLabel.setText("Wide label bounds=" + wide.getBounds());
                }
            });

        narrow.addPropertyChangeListener("bounds", new PropertyChangeListener() {
                /** {@inheritDoc} */
                public void propertyChange(final PropertyChangeEvent event) {
                    narrowLabel.setText("Narrow label bounds=" + narrow.getBounds());
                }
            });

        wide.setPaint(Color.GRAY);
        wide.setBounds(10.0d, 10.0d, 400.0d, 100.0d);

        narrow.setPaint(Color.YELLOW);
        narrow.setBounds(10.0d, 210.0d, 100.0d, 400.0d);

        wideLabel.offset(10.0d, 290.0d);
        narrowLabel.offset(10.0d, 315.0d);

        getCanvas().getLayer().addChild(wide);
        getCanvas().getLayer().addChild(narrow);
        getCanvas().getCamera().addChild(wideLabel);
        getCanvas().getCamera().addChild(narrowLabel);
    }

    /**
     * Main.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        new TextWrappingExample();
    }
}
