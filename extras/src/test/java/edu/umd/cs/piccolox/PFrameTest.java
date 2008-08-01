package edu.umd.cs.piccolox;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

public class PFrameTest extends TestCase {
    private static final int TEST_WIDTH = 500;
    private static final int TEST_HEIGHT = 300;

    public PFrameTest(String name) {
        super(name);
    }

    public void testComponentResized() throws InvocationTargetException, InterruptedException {
        final PFrame frame = new PFrame();
        frame.setBounds(0, 0, TEST_WIDTH, TEST_HEIGHT);
        EventQueue.invokeAndWait(new Runnable() {
            public void run() {
                // clear the event queue
            }
        });
        Rectangle bounds = frame.getCanvas().getBounds();
        assertEquals("Canvas width should match width of content pane", frame.getContentPane().getWidth(), bounds.width);
        assertEquals("Canvas height should match height of content pane", frame.getContentPane().getHeight(),
                bounds.height);
    }
}
