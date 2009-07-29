package edu.umd.cs.piccolo.nodes;

import java.awt.geom.Point2D;

import junit.framework.TestCase;
import edu.umd.cs.piccolo.util.PBounds;

public class PHtmlTest extends TestCase {

    public void testGetClickedAddressReturnsSingleQuotedAddress() {
        PHtml html = new PHtml("<a href='http://www.testing.com'>testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("http://www.testing.com", html.getClickedAddress(new Point2D.Double(5,5)));
    }
    
    public void testGetClickedAddressReturnsDoubleQuotedAddress() {
        PHtml html = new PHtml("<a href=\"http://www.testing.com\">testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("http://www.testing.com", html.getClickedAddress(new Point2D.Double(5,5)));
    }    

    public void testBracketsAreValidInHrefs() {
        PHtml html = new PHtml("<a href='a>b'>testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("a>b", html.getClickedAddress(new Point2D.Double(5,5)));
    }
    
    public void testGetClickedAddressReturnsNullWhenInvalid() {
        PHtml html = new PHtml("<a ='a>b'>testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertNull(html.getClickedAddress(new Point2D.Double(5,5)));
    }
    
    public void testGetClickedAddressReturnsHrefWhenMissingEndAnchorTag() {
        PHtml html = new PHtml("<a href='testing.com'>testing");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("testing.com", html.getClickedAddress(new Point2D.Double(5,5)));
    }
    
    public void testHandlesTricksyTitles() {
        PHtml html = new PHtml("<a href=\"where to go\" title=\"this is not the href='gotcha!' \">testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("where to go", html.getClickedAddress(new Point2D.Double(5,5)));
    }

    public void testHandlesHrefWithoutQuotes() {
        PHtml html = new PHtml("<a href=testing.com>testing</a>");
        html.setBounds(new PBounds(0, 0, 100, 100));
        assertEquals("testing.com", html.getClickedAddress(new Point2D.Double(5,5)));
    }

}
