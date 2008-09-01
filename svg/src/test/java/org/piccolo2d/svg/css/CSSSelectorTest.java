package org.piccolo2d.svg.css;

import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.piccolo2d.svg.css.CSSSelector.Builder;

public class CSSSelectorTest extends TestCase {

    static void assertMatch(final CharSequence str, final Pattern pat) {
        assertTrue("[" + str + "] doesn't match [" + pat.pattern() + "]", pat.matcher(str).matches());
    }

    static void assertNoMatch(final CharSequence str, final Pattern pat) {
        assertFalse("[" + str + "] does match [" + pat.pattern() + "]", pat.matcher(str).matches());
    }

    public void testBuilder() {
        final Builder b = new Builder();
        b.startElement("circle");
        b.endElement();
        CSSSelector s = b.finish();
        assertEquals("circle", s.getSelectorText());
        assertMatch("svg > g > circle", s.getSelectorpPattern());
        assertMatch("svg > g .c1 > circle .c2 .c3", s.getSelectorpPattern());
        assertNoMatch("svg > e1 > circle e2", s.getSelectorpPattern());

        assertMatch("/svg/g/circle", s.getXPathPattern());
        // FIXME assertMatch("/svg/g[@class='c1']/circle[@class='c2 c3']",
        // s.getXPathPattern());
        assertNoMatch("/svg/e1/circle/e2", s.getXPathPattern());

        assertEquals("//circle", s.getXPathText());
        assertEquals("^\n" + ".*# <= Element Climber\n" + "/# <= Element Start\n" + "circle# Element End\n" + "$", s
                .getXPathPattern().pattern());

        b.startElement("circle");
        b.addClass("c2");
        b.addClass("c1");
        b.endElement();
        s = b.finish();
        assertEquals("circle .c1 .c2", s.getSelectorText());
        assertMatch("svg > g > circle .c1 .c2", s.getSelectorpPattern());
        assertMatch("svg > g .c1 > circle .c1 .a .c2 .b", s.getSelectorpPattern());
        assertNoMatch("svg > e1 > circle .c1 .c2 > e2", s.getSelectorpPattern());

        assertMatch("/svg/g/circle[@class='c1 c2']", s.getXPathPattern());
        // FIXME assertMatch("/svg/g[@class='c1']/circle[@class='c1 a c2 b']",
        // s.getSelectorpPattern());
        assertNoMatch("/svg/e1/circle[@class='c1 c2']/e2", s.getSelectorpPattern());

        assertEquals("//circle[contains('c1', @class) and contains('c2', @class)]", s.getXPathText());
        assertEquals("^\n" + ".*# <= Element Climber\n" + "/# <= Element Start\n"
                + "circle\\[@class=\'(?:[^\']+\\s)?\\s*# <= class Attribute Start \n"
                + "c1(?:\\s[^\']+)?\\s*# <= inter-Attribute separator\n" + "c2(?:\\s[^\']+)?\'\\]# <= Attribute end\n"
                + "# Element End\n" + "$", s.getXPathPattern().pattern());

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
        assertEquals("^\n" + ".*# <= Element Climber\n" + "/# <= Element Start\n"
                + "g\\[@class=\'(?:[^\']+\\s)?\\s*# <= class Attribute Start \n"
                + "c1(?:\\s[^\']+)?\\s*# <= inter-Attribute separator\n" + "c2(?:\\s[^\']+)?\'\\]# <= Attribute end\n"
                + "# Element End\n" + "/# <= Element Start\n"
                + "circle\\[@class=\'(?:[^\']+\\s)?\\s*# <= class Attribute Start \n"
                + "c3(?:\\s[^\']+)?\\s*# <= inter-Attribute separator\n"
                + "c4(?:\\s[^\']+)?\\s*# <= inter-Attribute separator\n" + "c5(?:\\s[^\']+)?\'\\]# <= Attribute end\n"
                + "# Element End\n" + "$", s.getXPathPattern().pattern());
    }
}
