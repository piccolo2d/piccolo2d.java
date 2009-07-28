package edu.umd.cs.piccolo;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;

import junit.framework.TestCase;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPickPath;

public class PInputManagerTest extends TestCase {
    private PInputManager manager;
    private MockPInputEventListener mockListener;

    public void setUp() {
        manager = new PInputManager();
        mockListener = new MockPInputEventListener();
    }

    public void testGetKeyboardFocusNullByDefault() {
        assertNull(manager.getKeyboardFocus());
    }

    public void testSetKeyboardFocusIsPersisted() {
        manager.setKeyboardFocus(mockListener);
        assertEquals(mockListener, manager.getKeyboardFocus());
    }

    public void testSetKeyboardFocusDispatchesEventsAboutFocus() {
        final MockPInputEventListener oldListener = new MockPInputEventListener();
        manager.setKeyboardFocus(oldListener);

        assertEquals(1, oldListener.getNotificationCount());
        assertEquals(FocusEvent.FOCUS_GAINED, oldListener.getNotification(0).type);

        final MockPInputEventListener newListener = new MockPInputEventListener();
        manager.setKeyboardFocus(newListener);

        assertEquals(1, newListener.getNotificationCount());
        assertEquals(FocusEvent.FOCUS_GAINED, newListener.getNotification(0).type);
        assertEquals(2, oldListener.getNotificationCount());
        assertEquals(FocusEvent.FOCUS_LOST, oldListener.getNotification(1).type);
    }

    public void testGetMouseFocusNullByDefault() {
        assertNull(manager.getMouseFocus());
    }

    public void testSetMouseFocusPersists() {
        final PCamera camera = new PCamera();
        final PPickPath path = new PPickPath(camera, new PBounds(0, 0, 10, 10));
        manager.setMouseFocus(path);
        assertEquals(path, manager.getMouseFocus());
    }

    public void testGetMouseOverNullByDefault() {
        assertNull(manager.getMouseOver());
    }

    public void testSetMouseOverPersists() {
        final PCamera camera = new PCamera();
        final PPickPath path = new PPickPath(camera, new PBounds(0, 0, 10, 10));
        manager.setMouseOver(path);
        assertEquals(path, manager.getMouseOver());
    }

    public void testGetCurrentCanvasPositionIsOriginByDefault() {
        assertEquals(new Point2D.Double(0, 0), manager.getCurrentCanvasPosition());
    }

    public void testGetLastCanvasPositionIsOriginByDefault() {
        assertEquals(new Point2D.Double(0, 0), manager.getLastCanvasPosition());
    }

    public void testKeyPressedDispatchesToCurrentFocus() {
        manager.setKeyboardFocus(mockListener);
        final PInputEvent event = new PInputEvent(manager, null);
        manager.keyPressed(event);
        assertEquals(2, mockListener.getNotificationCount());
        assertEquals(KeyEvent.KEY_PRESSED, mockListener.getNotification(1).type);
    }

    public void testKeyReleasedDispatchesToCurrentFocus() {
        manager.setKeyboardFocus(mockListener);
        final PInputEvent event = new PInputEvent(manager, null);
        manager.keyReleased(event);
        assertEquals(2, mockListener.getNotificationCount());
        assertEquals(KeyEvent.KEY_RELEASED, mockListener.getNotification(1).type);
    }

    public void testKeyTypedDispatchesToCurrentFocus() {
        manager.setKeyboardFocus(mockListener);
        final PInputEvent event = new PInputEvent(manager, null);
        manager.keyTyped(event);
        assertEquals(2, mockListener.getNotificationCount());
        assertEquals(KeyEvent.KEY_TYPED, mockListener.getNotification(1).type);
    }

    public void testProcessInputMayBeCalledOnFreshManager() {
        manager.processInput();
    }

}
