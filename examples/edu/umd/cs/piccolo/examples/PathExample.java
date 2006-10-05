package edu.umd.cs.piccolo.examples;
import java.awt.BasicStroke;
import java.awt.Color;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.handles.PStickyHandleManager;
import edu.umd.cs.piccolox.util.PFixedWidthStroke;

public class PathExample extends PFrame {

	public PathExample() {
		this(null);
	}
	
	public PathExample(PCanvas aCanvas) {
		super("PathExample", false, aCanvas);
	}
	
	public void initialize() {		
		PPath n1 = PPath.createRectangle(0, 0, 100, 80);
		PPath n2 = PPath.createEllipse(100, 100, 200, 34);
		PPath n3 = new PPath();
		n3.moveTo(0, 0);
		n3.lineTo(20, 40);
		n3.lineTo(10, 200);
		n3.lineTo(155.444f, 33.232f);
		n3.closePath();
		n3.setPaint(Color.yellow);
		
		n1.setStroke(new BasicStroke(5));
		n1.setStrokePaint(Color.red);
		n2.setStroke(new PFixedWidthStroke());
		n3.setStroke(new PFixedWidthStroke());
//		n3.setStroke(null);
			
		getCanvas().getLayer().addChild(n1);
		getCanvas().getLayer().addChild(n2);		
		getCanvas().getLayer().addChild(n3);	
		
		// create a set of bounds handles for reshaping n3, and make them
		// sticky relative to the getCanvas().getCamera().
		new PStickyHandleManager(getCanvas().getCamera(), n3);
		
		getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
		getCanvas().addInputEventListener(new PDragEventHandler()); 		
	}

	public static void main(String[] args) {
		new PathExample();
	}
}
