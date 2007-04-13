package edu.umd.cs.piccolo.examples;
import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Iterator;
import java.util.Random;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.util.PFixedWidthStroke;

/**
 * 1000 nodes rotated continuously. Note that if you zoom to a portion of the screen where
 * you can't see any nodes the CPU usage goes down to 1%, even though all the objects are
 * still getting rotated continuously (every 20 milliseconds). This shows that the cost
 * of repainting and bounds caches is very cheap compared to the cost of drawing.
 */
public class DynamicExample extends PFrame {

	public DynamicExample() {
		this(null);
	}
	
	public DynamicExample(PCanvas aCanvas) {
		super("DynamicExample", false, aCanvas);
	}
	
	public void initialize() {
		final PLayer layer = getCanvas().getLayer();
		PRoot root = getCanvas().getRoot();
		Random r = new Random();
		for (int i = 0; i < 1000; i++) {
			final PNode n = PPath.createRectangle(0, 0, 100, 80);	
			n.translate(10000 * r.nextFloat(), 10000 * r.nextFloat());
			n.setPaint(new Color(r.nextFloat(), r.nextFloat(),r.nextFloat()));
			layer.addChild(n);
		}
		getCanvas().getCamera().animateViewToCenterBounds(layer.getGlobalFullBounds(), true, 0);
		PActivity a = new PActivity(-1, 20) {
			public void activityStep(long currentTime) {
				super.activityStep(currentTime);
				rotateNodes();
			}
		};
		root.addActivity(a);
		
		PPath p = new PPath();
		p.moveTo(0, 0);
		p.lineTo(0, 1000);
		PFixedWidthStroke stroke = new PFixedWidthStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10, new float[] {5, 2}, 0);
		p.setStroke(stroke);
		layer.addChild(p);
	}

	public void rotateNodes() {
		Iterator i = getCanvas().getLayer().getChildrenReference().iterator();
		while (i.hasNext()) {
			PNode each = (PNode) i.next();
			each.rotate(Math.toRadians(2));
		}
	}

	public static void main(String[] args) {
		new DynamicExample();
	}
}
