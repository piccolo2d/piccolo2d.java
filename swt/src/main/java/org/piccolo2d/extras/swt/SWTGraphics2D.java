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

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Transform;

/**
 * An extension to Graphics2D to support an SWT Piccolo Canvas with little
 * modification to the current Piccolo architecture
 * 
 * There is an outstanding SWT bug request #33319 for more efficient
 * polyline/polygon rendering methods. It also appears that most of the code
 * below could be made obselete by bug fix #6490
 * 
 * A lot of this may also be duplicated in GEF - the eclipse Graphical Editor
 * Framework
 * 
 * @author Lance Good
 */
public class SWTGraphics2D extends Graphics2D {
    private static final int DEFAULT_FONT_SIZE = 12;

    private static final boolean DEFAULT_STRING_TRANSPARENCY = true;
    private static final float DEFAULT_TRANSPARENCY = 1.0f;

    /**
     * The number of Graphics Contexts active as determined by called to
     * incrementGCCount and decrementGCCount.
     */
    protected static int CACHE_COUNT = 0;
    /** Map from font names to Fonts. */
    protected static final HashMap FONT_CACHE = new HashMap();
    /** Map from awt colors to swt colors. */
    protected static final HashMap COLOR_CACHE = new HashMap();
    /** Map from awt shapess to swt Paths. */
    protected static final HashMap SHAPE_CACHE = new HashMap();
    /** Buffer used to extract the graphics device. */
    protected static final BufferedImage BUFFER = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    private static final Point TEMP_POINT = new Point();
    private static final Rectangle2D TEMP_RECT = new Rectangle2D.Double();
    private static final Rectangle2D TEMP_LINE_RECT = new Rectangle2D.Double();
    private static final org.eclipse.swt.graphics.Rectangle SWT_RECT = new org.eclipse.swt.graphics.Rectangle(0, 0, 0, 0);

    /** The Underlying GraphicsContext provided by swt. */
    protected GC gc;
    /** Device onto which all graphics operations will ultimately take place. */
    protected Device device;
    /** The current transform to apply to drawing operations. */
    protected AffineTransform transform = new AffineTransform();
    private final Transform swtTransform;
    /** The current font to use when drawing text. */
    protected org.eclipse.swt.graphics.Font curFont;
    /** The current stroke width to use when drawing lines. */
    protected double lineWidth = 1.0d;
    /** Transparency, <code>0.0f &lt;= transparency &lt;= 1.0f</code>. */
    private float transparency = DEFAULT_TRANSPARENCY;

    /**
     * Constructor for SWTGraphics2D.
     * 
     * @param gc The Eclipse Graphics Context onto which all Graphics2D
     *            operations are delegating
     * @param device Device onto which ultimately all gc operations are drawn
     *            onto
     */
    public SWTGraphics2D(final GC gc, final Device device) {
        this.gc = gc;
        this.device = device;

        swtTransform = new Transform(device);
        gc.setAntialias(SWT.ON);
    }

    // //////////////////
    // GET CLIP
    // //////////////////

    /** {@inheritDoc} */
    public Rectangle getClipBounds() {
        final org.eclipse.swt.graphics.Rectangle rect = gc.getClipping();
        final Rectangle aRect = new Rectangle(rect.x, rect.y, rect.width, rect.height);
        try {
            SWTShapeManager.transform(aRect, transform.createInverse());
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return aRect;
    }

    /** {@inheritDoc} */
    public void clipRect(final int x, final int y, final int width, final int height) {
        TEMP_RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(TEMP_RECT, transform);
        SWTShapeManager.awtToSWT(TEMP_RECT, SWT_RECT);

        org.eclipse.swt.graphics.Rectangle clip = gc.getClipping();
        clip = clip.intersection(SWT_RECT);

        gc.setClipping(clip);
    }

    /** {@inheritDoc} */
    public void setClip(final int x, final int y, final int width, final int height) {
        TEMP_RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(TEMP_RECT, transform);
        SWTShapeManager.awtToSWT(TEMP_RECT, SWT_RECT);

        gc.setClipping(SWT_RECT);
    }

    /**
     * This method isn't really supported by SWT - so will use the shape bounds.
     * 
     * @param s shape of the clipping region to apply to graphics operations
     */
    public void clip(final Shape s) {
        final Rectangle2D clipBds = s.getBounds2D();
        SWTShapeManager.transform(clipBds, transform);
        SWTShapeManager.awtToSWT(clipBds, SWT_RECT);

        org.eclipse.swt.graphics.Rectangle clip = gc.getClipping();
        clip = clip.intersection(SWT_RECT);

        gc.setClipping(SWT_RECT);
    }

    /**
     * This method isn't really supported by SWT - so will use the shape bounds.
     * 
     * @param clip the desired clipping region's shape, will be simplified to
     *            its bounds
     */
    public void setClip(final Shape clip) {
        if (clip == null) {
            gc.setClipping((org.eclipse.swt.graphics.Rectangle) null);
        }
        else {
            final Rectangle2D clipBds = clip.getBounds2D();
            SWTShapeManager.transform(clipBds, transform);
            SWTShapeManager.awtToSWT(clipBds, SWT_RECT);

            gc.setClipping(SWT_RECT);
        }
    }

    /** {@inheritDoc} */
    public Shape getClip() {
        final org.eclipse.swt.graphics.Rectangle rect = gc.getClipping();
        final Rectangle2D aRect = new Rectangle2D.Double(rect.x, rect.y, rect.width, rect.height);
        try {
            SWTShapeManager.transform(aRect, transform.createInverse());
        }
        catch (final NoninvertibleTransformException e) {
            throw new RuntimeException(e);
        }
        return aRect;
    }

    /**
     * Returns a dummy device configuration.
     * 
     * @return a dummy device configuration
     */
    public GraphicsConfiguration getDeviceConfiguration() {
        return ((Graphics2D) BUFFER.getGraphics()).getDeviceConfiguration();
    }

    // //////////////
    // COLOR METHODS
    // //////////////

    /** {@inheritDoc} */
    public Paint getPaint() {
        return getColor();
    }

    /** {@inheritDoc} */
    public void setPaint(final Paint paint) {
        if (paint instanceof Color) {
            setColor((Color) paint);
        }
    }

    /** {@inheritDoc} */
    public Color getColor() {
        final org.eclipse.swt.graphics.Color color = gc.getForeground();
        final Color awtColor = new Color(color.getRed(), color.getGreen(), color.getBlue());
        return awtColor;
    }

    /** {@inheritDoc} */
    public void setColor(final Color c) {
        org.eclipse.swt.graphics.Color cachedColor = (org.eclipse.swt.graphics.Color) COLOR_CACHE.get(c);
        if (cachedColor == null) {
            cachedColor = new org.eclipse.swt.graphics.Color(device, c.getRed(), c.getGreen(), c.getBlue());
            COLOR_CACHE.put(c, cachedColor);
        }
        gc.setForeground(cachedColor);
    }

    /**
     * Sets the foreground color to the provided swt color.
     * 
     * @param foregroundColor new foreground color
     */
    public void setColor(final org.eclipse.swt.graphics.Color foregroundColor) {
        gc.setForeground(foregroundColor);
    }

    /** {@inheritDoc} */
    public void setBackground(final Color c) {
        org.eclipse.swt.graphics.Color cachedColor = (org.eclipse.swt.graphics.Color) COLOR_CACHE.get(c);
        if (cachedColor == null) {
            cachedColor = new org.eclipse.swt.graphics.Color(device, c.getRed(), c.getGreen(), c.getBlue());
            COLOR_CACHE.put(c, cachedColor);
        }
        gc.setBackground(cachedColor);
    }

    /**
     * Sets the background color to the provided swt color.
     * 
     * @param backgroundColor new background color
     */
    public void setBackground(final org.eclipse.swt.graphics.Color backgroundColor) {
        gc.setBackground(backgroundColor);
    }

    /** {@inheritDoc} */
    public Color getBackground() {
        final org.eclipse.swt.graphics.Color color = gc.getBackground();
        final Color awtColor = new Color(color.getRed(), color.getGreen(), color.getBlue());
        return awtColor;
    }

    // //////////////
    // FONT METHODS
    // //////////////

    /**
     * Returns the current swt font to use when drawing.
     * 
     * @return current swt font
     */
    public org.eclipse.swt.graphics.Font getSWTFont() {
        return curFont;
    }

    /**
     * Returns the font metrics of the current SWT font.
     * 
     * @return font metrics of the current SWT font
     */
    public org.eclipse.swt.graphics.FontMetrics getSWTFontMetrics() {
        gc.setFont(curFont);
        return gc.getFontMetrics();
    }

    /** {@inheritDoc} */
    public Font getFont() {
        if (curFont != null) {
            int style = Font.PLAIN;

            final FontData[] fd = curFont.getFontData();
            if (fd.length > 0) {
                if ((fd[0].getStyle() & SWT.BOLD) != 0) {
                    style = style | Font.BOLD;
                }
                if ((fd[0].getStyle() & SWT.ITALIC) != 0) {
                    style = style | SWT.ITALIC;
                }

                return new Font(fd[0].getName(), style, fd[0].getHeight());
            }
            return null;
        }
        else {
            return null;
        }
    }

    /** {@inheritDoc} */
    public void setFont(final Font font) {
        // TODO:  prevent NPE
        final String fontString = "name=" + font.getFamily() + ";bold=" + font.isBold() + ";italic=" + font.isItalic()
                + ";size=" + font.getSize();

        curFont = getFont(fontString);
    }

    /**
     * Set the font for this SWTGraphics2D to <code>font</code>.
     *
     * @param font font for this SWTGraphics2D
     */
    public void setFont(final org.eclipse.swt.graphics.Font font) {
        curFont = font;
    }

    /**
     * Returns the SWT font matching the given font string.
     * 
     * @param fontString description of the font desired
     * @return matching font, or null if not found
     */
    public org.eclipse.swt.graphics.Font getFont(final String fontString) {
        org.eclipse.swt.graphics.Font cachedFont = (org.eclipse.swt.graphics.Font) FONT_CACHE.get(fontString);
        if (cachedFont == null) {
            int style = 0;
            if (fontString.indexOf("bold=true") != -1) {
                style = style | SWT.BOLD;
            }
            if (fontString.indexOf("italic=true") != -1) {
                style = style | SWT.ITALIC;
            }

            final String name = fontString.substring(0, fontString.indexOf(";"));
            final String size = fontString.substring(fontString.lastIndexOf(";") + 1, fontString.length());
            int sizeInt = DEFAULT_FONT_SIZE;
            try {
                sizeInt = Integer.parseInt(size.substring(size.indexOf("=") + 1, size.length()));
            }
            catch (final Exception e) {
                throw new RuntimeException(e);
            }

            cachedFont = new org.eclipse.swt.graphics.Font(device,
                    name.substring(name.indexOf("=") + 1, name.length()), sizeInt, style);
            FONT_CACHE.put(fontString, cachedFont);
        }
        return cachedFont;
    }

    // /////////////////////////
    // AFFINE TRANSFORM METHODS
    // /////////////////////////

    /** {@inheritDoc} */
    public void translate(final int x, final int y) {
        transform.translate(x, y);
        updateSWTTransform();
    }

    /** {@inheritDoc} */
    public void translate(final double tx, final double ty) {
        transform.translate(tx, ty);
        updateSWTTransform();
    }

    /** {@inheritDoc} */
    public void rotate(final double theta) {
        transform.rotate(theta);
        updateSWTTransform();
    }

    /** {@inheritDoc} */
    public void rotate(final double theta, final double x, final double y) {
        transform.rotate(theta, x, y);
        updateSWTTransform();
    }

    /** {@inheritDoc} */
    public void scale(final double sx, final double sy) {
        transform.scale(sx, sy);
        updateSWTTransform();
    }

    /** {@inheritDoc} */
    public void shear(final double shx, final double shy) {
        transform.shear(shx, shy);
        updateSWTTransform();
    }

    /** {@inheritDoc} */
    public void transform(final AffineTransform srcTransform) {
        transform.concatenate(srcTransform);
        updateSWTTransform();
    }

    /** {@inheritDoc} */
    public void setTransform(final AffineTransform newTransform) {
        transform = (AffineTransform) newTransform.clone();
        updateSWTTransform();
    }

    /** {@inheritDoc} */
    public AffineTransform getTransform() {
        return (AffineTransform) transform.clone();
    }

    // SUPPORT METHODS
    // /////////////////////////////

    /**
     * Updates the SWT transform instance such that it matches AWTs counterpart.
     */
    private void updateSWTTransform() {
        final double[] m = new double[6];
        transform.getMatrix(m);
        swtTransform.setElements((float) m[0], (float) m[1], (float) m[2], (float) m[3], (float) m[4], (float) m[5]);
    }

    /**
     * Converts a java 2d path iterator to a SWT path.
     * 
     * @param iter specifies the iterator to be converted.
     * @return the corresponding path object. Must be disposed() when no longer
     *         used.
     */
    private Path pathIterator2Path(final PathIterator iter) {
        final float[] coords = new float[6];

        final Path path = new Path(device);

        while (!iter.isDone()) {
            final int type = iter.currentSegment(coords);

            switch (type) {
                case PathIterator.SEG_MOVETO:
                    path.moveTo(coords[0], coords[1]);
                    break;

                case PathIterator.SEG_LINETO:
                    path.lineTo(coords[0], coords[1]);
                    break;

                case PathIterator.SEG_CLOSE:
                    path.close();
                    break;

                case PathIterator.SEG_QUADTO:
                    path.quadTo(coords[0], coords[1], coords[2], coords[3]);
                    break;

                case PathIterator.SEG_CUBICTO:
                    path.cubicTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
                    break;
                default:
                    // log this?
            }

            iter.next();
        }
        return path;
    }

    /** {@inheritDoc} */
    public void clearRect(final int x, final int y, final int width, final int height) {
        fillRect(x, y, width, height);
    }

    /** {@inheritDoc} */
    public void draw(final Shape s) {
        if (s instanceof Rectangle2D) {
            final Rectangle2D r2 = (Rectangle2D) s;
            drawRect(r2.getX(), r2.getY(), r2.getWidth(), r2.getHeight());
        }
        else if (s instanceof Ellipse2D) {
            final Ellipse2D e2 = (Ellipse2D) s;
            drawOval(e2.getX(), e2.getY(), e2.getWidth(), e2.getHeight());
        }
        else if (s instanceof RoundRectangle2D) {
            final RoundRectangle2D r2 = (RoundRectangle2D) s;
            drawRoundRect(r2.getX(), r2.getY(), r2.getWidth(), r2.getHeight(), r2.getArcWidth(), r2.getArcHeight());
        }
        else if (s instanceof Arc2D) {
            final Arc2D a2 = (Arc2D) s;
            drawArc(a2.getX(), a2.getY(), a2.getWidth(), a2.getHeight(), a2.getAngleStart(), a2.getAngleExtent());
        }
        else {
            Path p = (Path) SHAPE_CACHE.get(s);
            if (p == null) {
                p = pathIterator2Path(s.getPathIterator(null));
                SHAPE_CACHE.put(s, p);
            }
            drawPath(p);
        }
    }

    /** {@inheritDoc} */
    public void fill(final Shape s) {
        if (s instanceof Rectangle2D) {
            final Rectangle2D r2 = (Rectangle2D) s;
            fillRect(r2.getX(), r2.getY(), r2.getWidth(), r2.getHeight());
        }
        else if (s instanceof Ellipse2D) {
            final Ellipse2D e2 = (Ellipse2D) s;
            fillOval(e2.getX(), e2.getY(), e2.getWidth(), e2.getHeight());
        }
        else if (s instanceof RoundRectangle2D) {
            final RoundRectangle2D r2 = (RoundRectangle2D) s;
            fillRoundRect(r2.getX(), r2.getY(), r2.getWidth(), r2.getHeight(), r2.getArcWidth(), r2.getArcHeight());
        }
        else if (s instanceof Arc2D) {
            final Arc2D a2 = (Arc2D) s;
            fillArc(a2.getX(), a2.getY(), a2.getWidth(), a2.getHeight(), a2.getAngleStart(), a2.getAngleExtent());
        }
        else {
            Path p = (Path) SHAPE_CACHE.get(s);
            if (p == null) {
                p = pathIterator2Path(s.getPathIterator(null));
                SHAPE_CACHE.put(s, p);
            }
            fillPath(p);
        }
    }

    /** {@inheritDoc} */
    public void drawPolyline(final int[] xPoints, final int[] yPoints, final int nPoints) {
        final int[] ptArray = new int[2 * nPoints];
        for (int i = 0; i < nPoints; i++) {
            TEMP_POINT.setLocation(xPoints[i], yPoints[i]);
            transform.transform(TEMP_POINT, TEMP_POINT);
            ptArray[2 * i] = xPoints[i];
            ptArray[2 * i + 1] = yPoints[i];
        }

        gc.setLineWidth(getTransformedLineWidth());
        gc.drawPolyline(ptArray);
    }

    /**
     * Draw a polyline from the specified double array of points.
     *
     * @param pts double array of points
     */
    public void drawPolyline(final double[] pts) {
        final int[] intPts = SWTShapeManager.transform(pts, transform);
        gc.drawPolyline(intPts);
    }

    /** {@inheritDoc} */
    public void drawPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        final int[] ptArray = new int[2 * nPoints];
        for (int i = 0; i < nPoints; i++) {
            TEMP_POINT.setLocation(xPoints[i], yPoints[i]);
            transform.transform(TEMP_POINT, TEMP_POINT);
            ptArray[2 * i] = xPoints[i];
            ptArray[2 * i + 1] = yPoints[i];
        }

        gc.drawPolygon(ptArray);
    }

    /**
     * Fill a polyline from the specified double array of points.
     *
     * @param pts double array of points
     */
    public void fillPolygon(final double[] pts) {
        final int[] intPts = SWTShapeManager.transform(pts, transform);
        gc.fillPolygon(intPts);
    }

    /** {@inheritDoc} */
    public void fillPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        final int[] ptArray = new int[2 * nPoints];
        for (int i = 0; i < nPoints; i++) {
            TEMP_POINT.setLocation(xPoints[i], yPoints[i]);
            transform.transform(TEMP_POINT, TEMP_POINT);
            ptArray[2 * i] = xPoints[i];
            ptArray[2 * i + 1] = yPoints[i];
        }

        gc.fillPolygon(ptArray);
    }

    /** {@inheritDoc} */
    public void drawLine(final int x1, final int y1, final int x2, final int y2) {
        drawLine((double) x1, (double) y1, (double) x2, (double) y2);
    }

    /**
     * Draws a line, using the current color, between the points (x1, y1) and
     * (x2, y2) in this graphics context's coordinate system.
     * 
     * @param x1 the first point's x coordinate.
     * @param y1 the first point's y coordinate.
     * @param x2 the second point's x coordinate.
     * @param y2 the second point's y coordinate.
     */
    public void drawLine(final double x1, final double y1, final double x2, final double y2) {
        TEMP_POINT.setLocation(x1, y1);
        transform.transform(TEMP_POINT, TEMP_POINT);
        final double transformedX1 = (int) TEMP_POINT.getX();
        final double transformedY1 = (int) TEMP_POINT.getY();
        TEMP_POINT.setLocation(x2, y2);
        transform.transform(TEMP_POINT, TEMP_POINT);
        final double transformedX2 = (int) TEMP_POINT.getX();
        final double transformedY2 = (int) TEMP_POINT.getY();

        gc.setLineWidth(getTransformedLineWidth());
        gc.drawLine((int) (transformedX1 + 0.5), (int) (transformedY1 + 0.5), (int) (transformedX2 + 0.5),
                (int) (transformedY2 + 0.5));
    }

    // **************************************************************************
    // *
    // FOR NOW - ASSUME NO ROTATION ON THE TRANSFORM FOR THE FOLLOWING CALLS!
    // **************************************************************************
    // *

    /**
     * Copies the image to the specified position.
     * 
     * @param img swt image to be copied
     * @param x x component of position
     * @param y y component of position
     */
    public void copyArea(final org.eclipse.swt.graphics.Image img, final double x, final double y) {
        TEMP_POINT.setLocation(x, y);
        transform.transform(TEMP_POINT, TEMP_POINT);

        gc.copyArea(img, (int) (TEMP_POINT.getX() + 0.5), (int) (TEMP_POINT.getY() + 0.5));
    }

    /** {@inheritDoc} */
    public void copyArea(final int x, final int y, final int width, final int height, final int dx, final int dy) {
        TEMP_RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(TEMP_RECT, transform);

        TEMP_POINT.setLocation(dx, dy);
        transform.transform(TEMP_POINT, TEMP_POINT);
        gc.copyArea((int) TEMP_RECT.getX(), (int) TEMP_RECT.getY(), (int) TEMP_RECT.getWidth(), (int) TEMP_RECT
                .getHeight(), (int) TEMP_POINT.getX(), (int) TEMP_POINT.getY());
    }

    /**
     * Renders the text of the specified String, using the current text
     * attribute state in the Graphics2D context. The baseline of the first
     * character is at position (x, y) in the User Space. The rendering
     * attributes applied include the Clip, Transform, Paint, Font and Composite
     * attributes. For characters in script systems such as Hebrew and Arabic,
     * the glyphs can be rendered from right to left, in which case the
     * coordinate supplied is the location of the leftmost character on the
     * baseline.
     * 
     * @param str the string to be rendered
     * @param x the x coordinate of the location where the String should be
     *            rendered
     * @param y the y coordinate of the location where the String should be
     *            rendered
     * @param isTransparent whether a background should be painted behind the
     *            text
     */
    public void drawString(final String str, final int x, final int y, final boolean isTransparent) {
        gc.setTransform(swtTransform);
        gc.drawString(str, x, y, isTransparent);
        gc.setTransform(null);
    }

    /** {@inheritDoc} */
    public void drawString(final String str, final int x, final int y) {
        drawString(str, x, y, DEFAULT_STRING_TRANSPARENCY);
    }

    /**
     * Renders the text of the specified String, using the current text
     * attribute state in the Graphics2D context. The baseline of the first
     * character is at position (x, y) in the User Space. The rendering
     * attributes applied include the Clip, Transform, Paint, Font and Composite
     * attributes. For characters in script systems such as Hebrew and Arabic,
     * the glyphs can be rendered from right to left, in which case the
     * coordinate supplied is the location of the leftmost character on the
     * baseline.
     * 
     * @param str the string to be rendered
     * @param x the x coordinate of the location where the String should be
     *            rendered
     * @param y the y coordinate of the location where the String should be
     *            rendered
     */
    public void drawString(final String str, final double x, final double y) {
        drawString(str, (int) (x + 0.5), (int) (y + 0.5));
    }

    /**
     * Renders the text of the specified String, using the current text
     * attribute state in the Graphics2D context. The baseline of the first
     * character is at position (x, y) in the User Space. The rendering
     * attributes applied include the Clip, Transform, Paint, Font and Composite
     * attributes. For characters in script systems such as Hebrew and Arabic,
     * the glyphs can be rendered from right to left, in which case the
     * coordinate supplied is the location of the leftmost character on the
     * baseline.
     * 
     * @param str the string to be rendered
     * @param x the x coordinate of the location where the String should be
     *            rendered
     * @param y the y coordinate of the location where the String should be
     *            rendered
     * @param isTransparent whether a background should be painted behind the
     *            text
     */
    public void drawString(final String str, final double x, final double y, final boolean isTransparent) {
        drawString(str, (int) (x + 0.5), (int) (y + 0.5), isTransparent);
    }

    /** {@inheritDoc} */
    public void drawString(final String str, final float x, final float y) {
        drawString(str, (int) (x + 0.5), (int) (y + 0.5));
    }

    /**
     * Renders the text of the specified String, using the current text
     * attribute state in the Graphics2D context. The baseline of the first
     * character is at position (x, y) in the User Space. The rendering
     * attributes applied include the Clip, Transform, Paint, Font and Composite
     * attributes. For characters in script systems such as Hebrew and Arabic,
     * the glyphs can be rendered from right to left, in which case the
     * coordinate supplied is the location of the leftmost character on the
     * baseline.
     * 
     * @param str the string to be rendered
     * @param x the x coordinate of the location where the String should be
     *            rendered
     * @param y the y coordinate of the location where the String should be
     *            rendered
     */
    public void drawText(final String str, final double x, final double y) {
        drawString(str, (int) (x + 0.5), (int) (y + 0.5));
    }

    /**
     * Renders the text of the specified String, using the current text
     * attribute state in the Graphics2D context. The baseline of the first
     * character is at position (x, y) in the User Space. The rendering
     * attributes applied include the Clip, Transform, Paint, Font and Composite
     * attributes. For characters in script systems such as Hebrew and Arabic,
     * the glyphs can be rendered from right to left, in which case the
     * coordinate supplied is the location of the leftmost character on the
     * baseline.
     * 
     * @param str the string to be rendered
     * @param x the x coordinate of the location where the String should be
     *            rendered
     * @param y the y coordinate of the location where the String should be
     *            rendered
     * @param flags flags to apply to the string as defined by SWT
     */
    public void drawText(final String str, final double x, final double y, final int flags) {
        drawText(str, (int) (x + 0.5), (int) (y + 0.5), flags);
    }

    /**
     * Renders the text of the specified String, using the current text
     * attribute state in the Graphics2D context. The baseline of the first
     * character is at position (x, y) in the User Space. The rendering
     * attributes applied include the Clip, Transform, Paint, Font and Composite
     * attributes. For characters in script systems such as Hebrew and Arabic,
     * the glyphs can be rendered from right to left, in which case the
     * coordinate supplied is the location of the leftmost character on the
     * baseline.
     * 
     * @param str the string to be rendered
     * @param x the x coordinate of the location where the String should be
     *            rendered
     * @param y the y coordinate of the location where the String should be
     *            rendered
     * @param flags flags to apply to the string as defined by SWT
     */
    public void drawText(final String str, final int x, final int y, final int flags) {
        gc.setTransform(swtTransform);
        gc.drawText(str, x, y, flags);
        gc.setTransform(null);
    }

    /** {@inheritDoc} */
    public void drawRect(final int x, final int y, final int width, final int height) {
        drawRect((double) x, (double) y, (double) width, (double) height);
    }

    /**
     * Draws the outline of the specified rectangle. The left and right edges of
     * the rectangle are at x and x + width. The top and bottom edges are at y
     * and y + height. The rectangle is drawn using the graphics context's
     * current color.
     * 
     * @param x the x coordinate of the rectangle to be drawn.
     * @param y the y coordinate of the rectangle to be drawn.
     * @param width the width of the rectangle to be drawn.
     * @param height the height of the rectangle to be drawn.
     */
    public void drawRect(final double x, final double y, final double width, final double height) {
        TEMP_RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(TEMP_RECT, transform);
        SWTShapeManager.awtToSWT(TEMP_RECT, SWT_RECT);

        gc.setLineWidth(getTransformedLineWidth());
        gc.drawRectangle(SWT_RECT);
    }

    /** {@inheritDoc} */
    public void fillRect(final int x, final int y, final int width, final int height) {
        fillRect((double) x, (double) y, (double) width, (double) height);
    }

    /**
     * Fills the specified rectangle. The left and right edges of the rectangle
     * are at x and x + width - 1. The top and bottom edges are at y and y +
     * height - 1. The resulting rectangle covers an area width pixels wide by
     * height pixels tall. The rectangle is filled using the graphics context's
     * current color.
     * 
     * @param x the x coordinate of the rectangle to be filled.
     * @param y the y coordinate of the rectangle to be filled.
     * @param width the width of the rectangle to be filled.
     * @param height the height of the rectangle to be filled.
     */
    public void fillRect(final double x, final double y, final double width, final double height) {
        TEMP_RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(TEMP_RECT, transform);
        SWTShapeManager.awtToSWT(TEMP_RECT, SWT_RECT);

        gc.fillRectangle(SWT_RECT);
    }

    /** {@inheritDoc} */
    public void drawRoundRect(final int x, final int y, final int width, final int height, final int arcWidth,
            final int arcHeight) {
        drawRoundRect((double) x, (double) y, (double) width, (double) height, (double) arcWidth, (double) arcHeight);
    }

    /**
     * Draws an outlined round-cornered rectangle using this graphics context's
     * current color. The left and right edges of the rectangle are at x and x +
     * width, respectively. The top and bottom edges of the rectangle are at y
     * and y + height.
     * 
     * @param x the x coordinate of the rectangle to be drawn.
     * @param y the y coordinate of the rectangle to be drawn.
     * @param width the width of the rectangle to be drawn.
     * @param height the height of the rectangle to be drawn.
     * @param arcWidth the horizontal diameter of the arc at the four corners.
     * @param arcHeight the vertical diameter of the arc at the four corners.
     */
    public void drawRoundRect(final double x, final double y, final double width, final double height,
            final double arcWidth, final double arcHeight) {
        TEMP_RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(TEMP_RECT, transform);
        final double tx = TEMP_RECT.getX();
        final double ty = TEMP_RECT.getY();
        final double twidth = TEMP_RECT.getWidth();
        final double theight = TEMP_RECT.getHeight();

        TEMP_RECT.setRect(0, 0, arcWidth, arcHeight);
        SWTShapeManager.transform(TEMP_RECT, transform);
        final double tarcWidth = TEMP_RECT.getWidth();
        final double tarcHeight = TEMP_RECT.getHeight();

        gc.setLineWidth(getTransformedLineWidth());
        gc.drawRoundRectangle((int) (tx + 0.5), (int) (ty + 0.5), (int) (twidth + 0.5), (int) (theight + 0.5),
                (int) (tarcWidth + 0.5), (int) (tarcHeight + 0.5));
    }

    /** {@inheritDoc} */
    public void fillRoundRect(final int x, final int y, final int width, final int height, final int arcWidth,
            final int arcHeight) {
        fillRoundRect((double) x, (double) y, (double) width, (double) height, (double) arcWidth, (double) arcHeight);
    }

    /**
     * Fills the specified rounded corner rectangle with the current color. The
     * left and right edges of the rectangle are at x and x + width - 1,
     * respectively. The top and bottom edges of the rectangle are at y and y +
     * height - 1.
     * 
     *@param x the x coordinate of the rectangle to be filled.
     *@param y the y coordinate of the rectangle to be filled.
     *@param width the width of the rectangle to be filled.
     *@param height the height of the rectangle to be filled.
     *@param arcWidth the horizontal diameter of the arc at the four corners.
     *@param arcHeight the vertical diameter of the arc at the four corners.
     */
    public void fillRoundRect(final double x, final double y, final double width, final double height,
            final double arcWidth, final double arcHeight) {
        TEMP_RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(TEMP_RECT, transform);
        final double tx = TEMP_RECT.getX();
        final double ty = TEMP_RECT.getY();
        final double twidth = TEMP_RECT.getWidth();
        final double theight = TEMP_RECT.getHeight();

        TEMP_RECT.setRect(0, 0, arcWidth, arcHeight);
        SWTShapeManager.transform(TEMP_RECT, transform);
        final double tarcWidth = TEMP_RECT.getWidth();
        final double tarcHeight = TEMP_RECT.getHeight();

        gc.setLineWidth(getTransformedLineWidth());
        gc.fillRoundRectangle((int) (tx + 0.5), (int) (ty + 0.5), (int) (twidth + 0.5), (int) (theight + 0.5),
                (int) (tarcWidth + 0.5), (int) (tarcHeight + 0.5));
    }

    /** {@inheritDoc} */
    public void drawOval(final int x, final int y, final int width, final int height) {
        drawOval((double) x, (double) y, (double) width, (double) height);
    }

    /**
     * Draws the outline of an oval. The result is a circle or ellipse that fits
     * within the rectangle specified by the x, y, width, and height arguments.
     * The oval covers an area that is width + 1 pixels wide and height + 1
     * pixels tall.
     * 
     * @param x the x coordinate of the upper left corner of the oval to be
     *            drawn.
     * @param y the y coordinate of the upper left corner of the oval to be
     *            drawn.
     * @param width the width of the oval to be drawn.
     * @param height the height of the oval to be drawn.
     */
    public void drawOval(final double x, final double y, final double width, final double height) {
        TEMP_RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(TEMP_RECT, transform);

        gc.setLineWidth(getTransformedLineWidth());
        gc.drawOval((int) (TEMP_RECT.getX() + 0.5), (int) (TEMP_RECT.getY() + 0.5), (int) (TEMP_RECT.getWidth() + 0.5),
                (int) (TEMP_RECT.getHeight() + 0.5));
    }

    /** {@inheritDoc} */
    public void fillOval(final int x, final int y, final int width, final int height) {
        fillOval((double) x, (double) y, (double) width, (double) height);
    }

    /**
     * Fills an oval bounded by the specified rectangle with the current color.
     * 
     * @param x the x coordinate of the upper left corner of the oval to be
     *            filled.
     * @param y the y coordinate of the upper left corner of the oval to be
     *            filled.
     * @param width the width of the oval to be filled.
     * @param height the height of the oval to be filled.
     */
    public void fillOval(final double x, final double y, final double width, final double height) {
        TEMP_RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(TEMP_RECT, transform);

        gc.fillOval((int) (TEMP_RECT.getX() + 0.5), (int) (TEMP_RECT.getY() + 0.5), (int) (TEMP_RECT.getWidth() + 0.5),
                (int) (TEMP_RECT.getHeight() + 0.5));
    }

    /** {@inheritDoc} */
    public void drawArc(final int x, final int y, final int width, final int height, final int startAngle,
            final int extent) {
        drawArc((double) x, (double) y, (double) width, (double) height, (double) startAngle, (double) extent);
    }

    /**
     * Draws the outline of a circular or elliptical arc covering the specified
     * rectangle.
     * 
     * The resulting arc begins at startAngle and extends for arcAngle degrees,
     * using the current color. Angles are interpreted such that 0 degrees is at
     * the 3 o'clock position. A positive value indicates a counter-clockwise
     * rotation while a negative value indicates a clockwise rotation.
     * 
     * The center of the arc is the center of the rectangle whose origin is (x,
     * y) and whose size is specified by the width and height arguments.
     * 
     * The resulting arc covers an area width + 1 pixels wide by height + 1
     * pixels tall.
     * 
     * The angles are specified relative to the non-square extents of the
     * bounding rectangle such that 45 degrees always falls on the line from the
     * center of the ellipse to the upper right corner of the bounding
     * rectangle. As a result, if the bounding rectangle is noticeably longer in
     * one axis than the other, the angles to the start and end of the arc
     * segment will be skewed farther along the longer axis of the bounds.
     * 
     * @param x the x coordinate of the upper-left corner of the arc to be
     *            drawn.
     * @param y the y coordinate of the upper-left corner of the arc to be
     *            drawn.
     * @param width the width of the arc to be drawn.
     * @param height the height of the arc to be drawn.
     * @param startAngle the beginning angle.
     * @param extent the angular extent of the arc, relative to the start angle.
     */
    public void drawArc(final double x, final double y, final double width, final double height,
            final double startAngle, final double extent) {
        TEMP_RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(TEMP_RECT, transform);

        gc.setLineWidth(getTransformedLineWidth());
        gc.drawArc((int) (TEMP_RECT.getX() + 0.5), (int) (TEMP_RECT.getY() + 0.5), (int) (TEMP_RECT.getWidth() + 0.5),
                (int) (TEMP_RECT.getHeight() + 0.5), (int) (startAngle + 0.5), (int) (startAngle + extent + 0.5));
    }

    /** {@inheritDoc} */
    public void fillArc(final int x, final int y, final int width, final int height, final int startAngle,
            final int extent) {
        drawArc((double) x, (double) y, (double) width, (double) height, (double) startAngle, (double) extent);
    }

    /**
     * Draws a filledArc with the options provided.
     * 
     * @param x the x coordinate of the upper-left corner of the arc to be
     *            filled.
     * @param y the y coordinate of the upper-left corner of the arc to be
     *            filled.
     * @param width the width of the arc to be filled.
     * @param height the height of the arc to be filled.
     * @param startAngle the beginning angle.
     * @param extent the angular extent of the arc, relative to the start angle.
     */
    public void fillArc(final double x, final double y, final double width, final double height,
            final double startAngle, final double extent) {
        TEMP_RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(TEMP_RECT, transform);

        gc.drawArc((int) (TEMP_RECT.getX() + 0.5), (int) (TEMP_RECT.getY() + 0.5), (int) (TEMP_RECT.getWidth() + 0.5),
                (int) (TEMP_RECT.getHeight() + 0.5), (int) (startAngle + 0.5), (int) (startAngle + extent + 0.5));
    }

    /**
     * Draws the provided path.
     * 
     * @param p path to draw
     */
    public void drawPath(final Path p) {
        gc.setTransform(swtTransform);
        gc.drawPath(p);
        gc.setTransform(null);
    }

    /**
     * Draws a filled version of the provided path.
     * 
     * @param p path to draw filled
     */
    public void fillPath(final Path p) {
        gc.setTransform(swtTransform);
        gc.fillPath(p);
        gc.setTransform(null);
    }

    /**
     * Draws the provided image at the position specified.
     * 
     * @param image image to draw
     * @param x x component of the position
     * @param y y component of the position
     */
    public void drawImage(final org.eclipse.swt.graphics.Image image, final double x, final double y) {
        final org.eclipse.swt.graphics.Rectangle bounds = image.getBounds();
        TEMP_RECT.setRect(x, y, bounds.width, bounds.height);
        SWTShapeManager.transform(TEMP_RECT, transform);
        SWTShapeManager.awtToSWT(TEMP_RECT, SWT_RECT);

        gc.drawImage(image, 0, 0, bounds.width, bounds.height, SWT_RECT.x, SWT_RECT.y, SWT_RECT.width, SWT_RECT.height);
    }

    /**
     * Draws the source region from the image onto the destination region of the
     * graphics context. Stretching if necessary.
     * 
     * @param image image from which to copy
     * @param srcX the left of the source region
     * @param srcY the top of the source region
     * @param srcW the width of the source region
     * @param srcH the height of the source region
     * @param destX the left of the destination region
     * @param destY the top of the destination region
     * @param destW the width of the destination region
     * @param destH the height of the destination region
     */
    public void drawImage(final org.eclipse.swt.graphics.Image image, final int srcX, final int srcY, final int srcW,
            final int srcH, final double destX, final double destY, final double destW, final double destH) {
        TEMP_RECT.setRect(destX, destY, destW, destH);
        SWTShapeManager.transform(TEMP_RECT, transform);
        SWTShapeManager.awtToSWT(TEMP_RECT, SWT_RECT);

        gc.drawImage(image, srcX, srcY, srcW, srcH, SWT_RECT.x, SWT_RECT.y, SWT_RECT.width, SWT_RECT.height);
    }

    /**
     * Sets the line width to use when drawing shapes.
     * 
     * @param lineWidth width of line when drawing shapes
     */
    public void setLineWidth(final double lineWidth) {
        this.lineWidth = lineWidth;
    }

    /**
     * Computes the width of the line after it passes through the current
     * transform.
     * 
     * @return resulting width of line after being transform
     */
    protected int getTransformedLineWidth() {
        TEMP_LINE_RECT.setRect(0, 0, lineWidth, lineWidth);
        SWTShapeManager.transform(TEMP_LINE_RECT, transform);

        return (int) (Math.max(TEMP_LINE_RECT.getWidth(), 1) + 0.5);
    }

    /**
     * Return the transparency for this graphics context.
     *
     * @return the transparency for this graphics context
     */
    public float getTransparency() {
        return transparency;
    }

    /**
     * Set the transparency for this graphics context to <code>transparency</code>.
     *
     * @param transparency transparency, must be between <code>0.0f</code> and <code>1.0f</code> inclusive
     */
    public void setTransparency(final float transparency) {
        if ((transparency < 0.0f) || (transparency > 1.0f)) {
            throw new IllegalArgumentException("transparency must be between 0.0f and 1.0f inclusive");
        }
        this.transparency = transparency;
        gc.setAlpha((int) (this.transparency * 255.0f));
    }

    /**
     * Fills a gradient rectangle of in the direction specified.
     * 
     * @param x left of resulting rectangle
     * @param y top of resulting rectangle
     * @param width width of resulting rectangle
     * @param height height of resulting rectangle
     * @param vertical whether the gradient should be drawn vertically or
     *            horizontally
     */
    public void fillGradientRectangle(final double x, final double y, final double width, final double height,
            final boolean vertical) {
        TEMP_RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(TEMP_RECT, transform);
        SWTShapeManager.awtToSWT(TEMP_RECT, SWT_RECT);

        gc.fillGradientRectangle(SWT_RECT.x, SWT_RECT.y, SWT_RECT.width, SWT_RECT.height, vertical);
    }

    /**
     * Returns the advance width of the character provided in the current font.
     * 
     * @param ch character to calculate the advance width of.
     * 
     * @return advance width of the character in the current font
     */
    public int getAdvanceWidth(final char ch) {
        final org.eclipse.swt.graphics.Font scaledFont = gc.getFont();
        gc.setFont(curFont);
        final int width = gc.getAdvanceWidth(ch);
        gc.setFont(scaledFont);
        return width;
    }

    /**
     * Returns the width of the character provided in the current font.
     * 
     * @param ch character to calculate the width of.
     * 
     * @return width of the character in the current font
     */
    public int getCharWidth(final char ch) {
        final org.eclipse.swt.graphics.Font scaledFont = gc.getFont();
        gc.setFont(curFont);
        final int width = gc.getCharWidth(ch);
        gc.setFont(scaledFont);
        return width;
    }

    /**
     * Returns the extent of the provided string in the current font.
     * 
     * @param str string to calculate the extent of.
     * 
     * @return extent of the string in the current font
     */
    public org.eclipse.swt.graphics.Point stringExtent(final String str) {
        final org.eclipse.swt.graphics.Font scaledFont = gc.getFont();
        gc.setFont(curFont);
        final org.eclipse.swt.graphics.Point extent = gc.stringExtent(str);
        gc.setFont(scaledFont);
        return extent;
    }

    /**
     * Returns the extent of the provided text in the current font.
     * 
     * @param str string to calculate the extent of.
     * 
     * @return extent of the string in the current font
     */
    public org.eclipse.swt.graphics.Point textExtent(final String str) {
        final org.eclipse.swt.graphics.Font scaledFont = gc.getFont();
        gc.setFont(curFont);
        final org.eclipse.swt.graphics.Point extent = gc.textExtent(str);
        gc.setFont(scaledFont);
        return extent;
    }

    /**
     * Returns the extent of the provided text in the current font assuming the
     * flags given.
     * 
     * @param str string to calculate the extent of
     * @param flags flags to apply to the rendered font before calculation of
     *            extent takes place
     * @return extent of the string in the current font assuming flags provided
     */
    public org.eclipse.swt.graphics.Point textExtent(final String str, final int flags) {
        final org.eclipse.swt.graphics.Font scaledFont = gc.getFont();
        gc.setFont(curFont);
        final org.eclipse.swt.graphics.Point extent = gc.textExtent(str, flags);
        gc.setFont(scaledFont);
        return extent;
    }

    // ///////////////////////////////
    // CURRENTLY UNSUPPORTED METHODS
    // ///////////////////////////////

    /** {@inheritDoc} */
    public void drawString(final AttributedCharacterIterator iterator, final int x, final int y) {
    }

    /** {@inheritDoc} */
    public void drawString(final AttributedCharacterIterator iterator, final float x, final float y) {
    }

    /** {@inheritDoc} */
    public void drawGlyphVector(final GlyphVector g, final float x, final float y) {
    }

    /**
     * Returns whether the given rect and shape touch. If onStroke = true then
     * it'll include the width of the stroke when calculating.
     * 
     * @param rect rect to test
     * @param s shape to test
     * @param onStroke whether to consider the width of the stroke
     * @return true if they touch
     */
    public boolean hit(final Rectangle rect, final Shape s, final boolean onStroke) {
        return false;
    }

    /** {@inheritDoc} */
    public void setComposite(final Composite comp) {
    }

    /** {@inheritDoc} */
    public void setStroke(final Stroke s) {
    }

    /** {@inheritDoc} */
    public void setRenderingHint(final Key hintKey, final Object hintValue) {
    }

    /** {@inheritDoc} */
    public Object getRenderingHint(final Key hintKey) {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.Graphics2D#setRenderingHints(Map)
     */
    public void setRenderingHints(final Map hints) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.Graphics2D#addRenderingHints(Map)
     */
    public void addRenderingHints(final Map hints) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.Graphics2D#getRenderingHints()
     */
    public RenderingHints getRenderingHints() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.Graphics2D#getComposite()
     */
    public Composite getComposite() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.Graphics2D#getStroke()
     */
    public Stroke getStroke() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.Graphics2D#getFontRenderContext()
     */
    public FontRenderContext getFontRenderContext() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.Graphics#create()
     */
    public Graphics create() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.Graphics#setPaintMode()
     */
    public void setPaintMode() {
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.Graphics#setXORMode(Color)
     */
    public void setXORMode(final Color c1) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.Graphics#getFontMetrics(Font)
     */
    public FontMetrics getFontMetrics(final Font f) {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.Graphics2D#drawImage(Image, AffineTransform, ImageObserver)
     */
    public boolean drawImage(final Image img, final AffineTransform xform, final ImageObserver obs) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.Graphics2D#drawImage(BufferedImage, BufferedImageOp, int,
     *      int)
     */
    public void drawImage(final BufferedImage img, final BufferedImageOp op, final int x, final int y) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.Graphics2D#drawRenderedImage(RenderedImage,
     *      AffineTransform)
     */
    public void drawRenderedImage(final RenderedImage img, final AffineTransform xform) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.Graphics2D#drawRenderableImage(RenderableImage,
     *      AffineTransform)
     */
    public void drawRenderableImage(final RenderableImage img, final AffineTransform xform) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.Graphics#drawImage(Image, int, int, ImageObserver)
     */
    public boolean drawImage(final Image img, final int x, final int y, final ImageObserver observer) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.Graphics#drawImage(Image, int, int, int, int,
     *      ImageObserver)
     */
    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height,
            final ImageObserver observer) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.Graphics#drawImage(Image, int, int, Color, ImageObserver)
     */
    public boolean drawImage(final Image img, final int x, final int y, final Color bgcolor,
            final ImageObserver observer) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.Graphics#drawImage(Image, int, int, int, int, Color,
     *      ImageObserver)
     */
    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height,
            final Color bgcolor, final ImageObserver observer) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.Graphics#drawImage(Image, int, int, int, int, int, int,
     *      int, int, ImageObserver)
     */
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2,
            final int sx1, final int sy1, final int sx2, final int sy2, final ImageObserver observer) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.Graphics#drawImage(Image, int, int, int, int, int, int,
     *      int, int, Color, ImageObserver)
     */
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2,
            final int sx1, final int sy1, final int sx2, final int sy2, final Color bgcolor,
            final ImageObserver observer) {
        return false;
    }

    /**
     * DO NOTHING - DISPOSED IN RENDERING CLASS.
     */
    public void dispose() {
    }

    // ///////////////////////////////
    // CLEAN-UP METHODS
    // ///////////////////////////////

    /**
     * Increases the number of uses of this graphics 2d object.
     */
    public static void incrementGCCount() {
        CACHE_COUNT++;
    }

    /**
     * Decreases the number of uses of this graphics 2d object.
     */
    public static void decrementGCCount() {
        CACHE_COUNT--;

        if (CACHE_COUNT == 0) {
            for (final Iterator i = FONT_CACHE.values().iterator(); i.hasNext();) {
                final org.eclipse.swt.graphics.Font font = (org.eclipse.swt.graphics.Font) i.next();
                font.dispose();
            }
            FONT_CACHE.clear();
            for (final Iterator i = COLOR_CACHE.values().iterator(); i.hasNext();) {
                final org.eclipse.swt.graphics.Color color = (org.eclipse.swt.graphics.Color) i.next();
                color.dispose();
            }
            COLOR_CACHE.clear();
            for (final Iterator i = SHAPE_CACHE.values().iterator(); i.hasNext();) {
                final Path path = (Path) i.next();
                path.dispose();
            }
            SHAPE_CACHE.clear();
        }
    }
}
