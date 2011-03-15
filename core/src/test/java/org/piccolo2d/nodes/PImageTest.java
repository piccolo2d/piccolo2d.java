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
package org.piccolo2d.nodes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.piccolo2d.nodes.PImage;
import org.piccolo2d.util.PPaintContext;

import junit.framework.TestCase;

/**
 * Unit test for PImage.
 */
public class PImageTest extends TestCase {

    public void testClone() {
        final PImage srcNode = new PImage(new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB));
        final PImage clonedNode = (PImage) srcNode.clone();
        assertNotNull(clonedNode.getImage());

        assertEquals(srcNode.getImage().getWidth(null), clonedNode.getImage().getWidth(null));
        assertEquals(srcNode.getImage().getHeight(null), clonedNode.getImage().getHeight(null));

        assertEquals(srcNode.getBounds(), clonedNode.getBounds());        
    }

    public void testToString() {
        final PImage aNode = new PImage(new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB));
        assertNotNull(aNode.toString());
    }

    public void testToBufferedImageReturnsCopyIfToldTo() {
        final BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        final BufferedImage copy = PImage.toBufferedImage(img, true);
        assertNotSame(img, copy);
    }

    public void testCanBeCreatedFromFile() throws IOException {
        final BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        final File imgFile = File.createTempFile("test", ".jpeg");
        ImageIO.write(img, "JPEG", imgFile);
        imgFile.deleteOnExit();
        final PImage imageNode = new PImage(imgFile.getAbsolutePath());
        assertNotNull(imageNode.getImage());
        assertEquals(100, imageNode.getImage().getWidth(null));
        assertEquals(100, imageNode.getImage().getHeight(null));
    }

    public void testCanBeCreatedFromUrl() throws IOException {
        final BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        final File imgFile = File.createTempFile("test", ".jpeg");
        imgFile.deleteOnExit();
        ImageIO.write(img, "JPEG", imgFile);

        final PImage imageNode = new PImage(imgFile.toURI().toURL());
        assertEquals(100, imageNode.getImage().getWidth(null));
        assertEquals(100, imageNode.getImage().getHeight(null));
    }

    public void testImageCanBeSetFromFile() throws IOException {
        final BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        final File imgFile = File.createTempFile("test", ".jpeg");
        imgFile.deleteOnExit();
        ImageIO.write(img, "JPEG", imgFile);

        final PImage imageNode = new PImage();
        imageNode.setImage(imgFile.getAbsolutePath());
        assertEquals(100, imageNode.getImage().getWidth(null));
        assertEquals(100, imageNode.getImage().getHeight(null));
    }

    public void testPaintAnEmptyImageNodeDoesNothing() {
        final PImage imageNode = new PImage();

        final BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        final PPaintContext paintContext = new PPaintContext(img.createGraphics());
        imageNode.paint(paintContext);
    }

}
