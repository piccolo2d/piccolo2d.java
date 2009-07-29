package edu.umd.cs.piccolo.nodes;

import java.awt.Color;
import java.awt.Font;

import junit.framework.TestCase;
import edu.umd.cs.piccolo.MockPropertyChangeListener;
import edu.umd.cs.piccolo.util.PBounds;

public class PHtmlTest extends TestCase {

    private MockPropertyChangeListener mockListener;

    public void setUp() {
        mockListener = new MockPropertyChangeListener();
    }
    
    public void testGetClickedAddressReturnsSingleQuotedAddress() {
        PHtml html = new PHtml("<a href='http://www.testing.com'>testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("http://www.testing.com", html.getClickedAddress(5,5));
    }
    
    public void testGetClickedAddressReturnsDoubleQuotedAddress() {
        PHtml html = new PHtml("<a href=\"http://www.testing.com\">testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("http://www.testing.com", html.getClickedAddress(5,5));
    }    

    public void testBracketsAreValidInHrefs() {
        PHtml html = new PHtml("<a href='a>b'>testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("a>b", html.getClickedAddress(5,5));
    }
    
    public void testGetClickedAddressReturnsNullWhenInvalid() {
        PHtml html = new PHtml("<a ='a>b'>testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertNull(html.getClickedAddress(5,5));
    }
    
    public void testGetClickedAddressReturnsHrefWhenMissingEndAnchorTag() {
        PHtml html = new PHtml("<a href='testing.com'>testing");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("testing.com", html.getClickedAddress(5,5));
    }
    
    public void testHandlesTricksyTitles() {
        PHtml html = new PHtml("<a href=\"where to go\" title=\"this is not the href='gotcha!' \">testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("where to go", html.getClickedAddress(5,5));
    }

    public void testHandlesHrefWithoutQuotes() {
        PHtml html = new PHtml("<a href=testing.com>testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("testing.com", html.getClickedAddress(5,5));
    }
    
    public void testUnclosedTagsCauseIgnoreOfTag() {
        PHtml html = new PHtml("<a href='testing.com' ");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertNull(html.getClickedAddress(5,5));
    }
    
    public void testMissingEndTagCausesRemainderOfHtmlToBeLinkTarget() {
        PHtml html = new PHtml("<a href='testing.com'>Missing End TAg ");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("testing.com", html.getClickedAddress(5,5));
    }
    
    public void testUnclosedQuotesCauseIgnoreOfLink() {
        PHtml html = new PHtml("<a href='testing.com>testing");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertNull(html.getClickedAddress(5,5));
    }
    
    public void testEmptyAddressReturnsEmptyString() {
        PHtml html = new PHtml("<a href=''>testing");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("", html.getClickedAddress(5,5));
    }
    
    public void testReturnsNullWhenClickOutsideLink() {
        PHtml html = new PHtml("0123456789 <a href=#>testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertNull(html.getClickedAddress(5,5));
    }

    public void testSetHtmlColorPersists() {
        PHtml html = new PHtml();
        html.setHtmlColor(Color.RED);
        assertEquals(Color.RED, html.getHtmlColor());
    }
    
    public void testFontIsNotNullByDefault() {
        PHtml html = new PHtml();
        assertNotNull(html.getFont());
    }
    
    public void testHtmlColorIsNotNullByDefault() {
        PHtml html = new PHtml();
        assertNotNull(html.getHtmlColor());
    }
    
    public void testSetHtmlFiresEventOnChangeOnly() {
        PHtml html = new PHtml();
        html.addPropertyChangeListener(mockListener);
        html.setHtml("testing");
        assertEquals(1, mockListener.getPropertyChangeCount());
        assertEquals(PHtml.PROPERTY_HTML, mockListener.getPropertyChange(0).getPropertyName());
        html.setHtml("testing");
        assertEquals(1, mockListener.getPropertyChangeCount());
    }
    
    public void testSetHtmlToNullIsAllowed() {
        PHtml html = new PHtml();
        html.setHtml(null);
        assertNull(html.getHtml());
    }
    
    public void testSetFontPerists() {
        PHtml html = new PHtml();
        Font font = Font.getFont("arial");
        html.setFont(font);
        assertSame(font, html.getFont());       
    }       
}
