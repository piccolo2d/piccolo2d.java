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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.Position;
import javax.swing.text.View;

import org.piccolo2d.PNode;
import org.piccolo2d.util.PPaintContext;


/**
 * PHtmlView is a Piccolo node for rendering HTML text. It uses a JLabel under
 * the hood so you have the same restrictions regarding HTML as you have when
 * using standard Swing components (HTML 3.2 + subset of CSS 1.0).
 * 
 * @since 1.3
 * @author Chris Malley (cmal...@pixelzoom.com)
 * @author Sam Reid
 * @author Allain Lalonde
 */
public class PHtmlView extends PNode {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Default font if not otherwise specified in the HTML text, 12 point
     * <code>"SansSerif"</code>.
     */
    // public static final Font DEFAULT_FONT = new Font(Font.SANS_SERIF,
    // Font.PLAIN, 12); jdk 1.6+
    public static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 12);

    /**
     * Default text color if not otherwise specified in the HTML text,
     * <code>Color.BLACK</code>.
     */
    public static final Color DEFAULT_TEXT_COLOR = Color.BLACK;

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
     * The property name that identifies a change of this node's HTML text (see
     * {@link #getText getText}). Both old and new value will be set in any
     * property change event.
     */
    public static final String PROPERTY_TEXT = "text";

    /**
     * The property code that identifies a change of this node's HTML text (see
     * {@link #getText getText}). Both old and new value will be set in any
     * property change event.
     */
    public static final int PROPERTY_CODE_TEXT = 1 << 21;

    /**
     * The property name that identifies a change of this node's HTML text color
     * (see {@link #getTextColor getTextColor}). Both old and new value will be set
     * in any property change event.
     */
    public static final String PROPERTY_TEXT_COLOR = "text color";

    /**
     * The property code that identifies a change of this node's HTML text color
     * (see {@link #getTextColor getTextColor}). Both old and new value will be set
     * in any property change event.
     */
    public static final int PROPERTY_CODE_TEXT_COLOR = 1 << 22;

    /** Underlying JLabel used to handle the rendering logic. */
    private final JLabel label;

    /** Object that encapsulates the HTML rendering logic. */
    private transient View htmlView;

    /**
     * Create an empty HTML text node with the default font and text color.
     */
    public PHtmlView() {
        this(null, DEFAULT_FONT, DEFAULT_TEXT_COLOR);
    }

    /**
     * Create a HTML text node with the specified HTML text and the default font
     * and text color.
     * 
     * @param text HTML text for this HTML text node
     */
    public PHtmlView(final String text) {
        this(text, DEFAULT_FONT, DEFAULT_TEXT_COLOR);
    }

    /**
     * Create a HTML text node with the specified HTML text, font, and text
     * color. The font and text color are used to render the HTML text if not
     * otherwise specified via CSS.
     * 
     * @param text HTML text for this HTML text node
     * @param font font for this HTML text node
     * @param textColor text color for this HTML text node
     */
    public PHtmlView(final String text, final Font font, final Color textColor) {
        label = new JLabel(text);
        label.setFont(font);
        label.setForeground(textColor);
        super.setBounds(0, 0, label.getPreferredSize().getWidth(), label.getPreferredSize().getHeight());
        update();
    }

    /**
     * Return the HTML text for this HTML text node.
     * 
     * @return the HTML text for this HTML text node
     */
    public String getText() {
        return label.getText();
    }

    /**
     * Set the HTML text for this HTML text node to <code>text</code>.
     * 
     * <p>
     * This is a <b>bound</b> property.
     * </p>
     * 
     * @param text HTML text for this HTML text node
     */
    public void setText(final String text) {
        final String oldText = label.getText();

        if (oldText == null && text == null) {
            return;
        }

        if (oldText == null || !oldText.equals(text)) {
            label.setText(text);

            update();
            firePropertyChange(PROPERTY_CODE_TEXT, PROPERTY_TEXT, oldText, label.getText());
        }
    }

    /**
     * Return the font for this HTML text node. This font is used to render the
     * HTML text if not otherwise specified via CSS. Defaults to
     * {@link #DEFAULT_FONT}.
     * 
     * @return the font for this HTML text node
     */
    public Font getFont() {
        return label.getFont();
    }

    /**
     * Set the font for this HTML text node to <code>font</code>. This font is
     * used to render the HTML text if not otherwise specified via CSS.
     * 
     * <p>
     * This is a <b>bound</b> property.
     * </p>
     * 
     * @param font font for this HTML text node
     */
    public void setFont(final Font font) {
        final Font oldFont = label.getFont();
        label.setFont(font);
        update();
        firePropertyChange(PROPERTY_CODE_FONT, PROPERTY_FONT, oldFont, label.getFont());
    }

    /**
     * Return the text color for this HTML text node. This text color is used to
     * render the HTML text if not otherwise specified via CSS. Defaults to
     * {@link #DEFAULT_TEXT_COLOR}.
     * 
     * @return the text color for this HTML text node
     */
    public Color getTextColor() {
        return label.getForeground();
    }

    /**
     * Set the text color for this HTML text node to <code>textColor</code>.
     * This text color is used to render the HTML text if not otherwise
     * specified via CSS.
     * 
     * This is a <b>bound</b> property.
     * 
     * @param textColor text color for this HTML text node
     */
    public void setTextColor(final Color textColor) {
        final Color oldColor = label.getForeground();
        label.setForeground(textColor);
        repaint();
        firePropertyChange(PROPERTY_CODE_TEXT_COLOR, PROPERTY_TEXT_COLOR, oldColor, label.getForeground());
    }

    /**
     * Applies all properties to the underlying JLabel, creates an htmlView and
     * updates bounds.
     */
    private void update() {
        String htmlContent = label.getText();
        if (htmlContent == null) {
            htmlContent = "";
        }

        htmlView = BasicHTML.createHTMLView(label, htmlContent);
        fitHeightToHtmlContent();

        repaint();
    }

    /**
     * Resizes the height to be as tall as its rendered html. Takes wrapping
     * into account.
     */
    private void fitHeightToHtmlContent() {
        if (getWidth() > 0) {
            htmlView.setSize((float) getWidth(), 0f);

            float wrapHeight = htmlView.getPreferredSpan(View.Y_AXIS);
            label.setSize(new Dimension((int) getWidth(), (int) wrapHeight));

            if (getHeight() < wrapHeight) {
                System.out.println(getHeight());
                System.out.println(wrapHeight);
                super.setBounds(getX(), getY(), getWidth(), wrapHeight);
            }
        }
    }

    /** {@inheritDoc} */
    public boolean setBounds(final double x, final double y, final double width, final double height) {
        final boolean boundsChanged = super.setBounds(x, y, width, height);
        update();
        return boundsChanged;
    }

    /** {@inheritDoc} */
    public boolean setBounds(final Rectangle2D newBounds) {
        final boolean boundsChanged = super.setBounds(newBounds);
        update();
        return boundsChanged;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * The HTML text is painted last, so it will appear on top of any child
     * nodes.
     * </p>
     */
    protected void paint(final PPaintContext paintContext) {
        super.paint(paintContext);
        paintContext.pushClip(getBounds());
        final Graphics2D g2 = paintContext.getGraphics();
        htmlView.paint(g2, getBounds().getBounds());
        paintContext.popClip(getBounds());
    }

    /**
     * Return the address specified in the HTML link at the specified point in
     * this node's local coordinate system, if any.
     * 
     * @param point point in this node's local coordinate system
     * @return the address specified in the HTML link at the specified point in
     *         this node's local coordinate system, or <code>null</code> if no
     *         such HTML link exists
     */
    public String getLinkAddressAt(final Point2D point) {
        return getLinkAddressAt(point.getX(), point.getY());
    }

    /**
     * Return the address specified in the HTML link at the specified x and y
     * coordinates in this node's local coordinate system, if any.
     * 
     * @param x x coordinate in this node's local coordinate system
     * @param y y coordinate in this node's local coordinate system
     * @return the address specified in the HTML link at the specified x and y
     *         coordinates in this node's local coordinate system, or
     *         <code>null</code> if no such HTML link exists
     */
    public String getLinkAddressAt(final double x, final double y) {
        int position = pointToModelIndex(x, y);

        final String text = label.getText();

        String address = null;

        int currentPos = 0;
        while (currentPos < text.length()) {
            currentPos = text.indexOf('<', currentPos);
            if (currentPos == -1 || position < currentPos) {
                break;
            }

            final int tagStart = currentPos;
            final int tagEnd = findTagEnd(text, currentPos);

            if (tagEnd == -1) {
                return null;
            }

            currentPos = tagEnd + 1;

            final String tag = text.substring(tagStart, currentPos);

            position += tag.length();

            if ("</a>".equals(tag)) {
                address = null;
            }
            else if (tag.startsWith("<a ")) {
                address = extractHref(tag);
            }
        }

        return address;
    }

    /**
     * Return the index into the raw text at the specified x and y coordinates
     * in this node's local coordinate system.
     * 
     * @param x x coordinate in this node's local coordinate system
     * @param y y coordinate in this node's local coordinate system
     * @return the index into the raw text at the specified x and y coordinates
     *         in this node's local coordinate system
     */
    private int pointToModelIndex(final double x, final double y) {
        final Position.Bias[] biasReturn = new Position.Bias[1];
        return htmlView.viewToModel((float) x, (float) y, getBounds(), biasReturn);
    }

    /**
     * Starting from the startPos, find the position at which the given tag
     * ends. Returns <code>-1</code> if the end of the string was encountered
     * before the end of the tag was encountered.
     * 
     * @param text HTML text being searched
     * @param startPos where in the string to start searching for ">"
     * @return index after the ">" character
     */
    private int findTagEnd(final String text, final int startPos) {
        int currentPos = startPos;

        currentPos++;

        while (currentPos > 0 && currentPos < text.length() && text.charAt(currentPos) != '>') {
            if (text.charAt(currentPos) == '\"') {
                currentPos = text.indexOf('\"', currentPos + 1);
            }
            else if (text.charAt(currentPos) == '\'') {
                currentPos = text.indexOf('\'', currentPos + 1);
            }
            currentPos++;
        }

        if (currentPos == 0 || currentPos >= text.length()) {
            return -1;
        }

        return currentPos + 1;
    }

    /**
     * Given a tag, extracts the value of the href attribute or returns null if
     * none was found.
     * 
     * @param tag from which to extract the href value
     * @return href value without quotes or <code>null</code> if not found
     */
    private String extractHref(final String tag) {
        int currentPos = 0;

        final String href = null;

        while (currentPos >= 0 && currentPos < tag.length() - 1) {
            currentPos = tag.indexOf('=', currentPos + 1);
            if (currentPos != -1 && isHrefAttributeAssignment(tag, currentPos)) {
                return extractHrefValue(tag, currentPos + 1);
            }
        }
        return href;
    }

    /**
     * Starting at the character after the equal sign of an href=..., it extract
     * the value. Handles single, double, and no quotes.
     * 
     * @param tag tag
     * @param startPos start position
     * @return value of href or null if not found.
     */
    private String extractHrefValue(final String tag, final int startPos) {
        int currentPos = startPos;

        if (tag.charAt(currentPos) == '\"') {
            final int startHref = currentPos + 1;
            currentPos = tag.indexOf('\"', startHref);
            if (currentPos == -1) {
                return null;
            }
            return tag.substring(startHref, currentPos);
        }
        else if (currentPos < tag.length() && tag.charAt(currentPos) == '\'') {
            final int startHref = currentPos + 1;
            currentPos = tag.indexOf('\'', startHref);
            if (currentPos == -1) {
                return null;
            }
            return tag.substring(startHref, currentPos);
        }
        else {
            final int startHref = currentPos;

            if (currentPos < tag.length()) {
                do {
                    currentPos++;
                } while (currentPos < tag.length() && tag.charAt(currentPos) != ' ' && tag.charAt(currentPos) != '>');
            }
            return tag.substring(startHref, currentPos);
        }
    }

    /**
     * Given the position in a string returns whether it points to the equal
     * sign of an href attribute.
     * 
     * @param tag html code of the tag
     * @param equalPos the index of the assignment
     * @return true if to left of assignment is href
     */
    private boolean isHrefAttributeAssignment(final String tag, final int equalPos) {
        return tag.charAt(equalPos) == '=' && equalPos > 4 && " href".equals(tag.substring(equalPos - 5, equalPos));
    }
}