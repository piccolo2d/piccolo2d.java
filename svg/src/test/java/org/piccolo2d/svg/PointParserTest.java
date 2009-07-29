package org.piccolo2d.svg;

import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.text.ParseException;

import junit.framework.TestCase;

public class PointParserTest extends TestCase {

    public void testList() throws ParseException {
        final PointParser pl = new PointParser();
        GeneralPath gp = pl.parse("");
        assertTrue(gp.getPathIterator(null).isDone());

        try {
            gp = pl.parse("850,75  958,137.5 958,262.5\n" + "                    850,325 742,262.6 742");
        }
        catch (final ParseException e) {
            assertEquals(69, e.getErrorOffset());
        }

        gp = pl.parse("850,75  958,137.5 958,262.5\n" + "                    850,325 742,262.6 742,137.5");
        final PathIterator pi = gp.getPathIterator(null);
        final float[] coords = new float[6];
        assertEquals(PathIterator.SEG_MOVETO, pi.currentSegment(coords));
        pi.next();
        assertEquals(PathIterator.SEG_LINETO, pi.currentSegment(coords));
        pi.next();
        assertEquals(PathIterator.SEG_LINETO, pi.currentSegment(coords));
        pi.next();
        assertEquals(PathIterator.SEG_LINETO, pi.currentSegment(coords));
        pi.next();
        assertEquals(PathIterator.SEG_LINETO, pi.currentSegment(coords));
        pi.next();
        assertEquals(PathIterator.SEG_LINETO, pi.currentSegment(coords));
        pi.next();
        assertTrue(pi.isDone());
    }
}
