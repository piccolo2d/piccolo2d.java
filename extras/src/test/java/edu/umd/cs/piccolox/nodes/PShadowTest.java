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
package edu.umd.cs.piccolox.nodes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;

import java.awt.image.BufferedImage;

import junit.framework.TestCase;

/**
 * Unit test for PShadow.
 */
public final class PShadowTest extends TestCase {

    public void testConstructor() {
        BufferedImage src = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Paint srcPaint = new Color(255, 0, 0, 200);
        Paint shadowPaint = new Color(20, 20, 20, 200);
        Graphics2D g = src.createGraphics();
        g.setPaint(srcPaint);
        g.drawRect(25, 25, 50, 50);
        g.dispose();

        for (int blurRadius = 1; blurRadius < 33; blurRadius += 4) {
            PShadow shadowNode = new PShadow(src, shadowPaint, blurRadius);
            assertNotNull(shadowNode);
            assertEquals(src.getWidth() + 4 * blurRadius, shadowNode.getWidth(), 0.001d);
            assertEquals(src.getHeight() + 4 * blurRadius, shadowNode.getHeight(), 0.001d);
        }

        try {
            PShadow shadowNode = new PShadow(null, shadowPaint, 4);
            fail("ctr(null, ...) expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
        try {
            PShadow shadowNode = new PShadow(src, shadowPaint, 0);
            fail("ctr(..., -1) expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
        try {
            PShadow shadowNode = new PShadow(src, shadowPaint, -1);
            fail("ctr(..., -1) expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
    }
}