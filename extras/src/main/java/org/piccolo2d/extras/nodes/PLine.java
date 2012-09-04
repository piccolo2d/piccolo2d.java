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
package org.piccolo2d.extras.nodes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.piccolo2d.PNode;
import org.piccolo2d.extras.util.LineShape;
import org.piccolo2d.nodes.PPath;
import org.piccolo2d.util.PAffineTransform;
import org.piccolo2d.util.PPaintContext;
import org.piccolo2d.util.PUtil;


/**
 * <b>PLine</b> a class for drawing multisegment lines.
 * 
 * @author Hallvard Traetteberg.
 */
public class PLine extends PNode {

    private static final long serialVersionUID = 1L;
    private static final PAffineTransform TEMP_TRANSFORM = new PAffineTransform();
    private static final BasicStroke DEFAULT_STROKE = new BasicStroke(1.0f);
    private static final Color DEFAULT_STROKE_PAINT = Color.black;
    private static final String PROPERTY_STROKE_PAINT = "strokePaint";
    private static final int PROPERTY_CODE_STROKE_PAINT = 1 << 16;
    private static final String PROPERTY_STROKE = "stroke";
    private static final int PROPERTY_CODE_STROKE = 1 << 17;
    private static final String PROPERTY_PATH = "path";
    private static final int PROPERTY_CODE_PATH = 1 << 18;

    private final transient LineShape lineShape;
    private transient Stroke stroke;
    private Paint strokePaint;

    /**
     * Constructs a new PLine with an empty LineShape.
     */
    public PLine() {
        this(null);
    }

    /**
     * Constructs a PLine object for displaying the provided line.
     * 
     * @param lineShape will be displayed by this PLine
     */
    public PLine(final LineShape lineShape) {
        strokePaint = DEFAULT_STROKE_PAINT;
        stroke = DEFAULT_STROKE;

        if (lineShape == null) {
            this.lineShape = new LineShape(null);
        }
        else {
            this.lineShape = lineShape;
        }
    }

    /**
     * Constructs a PLine for the given lineShape and the given stroke.
     * 
     * @param line line to be wrapped by this PLine
     * @param aStroke stroke to use when drawling the line
     */
    public PLine(final LineShape line, final Stroke aStroke) {
        this(line);
        stroke = aStroke;
    }

    /**
     * Returns the paint to be used while drawing the line.
     * 
     * @return paint used when drawing the line
     */
    public Paint getStrokePaint() {
        return strokePaint;
    }

    /**
     * Changes the paint to be used while drawing the line.
     * 
     * @param newStrokePaint paint to use when drawing the line
     */
    public void setStrokePaint(final Paint newStrokePaint) {
        final Paint oldPaint = strokePaint;
        strokePaint = newStrokePaint;
        invalidatePaint();
        firePropertyChange(PROPERTY_CODE_STROKE_PAINT, PROPERTY_STROKE_PAINT, oldPaint, strokePaint);
    }

    /**
     * Returns the stroke that will be used when drawing the line.
     * 
     * @return stroke used to draw the line
     */
    public Stroke getStroke() {
        return stroke;
    }

    /**
     * Sets stroke to use when drawing the line.
     * 
     * @param newStroke stroke to use when drawing the line
     */
    public void setStroke(final Stroke newStroke) {
        final Stroke oldStroke = stroke;
        stroke = newStroke;
        updateBoundsFromLine();
        invalidatePaint();
        firePropertyChange(PROPERTY_CODE_STROKE, PROPERTY_STROKE, oldStroke, stroke);
    }

    /** {@inheritDoc} */
    public boolean setBounds(final double x, final double y, final double width, final double height) {
        if (lineShape == null || !super.setBounds(x, y, width, height)) {
            return false;
        }

        final Rectangle2D lineBounds = lineShape.getBounds2D();
        final Rectangle2D lineStrokeBounds = getLineBoundsWithStroke();
        final double strokeOutset = Math.max(lineStrokeBounds.getWidth() - lineBounds.getWidth(), lineStrokeBounds
                .getHeight()
                - lineBounds.getHeight());

        double adjustedX = x + strokeOutset / 2;
        double adjustedY = y + strokeOutset / 2;
        double adjustedWidth = width - strokeOutset;
        double adjustedHeight = height - strokeOutset;

        TEMP_TRANSFORM.setToIdentity();
        TEMP_TRANSFORM.translate(adjustedX, adjustedY);
        TEMP_TRANSFORM.scale(adjustedWidth / lineBounds.getWidth(), adjustedHeight / lineBounds.getHeight());
        TEMP_TRANSFORM.translate(-lineBounds.getX(), -lineBounds.getY());
        lineShape.transformPoints(TEMP_TRANSFORM);

        return true;
    }

    /** {@inheritDoc} */
    public boolean intersects(final Rectangle2D aBounds) {
        if (super.intersects(aBounds)) {
            if (lineShape.intersects(aBounds)) {
                return true;
            }
            else if (stroke != null && strokePaint != null) {
                return stroke.createStrokedShape(lineShape).intersects(aBounds);
            }
        }
        return false;
    }

    /**
     * Calculates the bounds of the line taking stroke width into account.
     * 
     * @return rectangle representing the bounds of the line taking stroke width
     *         into account
     */
    public Rectangle2D getLineBoundsWithStroke() {
        if (stroke != null) {
            return stroke.createStrokedShape(lineShape).getBounds2D();
        }
        else {
            return lineShape.getBounds2D();
        }
    }

    /**
     * Recalculates the bounds when a change to the underlying line occurs.
     */
    public void updateBoundsFromLine() {
        if (lineShape.getPointCount() == 0) {
            resetBounds();
        }
        else {
            final Rectangle2D b = getLineBoundsWithStroke();
            super.setBounds(b.getX(), b.getY(), b.getWidth(), b.getHeight());
        }
    }

    /**
     * Paints the PLine in the provided context if it has both a stroke and a
     * stroke paint assigned.
     * 
     * @param paintContext the context into which the line should be drawn
     */
    protected void paint(final PPaintContext paintContext) {
        final Graphics2D g2 = paintContext.getGraphics();

        if (stroke != null && strokePaint != null) {
            g2.setPaint(strokePaint);
            g2.setStroke(stroke);
            g2.draw(lineShape);
        }
    }

    /**
     * Returns a reference to the underlying line shape. Be careful!
     * 
     * @return direct reference to the underlying line shape
     */
    public LineShape getLineReference() {
        return lineShape;
    }

    /**
     * Returns the number of points in the line.
     * 
     * @return number of points in the line
     */
    public int getPointCount() {
        return lineShape.getPointCount();
    }

    /**
     * Returns the point at the provided index. If dst is not null, it will
     * populate it with the point's coordinates rather than create a new point.
     * 
     * @param pointIndex index of desired point in line
     * @param dst point to populate, may be null
     * @return the desired point, or dst populate with its coordinates
     */
    public Point2D getPoint(final int pointIndex, final Point2D dst) {
        final Point2D result;
        if (dst == null) {
            result = new Point2D.Double();
        } 
        else {
            result = dst;
        }
        return lineShape.getPoint(pointIndex, result);
    }

    /**
     * Fires appropriate change events, updates line bounds and flags the PLine
     * as requiring a repaint.
     */
    protected void lineChanged() {
        firePropertyChange(PROPERTY_CODE_PATH, PROPERTY_PATH, null, lineShape);
        updateBoundsFromLine();
        invalidatePaint();
    }

    /**
     * Changes the point at the provided index.
     * 
     * @param pointIndex index of point to change
     * @param x x component to assign to the point
     * @param y y component to assign to the point
     */
    public void setPoint(final int pointIndex, final double x, final double y) {
        lineShape.setPoint(pointIndex, x, y);
        lineChanged();
    }

    /**
     * Inserts a point at the provided index.
     * 
     * @param pointIndex index at which to add the point
     * @param x x component of new point
     * @param y y component of new point
     */
    public void addPoint(final int pointIndex, final double x, final double y) {
        lineShape.addPoint(pointIndex, x, y);
        lineChanged();
    }

    /**
     * Removes points from the line.
     * 
     * @param startIndex index from which to remove the points
     * @param numberOfPoints number of points to remove
     */
    public void removePoints(final int startIndex, final int numberOfPoints) {
        lineShape.removePoints(startIndex, numberOfPoints);
        lineChanged();
    }

    /**
     * Removes all points from the underlying line.
     */
    public void removeAllPoints() {
        lineShape.removePoints(0, lineShape.getPointCount());
        lineChanged();
    }

    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        PUtil.writeStroke(stroke, out);
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        stroke = PUtil.readStroke(in);
    }
}
