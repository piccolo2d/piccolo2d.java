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

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * Static utility methods for creating shadows.
 *
 * @since 1.3
 */
public final class ShadowUtils {

    private static final int BLUR_BOUNDS_AFFORDANCE = 4;

    /**
     * Private no-arg constructor.
     */
    private ShadowUtils() {
        // empty
    }

    /**
     * Create and return a new buffered image containing a shadow of the
     * specified source image using the specifed shadow paint and gaussian blur
     * radius. The dimensions of the returned image will be
     * <code>src.getWidth() + 4 * blurRadius</code> x
     * <code>src.getHeight() + 4 * blurRadius</code> to account for blurring
     * beyond the bounds of the source image. Thus the source image will appear
     * to be be offset by (<code>2 * blurRadius</code>,
     * <code>2 * blurRadius</code>) in the returned image.
     * 
     * @param src source image, must not be null
     * @param shadowPaint shadow paint
     * @param blurRadius gaussian blur radius, must be <code>&gt; 0</code>
     * @return a new buffered image containing a shadow of the specified source
     *         image using the specifed shadow paint and gaussian blur radius
     */
    public static BufferedImage createShadow(final Image src, final Paint shadowPaint, final int blurRadius) {
        if (src == null) {
            throw new IllegalArgumentException("src image must not be null");
        }
        if (blurRadius < 1) {
            throw new IllegalArgumentException("blur radius must be greater than zero, was " + blurRadius);
        }
        int w = src.getWidth(null) + (BLUR_BOUNDS_AFFORDANCE * blurRadius);
        int h = src.getHeight(null) + (BLUR_BOUNDS_AFFORDANCE * blurRadius);

        // paint src image into mask
        BufferedImage mask = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = mask.createGraphics();
        g.drawImage(src, 2 * blurRadius, 2 * blurRadius, null);

        // composite mask with shadow paint
        g.setComposite(AlphaComposite.SrcIn);
        g.setPaint(shadowPaint);
        g.fillRect(0, 0, w, h);
        g.dispose();

        // apply convolve op for blur
        ConvolveOp convolveOp = new ConvolveOp(new GaussianKernel(blurRadius));
        BufferedImage shadow = convolveOp.filter(mask, null);
        return shadow;
    }

    /**
     * Gaussian kernel.
     */
    private static class GaussianKernel extends Kernel {

        /**
         * Create a new gaussian kernel with the specified blur radius.
         * 
         * @param blurRadius blur radius
         */
        GaussianKernel(final int blurRadius) {
            super((2 * blurRadius) + 1, (2 * blurRadius) + 1, createKernel(blurRadius));
        }

        /**
         * Create an array of floats representing a gaussian kernel with the
         * specified radius.
         * 
         * @param r radius
         * @return an array of floats representing a gaussian kernel with the
         *         specified radius
         */
        private static float[] createKernel(final int r) {
            int w = (2 * r) + 1;
            float[] kernel = new float[w * w];
            double m = 2.0d * Math.pow((r / 3.0d), 2);
            double n = Math.PI * m;

            double sum = 0.0d;
            for (int x = 0; x < w; x++) {
                int xr2 = (x - r) * (x - r);
                for (int y = 0; y < w; y++) {
                    int yr2 = (y - r) * (y - r);
                    kernel[x * w + y] = (float) (Math.pow(Math.E, -(yr2 + xr2) / m) / n);
                    sum += kernel[x * w + y];
                }
            }

            for (int i = kernel.length - 1; i >= 0; i--) {
                kernel[i] /= sum;
            }
            return kernel;
        }
    }
}