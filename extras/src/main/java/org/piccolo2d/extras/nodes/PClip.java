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
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import org.piccolo2d.PNode;
import org.piccolo2d.nodes.PPath;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PPaintContext;
import org.piccolo2d.util.PPickPath;


/**
 * <b>PClip</b> is a simple node that applies a clip before rendering or picking
 * its children. PClip is a subclass of PPath, the clip applies is the
 * GeneralPath wrapped by its super class. See piccolo2d/examples ClipExample.
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PClip extends PPath.Float {
    private static final long serialVersionUID = 1L;

    /**
     * Computes the full bounds and stores them in dstBounds, if dstBounds is
     * null, create a new Bounds and returns it.
     * 
     * @param dstBounds output parameter where computed bounds will be stored
     * @return the computed full bounds
     */
    public PBounds computeFullBounds(final PBounds dstBounds) {
        final PBounds result;
        if (dstBounds == null) {
            result = new PBounds();
        }
        else {
            result = dstBounds;
            result.reset();
        }

        result.add(getBoundsReference());
        localToParent(result);
        return result;
    }

    /**
     * Callback that receives notification of repaint requests from nodes in
     * this node's tree.
     * 
     * @param localBounds region in local coordinations the needs repainting
     * @param childOrThis the node that emitted the repaint notification
     */
    public void repaintFrom(final PBounds localBounds, final PNode childOrThis) {
        if (childOrThis != this) {
            Rectangle2D.intersect(getBoundsReference(), localBounds, localBounds);
            super.repaintFrom(localBounds, childOrThis);
        }
        else {
            super.repaintFrom(localBounds, childOrThis);
        }
    }

    /**
     * Paint's this node as a solid rectangle if paint is provided, clipping
     * appropriately.
     * 
     * @param paintContext context into which this node will be painted
     */
    protected void paint(final PPaintContext paintContext) {
        final Paint p = getPaint();
        if (p != null) {
            final Graphics2D g2 = paintContext.getGraphics();
            g2.setPaint(p);
            g2.fill(getPathReference());
        }
        paintContext.pushClip(getPathReference());
    }

    /**
     * Paints a border around this node if it has a stroke and stroke paint
     * provided.
     * 
     * @param paintContext context into which the border will be drawn
     */
    protected void paintAfterChildren(final PPaintContext paintContext) {
        paintContext.popClip(getPathReference());
        if (getStroke() != null && getStrokePaint() != null) {
            final Graphics2D g2 = paintContext.getGraphics();
            g2.setPaint(getStrokePaint());
            g2.setStroke(getStroke());
            g2.draw(getPathReference());
        }
    }

    /**
     * Try to pick this node and all of its descendants if they are visible in
     * the clipping region.
     * 
     * @param pickPath the pick path to add the node to if its picked
     * @return true if this node or one of its descendants was picked.
     */
    public boolean fullPick(final PPickPath pickPath) {
        if (getPickable() && fullIntersects(pickPath.getPickBounds())) {
            pickPath.pushNode(this);
            pickPath.pushTransform(getTransformReference(false));

            if (pick(pickPath)) {
                return true;
            }

            if (getChildrenPickable() && getPathReference().intersects(pickPath.getPickBounds())) {
                final int count = getChildrenCount();
                for (int i = count - 1; i >= 0; i--) {
                    final PNode each = getChild(i);
                    if (each.fullPick(pickPath)) {
                        return true;
                    }
                }
            }

            if (pickAfterChildren(pickPath)) {
                return true;
            }

            pickPath.popTransform(getTransformReference(false));
            pickPath.popNode(this);
        }

        return false;
    }
}
