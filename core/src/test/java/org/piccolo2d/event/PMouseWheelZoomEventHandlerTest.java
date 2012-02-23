/*
 * Copyright (c) 2008-2012, Piccolo2D project, http://piccolo2d.org
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
package org.piccolo2d.event;

import static org.piccolo2d.event.PMouseWheelZoomEventHandler.DEFAULT_SCALE_FACTOR;
import static org.piccolo2d.event.PMouseWheelZoomEventHandler.ZoomMode.ZOOM_ABOUT_MOUSE;
import static org.piccolo2d.event.PMouseWheelZoomEventHandler.ZoomMode.ZOOM_ABOUT_CANVAS_CENTER;
import static org.piccolo2d.event.PMouseWheelZoomEventHandler.ZoomMode.ZOOM_ABOUT_VIEW_CENTER;

import junit.framework.TestCase;

/**
 * Unit test for PMouseWheelZoomEventHandler.
 */
public final class PMouseWheelZoomEventHandlerTest extends TestCase {
    private static final double TOLERANCE = 0.1d;

    public void testConstructor() {
        assertNotNull(new PMouseWheelZoomEventHandler());
    }

    public void testScaleFactor() {
        PMouseWheelZoomEventHandler zoomHandler = new PMouseWheelZoomEventHandler();
        assertEquals(DEFAULT_SCALE_FACTOR, zoomHandler.getScaleFactor(), TOLERANCE);

        zoomHandler.setScaleFactor(42.0d);
        assertEquals(42.0d, zoomHandler.getScaleFactor(), TOLERANCE);
    }

    public void testDefaultZoomMode() {
        PMouseWheelZoomEventHandler zoomHandler = new PMouseWheelZoomEventHandler();
        assertSame(ZOOM_ABOUT_CANVAS_CENTER, zoomHandler.getZoomMode());
    }

    public void testZoomAboutMouse() {
        PMouseWheelZoomEventHandler zoomHandler = new PMouseWheelZoomEventHandler();
        zoomHandler.zoomAboutMouse();
        assertSame(ZOOM_ABOUT_MOUSE, zoomHandler.getZoomMode());
    }

    public void testZoomAboutCanvasCenter() {
        PMouseWheelZoomEventHandler zoomHandler = new PMouseWheelZoomEventHandler();
        zoomHandler.zoomAboutCanvasCenter();
        assertSame(ZOOM_ABOUT_CANVAS_CENTER, zoomHandler.getZoomMode());
    }

    public void testZoomAboutViewCenter() {
        PMouseWheelZoomEventHandler zoomHandler = new PMouseWheelZoomEventHandler();
        zoomHandler.zoomAboutViewCenter();
        assertSame(ZOOM_ABOUT_VIEW_CENTER, zoomHandler.getZoomMode());
    }
}
