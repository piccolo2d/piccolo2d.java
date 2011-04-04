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

import org.piccolo2d.PNode;
import org.piccolo2d.util.PPickPath;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;


/**
 * Interface allowing PSwing events that originated from swing and are destined
 * for PSwing nodes must conform to.
 */
public interface PSwingEvent {
    /**
     * Returns the x,y position of the event in the local coordinate system of
     * the node the event occurred on.
     * 
     * @return a Point2D object containing the x and y coordinates local to the
     *         node.
     */
    Point2D getLocalPoint();

    /**
     * Returns the horizontal x position of the event in the local coordinate
     * system of the node the event occurred on.
     * 
     * @return x a double indicating horizontal position local to the node.
     */
    double getLocalX();

    /**
     * Returns the vertical y position of the event in the local coordinate
     * system of the node the event occurred on.
     * 
     * @return y a double indicating vertical position local to the node.
     */
    double getLocalY();

    /**
     * Determine the event type.
     * 
     * @return the id
     */
    int getID();

    /**
     * Determine the node the event originated at. If an event percolates up the
     * tree and is handled by an event listener higher up in the tree than the
     * original node that generated the event, this returns the original node.
     * For mouse drag and release events, this is the node that the original
     * matching press event went to - in other words, the event is 'grabbed' by
     * the originating node.
     * 
     * @return the node
     */
    PNode getNode();

    /**
     * Determine the path the event took from the PCanvas down to the visual
     * component.
     * 
     * @return the path
     */
    PPickPath getPath();

    /**
     * Determine the node the event originated at. If an event percolates up the
     * tree and is handled by an event listener higher up in the tree than the
     * original node that generated the event, this returns the original node.
     * For mouse drag and release events, this is the node that the original
     * matching press event went to - in other words, the event is 'grabbed' by
     * the originating node.
     * 
     * @return the node
     */
    PNode getGrabNode();

    /**
     * Return the path from the PCanvas down to the currently grabbed object.
     * 
     * @return the path
     */
    PPickPath getGrabPath();

    /**
     * Get the current node that is under the cursor. This may return a
     * different result then getGrabNode() when in a MOUSE_RELEASED or
     * MOUSE_DRAGGED event.
     * 
     * @return the current node.
     */
    PNode getCurrentNode();

    /**
     * Get the path from the PCanvas down to the visual component currently
     * under the mouse.This may give a different result then getGrabPath()
     * during a MOUSE_DRAGGED or MOUSE_RELEASED operation.
     * 
     * @return the current path.
     */
    PPickPath getCurrentPath();

    /**
     * Calls appropriate method on the listener based on this events ID.
     * 
     * @param listener the MouseListener or MouseMotionListener to dispatch to.
     */
    void dispatchTo(Object listener);

    /**
     * Set the source of this event. As the event is fired up the tree the
     * source of the event will keep changing to reflect the scenegraph object
     * that is firing the event.
     * 
     * @param aSource the source of the event
     */
    void setSource(Object aSource);

    /**
     * Returns this event as a mouse event. This reduces the need to cast
     * instances of this interface when they are known to all extend MouseEvent.
     * 
     * @return this object casted to a MouseEvent
     */
    MouseEvent asMouseEvent();
}