/*
 * Copyright (c) 2008-2009, Piccolo2D project, http://piccolo2d.org
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
package edu.umd.cs.piccolox.swt;

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

    protected static int CACHE_COUNT = 0;
    protected static HashMap FONT_CACHE = new HashMap();
    protected static HashMap COLOR_CACHE = new HashMap();
    protected static HashMap SHAPE_CACHE = new HashMap();
    protected static BufferedImage BUFFER = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    static Point PT = new Point();
    static Rectangle2D RECT = new Rectangle2D.Double();
    static Rectangle2D LINE_RECT = new Rectangle2D.Double();
    static org.eclipse.swt.graphics.Rectangle SWT_RECT = new org.eclipse.swt.graphics.Rectangle(0, 0, 0, 0);

    protected GC gc;
    protected Device device;
    protected AffineTransform transform = new AffineTransform();
    protected org.eclipse.swt.graphics.Font curFont;
    protected double lineWidth = 1.0;

    /**
     * Constructor for SWTGraphics2D.
     */
    public SWTGraphics2D(final GC gc, final Device device) {
        super();

        this.gc = gc;
        this.device = device;
    }

    // //////////////////
    // GET CLIP
    // //////////////////

    /**
     * @see java.awt.Graphics#getClipBounds()
     */
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

    public void clipRect(final int x, final int y, final int width, final int height) {
        RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(RECT, transform);
        SWTShapeManager.awtToSWT(RECT, SWT_RECT);

        org.eclipse.swt.graphics.Rectangle clip = gc.getClipping();
        clip = clip.intersection(SWT_RECT);

        gc.setClipping(clip);
    }

    public void setClip(final int x, final int y, final int width, final int height) {
        RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(RECT, transform);
        SWTShapeManager.awtToSWT(RECT, SWT_RECT);

        gc.setClipping(SWT_RECT);
    }

    /**
     * This method isn't really supported by SWT - so will use the shape bounds
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
     * This method isn't really supported by SWT - so will use the shape bounds
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

    public Shape getClip() {
        final org.eclipse.swt.graphics.Rectangle rect = gc.getClipping();
        final Rectangle2D aRect = new Rectangle2D.Double(rect.x, rect.y, rect.width, rect.height);
        try {
            SWTShapeManager.transform(aRect, transform.createInverse());
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return aRect;
    }

    // ///////////////////
    // DEVICE SPECIFIC
    // ///////////////////

    public GraphicsConfiguration getDeviceConfiguration() {
        return ((Graphics2D) BUFFER.getGraphics()).getDeviceConfiguration();
    }

    // //////////////
    // COLOR METHODS
    // //////////////

    public Paint getPaint() {
        return getColor();
    }

    public void setPaint(final Paint paint) {
        if (paint instanceof Color) {
            setColor((Color) paint);
        }
    }

    public Color getColor() {
        final org.eclipse.swt.graphics.Color color = gc.getForeground();
        final Color awtColor = new Color(color.getRed(), color.getGreen(), color.getBlue());
        return awtColor;
    }

    public void setColor(final Color c) {
        org.eclipse.swt.graphics.Color cachedColor = (org.eclipse.swt.graphics.Color) COLOR_CACHE.get(c);
        if (cachedColor == null) {
            cachedColor = new org.eclipse.swt.graphics.Color(device, c.getRed(), c.getGreen(), c.getBlue());
            COLOR_CACHE.put(c, cachedColor);
        }
        gc.setForeground(cachedColor);
    }

    public void setColor(final org.eclipse.swt.graphics.Color c) {
        gc.setForeground(c);
    }

    public void setBackground(final Color c) {
        org.eclipse.swt.graphics.Color cachedColor = (org.eclipse.swt.graphics.Color) COLOR_CACHE.get(c);
        if (cachedColor == null) {
            cachedColor = new org.eclipse.swt.graphics.Color(device, c.getRed(), c.getGreen(), c.getBlue());
            COLOR_CACHE.put(c, cachedColor);
        }
        gc.setBackground(cachedColor);
    }

    public void setBackground(final org.eclipse.swt.graphics.Color c) {
        gc.setBackground(c);
    }

    public Color getBackground() {
        final org.eclipse.swt.graphics.Color color = gc.getBackground();
        final Color awtColor = new Color(color.getRed(), color.getGreen(), color.getBlue());
        return awtColor;
    }

    // //////////////
    // FONT METHODS
    // //////////////

    public org.eclipse.swt.graphics.Font getSWTFont() {
        return curFont;
    }

    public org.eclipse.swt.graphics.FontMetrics getSWTFontMetrics() {
        gc.setFont(curFont);
        return gc.getFontMetrics();
    }

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

                return new Font(fd[0].getName(), style, fd[0].height);
            }
            return null;
        }
        else {
            return null;
        }
    }

    public void setFont(final Font font) {
        final String fontString = "name=" + font.getFamily() + ";bold=" + font.isBold() + ";italic=" + font.isItalic()
                + ";size=" + font.getSize();

        curFont = getFont(fontString);
    }

    public void setFont(final org.eclipse.swt.graphics.Font font) {
        curFont = font;
    }

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
            int sizeInt = 12;
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

    protected org.eclipse.swt.graphics.Font getTransformedFont() {
        if (curFont != null) {
            final FontData fontData = curFont.getFontData()[0];
            int height = fontData.getHeight();
            RECT.setRect(0, 0, height, height);
            SWTShapeManager.transform(RECT, transform);
            height = (int) (RECT.getHeight() + 0.5);

            final String fontString = "name=" + fontData.getName() + ";bold=" + ((fontData.getStyle() & SWT.BOLD) != 0)
                    + ";italic=" + ((fontData.getStyle() & SWT.ITALIC) != 0) + ";size=" + height;
            return getFont(fontString);
        }
        return null;
    }

    // /////////////////////////
    // AFFINE TRANSFORM METHODS
    // /////////////////////////

    public void translate(final int x, final int y) {
        transform.translate(x, y);
    }

    public void translate(final double tx, final double ty) {
        transform.translate(tx, ty);
    }

    public void rotate(final double theta) {
        transform.rotate(theta);
    }

    public void rotate(final double theta, final double x, final double y) {
        transform.rotate(theta, x, y);
    }

    public void scale(final double sx, final double sy) {
        transform.scale(sx, sy);
    }

    public void shear(final double shx, final double shy) {
        transform.shear(shx, shy);
    }

    public void transform(final AffineTransform Tx) {
        transform.concatenate(Tx);
    }

    public void setTransform(final AffineTransform Tx) {
        transform = (AffineTransform) Tx.clone();
    }

    public AffineTransform getTransform() {
        return (AffineTransform) transform.clone();
    }

    // /////////////////////////////
    // DRAWING AND FILLING METHODS
    // /////////////////////////////

    public void clearRect(final int x, final int y, final int width, final int height) {
        fillRect(x, y, width, height);
    }

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
            double[] pts = (double[]) SHAPE_CACHE.get(s);

            if (pts == null) {
                pts = SWTShapeManager.shapeToPolyline(s);
                SHAPE_CACHE.put(s, pts);
            }

            drawPolyline(pts);
        }
    }

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
            double[] pts = (double[]) SHAPE_CACHE.get(s);

            if (pts == null) {
                pts = SWTShapeManager.shapeToPolyline(s);
                SHAPE_CACHE.put(s, pts);
            }

            fillPolygon(pts);
        }
    }

    public void drawPolyline(final int[] xPoints, final int[] yPoints, final int nPoints) {
        final int[] ptArray = new int[2 * nPoints];
        for (int i = 0; i < nPoints; i++) {
            PT.setLocation(xPoints[i], yPoints[i]);
            transform.transform(PT, PT);
            ptArray[2 * i] = xPoints[i];
            ptArray[2 * i + 1] = yPoints[i];
        }

        gc.setLineWidth(getTransformedLineWidth());
        gc.drawPolyline(ptArray);
    }

    public void drawPolyline(final double[] pts) {
        final int[] intPts = SWTShapeManager.transform(pts, transform);
        gc.drawPolyline(intPts);
    }

    public void drawPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        final int[] ptArray = new int[2 * nPoints];
        for (int i = 0; i < nPoints; i++) {
            PT.setLocation(xPoints[i], yPoints[i]);
            transform.transform(PT, PT);
            ptArray[2 * i] = xPoints[i];
            ptArray[2 * i + 1] = yPoints[i];
        }

        gc.drawPolygon(ptArray);
    }

    public void fillPolygon(final double[] pts) {
        final int[] intPts = SWTShapeManager.transform(pts, transform);
        gc.fillPolygon(intPts);
    }

    public void fillPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        final int[] ptArray = new int[2 * nPoints];
        for (int i = 0; i < nPoints; i++) {
            PT.setLocation(xPoints[i], yPoints[i]);
            transform.transform(PT, PT);
            ptArray[2 * i] = xPoints[i];
            ptArray[2 * i + 1] = yPoints[i];
        }

        gc.fillPolygon(ptArray);
    }

    public void drawLine(final int x1, final int y1, final int x2, final int y2) {
        drawLine((double) x1, (double) y1, (double) x2, (double) y2);
    }

    public void drawLine(double x1, double y1, double x2, double y2) {
        PT.setLocation(x1, y1);
        transform.transform(PT, PT);
        x1 = (int) PT.getX();
        y1 = (int) PT.getY();
        PT.setLocation(x2, y2);
        transform.transform(PT, PT);
        x2 = (int) PT.getX();
        y2 = (int) PT.getY();

        gc.setLineWidth(getTransformedLineWidth());
        gc.drawLine((int) (x1 + 0.5), (int) (y1 + 0.5), (int) (x2 + 0.5), (int) (y2 + 0.5));
    }

    // **************************************************************************
    // *
    // FOR NOW - ASSUME NO ROTATION ON THE TRANSFORM FOR THE FOLLOWING CALLS!
    // **************************************************************************
    // *

    public void copyArea(final org.eclipse.swt.graphics.Image img, final double x, final double y) {
        PT.setLocation(x, y);
        transform.transform(PT, PT);

        gc.copyArea(img, (int) (PT.getX() + 0.5), (int) (PT.getY() + 0.5));
    }

    public void copyArea(final int x, final int y, final int width, final int height, final int dx, final int dy) {
        RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(RECT, transform);

        PT.setLocation(dx, dy);
        transform.transform(PT, PT);
        gc.copyArea((int) RECT.getX(), (int) RECT.getY(), (int) RECT.getWidth(), (int) RECT.getHeight(), (int) PT
                .getX(), (int) PT.getY());
    }

    public void drawString(final String str, final double x, final double y) {
        PT.setLocation(x, y);
        transform.transform(PT, PT);
        gc.setFont(getTransformedFont());
        gc.drawString(str, (int) (PT.getX() + 0.5), (int) (PT.getY() + 0.5), true);
    }

    public void drawString(final String str, final int x, final int y) {
        drawString(str, (double) x, (double) y);
    }

    public void drawString(final String str, final float x, final float y) {
        drawString(str, (double) x, (double) y);
    }

    public void drawText(final String s, final double x, final double y) {
        PT.setLocation(x, y);
        transform.transform(PT, PT);
        gc.setFont(getTransformedFont());
        gc.drawText(s, (int) (PT.getX() + 0.5), (int) (PT.getY() + 0.5), true);
    }

    public void drawText(final String s, final double x, final double y, final int flags) {
        PT.setLocation(x, y);
        transform.transform(PT, PT);
        gc.setFont(getTransformedFont());
        gc.drawText(s, (int) (PT.getX() + 0.5), (int) (PT.getY() + 0.5), flags);
    }

    public void drawRect(final int x, final int y, final int width, final int height) {
        drawRect((double) x, (double) y, (double) width, (double) height);
    }

    public void drawRect(final double x, final double y, final double width, final double height) {
        RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(RECT, transform);
        SWTShapeManager.awtToSWT(RECT, SWT_RECT);

        gc.setLineWidth(getTransformedLineWidth());
        gc.drawRectangle(SWT_RECT);
    }

    public void fillRect(final int x, final int y, final int width, final int height) {
        fillRect((double) x, (double) y, (double) width, (double) height);
    }

    public void fillRect(final double x, final double y, final double width, final double height) {
        RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(RECT, transform);
        SWTShapeManager.awtToSWT(RECT, SWT_RECT);

        gc.fillRectangle(SWT_RECT);
    }

    public void drawRoundRect(final int x, final int y, final int width, final int height, final int arcWidth,
            final int arcHeight) {
        drawRoundRect((double) x, (double) y, (double) width, (double) height, (double) arcWidth, (double) arcHeight);
    }

    public void drawRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight) {
        RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(RECT, transform);
        x = RECT.getX();
        y = RECT.getY();
        width = RECT.getWidth();
        height = RECT.getHeight();

        RECT.setRect(0, 0, arcWidth, arcHeight);
        SWTShapeManager.transform(RECT, transform);
        arcWidth = RECT.getWidth();
        arcHeight = RECT.getHeight();

        gc.setLineWidth(getTransformedLineWidth());
        gc.drawRoundRectangle((int) (x + 0.5), (int) (y + 0.5), (int) (width + 0.5), (int) (height + 0.5),
                (int) (arcWidth + 0.5), (int) (arcHeight + 0.5));
    }

    public void fillRoundRect(final int x, final int y, final int width, final int height, final int arcWidth,
            final int arcHeight) {
        fillRoundRect((double) x, (double) y, (double) width, (double) height, (double) arcWidth, (double) arcHeight);
    }

    public void fillRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight) {
        RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(RECT, transform);
        x = RECT.getX();
        y = RECT.getY();
        width = RECT.getWidth();
        height = RECT.getHeight();

        RECT.setRect(0, 0, arcWidth, arcHeight);
        SWTShapeManager.transform(RECT, transform);
        arcWidth = RECT.getWidth();
        arcHeight = RECT.getHeight();

        gc.setLineWidth(getTransformedLineWidth());
        gc.fillRoundRectangle((int) (x + 0.5), (int) (y + 0.5), (int) (width + 0.5), (int) (height + 0.5),
                (int) (arcWidth + 0.5), (int) (arcHeight + 0.5));
    }

    public void drawOval(final int x, final int y, final int width, final int height) {
        drawOval((double) x, (double) y, (double) width, (double) height);
    }

    public void drawOval(final double x, final double y, final double width, final double height) {
        RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(RECT, transform);

        gc.setLineWidth(getTransformedLineWidth());
        gc.drawOval((int) (RECT.getX() + 0.5), (int) (RECT.getY() + 0.5), (int) (RECT.getWidth() + 0.5), (int) (RECT
                .getHeight() + 0.5));
    }

    public void fillOval(final int x, final int y, final int width, final int height) {
        fillOval((double) x, (double) y, (double) width, (double) height);
    }

    public void fillOval(final double x, final double y, final double width, final double height) {
        RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(RECT, transform);

        gc.fillOval((int) (RECT.getX() + 0.5), (int) (RECT.getY() + 0.5), (int) (RECT.getWidth() + 0.5), (int) (RECT
                .getHeight() + 0.5));
    }

    public void drawArc(final int x, final int y, final int width, final int height, final int startAngle,
            final int extent) {
        drawArc((double) x, (double) y, (double) width, (double) height, (double) startAngle, (double) extent);
    }

    public void drawArc(final double x, final double y, final double width, final double height,
            final double startAngle, final double extent) {
        RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(RECT, transform);

        gc.setLineWidth(getTransformedLineWidth());
        gc.drawArc((int) (RECT.getX() + 0.5), (int) (RECT.getY() + 0.5), (int) (RECT.getWidth() + 0.5), (int) (RECT
                .getHeight() + 0.5), (int) (startAngle + 0.5), (int) (startAngle + extent + 0.5));
    }

    public void fillArc(final int x, final int y, final int width, final int height, final int startAngle,
            final int extent) {
        drawArc((double) x, (double) y, (double) width, (double) height, (double) startAngle, (double) extent);
    }

    public void fillArc(final double x, final double y, final double width, final double height,
            final double startAngle, final double extent) {
        RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(RECT, transform);

        gc.drawArc((int) (RECT.getX() + 0.5), (int) (RECT.getY() + 0.5), (int) (RECT.getWidth() + 0.5), (int) (RECT
                .getHeight() + 0.5), (int) (startAngle + 0.5), (int) (startAngle + extent + 0.5));
    }

    // ////////////////////////
    // SWT IMAGE METHODS
    // ////////////////////////

    public void drawImage(final org.eclipse.swt.graphics.Image image, final double x, final double y) {
        final org.eclipse.swt.graphics.Rectangle bounds = image.getBounds();
        RECT.setRect(x, y, bounds.width, bounds.height);
        SWTShapeManager.transform(RECT, transform);
        SWTShapeManager.awtToSWT(RECT, SWT_RECT);

        gc.drawImage(image, 0, 0, bounds.width, bounds.height, SWT_RECT.x, SWT_RECT.y, SWT_RECT.width, SWT_RECT.height);
    }

    public void drawImage(final org.eclipse.swt.graphics.Image image, final int srcX, final int srcY, final int srcW,
            final int srcH, final double destX, final double destY, final double destW, final double destH) {
        RECT.setRect(destX, destY, destW, destH);
        SWTShapeManager.transform(RECT, transform);
        SWTShapeManager.awtToSWT(RECT, SWT_RECT);

        gc.drawImage(image, srcX, srcY, srcW, srcH, SWT_RECT.x, SWT_RECT.y, SWT_RECT.width, SWT_RECT.height);
    }

    // ////////////////////////////
    // OTHER SWT SPECIFIC METHODS
    // ////////////////////////////

    public void setLineWidth(final double lineWidth) {
        this.lineWidth = lineWidth;
    }

    protected int getTransformedLineWidth() {
        LINE_RECT.setRect(0, 0, lineWidth, lineWidth);
        SWTShapeManager.transform(LINE_RECT, transform);

        return (int) (Math.max(LINE_RECT.getWidth(), 1) + 0.5);
    }

    public void fillGradientRectangle(final double x, final double y, final double width, final double height,
            final boolean vertical) {
        RECT.setRect(x, y, width, height);
        SWTShapeManager.transform(RECT, transform);
        SWTShapeManager.awtToSWT(RECT, SWT_RECT);

        gc.fillGradientRectangle(SWT_RECT.x, SWT_RECT.y, SWT_RECT.width, SWT_RECT.height, vertical);
    }

    public void setXORMode(final boolean xOr) {
        gc.setXORMode(xOr);
    }

    public int getAdvanceWidth(final char ch) {
        final org.eclipse.swt.graphics.Font scaledFont = gc.getFont();
        gc.setFont(curFont);
        final int width = gc.getAdvanceWidth(ch);
        gc.setFont(scaledFont);
        return width;
    }

    public int getCharWidth(final char ch) {
        final org.eclipse.swt.graphics.Font scaledFont = gc.getFont();
        gc.setFont(curFont);
        final int width = gc.getCharWidth(ch);
        gc.setFont(scaledFont);
        return width;
    }

    public org.eclipse.swt.graphics.Point stringExtent(final String str) {
        final org.eclipse.swt.graphics.Font scaledFont = gc.getFont();
        gc.setFont(curFont);
        final org.eclipse.swt.graphics.Point extent = gc.stringExtent(str);
        gc.setFont(scaledFont);
        return extent;
    }

    public org.eclipse.swt.graphics.Point textExtent(final String str) {
        final org.eclipse.swt.graphics.Font scaledFont = gc.getFont();
        gc.setFont(curFont);
        final org.eclipse.swt.graphics.Point extent = gc.textExtent(str);
        gc.setFont(scaledFont);
        return extent;
    }

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

    /**
     * @see java.awt.Graphics#drawString(AttributedCharacterIterator, int, int)
     */
    public void drawString(final AttributedCharacterIterator iterator, final int x, final int y) {
    }

    /**
     * @see java.awt.Graphics2D#drawString(AttributedCharacterIterator, float,
     *      float)
     */
    public void drawString(final AttributedCharacterIterator iterator, final float x, final float y) {
    }

    /**
     * @see java.awt.Graphics2D#drawGlyphVector(GlyphVector, float, float)
     */
    public void drawGlyphVector(final GlyphVector g, final float x, final float y) {
    }

    /**
     * @see java.awt.Graphics2D#hit(Rectangle, Shape, boolean)
     */
    public boolean hit(final Rectangle rect, final Shape s, final boolean onStroke) {
        return false;
    }

    /**
     * @see java.awt.Graphics2D#setComposite(Composite)
     */
    public void setComposite(final Composite comp) {
    }

    /**
     * @see java.awt.Graphics2D#setStroke(Stroke)
     */
    public void setStroke(final Stroke s) {
    }

    public void setRenderingHint(final Key hintKey, final Object hintValue) {
    }

    public Object getRenderingHint(final Key hintKey) {
        return null;
    }

    /**
     * @see java.awt.Graphics2D#setRenderingHints(Map)
     */
    public void setRenderingHints(final Map hints) {
    }

    /**
     * @see java.awt.Graphics2D#addRenderingHints(Map)
     */
    public void addRenderingHints(final Map hints) {
    }

    /**
     * @see java.awt.Graphics2D#getRenderingHints()
     */
    public RenderingHints getRenderingHints() {
        return null;
    }

    /**
     * @see java.awt.Graphics2D#getComposite()
     */
    public Composite getComposite() {
        return null;
    }

    /**
     * @see java.awt.Graphics2D#getStroke()
     */
    public Stroke getStroke() {
        return null;
    }

    /**
     * @see java.awt.Graphics2D#getFontRenderContext()
     */
    public FontRenderContext getFontRenderContext() {
        return null;
    }

    /**
     * @see java.awt.Graphics#create()
     */
    public Graphics create() {
        return null;
    }

    /**
     * @see java.awt.Graphics#setPaintMode()
     */
    public void setPaintMode() {
    }

    /**
     * @see java.awt.Graphics#setXORMode(Color)
     */
    public void setXORMode(final Color c1) {
    }

    /**
     * @see java.awt.Graphics#getFontMetrics(Font)
     */
    public FontMetrics getFontMetrics(final Font f) {
        return null;
    }

    /**
     * @see java.awt.Graphics2D#drawImage(Image, AffineTransform, ImageObserver)
     */
    public boolean drawImage(final Image img, final AffineTransform xform, final ImageObserver obs) {
        return false;
    }

    /**
     * @see java.awt.Graphics2D#drawImage(BufferedImage, BufferedImageOp, int,
     *      int)
     */
    public void drawImage(final BufferedImage img, final BufferedImageOp op, final int x, final int y) {
    }

    /**
     * @see java.awt.Graphics2D#drawRenderedImage(RenderedImage,
     *      AffineTransform)
     */
    public void drawRenderedImage(final RenderedImage img, final AffineTransform xform) {
    }

    /**
     * @see java.awt.Graphics2D#drawRenderableImage(RenderableImage,
     *      AffineTransform)
     */
    public void drawRenderableImage(final RenderableImage img, final AffineTransform xform) {
    }

    /**
     * @see java.awt.Graphics#drawImage(Image, int, int, ImageObserver)
     */
    public boolean drawImage(final Image img, final int x, final int y, final ImageObserver observer) {
        return false;
    }

    /**
     * @see java.awt.Graphics#drawImage(Image, int, int, int, int,
     *      ImageObserver)
     */
    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height,
            final ImageObserver observer) {
        return false;
    }

    /**
     * @see java.awt.Graphics#drawImage(Image, int, int, Color, ImageObserver)
     */
    public boolean drawImage(final Image img, final int x, final int y, final Color bgcolor,
            final ImageObserver observer) {
        return false;
    }

    /**
     * @see java.awt.Graphics#drawImage(Image, int, int, int, int, Color,
     *      ImageObserver)
     */
    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height,
            final Color bgcolor, final ImageObserver observer) {
        return false;
    }

    /**
     * @see java.awt.Graphics#drawImage(Image, int, int, int, int, int, int,
     *      int, int, ImageObserver)
     */
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2,
            final int sx1, final int sy1, final int sx2, final int sy2, final ImageObserver observer) {
        return false;
    }

    /**
     * @see java.awt.Graphics#drawImage(Image, int, int, int, int, int, int,
     *      int, int, Color, ImageObserver)
     */
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2,
            final int sx1, final int sy1, final int sx2, final int sy2, final Color bgcolor,
            final ImageObserver observer) {
        return false;
    }

    /**
     * DO NOTHING - DISPOSED IN RENDERING CLASS
     */
    public void dispose() {
    }

    // ///////////////////////////////
    // CLEAN-UP METHODS
    // ///////////////////////////////

    public static void incrementGCCount() {
        CACHE_COUNT++;
    }

    public static void decrementGCCount() {
        CACHE_COUNT--;

        if (CACHE_COUNT == 0) {
            for (final Iterator i = FONT_CACHE.values().iterator(); i.hasNext();) {
                final org.eclipse.swt.graphics.Font font = (org.eclipse.swt.graphics.Font) i.next();
                font.dispose();
            }
            for (final Iterator i = COLOR_CACHE.values().iterator(); i.hasNext();) {
                final org.eclipse.swt.graphics.Color color = (org.eclipse.swt.graphics.Color) i.next();
                color.dispose();
            }
        }
    }
}
