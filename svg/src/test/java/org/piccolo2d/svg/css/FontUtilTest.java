package org.piccolo2d.svg.css;

import java.awt.Font;

import junit.framework.TestCase;

public class FontUtilTest extends TestCase {

    public void testFindFont() {
        Font f = FontUtil.findFont("Verdana", null, "12");
        assertEquals("Verdana", f.getFamily());
        assertEquals(Font.PLAIN, f.getStyle());
        assertEquals(12 * FontUtil.SCALE, f.getSize());

        f = FontUtil.findFont("Verdana", null, "0.40000001px");
        assertEquals("Verdana", f.getFamily());
        assertEquals(Font.PLAIN, f.getStyle());
        assertEquals(40, f.getSize());
    }
}
