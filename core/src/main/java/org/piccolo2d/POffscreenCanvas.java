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
package org.piccolo2d;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;

import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PPaintContext;
import org.piccolo2d.util.PUtil;


/**
 * Offscreen canvas.
 *
 * @since 1.3
 */
public final class POffscreenCanvas implements PComponent {

    /** Default render quality, <code>PPaintContext.HIGH_QUALITY_RENDERING</code>. */
    static final int DEFAULT_RENDER_QUALITY = PPaintContext.HIGH_QUALITY_RENDERING;
    
    /** Bounds of this offscreen canvas. */
    private final PBounds bounds;

    /** Camera for this offscreen canvas. */
    private PCamera camera;

    /** Render quality. */
    private int renderQuality = DEFAULT_RENDER_QUALITY;

    /** True if this offscreen canvas is opaque. */
    private boolean opaque;

    /** Background color for this offscreen canvas. */
    private Color backgroundColor;


    /**
     * Create a new offscreen canvas the specified width and height.
     * 
     * @param width width of this offscreen canvas, must be at least zero
     * @param height height of this offscreen canvas, must be at least zero
     */
    public POffscreenCanvas(final int width, final int height) {
        if (width < 0) {
            throw new IllegalArgumentException("width must be at least zero, was " + width);
        }
        if (height < 0) {
            throw new IllegalArgumentException("height must be at least zero, was " + height);
        }
        bounds = new PBounds(0.0d, 0.0d, width, height);
        setCamera(PUtil.createBasicScenegraph());
        
        opaque = false;
        backgroundColor = null;
    }


    /**
     * Render this offscreen canvas to the specified graphics.
     *
     * @param graphics graphics to render this offscreen canvas to, must not be null
     */
    public void render(final Graphics2D graphics) {
        if (graphics == null) {
            throw new IllegalArgumentException("graphics must not be null");
        }
        
        if (opaque && backgroundColor != null) {
            graphics.setBackground(backgroundColor);
            graphics.clearRect(0, 0, (int) bounds.getWidth(), (int) bounds.getHeight());
        }
        
        final PPaintContext paintContext = new PPaintContext(graphics);
        paintContext.setRenderQuality(renderQuality);
        camera.fullPaint(paintContext);
    }

    /**
     * Set the camera for this offscreen canvas to <code>camera</code>.
     * 
     * @param camera camera for this offscreen canvas
     */
    public void setCamera(final PCamera camera) {
        if (this.camera != null) {
            this.camera.setComponent(null);
        }
        this.camera = camera;
        if (camera != null) {
            camera.setComponent(this);
            camera.setBounds((PBounds) bounds.clone());
        }
    }

    /**
     * Return the camera for this offscreen canvas.
     * 
     * @return the camera for this offscreen canvas
     */
    public PCamera getCamera() {
        return camera;
    }

    /**
     * Set the render quality hint for this offscreen canvas to
     * <code>renderQuality</code>.
     * 
     * @param renderQuality render quality hint, must be one of
     *            <code>PPaintContext.HIGH_QUALITY_RENDERING</code> or
     *            <code>PPaintContext.LOW_QUALITY_RENDERING</code>
     */
    public void setRenderQuality(final int renderQuality) {
        if (renderQuality == PPaintContext.HIGH_QUALITY_RENDERING
                || renderQuality == PPaintContext.LOW_QUALITY_RENDERING) {
            this.renderQuality = renderQuality;
        }
        else {
            throw new IllegalArgumentException("renderQuality must be one of PPaintContext.HIGH_QUALITY_RENDERING"
                    + " or PPaintContext.LOW_QUALITY_RENDERING, was " + renderQuality);
        }
    }

    /**
     * Return the render quality hint for this offscreen canvas.
     * 
     * @return the render quality hint for this offscreen canvas
     */
    public int getRenderQuality() {
        return renderQuality;
    }

    /** {@inheritDoc} */
    public void paintImmediately() {
        // empty
    }

    /** {@inheritDoc} */
    public void popCursor() {
        // empty
    }

    /** {@inheritDoc} */
    public void pushCursor(final Cursor cursor) {
        // empty
    }

    /** {@inheritDoc} */
    public void repaint(final PBounds repaintBounds) {
        // empty
    }

    /** {@inheritDoc} */
    public void setInteracting(final boolean interacting) {
        // empty
    }
    
    /**
     * Return the root node of the scene graph for this offscreen canvas.  The
     * root node will be null if the camera for this offscreen canvas is null.
     * 
     * @return the root node of the scene graph for this offscreen canvas
     */
    public PRoot getRoot() {
        return camera == null ? null : camera.getRoot();
    }

    /**
     * Return true if this offscreen canvas is opaque.  Defaults to <code>false</code>.
     *
     * @return true if this offscreen canvas is opaque
     */
    public boolean isOpaque() {
        return opaque;
    }

    /**
     * Set to true if this offscreen canvas is opaque.
     *
     * @param opaque true if this offscreen canvas is opaque
     */
    public void setOpaque(final boolean opaque) {
        this.opaque = opaque;
    }

    /**
     * Return the background color for this offscreen canvas.  If this
     * offscreen canvas is opaque, the background color will be painted
     * before the contents of the scene are rendered.
     *
     * @see #isOpaque
     * @return the background color for this offscreen canvas
     */
    public Color getBackground() {
        return backgroundColor;
    }

    /**
     * Set the background color for this offscreen canvas to <code>backgroundColor</code>.
     * If this offscreen canvas is opaque, the background color will be painted
     * before the contents of the scene are rendered.
     *
     * @see #isOpaque
     * @param backgroundColor background color for this offscreen canvas
     */
    public void setBackground(final Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}