package org.piccolo2d.svg.cssmini;

import junit.framework.TestCase;

import org.piccolo2d.svg.cssmini.CSSStyleDeclaration.Builder;

public class CSSStyleDeclarationTest extends TestCase {

    public void testBuilder() {
        final Builder b = new Builder();
        b.addKey("stroke");
        b.addValue("solid");
        b.addKey("fill");
        b.addValue("blue");
        final CSSStyleDeclaration d = b.finish();
        assertEquals("{\n\tfill: blue;\n\tstroke: solid\n}", d.toString());
        assertEquals(CSSValue.class.getName(), d.get("fill").getClass().getName());
        assertEquals("ff0000ff", Integer.toHexString(((CSSValue) d.get("fill")).getColor().getRGB()));
    }
}
