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

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.piccolo2d.PCamera;
import org.piccolo2d.PRoot;
import org.piccolo2d.activities.PTransformActivity;
import org.piccolo2d.util.PAffineTransform;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PDimension;
import org.piccolo2d.util.PPaintContext;
import org.piccolo2d.util.PUtil;


/**
 * An extension to PCamera that provides a fast image based
 * animationToCenterBounds method.
 * 
 * @author Lance Good
 */
public class PCacheCamera extends PCamera {

    private static final long serialVersionUID = 1L;
    private transient BufferedImage paintBuffer;
    private boolean imageAnimate;
    private PBounds imageAnimateBounds;

    /**
     * Get the buffer used to provide fast image based animation.
     * 
     * @return buffered image used to provide fast image based animation
     */
    protected BufferedImage getPaintBuffer() {
        final PBounds fRef = getFullBoundsReference();
        if (paintBuffer == null || isBufferSmallerThanBounds(fRef)) {
            paintBuffer = buildPaintBuffer(fRef);
        }
        return paintBuffer;
    }

    private boolean isBufferSmallerThanBounds(final PBounds bounds) {
        return paintBuffer.getWidth() < bounds.getWidth() || paintBuffer.getHeight() < bounds.getHeight();
    }

    private BufferedImage buildPaintBuffer(final PBounds fRef) {
        final int newBufferWidth = (int) Math.ceil(fRef.getWidth());
        final int newBufferHeight = (int) Math.ceil(fRef.getHeight());

        if (GraphicsEnvironment.isHeadless()) {
            return new BufferedImage(newBufferWidth, newBufferHeight, BufferedImage.TYPE_4BYTE_ABGR);
        }
        else {
            return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()
                    .createCompatibleImage(newBufferWidth, newBufferHeight);
        }
    }

    /**
     * Caches the information necessary to animate from the current view bounds
     * to the specified centerBounds.
     */
    private AffineTransform cacheViewBounds(final Rectangle2D centerBounds, final boolean scaleToFit) {
        final PBounds viewBounds = getViewBounds();

        // Initialize the image to the union of the current and destination
        // bounds
        final PBounds imageBounds = new PBounds(viewBounds);
        imageBounds.add(centerBounds);

        animateViewToCenterBounds(imageBounds, scaleToFit, 0);

        imageAnimateBounds = getViewBounds();

        // Now create the actual cache image that we will use to animate fast

        final BufferedImage buffer = getPaintBuffer();
        Paint fPaint = Color.white;
        if (getPaint() != null) {
            fPaint = getPaint();
        }
        toImage(buffer, fPaint);

        // Do this after the painting above!
        imageAnimate = true;

        // Return the bounds to the previous viewbounds
        animateViewToCenterBounds(viewBounds, scaleToFit, 0);

        // The code below is just copied from animateViewToCenterBounds to
        // create the correct transform to center the specified bounds

        final PDimension delta = viewBounds.deltaRequiredToCenter(centerBounds);
        final PAffineTransform newTransform = getViewTransform();
        newTransform.translate(delta.width, delta.height);

        if (scaleToFit) {
            final double s = Math.min(viewBounds.getWidth() / centerBounds.getWidth(), viewBounds.getHeight()
                    / centerBounds.getHeight());
            newTransform.scaleAboutPoint(s, centerBounds.getCenterX(), centerBounds.getCenterY());
        }

        return newTransform;
    }

    /**
     * Turns off the fast image animation and does any other applicable cleanup.
     */
    private void clearViewCache() {
        imageAnimate = false;
        imageAnimateBounds = null;
    }

    /**
     * Mimics the standard animateViewToCenterBounds but uses a cached image for
     * performance rather than re-rendering the scene at each step.
     * 
     * @param centerBounds bounds to which the view should be centered
     * @param shouldScaleToFit whether the camera should scale to fit the bounds
     *            so the cover as large a portion of the canvas without changing
     *            the aspect ratio
     * @param duration milliseconds the animation should last
     * @return the scheduled activity, null if duration was 0
     */
    public PTransformActivity animateStaticViewToCenterBoundsFast(final Rectangle2D centerBounds,
            final boolean shouldScaleToFit, final long duration) {
        if (duration == 0) {
            return animateViewToCenterBounds(centerBounds, shouldScaleToFit, duration);
        }

        final AffineTransform newViewTransform = cacheViewBounds(centerBounds, shouldScaleToFit);

        return animateStaticViewToTransformFast(newViewTransform, duration);
    }

    /**
     * This copies the behavior of the standard animateViewToTransform but
     * clears the cache when it is done.
     * 
     * @param dest the resulting transform that the view should be
     *            applying when the animation is complete
     * @param duration length in milliseconds that the animation should last
     * @return the scheduled PTransformActivity, null if duration was 0
     */
    protected PTransformActivity animateStaticViewToTransformFast(final AffineTransform dest, final long duration) {
        if (duration == 0) {
            setViewTransform(dest);
            return null;
        }

        final PTransformActivity.Target t = new PTransformActivity.Target() {
            public void setTransform(final AffineTransform aTransform) {
                PCacheCamera.this.setViewTransform(aTransform);
            }

            public void getSourceMatrix(final double[] aSource) {
                getViewTransformReference().getMatrix(aSource);
            }
        };

        final PTransformActivity ta = new PTransformActivity(duration, PUtil.DEFAULT_ACTIVITY_STEP_RATE, t, dest) {
            protected void activityFinished() {
                clearViewCache();
                repaint();
                super.activityFinished();
            }
        };

        final PRoot r = getRoot();
        if (r != null) {
            r.getActivityScheduler().addActivity(ta);
        }

        return ta;
    }

    /**
     * Overrides the camera's full paint method to do the fast rendering when
     * possible.
     * 
     * @param paintContext Paint Contex in which the painting is done
     */
    public void fullPaint(final PPaintContext paintContext) {
        if (imageAnimate) {
            final PBounds fRef = getFullBoundsReference();
            final PBounds viewBounds = getViewBounds();
            final double scale = getFullBoundsReference().getWidth() / imageAnimateBounds.getWidth();
            final double xOffset = (viewBounds.getX() - imageAnimateBounds.getX()) * scale;
            final double yOffset = (viewBounds.getY() - imageAnimateBounds.getY()) * scale;
            final double scaleW = viewBounds.getWidth() * scale;
            final double scaleH = viewBounds.getHeight() * scale;
            paintContext.getGraphics().drawImage(paintBuffer, 0, 0, (int) Math.ceil(fRef.getWidth()),
                    (int) Math.ceil(fRef.getHeight()), (int) Math.floor(xOffset), (int) Math.floor(yOffset),
                    (int) Math.ceil(xOffset + scaleW), (int) Math.ceil(yOffset + scaleH), null);
        }
        else {
            super.fullPaint(paintContext);
        }
    }
}
