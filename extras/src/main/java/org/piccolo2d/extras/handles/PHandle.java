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
package org.piccolo2d.extras.handles;

import java.awt.Color;
import java.awt.Shape;
import java.awt.event.InputEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.piccolo2d.PCamera;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PDragSequenceEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PInputEventFilter;
import org.piccolo2d.extras.util.PLocator;
import org.piccolo2d.extras.util.PNodeLocator;
import org.piccolo2d.nodes.PPath;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PDimension;


/**
 * <b>PHandle</b> is used to modify some aspect of Piccolo when it is dragged.
 * Each handle has a PLocator that it uses to automatically position itself. See
 * PBoundsHandle for an example of a handle that resizes the bounds of another
 * node.
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PHandle extends PPath.Float {
    private final class HandleDragHandler extends PDragSequenceEventHandler {
        protected void startDrag(final PInputEvent event) {
            super.startDrag(event);
            startHandleDrag(event.getPositionRelativeTo(PHandle.this), event);
        }

        protected void drag(final PInputEvent event) {
            super.drag(event);
            final PDimension aDelta = event.getDeltaRelativeTo(PHandle.this);
            if (aDelta.getWidth() != 0 || aDelta.getHeight() != 0) {
                dragHandle(aDelta, event);
            }
        }

        protected void endDrag(final PInputEvent event) {
            super.endDrag(event);
            endHandleDrag(event.getPositionRelativeTo(PHandle.this), event);
        }
    }

    private static final long serialVersionUID = 1L;

    /** The default size for a handle. */
    public static final float DEFAULT_HANDLE_SIZE = 8;
    /** Default shape to use when drawing handles. */
    public static final Shape DEFAULT_HANDLE_SHAPE = new Ellipse2D.Float(0f, 0f, DEFAULT_HANDLE_SIZE, DEFAULT_HANDLE_SIZE);

    /** Default color to paint handles. */
    public static final Color DEFAULT_COLOR = Color.white;

    private PLocator locator;
    private transient PDragSequenceEventHandler handleDragger;

    /**
     * Construct a new handle that will use the given locator to locate itself
     * on its parent node.
     * 
     * @param aLocator locator to use when laying out the handle
     */
    public PHandle(final PLocator aLocator) {
        super(DEFAULT_HANDLE_SHAPE);
        locator = aLocator;
        setPaint(DEFAULT_COLOR);
        installHandleEventHandlers();
    }

    /**
     * Installs the handler that notify its subclasses of handle interaction.
     */
    protected void installHandleEventHandlers() {
        handleDragger = new HandleDragHandler();

        addPropertyChangeListener(PNode.PROPERTY_TRANSFORM, new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent evt) {
                relocateHandle();
            }
        });

        handleDragger.setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
        handleDragger.getEventFilter().setMarksAcceptedEventsAsHandled(true);
        handleDragger.getEventFilter().setAcceptsMouseEntered(false);
        handleDragger.getEventFilter().setAcceptsMouseExited(false);
        // no need for moved events for handle interaction,
        handleDragger.getEventFilter().setAcceptsMouseMoved(false);
        // so reject them so we don't consume them
        addInputEventListener(handleDragger);
    }

    /**
     * Return the event handler that is responsible for the drag handle
     * interaction.
     * 
     * @return current handler for HandleDrag events
     */
    public PDragSequenceEventHandler getHandleDraggerHandler() {
        return handleDragger;
    }

    /**
     * Get the locator that this handle uses to position itself on its parent
     * node.
     * 
     * @return the locator associated with this handle
     */
    public PLocator getLocator() {
        return locator;
    }

    /**
     * Set the locator that this handle uses to position itself on its parent
     * node.
     * 
     * @param locator the locator to assign to this handle
     */
    public void setLocator(final PLocator locator) {
        this.locator = locator;
        invalidatePaint();
        relocateHandle();
    }

    /**
     * Override this method to get notified when the handle starts to get
     * dragged.
     * 
     * @param aLocalPoint point on the handle at which the event occurred
     * @param aEvent the event responsible for starting the dragging
     */
    public void startHandleDrag(final Point2D aLocalPoint, final PInputEvent aEvent) {
    }

    /**
     * Override this method to get notified as the handle is dragged.
     * 
     * @param aLocalDimension size of the drag in handle coordinates
     * @param aEvent event representing the drag
     */
    public void dragHandle(final PDimension aLocalDimension, final PInputEvent aEvent) {
    }

    /**
     * Override this method to get notified when the handle stops getting
     * dragged.
     * 
     * @param aLocalPoint point in handle coordinate system of the end of the
     *            drag
     * @param aEvent event responsible for ending the drag
     */
    public void endHandleDrag(final Point2D aLocalPoint, final PInputEvent aEvent) {
    }

    /**
     * Set's this handle's parent. Handles respond to changes in their parent's
     * bounds by invalidating themselves.
     * 
     * @param newParent the new parent to assign to this handle
     */
    public void setParent(final PNode newParent) {
        super.setParent(newParent);
        relocateHandle();
    }

    /**
     * Forces the handles to reposition themselves using their associated
     * locator.
     */
    public void parentBoundsChanged() {
        relocateHandle();
    }

    /**
     * Force this handle to relocate itself using its locator.
     */
    public void relocateHandle() {
        if (locator == null) {
            return;
        }

        final PBounds b = getBoundsReference();
        final Point2D aPoint = locator.locatePoint(null);

        if (locator instanceof PNodeLocator) {
            final PNode located = ((PNodeLocator) locator).getNode();
            final PNode parent = getParent();

            located.localToGlobal(aPoint);
            globalToLocal(aPoint);

            if (parent != located && parent instanceof PCamera) {
                ((PCamera) parent).viewToLocal(aPoint);
            }
        }

        final double newCenterX = aPoint.getX();
        final double newCenterY = aPoint.getY();

        if (newCenterX != b.getCenterX() || newCenterY != b.getCenterY()) {

            centerBoundsOnPoint(newCenterX, newCenterY);
        }

    }

    /**
     * Deserializes a PHandle from the input stream provided. Ensures tha all
     * event handles are correctly installed.
     * 
     * @param in stream from which to read the handle
     * @throws IOException is thrown if the underlying input stream fails
     * @throws ClassNotFoundException should never happen but can happen if the
     *             classpath gets messed up
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        installHandleEventHandlers();
    }
}