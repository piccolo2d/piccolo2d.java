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
package org.piccolo2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.piccolo2d.activities.PTransformActivity;
import org.piccolo2d.util.PAffineTransform;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PDebug;
import org.piccolo2d.util.PDimension;
import org.piccolo2d.util.PObjectOutputStream;
import org.piccolo2d.util.PPaintContext;
import org.piccolo2d.util.PPickPath;
import org.piccolo2d.util.PUtil;


/**
 * <b>PCamera</b> represents a viewport onto a list of layer nodes. Each camera
 * maintains a view transform through which it views these layers. Translating
 * and scaling this view transform is how zooming and panning are implemented.
 * <p>
 * Cameras are also the point through which all PInputEvents enter Piccolo. The
 * canvas coordinate system and the local coordinate system of the topmost
 * camera should always be the same.
 * </p>
 * 
 * @see PLayer
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PCamera extends PNode {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * The property name that identifies a change in the set of this camera's
     * layers (see {@link #getLayer getLayer}, {@link #getLayerCount
     * getLayerCount}, {@link #getLayersReference getLayersReference}). A
     * property change event's new value will be a reference to the list of this
     * nodes layers, but old value will always be null.
     */
    public static final String PROPERTY_LAYERS = "layers";

    /**
     * The property code that identifies a change in the set of this camera's
     * layers (see {@link #getLayer getLayer}, {@link #getLayerCount
     * getLayerCount}, {@link #getLayersReference getLayersReference}). A
     * property change event's new value will be a reference to the list of this
     * nodes layers, but old value will always be null.
     */
    public static final int PROPERTY_CODE_LAYERS = 1 << 11;

    /**
     * The property name that identifies a change in this camera's view
     * transform (see {@link #getViewTransform getViewTransform},
     * {@link #getViewTransformReference getViewTransformReference}). A property
     * change event's new value will be a reference to the view transform, but
     * old value will always be null.
     */
    public static final String PROPERTY_VIEW_TRANSFORM = "viewTransform";

    /**
     * The property code that identifies a change in this camera's view
     * transform (see {@link #getViewTransform getViewTransform},
     * {@link #getViewTransformReference getViewTransformReference}). A property
     * change event's new value will be a reference to the view transform, but
     * old value will always be null.
     */
    public static final int PROPERTY_CODE_VIEW_TRANSFORM = 1 << 12;

    /** Denotes that the view has no constraints. */
    public static final int VIEW_CONSTRAINT_NONE = 0;

    /** Enforces that the view be able to see all nodes in the scene. */
    public static final int VIEW_CONSTRAINT_ALL = 1;

    /** Constrains the the view to be centered on the scene's full bounds. */
    public static final int VIEW_CONSTRAINT_CENTER = 2;

    /** Component which receives repaint notification from this camera. */
    private transient PComponent component;

    /** List of layers viewed by this camera. */
    private transient List/*<PLayer>*/ layers;

    /**
     * Transform applied to layers before they are rendered. This transform
     * differs from the transform applied to the children of this PCamera
     * (sticky objects).
     */
    private final PAffineTransform viewTransform;

    /** Constraints to apply to the viewing area. */
    private int viewConstraint;

    /** Temporary bounds used as an optimization during repaint. */
    private static final PBounds TEMP_REPAINT_RECT = new PBounds();


    /**
     * Create a new camera with an empy list of layers.
     */
    public PCamera() {
        super();
        viewTransform = new PAffineTransform();
        layers = new ArrayList/*<PLayer>*/();
        viewConstraint = VIEW_CONSTRAINT_NONE;
    }


    /**
     * Return the component for this camera, or <code>null</code> if no
     * component has been associated with this camera, as may be the case for
     * internal cameras.
     * 
     * @return the component for this camera, or <code>null</code> if no such
     *    component exists
     */
    public PComponent getComponent() {
        return component;
    }

    /**
     * Set the component for this camera to <code>component</code>. The
     * component, if non-null, receives repaint notification from this camera.
     * 
     * @param component component for this camera
     */
    public void setComponent(final PComponent component) {
        this.component = component;
        invalidatePaint();
    }

    /**
     * Repaint this camera and forward the repaint request to the component
     * for this camera, if it is non-null.
     * 
     * @param localBounds bounds that require repainting, in local coordinates
     * @param sourceNode node from which the repaint message originates, may
     *    be the camera itself
     */
    public void repaintFrom(final PBounds localBounds, final PNode sourceNode) {
        if (getParent() != null) {
            if (sourceNode != this) {
                localToParent(localBounds);
            }
            if (component != null) {
                component.repaint(localBounds);
            }
            getParent().repaintFrom(localBounds, this);
        }
    }

    /**
     * Repaint from one of the camera's layers. The repaint region needs to be
     * transformed from view to local in this case. Unlike most repaint methods
     * in Piccolo2D this one must not modify the <code>viewBounds</code>
     * parameter.
     * 
     * @since 1.3
     * @param viewBounds bounds that require repainting, in view coordinates
     * @param repaintedLayer layer dispatching the repaint notification
     */
    public void repaintFromLayer(final PBounds viewBounds, final PLayer repaintedLayer) {
        TEMP_REPAINT_RECT.setRect(viewBounds);
        viewToLocal(TEMP_REPAINT_RECT);
        if (getBoundsReference().intersects(TEMP_REPAINT_RECT)) {
            Rectangle2D.intersect(TEMP_REPAINT_RECT, getBoundsReference(), TEMP_REPAINT_RECT);
            repaintFrom(TEMP_REPAINT_RECT, repaintedLayer);
        }
    }

    /**
     * Return a reference to the list of layers viewed by this camera.
     * 
     * @return the list of layers viewed by this camera
     */
    public List/*<PLayer>*/ getLayersReference() {
        return layers;
    }

    /**
     * Return the number of layers in the list of layers viewed by this camera.
     * 
     * @return the number of layers in the list of layers viewed by this camera
     */
    public int getLayerCount() {
        return layers.size();
    }

    /**
     * Return the layer at the specified position in the list of layers viewed by this camera.
     * 
     * @param index index of the layer to return
     * @return the layer at the specified position in the list of layers viewed by this camera
     * @throws IndexOutOfBoundsException if the specified index is out of range
     *    (<code>index &lt; 0 || index &gt;= getLayerCount()</code>)
     */
    public PLayer getLayer(final int index) {
        return (PLayer) layers.get(index);
    }

    /**
     * Return the index of the first occurrence of the specified layer in the
     * list of layers viewed by this camera, or <code>-1</code> if the list of layers
     * viewed by this camera does not contain the specified layer.
     * 
     * @param layer layer to search for
     * @return the index of the first occurrence of the specified layer in the
     *    list of layers viewed by this camera, or <code>-1</code> if the list of
     *    layers viewed by this camera does not contain the specified layer
     */
    public int indexOfLayer(final PLayer layer) {
        return layers.indexOf(layer);
    }

    /**
     * Inserts the specified layer at the end of the list of layers viewed by this camera.
     * Layers may be viewed by multiple cameras at once.
     * 
     * @param layer layer to add
     */
    public void addLayer(final PLayer layer) {
        addLayer(layers.size(), layer);
    }

    /**
     * Inserts the specified layer at the specified position in the list of layers viewed by this camera.
     * Layers may be viewed by multiple cameras at once.
     * 
     * @param index index at which the specified layer is to be inserted
     * @param layer layer to add
     * @throws IndexOutOfBoundsException if the specified index is out of range
     *    (<code>index &lt; 0 || index &gt;= getLayerCount()</code>)
     */
    public void addLayer(final int index, final PLayer layer) {
        layers.add(index, layer);
        layer.addCamera(this);
        invalidatePaint();
        firePropertyChange(PROPERTY_CODE_LAYERS, PROPERTY_LAYERS, null, layers);
    }

    /**
     * Removes the first occurrence of the specified layer from the list of
     * layers viewed by this camera, if it is present.
     * 
     * @param layer layer to be removed
     * @return the specified layer
     */
    public PLayer removeLayer(final PLayer layer) {
        layer.removeCamera(this);
        if (layers.remove(layer)) {
            invalidatePaint();
            firePropertyChange(PROPERTY_CODE_LAYERS, PROPERTY_LAYERS, null, layers);
        }
        return layer;
    }

    /**
     * Removes the element at the specified position from the list of layers
     * viewed by this camera.
     * 
     * @param index index of the layer to remove
     * @return the layer previously at the specified position
     * @throws IndexOutOfBoundsException if the specified index is out of range
     *    (<code>index &lt; 0 || index &gt;= getLayerCount()</code>)
     */
    public PLayer removeLayer(final int index) {
        final PLayer layer = (PLayer) layers.remove(index);
        layer.removeCamera(this);
        invalidatePaint();
        firePropertyChange(PROPERTY_CODE_LAYERS, PROPERTY_LAYERS, null, layers);
        return layer;
    }

    /**
     * Return the union of the full bounds of each layer in the list of layers
     * viewed by this camera, or empty bounds if the list of layers viewed by
     * this camera is empty.
     * 
     * @return the union of the full bounds of each layer in the list of layers
     *    viewed by this camera, or empty bounds if the list of layers viewed
     *    by this camera is empty
     */
    public PBounds getUnionOfLayerFullBounds() {
        final PBounds result = new PBounds();
        final int size = layers.size();
        for (int i = 0; i < size; i++) {
            final PLayer each = (PLayer) layers.get(i);
            result.add(each.getFullBoundsReference());
        }
        return result;
    }

    /**
     * Paint this camera and then paint this camera's view through its view
     * transform.
     * 
     * @param paintContext context in which painting occurs
     */
    protected void paint(final PPaintContext paintContext) {
        super.paint(paintContext);

        paintContext.pushClip(getBoundsReference());
        paintContext.pushTransform(viewTransform);

        paintCameraView(paintContext);
        paintDebugInfo(paintContext);

        paintContext.popTransform(viewTransform);
        paintContext.popClip(getBoundsReference());
    }

    /**
     * Paint all the layers in the list of layers viewed by this camera. This method
     * is called after the view transform and clip have been applied to the
     * specified paint context.
     * 
     * @param paintContext context in which painting occurs
     */
    protected void paintCameraView(final PPaintContext paintContext) {
        final int size = layers.size();
        for (int i = 0; i < size; i++) {
            final PLayer each = (PLayer) layers.get(i);
            each.fullPaint(paintContext);
        }
    }

    /**
     * Renders debug info onto the newly painted scene. Things like full bounds
     * and bounds are painted as filled and outlines.
     * 
     * @param paintContext context in which painting occurs
     */
    protected void paintDebugInfo(final PPaintContext paintContext) {
        if (PDebug.debugBounds || PDebug.debugFullBounds) {
            final Graphics2D g2 = paintContext.getGraphics();
            paintContext.setRenderQuality(PPaintContext.LOW_QUALITY_RENDERING);
            g2.setStroke(new BasicStroke(0));
            final ArrayList nodes = new ArrayList();
            final PBounds nodeBounds = new PBounds();

            final Color boundsColor = Color.red;
            final Color fullBoundsColor = new Color(1.0f, 0f, 0f, 0.2f);

            final int size = layers.size();
            for (int i = 0; i < size; i++) {
                ((PLayer) layers.get(i)).getAllNodes(null, nodes);
            }

            final Iterator i = getAllNodes(null, nodes).iterator();

            while (i.hasNext()) {
                final PNode each = (PNode) i.next();

                if (PDebug.debugBounds) {
                    g2.setPaint(boundsColor);
                    nodeBounds.setRect(each.getBoundsReference());

                    if (!nodeBounds.isEmpty()) {
                        each.localToGlobal(nodeBounds);
                        globalToLocal(nodeBounds);
                        if (each == this || each.isDescendentOf(this)) {
                            localToView(nodeBounds);
                        }
                        g2.draw(nodeBounds);
                    }
                }

                if (PDebug.debugFullBounds) {
                    g2.setPaint(fullBoundsColor);
                    nodeBounds.setRect(each.getFullBoundsReference());

                    if (!nodeBounds.isEmpty()) {
                        if (each.getParent() != null) {
                            each.getParent().localToGlobal(nodeBounds);
                        }
                        globalToLocal(nodeBounds);
                        if (each == this || each.isDescendentOf(this)) {
                            localToView(nodeBounds);
                        }
                        g2.fill(nodeBounds);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Pushes this camera onto the specified paint context so that it
     * can be accessed later by {@link PPaintContext#getCamera}.
     * </p>
     */
    public void fullPaint(final PPaintContext paintContext) {
        paintContext.pushCamera(this);
        super.fullPaint(paintContext);
        paintContext.popCamera();
    }

    /**
     * Generate and return a PPickPath for the point x,y specified in the local
     * coord system of this camera. Picking is done with a rectangle, halo
     * specifies how large that rectangle will be.
     * 
     * @param x the x coordinate of the pick path given in local coordinates
     * @param y the y coordinate of the pick path given in local coordinates
     * @param halo the distance from the x,y coordinate that is considered for
     *            inclusion in the pick path
     * 
     * @return the picked path
     */
    public PPickPath pick(final double x, final double y, final double halo) {
        final PBounds b = new PBounds(new Point2D.Double(x, y), -halo, -halo);
        final PPickPath result = new PPickPath(this, b);

        fullPick(result);

        // make sure this camera is pushed.
        if (result.getNodeStackReference().size() == 0) {
            result.pushNode(this);
            result.pushTransform(getTransformReference(false));
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * After the direct children of this camera have been given a chance to be
     * picked all of the layers in the list of layers viewed by this camera are
     * given a chance to be picked.
     * </p>
     * 
     * @return true if any of the layers in the list of layers viewed by this
     *    camera were picked
     */
    protected boolean pickAfterChildren(final PPickPath pickPath) {
        if (intersects(pickPath.getPickBounds())) {
            pickPath.pushTransform(viewTransform);

            if (pickCameraView(pickPath)) {
                return true;
            }

            pickPath.popTransform(viewTransform);
            return true;
        }
        return false;
    }

    /**
     * Try to pick all of the layers in the list of layers viewed by this
     * camera. This method is called after the view transform has been applied
     * to the specified pick path.
     * 
     * @param pickPath pick path
     * @return true if any of the layers in the list of layers viewed by this
     *    camera were picked
     */
    protected boolean pickCameraView(final PPickPath pickPath) {
        final int size = layers.size();
        for (int i = size - 1; i >= 0; i--) {
            final PLayer each = (PLayer) layers.get(i);
            if (each.fullPick(pickPath)) {
                return true;
            }
        }
        return false;
    }

    // ****************************************************************
    // View Transform - Methods for accessing the view transform. The
    // view transform is applied before painting and picking the cameras
    // layers. But not before painting or picking its direct children.
    //
    // Changing the view transform is how zooming and panning are
    // accomplished.
    // ****************************************************************

    /**
     * Return the bounds of this camera in the view coordinate system.
     * 
     * @return the bounds of this camera in the view coordinate system
     */
    public PBounds getViewBounds() {
        return (PBounds) localToView(getBounds());
    }

    /**
     * Animates the camera's view so that the given bounds (in camera layer's
     * coordinate system) are centered within the cameras view bounds. Use this
     * method to point the camera at a given location.
     * 
     * @param centerBounds the targetBounds
     */
    public void setViewBounds(final Rectangle2D centerBounds) {
        animateViewToCenterBounds(centerBounds, true, 0);
    }

    /**
     * Return the scale applied by the view transform to the list of layers
     * viewed by this camera.
     * 
     * @return the scale applied by the view transform to the list of layers
     *    viewed by this camera
     */
    public double getViewScale() {
        return viewTransform.getScale();
    }

    /**
     * Scale the view transform applied to the list of layers viewed by this
     * camera by <code>scale</code> about the point <code>[0, 0]</code>.
     * 
     * @param scale view transform scale
     */
    public void scaleView(final double scale) {
        scaleViewAboutPoint(scale, 0, 0);
    }

    /**
     * Scale the view transform applied to the list of layers viewed by this
     * camera by <code>scale</code> about the specified point
     * <code>[x, y]</code>.
     * 
     * @param scale view transform scale
     * @param x scale about point, x coordinate
     * @param y scale about point, y coordinate
     */
    public void scaleViewAboutPoint(final double scale, final double x, final double y) {
        viewTransform.scaleAboutPoint(scale, x, y);
        applyViewConstraints();
        invalidatePaint();
        firePropertyChange(PROPERTY_CODE_VIEW_TRANSFORM, PROPERTY_VIEW_TRANSFORM, null, viewTransform);
    }

    /**
     * Set the scale applied by the view transform to the list of layers
     * viewed by this camera to <code>scale</code>.
     * 
     * @param scale view transform scale
     */
    public void setViewScale(final double scale) {
        scaleView(scale / getViewScale());
    }

    /**
     * Translate the view transform applied to the list of layers viewed by this
     * camera by <code>[dx, dy]</code>.
     * 
     * @param dx translate delta x
     * @param dy translate delta y
     */
    public void translateView(final double dx, final double dy) {
        viewTransform.translate(dx, dy);
        applyViewConstraints();
        invalidatePaint();
        firePropertyChange(PROPERTY_CODE_VIEW_TRANSFORM, PROPERTY_VIEW_TRANSFORM, null, viewTransform);
    }

    /**
     * Offset the view transform applied to the list of layers viewed by this camera by <code>[dx, dy]</code>. This is
     * NOT effected by the view transform's current scale or rotation. This is implemented by directly adding dx to the
     * m02 position and dy to the m12 position in the affine transform.
     * 
     * @param dx offset delta x
     * @param dy offset delta y
     */
    /*
    public void offsetView(final double dx, final double dy) {
        setViewOffset(viewTransform.getTranslateX() + dx, viewTransform.getTranslateY() + dy);
    }
    */

    /**
     * Set the offset for the view transform applied to the list of layers
     * viewed by this camera to <code>[x, y]</code>.
     * 
     * @param x offset x
     * @param y offset y
     */
    public void setViewOffset(final double x, final double y) {
        viewTransform.setOffset(x, y);
        applyViewConstraints();
        invalidatePaint();
        firePropertyChange(PROPERTY_CODE_VIEW_TRANSFORM, PROPERTY_VIEW_TRANSFORM, null, viewTransform);
    }

    /**
     * Return a copy of the view transform applied to the list of layers
     * viewed by this camera.
     * 
     * @return a copy of the view transform applied to the list of layers
     *    viewed by this camera
     */
    public PAffineTransform getViewTransform() {
        return (PAffineTransform) viewTransform.clone();
    }

    /**
     * Return a reference to the view transform applied to the list of layers
     * viewed by this camera.
     * 
     * @return the view transform applied to the list of layers
     *    viewed by this camera
     */
    public PAffineTransform getViewTransformReference() {
        return viewTransform;
    }

    /**
     * Set the view transform applied to the list of layers
     * viewed by this camera to <code>viewTransform</code>.
     * 
     * @param viewTransform  view transform applied to the list of layers
     *    viewed by this camera
     */
    public void setViewTransform(final AffineTransform viewTransform) {
        this.viewTransform.setTransform(viewTransform);
        applyViewConstraints();
        invalidatePaint();
        firePropertyChange(PROPERTY_CODE_VIEW_TRANSFORM, PROPERTY_VIEW_TRANSFORM, null, this.viewTransform);
    }

    /**
     * Animate the camera's view from its current transform when the activity
     * starts to a new transform that centers the given bounds in the camera
     * layer's coordinate system into the cameras view bounds. If the duration is
     * 0 then the view will be transformed immediately, and null will be
     * returned. Else a new PTransformActivity will get returned that is set to
     * animate the camera's view transform to the new bounds. If shouldScale is
     * true, then the camera will also scale its view so that the given bounds
     * fit fully within the cameras view bounds, else the camera will maintain
     * its original scale.
     * 
     * @param centerBounds the bounds which the animation will pace at the
     *            center of the view
     * @param shouldScaleToFit whether the camera should scale the view while
     *            animating to it
     * @param duration how many milliseconds the animations should take
     * 
     * @return the scheduled PTransformActivity
     */
    public PTransformActivity animateViewToCenterBounds(final Rectangle2D centerBounds, final boolean shouldScaleToFit,
            final long duration) {
        final PBounds viewBounds = getViewBounds();
        final PDimension delta = viewBounds.deltaRequiredToCenter(centerBounds);
        final PAffineTransform newTransform = getViewTransform();
        newTransform.translate(delta.width, delta.height);

        if (shouldScaleToFit) {
            final double s = Math.min(viewBounds.getWidth() / centerBounds.getWidth(), viewBounds.getHeight()
                    / centerBounds.getHeight());
            if (s != Double.POSITIVE_INFINITY && s != 0) {
                newTransform.scaleAboutPoint(s, centerBounds.getCenterX(), centerBounds.getCenterY());
            }
        }

        return animateViewToTransform(newTransform, duration);
    }

    /**
     * Pan the camera's view from its current transform when the activity starts
     * to a new transform so that the view bounds will contain (if possible,
     * intersect if not possible) the new bounds in the camera layers coordinate
     * system. If the duration is 0 then the view will be transformed
     * immediately, and null will be returned. Else a new PTransformActivity
     * will get returned that is set to animate the camera's view transform to
     * the new bounds.
     * 
     * @param panToBounds the bounds to which the view will animate to
     * @param duration the duration of the animation given in milliseconds
     * 
     * @return the scheduled PTransformActivity
     */
    public PTransformActivity animateViewToPanToBounds(final Rectangle2D panToBounds, final long duration) {
        final PBounds viewBounds = getViewBounds();
        final PDimension delta = viewBounds.deltaRequiredToContain(panToBounds);

        if (delta.width != 0 || delta.height != 0) {
            if (duration == 0) {
                translateView(-delta.width, -delta.height);
            }
            else {
                final AffineTransform at = getViewTransform();
                at.translate(-delta.width, -delta.height);
                return animateViewToTransform(at, duration);
            }
        }

        return null;
    }

    /**
     * Animate the cameras view transform from its current value when the
     * activity starts to the new destination transform value.
     * 
     * @param destination the transform to which the view should be transformed
     *            into
     * @param duration the duraiton in milliseconds the animation should take
     * 
     * @return the scheduled PTransformActivity
     */
    public PTransformActivity animateViewToTransform(final AffineTransform destination, final long duration) {
        if (duration == 0) {
            setViewTransform(destination);
            return null;
        }

        final PTransformActivity.Target t = new PTransformActivity.Target() {
            /** {@inheritDoc} */
            public void setTransform(final AffineTransform aTransform) {
                PCamera.this.setViewTransform(aTransform);
            }

            /** {@inheritDoc} */
            public void getSourceMatrix(final double[] aSource) {
                viewTransform.getMatrix(aSource);
            }
        };

        final PTransformActivity transformActivity = new PTransformActivity(duration, PUtil.DEFAULT_ACTIVITY_STEP_RATE,
                t, destination);

        final PRoot r = getRoot();
        if (r != null) {
            r.getActivityScheduler().addActivity(transformActivity);
        }

        return transformActivity;
    }

    // ****************************************************************
    // View Transform Constraints - Methods for setting and applying
    // constraints to the view transform.
    // ****************************************************************

    /**
     * Return the constraint applied to the view. The view constraint will be one of {@link #VIEW_CONSTRAINT_NONE},
     * {@link #VIEW_CONSTRAINT_CENTER}, or {@link #VIEW_CONSTRAINT_CENTER}. Defaults to {@link #VIEW_CONSTRAINT_NONE}.
     * 
     * @return the view constraint being applied to the view
     */
    public int getViewConstraint() {
        return viewConstraint;
    }

    /**
     * Set the view constraint to apply to the view to <code>viewConstraint</code>. The view constraint must be one of
     * {@link #VIEW_CONSTRAINT_NONE}, {@link #VIEW_CONSTRAINT_CENTER}, or {@link #VIEW_CONSTRAINT_CENTER}.
     * 
     * @param viewConstraint constraint to apply to the view
     * @throws IllegalArgumentException if <code>viewConstraint</code> is not one of {@link #VIEW_CONSTRAINT_NONE},
     *         {@link #VIEW_CONSTRAINT_CENTER}, or {@link #VIEW_CONSTRAINT_CENTER}
     */
    public void setViewConstraint(final int viewConstraint) {
        if (viewConstraint != VIEW_CONSTRAINT_NONE && viewConstraint != VIEW_CONSTRAINT_CENTER
                && viewConstraint != VIEW_CONSTRAINT_ALL) {
            throw new IllegalArgumentException("view constraint must be one "
                    + "of VIEW_CONSTRAINT_NONE, VIEW_CONSTRAINT_CENTER, or VIEW_CONSTRAINT_ALL");
        }
        this.viewConstraint = viewConstraint;
        applyViewConstraints();
    }

    /**
     * Transforms the view so that it conforms to the given constraint.
     */
    protected void applyViewConstraints() {
        if (VIEW_CONSTRAINT_NONE == viewConstraint) {
            return;
        }
        final PBounds viewBounds = getViewBounds();
        final PBounds layerBounds = (PBounds) globalToLocal(getUnionOfLayerFullBounds());

        if (VIEW_CONSTRAINT_CENTER == viewConstraint) {
            layerBounds.setRect(layerBounds.getCenterX(), layerBounds.getCenterY(), 0, 0);            
        }
        PDimension constraintDelta = viewBounds.deltaRequiredToContain(layerBounds);
        viewTransform.translate(-constraintDelta.width, -constraintDelta.height);
    }

    // ****************************************************************
    // Camera View Coord System Conversions - Methods to translate from
    // the camera's local coord system (above the camera's view transform) to
    // the
    // camera view coord system (below the camera's view transform). When
    // converting geometry from one of the canvas's layers you must go
    // through the view transform.
    // ****************************************************************

    /**
     * Convert the point from the camera's view coordinate system to the
     * camera's local coordinate system. The given point is modified by this.
     * 
     * @param viewPoint the point to transform to the local coordinate system
     *            from the view's coordinate system
     * @return the transformed point
     */
    public Point2D viewToLocal(final Point2D viewPoint) {
        return viewTransform.transform(viewPoint, viewPoint);
    }

    /**
     * Convert the dimension from the camera's view coordinate system to the
     * camera's local coordinate system. The given dimension is modified by
     * this.
     * 
     * @param viewDimension the dimension to transform from the view system to
     *            the local coordinate system
     * 
     * @return returns the transformed dimension
     */
    public Dimension2D viewToLocal(final Dimension2D viewDimension) {
        return viewTransform.transform(viewDimension, viewDimension);
    }

    /**
     * Convert the rectangle from the camera's view coordinate system to the
     * camera's local coordinate system. The given rectangle is modified by this
     * method.
     * 
     * @param viewRectangle the rectangle to transform from view to local
     *            coordinate System
     * @return the transformed rectangle
     */
    public Rectangle2D viewToLocal(final Rectangle2D viewRectangle) {
        return viewTransform.transform(viewRectangle, viewRectangle);
    }

    /**
     * Convert the point from the camera's local coordinate system to the
     * camera's view coordinate system. The given point is modified by this
     * method.
     * 
     * @param localPoint point to transform from local to view coordinate system
     * @return the transformed point
     */
    public Point2D localToView(final Point2D localPoint) {
        return viewTransform.inverseTransform(localPoint, localPoint);
    }

    /**
     * Convert the dimension from the camera's local coordinate system to the
     * camera's view coordinate system. The given dimension is modified by this
     * method.
     * 
     * @param localDimension the dimension to transform from local to view
     *            coordinate systems
     * @return the transformed dimension
     */
    public Dimension2D localToView(final Dimension2D localDimension) {
        return viewTransform.inverseTransform(localDimension, localDimension);
    }

    /**
     * Convert the rectangle from the camera's local coordinate system to the
     * camera's view coordinate system. The given rectangle is modified by this
     * method.
     * 
     * @param localRectangle the rectangle to transform from local to view
     *            coordinate system
     * @return the transformed rectangle
     */
    public Rectangle2D localToView(final Rectangle2D localRectangle) {
        return viewTransform.inverseTransform(localRectangle, localRectangle);
    }

    // ****************************************************************
    // Serialization - Cameras conditionally serialize their layers.
    // This means that only the layer references that were unconditionally
    // (using writeObject) serialized by someone else will be restored
    // when the camera is unserialized.
    // ****************************************************************/

    /**
     * Write this camera and all its children out to the given stream. Note that
     * the cameras layers are written conditionally, so they will only get
     * written out if someone else writes them unconditionally.
     * 
     * @param out the PObjectOutputStream to which this camera should be
     *            serialized
     * @throws IOException if an error occured writing to the output stream
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        if (!(out instanceof PObjectOutputStream)) {
            throw new RuntimeException("cannot serialize PCamera to a non PObjectOutputStream");
        }
        out.defaultWriteObject();

        final int count = getLayerCount();
        for (int i = 0; i < count; i++) {
            ((PObjectOutputStream) out).writeConditionalObject(layers.get(i));
        }

        out.writeObject(Boolean.FALSE);
        ((PObjectOutputStream) out).writeConditionalObject(component);
    }

    /**
     * Deserializes this PCamera from the ObjectInputStream.
     * 
     * @param in the source ObjectInputStream
     * @throws IOException when error occurs during read
     * @throws ClassNotFoundException if the stream attempts to deserialize a
     *             missing class
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        layers = new ArrayList();

        while (true) {
            final Object each = in.readObject();
            if (each != null) {
                if (each.equals(Boolean.FALSE)) {
                    break;
                }
                else {
                    layers.add(each);
                }
            }
        }

        component = (PComponent) in.readObject();
    }
}
