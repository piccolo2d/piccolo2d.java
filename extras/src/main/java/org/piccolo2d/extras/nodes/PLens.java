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
package org.piccolo2d.extras.nodes;

import java.awt.Color;
import java.awt.Paint;

import java.awt.geom.Rectangle2D;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.piccolo2d.PCamera;
import org.piccolo2d.PLayer;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PDragEventHandler;
import org.piccolo2d.nodes.PPath;


/**
 * <b>PLens</b> is a simple default lens implementation for Piccolo2D. See
 * piccolo2d/examples LensExample for one possible use of this lens. Lens's are
 * often application specific, it may be easiest to study this code, and then
 * implement your own custom lens using the general principles illustrated here.
 * <p>
 * The basic design here is to add a PCamera as the child of a PNode (the lens
 * node). The camera is the viewing part of the lens, and the node is the title
 * bar that can be used to move the lens around. Users of this lens will
 * probably want to set up some lens specific event handler and attach it to the
 * camera.
 * </p>
 * <p>
 * A lens also needs a layer that it will look at (it should not be the same as
 * the layer that it's added to because then it will draw itself in a recursive
 * loop. Last of all the PLens will need to be added to the PCanvas layer (so
 * that it can be seen by the main camera).
 * </p>
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PLens extends PNode {

    private static final long serialVersionUID = 1L;
    private final PPath dragBar;
    private final Rectangle2D dragBarRect;
    private final PCamera camera;
    private final transient PDragEventHandler lensDragger;

    /** The height of the drag bar. */
    public static final double LENS_DRAGBAR_HEIGHT = 20;

    /** Default paint to use for the drag bar. */
    public static final Paint DEFAULT_DRAGBAR_PAINT = Color.DARK_GRAY;

    /** Default paint to use when drawing the background of the lens. */
    public static final Paint DEFAULT_LENS_PAINT = Color.LIGHT_GRAY;

    /**
     * Constructs the default PLens.
     */
    public PLens() {
        // Drag bar gets resized to fit the available space, so any rectangle
        // will do here
        dragBarRect = new Rectangle2D.Float(0.0f, 0.0f, 1.0f, 1.0f);
        dragBar = new PPath.Float(dragBarRect);
        dragBar.setPaint(DEFAULT_DRAGBAR_PAINT);
        // This forces drag events to percolate up to PLens object
        dragBar.setPickable(false);
        addChild(dragBar);

        camera = new PCamera();
        camera.setPaint(DEFAULT_LENS_PAINT);
        addChild(camera);

        // create an event handler to drag the lens around. Note that this event
        // handler consumes events in case another conflicting event handler has
        // been installed higher up in the heirarchy.
        lensDragger = new PDragEventHandler();
        lensDragger.getEventFilter().setMarksAcceptedEventsAsHandled(true);
        addInputEventListener(lensDragger);

        // When this PLens is dragged around adjust the cameras view transform.
        addPropertyChangeListener(PNode.PROPERTY_TRANSFORM, new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent evt) {
                camera.setViewTransform(getInverseTransform());
            }
        });
    }

    /**
     * Creates the default PLens and attaches the given layer to it.
     * 
     * @param layer layer to attach to this PLens
     */
    public PLens(final PLayer layer) {
        this();
        addLayer(0, layer);
    }

    /**
     * Returns the camera on which this lens is appearing.
     * 
     * @return camera on which lens is appearing
     */
    public PCamera getCamera() {
        return camera;
    }

    /**
     * Returns the drag bar for this lens.
     * 
     * @return this lens' drag bar
     */
    public PPath getDragBar() {
        return dragBar;
    }

    /**
     * Returns the event handler that this lens uses for its drag bar.
     * 
     * @return drag bar's drag event handler
     */
    public PDragEventHandler getLensDraggerHandler() {
        return lensDragger;
    }

    /**
     * Adds the layer to the camera.
     * 
     * @param index index at which to add the layer to the camera
     * @param layer layer to add to the camera
     */
    public void addLayer(final int index, final PLayer layer) {
        camera.addLayer(index, layer);
    }

    /**
     * Removes the provided layer from the camera.
     * 
     * @param layer layer to be removed
     */
    public void removeLayer(final PLayer layer) {
        camera.removeLayer(layer);
    }

    /**
     * When the lens is resized this method gives us a chance to layout the
     * lenses camera child appropriately.
     */
    protected void layoutChildren() {
        dragBar.reset();
        dragBarRect.setRect((float) getX(), (float) getY(), (float) getWidth(), (float) LENS_DRAGBAR_HEIGHT);
        dragBar.append(dragBarRect, false);
        dragBar.closePath();
        camera.setBounds(getX(), getY() + LENS_DRAGBAR_HEIGHT, getWidth(), getHeight() - LENS_DRAGBAR_HEIGHT);
    }
}
