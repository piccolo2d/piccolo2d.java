/*
 * Copyright (c) 2008-2009, Piccolo2D project, http://piccolo2d.org
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
package edu.umd.cs.piccolo.event;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * <b>PBasicInputEventHandler</b> is the standard class in Piccolo that is used
 * to register for mouse and keyboard events on a PNode. Note the events that
 * you get depends on the node that you have registered with. For example you
 * will only get mouse moved events when the mouse is over the node that you
 * have registered with, not when the mouse is over some other node.
 * <P>
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PBasicInputEventHandler implements PInputEventListener {

    private PInputEventFilter eventFilter;

    public PBasicInputEventHandler() {
        super();
        eventFilter = new PInputEventFilter();
    }

    public void processEvent(final PInputEvent event, final int type) {
        if (!acceptsEvent(event, type)) {
            return;
        }

        switch (type) {
            case KeyEvent.KEY_PRESSED:
                keyPressed(event);
                break;

            case KeyEvent.KEY_RELEASED:
                keyReleased(event);
                break;

            case KeyEvent.KEY_TYPED:
                keyTyped(event);
                break;

            case MouseEvent.MOUSE_CLICKED:
                mouseClicked(event);
                break;

            case MouseEvent.MOUSE_DRAGGED:
                mouseDragged(event);
                break;

            case MouseEvent.MOUSE_ENTERED:
                mouseEntered(event);
                break;

            case MouseEvent.MOUSE_EXITED:
                mouseExited(event);
                break;

            case MouseEvent.MOUSE_MOVED:
                mouseMoved(event);
                break;

            case MouseEvent.MOUSE_PRESSED:
                mousePressed(event);
                break;

            case MouseEvent.MOUSE_RELEASED:
                mouseReleased(event);
                break;

            case MouseWheelEvent.WHEEL_UNIT_SCROLL:
                mouseWheelRotated(event);
                break;

            case MouseWheelEvent.WHEEL_BLOCK_SCROLL:
                mouseWheelRotatedByBlock(event);
                break;

            case FocusEvent.FOCUS_GAINED:
                keyboardFocusGained(event);
                break;

            case FocusEvent.FOCUS_LOST:
                keyboardFocusLost(event);
                break;

            default:
                throw new RuntimeException("Bad Event Type");
        }
    }

    // ****************************************************************
    // Event Filter - All this event listener can be associated with a event
    // filter. The filter accepts and rejects events based on their modifier
    // flags and type. If the filter is null (the
    // default case) then it accepts all events.
    // ****************************************************************

    public boolean acceptsEvent(final PInputEvent event, final int type) {
        return eventFilter.acceptsEvent(event, type);
    }

    public PInputEventFilter getEventFilter() {
        return eventFilter;
    }

    public void setEventFilter(final PInputEventFilter newEventFilter) {
        eventFilter = newEventFilter;
    }

    // ****************************************************************
    // Events - Methods for handling events sent to the event listener.
    // ****************************************************************

    public void keyPressed(final PInputEvent event) {
    }

    public void keyReleased(final PInputEvent event) {
    }

    public void keyTyped(final PInputEvent event) {
    }

    public void mouseClicked(final PInputEvent event) {
    }

    public void mousePressed(final PInputEvent event) {
    }

    public void mouseDragged(final PInputEvent event) {
    }

    public void mouseEntered(final PInputEvent event) {
    }

    public void mouseExited(final PInputEvent event) {
    }

    public void mouseMoved(final PInputEvent event) {
    }

    public void mouseReleased(final PInputEvent event) {
    }

    public void mouseWheelRotated(final PInputEvent event) {
    }

    public void mouseWheelRotatedByBlock(final PInputEvent event) {
    }

    public void keyboardFocusGained(final PInputEvent event) {
    }

    public void keyboardFocusLost(final PInputEvent event) {
    }

    /**
     * @deprecated see http://code.google.com/p/piccolo2d/issues/detail?id=99
     */
    protected String paramString() {
        return "";
    }
}
