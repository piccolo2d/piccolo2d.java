import edu.umd.cs.piccolox.PFrame;
import junit.framework.TestCase;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

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
        assertTrue("Canvas width should be inset by frame decoration size", bounds.getWidth() < TEST_WIDTH);
        assertTrue("Canvas height should be inset by frame decoration size", bounds.getHeight() < TEST_HEIGHT);
    }
}
