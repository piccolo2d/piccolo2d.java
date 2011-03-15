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
package org.piccolo2d.nodes;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.piccolo2d.PNode;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PPaintContext;


/**
 * <b>PImage</b> is a wrapper around a java.awt.Image. If this node is copied or
 * serialized that image will be converted into a BufferedImage if it is not
 * already one.
 * <P>
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PImage extends PNode {

    /**
     * Allows for future serialization code to understand versioned binary
     * formats.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The property name that identifies a change of this node's image (see
     * {@link #getImage getImage}). Both old and new value will be set correctly
     * to Image objects in any property change event.
     */
    public static final String PROPERTY_IMAGE = "image";
    /**
     * The property code that identifies a change of this node's image (see
     * {@link #getImage getImage}). Both old and new value will be set correctly
     * to Image objects in any property change event.
     */
    
    public static final int PROPERTY_CODE_IMAGE = 1 << 15;

    private transient Image image;

    /** Constructs a PImage without a java.awt.Image attached. */
    public PImage() {
    }

    /**
     * Construct a new PImage by loading the given fileName and wrapping the
     * resulting java.awt.Image.
     * 
     * @param fileName of the image to wrap
     */
    public PImage(final String fileName) {
        this(Toolkit.getDefaultToolkit().getImage(fileName));
    }

    /**
     * Construct a new PImage wrapping the given java.awt.Image.
     * 
     * @param image image that this PImage will wrap
     */
    public PImage(final Image image) {
        setImage(image);
    }

    /**
     * Construct a new PImage by loading the given url and wrapping the
     * resulting java.awt.Image. If the url is <code>null</code>, create an
     * empty PImage; this behaviour is useful when fetching resources that may
     * be missing.
     * 
     * @param url URL of image resource to load
     */
    public PImage(final java.net.URL url) {
        if (url != null) {
            setImage(Toolkit.getDefaultToolkit().getImage(url));
        }
    }

    /**
     * Returns the image that is shown by this node, or null if none.
     * 
     * @return java.awt.Image being wrapped by this node
     */
    public Image getImage() {
        return image;
    }

    /**
     * Set the image that is wrapped by this PImage node. This method will also
     * load the image using a MediaTracker before returning.
     * 
     * @param fileName file to be wrapped by this PImage
     */
    public void setImage(final String fileName) {
        setImage(Toolkit.getDefaultToolkit().getImage(fileName));
    }

    /**
     * Set the image that is wrapped by this PImage node. This method will also
     * load the image using a MediaTracker before returning.
     * 
     * @param newImage image to be displayed by this PImage
     */
    public void setImage(final Image newImage) {
        final Image oldImage = image;

        if (newImage == null || newImage instanceof BufferedImage) {
            image = newImage;
        }
        else {
            image = getLoadedImage(newImage);
        }

        if (image != null) {
            setBounds(0, 0, getImage().getWidth(null), getImage().getHeight(null));
            invalidatePaint();
        }

        firePropertyChange(PROPERTY_CODE_IMAGE, PROPERTY_IMAGE, oldImage, image);
    }

    /**
     * Ensures the image is loaded enough (loading is fine).
     * 
     * @param newImage to check
     * @return image or null if not loaded enough.
     */
    private Image getLoadedImage(final Image newImage) {
        final ImageIcon imageLoader = new ImageIcon(newImage);
        switch (imageLoader.getImageLoadStatus()) {
            case MediaTracker.LOADING:
            case MediaTracker.COMPLETE:
                return imageLoader.getImage();
            default:
                return null;
        }
    }

    /**
     * Renders the wrapped Image, stretching it appropriately if the bounds of
     * this PImage doesn't match the bounds of the image.
     * 
     * @param paintContext context into which the rendering will occur
     */
    protected void paint(final PPaintContext paintContext) {
        if (getImage() == null) {
            return;
        }

        final double iw = image.getWidth(null);
        final double ih = image.getHeight(null);

        final PBounds b = getBoundsReference();
        final Graphics2D g2 = paintContext.getGraphics();

        if (b.x != 0 || b.y != 0 || b.width != iw || b.height != ih) {
            g2.translate(b.x, b.y);
            g2.scale(b.width / iw, b.height / ih);
            g2.drawImage(image, 0, 0, null);
            g2.scale(iw / b.width, ih / b.height);
            g2.translate(-b.x, -b.y);
        }
        else {
            g2.drawImage(image, 0, 0, null);
        }

    }

    /**
     * Serializes this PImage to the stream provided. The java.awt.Image wrapped
     * by this PImage is converted into a BufferedImage when serialized.
     * 
     * @param out stream into which serialized object will be serialized
     * @throws IOException if error occurs while writing to the output stream
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        final BufferedImage bufferedImage = toBufferedImage(image, false);
        if (bufferedImage != null) {
            ImageIO.write(bufferedImage, "png", out);
        }
    }

    /**
     * Deserializes a PImage from the input stream provided.
     * 
     * @param in stream from which the PImage should be read
     * @throws IOException if problem occurs while reading from input stream
     * @throws ClassNotFoundException occurs is no mapping from the bytes in the
     *             stream can be found to classes available
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        image = ImageIO.read(in);
    }

    /**
     * Converts the provided image into a BufferedImage. If alwaysCreateCopy is
     * false then if the image is already a buffered image it will not be copied
     * and instead the original image will just be returned.
     * 
     * @param image the image to be converted
     * @param alwaysCreateCopy if true, will create a copy even if image is
     *            already a BufferedImage
     * @return a BufferedImage equivalent to the Image provided
     */
    public static BufferedImage toBufferedImage(final Image image, final boolean alwaysCreateCopy) {
        if (image == null) {
            return null;
        }

        if (!alwaysCreateCopy && image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        BufferedImage result;

        if (GraphicsEnvironment.isHeadless()) {
            result = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        }
        else {
            final GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().getDefaultConfiguration();
            result = graphicsConfiguration.createCompatibleImage(image.getWidth(null), image.getHeight(null));
        }

        final Graphics2D g2 = result.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return result;
    }
}
