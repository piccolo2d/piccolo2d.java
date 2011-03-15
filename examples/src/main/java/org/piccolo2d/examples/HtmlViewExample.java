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
package org.piccolo2d.examples;

import java.awt.geom.Point2D;

import javax.swing.JOptionPane;

import org.piccolo2d.PCanvas;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.extras.PFrame;
import org.piccolo2d.nodes.PHtmlView;


public class HtmlViewExample extends PFrame {
    private static final long serialVersionUID = 1L;
    private StringBuffer html;

    public HtmlViewExample() {
        this(null);
    }

    public HtmlViewExample(final PCanvas aCanvas) {
        super("HTMLExample", false, aCanvas);
    }

    public void initialize() {
        html = new StringBuffer();
        html.append("<p style='margin-bottom: 10px;'>");
        html.append("This is an example <a href='#testing'>of what can</a> be done with PHtml.");
        html.append("</p>");
        html.append("<p>It supports:</p>");
        appendFeatures();

        final PHtmlView htmlNode = new PHtmlView(html.toString());
        htmlNode.setBounds(0, 0, 400, 400);
        getCanvas().getLayer().addChild(htmlNode);

        getCanvas().addInputEventListener(new PBasicInputEventHandler() {
            public void mouseClicked(final PInputEvent event) {
                final PNode clickedNode = event.getPickedNode();
                if (!(clickedNode instanceof PHtmlView)) {
                    return;
                }

                final Point2D clickPoint = event.getPositionRelativeTo(clickedNode);
                final PHtmlView htmlNode = (PHtmlView) clickedNode;

                final String url = htmlNode.getLinkAddressAt(clickPoint.getX(), clickPoint.getY());
                JOptionPane.showMessageDialog(null, url);
            }
        });
    }

    private void appendFeatures() {
        html.append("<ul>");
        html.append("<li><b>HTML</b> 3.2</li>");
        html.append("<li><font style='color:red; font-style: italic;'>Limited CSS 1.0</font></li>");
        html.append("<li>Tables:");
        appendTable();
        html.append("</li>");
        html.append("</ul>");
    }

    private void appendTable() {
        html.append("<table border='1' cellpadding='2' cellspacing='0'>");
        html.append("<tr><th>Col 1</th><th>Col 2</th></tr>");
        html.append("<tr><td>Col 1 val</td><td>Col 2 val</td></tr>");
        html.append("</table>");
    }

    public static void main(final String[] args) {
        new HtmlViewExample();
    }
}
