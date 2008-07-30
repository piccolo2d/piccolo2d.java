import edu.umd.cs.piccolo.event.PZoomEventHandler;

import junit.framework.TestCase;

public class ZoomEventHandlerTest extends TestCase {

	public ZoomEventHandlerTest(String name) {
		super(name);
	}
	
	public void testToString() {
		PZoomEventHandler zoomEventHandler = new PZoomEventHandler();
		assertNotNull(zoomEventHandler.toString());
	}
}
