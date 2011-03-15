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
package org.piccolo2d.event;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import org.piccolo2d.PCanvas;
import org.piccolo2d.PiccoloAsserts;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PInputEventFilter;

import junit.framework.TestCase;

/**
 * Unit test for PBasicInputEventListener.
 */
public class PBasicInputEventListenerTest extends TestCase {
    private PBasicInputEventHandler listener;
    private MockPBasicInputEventHandler mockListener;

    public void setUp() {
        listener = new PBasicInputEventHandler();
    }

    public void testSetEventFilterIsPersisted() {
        final PInputEventFilter filter = new PInputEventFilter();
        listener.setEventFilter(filter);
        assertSame(filter, listener.getEventFilter());
    }

    public void testAcceptsEventDelegatesToFilter() {
        final PInputEventFilter filter = new PInputEventFilter();
        listener.setEventFilter(filter);
        final PInputEvent event = buildInputEvent();
        assertTrue(listener.acceptsEvent(event, MouseEvent.MOUSE_CLICKED));
        filter.rejectAllEventTypes();
        assertFalse(listener.acceptsEvent(event, MouseEvent.MOUSE_CLICKED));
    }

    public void testProcessEventDelegatesToSubClassMethodsBasedOnType() {
        final PInputEvent event = buildInputEvent();

        mockListener = new MockPBasicInputEventHandler();
        final int[] eventTypes = new int[] { KeyEvent.KEY_PRESSED, KeyEvent.KEY_RELEASED, KeyEvent.KEY_TYPED,
                MouseEvent.MOUSE_RELEASED, MouseEvent.MOUSE_CLICKED, MouseEvent.MOUSE_DRAGGED,
                MouseEvent.MOUSE_ENTERED, MouseEvent.MOUSE_EXITED, MouseEvent.MOUSE_MOVED, MouseEvent.MOUSE_PRESSED,
                MouseWheelEvent.WHEEL_UNIT_SCROLL, MouseWheelEvent.WHEEL_BLOCK_SCROLL, FocusEvent.FOCUS_GAINED,
                FocusEvent.FOCUS_LOST };

        for (int i = 0; i < eventTypes.length; i++) {
            mockListener.processEvent(event, eventTypes[i]);
        }

        PiccoloAsserts.assertEquals(new String[] { "keyPressed", "keyReleased", "keyTyped", "mouseReleased",
                "mouseClicked", "mouseDragged", "mouseEntered", "mouseExited", "mouseMoved", "mousePressed",
                "mouseWheelRotated", "mouseWheelRotatedByBlock", "focusGained", "focusLost" }, mockListener
                .getMethodCalls());
    }

    private PInputEvent buildInputEvent() {
        final PCanvas canvas = new PCanvas();
        final MouseEvent mouseEvent = new MouseEvent(canvas, 1, System.currentTimeMillis(), 0, 0, 0, 1, false);
        final PInputEvent event = new PInputEvent(canvas.getRoot().getDefaultInputManager(), mouseEvent);
        return event;
    }

}
