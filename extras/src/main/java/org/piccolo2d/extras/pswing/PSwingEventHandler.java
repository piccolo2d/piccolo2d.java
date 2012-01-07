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
package org.piccolo2d.extras.pswing;

import org.piccolo2d.PCamera;
import org.piccolo2d.PLayer;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PInputEventListener;
import org.piccolo2d.util.PAffineTransform;
import org.piccolo2d.util.PAffineTransformException;

import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;


/**
 * Event handler to send MousePressed, MouseReleased, MouseMoved, MouseClicked,
 * and MouseDragged events on Swing components within a PCanvas.
 * 
 * @author Ben Bederson
 * @author Lance Good
 * @author Sam Reid
 */
public class PSwingEventHandler implements PInputEventListener {
    /** Used to listen for events. */
    private PNode listenNode = null;

    /** Tracks whether this event handler is active. */
    private boolean active = false;

    /**
     * The previous component - used to generate mouseEntered and mouseExited
     * events.
     */
    private Component previousComponent = null;

    /** Previous point used for mouseEntered and exited events. */
    private Point2D prevPoint = null;

    /** Previous offset used for mouseEntered and exited events. */
    private Point2D previousOffset = null;

    /** Used to avoid accidental recursive handling. */
    private boolean recursing = false;

    /** Used for tracking the left button's state. */
    private final ButtonData leftButtonData = new ButtonData();

    /** Used for tracking the middle button's state. */
    private final ButtonData middleButtonData = new ButtonData();

    /** Used for tracking the right button's state. */
    private final ButtonData rightButtonData = new ButtonData();

    /** The Canvas in which all this pswing activity is taking place. */
    private final PSwingCanvas canvas;

    /**
     * Constructs a new PSwingEventHandler for the given canvas, and a node that
     * will receive the mouse events.
     * 
     * @param canvas the canvas associated with this PSwingEventHandler.
     * @param listenNode the node the mouse listeners will be attached to.
     */
    public PSwingEventHandler(final PSwingCanvas canvas, final PNode listenNode) {
        this.canvas = canvas;
        this.listenNode = listenNode;
    }

    /**
     * Constructs a new PSwingEventHandler for the given canvas.
     * 
     * @param canvas to associate this event handler to
     */
    public PSwingEventHandler(final PSwingCanvas canvas) {
        this.canvas = canvas;
    }

    /**
     * Sets whether this event handler can fire events.
     * 
     * @param active true if this event handler can fire events
     */
    void setActive(final boolean active) {
        if (this.active && !active) {
            if (listenNode != null) {
                this.active = false;
                listenNode.removeInputEventListener(this);
            }
        }
        else if (!this.active && active && listenNode != null) {
            this.active = true;
            listenNode.addInputEventListener(this);
        }
    }

    /**
     * Returns if this event handler is active.
     * 
     * @return true if can fire events
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Finds the best visible component or subcomponent at the specified
     * location.
     * 
     * @param component component to test children or self for
     * @param x x component of location
     * @param y y component of location
     * @return the component or subcomponent at the specified location.
     */
    private Component findShowingComponentAt(final Component component, final int x, final int y) {
        if (!component.contains(x, y)) {
            return null;
        }

        if (component instanceof Container) {
            final Container contain = (Container) component;
            final Component child = findShowingChildAt(contain, x, y);
            if (child != null) {
                return child;
            }
        }
        return component;
    }

    private Component findShowingChildAt(final Container container, final int x, final int y) {
        final Component[] children = container.getComponents();

        for (int i = 0; i < children.length; i++) {
            Component child = children[i];
            if (child != null) {
                final Point p = child.getLocation();
                if (child instanceof Container) {
                    child = findShowingComponentAt(child, x - p.x, y - p.y);
                }
                else {
                    child = child.getComponentAt(x - p.x, y - p.y);
                }
                if (child != null && child.isShowing()) {
                    return child;
                }
            }
        }

        return null;
    }

    /**
     * Determines if any Swing components in Piccolo2D should receive the given
     * MouseEvent and forwards the event to that component. However,
     * mouseEntered and mouseExited are independent of the buttons. Also, notice
     * the notes on mouseEntered and mouseExited.
     * 
     * @param pSwingMouseEvent event being dispatched
     * @param aEvent Piccolo2D event translation of the pSwingMouseEvent
     */
    void dispatchEvent(final PSwingEvent pSwingMouseEvent, final PInputEvent aEvent) {
        final MouseEvent mEvent = pSwingMouseEvent.asMouseEvent();
        final PNode pickedNode = pSwingMouseEvent.getPath().getPickedNode();
        final PNode currentNode = pSwingMouseEvent.getCurrentNode();

        Component comp = null;
        Point point = null;

        Point offset = new Point();

        if (currentNode instanceof PSwing && pickedNode.isDescendentOf(canvas.getRoot())) {

            final PSwing swing = (PSwing) currentNode;
            final PNode grabNode = pickedNode;

            // use a floating point object to perform cameraToLocal to survive the transform math
            final Point2D.Double p2d = new Point2D.Double(mEvent.getX(), mEvent.getY());
            cameraToLocal(pSwingMouseEvent.getPath().getTopCamera(), p2d, grabNode);
            
            point = new Point((int) p2d.getX(), (int) p2d.getY());
            prevPoint = (Point2D) p2d.clone();

            // This is only partially fixed to find the deepest
            // component at pt. It needs to do something like
            // package private method:
            // Container.getMouseEventTarget(int,int,boolean)
            comp = findShowingComponentAt(swing.getComponent(), point.x, point.y);

            // We found the right component - but we need to
            // get the offset to put the event in the component's
            // coordinates
            if (comp != null && comp != swing.getComponent()) {
                offset = extractSwingOffset(comp, swing);
            }

            // Mouse Pressed gives focus - effects Mouse Drags and
            // Mouse Releases
            if (comp != null && isMousePress(pSwingMouseEvent)) {
                if (SwingUtilities.isLeftMouseButton(mEvent)) {
                    leftButtonData.setState(pickedNode, comp, offset.x, offset.y);
                }
                else if (SwingUtilities.isMiddleMouseButton(mEvent)) {
                    middleButtonData.setState(pickedNode, comp, offset.x, offset.y);
                }
                else if (SwingUtilities.isRightMouseButton(mEvent)) {
                    rightButtonData.setState(pickedNode, comp, offset.x, offset.y);
                }
            }
        }

        // This first case we don't want to give events to just
        // any Swing component - but to the one that got the
        // original mousePressed
        if (isDragOrRelease(pSwingMouseEvent)) {
            if (isLeftMouseButtonOnComponent(mEvent)) {
                handleButton(pSwingMouseEvent, aEvent, leftButtonData);
            }

            if (isMiddleMouseButtonOnComponent(mEvent)) {
                handleButton(pSwingMouseEvent, aEvent, middleButtonData);
            }

            if (isRightMouseButtonOnComponent(mEvent)) {
                handleButton(pSwingMouseEvent, aEvent, rightButtonData);
            }
        }
        else if (isPressOrClickOrMove(pSwingMouseEvent) && comp != null) {
            final MouseEvent tempEvent = new MouseEvent(comp, pSwingMouseEvent.getID(), mEvent.getWhen(), mEvent
                    .getModifiers(), point.x - offset.x, point.y - offset.y, mEvent.getClickCount(), mEvent
                    .isPopupTrigger());

            final PSwingEvent e2 = PSwingMouseEvent.createMouseEvent(tempEvent.getID(), tempEvent, aEvent);
            dispatchEvent(comp, e2);
        }
        else if (isWheelEvent(pSwingMouseEvent) && comp != null) {
            final MouseWheelEvent mWEvent = (MouseWheelEvent) mEvent;

            final MouseWheelEvent tempEvent = new MouseWheelEvent(comp, pSwingMouseEvent.getID(), mEvent.getWhen(),
                    mEvent.getModifiers(), point.x - offset.x, point.y - offset.y, mEvent.getClickCount(), mEvent
                    .isPopupTrigger(), mWEvent.getScrollType(), mWEvent.getScrollAmount(), mWEvent
                    .getWheelRotation());

            final PSwingMouseWheelEvent e2 = new PSwingMouseWheelEvent(tempEvent.getID(), tempEvent, aEvent);
            dispatchEvent(comp, e2);
        }

        // Now we need to check if an exit or enter event needs to
        // be dispatched - this code is independent of the mouseButtons.
        // I tested in normal Swing to see the correct behavior.
        if (previousComponent != null) {
            // This means mouseExited

            // This shouldn't happen - since we're only getting node events
            if (comp == null || pSwingMouseEvent.getID() == MouseEvent.MOUSE_EXITED) {
                final MouseEvent tempEvent = createExitEvent(mEvent);

                final PSwingEvent e2 = PSwingMouseEvent.createMouseEvent(tempEvent.getID(), tempEvent, aEvent);

                dispatchEvent(previousComponent, e2);
                previousComponent = null;
            }

            // This means mouseExited prevComponent and mouseEntered comp
            else if (previousComponent != comp) {
                MouseEvent tempEvent = createExitEvent(mEvent);
                PSwingEvent e2 = PSwingMouseEvent.createMouseEvent(tempEvent.getID(), tempEvent, aEvent);
                dispatchEvent(previousComponent, e2);

                tempEvent = createEnterEvent(comp, mEvent, offset.x, offset.y);
                e2 = PSwingMouseEvent.createMouseEvent(tempEvent.getID(), tempEvent, aEvent);
                comp.dispatchEvent(e2.asMouseEvent());
            }
        }
        else if (comp != null) { // This means mouseEntered
            final MouseEvent tempEvent = createEnterEvent(comp, mEvent, offset.x, offset.y);
            final PSwingEvent e2 = PSwingMouseEvent.createMouseEvent(tempEvent.getID(), tempEvent, aEvent);
            dispatchEvent(comp, e2);
        }

        previousComponent = comp;

        if (comp != null) {
            previousOffset = offset;
        }
    }

    private Point extractSwingOffset(final Component comp, final PSwing swing) {
        int offsetX = 0;
        int offsetY = 0;

        for (Component c = comp; c != swing.getComponent(); c = c.getParent()) {
            offsetX += c.getLocation().x;
            offsetY += c.getLocation().y;
        }

        return new Point(offsetX, offsetY);
    }

    private boolean isRightMouseButtonOnComponent(final MouseEvent mEvent) {
        return SwingUtilities.isRightMouseButton(mEvent) && rightButtonData.getFocusedComponent() != null;
    }

    private boolean isMiddleMouseButtonOnComponent(final MouseEvent mEvent) {
        return SwingUtilities.isMiddleMouseButton(mEvent) && middleButtonData.getFocusedComponent() != null;
    }

    private boolean isLeftMouseButtonOnComponent(final MouseEvent mEvent) {
        return SwingUtilities.isLeftMouseButton(mEvent) && leftButtonData.getFocusedComponent() != null;
    }

    private boolean isMousePress(final PSwingEvent pSwingMouseEvent) {
        return pSwingMouseEvent.getID() == MouseEvent.MOUSE_PRESSED;
    }

    private boolean isWheelEvent(final PSwingEvent pSwingMouseEvent) {
        return pSwingMouseEvent.getID() == MouseEvent.MOUSE_WHEEL;
    }

    private boolean isPressOrClickOrMove(final PSwingEvent pSwingMouseEvent) {
        return isMousePress(pSwingMouseEvent) || pSwingMouseEvent.getID() == MouseEvent.MOUSE_CLICKED
                || pSwingMouseEvent.getID() == MouseEvent.MOUSE_MOVED;
    }

    private boolean isDragOrRelease(final PSwingEvent pSwingMouseEvent) {
        return pSwingMouseEvent.getID() == MouseEvent.MOUSE_DRAGGED
                || pSwingMouseEvent.getID() == MouseEvent.MOUSE_RELEASED;
    }

    private MouseEvent createEnterEvent(final Component comp, final MouseEvent e1, final int offX, final int offY) {
        return new MouseEvent(comp, MouseEvent.MOUSE_ENTERED, e1.getWhen(), 0, (int) prevPoint.getX() - offX,
                (int) prevPoint.getY() - offY, e1.getClickCount(), e1.isPopupTrigger());
    }

    private MouseEvent createExitEvent(final MouseEvent e1) {
        return new MouseEvent(previousComponent, MouseEvent.MOUSE_EXITED, e1.getWhen(), 0, (int) prevPoint.getX()
                - (int) previousOffset.getX(), (int) prevPoint.getY() - (int) previousOffset.getY(),
                e1.getClickCount(), e1.isPopupTrigger());
    }

    private void handleButton(final PSwingEvent e1, final PInputEvent aEvent, final ButtonData buttonData) {
        final MouseEvent m1 = e1.asMouseEvent();
        if (involvesSceneNode(buttonData)) {
            // TODO: this probably won't handle viewing through multiple
            // cameras.

            final Point2D pt = new Point2D.Double(m1.getX(), m1.getY());
            cameraToLocal(e1.getPath().getTopCamera(), pt, buttonData.getPNode());
            final MouseEvent tempEvent = new MouseEvent(buttonData.getFocusedComponent(), m1.getID(), m1.getWhen(), m1
                    .getModifiers(), (int) pt.getX() - buttonData.getOffsetX(), (int) pt.getY()
                    - buttonData.getOffsetY(), m1.getClickCount(), m1.isPopupTrigger());

            final PSwingEvent e2 = PSwingMouseEvent.createMouseEvent(tempEvent.getID(), tempEvent, aEvent);
            dispatchEvent(buttonData.getFocusedComponent(), e2);
        }
        else {
            dispatchEvent(buttonData.getFocusedComponent(), e1);
        }
        // buttonData.getPSwing().repaint(); //Experiment with SliderExample
        // (from Martin) suggests this line is unnecessary, and a serious
        // problem in performance.
        m1.consume();
        if (e1.getID() == MouseEvent.MOUSE_RELEASED) {
            buttonData.mouseReleased();
        }
    }

    private boolean involvesSceneNode(final ButtonData buttonData) {
        return buttonData.getPNode().isDescendentOf(canvas.getRoot());
    }

    private void dispatchEvent(final Component target, final PSwingEvent event) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                target.dispatchEvent(event.asMouseEvent());
            }
        });
    }

    /**
     * Transforms the given point from camera coordinates to the node's local
     * system.
     * 
     * @param camera camera from which coordinates are measured
     * @param pt point to transform (will be modified)
     * @param node node from which local coordinates are measured
     */
    private void cameraToLocal(final PCamera camera, final Point2D pt, final PNode node) {
        if (node != null) {
            if (descendsFromLayer(node)) {
                final AffineTransform inverse = invertTransform(camera.getViewTransform());
                inverse.transform(pt, pt);
            }

            node.globalToLocal(pt);
        }
    }

    /**
     * Returns true if the provided layer has a PLayer ancestor.
     * 
     * @param node node being tested
     * 
     * @return true if node is a descendant of a PLayer
     */
    private boolean descendsFromLayer(final PNode node) {
        PNode searchNode = node;
        while (searchNode != null) {
            searchNode = searchNode.getParent();
            if (searchNode instanceof PLayer) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the inverse transform for the provided transform. Throws
     * exception if transform is non invertible.
     * 
     * @param transform transform to invert
     * @return inverted transform
     */
    private AffineTransform invertTransform(final PAffineTransform transform) {
        try {
            return transform.createInverse();
        }
        catch (final NoninvertibleTransformException e) {
            throw new PAffineTransformException(e, transform);
        }
    }

    /**
     * Process a Piccolo2D event and (if active) dispatch the corresponding
     * Swing event.
     * 
     * @param aEvent Piccolo2D event being tested for dispatch to swing
     * @param type is not used in this method
     */
    public void processEvent(final PInputEvent aEvent, final int type) {
        if (!aEvent.isMouseEvent()) {
            return;
        }

        final InputEvent sourceSwingEvent = aEvent.getSourceSwingEvent();
        if (!(sourceSwingEvent instanceof MouseEvent)) {
            throw new RuntimeException("PInputEvent.getSourceSwingEvent was not a MouseEvent.  Actual event: "
                    + sourceSwingEvent + ", class=" + sourceSwingEvent.getClass().getName());
        }

        processMouseEvent(aEvent, (MouseEvent) sourceSwingEvent);
    }

    private void processMouseEvent(final PInputEvent aEvent, final MouseEvent swingMouseEvent) {
        if (!recursing) {
            recursing = true;
            final PSwingEvent pSwingMouseEvent = PSwingMouseEvent.createMouseEvent(swingMouseEvent.getID(),
                    swingMouseEvent, aEvent);
            
            dispatchEvent(pSwingMouseEvent, aEvent);
            if (pSwingMouseEvent.asMouseEvent().isConsumed()) {
                aEvent.setHandled(true);
            }
            recursing = false;
        }
    }

    /**
     * Internal Utility class for handling button interactivity.
     */
    private static class ButtonData {
        private PNode focusNode = null;
        private Component focusComponent = null;
        private int focusOffX = 0;
        private int focusOffY = 0;

        public void setState(final PNode visualNode, final Component comp, final int offX, final int offY) {
            focusComponent = comp;
            focusNode = visualNode;
            focusOffX = offX;
            focusOffY = offY;
        }

        public Component getFocusedComponent() {
            return focusComponent;
        }

        public PNode getPNode() {
            return focusNode;
        }

        public int getOffsetX() {
            return focusOffX;
        }

        public int getOffsetY() {
            return focusOffY;
        }

        public void mouseReleased() {
            focusComponent = null;
            focusNode = null;
        }
    }
}
