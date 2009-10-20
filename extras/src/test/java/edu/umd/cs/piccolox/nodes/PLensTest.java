package edu.umd.cs.piccolox.nodes;

import junit.framework.TestCase;

public class PLensTest extends TestCase {
    public void testClone() {
        PLens lens = new PLens();           
        assertTrue(lens.getInputEventListeners().length > 0);
        PLens cloned = (PLens) lens.clone();
        assertNotNull(cloned);      

        //assertTrue(cloned.getInputEventListeners().length > 0);        
        //assertNotNull(cloned.getPropertyChangeListeners());
        //assertFalse(cloned.getPropertyChangeListeners().length == 0); 
        //assertNotSame(cloned.getPropertyChangeListeners()[0], lens.getPropertyChangeListeners()[0]);
    }
}
