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
package org.piccolo2d.extras.event;

import java.awt.event.KeyEvent;

import org.piccolo2d.PCanvas;
import org.piccolo2d.PLayer;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.extras.event.PNotification;
import org.piccolo2d.extras.event.PNotificationCenter;
import org.piccolo2d.extras.event.PSelectionEventHandler;



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

    public void testSelectionChange()
    {
        PCanvas canvas = new PCanvas();
        PLayer layer = canvas.getLayer();
        PNode node = new PNode();
        layer.addChild(node);

        PSelectionEventHandler selectionHandler = new PSelectionEventHandler(layer, layer);
        assertTrue(selectionHandler.getSelectionReference().isEmpty());

        PNotificationCenter notificationCenter = PNotificationCenter.defaultCenter();
        notificationCenter.addListener(this, "selectionChanged", PSelectionEventHandler.SELECTION_CHANGED_NOTIFICATION, null);

        selectionHandler.select(node);
        assertTrue(selectionHandler.getSelectionReference().contains(node));
        assertTrue(selectionChanged);
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
        assertTrue(selectionChanged);
    }

    public void testKeyboardDeleteInactive()
    {
        PCanvas canvas = new PCanvas();
        PLayer layer = canvas.getLayer();
        PNode node = new PNode();
        layer.addChild(node);

        PSelectionEventHandler selectionHandler = new PSelectionEventHandler(layer, layer);
        selectionHandler.setDeleteKeyActive(false);
        selectionHandler.select(node);
        assertTrue(selectionHandler.getSelectionReference().contains(node));

        PNotificationCenter notificationCenter = PNotificationCenter.defaultCenter();
        notificationCenter.addListener(this, "selectionChanged", PSelectionEventHandler.SELECTION_CHANGED_NOTIFICATION, null);

        KeyEvent keyEvent = new KeyEvent(canvas, -1, System.currentTimeMillis(), 0, KeyEvent.VK_DELETE);
        PInputEvent event = new PInputEvent(null, keyEvent);
        selectionHandler.keyPressed(event);
        assertTrue(selectionHandler.getSelectionReference().contains(node));
        assertFalse(selectionChanged);
    }

    public void testKeyboardDeleteEmptySelection()
    {
        PCanvas canvas = new PCanvas();
        PLayer layer = canvas.getLayer();

        PSelectionEventHandler selectionHandler = new PSelectionEventHandler(layer, layer);
        selectionHandler.setDeleteKeyActive(true);
        assertTrue(selectionHandler.getSelectionReference().isEmpty());

        PNotificationCenter notificationCenter = PNotificationCenter.defaultCenter();
        notificationCenter.addListener(this, "selectionChanged", PSelectionEventHandler.SELECTION_CHANGED_NOTIFICATION, null);

        KeyEvent keyEvent = new KeyEvent(canvas, -1, System.currentTimeMillis(), 0, KeyEvent.VK_DELETE);
        PInputEvent event = new PInputEvent(null, keyEvent);
        selectionHandler.keyPressed(event);
        assertTrue(selectionHandler.getSelectionReference().isEmpty());
        assertFalse(selectionChanged);
    }

    /**
     * Selection changed, called by PNotificationCenter.
     *
     * @param notification notification
     */
    public void selectionChanged(final PNotification notification)
    {
        this.selectionChanged = true;
    }
}