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
package org.piccolo2d.extras.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.ScrollPaneConstants;

import org.piccolo2d.PCamera;
import org.piccolo2d.PCanvas;
import org.piccolo2d.PLayer;
import org.piccolo2d.PNode;
import org.piccolo2d.PRoot;
import org.piccolo2d.util.PAffineTransform;
import org.piccolo2d.util.PBounds;


/**
 * The default scroll director implementation. This default implementation
 * follows the widely accepted model of scrolling - namely the scrollbars
 * control the movement of the window over the document rather than the movement
 * of the document under the window.
 * 
 * @author Lance Good
 */
public class PDefaultScrollDirector implements PScrollDirector, PropertyChangeListener {

    /** The viewport that signals this scroll director. */
    protected PViewport viewPort;

    /** The scrollpane that contains the viewport. */
    protected PScrollPane scrollPane;

    /** The canvas that this class directs. */
    protected PCanvas view;

    /** The canvas' camera. */
    protected PCamera camera;

    /** The canvas' root. */
    protected PRoot root;

    /** Flag to indicate when scrolling is currently in progress. */
    protected boolean scrollInProgress = false;

    /**
     * The default constructor.
     */
    public PDefaultScrollDirector() {
    }

    /**
     * Installs the scroll director and adds the appropriate listeners.
     * 
     * @param targetViewPort viewport on which this director directs
     * @param targetView PCanvas that the viewport looks at
     */
    public void install(final PViewport targetViewPort, final PCanvas targetView) {
        scrollPane = (PScrollPane) targetViewPort.getParent();
        this.viewPort = targetViewPort;
        this.view = targetView;

        if (targetView != null) {
            camera = targetView.getCamera();
            root = targetView.getRoot();
        }

        if (camera != null) {
            camera.addPropertyChangeListener(this);
        }
        if (root != null) {
            root.addPropertyChangeListener(this);
        }

        if (scrollPane != null) {
            scrollPane.revalidate();
        }
    }

    /**
     * Uninstall the scroll director from the viewport.
     */
    public void unInstall() {
        viewPort = null;
        view = null;

        if (camera != null) {
            camera.removePropertyChangeListener(this);
        }
        if (root != null) {
            root.removePropertyChangeListener(this);
        }

        camera = null;
        root = null;
    }

    /**
     * Get the View position given the specified camera bounds.
     * 
     * @param viewBounds The bounds for which the view position will be computed
     * @return The view position
     */
    public Point getViewPosition(final Rectangle2D viewBounds) {
        final Point pos = new Point();
        if (camera != null) {
            // First we compute the union of all the layers
            final PBounds layerBounds = new PBounds();
            final List layers = camera.getLayersReference();
            for (final Iterator i = layers.iterator(); i.hasNext();) {
                final PLayer layer = (PLayer) i.next();
                layerBounds.add(layer.getFullBoundsReference());
            }

            // Then we put the bounds into camera coordinates and
            // union the camera bounds
            camera.viewToLocal(layerBounds);
            layerBounds.add(viewBounds);

            pos.setLocation((int) (viewBounds.getX() - layerBounds.getX() + 0.5), (int) (viewBounds.getY()
                    - layerBounds.getY() + 0.5));
        }

        return pos;
    }

    /**
     * Get the size of the view based on the specified camera bounds.
     * 
     * @param viewBounds The view bounds for which the view size will be
     *            computed
     * @return The view size
     */
    public Dimension getViewSize(final Rectangle2D viewBounds) {
        final Dimension size = new Dimension();
        if (camera != null) {
            // First we compute the union of all the layers
            final PBounds bounds = new PBounds();
            final List layers = camera.getLayersReference();
            for (final Iterator i = layers.iterator(); i.hasNext();) {
                final PLayer layer = (PLayer) i.next();
                bounds.add(layer.getFullBoundsReference());
            }

            // Then we put the bounds into camera coordinates and
            // union the camera bounds
            if (!bounds.isEmpty()) {
                camera.viewToLocal(bounds);
            }
            bounds.add(viewBounds);

            size.setSize((int) (bounds.getWidth() + 0.5), (int) (bounds.getHeight() + 0.5));
        }

        return size;
    }

    /**
     * Set the view position in a manner consistent with standardized scrolling.
     * 
     * @param x The new x position
     * @param y The new y position
     */
    public void setViewPosition(final double x, final double y) {
        // Bail out if scrollInProgress because we can end up with an infinite
        // loop since the scrollbars depend on the camera location
        if (camera == null || scrollInProgress) {
            return;
        }

        scrollInProgress = true;

        // Get the union of all the layers' bounds
        final PBounds layerBounds = new PBounds();
        final List layers = camera.getLayersReference();
        for (final Iterator i = layers.iterator(); i.hasNext();) {
            final PLayer layer = (PLayer) i.next();
            layerBounds.add(layer.getFullBoundsReference());
        }

        final PAffineTransform at = camera.getViewTransform();
        at.transform(layerBounds, layerBounds);

        // Union the camera bounds
        final PBounds viewBounds = camera.getBoundsReference();
        layerBounds.add(viewBounds);

        // Now find the new view position in view coordinates
        final Point2D newPoint = new Point2D.Double(layerBounds.getX() + x, layerBounds.getY() + y);

        // Now transform the new view position into global coords
        camera.localToView(newPoint);

        // Compute the new matrix values to put the camera at the
        // correct location
        final double newX = -(at.getScaleX() * newPoint.getX() + at.getShearX() * newPoint.getY());
        final double newY = -(at.getShearY() * newPoint.getX() + at.getScaleY() * newPoint.getY());

        at.setTransform(at.getScaleX(), at.getShearY(), at.getShearX(), at.getScaleY(), newX, newY);

        // Now actually set the camera's transform
        camera.setViewTransform(at);
        scrollInProgress = false;
    }

    /**
     * Invoked when the camera's view changes, or the bounds of the root or
     * camera changes.
     * 
     * @param pce property change event to examine
     */
    public void propertyChange(final PropertyChangeEvent pce) {
        final boolean isRelevantViewEvent = PCamera.PROPERTY_VIEW_TRANSFORM.equals(pce.getPropertyName());
        final boolean isRelevantBoundsEvent = isBoundsChangedEvent(pce)
                && (pce.getSource() == camera || pce.getSource() == view.getRoot());

        if (isRelevantViewEvent || isRelevantBoundsEvent) {
            if (shouldRevalidateScrollPane()) {
                scrollPane.revalidate();
            }
            else {
                viewPort.fireStateChanged();
            }
        }
    }

    private boolean isBoundsChangedEvent(final PropertyChangeEvent pce) {
        return PNode.PROPERTY_BOUNDS.equals(pce.getPropertyName()) || PNode.PROPERTY_FULL_BOUNDS.equals(pce.getPropertyName());
    }

    /**
     * Should the ScrollPane be revalidated. This occurs when either the scroll
     * bars are showing and should be remove or are not showing and should be
     * added.
     * 
     * @return Whether the scroll pane should be revalidated
     */
    public boolean shouldRevalidateScrollPane() {
        if (camera != null) {
            if (scrollPane.getHorizontalScrollBarPolicy() != ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
                    && scrollPane.getVerticalScrollBarPolicy() != ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED) {
                return false;
            }

            // Get the union of all the layers' bounds
            final PBounds layerBounds = new PBounds();
            final List layers = camera.getLayersReference();
            for (final Iterator i = layers.iterator(); i.hasNext();) {
                final PLayer layer = (PLayer) i.next();
                layerBounds.add(layer.getFullBoundsReference());
            }

            // Put into camera coordinates
            camera.viewToLocal(layerBounds);

            // And union with the camera bounds
            final PBounds cameraBounds = camera.getBoundsReference();
            layerBounds.add(cameraBounds);

            // Truncate these to ints before comparing since
            // that's what the ScrollPane uses
            final int layerWidth = (int) (layerBounds.getWidth() + 0.5);
            final int layerHeight = (int) (layerBounds.getHeight() + 0.5);
            final int cameraWidth = (int) (cameraBounds.getWidth() + 0.5);
            final int cameraHeight = (int) (cameraBounds.getHeight() + 0.5);

            if (scrollPane.getHorizontalScrollBar().isShowing() && layerWidth <= cameraWidth
                    || !scrollPane.getHorizontalScrollBar().isShowing() && layerWidth > cameraWidth
                    || scrollPane.getVerticalScrollBar().isShowing() && layerHeight <= cameraHeight
                    || !scrollPane.getVerticalScrollBar().isShowing() && layerHeight > cameraHeight) {
                return true;
            }
        }
        return false;
    }
}
