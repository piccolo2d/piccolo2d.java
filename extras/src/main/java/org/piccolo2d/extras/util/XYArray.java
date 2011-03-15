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

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Represents a sequence as points that's internally stored as a single array of
 * point components.
 */
public class XYArray implements MutablePoints, Cloneable {
    /** The coordinates of the points, specifically 2x the number of points. */
    private double[] points = null;

    /** the number of valid x, y pairs. */
    private int numPoints = 0;

    /**
     * Constructs an XYArray wrapping the given points.
     * 
     * @param points array of coordinates defining the points
     */
    public XYArray(final double[] points) {
        initPoints(points, points.length / 2);
    }

    /**
     * Constructs an XYArray of the given size.
     * 
     * @param n number of points XYArray should contain
     */
    public XYArray(final int n) {
        initPoints(null, n);
    }

    /**
     * Constructs an empty XYArray.
     */
    public XYArray() {
        this(0);
    }

    /**
     * Returns the number of points this XYArray represents.
     * 
     * @return number of points
     */
    public int getPointCount() {
        return numPoints;
    }

    /**
     * Converts negative indexes to positive ones by adding numPoints to it.
     * 
     * @param i index to be normalized
     * @return normalized index
     */
    private int normalize(final int i) {
        if (i >= numPoints) {
            throw new IllegalArgumentException("The point index " + i + " is not below " + numPoints);
        }

        if (i < 0) {
            return numPoints + i;
        }
        else {
            return i;
        }
    }

    /**
     * Returns the x component of the point at the given index.
     * 
     * @param i index of point
     * @return x component of point at given index
     */
    public double getX(final int i) {
        return points[normalize(i) * 2];
    }

    /**
     * Returns the y component of the point at the given index.
     * 
     * @param i index of point
     * @return y component of point at given index
     */
    public double getY(final int i) {
        return points[normalize(i) * 2 + 1];
    }

    /**
     * Returns modified point representing the wrapped point at the given index.
     * 
     * @param i index of desired point
     * @param dst point to be modified
     * @return dst
     */
    public Point2D getPoint(final int i, final Point2D dst) {
        final int pointIndex = normalize(i);
        dst.setLocation(points[pointIndex * 2], points[pointIndex * 2 + 1]);
        return dst;
    }

    /**
     * Sets the x component of the point at the given index.
     * 
     * @param i index of point to modify
     * @param x new x component
     */
    public void setX(final int i, final double x) {
        points[normalize(i) * 2] = x;
    }

    /**
     * Sets the y component of the point at the given index.
     * 
     * @param i index of point to modify
     * @param y new y component
     */
    public void setY(final int i, final double y) {
        points[normalize(i) * 2 + 1] = y;
    }

    /**
     * Sets the coordinates of the point at the given index.
     * 
     * @param i index of point to modify
     * @param x new x component
     * @param y new y component
     */
    public void setPoint(final int i, final double x, final double y) {
        final int pointIndex = normalize(i);
        points[pointIndex * 2] = x;
        points[pointIndex * 2 + 1] = y;
    }

    /**
     * Sets the coordinates of the point at the given index.
     * 
     * @param i index of point to modify
     * @param pt point from which coordinate is to be extracted
     */
    public void setPoint(final int i, final Point2D pt) {
        setPoint(i, pt.getX(), pt.getY());
    }

    /**
     * Applies the given transform to all points represented by this XYArray.
     * 
     * @param t transform to apply
     */
    public void transformPoints(final AffineTransform t) {
        t.transform(points, 0, points, 0, numPoints);
    }

    /**
     * Modifies dst to be the bounding box of the points represented by this
     * XYArray.
     * 
     * @param dst rectangle to be modified
     * @return the bounding rectangle
     */
    public Rectangle2D getBounds(final Rectangle2D dst) {
        int i = 0;
        if (dst.isEmpty() && getPointCount() > 0) {
            dst.setRect(getX(i), getY(i), 1.0d, 1.0d);
            i++;
        }
        while (i < getPointCount()) {
            dst.add(getX(i), getY(i));
            i++;
        }
        return dst;
    }

    /**
     * Constructs an array of point coordinates for n points and copies the old
     * values if provided.
     * 
     * @param points array to populate with point values, or null to generate a
     *            new array
     * @param n number of points
     * @param old old values to repopulate the array with, or null if not
     *            desired
     * @return initialized points
     */
    public static double[] initPoints(final double[] points, final int n, final double[] old) {
        final double[] result;
        if (points == null || n * 2 > points.length) {
            result = new double[n * 2];
        }
        else {
            result = points;
        }
        if (old != null && result != old) {
            System.arraycopy(old, 0, result, 0, Math.min(old.length, n * 2));
        }

        return result;
    }

    /**
     * Constructs an array of point coordinates for n points.
     * 
     * @param srcPoints array to populate with point values, or null to generate
     *            a new array
     * @param n number of points
     */
    private void initPoints(final double[] srcPoints, final int n) {
        this.points = initPoints(srcPoints, n, this.points);
        if (srcPoints == null) {
            numPoints = 0;
        }
        else {
            numPoints = srcPoints.length / 2;
        }
    }

    /**
     * Adds a subsequence of the points provided at the given position.
     * 
     * @param index position at which the points should be inserted
     * @param newPoints points from which to extract the subsequence of points
     * @param start the start index within newPoints to start extracting points
     * @param end the end index within newPoints to finish extracting points
     */
    public void addPoints(final int index, final Points newPoints, final int start, final int end) {
        final int sanitizedEnd;
        if (end < 0) {
            sanitizedEnd = newPoints.getPointCount() + end + 1;
        }
        else {
            sanitizedEnd = end;
        }
        final int n = numPoints + sanitizedEnd - start;
        points = initPoints(points, n, points);
        final int pos1 = index * 2;
        final int pos2 = (index + sanitizedEnd - start) * 2;
        final int len = (numPoints - index) * 2;

        System.arraycopy(points, pos1, points, pos2, len);

        numPoints = n;
        if (newPoints != null) {
            for (int count = 0, currentPos = start; currentPos < sanitizedEnd; count++, currentPos++) {
                setPoint(index + count, newPoints.getX(currentPos), newPoints.getY(currentPos));
            }
        }
    }

    /**
     * Inserts all the provided points at the given position.
     * 
     * @param pos index at which to insert the points
     * @param pts points to be inserted
     */
    public void addPoints(final int pos, final Points pts) {
        addPoints(pos, pts, 0, pts.getPointCount());
    }

    /**
     * Adds the provided points to the end of the points.
     * 
     * @param pts points to be added
     */
    public void appendPoints(final Points pts) {
        addPoints(numPoints, pts);
    }

    /**
     * Creates an XYArray representing the given points.
     * 
     * @param pts points to copy
     * @return XYArray representing the points provided
     */
    public static XYArray copyPoints(final Points pts) {
        final XYArray newList = new XYArray(pts.getPointCount());
        newList.appendPoints(pts);
        return newList;
    }

    /**
     * Adds a point to the index provided.
     * 
     * @param pos index at which to add the point
     * @param x x coordinate of new point
     * @param y y coordinate of new point
     */
    public void addPoint(final int pos, final double x, final double y) {
        addPoints(pos, null, 0, 1);
        setPoint(pos, x, y);
    }

    /**
     * Inserts the given point at the given index.
     * 
     * @param pos index at which to add the point
     * @param pt point to be inserted *
     */
    public void addPoint(final int pos, final Point2D pt) {
        addPoint(pos, pt.getX(), pt.getY());
    }

    /**
     * Remove a subsequence of points from this XYArray starting as pos.
     * 
     * @param pos the position to start removing points
     * @param num the number of points to remove
     */
    public void removePoints(final int pos, final int num) {
        int sanitizedNum = Math.min(num, numPoints - pos);
        if (sanitizedNum > 0) {
            System.arraycopy(points, (pos + sanitizedNum) * 2, points, pos * 2, (numPoints - (pos + sanitizedNum)) * 2);
            numPoints -= sanitizedNum;
        }
    }

    /**
     * Remove all points from this XYArray.
     */
    public void removeAllPoints() {
        removePoints(0, numPoints);
    }

    /**
     * Returns a clone of this XYArray ensuring a deep copy of coordinates is
     * made.
     * 
     * @return cloned XYArray
     */
    public Object clone() {
        XYArray ps = null;

        try {
            ps = (XYArray) super.clone();
            ps.points = initPoints(ps.points, numPoints, points);
            ps.numPoints = numPoints;
        }
        catch (final CloneNotSupportedException e) {
            // wow, this is terrible.
        }

        return ps;
    }
}
