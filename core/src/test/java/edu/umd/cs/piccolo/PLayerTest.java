package edu.umd.cs.piccolo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PObjectOutputStream;

public class PLayerTest extends TestCase {
	private PLayer layer;

	public void setUp() {
		layer = new PLayer();
	}
	
	public void testLayerHasEmptyCamerasCollectionByDefault() {
		Collection cameras = layer.getCamerasReference();
		assertNotNull(cameras);
		assertTrue(cameras.isEmpty());
		assertEquals(0, layer.getCameraCount());
	}
	
	public void testGetCameraByIndexThrowsIndexOutOfBoundsExceptionWhenOutOfBounds() {
		PCamera camera = new PCamera();
		layer.addCamera(camera);
		try {
			layer.getCamera(-1);
			fail("Exception should have been thrown");
		} catch (IndexOutOfBoundsException e) {
			// expected;
		}

		try {
			layer.getCamera(1);
			fail("Exception should have been thrown");
		} catch (IndexOutOfBoundsException e) {
			// expected;
		}		
	}

	public void testGetCameraReturnsCameraAtCorrectIndex() {
		PCamera camera1 = new PCamera();
		PCamera camera2 = new PCamera();
		PCamera camera3 = new PCamera();
		
		layer.addCamera(camera1);
		layer.addCamera(camera2);
		layer.addCamera(camera3);
		
		assertEquals(camera1, layer.getCamera(0));
		assertEquals(camera2, layer.getCamera(1));
		assertEquals(camera3, layer.getCamera(2));
	}
	
	public void testAddCameraCorrectlyHandlesIndex() {
		PCamera camera1 = new PCamera();
		PCamera camera2 = new PCamera();
		PCamera camera3 = new PCamera();
		
		layer.addCamera(0, camera1);
		layer.addCamera(0, camera2);
		layer.addCamera(1, camera3);
		
		assertEquals(camera2, layer.getCamera(0));
		assertEquals(camera3, layer.getCamera(1));
		assertEquals(camera1, layer.getCamera(2));
	}
	
	public void testRemovingCameraByReferenceWorksWhenCameraIsFound() {
		PCamera camera = new PCamera();
		layer.addCamera(camera);
		layer.removeCamera(camera);
		assertEquals(0, layer.getCameraCount());
	}
	
	public void testRemovingCameraByIndexWorksWhenIndexIsValid() {
		PCamera camera = new PCamera();
		layer.addCamera(camera);
		layer.removeCamera(0);
		assertEquals(0, layer.getCameraCount());
	}
	
	// I think PLayer should behave this way. Seems more logical since it's how
	// the Java Collection libraries behave
	/*
	public void testRemovingCameraNotAttachedToCameraShouldDoNothing() {
		PCamera strangerCamera = new PCamera();
		layer.removeCamera(strangerCamera);
		assertEquals(0, layer.getCameraCount());
	}*/
	
	public void testRepaintFromNotifiesCameras() {
		MockPCamera camera = new MockPCamera();
		layer.addCamera(camera);
		
		PBounds bounds = new PBounds(0, 0, 100, 100);
		layer.repaintFrom(bounds, layer);
		
		assertEquals(1, camera.notifications.size());
		
		MockPCamera.Notification notification = (MockPCamera.Notification)camera.notifications.get(0);
		assertEquals(layer, notification.layer);		
		assertEquals(bounds, notification.bounds);
	}		
	
	class MockPCamera extends PCamera {
		class Notification {
		    String type;
			PBounds bounds;
			//this should really be PLayer
			PNode layer;
			
			Notification(String type, PBounds bounds, PNode layer) {
				this.bounds = bounds;
				this.layer = layer;
				this.type = type;
			}
		}
		
		List notifications = new ArrayList();
		
		public void repaintFromLayer(PBounds bounds, PNode layer) {			
			notifications.add(new Notification("repaintFromLayer", bounds, layer));
			super.repaintFromLayer(bounds, layer);
		}		
	}
}
