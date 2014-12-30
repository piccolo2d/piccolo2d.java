/*
 * Copyright (c) 2008-2012, Piccolo2D project, http://piccolo2d.org
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
package org.piccolo2d.nodes;

import java.awt.Shape;
import java.awt.Stroke;

import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.PathIterator;

/**
 * Abstract path node.
 */
public abstract class PPath extends PShape {

    /** Path for this path node. */
    private final Path2D path;


    /**
     * Create a new path node with the specified path.
     *
     * @param path path
     */
    private PPath(final Path2D path) {
        this.path = (Path2D) path.clone();
        updateBoundsFromShape();
    }

    /**
     * Create a new path node with the specified path and stroke.
     *
     * @param path path
     * @param stroke stroke
     */
    private PPath(final Path2D path, final Stroke stroke) {
        this.path = (Path2D) path.clone();
        setStroke(stroke);
    }


    /**
     * Path node with coordinates stored in single precision floating point.
     */
    public static class Float extends PPath {

        /**
         * Create a new empty path node.
         */
        public Float() {
            super(new Path2D.Float());
        }

        /**
         * Create a new empty path node with the specified stroke.
         *
         * @param stroke stroke
         */
        public Float(final Stroke stroke) {
            super(new Path2D.Float(), stroke);
        }

        /**
         * Create a new path node with the specified shape.
         *
         * @param shape shape, must not be null
         * @throws NullPointerException if shape is null
         */
        public Float(final Shape shape) {
            super(new Path2D.Float(shape));
        }

        /**
         * Create a new path node with the specified shape and stroke.
         *
         * @param shape shape, must not be null
         * @param stroke stroke
         * @throws NullPointerException if shape is null
         */
        public Float(final Shape shape, final Stroke stroke) {
            super(new Path2D.Float(shape), stroke);
        }

        /**
         * Create a new path node with the specified path.
         *
         * @param path path, must not be null
         * @throws NullPointerException if path is null
         */
        public Float(final Path2D.Float path) {
            super(path);
        }

        /**
         * Create a new path node with the specified path and stroke.
         *
         * @param path path, must not be null
         * @param stroke stroke, must not be null
         * @throws NullPointerException if path is null
         */
        public Float(final Path2D.Float path, final Stroke stroke) {
            super(path, stroke);
        }
    }

    /**
     * Path node with coordinates stored in double precision floating point.
     */
    public static class Double extends PPath {

        /**
         * Create a new empty path node.
         */
        public Double() {
            super(new Path2D.Double());
        }

        /**
         * Create a new empty path node with the specified stroke.
         *
         * @param stroke stroke
         */
        public Double(final Stroke stroke) {
            super(new Path2D.Double(), stroke);
        }

        /**
         * Create a new path node with the specified shape.
         *
         * @param shape shape, must not be null
         * @throws NullPointerException if shape is null
         */
        public Double(final Shape shape) {
            super(new Path2D.Double(shape));
        }

        /**
         * Create a new path node with the specified shape and stroke.
         *
         * @param shape shape, must not be null
         * @param stroke stroke
         * @throws NullPointerException if shape is null
         */
        public Double(final Shape shape, final Stroke stroke) {
            super(new Path2D.Double(shape), stroke);
        }

        /**
         * Create a new path node with the specified path.
         *
         * @param path path, must not be null
         * @throws NullPointerException if path is null
         */
        public Double(final Path2D.Double path) {
            super(path);
        }

        /**
         * Create a new path node with the specified path and stroke.
         *
         * @param path path, must not be null
         * @param stroke stroke
         * @throws NullPointerException if path is null
         */
        public Double(final Path2D.Double path, final Stroke stroke) {
            super(path, stroke);
        }
    }


    /**
     * Create and return a new path node with the specified arc in single
     * precision floating point coordinates.
     *
     * @param x x coordinate of the upper-left corner of the arc's framing rectangle
     * @param y y coordinate of the upper-left corner of the arc's framing rectangle
     * @param width width of the full ellipse of which this arc is a partial section
     * @param height height of the full ellipse of which this arc is a partial section
     * @param start starting angle of the arc in degrees
     * @param extent angular extent of the arc in degrees
     * @param type closure type for the arc, one of {@link Arc2D#OPEN}, {@link Arc2D#CHORD},
     *    or {@link Arc2D#PIE}
     * @return a new path node with the specified arc in single
     *    precision floating point coordinates
     */
    public static final PPath createArc(final float x,
                                        final float y,
                                        final float width,
                                        final float height,
                                        final float start,
                                        final float extent,
                                        final int type) {
        return new PPath.Float(new Arc2D.Float(x, y, width, height, start, extent, type));
    }

    /**
     * Create and return a new path node with the specified cubic curve in single
     * precision floating point coordinates.
     *
     * @param x1 x coordinate of the start point
     * @param y1 y coordinate of the start point
     * @param ctrlx1 x coordinate of the first control point
     * @param ctrly1 y coordinate of the first control point
     * @param ctrlx2 x coordinate of the second control point
     * @param ctrly2 y coordinate of the second control point
     * @param x2 x coordinate of the end point
     * @param y2 y coordinate of the end point
     * @return a new path node with the specified cubic curve in single
     *    precision floating point coordinates
     */
    public static final PPath createCubicCurve(final float x1,
                                               final float y1,
                                               final float ctrlx1,
                                               final float ctrly1,
                                               final float ctrlx2,
                                               final float ctrly2,
                                               final float x2,
                                               final float y2) {
        return new PPath.Float(new CubicCurve2D.Float(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2));
    }

    /**
     * Create and return a new path node with the specified ellipse in single
     * precision floating point coordinates.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param width width
     * @param height height
     * @return a new path node with the specified ellipse in single
     *    precision floating point coordinates
     */
    public static final PPath createEllipse(final float x, final float y, final float width, final float height) {
        return new PPath.Float(new Ellipse2D.Float(x, y, width, height));
    }

    /**
     * Create and return a new path node with the specified line in single
     * precision floating point coordinates.
     *
     * @param x1 x coordinate of the start point
     * @param y1 y coordinate of the start point
     * @param x2 x coordinate of the end point
     * @param y2 y coordinate of the end point
     * @return a new path node with the specified line in single
     *    precision floating point coordinates
     */
    public static final PPath createLine(final float x1, final float y1, final float x2, final float y2) {
        return new PPath.Float(new Line2D.Float(x1, y1, x2, y2));
    }

    /**
     * Create and return a new path node with a shape defined by the specified line segments in single
     * precision floating point coordinates.  Will be marked <code>final</code> in version 4.0.
     *
     * @since 3.0.1
     * @param xp array of x coordinates, must contain at least one x coordinate
     * @param yp array of y coordinates, must contain at least one y coordinate
     * @return a new path node with the a shape defined by the specified line segments in single
     *    precision floating point coordinates
     */
    public static PPath createPolyline(final float[] xp, final float[] yp) {
        if (xp.length < 1) {
            throw new IllegalArgumentException("xp must contain at least one x coordinate");
        }
        if (yp.length < 1) {
            throw new IllegalArgumentException("yp must contain at least one x coordinate");
        }
        if (xp.length != yp.length) {
            throw new IllegalArgumentException("xp and yp must contain the same number of coordinates");
        }
        Path2D.Float path = new Path2D.Float();
        path.moveTo(xp[0], yp[0]);
        for (int i = 1; i < xp.length; i++) {
            path.lineTo(xp[i], yp[i]);
        }
        path.closePath();
        return new PPath.Float(path);
    }

    /**
     * Create and return a new path node with a shape defined by the specified line segments in single
     * precision floating point coordinates.  Will be marked <code>final</code> in version 4.0.
     *
     * @since 3.0.1
     * @param points array of points, must not be null and must contain at least one point
     * @return a new path node with the a shape defined by the specified line segments in single
     *    precision floating point coordinates
     */
    public static PPath createPolyline(final Point2D.Float[] points) {
        if (points == null) {
            throw new NullPointerException("points must not be null");
        }
        if (points.length < 1) {
            throw new IllegalArgumentException("points must contain at least one point");
        }
        Path2D.Float path = new Path2D.Float();
        path.moveTo(points[0].getX(), points[0].getY());
        for (int i = 1; i < points.length; i++) {
            path.lineTo(points[i].getX(), points[i].getY());
        }
        path.closePath();
        return new PPath.Float(path);
    }

    /**
     * Create and return a new path node with the specified quadratic curve in single
     * precision floating point coordinates.
     *
     * @param x1 x coordinate of the start point
     * @param y1 y coordinate of the start point
     * @param ctrlx x coordinate of the control point
     * @param ctrly y coordinate of the control point
     * @param x2 x coordinate of the end point
     * @param y2 y coordinate of the end point
     * @return a new path node with the specified quadratic curve in single
     *    precision floating point coordinates
     */
    public static final PPath createQuadCurve(final float x1,
                                              final float y1,
                                              final float ctrlx,
                                              final float ctrly,
                                              final float x2,
                                              final float y2) {
        return new PPath.Float(new QuadCurve2D.Float(x1, y1, ctrlx, ctrly, x2, y2));
    }

    /**
     * Create and return a new path node with the specified rectangle in single
     * precision floating point coordinates.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param width width
     * @param height height
     * @return a new path node with the specified rectangle in single
     *    precision floating point coordinates
     */
    public static final PPath createRectangle(final float x, final float y, final float width, final float height) {
        return new PPath.Float(new Rectangle2D.Float(x, y, width, height));
    }

    /**
     * Create and return a new path node with the specified round rectangle in single
     * precision floating point coordinates.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param width width
     * @param height height
     * @param arcWidth width of the arc that rounds off the corners
     * @param arcHeight height of the arc that rounds off the corners
     * @return a new path node with the specified round rectangle in single
     *    precision floating point coordinates
     */
    public static final PPath createRoundRectangle(final float x,
                                                   final float y,
                                                   final float width,
                                                   final float height,
                                                   final float arcWidth,
                                                   final float arcHeight) {
        return new PPath.Float(new RoundRectangle2D.Float(x, y, width, height, arcWidth, arcHeight));
    }

    /**
     * Create and return a new path node with the specified arc in double
     * precision floating point coordinates.
     *
     * @param x x coordinate of the upper-left corner of the arc's framing rectangle
     * @param y y coordinate of the upper-left corner of the arc's framing rectangle
     * @param width width of the full ellipse of which this arc is a partial section
     * @param height height of the full ellipse of which this arc is a partial section
     * @param start starting angle of the arc in degrees
     * @param extent angular extent of the arc in degrees
     * @param type closure type for the arc, one of {@link Arc2D#OPEN}, {@link Arc2D#CHORD},
     *    or {@link Arc2D#PIE}
     * @return a new path node with the specified arc in double
     *    precision floating point coordinates
     */
    public static final PPath createArc(final double x,
                                        final double y,
                                        final double width,
                                        final double height,
                                        final double start,
                                        final double extent,
                                        final int type) {
        return new PPath.Double(new Arc2D.Double(x, y, width, height, start, extent, type));
    }

    /**
     * Create and return a new path node with the specified cubic curve in double
     * precision floating point coordinates.
     *
     * @param x1 x coordinate of the start point
     * @param y1 y coordinate of the start point
     * @param ctrlx1 x coordinate of the first control point
     * @param ctrly1 y coordinate of the first control point
     * @param ctrlx2 x coordinate of the second control point
     * @param ctrly2 y coordinate of the second control point
     * @param x2 x coordinate of the end point
     * @param y2 y coordinate of the end point
     * @return a new path node with the specified cubic curve in double
     *    precision floating point coordinates
     */
    public static final PPath createCubicCurve(final double x1,
                                               final double y1,
                                               final double ctrlx1,
                                               final double ctrly1,
                                               final double ctrlx2,
                                               final double ctrly2,
                                               final double x2,
                                               final double y2) {
        return new PPath.Double(new CubicCurve2D.Double(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2));
    }

    /**
     * Create and return a new path node with the specified ellipse in double
     * precision floating point coordinates.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param width width
     * @param height height
     * @return a new path node with the specified ellipse in double
     *    precision floating point coordinates
     */
    public static final PPath createEllipse(final double x, final double y, final double width, final double height) {
        return new PPath.Double(new Ellipse2D.Double(x, y, width, height));
    }

    /**
     * Create and return a new path node with the specified line in double
     * precision floating point coordinates.
     *
     * @param x1 x coordinate of the start point
     * @param y1 y coordinate of the start point
     * @param x2 x coordinate of the end point
     * @param y2 y coordinate of the end point
     * @return a new path node with the specified line in double
     *    precision floating point coordinates
     */
    public static final PPath createLine(final double x1, final double y1, final double x2, final double y2) {
        return new PPath.Double(new Line2D.Double(x1, y1, x2, y2));
    }

    /**
     * Create and return a new path node with the specified quadratic curve in double
     * precision floating point coordinates.
     *
     * @param x1 x coordinate of the start point
     * @param y1 y coordinate of the start point
     * @param ctrlx x coordinate of the control point
     * @param ctrly y coordinate of the control point
     * @param x2 x coordinate of the end point
     * @param y2 y coordinate of the end point
     * @return a new path node with the specified quadratic curve in double
     *    precision floating point coordinates
     */
    public static final PPath createQuadCurve(final double x1,
                                              final double y1,
                                              final double ctrlx,
                                              final double ctrly,
                                              final double x2,
                                              final double y2) {
        return new PPath.Double(new QuadCurve2D.Double(x1, y1, ctrlx, ctrly, x2, y2));
    }

    /**
     * Create and return a new path node with the specified rectangle in double
     * precision floating point coordinates.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param width width
     * @param height height
     * @return a new path node with the specified rectangle in double
     *    precision floating point coordinates
     */
    public static final PPath createRectangle(final double x, final double y, final double width, final double height) {
        return new PPath.Double(new Rectangle2D.Double(x, y, width, height));
    }

    /**
     * Create and return a new path node with the specified round rectangle in double
     * precision floating point coordinates.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param width width
     * @param height height
     * @param arcWidth width of the arc that rounds off the corners
     * @param arcHeight height of the arc that rounds off the corners
     * @return a new path node with the specified round rectangle in double
     *    precision floating point coordinates
     */
    public static final PPath createRoundRectangle(final double x,
                                                   final double y,
                                                   final double width,
                                                   final double height,
                                                   final double arcWidth,
                                                   final double arcHeight) {
        return new PPath.Double(new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight));
    }


    /**
     * Return a copy of the path backing this path node.
     *
     * @return a copy of the path backing this path node
     */
    public final Path2D getPath() {
        return (Path2D) path.clone();
    }

    /**
     * Return the path backing this node.  The returned path must not be
     * modified or the bounds of this node may no longer be valid and any
     * <code>path</code> property change listeners will not be notified.
     *
     * @return the path backing this path node
     */
    public final Path2D getPathReference() {
        return path;
    }

    /**
     * Append the geometry of the specified shape to this path node, possibly
     * connecting the new geometry to the existing path segments with a line
     * segment. If the connect parameter is true and the path is not empty then
     * any initial <code>moveTo</code> in the geometry of the appended shape is turned into
     * a <code>lineTo</code> segment. If the destination coordinates of such a connecting
     * <code>lineTo</code> segment match the ending coordinates of a currently open subpath
     * then the segment is omitted as superfluous. The winding rule of the specified
     * shape is ignored and the appended geometry is governed by the winding
     * rule specified for this path node.
     *
     * @param shape shape to append to this path node
     * @param connect true to turn an initial <code>moveTo</code> segment into a
     *    <code>lineTo</code> segment to connect the new geometry to the existing path
     */
    public final void append(final Shape shape, final boolean connect) {
        Path2D oldPath = (Path2D) path.clone();
        path.append(shape, connect);
        updateBoundsFromShape();
        firePropertyChange(-1, "path", oldPath, getPath());
    }

    /**
     * Append the geometry of the specified path iterator to this path node, possibly
     * connecting the new geometry to the existing path segments with a line segment.
     * If the connect parameter is true and the path is not empty then any initial <code>moveTo</code>
     * in the geometry of the appended path iterator is turned into a <code>lineTo</code> segment.
     * If the destination coordinates of such a connecting <code>lineTo</code> segment match
     * the ending coordinates of a currently open subpath then the segment is omitted
     * as superfluous.
     *
     * @param pathIterator path iterator to append to this path node
     * @param connect true to turn an initial <code>moveTo</code> segment into a
     *    <code>lineTo</code> segment to connect the new geometry to the existing path
     */
    public final void append(final PathIterator pathIterator, final boolean connect) {
        Path2D oldPath = (Path2D) path.clone();
        path.append(pathIterator, connect);
        updateBoundsFromShape();
        firePropertyChange(-1, "path", oldPath, getPath());
    }

    /**
     * Add a curved segment, defined by three new points, to this path node by drawing
     * a B&eacute;zier curve that intersects both the current coordinates and the specified
     * coordinates <code>(x3,y3)</code>, using the specified points <code>(x1,y1)</code>
     * and <code>(x2,y2)</code> as B&eacute;zier control points. All coordinates are specified in
     * double precision. 
     *
     * @param x1 x coordinate of the first B&eacute;zier control point
     * @param y1 y coordinate of the first B&eacute;zier control point
     * @param x2 x coordinate of the second B&eacute;zier control point
     * @param y2 y coordinate of the second B&eacute;zier control point
     * @param x3 x coordinate of the final end point
     * @param y3 y coordinate of the final end point
     */
    public final void curveTo(final double x1,
                              final double y1,
                              final double x2,
                              final double y2,
                              final double x3,
                              final double y3) {
        Path2D oldPath = (Path2D) path.clone();
        path.curveTo(x1, y1, x2, y2, x3, y3);
        updateBoundsFromShape();
        firePropertyChange(-1, "path", oldPath, getPath());
    }

    /**
     * Add a point to this path node by drawing a straight line from the
     * current coordinates to the new specified coordinates specified in double precision.
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public final void lineTo(final double x, final double y) {
        Path2D oldPath = (Path2D) path.clone();
        path.lineTo(x, y);
        updateBoundsFromShape();
        firePropertyChange(-1, "path", oldPath, getPath());
    }

    /**
     * Add a point to this path node by moving to the specified coordinates
     * specified in double precision.
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public final void moveTo(final double x, final double y) {
        Path2D oldPath = (Path2D) path.clone();
        path.moveTo(x, y);
        updateBoundsFromShape();
        firePropertyChange(-1, "path", oldPath, getPath());
    }

    /**
     * Add a curved segment, defined by two new points, to this path node by
     * drawing a Quadratic curve that intersects both the current coordinates and
     * the specified coordinates <code>(x2,y2)</code>, using the specified point
     * <code>(x1,y1)</code> as a quadratic parametric control point.  All coordinates
     * are specified in double precision.
     *
     * @param x1 x coordinate of the quadratic control point
     * @param y1 y coordinate of the quadratic control point
     * @param x2 x coordinate of the final end point
     * @param y2 y coordinate of the final end point
     */
    public final void quadTo(final double x1, final double y1, final double x2, final double y2) {
        Path2D oldPath = (Path2D) path.clone();
        path.quadTo(x1, y1, x2, y2);
        updateBoundsFromShape();
        firePropertyChange(-1, "path", oldPath, getPath());
    }

    /**
     * Reset the geometry for this path node to empty.
     */
    public final void reset() {
        Path2D oldPath = (Path2D) path.clone();
        path.reset();
        updateBoundsFromShape();
        firePropertyChange(-1, "path", oldPath, getPath());
    }

    /**
     * Close the current subpath by drawing a straight line back to the coordinates
     * of the last <code>moveTo</code>. If the path is already closed then this method
     * has no effect. 
     */
    public final void closePath() {
        Path2D oldPath = (Path2D) path.clone();
        path.closePath();
        updateBoundsFromShape();
        firePropertyChange(-1, "path", oldPath, getPath());
    }

    // todo:  setPathTo...

    /** {@inheritDoc} */
    protected final Shape getShape() {
        return path;
    }

    /** {@inheritDoc} */
    protected final void transform(final AffineTransform transform) {
        path.transform(transform);
    }
}
