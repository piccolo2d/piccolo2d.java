/*
 * Copyright (c) 2008-2010, Piccolo2D project, http://piccolo2d.org
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

import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.PathIterator;

/**
 * Path node.
 */
public abstract class PPath extends PShape
{
    private Path2D path;

    // todo: ctr with stroke?

    PPath(final Path2D path) {
        if (path == null) {
            throw new IllegalArgumentException("path must not be null");
        }
        this.path = (Path2D) path.clone();
        // todo:
        // not sure why this call is required for this class, it is not present in original PPath
        updateBoundsFromShape();
    }


    public static class Float extends PPath {
        public Float() {
            super(new Path2D.Float());
        }
        public Float(final Shape shape) {
            super(new Path2D.Float(shape));
        }
        public Float(final Path2D.Float path) {
            super(path);
        }
    }

    public static class Double extends PPath {
        public Double() {
            super(new Path2D.Double());
        }
        public Double(final Shape shape) {
            super(new Path2D.Double(shape));
        }
        public Double(final Path2D.Double path) {
            super(path);
        }
    }


    public static PPath createCubicCurve(final float x1, final float y1, final float ctrlx1, final float ctrly1, final float ctrlx2, final float ctrly2, final float x2, final float y2) {
        return new PPath.Float(new CubicCurve2D.Float(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2));
    }

    public static PPath createEllipse(final float x, final float y, final float width, final float height) {
        return new PPath.Float(new Ellipse2D.Float(x, y, width, height));
    }

    public static PPath createLine(final float x1, final float y1, final float x2, final float y2) {
        return new PPath.Float(new Line2D.Float(x1, y1, x2, y2));
    }

    /*
      need setPathToPolyline
    public static PPath createPolyline(final float[] xp, final float[] yp) {
    }

    public static PPath createPolyline(final Point2D.Float[] points) {
    }
    */

    public static PPath createQuadCurve(final float x1, final float y1, final float ctrlx, final float ctrly, final float x2, final float y2) {
        return new PPath.Float(new QuadCurve2D.Float(x1, y1, ctrlx, ctrly, x2, y2));
    }

    public static PPath createRectangle(final float x, final float y, final float width, final float height) {
        return new PPath.Float(new Rectangle2D.Float(x, y, width, height));
    }

    public static PPath createRoundRectangle(final float x, final float y, final float width, final float height, final float arcWidth, final float arcHeight) {
        return new PPath.Float(new RoundRectangle2D.Float(x, y, width, height, arcWidth, arcHeight));
    }


    public static PPath createCubicCurve(final double x1, final double y1, final double ctrlx1, final double ctrly1, final double ctrlx2, final double ctrly2, final double x2, final double y2) {
        return new PPath.Double(new CubicCurve2D.Double(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2));
    }

    public static PPath createEllipse(final double x, final double y, final double width, final double height) {
        return new PPath.Double(new Ellipse2D.Double(x, y, width, height));
    }

    public static PPath createLine(final double x1, final double y1, final double x2, final double y2) {
        return new PPath.Double(new Line2D.Double(x1, y1, x2, y2));
    }

    /*
    public static PPath createPolyline(final double[] xp, final double[] yp) {
    }

    public static PPath createPolyline(final Point2D.Double[] points) {
    }
    */

    public static PPath createQuadCurve(final double x1, final double y1, final double ctrlx, final double ctrly, final double x2, final double y2) {
        return new PPath.Double(new QuadCurve2D.Double(x1, y1, ctrlx, ctrly, x2, y2));
    }

    public static PPath createRectangle(final double x, final double y, final double width, final double height) {
        return new PPath.Double(new Rectangle2D.Double(x, y, width, height));
    }

    public static PPath createRoundRectangle(final double x, final double y, final double width, final double height, final double arcWidth, final double arcHeight) {
        return new PPath.Double(new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight));
    }


    public void append(final Shape shape, final boolean connect) {
        path.append(shape, connect);
        updateBoundsFromShape();
    }

    public void append(final PathIterator pathIterator, final boolean connect) {
        path.append(pathIterator, connect);
        updateBoundsFromShape();
    }

    public void curveTo(final double x1, final double y1, final double x2, final double y2, final double x3, final double y3) {
        path.curveTo(x1, y1, x2, y2, x3, y3);
        updateBoundsFromShape();
    }

    public void lineTo(final double x, final double y) {
        path.lineTo(x, y);
        updateBoundsFromShape();
    }

    public void moveTo(final double x, final double y) {
        path.moveTo(x, y);
        updateBoundsFromShape();
    }

    public void quadTo(final double x1, final double y1, final double x2, final double y2) {
        path.quadTo(x1, y1, x2, y2);
        updateBoundsFromShape();
    }

    public void closePath() {
        path.closePath();
        updateBoundsFromShape();
    }

    // todo:  setPathTo...
    //    path property change events

    /** {@inheritDoc} */
    protected Shape getShape() {
        return path;
    }

    /** {@inheritDoc} */
    protected void transform(final AffineTransform transform) {
        path.transform(transform);
    }
}