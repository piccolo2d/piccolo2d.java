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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * <b>PInputEventFilter</b> is a class that filters input events based on the
 * events modifiers and type. Any PBasicInputEventHandler that is associated
 * with an event filter will only receive events that pass through the filter.
 * <P>
 * To be accepted events must contain all the modifiers listed in the andMask,
 * at least one of the modifiers listed in the orMask, and none of the modifiers
 * listed in the notMask. The event filter also lets you specify specific event
 * types (mousePressed, released, ...) to accept or reject.
 * <P>
 * If the event filter is set to consume, then it will call consume on any event
 * that it successfully accepts.
 * <P>
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PInputEventFilter {
    /** Mask representing all possible modifiers. */
    public static final int ALL_MODIFIERS_MASK = InputEvent.BUTTON1_MASK | InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK
            | InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK | InputEvent.ALT_MASK | InputEvent.ALT_GRAPH_MASK
            | InputEvent.META_MASK;

    /** If event modifiers don't match this exactly, event it filtered. */
    private int andMask;

    /** If event modifiers have no bits from orMask enabled, event is filtered. */
    private int orMask;

    /** If event modifier has any of the notMask bits on, it is not accepted. */
    private int notMask;

    /** Number of clicks that an incoming event must have to be accepted. */
    private short clickCount = -1;

    /** Whether accepted events should be marked as handled. */
    private boolean marksAcceptedEventsAsHandled = false;

    /** Whether handled events should be immediately filtered. */
    private boolean acceptsAlreadyHandledEvents = false;

    /** Whether key pressed events are accepted. */
    private boolean acceptsKeyPressed = true;

    /** Whether key released events are accepted. */
    private boolean acceptsKeyReleased = true;

    /** Whether key typed events are accepted. */
    private boolean acceptsKeyTyped = true;

    /** Whether mouse clicked events are accepted. */
    private boolean acceptsMouseClicked = true;

    /** Whether mouse dragged events are accepted. */
    private boolean acceptsMouseDragged = true;

    /** Whether mouse entered events are accepted. */
    private boolean acceptsMouseEntered = true;

    /** Whether mouse exited events are accepted. */
    private boolean acceptsMouseExited = true;

    /** Whether mouse moved events are accepted. */
    private boolean acceptsMouseMoved = true;

    /** Whether mouse pressed events are accepted. */
    private boolean acceptsMousePressed = true;

    /** Whether mouse released events are accepted. */
    private boolean acceptsMouseReleased = true;

    /** Whether mouse wheel rotated events are accepted. */
    private boolean acceptsMouseWheelRotated = true;

    /** Whether focus events are accepted. */
    private boolean acceptsFocusEvents = true;

    /**
     * Creates a PInputEventFilter that accepts everything.
     */
    public PInputEventFilter() {
        acceptEverything();
    }

    /**
     * Creates a PInputEventFilter that will accept events if they have the
     * given andMask.
     * 
     * @param andMask exact pattern event modifiers must be to get accepted
     */
    public PInputEventFilter(final int andMask) {
        this();
        this.andMask = andMask;
    }

    /**
     * Creates a PInputEventFilter that will accept events if they have the
     * given andMask and do not contain any of the bits in the notMask.
     * 
     * @param andMask exact pattern event modifiers must be to get accepted
     * @param notMask if any or these bits are on event is not accepted
     */
    public PInputEventFilter(final int andMask, final int notMask) {
        this(andMask);
        this.notMask = notMask;
    }

    /**
     * Returns true if the passed event is one that is accepted.
     * 
     * @param event Event under consideration
     * @param type The type of event encoded as the PInputEvent
     * @return true if event is accepted
     */
    public boolean acceptsEvent(final PInputEvent event, final int type) {
        boolean aResult = false;
        int modifiers = 0;

        if (!event.isFocusEvent()) {
            modifiers = event.getModifiers();
        }

        if (event.isHandled() && !acceptsAlreadyHandledEvents) {
            return false;
        }

        if (modifiers != 0) {
            if ((modifiers & andMask) != andMask || (modifiers & notMask) != 0) {
                return false;
            }

            if (orMask != ALL_MODIFIERS_MASK && (modifiers & orMask) == 0) {
                return false;
            }
        }

        if (event.isMouseEvent() && clickCount != -1 && clickCount != event.getClickCount()) {
            return false;
        }

        switch (type) {
            case KeyEvent.KEY_PRESSED:
                aResult = getAcceptsKeyPressed();
                break;

            case KeyEvent.KEY_RELEASED:
                aResult = getAcceptsKeyReleased();
                break;

            case KeyEvent.KEY_TYPED:
                aResult = getAcceptsKeyTyped();
                break;

            case MouseEvent.MOUSE_CLICKED:
                aResult = getAcceptsMouseClicked();
                break;

            case MouseEvent.MOUSE_DRAGGED:
                aResult = getAcceptsMouseDragged();
                break;

            case MouseEvent.MOUSE_ENTERED:
                aResult = getAcceptsMouseEntered();
                break;

            case MouseEvent.MOUSE_EXITED:
                aResult = getAcceptsMouseExited();
                break;

            case MouseEvent.MOUSE_MOVED:
                aResult = getAcceptsMouseMoved();
                break;

            case MouseEvent.MOUSE_PRESSED:
                aResult = getAcceptsMousePressed();
                break;

            case MouseEvent.MOUSE_RELEASED:
                aResult = getAcceptsMouseReleased();
                break;

            case MouseWheelEvent.WHEEL_UNIT_SCROLL:
            case MouseWheelEvent.WHEEL_BLOCK_SCROLL:
                aResult = getAcceptsMouseWheelRotated();
                break;

            case FocusEvent.FOCUS_GAINED:
            case FocusEvent.FOCUS_LOST:
                aResult = getAcceptsFocusEvents();
                break;

            default:
                throw new RuntimeException("PInputEvent with bad ID");
        }

        if (aResult && getMarksAcceptedEventsAsHandled()) {
            event.setHandled(true);
        }

        return aResult;
    }

    /**
     * Makes this filter accept all mouse click combinations.
     */
    public void acceptAllClickCounts() {
        clickCount = -1;
    }

    /**
     * Makes the filter accept all event types.
     */
    public void acceptAllEventTypes() {
        acceptsKeyPressed = true;
        acceptsKeyReleased = true;
        acceptsKeyTyped = true;
        acceptsMouseClicked = true;
        acceptsMouseDragged = true;
        acceptsMouseEntered = true;
        acceptsMouseExited = true;
        acceptsMouseMoved = true;
        acceptsMousePressed = true;
        acceptsMouseReleased = true;
        acceptsMouseWheelRotated = true;
        acceptsFocusEvents = true;
    }

    /**
     * Makes this filter accept absolutely everything.
     */
    public void acceptEverything() {
        acceptAllEventTypes();
        setAndMask(0);
        setOrMask(ALL_MODIFIERS_MASK);
        setNotMask(0);
        acceptAllClickCounts();
    }

    /**
     * Returns whether this filter accepts key pressed events.
     * 
     * @return true if filter accepts key pressed events
     */
    public boolean getAcceptsKeyPressed() {
        return acceptsKeyPressed;
    }

    /**
     * Returns whether this filter accepts key released events.
     * 
     * @return true if filter accepts key released events
     */
    public boolean getAcceptsKeyReleased() {
        return acceptsKeyReleased;
    }

    /**
     * Returns whether this filter accepts key typed events.
     * 
     * @return true if filter accepts key typed events
     */
    public boolean getAcceptsKeyTyped() {
        return acceptsKeyTyped;
    }

    /**
     * Returns whether this filter accepts mouse clicked events.
     * 
     * @return true if filter accepts mouse clicked events
     */
    public boolean getAcceptsMouseClicked() {
        return acceptsMouseClicked;
    }

    /**
     * Returns whether this filter accepts mouse dragged events.
     * 
     * @return true if filter accepts mouse dragged events
     */
    public boolean getAcceptsMouseDragged() {
        return acceptsMouseDragged;
    }

    /**
     * Returns whether this filter accepts mouse entered events.
     * 
     * @return true if filter accepts mouse entered events
     */
    public boolean getAcceptsMouseEntered() {
        return acceptsMouseEntered;
    }

    /**
     * Returns whether this filter accepts mouse exited events.
     * 
     * @return true if filter accepts mouse exited events
     */
    public boolean getAcceptsMouseExited() {
        return acceptsMouseExited;
    }

    /**
     * Returns whether this filter accepts mouse moved events.
     * 
     * @return true if filter accepts mouse moved events
     */
    public boolean getAcceptsMouseMoved() {
        return acceptsMouseMoved;
    }

    /**
     * Returns whether this filter accepts mouse pressed events.
     * 
     * @return true if filter accepts mouse pressed events
     */
    public boolean getAcceptsMousePressed() {
        return acceptsMousePressed;
    }

    /**
     * Returns whether this filter accepts mouse released events.
     * 
     * @return true if filter accepts mouse released events
     */
    public boolean getAcceptsMouseReleased() {
        return acceptsMouseReleased;
    }

    /**
     * Returns whether this filter accepts mouse wheel rotated events.
     * 
     * @return true if filter accepts mouse wheel rotated events
     */
    public boolean getAcceptsMouseWheelRotated() {
        return acceptsMouseWheelRotated;
    }

    /**
     * Returns whether this filter accepts focus events.
     * 
     * @return true if filter accepts focus events
     */
    public boolean getAcceptsFocusEvents() {
        return acceptsFocusEvents;
    }

    /**
     * Returns whether this filter accepts events that have already been flagged
     * as handled.
     * 
     * @return true if filter accepts events that have already been flagged as
     *         handled
     */
    public boolean getAcceptsAlreadyHandledEvents() {
        return acceptsAlreadyHandledEvents;
    }

    /**
     * Returns whether this filter marks events as handled if they are accepted.
     * 
     * @return true if filter will mark events as filtered if they are accepted
     */
    public boolean getMarksAcceptedEventsAsHandled() {
        return marksAcceptedEventsAsHandled;
    }

    /**
     * Flags all mouse click events as disallowed, regardless of button
     * configuration.
     */
    public void rejectAllClickCounts() {
        clickCount = Short.MAX_VALUE;
    }

    /**
     * Configures filter so that no events will ever get accepted. By itself not
     * terribly useful, but it's a more restrictive starting point than
     * acceptAllEvents();
     */
    public void rejectAllEventTypes() {
        acceptsKeyPressed = false;
        acceptsKeyReleased = false;
        acceptsKeyTyped = false;
        acceptsMouseClicked = false;
        acceptsMouseDragged = false;
        acceptsMouseEntered = false;
        acceptsMouseExited = false;
        acceptsMouseMoved = false;
        acceptsMousePressed = false;
        acceptsMouseReleased = false;
        acceptsMouseWheelRotated = false;
        acceptsFocusEvents = false;
    }

    /**
     * Sets the number of clicks that an incoming event must have to be accepted.
     * 
     * @param aClickCount number clicks that an incoming event must have to be accepted
     */
    public void setAcceptClickCount(final short aClickCount) {
        clickCount = aClickCount;
    }

    /**
     * Sets whether this filter accepts key pressed events.
     * 
     * @param aBoolean whether filter should accept key pressed events
     */
    public void setAcceptsKeyPressed(final boolean aBoolean) {
        acceptsKeyPressed = aBoolean;
    }

    /**
     * Sets whether this filter accepts key released events.
     * 
     * @param aBoolean whether filter should accept key released events
     */
    public void setAcceptsKeyReleased(final boolean aBoolean) {
        acceptsKeyReleased = aBoolean;
    }

    /**
     * Sets whether this filter accepts key typed events.
     * 
     * @param aBoolean whether filter should accept key typed events
     */

    public void setAcceptsKeyTyped(final boolean aBoolean) {
        acceptsKeyTyped = aBoolean;
    }

    /**
     * Sets whether this filter accepts mouse clicked events.
     * 
     * @param aBoolean whether filter should accept mouse clicked events
     */
    public void setAcceptsMouseClicked(final boolean aBoolean) {
        acceptsMouseClicked = aBoolean;
    }

    /**
     * Sets whether this filter accepts mouse dragged events.
     * 
     * @param aBoolean whether filter should accept mouse dragged events
     */
    public void setAcceptsMouseDragged(final boolean aBoolean) {
        acceptsMouseDragged = aBoolean;
    }

    /**
     * Sets whether this filter accepts mouse entered events.
     * 
     * @param aBoolean whether filter should accept mouse entered events
     */
    public void setAcceptsMouseEntered(final boolean aBoolean) {
        acceptsMouseEntered = aBoolean;
    }

    /**
     * Sets whether this filter accepts mouse exited events.
     * 
     * @param aBoolean whether filter should accept mouse exited events
     */
    public void setAcceptsMouseExited(final boolean aBoolean) {
        acceptsMouseExited = aBoolean;
    }

    /**
     * Sets whether this filter accepts mouse moved events.
     * 
     * @param aBoolean whether filter should accept mouse moved events
     */
    public void setAcceptsMouseMoved(final boolean aBoolean) {
        acceptsMouseMoved = aBoolean;
    }

    /**
     * Sets whether this filter accepts mouse pressed events.
     * 
     * @param aBoolean whether filter should accept mouse pressed events
     */
    public void setAcceptsMousePressed(final boolean aBoolean) {
        acceptsMousePressed = aBoolean;
    }

    /**
     * Sets whether this filter accepts mouse released events.
     * 
     * @param aBoolean whether filter should accept mouse released events
     */
    public void setAcceptsMouseReleased(final boolean aBoolean) {
        acceptsMouseReleased = aBoolean;
    }

    /**
     * Sets whether this filter accepts mouse wheel rotation events.
     * 
     * @param aBoolean whether filter should accept mouse wheel rotated events
     */
    public void setAcceptsMouseWheelRotated(final boolean aBoolean) {
        acceptsMouseWheelRotated = aBoolean;
    }

    /**
     * Sets whether this filter accepts focus events.
     * 
     * @param aBoolean whether filter should accept focus events
     */
    public void setAcceptsFocusEvents(final boolean aBoolean) {
        acceptsFocusEvents = aBoolean;
    }

    /**
     * Sets and mask used to filter events. All bits of the andMask must be 1s
     * for the event to be accepted.
     * 
     * @param aAndMask the and mask to use for filtering events
     */
    public void setAndMask(final int aAndMask) {
        andMask = aAndMask;
    }

    /**
     * Sets whether already handled events should be accepted.
     * 
     * @param aBoolean whether already handled events should be accepted
     */
    public void setAcceptsAlreadyHandledEvents(final boolean aBoolean) {
        acceptsAlreadyHandledEvents = aBoolean;
    }

    /**
     * Sets whether events will be marked as dirty once accepted.
     * 
     * @param aBoolean whether events will be marked as dirty once accepted
     */
    public void setMarksAcceptedEventsAsHandled(final boolean aBoolean) {
        marksAcceptedEventsAsHandled = aBoolean;
    }

    /**
     * Sets not mask used to filter events. If any of the not bits are enabled,
     * then the event is not accepted.
     * 
     * @param aNotMask the not mask to use for filtering events
     */
    public void setNotMask(final int aNotMask) {
        notMask = aNotMask;
    }

    /**
     * Sets or mask used to filter events. If any of the or bits are enabled,
     * then the event is accepted.
     * 
     * @param aOrMask the or mask to use for filtering events
     */
    public void setOrMask(final int aOrMask) {
        orMask = aOrMask;
    }
}
