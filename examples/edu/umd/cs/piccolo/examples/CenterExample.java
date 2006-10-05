package edu.umd.cs.piccolo.examples;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;

public class CenterExample extends PFrame {

	public CenterExample() {
		this(null);
	}
	
	public CenterExample(PCanvas aCanvas) {
		super("CenterExample", false, aCanvas);
	}

	public void initialize() {
		PCanvas c = getCanvas();
		PLayer l = c.getLayer();
		PCamera cam = c.getCamera();
		
		cam.scaleView(2.0);
		PPath path = PPath.createRectangle(0, 0, 100, 100);
		
		l.addChild(path);
		path.translate(100, 10);
		path.scale(0.2);
		cam.animateViewToCenterBounds(path.getGlobalFullBounds(), true, 1000);
	}

	public static void main(String[] args) {
		new CenterExample();
	}
}
