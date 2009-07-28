/*
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * FIXME: so it's not necessary well-formed? Atts like <a href=unquoted /> are
 * ok?
 * 
 * @author Chris Malley (cmal...@pixelzoom.com)
 * @author Sam Reid
 * @author Allain Lalonde
 */
public class PHtml extends PNode {

    private static final long serialVersionUID = 1L;

    // FIXME: imagine <img alt="2>1" src="here comes the meat" />
    private static final Pattern tagPattern = Pattern.compile("</?[^>]+>");
    // FIXME: imagine <a href="where to go"
    // title="this is not the href='gotcha!' " />
    private static final Pattern linkPattern = Pattern.compile("<a .*href=(\\\"([^\\\"]*)\\\"|\\\'([^\\\"]*)\\\')");

    private static final Font DEFAULT_FONT = new JTextField().getFont();
    private static final Color DEFAULT_HTML_COLOR = Color.BLACK;

    /**
     * The property name that identifies a change of this node's font (see
     * {@link #getFont getFont}). Both old and new value will be set in any
     * property change event.
     * 
     * FIXME what's that?
     */
    public static final String PROPERTY_FONT = "font";
    // FIXME what's that?
    public static final int PROPERTY_CODE_FONT = 1 << 20;

    /**
     * The property name that identifies a change of this node's html (see
     * {@link #getHTML getHTML}). Both old and new value will be set in any
     * property change event. FIXME what's that?
     */
    public static final String PROPERTY_HTML = "html";
    // FIXME what's that?
    public static final int PROPERTY_CODE_HTML = 1 << 21;

    /**
     * The property name that identifies a change of this node's html color (see
     * {@link #getHtml getHTMLColor}). Both old and new value will be set in any
     * property change event. FIXME what's that?
     */
    public static final String PROPERTY_HTML_COLOR = "html color";
    // FIXME what's that?
    public static final int PROPERTY_CODE_HTML_COLOR = 1 << 22;

    private final JLabel htmlLabel;
    private View htmlView;
    private final Rectangle htmlBounds;

    public PHtml() {
        this(null, DEFAULT_FONT, DEFAULT_HTML_COLOR);
    }

    public PHtml(final String html) {
        this(html, DEFAULT_FONT, DEFAULT_HTML_COLOR);
    }

    public PHtml(final String html, final Color htmlColor) {
        this(html, DEFAULT_FONT, htmlColor);
    }

    public PHtml(final String html, final Font font, final Color htmlColor) {
        htmlLabel = new JLabel(html);
        htmlLabel.setFont(font);
        htmlLabel.setForeground(htmlColor);
        htmlBounds = new Rectangle();
        update();
    }

    /**
     * FIXME better name innerHtml as w3c does?
     * 
     * @return HTML being rendered by this node
     */
    public String getHTML() {
        return htmlLabel.getText();
    }

    /**
     * Changes the HTML being rendered by this node
     * 
     * FIXME better name innerHtml as w3c does?
     * 
     * @param newHtml
     */
    public void setHTML(final String newHtml) {
        if (isNewHtml(newHtml)) {
            final String oldHtml = htmlLabel.getText();
            htmlLabel.setText(newHtml);
            update();
            firePropertyChange(PROPERTY_CODE_HTML, PROPERTY_HTML, oldHtml, newHtml);
        }
    }

    private boolean isNewHtml(final String html) {
        // FIXME NPE if both are null - can this happen?
        return htmlLabel.getText() != null && html == null || htmlLabel.getText() == null && html != null
                || !htmlLabel.getText().equals(html);
    }

    /**
     * Gets the font.
     * 
     * @return the font
     */
    public Font getFont() {
        return htmlLabel.getFont();
    }

    /**
     * Set the font of this PHtml.
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
     * FIXME be more consistent - either always HTML or Html but don't mix.
     * 
     * @return the color used to render the HTML.
     */
    public Color getHTMLColor() {
        return htmlLabel.getForeground();
    }

    /**
     * Sets the color used to render the HTML. If you want to set the paint used
     * for the node, use setPaint.
     * 
     * @param newColor
     */
    public void setHTMLColor(final Color newColor) {
        final Color oldColor = htmlLabel.getForeground();
        htmlLabel.setForeground(newColor);
        repaint();
        firePropertyChange(PROPERTY_CODE_HTML_COLOR, PROPERTY_HTML_COLOR, oldColor, newColor);
    }

    /**
     * Applies all properties to the underlying JLabel, creates an htmlView and
     * updates bounds
     */
    private void update() {
        htmlLabel.setSize(htmlLabel.getPreferredSize());
        htmlView = BasicHTML.createHTMLView(htmlLabel, htmlLabel.getText() == null ? "" : htmlLabel.getText());

        final Rectangle2D bounds = getBounds();
        htmlBounds.setRect(0, 0, bounds.getWidth(), bounds.getHeight());
        repaint();
    }

    public boolean setBounds(final double x, final double y, final double width, final double height) {
        final boolean boundsChanged = super.setBounds(x, y, width, height);
        update();
        return boundsChanged;
    }

    public boolean setBounds(final Rectangle2D newBounds) {
        final boolean boundsChanged = super.setBounds(newBounds);
        update();
        return boundsChanged;
    }

    /**
     * Paints the node. The HTML string is painted last, so it appears on top of
     * any child nodes.
     * 
     * @param paintContext
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
     * FIXME this method looks shaky - can you refactor to get it into a test
     * harness?
     * 
     * @param clickedPoint
     * @return String containing value of href for clicked link, or null if no
     *         link clicked
     */
    public String getClickedAddress(final Point2D clickedPoint) {
        int position = pointToModelIndex(clickedPoint);

        final Matcher tagMatcher = tagPattern.matcher(htmlLabel.getText());

        String address = null;

        while (tagMatcher.find()) {
            if (position <= tagMatcher.start()) {
                break;
            }
            position += tagMatcher.end() - tagMatcher.start();

            final String tag = tagMatcher.group().toLowerCase();
            if ("</a>".equals(tag)) {
                address = null;
            }
            else {
                final Matcher linkMatcher = linkPattern.matcher(tag);
                if (linkMatcher.find()) {
                    address = linkMatcher.group(2);
                    if (address == null) {
                        address = linkMatcher.group(3);
                    }
                }
            }
        }

        return address;
    }

    private int pointToModelIndex(final Point2D clickedPoint) {
        final Position.Bias[] biasReturn = new Position.Bias[1];
        return htmlView.viewToModel((float) clickedPoint.getX(), (float) clickedPoint.getY(), getBounds(), biasReturn);
    }
}