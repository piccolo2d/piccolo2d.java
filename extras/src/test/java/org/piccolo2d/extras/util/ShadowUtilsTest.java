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
package org.piccolo2d.extras.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;

import org.piccolo2d.extras.nodes.PShadow;
import org.piccolo2d.extras.util.ShadowUtils;


import junit.framework.TestCase;

/**
 * Unit test for ShadowUtils.
 */
public final class ShadowUtilsTest extends TestCase {
    private static final int TEST_IMAGE_SIZE = 25;
    private static final Paint shadowPaint = new Color(20, 20, 20, 200);
    private BufferedImage src;

    public void setUp() {
        src = new BufferedImage(TEST_IMAGE_SIZE, TEST_IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Paint srcPaint = new Color(255, 0, 0, 200);
        Graphics2D g = src.createGraphics();
        g.setPaint(srcPaint);
        g.drawRect(25, 25, 50, 50);
        g.dispose();
    }

    public void testCreateShadowAcceptsTinyShadow() {
        BufferedImage dest = ShadowUtils.createShadow(src, shadowPaint, 1);
        assertNotNull(dest);
        assertEquals(TEST_IMAGE_SIZE + 4, dest.getWidth());
        assertEquals(TEST_IMAGE_SIZE + 4, dest.getHeight());
    }

    public void testCreateShadowAcceptsHugeShadow() {
        BufferedImage dest = ShadowUtils.createShadow(src, shadowPaint, 25);
        assertNotNull(dest);
        assertEquals(TEST_IMAGE_SIZE + 100, dest.getWidth());
        assertEquals(TEST_IMAGE_SIZE + 100, dest.getHeight());
    }

    public void testNonPositiveBlurRadiusFails() {
        try {
            ShadowUtils.createShadow(src, shadowPaint, 0);
            fail("Non positive blur radius should fail");
        }
        catch (IllegalArgumentException e) {
            // expected
        }

        try {
            ShadowUtils.createShadow(src, shadowPaint, -1);
            fail("Non positive blur radius should fail");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testConstructorDoesNotAcceptNullSrc() {
        try {
            ShadowUtils.createShadow(null, Color.BLACK, 4);
            fail("ctr(null, ...) expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
    }

}