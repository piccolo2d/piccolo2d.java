/*
 * Copyright (c) 2008-2012, Piccolo2D project, http://piccolo2d.org
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
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.piccolo2d.PCanvas;
import org.piccolo2d.event.PMouseWheelZoomEventHandler;
import org.piccolo2d.extras.PFrame;
import org.piccolo2d.nodes.PPath;
import org.piccolo2d.nodes.PText;

/**
 * Mouse wheel zoom example.
 *
 * @since 2.0
 */
public final class MouseWheelZoomExample extends PFrame {
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Number of text and rect nodes. */
    private static final int N = 100;

    /** Text paint. */
    private static final Paint TEXT_PAINT = new Color(20, 20, 20, 200);

    /** Source of randomness. */
    private final Random random;


    /**
     * Create a new mouse wheel zoom example.
     */
    public MouseWheelZoomExample() {
        this(null);
    }

    /**
     * Create a new mouse wheel zoom example with the specified canvas.
     *
     * @param canvas canvas for this mouse wheel zoom example
     */
    public MouseWheelZoomExample(final PCanvas canvas) {
        super("MouseWheelZoomExample", false, canvas);
        random = new Random();
    }


    /** {@inheritDoc} */
    public void initialize() {
        for (int i = 0; i < N; i++) {
            createTextNode(i);
        }
        for (int i = 0; i < N; i++) {
            createRectNode(i);
        }

        // uninstall default zoom event handler
        getCanvas().removeInputEventListener(getCanvas().getZoomEventHandler());

        // install mouse wheel zoom event handler
        final PMouseWheelZoomEventHandler mouseWheelZoomEventHandler = new PMouseWheelZoomEventHandler();
        getCanvas().addInputEventListener(mouseWheelZoomEventHandler);

        // create a toolbar
        final JToolBar toolBar = new JToolBar();
        final JToggleButton zoomAboutMouse = new JToggleButton("Zoom about mouse");
        final JToggleButton zoomAboutCanvasCenter = new JToggleButton("Zoom about canvas center");
        final JToggleButton zoomAboutViewCenter = new JToggleButton("Zoom about view center");
        final ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(zoomAboutMouse);
        buttonGroup.add(zoomAboutCanvasCenter);
        buttonGroup.add(zoomAboutViewCenter);
        toolBar.add(zoomAboutMouse);
        toolBar.add(zoomAboutCanvasCenter);
        toolBar.add(zoomAboutViewCenter);
        toolBar.setFloatable(false);

        zoomAboutMouse.addActionListener(new ActionListener() {
                /** {@inheritDoc} */
                public void actionPerformed(final ActionEvent event) {
                    mouseWheelZoomEventHandler.zoomAboutMouse();
                }
            });
        zoomAboutCanvasCenter.addActionListener(new ActionListener() {
                /** {@inheritDoc} */
                public void actionPerformed(final ActionEvent event) {
                    mouseWheelZoomEventHandler.zoomAboutCanvasCenter();
                }
            });
        zoomAboutViewCenter.addActionListener(new ActionListener() {
                /** {@inheritDoc} */
                public void actionPerformed(final ActionEvent event) {
                    mouseWheelZoomEventHandler.zoomAboutViewCenter();
                }
            });

        final JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add("North", toolBar);
        contentPane.add("Center", getCanvas());
        setContentPane(contentPane);
        validate();
    }

    private void createTextNode(final int i) {
        PText text = new PText("Label " + i);
        text.setTextPaint(TEXT_PAINT);
        text.setOffset(random.nextDouble() * 1000.0d - random.nextDouble() * 1000.0,
                       random.nextDouble() * 1000.0d - random.nextDouble() * 1000.0);
        getCanvas().getLayer().addChild(text);
    }

    private void createRectNode(final int i) {
        PPath path = PPath.createRectangle(0.0f, 0.0f, 50.0f * i/N + 10.0f, 50.0f * i/N + 10.0f);
        int r = 200 * i/N + 45;
        path.setPaint(new Color(r, 0, 0, 200));
        path.setStrokePaint(new Color(r - 10, 0, 0, 200));
        path.setOffset(random.nextDouble() * 1000.0d - random.nextDouble() * 1000.0,
                       random.nextDouble() * 1000.0d - random.nextDouble() * 1000.0);
        getCanvas().getLayer().addChild(path);
    }


    /**
     * Main.
     *
     * @param args command line arguments, ignored
     */
    public static void main(final String[] args) {
        new MouseWheelZoomExample();
    }
}