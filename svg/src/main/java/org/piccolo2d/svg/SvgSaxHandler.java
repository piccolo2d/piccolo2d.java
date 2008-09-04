/*
 * Copyright (c) 2008, Piccolo2D project, http://piccolo2d.org
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
 * None of the name of the Piccolo2D project, the University of Maryland, or the names of its contributors
 * may be used to endorse or promote products derived from this software without specific prior written
 * permission.
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
package org.piccolo2d.svg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.piccolo2d.svg.css.CssManager;
import org.piccolo2d.svg.css.CssManager.Style;
import org.piccolo2d.svg.util.FontUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Sax parser for svg.
 * 
 * @see SvgLoader
 * @author mr0738@mro.name
 */
class SvgSaxHandler extends DefaultHandler {
    /**
     * <a href="http://www.w3.org/TR/SVG11/shapes.html#CircleElement">svg
     * circle</a>
     * 
     * @see Arc2D.Double#Double(double, double, double, double, double, double,
     *      int)
     */
    private final class XmlCircle extends XmlPPath {
        PNode create(final CharSequence xpath, final CharSequence localName, final Style style, final Attributes unused)
                throws ParseException {
            final double r = css.getNumber(style, "r", 0.0);
            final double cx = css.getNumber(style, "cx", 0.0);
            final double cy = css.getNumber(style, "cy", 0.0);
            return new PPath(new Arc2D.Double(cx - r, cy - r, 2 * r, 2 * r, 0, 360, Arc2D.CHORD));
        }

        PNode style(final PNode node, final Style style) throws ParseException {
            super.style(node, style);
            node.setVisible(css.getNumber(style, "r", 0.0) > 0);
            return node;
        }
    }

    private final class XmlDefs extends XmlHeadElement {
        void end() throws SAXException {
            current = root;
        }

        boolean start(final CharSequence xpath, final CharSequence localName, final Style style, final Attributes atts)
                throws SAXException {
            current = new PNode();
            return true;
        }
    }

    private abstract class XmlElement {

        void end() throws SAXException {
        };

        /**
         * @param xpath TODO
         * @param localName TODO
         * @param style css style + class + inherited + xml attributes
         *            (localnames)
         * @param atts the raw xml attributes (incl. preserved)
         * @return was a new PNode created?
         */
        boolean start(final CharSequence xpath, final CharSequence localName, final Style style, final Attributes atts)
                throws SAXException {
            return false;
        };
    }

    /**
     * <a href="http://www.w3.org/TR/SVG11/shapes.html#EllipseElement">svg
     * ellipse</a>
     * 
     * @see Arc2D.Double#Double(double, double, double, double, double, double,
     *      int)
     */
    private final class XmlEllipse extends XmlPPath {
        PNode create(final CharSequence xpath, final CharSequence localName, final Style style, final Attributes unused)
                throws ParseException {
            final double rx = css.getNumber(style, "rx", 0.0);
            final double ry = css.getNumber(style, "ry", 0.0);
            final double cx = css.getNumber(style, "cx", 0.0);
            final double cy = css.getNumber(style, "cy", 0.0);
            return new PPath(new Arc2D.Double(cx - rx, cy - ry, 2 * rx, 2 * ry, 0, 360, Arc2D.CHORD));
        }

        PNode style(final PNode node, final Style style) throws ParseException {
            super.style(node, style);
            node.setVisible(css.getNumber(style, "rx", 0.0) > 0 && css.getNumber(style, "ry", 0.0) > 0);
            return node;
        }
    }

    private abstract class XmlGeneralPath extends XmlPPath {
    }

    /**
     * <a href="http://www.w3.org/TR/SVG11/struct.html#Groups">svg g</a>
     */
    private class XmlGroup extends XmlPNode {
        PNode create(final CharSequence xpath, final CharSequence localName, final Style style, final Attributes atts)
                throws ParseException {
            return new PNode();
        }
    }

    private abstract class XmlHeadElement extends XmlElement {
    }

    private final class XmlIgnore extends XmlHeadElement {
    }

    /**
     * <a href="http://www.w3.org/TR/SVG11/shapes.html#LineElement">svg line</a>
     * 
     * @see Line2D.Double#Double(double, double, double, double)
     */
    private final class XmlLine extends XmlPPath {
        PNode create(final CharSequence xpath, final CharSequence localName, final Style style, final Attributes unused)
                throws ParseException {
            final double x1 = css.getNumber(style, "x1", 0.0);
            final double y1 = css.getNumber(style, "y1", 0.0);
            final double x2 = css.getNumber(style, "x2", 0.0);
            final double y2 = css.getNumber(style, "y2", 0.0);
            return new PPath(new Line2D.Double(x1, y1, x2, y2));
        }
    }

    /**
     * <a href="http://www.w3.org/TR/SVG11/paths.html">svg path</a>
     */
    private final class XmlPath extends XmlGeneralPath {
        PNode create(final CharSequence xpath, final CharSequence localName, final Style style, final Attributes unused)
                throws ParseException {
            return new PPath(path.parse(find(style, "d")));
        }
    }

    private abstract class XmlPNode extends XmlElement {

        /**
         * Factory without look!
         * 
         * @param xpath TODO
         * @param localName TODO
         * 
         * @see #style
         */
        abstract PNode create(CharSequence xpath, CharSequence localName, Style style, Attributes atts)
                throws ParseException;

        void end() throws SAXException {
            super.end();
            current = current.getParent();
        }

        final boolean start(final CharSequence xpath, final CharSequence localName, final Style style,
                final Attributes atts) throws SAXException {
            try {
                final PNode child = style(create(xpath, localName, style, atts), style);
                child.addAttribute(P2DElement, localName);
                child.addAttribute(P2DXPath, xpath);
                child.addAttribute(P2DClass, find(atts, "class"));
                child.addAttribute(P2DStyle, find(atts, "style"));
                current.addChild(child);
                current = child;
                return true;
            }
            catch (final ParseException e) {
                throw new SAXException(e);
            }
        }

        /** apply the look */
        PNode style(final PNode n, final Style style) throws ParseException {
            final CharSequence tr = find(style, "transform");
            if (tr != null) {
                trafo.parse(tr, n.getTransformReference(true));
            }
            return n;
        }
    }

    /**
     * <a href="http://www.w3.org/TR/SVG11/shapes.html#PolygonElement">svg
     * polylgon</a>
     */
    private final class XmlPolygon extends XmlPolyline {
        PNode create(final CharSequence xpath, final CharSequence localName, final Style style, final Attributes atts)
                throws ParseException {
            final GeneralPath p = point.parse(find(style, "points"));
            p.closePath();
            return new PPath(p);
        }
    }

    /**
     * <a href="http://www.w3.org/TR/SVG11/shapes.html#PolylineElement">svg
     * polyline</a>
     */
    private class XmlPolyline extends XmlGeneralPath {
        PNode create(final CharSequence xpath, final CharSequence localName, final Style style, final Attributes atts)
                throws ParseException {
            return new PPath(point.parse(find(style, "points")));
        }
    }

    private abstract class XmlPPath extends XmlPNode {
        PNode style(final PNode n, final Style style) throws ParseException {
            final PPath node = (PPath) super.style(n, style);
            // http://www.w3.org/TR/SVG11/painting.html#paint-att-mod
            node.setPaint(css.getColor(style, "fill"));

            // TODO stroke type
            final float t = css.getNumber(style, "stroke-width", -1.0F);
            if (t >= 0) {
                node.setStroke(new BasicStroke(t));
            }
            node.setStrokePaint(css.getColor(style, "stroke"));

            // set drawing attributes

            // TODO styling attributes http://www.w3.org/TR/SVG11/styling.html

            return node;
        }
    }

    /**
     * http://www.w3.org/TR/SVG11/text.html#TextElement
     * 
     * @see Font
     * @author mr0738@mro.name
     */
    private final class XmlPText extends XmlPNode {
        PNode create(final CharSequence xpath, final CharSequence localName, final Style style, final Attributes atts)
                throws ParseException {
            return new PText();
        }

        void end() throws SAXException {
            final PText node = (PText) current;
            node.setText(txt.toString().trim());

            // TODO justification!
            if ("middle".equals(node.getAttribute(P2D_Prefix + "text-anchor"))) {
                // node.setJustification(JLabel.CENTER_ALIGNMENT);
                node.translate(-0.5 * node.getWidth(), -0.5 * node.getHeight());
            }
            else {
                node.translate(0, -node.getHeight());
            }

            super.end();
        }

        /** apply the look */
        PNode style(final PNode n, final Style style) throws ParseException {
            final PText node = (PText) super.style(n, style);
            node.setConstrainWidthToTextWidth(true);
            node.setConstrainHeightToTextHeight(true);

            // http://www.w3.org/TR/SVG11/painting.html#paint-att-mod
            final Color c = css.getColor(style, "fill");
            node.setTextPaint(c == null ? Color.BLACK : c);

            final double x = css.getNumber(style, "x", 0.0);
            final double y = css.getNumber(style, "y", 0.0);
            node.translate(x, y);

            // TODO text attributes!
            node.addAttribute(P2D_Prefix + "text-anchor", css.getString(style, "text-anchor"));

            node.scale(1.0 / FontUtil.SCALE);
            node.setFont(css.getFont(style));

            return node;
        }
    }

    /**
     * <a href="http://www.w3.org/TR/SVG11/shapes.html#RectElement">svg rect</a>
     * 
     * @see Rectangle2D.Double#Double(double, double, double, double)
     * @see RoundRectangle2D.Double#Double(double, double, double, double,
     *      double, double)
     */
    private final class XmlRect extends XmlPPath {
        PNode create(final CharSequence xpath, final CharSequence localName, final Style style, final Attributes atts)
                throws ParseException {
            final double x = css.getNumber(style, "x", 0.0);
            final double y = css.getNumber(style, "y", 0.0);
            final double w = css.getNumber(style, "width", 0.0);
            final double h = css.getNumber(style, "height", 0.0);
            final double rx = css.getNumber(style, "rx", 0.0);
            final double ry = css.getNumber(style, "ry", rx);
            final Shape s;
            if (rx > 0 || ry > 0) {
                s = new RoundRectangle2D.Double(x, y, w, h, rx, ry);
            }
            else {
                s = new Rectangle2D.Double(x, y, w, h);
            }
            return new PPath(s);
        }
    }

    /**
     * TODO allow external (classpath) stylesheets
     */
    private final class XmlStyle extends XmlHeadElement {
        void end() throws SAXException {
            try {
                // TODO check for src attribute and load style.

                css.loadStyleSheet(txt);
            }
            catch (final ParseException e) {
                throw new SAXException(e);
            }
        }
    }

    /**
     * http://www.w3.org/TR/SVG11/struct.html#UseElement
     */
    private final class XmlUse extends XmlUseBase {
        protected PNode deepClone(final CharSequence xpath, final CharSequence localName, final Style sty,
                final PNode src) {
            final String elem = (String) src.getAttribute(P2DElement);
            final XmlPNode handl = (XmlPNode) handler.get(elem);
            try {
                final PNode dst = shallowClone(src);
                handl.style(dst, sty);
                dst.setVisible(true);
                // deep recursion
                for (int i = 0; i < src.getChildrenCount(); i++) {
                    final PNode fromc = src.getChild(i);
                    final CharSequence el = (CharSequence) fromc.getAttribute(P2DElement);
                    final CharSequence tocp = appendXPath(xpath, el, (CharSequence) fromc.getAttribute(P2DClass));
                    System.out.println(tocp);
                    Style s = css.findStyleByXPath(tocp, (CharSequence) fromc.getAttribute(P2DStyle));
                    s = css.merge(sty, s);
                    dst.addChild(deepClone(tocp, el, s, fromc));
                }
                return dst;
            }
            catch (final ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * http://www.w3.org/TR/SVG11/struct.html#UseElement
     */
    private final class XmlUse0 extends XmlUseBase {
        protected PNode deepClone(final CharSequence xpath, final CharSequence localName, final Style sty, final PNode n) {
            // this is terribly slow:
            return stripId((PNode) n.clone());
        }
    }

    /**
     * http://www.w3.org/TR/SVG11/struct.html#UseElement
     */
    private abstract class XmlUseBase extends XmlGroup {
        PNode create(final CharSequence xpath, final CharSequence localName, final Style style, final Attributes atts)
                throws ParseException {
            final PNode r = super.create(xpath, localName, style, atts);
            final String href = atts.getValue(xlinkNS, "href");
            if (href == null) {
                throw new IllegalArgumentException("<use> element without xlink:href");
            }
            final Matcher m = anchor.matcher(href);
            if (!m.matches()) {
                throw new IllegalArgumentException("xlink:href [" + href + "] does not match [" + anchor.pattern()
                        + "]");
            }

            final PNode used = (PNode) id2node.get(m.group(1));
            r.addChild(deepClone(xpath, localName, style, used));

            final double x = css.getNumber(style, "x", 0.0);
            final double y = css.getNumber(style, "y", 0.0);
            // r.setTransform(AffineTransform.getTranslateInstance(x, y));
            r.translate(x, y);
            return r;
        }

        protected abstract PNode deepClone(final CharSequence xpat, CharSequence localName, final Style sty,
                final PNode n);

        protected PNode shallowClone(final PNode node) {
            final PNode ret;
            if (PNode.class.equals(node.getClass())) {
                ret = new PNode();
            }
            else if (PPath.class.equals(node.getClass())) {
                final PPath n = (PPath) node;
                final PPath r = new PPath(n.getPathReference());
                r.setStroke(n.getStroke());
                r.setStrokePaint(n.getStrokePaint());
                ret = r;
            }
            else if (PText.class.equals(node.getClass())) {
                final PText n = (PText) node;
                final PText r = new PText(n.getText());
                r.setTextPaint(n.getTextPaint());
                ret = r;
            }
            else {
                throw new IllegalStateException(node.getClass().getName());
            }
            ret.setTransform(node.getTransformReference(false));
            ret.setPaint(node.getPaint());
            ret.setTransparency(node.getTransparency());
            ret.setVisible(node.getVisible());
            // ret.setBounds(node.getBoundsReference());
            for (final Enumeration enu = node.getClientPropertyKeysEnumeration(); enu.hasMoreElements();) {
                final Object key = enu.nextElement();
                if (!P2DID.equals(key)) {
                    System.out.println("\t" + key + "=" + node.getAttribute(key));
                    ret.addAttribute(key, node.getAttribute(key));
                }
            }
            System.out.println();
            return ret;
        }
    }

    private static final Pattern anchor = Pattern.compile("^#(.+)$");
    private static final Pattern clzSplit = Pattern.compile("\\s+");
    private static final Logger log = Logger.getLogger(SvgSaxHandler.class.getName());
    private static final String P2D_Prefix = "svg:";
    private static final String P2DClass = P2D_Prefix + "class";
    private static final String P2DElement = P2D_Prefix + "element";
    // piccolo2d node attribute name of the svg id attribute value
    private static final String P2DID = P2D_Prefix + "id";
    private static final String P2DStyle = P2D_Prefix + "style";
    private static final String P2DXPath = P2D_Prefix + "xpath";
    private static final PathParser path = new PathParser();
    private static final PointParser point = new PointParser();
    private static final String svgNS = "http://www.w3.org/2000/svg";
    private static final TrafoParser trafo = new TrafoParser();
    private static final String xlinkNS = "http://www.w3.org/1999/xlink";

    private static CharSequence appendXPath(final CharSequence parent, final CharSequence localName,
            final CharSequence classes) {
        // get the current xpath incl. @class attribute
        final StringBuilder txt = new StringBuilder(parent);
        // append the element name
        txt.append('/');
        txt.append(localName);
        // append the class attribute
        if (classes != null) {
            final String[] cs = clzSplit.split(classes);
            if (cs.length > 0) {
                txt.append("[@class='");
                Arrays.sort(cs);
                txt.append(cs[0]);
                for (int i = 1; i < cs.length; i++) {
                    txt.append(' ').append(cs[i]);
                }
                txt.append("']");
            }
        }
        return txt.toString();
    }

    private static void debug(final Object o) {
        log.info("" + o);
    }

    private static String find(final Attributes attributes, final String name) {
        final boolean qualified = true;
        if (false) {
            final int index;
            if (qualified) {
                index = attributes.getIndex(name);
            }
            else {
                index = attributes.getIndex(svgNS, name);
            }
            if (index < 0) {
                return null;
            }
            return attributes.getValue(index);
        }
        else {
            if (qualified) {
                return attributes.getValue(name);
            }
            else {
                return attributes.getValue(svgNS, name);
            }
        }
    }

    private static final boolean isDebugEnabled() {
        return false;
    }

    private final CssManager css;
    private PNode current = null;
    // <String, XmlElement>
    private final Map handler = new TreeMap();
    // <String, PNode>
    private final Map id2node = new TreeMap();
    // <CssManager.Style>
    private final Stack inherited = new Stack();
    private PNode root = null;
    private final StringBuilder txt = new StringBuilder();

    // <String>
    private final Stack xpath = new Stack();

    public SvgSaxHandler(final CssManager css) {
        this.css = css;
        handler.put("svg", new XmlIgnore());
        handler.put("desc", new XmlIgnore());
        handler.put("title", new XmlIgnore());
        handler.put("metadata", new XmlIgnore());
        handler.put("defs", new XmlDefs());
        handler.put("style", new XmlStyle());
        handler.put("rect", new XmlRect());
        handler.put("circle", new XmlCircle());
        handler.put("ellipse", new XmlEllipse());
        handler.put("line", new XmlLine());
        handler.put("polyline", new XmlPolyline());
        handler.put("polygon", new XmlPolygon());
        handler.put("path", new XmlPath());
        handler.put("g", new XmlGroup());
        handler.put("a", new XmlGroup());
        handler.put("text", new XmlPText());
        handler.put("use", new XmlUse());
    }

    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        txt.append(ch, start, length);
    }

    public void endDocument() throws SAXException {
        if (isDebugEnabled()) {
            debug("endDocument");
        }
    }

    public void endElement(final String uri, final String localName, final String name) throws SAXException {
        if (isDebugEnabled()) {
            debug("endElement(" + uri + ", " + localName + ", " + name + ")");
        }
        if (svgNS.equals(uri)) {
            final XmlElement eh = (XmlElement) handler.get(localName);
            if (eh == null) {
                throw new IllegalArgumentException("Unknown element '" + localName + "'");
            }
            eh.end();
            xpath.pop();
            inherited.pop();
        }
    }

    public void endPrefixMapping(final String prefix) throws SAXException {
        if (isDebugEnabled()) {
            debug("endPrefixMapping(" + prefix + ")");
        }
    }

    public void error(final SAXParseException e) throws SAXException {
        throw e;
    }

    public void fatalError(final SAXParseException e) throws SAXException {
        throw e;
    }

    private CharSequence find(final Style attributes, final String name) {
        return css.getString(attributes, name);
    }

    // <String, PNode>
    public Map getIDs() {
        return id2node;
    }

    public PNode getScene() {
        return root;
    }

    public InputSource resolveEntity(final String publicId, final String systemId) throws IOException, SAXException {
        if ("-//W3C//DTD SVG 1.1//EN".equals(publicId)
                && "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd".equals(systemId)) {
            // return new
            // InputSource(getClass().getResourceAsStream(
            // "/svg11-flat-20030114.dtd"));
            return new InputSource(new StringReader(""));
        }
        return super.resolveEntity(publicId, systemId);
    }

    public void startDocument() throws SAXException {
        if (isDebugEnabled()) {
            debug("startDocument");
        }
        txt.setLength(0);
        current = root = new PNode();
        id2node.clear();
        xpath.clear();
        xpath.push("");
        inherited.clear();
        inherited.push(css.getDefaultStyle());
    }

    public void startElement(final String uri, final String localName, final String name, final Attributes attributes)
            throws SAXException {
        if (isDebugEnabled()) {
            debug("startElement(" + uri + ", " + localName + ", " + name + ")");
        }
        txt.setLength(0);
        if (svgNS.equals(uri)) {
            final XmlElement eh = (XmlElement) handler.get(localName);
            if (eh == null) {
                throw new IllegalArgumentException("Unknown element '" + localName + "'");
            }

            final CharSequence xp = appendXPath((CharSequence) xpath.peek(), localName, find(attributes, "class"));
            xpath.push(xp);
            if (isDebugEnabled()) {
                debug("xpath: " + xp);
            }

            // get the style of this element (no inheritance)
            Style style;
            try {
                // merge style(s) and element attributes:
                style = css.findStyleByXPath(xp, find(attributes, "style"));
                merge(style, attributes);
            }
            catch (final ParseException e) {
                throw new SAXException(e);
            }

            // inherit parent style
            style = css.merge((Style) inherited.peek(), style);
            inherited.push(style);

            txt.setLength(0);
            if (eh.start(xp, localName, style, attributes)) {
                final String id = find(attributes, "id");
                if (id != null) {
                    id2node.put(id, current);
                }
            }
        }
    }

    private void merge(Style style, final Attributes attributes) throws ParseException {
        for (int i = 0; i < attributes.getLength(); i++) {
            css.setProperty(style, attributes.getLocalName(i), attributes.getValue(i));
        }
    }

    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        if (isDebugEnabled()) {
            debug("startPrefixMapping(" + prefix + ", " + uri + ")");
        }
    }

    // TODO Re-apply the styles!
    private PNode stripId(final PNode n) {
        n.addAttribute(P2DID, null);
        for (int i = 0; i < n.getChildrenCount(); i++) {
            stripId(n.getChild(i));
        }
        return n;
    }

    public void warning(final SAXParseException e) throws SAXException {
        throw e;
    }
}
