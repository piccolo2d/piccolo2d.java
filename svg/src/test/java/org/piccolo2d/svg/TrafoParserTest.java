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
package org.piccolo2d.svg;

import java.awt.geom.AffineTransform;
import java.text.ParseException;

import junit.framework.TestCase;

public class TrafoParserTest extends TestCase {

    static String toString(final ParseException e) {
        final String s = e.getMessage();
        final int off = e.getErrorOffset();

        final StringBuilder b = new StringBuilder(s.length() * 2 + 2);
        b.append(s).append("\n");
        for (int i = 0; i < s.length(); i++) {
            if (i == off) {
                b.append('^');
            }
            else {
                b.append('-');
            }
        }
        return b.toString();
    }

    public void testScale() throws ParseException {
        final TrafoParser p = new TrafoParser();
        final AffineTransform t = p.parse("scale(2.0 -3.0)", null);
        assertEquals(2, t.getScaleX(), 1e-9);
        assertEquals(-3, t.getScaleY(), 1e-9);
        assertEquals(0, t.getShearX(), 1e-9);
        assertEquals(0, t.getShearY(), 1e-9);
        assertEquals(0, t.getTranslateX(), 1e-9);
        assertEquals(0, t.getTranslateY(), 1e-9);
    }

    public void testTranslate() throws ParseException {
        final TrafoParser p = new TrafoParser();
        final AffineTransform t = p.parse("translate(2 3)", null);
        assertEquals(1, t.getScaleX(), 1e-9);
        assertEquals(1, t.getScaleY(), 1e-9);
        assertEquals(0, t.getShearX(), 1e-9);
        assertEquals(0, t.getShearY(), 1e-9);
        assertEquals(2, t.getTranslateX(), 1e-9);
        assertEquals(3, t.getTranslateY(), 1e-9);
    }
}
