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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.event.EventListenerList;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;

import org.piccolo2d.activities.PActivity;
import org.piccolo2d.activities.PColorActivity;
import org.piccolo2d.activities.PInterpolatingActivity;
import org.piccolo2d.activities.PTransformActivity;
import org.piccolo2d.event.PInputEventListener;
import org.piccolo2d.util.PAffineTransform;
import org.piccolo2d.util.PAffineTransformException;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PNodeFilter;
import org.piccolo2d.util.PObjectOutputStream;
import org.piccolo2d.util.PPaintContext;
import org.piccolo2d.util.PPickPath;
import org.piccolo2d.util.PUtil;


/**
 * <b>PNode</b> is the central abstraction in Piccolo. All objects that are
 * visible on the screen are instances of the node class. All nodes may have
 * other "child" nodes added to them.
 * <p>
 * See edu.umd.piccolo.examples.NodeExample.java for demonstrations of how nodes
 * can be used and how new types of nodes can be created.
 * <P>
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PNode implements Cloneable, Serializable, Printable {
    /**
     * The minimum difference in transparency required before the transparency
     * is allowed to change. Done for efficiency reasons. I doubt very much that
     * the human eye could tell the difference between 0.01 and 0.02
     * transparency.
     */
    private static final float TRANSPARENCY_RESOLUTION = 0.01f;

    /**
     * Allows for future serialization code to understand versioned binary
     * formats.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The property name that identifies a change in this node's client
     * propertie (see {@link #getClientProperty getClientProperty}). In an
     * property change event the new value will be a reference to the map of
     * client properties but old value will always be null.
     */
    public static final String PROPERTY_CLIENT_PROPERTIES = "clientProperties";

    /**
     * The property code that identifies a change in this node's client
     * propertie (see {@link #getClientProperty getClientProperty}). In an
     * property change event the new value will be a reference to the map of
     * client properties but old value will always be null.
     */
    public static final int PROPERTY_CODE_CLIENT_PROPERTIES = 1 << 0;

    /**
     * The property name that identifies a change of this node's bounds (see
     * {@link #getBounds getBounds}, {@link #getBoundsReference
     * getBoundsReference}). In any property change event the new value will be
     * a reference to this node's bounds, but old value will always be null.
     */
    public static final String PROPERTY_BOUNDS = "bounds";

    /**
     * The property code that identifies a change of this node's bounds (see
     * {@link #getBounds getBounds}, {@link #getBoundsReference
     * getBoundsReference}). In any property change event the new value will be
     * a reference to this node's bounds, but old value will always be null.
     */
    public static final int PROPERTY_CODE_BOUNDS = 1 << 1;

    /**
     * The property name that identifies a change of this node's full bounds
     * (see {@link #getFullBounds getFullBounds},
     * {@link #getFullBoundsReference getFullBoundsReference}). In any property
     * change event the new value will be a reference to this node's full bounds
     * cache, but old value will always be null.
     */
    public static final String PROPERTY_FULL_BOUNDS = "fullBounds";

    /**
     * The property code that identifies a change of this node's full bounds
     * (see {@link #getFullBounds getFullBounds},
     * {@link #getFullBoundsReference getFullBoundsReference}). In any property
     * change event the new value will be a reference to this node's full bounds
     * cache, but old value will always be null.
     */
    public static final int PROPERTY_CODE_FULL_BOUNDS = 1 << 2;

    /**
     * The property name that identifies a change of this node's transform (see
     * {@link #getTransform getTransform}, {@link #getTransformReference
     * getTransformReference}). In any property change event the new value will
     * be a reference to this node's transform, but old value will always be
     * null.
     */
    public static final String PROPERTY_TRANSFORM = "transform";

    /**
     * The property code that identifies a change of this node's transform (see
     * {@link #getTransform getTransform}, {@link #getTransformReference
     * getTransformReference}). In any property change event the new value will
     * be a reference to this node's transform, but old value will always be
     * null.
     */
    public static final int PROPERTY_CODE_TRANSFORM = 1 << 3;

    /**
     * The property name that identifies a change of this node's visibility (see
     * {@link #getVisible getVisible}). Both old value and new value will be
     * null in any property change event.
     */
    public static final String PROPERTY_VISIBLE = "visible";

    /**
     * The property code that identifies a change of this node's visibility (see
     * {@link #getVisible getVisible}). Both old value and new value will be
     * null in any property change event.
     */
    public static final int PROPERTY_CODE_VISIBLE = 1 << 4;

    /**
     * The property name that identifies a change of this node's paint (see
     * {@link #getPaint getPaint}). Both old value and new value will be set
     * correctly in any property change event.
     */
    public static final String PROPERTY_PAINT = "paint";

    /**
     * The property code that identifies a change of this node's paint (see
     * {@link #getPaint getPaint}). Both old value and new value will be set
     * correctly in any property change event.
     */
    public static final int PROPERTY_CODE_PAINT = 1 << 5;

    /**
     * The property name that identifies a change of this node's transparency
     * (see {@link #getTransparency getTransparency}). Both old value and new
     * value will be null in any property change event.
     */
    public static final String PROPERTY_TRANSPARENCY = "transparency";

    /**
     * The property code that identifies a change of this node's transparency
     * (see {@link #getTransparency getTransparency}). Both old value and new
     * value will be null in any property change event.
     */
    public static final int PROPERTY_CODE_TRANSPARENCY = 1 << 6;

    /**
     * The property name that identifies a change of this node's pickable status
     * (see {@link #getPickable getPickable}). Both old value and new value will
     * be null in any property change event.
     */
    public static final String PROPERTY_PICKABLE = "pickable";
    /**
     * The property code that identifies a change of this node's pickable status
     * (see {@link #getPickable getPickable}). Both old value and new value will
     * be null in any property change event.
     */
    public static final int PROPERTY_CODE_PICKABLE = 1 << 7;

    /**
     * The property name that identifies a change of this node's children
     * pickable status (see {@link #getChildrenPickable getChildrenPickable}).
     * Both old value and new value will be null in any property change event.
     */
    public static final String PROPERTY_CHILDREN_PICKABLE = "childrenPickable";

    /**
     * The property code that identifies a change of this node's children
     * pickable status (see {@link #getChildrenPickable getChildrenPickable}).
     * Both old value and new value will be null in any property change event.
     */
    public static final int PROPERTY_CODE_CHILDREN_PICKABLE = 1 << 8;

    /**
     * The property name that identifies a change in the set of this node's
     * direct children (see {@link #getChildrenReference getChildrenReference},
     * {@link #getChildrenIterator getChildrenIterator}). In any property change
     * event the new value will be a reference to this node's children, but old
     * value will always be null.
     */
    public static final String PROPERTY_CHILDREN = "children";

    /**
     * The property code that identifies a change in the set of this node's
     * direct children (see {@link #getChildrenReference getChildrenReference},
     * {@link #getChildrenIterator getChildrenIterator}). In any property change
     * event the new value will be a reference to this node's children, but old
     * value will always be null.
     */
    public static final int PROPERTY_CODE_CHILDREN = 1 << 9;

    /**
     * The property name that identifies a change of this node's parent (see
     * {@link #getParent getParent}). Both old value and new value will be set
     * correctly in any property change event.
     */
    public static final String PROPERTY_PARENT = "parent";

    /**
     * The property code that identifies a change of this node's parent (see
     * {@link #getParent getParent}). Both old value and new value will be set
     * correctly in any property change event.
     */
    public static final int PROPERTY_CODE_PARENT = 1 << 10;

    /** Is an optimization for use during repaints. */
    private static final PBounds TEMP_REPAINT_BOUNDS = new PBounds();

    /** The single scene graph delegate that receives low level node events. */
    public static PSceneGraphDelegate SCENE_GRAPH_DELEGATE = null;

    /** Tracks the parent of this node, may be null. */
    private transient PNode parent;

    /** Tracks all immediate child nodes. */
    private List children;

    /** Bounds of the PNode. */
    private final PBounds bounds;

    /** Transform that applies to this node in relation to its parent. */
    private PAffineTransform transform;

    /** The paint to use for the background of this node. */
    private Paint paint;

    /**
     * How Opaque this node should be 1f = fully opaque, 0f = completely
     * transparent.
     */
    private float transparency;

    /** A modifiable set of client properties. */
    private MutableAttributeSet clientProperties;

    /**
     * An optimization that remembers the full bounds of a node rather than
     * computing it every time.
     */
    private PBounds fullBoundsCache;

    /**
     * Mask used when deciding whether to bubble up property change events to
     * parents.
     */
    private int propertyChangeParentMask = 0;

    /** Used to handle property change listeners. */
    private transient SwingPropertyChangeSupport changeSupport;

    /** List of event listeners. */
    private transient EventListenerList listenerList;

    /** Whether this node is pickable or not. */
    private boolean pickable;

    /**
     * Whether to stop processing pick at this node and not bother drilling down
     * into children.
     */
    private boolean childrenPickable;

    /** Whether this node will be rendered. */
    private boolean visible;

    private boolean childBoundsVolatile;

    /** Whether this node needs to be repainted. */
    private boolean paintInvalid;

    /** Whether children need to be repainted. */
    private boolean childPaintInvalid;

    /** Whether this node's bounds have changed, and so needs to be relaid out. */
    private boolean boundsChanged;

    /** Whether this node's full bounds need to be recomputed. */
    private boolean fullBoundsInvalid;

    /** Whether this node's child bounds need to be recomputed. */
    private boolean childBoundsInvalid;

    private boolean occluded;

    /** Stores the name associated to this node. */
    private String name;

    /**
     * toImage fill strategy that stretches the node be as large as possible
     * while still retaining its aspect ratio.
     * 
     * @since 1.3
     */
    public static final int FILL_STRATEGY_ASPECT_FIT = 1;

    /**
     * toImage fill strategy that stretches the node be large enough to cover
     * the image, and centers it.
     * 
     * @since 1.3
     */
    public static final int FILL_STRATEGY_ASPECT_COVER = 2;

    /**
     * toImage fill strategy that stretches the node to be exactly the
     * dimensions of the image. Will result in distortion if the aspect ratios
     * are different.
     * 
     * @since 1.3
     */
    public static final int FILL_STRATEGY_EXACT_FIT = 4;

    /**
     * Creates a new PNode with the given name.
     * 
     * @since 1.3
     * @param newName name to assign to node
     */
    public PNode(final String newName) {
        this();
        setName(newName);
    }

    /**
     * Constructs a new PNode.
     * <P>
     * By default a node's paint is null, and bounds are empty. These values
     * must be set for the node to show up on the screen once it's added to a
     * scene graph.
     */
    public PNode() {
        bounds = new PBounds();
        fullBoundsCache = new PBounds();
        transparency = 1.0f;
        pickable = true;
        childrenPickable = true;
        visible = true;
    }

    // ****************************************************************
    // Animation - Methods to animate this node.
    //
    // Note that animation is implemented by activities (PActivity),
    // so if you need more control over your animation look at the
    // activities package. Each animate method creates an animation that
    // will animate the node from its current state to the new state
    // specified over the given duration. These methods will try to
    // automatically schedule the new activity, but if the node does not
    // descend from the root node when the method is called then the
    // activity will not be scheduled and you must schedule it manually.
    // ****************************************************************

    /**
     * Animate this node's bounds from their current location when the activity
     * starts to the specified bounds. If this node descends from the root then
     * the activity will be scheduled, else the returned activity should be
     * scheduled manually. If two different transform activities are scheduled
     * for the same node at the same time, they will both be applied to the
     * node, but the last one scheduled will be applied last on each frame, so
     * it will appear to have replaced the original. Generally you will not want
     * to do that. Note this method animates the node's bounds, but does not
     * change the node's transform. Use animateTransformToBounds() to animate
     * the node's transform instead.
     * 
     * @param x left of target bounds
     * @param y top of target bounds
     * @param width width of target bounds
     * @param height height of target bounds
     * @param duration amount of time that the animation should take
     * @return the newly scheduled activity
     */
    public PInterpolatingActivity animateToBounds(final double x, final double y, final double width,
            final double height, final long duration) {
        if (duration == 0) {
            setBounds(x, y, width, height);
            return null;
        }

        final PBounds dst = new PBounds(x, y, width, height);

        final PInterpolatingActivity interpolatingActivity = new PInterpolatingActivity(duration,
                PUtil.DEFAULT_ACTIVITY_STEP_RATE) {
            private PBounds src;

            protected void activityStarted() {
                src = getBounds();
                startResizeBounds();
                super.activityStarted();
            }

            public void setRelativeTargetValue(final float zeroToOne) {
                PNode.this.setBounds(src.x + zeroToOne * (dst.x - src.x), src.y + zeroToOne * (dst.y - src.y),
                        src.width + zeroToOne * (dst.width - src.width), src.height + zeroToOne
                                * (dst.height - src.height));
            }

            protected void activityFinished() {
                super.activityFinished();
                endResizeBounds();
            }
        };

        addActivity(interpolatingActivity);
        return interpolatingActivity;
    }

    /**
     * Animate this node from it's current transform when the activity starts a
     * new transform that will fit the node into the given bounds. If this node
     * descends from the root then the activity will be scheduled, else the
     * returned activity should be scheduled manually. If two different
     * transform activities are scheduled for the same node at the same time,
     * they will both be applied to the node, but the last one scheduled will be
     * applied last on each frame, so it will appear to have replaced the
     * original. Generally you will not want to do that. Note this method
     * animates the node's transform, but does not directly change the node's
     * bounds rectangle. Use animateToBounds() to animate the node's bounds
     * rectangle instead.
     * 
     * @param x left of target bounds
     * @param y top of target bounds
     * @param width width of target bounds
     * @param height height of target bounds
     * @param duration amount of time that the animation should take
     * @return the newly scheduled activity
     */
    public PTransformActivity animateTransformToBounds(final double x, final double y, final double width,
            final double height, final long duration) {
        final PAffineTransform t = new PAffineTransform();
        t.setToScale(width / getWidth(), height / getHeight());
        final double scale = t.getScale();
        t.setOffset(x - getX() * scale, y - getY() * scale);
        return animateToTransform(t, duration);
    }

    /**
     * Animate this node's transform from its current location when the activity
     * starts to the specified location, scale, and rotation. If this node
     * descends from the root then the activity will be scheduled, else the
     * returned activity should be scheduled manually. If two different
     * transform activities are scheduled for the same node at the same time,
     * they will both be applied to the node, but the last one scheduled will be
     * applied last on each frame, so it will appear to have replaced the
     * original. Generally you will not want to do that.
     * 
     * @param x the final target x position of node
     * @param y the final target y position of node
     * @param duration amount of time that the animation should take
     * @param scale the final scale for the duration
     * @param theta final theta value (in radians) for the animation
     * @return the newly scheduled activity
     */
    public PTransformActivity animateToPositionScaleRotation(final double x, final double y, final double scale,
            final double theta, final long duration) {
        final PAffineTransform t = getTransform();
        t.setOffset(x, y);
        t.setScale(scale);
        t.setRotation(theta);
        return animateToTransform(t, duration);
    }

    /**
     * Animate this node's transform from its current values when the activity
     * starts to the new values specified in the given transform. If this node
     * descends from the root then the activity will be scheduled, else the
     * returned activity should be scheduled manually. If two different
     * transform activities are scheduled for the same node at the same time,
     * they will both be applied to the node, but the last one scheduled will be
     * applied last on each frame, so it will appear to have replaced the
     * original. Generally you will not want to do that.
     * 
     * @param destTransform the final transform value
     * @param duration amount of time that the animation should take
     * @return the newly scheduled activity
     */
    public PTransformActivity animateToTransform(final AffineTransform destTransform, final long duration) {
        if (duration == 0) {
            setTransform(destTransform);
            return null;
        }
        else {
            final PTransformActivity.Target t = new PTransformActivity.Target() {
                public void setTransform(final AffineTransform aTransform) {
                    PNode.this.setTransform(aTransform);
                }

                public void getSourceMatrix(final double[] aSource) {
                    PNode.this.getTransformReference(true).getMatrix(aSource);
                }
            };

            final PTransformActivity ta = new PTransformActivity(duration, PUtil.DEFAULT_ACTIVITY_STEP_RATE, t,
                    destTransform);
            addActivity(ta);
            return ta;
        }
    }

    /**
     * Animate this node's color from its current value to the new value
     * specified. This meathod assumes that this nodes paint property is of type
     * color. If this node descends from the root then the activity will be
     * scheduled, else the returned activity should be scheduled manually. If
     * two different color activities are scheduled for the same node at the
     * same time, they will both be applied to the node, but the last one
     * scheduled will be applied last on each frame, so it will appear to have
     * replaced the original. Generally you will not want to do that.
     * 
     * @param destColor final color value.
     * @param duration amount of time that the animation should take
     * @return the newly scheduled activity
     */
    public PInterpolatingActivity animateToColor(final Color destColor, final long duration) {
        if (duration == 0) {
            setPaint(destColor);
            return null;
        }
        else {
            final PColorActivity.Target t = new PColorActivity.Target() {
                public Color getColor() {
                    return (Color) getPaint();
                }

                public void setColor(final Color color) {
                    setPaint(color);
                }
            };

            final PColorActivity ca = new PColorActivity(duration, PUtil.DEFAULT_ACTIVITY_STEP_RATE, t, destColor);
            addActivity(ca);
            return ca;
        }
    }

    /**
     * Animate this node's transparency from its current value to the new value
     * specified. Transparency values must range from zero to one. If this node
     * descends from the root then the activity will be scheduled, else the
     * returned activity should be scheduled manually. If two different
     * transparency activities are scheduled for the same node at the same time,
     * they will both be applied to the node, but the last one scheduled will be
     * applied last on each frame, so it will appear to have replaced the
     * original. Generally you will not want to do that.
     * 
     * @param zeroToOne final transparency value.
     * @param duration amount of time that the animation should take
     * @return the newly scheduled activity
     */
    public PInterpolatingActivity animateToTransparency(final float zeroToOne, final long duration) {
        if (duration == 0) {
            setTransparency(zeroToOne);
            return null;
        }
        else {
            final float dest = zeroToOne;

            final PInterpolatingActivity ta = new PInterpolatingActivity(duration, PUtil.DEFAULT_ACTIVITY_STEP_RATE) {
                private float source;

                protected void activityStarted() {
                    source = getTransparency();
                    super.activityStarted();
                }

                public void setRelativeTargetValue(final float zeroToOne) {
                    PNode.this.setTransparency(source + zeroToOne * (dest - source));
                }
            };

            addActivity(ta);
            return ta;
        }
    }

    /**
     * Schedule the given activity with the root, note that only scheduled
     * activities will be stepped. If the activity is successfully added true is
     * returned, else false.
     * 
     * @param activity new activity to schedule
     * @return true if the activity is successfully scheduled.
     */
    public boolean addActivity(final PActivity activity) {
        final PRoot r = getRoot();
        if (r != null) {
            return r.addActivity(activity);
        }
        return false;
    }

    // ****************************************************************
    // Client Properties - Methods for managing client properties for
    // this node.
    //
    // Client properties provide a way for programmers to attach
    // extra information to a node without having to subclass it and
    // add new instance variables.
    // ****************************************************************

    /**
     * Return mutable attributed set of client properties associated with this
     * node.
     * 
     * @return the client properties associated to this node
     */
    public MutableAttributeSet getClientProperties() {
        if (clientProperties == null) {
            clientProperties = new SimpleAttributeSet();
        }
        return clientProperties;
    }

    /**
     * Returns the value of the client attribute with the specified key. Only
     * attributes added with <code>addAttribute</code> will return a non-null
     * value.
     * 
     * @param key key to use while fetching client attribute
     * 
     * @return the value of this attribute or null
     */
    public Object getAttribute(final Object key) {
        if (clientProperties == null || key == null) {
            return null;
        }
        else {
            return clientProperties.getAttribute(key);
        }
    }

    /**
     * Add an arbitrary key/value to this node.
     * <p>
     * The <code>get/add attribute</code> methods provide access to a small
     * per-instance attribute set. Callers can use get/add attribute to annotate
     * nodes that were created by another module.
     * <p>
     * If value is null this method will remove the attribute.
     * 
     * @param key to use when adding the attribute
     * @param value value to associate to the new attribute
     */
    public void addAttribute(final Object key, final Object value) {
        if (value == null && clientProperties == null) {
            return;
        }

        final Object oldValue = getAttribute(key);

        if (value != oldValue) {
            if (clientProperties == null) {
                clientProperties = new SimpleAttributeSet();
            }

            if (value == null) {
                clientProperties.removeAttribute(key);
            }
            else {
                clientProperties.addAttribute(key, value);
            }

            if (clientProperties.getAttributeCount() == 0 && clientProperties.getResolveParent() == null) {
                clientProperties = null;
            }

            firePropertyChange(PROPERTY_CODE_CLIENT_PROPERTIES, PROPERTY_CLIENT_PROPERTIES, null, clientProperties);
            firePropertyChange(PROPERTY_CODE_CLIENT_PROPERTIES, key.toString(), oldValue, value);
        }
    }

    /**
     * Returns an enumeration of all keys maped to attribute values values.
     * 
     * @return an Enumeration over attribute keys
     */
    public Enumeration getClientPropertyKeysEnumeration() {
        if (clientProperties == null) {
            return PUtil.NULL_ENUMERATION;
        }
        else {
            return clientProperties.getAttributeNames();
        }
    }

    // convenience methods for attributes

    /**
     * Fetches the value of the requested attribute, returning defaultValue is
     * not found.
     * 
     * @param key attribute to search for
     * @param defaultValue value to return if attribute is not found
     * 
     * @return value of attribute or defaultValue if not found
     */
    public Object getAttribute(final Object key, final Object defaultValue) {
        final Object value = getAttribute(key);
        if (value == null) {
            return defaultValue;
        }

        return value;
    }

    /**
     * Fetches the boolean value of the requested attribute, returning
     * defaultValue is not found.
     * 
     * @param key attribute to search for
     * @param defaultValue value to return if attribute is not found
     * 
     * @return value of attribute or defaultValue if not found
     */
    public boolean getBooleanAttribute(final Object key, final boolean defaultValue) {
        final Boolean value = (Boolean) getAttribute(key);
        if (value == null) {
            return defaultValue;
        }

        return value.booleanValue();
    }

    /**
     * Fetches the integer value of the requested attribute, returning
     * defaultValue is not found.
     * 
     * @param key attribute to search for
     * @param defaultValue value to return if attribute is not found
     * 
     * @return value of attribute or defaultValue if not found
     */
    public int getIntegerAttribute(final Object key, final int defaultValue) {
        final Number value = (Number) getAttribute(key);
        if (value == null) {
            return defaultValue;
        }

        return value.intValue();
    }

    /**
     * Fetches the double value of the requested attribute, returning
     * defaultValue is not found.
     * 
     * @param key attribute to search for
     * @param defaultValue value to return if attribute is not found
     * 
     * @return value of attribute or defaultValue if not found
     */
    public double getDoubleAttribute(final Object key, final double defaultValue) {
        final Number value = (Number) getAttribute(key);
        if (value == null) {
            return defaultValue;
        }

        return value.doubleValue();
    }

    // ****************************************************************
    // Copying - Methods for copying this node and its descendants.
    // Copying is implemented in terms of serialization.
    // ****************************************************************

    /**
     * The copy method copies this node and all of its descendants. Note that
     * copying is implemented in terms of java serialization. See the
     * serialization notes for more information.
     * 
     * @return new copy of this node or null if the node was not serializable
     */
    public Object clone() {
        try {
            final byte[] ser = PObjectOutputStream.toByteArray(this);
            return new ObjectInputStream(new ByteArrayInputStream(ser)).readObject();
        }
        catch (final IOException e) {
            return null;
        }
        catch (final ClassNotFoundException e) {
            return null;
        }
    }

    // ****************************************************************
    // Coordinate System Conversions - Methods for converting
    // geometry between this nodes local coordinates and the other
    // major coordinate systems.
    //
    // Each nodes has an affine transform that it uses to define its
    // own coordinate system. For example if you create a new node and
    // add it to the canvas it will appear in the upper right corner. Its
    // coordinate system matches the coordinate system of its parent
    // (the root node) at this point. But if you move this node by calling
    // node.translate() the nodes affine transform will be modified and the
    // node will appear at a different location on the screen. The node
    // coordinate system no longer matches the coordinate system of its
    // parent.
    //
    // This is useful because it means that the node's methods for
    // rendering and picking don't need to worry about the fact that
    // the node has been moved to another position on the screen, they
    // keep working just like they did when it was in the upper right
    // hand corner of the screen.
    //
    // The problem is now that each node defines its own coordinate
    // system it is difficult to compare the positions of two node with
    // each other. These methods are all meant to help solve that problem.
    //
    // The terms used in the methods are as follows:
    //
    // local - The local or base coordinate system of a node.
    // parent - The coordinate system of a node's parent
    // global - The topmost coordinate system, above the root node.
    //
    // Normally when comparing the positions of two nodes you will
    // convert the local position of each node to the global coordinate
    // system, and then compare the positions in that common coordinate
    // system.
    // ***************************************************************

    /**
     * Transform the given point from this node's local coordinate system to its
     * parent's local coordinate system. Note that this will modify the point
     * parameter.
     * 
     * @param localPoint point in local coordinate system to be transformed.
     * @return point in parent's local coordinate system
     */
    public Point2D localToParent(final Point2D localPoint) {
        if (transform == null) {
            return localPoint;
        }
        return transform.transform(localPoint, localPoint);
    }

    /**
     * Transform the given dimension from this node's local coordinate system to
     * its parent's local coordinate system. Note that this will modify the
     * dimension parameter.
     * 
     * @param localDimension dimension in local coordinate system to be
     *            transformed.
     * @return dimension in parent's local coordinate system
     */
    public Dimension2D localToParent(final Dimension2D localDimension) {
        if (transform == null) {
            return localDimension;
        }
        return transform.transform(localDimension, localDimension);
    }

    /**
     * Transform the given rectangle from this node's local coordinate system to
     * its parent's local coordinate system. Note that this will modify the
     * rectangle parameter.
     * 
     * @param localRectangle rectangle in local coordinate system to be
     *            transformed.
     * @return rectangle in parent's local coordinate system
     */
    public Rectangle2D localToParent(final Rectangle2D localRectangle) {
        if (transform == null) {
            return localRectangle;
        }
        return transform.transform(localRectangle, localRectangle);
    }

    /**
     * Transform the given point from this node's parent's local coordinate
     * system to the local coordinate system of this node. Note that this will
     * modify the point parameter.
     * 
     * @param parentPoint point in parent's coordinate system to be transformed.
     * @return point in this node's local coordinate system
     */
    public Point2D parentToLocal(final Point2D parentPoint) {
        if (transform == null) {
            return parentPoint;
        }

        return transform.inverseTransform(parentPoint, parentPoint);
    }

    /**
     * Transform the given dimension from this node's parent's local coordinate
     * system to the local coordinate system of this node. Note that this will
     * modify the dimension parameter.
     * 
     * @param parentDimension dimension in parent's coordinate system to be
     *            transformed.
     * @return dimension in this node's local coordinate system
     */
    public Dimension2D parentToLocal(final Dimension2D parentDimension) {
        if (transform == null) {
            return parentDimension;
        }
        return transform.inverseTransform(parentDimension, parentDimension);
    }

    /**
     * Transform the given rectangle from this node's parent's local coordinate
     * system to the local coordinate system of this node. Note that this will
     * modify the rectangle parameter.
     * 
     * @param parentRectangle rectangle in parent's coordinate system to be
     *            transformed.
     * @return rectangle in this node's local coordinate system
     */
    public Rectangle2D parentToLocal(final Rectangle2D parentRectangle) {
        if (transform == null) {
            return parentRectangle;
        }
        return transform.inverseTransform(parentRectangle, parentRectangle);
    }

    /**
     * Transform the given point from this node's local coordinate system to the
     * global coordinate system. Note that this will modify the point parameter.
     * 
     * @param localPoint point in local coordinate system to be transformed.
     * @return point in global coordinates
     */
    public Point2D localToGlobal(final Point2D localPoint) {
        PNode n = this;
        while (n != null) {
            n.localToParent(localPoint);
            n = n.parent;
        }
        return localPoint;
    }

    /**
     * Transform the given dimension from this node's local coordinate system to
     * the global coordinate system. Note that this will modify the dimension
     * parameter.
     * 
     * @param localDimension dimension in local coordinate system to be
     *            transformed.
     * @return dimension in global coordinates
     */
    public Dimension2D localToGlobal(final Dimension2D localDimension) {
        PNode n = this;
        while (n != null) {
            n.localToParent(localDimension);
            n = n.parent;
        }
        return localDimension;
    }

    /**
     * Transform the given rectangle from this node's local coordinate system to
     * the global coordinate system. Note that this will modify the rectangle
     * parameter.
     * 
     * @param localRectangle rectangle in local coordinate system to be
     *            transformed.
     * @return rectangle in global coordinates
     */
    public Rectangle2D localToGlobal(final Rectangle2D localRectangle) {
        PNode n = this;
        while (n != null) {
            n.localToParent(localRectangle);
            n = n.parent;
        }
        return localRectangle;
    }

    /**
     * Transform the given point from global coordinates to this node's local
     * coordinate system. Note that this will modify the point parameter.
     * 
     * @param globalPoint point in global coordinates to be transformed.
     * @return point in this node's local coordinate system.
     */
    public Point2D globalToLocal(final Point2D globalPoint) {
        final PAffineTransform globalTransform = computeGlobalTransform(this);
        return globalTransform.inverseTransform(globalPoint, globalPoint);
    }

    private PAffineTransform computeGlobalTransform(final PNode node) {
        if (node == null) {
            return new PAffineTransform();
        }

        final PAffineTransform parentGlobalTransform = computeGlobalTransform(node.parent);
        if (node.transform != null) {
            parentGlobalTransform.concatenate(node.transform);
        }
        return parentGlobalTransform;
    }

    /**
     * Transform the given dimension from global coordinates to this node's
     * local coordinate system. Note that this will modify the dimension
     * parameter.
     * 
     * @param globalDimension dimension in global coordinates to be transformed.
     * @return dimension in this node's local coordinate system.
     */
    public Dimension2D globalToLocal(final Dimension2D globalDimension) {
        if (parent != null) {
            parent.globalToLocal(globalDimension);
        }
        return parentToLocal(globalDimension);
    }

    /**
     * Transform the given rectangle from global coordinates to this node's
     * local coordinate system. Note that this will modify the rectangle
     * parameter.
     * 
     * @param globalRectangle rectangle in global coordinates to be transformed.
     * @return rectangle in this node's local coordinate system.
     */
    public Rectangle2D globalToLocal(final Rectangle2D globalRectangle) {
        if (parent != null) {
            parent.globalToLocal(globalRectangle);
        }
        return parentToLocal(globalRectangle);
    }

    /**
     * Return the transform that converts local coordinates at this node to the
     * global coordinate system.
     * 
     * @param dest PAffineTransform to transform to global coordinates
     * @return The concatenation of transforms from the top node down to this
     *         node.
     */
    public PAffineTransform getLocalToGlobalTransform(final PAffineTransform dest) {
        PAffineTransform result = dest;
        if (parent != null) {
            result = parent.getLocalToGlobalTransform(result);
            if (transform != null) {
                result.concatenate(transform);
            }
        }
        else if (dest == null) {
            result = getTransform();
        }
        else if (transform != null) {
            result.setTransform(transform);
        }
        else {
            result.setToIdentity();
        }

        return result;
    }

    /**
     * Return the transform that converts global coordinates to local
     * coordinates of this node.
     * 
     * @param dest PAffineTransform to transform from global to local
     * 
     * @return The inverse of the concatenation of transforms from the root down
     *         to this node.
     */
    public PAffineTransform getGlobalToLocalTransform(final PAffineTransform dest) {
        PAffineTransform result = getLocalToGlobalTransform(dest);
        try {
            result.setTransform(result.createInverse());
        }
        catch (final NoninvertibleTransformException e) {
            throw new PAffineTransformException(e, result);
        }
        return result;
    }

    // ****************************************************************
    // Event Listeners - Methods for adding and removing event listeners
    // from a node.
    //
    // Here methods are provided to add property change listeners and
    // input event listeners. The property change listeners are notified
    // when certain properties of this node change, and the input event
    // listeners are notified when the nodes receives new key and mouse
    // events.
    // ****************************************************************

    /**
     * Return the list of event listeners associated with this node.
     * 
     * @return event listener list or null
     */
    public EventListenerList getListenerList() {
        return listenerList;
    }

    /**
     * Adds the specified input event listener to receive input events from this
     * node.
     * 
     * @param listener the new input listener
     */
    public void addInputEventListener(final PInputEventListener listener) {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        getListenerList().add(PInputEventListener.class, listener);
    }

    /**
     * Removes the specified input event listener so that it no longer receives
     * input events from this node.
     * 
     * @param listener the input listener to remove
     */
    public void removeInputEventListener(final PInputEventListener listener) {
        if (listenerList == null) {
            return;
        }
        getListenerList().remove(PInputEventListener.class, listener);
        if (listenerList.getListenerCount() == 0) {
            listenerList = null;
        }
    }

    /**
     * Add a PropertyChangeListener to the listener list. The listener is
     * registered for all properties. See the fields in PNode and subclasses
     * that start with PROPERTY_ to find out which properties exist.
     * 
     * @param listener the PropertyChangeListener to be added
     */
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        if (changeSupport == null) {
            changeSupport = new SwingPropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Add a PropertyChangeListener for a specific property. The listener will
     * be invoked only when a call on firePropertyChange names that specific
     * property. See the fields in PNode and subclasses that start with
     * PROPERTY_ to find out which properties are supported.
     * 
     * @param propertyName The name of the property to listen on.
     * @param listener the PropertyChangeListener to be added
     */
    public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
        if (listener == null) {
            return;
        }
        if (changeSupport == null) {
            changeSupport = new SwingPropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Remove a PropertyChangeListener from the listener list. This removes a
     * PropertyChangeListener that was registered for all properties.
     * 
     * @param listener the PropertyChangeListener to be removed
     */
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        if (changeSupport != null) {
            changeSupport.removePropertyChangeListener(listener);
        }
    }

    /**
     * Remove a PropertyChangeListener for a specific property.
     * 
     * @param propertyName the name of the property that was listened on.
     * @param listener the PropertyChangeListener to be removed
     */
    public void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
        if (listener == null) {
            return;
        }
        if (changeSupport == null) {
            return;
        }
        changeSupport.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * Return an array of all the property change listeners added to this node.
     * <p>
     * If some listeners have been added with a named property, then
     * the returned array will be a mixture of PropertyChangeListeners
     * and <code>PropertyChangeListenerProxy</code>s. If the calling
     * method is interested in distinguishing the listeners then it must
     * test each element to see if it is a <code>PropertyChangeListenerProxy</code>,
     * perform the cast, and examine the parameter.
     *
     * <pre>
     * PropertyChangeListener[] listeners = bean.getPropertyChangeListeners();
     * for (int i = 0; i &lt; listeners.length; i++) {
     *   if (listeners[i] instanceof PropertyChangeListenerProxy) {
     *     PropertyChangeListenerProxy proxy = (PropertyChangeListenerProxy) listeners[i];
     *     if (proxy.getPropertyName().equals("foo")) {
     *       // proxy is a PropertyChangeListener which was associated
     *       // with the property named "foo"
     *     }
     *   }
     * }
     *</pre>
     *
     * @since 3.0.1
     * @return all of the <code>PropertyChangeListener</code>s added or an
     *   empty array if no listeners have been added
     */
    public PropertyChangeListener[] getPropertyChangeListeners() {
        if (changeSupport == null) {
            return new PropertyChangeListener[0];
        }
        return changeSupport.getPropertyChangeListeners();
    }

    /**
     * Return an array of all the property change listeners which have been
     * associated with the named property.
     *
     * @since 3.0.1
     * @param propertyName the name of the property being listened to
     * @return all of the <code>PropertyChangeListener</code>s associated with
     *   the named property.  If no such listeners have been added,
     *   or if <code>propertyName</code> is null, an empty array is
     *   returned.
     */
    public PropertyChangeListener[] getPropertyChangeListeners(final String propertyName) {
        if (changeSupport == null) {
            return new PropertyChangeListener[0];
        }
        return changeSupport.getPropertyChangeListeners(propertyName);
    }

    /**
     * Return the propertyChangeParentMask that determines which property change
     * events are forwared to this nodes parent so that its property change
     * listeners will also be notified.
     * 
     * @return mask used for deciding whether to bubble property changes up to
     *         parent
     */
    public int getPropertyChangeParentMask() {
        return propertyChangeParentMask;
    }

    /**
     * Set the propertyChangeParentMask that determines which property change
     * events are forwared to this nodes parent so that its property change
     * listeners will also be notified.
     * 
     * @param propertyChangeParentMask new mask for property change bubble up
     */
    public void setPropertyChangeParentMask(final int propertyChangeParentMask) {
        this.propertyChangeParentMask = propertyChangeParentMask;
    }

    /**
     * Report a bound property update to any registered listeners. No event is
     * fired if old and new are equal and non-null. If the propertyCode exists
     * in this node's propertyChangeParentMask then a property change event will
     * also be fired on this nodes parent.
     * 
     * @param propertyCode The code of the property changed.
     * @param propertyName The name of the property that was changed.
     * @param oldValue The old value of the property.
     * @param newValue The new value of the property.
     */
    protected void firePropertyChange(final int propertyCode, final String propertyName, final Object oldValue,
            final Object newValue) {
        PropertyChangeEvent event = null;

        if (changeSupport != null) {
            event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
            changeSupport.firePropertyChange(event);
        }
        if (parent != null && (propertyCode & propertyChangeParentMask) != 0) {
            if (event == null) {
                event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
            }
            parent.fireChildPropertyChange(event, propertyCode);
        }
    }

    /**
     * Called by child node to forward property change events up the node tree
     * so that property change listeners registered with this node will be
     * notified of property changes of its children nodes. For performance
     * reason only propertyCodes listed in the propertyChangeParentMask are
     * forwarded.
     * 
     * @param event The property change event containing source node and changed
     *            values.
     * @param propertyCode The code of the property changed.
     */
    protected void fireChildPropertyChange(final PropertyChangeEvent event, final int propertyCode) {
        if (changeSupport != null) {
            changeSupport.firePropertyChange(event);
        }
        if (parent != null && (propertyCode & propertyChangeParentMask) != 0) {
            parent.fireChildPropertyChange(event, propertyCode);
        }
    }

    // ****************************************************************
    // Bounds Geometry - Methods for setting and querying the bounds
    // of this node.
    //
    // The bounds of a node store the node's position and size in
    // the nodes local coordinate system. Many node subclasses will need
    // to override the setBounds method so that they can update their
    // internal state appropriately. See PPath for an example.
    //
    // Since the bounds are stored in the local coordinate system
    // they WILL NOT change if the node is scaled, translated, or rotated.
    //
    // The bounds may be accessed with either getBounds, or
    // getBoundsReference. The former returns a copy of the bounds
    // the latter returns a reference to the nodes bounds that should
    // normally not be modified. If a node is marked as volatile then
    // it may modify its bounds before returning them from getBoundsReference,
    // otherwise it may not.
    // ****************************************************************

    /**
     * Return a copy of this node's bounds. These bounds are stored in the local
     * coordinate system of this node and do not include the bounds of any of
     * this node's children.
     * 
     * @return copy of this node's local bounds
     */
    public PBounds getBounds() {
        return (PBounds) getBoundsReference().clone();
    }

    /**
     * Return a direct reference to this node's bounds. These bounds are stored
     * in the local coordinate system of this node and do not include the bounds
     * of any of this node's children. The value returned should not be
     * modified.
     * 
     * @return direct reference to local bounds
     */
    public PBounds getBoundsReference() {
        return bounds;
    }

    /**
     * Notify this node that you will begin to repeatedly call <code>setBounds
     * </code>. When you
     * are done call <code>endResizeBounds</code> to let the node know that you
     * are done.
     */
    public void startResizeBounds() {
    }

    /**
     * Notify this node that you have finished a resize bounds sequence.
     */
    public void endResizeBounds() {
    }

    /**
     * Set's this node's bounds left position, leaving y, width, and height
     * unchanged.
     * 
     * @param x new x position of bounds
     * 
     * @return whether the change was successful
     */
    public boolean setX(final double x) {
        return setBounds(x, getY(), getWidth(), getHeight());
    }

    /**
     * Set's this node's bounds top position, leaving x, width, and height
     * unchanged.
     * 
     * @param y new y position of bounds
     * 
     * @return whether the change was successful
     */
    public boolean setY(final double y) {
        return setBounds(getX(), y, getWidth(), getHeight());
    }

    /**
     * Set's this node's bounds width, leaving x, y, and height unchanged.
     * 
     * @param width new width position of bounds
     * 
     * @return whether the change was successful
     */
    public boolean setWidth(final double width) {
        return setBounds(getX(), getY(), width, getHeight());
    }

    /**
     * Set's this node's bounds height, leaving x, y, and width unchanged.
     * 
     * @param height new height position of bounds
     * 
     * @return whether the change was successful
     */
    public boolean setHeight(final double height) {
        return setBounds(getX(), getY(), getWidth(), height);
    }

    /**
     * Set the bounds of this node to the given value. These bounds are stored
     * in the local coordinate system of this node.
     * 
     * @param newBounds bounds to apply to this node
     * 
     * @return true if the bounds changed.
     */
    public boolean setBounds(final Rectangle2D newBounds) {
        return setBounds(newBounds.getX(), newBounds.getY(), newBounds.getWidth(), newBounds.getHeight());
    }

    /**
     * Set the bounds of this node to the given position and size. These bounds
     * are stored in the local coordinate system of this node.
     * 
     * If the width or height is less then or equal to zero then the bound's
     * empty bit will be set to true.
     * 
     * Subclasses must call the super.setBounds() method.
     * 
     * @param x x position of bounds
     * @param y y position of bounds
     * @param width width to apply to the bounds
     * @param height height to apply to the bounds
     * 
     * @return true if the bounds changed.
     */
    public boolean setBounds(final double x, final double y, final double width, final double height) {
        if (bounds.x != x || bounds.y != y || bounds.width != width || bounds.height != height) {
            bounds.setRect(x, y, width, height);

            if (width <= 0 || height <= 0) {
                bounds.reset();
            }

            internalUpdateBounds(x, y, width, height);
            invalidatePaint();
            signalBoundsChanged();
            return true;
        }
        // Don't put any invalidating code here or else nodes with volatile
        // bounds will
        // create a soft infinite loop (calling Swing.invokeLater()) when they
        // validate
        // their bounds.
        return false;
    }

    /**
     * Gives nodes a chance to update their internal structure before bounds
     * changed notifications are sent. When this message is recived the nodes
     * bounds field will contain the new value.
     * 
     * See PPath for an example that uses this method.
     * 
     * @param x x position of bounds
     * @param y y position of bounds
     * @param width width to apply to the bounds
     * @param height height to apply to the bounds
     */
    protected void internalUpdateBounds(final double x, final double y, final double width, final double height) {
    }

    /**
     * Set the empty bit of this bounds to true.
     */
    public void resetBounds() {
        setBounds(0, 0, 0, 0);
    }

    /**
     * Return the x position (in local coords) of this node's bounds.
     * 
     * @return local x position of bounds
     */
    public double getX() {
        return getBoundsReference().getX();
    }

    /**
     * Return the y position (in local coords) of this node's bounds.
     * 
     * @return local y position of bounds
     */
    public double getY() {
        return getBoundsReference().getY();
    }

    /**
     * Return the width (in local coords) of this node's bounds.
     * 
     * @return local width of bounds
     */
    public double getWidth() {
        return getBoundsReference().getWidth();
    }

    /**
     * Return the height (in local coords) of this node's bounds.
     * 
     * @return local width of bounds
     */
    public double getHeight() {
        return getBoundsReference().getHeight();
    }

    /**
     * Return a copy of the bounds of this node in the global coordinate system.
     * 
     * @return the bounds in global coordinate system.
     */
    public PBounds getGlobalBounds() {
        return (PBounds) localToGlobal(getBounds());
    }

    /**
     * Center the bounds of this node so that they are centered on the given
     * point specified on the local coordinates of this node. Note that this
     * method will modify the nodes bounds, while centerFullBoundsOnPoint will
     * modify the nodes transform.
     * 
     * @param localX x position of point around which to center bounds
     * @param localY y position of point around which to center bounds
     * 
     * @return true if the bounds changed.
     */
    public boolean centerBoundsOnPoint(final double localX, final double localY) {
        final double dx = localX - bounds.getCenterX();
        final double dy = localY - bounds.getCenterY();
        return setBounds(bounds.x + dx, bounds.y + dy, bounds.width, bounds.height);
    }

    /**
     * Center the full bounds of this node so that they are centered on the
     * given point specified on the local coordinates of this nodes parent. Note
     * that this method will modify the nodes transform, while
     * centerBoundsOnPoint will modify the nodes bounds.
     * 
     * @param parentX x position around which to center full bounds
     * @param parentY y position around which to center full bounds
     */
    public void centerFullBoundsOnPoint(final double parentX, final double parentY) {
        final double dx = parentX - getFullBoundsReference().getCenterX();
        final double dy = parentY - getFullBoundsReference().getCenterY();
        offset(dx, dy);
    }

    /**
     * Return true if this node intersects the given rectangle specified in
     * local bounds. If the geometry of this node is complex this method can
     * become expensive, it is therefore recommended that
     * <code>fullIntersects</code> is used for quick rejects before calling this
     * method.
     * 
     * @param localBounds the bounds to test for intersection against
     * @return true if the given rectangle intersects this nodes geometry.
     */
    public boolean intersects(final Rectangle2D localBounds) {
        if (localBounds == null) {
            return true;
        }
        return getBoundsReference().intersects(localBounds);
    }

    // ****************************************************************
    // Full Bounds - Methods for computing and querying the
    // full bounds of this node.
    // 
    // The full bounds of a node store the nodes bounds
    // together with the union of the bounds of all the
    // node's descendants. The full bounds are stored in the parent
    // coordinate system of this node, the full bounds DOES change
    // when you translate, scale, or rotate this node.
    // 
    // The full bounds may be accessed with either getFullBounds, or
    // getFullBoundsReference. The former returns a copy of the full bounds
    // the latter returns a reference to the node's full bounds that should
    // not be modified.
    // ****************************************************************

    /**
     * Return a copy of this node's full bounds. These bounds are stored in the
     * parent coordinate system of this node and they include the union of this
     * node's bounds and all the bounds of it's descendants.
     * 
     * @return a copy of this node's full bounds.
     */
    public PBounds getFullBounds() {
        return (PBounds) getFullBoundsReference().clone();
    }

    /**
     * Return a reference to this node's full bounds cache. These bounds are
     * stored in the parent coordinate system of this node and they include the
     * union of this node's bounds and all the bounds of it's descendants. The
     * bounds returned by this method should not be modified.
     * 
     * @return a reference to this node's full bounds cache.
     */
    public PBounds getFullBoundsReference() {
        validateFullBounds();
        return fullBoundsCache;
    }

    /**
     * Compute and return the full bounds of this node. If the dstBounds
     * parameter is not null then it will be used to return the results instead
     * of creating a new PBounds.
     * 
     * @param dstBounds if not null the new bounds will be stored here
     * @return the full bounds in the parent coordinate system of this node
     */
    public PBounds computeFullBounds(final PBounds dstBounds) {
        final PBounds result = getUnionOfChildrenBounds(dstBounds);
        result.add(getBoundsReference());
        localToParent(result);
        return result;
    }

    /**
     * Compute and return the union of the full bounds of all the children of
     * this node. If the dstBounds parameter is not null then it will be used to
     * return the results instead of creating a new PBounds.
     * 
     * @param dstBounds if not null the new bounds will be stored here
     * @return union of children bounds
     */
    public PBounds getUnionOfChildrenBounds(final PBounds dstBounds) {
        PBounds resultBounds;
        if (dstBounds == null) {
            resultBounds = new PBounds();
        }
        else {
            resultBounds = dstBounds;
            resultBounds.resetToZero();
        }

        final int count = getChildrenCount();
        for (int i = 0; i < count; i++) {
            final PNode each = (PNode) children.get(i);
            resultBounds.add(each.getFullBoundsReference());
        }

        return resultBounds;
    }

    /**
     * Return a copy of the full bounds of this node in the global coordinate
     * system.
     * 
     * @return the full bounds in global coordinate system.
     */
    public PBounds getGlobalFullBounds() {
        final PBounds b = getFullBounds();
        if (parent != null) {
            parent.localToGlobal(b);
        }
        return b;
    }

    /**
     * Return true if the full bounds of this node intersects with the specified
     * bounds.
     * 
     * @param parentBounds the bounds to test for intersection against
     *            (specified in parent's coordinate system)
     * @return true if this nodes full bounds intersect the given bounds.
     */
    public boolean fullIntersects(final Rectangle2D parentBounds) {
        if (parentBounds == null) {
            return true;
        }
        return getFullBoundsReference().intersects(parentBounds);
    }

    // ****************************************************************
    // Bounds Damage Management - Methods used to invalidate and validate
    // the bounds of nodes.
    // ****************************************************************

    /**
     * Return true if this nodes bounds may change at any time. The default
     * behavior is to return false, subclasses that override this method to
     * return true should also override getBoundsReference() and compute their
     * volatile bounds there before returning the reference.
     * 
     * @return true if this node has volatile bounds
     */
    protected boolean getBoundsVolatile() {
        return false;
    }

    /**
     * Return true if this node has a child with volatile bounds.
     * 
     * @return true if this node has a child with volatile bounds
     */
    protected boolean getChildBoundsVolatile() {
        return childBoundsVolatile;
    }

    /**
     * Set if this node has a child with volatile bounds. This should normally
     * be managed automatically by the bounds validation process.
     * 
     * @param childBoundsVolatile true if this node has a descendant with
     *            volatile bounds
     */
    protected void setChildBoundsVolatile(final boolean childBoundsVolatile) {
        this.childBoundsVolatile = childBoundsVolatile;
    }

    /**
     * Return true if this node's bounds have recently changed. This flag will
     * be reset on the next call of validateFullBounds.
     * 
     * @return true if this node's bounds have changed.
     */
    protected boolean getBoundsChanged() {
        return boundsChanged;
    }

    /**
     * Set the bounds chnaged flag. This flag will be reset on the next call of
     * validateFullBounds.
     * 
     * @param boundsChanged true if this nodes bounds have changed.
     */
    protected void setBoundsChanged(final boolean boundsChanged) {
        this.boundsChanged = boundsChanged;
    }

    /**
     * Return true if the full bounds of this node are invalid. This means that
     * the full bounds of this node have changed and need to be recomputed.
     * 
     * @return true if the full bounds of this node are invalid
     */
    protected boolean getFullBoundsInvalid() {
        return fullBoundsInvalid;
    }

    /**
     * Set the full bounds invalid flag. This flag is set when the full bounds
     * of this node need to be recomputed as is the case when this node is
     * transformed or when one of this node's children changes geometry.
     * 
     * @param fullBoundsInvalid true=invalid, false=valid
     */
    protected void setFullBoundsInvalid(final boolean fullBoundsInvalid) {
        this.fullBoundsInvalid = fullBoundsInvalid;
    }

    /**
     * Return true if one of this node's descendants has invalid bounds.
     * 
     * @return whether child bounds are invalid
     */
    protected boolean getChildBoundsInvalid() {
        return childBoundsInvalid;
    }

    /**
     * Set the flag indicating that one of this node's descendants has invalid
     * bounds.
     * 
     * @param childBoundsInvalid true=invalid, false=valid
     */
    protected void setChildBoundsInvalid(final boolean childBoundsInvalid) {
        this.childBoundsInvalid = childBoundsInvalid;
    }

    /**
     * This method should be called when the bounds of this node are changed. It
     * invalidates the full bounds of this node, and also notifies each of this
     * nodes children that their parent's bounds have changed. As a result of
     * this method getting called this nodes layoutChildren will be called.
     */
    public void signalBoundsChanged() {
        invalidateFullBounds();
        setBoundsChanged(true);
        firePropertyChange(PROPERTY_CODE_BOUNDS, PROPERTY_BOUNDS, null, bounds);

        final int count = getChildrenCount();
        for (int i = 0; i < count; i++) {
            final PNode each = (PNode) children.get(i);
            each.parentBoundsChanged();
        }
    }

    /**
     * Invalidate this node's layout, so that later layoutChildren will get
     * called.
     */
    public void invalidateLayout() {
        invalidateFullBounds();
    }

    /**
     * A notification that the bounds of this node's parent have changed.
     */
    protected void parentBoundsChanged() {
    }

    /**
     * Invalidates the full bounds of this node, and sets the child bounds
     * invalid flag on each of this node's ancestors.
     */
    public void invalidateFullBounds() {
        setFullBoundsInvalid(true);

        PNode n = parent;
        while (n != null && !n.getChildBoundsInvalid()) {
            n.setChildBoundsInvalid(true);
            n = n.parent;
        }

        if (SCENE_GRAPH_DELEGATE != null) {
            SCENE_GRAPH_DELEGATE.nodeFullBoundsInvalidated(this);
        }
    }

    /**
     * This method is called to validate the bounds of this node and all of its
     * descendants. It returns true if this nodes bounds or the bounds of any of
     * its descendants are marked as volatile.
     * 
     * @return true if this node or any of its descendants have volatile bounds
     */
    protected boolean validateFullBounds() {
        final boolean boundsVolatile = getBoundsVolatile();

        // 1. Only compute new bounds if invalid flags are set.
        if (fullBoundsInvalid || childBoundsInvalid || boundsVolatile || childBoundsVolatile) {

            // 2. If my bounds are volatile and they have not been changed then
            // signal a change.
            //
            // For most cases this will do nothing, but if a nodes bounds depend
            // on its model, then
            // validate bounds has the responsibility of making the bounds match
            // the models value.
            // For example PPaths validateBounds method makes sure that the
            // bounds are equal to the
            // bounds of the GeneralPath model.
            if (boundsVolatile && !boundsChanged) {
                signalBoundsChanged();
            }


            // 3. If the bounds of on of my decendents are invalidate then
            // validate the bounds of all of my children.
            if (childBoundsInvalid || childBoundsVolatile) {
                childBoundsVolatile = false;
                final int count = getChildrenCount();
                for (int i = 0; i < count; i++) {
                    final PNode each = (PNode) children.get(i);
                    childBoundsVolatile |= each.validateFullBounds();
                }
            }

            // 4. Now that my children's bounds are valid and my own bounds are
            // valid run any layout algorithm here. Note that if you try to
            // layout volatile
            // children piccolo will most likely start a "soft" infinite loop.
            // It won't freeze
            // your program, but it will make an infinite number of calls to
            // SwingUtilities
            // invoke later. You don't want to do that.
            layoutChildren();

            // 5. If the full bounds cache is invalid then recompute the full
            // bounds cache here after our own bounds and the children's bounds
            // have been computed above.
            if (fullBoundsInvalid) {
                final double oldX = fullBoundsCache.x;
                final double oldY = fullBoundsCache.y;
                final double oldWidth = fullBoundsCache.width;
                final double oldHeight = fullBoundsCache.height;
                final boolean oldEmpty = fullBoundsCache.isEmpty();

                // 6. This will call getFullBoundsReference on all of the
                // children. So if the above
                // layoutChildren method changed the bounds of any of the
                // children they will be
                // validated again here.
                fullBoundsCache = computeFullBounds(fullBoundsCache);

                final boolean fullBoundsChanged = fullBoundsCache.x != oldX || fullBoundsCache.y != oldY
                    || fullBoundsCache.width != oldWidth || fullBoundsCache.height != oldHeight
                    || fullBoundsCache.isEmpty() != oldEmpty;

                // 7. If the new full bounds cache differs from the previous
                // cache then
                // tell our parent to invalidate their full bounds. This is how
                // bounds changes
                // deep in the tree percolate up.
                if (fullBoundsChanged) {
                    if (parent != null) {
                        parent.invalidateFullBounds();
                    }
                    firePropertyChange(PROPERTY_CODE_FULL_BOUNDS, PROPERTY_FULL_BOUNDS, null, fullBoundsCache);

                    // 8. If our paint was invalid make sure to repaint our old
                    // full bounds. The
                    // new bounds will be computed later in the validatePaint
                    // pass.
                    if (paintInvalid && !oldEmpty) {
                        TEMP_REPAINT_BOUNDS.setRect(oldX, oldY, oldWidth, oldHeight);
                        repaintFrom(TEMP_REPAINT_BOUNDS, this);
                    }
                }
            }

            // 9. Clear the invalid bounds flags.
            boundsChanged = false;
            fullBoundsInvalid = false;
            childBoundsInvalid = false;
        }

        return boundsVolatile || childBoundsVolatile;
    }

    /**
     * Nodes that apply layout constraints to their children should override
     * this method and do the layout there.
     */
    protected void layoutChildren() {
    }

    // ****************************************************************
    // Node Transform - Methods to manipulate the node's transform.
    // 
    // Each node has a transform that is used to define the nodes
    // local coordinate system. IE it is applied before picking and
    // rendering the node.
    // 
    // The usual way to move nodes about on the canvas is to manipulate
    // this transform, as opposed to changing the bounds of the
    // node.
    // 
    // Since this transform defines the local coordinate system of this
    // node the following methods with affect the global position both
    // this node and all of its descendants.
    // ****************************************************************

    /**
     * Returns the rotation applied by this node's transform in radians. This
     * rotation affects this node and all its descendants. The value returned
     * will be between 0 and 2pi radians.
     * 
     * @return rotation in radians.
     */
    public double getRotation() {
        if (transform == null) {
            return 0;
        }
        return transform.getRotation();
    }

    /**
     * Sets the rotation of this nodes transform in radians. This will affect
     * this node and all its descendents.
     * 
     * @param theta rotation in radians
     */
    public void setRotation(final double theta) {
        rotate(theta - getRotation());
    }

    /**
     * Rotates this node by theta (in radians) about the 0,0 point. This will
     * affect this node and all its descendants.
     * 
     * @param theta the amount to rotate by in radians
     */
    public void rotate(final double theta) {
        rotateAboutPoint(theta, 0, 0);
    }

    /**
     * Rotates this node by theta (in radians), and then translates the node so
     * that the x, y position of its fullBounds stays constant.
     * 
     * @param theta the amount to rotate by in radians
     */
    public void rotateInPlace(final double theta) {
        PBounds b = getFullBoundsReference();
        final double px = b.x;
        final double py = b.y;
        rotateAboutPoint(theta, 0, 0);
        b = getFullBoundsReference();
        offset(px - b.x, py - b.y);
    }

    /**
     * Rotates this node by theta (in radians) about the given point. This will
     * affect this node and all its descendants.
     * 
     * @param theta the amount to rotate by in radians
     * @param point the point about which to rotate
     */
    public void rotateAboutPoint(final double theta, final Point2D point) {
        rotateAboutPoint(theta, point.getX(), point.getY());
    }

    /**
     * Rotates this node by theta (in radians) about the given point. This will
     * affect this node and all its descendants.
     * 
     * @param theta the amount to rotate by in radians
     * @param x the x coordinate of the point around which to rotate
     * @param y the y coordinate of the point around which to rotate
     */
    public void rotateAboutPoint(final double theta, final double x, final double y) {
        getTransformReference(true).rotate(theta, x, y);
        invalidatePaint();
        invalidateFullBounds();
        firePropertyChange(PROPERTY_CODE_TRANSFORM, PROPERTY_TRANSFORM, null, transform);
    }

    /**
     * Return the total amount of rotation applied to this node by its own
     * transform together with the transforms of all its ancestors. The value
     * returned will be between 0 and 2pi radians.
     * 
     * @return the total amount of rotation applied to this node in radians
     */
    public double getGlobalRotation() {
        return getLocalToGlobalTransform(null).getRotation();
    }

    /**
     * Set the global rotation (in radians) of this node. This is implemented by
     * rotating this nodes transform the required amount so that the nodes
     * global rotation is as requested.
     * 
     * @param theta the amount to rotate by in radians relative to the global
     *            coordinate system.
     */
    public void setGlobalRotation(final double theta) {
        if (parent != null) {
            setRotation(theta - parent.getGlobalRotation());
        }
        else {
            setRotation(theta);
        }
    }

    /**
     * Return the scale applied by this node's transform. The scale is effecting
     * this node and all its descendants.
     * 
     * @return scale applied by this nodes transform.
     */
    public double getScale() {
        if (transform == null) {
            return 1;
        }
        return transform.getScale();
    }

    /**
     * Set the scale of this node's transform. The scale will affect this node
     * and all its descendants.
     * 
     * @param scale the scale to set the transform to
     */
    public void setScale(final double scale) {
        if (scale == 0) {
            throw new RuntimeException("Can't set scale to 0");
        }
        scale(scale / getScale());
    }

    /**
     * Scale this nodes transform by the given amount. This will affect this
     * node and all of its descendants.
     * 
     * @param scale the amount to scale by
     */
    public void scale(final double scale) {
        scaleAboutPoint(scale, 0, 0);
    }

    /**
     * Scale this nodes transform by the given amount about the specified point.
     * This will affect this node and all of its descendants.
     * 
     * @param scale the amount to scale by
     * @param point the point to scale about
     */
    public void scaleAboutPoint(final double scale, final Point2D point) {
        scaleAboutPoint(scale, point.getX(), point.getY());
    }

    /**
     * Scale this nodes transform by the given amount about the specified point.
     * This will affect this node and all of its descendants.
     * 
     * @param scale the amount to scale by
     * @param x the x coordinate of the point around which to scale
     * @param y the y coordinate of the point around which to scale
     */
    public void scaleAboutPoint(final double scale, final double x, final double y) {
        getTransformReference(true).scaleAboutPoint(scale, x, y);
        invalidatePaint();
        invalidateFullBounds();
        firePropertyChange(PROPERTY_CODE_TRANSFORM, PROPERTY_TRANSFORM, null, transform);
    }

    /**
     * Return the global scale that is being applied to this node by its
     * transform together with the transforms of all its ancestors.
     * 
     * @return global scale of this node
     */
    public double getGlobalScale() {
        return getLocalToGlobalTransform(null).getScale();
    }

    /**
     * Set the global scale of this node. This is implemented by scaling this
     * nodes transform the required amount so that the nodes global scale is as
     * requested.
     * 
     * @param scale the desired global scale
     */
    public void setGlobalScale(final double scale) {
        if (parent != null) {
            setScale(scale / parent.getGlobalScale());
        }
        else {
            setScale(scale);
        }
    }

    /**
     * Returns the x offset of this node as applied by its transform.
     * 
     * @return x offset of this node as applied by its transform
     */
    public double getXOffset() {
        if (transform == null) {
            return 0;
        }
        return transform.getTranslateX();
    }

    /**
     * Returns the y offset of this node as applied by its transform.
     * 
     * @return y offset of this node as applied by its transform
     */
    public double getYOffset() {
        if (transform == null) {
            return 0;
        }
        return transform.getTranslateY();
    }

    /**
     * Return the offset that is being applied to this node by its transform.
     * This offset effects this node and all of its descendants and is specified
     * in the parent coordinate system. This returns the values that are in the
     * m02 and m12 positions in the affine transform.
     * 
     * @return a point representing the x and y offset
     */
    public Point2D getOffset() {
        if (transform == null) {
            return new Point2D.Double();
        }
        return new Point2D.Double(transform.getTranslateX(), transform.getTranslateY());
    }

    /**
     * Set the offset that is being applied to this node by its transform. This
     * offset effects this node and all of its descendants and is specified in
     * the nodes parent coordinate system. This directly sets the values of the
     * m02 and m12 positions in the affine transform. Unlike "PNode.translate()"
     * it is not effected by the transforms scale.
     * 
     * @param point value of new offset
     */
    public void setOffset(final Point2D point) {
        setOffset(point.getX(), point.getY());
    }

    /**
     * Set the offset that is being applied to this node by its transform. This
     * offset effects this node and all of its descendants and is specified in
     * the nodes parent coordinate system. This directly sets the values of the
     * m02 and m12 positions in the affine transform. Unlike "PNode.translate()"
     * it is not effected by the transforms scale.
     * 
     * @param x amount of x offset
     * @param y amount of y offset
     */
    public void setOffset(final double x, final double y) {
        getTransformReference(true).setOffset(x, y);
        invalidatePaint();
        invalidateFullBounds();
        firePropertyChange(PROPERTY_CODE_TRANSFORM, PROPERTY_TRANSFORM, null, transform);
    }

    /**
     * Offset this node relative to the parents coordinate system, and is NOT
     * effected by this nodes current scale or rotation. This is implemented by
     * directly adding dx to the m02 position and dy to the m12 position in the
     * affine transform.
     * 
     * @param dx amount to add to this nodes current x Offset
     * @param dy amount to add to this nodes current y Offset
     */
    public void offset(final double dx, final double dy) {
        getTransformReference(true);
        setOffset(transform.getTranslateX() + dx, transform.getTranslateY() + dy);
    }

    /**
     * Translate this node's transform by the given amount, using the standard
     * affine transform translate method. This translation effects this node and
     * all of its descendants.
     * 
     * @param dx amount to add to this nodes current x translation
     * @param dy amount to add to this nodes current y translation
     */
    public void translate(final double dx, final double dy) {
        getTransformReference(true).translate(dx, dy);
        invalidatePaint();
        invalidateFullBounds();
        firePropertyChange(PROPERTY_CODE_TRANSFORM, PROPERTY_TRANSFORM, null, transform);
    }

    /**
     * Return the global translation that is being applied to this node by its
     * transform together with the transforms of all its ancestors.
     * 
     * @return the global translation applied to this node
     */
    public Point2D getGlobalTranslation() {
        final Point2D p = getOffset();
        if (parent != null) {
            parent.localToGlobal(p);
        }
        return p;
    }

    /**
     * Set the global translation of this node. This is implemented by
     * translating this nodes transform the required amount so that the nodes
     * global scale is as requested.
     * 
     * @param globalPoint the desired global translation
     */
    public void setGlobalTranslation(final Point2D globalPoint) {
        if (parent != null) {
            parent.getGlobalToLocalTransform(null).transform(globalPoint, globalPoint);
        }
        setOffset(globalPoint);
    }

    /**
     * Transform this nodes transform by the given transform.
     * 
     * @param aTransform the transform to apply.
     */
    public void transformBy(final AffineTransform aTransform) {
        getTransformReference(true).concatenate(aTransform);
        invalidatePaint();
        invalidateFullBounds();
        firePropertyChange(PROPERTY_CODE_TRANSFORM, PROPERTY_TRANSFORM, null, transform);
    }

    /**
     * Linearly interpolates between a and b, based on t. Specifically, it
     * computes lerp(a, b, t) = a + t*(b - a). This produces a result that
     * changes from a (when t = 0) to b (when t = 1).
     * 
     * @param t variable 'time' parameter
     * @param a from point
     * @param b to Point
     * 
     * @return linear interpolation between and b at time interval t (given as #
     *         between 0f and 1f)
     */
    public static double lerp(final double t, final double a, final double b) {
        return a + t * (b - a);
    }

    /**
     * This will calculate the necessary transform in order to make this node
     * appear at a particular position relative to the specified bounding box.
     * The source point specifies a point in the unit square (0, 0) - (1, 1)
     * that represents an anchor point on the corresponding node to this
     * transform. The destination point specifies an anchor point on the
     * reference node. The position method then computes the transform that
     * results in transforming this node so that the source anchor point
     * coincides with the reference anchor point. This can be useful for layout
     * algorithms as it is straightforward to position one object relative to
     * another.
     * <p>
     * For example, If you have two nodes, A and B, and you call
     * 
     * <PRE>
     * Point2D srcPt = new Point2D.Double(1.0, 0.0);
     * Point2D destPt = new Point2D.Double(0.0, 0.0);
     * A.position(srcPt, destPt, B.getGlobalBounds(), 750, null);
     * </PRE>
     * 
     * The result is that A will move so that its upper-right corner is at the
     * same place as the upper-left corner of B, and the transition will be
     * smoothly animated over a period of 750 milliseconds.
     * 
     * @since 1.3
     * @param srcPt The anchor point on this transform's node (normalized to a
     *            unit square)
     * @param destPt The anchor point on destination bounds (normalized to a
     *            unit square)
     * @param destBounds The bounds (in global coordinates) used to calculate
     *            this transform's node
     * @param millis Number of milliseconds over which to perform the animation
     * 
     * @return newly scheduled activity or node if activity could not be
     *         scheduled
     */
    public PActivity animateToRelativePosition(final Point2D srcPt, final Point2D destPt, final Rectangle2D destBounds,
            final int millis) {
        double srcx, srcy;
        double destx, desty;
        double dx, dy;
        Point2D pt1, pt2;

        if (parent == null) {
            return null;
        }
        else {
            // First compute translation amount in global coordinates
            final Rectangle2D srcBounds = getGlobalFullBounds();
            srcx = lerp(srcPt.getX(), srcBounds.getX(), srcBounds.getX() + srcBounds.getWidth());
            srcy = lerp(srcPt.getY(), srcBounds.getY(), srcBounds.getY() + srcBounds.getHeight());
            destx = lerp(destPt.getX(), destBounds.getX(), destBounds.getX() + destBounds.getWidth());
            desty = lerp(destPt.getY(), destBounds.getY(), destBounds.getY() + destBounds.getHeight());

            // Convert vector to local coordinates
            pt1 = new Point2D.Double(srcx, srcy);
            globalToLocal(pt1);
            pt2 = new Point2D.Double(destx, desty);
            globalToLocal(pt2);
            dx = pt2.getX() - pt1.getX();
            dy = pt2.getY() - pt1.getY();

            // Finally, animate change
            final PAffineTransform at = new PAffineTransform(getTransformReference(true));
            at.translate(dx, dy);
            return animateToTransform(at, millis);
        }
    }

    /**
     * Return a copy of the transform associated with this node.
     * 
     * @return copy of this node's transform
     */
    public PAffineTransform getTransform() {
        if (transform == null) {
            return new PAffineTransform();
        }
        else {
            return (PAffineTransform) transform.clone();
        }
    }

    /**
     * Return a reference to the transform associated with this node. This
     * returned transform should not be modified. PNode transforms are created
     * lazily when needed. If you access the transform reference before the
     * transform has been created it may return null. The
     * createNewTransformIfNull parameter is used to specify that the PNode
     * should create a new transform (and assign that transform to the nodes
     * local transform variable) instead of returning null.
     * 
     * @param createNewTransformIfNull if the transform has not been
     *            initialised, should it be?
     * 
     * @return reference to this node's transform
     */
    public PAffineTransform getTransformReference(final boolean createNewTransformIfNull) {
        if (transform == null && createNewTransformIfNull) {
            transform = new PAffineTransform();
        }
        return transform;
    }

    /**
     * Return an inverted copy of the transform associated with this node.
     * 
     * @return inverted copy of this node's transform
     */
    public PAffineTransform getInverseTransform() {
        if (transform == null) {
            return new PAffineTransform();
        }

        try {
            return new PAffineTransform(transform.createInverse());
        }
        catch (final NoninvertibleTransformException e) {
            throw new PAffineTransformException(e, transform);
        }
    }

    /**
     * Set the transform applied to this node.
     * 
     * @param transform the new transform value
     */
    public void setTransform(final AffineTransform transform) {
        if (transform == null) {
            this.transform = null;
        }
        else {
            getTransformReference(true).setTransform(transform);
        }

        invalidatePaint();
        invalidateFullBounds();
        firePropertyChange(PROPERTY_CODE_TRANSFORM, PROPERTY_TRANSFORM, null, this.transform);
    }

    // ****************************************************************
    // Paint Damage Management - Methods used to invalidate the areas of
    // the screen that this node appears in so that they will later get
    // painted.
    // 
    // Generally you will not need to call these invalidate methods
    // when starting out with Piccolo2d because methods such as setPaint
    // already automatically call them for you. You will need to call
    // them when you start creating your own nodes.
    // 
    // When you do create you own nodes the only method that you will
    // normally need to call is invalidatePaint. This method marks the
    // nodes as having invalid paint, the root node's UI cycle will then
    // later discover this damage and report it to the Java repaint manager.
    // 
    // Repainting is normally done with PNode.invalidatePaint() instead of
    // directly calling PNode.repaint() because PNode.repaint() requires
    // the nodes bounds to be computed right away. But with invalidatePaint
    // the bounds computation can be delayed until the end of the root's UI
    // cycle, and this can add up to a bit savings when modifying a
    // large number of nodes all at once.
    // 
    // The other methods here will rarely be called except internally
    // from the framework.
    // ****************************************************************

    /**
     * Return true if this nodes paint is invalid, in which case the node needs
     * to be repainted.
     * 
     * @return true if this node needs to be repainted
     */
    public boolean getPaintInvalid() {
        return paintInvalid;
    }

    /**
     * Mark this node as having invalid paint. If this is set the node will
     * later be repainted. Node this method is most often used internally.
     * 
     * @param paintInvalid true if this node should be repainted
     */
    public void setPaintInvalid(final boolean paintInvalid) {
        this.paintInvalid = paintInvalid;
    }

    /**
     * Return true if this node has a child with invalid paint.
     * 
     * @return true if this node has a child with invalid paint
     */
    public boolean getChildPaintInvalid() {
        return childPaintInvalid;
    }

    /**
     * Mark this node as having a child with invalid paint.
     * 
     * @param childPaintInvalid true if this node has a child with invalid paint
     */
    public void setChildPaintInvalid(final boolean childPaintInvalid) {
        this.childPaintInvalid = childPaintInvalid;
    }

    /**
     * Invalidate this node's paint, and mark all of its ancestors as having a
     * node with invalid paint.
     */
    public void invalidatePaint() {
        setPaintInvalid(true);

        PNode n = parent;
        while (n != null && !n.getChildPaintInvalid()) {
            n.setChildPaintInvalid(true);
            n = n.parent;
        }

        if (SCENE_GRAPH_DELEGATE != null) {
            SCENE_GRAPH_DELEGATE.nodePaintInvalidated(this);
        }
    }

    /**
     * Repaint this node and any of its descendants if they have invalid paint.
     */
    public void validateFullPaint() {
        if (getPaintInvalid()) {
            repaint();
            setPaintInvalid(false);
        }

        if (getChildPaintInvalid()) {
            final int count = getChildrenCount();
            for (int i = 0; i < count; i++) {
                final PNode each = (PNode) children.get(i);
                each.validateFullPaint();
            }
            setChildPaintInvalid(false);
        }
    }

    /**
     * Mark the area on the screen represented by this nodes full bounds as
     * needing a repaint.
     */
    public void repaint() {
        TEMP_REPAINT_BOUNDS.setRect(getFullBoundsReference());
        repaintFrom(TEMP_REPAINT_BOUNDS, this);
    }

    /**
     * Pass the given repaint request up the tree, so that any cameras can
     * invalidate that region on their associated canvas.
     * 
     * @param localBounds the bounds to repaint
     * @param childOrThis if childOrThis does not equal this then this nodes
     *            transform will be applied to the localBounds param
     */
    public void repaintFrom(final PBounds localBounds, final PNode childOrThis) {
        if (parent != null) {
            if (childOrThis != this) {
                localToParent(localBounds);
            }
            else if (!getVisible()) {
                return;
            }
            parent.repaintFrom(localBounds, this);
        }
    }

    // ****************************************************************
    // Occluding - Methods to support occluding optimisation. Not yet
    // complete.
    // ****************************************************************

    /**
     * Returns whether this node is Opaque.
     * 
     * @param boundary boundary to check and see if this node covers completely.
     * 
     * @return true if opaque
     */
    public boolean isOpaque(final Rectangle2D boundary) {
        return false;
    }

    /**
     * Returns whether this node has been flagged as occluded.
     * 
     * @return true if occluded
     */
    public boolean getOccluded() {
        return occluded;
    }

    /**
     * Flags this node as occluded.
     * 
     * @param occluded new value for occluded
     */
    public void setOccluded(final boolean occluded) {
        this.occluded = occluded;
    }

    // ****************************************************************
    // Painting - Methods for painting this node and its children
    // 
    // Painting is how a node defines its visual representation on the
    // screen, and is done in the local coordinate system of the node.
    // 
    // The default painting behavior is to first paint the node, and
    // then paint the node's children on top of the node. If a node
    // needs wants specialised painting behavior it can override:
    // 
    // paint() - Painting here will happen before the children
    // are painted, so the children will be painted on top of painting done
    // here.
    // paintAfterChildren() - Painting here will happen after the children
    // are painted, so it will paint on top of them.
    // 
    // Note that you should not normally need to override fullPaint().
    // 
    // The visible flag can be used to make a node invisible so that
    // it will never get painted.
    // ****************************************************************

    /**
     * Return true if this node is visible, that is if it will paint itself and
     * descendants.
     * 
     * @return true if this node and its descendants are visible.
     */
    public boolean getVisible() {
        return visible;
    }

    /**
     * Set the visibility of this node and its descendants.
     * 
     * @param isVisible true if this node and its descendants are visible
     */
    public void setVisible(final boolean isVisible) {
        if (getVisible() != isVisible) {
            if (!isVisible) {
                repaint();
            }
            visible = isVisible;
            firePropertyChange(PROPERTY_CODE_VISIBLE, PROPERTY_VISIBLE, null, null);
            invalidatePaint();
        }
    }

    /**
     * Return the paint used while painting this node. This value may be null.
     * 
     * @return the paint used while painting this node.
     */
    public Paint getPaint() {
        return paint;
    }

    /**
     * Set the paint used to paint this node, which may be null.
     * 
     * @param newPaint paint that this node should use when painting itself.
     */
    public void setPaint(final Paint newPaint) {
        if (paint == newPaint) {
            return;
        }

        final Paint oldPaint = paint;
        paint = newPaint;
        invalidatePaint();
        firePropertyChange(PROPERTY_CODE_PAINT, PROPERTY_PAINT, oldPaint, paint);
    }

    /**
     * Return the transparency used when painting this node. Note that this
     * transparency is also applied to all of the node's descendants.
     * 
     * @return how transparent this node is 0f = completely transparent, 1f =
     *         completely opaque
     */
    public float getTransparency() {
        return transparency;
    }

    /**
     * Set the transparency used to paint this node. Note that this transparency
     * applies to this node and all of its descendants.
     * 
     * @param newTransparency transparency value for this node. 0f = fully
     *            transparent, 1f = fully opaque
     */
    public void setTransparency(final float newTransparency) {
        if (Math.abs(transparency - newTransparency) > TRANSPARENCY_RESOLUTION) {
            final float oldTransparency = transparency;
            transparency = newTransparency;
            invalidatePaint();
            firePropertyChange(PROPERTY_CODE_TRANSPARENCY, PROPERTY_TRANSPARENCY, new Float(oldTransparency),
                    new Float(newTransparency));
        }
    }

    /**
     * Paint this node behind any of its children nodes. Subclasses that define
     * a different appearance should override this method and paint themselves
     * there.
     * 
     * @param paintContext the paint context to use for painting the node
     */
    protected void paint(final PPaintContext paintContext) {
        if (paint != null) {
            final Graphics2D g2 = paintContext.getGraphics();
            g2.setPaint(paint);
            g2.fill(getBoundsReference());
        }
    }

    /**
     * Paint this node and all of its descendants. Most subclasses do not need
     * to override this method, they should override <code>paint</code> or
     * <code>paintAfterChildren</code> instead.
     * 
     * @param paintContext the paint context to use for painting this node and
     *            its children
     */
    public void fullPaint(final PPaintContext paintContext) {
        if (getVisible() && fullIntersects(paintContext.getLocalClip())) {
            paintContext.pushTransform(transform);
            paintContext.pushTransparency(transparency);

            if (!getOccluded()) {
                paint(paintContext);
            }

            final int count = getChildrenCount();
            for (int i = 0; i < count; i++) {
                final PNode each = (PNode) children.get(i);
                each.fullPaint(paintContext);
            }

            paintAfterChildren(paintContext);

            paintContext.popTransparency(transparency);
            paintContext.popTransform(transform);
        }
    }

    /**
     * Subclasses that wish to do additional painting after their children are
     * painted should override this method and do that painting here.
     * 
     * @param paintContext the paint context to sue for painting after the
     *            children are painted
     */
    protected void paintAfterChildren(final PPaintContext paintContext) {
    }

    /**
     * Return a new Image representing this node and all of its children. The
     * image size will be equal to the size of this nodes full bounds.
     * 
     * @return a new image representing this node and its descendants
     */
    public Image toImage() {
        final PBounds b = getFullBoundsReference();
        return toImage((int) Math.ceil(b.getWidth()), (int) Math.ceil(b.getHeight()), null);
    }

    /**
     * Return a new Image of the requested size representing this node and all
     * of its children. If backGroundPaint is null the resulting image will have
     * transparent regions, otherwise those regions will be filled with the
     * backgroundPaint.
     * 
     * @param width pixel width of the resulting image
     * @param height pixel height of the resulting image
     * @param backgroundPaint paint to fill the image with before drawing this
     *            node, may be null
     * 
     * @return a new image representing this node and its descendants
     */
    public Image toImage(final int width, final int height, final Paint backgroundPaint) {
        BufferedImage result;

        if (GraphicsEnvironment.isHeadless()) {
            result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        else {
            final GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().getDefaultConfiguration();
            result = graphicsConfiguration.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        }

        return toImage(result, backgroundPaint);
    }

    /**
     * Paint a representation of this node into the specified buffered image. If
     * background, paint is null, then the image will not be filled with a color
     * prior to rendering
     * 
     * @param image Image onto which this node will be painted
     * @param backGroundPaint will fill background of image with this. May be
     *            null.
     * @return a rendering of this image and its descendants onto the specified
     *         image
     */
    public Image toImage(final BufferedImage image, final Paint backGroundPaint) {
        return toImage(image, backGroundPaint, FILL_STRATEGY_ASPECT_FIT);
    }

    /**
     * Paint a representation of this node into the specified buffered image. If
     * background, paint is null, then the image will not be filled with a color
     * prior to rendering
     * 
     * @since 1.3
     * @param image Image onto which this node will be painted
     * @param backGroundPaint will fill background of image with this. May be
     *            null.
     * @param fillStrategy strategy to use regarding how node will cover the
     *            image
     * @return a rendering of this image and its descendants onto the specified
     *         image
     */
    public Image toImage(final BufferedImage image, final Paint backGroundPaint, final int fillStrategy) {
        final int imageWidth = image.getWidth();
        final int imageHeight = image.getHeight();
        final Graphics2D g2 = image.createGraphics();

        if (backGroundPaint != null) {
            g2.setPaint(backGroundPaint);
            g2.fillRect(0, 0, imageWidth, imageHeight);
        }
        g2.setClip(0, 0, imageWidth, imageHeight);

        final PBounds nodeBounds = getFullBounds();
        nodeBounds.expandNearestIntegerDimensions();

        final double nodeWidth = nodeBounds.getWidth();
        final double nodeHeight = nodeBounds.getHeight();

        double imageRatio = imageWidth / (imageHeight * 1.0);
        double nodeRatio = nodeWidth / nodeHeight;
        double scale;
        switch (fillStrategy) {
            case FILL_STRATEGY_ASPECT_FIT:
                // scale the graphics so node's full bounds fit in the imageable
                // bounds but aspect ration is retained

                if (nodeRatio <= imageRatio) {
                    scale = image.getHeight() / nodeHeight;
                }
                else {
                    scale = image.getWidth() / nodeWidth;
                }
                g2.scale(scale, scale);
                g2.translate(-nodeBounds.x, -nodeBounds.y);
                break;
            case FILL_STRATEGY_ASPECT_COVER:
                // scale the graphics so node completely covers the imageable
                // area, but retains its aspect ratio.
                if (nodeRatio <= imageRatio) {
                    scale = image.getWidth() / nodeWidth;
                }
                else {
                    scale = image.getHeight() / nodeHeight;
                }
                g2.scale(scale, scale);
                break;
            case FILL_STRATEGY_EXACT_FIT:
                // scale the node so that it covers then entire image,
                // distorting it if necessary.
                g2.scale(image.getWidth() / nodeWidth, image.getHeight() / nodeHeight);
                g2.translate(-nodeBounds.x, -nodeBounds.y);
                break;
            default:
                throw new IllegalArgumentException("Fill strategy provided is invalid");
        }

        final PPaintContext pc = new PPaintContext(g2);
        pc.setRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
        fullPaint(pc);
        return image;
    }

    /**
     * Constructs a new PrinterJob, allows the user to select which printer to
     * print to, And then prints the node.
     * @throws PrinterException 
     */
    public void print() throws PrinterException {
        final PrinterJob printJob = PrinterJob.getPrinterJob();
        final PageFormat pageFormat = printJob.defaultPage();
        final Book book = new Book();
        book.append(this, pageFormat);
        printJob.setPageable(book);

        if (printJob.printDialog()) {
            printJob.print();
        }
    }

    /**
     * Prints the node into the given Graphics context using the specified
     * format. The zero based index of the requested page is specified by
     * pageIndex. If the requested page does not exist then this method returns
     * NO_SUCH_PAGE; otherwise PAGE_EXISTS is returned. If the printable object
     * aborts the print job then it throws a PrinterException.
     * 
     * @param graphics the context into which the node is drawn
     * @param pageFormat the size and orientation of the page
     * @param pageIndex the zero based index of the page to be drawn
     * 
     * @return Either NO_SUCH_PAGE or PAGE_EXISTS
     */
    public int print(final Graphics graphics, final PageFormat pageFormat, final int pageIndex) {
        if (pageIndex != 0) {
            return NO_SUCH_PAGE;
        }

        if (!(graphics instanceof Graphics2D)) {
            throw new IllegalArgumentException("Provided graphics context is not a Graphics2D object");
        }

        final Graphics2D g2 = (Graphics2D) graphics;
        final PBounds imageBounds = getFullBounds();

        imageBounds.expandNearestIntegerDimensions();

        g2.setClip(0, 0, (int) pageFormat.getWidth(), (int) pageFormat.getHeight());
        g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        // scale the graphics so node's full bounds fit in the imageable bounds.
        double scale = pageFormat.getImageableWidth() / imageBounds.getWidth();
        if (pageFormat.getImageableHeight() / imageBounds.getHeight() < scale) {
            scale = pageFormat.getImageableHeight() / imageBounds.getHeight();
        }

        g2.scale(scale, scale);
        g2.translate(-imageBounds.x, -imageBounds.y);

        final PPaintContext pc = new PPaintContext(g2);
        pc.setRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
        fullPaint(pc);

        return PAGE_EXISTS;
    }

    // ****************************************************************
    // Picking - Methods for picking this node and its children.
    // 
    // Picking is used to determine the node that intersects a point or
    // rectangle on the screen. It is most frequently used by the
    // PInputManager to determine the node that the cursor is over.
    // 
    // The intersects() method is used to determine if a node has
    // been picked or not. The default implementation just test to see
    // if the pick bounds intersects the bounds of the node. Subclasses
    // whose geometry (a circle for example) does not match up exactly with
    // the bounds should override the intersects() method.
    // 
    // The default picking behavior is to first try to pick the nodes
    // children, and then try to pick the nodes own bounds. If a node
    // wants specialized picking behavior it can override:
    // 
    // pick() - Pick nodes here that should be picked before the nodes
    // children are picked.
    // pickAfterChildren() - Pick nodes here that should be picked after the
    // node's children are picked.
    // 
    // Note that fullPick should not normally be overridden.
    // 
    // The pickable and childrenPickable flags can be used to make a
    // node or it children not pickable even if their geometry does
    // intersect the pick bounds.
    // ****************************************************************

    /**
     * Return true if this node is pickable. Only pickable nodes can receive
     * input events. Nodes are pickable by default.
     * 
     * @return true if this node is pickable
     */
    public boolean getPickable() {
        return pickable;
    }

    /**
     * Set the pickable flag for this node. Only pickable nodes can receive
     * input events. Nodes are pickable by default.
     * 
     * @param isPickable true if this node is pickable
     */
    public void setPickable(final boolean isPickable) {
        if (getPickable() != isPickable) {
            pickable = isPickable;
            firePropertyChange(PROPERTY_CODE_PICKABLE, PROPERTY_PICKABLE, null, null);
        }
    }

    /**
     * Return true if the children of this node should be picked. If this flag
     * is false then this node will not try to pick its children. Children are
     * pickable by default.
     * 
     * @return true if this node tries to pick its children
     */
    public boolean getChildrenPickable() {
        return childrenPickable;
    }

    /**
     * Set the children pickable flag. If this flag is false then this node will
     * not try to pick its children. Children are pickable by default.
     * 
     * @param areChildrenPickable true if this node tries to pick its children
     */
    public void setChildrenPickable(final boolean areChildrenPickable) {
        if (getChildrenPickable() != areChildrenPickable) {
            childrenPickable = areChildrenPickable;
            firePropertyChange(PROPERTY_CODE_CHILDREN_PICKABLE, PROPERTY_CHILDREN_PICKABLE, null, null);
        }
    }

    /**
     * Try to pick this node before its children have had a chance to be picked.
     * Nodes that paint on top of their children may want to override this
     * method to if the pick path intersects that paint.
     * 
     * @param pickPath the pick path used for the pick operation
     * @return true if this node was picked
     */
    protected boolean pick(final PPickPath pickPath) {
        return false;
    }

    /**
     * Try to pick this node and all of its descendants. Most subclasses should
     * not need to override this method. Instead they should override
     * <code>pick</code> or <code>pickAfterChildren</code>.
     * 
     * @param pickPath the pick path to add the node to if its picked
     * @return true if this node or one of its descendants was picked.
     */
    public boolean fullPick(final PPickPath pickPath) {
        if (getVisible() && (getPickable() || getChildrenPickable()) && fullIntersects(pickPath.getPickBounds())) {
            pickPath.pushNode(this);
            pickPath.pushTransform(transform);

            final boolean thisPickable = getPickable() && pickPath.acceptsNode(this);

            if (thisPickable && pick(pickPath)) {
                return true;
            }

            if (getChildrenPickable()) {
                final int count = getChildrenCount();
                for (int i = count - 1; i >= 0; i--) {
                    final PNode each = (PNode) children.get(i);
                    if (each.fullPick(pickPath)) {
                        return true;
                    }
                }
            }

            if (thisPickable && pickAfterChildren(pickPath)) {
                return true;
            }

            pickPath.popTransform(transform);
            pickPath.popNode(this);
        }

        return false;
    }

    /**
     * Finds all descendants of this node that intersect with the given bounds
     * and adds them to the results array.
     * 
     * @param fullBounds bounds to compare against
     * @param results array into which to add matches
     */
    public void findIntersectingNodes(final Rectangle2D fullBounds, final ArrayList results) {
        if (fullIntersects(fullBounds)) {
            final Rectangle2D localBounds = parentToLocal((Rectangle2D) fullBounds.clone());

            if (intersects(localBounds)) {
                results.add(this);
            }

            final int count = getChildrenCount();
            for (int i = count - 1; i >= 0; i--) {
                final PNode each = (PNode) children.get(i);
                each.findIntersectingNodes(localBounds, results);
            }
        }
    }

    /**
     * Try to pick this node after its children have had a chance to be picked.
     * Most subclasses the define a different geometry will need to override
     * this method.
     * 
     * @param pickPath the pick path used for the pick operation
     * @return true if this node was picked
     */
    protected boolean pickAfterChildren(final PPickPath pickPath) {
        if (intersects(pickPath.getPickBounds())) {
            return true;
        }
        return false;
    }

    // ****************************************************************
    // Structure - Methods for manipulating and traversing the
    // parent child relationship
    // 
    // Most of these methods won't need to be overridden by subclasses
    // but you will use them frequently to build up your node structures.
    // ****************************************************************

    /**
     * Add a node to be a new child of this node. The new node is added to the
     * end of the list of this node's children. If child was previously a child
     * of another node, it is removed from that first.
     * 
     * @param child the new child to add to this node
     */
    public void addChild(final PNode child) {
        int insertIndex = getChildrenCount();
        if (child.parent == this) {
            insertIndex--;
        }
        addChild(insertIndex, child);
    }

    /**
     * Add a node to be a new child of this node at the specified index. If
     * child was previously a child of another node, it is removed from that
     * node first.
     * 
     * @param index where in the children list to insert the child
     * @param child the new child to add to this node
     */
    public void addChild(final int index, final PNode child) {
        final PNode oldParent = child.getParent();

        if (oldParent != null) {
            oldParent.removeChild(child);
        }

        child.setParent(this);
        getChildrenReference().add(index, child);
        child.invalidatePaint();
        invalidateFullBounds();

        firePropertyChange(PROPERTY_CODE_CHILDREN, PROPERTY_CHILDREN, null, children);
    }

    /**
     * Add a collection of nodes to be children of this node. If these nodes
     * already have parents they will first be removed from those parents.
     * 
     * @param nodes a collection of nodes to be added to this node
     */
    public void addChildren(final Collection nodes) {
        final Iterator i = nodes.iterator();
        while (i.hasNext()) {
            final PNode each = (PNode) i.next();
            addChild(each);
        }
    }

    /**
     * Return true if this node is an ancestor of the parameter node.
     * 
     * @param node a possible descendant node
     * @return true if this node is an ancestor of the given node
     */
    public boolean isAncestorOf(final PNode node) {
        PNode p = node.parent;
        while (p != null) {
            if (p == this) {
                return true;
            }
            p = p.parent;
        }
        return false;
    }

    /**
     * Return true if this node is a descendant of the parameter node.
     * 
     * @param node a possible ancestor node
     * @return true if this nodes descends from the given node
     */
    public boolean isDescendentOf(final PNode node) {
        PNode p = parent;
        while (p != null) {
            if (p == node) {
                return true;
            }
            p = p.parent;
        }
        return false;
    }

    /**
     * Return true if this node descends from the root.
     *
     * @return whether this node descends from root node
     */
    public boolean isDescendentOfRoot() {
        return getRoot() != null;
    }

    /**
     * Raise this node within the Z-order of its parent.
     *
     * @since 3.0
     */
    public void raise() {
        final PNode p = parent;
        if (p != null) {
            final int index = parent.indexOfChild(this);
            final int siblingIndex = Math.min(parent.getChildrenCount() - 1, index + 1);
            if (siblingIndex != index) {
                raiseAbove(parent.getChild(siblingIndex));
            }
        }
    }

    /**
     * Lower this node within the Z-order of its parent.
     *
     * @since 3.0
     */
    public void lower() {
        final PNode p = parent;
        if (p != null) {
            final int index = parent.indexOfChild(this);
            final int siblingIndex = Math.max(0, index - 1);
            if (siblingIndex != index) {
                lowerBelow(parent.getChild(siblingIndex));
            }
        }
    }

    /**
     * Raise this node within the Z-order of its parent to the top.
     *
     * @since 3.0
     */
    public void raiseToTop() {
        final PNode p = parent;
        if (p != null) {
            p.removeChild(this);
            p.addChild(this);
        }
    }

    /**
     * Lower this node within the Z-order of its parent to the bottom.
     *
     * @since 3.0
     */
    public void lowerToBottom() {
        final PNode p = parent;
        if (p != null) {
            p.removeChild(this);
            p.addChild(0, this);
        }
    }

    /**
     * Raise this node within the Z-order of its parent above the specified sibling node.
     *
     * @since 3.0
     * @param sibling sibling node to raise this node above
     */
    public void raiseAbove(final PNode sibling) {
        final PNode p = parent;
        if (p != null && p == sibling.getParent()) {
            p.removeChild(this);
            final int index = p.indexOfChild(sibling);
            p.addChild(index + 1, this);
        }
    }

    /**
     * Lower this node within the Z-order of its parent below the specified sibling node.
     *
     * @since 3.0
     * @param sibling sibling node to lower this node below
     */
    public void lowerBelow(final PNode sibling) {
        final PNode p = parent;
        if (p != null && p == sibling.getParent()) {
            p.removeChild(this);
            final int index = p.indexOfChild(sibling);
            p.addChild(index, this);
        }
    }

    /**
     * Raise the specified child node within the Z-order of this.
     *
     * @since 3.0
     * @param child child node to raise
     */
    public void raise(final PNode child) {
        if (children != null && children.contains(child) && this.equals(child.getParent())) {
            child.raise();
        }
    }

    /**
     * Lower the specified child node within the Z-order of this.
     *
     * @since 3.0
     * @param child child node to lower
     */
    public void lower(final PNode child) {
        if (children != null && children.contains(child) && this.equals(child.getParent())) {
            child.lower();
        }
    }

    /**
     * Raise the specified child node within the Z-order of this to the top.
     *
     * @since 3.0
     * @param child child node to raise to the top
     */
    public void raiseToTop(final PNode child) {
        if (children != null && children.contains(child) && this.equals(child.getParent())) {
            child.raiseToTop();
        }
    }

    /**
     * Lower the specified child node within the Z-order of this to the bottom.
     *
     * @since 3.0
     * @param child child node to lower to the bottom
     */
    public void lowerToBottom(final PNode child) {
        if (children != null && children.contains(child) && this.equals(child.getParent())) {
            child.lowerToBottom();
        }
    }

    /**
     * Return the parent of this node. This will be null if this node has not
     * been added to a parent yet.
     * 
     * @return this nodes parent or null
     */
    public PNode getParent() {
        return parent;
    }

    /**
     * Set the parent of this node. Note this is set automatically when adding
     * and removing children.
     * 
     * @param newParent the parent to which this node should be added
     */
    public void setParent(final PNode newParent) {
        final PNode old = parent;
        parent = newParent;
        firePropertyChange(PROPERTY_CODE_PARENT, PROPERTY_PARENT, old, parent);
    }

    /**
     * Return the index where the given child is stored.
     * 
     * @param child child so search for
     * @return index of child or -1 if not found
     */
    public int indexOfChild(final PNode child) {
        if (children == null) {
            return -1;
        }
        return children.indexOf(child);
    }

    /**
     * Remove the given child from this node's children list. Any subsequent
     * children are shifted to the left (one is subtracted from their indices).
     * The removed child's parent is set to null.
     * 
     * @param child the child to remove
     * @return the removed child
     */
    public PNode removeChild(final PNode child) {
        final int index = indexOfChild(child);
        if (index == -1) {
            return null;
        }
        return removeChild(index);
    }

    /**
     * Remove the child at the specified position of this group node's children.
     * Any subsequent children are shifted to the left (one is subtracted from
     * their indices). The removed child's parent is set to null.
     * 
     * @param index the index of the child to remove
     * @return the removed child
     */
    public PNode removeChild(final int index) {
        if (children == null) {
            return null;
        }
        final PNode child = (PNode) children.remove(index);

        if (children.size() == 0) {
            children = null;
        }

        child.repaint();
        child.setParent(null);
        invalidateFullBounds();

        firePropertyChange(PROPERTY_CODE_CHILDREN, PROPERTY_CHILDREN, null, children);

        return child;
    }

    /**
     * Remove all the children in the given collection from this node's list of
     * children. All removed nodes will have their parent set to null.
     * 
     * @param childrenNodes the collection of children to remove
     */
    public void removeChildren(final Collection childrenNodes) {
        final Iterator i = childrenNodes.iterator();
        while (i.hasNext()) {
            final PNode each = (PNode) i.next();
            removeChild(each);
        }
    }

    /**
     * Remove all the children from this node. Node this method is more
     * efficient then removing each child individually.
     */
    public void removeAllChildren() {
        if (children != null) {
            final int count = children.size();
            for (int i = 0; i < count; i++) {
                final PNode each = (PNode) children.get(i);
                each.setParent(null);
            }
            children = null;
            invalidatePaint();
            invalidateFullBounds();

            firePropertyChange(PROPERTY_CODE_CHILDREN, PROPERTY_CHILDREN, null, children);
        }
    }

    /**
     * Delete this node by removing it from its parent's list of children.
     */
    public void removeFromParent() {
        if (parent != null) {
            parent.removeChild(this);
        }
    }

    /**
     * Set the parent of this node, and transform the node in such a way that it
     * doesn't move in global coordinates.
     * 
     * @param newParent The new parent of this node.
     */
    public void reparent(final PNode newParent) {
        final AffineTransform originalTransform = getLocalToGlobalTransform(null);
        final AffineTransform newTransform = newParent.getGlobalToLocalTransform(null);
        newTransform.concatenate(originalTransform);

        removeFromParent();
        setTransform(newTransform);
        newParent.addChild(this);
        computeFullBounds(fullBoundsCache);
    }

    /**
     * Swaps this node out of the scene graph tree, and replaces it with the
     * specified replacement node. This node is left dangling, and it is up to
     * the caller to manage it. The replacement node will be added to this
     * node's parent in the same position as this was. That is, if this was the
     * 3rd child of its parent, then after calling replaceWith(), the
     * replacement node will also be the 3rd child of its parent. If this node
     * has no parent when replace is called, then nothing will be done at all.
     * 
     * @param replacementNode the new node that replaces the current node in the
     *            scene graph tree.
     */
    public void replaceWith(final PNode replacementNode) {
        if (parent != null) {
            final PNode p = parent;
            final int index = p.getChildrenReference().indexOf(this);
            p.removeChild(this);
            p.addChild(index, replacementNode);
        }
    }

    /**
     * Sets the name of this node, may be null.
     * 
     * @since 1.3
     * @param name new name for this node
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the name given to this node.
     * 
     * @since 1.3
     * @return name given to this node, may be null
     */
    public String getName() {
        return name;
    }

    /**
     * Return the number of children that this node has.
     * 
     * @return the number of children
     */
    public int getChildrenCount() {
        if (children == null) {
            return 0;
        }
        return children.size();
    }

    /**
     * Return the child node at the specified index.
     * 
     * @param index a child index
     * @return the child node at the specified index
     */
    public PNode getChild(final int index) {
        return (PNode) children.get(index);
    }

    /**
     * Return a reference to the list used to manage this node's children. This
     * list should not be modified.
     * 
     * @return reference to the children list
     */
    public List getChildrenReference() {
        if (children == null) {
            children = new ArrayList();
        }
        return children;
    }

    /**
     * Return an iterator over this node's direct descendant children.
     * 
     * @return iterator over this nodes children
     */
    public ListIterator getChildrenIterator() {
        if (children == null) {
            return Collections.EMPTY_LIST.listIterator();
        }
        return Collections.unmodifiableList(children).listIterator();
    }

    /**
     * Return the root node (instance of PRoot). If this node does not descend
     * from a PRoot then null will be returned.
     * 
     * @return root element of this node, or null if this node does not descend
     *         from a PRoot
     */
    public PRoot getRoot() {
        if (parent != null) {
            return parent.getRoot();
        }
        return null;
    }

    /**
     * Return a collection containing this node and all of its descendant nodes.
     * 
     * @return a new collection containing this node and all descendants
     */
    public Collection getAllNodes() {
        return getAllNodes(null, null);
    }

    /**
     * Return a collection containing the subset of this node and all of its
     * descendant nodes that are accepted by the given node filter. If the
     * filter is null then all nodes will be accepted. If the results parameter
     * is not null then it will be used to collect this subset instead of
     * creating a new collection.
     * 
     * @param filter the filter used to determine the subset
     * @param resultantNodes where matching nodes should be added
     * @return a collection containing this node and all descendants
     */
    public Collection getAllNodes(final PNodeFilter filter, final Collection resultantNodes) {
        Collection results;
        if (resultantNodes == null) {
            results = new ArrayList();
        }
        else {
            results = resultantNodes;
        }

        if (filter == null || filter.accept(this)) {
            results.add(this);
        }

        if (filter == null || filter.acceptChildrenOf(this)) {
            final int count = getChildrenCount();
            for (int i = 0; i < count; i++) {
                final PNode each = (PNode) children.get(i);
                each.getAllNodes(filter, results);
            }
        }

        return results;
    }

    // ****************************************************************
    // Serialization - Nodes conditionally serialize their parent.
    // This means that only the parents that were unconditionally
    // (using writeObject) serialized by someone else will be restored
    // when the node is unserialized.
    // ****************************************************************

    /**
     * Write this node and all of its descendant nodes to the given outputsteam.
     * This stream must be an instance of PObjectOutputStream or serialization
     * will fail. This nodes parent is written out conditionally, that is it
     * will only be written out if someone else writes it out unconditionally.
     * 
     * @param out the output stream to write to, must be an instance of
     *            PObjectOutputStream
     * @throws IOException when an error occurs speaking to underlying
     *             ObjectOutputStream
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        if (!(out instanceof PObjectOutputStream)) {
            throw new IllegalArgumentException("PNode.writeObject may only be used with PObjectOutputStreams");
        }
        out.defaultWriteObject();
        ((PObjectOutputStream) out).writeConditionalObject(parent);
    }

    /**
     * Read this node and all of its descendants in from the given input stream.
     * 
     * @param in the stream to read from
     * 
     * @throws IOException when an error occurs speaking to underlying
     *             ObjectOutputStream
     * @throws ClassNotFoundException when a class is deserialized that no
     *             longer exists. This can happen if it's renamed or deleted.
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        parent = (PNode) in.readObject();
    }

    /**
     * Returns an array of input event listeners that are attached to this node.
     * 
     * @since 1.3
     * @return event listeners attached to this node
     */
    public PInputEventListener[] getInputEventListeners() {
        if (listenerList == null || listenerList.getListenerCount() == 0) {
            return new PInputEventListener[] {};
        }

        final EventListener[] listeners = listenerList.getListeners(PInputEventListener.class);

        final PInputEventListener[] result = new PInputEventListener[listeners.length];
        for (int i = 0; i < listeners.length; i++) {
            result[i] = (PInputEventListener) listeners[i];
        }
        return result;
    }

    /**
     * <b>PSceneGraphDelegate</b> is an interface to receive low level node
     * events. It together with PNode.SCENE_GRAPH_DELEGATE gives Piccolo2d users
     * an efficient way to learn about low level changes in Piccolo's scene
     * graph. Most users will not need to use this.
     */
    public interface PSceneGraphDelegate {
        /**
         * Called to notify delegate that the node needs repainting.
         * 
         * @param node node needing repaint
         */
        void nodePaintInvalidated(PNode node);

        /**
         * Called to notify delegate that the node and all it's children need
         * repainting.
         * 
         * @param node node needing repaint
         */
        void nodeFullBoundsInvalidated(PNode node);
    }
}
