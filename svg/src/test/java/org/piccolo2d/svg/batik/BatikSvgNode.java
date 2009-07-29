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

package org.piccolo2d.svg.batik;

import java.awt.Graphics2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGElement;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * 
 * @author mr0738@mro.name
 */
public class BatikSvgNode extends PNode {

    private static final long serialVersionUID = -2846124881947793859L;

    private static void printTree(final SVGElement parent, final int nesting, final PrintStream dst) {
        if (dst == null) {
            return;
        }
        final StringBuffer indent = new StringBuffer();
        for (int i = 0; i < nesting; i++) {
            indent.append("\t");
        }

        dst.println(indent + parent.getClass().getName());
        final NamedNodeMap a = parent.getAttributes();
        for (int i = 0; i < a.getLength(); i++) {
            final Node n = a.item(i);
            dst.println(indent.toString() + n.getNodeName() + "=" + n.getNodeValue());
        }
        final NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node c = children.item(i);
            if (c instanceof SVGElement) {
                printTree((SVGElement) c, nesting + 1, dst);
            }
        }
    }

    private final GraphicsNode svg;

    public BatikSvgNode(final InputStream is) throws IOException {
        final SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
        final SVGDocument doc = (SVGDocument) f.createDocument(null, is);
        printTree(doc.getRootElement(), 0, null);

        final UserAgentAdapter ua = new UserAgentAdapter();
        final DocumentLoader loader = new DocumentLoader(ua);
        final BridgeContext bridge = new BridgeContext(ua, loader);

        final GVTBuilder builder = new GVTBuilder();
        svg = builder.build(bridge, doc);
        setBounds(svg.getBounds());
    }

    protected void paint(final PPaintContext paintContext) {
        final Graphics2D g2 = paintContext.getGraphics();
        svg.paint(g2);
    }
}
