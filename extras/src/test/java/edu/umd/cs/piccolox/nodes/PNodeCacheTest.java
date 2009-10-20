package edu.umd.cs.piccolox.nodes;

import junit.framework.TestCase;

public class PNodeCacheTest extends TestCase {
    public void testClone() {
        PNodeCache line = new PNodeCache();       
        PNodeCache cloned = (PNodeCache) line.clone();
        assertNotNull(cloned);                   
    }
}
