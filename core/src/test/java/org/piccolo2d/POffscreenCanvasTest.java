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
package org.piccolo2d;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.piccolo2d.PCamera;
import org.piccolo2d.POffscreenCanvas;
import org.piccolo2d.nodes.PPath;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PPaintContext;

import junit.framework.TestCase;

/**
 * Unit test for POffscreenCanvas.
 */
public class POffscreenCanvasTest extends TestCase {

    public void testConstructor() {
        final POffscreenCanvas canvas0 = new POffscreenCanvas(100, 100);
        assertNotNull(canvas0);
        final POffscreenCanvas canvas1 = new POffscreenCanvas(0, 0);
        assertNotNull(canvas1);
        final POffscreenCanvas canvas2 = new POffscreenCanvas(0, 100);
        assertNotNull(canvas2);
        final POffscreenCanvas canvas3 = new POffscreenCanvas(100, 0);
        assertNotNull(canvas3);

        try {
            new POffscreenCanvas(-1, 100);
            fail("ctr(-1, 100) expected IllegalArgumentException");
        }
        catch (final IllegalArgumentException e) {
            // expected
        }
        try {
            new POffscreenCanvas(100, -1);
            fail("ctr(100, -1) expected IllegalArgumentException");
        }
        catch (final IllegalArgumentException e) {
            // expected
        }
        try {
            new POffscreenCanvas(-1, -1);
            fail("ctr(-1, -1) expected IllegalArgumentException");
        }
        catch (final IllegalArgumentException e) {
            // expected
        }
    }

    public void testCamera() {
        final POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
        assertNotNull(canvas);
        final PCamera camera = canvas.getCamera();
        assertNotNull(camera);
        assertEquals(canvas, camera.getComponent());
        final PCamera camera1 = new PCamera();
        canvas.setCamera(camera1);
        assertEquals(camera1, canvas.getCamera());
        assertEquals(null, camera.getComponent());
        assertEquals(canvas, camera1.getComponent());
        canvas.setCamera(null);
        assertEquals(null, camera1.getComponent());
        assertEquals(null, canvas.getCamera());
    }

    public void testRenderEmpty() {
        final POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
        final BufferedImage image = new BufferedImage(100, 200, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics = image.createGraphics();
        canvas.render(graphics);
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 200; y++) {
                assertEquals(0, image.getRGB(x, y));
            }
        }
    }

    public void testRenderEmptyOpaqueNullBackgroundColor() {
        final POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
        final BufferedImage image = new BufferedImage(100, 200, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics = image.createGraphics();
        canvas.setOpaque(true);
        canvas.render(graphics);
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 200; y++) {
                assertEquals(0, image.getRGB(x, y));
            }
        }
    }

    public void testRenderEmptyOpaque() {
        final POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
        final BufferedImage image = new BufferedImage(100, 200, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics = image.createGraphics();
        canvas.setOpaque(true);
        canvas.setBackground(Color.RED);
        canvas.render(graphics);
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 200; y++) {
                // red pixel, RGBA is 255, 0, 0, 255
                assertEquals(-65536, image.getRGB(x, y));
            }
        }
    }

    public void testRenderFull() {
        final POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
        final PPath rect = PPath.createRectangle(0.0f, 0.0f, 200.0f, 300.0f);
        rect.setPaint(new Color(255, 0, 0));
        rect.setStroke(null);
        rect.offset(-100.0d, -100.0d);
        canvas.getCamera().getLayer(0).addChild(rect);
        final BufferedImage image = new BufferedImage(100, 200, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics = image.createGraphics();
        canvas.render(graphics);
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 200; y++) {
                // red pixel, RGBA is 255, 0, 0, 255
                assertEquals(-65536, image.getRGB(x, y));
            }
        }
    }

    public void testRenderNull() {
        try {
            final POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
            canvas.render(null);
            fail("render(null) expected IllegalArgumentException");
        }
        catch (final IllegalArgumentException e) {
            // expected
        }
    }

    public void testRenderQuality() {
        final POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
        assertEquals(POffscreenCanvas.DEFAULT_RENDER_QUALITY, canvas.getRenderQuality());
        canvas.setRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
        assertEquals(PPaintContext.HIGH_QUALITY_RENDERING, canvas.getRenderQuality());
        canvas.setRenderQuality(PPaintContext.LOW_QUALITY_RENDERING);
        assertEquals(PPaintContext.LOW_QUALITY_RENDERING, canvas.getRenderQuality());

        try {
            canvas.setRenderQuality(-1);
        }
        catch (final IllegalArgumentException e) {
            // expected
        }
    }

    public void testRepaint() {
        final POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
        canvas.repaint(null);
        canvas.repaint(new PBounds(0.0, 0.0, 50.0, 50.0));
    }

    public void testPaintImmediately() {
        final POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
        canvas.paintImmediately();
    }

    public void testPopCursor() {
        final POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
        canvas.popCursor();
    }

    public void testPushCursor() {
        final POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
        canvas.pushCursor(null);
        canvas.pushCursor(Cursor.getDefaultCursor());
    }

    public void testInteracting() {
        final POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
        canvas.setInteracting(true);
        canvas.setInteracting(false);
    }

    public void testRoot() {
        final POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
        assertNotNull(canvas.getRoot());
    }

    public void testRootIsNullWhenCameraIsNull() {
        final POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
        canvas.setCamera(null);
        assertEquals(null, canvas.getRoot());
    }

    public void testOpaque() {
        final POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
        assertFalse(canvas.isOpaque());
        canvas.setOpaque(true);
        assertTrue(canvas.isOpaque());
    }

    public void testBackground() {
        final POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
        assertEquals(null, canvas.getBackground());
        canvas.setBackground(Color.RED);
        assertEquals(Color.RED, canvas.getBackground());
    }
}