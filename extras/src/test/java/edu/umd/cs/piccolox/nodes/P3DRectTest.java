package edu.umd.cs.piccolox.nodes;

import java.awt.Color;

import junit.framework.TestCase;

public class P3DRectTest extends TestCase {
    public void testClone() {
        P3DRect rect = new P3DRect(10, 10, 10, 10);
        rect.setPaint(Color.BLUE);
        P3DRect cloned = (P3DRect) rect.clone();
        assertNotNull(cloned);
        assertEquals(Color.BLUE, cloned.getPaint());       
    }
}
