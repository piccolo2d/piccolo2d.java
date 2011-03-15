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
package org.piccolo2d.extras.swt;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.swt.layout.FillLayout;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.piccolo2d.extras.swt.PSWTCanvas;
import org.piccolo2d.extras.swt.PSWTImage;

/**
 * Unit test for PSWTImage.
 */
public class PSWTImageTest extends TestCase {
    File imageFile;
    PSWTCanvas canvas;
    PSWTImage imageNode;
    Image image;

    public void setUp() throws Exception {
        final Display display = Display.getDefault();
        final Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        canvas = new PSWTCanvas(shell, 0);
        imageNode = new PSWTImage(canvas);
        image = new Image(display, new Rectangle(0, 0, 100, 100));
    }

    public void testImageShouldDefaultToNull() {
        assertNull(imageNode.getImage());
    }

    public void testPaintShouldDoNothingWhenImageIsNull() {
        // if it tries to use the graphics context, it would throw a NPE
        imageNode.paint(null);
    }

    public void testImageInConstructorPersists() {
        imageNode = new PSWTImage(canvas, image);
        assertSame(image, imageNode.getImage());
    }

    public void testDisposingCanvasDisposesImage() {
        final boolean[] called = new boolean[1];
        called[0] = false;
        imageNode = new PSWTImage(canvas, image) {
            protected void disposeImage() {
                called[0] = true;
                super.disposeImage();
            }
        };
        canvas.dispose();
        assertTrue(called[0]);
    }
}
