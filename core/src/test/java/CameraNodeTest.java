import junit.framework.TestCase;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;

public class CameraNodeTest extends TestCase {
	
	public CameraNodeTest(String name) {
		super(name);
	}
	
	public void testCopy() {
		PNode n = new PNode();
		
		PLayer layer1 = new PLayer();
		PLayer layer2 = new PLayer();
		
		PCamera camera1 = new PCamera();
		PCamera camera2 = new PCamera();

		n.addChild(layer1); 			
		n.addChild(layer2); 			
		n.addChild(camera1);
		n.addChild(camera2);				
		
		camera1.addLayer(layer1);
		camera1.addLayer(layer2);
		camera2.addLayer(layer1);
		camera2.addLayer(layer2);

		// no layers should be written out since they are written conditionally.
		PCamera cameraCopy = (PCamera) camera1.clone();
		assertEquals(cameraCopy.getLayerCount(), 0);
		
		n.clone();
		assertEquals(((PCamera)n.getChildrenReference().get(2)).getLayerCount(), 2);						
		assertEquals(((PLayer)n.getChildrenReference().get(1)).getCameraCount(), 2);				
	}	
}
