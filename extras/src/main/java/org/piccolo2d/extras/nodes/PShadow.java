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

import java.awt.Image;
import java.awt.Paint;

import org.piccolo2d.extras.util.ShadowUtils;
import org.piccolo2d.nodes.PImage;



/**
 * Shadow node.
 *
 * @since 1.3
 */
public final class PShadow extends PImage {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;


    /**
     * Create a new shadow node containing a shadow of the specified source image using the
     * specified shadow paint and gaussian blur radius.  The dimensions of this node will be
     * <code>src.getWidth() + 4 * blurRadius</code> x <code>src.getHeight() + 4 * blurRadius</code>
     * to account for blurring beyond the bounds of the source image.  Thus the source image
     * will appear to be be offset by (<code>2 * blurRadius</code>, <code>2 * blurRadius</code>)
     * in this node.
     *
     * @param src source image, must not be null
     * @param shadowPaint shadow paint
     * @param blurRadius gaussian blur radius, must be <code>&gt; 0</code>
     */
    public PShadow(final Image src, final Paint shadowPaint, final int blurRadius) {
        super(ShadowUtils.createShadow(src, shadowPaint, blurRadius));
    }
}