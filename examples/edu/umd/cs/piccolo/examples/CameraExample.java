package edu.umd.cs.piccolo.examples;
import java.awt.Color;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.handles.PBoundsHandle;

/**
 * This example shows how to create internal cameras
 */
public class CameraExample extends PFrame {

	public CameraExample() {
		this(null);
	}
	
	public CameraExample(PCanvas aCanvas) {
		super("CameraExample", false, aCanvas);
	}
	
	public void initialize() {
		PLayer l = new PLayer();
		PPath n = PPath.createEllipse(0, 0, 100, 80);
		n.setPaint(Color.red);
		n.setStroke(null);
		PBoundsHandle.addBoundsHandlesTo(n);
		l.addChild(n);
		n.translate(200, 200);
		
		PCamera c = new PCamera();
		c.setBounds(0, 0, 100, 80);
		c.scaleView(0.1);
		c.addLayer(l);
		PBoundsHandle.addBoundsHandlesTo(c);
		c.setPaint(Color.yellow);

		getCanvas().getLayer().addChild(l);
		getCanvas().getLayer().addChild(c); 	
	}
		
	public static void main(String[] args) {
		new CameraExample();
	}	
}
