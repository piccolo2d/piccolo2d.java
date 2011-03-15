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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Dimension2D;

import org.piccolo2d.PNode;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PDimension;
import org.piccolo2d.util.PPaintContext;
import org.piccolo2d.util.PPickPath;


/**
 * <b>PNodeCache</b> caches a visual representation of it's children into an
 * image and uses this cached image for painting instead of painting it's
 * children directly. This is intended to be used in two ways.
 * <p>
 * First it can be used as a simple optimization technique. If a node has many
 * descendents it may be faster to paint the cached image representation instead
 * of painting each node.
 * </p>
 * <p>
 * Second PNodeCache provides a place where "image" effects such as blurring and
 * drop shadows can be added to the Piccolo scene graph. This can be done by
 * overriding the method createImageCache and returing an image with the desired
 * effect applied.
 * </p>
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PNodeCache extends PNode {
    private static final long serialVersionUID = 1L;
    private transient Image imageCache;
    private boolean validatingCache;

    /**
     * Override this method to customize the image cache creation process. For
     * example if you want to create a shadow effect you would do that here.
     * Fill in the cacheOffsetRef if needed to make your image cache line up
     * with the nodes children.
     * 
     * @param cacheOffsetRef output parameter that can be changed to make the
     *            cached offset line up with the node's children
     * @return an image representing this node
     */
    public Image createImageCache(final Dimension2D cacheOffsetRef) {
        return toImage();
    }

    /**
     * Returns an image that is a cached representation of its children.
     * 
     * @return image representation of its children
     */
    public Image getImageCache() {
        if (imageCache == null) {
            final PDimension cacheOffsetRef = new PDimension();
            validatingCache = true;
            resetBounds();
            imageCache = createImageCache(cacheOffsetRef);
            final PBounds b = getFullBoundsReference();
            setBounds(b.getX() + cacheOffsetRef.getWidth(), b.getY() + cacheOffsetRef.getHeight(), imageCache
                    .getWidth(null), imageCache.getHeight(null));
            validatingCache = false;
        }
        return imageCache;
    }

    /**
     * Clears the cache, forcing it to be recalculated on the next call to
     * getImageCache.
     */
    public void invalidateCache() {
        imageCache = null;
    }

    /**
     * Intercepts the normal invalidatePaint mechanism so that the node will not
     * be repainted unless it's cache has been invalidated.
     */
    public void invalidatePaint() {
        if (!validatingCache) {
            super.invalidatePaint();
        }
    }

    /**
     * Handles a repaint event issued from a node in this node's tree.
     * 
     * @param localBounds local bounds of this node that need repainting
     * @param childOrThis the node that emitted the repaint notification
     */
    public void repaintFrom(final PBounds localBounds, final PNode childOrThis) {
        if (!validatingCache) {
            super.repaintFrom(localBounds, childOrThis);
            invalidateCache();
        }
    }

    /**
     * Repaints this node, using the cached result if possible.
     * 
     * @param paintContext context in which painting should occur
     */
    public void fullPaint(final PPaintContext paintContext) {
        if (validatingCache) {
            super.fullPaint(paintContext);
        }
        else {
            final Graphics2D g2 = paintContext.getGraphics();
            g2.drawImage(getImageCache(), (int) getX(), (int) getY(), null);
        }
    }

    /**
     * By always returning false, makes the PNodeCache instance NOT pickable.
     * 
     * @param pickPath path which this node is being tested for inclusion
     * @return always returns false
     */
    protected boolean pickAfterChildren(final PPickPath pickPath) {
        return false;
    }
}
