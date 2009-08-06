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

import junit.framework.TestCase;
import edu.umd.cs.piccolo.MockPropertyChangeListener;
import edu.umd.cs.piccolo.util.PBounds;

public class PHtmlViewTest extends TestCase {

    private MockPropertyChangeListener mockListener;

    public void setUp() {
        mockListener = new MockPropertyChangeListener();
    }
    
    public void testGetClickedAddressReturnsSingleQuotedAddress() {
        PHtmlView html = new PHtmlView("<a href='http://www.testing.com'>testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("http://www.testing.com", html.getClickedAddress(5,5));
    }
    
    public void testGetClickedAddressReturnsDoubleQuotedAddress() {
        PHtmlView html = new PHtmlView("<a href=\"http://www.testing.com\">testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("http://www.testing.com", html.getClickedAddress(5,5));
    }    

    public void testBracketsAreValidInHrefs() {
        PHtmlView html = new PHtmlView("<a href='a>b'>testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("a>b", html.getClickedAddress(5,5));
    }
    
    public void testGetClickedAddressReturnsNullWhenInvalid() {
        PHtmlView html = new PHtmlView("<a ='a>b'>testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertNull(html.getClickedAddress(5,5));
    }
    
    public void testGetClickedAddressReturnsHrefWhenMissingEndAnchorTag() {
        PHtmlView html = new PHtmlView("<a href='testing.com'>testing");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("testing.com", html.getClickedAddress(5,5));
    }
    
    public void testHandlesTricksyTitles() {
        PHtmlView html = new PHtmlView("<a href=\"where to go\" title=\"this is not the href='gotcha!' \">testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("where to go", html.getClickedAddress(5,5));
    }

    public void testHandlesHrefWithoutQuotes() {
        PHtmlView html = new PHtmlView("<a href=testing.com>testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("testing.com", html.getClickedAddress(5,5));
    }
    
    public void testUnclosedTagsCauseIgnoreOfTag() {
        PHtmlView html = new PHtmlView("<a href='testing.com' ");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertNull(html.getClickedAddress(5,5));
    }
    
    public void testMissingEndTagCausesRemainderOfHtmlToBeLinkTarget() {
        PHtmlView html = new PHtmlView("<a href='testing.com'>Missing End TAg ");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("testing.com", html.getClickedAddress(5,5));
    }
    
    public void testUnclosedQuotesCauseIgnoreOfLink() {
        PHtmlView html = new PHtmlView("<a href='testing.com>testing");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertNull(html.getClickedAddress(5,5));
    }
    
    public void testEmptyAddressReturnsEmptyString() {
        PHtmlView html = new PHtmlView("<a href=''>testing");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("", html.getClickedAddress(5,5));
    }
    
    public void testReturnsNullWhenClickOutsideLink() {
        PHtmlView html = new PHtmlView("0123456789 <a href=#>testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertNull(html.getClickedAddress(5,5));
    }

    public void testSetHtmlColorPersists() {
        PHtmlView html = new PHtmlView();
        html.setHtmlColor(Color.RED);
        assertEquals(Color.RED, html.getHtmlColor());
    }
    
    public void testFontIsNotNullByDefault() {
        PHtmlView html = new PHtmlView();
        assertNotNull(html.getFont());
    }
    
    public void testHtmlColorIsNotNullByDefault() {
        PHtmlView html = new PHtmlView();
        assertNotNull(html.getHtmlColor());
    }
    
    public void testSetHtmlFiresEventOnChangeOnly() {
        PHtmlView html = new PHtmlView();
        html.addPropertyChangeListener(mockListener);
        html.setHtml("testing");
        assertEquals(1, mockListener.getPropertyChangeCount());
        assertEquals(PHtmlView.PROPERTY_HTML, mockListener.getPropertyChange(0).getPropertyName());
        html.setHtml("testing");
        assertEquals(1, mockListener.getPropertyChangeCount());
    }
    
    public void testSetHtmlToNullIsAllowed() {
        PHtmlView html = new PHtmlView();
        html.setHtml(null);
        assertNull(html.getHtml());
    }
    
    public void testSetFontPerists() {
        PHtmlView html = new PHtmlView();
        Font font = Font.getFont("arial");
        html.setFont(font);
        assertSame(font, html.getFont());       
    }       
}
