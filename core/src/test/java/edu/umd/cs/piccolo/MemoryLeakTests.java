package edu.umd.cs.piccolo;

import javax.swing.JPanel;

import junit.framework.TestCase;
import edu.umd.cs.piccolo.PCanvas;

public class MemoryLeakTests extends TestCase {
    private int pCanvasFinalizerCount;
    
    public void setUp() {
        pCanvasFinalizerCount = 0;
    }
    
    public void testMemoryLeakWithPCanvas() throws InterruptedException {                  
        JPanel panel = new JPanel();       
        for (int i=0; i < 10; i++) {            
            PCanvas canvas = new PCanvas() {
                public void finalize() {
                    pCanvasFinalizerCount ++;                    
                }
            };
            panel.add(canvas);
            panel.remove(canvas);   
            canvas = null;
        }
        System.gc();
        System.runFinalization();
        
        // Not sure why I need -1 here, but I do. If I create 10000 it'll always be 1 less
        assertEquals(10-1, pCanvasFinalizerCount);                  
    }
}
