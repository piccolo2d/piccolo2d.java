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
package org.piccolo2d.extras.event;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.piccolo2d.PCamera;
import org.piccolo2d.PNode;
import org.piccolo2d.activities.PActivity;
import org.piccolo2d.activities.PTransformActivity;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PInputEventFilter;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PDimension;


/**
 * <b>PNavigationEventHandler</b> implements simple focus based navigation. Uses
 * mouse button one or the arrow keys to set a new focus. Animates the canvas
 * view to keep the focus node on the screen and at 100 percent scale with
 * minimal view movement.
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PNavigationEventHandler extends PBasicInputEventHandler {
    /** Minum size under which two scales are considered the same. */
    private static final double SCALING_THRESHOLD = 0.0001;
    /** Amount of time it takes to animation view from one location to another. */
    private static final int NAVIGATION_DURATION = 500;
    /** The UP direction on the screen. */
    public static final int NORTH = 0;
    /** The DOWN direction on the screen. */
    public static final int SOUTH = 1;
    /** The RIGHT direction on the screen. */
    public static final int EAST = 2;
    /** The LEFT direction on the screen. */
    public static final int WEST = 3;
    /** The IN direction on the scene. */
    public static final int IN = 4;
    /** The OUT direction on the scene. */
    public static final int OUT = 5;

    private static Hashtable NODE_TO_GLOBAL_NODE_CENTER_MAPPING = new Hashtable();

    private PNode focusNode;
    private PTransformActivity navigationActivity;

    /**
     * Constructs a Navigation Event Handler that will only accepts left mouse
     * clicks.
     */
    public PNavigationEventHandler() {
        super();
        setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
    }

    // ****************************************************************
    // Focus Change Events.
    // ****************************************************************

    /**
     * Processes key pressed events.
     * 
     * @param event event representing the key press
     */
    public void keyPressed(final PInputEvent event) {
        final PNode oldLocation = focusNode;

        switch (event.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                moveFocusLeft(event);
                break;

            case KeyEvent.VK_RIGHT:
                moveFocusRight(event);
                break;

            case KeyEvent.VK_UP:
            case KeyEvent.VK_PAGE_UP:
                if (event.isAltDown()) {
                    moveFocusOut(event);
                }
                else {
                    moveFocusUp(event);
                }
                break;

            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_PAGE_DOWN:
                if (event.isAltDown()) {
                    moveFocusIn(event);
                }
                else {
                    moveFocusDown(event);
                }
                break;
            default:
                // Pressed key is not a navigation key.
        }

        if (focusNode != null && oldLocation != focusNode) {
            directCameraViewToFocus(event.getCamera(), focusNode, NAVIGATION_DURATION);
        }
    }

    /**
     * Animates the camera to the node that has been pressed.
     * 
     * @param event event representing the mouse press
     */
    public void mousePressed(final PInputEvent event) {
        moveFocusToMouseOver(event);

        if (focusNode != null) {
            directCameraViewToFocus(event.getCamera(), focusNode, NAVIGATION_DURATION);
            event.getInputManager().setKeyboardFocus(event.getPath());
        }
    }

    // ****************************************************************
    // Focus Movement - Moves the focus the specified direction. Left,
    // right, up, down mean move the focus to the closest sibling of the
    // current focus node that exists in that direction. Move in means
    // move the focus to a child of the current focus, move out means
    // move the focus to the parent of the current focus.
    // ****************************************************************

    /**
     * Moves the focus in the downward direction. Animating the camera
     * accordingly.
     * 
     * @param event ignored
     */
    public void moveFocusDown(final PInputEvent event) {
        moveFocusInDirection(SOUTH);
    }

    /**
     * Moves the focus "into" the scene. So smaller nodes appear larger on
     * screen. Animates the camera accordingly.
     * 
     * @param event ignored
     */
    public void moveFocusIn(final PInputEvent event) {
        moveFocusInDirection(IN);
    }

    /**
     * Moves the focus in the left direction. Animating the camera accordingly.
     * 
     * @param event ignored
     */
    public void moveFocusLeft(final PInputEvent event) {
        moveFocusInDirection(WEST);
    }

    /**
     * Moves the focus "out" of scene. So larger nodes appear smaller on screen.
     * Animates the camera accordingly.
     * 
     * @param event ignored
     */
    public void moveFocusOut(final PInputEvent event) {
        moveFocusInDirection(OUT);
    }

    /**
     * Moves the focus in the right direction. Animating the camera accordingly.
     * 
     * @param event ignored
     */
    public void moveFocusRight(final PInputEvent event) {
        moveFocusInDirection(EAST);
    }

    /**
     * Moves the focus in the up direction. Animating the camera accordingly.
     * 
     * @param event ignored
     */
    public void moveFocusUp(final PInputEvent event) {
        moveFocusInDirection(NORTH);
    }

    /**
     * Moves the focus to the nearest node in the direction specified. Animating
     * the camera appropriately.
     * 
     * @param direction one of NORTH, SOUTH, EAST, WEST, IN, OUT
     */
    private void moveFocusInDirection(final int direction) {
        final PNode n = getNeighborInDirection(direction);

        if (n != null) {
            focusNode = n;
        }
    }

    /**
     * Moves the focus to the mouse under the mouse. Animating the camera
     * appropriately.
     * 
     * @param event mouse event
     */
    public void moveFocusToMouseOver(final PInputEvent event) {
        final PNode focus = event.getPickedNode();
        if (!(focus instanceof PCamera)) {
            focusNode = focus;
        }
    }

    /**
     * Returns the nearest node in the given direction.
     * 
     * @param direction direction in which to look the nearest node
     * 
     * @return nearest node in the given direction
     */
    public PNode getNeighborInDirection(final int direction) {
        if (focusNode == null) {
            return null;
        }

        NODE_TO_GLOBAL_NODE_CENTER_MAPPING.clear();

        final Point2D highlightCenter = focusNode.getGlobalFullBounds().getCenter2D();
        NODE_TO_GLOBAL_NODE_CENTER_MAPPING.put(focusNode, highlightCenter);

        final List l = getNeighbors();
        sortNodesByDistanceFromPoint(l, highlightCenter);

        final Iterator i = l.iterator();
        while (i.hasNext()) {
            final PNode each = (PNode) i.next();
            if (nodeIsNeighborInDirection(each, direction)) {
                return each;
            }
        }

        return null;
    }

    /**
     * Returns all pickable nodes that are 1 hop away from the currently focused
     * node. This includes, parent, children, and siblings.
     * 
     * @return list of nodes that are 1 hop away from the current focusNode
     */
    public List getNeighbors() {
        final ArrayList result = new ArrayList();
        if (focusNode == null || focusNode.getParent() == null) {
            return result;
        }

        final PNode focusParent = focusNode.getParent();

        final Iterator i = focusParent.getChildrenIterator();

        while (i.hasNext()) {
            final PNode each = (PNode) i.next();
            if (each != focusNode && each.getPickable()) {
                result.add(each);
            }
        }

        result.add(focusParent);
        result.addAll(focusNode.getChildrenReference());
        return result;
    }

    /**
     * Returns true if the given node is a neighbor in the given direction
     * relative to the current focus.
     * 
     * @param node the node being tested
     * @param direction the direction in which we're testing
     * 
     * @return true if node is a neighbor in the direction provided
     */
    public boolean nodeIsNeighborInDirection(final PNode node, final int direction) {
        switch (direction) {
            case IN:
                return node.isDescendentOf(focusNode);

            case OUT:
                return node.isAncestorOf(focusNode);

            default:
                if (node.isAncestorOf(focusNode) || node.isDescendentOf(focusNode)) {
                    return false;
                }
        }

        final Point2D highlightCenter = (Point2D) NODE_TO_GLOBAL_NODE_CENTER_MAPPING.get(focusNode);
        final Point2D nodeCenter = (Point2D) NODE_TO_GLOBAL_NODE_CENTER_MAPPING.get(node);

        final double ytest1 = nodeCenter.getX() - highlightCenter.getX() + highlightCenter.getY();
        final double ytest2 = -nodeCenter.getX() + highlightCenter.getX() + highlightCenter.getY();

        switch (direction) {
            case NORTH:
                return nodeCenter.getY() < highlightCenter.getY() && nodeCenter.getY() < ytest1
                        && nodeCenter.getY() < ytest2;

            case EAST:
                return nodeCenter.getX() > highlightCenter.getX() && nodeCenter.getY() < ytest1
                        && nodeCenter.getY() > ytest2;

            case SOUTH:
                return nodeCenter.getY() > highlightCenter.getY() && nodeCenter.getY() > ytest1
                        && nodeCenter.getY() > ytest2;

            case WEST:
                return nodeCenter.getX() < highlightCenter.getX() && nodeCenter.getY() > ytest1
                        && nodeCenter.getY() < ytest2;

            default:
                return false;
        }
    }

    /**
     * Modifies the array so that it's sorted in ascending order based on the
     * distance from the given point.
     * 
     * @param nodes list of nodes to be sorted
     * @param point point from which distance is being computed
     */
    public void sortNodesByDistanceFromPoint(final List nodes, final Point2D point) {
        Collections.sort(nodes, new Comparator() {
            public int compare(final Object o1, final Object o2) {
                return compare((PNode) o1, (PNode) o2);
            }

            private int compare(final PNode each1, final PNode each2) {
                final Point2D center1 = each1.getGlobalFullBounds().getCenter2D();
                final Point2D center2 = each2.getGlobalFullBounds().getCenter2D();

                NODE_TO_GLOBAL_NODE_CENTER_MAPPING.put(each1, center1);
                NODE_TO_GLOBAL_NODE_CENTER_MAPPING.put(each2, center2);

                return Double.compare(point.distance(center1), point.distance(center2));
            }
        });
    }

    // ****************************************************************
    // Canvas Movement - The canvas view is updated so that the current
    // focus remains visible on the screen at 100 percent scale.
    // ****************************************************************

    /**
     * Animates the camera's view transform into the provided one over the
     * duration provided.
     * 
     * @param camera camera being animated
     * @param targetTransform the transform to which the camera's transform will
     *            be animated
     * @param duration the number of milliseconds the animation should last
     * 
     * @return an activity object that represents the animation
     */
    protected PActivity animateCameraViewTransformTo(final PCamera camera, final AffineTransform targetTransform,
            final int duration) {
        boolean wasOldAnimation = false;

        // first stop any old animations.
        if (navigationActivity != null) {
            navigationActivity.terminate();
            wasOldAnimation = true;
        }

        if (duration == 0) {
            camera.setViewTransform(targetTransform);
            return null;
        }

        final AffineTransform source = camera.getViewTransformReference();

        if (source.equals(targetTransform)) {
            return null;
        }

        navigationActivity = camera.animateViewToTransform(targetTransform, duration);
        navigationActivity.setSlowInSlowOut(!wasOldAnimation);
        return navigationActivity;
    }

    /**
     * Animates the Camera's view so that it contains the new focus node.
     * 
     * @param camera The camera to be animated
     * @param newFocus the node that will gain focus
     * @param duration number of milliseconds that animation should last for
     * 
     * @return an activity object representing the scheduled animation
     */
    public PActivity directCameraViewToFocus(final PCamera camera, final PNode newFocus, final int duration) {
        focusNode = newFocus;
        final AffineTransform originalViewTransform = camera.getViewTransform();

        final PDimension d = new PDimension(1, 0);
        focusNode.globalToLocal(d);

        final double scaleFactor = d.getWidth() / camera.getViewScale();
        final Point2D scalePoint = focusNode.getGlobalFullBounds().getCenter2D();
        if (Math.abs(1f - scaleFactor) < SCALING_THRESHOLD) {
            camera.scaleViewAboutPoint(scaleFactor, scalePoint.getX(), scalePoint.getY());
        }

        // Pan the canvas to include the view bounds with minimal canvas
        // movement.
        camera.animateViewToPanToBounds(focusNode.getGlobalFullBounds(), 0);

        // Get rid of any white space. The canvas may be panned and
        // zoomed in to do this. But make sure not stay constrained by max
        // magnification.
        // fillViewWhiteSpace(aCamera);

        final AffineTransform resultingTransform = camera.getViewTransform();
        camera.setViewTransform(originalViewTransform);

        // Animate the canvas so that it ends up with the given
        // view transform.
        return animateCameraViewTransformTo(camera, resultingTransform, duration);
    }

    /**
     * Instantaneously transforms the provided camera so that it does not
     * contain any extra white space.
     * 
     * @param camera the camera to be transformed
     */
    protected void fillViewWhiteSpace(final PCamera camera) {
        final PBounds rootBounds = camera.getRoot().getFullBoundsReference();        

        if (rootBounds.contains(camera.getViewBounds())) {
            return;
        }

        camera.animateViewToPanToBounds(rootBounds, 0);
        camera.animateViewToPanToBounds(focusNode.getGlobalFullBounds(), 0);

        // center content.
        double dx = 0;
        double dy = 0;
        
        PBounds viewBounds = camera.getViewBounds();

        if (viewBounds.getWidth() > rootBounds.getWidth()) {
            // then center along x axis.
            dx = rootBounds.getCenterX() - viewBounds.getCenterX();
        }

        if (viewBounds.getHeight() > rootBounds.getHeight()) {
            // then center along y axis.
            dy = rootBounds.getCenterX() - viewBounds.getCenterX();
        }

        camera.translateView(dx, dy);
    }
}
