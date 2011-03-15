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

import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import org.piccolo2d.PCamera;
import org.piccolo2d.PCanvas;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PPickPath;

import junit.framework.TestCase;

/**
 * Unit test for PInputEvent.
 */
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
