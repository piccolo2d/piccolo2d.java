package edu.umd.cs.piccolox.handles;

import junit.framework.TestCase;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.util.PLocator;

public class PHandleTest extends TestCase {
    public void testCloneWorksAsExpected() {
        PHandle handle = new PHandle(new OriginLocator());

        PHandle cloned = (PHandle) handle.clone();
        assertNull(cloned);
    }

    public void testDragHandlerIsNotNull() {
        PHandle handle = new PHandle(new OriginLocator());
        assertNotNull(handle.getHandleDraggerHandler());
    }

    public void testLocatorIsSameAsPassedToConstructor() {
        PLocator locator = new OriginLocator();
        PHandle handle = new PHandle(locator);
        assertSame(locator, handle.getLocator());
    }

    public void testChangingLocatorWorks() {
        PLocator locator = new OriginLocator();
        PLocator locator2 = new OriginLocator();
        PHandle handle = new PHandle(locator);
        handle.setLocator(locator2);
        assertSame(locator2, handle.getLocator());
    }

    public void testChangingParentCausesRelocateHandle() {
        final int[] relocateCounts = new int[1];
        PHandle handle = new PHandle(new OriginLocator()) {
            public void relocateHandle() {
                super.relocateHandle();
                relocateCounts[0]++;
            }
        };
        relocateCounts[0] = 0;
        PNode parent = new PNode();
        handle.setParent(parent);
        assertEquals(1, relocateCounts[0]);
    }
    
    public void testResizingParentCausesRelocateHandle() {
        final int[] relocateCounts = new int[1];
        PHandle handle = new PHandle(new OriginLocator()) {
            public void relocateHandle() {
                super.relocateHandle();
                relocateCounts[0]++;
            }
        };        
        PNode parent = new PNode();
        parent.addChild(handle);
        relocateCounts[0] = 0;
        parent.setBounds(0, 0, 100, 100);
        assertEquals(1, relocateCounts[0]);
    }

    private final class OriginLocator extends PLocator {
        public double locateX() {
            return 0;
        }

        public double locateY() {
            return 0;
        }
    }
}
