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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.piccolo2d.svg.css.CssManager;
import org.piccolo2d.svg.css.CssManagerImpl;
import org.xml.sax.SAXException;

import edu.umd.cs.piccolo.PNode;

/**
 * <b>THE</b> central class. Load svg and produce a {@link PNode}.
 */
public class SvgLoader {

    public PNode load(final InputStream in) throws IOException {
        return load(in, new CssManagerImpl());
    }

    public PNode load(final InputStream in, final CssManager css) throws IOException {
        final SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setValidating(false);
        spf.setXIncludeAware(false);
        try {
            // final SchemaFactory sf =
            // SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            // spf.setSchema(sf.newSchema(getClass().getResource(
            // "/svg11-flat-20030114.xsd")));
            final SvgSaxHandler svg = new SvgSaxHandler(css);
            final SAXParser sp = spf.newSAXParser();
            sp.parse(in, svg);
            return svg.getScene();
        }
        catch (final ParserConfigurationException e) {
            final IOException ioe = new IOException("Sax Parser Exception");
            ioe.initCause(e);
            throw ioe;
        }
        catch (final SAXException e) {
            final IOException ioe = new IOException("Sax Parser Exception");
            ioe.initCause(e);
            throw ioe;
        }
    }
}
