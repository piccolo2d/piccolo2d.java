package edu.umd.cs.piccolox;

import java.awt.event.KeyListener;

import junit.framework.TestCase;
import edu.umd.cs.piccolo.PCanvas;

public class PFrameTest extends TestCase {
    private PFrame frame;   

    public void testCanvasIsValidWithDefaultConstructor() {
        PFrame frame = new PFrame() {
            public void setVisible(boolean visible) {
                // why oh why is PFrame visible by default
            }
        };
        PCanvas canvas = frame.getCanvas();
        assertNotNull(canvas);
        assertNotNull(canvas.getLayer());
        assertNotNull(canvas.getCamera());
        assertSame(canvas.getLayer(), canvas.getCamera().getLayer(0));
    }

    public void testDefaultsToWindowed() {
        PFrame frame = new PFrame() {
            public void setVisible(boolean visible) {
                // why oh why is PFrame visible by default
            }
        };
        assertFalse(frame.isFullScreenMode());
    }

    public void testFullScreenModeInstallsEscapeListeners() {
        PFrame frame = new PFrame();        
        frame.setFullScreenMode(true);        
        

        KeyListener[] listeners = frame.getCanvas().getKeyListeners();
        assertEquals(1, listeners.length);

        KeyListener listener = listeners[0];
        assertNotNull(listener);
        frame.setVisible(false);
        frame.setFullScreenMode(false);
    }
}
