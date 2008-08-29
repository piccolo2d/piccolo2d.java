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
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.piccolo2d.svg.css.CssManager;
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
 * @author mr0738@mro.name
 */
class SvgSaxHandler extends DefaultHandler {
    /**
     * <a href="http://www.w3.org/TR/SVG11/shapes.html#CircleElement">svg circle</a>
     * 
     * @see Arc2D.Double#Double(double, double, double, double, double, double,
     *      int)
     */
    final class XmlCircle extends XmlPPath {
        PPath createPPath(final Map attributes) throws ParseException {
            final PPath ret = super.createPPath(attributes);
            ret.setVisible(find(attributes, "r", 0.0) > 0);
            return ret;
        }

        Shape createShape(final Map attributes) throws ParseException {
            final double r = find(attributes, "r", 0.0);
            final double cx = find(attributes, "cx", 0.0);
            final double cy = find(attributes, "cy", 0.0);
            return new Arc2D.Double(cx - r, cy - r, 2 * r, 2 * r, 0, 360, Arc2D.CHORD);
        }
    }

    final class XmlDefs extends XmlHeadElement {
        void end() throws SAXException {
            current = root;
        }

        boolean start(final Map attributes) throws SAXException {
            current = new PNode();
            return true;
        }
    }

    abstract class XmlElement {
        void end() throws SAXException {
        };

        /**
         * @return was a new PNode created?
         */
        boolean start(final Map attributes) throws SAXException {
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
    final class XmlEllipse extends XmlPPath {
        PPath createPPath(final Map attributes) throws ParseException {
            final PPath ret = super.createPPath(attributes);
            ret.setVisible(find(attributes, "rx", 0.0) > 0 && find(attributes, "ry", 0.0) > 0);
            return ret;
        }

        Shape createShape(final Map attributes) throws ParseException {
            final double rx = find(attributes, "rx", 0.0);
            final double ry = find(attributes, "ry", 0.0);
            final double cx = find(attributes, "cx", 0.0);
            final double cy = find(attributes, "cy", 0.0);
            return new Arc2D.Double(cx - rx, cy - ry, 2 * rx, 2 * ry, 0, 360, Arc2D.CHORD);
        }
    }

    abstract class XmlGeneralPath extends XmlPPath {

        abstract GeneralPath createGeneralPath(final CharSequence points) throws ParseException;

        Shape createShape(final Map attributes) throws ParseException {
            return createGeneralPath(find(attributes, "points"));
        }
    }

    /**
     * <a href="http://www.w3.org/TR/SVG11/struct.html#Groups">svg g</a>
     */
    final class XmlGroup extends XmlPNode {
        PNode createPNode(final Map attributes) throws ParseException {
            return new PNode();
        }
    }

    abstract class XmlHeadElement extends XmlElement {

    }

    final class XmlIgnore extends XmlHeadElement {
    }

    /**
     * <a href="http://www.w3.org/TR/SVG11/shapes.html#LineElement">svg line</a>
     * 
     * @see Line2D.Double#Double(double, double, double, double)
     */
    final class XmlLine extends XmlPPath {

        Shape createShape(final Map attributes) throws ParseException {
            final double x1 = find(attributes, "x1", 0.0);
            final double y1 = find(attributes, "y1", 0.0);
            final double x2 = find(attributes, "x2", 0.0);
            final double y2 = find(attributes, "y2", 0.0);
            return new Line2D.Double(x1, y1, x2, y2);
        }
    }

    /**
     * <a href="http://www.w3.org/TR/SVG11/paths.html">svg path</a>
     */
    class XmlPath extends XmlGeneralPath {
        GeneralPath createGeneralPath(final CharSequence d) throws ParseException {
            return path.parse(d);
        }

        Shape createShape(final Map attributes) throws ParseException {
            return createGeneralPath(find(attributes, "d"));
        }
    }

    abstract class XmlPNode extends XmlElement {

        /**
         * @throws ParseException TODO
         * @see #start(Map)
         */
        abstract PNode createPNode(Map attributes) throws ParseException;

        void end() throws SAXException {
            super.end();
            current = current.getParent();
        }

        final boolean start(final Map attributes) throws SAXException {
            final String id = find(attributes, "id");
            try {
                final PNode child = createPNode(attributes);
                for (final Iterator it = attributes.entrySet().iterator(); it.hasNext();) {
                    final Entry elem = (Entry) it.next();
                    child.addAttribute(P2D_Prefix + elem.getKey(), elem.getValue());
                }
                // child.addAttribute(ID, id);
                if (id != null) {
                    id2node.put(id, child);
                }

                // attributes common to all elements:
                trafo.parse(find(attributes, "transform"), child.getTransformReference(true));

                current.addChild(child);
                current = child;
                return true;
            }
            catch (final ParseException e) {
                throw new SAXException(e);
            }
        }
    }

    /**
     * <a href="http://www.w3.org/TR/SVG11/shapes.html#PolygonElement">svg
     * polylgon</a>
     * 
     * @see GeneralPath
     */
    class XmlPolygon extends XmlPolyline {
        final GeneralPath createGeneralPath(final CharSequence points) throws ParseException {
            final GeneralPath gp = super.createGeneralPath(points);
            gp.closePath();
            return gp;
        }

        Shape createShape(final Map attributes) throws ParseException {
            return createGeneralPath(find(attributes, "points"));
        }
    }

    /**
     * <a href="http://www.w3.org/TR/SVG11/shapes.html#PolylineElement">svg
     * polyline</a>
     */
    class XmlPolyline extends XmlGeneralPath {
        GeneralPath createGeneralPath(final CharSequence points) throws ParseException {
            return point.parse(points);
        }

        Shape createShape(final Map attributes) throws ParseException {
            return createGeneralPath(find(attributes, "points"));
        }
    }

    abstract class XmlPPath extends XmlPNode {
        /** Uses {@link #createPPath(Map)} */
        final PNode createPNode(final Map attributes) throws ParseException {
            return createPPath(attributes);
        }

        /**
         * Uses {@link #createShape(Map)}
         */
        PPath createPPath(final Map attributes) throws ParseException {
            final PPath node = new PPath(createShape(attributes));
            // http://www.w3.org/TR/SVG11/painting.html#paint-att-mod
            node.setPaint(SvgColor.valueOf((String) attributes.get("fill")));

            // TODO stroke type
            final String t = (String) attributes.get("stroke-width");
            if (t != null) {
                node.setStroke(new BasicStroke(Float.parseFloat(t)));
            }
            node.setStrokePaint(SvgColor.valueOf((String) attributes.get("stroke")));

            // set drawing attributes

            // TODO styling attributes http://www.w3.org/TR/SVG11/styling.html

            return node;
        }

        /**
         * Used by {@link #createPPath(Map)}
         */
        abstract Shape createShape(Map attributes) throws ParseException;
    }

    /**
     * http://www.w3.org/TR/SVG11/text.html#TextElement
     * 
     * @see Font
     * @author mr0738@mro.name
     */
    final class XmlPText extends XmlPNode {
        PNode createPNode(final Map attributes) throws ParseException {
            final PText node = new PText();
            node.setConstrainWidthToTextWidth(true);
            node.setConstrainHeightToTextHeight(true);

            // http://www.w3.org/TR/SVG11/painting.html#paint-att-mod
            node.setTextPaint(SvgColor.valueOf((String) attributes.get("fill")));

            final double x = find(attributes, "x", 0.0);
            final double y = find(attributes, "y", 0.0);
            node.translate(x, y);

            // TODO text attributes!

            node.scale(1.0 / FontUtil.SCALE);
            node.setFont(FontUtil.findFont(attributes));

            return node;
        }

        void end() throws SAXException {
            final PText node = (PText) current;
            node.setText(txt.toString().trim());

            // TODO justification!
            node.translate(0, -0.5 * node.getHeight());

            super.end();
        }
    }

    /**
     * <a href="http://www.w3.org/TR/SVG11/shapes.html#RectElement">svg rect</a>
     * 
     * @see Rectangle2D.Double#Double(double, double, double, double)
     * @see RoundRectangle2D.Double#Double(double, double, double, double,
     *      double, double)
     */
    final class XmlRect extends XmlPPath {
        Shape createShape(final Map attributes) throws ParseException {
            final double x = find(attributes, "x", 0.0);
            final double y = find(attributes, "y", 0.0);
            final double w = find(attributes, "width", 0.0);
            final double h = find(attributes, "height", 0.0);
            final double rx = find(attributes, "rx", 0.0);
            final double ry = find(attributes, "ry", rx);
            if (rx > 0 || ry > 0) {
                return new RoundRectangle2D.Double(x, y, w, h, rx, ry);
            }
            else {
                return new Rectangle2D.Double(x, y, w, h);
            }
        }
    }

    final class XmlStyle extends XmlHeadElement {
        void end() throws SAXException {
            try {
                css.loadStyleSheet(txt);
            }
            catch (final ParseException e) {
                throw new SAXException(e);
            }
        }
    }

    final class XmlSvg extends XmlElement {
        boolean start(final Map attributes) throws SAXException {
            current = root = new PNode();
            txt.setLength(0);
            id2node.clear();
            return false;
        }
    }

    final class XmlUse extends XmlPNode {
        PNode createPNode(final Map attributes) throws ParseException {
            final String id = raw_atts.getValue(xlinkNS, "href");
            if (id == null) {
                throw new IllegalArgumentException("<use> element without xlink:href");
            }
            final Matcher m = anchor.matcher(id);
            if (!m.matches()) {
                throw new IllegalArgumentException("xlink:href [" + id + "] does not match [" + anchor.pattern() + "]");
            }

            final PNode used = (PNode) id2node.get(m.group(1));
            final PNode r = new PNode();
            r.addChild(stripId(doClone(used)));

            final double x = find(attributes, "x", 0.0);
            final double y = find(attributes, "y", 0.0);
            r.setTransform(AffineTransform.getTranslateInstance(x, y));
            return r;
        }
    }

    private static final Pattern anchor = Pattern.compile("^#(.+)$");
    private static final Pattern clzSplit = Pattern.compile("\\s+");
    private static final Logger log = Logger.getLogger(SvgSaxHandler.class.getName());
    private static final String P2D_Prefix = "svg:";
    // piccolo2d node attribute name of the svg id attribute value
    private static final String P2DID = P2D_Prefix + "id";
    private static final PathParser path = new PathParser();
    private static final PointParser point = new PointParser();
    private static final String svgNS = "http://www.w3.org/2000/svg";
    private static final TrafoParser trafo = new TrafoParser();
    private static final String xlinkNS = "http://www.w3.org/1999/xlink";

    private static void debug(final Object o) {
        ;// log.info("" + o);
    }

    static final PNode doClone(final PNode n) {
        return stripId((PNode) n.clone());
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

    private static String find(final Map attributes, final String name) {
        return (String) attributes.get(name);
    }

    private static double find(final Map attributes, final String name, final double def) {
        final String v = find(attributes, name);
        if (v == null) {
            return def;
        }
        return Double.parseDouble(v);
    }

    private static final boolean isDebugEnabled() {
        return true;
    }

    // TODO Re-apply the styles!
    static final PNode stripId(final PNode n) {
        n.addAttribute(P2DID, null);
        for (int i = 0; i < n.getChildrenCount(); i++) {
            stripId(n.getChild(i));
        }
        return n;
    }

    private final CssManager css;
    private PNode current = null;
    // <String, XmlElement>
    private final Map handler = new TreeMap();
    // <String, PNode>
    private final Map id2node = new TreeMap();
    // <Map<CharSequence, CharSequence>>
    private final Stack inherited = new Stack();
    private Attributes raw_atts = null;
    private PNode root = null;
    private final StringBuilder txt = new StringBuilder();
    // <String>
    private final Stack xpath = new Stack();

    public SvgSaxHandler(final CssManager css) {
        this.css = css;
        handler.put("svg", new XmlSvg());
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
        debug("endDocument");
    }

    public void endElement(final String uri, final String localName, final String name) throws SAXException {
        debug("endElement(" + uri + ", " + localName + ", " + name + ")");
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
        debug("endPrefixMapping(" + prefix + ")");
    }

    public void error(final SAXParseException e) throws SAXException {
        throw e;
    }

    public void fatalError(final SAXParseException e) throws SAXException {
        throw e;
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
            // InputSource(getClass().getResourceAsStream("/svg11-flat-20030114.dtd"));
            return new InputSource(new StringReader(""));
        }
        return super.resolveEntity(publicId, systemId);
    }

    public void startDocument() throws SAXException {
        debug("startDocument");
        xpath.clear();
        xpath.push("");
        inherited.push(new TreeMap());
    }

    public void startElement(final String uri, final String localName, final String name, final Attributes attributes)
            throws SAXException {
        debug("startElement(" + uri + ", " + localName + ", " + name + ")");
        txt.setLength(0);
        if (svgNS.equals(uri)) {
            final XmlElement eh = (XmlElement) handler.get(localName);
            if (eh == null) {
                throw new IllegalArgumentException("Unknown element '" + localName + "'");
            }

            // get the current xpath incl. @class attribute
            final String xp;
            {
                txt.append((String) xpath.peek());
                // append the element name
                txt.append('/');
                txt.append(localName);
                // append the class attribute
                final String clz = find(attributes, "class");
                if (clz != null) {
                    final String[] cs = clzSplit.split(clz.trim());
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
                xp = txt.toString();
            }
            xpath.push(xp);
            debug("xpath: " + xp);

            final Map a;
            try {
                // merge style(s) and element attributes:
                a = css.findStyleByXPath(xp, find(attributes, "style"));
                for (int i = 0; i < attributes.getLength(); i++) {
                    final String n = attributes.getLocalName(i);
                    if ("class".equals(n) || "style".equals(n)) {
                        continue;
                    }
                    a.put(n, attributes.getValue(i));
                }
            }
            catch (final ParseException e) {
                throw new SAXException(e);
            }

            // inherit attributes
            final Map in = new TreeMap((Map) inherited.peek());
            in.putAll(a);
            inherited.push(in);

            txt.setLength(0);
            raw_atts = attributes;
            if (eh.start(in)) {
                current.addAttribute(P2D_Prefix + "xpath", xp);
                current.addAttribute(P2D_Prefix + "style", find(raw_atts, "style"));
                current.addAttribute(P2D_Prefix + "class", find(raw_atts, "class"));
            }
        }
    }

    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        debug("startPrefixMapping(" + prefix + ", " + uri + ")");
    }

    public void warning(final SAXParseException e) throws SAXException {
        throw e;
    }
}
