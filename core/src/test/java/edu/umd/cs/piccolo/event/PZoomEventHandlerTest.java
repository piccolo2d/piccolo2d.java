package edu.umd.cs.piccolo.event;
import edu.umd.cs.piccolo.event.PZoomEventHandler;

import junit.framework.TestCase;

public class PZoomEventHandlerTest extends TestCase {

	public PZoomEventHandlerTest(String name) {
		super(name);
	}
	
	public void testToString() {
		PZoomEventHandler zoomEventHandler = new PZoomEventHandler();
		assertNotNull(zoomEventHandler.toString());
	}
}
