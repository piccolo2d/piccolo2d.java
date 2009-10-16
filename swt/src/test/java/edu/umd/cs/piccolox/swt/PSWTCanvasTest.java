package edu.umd.cs.piccolox.swt;

import org.eclipse.swt.widgets.Display;

import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.event.PPanEventHandler;
import edu.umd.cs.piccolo.event.PZoomEventHandler;

public class PSWTCanvasTest extends SWTTest {
    private PSWTCanvas canvas;

    public void setUp() {
        if (hasHead()) {
            canvas = buildSimpleCanvas(Display.getDefault());
        }
    }
    
    public void testPanEventListenerIsInstalledByDefault() {
        if (hasHead()) {
            PPanEventHandler handler = canvas.getPanEventHandler();
            assertNotNull(handler);
            
            int handlerIndex = getHandlerIndex(handler);
            assertFalse("Pan Event Handler not installed", handlerIndex == -1);            
        }
    }

    private int getHandlerIndex(PInputEventListener handler) {        
        PInputEventListener[] listeners = canvas.getCamera().getInputEventListeners();
        int handlerIndex = -1;
        for (int i=0; i<listeners.length; i++) {
            if (listeners[i] == handler) {
                handlerIndex = i;
            }
        }
        return handlerIndex;
    }
    
    public void testZoomEventListenerIsInstalledByDefault() {
        if (hasHead()) {
            PZoomEventHandler handler = canvas.getZoomEventHandler();
            assertNotNull(handler);
            
            int handlerIndex = getHandlerIndex(handler);
            assertFalse("Zoom Event Handler not installed", handlerIndex == -1);            
        }
    }
    
    public void testAnimatingDefaultsToFalse() {
        if (hasHead()) {
            assertFalse(canvas.getAnimating());
        }
    }
    
    public void testInteractingDefaultsToFalse() {
        if (hasHead()) {
            assertFalse(canvas.getInteracting());
        }
    }
    
    public void testInteractingWorksByCountingCallsToSetInteracting() {
        if (hasHead()) {
            canvas.setInteracting(true);
            assertTrue(canvas.getInteracting());
            
            canvas.setInteracting(true);
            assertTrue(canvas.getInteracting());
            
            canvas.setInteracting(false);
            //This is terrible
            assertTrue(canvas.getInteracting());
            
            canvas.setInteracting(false);
            assertFalse(canvas.getInteracting());
            
        }
    }
    
    public void testCanvasIsDoubleBufferedByDefault() {
        if (hasHead()) {
            assertTrue(canvas.getDoubleBuffered());
        }
    }
    
    
    public void testDoubleBufferingPersists() {
        if (hasHead()) {
            canvas.setDoubleBuffered(false);
            assertFalse(canvas.getDoubleBuffered());
            canvas.setDoubleBuffered(true);
            assertTrue(canvas.getDoubleBuffered());
        }
    }

}
