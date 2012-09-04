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
package org.piccolo2d.extras.swt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import org.piccolo2d.PNode;
import org.piccolo2d.nodes.PPath;
import org.piccolo2d.util.PAffineTransform;
import org.piccolo2d.util.PAffineTransformException;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PPaintContext;


/**
 * <b>PSWTPath</b> is a wrapper around a java.awt.geom.GeneralPath, with
 * workarounds for drawing shapes in SWT where necessary.
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PSWTPath extends PNode {
    private static final long serialVersionUID = 1L;

    /**
     * The property name that identifies a change of this node's path. In any
     * property change event the new value will be a reference to this node's
     * path, but old value will always be null.
     */
    public static final String PROPERTY_SHAPE = "shape";
    private static final String PROPERTY_PATH = "path";
    private static final int PROPERTY_CODE_PATH = 1 << 18;
    private static final String PROPERTY_STROKE_PAINT = "strokePaint";
    private static final int PROPERTY_CODE_STROKE_PAINT = 1 << 16;

    private static final double BOUNDS_TOLERANCE = 0.01;
    private static final Rectangle2D.Float TEMP_RECTANGLE = new Rectangle2D.Float();
    private static final RoundRectangle2D.Float TEMP_ROUNDRECTANGLE = new RoundRectangle2D.Float();
    private static final Ellipse2D.Float TEMP_ELLIPSE = new Ellipse2D.Float();
    private static final Color DEFAULT_STROKE_PAINT = Color.black;
    private static final BasicStroke BASIC_STROKE = new BasicStroke();
    private static final float PEN_WIDTH = 1f;
    private static final float DEFAULT_TRANSPARENCY = 1.0f;

    private Paint strokePaint;

    private boolean updatingBoundsFromPath;
    private Shape origShape;
    private Shape shape;

    private PAffineTransform internalXForm;
    private AffineTransform inverseXForm;

    private double[] shapePts;
    private float transparency = DEFAULT_TRANSPARENCY;

    /**
     * Creates a path representing the rectangle provided.
     * 
     * @param x left of rectangle
     * @param y top of rectangle
     * @param width width of rectangle
     * @param height height of rectangle
     * @return created rectangle
     */
    public static PSWTPath createRectangle(final float x, final float y, final float width, final float height) {
        TEMP_RECTANGLE.setFrame(x, y, width, height);
        final PSWTPath result = new PSWTPath(TEMP_RECTANGLE);
        result.setPaint(Color.white);
        return result;
    }

    /**
     * Creates a path representing the rounded rectangle provided.
     * 
     * @param x left of rectangle
     * @param y top of rectangle
     * @param width width of rectangle
     * @param height height of rectangle
     * @param arcWidth width of the arc at the corners
     * @param arcHeight height of arc at the corners
     * @return created rounded rectangle
     */
    public static PSWTPath createRoundRectangle(final float x, final float y, final float width, final float height,
            final float arcWidth, final float arcHeight) {
        TEMP_ROUNDRECTANGLE.setRoundRect(x, y, width, height, arcWidth, arcHeight);
        final PSWTPath result = new PSWTPath(TEMP_ROUNDRECTANGLE);
        result.setPaint(Color.white);
        return result;
    }

    /**
     * Creates a path representing an ellipse that covers the rectangle
     * provided.
     * 
     * @param x left of rectangle
     * @param y top of rectangle
     * @param width width of rectangle
     * @param height height of rectangle
     * @return created ellipse
     */
    public static PSWTPath createEllipse(final float x, final float y, final float width, final float height) {
        TEMP_ELLIPSE.setFrame(x, y, width, height);
        final PSWTPath result = new PSWTPath(TEMP_ELLIPSE);
        result.setPaint(Color.white);
        return result;
    }

    /**
     * Creates a PPath for the poly-line for the given points.
     * 
     * @param points array of points for the point lines
     * 
     * @return created poly-line for the given points
     */
    public static PSWTPath createPolyline(final Point2D[] points) {
        final PSWTPath result = new PSWTPath();
        result.setPathToPolyline(points);
        result.setPaint(Color.white);
        return result;
    }

    /**
     * Creates a PPath for the poly-line for the given points.
     * 
     * @param xp array of x components of the points of the poly-lines
     * @param yp array of y components of the points of the poly-lines
     * 
     * @return created poly-line for the given points
     */
    public static PSWTPath createPolyline(final float[] xp, final float[] yp) {
        final PSWTPath result = new PSWTPath();
        result.setPathToPolyline(xp, yp);
        result.setPaint(Color.white);
        return result;
    }

    /**
     * Creates an empty PSWTPath.
     */
    public PSWTPath() {
        strokePaint = DEFAULT_STROKE_PAINT;
    }

    /**
     * Creates an SWTPath in the given shape with the default paint and stroke.
     * 
     * @param aShape the desired shape
     */
    public PSWTPath(final Shape aShape) {
        this();
        setShape(aShape);
    }

    // ****************************************************************
    // Stroke
    // ****************************************************************
    /**
     * Returns the paint to use when drawing the stroke of the shape.
     * 
     * @return path's stroke paint
     */
    public Paint getStrokePaint() {
        return strokePaint;
    }

    /**
     * Sets the paint to use when drawing the stroke of the shape.
     * 
     * @param strokeColor new stroke color
     */
    public void setStrokeColor(final Paint strokeColor) {
        final Paint old = strokePaint;
        strokePaint = strokeColor;
        invalidatePaint();
        firePropertyChange(PROPERTY_CODE_STROKE_PAINT, PROPERTY_STROKE_PAINT, old, strokePaint);
    }

    /**
     * Set the bounds of this path. This method works by scaling the path to fit
     * into the specified bounds. This normally works well, but if the specified
     * base bounds get too small then it is impossible to expand the path shape
     * again since all its numbers have tended to zero, so application code may
     * need to take this into consideration.
     * 
     * @param x new left position of bounds
     * @param y new top position of bounds
     * @param width the new width of the bounds
     * @param height the new height of the bounds
     */
    protected void internalUpdateBounds(final double x, final double y, final double width, final double height) {
        if (updatingBoundsFromPath) {
            return;
        }
        if (origShape == null) {
            return;
        }

        final Rectangle2D pathBounds = origShape.getBounds2D();

        if (Math.abs(x - pathBounds.getX()) / x < BOUNDS_TOLERANCE
                && Math.abs(y - pathBounds.getY()) / y < BOUNDS_TOLERANCE
                && Math.abs(width - pathBounds.getWidth()) / width < BOUNDS_TOLERANCE
                && Math.abs(height - pathBounds.getHeight()) / height < BOUNDS_TOLERANCE) {
            return;
        }

        if (internalXForm == null) {
            internalXForm = new PAffineTransform();
        }
        internalXForm.setToIdentity();
        internalXForm.translate(x, y);
        internalXForm.scale(width / pathBounds.getWidth(), height / pathBounds.getHeight());
        internalXForm.translate(-pathBounds.getX(), -pathBounds.getY());

        try {
            inverseXForm = internalXForm.createInverse();
        }
        catch (final Exception e) {
            throw new PAffineTransformException("unable to invert transform", internalXForm);
        }
    }

    /**
     * Returns true if path crosses the provided bounds. Takes visibility of
     * path into account.
     * 
     * @param aBounds bounds being tested for intersection
     * @return true if path visibly crosses bounds
     */
    public boolean intersects(final Rectangle2D aBounds) {
        if (super.intersects(aBounds)) {
            final Rectangle2D srcBounds;
            if (internalXForm == null) {
                srcBounds = aBounds;
            }
            else {
                srcBounds = new PBounds(aBounds);
                internalXForm.inverseTransform(srcBounds, srcBounds);
            }

            if (getPaint() != null && shape.intersects(srcBounds)) {
                return true;
            }
            else if (strokePaint != null) {
                return BASIC_STROKE.createStrokedShape(shape).intersects(srcBounds);
            }
        }
        return false;
    }

    /**
     * Recalculates the path's bounds by examining it's associated shape.
     */
    public void updateBoundsFromPath() {
        updatingBoundsFromPath = true;

        if (origShape == null) {
            resetBounds();
        }
        else {
            final Rectangle2D b = origShape.getBounds2D();

            // Note that this pen width code does not really work for SWT since
            // it assumes
            // that the pen width scales - in actuality it does not. However,
            // the fix would
            // be to have volatile bounds for all shapes which isn't a nice
            // alternative
            super.setBounds(b.getX() - PEN_WIDTH, b.getY() - PEN_WIDTH, b.getWidth() + 2 * PEN_WIDTH, b.getHeight() + 2
                    * PEN_WIDTH);
        }
        updatingBoundsFromPath = false;
    }

    // ****************************************************************
    // Painting
    // ****************************************************************
    /**
     * Paints the path on the context provided.
     * 
     * @param paintContext the context onto which the path will be painted
     */
    protected void paint(final PPaintContext paintContext) {
        final Paint p = getPaint();
        final SWTGraphics2D g2 = (SWTGraphics2D) paintContext.getGraphics();
        g2.setTransparency(transparency);

        if (internalXForm != null) {
            g2.transform(internalXForm);
        }

        if (p != null) {
            g2.setBackground((Color) p);
            fillShape(g2);
        }

        if (strokePaint != null) {
            g2.setColor((Color) strokePaint);
            drawShape(g2);
        }

        if (inverseXForm != null) {
            g2.transform(inverseXForm);
        }
    }

    private void drawShape(final SWTGraphics2D g2) {
        final double lineWidth = g2.getTransformedLineWidth();
        if (shape instanceof Rectangle2D) {
            g2.drawRect(shapePts[0] + lineWidth / 2, shapePts[1] + lineWidth / 2, shapePts[2] - lineWidth, shapePts[3]
                    - lineWidth);
        }
        else if (shape instanceof Ellipse2D) {
            g2.drawOval(shapePts[0] + lineWidth / 2, shapePts[1] + lineWidth / 2, shapePts[2] - lineWidth, shapePts[3]
                    - lineWidth);
        }
        else if (shape instanceof Arc2D) {
            g2.drawArc(shapePts[0] + lineWidth / 2, shapePts[1] + lineWidth / 2, shapePts[2] - lineWidth, shapePts[3]
                    - lineWidth, shapePts[4], shapePts[5]);
        }
        else if (shape instanceof RoundRectangle2D) {
            g2.drawRoundRect(shapePts[0] + lineWidth / 2, shapePts[1] + lineWidth / 2, shapePts[2] - lineWidth,
                    shapePts[3] - lineWidth, shapePts[4], shapePts[5]);
        }
        else {
            g2.draw(shape);
        }
    }

    private void fillShape(final SWTGraphics2D g2) {
        final double lineWidth = g2.getTransformedLineWidth();
        if (shape instanceof Rectangle2D) {
            g2.fillRect(shapePts[0] + lineWidth / 2, shapePts[1] + lineWidth / 2, shapePts[2] - lineWidth, shapePts[3]
                    - lineWidth);
        }
        else if (shape instanceof Ellipse2D) {
            g2.fillOval(shapePts[0] + lineWidth / 2, shapePts[1] + lineWidth / 2, shapePts[2] - lineWidth, shapePts[3]
                    - lineWidth);
        }
        else if (shape instanceof Arc2D) {
            g2.fillArc(shapePts[0] + lineWidth / 2, shapePts[1] + lineWidth / 2, shapePts[2] - lineWidth, shapePts[3]
                    - lineWidth, shapePts[4], shapePts[5]);
        }
        else if (shape instanceof RoundRectangle2D) {
            g2.fillRoundRect(shapePts[0] + lineWidth / 2, shapePts[1] + lineWidth / 2, shapePts[2] - lineWidth,
                    shapePts[3] - lineWidth, shapePts[4], shapePts[5]);
        }
        else {
            g2.fill(shape);
        }
    }

    /**
     * Changes the underlying shape of this PSWTPath.
     * 
     * @param newShape new associated shape of this PSWTPath
     */
    public void setShape(final Shape newShape) {
        shape = cloneShape(newShape);
        origShape = shape;
        updateShapePoints(newShape);

        firePropertyChange(PROPERTY_CODE_PATH, PROPERTY_PATH, null, shape);
        updateBoundsFromPath();
        invalidatePaint();
    }

    /**
     * Updates the internal points used to draw the shape.
     * 
     * @param aShape shape to read points from
     */
    public void updateShapePoints(final Shape aShape) {
        if (aShape instanceof Rectangle2D) {
            if (shapePts == null || shapePts.length < 4) {
                shapePts = new double[4];
            }

            shapePts[0] = ((Rectangle2D) shape).getX();
            shapePts[1] = ((Rectangle2D) shape).getY();
            shapePts[2] = ((Rectangle2D) shape).getWidth();
            shapePts[3] = ((Rectangle2D) shape).getHeight();
        }
        else if (aShape instanceof Ellipse2D) {
            if (shapePts == null || shapePts.length < 4) {
                shapePts = new double[4];
            }

            shapePts[0] = ((Ellipse2D) shape).getX();
            shapePts[1] = ((Ellipse2D) shape).getY();
            shapePts[2] = ((Ellipse2D) shape).getWidth();
            shapePts[3] = ((Ellipse2D) shape).getHeight();
        }
        else if (aShape instanceof Arc2D) {
            if (shapePts == null || shapePts.length < 6) {
                shapePts = new double[6];
            }

            shapePts[0] = ((Arc2D) shape).getX();
            shapePts[1] = ((Arc2D) shape).getY();
            shapePts[2] = ((Arc2D) shape).getWidth();
            shapePts[3] = ((Arc2D) shape).getHeight();
            shapePts[4] = ((Arc2D) shape).getAngleStart();
            shapePts[5] = ((Arc2D) shape).getAngleExtent();
        }
        else if (aShape instanceof RoundRectangle2D) {
            if (shapePts == null || shapePts.length < 6) {
                shapePts = new double[6];
            }

            shapePts[0] = ((RoundRectangle2D) shape).getX();
            shapePts[1] = ((RoundRectangle2D) shape).getY();
            shapePts[2] = ((RoundRectangle2D) shape).getWidth();
            shapePts[3] = ((RoundRectangle2D) shape).getHeight();
            shapePts[4] = ((RoundRectangle2D) shape).getArcWidth();
            shapePts[5] = ((RoundRectangle2D) shape).getArcHeight();
        }
        else {
            shapePts = SWTShapeManager.shapeToPolyline(shape);
        }
    }

    /**
     * Clone's the shape provided.
     * 
     * @param aShape shape to be cloned
     * 
     * @return a cloned version of the provided shape
     */
    public Shape cloneShape(final Shape aShape) {
        if (aShape instanceof Rectangle2D) {
            return new PBounds((Rectangle2D) aShape);
        }
        else if (aShape instanceof Ellipse2D) {
            final Ellipse2D e2 = (Ellipse2D) aShape;
            return new Ellipse2D.Double(e2.getX(), e2.getY(), e2.getWidth(), e2.getHeight());
        }
        else if (aShape instanceof Arc2D) {
            final Arc2D a2 = (Arc2D) aShape;
            return new Arc2D.Double(a2.getX(), a2.getY(), a2.getWidth(), a2.getHeight(), a2.getAngleStart(), a2
                    .getAngleExtent(), a2.getArcType());
        }
        else if (aShape instanceof RoundRectangle2D) {
            final RoundRectangle2D r2 = (RoundRectangle2D) aShape;
            return new RoundRectangle2D.Double(r2.getX(), r2.getY(), r2.getWidth(), r2.getHeight(), r2.getArcWidth(),
                    r2.getArcHeight());
        }
        else if (aShape instanceof Line2D) {
            final Line2D l2 = (Line2D) aShape;
            return new Line2D.Double(l2.getP1(), l2.getP2());
        }
        else {
            final GeneralPath aPath = new GeneralPath();
            aPath.append(aShape, false);
            return aPath;
        }
    }

    /**
     * Resets the path to a rectangle with the dimensions and position provided.
     * 
     * @param x left of the rectangle
     * @param y top of te rectangle
     * @param width width of the rectangle
     * @param height height of the rectangle
     */
    public void setPathToRectangle(final float x, final float y, final float width, final float height) {
        TEMP_RECTANGLE.setFrame(x, y, width, height);
        setShape(TEMP_RECTANGLE);
    }

    /**
     * Resets the path to a rectangle with the dimensions and position provided.
     * 
     * @param x left of the rectangle
     * @param y top of te rectangle
     * @param width width of the rectangle
     * @param height height of the rectangle
     * @param arcWidth width of arc in the corners of the rectangle
     * @param arcHeight height of arc in the corners of the rectangle
     */
    public void setPathToRoundRectangle(final float x, final float y, final float width, final float height,
            final float arcWidth, final float arcHeight) {
        TEMP_ROUNDRECTANGLE.setRoundRect(x, y, width, height, arcWidth, arcHeight);
        setShape(TEMP_ROUNDRECTANGLE);
    }

    /**
     * Resets the path to an ellipse positioned at the coordinate provided with
     * the dimensions provided.
     * 
     * @param x left of the ellipse
     * @param y top of the ellipse
     * @param width width of the ellipse
     * @param height height of the ellipse
     */
    public void setPathToEllipse(final float x, final float y, final float width, final float height) {
        TEMP_ELLIPSE.setFrame(x, y, width, height);
        setShape(TEMP_ELLIPSE);
    }

    /**
     * Sets the path to a sequence of segments described by the points.
     * 
     * @param points points to that lie along the generated path
     */
    public void setPathToPolyline(final Point2D[] points) {
        final GeneralPath path = new GeneralPath();
        path.reset();
        path.moveTo((float) points[0].getX(), (float) points[0].getY());
        for (int i = 1; i < points.length; i++) {
            path.lineTo((float) points[i].getX(), (float) points[i].getY());
        }
        setShape(path);
    }

    /**
     * Sets the path to a sequence of segments described by the point components
     * provided.
     * 
     * @param xp the x components of the points along the path
     * @param yp the y components of the points along the path
     */
    public void setPathToPolyline(final float[] xp, final float[] yp) {
        final GeneralPath path = new GeneralPath();
        path.reset();
        path.moveTo(xp[0], yp[0]);
        for (int i = 1; i < xp.length; i++) {
            path.lineTo(xp[i], yp[i]);
        }
        setShape(path);
    }

    /**
     * Return the center of this SWT path node, based on its bounds.
     *
     * @return the center of this SWT path node, based on its bounds
     */
    public Point2D getCenter() {                                                                                                              
        PBounds bounds = getBoundsReference();                                                                                                        
        return new Point2D.Double(bounds.x + (bounds.width / 2.0), bounds.y + (bounds.height / 2.0));
    } 

    /**
     * Return the transparency for this SWT path node.
     *
     * @return the transparency for this SWT path node
     */
    public float getTransparency() {
        return transparency;
    }

    /**
     * Set the transparency for this SWT path node to <code>transparency</code>.
     *
     * @param transparency transparency, must be between <code>0.0f</code> and <code>1.0f</code> inclusive
     */
    public void setTransparency(final float transparency) {
        if ((transparency < 0.0f) || (transparency > 1.0f)) {
            throw new IllegalArgumentException("transparency must be between 0.0f and 1.0f inclusive");
        }
        this.transparency = transparency;
    }
}