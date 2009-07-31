package edu.umd.cs.piccolo;

import java.awt.Cursor;

import junit.framework.TestCase;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

public class PCanvasTest extends TestCase {
    private PCanvas canvas;
    private MockPInputEventListener mockListener;

    public void setUp() {
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
        final PInputEventListener[] listeners = canvas.getInputEventListeners();
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
        final MockPropertyChangeListener mockListener = new MockPropertyChangeListener();
        canvas.addPropertyChangeListener(PCanvas.INTERACTING_CHANGED_NOTIFICATION, mockListener);
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
        final Cursor moveCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
        final Cursor handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        final Cursor crosshairCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);

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
        catch (final IndexOutOfBoundsException e) {
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
        final PInputEventListener[] listeners = canvas.getInputEventListeners();
        assertNotNull(listeners);
        assertEquals(3, listeners.length); // zoom + pan + mockListener
        // by default
    }

    public void testRemoveInputEventListenersIsHonoured() {
        canvas.addInputEventListener(mockListener);
        canvas.removeInputEventListener(mockListener);
        final PInputEventListener[] listeners = canvas.getInputEventListeners();
        assertNotNull(listeners);
        assertEquals(2, listeners.length); // zoom + pan + mockListener
    }
}
