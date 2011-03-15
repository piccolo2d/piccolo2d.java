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
package org.piccolo2d.extras.util;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A shape that can be used to represent hand drawn lines.
 */
public class LineShape implements Shape, MutablePoints {
    private MutablePoints points;
    private final Rectangle2D bounds = new Rectangle2D.Double();

    /**
     * Constructs a LineShape from a list of mutable points.
     * 
     * @param points points to use when constructing LineShape
     */
    public LineShape(final MutablePoints points) {
        setPoints(points);
    }

    /**
     * Changes the LineShape so that it's composed of the given points.
     * 
     * @param points new Points to use as this shape's path
     */
    public void setPoints(final MutablePoints points) {
        if (points == null) {
            this.points = new XYArray();
        }
        else {
            this.points = points;
        }
    }

    /**
     * Returns the number points in this LineShape.
     * 
     * @return # of points in this line shape
     */
    public int getPointCount() {
        return points.getPointCount();
    }

    /**
     * Returns the x component of the point at the given index.
     * 
     * @param pointIndex index of desired point
     * 
     * @return x component of indexed point
     */
    public double getX(final int pointIndex) {
        return points.getX(pointIndex);
    }

    /**
     * Returns the y component of the point at the given index.
     * 
     * @param pointIndex index of desired point
     * 
     * @return y component of indexed point
     */
    public double getY(final int pointIndex) {
        return points.getY(pointIndex);
    }

    /**
     * Copies the point at the given index into the destination point.
     * 
     * @param pointIndex the index of the desired point
     * @param destinationPoint the point into which to load the values, or null
     *            if a new point is desired
     * 
     * @return destinationPoint or new one if null was provided
     */
    public Point2D getPoint(final int pointIndex, final Point2D destinationPoint) {
        return points.getPoint(pointIndex, destinationPoint);
    }

    /**
     * Computes the bounds of this LineShape and stores them in the provided
     * rectangle.
     * 
     * @param dst rectangle to populate with this LineShape's bounds
     * @return the bounds
     */
    public Rectangle2D getBounds(final Rectangle2D dst) {
        points.getBounds(dst);
        return dst;
    }

    /**
     * Recalculates the bounds of this LineShape.
     */
    public void updateBounds() {
        bounds.setRect(0.0d, 0.0d, 0.0d, 0.0d);
        points.getBounds(bounds);
    }

    /**
     * Sets the coordinate of the point at the given index.
     * 
     * @param pointIndex index of the point to change
     * @param x x component to assign to the point
     * @param y y component to assign to the point
     */
    public void setPoint(final int pointIndex, final double x, final double y) {
        points.setPoint(pointIndex, x, y);
        updateBounds();
    }

    /**
     * Adds a point with the given coordinates at the desired index.
     * 
     * @param pointIndex Index at which to add the point
     * @param x x component of the new point
     * @param y y component of the new point
     */
    public void addPoint(final int pointIndex, final double x, final double y) {
        points.addPoint(pointIndex, x, y);
        updateBounds();
    }

    /**
     * Removes n points from the LineShape starting at the provided index.
     * 
     * @param pointIndex Starting index from which points are being removed
     * @param num The number of sequential points to remove
     */
    public void removePoints(final int pointIndex, final int num) {
        points.removePoints(pointIndex, num);
        updateBounds();
    }

    /**
     * Applies the given transform to all points in this LineShape.
     * 
     * @param transform Transform to apply
     */
    public void transformPoints(final AffineTransform transform) {
        final XYArray newPoints = new XYArray(points.getPointCount());
        newPoints.appendPoints(points);
        newPoints.transformPoints(transform);
        points = newPoints;
    }

    /**
     * Returns the current points of this LineShape as a simple Rectangle.
     * 
     * @return bounds of this LineShape
     */
    public Rectangle getBounds() {
        return new Rectangle((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds
                .getHeight());
    }

    /**
     * Returns the current bounds in Rectangle2D format.
     * 
     * @return bounds of LineShape as a Rectangle2D
     */
    public Rectangle2D getBounds2D() {
        return bounds;
    }

    /**
     * Returns whether the given coordinates are on the line defined by (x1,y1)
     * and (x2,y2) within the given distance.
     * 
     * @param x x component of point being tested
     * @param y y component of point being tested
     * @param x1 x component of start point of line segment
     * @param y1 y component of start point of line segment
     * @param x2 x component of end point of line segment
     * @param y2 y component of end point of line segment
     * @param min whether the point should be constrained to "after" the start
     *            of the segment
     * @param max whether the point should be constrained to "before" the end of
     *            the segment
     * @param distance distance from line acceptable as "touching"
     * @return whether the point (x,y) is near enough to the given line
     */
    public static boolean contains(final double x, final double y, final double x1, final double y1, final double x2,
            final double y2, final boolean min, final boolean max, final double distance) {
        double dx = x2 - x1;
        double dy = y2 - y1;

        // If line is a point then bail out
        if (dx == 0 && dy == 0) {
            return false;
        }

        final double dx2 = dx * dx;
        final double dy2 = dy * dy;

        // distance along segment as a ratio or the (x1,y1)->(x2,y2) vector
        final double p;
        if (dx != 0) {
            p = ((x - x1) / dx + dy * (y - y1) / dx2) / (1 + dy2 / dx2);
        }
        else {
            p = ((y - y1) / dy + dx * (x - x1) / dy2) / (1 + dx2 / dy2);
        }

        // Point is not "beside" the segment and it's been disallowed, bail.
        if (min && p < 0 || max && p > 1.0) {
            return false;
        }

        dx = p * dx + x1 - x;
        dy = p * dy + y1 - y;

        final double len = dx * dx + dy * dy;
        return len < distance;
    }

    /**
     * Returns true if the given coordinates are within d units from any segment
     * of the LineShape.
     * 
     * @param x x component of point being tested
     * @param y y component of point being tested
     * @param d acceptable distance
     * @return true if point is close enough to the LineShape
     */
    public boolean contains(final double x, final double y, final double d) {
        double x1, y1, x2, y2;
        if (points.getPointCount() == 0) {
            return false;
        }
        x2 = points.getX(0);
        y2 = points.getX(0);
        for (int i = 0; i < points.getPointCount(); i++) {
            x1 = x2;
            y1 = y2;
            x2 = points.getX(i);
            y2 = points.getX(i);
            if (contains(x, y, x1, y1, x2, y2, false, false, d)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if point is within 2 pixels of any line segment of this
     * LineShape.
     * 
     * @param x x component of point being tested
     * @param y y component of point being tested
     * @return true if point is within 2 pixels of any of this LineShape's
     *         segments
     */
    public boolean contains(final double x, final double y) {
        return contains(x, y, 2.0d);
    }

    /**
     * Returns true if point is within 2 pixels of any line segment of this
     * LineShape.
     * 
     * @param p point being tested
     * @return true if point is within 2 pixels of any of this LineShape's
     *         segments
     */
    public boolean contains(final Point2D p) {
        return contains(p.getX(), p.getY());
    }

    /**
     * Returns true if the two segments defined by (x1,y1)->(x2,y2) and
     * (x3,y3)->(x4,y4) intersect. Optional fields allow for consideration of
     * extending the segments to infinity at either end.
     * 
     * @param x1 segment 1's start x component
     * @param y1 segment 1's start y component
     * @param x2 segment 1's end x component
     * @param y2 segment 1's end y component
     * @param x3 segment 2's start x component
     * @param y3 segment 2's start y component
     * @param x4 segment 2's end x component
     * @param y4 segment 2's end y component
     * @param min1 whether the second segment is acceptable if it passes
     *            "before the start of the first segment"
     * @param max1 whether the second segment is acceptable if it passes
     *            "after the end of the first segment"
     * @param min2 whether the first segment is acceptable if it passes
     *            "before the start of the second segment"
     * @param max2 whether the first segment is acceptable if it passes
     *            "after the start of the second segment"
     * @return true if line segments intersect
     */
    public static boolean intersects(final double x1, final double y1, final double x2, final double y2,
            final double x3, final double y3, final double x4, final double y4, final boolean min1, final boolean max1,
            final boolean min2, final boolean max2) {
        final double dx1 = x2 - x1, dy1 = y2 - y1, dx2 = x4 - x3, dy2 = y4 - y3;
        double d, p2, p1;

        if (dy1 != 0.0) {
            d = dx1 / dy1;
            p2 = (x3 - x1 + d * (y1 - y3)) / (d * dy2 - dx2);
            p1 = (dy2 * p2 + y3 - y1) / dy1;
        }
        else if (dy2 != 0.0) {
            d = dx2 / dy2;
            p1 = (x1 - x3 + d * (y3 - y1)) / (d * dy1 - dx1);
            p2 = (dy1 * p1 + y1 - y3) / dy2;
        }
        else if (dx1 != 0.0) {
            d = dy1 / dx1;
            p2 = (y3 - y1 + d * (x1 - x3)) / (d * dx2 - dy2);
            p1 = (dx2 * p2 + x3 - x1) / dx1;
        }
        else if (dx2 != 0.0) {
            d = dy2 / dx2;
            p1 = (y1 - y3 + d * (x3 - x1)) / (d * dx1 - dy1);
            p2 = (dx1 * p1 + x1 - x3) / dx2;
        }
        else {
            return false;
        }
        return (!min1 || p1 >= 0.0) && (!max1 || p1 <= 1.0) && (!min2 || p2 >= 0.0) && (!max2 || p2 <= 1.0);
    }

    /**
     * Returns true if any segment crosses an edge of the rectangle.
     * 
     * @param x left of rectangle to be tested
     * @param y top of rectangle to be tested
     * @param w width of rectangle to be tested
     * @param h height of rectangle to be tested
     * 
     * @return true if rectangle intersects
     */
    public boolean intersects(final double x, final double y, final double w, final double h) {
        double x1, y1, x2, y2;
        if (points.getPointCount() == 0) {
            return false;
        }
        x2 = points.getX(0);
        y2 = points.getY(0);
        for (int i = 0; i < points.getPointCount(); i++) {
            x1 = x2;
            y1 = y2;
            x2 = points.getX(i);
            y2 = points.getY(i);
            if (intersects(x, y, x + w, y, x1, y1, x2, y2, true, true, true, true)
                    || intersects(x + w, y, x + w, y + h, x1, y1, x2, y2, true, true, true, true)
                    || intersects(x + w, y + h, x, y + h, x1, y1, x2, y2, true, true, true, true)
                    || intersects(x, y + h, x, y, x1, y1, x2, y2, true, true, true, true)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if any segment crosses an edge of the rectangle.
     * 
     * @param r rectangle to be tested
     * @return true if rectangle intersects
     */
    public boolean intersects(final Rectangle2D r) {
        return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    /**
     * Whether the LineShape contains the rectangle defined.
     * 
     * @param x left of defined rectangle
     * @param y top of defined rectangle
     * @param width width of defined rectangle
     * @param height height of defined rectangle
     * @return true if rectangle is contained
     */
    public boolean contains(final double x, final double y, final double width, final double height) {
        return contains(x, y) && contains(x + width, y) && contains(x, y + height) && contains(x + width, y + height);
    }

    /**
     * Whether the LineShape contains the rectangle.
     * 
     * @param r rectangle being tested
     * @return true if rectangle is contained
     */
    public boolean contains(final Rectangle2D r) {
        return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    /**
     * Returns an iterator that can be used to iterate of the segments of this
     * LineShape. Optionally applying the given transform before returning it.
     * 
     * @param at optional transform to apply to segment before returning it. May
     *            be null
     * @return iterator for iterating segments of this LineShape
     */
    public PathIterator getPathIterator(final AffineTransform at) {
        return new LinePathIterator(points, at);
    }

    /**
     * Returns an iterator that can be used to iterate of the segments of this
     * LineShape. Optionally applying the given transform before returning it.
     * 
     * @param at optional transform to apply to segment before returning it. May
     *            be null
     * @param flatness ignored completely
     * @return iterator for iterating segments of this LineShape
     */
    public PathIterator getPathIterator(final AffineTransform at, final double flatness) {
        return new LinePathIterator(points, at);
    }

    private static class LinePathIterator implements PathIterator {

        private final Points points;
        private final AffineTransform trans;
        private int i = 0;

        /**
         * Constructs a LinePathIterator for the given points and with an
         * optional transform.
         * 
         * @param points points to be iterated
         * @param trans optional iterator to apply to paths before returning
         *            them
         */
        public LinePathIterator(final Points points, final AffineTransform trans) {
            this.points = points;
            this.trans = trans;
        }

        /**
         * Returns the winding rule being applied when selecting next paths.
         * 
         * @return GeneralPath.WIND_EVEN_ODD since that's the only policy
         *         supported
         */
        public int getWindingRule() {
            return GeneralPath.WIND_EVEN_ODD;
        }

        /**
         * Returns true if there are no more paths to iterate over.
         * 
         * @return true if iteration is done
         */
        public boolean isDone() {
            return i >= points.getPointCount();
        }

        /**
         * Moves to the next path.
         */
        public void next() {
            i++;
        }

        private final Point2D tempPoint = new Point2D.Double();

        private void currentSegment() {
            tempPoint.setLocation(points.getX(i), points.getY(i));
            if (trans != null) {
                trans.transform(tempPoint, tempPoint);
            }
        }

        /**
         * Populates the given array with the current segment and returns the
         * type of segment.
         * 
         * @param coords array to be populated
         * 
         * @return type of segment SEG_MOVETO or SEG_LINETO
         */
        public int currentSegment(final float[] coords) {
            currentSegment();
            coords[0] = (float) tempPoint.getX();
            coords[1] = (float) tempPoint.getY();
            if (i == 0) {
                return PathIterator.SEG_MOVETO;
            }
            else {
                return PathIterator.SEG_LINETO;
            }
        }

        /**
         * Populates the given array with the current segment and returns the
         * type of segment.
         * 
         * @param coords array to be populated
         * 
         * @return type of segment SEG_MOVETO or SEG_LINETO
         */
        public int currentSegment(final double[] coords) {
            currentSegment();
            coords[0] = tempPoint.getX();
            coords[1] = tempPoint.getY();
            if (i == 0) {
                return PathIterator.SEG_MOVETO;
            }
            else {
                return PathIterator.SEG_LINETO;
            }
        }
    }
}
