/*
 * Copyright (c) 2008, Piccolo2D project, http://piccolo2d.org
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
 * None of the name of the Piccolo2D project, the University of Maryland, or the names of its contributors
 * may be used to endorse or promote products derived from this software without specific prior written
 * permission.
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
package org.piccolo2d.svg.cssmini;

import java.awt.Color;

import junit.framework.TestCase;

public class SvgColorTest extends TestCase {

    public void testColorName() {
        assertEquals(147, SvgColor.name2col.size());
        final Color c = SvgColor.compute("chartreuse");
        assertEquals(127, c.getRed());
        assertEquals(255, c.getGreen());
        assertEquals(0, c.getBlue());
    }

    public void testDuplicateHex() {
        assertEquals(0x112233, SvgColor.duplicate(0x123));
        assertEquals(0x0088AA, SvgColor.duplicate(0x08a));
    }

    public void testRgb() {
        Color c = SvgColor.compute("rgb( 11 , 22 , 33 )");
        assertEquals(11, c.getRed());
        assertEquals(22, c.getGreen());
        assertEquals(33, c.getBlue());

        c = SvgColor.compute("rgb( 25 % , 50 % , 75 % )");
        assertEquals(64, c.getRed());
        assertEquals(128, c.getGreen());
        assertEquals(191, c.getBlue());
    }

    public void testRgbHex() {
        Color c = SvgColor.compute("#1b3");
        assertEquals(0x11, c.getRed());
        assertEquals(0xbb, c.getGreen());
        assertEquals(0x33, c.getBlue());

        c = SvgColor.compute("#3a5b7c");
        assertEquals(0x3a, c.getRed());
        assertEquals(0x5b, c.getGreen());
        assertEquals(0x7c, c.getBlue());
    }
}
