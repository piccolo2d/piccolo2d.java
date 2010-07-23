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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.piccolo2d.PNode;

import org.piccolo2d.util.PPaintContext;

/**
 * Abstract shape node.
 */
public abstract class PShape extends PNode {
    private transient Stroke stroke = DEFAULT_STROKE;
    private Paint strokePaint = DEFAULT_STROKE_PAINT;
    public static final Stroke DEFAULT_STROKE = new BasicStroke(1.0f);
    public static final Paint DEFAULT_STROKE_PAINT = Color.BLACK;

    protected abstract Shape getShape();
    protected abstract void transform(AffineTransform transform);

    public final Stroke getStroke() {
        return stroke;
    }

    public final void setStroke(final Stroke stroke) {
        Stroke oldStroke = this.stroke;
        this.stroke = stroke;
        updateBoundsFromShape();
        invalidatePaint();
        firePropertyChange(-1, "stroke", oldStroke, this.stroke);
    }

    public final Paint getStrokePaint() {
        return strokePaint;
    }

    public final void setStrokePaint(final Paint strokePaint) {
        Paint oldStrokePaint = this.strokePaint;
        this.strokePaint = strokePaint;
        invalidatePaint();
        firePropertyChange(-1, "strokePaint", oldStrokePaint, this.strokePaint);
    }

    protected final void updateBoundsFromShape() {
        if (getShape() == null) {
            resetBounds();
        }
        else {
            final Rectangle2D b = getBoundsWithStroke();
            setBounds(b.getX(), b.getY(), b.getWidth(), b.getHeight());
        }
    }

    protected final Rectangle2D getBoundsWithStroke() {
        if (stroke != null) {
            return stroke.createStrokedShape(getShape()).getBounds2D();
        }
        else {
            return getShape().getBounds2D();
        }
    }

    /** {@inheritDoc} */
    protected final void internalUpdateBounds(final double x, final double y, final double width, final double height) {
        final Rectangle2D bounds = getShape().getBounds2D();
        final Rectangle2D strokeBounds = getBoundsWithStroke();
        final double strokeOutset = Math.max(strokeBounds.getWidth() - bounds.getWidth(),
                                             strokeBounds.getHeight() - bounds.getHeight());

        double adjustedX = x + strokeOutset / 2;
        double adjustedY = y + strokeOutset / 2;
        double adjustedWidth = width - strokeOutset;
        double adjustedHeight = height - strokeOutset;

        final double scaleX;
        if (adjustedWidth == 0 || bounds.getWidth() == 0) {
            scaleX = 1;
        }
        else {
            scaleX = adjustedWidth / bounds.getWidth();
        }
        final double scaleY;
        if (adjustedHeight == 0 || bounds.getHeight() == 0) {
            scaleY = 1;
        }
        else {
            scaleY = adjustedHeight / bounds.getHeight();
        }

        // todo: cache instance as static?
        final AffineTransform transform = new AffineTransform();
        transform.translate(adjustedX, adjustedY);
        transform.scale(scaleX, scaleY);
        transform.translate(-bounds.getX(), -bounds.getY());

        transform(transform);
    }

    /** {@inheritDoc} */
    public final boolean intersects(final Rectangle2D bounds) {
        if (super.intersects(bounds)) {
            if (getPaint() != null && getShape().intersects(bounds)) {
                return true;
            }
            else if (stroke != null && strokePaint != null) {
                return stroke.createStrokedShape(getShape()).intersects(bounds);
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    protected final void paint(final PPaintContext paintContext) {
        final Paint p = getPaint();
        final Graphics2D g2 = paintContext.getGraphics();

        if (p != null) {
            g2.setPaint(p);
            g2.fill(getShape());
        }

        if (stroke != null && strokePaint != null) {
            g2.setPaint(strokePaint);
            g2.setStroke(stroke);
            g2.draw(getShape());
        }
    }
}