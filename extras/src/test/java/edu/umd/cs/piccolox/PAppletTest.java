package edu.umd.cs.piccolox;

import junit.framework.TestCase;
import edu.umd.cs.piccolo.PCanvas;

public class PAppletTest extends TestCase {
    private PApplet applet;
    
    public void setUp() {
        applet = new PApplet();
        applet.init();
        applet.setVisible(false);        
    }
    
    public void tearDown() {       
        applet.setVisible(false);
    }
    
    public void testCanvasIsValidWithDefaultConstructor() {        
        PCanvas canvas = applet.getCanvas();
        assertNotNull(canvas);
        assertNotNull(canvas.getLayer());
        assertNotNull(canvas.getCamera());
        assertSame(canvas.getLayer(), canvas.getCamera().getLayer(0));       
    }   
}
