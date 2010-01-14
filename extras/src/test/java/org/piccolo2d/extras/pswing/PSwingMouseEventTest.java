package org.piccolo2d.extras.pswing;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.extras.pswing.PSwingEvent;
import org.piccolo2d.extras.pswing.PSwingMouseEvent;
import org.piccolo2d.extras.pswing.PSwingMouseMotionEvent;
import org.piccolo2d.extras.pswing.PSwingMouseWheelEvent;

import junit.framework.TestCase;

public class PSwingMouseEventTest extends TestCase {

    public void testCreateMouseEventDoesNotAcceptNullPInputEvent() {
        try {
            MouseEvent mouseEvent = new MouseEvent(null, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, 0, 0,
                    0, false);
            PSwingMouseEvent.createMouseEvent(0, mouseEvent, null);
            fail("Null PInputEvent should throw an exception");
        }
        catch (IllegalArgumentException iae) {
            // expected exception
        }
    }

    public void testCreateMouseEventDoesNotAcceptNullMouseEvent() {
        try {
            PInputEvent event = new PInputEvent(null, null);
            PSwingMouseEvent.createMouseEvent(0, null, event);
            fail("Null MouseEvent should throw an exception");
        }
        catch (NullPointerException iae) {
            // expected exception
        }
    }

    public void testCreateMouseEventReturnsValidMouseEventWhenParamsAreGood() {
        JComponent src = new JPanel();
        PInputEvent piccoloEvent = new PInputEvent(null, null);
        MouseEvent mouseEvent = new MouseEvent(src, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, 0, 0, 0,
                false);
        PSwingEvent swingEvent = PSwingMouseEvent.createMouseEvent(0, mouseEvent, piccoloEvent);
        assertNotNull(swingEvent);
    }

    public void testCreateMouseEventReturnsPSwingMouseEventWhenGivenGenericID() {
        JComponent src = new JPanel();
        PInputEvent piccoloEvent = new PInputEvent(null, null);
        MouseEvent mouseEvent = new MouseEvent(src, 0, System.currentTimeMillis(), 0, 0, 0, 0,
                false);
        PSwingEvent swingEvent = PSwingMouseEvent.createMouseEvent(0, mouseEvent, piccoloEvent);
        assertTrue(swingEvent instanceof PSwingMouseEvent);
    }

    public void testCreateMouseEventReturnsPSwingMouseMotionEventWhenGivenMotionID() {
        JComponent src = new JPanel();
        PInputEvent piccoloEvent = new PInputEvent(null, null);
        MouseEvent mouseEvent = new MouseEvent(src, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, 0, 0, 0,
                false);
        PSwingEvent swingEvent = PSwingMouseEvent.createMouseEvent(MouseEvent.MOUSE_MOVED, mouseEvent, piccoloEvent);
        assertTrue(swingEvent instanceof PSwingMouseMotionEvent);
    }

    public void testCreateMouseEventReturnsPSwingMouseWheelEventWhenGivenWheelID() {
        JComponent src = new JPanel();
        PInputEvent piccoloEvent = new PInputEvent(null, null);
        MouseWheelEvent mouseEvent = new MouseWheelEvent(src, MouseEvent.MOUSE_WHEEL, System.currentTimeMillis(), 0, 0,
                0, 0,
                false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 10, 1);
        PSwingEvent swingEvent = PSwingMouseEvent.createMouseEvent(MouseEvent.MOUSE_WHEEL, mouseEvent, piccoloEvent);
        assertTrue(swingEvent instanceof PSwingMouseWheelEvent);
    }

}
