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
package edu.umd.cs.piccolox;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;

import java.awt.image.BufferedImage;

import edu.umd.cs.piccolo.PCamera;

import edu.umd.cs.piccolo.nodes.PPath;

import edu.umd.cs.piccolo.util.PBounds;

import junit.framework.TestCase;

/**
 * Unit test for POffscreenCanvas.
 */
public class POffscreenCanvasTest extends TestCase {

    public void testConstructor() {
        POffscreenCanvas canvas0 = new POffscreenCanvas(100, 100);
        assertNotNull(canvas0);
        POffscreenCanvas canvas1 = new POffscreenCanvas(0, 0);
        assertNotNull(canvas1);
        POffscreenCanvas canvas2 = new POffscreenCanvas(0, 100);
        assertNotNull(canvas2);
        POffscreenCanvas canvas3 = new POffscreenCanvas(100, 0);
        assertNotNull(canvas3);

        try {
            POffscreenCanvas canvas = new POffscreenCanvas(-1, 100);
            fail("ctr(-1, 100) expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
        try {
            POffscreenCanvas canvas = new POffscreenCanvas(100, -1);
            fail("ctr(100, -1) expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
        try {
            POffscreenCanvas canvas = new POffscreenCanvas(-1, -1);
            fail("ctr(-1, -1) expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testCamera() {
        POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
        assertNotNull(canvas);
        PCamera camera = canvas.getCamera();
        assertNotNull(camera);
        assertEquals(canvas, camera.getComponent());
        PCamera camera1 = new PCamera();
        canvas.setCamera(camera1);
        assertEquals(camera1, canvas.getCamera());
        assertEquals(null, camera.getComponent());
        assertEquals(canvas, camera1.getComponent());
        canvas.setCamera(null);
        assertEquals(null, camera1.getComponent());
        assertEquals(null, canvas.getCamera());
    }

    public void testRenderEmpty() {
        POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
        BufferedImage image = new BufferedImage(100, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        canvas.render(graphics);
        for (int x = 0; x < 100; x++)
        {
            for (int y = 0; y < 200; y++)
            {
                assertEquals(0, image.getRGB(x, y));
            }
        }
    }

    public void testRenderFull() {
        POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
        PPath rect = PPath.createRectangle(0.0f, 0.0f, 200.0f, 300.0f);
        rect.setPaint(new Color(255, 0, 0));
        rect.setStroke(null);
        rect.offset(-100.0d, -100.0d);
        canvas.getCamera().getLayer(0).addChild(rect);
        BufferedImage image = new BufferedImage(100, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        canvas.render(graphics);
        for (int x = 0; x < 100; x++)
        {
            for (int y = 0; y < 200; y++)
            {
                // red pixel, RGBA is 255, 0, 0, 255
                assertEquals(-65536, image.getRGB(x, y));
            }
        }
    }

    public void testRenderNull() {
        try {
            POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
            canvas.render(null);
            fail("render(null) expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testPaintImmediately() {
        POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
        canvas.paintImmediately();
    }

    public void testPopCursor() {
        POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
        canvas.popCursor();
    }

    public void testPushCursor() {
        POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
        canvas.pushCursor(null);
        canvas.pushCursor(Cursor.getDefaultCursor());
    }

    public void testInteracting() {
        POffscreenCanvas canvas = new POffscreenCanvas(100, 200);
        canvas.setInteracting(true);
        canvas.setInteracting(false);
    }
}