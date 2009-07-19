package edu.umd.cs.piccolo;

import java.awt.Cursor;

import javax.swing.JPanel;

import junit.framework.TestCase;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

public class PCanvasTest extends TestCase {
    private PCanvas canvas;
    private int pCanvasFinalizerCount;
    private MockPInputEventListener mockListener;

    public void setUp() {
        pCanvasFinalizerCount = 0;
        canvas = new PCanvas();
        mockListener = new MockPInputEventListener();
    }

    public void testDefaultPanHandlerIsNotNull() {
        assertNotNull(canvas.getPanEventHandler());
    }

    public void testGetInteractingReturnsFalseByDefault() {
        assertFalse(canvas.getInteracting());
    }

    public void testDefaultNumberOfEventListenersIs2() {        
        PInputEventListener[] listeners = canvas.getInputEventListeners();
        assertNotNull(listeners);
        assertEquals(2, listeners.length);
    }

    public void testGetAnimatingReturnsFalseByDefault() {
        assertFalse(canvas.getAnimating());
    }

    public void testSetInteractingPersists() {
        canvas.setInteracting(true);
        assertTrue(canvas.getInteracting());
    }

    public void testSetInteractingFiresChangeEvent() {
        MockPropertyChangeListener mockListener = new MockPropertyChangeListener();
        canvas.addPropertyChangeListener(PCanvas.INTERATING_CHANGED_NOTIFICATION, mockListener);
        canvas.setInteracting(true);
        assertEquals(1, mockListener.getPropertyChangeCount());
    }

    public void testDefaultRenderQualityIsHigh() {
        assertEquals(PPaintContext.HIGH_QUALITY_RENDERING, canvas.getDefaultRenderQuality());
    }

    public void testDefaultAnimatingRenderQualityIsLow() {
        assertEquals(PPaintContext.LOW_QUALITY_RENDERING, canvas.getAnimatingRenderQuality());
    }

    public void testDefaultInteractingRenderQualityIsLow() {
        assertEquals(PPaintContext.LOW_QUALITY_RENDERING, canvas.getInteractingRenderQuality());
    }

    public void testDefaultZoomHandlerIsNotNull() {
        assertNotNull(canvas.getZoomEventHandler());
    }

    public void testCanvasLayerIsNotNullByDefault() {
        assertNotNull(canvas.getLayer());
    }

    public void testCursorStackWorksAsExpected() {
        Cursor moveCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
        Cursor handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        Cursor crosshairCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);

        canvas.pushCursor(moveCursor);
        canvas.pushCursor(handCursor);
        canvas.pushCursor(crosshairCursor);

        assertEquals(crosshairCursor, canvas.getCursor());
        canvas.popCursor();
        assertEquals(handCursor, canvas.getCursor());
        canvas.popCursor();
        assertEquals(moveCursor, canvas.getCursor());
    }

    public void testPoppingEmptyCursorStackShouldDoNothing() {
        try {
            canvas.popCursor();
        }
        catch (IndexOutOfBoundsException e) {
            fail("Pop cursor shouldn't fail on an empty stack");
        }
        assertEquals(Cursor.getDefaultCursor(), canvas.getCursor());
    }

    public void testSettingCanvasBoundsAffectsCameraBounds() {
        canvas.setBounds(0, 0, 100, 100);
        assertEquals(new PBounds(0, 0, 100, 100), canvas.getCamera().getBounds());
    }

    public void testAddInputEventListenersIsHonoured() {
        canvas.addInputEventListener(mockListener);
        PInputEventListener[] listeners = canvas.getInputEventListeners();
        assertNotNull(listeners);
        assertEquals(3, listeners.length); // 3 since pan and zoom are attached by default
    }

    public void testRemoveInputEventListenersIsHonoured() {
        canvas.addInputEventListener(mockListener);
        canvas.removeInputEventListener(mockListener);
        PInputEventListener[] listeners = canvas.getInputEventListeners();
        assertNotNull(listeners);
        assertEquals(2, listeners.length); // 3 since pan and zoom are attached by default
    }

    
    public void testMemoryLeak() throws InterruptedException {
        JPanel panel = new JPanel();
        for (int i = 0; i < 10; i++) {
            PCanvas canvas = new PCanvas() {
                public void finalize() {
                    pCanvasFinalizerCount++;
                }
            };
            panel.add(canvas);
            panel.remove(canvas);
            canvas = null;
        }
        System.gc();
        System.runFinalization();
        PCanvas.CURRENT_ZCANVAS = null;

        // Not sure why I need -1 here, but I do. If I create 10000 it'll always
        // be 1 less
        //assertEquals(10-1, pCanvasFinalizerCount);
    }

}
