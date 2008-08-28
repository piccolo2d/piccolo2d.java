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
package org.piccolo2d.svg.css;

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class CssParserTest extends TestCase {

    private static void debug(final String label, final Object o) {
        System.out.println("-- debug " + label + " -----------");
        System.out.println(o);
    }

    public void testParse() throws ParseException {
        final CssParser p = new CssParser();
        LinkedHashMap css = null;

        assertNotNull(css = p.parse("d { key:value }", null));
        Pattern pa = (Pattern) css.keySet().iterator().next();
        debug("p0", pa.pattern());
        assertTrue(pa.matcher("/a/b/d").matches());
        assertTrue(pa.matcher("/a/b/d[@class='c0 c1 c2 c3']").matches());
        assertTrue(pa.matcher("/a/b/d[@class='c1']").matches());
        assertFalse(pa.matcher("/a/b/de").matches());
        assertFalse(pa.matcher("/a/b/cd").matches());

        // FIXME invert this test:
        try {
            assertNull(css = p.parse("d f{ key:value }", null));
        }
        catch (final ParseException e) {
            assertEquals(2, e.getErrorOffset());
        }
        // pa = (Pattern) css.keySet().iterator().next();
        // debug("p0", pa.pattern());
        // assertTrue(pa.matcher("/a/b/d/e/f").matches());
        // assertTrue(pa.matcher("/a/b/d[@class='c0']/e/f").matches());
        // assertTrue(pa.matcher("/a/b/d/e[@class='c1']/f").matches());
        // assertTrue(pa.matcher("/a/b/d/e/f[@class='c1']").matches());
        // assertFalse(pa.matcher("/a/b/df").matches());

        assertNotNull(css = p.parse(" .c1 { key:value }", null));
        pa = (Pattern) css.keySet().iterator().next();
        debug("p01", pa.pattern());
        assertTrue(pa.matcher("/a/b/d[@class='c0 c1 c2 c3']").matches());
        assertTrue(pa.matcher("/a/b/d[@class='c1']").matches());
        assertFalse(pa.matcher("/a/b/d[@class='c12']").matches());
        assertTrue(pa.matcher("/a/b/cd[@class='c0 c1 c2']").matches());
        assertTrue(pa.matcher("/a/b/de[@class='c0 c1 c2']").matches());
        assertFalse(pa.matcher("/a/b/d[@class='c0 c1 c2']/e").matches());

        assertNotNull(css = p.parse("d.c1{ key:value }", null));
        pa = (Pattern) css.keySet().iterator().next();
        debug("p1", pa.pattern());
        assertTrue(pa.matcher("/a/b/d[@class='c0 c1 c2 c3']").matches());
        assertTrue(pa.matcher("/a/b/d[@class='c1']").matches());
        assertFalse(pa.matcher("/a/b/d[@class='c12']").matches());
        assertFalse(pa.matcher("/a/b/cd[@class='c0 c1 c2']").matches());
        assertFalse(pa.matcher("/a/b/de[@class='c0 c1 c2']").matches());
        assertFalse(pa.matcher("/a/b/d[@class='c0 c1 c2']/e").matches());

        assertNotNull(css = p.parse(" d .c1 { key:value }", null));
        pa = (Pattern) css.keySet().iterator().next();
        debug("p2", pa.pattern());
        assertTrue(pa.matcher("/a/b/d[@class='c0 c1 c2 c3']").matches());
        assertTrue(pa.matcher("/a/b/d[@class='c1']").matches());
        assertFalse(pa.matcher("/a/b/d[@class='c12']").matches());
        assertFalse(pa.matcher("/a/b/cd[@class='c0 c1 c2']").matches());
        assertFalse(pa.matcher("/a/b/de[@class='c0 c1 c2']").matches());
        assertFalse(pa.matcher("/a/b/d[@class='c0 c1 c2']/e").matches());

        assertNotNull(css = p.parse("e.c2 .c1 { key:value }", null));
        pa = (Pattern) css.keySet().iterator().next();
        debug("p3", pa.pattern());
        assertTrue(pa.matcher("/a/e[@class='c0 c1 c2 c3']").matches());
        assertTrue(pa.matcher("/a/e[@class='c1 c2']").matches());
        assertFalse(pa.matcher("/a/e[@class='c11 c2']").matches());
        assertFalse(pa.matcher("/a/e[@class='c1 c21']").matches());
        assertFalse(pa.matcher("/a/de[@class='c1 c2']").matches());
        assertFalse(pa.matcher("/a/e[@class='c1 c2']/f").matches());

        assertNotNull(css = p.parse(".c2.c1 { key:value }", null));
        assertNotNull(css = p.parse(".c2 .c1 { key:value }", null));
        try {
            // FIXME invert this test:
            assertNull(css = p.parse("f.c2 g.c1{ key:value }", null));
        }
        catch (final ParseException e) {
            assertEquals(5, e.getErrorOffset());
        }
        try {
            // FIXME invert this test:
            assertNull(css = p.parse("parent child { key:value }", null));
        }
        catch (final ParseException e) {
            assertEquals(7, e.getErrorOffset());
        }
        try {
            assertNull(p.parse("parent > child { key:value }", null));
        }
        catch (final ParseException e) {
            assertEquals(7, e.getErrorOffset());
        }
        try {
            assertNull(p.parse("parent + child { key:value }", null));
        }
        catch (final ParseException e) {
            assertEquals(7, e.getErrorOffset());
        }
        try {
            assertNull(p.parse("bla", null));
        }
        catch (final ParseException e) {
            assertEquals(3, e.getErrorOffset());
        }
    }
}
