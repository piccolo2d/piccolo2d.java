package edu.umd.cs.piccolox.swt;

import junit.framework.TestCase;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.event.PPanEventHandler;
import edu.umd.cs.piccolo.event.PZoomEventHandler;

public class PSWTCanvasTest extends TestCase {
    private PSWTCanvas canvas;

    public void setUp() {        
        final Shell shell = new Shell(Display.getDefault());
        shell.setLayout(new FillLayout());      
        canvas = new PSWTCanvas(shell, 0);
    }

    public void testPanEventListenerIsInstalledByDefault() {
        PPanEventHandler handler = canvas.getPanEventHandler();
        assertNotNull(handler);

        int handlerIndex = getHandlerIndex(handler);
        assertFalse("Pan Event Handler not installed", handlerIndex == -1);
    }

    public void testZoomEventListenerIsInstalledByDefault() {
        PZoomEventHandler handler = canvas.getZoomEventHandler();
        assertNotNull(handler);

        int handlerIndex = getHandlerIndex(handler);
        assertFalse("Zoom Event Handler not installed", handlerIndex == -1);
    }

    private int getHandlerIndex(PInputEventListener handler) {
        PInputEventListener[] listeners = canvas.getCamera().getInputEventListeners();
        int handlerIndex = -1;
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] == handler) {
                handlerIndex = i;
            }
        }
        return handlerIndex;
    }

    public void testAnimatingDefaultsToFalse() {
        assertFalse(canvas.getAnimating());
    }

    public void testInteractingDefaultsToFalse() {
        assertFalse(canvas.getInteracting());
    }

    public void testInteractingWorksByCountingCallsToSetInteracting() {
        canvas.setInteracting(true);
        assertTrue(canvas.getInteracting());

        canvas.setInteracting(true);
        assertTrue(canvas.getInteracting());

        canvas.setInteracting(false);
        // This is terrible
        assertTrue(canvas.getInteracting());

        canvas.setInteracting(false);
        assertFalse(canvas.getInteracting());
    }

    public void testCanvasIsDoubleBufferedByDefault() {
        assertTrue(canvas.getDoubleBuffered());
    }

    public void testDoubleBufferingPersists() {
        canvas.setDoubleBuffered(false);
        assertFalse(canvas.getDoubleBuffered());
        canvas.setDoubleBuffered(true);
        assertTrue(canvas.getDoubleBuffered());
    }
}
