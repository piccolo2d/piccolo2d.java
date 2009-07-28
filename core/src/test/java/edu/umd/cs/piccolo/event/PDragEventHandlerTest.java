package edu.umd.cs.piccolo.event;

import junit.framework.TestCase;

public class PDragEventHandlerTest extends TestCase {
    private PDragEventHandler handler;

    public void setUp() {
        handler = new PDragEventHandler();
    }

    public void testMoveToFrontOnPressDefaultToFalse() {
        assertFalse(handler.getMoveToFrontOnPress());
    }

    public void testMoveToFrontOnPressPersists() {
        handler.setMoveToFrontOnPress(true);
        assertTrue(handler.getMoveToFrontOnPress());
    }

}
