package edu.umd.cs.piccolo.examples;

import java.awt.Color;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * Quick example of how to use a clip.
 */
public class ClipExample extends PFrame {

	public ClipExample() {
		this(null);
	}
	
	public ClipExample(PCanvas aCanvas) {
		super("ClipExample", false, aCanvas);
	}
	
	public void initialize() {
		PClip clip = new PClip();
		clip.setPathToEllipse(0, 0, 100, 100);
		clip.setPaint(Color.red);
		
		clip.addChild(PPath.createRectangle(20, 20, 100, 50));
		getCanvas().getLayer().addChild(clip);

		getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
		getCanvas().addInputEventListener(new PDragEventHandler());
	}
		
	public static void main(String[] args) {
		new ClipExample();
	}	
}
