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
