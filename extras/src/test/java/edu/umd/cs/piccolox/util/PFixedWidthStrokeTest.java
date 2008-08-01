package edu.umd.cs.piccolox.util;

import java.awt.BasicStroke;

import junit.framework.TestCase;

public class PFixedWidthStrokeTest extends TestCase {
    public void testContants() {
        assertEquals(PFixedWidthStroke.CAP_BUTT, BasicStroke.CAP_BUTT);
        assertEquals(PFixedWidthStroke.CAP_ROUND, BasicStroke.CAP_ROUND);
        assertEquals(PFixedWidthStroke.CAP_SQUARE, BasicStroke.CAP_SQUARE);
        assertEquals(PFixedWidthStroke.JOIN_BEVEL, BasicStroke.JOIN_BEVEL);
        assertEquals(PFixedWidthStroke.JOIN_MITER, BasicStroke.JOIN_MITER);
        assertEquals(PFixedWidthStroke.JOIN_ROUND, BasicStroke.JOIN_ROUND);
    }
}
