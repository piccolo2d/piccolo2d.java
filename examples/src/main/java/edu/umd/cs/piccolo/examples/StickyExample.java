package edu.umd.cs.piccolo.examples;
import java.awt.Color;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.handles.PBoundsHandle;

public class StickyExample extends PFrame {

	public StickyExample() {
		this(null);
	}

	public StickyExample(PCanvas aCanvas) {
		super("StickyExample", false, aCanvas);
	}
	
	public void initialize() {	
		PPath sticky = PPath.createRectangle(0, 0, 50, 50);
		sticky.setPaint(Color.YELLOW);
		sticky.setStroke(null);
		PBoundsHandle.addBoundsHandlesTo(sticky);
		getCanvas().getLayer().addChild(PPath.createRectangle(0, 0, 100, 80));
		getCanvas().getCamera().addChild(sticky);
	}

	public static void main(String[] args) {
		new StickyExample();
	}
}
