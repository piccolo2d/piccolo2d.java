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
package edu.umd.cs.piccolox.event;

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

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

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

    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int IN = 4;
    public static final int OUT = 5;

    private static Hashtable NODE_TO_GLOBAL_NODE_CENTER_MAPPING = new Hashtable();

    private PNode focusNode;
    private PTransformActivity navigationActivity;

    public PNavigationEventHandler() {
        super();
        setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
    }

    // ****************************************************************
    // Focus Change Events.
    // ****************************************************************

    public void keyPressed(final PInputEvent e) {
        final PNode oldLocation = focusNode;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                moveFocusLeft(e);
                break;

            case KeyEvent.VK_RIGHT:
                moveFocusRight(e);
                break;

            case KeyEvent.VK_UP:
            case KeyEvent.VK_PAGE_UP:
                if (e.isAltDown()) {
                    moveFocusOut(e);
                }
                else {
                    moveFocusUp(e);
                }
                break;

            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_PAGE_DOWN:
                if (e.isAltDown()) {
                    moveFocusIn(e);
                }
                else {
                    moveFocusDown(e);
                }
                break;
        }

        if (focusNode != null && oldLocation != focusNode) {
            directCameraViewToFocus(e.getCamera(), focusNode, 500);
        }
    }

    public void mousePressed(final PInputEvent aEvent) {
        moveFocusToMouseOver(aEvent);

        if (focusNode != null) {
            directCameraViewToFocus(aEvent.getCamera(), focusNode, 500);
            aEvent.getInputManager().setKeyboardFocus(aEvent.getPath());
        }
    }

    // ****************************************************************
    // Focus Movement - Moves the focus the specified direction. Left,
    // right, up, down mean move the focus to the closest sibling of the
    // current focus node that exists in that direction. Move in means
    // move the focus to a child of the current focus, move out means
    // move the focus to the parent of the current focus.
    // ****************************************************************

    public void moveFocusDown(final PInputEvent e) {
        moveFocusInDirection(SOUTH);
    }

    public void moveFocusIn(final PInputEvent e) {
        moveFocusInDirection(IN);
    }

    public void moveFocusLeft(final PInputEvent e) {
        moveFocusInDirection(WEST);
    }

    public void moveFocusOut(final PInputEvent e) {
        moveFocusInDirection(OUT);
    }

    public void moveFocusRight(final PInputEvent e) {
        moveFocusInDirection(EAST);
    }

    public void moveFocusUp(final PInputEvent e) {
        moveFocusInDirection(NORTH);
    }

    private void moveFocusInDirection(final int direction) {
        final PNode n = getNeighborInDirection(direction);

        if (n != null) {
            focusNode = n;
        }
    }

    public void moveFocusToMouseOver(final PInputEvent e) {
        final PNode focus = e.getPickedNode();
        if (!(focus instanceof PCamera)) {
            focusNode = focus;
        }
    }

    public PNode getNeighborInDirection(final int aDirection) {
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
            if (nodeIsNeighborInDirection(each, aDirection)) {
                return each;
            }
        }

        return null;
    }

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

    public boolean nodeIsNeighborInDirection(final PNode aNode, final int aDirection) {
        switch (aDirection) {
            case IN: {
                return aNode.isDescendentOf(focusNode);
            }

            case OUT: {
                return aNode.isAncestorOf(focusNode);
            }

            default: {
                if (aNode.isAncestorOf(focusNode) || aNode.isDescendentOf(focusNode)) {
                    return false;
                }
            }
        }

        final Point2D highlightCenter = (Point2D) NODE_TO_GLOBAL_NODE_CENTER_MAPPING.get(focusNode);
        final Point2D nodeCenter = (Point2D) NODE_TO_GLOBAL_NODE_CENTER_MAPPING.get(aNode);

        final double ytest1 = nodeCenter.getX() - highlightCenter.getX() + highlightCenter.getY();
        final double ytest2 = -nodeCenter.getX() + highlightCenter.getX() + highlightCenter.getY();

        switch (aDirection) {
            case NORTH: {
                return nodeCenter.getY() < highlightCenter.getY() && nodeCenter.getY() < ytest1
                        && nodeCenter.getY() < ytest2;
            }

            case EAST: {
                return nodeCenter.getX() > highlightCenter.getX() && nodeCenter.getY() < ytest1
                        && nodeCenter.getY() > ytest2;
            }

            case SOUTH: {
                return nodeCenter.getY() > highlightCenter.getY() && nodeCenter.getY() > ytest1
                        && nodeCenter.getY() > ytest2;
            }
            case WEST: {
                return nodeCenter.getX() < highlightCenter.getX() && nodeCenter.getY() > ytest1
                        && nodeCenter.getY() < ytest2;
            }
        }
        return false;
    }

    public void sortNodesByDistanceFromPoint(final List aNodesList, final Point2D aPoint) {
        Collections.sort(aNodesList, new Comparator() {
            public int compare(final Object o1, final Object o2) {
                return compare((PNode) o1, (PNode) o2);
            }

            private int compare(final PNode each1, final PNode each2) {
                final Point2D center1 = each1.getGlobalFullBounds().getCenter2D();
                final Point2D center2 = each2.getGlobalFullBounds().getCenter2D();

                NODE_TO_GLOBAL_NODE_CENTER_MAPPING.put(each1, center1);
                NODE_TO_GLOBAL_NODE_CENTER_MAPPING.put(each2, center2);

                return Double.compare(aPoint.distance(center1), aPoint.distance(center2));
            }
        });
    }

    // ****************************************************************
    // Canvas Movement - The canvas view is updated so that the current
    // focus remains visible on the screen at 100 percent scale.
    // ****************************************************************

    protected PActivity animateCameraViewTransformTo(final PCamera aCamera, final AffineTransform aTransform,
            final int duration) {
        boolean wasOldAnimation = false;

        // first stop any old animations.
        if (navigationActivity != null) {
            navigationActivity.terminate();
            wasOldAnimation = true;
        }

        if (duration == 0) {
            aCamera.setViewTransform(aTransform);
            return null;
        }

        final AffineTransform source = aCamera.getViewTransformReference();

        if (source.equals(aTransform)) {
            return null;
        }

        navigationActivity = aCamera.animateViewToTransform(aTransform, duration);
        navigationActivity.setSlowInSlowOut(!wasOldAnimation);
        return navigationActivity;
    }

    public PActivity directCameraViewToFocus(final PCamera aCamera, final PNode aFocusNode, final int duration) {
        focusNode = aFocusNode;
        final AffineTransform originalViewTransform = aCamera.getViewTransform();

        // Scale the canvas to include
        final PDimension d = new PDimension(1, 0);
        focusNode.globalToLocal(d);

        final double scaleFactor = d.getWidth() / aCamera.getViewScale();
        final Point2D scalePoint = focusNode.getGlobalFullBounds().getCenter2D();
        if (Math.abs(1f - scaleFactor) < 0.0001) {
            aCamera.scaleViewAboutPoint(scaleFactor, scalePoint.getX(), scalePoint.getY());
        }

        // Pan the canvas to include the view bounds with minimal canvas
        // movement.
        aCamera.animateViewToPanToBounds(focusNode.getGlobalFullBounds(), 0);

        // Get rid of any white space. The canvas may be panned and
        // zoomed in to do this. But make sure not stay constrained by max
        // magnification.
        // fillViewWhiteSpace(aCamera);

        final AffineTransform resultingTransform = aCamera.getViewTransform();
        aCamera.setViewTransform(originalViewTransform);

        // Animate the canvas so that it ends up with the given
        // view transform.
        return animateCameraViewTransformTo(aCamera, resultingTransform, duration);
    }

    protected void fillViewWhiteSpace(final PCamera aCamera) {
        final PBounds rootBounds = aCamera.getRoot().getFullBoundsReference();
        PBounds viewBounds = aCamera.getViewBounds();

        if (rootBounds.contains(aCamera.getViewBounds())) {
            return;
        }

        aCamera.animateViewToPanToBounds(rootBounds, 0);
        aCamera.animateViewToPanToBounds(focusNode.getGlobalFullBounds(), 0);

        // center content.
        double dx = 0;
        double dy = 0;
        viewBounds = aCamera.getViewBounds();

        if (viewBounds.getWidth() > rootBounds.getWidth()) {
            // then center along x axis.
            dx = rootBounds.getCenterX() - viewBounds.getCenterX();
        }

        if (viewBounds.getHeight() > rootBounds.getHeight()) {
            // then center along y axis.
            dy = rootBounds.getCenterX() - viewBounds.getCenterX();
        }

        aCamera.translateView(dx, dy);
    }
}
