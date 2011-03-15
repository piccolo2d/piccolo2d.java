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
package org.piccolo2d.util;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * <b>PBounds</b> is simply a Rectangle2D.Double with extra methods that more
 * properly deal with the case when the rectangle is "empty". A PBounds has an
 * extra bit to store emptiness. In this state, adding new geometry replaces the
 * current geometry. A PBounds is emptied with the reset() method. A useful side
 * effect of the reset method is that it only modifies the fIsEmpty variable,
 * the other x, y, with, height variables are left alone. This is used by
 * Piccolo's layout management system to see if a the full bounds of a node has
 * really changed when it is recomputed. See PNode.validateLayout.
 * <P>
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PBounds extends Rectangle2D.Double implements Serializable {
    /**
     * Allows for future serialization code to understand versioned binary
     * formats.
     */
    private static final long serialVersionUID = 1L;

    private boolean isEmpty = true;

    /**
     * Creates an empty bounds.
     */
    public PBounds() {
        super();
    }

    /**
     * Creates a bounds identical to the one provided.
     * 
     * @param aBounds bounds to be copied
     */
    public PBounds(final PBounds aBounds) {
        this(aBounds.x, aBounds.y, aBounds.width, aBounds.height);
        isEmpty = aBounds.isEmpty();
    }

    /**
     * Creates a bounds with the same shape as the rectangle provided.
     * 
     * @param aBounds rectangle to be copied
     */
    public PBounds(final Rectangle2D aBounds) {
        this(aBounds.getX(), aBounds.getY(), aBounds.getWidth(), aBounds.getHeight());
        isEmpty = aBounds.isEmpty();
    }

    /**
     * Constructs a PBounds object with the given center point and the specified
     * insets.
     * 
     * @param aCenterPoint resulting center point of the PBounds object
     * @param insetX distance from left and right the center should be
     * @param insetY distance from top and bottom the center should be
     */
    public PBounds(final Point2D aCenterPoint, final double insetX, final double insetY) {
        this(aCenterPoint.getX(), aCenterPoint.getY(), 0, 0);
        inset(insetX, insetY);
    }

    /**
     * Constructs a PBounds object at the given coordinates with the given
     * dimensions.
     * 
     * @param x left of bounds
     * @param y top of bounds
     * @param width width of bounds
     * @param height height of bounds
     */
    public PBounds(final double x, final double y, final double width, final double height) {
        super(x, y, width, height);
        isEmpty = false;
    }

    /**
     * Returns a clone of this node.
     * 
     * @return cloned copy of this bounds
     */
    public Object clone() {
        return new PBounds(this);
    }

    /**
     * Returns true if this bounds has been flagged as empty. Not necessarily if
     * it is empty.
     * 
     * @return true if bounds marked as empty
     */
    public boolean isEmpty() {
        return isEmpty;
    }

    /**
     * Flags this bounds as empty.
     * 
     * @return itself for chaining
     */
    public PBounds reset() {
        isEmpty = true;
        return this;
    }

    /**
     * Resets the bounds to (0,0,0,0) and flags it as empty.
     * 
     * @return itself for chaining
     */
    public PBounds resetToZero() {
        x = 0;
        y = 0;
        width = 0;
        height = 0;
        isEmpty = true;
        return this;
    }

    /**
     * Sets the bounds to the same shape as the rectangle. And flags the bounds
     * as not empty.
     * 
     * @param r rectangle to copy
     */
    public void setRect(final Rectangle2D r) {
        super.setRect(r);
        isEmpty = false;
    }

    /**
     * Sets the bounds to the same shape as the bounds provided. And flags the
     * bounds as not empty.
     * 
     * @param b bounds to copy
     */
    public void setRect(final PBounds b) {
        isEmpty = b.isEmpty;
        x = b.x;
        y = b.y;
        width = b.width;
        height = b.height;
    }

    /**
     * Sets the shape of the bounds to the position and dimension provided.
     * 
     * @param x new left of bounds
     * @param y new top of bounds
     * @param width new width of bounds
     * @param height new height of bounds
     */
    public void setRect(final double x, final double y, final double width, final double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        isEmpty = false;
    }

    /**
     * Grows the bounds to contain the coordinate provided.
     * 
     * @param newx x component of point
     * @param newy y component of point
     */
    public void add(final double newx, final double newy) {
        if (isEmpty) {
            setRect(newx, newy, 0, 0);
            isEmpty = false;
        }
        else {
            super.add(newx, newy);
        }
    }

    /**
     * Grows bounds to contain the rectangle if needed.
     * 
     * @param r rectangle being added
     */
    public void add(final Rectangle2D r) {
        if (isEmpty) {
            setRect(r);
        }
        else {
            super.add(r);
        }
    }

    /**
     * Changes this bounds to contain the provided bounds.
     * 
     * @param bounds bounds being added
     */
    public void add(final PBounds bounds) {
        if (bounds.isEmpty) {
            return;
        }
        else if (isEmpty) {
            x = bounds.x;
            y = bounds.y;
            width = bounds.width;
            height = bounds.height;
            isEmpty = false;
        }
        else {
            final double x1 = Math.min(x, bounds.x);
            final double y1 = Math.min(y, bounds.y);
            final double x2 = Math.max(x + width, bounds.x + bounds.width);
            final double y2 = Math.max(y + height, bounds.y + bounds.height);

            x = x1;
            y = y1;
            width = x2 - x1;
            height = y2 - y1;
            isEmpty = false;
        }
    }

    /**
     * Returns the x,y coordinate of the bounds.
     * 
     * @return coordinate of the bounds
     */
    public Point2D getOrigin() {
        return new Point2D.Double(x, y);
    }

    /**
     * Changes the origin of these bounds. And flags it as non-empty.
     * 
     * @param x new x component of bounds
     * @param y new y component of the bounds
     * @return the modified PBounds with its new origin
     */
    public PBounds setOrigin(final double x, final double y) {
        this.x = x;
        this.y = y;
        isEmpty = false;
        return this;
    }

    /**
     * Returns the size of the bounds.
     * 
     * @return size of the bounds
     */
    public Dimension2D getSize() {
        return new PDimension(width, height);
    }

    /**
     * Changes the size of the bounds, but retains the origin.
     * 
     * @param width new width of the bounds
     * @param height new height of the bounds
     */
    public void setSize(final double width, final double height) {
        setRect(x, y, width, height);
    }

    /**
     * Returns the midpoint of the bounds.
     * 
     * @return midpoint of the bounds
     */
    public Point2D getCenter2D() {
        return new Point2D.Double(getCenterX(), getCenterY());
    }

    /**
     * Translates the bounds by the given deltas.
     * 
     * @param dx amount to move x
     * @param dy amount to move y
     * @return itself for chaining
     */
    public PBounds moveBy(final double dx, final double dy) {
        setOrigin(x + dx, y + dy);
        return this;
    }

    /**
     * Rounds the rectangle to the next largest bounds who's measurements are
     * integers. Note: this is not the same as rounding its measurements.
     */
    public void expandNearestIntegerDimensions() {
        x = Math.floor(x);
        y = Math.floor(y);
        width = Math.ceil(width);
        height = Math.ceil(height);
    }

    /**
     * Adjust the measurements of this bounds so that they are the amounts given
     * "in" from their previous border.
     * 
     * @param dx amount to move in from border along horizontal axis
     * @param dy amount to move in from border along vertical axis
     * @return itself for chaining
     */
    public PBounds inset(final double dx, final double dy) {
        setRect(x + dx, y + dy, width - dx * 2, height - dy * 2);
        return this;
    }

    /**
     * Returns the required translation in order for this bounds origin to sit
     * on the center of the provided rectangle.
     * 
     * @param targetBounds rectangle to measure the center of
     * @return the delta required to move to center of the targetBounds
     */
    public PDimension deltaRequiredToCenter(final Rectangle2D targetBounds) {
        final PDimension result = new PDimension();
        final double xDelta = getCenterX() - targetBounds.getCenterX();
        final double yDelta = getCenterY() - targetBounds.getCenterY();
        result.setSize(xDelta, yDelta);
        return result;
    }

    /**
     * Returns the required translation in order for these to contain the bounds
     * provided.
     * 
     * @param targetBounds rectangle to measure the center of
     * @return the delta required in order for the bounds to overlap completely
     *         the targetBounds
     */
    public PDimension deltaRequiredToContain(final Rectangle2D targetBounds) {
        final PDimension result = new PDimension();

        if (contains(targetBounds)) {
            return result;
        }

        final double targetMaxX = targetBounds.getMaxX();
        final double targetMinX = targetBounds.getMinX();
        final double targetMaxY = targetBounds.getMaxY();
        final double targetMinY = targetBounds.getMinY();
        final double maxX = getMaxX();
        final double minX = getMinX();
        final double maxY = getMaxY();
        final double minY = getMinY();

        if (targetMaxX > maxX ^ targetMinX < minX) {
            final double difMaxX = targetMaxX - maxX;
            final double difMinX = targetMinX - minX;
            if (Math.abs(difMaxX) < Math.abs(difMinX)) {
                result.width = difMaxX;
            }
            else {
                result.width = difMinX;
            }
        }

        if (targetMaxY > maxY ^ targetMinY < minY) {
            final double difMaxY = targetMaxY - maxY;
            final double difMinY = targetMinY - minY;
            if (Math.abs(difMaxY) < Math.abs(difMinY)) {
                result.height = difMaxY;
            }
            else {
                result.height = difMinY;
            }
        }

        return result;
    }

    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(width);
        out.writeDouble(height);
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        x = in.readDouble();
        y = in.readDouble();
        width = in.readDouble();
        height = in.readDouble();
    }

    /**
     * Returns a string representation of this PBounds for debugging purposes.
     * 
     * @return string representation of this PBounds
     */
    public String toString() {
        final StringBuffer result = new StringBuffer();

        result.append(getClass().getName().replaceAll(".*\\.", ""));
        result.append('[');

        if (isEmpty) {
            result.append("EMPTY");
        }
        else {
            result.append("x=");
            result.append(x);
            result.append(",y=");
            result.append(y);
            result.append(",width=");
            result.append(width);
            result.append(",height=");
            result.append(height);
        }

        result.append(']');

        return result.toString();
    }
}
