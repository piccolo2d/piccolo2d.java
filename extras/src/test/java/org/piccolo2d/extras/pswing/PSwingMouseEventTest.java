package org.piccolo2d.extras.pswing;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;

import junit.framework.TestCase;

import org.piccolo2d.event.PInputEvent;

public class PSwingMouseEventTest extends TestCase {

    public void testCreateMouseEventDoesNotAcceptNullPInputEvent() {
        try {
            final MouseEvent mouseEvent = new MouseEvent(null, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0,
                    0, 0, 0, false);
            PSwingMouseEvent.createMouseEvent(0, mouseEvent, null);
            fail("Null PInputEvent should throw an exception");
        }
        catch (final IllegalArgumentException iae) {
            // expected exception
        }
    }

    public void testCreateMouseEventDoesNotAcceptNullMouseEvent() {
        try {
            final PInputEvent event = new PInputEvent(null, null);
            PSwingMouseEvent.createMouseEvent(0, null, event);
            fail("Null MouseEvent should throw an exception");
        }
        catch (final NullPointerException iae) {
            // expected exception
        }
    }

    public void testCreateMouseEventReturnsValidMouseEventWhenParamsAreGood() {
        final JComponent src = new JPanel();
        final PInputEvent piccoloEvent = new PInputEvent(null, null);
        final MouseEvent mouseEvent = new MouseEvent(src, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, 0, 0,
                0, false);
        final PSwingEvent swingEvent = PSwingMouseEvent.createMouseEvent(0, mouseEvent, piccoloEvent);
        assertNotNull(swingEvent);
    }

    public void testCreateMouseEventReturnsPSwingMouseEventWhenGivenGenericID() {
        final JComponent src = new JPanel();
        final PInputEvent piccoloEvent = new PInputEvent(null, null);
        final MouseEvent mouseEvent = new MouseEvent(src, 0, System.currentTimeMillis(), 0, 0, 0, 0, false);
        final PSwingEvent swingEvent = PSwingMouseEvent.createMouseEvent(0, mouseEvent, piccoloEvent);
        assertTrue(swingEvent instanceof PSwingMouseEvent);
    }

    public void testCreateMouseEventReturnsPSwingMouseMotionEventWhenGivenMotionID() {
        final JComponent src = new JPanel();
        final PInputEvent piccoloEvent = new PInputEvent(null, null);
        final MouseEvent mouseEvent = new MouseEvent(src, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, 0, 0,
                0, false);
        final PSwingEvent swingEvent = PSwingMouseEvent.createMouseEvent(MouseEvent.MOUSE_MOVED, mouseEvent,
                piccoloEvent);
        assertTrue(swingEvent instanceof PSwingMouseMotionEvent);
    }

    public void testCreateMouseEventReturnsPSwingMouseWheelEventWhenGivenWheelID() {
        final JComponent src = new JPanel();
        final PInputEvent piccoloEvent = new PInputEvent(null, null);
        final MouseWheelEvent mouseEvent = new MouseWheelEvent(src, MouseEvent.MOUSE_WHEEL, System.currentTimeMillis(),
                0, 0, 0, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 10, 1);
        final PSwingEvent swingEvent = PSwingMouseEvent.createMouseEvent(MouseEvent.MOUSE_WHEEL, mouseEvent,
                piccoloEvent);
        assertTrue(swingEvent instanceof PSwingMouseWheelEvent);
    }

}
