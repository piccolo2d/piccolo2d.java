package org.piccolo2d.svg.cssmini;

import java.text.ParseException;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

public class CssParserTest extends TestCase {

    private static void asser(final String expected, final Collection found) {
        final CSSStyleRule r = (CSSStyleRule) found.iterator().next();
        assertEquals(expected, r.getSelector().toString());
    }

    public void test01ParseUnnestedSelector() throws ParseException {
        final CssParser cp = new CssParser();
        asser("el", cp.parse("el{ fill: #ff3131; stroke: none } "));
        asser("el", cp.parse(" el{ fill: #ff3131; stroke: none } "));
        asser("el", cp.parse("el { fill: #ff3131; stroke: none } "));
        asser("el", cp.parse(" el { fill: #ff3131; stroke: none } "));

        asser("* .c1", cp.parse(".c1{ fill: #ff3131; stroke: none } "));
        asser("* .c1", cp.parse(" .c1{ fill: #ff3131; stroke: none } "));
        asser("* .c1", cp.parse(".c1 { fill: #ff3131; stroke: none } "));
        asser("* .c1", cp.parse(" .c1 { fill: #ff3131; stroke: none } "));

        asser("* .c1 .c2", cp.parse(".c1.c2{ fill: #ff3131; stroke: none } "));
        asser("* .c1 .c2", cp.parse(" .c1.c2{ fill: #ff3131; stroke: none } "));
        asser("* .c1 .c2", cp.parse(".c1.c2 { fill: #ff3131; stroke: none } "));
        asser("* .c1 .c2", cp.parse(" .c1.c2 { fill: #ff3131; stroke: none } "));
        asser("* .c1 .c2", cp.parse(".c1 .c2{ fill: #ff3131; stroke: none } "));
        asser("* .c1 .c2", cp.parse(" .c1 .c2{ fill: #ff3131; stroke: none } "));
        asser("* .c1 .c2", cp.parse(".c1 .c2 { fill: #ff3131; stroke: none } "));
        asser("* .c1 .c2", cp.parse(" .c1 .c2 { fill: #ff3131; stroke: none } "));

        asser("el .c1 .c2", cp.parse("el.c1.c2{ fill: #ff3131; stroke: none } "));
        asser("el .c1 .c2", cp.parse(" el.c1.c2{ fill: #ff3131; stroke: none } "));
        asser("el .c1 .c2", cp.parse("el.c1.c2 { fill: #ff3131; stroke: none } "));
        asser("el .c1 .c2", cp.parse("el.c1.c2{ fill: #ff3131; stroke: none } "));
        asser("el .c1 .c2", cp.parse("el .c1.c2{ fill: #ff3131; stroke: none } "));
        asser("el .c1 .c2", cp.parse(" el .c1.c2{ fill: #ff3131; stroke: none } "));
        asser("el .c1 .c2", cp.parse("el .c1.c2 { fill: #ff3131; stroke: none } "));
        asser("el .c1 .c2", cp.parse("el .c1.c2{ fill: #ff3131; stroke: none } "));
        asser("el .c1 .c2", cp.parse("el.c1 .c2{ fill: #ff3131; stroke: none } "));
        asser("el .c1 .c2", cp.parse(" el.c1 .c2{ fill: #ff3131; stroke: none } "));
        asser("el .c1 .c2", cp.parse("el.c1 .c2 { fill: #ff3131; stroke: none } "));
        asser("el .c1 .c2", cp.parse("el.c1 .c2{ fill: #ff3131; stroke: none } "));
    }

    public void test02ParseNestedSelector() throws ParseException {
        final CssParser cp = new CssParser();
        Collection css = null;
        asser("e1 e2", cp.parse("e1 e2{ fill: #ff3131; stroke: none } "));
        asser("e1 e2", cp.parse(" e1 e2{ fill: #ff3131; stroke: none } "));
        asser("e1 e2", cp.parse("e1 e2 { fill: #ff3131; stroke: none } "));

        asser("e1 e2", cp.parse("e1  e2{ fill: #ff3131; stroke: none } "));
        asser("e1 e2", cp.parse(" e1  e2{ fill: #ff3131; stroke: none } "));
        asser("e1 e2", cp.parse("e1  e2 { fill: #ff3131; stroke: none } "));

        asser("e1 .c1 e2 .c20 .c21 e3 .c30 .c31", cp
                .parse("e1.c1 e2.c20.c21 e3.c30.c31{ fill: #ff3131; stroke: none } "));
        asser("e1 .c1 e2 .c20 .c21 e3 .c30 .c31", cp
                .parse(" e1 .c1 e2 .c20 .c21 e3 .c30 .c31 { fill: #ff3131; stroke: none } "));

        asser("e1 > e2", cp.parse("e1>e2{ fill: #ff3131; stroke: none } "));
        asser("e1 > e2", cp.parse(" e1>e2{ fill: #ff3131; stroke: none } "));
        asser("e1 > e2", cp.parse("e1>e2 { fill: #ff3131; stroke: none } "));

        asser("e1 > e2", cp.parse("e1 >e2{ fill: #ff3131; stroke: none } "));
        asser("e1 > e2", cp.parse(" e1 >e2{ fill: #ff3131; stroke: none } "));
        asser("e1 > e2", cp.parse("e1 >e2 { fill: #ff3131; stroke: none } "));

        asser("e1 > e2", cp.parse("e1> e2{ fill: #ff3131; stroke: none } "));
        asser("e1 > e2", cp.parse(" e1> e2{ fill: #ff3131; stroke: none } "));
        asser("e1 > e2", cp.parse("e1> e2 { fill: #ff3131; stroke: none } "));

        asser("e1 > e2", cp.parse("e1 > e2{ fill: #ff3131; stroke: none } "));
        asser("e1 > e2", cp.parse(" e1 > e2{ fill: #ff3131; stroke: none } "));
        asser("e1 > e2", cp.parse("e1 > e2 { fill: #ff3131; stroke: none } "));

        asser("e1 .c1 > e2 .c20 .c21 > e3 .c30 .c31", cp
                .parse("e1.c1>e2.c20.c21>e3.c30.c31{ fill: #ff3131; stroke: none } "));
        asser("e1 .c1 > e2 .c20 .c21 > e3 .c30 .c31", cp
                .parse(" e1 .c1 > e2 .c20 .c21 > e3 .c30 .c31 { fill: #ff3131; stroke: none } "));

        css = cp.parse("\n      g  >  h  .c1  , i  >  j  .c2  { stroke: none; fill: #ff3131;  }\n    ");
        assertEquals(2, css.size());
        final Iterator it = css.iterator();
        assertEquals("g > h .c1 {\n\tfill: #ff3131;\n\tstroke: none\n}", it.next().toString());
        assertEquals("i > j .c2 {\n\tfill: #ff3131;\n\tstroke: none\n}", it.next().toString());
        assertFalse(it.hasNext());
    }
}