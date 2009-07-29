package org.piccolo2d.svg;

import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.io.IOException;

import junit.framework.TestCase;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

public class XmlPathTest extends TestCase {

    private static void assertRectangle(final PNode c) {
        final float[] xy = new float[6];
        assertEquals(PPath.class.getName(), c.getClass().getName());
        assertEquals(0, c.getChildrenCount());
        final GeneralPath s = ((PPath) c).getPathReference();
        final PathIterator pi = s.getPathIterator(null);
        assertEquals(PathIterator.SEG_MOVETO, pi.currentSegment(xy));
        pi.next();
        assertEquals(PathIterator.SEG_LINETO, pi.currentSegment(xy));
        pi.next();
        assertEquals(PathIterator.SEG_LINETO, pi.currentSegment(xy));
        pi.next();
        assertEquals(PathIterator.SEG_LINETO, pi.currentSegment(xy));
        pi.next();
        assertEquals(PathIterator.SEG_LINETO, pi.currentSegment(xy));
        pi.next();
        assertEquals(PathIterator.SEG_CLOSE, pi.currentSegment(xy));
        pi.next();
        assertTrue(pi.isDone());
    }

    public void testCharacterOrder() {
        assertTrue('m' > 'M');
    }

    public void testTriangle() throws IOException {
        final float[] xy = new float[6];
        final PNode p = new SvgLoader().load(XmlPathTest.class.getResourceAsStream("/w3c-svg/path/triangle01.svg"));
        assertEquals(PNode.class, p.getClass());
        assertEquals(2, p.getChildrenCount());

        // the bounding rectangle:
        assertRectangle(p.getChild(0));
        {
            // the triangle:
            final PNode c = p.getChild(1);
            assertEquals(PPath.class.getName(), c.getClass().getName());
            assertEquals(0, c.getChildrenCount());
            final GeneralPath s = ((PPath) c).getPathReference();
            final PathIterator pi = s.getPathIterator(null);
            assertEquals(PathIterator.SEG_MOVETO, pi.currentSegment(xy));
            pi.next();
            assertEquals(PathIterator.SEG_LINETO, pi.currentSegment(xy));
            pi.next();
            assertEquals(PathIterator.SEG_LINETO, pi.currentSegment(xy));
            pi.next();
            assertEquals(PathIterator.SEG_CLOSE, pi.currentSegment(xy));
            pi.next();
            assertTrue(pi.isDone());
        }
    }
}
