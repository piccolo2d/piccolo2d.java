package edu.umd.cs.piccolo.examples;
import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Random;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.event.PNavigationEventHandler;

public class NavigationExample extends PFrame {

	public NavigationExample() {
		this(null);
	}
	
	public NavigationExample(PCanvas aCanvas) {
		super("NavigationExample", false, aCanvas);
	}
	
	public void initialize() {		
		PLayer layer = getCanvas().getLayer();
		
		Random random = new Random();
		for (int i = 0; i < 1000; i++) {
			PPath each = PPath.createRectangle(0, 0, 100, 80);
			each.scale(random.nextFloat() * 2);
			each.offset(random.nextFloat() * 10000, random.nextFloat() * 10000);
			each.setPaint(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()));
			each.setStroke(new BasicStroke(1 + (10 * random.nextFloat())));
			each.setStrokePaint(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()));
			layer.addChild(each);
		}
		getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
		getCanvas().addInputEventListener(new PNavigationEventHandler());
	}

	public static void main(String[] args) {
		new NavigationExample();
	}
}
