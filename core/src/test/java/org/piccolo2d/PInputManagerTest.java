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
package org.piccolo2d;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;

import org.piccolo2d.PCamera;
import org.piccolo2d.PInputManager;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PPickPath;

import junit.framework.TestCase;

/**
 * Unit test for PInputManager.
 */
public class PInputManagerTest extends TestCase {
    private PInputManager manager;
    private MockPInputEventListener mockListener;

    public void setUp() {
        manager = new PInputManager();
        mockListener = new MockPInputEventListener();
    }

    public void testGetKeyboardFocusNullByDefault() {
        assertNull(manager.getKeyboardFocus());
    }

    public void testSetKeyboardFocusIsPersisted() {
        manager.setKeyboardFocus(mockListener);
        assertEquals(mockListener, manager.getKeyboardFocus());
    }

    public void testSetKeyboardFocusDispatchesEventsAboutFocus() {
        final MockPInputEventListener oldListener = new MockPInputEventListener();
        manager.setKeyboardFocus(oldListener);

        assertEquals(1, oldListener.getNotificationCount());
        assertEquals(FocusEvent.FOCUS_GAINED, oldListener.getNotification(0).type);

        final MockPInputEventListener newListener = new MockPInputEventListener();
        manager.setKeyboardFocus(newListener);

        assertEquals(1, newListener.getNotificationCount());
        assertEquals(FocusEvent.FOCUS_GAINED, newListener.getNotification(0).type);
        assertEquals(2, oldListener.getNotificationCount());
        assertEquals(FocusEvent.FOCUS_LOST, oldListener.getNotification(1).type);
    }

    public void testGetMouseFocusNullByDefault() {
        assertNull(manager.getMouseFocus());
    }

    public void testSetMouseFocusPersists() {
        final PCamera camera = new PCamera();
        final PPickPath path = new PPickPath(camera, new PBounds(0, 0, 10, 10));
        manager.setMouseFocus(path);
        assertEquals(path, manager.getMouseFocus());
    }

    public void testGetMouseOverNullByDefault() {
        assertNull(manager.getMouseOver());
    }

    public void testSetMouseOverPersists() {
        final PCamera camera = new PCamera();
        final PPickPath path = new PPickPath(camera, new PBounds(0, 0, 10, 10));
        manager.setMouseOver(path);
        assertEquals(path, manager.getMouseOver());
    }

    public void testGetCurrentCanvasPositionIsOriginByDefault() {
        assertEquals(new Point2D.Double(0, 0), manager.getCurrentCanvasPosition());
    }

    public void testGetLastCanvasPositionIsOriginByDefault() {
        assertEquals(new Point2D.Double(0, 0), manager.getLastCanvasPosition());
    }

    public void testKeyPressedDispatchesToCurrentFocus() {
        manager.setKeyboardFocus(mockListener);
        final PInputEvent event = new PInputEvent(manager, null);
        manager.keyPressed(event);
        assertEquals(2, mockListener.getNotificationCount());
        assertEquals(KeyEvent.KEY_PRESSED, mockListener.getNotification(1).type);
    }

    public void testKeyReleasedDispatchesToCurrentFocus() {
        manager.setKeyboardFocus(mockListener);
        final PInputEvent event = new PInputEvent(manager, null);
        manager.keyReleased(event);
        assertEquals(2, mockListener.getNotificationCount());
        assertEquals(KeyEvent.KEY_RELEASED, mockListener.getNotification(1).type);
    }

    public void testKeyTypedDispatchesToCurrentFocus() {
        manager.setKeyboardFocus(mockListener);
        final PInputEvent event = new PInputEvent(manager, null);
        manager.keyTyped(event);
        assertEquals(2, mockListener.getNotificationCount());
        assertEquals(KeyEvent.KEY_TYPED, mockListener.getNotification(1).type);
    }

    public void testProcessInputMayBeCalledOnFreshManager() {
        manager.processInput();
    }

}
