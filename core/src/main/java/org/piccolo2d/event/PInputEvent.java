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

import java.awt.Cursor;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import org.piccolo2d.PCamera;
import org.piccolo2d.PComponent;
import org.piccolo2d.PInputManager;
import org.piccolo2d.PNode;
import org.piccolo2d.util.PDimension;
import org.piccolo2d.util.PPickPath;


/**
 * <b>PInputEvent</b> is used to notify PInputEventListeners of keyboard and
 * mouse input. It has methods for normal event properties such as event
 * modifier keys and event canvas location.
 * <P>
 * In addition is has methods to get the mouse position and delta in a variety
 * of coordinate systems.
 * <P>
 * Last of all it provides access to the dispatch manager that can be queried to
 * find the current mouse over, mouse focus, and keyboard focus.
 * <P>
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PInputEvent {
    /** The underlying Swing Event. */
    private final InputEvent inputEvent;

    /** Path relating to the current mouse event. */
    private PPickPath pickPath;

    /** Input manager responsible for the creation of this event. */
    private final PInputManager inputManager;

    /** Flag used to identify this event as handled. */
    private boolean handled;

    /**
     * Create an event with the given inputManager and based on the given swing
     * event.
     * 
     * @param inputManager source of PInputEvent
     * @param event underlying swing event
     */
    public PInputEvent(final PInputManager inputManager, final InputEvent event) {        
        inputEvent = event;
        this.inputManager = inputManager;
    }

    /**
     * Changes the cursor to the one provided and stores it on the cursor stack
     * for later retrieval.
     * 
     * @param cursor cursor to push on cursor stack
     */
    public void pushCursor(final Cursor cursor) {
        final PComponent component = getTopCamera().getComponent();
        component.pushCursor(cursor);
    }

    /**
     * Removes the top most cursor from the cursor stack and sets it as the
     * current cursor.
     */
    public void popCursor() {
        final PComponent component = getTopCamera().getComponent();
        component.popCursor();
    }

    // ****************************************************************
    // Accessing Picked Objects - Methods to access the objects associated
    // with this event.
    // <p>
    // Cameras can view layers that have
    // other cameras on them, so events may be arriving through a stack
    // of many cameras. The getCamera() method returns the bottommost
    // camera on that stack. The getTopCamera method returns the topmost
    // camera on that stack, this is also the camera through which the
    // event originated.
    // ****************************************************************

    /**
     * Return the bottom most camera that is currently painting. If you are
     * using internal cameras this may be different then what is returned by
     * getTopCamera.
     * 
     * @return the current PickPath's bottom camera.
     */
    public PCamera getCamera() {
        return getPath().getBottomCamera();
    }

    /**
     * Return the topmost camera this is painting. This is the camera associated
     * with the PCanvas that requested the current repaint.
     * 
     * @return topmost camera on the pick path
     */
    public PCamera getTopCamera() {
        return getPath().getTopCamera();
    }

    /**
     * Get the canvas associated with the top camera. This is the canvas where
     * the originating swing event came from.
     * 
     * @return component attached to the top camera of the current pick path
     */
    public PComponent getComponent() {
        return getTopCamera().getComponent();
    }

    /**
     * Return the input manager that dispatched this event. You can use this
     * input manager to find the current mouse focus, mouse over, and key focus
     * nodes. You can also set a new key focus node.
     * 
     * @return input manager that dispatched this event
     */
    public PInputManager getInputManager() {
        return inputManager;
    }

    /**
     * Return the PPickPath associated with this input event.
     * 
     * @return pick path associated with this event (may be null)
     */
    public PPickPath getPath() {
        return pickPath;
    }

    /**
     * Sets the PIckPath associated with this mouse event.
     * 
     * @param path path to associate with this mouse event
     */
    public void setPath(final PPickPath path) {
        pickPath = path;
    }

    /**
     * Return the bottom node on the current pickpath, that is the picked node
     * furthest from the root node.
     * 
     * @return the currently picked node of this mouse event
     */
    public PNode getPickedNode() {
        if (pickPath == null) {
            return null;
        }
        return pickPath.getPickedNode();
    }

    // ****************************************************************
    // Basics
    // ****************************************************************

    /**
     * Returns the key code associated with a key event.
     * 
     * @return key code associated with a key event
     */
    public int getKeyCode() {
        if (isKeyEvent()) {
            final KeyEvent e = (KeyEvent) inputEvent;
            return e.getKeyCode();
        }
        throw new IllegalStateException("Can't get keycode from mouse event");
    }

    /**
     * Returns the character associated with a key event.
     * 
     * @return char associated with a key event
     */
    public char getKeyChar() {
        if (isKeyEvent()) {
            final KeyEvent e = (KeyEvent) inputEvent;
            return e.getKeyChar();
        }
        throw new IllegalStateException("Can't get keychar from mouse event");
    }

    /**
     * Returns the location on the keyboard from which the key stroke
     * originated.
     * 
     * @return location on keyboard from which stroke originated.
     */
    public int getKeyLocation() {
        if (isKeyEvent()) {
            final KeyEvent e = (KeyEvent) inputEvent;
            return e.getKeyLocation();
        }
        throw new IllegalStateException("Can't get keylocation from mouse event");
    }

    /**
     * Returns whether the key event involves the action key.
     * 
     * @return true if key involved is the action key
     */
    public boolean isActionKey() {
        if (isKeyEvent()) {
            final KeyEvent e = (KeyEvent) inputEvent;
            return e.isActionKey();
        }
        throw new IllegalStateException("Can't get isActionKey from mouse event");
    }

    /**
     * Returns the modifiers provided for the input event by swing.
     * 
     * @return modifier flags for the input event
     */
    public int getModifiers() {
        if (!isFocusEvent()) {
            return inputEvent.getModifiers();
        }
        throw new IllegalStateException("Can't get modifiers from focus event");
    }

    /**
     * Returns the extended modifiers provided for the input event by swing.
     * 
     * @return extended modifies of input event
     */
    public int getModifiersEx() {
        if (!isFocusEvent()) {
            return inputEvent.getModifiersEx();
        }
        throw new IllegalStateException("Can't get modifiers ex from focus event");
    }

    /**
     * Returns the click count of the mouse event.
     * 
     * @return click count of mouse event
     */
    public int getClickCount() {
        if (isMouseEvent()) {
            return ((MouseEvent) inputEvent).getClickCount();
        }
        throw new IllegalStateException("Can't get clickcount from key event");
    }

    /**
     * Returns the time at which the event was emitted.
     * 
     * @return time at which the vent was emitted
     */
    public long getWhen() {
        if (!isFocusEvent()) {
            return inputEvent.getWhen();
        }
        throw new IllegalStateException("Can't get when from focus event");
    }

    /**
     * Returns whether the alt key is currently down.
     * 
     * @return true if alt key is down
     */
    public boolean isAltDown() {
        if (!isFocusEvent()) {
            return inputEvent.isAltDown();
        }
        throw new IllegalStateException("Can't get altdown from focus event");
    }

    /**
     * Returns whether the control key is currently down.
     * 
     * @return true if control key is down
     */
    public boolean isControlDown() {
        if (!isFocusEvent()) {
            return inputEvent.isControlDown();
        }
        throw new IllegalStateException("Can't get controldown from focus event");
    }

    /**
     * Returns whether the meta key is currently down.
     * 
     * @return true if meta key is down
     */
    public boolean isMetaDown() {
        if (!isFocusEvent()) {
            return inputEvent.isMetaDown();
        }
        throw new IllegalStateException("Can't get modifiers from focus event");
    }

    /**
     * Returns whether the shift key is currently down.
     * 
     * @return true if shift key is down
     */
    public boolean isShiftDown() {
        if (!isFocusEvent()) {
            return inputEvent.isShiftDown();
        }
        throw new IllegalStateException("Can't get shiftdown from focus event");
    }

    /**
     * Returns whether the mouse event involves the left mouse button.
     * 
     * @return true if left mouse button is involved the mouse event
     */
    public boolean isLeftMouseButton() {
        if (isMouseEvent()) {
            return SwingUtilities.isLeftMouseButton((MouseEvent) getSourceSwingEvent());
        }
        throw new IllegalStateException("Can't get isLeftMouseButton from focus event");
    }

    /**
     * Returns whether the mouse event involves the middle mouse button.
     * 
     * @return true if middle mouse button is involved the mouse event
     */
    public boolean isMiddleMouseButton() {
        if (isMouseEvent()) {
            return SwingUtilities.isMiddleMouseButton((MouseEvent) getSourceSwingEvent());
        }
        throw new IllegalStateException("Can't get isMiddleMouseButton from focus event");
    }

    /**
     * Returns whether the mouse event involves the right mouse button.
     * 
     * @return true if right mouse button is involved the mouse event
     */
    public boolean isRightMouseButton() {
        if (isMouseEvent()) {
            return SwingUtilities.isRightMouseButton((MouseEvent) getSourceSwingEvent());
        }
        throw new IllegalStateException("Can't get isRightMouseButton from focus event");
    }

    /**
     * Return true if another event handler has already handled this event.
     * Event handlers should use this as a hint before handling the event
     * themselves and possibly reject events that have already been handled.
     * 
     * @return true if event has been marked as handled
     */
    public boolean isHandled() {
        return handled;
    }

    /**
     * Set that this event has been handled by an event handler. This is a
     * relaxed for of consuming events. The event will continue to get
     * dispatched to event handlers even after it is marked as handled, but
     * other event handlers that might conflict are expected to ignore events
     * that have already been handled.
     * 
     * @param handled whether the event is marked
     */
    public void setHandled(final boolean handled) {
        this.handled = handled;
    }

    /**
     * Returns the mouse button value of the underlying mouse event.
     * 
     * @return button value of underlying mouse event
     */
    public int getButton() {
        if (isMouseEvent()) {
            return ((MouseEvent) inputEvent).getButton();
        }
        throw new IllegalStateException("Can't get button from key event");
    }

    /**
     * Returns the current value of the wheel rotation on Mouse Wheel Rotation
     * events.
     * 
     * @return wheel rotation value
     */
    public int getWheelRotation() {
        if (isMouseWheelEvent()) {
            return ((MouseWheelEvent) inputEvent).getWheelRotation();
        }
        throw new IllegalStateException("Can't get wheel rotation from non-wheel event");
    }

    /**
     * Returns the underlying swing event that this PInputEvent is wrapping.
     * 
     * @return underlying swing event
     */
    public InputEvent getSourceSwingEvent() {
        return inputEvent;
    }

    // ****************************************************************
    // Classification - Methods to distinguish between mouse and key
    // events.
    // ****************************************************************

    /**
     * Returns whether the underlying event is a KeyEvent.
     * 
     * @return true if is key event
     */
    public boolean isKeyEvent() {
        return inputEvent instanceof KeyEvent;
    }

    /**
     * Returns whether the underlying event is a MouseEvent.
     * 
     * @return true if is mouse event
     */
    public boolean isMouseEvent() {
        return inputEvent instanceof MouseEvent;
    }

    /**
     * Returns whether the underlying event is a Mouse Wheel Event.
     * 
     * @return true if is a mouse wheel event
     */

    public boolean isMouseWheelEvent() {
        return inputEvent instanceof MouseWheelEvent;
    }

    /**
     * Returns whether the underlying event is a Focus Event.
     * 
     * @return true if is focus event
     */
    public boolean isFocusEvent() {
        return inputEvent == null;
    }

    /**
     * Returns whether the underlying event is a mouse entered or exited event.
     * 
     * @return true if is a mouse entered or exited event
     */
    public boolean isMouseEnteredOrMouseExited() {
        if (isMouseEvent()) {
            return inputEvent.getID() == MouseEvent.MOUSE_ENTERED || inputEvent.getID() == MouseEvent.MOUSE_EXITED;
        }
        return false;
    }

    /**
     * Returns whether or not this event is a popup menu trigger event for the
     * platform. Must not be called if this event isn't a mouse event.
     * <p>
     * <b>Note</b>: Popup menus are triggered differently on different systems.
     * Therefore, <code>isPopupTrigger</code> should be checked in both
     * <code>mousePressed</code> and <code>mouseReleased</code> for proper
     * cross-platform functionality.
     * 
     * @return boolean, true if this event triggers a popup menu for this
     *         platform
     */
    public boolean isPopupTrigger() {
        if (isMouseEvent()) {
            return ((MouseEvent) inputEvent).isPopupTrigger();
        }
        throw new IllegalStateException("Can't get clickcount from key event");
    }

    // ****************************************************************
    // Coordinate Systems - Methods for getting mouse location data
    // These methods are only designed for use with PInputEvents that
    // return true to the isMouseEvent method.
    // ****************************************************************

    /**
     * Return the mouse position in PCanvas coordinates.
     * 
     * @return mouse position in PCanvas coordinates
     */
    public Point2D getCanvasPosition() {
        return (Point2D) inputManager.getCurrentCanvasPosition().clone();
    }

    /**
     * Return the delta between the last and current mouse position in PCanvas
     * coordinates.
     * 
     * @return delta between last and current mouse position as measured by the
     *         PCanvas
     */
    public PDimension getCanvasDelta() {
        final Point2D last = inputManager.getLastCanvasPosition();
        final Point2D current = inputManager.getCurrentCanvasPosition();
        return new PDimension(current.getX() - last.getX(), current.getY() - last.getY());
    }

    /**
     * Return the mouse position relative to a given node on the pick path.
     * 
     * @param nodeOnPath node on the current PPickPath
     * 
     * @return mouse position relative to the provided node on pick path
     */
    public Point2D getPositionRelativeTo(final PNode nodeOnPath) {
        if (pickPath == null) {
            throw new RuntimeException("Attempting to use pickPath for a non-mouse event.");
        }
        final Point2D r = getCanvasPosition();
        return pickPath.canvasToLocal(r, nodeOnPath);
    }

    /**
     * Return the delta between the last and current mouse positions relative to
     * a given node on the pick path.
     * 
     * @param nodeOnPath node from which to measure
     * @return delta between current mouse position and a given node on the pick
     *         path
     */
    public PDimension getDeltaRelativeTo(final PNode nodeOnPath) {
        if (pickPath == null) {
            throw new RuntimeException("Attempting to use pickPath for a non-mouse event.");
        }
        final PDimension r = getCanvasDelta();
        return (PDimension) pickPath.canvasToLocal(r, nodeOnPath);
    }

    /**
     * Return the mouse position transformed through the view transform of the
     * bottom camera.
     * 
     * @return mouse position as measured by the bottom camera
     */
    public Point2D getPosition() {
        if (pickPath == null) {
            throw new RuntimeException("Attempting to use pickPath for a non-mouse event.");
        }
        final Point2D r = getCanvasPosition();
        pickPath.canvasToLocal(r, getCamera());
        return getCamera().localToView(r);
    }

    /**
     * Return the delta between the last and current mouse positions transformed
     * through the view transform of the bottom camera.
     * 
     * @return delta between last and current mouse position as measured by the
     *         bottom camera
     */
    public PDimension getDelta() {
        if (pickPath == null) {
            throw new RuntimeException("Attempting to use pickPath for a non-mouse event.");
        }
        final PDimension r = getCanvasDelta();
        pickPath.canvasToLocal(r, getCamera());
        return (PDimension) getCamera().localToView(r);
    }

    /**
     * Returns a string representation of this object for debugging purposes.
     * 
     * @return string representation of this object
     */
    public String toString() {
        final StringBuffer result = new StringBuffer();

        result.append(super.toString().replaceAll(".*\\.", ""));
        result.append('[');
        if (handled) {
            result.append("handled");
        }
        result.append(']');

        return result.toString();
    }
}
