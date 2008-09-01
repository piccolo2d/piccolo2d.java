package org.piccolo2d.svg;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SvgSaxHandlerTest extends TestCase {

    public void testClassAttribute() throws ParserConfigurationException, SAXException, IOException {
        final SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setValidating(false);
        spf.setXIncludeAware(false);

        final SAXParser sp = spf.newSAXParser();
        sp.parse(new InputSource(new StringReader(
                "<svg xmlns='http://www.w3.org/2000/svg'><circle class='circle_12' /></svg>")), new DefaultHandler() {
            public void startElement(final String uri, final String localName, final String name,
                    final Attributes attributes) throws SAXException {
                if ("svg".equals(localName)) {
                    return;
                }
                assertEquals("circle_12", attributes.getValue("class"));
            }
        });
    }

    public void testDocType() throws ParserConfigurationException, SAXException, IOException {
        final SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setValidating(true);
        spf.setXIncludeAware(false);

        final SAXParser sp = spf.newSAXParser();
        sp
                .parse(
                        new InputSource(
                                new StringReader(
                                        "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\"><svg xmlns='http://www.w3.org/2000/svg'><circle class='circle-12' /></svg>")),
                        new DefaultHandler() {
                            public InputSource resolveEntity(final String publicId, final String systemId)
                                    throws IOException, SAXException {
                                if ("-//W3C//DTD SVG 1.1//EN".equals(publicId)
                                        && "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd".equals(systemId)) {
                                    // return new
                                    //InputSource(getClass().getResourceAsStream
                                    // ("/svg11-flat-20030114.dtd"));
                                    return new InputSource(new StringReader(""));
                                }
                                return super.resolveEntity(publicId, systemId);
                            }

                            public void startElement(final String uri, final String localName, final String name,
                                    final Attributes attributes) throws SAXException {
                                if ("svg".equals(localName)) {
                                    return;
                                }
                                assertEquals("circle-12", attributes.getValue("class"));
                            }
                        });
    }
}
