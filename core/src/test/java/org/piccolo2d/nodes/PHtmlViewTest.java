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
package org.piccolo2d.nodes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.piccolo2d.MockPropertyChangeListener;
import org.piccolo2d.PCanvas;
import org.piccolo2d.nodes.PHtmlView;
import org.piccolo2d.util.PBounds;

import junit.framework.TestCase;

/**
 * Unit test for PHtmlView.
 */
public class PHtmlViewTest extends TestCase {

    private static final String LOREM_IPSUM = "<html><body>30. Lorem ipsum dolor sit amet, consectetur adipiscing elit posuere.</body></html>";
    private MockPropertyChangeListener mockListener;

    public void setUp() {
        mockListener = new MockPropertyChangeListener();
    }
    
    public void testConstructorRetainsHtmlWithCSSStyling() {   
        PHtmlView html = new PHtmlView("<html><body><p style=\"font-family: sans-serif; font-color: blue\">html text</p></body></html>");        
        assertEquals("<html><body><p style=\"font-family: sans-serif; font-color: blue\">html text</p></body></html>", html.getText());
    }

    public void testConstructorRetainsAllParametersWhenRealHtml() {        
        PHtmlView html = new PHtmlView("<html><body><p>html text</p></body></html>");        
        assertEquals("<html><body><p>html text</p></body></html>", html.getText());               
    }

    public void testConstructorRetainsAllParameters() {
        Font font = new Font("Serif", Font.PLAIN, 12);
        PHtmlView html = new PHtmlView("not html", font, Color.RED);        
        assertEquals("not html", html.getText());
        assertEquals(font, html.getFont());
        assertEquals(Color.RED, html.getTextColor());        
    }

    public void testConstructorAcceptsRealisticHtml() {
        PHtmlView html = new PHtmlView("<html><body><p>html text</p></body></html>");
        assertNotNull(html);
        assertEquals("<html><body><p>html text</p></body></html>", html.getText());
    }

    public void testConstructorAcceptsNonHtmlText() {
        PHtmlView html = new PHtmlView("not html");
        assertNotNull(html);
        assertEquals("not html", html.getText());
        assertEquals(PHtmlView.DEFAULT_FONT, html.getFont());
        assertEquals(PHtmlView.DEFAULT_TEXT_COLOR, html.getTextColor());
    }

    public void testConstructorAcceptsNullHtml() {
        PHtmlView html = new PHtmlView(null);
        assertEquals(null, html.getText());
    }

    public void testDefaultConstructorHasExpectedDefaults() {
        PHtmlView html = new PHtmlView();
        assertEquals(null, html.getText());
        assertEquals(PHtmlView.DEFAULT_FONT, html.getFont());
        assertEquals(PHtmlView.DEFAULT_TEXT_COLOR, html.getTextColor());
    }

    public void testConstructorAcceptsNullFontAndColor() {
        PHtmlView html9 = new PHtmlView("not html", null, null);
        assertEquals(null, html9.getFont());
        assertEquals(null, html9.getTextColor());
    }

    public void testConstructorAcceptsNullColor() {
        Font font = new Font("Serif", Font.PLAIN, 12);
        PHtmlView html = new PHtmlView("not html", font, null);                        
        assertEquals(null, html.getTextColor());
    }

    public void testConstructorAcceptsNullFont() {
        PHtmlView html = new PHtmlView("not html", null, Color.RED);                
        assertEquals(null, html.getFont());        
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
        html.addPropertyChangeListener(PHtmlView.PROPERTY_TEXT, mockListener);
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
    
    public void testPaintFillsBounds() {
        PHtmlView html = new PHtmlView(LOREM_IPSUM);
        html.setPaint(Color.RED);
        
        PCanvas canvas = new PCanvas();
        canvas.setBackground(Color.WHITE);
        canvas.setBounds(0, 0, 500, 30);
        canvas.getLayer().addChild(html);
        
        BufferedImage image = new BufferedImage(600, 30, BufferedImage.TYPE_INT_RGB);        
        Graphics2D g2 = image.createGraphics();
        canvas.paint(g2);
              
        assertEquals(Color.red.getRGB(), image.getRGB(0, 0));
        assertEquals(Color.red.getRGB(), image.getRGB(0, (int)(html.getHeight()-1)));        
        assertEquals(Color.red.getRGB(), image.getRGB(300, 0));
    }
    
    public void testClone() {
        PHtmlView html = new PHtmlView(LOREM_IPSUM);
        html.setTextColor(Color.RED);
        PHtmlView clone = (PHtmlView) html.clone();
        assertNotNull(clone);
        assertEquals(Color.RED, clone.getTextColor());
        assertEquals(LOREM_IPSUM, clone.getText());
    }
}
