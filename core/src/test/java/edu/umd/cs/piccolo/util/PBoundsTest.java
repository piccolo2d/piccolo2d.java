package edu.umd.cs.piccolo.util;

import java.awt.geom.Rectangle2D;

import junit.framework.TestCase;
import edu.umd.cs.piccolo.PiccoloAsserts;

public class PBoundsTest extends TestCase {
    public void testDefaultBoundsAreEmpty() {
        final PBounds b = new PBounds();
        PiccoloAsserts.assertEquals(new PBounds(0, 0, 0, 0), b, 0.0001);
    }

    public void testBoundsCloneConstructorWorks() {
        final PBounds b1 = new PBounds(10, 15, 100, 50);
        final PBounds b2 = new PBounds(b1);
        PiccoloAsserts.assertEquals(b1, b2, 0.00001);
    }

    public void testBoundsCanBeConstructedFromRectangle2D() {
        final Rectangle2D r = new Rectangle2D.Double(1, 2, 3, 4);
        final PBounds b = new PBounds(r);
        PiccoloAsserts.assertEquals(new PBounds(1, 2, 3, 4), b, 0.000001);
    }

    /*
     * public void testBoundsCreatedFromPointAndWidthIsCorrect() { PBounds b =
     * new PBounds(new Point2D.Double(), 10, 10);
     * PiccoloAsserts.assertEquals(new PBounds(-10, -10, 20, 20), b, 0.000001);
     * }
     */

    public void testResetToZeroClearsBounds() {
        final PBounds b = new PBounds(1, 2, 3, 4);
        b.resetToZero();
        assertTrue(b.isEmpty());
        PiccoloAsserts.assertEquals(new PBounds(), b, 0.0000001);
    }

    public void testAdding1PointToEmptyYieldsEmpty() {
        final PBounds b = new PBounds();
        b.add(-10, -10);
        PiccoloAsserts.assertEquals(new PDimension(0, 0), b.getSize(), 0.00001);
    }

    public void testAdding2PointsToEmptyYieldsNotEmpty() {
        final PBounds b = new PBounds();
        b.add(-10, -10);
        b.add(0, 0);
        PiccoloAsserts.assertEquals(new PDimension(10, 10), b.getSize(), 0.00001);
    }
}
