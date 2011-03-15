/*
 * Copyright (c) 2008-2011, Piccolo2D project, http://piccolo2d.org
 * Copyright (c) 1998-2008, University of Maryland
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * None of the name of the University of Maryland, the name of the Piccolo2D project, or the names of its
 * contributors may be used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.piccolo2d.util;

import java.awt.Color;

import org.piccolo2d.util.PDebug;

import junit.framework.TestCase;

/**
 * Unit test for PDebug.
 */
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
