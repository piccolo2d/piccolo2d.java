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
package edu.umd.cs.piccolox.pswing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.util.PAffineTransformException;

/**
 * Event handler to send MousePressed, MouseReleased, MouseMoved, MouseClicked,
 * and MouseDragged events on Swing components within a PCanvas.
 * 
 * @author Ben Bederson
 * @author Lance Good
 * @author Sam Reid
 */
public class PSwingEventHandler implements PInputEventListener {

    private PNode listenNode = null; // used to listen to for events
    private boolean active = false; // True when event handlers are set active.

    // The previous component - used to generate mouseEntered and
    // mouseExited events
    private Component prevComponent = null;

    // Previous points used in generating mouseEntered and mouseExited events
    private Point2D prevPoint = null;
    private Point2D prevOff = null;

    private boolean recursing = false;// to avoid accidental recursive handling

    private final ButtonData leftButtonData = new ButtonData();
    private final ButtonData rightButtonData = new ButtonData();
    private final ButtonData middleButtonData = new ButtonData();

    private final PSwingCanvas canvas;

    /**
     * Constructs a new PSwingEventHandler for the given canvas, and a node that
     * will recieve the mouse events.
     * 
     * @param canvas the canvas associated with this PSwingEventHandler.
     * @param node the node the mouse listeners will be attached to.
     */
    public PSwingEventHandler(final PSwingCanvas canvas, final PNode node) {
        this.canvas = canvas;
        listenNode = node;
    }

    /**
     * Constructs a new PSwingEventHandler for the given canvas.
     */
    public PSwingEventHandler(final PSwingCanvas canvas) {
        this.canvas = canvas;
    }

    /**
     * Sets whether this event handler can fire events.
     * 
     * @param active
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
     * Determines if this event handler is active.
     * 
     * @return True if active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Finds the component at the specified location (must be showing).
     * 
     * @param c
     * @param x
     * @param y
     * @return the component at the specified location.
     */
    private Component findShowingComponentAt(final Component c, final int x, final int y) {
        if (!c.contains(x, y)) {
            return null;
        }

        if (c instanceof Container) {
            final Container contain = (Container) c;
            final int ncomponents = contain.getComponentCount();
            final Component component[] = contain.getComponents();

            for (int i = 0; i < ncomponents; i++) {
                Component comp = component[i];
                if (comp != null) {
                    final Point p = comp.getLocation();
                    if (comp instanceof Container) {
                        comp = findShowingComponentAt(comp, x - (int) p.getX(), y - (int) p.getY());
                    }
                    else {
                        comp = comp.getComponentAt(x - (int) p.getX(), y - (int) p.getY());
                    }
                    if (comp != null && comp.isShowing()) {
                        return comp;
                    }
                }
            }
        }
        return c;
    }

    /**
     * Determines if any Swing components in Piccolo should receive the given
     * MouseEvent and forwards the event to that component. However,
     * mouseEntered and mouseExited are independent of the buttons. Also, notice
     * the notes on mouseEntered and mouseExited.
     * 
     * @param pSwingMouseEvent
     * @param aEvent
     */
    void dispatchEvent(final PSwingMouseEvent pSwingMouseEvent, final PInputEvent aEvent) {
        Component comp = null;
        Point2D pt = null;
        final PNode pickedNode = pSwingMouseEvent.getPath().getPickedNode();

        // The offsets to put the event in the correct context
        int offX = 0;
        int offY = 0;

        final PNode currentNode = pSwingMouseEvent.getCurrentNode();

        if (currentNode instanceof PSwing) {

            final PSwing swing = (PSwing) currentNode;
            final PNode grabNode = pickedNode;

            if (grabNode.isDescendentOf(canvas.getRoot())) {
                pt = new Point2D.Double(pSwingMouseEvent.getX(), pSwingMouseEvent.getY());
                cameraToLocal(pSwingMouseEvent.getPath().getTopCamera(), pt, grabNode);
                prevPoint = new Point2D.Double(pt.getX(), pt.getY());

                // This is only partially fixed to find the deepest
                // component at pt. It needs to do something like
                // package private method:
                // Container.getMouseEventTarget(int,int,boolean)
                comp = findShowingComponentAt(swing.getComponent(), (int) pt.getX(), (int) pt.getY());

                // We found the right component - but we need to
                // get the offset to put the event in the component's
                // coordinates
                if (comp != null && comp != swing.getComponent()) {
                    for (Component c = comp; c != swing.getComponent(); c = c.getParent()) {
                        offX += c.getLocation().getX();
                        offY += c.getLocation().getY();
                    }
                }

                // Mouse Pressed gives focus - effects Mouse Drags and
                // Mouse Releases
                if (comp != null && pSwingMouseEvent.getID() == MouseEvent.MOUSE_PRESSED) {
                    if (SwingUtilities.isLeftMouseButton(pSwingMouseEvent)) {
                        leftButtonData.setState(swing, pickedNode, comp, offX, offY);
                    }
                    else if (SwingUtilities.isMiddleMouseButton(pSwingMouseEvent)) {
                        middleButtonData.setState(swing, pickedNode, comp, offX, offY);
                    }
                    else if (SwingUtilities.isRightMouseButton(pSwingMouseEvent)) {
                        rightButtonData.setState(swing, pickedNode, comp, offX, offY);
                    }
                }
            }
        }

        // This first case we don't want to give events to just
        // any Swing component - but to the one that got the
        // original mousePressed
        if (pSwingMouseEvent.getID() == MouseEvent.MOUSE_DRAGGED
                || pSwingMouseEvent.getID() == MouseEvent.MOUSE_RELEASED) {

            // LEFT MOUSE BUTTON
            if (SwingUtilities.isLeftMouseButton(pSwingMouseEvent) && leftButtonData.getFocusedComponent() != null) {
                handleButton(pSwingMouseEvent, aEvent, leftButtonData);
            }

            // MIDDLE MOUSE BUTTON
            if (SwingUtilities.isMiddleMouseButton(pSwingMouseEvent) && middleButtonData.getFocusedComponent() != null) {
                handleButton(pSwingMouseEvent, aEvent, middleButtonData);
            }

            // RIGHT MOUSE BUTTON
            if (SwingUtilities.isRightMouseButton(pSwingMouseEvent) && rightButtonData.getFocusedComponent() != null) {
                handleButton(pSwingMouseEvent, aEvent, rightButtonData);
            }
        }
        // This case covers the cases mousePressed, mouseClicked,
        // and mouseMoved events
        else if ((pSwingMouseEvent.getID() == MouseEvent.MOUSE_PRESSED
                || pSwingMouseEvent.getID() == MouseEvent.MOUSE_CLICKED || pSwingMouseEvent.getID() == MouseEvent.MOUSE_MOVED)
                && comp != null) {

            final MouseEvent e_temp = new MouseEvent(comp, pSwingMouseEvent.getID(), pSwingMouseEvent.getWhen(),
                    pSwingMouseEvent.getModifiers(), (int) pt.getX() - offX, (int) pt.getY() - offY, pSwingMouseEvent
                            .getClickCount(), pSwingMouseEvent.isPopupTrigger());

            final PSwingMouseEvent e2 = PSwingMouseEvent.createMouseEvent(e_temp.getID(), e_temp, aEvent);
            dispatchEvent(comp, e2);
        }

        // Now we need to check if an exit or enter event needs to
        // be dispatched - this code is independent of the mouseButtons.
        // I tested in normal Swing to see the correct behavior.
        if (prevComponent != null) {
            // This means mouseExited

            // This shouldn't happen - since we're only getting node events
            if (comp == null || pSwingMouseEvent.getID() == MouseEvent.MOUSE_EXITED) {
                final MouseEvent e_temp = createExitEvent(pSwingMouseEvent);

                final PSwingMouseEvent e2 = PSwingMouseEvent.createMouseEvent(e_temp.getID(), e_temp, aEvent);

                dispatchEvent(prevComponent, e2);
                prevComponent = null;
            }

            // This means mouseExited prevComponent and mouseEntered comp
            else if (prevComponent != comp) {
                MouseEvent e_temp = createExitEvent(pSwingMouseEvent);
                PSwingMouseEvent e2 = PSwingMouseEvent.createMouseEvent(e_temp.getID(), e_temp, aEvent);
                dispatchEvent(prevComponent, e2);

                e_temp = createEnterEvent(comp, pSwingMouseEvent, offX, offY);
                e2 = PSwingMouseEvent.createMouseEvent(e_temp.getID(), e_temp, aEvent);
                comp.dispatchEvent(e2);
            }
        }
        else {
            // This means mouseEntered
            if (comp != null) {
                final MouseEvent e_temp = createEnterEvent(comp, pSwingMouseEvent, offX, offY);
                final PSwingMouseEvent e2 = PSwingMouseEvent.createMouseEvent(e_temp.getID(), e_temp, aEvent);
                dispatchEvent(comp, e2);
            }
        }

        // todo add cursors
        // // We have to manager our own Cursors since this is normally
        // // done on the native side
        // if( comp != cursorComponent &&
        // focusNodeLeft == null &&
        // focusNodeMiddle == null &&
        // focusNodeRight == null ) {
        // if( comp != null ) {
        // cursorComponent = comp;
        // canvas.setCursor( comp.getCursor(), false );
        // }
        // else {
        // cursorComponent = null;
        // canvas.resetCursor();
        // }
        // }

        // Set the previous variables for next time
        prevComponent = comp;

        if (comp != null) {
            prevOff = new Point2D.Double(offX, offY);
        }
    }

    private MouseEvent createEnterEvent(final Component comp, final PSwingMouseEvent e1, final int offX, final int offY) {
        return new MouseEvent(comp, MouseEvent.MOUSE_ENTERED, e1.getWhen(), 0, (int) prevPoint.getX() - offX,
                (int) prevPoint.getY() - offY, e1.getClickCount(), e1.isPopupTrigger());
    }

    private MouseEvent createExitEvent(final PSwingMouseEvent e1) {
        return new MouseEvent(prevComponent, MouseEvent.MOUSE_EXITED, e1.getWhen(), 0, (int) prevPoint.getX()
                - (int) prevOff.getX(), (int) prevPoint.getY() - (int) prevOff.getY(), e1.getClickCount(), e1
                .isPopupTrigger());
    }

    private void handleButton(final PSwingMouseEvent e1, final PInputEvent aEvent, final ButtonData buttonData) {
        Point2D pt;
        if (buttonData.getPNode().isDescendentOf(canvas.getRoot())) {
            pt = new Point2D.Double(e1.getX(), e1.getY());
            cameraToLocal(e1.getPath().getTopCamera(), pt, buttonData.getPNode());
            // todo this probably won't handle viewing through multiple cameras.
            final MouseEvent e_temp = new MouseEvent(buttonData.getFocusedComponent(), e1.getID(), e1.getWhen(), e1
                    .getModifiers(), (int) pt.getX() - buttonData.getOffsetX(), (int) pt.getY()
                    - buttonData.getOffsetY(), e1.getClickCount(), e1.isPopupTrigger());

            final PSwingMouseEvent e2 = PSwingMouseEvent.createMouseEvent(e_temp.getID(), e_temp, aEvent);
            dispatchEvent(buttonData.getFocusedComponent(), e2);
        }
        else {
            dispatchEvent(buttonData.getFocusedComponent(), e1);
        }
        // buttonData.getPSwing().repaint(); //Experiment with SliderExample
        // (from Martin) suggests this line is unnecessary, and a serious
        // problem in performance.
        e1.consume();
        if (e1.getID() == MouseEvent.MOUSE_RELEASED) {
            buttonData.mouseReleased();
        }
    }

    private void dispatchEvent(final Component target, final PSwingMouseEvent event) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                target.dispatchEvent(event);
            }
        });
    }

    private void cameraToLocal(final PCamera topCamera, final Point2D pt, final PNode node) {
        AffineTransform inverse;
        try {
            inverse = topCamera.getViewTransform().createInverse();
        }
        catch (final NoninvertibleTransformException e) {
            throw new PAffineTransformException(e, topCamera.getViewTransform());
        }

        /*
         * Only apply the camera's view transform when this node is a descendant
         * of PLayer
         */
        PNode searchNode = node;
        do {
            searchNode = searchNode.getParent();
            if (searchNode instanceof PLayer) {
                inverse.transform(pt, pt);
                break;
            }
        } while (searchNode != null);

        if (node != null) {
            node.globalToLocal(pt);
        }
        return;
    }

    /**
     * Process a piccolo event and (if active) dispatch the corresponding Swing
     * event.
     * 
     * @param aEvent
     * @param type
     */
    public void processEvent(final PInputEvent aEvent, final int type) {
        if (aEvent.isMouseEvent()) {
            final InputEvent sourceSwingEvent = aEvent.getSourceSwingEvent();
            if (sourceSwingEvent instanceof MouseEvent) {
                final MouseEvent swingMouseEvent = (MouseEvent) sourceSwingEvent;
                final PSwingMouseEvent pSwingMouseEvent = PSwingMouseEvent.createMouseEvent(swingMouseEvent.getID(),
                        swingMouseEvent, aEvent);
                if (!recursing) {
                    recursing = true;
                    dispatchEvent(pSwingMouseEvent, aEvent);
                    if (pSwingMouseEvent.isConsumed()) {
                        aEvent.setHandled(true);
                    }
                    recursing = false;
                }
            }
            else {
                throw new RuntimeException("PInputEvent.getSourceSwingEvent was not a MouseEvent.  Actual event: "
                        + sourceSwingEvent + ", class=" + sourceSwingEvent.getClass().getName());
            }
        }
    }

    /**
     * Internal Utility class for handling button interactivity.
     */
    private static class ButtonData {
        private PSwing focusPSwing = null;
        private PNode focusNode = null;
        private Component focusComponent = null;
        private int focusOffX = 0;
        private int focusOffY = 0;

        public void setState(final PSwing swing, final PNode visualNode, final Component comp, final int offX,
                final int offY) {
            focusPSwing = swing;
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

        public PSwing getPSwing() {
            return focusPSwing;
        }

        public void mouseReleased() {
            focusComponent = null;
            focusNode = null;
        }
    }
}
