package edu.umd.cs.piccolo.event;

import junit.framework.TestCase;

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
}
