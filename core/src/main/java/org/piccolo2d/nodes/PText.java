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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;

import org.piccolo2d.PNode;
import org.piccolo2d.util.PPaintContext;


/**
 * <b>PText</b> is a multi-line text node. The text will flow to base on the
 * width of the node's bounds.
 * 
 * @version 1.1
 * @author Jesse Grosjean
 */
public class PText extends PNode {

    /**
     * Allows for future serialization code to understand versioned binary
     * formats.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The property name that identifies a change of this node's text (see
     * {@link #getText getText}). Both old and new value will be set in any
     * property change event.
     */
    public static final String PROPERTY_TEXT = "text";

    /**
     * The property code that identifies a change of this node's text (see
     * {@link #getText getText}). Both old and new value will be set in any
     * property change event.
     */
    public static final int PROPERTY_CODE_TEXT = 1 << 19;

    /**
     * The property name that identifies a change of this node's font (see
     * {@link #getFont getFont}). Both old and new value will be set in any
     * property change event.
     */
    public static final String PROPERTY_FONT = "font";

    /**
     * The property code that identifies a change of this node's font (see
     * {@link #getFont getFont}). Both old and new value will be set in any
     * property change event.
     */
    public static final int PROPERTY_CODE_FONT = 1 << 20;

    /**
     * The property name that identifies a change of this node's text paint (see
     * {@link #getTextPaint getTextPaint}). Both old and new value will be set
     * in any property change event.
     *
     * @since 1.3
     */
    public static final String PROPERTY_TEXT_PAINT = "text  paint";

    /**
     * The property code that identifies a change of this node's text paint (see
     * {@link #getTextPaint getTextPaint}). Both old and new value will be set
     * in any property change event.
     *
     * @since 1.3
     */
    public static final int PROPERTY_CODE_TEXT_PAINT = 1 << 21;

    /**
     * Default font, 12 point <code>"SansSerif"</code>. Will be made final in
     * version 2.0.
     */
    // public static final Font DEFAULT_FONT = new Font(Font.SANS_SERIF,
    // Font.PLAIN, 12); jdk 1.6+
    public static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 12);

    /**
     * Default greek threshold, <code>5.5d</code>. Will be made final in version
     * 2.0.
     */
    public static final double DEFAULT_GREEK_THRESHOLD = 5.5d;

    /**
     * Default horizontal alignment, <code>Component.LEFT_ALIGNMENT</code>.
     *
     * @since 1.3
     */
    public static final float DEFAULT_HORIZONTAL_ALIGNMENT = Component.LEFT_ALIGNMENT;

    /**
     * Default text, <code>""</code>.
     *
     * @since 1.3
     */
    public static final String DEFAULT_TEXT = "";

    /**
     * Default text paint, <code>Color.BLACK</code>.
     *
     * @since 1.3
     */
    public static final Paint DEFAULT_TEXT_PAINT = Color.BLACK;

    /** Empty text layout array. */
    private static final TextLayout[] EMPTY_TEXT_LAYOUT_ARRAY = new TextLayout[0];

    /** Text for this text node. */
    private String text = DEFAULT_TEXT;

    /** Text paint for this text node. */
    private Paint textPaint = DEFAULT_TEXT_PAINT;

    /** Font for this text node. */
    private Font font = DEFAULT_FONT;

    /**
     * Greek threshold in screen font size for this text node. Will be made
     * private in version 2.0.
     */
    protected double greekThreshold = DEFAULT_GREEK_THRESHOLD;

    /** Horizontal alignment for this text node. */
    private float horizontalAlignment = DEFAULT_HORIZONTAL_ALIGNMENT;

    /**
     * True if this text node should constrain its height to the height of its
     * text.
     */
    private boolean constrainHeightToTextHeight = true;

    /**
     * True if this text node should constrain its height to the height of its
     * text.
     */
    private boolean constrainWidthToTextWidth = true;

    /** One or more lines of text layout. */
    private transient TextLayout[] lines;

    /**
     * Create a new text node with no text (<code>""</code>).
     */
    public PText() {
        super();
        setText(DEFAULT_TEXT);
    }

    /**
     * Create a new text node with the specified text.
     * 
     * @param text text for this text node
     */
    public PText(final String text) {
        this();
        setText(text);
    }
    
    /**
     * Return the horizontal alignment for this text node. The horizontal
     * alignment will be one of <code>Component.LEFT_ALIGNMENT</code>,
     * <code>Component.CENTER_ALIGNMENT</code>, or
     * <code>Component.RIGHT_ALIGNMENT</code>. Defaults to
     * {@link #DEFAULT_HORIZONTAL_ALIGNMENT}.
     * 
     * @since 1.3
     * @return the horizontal alignment for this text node
     */
    public float getHorizontalAlignment() {
        return horizontalAlignment;
    }

    /**
     * Set the horizontal alignment for this text node to
     * <code>horizontalAlignment</code>.
     * 
     * @since 1.3
     * @param horizontalAlignment horizontal alignment, must be one of
     *            <code>Component.LEFT_ALIGNMENT</code>,
     *            <code>Component.CENTER_ALIGNMENT</code>, or
     *            <code>Component.RIGHT_ALIGNMENT</code>
     */
    public void setHorizontalAlignment(final float horizontalAlignment) {
        if (!validHorizontalAlignment(horizontalAlignment)) {
            throw new IllegalArgumentException("horizontalAlignment must be one of Component.LEFT_ALIGNMENT, "
                    + "Component.CENTER_ALIGNMENT, or Component.RIGHT_ALIGNMENT");
        }
        this.horizontalAlignment = horizontalAlignment;
    }

    /**
     * Return true if the specified horizontal alignment is one of
     * <code>Component.LEFT_ALIGNMENT</code>,
     * <code>Component.CENTER_ALIGNMENT</code>, or
     * <code>Component.RIGHT_ALIGNMENT</code>.
     * 
     * @param horizontalAlignment horizontal alignment
     * @return true if the specified horizontal alignment is one of
     *         <code>Component.LEFT_ALIGNMENT</code>,
     *         <code>Component.CENTER_ALIGNMENT</code>, or
     *         <code>Component.RIGHT_ALIGNMENT</code>
     */
    private static boolean validHorizontalAlignment(final float horizontalAlignment) {
        return Component.LEFT_ALIGNMENT == horizontalAlignment || Component.CENTER_ALIGNMENT == horizontalAlignment
                || Component.RIGHT_ALIGNMENT == horizontalAlignment;
    }

    /**
     * Return the paint used to paint this node's text.
     * 
     * @return the paint used to paint this node's text
     */
    public Paint getTextPaint() {
        return textPaint;
    }

    /**
     * Set the paint used to paint this node's text to <code>textPaint</code>.
     * 
     * <p>
     * This is a <b>bound</b> property.
     * </p>
     * 
     * @param textPaint text paint
     */
    public void setTextPaint(final Paint textPaint) {
        if (textPaint == this.textPaint) {
            return;
        }
        final Paint oldTextPaint = this.textPaint;
        this.textPaint = textPaint;
        invalidatePaint();
        firePropertyChange(PROPERTY_CODE_TEXT_PAINT, PROPERTY_TEXT_PAINT, oldTextPaint, this.textPaint);
    }

    /**
     * Return true if this text node should constrain its width to the width of
     * its text. Defaults to <code>true</code>.
     * 
     * @return true if this text node should constrain its width to the width of
     *         its text
     */
    public boolean isConstrainWidthToTextWidth() {
        return constrainWidthToTextWidth;
    }

    /**
     * Set to <code>true</code> if this text node should constrain its width to
     * the width of its text.
     * 
     * @param constrainWidthToTextWidth true if this text node should constrain
     *            its width to the width of its text
     */
    public void setConstrainWidthToTextWidth(final boolean constrainWidthToTextWidth) {
        this.constrainWidthToTextWidth = constrainWidthToTextWidth;
        recomputeLayout();
    }

    /**
     * Return true if this text node should constrain its height to the height
     * of its text. Defaults to <code>true</code>.
     * 
     * @return true if this text node should constrain its height to the height
     *         of its text
     */
    public boolean isConstrainHeightToTextHeight() {
        return constrainHeightToTextHeight;
    }

    /**
     * Set to <code>true</code> if this text node should constrain its height to
     * the height of its text.
     * 
     * @param constrainHeightToTextHeight true if this text node should
     *            constrain its height to the height of its text
     */
    public void setConstrainHeightToTextHeight(final boolean constrainHeightToTextHeight) {
        this.constrainHeightToTextHeight = constrainHeightToTextHeight;
        recomputeLayout();
    }

    /**
     * Return the greek threshold in screen font size. When the screen font size
     * will be below this threshold the text is rendered as 'greek' instead of
     * drawing the text glyphs. Defaults to {@link #DEFAULT_GREEK_THRESHOLD}.
     * 
     * @see PText#paintGreek(PPaintContext)
     * @return the current greek threshold in screen font size
     */
    public double getGreekThreshold() {
        return greekThreshold;
    }

    /**
     * Set the greek threshold in screen font size to
     * <code>greekThreshold</code>. When the screen font size will be below this
     * threshold the text is rendered as 'greek' instead of drawing the text
     * glyphs.
     * 
     * @see PText#paintGreek(PPaintContext)
     * @param greekThreshold greek threshold in screen font size
     */
    public void setGreekThreshold(final double greekThreshold) {
        this.greekThreshold = greekThreshold;
        invalidatePaint();
    }

    /**
     * Return the text for this text node. Defaults to {@link #DEFAULT_TEXT}.
     * 
     * @return the text for this text node
     */
    public String getText() {
        return text;
    }

    /**
     * Set the text for this node to <code>text</code>. The text will be broken
     * up into multiple lines based on the size of the text and the bounds width
     * of this node.
     * 
     * <p>
     * This is a <b>bound</b> property.
     * </p>
     * 
     * @param newText text for this text node
     */
    public void setText(final String newText) {
        if (newText == null && text == null || newText != null && newText.equals(text)) {
            return;
        }

        final String oldText = text;
        if (newText == null) {
            text = DEFAULT_TEXT;
        }
        else {
            text = newText;
        }
        lines = null;
        recomputeLayout();
        invalidatePaint();
        firePropertyChange(PROPERTY_CODE_TEXT, PROPERTY_TEXT, oldText, text);
    }

    /**
     * Return the font for this text node. Defaults to {@link #DEFAULT_FONT}.
     * 
     * @return the font for this text node
     */
    public Font getFont() {
        return font;
    }

    /**
     * Set the font for this text node to <code>font</code>. Note that in
     * Piccolo if you want to change the size of a text object it's often a
     * better idea to scale the PText node instead of changing the font size to
     * get that same effect. Using very large font sizes can slow performance.
     * 
     * <p>
     * This is a <b>bound</b> property.
     * </p>
     * 
     * @param font font for this text node
     */
    public void setFont(final Font font) {
        if (font == this.font) {
            return;
        }
        final Font oldFont = this.font;
        if (font == null) {
            this.font = DEFAULT_FONT;
        }
        else {
            this.font = font;
        }

        lines = null;
        recomputeLayout();
        invalidatePaint();
        firePropertyChange(PROPERTY_CODE_FONT, PROPERTY_FONT, oldFont, this.font);
    }

    /**
     * Compute the bounds of the text wrapped by this node. The text layout is
     * wrapped based on the bounds of this node.
     */
    public void recomputeLayout() {
        final ArrayList linesList = new ArrayList();
        double textWidth = 0;
        double textHeight = 0;

        if (text != null && text.length() > 0) {
            final AttributedString atString = new AttributedString(text);
            atString.addAttribute(TextAttribute.FONT, getFont());
            final AttributedCharacterIterator itr = atString.getIterator();
            final LineBreakMeasurer measurer = new LineBreakMeasurer(itr, PPaintContext.RENDER_QUALITY_HIGH_FRC);
            final float availableWidth;
            if (constrainWidthToTextWidth) {
                availableWidth = Float.MAX_VALUE;
            }
            else {
                availableWidth = (float) getWidth();
            }

            int nextLineBreakOffset = text.indexOf('\n');
            if (nextLineBreakOffset == -1) {
                nextLineBreakOffset = Integer.MAX_VALUE;
            }
            else {
                nextLineBreakOffset++;
            }

            while (measurer.getPosition() < itr.getEndIndex()) {
                final TextLayout aTextLayout = computeNextLayout(measurer, availableWidth, nextLineBreakOffset);

                if (nextLineBreakOffset == measurer.getPosition()) {
                    nextLineBreakOffset = text.indexOf('\n', measurer.getPosition());
                    if (nextLineBreakOffset == -1) {
                        nextLineBreakOffset = Integer.MAX_VALUE;
                    }
                    else {
                        nextLineBreakOffset++;
                    }
                }

                linesList.add(aTextLayout);
                textHeight += aTextLayout.getAscent();
                textHeight += aTextLayout.getDescent() + aTextLayout.getLeading();
                textWidth = Math.max(textWidth, aTextLayout.getAdvance());
            }
        }

        lines = (TextLayout[]) linesList.toArray(EMPTY_TEXT_LAYOUT_ARRAY);

        if (constrainWidthToTextWidth || constrainHeightToTextHeight) {
            double newWidth = getWidth();
            double newHeight = getHeight();

            if (constrainWidthToTextWidth) {
                newWidth = textWidth;
            }

            if (constrainHeightToTextHeight) {
                newHeight = textHeight;
            }

            super.setBounds(getX(), getY(), newWidth, newHeight);
        }
    }

    /**
     * Compute the next layout using the specified line break measurer,
     * available width, and next line break offset.
     * 
     * @param lineBreakMeasurer line break measurer
     * @param availableWidth available width
     * @param nextLineBreakOffset next line break offset
     * @return the next layout computed using the specified line break measurer,
     *         available width, and next line break offset
     */
    protected TextLayout computeNextLayout(final LineBreakMeasurer lineBreakMeasurer, final float availableWidth,
            final int nextLineBreakOffset) {
        return lineBreakMeasurer.nextLayout(availableWidth, nextLineBreakOffset, false);
    }

    /**
     * Paint greek with the specified paint context.
     * 
     * @since 1.3
     * @param paintContext paint context
     */
    protected void paintGreek(final PPaintContext paintContext) {
        // empty
    }

    /**
     * Paint text with the specified paint context.
     * 
     * @since 1.3
     * @param paintContext paint context
     */
    protected void paintText(final PPaintContext paintContext) {
        final float x = (float) getX();
        float y = (float) getY();
        final float bottomY = (float) getHeight() + y;

        final Graphics2D g2 = paintContext.getGraphics();

        if (lines == null) {
            recomputeLayout();
            repaint();
            return;
        }

        g2.setPaint(textPaint);

        for (int i = 0; i < lines.length; i++) {
            final TextLayout tl = lines[i];
            y += tl.getAscent();

            if (bottomY < y) {
                return;
            }

            final float offset = (float) (getWidth() - tl.getAdvance()) * horizontalAlignment;

            tl.draw(g2, x + offset, y);

            y += tl.getDescent() + tl.getLeading();
        }
    }

    /** {@inheritDoc} */
    protected void paint(final PPaintContext paintContext) {
        super.paint(paintContext);
        if (textPaint == null) {
            return;
        }
        final float screenFontSize = getFont().getSize() * (float) paintContext.getScale();
        if (screenFontSize <= greekThreshold) {
            paintGreek(paintContext);
        }
        paintText(paintContext);
    }

    /** {@inheritDoc} */
    protected void internalUpdateBounds(final double x, final double y, final double width, final double height) {
        recomputeLayout();
    }
}
