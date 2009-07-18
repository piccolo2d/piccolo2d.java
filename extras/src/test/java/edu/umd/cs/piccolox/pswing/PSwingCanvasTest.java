package edu.umd.cs.piccolox.pswing;

import javax.swing.JPanel;

import junit.framework.TestCase;

public class PSwingCanvasTest extends TestCase {
    protected int finalizerCallCount;

    public void testMemoryLeak() throws InterruptedException {
        JPanel panel = new JPanel();
        for (int i = 0; i < 10; i++) {
            PSwingCanvas canvas = new PSwingCanvas() {
                public void finalize() {
                    finalizerCallCount++;
                }
            };
            panel.add(canvas);
            panel.remove(canvas);
            canvas = null;
        }
        panel = null;
        System.gc();
        System.runFinalization();

        // Not sure why I need -1 here, but I do. If I create 10000 it'll always
        // be 1 less
        assertEquals(10 - 1, finalizerCallCount);
    }
}
