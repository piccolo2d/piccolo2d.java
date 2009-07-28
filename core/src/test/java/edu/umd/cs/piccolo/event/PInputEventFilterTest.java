package edu.umd.cs.piccolo.event;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;

import junit.framework.TestCase;
import edu.umd.cs.piccolo.PInputManager;

public class PInputEventFilterTest extends TestCase {
    private PInputEventFilter filter;

    public void setUp() {
        filter = new PInputEventFilter();
    }

    public void testAcceptsAlreadyHandledEventsFalseByDefault() {
        assertFalse(filter.getAcceptsAlreadyHandledEvents());
    }

    public void testDoesNotMarkEventsHandledByDefault() {
        assertFalse(filter.getMarksAcceptedEventsAsHandled());
    }

    public void testAcceptsEverythingByDefault() {
        assertAcceptsAll();
    }

    public void testMaskDoesNotAffectReportedAccepts() {
        filter = new PInputEventFilter(0);
        assertAcceptsAll();
    }

    private void assertAcceptsAll() {
        assertTrue(filter.getAcceptsFocusEvents());
        assertTrue(filter.getAcceptsKeyPressed());
        assertTrue(filter.getAcceptsKeyReleased());
        assertTrue(filter.getAcceptsKeyTyped());
        assertTrue(filter.getAcceptsMouseClicked());
        assertTrue(filter.getAcceptsMouseDragged());
        assertTrue(filter.getAcceptsMouseEntered());
        assertTrue(filter.getAcceptsMouseExited());
        assertTrue(filter.getAcceptsMouseMoved());
        assertTrue(filter.getAcceptsMousePressed());
        assertTrue(filter.getAcceptsMouseReleased());
        assertTrue(filter.getAcceptsMouseWheelRotated());
    }

    public void testRejectsEverythingAfterCallingRejectAllEventTypes() {
        filter.rejectAllEventTypes();
        assertRejectsAll();
    }

    private void assertRejectsAll() {
        assertFalse(filter.getAcceptsFocusEvents());
        assertFalse(filter.getAcceptsKeyPressed());
        assertFalse(filter.getAcceptsKeyReleased());
        assertFalse(filter.getAcceptsKeyTyped());
        assertFalse(filter.getAcceptsMouseClicked());
        assertFalse(filter.getAcceptsMouseDragged());
        assertFalse(filter.getAcceptsMouseEntered());
        assertFalse(filter.getAcceptsMouseExited());
        assertFalse(filter.getAcceptsMouseMoved());
        assertFalse(filter.getAcceptsMousePressed());
        assertFalse(filter.getAcceptsMouseReleased());
        assertFalse(filter.getAcceptsMouseWheelRotated());
    }

    public void testSetAcceptsFocusEventsPersists() {
        filter.setAcceptsFocusEvents(false);
        assertFalse(filter.getAcceptsFocusEvents());
    }

    public void testSetAcceptsKeyPressedPersists() {
        filter.setAcceptsKeyPressed(false);
        assertFalse(filter.getAcceptsKeyPressed());
    }

    public void testSetAcceptsKeyReleasedPersists() {
        filter.setAcceptsKeyReleased(false);
        assertFalse(filter.getAcceptsKeyReleased());
    }

    public void testSetAcceptsKeyTypedPersists() {
        filter.setAcceptsKeyTyped(false);
        assertFalse(filter.getAcceptsKeyTyped());
    }

    public void testSetAcceptsMouseClickedPersists() {
        filter.setAcceptsMouseClicked(false);
        assertFalse(filter.getAcceptsMouseClicked());
    }

    public void testSetAcceptsMouseEnteredPersists() {
        filter.setAcceptsMouseEntered(false);
        assertFalse(filter.getAcceptsMouseEntered());
    }

    public void testSetAcceptsMouseExitedPersists() {
        filter.setAcceptsMouseExited(false);
        assertFalse(filter.getAcceptsMouseExited());
    }

    public void testSetAcceptsMouseMovedPersists() {
        filter.setAcceptsMouseMoved(false);
        assertFalse(filter.getAcceptsMouseMoved());
    }
    
    public void testSetAcceptsMouseDraggedPersists() {
        filter.setAcceptsMouseDragged(false);
        assertFalse(filter.getAcceptsMouseDragged());
    }

    public void testSetAcceptsMouseMovedPressed() {
        filter.setAcceptsMousePressed(false);
        assertFalse(filter.getAcceptsMousePressed());
    }

    public void testSetAcceptsMouseMovedReleased() {
        filter.setAcceptsMouseReleased(false);
        assertFalse(filter.getAcceptsMouseReleased());
    }

    public void testSetAcceptsMouseWheelRotated() {
        filter.setAcceptsMouseWheelRotated(false);
        assertFalse(filter.getAcceptsMouseWheelRotated());
    }

    public void testAcceptsSimpleEvent() {
        PInputEvent event = buildTestEvent();
        assertAcceptsEvent(event);
    }

    public void testRejectsAcceptedEventIfAcceptsHandledEventsIsFalse() {
        PInputEvent event = buildTestEvent();
        event.setHandled(true);
        filter.setAcceptsAlreadyHandledEvents(false);
        assertRejectsEvent(event);
    }

    public void testRejectsEventsUnlessModifiersContainAllOfMask() {
        PInputEvent event = buildTestEvent();
        filter.setAndMask(InputEvent.CTRL_MASK | InputEvent.ALT_MASK);
        assertRejectsEvent(event);
        event = buildTestEvent(InputEvent.CTRL_MASK | InputEvent.ALT_MASK);       
        assertAcceptsEvent(event);
        
        event = buildTestEvent(InputEvent.CTRL_MASK | InputEvent.ALT_MASK | InputEvent.META_MASK);       
        assertAcceptsEvent(event);
    }
    
    public void testRejectsEventsUnlessModifiersContainOneOfOrMask() {
        PInputEvent event = buildTestEvent();
        filter.setOrMask(InputEvent.CTRL_MASK | InputEvent.ALT_MASK);
        assertRejectsEvent(event);
        assertRejectsEvent(buildTestEvent(InputEvent.META_MASK));        
        assertAcceptsEvent(buildTestEvent(InputEvent.CTRL_MASK));
        assertAcceptsEvent(buildTestEvent(InputEvent.ALT_MASK));
        assertAcceptsEvent(buildTestEvent(InputEvent.CTRL_MASK | InputEvent.ALT_MASK));   
    }
    
    public void testRejectsEventsUnlessTheyMatchOneOfNotMask() {
        PInputEvent event = buildTestEvent();
        filter.setNotMask(InputEvent.CTRL_MASK | InputEvent.ALT_MASK);
        assertAcceptsEvent(event);

        assertAcceptsEvent(buildTestEvent(InputEvent.META_MASK));        
        assertRejectsEvent(buildTestEvent(InputEvent.CTRL_MASK));
        assertRejectsEvent(buildTestEvent(InputEvent.ALT_MASK));
        assertRejectsEvent(buildTestEvent(InputEvent.CTRL_MASK | InputEvent.ALT_MASK));                              
    }
    
    public void testRejectsMouseEventsIfMouseClickFilterSet() {
        filter.setAcceptClickCount((short)1);        
        assertRejectsEvent(buildTestEvent(0, 0));
        assertAcceptsEvent(buildTestEvent(0, 1));
        assertRejectsEvent(buildTestEvent(0, 2));
        assertRejectsEvent(buildTestEvent(0, 3));
    }
    
    public void testMarksEventsAsHandledIsHonnored() {
        filter.setMarksAcceptedEventsAsHandled(true);
        PInputEvent event = buildTestEvent();        
        assertAcceptsEvent(event);
        assertTrue(event.isHandled());
    }
    
    public void testRejectAllClickCountsIsHonoured() {
        filter.rejectAllClickCounts();
        assertRejectsEvent(buildTestEvent(0, 0));
        assertRejectsEvent(buildTestEvent(0, 1));
        assertRejectsEvent(buildTestEvent(0, 2));
        assertRejectsEvent(buildTestEvent(0, 3));
        
    }

    private void assertRejectsEvent(PInputEvent event) {
        assertFalse(filter.acceptsEvent(event, MouseEvent.MOUSE_CLICKED));
    }

    private void assertAcceptsEvent(PInputEvent event) {
        assertTrue(filter.acceptsEvent(event, MouseEvent.MOUSE_CLICKED));
    }

    private PInputEvent buildTestEvent() {
        return buildTestEvent(InputEvent.BUTTON1_MASK);
    }

    private PInputEvent buildTestEvent(int modifiers) {
        return buildTestEvent(modifiers, 0);
    }
    
    private PInputEvent buildTestEvent(int modifiers, int clickCount) {
        JComponent component = new JPanel();
        PInputManager inputManager = new PInputManager();

        MouseEvent event = new MouseEvent(component, 1, System.currentTimeMillis(), modifiers, 1, 1, clickCount, false);
        return new PInputEvent(inputManager, event);
    }
}
