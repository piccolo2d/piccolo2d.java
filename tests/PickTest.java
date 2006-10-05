import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPickPath;
import junit.framework.TestCase;

public class PickTest extends TestCase {

	public PickTest(String name) {
		super(name);
	}

	public void testPick() {
		PCanvas canvas = new PCanvas();
		PCamera camera = canvas.getCamera();
		PLayer layer = canvas.getLayer();
		
		camera.setBounds(0, 0, 100, 100);
		
		PNode a = PPath.createRectangle(0, 0, 100, 100);
		PNode b = PPath.createRectangle(0, 0, 100, 100);
		PNode c = PPath.createRectangle(0, 0, 100, 100);
		
		layer.addChild(a);
		layer.addChild(b);
		layer.addChild(c);
		
		PPickPath pickPath = camera.pick(50, 50, 2);
		
		assertTrue(pickPath.getPickedNode() == c);
		assertTrue(pickPath.nextPickedNode() == b);
		assertTrue(pickPath.nextPickedNode() == a);
		assertTrue(pickPath.nextPickedNode() == camera);
		assertTrue(pickPath.nextPickedNode() == null);
		assertTrue(pickPath.nextPickedNode() == null);
	}
}
