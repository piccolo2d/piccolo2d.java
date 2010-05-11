/**
 * Copyright (C) 1998-1999 by University of Maryland, College Park, MD 20742, USA
 * All rights reserved.
 */
package org.piccolo2d.extras.swt;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.piccolo2d.PNode;
import org.piccolo2d.util.PPaintContext;


/**
 * <b>PSWTText</b> creates a visual component to support text. Multiple lines
 * can be entered, and basic editing is supported. A caret is drawn, and can be
 * repositioned with mouse clicks. The text object is positioned so that its
 * upper-left corner is at the origin, though this can be changed with the
 * translate methods.
 */
public class PSWTText extends PNode {
    private static final long serialVersionUID = 1L;

    /** Below this magnification render text as 'greek'. */
    protected static final double DEFAULT_GREEK_THRESHOLD = 5.5;

    /** Default color of text rendered as 'greek'. */
    protected static final Color DEFAULT_GREEK_COLOR = Color.gray;

    /** Default font name of text. */
    protected static final String DEFAULT_FONT_NAME = "Helvetica";

    /** Default font style for text. */
    protected static final int DEFAULT_FONT_STYLE = Font.PLAIN;

    /** Default font size for text. */
    protected static final int DEFAULT_FONT_SIZE = 12;

    /** Default font for text. */
    protected static final Font DEFAULT_FONT = new Font(DEFAULT_FONT_NAME, DEFAULT_FONT_STYLE, DEFAULT_FONT_SIZE);

    /** Default color for text. */
    protected static final Color DEFAULT_PEN_COLOR = Color.black;

    /** Default text when new text area is created. */
    protected static final String DEFAULT_TEXT = "";

    /** Default background transparency state. */
    protected static final boolean DEFAULT_IS_TRANSPARENT = false;

    /** Default padding. */
    protected static final int DEFAULT_PADDING = 2;

    /** Whether the text be drawn with a transparent background. */
    private boolean transparent = DEFAULT_IS_TRANSPARENT;

    /** Below this magnification text is rendered as greek. */
    protected double greekThreshold = DEFAULT_GREEK_THRESHOLD;

    /** Color for greek text. */
    protected Color greekColor = DEFAULT_GREEK_COLOR;

    /** Current pen color. */
    protected Color penColor = DEFAULT_PEN_COLOR;

    /** Current text font. */
    protected Font font = DEFAULT_FONT;

    /** The amount of padding on each side of the text. */
    protected int padding = DEFAULT_PADDING;

    /** Each element is one line of text. */
    protected ArrayList lines = new ArrayList();

    /** Translation offset X. */
    protected double translateX = 0.0;

    /** Translation offset Y. */
    protected double translateY = 0.0;

    /** Default constructor for PSWTTest. */
    public PSWTText() {
        this(DEFAULT_TEXT, DEFAULT_FONT);
    }

    /**
     * PSWTTest constructor with initial text.
     * 
     * @param str The initial text.
     */
    public PSWTText(final String str) {
        this(str, DEFAULT_FONT);
    }

    /**
     * PSWTTest constructor with initial text and font.
     * 
     * @param str The initial text.
     * @param font The font for this PSWTText component.
     */
    public PSWTText(final String str, final Font font) {
        setText(str);
        this.font = font;

        recomputeBounds();
    }

    /**
     * Returns the current pen color.
     * 
     * @return current pen color
     */
    public Color getPenColor() {
        return penColor;
    }

    /**
     * Sets the current pen color.
     * 
     * @param color use this color.
     */
    public void setPenColor(final Color color) {
        penColor = color;
        repaint();
    }

    /**
     * Returns the current pen paint.
     * 
     * @return the current pen paint
     */
    public Paint getPenPaint() {
        return penColor;
    }

    /**
     * Sets the current pen paint.
     * 
     * @param aPaint use this paint.
     */
    public void setPenPaint(final Paint aPaint) {
        penColor = (Color) aPaint;
    }

    /**
     * Returns the current background color.
     * 
     * @return the current background color
     */
    public Color getBackgroundColor() {
        return (Color) getPaint();
    }

    /**
     * Sets the current background color.
     * 
     * @param color use this color.
     */
    public void setBackgroundColor(final Color color) {
        super.setPaint(color);
    }

    /**
     * Sets whether the text should be drawn in transparent mode, i.e., whether
     * the background should be drawn or not.
     * 
     * @param transparent the new transparency of the background
     */
    public void setTransparent(final boolean transparent) {
        this.transparent = transparent;
    }

    /**
     * Returns whether the text should be drawn using the transparent mode,
     * i.e., whether the background should be drawn or not.
     * 
     * @return true if background will not be drawn
     */
    public boolean isTransparent() {
        return transparent;
    }

    /**
     * Returns the current greek threshold. Below this magnification text is
     * rendered as 'greek'.
     * 
     * @return magnification at which the text will not be drawn and a blank
     *         rectangle will appear instead
     */
    public double getGreekThreshold() {
        return greekThreshold;
    }

    /**
     * Sets the current greek threshold. Below this magnification text is
     * rendered as 'greek'.
     * 
     * @param threshold compared to renderContext magnification.
     */
    public void setGreekThreshold(final double threshold) {
        greekThreshold = threshold;
        repaint();
    }

    /**
     * Returns the current font.
     * 
     * @return current font in node
     */
    public Font getFont() {
        return font;
    }

    /**
     * Return the text within this text component. Multiline text is returned as
     * a single string where each line is separated by a newline character.
     * Single line text does not have any newline characters.
     * 
     * @return string containing this node's text
     */
    public String getText() {
        StringBuffer result = new StringBuffer();

        final Iterator lineIterator = lines.iterator();
        while (lineIterator.hasNext()) {
            result.append(lineIterator.next());
            result.append('\n');
        }

        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
        }

        return result.toString();
    }

    /**
     * Sets the font for the text.
     * <p>
     * <b>Warning:</b> Java has a serious bug in that it does not support very
     * small fonts. In particular, fonts that are less than about a pixel high
     * just don't work. Since in Jazz, it is common to create objects of
     * arbitrary sizes, and then scale them, an application can easily create a
     * text object with a very small font by accident. The workaround for this
     * bug is to create a larger font for the text object, and then scale the
     * node down correspondingly.
     * 
     * @param aFont use this font.
     */
    public void setFont(final Font aFont) {
        font = aFont;

        recomputeBounds();
    }

    /**
     * Sets the text of this visual component to str. Multiple lines of text are
     * separated by a newline character.
     * 
     * @param str use this string.
     */
    public void setText(final String str) {
        int pos = 0;
        int index;
        boolean done = false;
        lines.clear();
        do {
            index = str.indexOf('\n', pos);
            if (index == -1) {
                lines.add(str.substring(pos));
                done = true;
            }
            else {
                lines.add(str.substring(pos, index));
                pos = index + 1;
            }
        } while (!done);

        recomputeBounds();
    }

    /**
     * Set text translation offset X.
     * 
     * @param x the X translation.
     */
    public void setTranslateX(final double x) {
        setTranslation(x, translateY);
    }

    /**
     * Get the X offset translation.
     * 
     * @return the X translation.
     */
    public double getTranslateX() {
        return translateX;
    }

    /**
     * Set text translation offset Y.
     * 
     * @param y the Y translation.
     */
    public void setTranslateY(final double y) {
        setTranslation(translateX, y);
    }

    /**
     * Get the Y offset translation.
     * 
     * @return the Y translation.
     */
    public double getTranslateY() {
        return translateY;
    }

    /**
     * Set the text translation offset to the specified position.
     * 
     * @param x the X component of translation
     * @param y the Y component of translation
     */
    public void setTranslation(final double x, final double y) {
        translateX = x;
        translateY = y;

        recomputeBounds();
    }

    /**
     * Set the text translation offset to point p.
     * 
     * @param p The translation offset.
     */
    public void setTranslation(final Point2D p) {
        setTranslation(p.getX(), p.getY());
    }

    /**
     * Get the text translation offset.
     * 
     * @return The translation offset.
     */
    public Point2D getTranslation() {
        final Point2D p = new Point2D.Double(translateX, translateY);
        return p;
    }

    /**
     * Renders the text object.
     * <p>
     * The transform, clip, and composite will be set appropriately when this
     * object is rendered. It is up to this object to restore the transform,
     * clip, and composite of the Graphics2D if this node changes any of them.
     * However, the color, font, and stroke are unspecified by Jazz. This object
     * should set those things if they are used, but they do not need to be
     * restored.
     * 
     * @param ppc Contains information about current render.
     */
    public void paint(final PPaintContext ppc) {
        if (lines.isEmpty()) {
            return;
        }

        final Graphics2D g2 = ppc.getGraphics();
        AffineTransform at = null;
        boolean translated = false;

        if (translateX != 0.0 || translateY != 0.0) {
            at = g2.getTransform();
            g2.translate(translateX, translateY);
            translated = true;
        }

        final double renderedFontSize = font.getSize() * ppc.getScale();

        // If font is too small then render it as "greek"
        if (renderedFontSize < greekThreshold) {
            paintAsGreek(ppc);
        }
        else {
            paintAsText(ppc);
        }

        if (translated) {
            g2.setTransform(at);
        }
    }

    /**
     * Paints this object as greek.
     * 
     * @param ppc The graphics context to paint into.
     */
    public void paintAsGreek(final PPaintContext ppc) {
        final Graphics2D g2 = ppc.getGraphics();

        if (greekColor != null) {
            g2.setBackground(greekColor);
            ((SWTGraphics2D) g2).fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * Paints this object normally (show it's text). Note that the entire text
     * gets rendered so that it's upper left corner appears at the origin of
     * this local object.
     * 
     * @param ppc The graphics context to paint into.
     */
    public void paintAsText(final PPaintContext ppc) {
        final SWTGraphics2D sg2 = (SWTGraphics2D) ppc.getGraphics();

        if (!transparent) {
            if (getPaint() == null) {
                sg2.setBackground(Color.WHITE);
            }
            else {
                sg2.setBackground((Color) getPaint());
            }

            sg2.fillRect(0, 0, (int) getWidth(), (int) getHeight());
        }

        sg2.translate(padding, padding);

        sg2.setColor(penColor);
        sg2.setFont(font);

        String line;
        double y = 0;

        final FontMetrics fontMetrics = sg2.getSWTFontMetrics();

        final Iterator lineIterator = lines.iterator();
        while (lineIterator.hasNext()) {
            line = (String) lineIterator.next();
            if (line.length() != 0) {
                sg2.drawString(line, 0, y, true);
            }

            y += fontMetrics.getHeight();
        }

        sg2.translate(-padding, -padding);
    }

    /**
     * Recalculates this node's bounding box by examining it's text content.
     */
    protected void recomputeBounds() {
        final GC gc = new GC(Display.getDefault());

        final Point newBounds;
        if (isTextEmpty()) {
            // If no text, then we want to have the bounds of a space character,
            // so get those bounds here
            newBounds = gc.stringExtent(" ");
        }
        else {
            newBounds = calculateTextBounds(gc);
        }

        gc.dispose();

        setBounds(translateX, translateY, newBounds.x + 2 * DEFAULT_PADDING, newBounds.y + 2 * DEFAULT_PADDING);
    }

    /**
     * Determines if this node's text is essentially empty.
     * 
     * @return true if the text is the empty string
     */
    private boolean isTextEmpty() {
        return lines.isEmpty() || lines.size() == 1 && ((String) lines.get(0)).equals("");
    }

    /**
     * Calculates the bounds of the text in the box as measured by the given
     * graphics context and font metrics.
     * 
     * @param gc graphics context from which the measurements are done
     * @return point representing the dimensions of the text's bounds
     */
    private Point calculateTextBounds(final GC gc) {
        final SWTGraphics2D g2 = new SWTGraphics2D(gc, Display.getDefault());
        g2.setFont(font);
        final FontMetrics fm = g2.getSWTFontMetrics();
        final Point textBounds = new Point(0, 0);

        boolean firstLine = true;

        final Iterator lineIterator = lines.iterator();
        while (lineIterator.hasNext()) {
            String line = (String) lineIterator.next();
            Point lineBounds = gc.stringExtent(line);
            if (firstLine) {
                textBounds.x = lineBounds.x;
                textBounds.y += fm.getAscent() + fm.getDescent() + fm.getLeading();
                firstLine = false;
            }
            else {
                textBounds.x = Math.max(lineBounds.x, textBounds.x);
                textBounds.y += fm.getHeight();
            }
        }

        return textBounds;
    }

    /** {@inheritDoc} */
    protected void internalUpdateBounds(final double x, final double y, final double width, final double height) {
        recomputeBounds();
    }

}
