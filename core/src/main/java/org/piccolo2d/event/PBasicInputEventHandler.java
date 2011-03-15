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

    /**
     * Constructs a PBasicInputEventHandler with a wide open event filter.
     */
    public PBasicInputEventHandler() {
        super();
        eventFilter = new PInputEventFilter();
    }

    /**
     * Dispatches a generic event to a more specific method. Sparing subclasses
     * from the dispatch logic.
     * 
     * @param event the event to be dispatched
     * @param type Swing event type of the underlying Swing event
     */
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

    /**
     * Returns true if the event would be dispatched if passed to processEvent.
     * 
     * @param event event being tested for acceptance
     * @param type Swing event type of underlying swing event
     * 
     * @return true if the event would be dispatched
     */
    public boolean acceptsEvent(final PInputEvent event, final int type) {
        return eventFilter.acceptsEvent(event, type);
    }

    /**
     * Returns the event filter responsible for filtering incoming events.
     * 
     * @return this handler's InputEventFilter
     */
    public PInputEventFilter getEventFilter() {
        return eventFilter;
    }

    /**
     * Changes this event handler's filter to the one provided.
     * 
     * @param newEventFilter filter to use for this input event handler
     */
    public void setEventFilter(final PInputEventFilter newEventFilter) {
        eventFilter = newEventFilter;
    }

    /**
     * Will get called whenever a key has been pressed down. Subclasses should
     * override this method to implement their own behavior.
     * 
     * @param event the event representing the keystroke
     */
    public void keyPressed(final PInputEvent event) {
    }

    /**
     * Will get called whenever a key has been released. Subclasses should
     * override this method to implement their own behavior.
     * 
     * @param event the event representing the keystroke
     */
    public void keyReleased(final PInputEvent event) {
    }

    /**
     * Will be called at the end of a full keystroke (down then up). Subclasses
     * should override this method to implement their own behavior.
     * 
     * @param event object which can be queried for the event's details
     */
    public void keyTyped(final PInputEvent event) {
    }

    /**
     * Will be called at the end of a full click (mouse pressed followed by
     * mouse released). Subclasses should override this method to implement
     * their own behavior.
     * 
     * @param event object which can be queried for the event's details
     */
    public void mouseClicked(final PInputEvent event) {
    }

    /**
     * Will be called when a mouse button is pressed down. Should two buttons be
     * pressed simultaneously, it will dispatch two of these in an unspecified
     * order. Subclasses should override this method to implement their own
     * behavior.
     * 
     * @param event object which can be queried for the event's details
     */
    public void mousePressed(final PInputEvent event) {
    }

    /**
     * Will be called when a drag is occurring. This is system dependent.
     * Subclasses should override this method to implement their own behavior.
     * 
     * @param event object which can be queried for the event's details
     */
    public void mouseDragged(final PInputEvent event) {
    }

    /**
     * Will be invoked when the mouse enters a specified region. Subclasses
     * should override this method to implement their own behavior.
     * 
     * @param event object which can be queried for the event's details
     */
    public void mouseEntered(final PInputEvent event) {
    }

    /**
     * Will be invoked when the mouse leaves a specified region. Subclasses
     * should override this method to implement their own behavior.
     * 
     * @param event object which can be queried for the event's details
     */
    public void mouseExited(final PInputEvent event) {
    }

    /**
     * Will be called when the mouse is moved. Subclasses should override this
     * method to implement their own behavior.
     * 
     * @param event object which can be queried for event details
     */
    public void mouseMoved(final PInputEvent event) {
    }

    /**
     * Will be called when any mouse button is released. Should two or more
     * buttons be released simultaneously, this method will be called multiple
     * times. Subclasses should override this method to implement their own
     * behavior.
     * 
     * @param event object which can be queried for event details
     */
    public void mouseReleased(final PInputEvent event) {
    }

    /**
     * This method is invoked when the mouse wheel is rotated. Subclasses should
     * override this method to implement their own behavior.
     * 
     * @param event an object that can be queries to discover the event's
     *            details
     */
    public void mouseWheelRotated(final PInputEvent event) {
    }

    /**
     * This method is invoked when the mouse wheel is rotated by a block.
     * Subclasses should override this method to implement their own behavior.
     * 
     * @param event an object that can be queries to discover the event's
     *            details
     */
    public void mouseWheelRotatedByBlock(final PInputEvent event) {
    }

    /**
     * This method is invoked when a node gains the keyboard focus. Subclasses
     * should override this method to implement their own behavior.
     * 
     * @param event an object that can be queries to discover the event's
     *            details
     */
    public void keyboardFocusGained(final PInputEvent event) {
    }

    /**
     * This method is invoked when a node loses the keyboard focus. Subclasses
     * should override this method to implement their own behavior.
     * 
     * @param event an object that can be queries to discover the event's
     *            details
     */
    public void keyboardFocusLost(final PInputEvent event) {
    }
}
