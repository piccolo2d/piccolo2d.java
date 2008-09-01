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
import java.util.Map;

import junit.framework.TestCase;

public class CssManagerTest extends TestCase {

    public void _testOk() throws ParseException {
        final CssManager cm = new CssManagerImpl();
        cm.loadStyleSheet("* {elem:none;class:none}\n" + "a {elem:a}\n" + ".c1 {class:c1}");

        Map m = cm.findStyleByXPath("/c/b", null);
        // for (final Iterator it = m.entrySet().iterator(); it.hasNext();) {
        // final Entry et = (Entry) it.next();
        // System.out.println(et.getKey() + "=" + et.getValue());
        // }
        assertEquals(2, m.size());
        assertEquals("none", m.get("class"));
        assertEquals("none", m.get("elem"));

        m = cm.findStyleByXPath("/c/b[@class='c0 c1 c2']/a", null);
        assertEquals(2, m.size());
        assertEquals("none", m.get("class"));
        assertEquals("a", m.get("elem"));

        m = cm.findStyleByXPath("/c/a/b[@class='c0 c1 c2']", null);
        assertEquals(2, m.size());
        assertEquals("c1", m.get("class"));
        assertEquals("none", m.get("elem"));

        m = cm.findStyleByXPath("/c/b/a[@class='c0 c1 c2']", null);
        assertEquals(2, m.size());
        assertEquals("c1", m.get("class"));
        assertEquals("a", m.get("elem"));

        m = cm.findStyleByXPath("/c/b/a[@class='c0 c1 c2']", "class:c2");
        assertEquals(2, m.size());
        assertEquals("c2", m.get("class"));
        assertEquals("a", m.get("elem"));
    }

    public void testFindStyleByCSSSelector() throws ParseException {
        final CssManagerImpl cm = new CssManagerImpl();
        cm.loadStyleSheet("* {elem:none;class:none}\n" + "a {elem:a}\n" + ".c1 {class:c1}");
        assertEquals(3, cm.ruleCount());

        Map m = cm.findStyleByCSSSelector("c > b", null);
        assertEquals(2, m.size());
        assertEquals(CSSValue.class.getName(), m.get("class").getClass().getName());
        assertEquals("none", m.get("class").toString());
        assertEquals("none", m.get("elem").toString());

        m = cm.findStyleByCSSSelector("c > b .c0 .c1 .c2 > a", null);
        assertEquals(2, m.size());
        assertEquals("none", m.get("class").toString());
        assertEquals("a", m.get("elem").toString());

        m = cm.findStyleByCSSSelector("c > a > b .c0 .c1 .c2", null);
        assertEquals(2, m.size());
        assertEquals("c1", m.get("class").toString());
        assertEquals("none", m.get("elem").toString());

        m = cm.findStyleByCSSSelector("c > b > a .c0 .c1 .c2", null);
        assertEquals(2, m.size());
        assertEquals("c1", m.get("class").toString());
        assertEquals("a", m.get("elem").toString());

        m = cm.findStyleByCSSSelector("c > b > a .c0 .c1 .c2", "class:c2");
        assertEquals(2, m.size());
        assertEquals("c2", m.get("class").toString());
        assertEquals("a", m.get("elem").toString());

    }
}
