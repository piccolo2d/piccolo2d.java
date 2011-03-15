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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Timer;

import org.piccolo2d.PCanvas;
import org.piccolo2d.extras.PFrame;
import org.piccolo2d.nodes.PText;

/**
 * Full bounds event example.
 */
public final class FullBoundsEventExample extends PFrame {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Parent node. */
    private PText parent;

    /** Child node. */
    private PText child;

    /** Internal child node. */
    private PText internalChild;

    /** Parent status node. */
    private PText parentStatus;

    /** Child status node. */
    private PText childStatus;

    /** Internal child status node. */
    private PText internalChildStatus;


    /**
     * Create a new full bounds event example.
     */
    public FullBoundsEventExample() {
        this(null);
    }

    /**
     * Create a new full bounds event example with the specified canvas.
     *
     * @param canvas canvas for this full bounds event example
     */
    public FullBoundsEventExample(final PCanvas canvas) {
        super("FullBoundsEventExample", false, canvas);
    }


    /** {@inheritDoc} */
    public void initialize() {
        parent = new PText("Parent");
        child = new PText("Child");
        internalChild = new PText("Internal child");
        parentStatus = new PText("Parent status");
        childStatus = new PText("Child status");
        internalChildStatus = new PText("Internal child status");

        parent.offset(50.0, 50.0);
        child.offset(100.0, 100.0);
        internalChild.offset(25.0, 25.0);
        parentStatus.offset(25.0, 295.0);
        childStatus.offset(25.0, 315.0);
        internalChildStatus.offset(25.0, 335.0);

        parent.addChild(child);
        parent.addChild(internalChild);
        getCanvas().getLayer().addChild(parent);
        getCanvas().getCamera().addChild(parentStatus);
        getCanvas().getCamera().addChild(childStatus);
        getCanvas().getCamera().addChild(internalChildStatus);

        parent.addPropertyChangeListener("fullBounds", new PropertyChangeListener()
            {
                /** {@inheritDoc} */
                public void propertyChange(final PropertyChangeEvent event) {
                    parentStatus.setText("parent fullBounds change heard");
                    Timer t = new Timer(2000, new ActionListener() {
                            /** {@inheritDoc} */
                            public void actionPerformed(final ActionEvent event) {
                                parentStatus.setText("");
                            }
                        });
                    t.setRepeats(false);
                    t.start();
                }
            });

        child.addPropertyChangeListener("fullBounds", new PropertyChangeListener()
            {
                /** {@inheritDoc} */
                public void propertyChange(final PropertyChangeEvent event) {
                    childStatus.setText("child fullBounds change heard");
                    Timer t = new Timer(2000, new ActionListener() {
                            /** {@inheritDoc} */
                            public void actionPerformed(final ActionEvent event) {
                                childStatus.setText("");
                            }
                        });
                    t.setRepeats(false);
                    t.start();
                }
            });

        internalChild.addPropertyChangeListener("fullBounds", new PropertyChangeListener()
            {
                /** {@inheritDoc} */
                public void propertyChange(final PropertyChangeEvent event) {
                    internalChildStatus.setText("internalChild fullBounds change heard");
                    Timer t = new Timer(2000, new ActionListener() {
                            /** {@inheritDoc} */
                            public void actionPerformed(final ActionEvent event) {
                                internalChildStatus.setText("");
                            }
                        });
                    t.setRepeats(false);
                    t.start();
                }
            });

        Timer parentOffset = new Timer(10000, new ActionListener() {
                /** {@inheritDoc} */
                public void actionPerformed(final ActionEvent event) {
                    parent.offset(10.0d, 0.0d);
                }
            });
        parentOffset.setRepeats(true);
        parentOffset.start();

        Timer childOffset = new Timer(10000, new ActionListener() {
                /** {@inheritDoc} */
                public void actionPerformed(final ActionEvent event) {
                    child.offset(10.0d, 0.0d);
                }
            });
        childOffset.setInitialDelay(3333);
        childOffset.setRepeats(true);
        childOffset.start();

        Timer internalChildOffset = new Timer(10000, new ActionListener() {
                /** {@inheritDoc} */
                public void actionPerformed(final ActionEvent event) {
                    internalChild.offset(10.0d, 0.0d);
                }
            });
        internalChildOffset.setInitialDelay(6666);
        internalChildOffset.setRepeats(true);
        internalChildOffset.start();
    }

    /**
     * Main.
     *
     * @param args command line arguments, ignored
     */
    public static void main(final String[] args) {
        new FullBoundsEventExample();
    }
}