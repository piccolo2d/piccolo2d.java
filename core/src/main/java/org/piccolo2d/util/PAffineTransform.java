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

import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * <b>PAffineTransform</b> is a subclass of AffineTransform that has been
 * extended with convenience methods.
 * <P>
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PAffineTransform extends AffineTransform {
    /**
     * Allows for future serialization code to understand versioned binary
     * formats.
     */
    private static final long serialVersionUID = 1L;

    /** Used internally to speed up computation. */
    private static final double[] PTS1 = new double[8];

    /** Used internally to speed up computation. */
    private static final double[] PTS2 = new double[8];

    /**
     * Constructs a new AffineTransform representing the Identity
     * transformation.
     */
    public PAffineTransform() {
        super();
    }

    /**
     * Constructs a new AffineTransform from an array of double precision values
     * representing either the 4 non-translation entries or the 6 specifiable
     * entries of the 3x3 transformation matrix. The values are retrieved from
     * the array as { m00 m10 m01 m11 [m02 m12]}.
     * 
     * @param flatmatrix the double array containing the values to be set in the
     *            new AffineTransform object. The length of the array is assumed
     *            to be at least 4. If the length of the array is less than 6,
     *            only the first 4 values are taken. If the length of the array
     *            is greater than 6, the first 6 values are taken.
     */
    public PAffineTransform(final double[] flatmatrix) {
        super(flatmatrix);
    }

    /**
     * Constructs a new AffineTransform from an array of floating point values
     * representing either the 4 non-translation entries or the 6 specifiable
     * entries of the 3x3 transformation matrix. The values are retrieved from
     * the array as { m00 m10 m01 m11 [m02 m12]}.
     * 
     * @param flatmatrix the float array containing the values to be set in the
     *            new AffineTransform object. The length of the array is assumed
     *            to be at least 4. If the length of the array is less than 6,
     *            only the first 4 values are taken. If the length of the array
     *            is greater than 6, the first 6 values are taken.
     */
    public PAffineTransform(final float[] flatmatrix) {
        super(flatmatrix);
    }

    /**
     * Constructs a new AffineTransform from 6 double precision values
     * representing the 6 specifiable entries of the 3x3 transformation matrix.
     * 
     * @param m00 the X coordinate scaling element of the 3x3 matrix
     * @param m10 the Y coordinate shearing element of the 3x3 matrix
     * @param m01 the X coordinate shearing element of the 3x3 matrix
     * @param m11 the Y coordinate scaling element of the 3x3 matrix
     * @param m02 the X coordinate translation element of the 3x3 matrix
     * @param m12 the Y coordinate translation element of the 3x3 matrix
     */
    public PAffineTransform(final double m00, final double m10, final double m01, final double m11, final double m02,
            final double m12) {
        super(m00, m10, m01, m11, m02, m12);
    }

    /**
     * Constructs a new AffineTransform from 6 floating point values
     * representing the 6 specifiable entries of the 3x3 transformation matrix.
     * 
     * @param m00 the X coordinate scaling element of the 3x3 matrix
     * @param m10 the Y coordinate shearing element of the 3x3 matrix
     * @param m01 the X coordinate shearing element of the 3x3 matrix
     * @param m11 the Y coordinate scaling element of the 3x3 matrix
     * @param m02 the X coordinate translation element of the 3x3 matrix
     * @param m12 the Y coordinate translation element of the 3x3 matrix
     */
    public PAffineTransform(final float m00, final float m10, final float m01, final float m11, final float m02,
            final float m12) {
        super(m00, m10, m01, m11, m02, m12);
    }

    /**
     * Constructs a new AffineTransform that is a copy of the specified
     * AffineTransform object.
     * 
     * @param tx transform to copy
     */
    public PAffineTransform(final AffineTransform tx) {
        super(tx);
    }

    /**
     * Scales the transform about the given point by the given scale.
     * 
     * @param scale to transform the transform by
     * @param x x coordinate around which the scale should take place
     * @param y y coordinate around which the scale should take place
     */
    public void scaleAboutPoint(final double scale, final double x, final double y) {
        translate(x, y);
        scale(scale, scale);
        translate(-x, -y);
    }

    /**
     * Returns the scale applied to this transform. Note that it does so by
     * computing the change in length of a unit segment after being passed
     * through the transform. This means that a transform that a transform that
     * doesn't scale the in the x but doubles the y will be reported as 2.
     * 
     * @return the different in length of a unit segment after being
     *         transformed.
     */
    public double getScale() {
        PTS1[0] = 0; // x1
        PTS1[1] = 0; // y1
        PTS1[2] = 1; // x2
        PTS1[3] = 0; // y2
        transform(PTS1, 0, PTS2, 0, 2);
        return Point2D.distance(PTS2[0], PTS2[1], PTS2[2], PTS2[3]);
    }

    /**
     * Sets the scale about to the origin of this transform to the scale
     * provided.
     * 
     * @param scale The desired resulting scale
     */
    public void setScale(final double scale) {
        if (scale == 0) {
            throw new PAffineTransformException("Can't set scale to 0", this);
        }

        scaleAboutPoint(scale / getScale(), 0, 0);
    }

    /**
     * Applies modifies the transform so that it translates by the given offset.
     * 
     * @param tx x translation of resulting transform
     * @param ty y translation of resulting transform
     */
    public void setOffset(final double tx, final double ty) {
        setTransform(getScaleX(), getShearY(), getShearX(), getScaleY(), tx, ty);
    }

    /**
     * Returns the rotation applied to this affine transform in radians. The
     * value returned will be between 0 and 2pi.
     * 
     * @return rotation in radians
     */
    public double getRotation() {
        PTS1[0] = 0; // x1
        PTS1[1] = 0; // y1
        PTS1[2] = 1; // x2
        PTS1[3] = 0; // y2

        transform(PTS1, 0, PTS2, 0, 2);

        final double dy = Math.abs(PTS2[3] - PTS2[1]);
        final double l = Point2D.distance(PTS2[0], PTS2[1], PTS2[2], PTS2[3]);
        double rotation = Math.asin(dy / l);

        // correct for quadrant
        if (PTS2[3] - PTS2[1] > 0) {
            if (PTS2[2] - PTS2[0] < 0) {
                rotation = Math.PI - rotation;
            }
        }
        else if (PTS2[2] - PTS2[0] > 0) {
            rotation = 2 * Math.PI - rotation;
        }
        else {
            rotation = rotation + Math.PI;
        }

        return rotation;
    }

    /**
     * Set rotation in radians. This is not cumulative.
     * 
     * @param theta desired rotation in radians.
     */
    public void setRotation(final double theta) {
        rotate(theta - getRotation());
    }

    /**
     * Applies the transform to the provided dimension.
     * 
     * @param dimSrc source dimension
     * @param dimDst will be changed to be the transformed dimension, may be
     *            null
     * @return the transformed dimension
     */
    public Dimension2D transform(final Dimension2D dimSrc, final Dimension2D dimDst) {
        final Dimension2D result;
        if (dimDst == null) {
            result = (Dimension2D) dimSrc.clone();
        }
        else {
            result = dimDst;
        }

        PTS1[0] = dimSrc.getWidth();
        PTS1[1] = dimSrc.getHeight();
        deltaTransform(PTS1, 0, PTS2, 0, 1);
        result.setSize(PTS2[0], PTS2[1]);
        return result;
    }

    /**
     * Applies the inverse of this transform to the source point if possible.
     * 
     * @since 1.3
     * @param ptSrc point to be transformed
     * @param ptDst result of transform will be placed in this point
     * 
     * @return the transformed point
     */
    public Point2D inverseTransform(final Point2D ptSrc, final Point2D ptDst) {
        try {
            return super.inverseTransform(ptSrc, ptDst);
        }
        catch (final NoninvertibleTransformException e) {
            throw new PAffineTransformException("Could not invert Transform", e, this);
        }
    }

    /**
     * Applies the inverse of this transform to the source dimension if
     * possible.
     * 
     * @param dimSrc dimension to be transformed
     * @param dimDst result of transform will be placed in this dimension
     * 
     * @return the transformed dimension
     */
    public Dimension2D inverseTransform(final Dimension2D dimSrc, final Dimension2D dimDst) {
        final Dimension2D result;
        if (dimDst == null) {
            result = (Dimension2D) dimSrc.clone();
        }
        else {
            result = dimDst;
        }

        final double width = dimSrc.getWidth();
        final double height = dimSrc.getHeight();
        final double m00 = getScaleX();
        final double m11 = getScaleY();
        final double m01 = getShearX();
        final double m10 = getShearY();
        final double det = m00 * m11 - m01 * m10;

        if (Math.abs(det) > Double.MIN_VALUE) {
            result.setSize((width * m11 - height * m01) / det, (height * m00 - width * m10) / det);
        }
        else {
            throw new PAffineTransformException("Could not invert transform", this);
        }

        return result;
    }

    /**
     * Applies this transform to the source rectangle and stores the result in
     * rectDst.
     * 
     * @param rectSrc rectangle to be transformed
     * @param rectDst result of transform will be placed in this rectangle
     * 
     * @return the transformed rectangle
     */
    public Rectangle2D transform(final Rectangle2D rectSrc, final Rectangle2D rectDst) {
        final Rectangle2D result;
        if (rectDst == null) {
            result = (Rectangle2D) rectSrc.clone();
        }
        else {
            result = rectDst;
        }

        if (rectSrc.isEmpty()) {
            result.setRect(rectSrc);
            if (result instanceof PBounds) {
                ((PBounds) result).reset();
            }
            return result;
        }

        double scale;

        switch (getType()) {
            case AffineTransform.TYPE_IDENTITY:
                if (rectSrc != result) {
                    result.setRect(rectSrc);
                }
                break;

            case AffineTransform.TYPE_TRANSLATION:
                result.setRect(rectSrc.getX() + getTranslateX(), rectSrc.getY() + getTranslateY(), rectSrc.getWidth(),
                        rectSrc.getHeight());
                break;

            case AffineTransform.TYPE_UNIFORM_SCALE:
                scale = getScaleX();
                result.setRect(rectSrc.getX() * scale, rectSrc.getY() * scale, rectSrc.getWidth() * scale, rectSrc
                        .getHeight()
                        * scale);
                break;

            case AffineTransform.TYPE_TRANSLATION | AffineTransform.TYPE_UNIFORM_SCALE:
                scale = getScaleX();
                result.setRect(rectSrc.getX() * scale + getTranslateX(), rectSrc.getY() * scale + getTranslateY(),
                        rectSrc.getWidth() * scale, rectSrc.getHeight() * scale);
                break;

            default:
                final double[] pts = rectToArray(rectSrc);
                transform(pts, 0, pts, 0, 4);
                rectFromArray(result, pts);
                break;
        }

        return result;
    }

    /**
     * Applies the inverse of this transform to the source rectangle and stores
     * the result in rectDst.
     * 
     * @param rectSrc rectangle to be transformed
     * @param rectDst result of transform will be placed in this rectangle
     * 
     * @return the transformed rectangle
     */
    public Rectangle2D inverseTransform(final Rectangle2D rectSrc, final Rectangle2D rectDst) {
        final Rectangle2D result;
        if (rectDst == null) {
            result = (Rectangle2D) rectSrc.clone();
        }
        else {
            result = rectDst;
        }

        if (rectSrc.isEmpty()) {
            result.setRect(rectSrc);
            if (result instanceof PBounds) {
                ((PBounds) result).reset();
            }
            return result;
        }

        double scale;

        switch (getType()) {
            case AffineTransform.TYPE_IDENTITY:
                if (rectSrc != result) {
                    result.setRect(rectSrc);
                }
                break;

            case AffineTransform.TYPE_TRANSLATION:
                result.setRect(rectSrc.getX() - getTranslateX(), rectSrc.getY() - getTranslateY(), rectSrc.getWidth(),
                        rectSrc.getHeight());
                break;

            case AffineTransform.TYPE_UNIFORM_SCALE:
                scale = getScaleX();
                if (scale == 0) {
                    throw new PAffineTransformException("Could not invertTransform rectangle", this);
                }

                result.setRect(rectSrc.getX() / scale, rectSrc.getY() / scale, rectSrc.getWidth() / scale, rectSrc
                        .getHeight()
                        / scale);
                break;

            case AffineTransform.TYPE_TRANSLATION | AffineTransform.TYPE_UNIFORM_SCALE:
                scale = getScaleX();
                if (scale == 0) {
                    throw new PAffineTransformException("Could not invertTransform rectangle", this);
                }
                result.setRect((rectSrc.getX() - getTranslateX()) / scale, (rectSrc.getY() - getTranslateY()) / scale,
                        rectSrc.getWidth() / scale, rectSrc.getHeight() / scale);
                break;

            default:
                final double[] pts = rectToArray(rectSrc);
                try {
                    inverseTransform(pts, 0, pts, 0, 4);
                }
                catch (final NoninvertibleTransformException e) {
                    throw new PAffineTransformException("Could not invert transform", e, this);
                }
                rectFromArray(result, pts);
                break;
        }

        return result;
    }

    /**
     * Builds an array of coordinates from an source rectangle.
     * 
     * @param aRectangle rectangle from which points coordinates will be
     *            extracted
     * 
     * @return coordinate array
     */
    private static double[] rectToArray(final Rectangle2D aRectangle) {
        PTS1[0] = aRectangle.getX();
        PTS1[1] = aRectangle.getY();
        PTS1[2] = PTS1[0] + aRectangle.getWidth();
        PTS1[3] = PTS1[1];
        PTS1[4] = PTS1[0] + aRectangle.getWidth();
        PTS1[5] = PTS1[1] + aRectangle.getHeight();
        PTS1[6] = PTS1[0];
        PTS1[7] = PTS1[1] + aRectangle.getHeight();
        return PTS1;
    }

    /**
     * Creates a rectangle from an array of coordinates.
     * 
     * @param aRectangle rectangle into which coordinates will be stored
     * @param pts coordinate source
     */
    private static void rectFromArray(final Rectangle2D aRectangle, final double[] pts) {
        double minX = pts[0];
        double minY = pts[1];
        double maxX = pts[0];
        double maxY = pts[1];

        double x;
        double y;

        for (int i = 1; i < 4; i++) {
            x = pts[2 * i];
            y = pts[2 * i + 1];

            if (x < minX) {
                minX = x;
            }
            if (y < minY) {
                minY = y;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (y > maxY) {
                maxY = y;
            }
        }
        aRectangle.setRect(minX, minY, maxX - minX, maxY - minY);
    }
}
