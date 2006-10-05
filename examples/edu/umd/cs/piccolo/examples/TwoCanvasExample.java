package edu.umd.cs.piccolo.examples;
import java.awt.Color;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.handles.PBoundsHandle;

public class TwoCanvasExample extends PFrame {

	public TwoCanvasExample() {
		this(null);
	}
	
	public TwoCanvasExample(PCanvas aCanvas) {
		super("TwoCanvasExample", false, aCanvas);
	}
	
	public void initialize() {		
		PRoot root = getCanvas().getRoot();
		PLayer layer = getCanvas().getLayer();
		
		PNode n = PPath.createRectangle(0, 0, 100, 80);
		PNode sticky = PPath.createRectangle(0, 0, 50, 50);
		PBoundsHandle.addBoundsHandlesTo(n);
		sticky.setPaint(Color.YELLOW);
		PBoundsHandle.addBoundsHandlesTo(sticky);
		
		layer.addChild(n);
		getCanvas().getCamera().addChild(sticky);
				
		PCamera otherCamera = new PCamera();
		otherCamera.addLayer(layer);
		root.addChild(otherCamera); 	
		
		PCanvas other = new PCanvas();
		other.setCamera(otherCamera);
		PFrame result = new PFrame("TwoCanvasExample", false, other);
		result.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		result.setLocation(500, 100);
	}

	public static void main(String[] args) {
		new TwoCanvasExample();
	}
}
