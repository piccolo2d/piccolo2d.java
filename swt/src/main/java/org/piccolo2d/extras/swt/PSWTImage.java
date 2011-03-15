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

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.piccolo2d.PNode;
import org.piccolo2d.nodes.PImage;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PPaintContext;


/**
 * <b>PSWTImage</b> is a wrapper around a org.eclipse.swt.graphics.Image.
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PSWTImage extends PNode {
    private static final long serialVersionUID = 1L;

    private final transient PSWTCanvas canvas;

    private transient Image image;

    /**
     * Constructs a PSWTImage attached to the provided canvas and with a null
     * image.
     * 
     * The developer will need to call setImage for this node to be useful.
     * 
     * TODO: determine if canvas is actually necessary
     * 
     * @param canvas canvas to associate with the image node
     */
    public PSWTImage(final PSWTCanvas canvas) {
        this.canvas = canvas;
        canvas.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(final DisposeEvent de) {
                disposeImage();
            }
        });
    }

    /**
     * Constructs a PSWTImage wrapping the provided image.
     * 
     * @param canvas canvas to associate with the image node
     * @param image image to be wrapped by this PSWTImage
     */
    public PSWTImage(final PSWTCanvas canvas, final Image image) {
        this(canvas);
        setImage(image);
    }

    /**
     * Constructs a PSWTImage wrapping the provided image after loading it from
     * the file.
     * 
     * @param canvas canvas to associate with the image node
     * @param fileName path to the image, will be loaded and converted to an
     *            Image internally
     */
    public PSWTImage(final PSWTCanvas canvas, final String fileName) {
        this(canvas);
        setImage(fileName);
    }

    /**
     * Returns the image that is shown by this node, may be null.
     * 
     * @return the image that is shown by this node
     */
    public Image getImage() {
        return image;
    }

    /**
     * Set the image that is wrapped by this PImage node. This method will also
     * load the image using a MediaTracker before returning. And if the this
     * PImage is accelerated that image will be copied into an accelerated image
     * if needed. Note that this may cause undesired results with images that
     * have transparent regions, for those cases you may want to set the PImage
     * to be not accelerated.
     * 
     * @param filePath path to the file to load as an image
     */
    public void setImage(final String filePath) {
        setImage(new Image(canvas.getDisplay(), filePath));
    }

    /**
     * Set the image that is wrapped by this PImage node. This method will also
     * load the image using a MediaTracker before returning. And if the this
     * PImage is accelerated that I'm will be copied into an accelerated image
     * if needed. Note that this may cause undesired results with images that
     * have transparent regions, for those cases you may want to set the PImage
     * to be not accelerated.
     * 
     * @param newImage the image that should replace the current one
     */
    public void setImage(final Image newImage) {
        final Image old = image;
        image = newImage;

        if (image != null) {
            final Rectangle bounds = getImage().getBounds();
            setBounds(0, 0, bounds.width, bounds.height);
            invalidatePaint();
        }

        firePropertyChange(PImage.PROPERTY_CODE_IMAGE, PImage.PROPERTY_IMAGE, old, image);
    }

    /**
     * Subclasses may override this method to provide different image dispose
     * behavior.
     */
    protected void disposeImage() {
        if (image != null) {
            image.dispose();
        }
    }

    /** {@inheritDoc} */
    protected void paint(final PPaintContext paintContext) {
        if (getImage() != null) {
            final Rectangle r = image.getBounds();
            final PBounds b = getBoundsReference();
            final SWTGraphics2D g2 = (SWTGraphics2D) paintContext.getGraphics();

            if (b.x == 0 && b.y == 0 && b.width == r.width && b.height == r.height) {
                g2.drawImage(image, 0, 0);
            }
            else {
                g2.translate(b.x, b.y);
                g2.scale(b.width / r.width, b.height / r.height);
                g2.drawImage(image, 0, 0);
                g2.scale(r.width / b.width, r.height / b.height);
                g2.translate(-b.x, -b.y);
            }
        }
    }
}
