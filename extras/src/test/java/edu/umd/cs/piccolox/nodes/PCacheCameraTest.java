package edu.umd.cs.piccolox.nodes;

import junit.framework.TestCase;

public class PCacheCameraTest extends TestCase {
    public void testClone() {
        PCacheCamera camera = new PCacheCamera();        
        PCacheCamera cloned = (PCacheCamera) camera.clone();
        assertNotNull(cloned);      
    }
}
