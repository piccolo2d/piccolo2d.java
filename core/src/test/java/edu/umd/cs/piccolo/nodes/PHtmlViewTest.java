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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import junit.framework.TestCase;
import edu.umd.cs.piccolo.MockPropertyChangeListener;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Unit test for PHtmlView.
 */
public class PHtmlViewTest extends TestCase {

    private MockPropertyChangeListener mockListener;

    public void setUp() {
        mockListener = new MockPropertyChangeListener();
    }
    
    public void testConstructor() {
        PHtmlView html0 = new PHtmlView();
        assertNotNull(html0);
        assertEquals(null, html0.getText());
        assertEquals(PHtmlView.DEFAULT_FONT, html0.getFont());
        assertEquals(PHtmlView.DEFAULT_TEXT_COLOR, html0.getTextColor());

        PHtmlView html1 = new PHtmlView(null);
        assertNotNull(html1);
        assertEquals(null, html1.getText());
        assertEquals(PHtmlView.DEFAULT_FONT, html1.getFont());
        assertEquals(PHtmlView.DEFAULT_TEXT_COLOR, html1.getTextColor());

        PHtmlView html2 = new PHtmlView("not html");
        assertNotNull(html2);
        assertEquals("not html", html2.getText());
        assertEquals(PHtmlView.DEFAULT_FONT, html2.getFont());
        assertEquals(PHtmlView.DEFAULT_TEXT_COLOR, html2.getTextColor());

        PHtmlView html3 = new PHtmlView("<html><body><p>html text</p></body></html>");
        assertNotNull(html3);
        assertEquals("<html><body><p>html text</p></body></html>", html3.getText());
        assertEquals(PHtmlView.DEFAULT_FONT, html3.getFont());
        assertEquals(PHtmlView.DEFAULT_TEXT_COLOR, html3.getTextColor());

        Font font = new Font("Serif", Font.PLAIN, 12);
        PHtmlView html4 = new PHtmlView("not html", font, Color.RED);
        assertNotNull(html4);
        assertEquals("not html", html4.getText());
        assertEquals(font, html4.getFont());
        assertEquals(Color.RED, html4.getTextColor());

        PHtmlView html5 = new PHtmlView("<html><body><p>html text</p></body></html>", font, Color.RED);
        assertNotNull(html5);
        assertEquals("<html><body><p>html text</p></body></html>", html5.getText());
        assertEquals(font, html5.getFont());
        assertEquals(Color.RED, html5.getTextColor());

        PHtmlView html6 = new PHtmlView("<html><body><p style=\"font-family: sans-serif; font-color: blue\">html text</p></body></html>", font, Color.RED);
        assertNotNull(html6);
        assertEquals("<html><body><p style=\"font-family: sans-serif; font-color: blue\">html text</p></body></html>", html6.getText());
        assertEquals(font, html6.getFont());
        assertEquals(Color.RED, html6.getTextColor());

        PHtmlView html7 = new PHtmlView("not html", null, Color.RED);
        assertNotNull(html7);
        assertEquals("not html", html7.getText());
        assertEquals(null, html7.getFont());
        assertEquals(Color.RED, html7.getTextColor());

        PHtmlView html8 = new PHtmlView("not html", font, null);
        assertNotNull(html8);
        assertEquals("not html", html8.getText());
        assertEquals(font, html8.getFont());
        assertEquals(null, html8.getTextColor());

        PHtmlView html9 = new PHtmlView("not html", null, null);
        assertNotNull(html9);
        assertEquals("not html", html9.getText());
        assertEquals(null, html9.getFont());
        assertEquals(null, html9.getTextColor());
    }

    public void testGetClickedAddressReturnsSingleQuotedAddress() {
        PHtmlView html = new PHtmlView("<a href='http://www.testing.com'>testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("http://www.testing.com", html.getLinkAddressAt(5,5));
    }
    
    public void testGetClickedAddressReturnsDoubleQuotedAddress() {
        PHtmlView html = new PHtmlView("<a href=\"http://www.testing.com\">testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("http://www.testing.com", html.getLinkAddressAt(5,5));
    }    

    public void testBracketsAreValidInHrefs() {
        PHtmlView html = new PHtmlView("<a href='a>b'>testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("a>b", html.getLinkAddressAt(5,5));
    }
    
    public void testGetClickedAddressReturnsNullWhenInvalid() {
        PHtmlView html = new PHtmlView("<a ='a>b'>testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertNull(html.getLinkAddressAt(5,5));
    }
    
    public void testGetClickedAddressReturnsHrefWhenMissingEndAnchorTag() {
        PHtmlView html = new PHtmlView("<a href='testing.com'>testing");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("testing.com", html.getLinkAddressAt(5,5));
    }
    
    public void testHandlesTricksyTitles() {
        PHtmlView html = new PHtmlView("<a href=\"where to go\" title=\"this is not the href='gotcha!' \">testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("where to go", html.getLinkAddressAt(5,5));
    }

    public void testHandlesHrefWithoutQuotes() {
        PHtmlView html = new PHtmlView("<a href=testing.com>testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("testing.com", html.getLinkAddressAt(5,5));
    }
    
    public void testUnclosedTagsCauseIgnoreOfTag() {
        PHtmlView html = new PHtmlView("<a href='testing.com' ");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertNull(html.getLinkAddressAt(5,5));
    }
    
    public void testMissingEndTagCausesRemainderOfHtmlToBeLinkTarget() {
        PHtmlView html = new PHtmlView("<a href='testing.com'>Missing End TAg ");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("testing.com", html.getLinkAddressAt(5,5));
    }
    
    public void testUnclosedQuotesCauseIgnoreOfLink() {
        PHtmlView html = new PHtmlView("<a href='testing.com>testing");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertNull(html.getLinkAddressAt(5,5));
    }
    
    public void testEmptyAddressReturnsEmptyString() {
        PHtmlView html = new PHtmlView("<a href=''>testing");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("", html.getLinkAddressAt(5,5));
    }
    
    public void testReturnsNullWhenClickOutsideLink() {
        PHtmlView html = new PHtmlView("0123456789 <a href=#>testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertNull(html.getLinkAddressAt(5,5));
    }

    public void testSetHtmlColorPersists() {
        PHtmlView html = new PHtmlView();
        html.setTextColor(Color.RED);
        assertEquals(Color.RED, html.getTextColor());
    }
    
    public void testFontIsNotNullByDefault() {
        PHtmlView html = new PHtmlView();
        assertNotNull(html.getFont());
    }
    
    public void testHtmlColorIsNotNullByDefault() {
        PHtmlView html = new PHtmlView();
        assertNotNull(html.getTextColor());
    }
    
    public void testSetHtmlFiresEventOnChangeOnly() {
        PHtmlView html = new PHtmlView();
        html.addPropertyChangeListener(mockListener);
        html.setText("testing");
        assertEquals(1, mockListener.getPropertyChangeCount());
        assertEquals(PHtmlView.PROPERTY_TEXT, mockListener.getPropertyChange(0).getPropertyName());
        html.setText("testing");
        assertEquals(1, mockListener.getPropertyChangeCount());
    }
    
    public void testSetHtmlToNullIsAllowed() {
        PHtmlView html = new PHtmlView();
        html.setText(null);
        assertNull(html.getText());
    }
    
    public void testSetFontPerists() {
        PHtmlView html = new PHtmlView();
        Font font = Font.getFont("arial");
        html.setFont(font);
        assertSame(font, html.getFont());       
    }       
    
    public void testPaintFillsBounds() throws IOException {
        PHtmlView html = new PHtmlView("<html><body>30. Lorem ipsum dolor sit amet, consectetur adipiscing elit posuere.</body></html>");
        html.setBounds(0, 0, 400, 30);
        html.setPaint(Color.RED);
        
        PCanvas canvas = new PCanvas();
        canvas.setBackground(Color.WHITE);
        canvas.setBounds(0, 0, 500, 30);
        canvas.getLayer().addChild(html);
        
        BufferedImage image = new BufferedImage(600, 30, BufferedImage.TYPE_INT_RGB);        
        Graphics2D g2 = image.createGraphics();
        canvas.paint(g2);
        
        ImageIO.write(image, "JPEG", new File("C:\\html.jpg"));
        assertEquals(Color.red.getRGB(), image.getRGB(0, 0));
        assertEquals(Color.red.getRGB(), image.getRGB(0, 15));
        assertEquals(Color.red.getRGB(), image.getRGB(0, 29));
        assertEquals(Color.red.getRGB(), image.getRGB(399, 0));
        assertEquals(Color.white.getRGB(), image.getRGB(400, 0));
        
        
    }
}
