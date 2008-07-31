package edu.umd.cs.piccolo;

import java.util.Iterator;

import junit.framework.TestCase;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

public class SerializationTest extends TestCase {

    public SerializationTest(String name) {
        super(name);
    }

    public void test() {
        PNode l = new PLayer();

        for (int i = 0; i < 100; i++) {
            l.addChild(new PNode());
            l.addChild(new PText("Hello World"));
            l.addChild(new PPath());
        }

        l = (PNode) l.clone(); // copy uses serialization internally
        assertTrue(l.getChildrenCount() == 300);

        Iterator i = l.getChildrenIterator();
        while (i.hasNext()) {
            PNode each = (PNode) i.next();
            assertEquals(l, each.getParent());
        }
    }
}
