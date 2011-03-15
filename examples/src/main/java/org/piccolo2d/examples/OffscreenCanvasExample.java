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
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;

import org.piccolo2d.POffscreenCanvas;
import org.piccolo2d.activities.PActivity;
import org.piccolo2d.nodes.PPath;
import org.piccolo2d.nodes.PText;


/**
 * Offscreen canvas example.
 */
public final class OffscreenCanvasExample {

    /** Frame. */
    private final Frame frame;

    /** Background color. */
    private final Color background = new Color(80, 80, 80);

    /** Offscreen canvas. */
    private final POffscreenCanvas canvas;

    /**
     * Create a new offscreen canvas example with the specified graphics device.
     * 
     * @param device graphics device
     */
    public OffscreenCanvasExample(final GraphicsDevice device) {
        final GraphicsConfiguration configuration = device.getDefaultConfiguration();
        frame = new Frame(configuration);
        frame.setUndecorated(true);
        frame.setIgnoreRepaint(true);
        frame.setBounds(100, 100, 400, 400);
        frame.setVisible(true);
        frame.createBufferStrategy(2);

        canvas = new POffscreenCanvas(400, 400);

        final PText text = new PText("Offscreen Canvas Example");
        text.setFont(text.getFont().deriveFont(32.0f));
        text.setTextPaint(new Color(200, 200, 200));
        text.offset(200.0f - text.getWidth() / 2.0f, 200.0f - text.getHeight() / 2.0f);

        final PPath rect = PPath.createRectangle(0.0f, 0.0f, 360.0f, 360.0f);
        rect.setPaint(new Color(20, 20, 20, 80));
        rect.setStroke(new BasicStroke(2.0f));
        rect.setStrokePaint(new Color(20, 20, 20));
        rect.offset(20.0f, 20.0f);

        canvas.getCamera().getLayer(0).addChild(text);
        canvas.getCamera().getLayer(0).addChild(rect);

        final Rectangle2D right = new Rectangle2D.Double(200.0f, 200.0f, 800.0f, 800.0f);
        final Rectangle2D left = new Rectangle2D.Double(-200.0f, 200.0f, 225.0f, 225.0f);
        final Rectangle2D start = new Rectangle2D.Double(0.0f, 0.0f, 400.0f, 400.0f);
        final PActivity toRight = canvas.getCamera().animateViewToCenterBounds(right, true, 5000);
        final PActivity toLeft = canvas.getCamera().animateViewToCenterBounds(left, true, 5000);
        final PActivity toStart = canvas.getCamera().animateViewToCenterBounds(start, true, 5000);
        toLeft.startAfter(toRight);
        toStart.startAfter(toLeft);
    }

    /**
     * Render offscreen graphics into the frame.
     */
    private void render() {
        final BufferStrategy bufferStrategy = frame.getBufferStrategy();
        do {
            do {
                final Graphics2D graphics = (Graphics2D) bufferStrategy.getDrawGraphics();
                // canvas is not opaque, so fill with background color
                graphics.setPaint(background);
                graphics.fillRect(0, 0, 400, 400);
                // then let canvas render into graphics
                canvas.render(graphics);
                graphics.dispose();
            } while (bufferStrategy.contentsRestored());
            bufferStrategy.show();
        } while (bufferStrategy.contentsLost());
    }

    /**
     * Main.
     * 
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        final GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice device = environment.getDefaultScreenDevice();
        final OffscreenCanvasExample example = new OffscreenCanvasExample(device);
        
        final boolean done = false;
        while (!done) {
            example.render();
        }
    }
}
