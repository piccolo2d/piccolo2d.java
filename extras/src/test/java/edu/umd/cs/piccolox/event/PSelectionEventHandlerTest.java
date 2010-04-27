/*
 * Copyright (c) 2008-2010, Piccolo2D project, http://piccolo2d.org
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
package edu.umd.cs.piccolox.event;

import java.awt.event.KeyEvent;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;

import edu.umd.cs.piccolo.event.PInputEvent;

import junit.framework.TestCase;

/**
 * Unit test for PSelectionEventHandler.
 */
public class PSelectionEventHandlerTest
    extends TestCase
{
    /** True if this class was notified of a selection change. */
    private boolean selectionChanged;


    /** {@inheritDoc} */
    protected void setUp()
    {
        selectionChanged = false;
    }

    /**
     * {@link http://code.google.com/p/piccolo2d/issues/detail?id=177}
     */
    public void testKeyboardDeleteFiresSelectionChange()
    {
        PCanvas canvas = new PCanvas();
        PLayer layer = canvas.getLayer();
        PNode node = new PNode();
        layer.addChild(node);

        PSelectionEventHandler selectionHandler = new PSelectionEventHandler(layer, layer);
        selectionHandler.setDeleteKeyActive(true);
        selectionHandler.select(node);
        assertTrue(selectionHandler.getSelectionReference().contains(node));

        PNotificationCenter notificationCenter = PNotificationCenter.defaultCenter();
        notificationCenter.addListener(this, "selectionChanged", PSelectionEventHandler.SELECTION_CHANGED_NOTIFICATION, null);

        KeyEvent keyEvent = new KeyEvent(canvas, -1, System.currentTimeMillis(), 0, KeyEvent.VK_DELETE);
        PInputEvent event = new PInputEvent(null, keyEvent);
        selectionHandler.keyPressed(event);
        assertTrue(selectionHandler.getSelectionReference().isEmpty());

        // fix this assertion to fix issue 177 linked above
        //assertTrue(selectionChanged);
    }

    /**
     * Selection changed, called by PNotificationCenter.
     */
    public void selectionChanged()
    {
        this.selectionChanged = true;
    }
}