package edu.umd.cs.piccolo.event;

import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import junit.framework.TestCase;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPickPath;

public class PInputEventTest extends TestCase {
    private PCanvas canvas;
    private MouseEvent swingEvent;
    private PInputEvent mouseEvent;

    public void setUp() {
        canvas = new PCanvas();
        canvas.setPreferredSize(new Dimension(100, 100));
        canvas.setBounds(0, 0, 100, 100);
        swingEvent = buildSwingClick(5, 5);
        final PCamera camera = canvas.getCamera();
        final PPickPath pickPath = new PPickPath(camera, new PBounds(0, 0, 10, 10));
        mouseEvent = new PInputEvent(canvas.getRoot().getDefaultInputManager(), swingEvent);
        mouseEvent.setPath(pickPath);
    }

    public void testInputManagerShouldBeSameAsGivenToConstructor() {
        assertSame(canvas.getRoot().getDefaultInputManager(), mouseEvent.getInputManager());
    }

    public void testComponentIsComponentPassedToSwingEvent() {
        assertEquals(canvas, mouseEvent.getComponent());
    }

    public void testKeyboardAccessorsThrowExceptionsOnMousEvents() {
        try {
            mouseEvent.getKeyChar();
        }
        catch (final IllegalStateException e) {
            // expected
        }

        try {
            mouseEvent.getKeyCode();
        }
        catch (final IllegalStateException e) {
            // expected
        }

        try {
            mouseEvent.getKeyLocation();
        }
        catch (final IllegalStateException e) {
            // expected
        }

        try {
            mouseEvent.isActionKey();
        }
        catch (final IllegalStateException e) {
            // expected
        }

    }

    public void testCorrectlyIdentifiesPositiveLeftMouseClick() {
        assertTrue(mouseEvent.isLeftMouseButton());
    }

    public void testCorrectlyIdentifiesNegativeRightMouseClick() {
        assertFalse(mouseEvent.isRightMouseButton());
    }

    public void testCorrectlyIdentifiesNegativeMiddleMouseClick() {
        assertFalse(mouseEvent.isMiddleMouseButton());
    }

    public void testEventsAreNotHandledByDefault() {
        assertFalse(mouseEvent.isHandled());
    }

    public void testSetHandledPersists() {
        mouseEvent.setHandled(true);
        assertTrue(mouseEvent.isHandled());
    }

    public void testHandledEventCanBeUnHandled() {
        mouseEvent.setHandled(true);
        mouseEvent.setHandled(false);
        assertFalse(mouseEvent.isHandled());
    }

    public void testReturnsCorrectModifiers() {
        assertEquals(InputEvent.BUTTON1_MASK, mouseEvent.getModifiers());
    }

    public void testGetButtonUsesWhatWasPassedToMouseEvent() {
        assertEquals(MouseEvent.BUTTON1, mouseEvent.getButton());
    }

    private MouseEvent buildSwingClick(final int x, final int y) {
        return new MouseEvent(canvas, 1, System.currentTimeMillis(), InputEvent.BUTTON1_MASK, x, y, 1, false,
                MouseEvent.BUTTON1);
    }

}
