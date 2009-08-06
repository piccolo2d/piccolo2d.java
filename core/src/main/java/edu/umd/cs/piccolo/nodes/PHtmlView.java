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
package edu.umd.cs.piccolo.nodes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.Position;
import javax.swing.text.View;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * PHtml is a Piccolo node for rendering HTML text. It uses a JLabel under the
 * hood so you have the same restrictions regarding html as you have when using
 * standard Swing components (HTML 3.2 + subset of CSS 1.0).
 * 
 * @author Chris Malley (cmal...@pixelzoom.com)
 * @author Sam Reid
 * @author Allain Lalonde
 */
public class PHtmlView extends PNode {

    private static final long serialVersionUID = 1L;

    /** Default font to use if not overridden in the HTML markup. */
    private static final Font DEFAULT_FONT = new JTextField().getFont();

    /** Default font color to use if not overridden in the HTML markup. */
    private static final Color DEFAULT_HTML_COLOR = Color.BLACK;

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
     * The property name that identifies a change of this node's HTML (see
     * {@link #getHTML getHTML}). Both old and new value will be set in any
     * property change event.
     */
    public static final String PROPERTY_HTML = "html";

    /**
     * The property code that identifies a change of this node's HTML (see
     * {@link #getHTML getHTML}). Both old and new value will be set in any
     * property change event.
     */
    public static final int PROPERTY_CODE_HTML = 1 << 21;

    /**
     * The property name that identifies a change of this node's HTML color (see
     * {@link #getHtml getHTMLColor}). Both old and new value will be set in any
     * property change event.
     */
    public static final String PROPERTY_HTML_COLOR = "html color";

    /**
     * The property code that identifies a change of this node's HTML color (see
     * {@link #getHtml getHTMLColor}). Both old and new value will be set in any
     * property change event.
     */
    public static final int PROPERTY_CODE_HTML_COLOR = 1 << 22;

    /** Underlying JLabel used to handle the rendering logic. */
    private final JLabel htmlLabel;

    /** Object that encapsulates the HTML rendering logic. */
    private View htmlView;

    /** Used to enforce bounds and wrapping on the HTML. */
    private final Rectangle htmlBounds;

    /**
     * Creates an empty PHtml node with default font and color.
     */
    public PHtmlView() {
        this(null, DEFAULT_FONT, DEFAULT_HTML_COLOR);
    }

    /**
     * Creates a PHtml node that contains the provided HTML. It will have
     * default font and color.
     * 
     * @param html markup label should contain
     */
    public PHtmlView(final String html) {
        this(html, DEFAULT_FONT, DEFAULT_HTML_COLOR);
    }

    /**
     * Creates a PHtml node with the given markup and default html color.
     * 
     * @param html markup label should contain
     * @param htmlColor color that will be used unless overridden by the markup
     */
    public PHtmlView(final String html, final Color htmlColor) {
        this(html, DEFAULT_FONT, htmlColor);
    }

    /**
     * Creates a PHtml node with the given markup and default HTML color.
     * 
     * @param html markup label should contain
     * @param font font that will be used unless overriden by the markup
     * @param htmlColor color that will be used unless overridden by the markup
     */
    public PHtmlView(final String html, final Font font, final Color htmlColor) {
        htmlLabel = new JLabel(html);
        htmlLabel.setFont(font);
        htmlLabel.setForeground(htmlColor);
        htmlBounds = new Rectangle();
        update();
    }

    /**
     * @return HTML being rendered by this node
     */
    public String getHtml() {
        return htmlLabel.getText();
    }

    /**
     * Changes the HTML being rendered by this node.
     * 
     * @param newHtml markup to swap with existing HTML
     */
    public void setHtml(final String newHtml) {
        if (isNewHtml(newHtml)) {
            final String oldHtml = htmlLabel.getText();
            htmlLabel.setText(newHtml);
            update();
            firePropertyChange(PROPERTY_CODE_HTML, PROPERTY_HTML, oldHtml, newHtml);
        }
    }

    private boolean isNewHtml(final String html) {
        if (html == null && getHtml() == null) {
            return false;
        }
        else if (html == null || getHtml() == null) {
            return true;
        }
        else {
            return !htmlLabel.getText().equals(html);
        }
    }

    /**
     * Returns the default font being used when not overridden in the markup.
     * 
     * @return font being used when not overridden by the markup
     */
    public Font getFont() {
        return htmlLabel.getFont();
    }

    /**
     * Set the font of this PHtml. This may be overridden by the markup using
     * either styles or the font tag.
     * 
     * @param newFont font to set as the default
     */
    public void setFont(final Font newFont) {
        final Font oldFont = htmlLabel.getFont();
        htmlLabel.setFont(newFont);
        update();

        firePropertyChange(PROPERTY_CODE_FONT, PROPERTY_FONT, oldFont, newFont);
    }

    /**
     * Gets the color used to render the HTML. If you want to get the paint used
     * for the node, use getPaint.
     * 
     * @return the color used to render the HTML.
     */
    public Color getHtmlColor() {
        return htmlLabel.getForeground();
    }

    /**
     * Sets the color used to render the HTML. If you want to set the paint used
     * for the node, use setPaint.
     * 
     * @param newColor new color to use when rendering HTML
     */
    public void setHtmlColor(final Color newColor) {
        final Color oldColor = htmlLabel.getForeground();
        htmlLabel.setForeground(newColor);
        repaint();
        firePropertyChange(PROPERTY_CODE_HTML_COLOR, PROPERTY_HTML_COLOR, oldColor, newColor);
    }

    /**
     * Applies all properties to the underlying JLabel, creates an htmlView and
     * updates bounds.
     */
    private void update() {
        htmlLabel.setSize(htmlLabel.getPreferredSize());

        String htmlContent = htmlLabel.getText();
        if (htmlContent == null) {
            htmlContent = "";
        }

        htmlView = BasicHTML.createHTMLView(htmlLabel, htmlContent);

        final Rectangle2D bounds = getBounds();
        htmlBounds.setRect(0, 0, bounds.getWidth(), bounds.getHeight());
        repaint();
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
     * Paints the node. The HTML string is painted last, so it appears on top of
     * any child nodes.
     * 
     * @param paintContext the context in which painting is occurring
     */
    protected void paint(final PPaintContext paintContext) {
        super.paint(paintContext);

        if (htmlLabel.getWidth() != 0 && htmlLabel.getHeight() != 0) {
            final Graphics2D g2 = paintContext.getGraphics();

            htmlView.paint(g2, htmlBounds);
        }
    }

    /**
     * Returns the address specified in the link under the given point.
     * 
     * @param clickedPoint point under which a link might be
     * @return String containing value of href for clicked link, or null if no
     *         link clicked
     */
    public String getClickedAddress(final Point2D clickedPoint) {
        return getClickedAddress(clickedPoint.getX(), clickedPoint.getY());
    }

    /**
     * Returns the address specified in the link under the given point.
     * 
     * @param x x component of point under which link may be
     * @param y y component of point under which link may be
     * @return String containing value of href for clicked link, or null if no
     *         link clicked
     */
    public String getClickedAddress(final double x, final double y) {
        int position = pointToModelIndex(x, y);

        final String html = htmlLabel.getText();

        String address = null;

        int currentPos = 0;
        while (currentPos < html.length()) {
            currentPos = html.indexOf('<', currentPos);
            if (currentPos == -1 || position < currentPos) {
                break;
            }

            final int tagStart = currentPos;
            final int tagEnd = findTagEnd(html, currentPos);

            if (tagEnd == -1) {
                return null;
            }

            currentPos = tagEnd + 1;

            final String tag = html.substring(tagStart, currentPos);

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
     * Returns the index into the raw text (without HTML) that the click
     * occurred.
     * 
     * @param x x component of the point clicked
     * @param y y component of the point clicked
     * @return index into the raw text (without HTML) that the click occurred
     */
    private int pointToModelIndex(final double x, final double y) {
        final Position.Bias[] biasReturn = new Position.Bias[1];
        return htmlView.viewToModel((float) x, (float) y, getBounds(), biasReturn);
    }

    /**
     * Starting from the startPos, it finds the position at which the given tag
     * ends. Returns -1 if the end of the string was encountered before the end
     * of the tag was encountered.
     * 
     * @param html raw HTML string being searched
     * @param startPos where in the string to start searching for ">"
     * @return index after the ">" character
     */
    private int findTagEnd(final String html, final int startPos) {
        int currentPos = startPos;

        currentPos++;

        while (currentPos > 0 && currentPos < html.length() && html.charAt(currentPos) != '>') {
            if (html.charAt(currentPos) == '\"') {
                currentPos = html.indexOf('\"', currentPos + 1);
            }
            else if (html.charAt(currentPos) == '\'') {
                currentPos = html.indexOf('\'', currentPos + 1);
            }
            currentPos++;
        }

        if (currentPos == 0 || currentPos >= html.length()) {
            return -1;
        }

        return currentPos + 1;
    }

    /**
     * Given a tag, extracts the value of the href attribute, returns null if
     * none was found.
     * 
     * @param tag from which to extract the href value
     * @return href value without quotes or null if not found
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
     * @param tag
     * @param startPos
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
     * 
     * @return true if to left of assignment is href
     */
    private boolean isHrefAttributeAssignment(final String tag, final int equalPos) {
        return tag.charAt(equalPos) == '=' && equalPos > 4 && " href".equals(tag.substring(equalPos - 5, equalPos));
    }
}