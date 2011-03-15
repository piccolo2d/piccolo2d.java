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

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.piccolo2d.PCamera;


/**
 * <b>PPaintContext</b> is used by piccolo nodes to paint themselves on the
 * screen. PPaintContext wraps a Graphics2D to implement painting.
 * <P>
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PPaintContext {
    /** Used for lowering quality of rendering when requested. */
    public static final int LOW_QUALITY_RENDERING = 0;

    /** Used for improving quality of rendering when requested. */
    public static final int HIGH_QUALITY_RENDERING = 1;

    /** Font context to use while in low quality rendering. */
    public static final FontRenderContext RENDER_QUALITY_LOW_FRC = new FontRenderContext(null, false, true);

    /** Font context to use while in high quality rendering. */
    public static final FontRenderContext RENDER_QUALITY_HIGH_FRC = new FontRenderContext(null, true, true);

    /** Used while calculating scale at which rendering is occurring. */
    private static final double[] PTS = new double[4];

    /** PaintContext is associated with this graphics context. */
    private final Graphics2D graphics;

    /** Used while computing transparency. */
    protected PStack compositeStack;

    /** Used to optimize clipping region. */
    protected PStack clipStack;

    /** Tracks clipping region in local coordinate system. */
    protected PStack localClipStack;

    /** Stack of cameras through which the node being painted is being viewed. */
    protected PStack cameraStack;

    /** Stack of transforms being applied to the drawing context. */
    protected PStack transformStack;

    /** The current render quality that all rendering should be done in. */
    protected int renderQuality;

    /**
     * Creates a PPaintContext associated with the given graphics context.
     * 
     * @param graphics graphics context to associate with this paint context
     */
    public PPaintContext(final Graphics2D graphics) {
        this.graphics = graphics;
        compositeStack = new PStack();
        clipStack = new PStack();
        localClipStack = new PStack();
        cameraStack = new PStack();
        transformStack = new PStack();
        renderQuality = HIGH_QUALITY_RENDERING;

        Shape clip = graphics.getClip();
        if (clip == null) {
            clip = new PBounds(-Integer.MAX_VALUE / 2, -Integer.MAX_VALUE / 2, Integer.MAX_VALUE, Integer.MAX_VALUE);
            graphics.setClip(clip);
        }

        localClipStack.push(clip.getBounds2D());
    }

    /**
     * Returns the graphics context associated with this paint context.
     * 
     * @return graphics context associated with this paint context
     */
    public Graphics2D getGraphics() {
        return graphics;
    }

    /**
     * Returns the clipping region in the local coordinate system applied by
     * graphics.
     * 
     * @return clipping region in the local coordinate system applied by
     *         graphics
     */
    public Rectangle2D getLocalClip() {
        return (Rectangle2D) localClipStack.peek();
    }

    /**
     * Returns scale of the current graphics context. By calculating how a unit
     * segment gets transformed after transforming it by the graphics context's
     * transform.
     * 
     * @return scale of the current graphics context's transformation
     */
    public double getScale() {
        // x1, y1, x2, y2
        PTS[0] = 0;
        PTS[1] = 0;
        PTS[2] = 1;
        PTS[3] = 0;
        graphics.getTransform().transform(PTS, 0, PTS, 0, 2);
        return Point2D.distance(PTS[0], PTS[1], PTS[2], PTS[3]);
    }

    /**
     * Pushes the camera onto the camera stack.
     * 
     * @param aCamera camera to push onto the stack
     */
    public void pushCamera(final PCamera aCamera) {
        cameraStack.push(aCamera);
    }

    /**
     * Removes the camera at the top of the camera stack.
     *
     * @since 1.3
     */
    public void popCamera() {
        cameraStack.pop();
    }

    /**
     * Returns the camera at the top of the camera stack, or null if stack is
     * empty.
     * 
     * @return topmost camera on camera stack or null if stack is empty
     */
    public PCamera getCamera() {
        return (PCamera) cameraStack.peek();
    }

    /**
     * Pushes the given clip to the pain context.
     * 
     * @param clip clip to be pushed
     */
    public void pushClip(final Shape clip) {
        final Shape currentClip = graphics.getClip();
        clipStack.push(currentClip);
        graphics.clip(clip);
        final Rectangle2D newLocalClip = clip.getBounds2D();
        Rectangle2D.intersect(getLocalClip(), newLocalClip, newLocalClip);
        localClipStack.push(newLocalClip);
    }

    /**
     * Removes the topmost clipping region from the clipping stack.
     * 
     * @param clip not used in this method
     */
    public void popClip(final Shape clip) {
        final Shape newClip = (Shape) clipStack.pop();
        graphics.setClip(newClip);
        localClipStack.pop();
    }

    /**
     * Pushes the provided transparency onto the transparency stack if
     * necessary. If the transparency is fully opaque, then it does nothing.
     * 
     * @param transparency transparency to be pushed onto the transparency stack
     */
    public void pushTransparency(final float transparency) {
        if (transparency == 1.0f) {
            return;
        }
        final Composite current = graphics.getComposite();
        float currentAlaph = 1.0f;
        compositeStack.push(current);

        if (current instanceof AlphaComposite) {
            currentAlaph = ((AlphaComposite) current).getAlpha();
        }
        final AlphaComposite newComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, currentAlaph
                * transparency);
        graphics.setComposite(newComposite);
    }

    /**
     * Removes the topmost transparency if the given transparency is not opaque
     * (1f).
     * 
     * @param transparency transparency to be popped
     */
    public void popTransparency(final float transparency) {
        if (transparency == 1.0f) {
            return;
        }
        final Composite c = (Composite) compositeStack.pop();
        graphics.setComposite(c);
    }

    /**
     * Pushed the provided transform onto the transform stack if it is not null.
     * 
     * @param transform will be pushed onto the transform stack if not null
     */
    public void pushTransform(final PAffineTransform transform) {
        if (transform != null) {
            final Rectangle2D newLocalClip = (Rectangle2D) getLocalClip().clone();
            transform.inverseTransform(newLocalClip, newLocalClip);
            transformStack.push(graphics.getTransform());
            localClipStack.push(newLocalClip);
            graphics.transform(transform);
        }
    }

    /**
     * Pops the topmost Transform from the top of the transform if the passed in
     * transform is not null.
     * 
     * @param transform transform that should be at the top of the stack
     */
    public void popTransform(final PAffineTransform transform) {
        if (transform != null) {
            graphics.setTransform((AffineTransform) transformStack.pop());
            localClipStack.pop();
        }
    }

    /**
     * Return the render quality used by this paint context.
     * 
     * @return the current render quality
     */
    public int getRenderQuality() {
        return renderQuality;
    }

    /**
     * Set the rendering hints for this paint context. The render quality is
     * most often set by the rendering PCanvas. Use PCanvas.setRenderQuality()
     * and PCanvas.setInteractingRenderQuality() to set these values.
     * 
     * @param requestedQuality supports PPaintContext.HIGH_QUALITY_RENDERING or
     *            PPaintContext.LOW_QUALITY_RENDERING
     */
    public void setRenderQuality(final int requestedQuality) {
        renderQuality = requestedQuality;

        switch (renderQuality) {
            case HIGH_QUALITY_RENDERING:
                setRenderQualityToHigh();
                break;

            case LOW_QUALITY_RENDERING:
                setRenderQualityToLow();
                break;

            default:
                throw new RuntimeException("Quality must be either HIGH_QUALITY_RENDERING or LOW_QUALITY_RENDERING");
        }
    }

    private void setRenderQualityToLow() {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    private void setRenderQualityToHigh() {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }
}
