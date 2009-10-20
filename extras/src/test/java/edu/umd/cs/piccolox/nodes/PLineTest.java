package edu.umd.cs.piccolox.nodes;

import java.awt.Color;

import junit.framework.TestCase;

public class PLineTest extends TestCase {
    public void testClone() {
        PLine line = new PLine();
        line.setStrokePaint(Color.RED);
        PLine cloned = (PLine) line.clone();
        assertNotNull(cloned);         
        assertEquals(Color.RED, cloned.getStrokePaint());
        assertNotSame(line.getLineReference(), cloned.getLineReference());
    }
}
