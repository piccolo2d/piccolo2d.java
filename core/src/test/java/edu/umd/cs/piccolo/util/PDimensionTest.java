package edu.umd.cs.piccolo.util;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import junit.framework.TestCase;

public class PDimensionTest extends TestCase {
    public void testDefaultConstructorResultsInEmptyDimension() {
        PDimension dimension = new PDimension();
        
        assertEquals(0, dimension.getWidth(), 0.00001);
        assertEquals(0, dimension.getHeight(), 0.00001);
    }
    
    public void testCloningConstructorDoesSo() {
        Dimension2D src = new Dimension(100, 50);
        PDimension copy = new PDimension(src);

        assertEquals(100, copy.getWidth(), 0.00001);
        assertEquals(50, copy.getHeight(), 0.00001);
    }
    
    public void testDimensionGetBuiltFromPoints() {
        PDimension dimension = new PDimension(new Point2D.Double(-50, -25), new Point2D.Double(50, 25));
        assertEquals(100, dimension.getWidth(), 0.00001);
        assertEquals(50, dimension.getHeight(), 0.00001);
    }
}
