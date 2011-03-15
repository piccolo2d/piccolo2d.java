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
package org.piccolo2d.extras.swt;

import org.piccolo2d.PCamera;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PInputEventListener;
import org.piccolo2d.extras.swt.PSWTBoundsHandle;
import org.piccolo2d.extras.util.PBoundsLocator;

import junit.framework.TestCase;




/**
 * Unit test for PSWTBoundsHandle.
 */
public class PSWTBoundsHandleTest extends TestCase {
    private PNode node;

    public void setUp() {
        node = new PNode();
        node.setBounds(0, 0, 100, 100);
    }

    public void testAddBoundsHandlesToNodeAddsHandles() {
        PSWTBoundsHandle.addBoundsHandlesTo(node);
        assertEquals(8, node.getChildrenCount());

        for (int i = 0; i < 8; i++) {
            PNode child = node.getChild(i);
            assertTrue(child instanceof PSWTBoundsHandle);
        }
    }

    public void testAddStickyBoundsHandlesToNodeAddsHandles() {
        PCamera camera = new PCamera();
        PSWTBoundsHandle.addStickyBoundsHandlesTo(node, camera);
        assertEquals(0, node.getChildrenCount());
        assertEquals(8, camera.getChildrenCount());

        for (int i = 0; i < 8; i++) {
            PNode child = camera.getChild(i);
            assertTrue(child instanceof PSWTBoundsHandle);
        }
    }

    public void testRemoveBoundsHandlesRemovesOnlyHandles() {
        PNode child = new PNode();
        node.addChild(child);
        PSWTBoundsHandle.addBoundsHandlesTo(node);
        PSWTBoundsHandle.removeBoundsHandlesFrom(node);
        assertEquals(1, node.getChildrenCount());
        assertEquals(child, node.getChild(0));
    }

    public void testRemoveBoundsHandlesDoesNothingWhenNoHandles() {
        PNode child = new PNode();
        node.addChild(child);
        PSWTBoundsHandle.removeBoundsHandlesFrom(node);
        assertEquals(1, node.getChildrenCount());
    }

    public void testCursorHandlerIsInstalledByDefault() {
        PSWTBoundsHandle handle = new PSWTBoundsHandle(PBoundsLocator.createEastLocator(node));
        PInputEventListener dragHandler = handle.getHandleDraggerHandler();
        PInputEventListener cursorHandler = handle.getHandleCursorEventHandler();
        assertNotNull(cursorHandler);
        PInputEventListener[] listeners = handle.getInputEventListeners();
        assertEquals(2, listeners.length);
        assertTrue(cursorHandler == listeners[0] || cursorHandler == listeners[1]);
        assertTrue(dragHandler == listeners[0] || dragHandler == listeners[1]);
    }
}
