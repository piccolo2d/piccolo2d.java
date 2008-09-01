package org.piccolo2d.svg.cssmini;

import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.piccolo2d.svg.cssmini.CSSSelector.Builder;

public class CSSSelectorTest extends TestCase {

    static void assertMatch(final CharSequence str, final Pattern pat) {
        assertTrue("[" + str + "] doesn't match [" + pat.pattern() + "]", pat.matcher(str).matches());
    }

    static void assertNoMatch(final CharSequence str, final Pattern pat) {
        assertFalse("[" + str + "] does match [" + pat.pattern() + "]", pat.matcher(str).matches());
    }

    public void testCSSSelectorBuilderXPath() {
        final Builder b = new CSSSelectorBuilderXPath();
        b.startElement("circle");
        b.endElement();
        CSSSelector s = b.finish();
        assertEquals("circle", s.getSelectorText());
        assertTrue(s.matchesSelector("svg > g > circle"));
        assertTrue(s.matchesSelector("svg > g .c1 > circle .c2 .c3"));
        assertFalse(s.matchesSelector("svg > e1 > circle e2"));

        assertTrue(s.matchesXPath("/svg/g/circle"));
        // FIXME assertMatch("/svg/g[@class='c1']/circle[@class='c2 c3']",
        // s.getXPathPattern());
        assertFalse(s.matchesXPath("/svg/e1/circle/e2"));

        assertEquals("//circle", s.getXPathText());

        b.startElement("circle");
        b.addClass("c2");
        b.addClass("c1");
        b.endElement();
        s = b.finish();
        assertEquals("circle .c1 .c2", s.getSelectorText());
        assertTrue(s.matchesSelector("svg > g > circle .c1 .c2"));
        assertTrue(s.matchesSelector("svg > g .c1 > circle .c1 .a .c2 .b"));
        assertFalse(s.matchesSelector("svg > e1 > circle .c1 .c2 > e2"));

        assertTrue(s.matchesXPath("/svg/g/circle[@class='c1 c2']"));
        // FIXME assertMatch("/svg/g[@class='c1']/circle[@class='c1 a c2 b']",
        // s.getSelectorpPattern());
        assertFalse(s.matchesXPath("/svg/e1/circle[@class='c1 c2']/e2"));

        assertEquals("//circle[contains('c1', @class) and contains('c2', @class)]", s.getXPathText());

        b.startElement("g");
        b.addClass("c2");
        b.addClass("c1");
        b.endElement();
        b.combinator('>');
        b.startElement("circle");
        b.addClass("c5");
        b.addClass("c4");
        b.addClass("c3");
        b.endElement();
        s = b.finish();
        assertEquals("g .c1 .c2 > circle .c3 .c4 .c5", s.getSelectorText());
        assertEquals(
                "//g[contains('c1', @class) and contains('c2', @class)]/circle[contains('c3', @class) and contains('c4', @class) and contains('c5', @class)]",
                s.getXPathText());
    }

    public void testSimple() {
        final Builder b = new CSSSelectorBuilderXPath();
        b.startElement("a");
        b.endElement();
        final CSSSelector s = b.finish();

        assertTrue(s.matchesXPath("/c/b/a[@class='c0 c1 c2']"));
    }
}
