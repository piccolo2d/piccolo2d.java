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
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.piccolo2d.svg.css.CssManager;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

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
        PPath createPPath(final Map attributes) {
            final PPath ret = super.createPPath(attributes);
            ret.setVisible(find(attributes, "r", 0.0) > 0);
            return ret;
        }

        Shape createShape(final Map attributes) {
            final double r = find(attributes, "r", 0.0);
            final double cx = find(attributes, "cx", 0.0);
            final double cy = find(attributes, "cy", 0.0);
            return new Arc2D.Double(cx - r, cy - r, 2 * r, 2 * r, 0, 360, Arc2D.CHORD);
        }
    }

    /**
     * <a href="http://www.w3.org/TR/SVG11/shapes.html#LineElement">svg line</a>
     * 
     * @see Line2D.Double#Double(double, double, double, double)
     */
    final class XmlLine extends XmlPPath {

        Shape createShape(final Map attributes) {
            final double x1 = find(attributes, "x1", 0.0);
            final double y1 = find(attributes, "y1", 0.0);
            final double x2 = find(attributes, "x2", 0.0);
            final double y2 = find(attributes, "y2", 0.0);
            return new Line2D.Double(x1, y1, x2, y2);
        }
    }

    final class XmlDefs extends XmlHeadElement {
        void end() throws SAXException {
            current = root;
        }

        void start(final Map attributes) throws SAXException {
            current = new PNode();
        }
    }

    final class XmlDesc extends XmlHeadElement {
    }

    abstract class XmlElement {
        void end() throws SAXException {
        };

        void start(final Map attributes) throws SAXException {

        };
    }

    // <Map<CharSequence, CharSequence>>
    private final Stack inherited = new Stack();

    /**
     * <a href="http://www.w3.org/TR/SVG11/shapes.html#EllipseElement">svg
     * ellipse</a>
     * 
     * @see Arc2D.Double#Double(double, double, double, double, double, double,
     *      int)
     */
    final class XmlEllipse extends XmlPPath {
        PPath createPPath(final Map attributes) {
            final PPath ret = super.createPPath(attributes);
            ret.setVisible(find(attributes, "rx", 0.0) > 0 && find(attributes, "ry", 0.0) > 0);
            return ret;
        }

        Shape createShape(final Map attributes) {
            final double rx = find(attributes, "rx", 0.0);
            final double ry = find(attributes, "ry", 0.0);
            final double cx = find(attributes, "cx", 0.0);
            final double cy = find(attributes, "cy", 0.0);
            return new Arc2D.Double(cx - rx, cy - ry, 2 * rx, 2 * ry, 0, 360, Arc2D.CHORD);
        }
    }

    /**
     * <a href="http://www.w3.org/TR/SVG11/struct.html#Groups">svg g</a>
     */
    final class XmlGroup extends XmlPNode {
        PNode createPNode(final Map attributes) {
            return new PNode();
        }
    }

    abstract class XmlHeadElement extends XmlElement {

    }

    abstract class XmlPNode extends XmlElement {
        /** @see #start(Map) */
        abstract PNode createPNode(Map attributes);

        final void end() throws SAXException {
            super.end();
            current = current.getParent();
        }

        final void start(final Map attributes) throws SAXException {
            final String id = find(attributes, "id");

            final PNode child = createPNode(attributes);
            child.addAttribute(ID, id);
            if (id != null) {
                id2node.put(id, child);
            }

            try {
                trafo.parse(find(attributes, "transform"), child.getTransformReference(true));
            }
            catch (final ParseException e) {
                throw new SAXException(e);
            }

            current.addChild(child);
            current = child;
        }

        /** http://www.w3.org/TR/SVG11/painting.html#SpecifyingPaint */
        protected Paint svgCreatePaint(final CharSequence paint) {
            // TODO paint
            return Color.BLACK;
        }
    }

    abstract class XmlPPath extends XmlPNode {
        /** Uses {@link #createPPath(Map)} */
        final PNode createPNode(final Map attributes) {
            return createPPath(attributes);
        }

        /** Uses {@link #createShape(Map)} */
        PPath createPPath(final Map attributes) {
            final PPath node = new PPath(createShape(attributes));
            // http://www.w3.org/TR/SVG11/painting.html#paint-att-mod
            node.setPaint(SvgColor.valueOf((String) attributes.get("fill")));
            final String t = (String) attributes.get("stroke-width");
            if (t != null) {
                node.setStroke(new BasicStroke(Float.parseFloat(t)));
            }
            node.setStrokePaint(SvgColor.valueOf((String) attributes.get("stroke")));

            // setPaint(node, attributes, "fill");
            // node.setPaint(newPaint); // fill
            // node.setStrokePaint(aPaint) // stroke

            // node.setStroke(new BasicStroke());

            // set drawing attributes

            // TODO styling attributes http://www.w3.org/TR/SVG11/styling.html

            // TODO stroke type + paint

            return node;
        }

        /** Used by {@link #createPPath(Map)} */
        abstract Shape createShape(Map attributes);
    }

    /**
     * <a href="http://www.w3.org/TR/SVG11/shapes.html#RectElement">svg rect</a>
     * 
     * @see Rectangle2D.Double#Double(double, double, double, double)
     * @see RoundRectangle2D.Double#Double(double, double, double, double,
     *      double, double)
     */
    final class XmlRect extends XmlPPath {
        Shape createShape(final Map attributes) {
            final double x = find(attributes, "x", 0.0);
            final double y = find(attributes, "y", 0.0);
            final double w = find(attributes, "width", 0.0);
            final double h = find(attributes, "height", 0.0);
            final double rx = find(attributes, "rx", 0.0);
            final double ry = find(attributes, "ry", rx);
            final RectangularShape s;
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
        void start(final Map attributes) throws SAXException {
            current = root = new PNode();
            txt.setLength(0);
            id2node.clear();
        }
    }

    private static final Pattern clzSplit = Pattern.compile("\\s+");
    private static final String ID = "id";
    private static final Logger log = Logger.getLogger(SvgSaxHandler.class.getName());
    private static final String svg = "http://www.w3.org/2000/svg";
    private static final TrafoParser trafo = new TrafoParser();
    private static final String xlink = "http://www.w3.org/1999/xlink";

    private static void debug(final Object o) {
        ;// log.info("" + o);
    }

    private static String find(final Attributes attributes, final String name) {
        final int index = attributes.getIndex(svg, name);
        if (index < 0) {
            return null;
        }
        return attributes.getValue(index);
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

    private final CssManager css;
    private PNode current = null;
    private final Map handler = new TreeMap();
    private final Map id2node = new TreeMap();
    private PNode root = null;
    private final StringBuilder txt = new StringBuilder();
    private final Stack xpath = new Stack();

    public SvgSaxHandler(final CssManager css) {
        this.css = css;
        handler.put("svg", new XmlSvg());
        handler.put("desc", new XmlDesc());
        handler.put("defs", new XmlDefs());
        handler.put("style", new XmlStyle());
        handler.put("rect", new XmlRect());
        handler.put("circle", new XmlCircle());
        handler.put("ellipse", new XmlEllipse());
        handler.put("line", new XmlLine());
        handler.put("g", new XmlGroup());
    }

    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        txt.append(ch, start, length);
    }

    public void endDocument() throws SAXException {
        debug("endDocument");
    }

    public void endElement(final String uri, final String localName, final String name) throws SAXException {
        debug("endElement(" + uri + ", " + localName + ", " + name + ")");
        if (svg.equals(uri)) {
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

    public PNode getScene() {
        return root;
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
        if (svg.equals(uri)) {
            final XmlElement eh = (XmlElement) handler.get(localName);
            if (eh == null) {
                throw new IllegalArgumentException("Unknown element '" + localName + "'");
            }

            // get the current xpath incl. @class attribute
            final String xpat;
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
                        Arrays.sort(cs);
                        txt.append("[@class='");
                        txt.append(cs[0]);
                        for (int i = 1; i < cs.length; i++) {
                            txt.append(' ').append(cs);
                        }
                        txt.append("']");
                    }
                }
                xpat = txt.toString();
            }
            xpath.push(xpat);
            debug(xpat);

            final Map a;
            try {
                // merge style and attributes:
                a = css.findStyle(xpat, find(attributes, "style"));
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
            final Map in = new TreeMap((Map)inherited.peek());
            inherited.push(in);
            in.putAll(a);

            txt.setLength(0);
            eh.start(in);
        }
    }

    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        debug("startPrefixMapping(" + prefix + ", " + uri + ")");
    }

    public void warning(final SAXParseException e) throws SAXException {
        throw e;
    }
}
