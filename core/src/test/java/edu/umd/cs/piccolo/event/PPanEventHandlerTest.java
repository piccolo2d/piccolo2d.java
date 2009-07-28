package edu.umd.cs.piccolo.event;

import junit.framework.TestCase;

public class PPanEventHandlerTest extends TestCase {
    private PPanEventHandler handler;

    public void setUp() {
        handler = new PPanEventHandler();
    }

    public void testAutoPanIsTrueByDefault() {
        assertTrue(handler.getAutopan());
    }

    public void testSetAutoPanPersists() {
        handler.setAutopan(true);
        assertTrue(handler.getAutopan());
    }

    public void testDefaultMinAutoPanSpeed() {
        assertEquals(250, handler.getMinAutoPanSpeed(), 0.0000001);
    }

    public void testMinAutoPanSpeedPersists() {
        handler.setMinAutopanSpeed(10);
        assertEquals(10, handler.getMinAutoPanSpeed(), 0.000001);
    }

    public void testMaxDefaultAutoPanSpeed() {
        assertEquals(250, handler.getMinAutoPanSpeed(), 0.0000001);
    }

    public void testMaxAutoPanSpeedPersists() {
        handler.setMaxAutopanSpeed(10);
        assertEquals(10, handler.getMaxAutoPanSpeed(), 0.000001);
    }
}
