package edu.umd.cs.piccolox.nodes;

import junit.framework.TestCase;

public class PCompositeTest extends TestCase {
    public void testClone() {
        PComposite composite = new PComposite();           
        PComposite cloned = (PComposite) composite.clone();
        assertNotNull(cloned);      
    }
}
