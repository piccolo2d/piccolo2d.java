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

import org.piccolo2d.PNode;
import org.piccolo2d.event.PInputEventListener;
import org.piccolo2d.extras.swt.PSWTHandle;
import org.piccolo2d.extras.util.PBoundsLocator;
import org.piccolo2d.extras.util.PLocator;

import junit.framework.TestCase;




/**
 * Unit test for PSWTHandle.
 */
public class PSWTHandleTest extends TestCase {
    private PNode node;
    private PSWTHandle handle;
    private PBoundsLocator locator;

    public void setUp() throws Exception {
        node = new PNode();
        locator = PBoundsLocator.createEastLocator(node);
        handle = new PSWTHandle(locator);
        node.setBounds(0, 0, 100, 100);
        node.addChild(handle);
    }

    public void testDefaultsAreCorrect() {
        assertEquals(PSWTHandle.DEFAULT_COLOR, handle.getPaint());
        assertEquals(PSWTHandle.DEFAULT_HANDLE_SIZE + 2 /** for border pen */
        , handle.getHeight(), Float.MIN_VALUE);
    }

    public void testLocatorPersists() {
        assertSame(locator, handle.getLocator());

        PLocator newLocator = PBoundsLocator.createWestLocator(node);
        handle.setLocator(newLocator);
        assertSame(newLocator, handle.getLocator());
    }

    public void testHandleHasDragHandlerInstalled() {
        PInputEventListener dragHandler = handle.getHandleDraggerHandler();
        assertNotNull(dragHandler);

        PInputEventListener[] installedListeners = handle.getInputEventListeners();
        assertEquals(1, installedListeners.length);
        assertSame(dragHandler, installedListeners[0]);
    }

    public void testChangingParentDoesNotChangeLocatorNode() {
        handle.relocateHandle();
        PNode newParent = new PNode();
        newParent.setBounds(50, 50, 100, 100);

        final double originalX = handle.getX();
        handle.setParent(newParent);

        final double newX = handle.getX();

        assertEquals(newX, originalX, Double.MIN_VALUE);
    }
}
