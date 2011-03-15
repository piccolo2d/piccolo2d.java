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

import java.awt.Cursor;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.SwingConstants;

import org.piccolo2d.PCamera;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.extras.util.PBoundsLocator;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PDimension;
import org.piccolo2d.util.PPickPath;


/**
 * <b>PBoundsHandle</b> a handle for resizing the bounds of another node. If a
 * bounds handle is dragged such that the other node's width or height becomes
 * negative then the each drag handle's locator assciated with that other node
 * is "flipped" so that they are attached to and dragging a different corner of
 * the nodes bounds.
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PBoundsHandle extends PHandle {
    private static final long serialVersionUID = 1L;

    /**
     * Event handler responsible for changing the mouse when it enters the
     * handle.
     */
    private transient PBasicInputEventHandler handleCursorHandler;

    /**
     * Adds bounds handles to the corners and edges of the provided node.
     * 
     * @param node node to be extended with bounds handles
     */
    public static void addBoundsHandlesTo(final PNode node) {
        node.addChild(new PBoundsHandle(PBoundsLocator.createEastLocator(node)));
        node.addChild(new PBoundsHandle(PBoundsLocator.createWestLocator(node)));
        node.addChild(new PBoundsHandle(PBoundsLocator.createNorthLocator(node)));
        node.addChild(new PBoundsHandle(PBoundsLocator.createSouthLocator(node)));
        node.addChild(new PBoundsHandle(PBoundsLocator.createNorthEastLocator(node)));
        node.addChild(new PBoundsHandle(PBoundsLocator.createNorthWestLocator(node)));
        node.addChild(new PBoundsHandle(PBoundsLocator.createSouthEastLocator(node)));
        node.addChild(new PBoundsHandle(PBoundsLocator.createSouthWestLocator(node)));
    }

    /**
     * Adds stick handles (always visible regardless of scale since they are
     * attached to the camera) to the node provided.
     * 
     * @param node node being extended with bounds handles
     * @param camera camera onto which handles will appear
     */
    public static void addStickyBoundsHandlesTo(final PNode node, final PCamera camera) {
        camera.addChild(new PBoundsHandle(PBoundsLocator.createEastLocator(node)));
        camera.addChild(new PBoundsHandle(PBoundsLocator.createWestLocator(node)));
        camera.addChild(new PBoundsHandle(PBoundsLocator.createNorthLocator(node)));
        camera.addChild(new PBoundsHandle(PBoundsLocator.createSouthLocator(node)));
        camera.addChild(new PBoundsHandle(PBoundsLocator.createNorthEastLocator(node)));
        camera.addChild(new PBoundsHandle(PBoundsLocator.createNorthWestLocator(node)));
        camera.addChild(new PBoundsHandle(PBoundsLocator.createSouthEastLocator(node)));
        camera.addChild(new PBoundsHandle(PBoundsLocator.createSouthWestLocator(node)));
    }

    /**
     * Removes all bounds from the node provided.
     * 
     * @param node node having its handles removed from
     */
    public static void removeBoundsHandlesFrom(final PNode node) {
        final ArrayList handles = new ArrayList();

        final Iterator i = node.getChildrenIterator();
        while (i.hasNext()) {
            final PNode each = (PNode) i.next();
            if (each instanceof PBoundsHandle) {
                handles.add(each);
            }
        }
        node.removeChildren(handles);
    }

    /**
     * Creates a bounds handle that will be attached to the provided locator.
     * 
     * @param locator locator used to position the node
     */
    public PBoundsHandle(final PBoundsLocator locator) {
        super(locator);
    }

    /**
     * Installs the handlers to this particular bounds handle.
     */
    protected void installHandleEventHandlers() {
        super.installHandleEventHandlers();
        handleCursorHandler = new MouseCursorUpdateHandler();
        addInputEventListener(handleCursorHandler);
    }

    /**
     * Return the event handler that is responsible for setting the mouse cursor
     * when it enters/exits this handle.
     * 
     * @return current handler responsible for changing the mouse cursor
     */
    public PBasicInputEventHandler getHandleCursorEventHandler() {
        return handleCursorHandler;
    }

    /**
     * Is invoked when the a drag starts on this handle.
     * 
     * @param aLocalPoint point in the handle's coordinate system that is
     *            pressed
     * @param aEvent event representing the start of the drag
     */
    public void startHandleDrag(final Point2D aLocalPoint, final PInputEvent aEvent) {
        final PBoundsLocator l = (PBoundsLocator) getLocator();
        l.getNode().startResizeBounds();
    }

    /**
     * Is invoked when the handle is being dragged.
     * 
     * @param aLocalDimension dimension representing the magnitude of the handle
     *            drag
     * @param aEvent event responsible for the call
     */
    public void dragHandle(final PDimension aLocalDimension, final PInputEvent aEvent) {
        final PBoundsLocator l = (PBoundsLocator) getLocator();

        final PNode n = l.getNode();
        final PBounds b = n.getBounds();

        final PNode parent = getParent();
        if (parent != n && parent instanceof PCamera) {
            ((PCamera) parent).localToView(aLocalDimension);
        }

        localToGlobal(aLocalDimension);
        n.globalToLocal(aLocalDimension);

        final double dx = aLocalDimension.getWidth();
        final double dy = aLocalDimension.getHeight();

        switch (l.getSide()) {
            case SwingConstants.NORTH:
                b.setRect(b.x, b.y + dy, b.width, b.height - dy);
                break;

            case SwingConstants.SOUTH:
                b.setRect(b.x, b.y, b.width, b.height + dy);
                break;

            case SwingConstants.EAST:
                b.setRect(b.x, b.y, b.width + dx, b.height);
                break;

            case SwingConstants.WEST:
                b.setRect(b.x + dx, b.y, b.width - dx, b.height);
                break;

            case SwingConstants.NORTH_WEST:
                b.setRect(b.x + dx, b.y + dy, b.width - dx, b.height - dy);
                break;

            case SwingConstants.SOUTH_WEST:
                b.setRect(b.x + dx, b.y, b.width - dx, b.height + dy);
                break;

            case SwingConstants.NORTH_EAST:
                b.setRect(b.x, b.y + dy, b.width + dx, b.height - dy);
                break;

            case SwingConstants.SOUTH_EAST:
                b.setRect(b.x, b.y, b.width + dx, b.height + dy);
                break;
            default:
                throw new RuntimeException("Invalid side returned from PBoundsLocator");
        }

        boolean flipX = false;
        boolean flipY = false;

        if (b.width < 0) {
            flipX = true;
            b.width = -b.width;
            b.x -= b.width;
        }

        if (b.height < 0) {
            flipY = true;
            b.height = -b.height;
            b.y -= b.height;
        }

        if (flipX || flipY) {
            flipSiblingBoundsHandles(flipX, flipY);
        }

        n.setBounds(b);
    }

    /**
     * Call back invoked when the drag is finished.
     * 
     * @param aLocalPoint point on the handle where the drag was ended
     * @param aEvent event responsible for the end of the drag
     */
    public void endHandleDrag(final Point2D aLocalPoint, final PInputEvent aEvent) {
        final PBoundsLocator l = (PBoundsLocator) getLocator();
        l.getNode().endResizeBounds();
    }

    /**
     * Moves locators around so that they are still logically positioned.
     * 
     * This is needed when a node is resized until its width or height is
     * negative.
     * 
     * @param flipX whether to allow flipping along the x direction
     * @param flipY whether to allow flipping along the y direction
     */
    public void flipSiblingBoundsHandles(final boolean flipX, final boolean flipY) {
        final Iterator i = getParent().getChildrenIterator();
        while (i.hasNext()) {
            final Object each = i.next();
            if (each instanceof PBoundsHandle) {
                ((PBoundsHandle) each).flipHandleIfNeeded(flipX, flipY);
            }
        }
    }

    /**
     * Flips this bounds around if it needs to be. This is required when a node
     * is resized until either its height or width is negative.
     * 
     * @param flipX whether to allow flipping along the x direction
     * @param flipY whether to allow flipping along the y direction
     */
    public void flipHandleIfNeeded(final boolean flipX, final boolean flipY) {
        final PBoundsLocator l = (PBoundsLocator) getLocator();

        if (!flipX && !flipY) {
            return;
        }

        switch (l.getSide()) {
            case SwingConstants.NORTH:
                if (flipY) {
                    l.setSide(SwingConstants.SOUTH);
                }
                break;

            case SwingConstants.SOUTH:
                if (flipY) {
                    l.setSide(SwingConstants.NORTH);
                }
                break;

            case SwingConstants.EAST:
                if (flipX) {
                    l.setSide(SwingConstants.WEST);
                }
                break;

            case SwingConstants.WEST:
                if (flipX) {
                    l.setSide(SwingConstants.EAST);
                }
                break;

            case SwingConstants.NORTH_WEST:
                if (flipX && flipY) {
                    l.setSide(SwingConstants.SOUTH_EAST);
                }
                else if (flipX) {
                    l.setSide(SwingConstants.NORTH_EAST);
                }
                else if (flipY) {
                    l.setSide(SwingConstants.SOUTH_WEST);
                }
                break;

            case SwingConstants.SOUTH_WEST:
                if (flipX && flipY) {
                    l.setSide(SwingConstants.NORTH_EAST);
                }
                else if (flipX) {
                    l.setSide(SwingConstants.SOUTH_EAST);
                }
                else if (flipY) {
                    l.setSide(SwingConstants.NORTH_WEST);
                }
                break;

            case SwingConstants.NORTH_EAST:
                if (flipX && flipY) {
                    l.setSide(SwingConstants.SOUTH_WEST);
                }
                else if (flipX) {
                    l.setSide(SwingConstants.NORTH_WEST);
                }
                else if (flipY) {
                    l.setSide(SwingConstants.SOUTH_EAST);
                }
                break;

            case SwingConstants.SOUTH_EAST:
                if (flipX && flipY) {
                    l.setSide(SwingConstants.NORTH_WEST);
                }
                else if (flipX) {
                    l.setSide(SwingConstants.SOUTH_WEST);
                }
                else if (flipY) {
                    l.setSide(SwingConstants.NORTH_EAST);
                }
                break;

            default:
                throw new RuntimeException("Invalid side received from PBoundsLocator");
        }

        // reset locator to update layout
        setLocator(l);
    }

    /**
     * Returns an appropriate handle for the given side of a node.
     * 
     * @param side side given as SwingConstants values.
     * 
     * @return Appropriate cursor, or null if none can be identified.
     */
    public Cursor getCursorFor(final int side) {
        switch (side) {
            case SwingConstants.NORTH:
                return new Cursor(Cursor.N_RESIZE_CURSOR);

            case SwingConstants.SOUTH:
                return new Cursor(Cursor.S_RESIZE_CURSOR);

            case SwingConstants.EAST:
                return new Cursor(Cursor.E_RESIZE_CURSOR);

            case SwingConstants.WEST:
                return new Cursor(Cursor.W_RESIZE_CURSOR);

            case SwingConstants.NORTH_WEST:
                return new Cursor(Cursor.NW_RESIZE_CURSOR);

            case SwingConstants.SOUTH_WEST:
                return new Cursor(Cursor.SW_RESIZE_CURSOR);

            case SwingConstants.NORTH_EAST:
                return new Cursor(Cursor.NE_RESIZE_CURSOR);

            case SwingConstants.SOUTH_EAST:
                return new Cursor(Cursor.SE_RESIZE_CURSOR);
            default:
                return null;
        }
    }

    private class MouseCursorUpdateHandler extends PBasicInputEventHandler {
        boolean cursorPushed;

        public MouseCursorUpdateHandler() {
            cursorPushed = false;
        }

        /**
         * When mouse is entered, push appropriate mouse cursor on cursor stack.
         * 
         * @param aEvent the mouse entered event
         */
        public void mouseEntered(final PInputEvent aEvent) {
            if (!cursorPushed) {
                aEvent.pushCursor(getCursorFor(((PBoundsLocator) getLocator()).getSide()));
                cursorPushed = true;
            }
        }

        /**
         * When mouse leaves, pop cursor from stack.
         * 
         * @param aEvent the mouse exited event
         */
        public void mouseExited(final PInputEvent aEvent) {
            if (cursorPushed) {
                final PPickPath focus = aEvent.getInputManager().getMouseFocus();

                if (focus == null || focus.getPickedNode() != PBoundsHandle.this) {
                    aEvent.popCursor();
                    cursorPushed = false;
                }
            }
        }

        /**
         * If mouse is released, cursor should pop as well.
         * 
         * @param event the mouse released event
         */
        public void mouseReleased(final PInputEvent event) {
            if (cursorPushed) {
                event.popCursor();
                cursorPushed = false;
            }
        }
    }
}
