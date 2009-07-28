package edu.umd.cs.piccolo.event;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import junit.framework.TestCase;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PiccoloAsserts;

public class PBasicInputEventListenerTest extends TestCase {
    private PBasicInputEventHandler listener;
    private MockPBasicInputEventHandler mockListener;

    public void setUp() {
        listener = new PBasicInputEventHandler();
    }

    public void testSetEventFilterIsPersisted() {
        PInputEventFilter filter = new PInputEventFilter();
        listener.setEventFilter(filter);
        assertSame(filter, listener.getEventFilter());
    }

    public void testAcceptsEventDelegatesToFilter() {
        PInputEventFilter filter = new PInputEventFilter();
        listener.setEventFilter(filter);
        PInputEvent event = buildInputEvent();
        assertTrue(listener.acceptsEvent(event, MouseEvent.MOUSE_CLICKED));
        filter.rejectAllEventTypes();
        assertFalse(listener.acceptsEvent(event, MouseEvent.MOUSE_CLICKED));
    }

    public void testProcessEventDelegatesToSubClassMethodsBasedOnType() {
        PInputEvent event = buildInputEvent();

        mockListener = new MockPBasicInputEventHandler();
        int[] eventTypes = new int[] { KeyEvent.KEY_PRESSED, KeyEvent.KEY_RELEASED, KeyEvent.KEY_TYPED,
                MouseEvent.MOUSE_RELEASED, MouseEvent.MOUSE_CLICKED, MouseEvent.MOUSE_DRAGGED,
                MouseEvent.MOUSE_ENTERED, MouseEvent.MOUSE_EXITED, MouseEvent.MOUSE_MOVED, MouseEvent.MOUSE_PRESSED,
                MouseWheelEvent.WHEEL_UNIT_SCROLL, MouseWheelEvent.WHEEL_BLOCK_SCROLL, FocusEvent.FOCUS_GAINED, FocusEvent.FOCUS_LOST };

        for (int i = 0; i < eventTypes.length; i++) {
            mockListener.processEvent(event, eventTypes[i]);
        }

        PiccoloAsserts.assertEquals(new String[] { "keyPressed", "keyReleased", "keyTyped", "mouseReleased",
                "mouseClicked", "mouseDragged", "mouseEntered", "mouseExited", "mouseMoved", "mousePressed",
                "mouseWheelRotated", "mouseWheelRotatedByBlock", "focusGained", "focusLost" }, mockListener.getMethodCalls());
    }

    private PInputEvent buildInputEvent() {
        PCanvas canvas = new PCanvas();
        MouseEvent mouseEvent = new MouseEvent(canvas, 1, System.currentTimeMillis(), 0, 0, 0, 1, false);
        PInputEvent event = new PInputEvent(canvas.getRoot().getDefaultInputManager(), mouseEvent);
        return event;
    }

}
