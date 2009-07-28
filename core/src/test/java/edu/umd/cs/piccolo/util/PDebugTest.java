package edu.umd.cs.piccolo.util;

import java.awt.Color;

import junit.framework.TestCase;

public class PDebugTest extends TestCase {
    public void setUp() {
        PDebug.resetFPSTiming();
    }

    public void testGetDebugColourGeneratesGraysInCycle() {
        assertEquals(new Color(100, 100, 100, 150), PDebug.getDebugPaintColor());
        assertEquals(new Color(110, 110, 110, 150), PDebug.getDebugPaintColor());
        assertEquals(new Color(120, 120, 120, 150), PDebug.getDebugPaintColor());
        assertEquals(new Color(130, 130, 130, 150), PDebug.getDebugPaintColor());
        assertEquals(new Color(140, 140, 140, 150), PDebug.getDebugPaintColor());
        assertEquals(new Color(150, 150, 150, 150), PDebug.getDebugPaintColor());
        assertEquals(new Color(160, 160, 160, 150), PDebug.getDebugPaintColor());
        assertEquals(new Color(170, 170, 170, 150), PDebug.getDebugPaintColor());
        assertEquals(new Color(180, 180, 180, 150), PDebug.getDebugPaintColor());
        assertEquals(new Color(190, 190, 190, 150), PDebug.getDebugPaintColor());
        assertEquals(new Color(100, 100, 100, 150), PDebug.getDebugPaintColor());
    }

    public void testUnlessOutputWasProcessedFPSisZero() throws InterruptedException {
        assertEquals(0.0, PDebug.getInputFPS(), 0.00001);
        PDebug.startProcessingInput();
        Thread.sleep(2);
        PDebug.endProcessingInput();

        assertEquals(0.0, PDebug.getTotalFPS(), 0.0001);
    }
}
