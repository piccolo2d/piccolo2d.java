package org.piccolo2d.svg.cssmini;

import java.text.ParseException;

import junit.framework.TestCase;

public class CssSelectorToXPathTest extends TestCase {

    public void testOk() throws ParseException {
        final StringBuffer s = new StringBuffer();

        final CssSelectorToXPath c = new CssSelectorToXPath();
        assertEquals("/root/e1[@class='c1 c2']/e2", c.parse("root > e1 .c2 .c1 > e2"));
        assertEquals("/root//e1[@class='c1 c2']//e2", c.parse("root e1 .c2 .c1 e2"));
    }
}
