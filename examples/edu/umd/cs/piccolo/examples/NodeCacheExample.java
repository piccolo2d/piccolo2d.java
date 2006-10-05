package edu.umd.cs.piccolo.examples;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.nodes.PNodeCache;

public class NodeCacheExample extends PFrame {

	public NodeCacheExample() {
		this(null);
	}
	
	public NodeCacheExample(PCanvas aCanvas) {
		super("NodeCacheExample", false, aCanvas);
	}

	public void initialize() {		
		PCanvas canvas = getCanvas();
		
		PPath circle = PPath.createEllipse(0, 0, 100, 100);
		circle.setStroke(new BasicStroke(10));
		circle.setPaint(Color.YELLOW);
		
		PPath rectangle = PPath.createRectangle(-100, -50, 100, 100);
		rectangle.setStroke(new BasicStroke(15));
		rectangle.setPaint(Color.ORANGE);
		
		PNodeCache cache = new PNodeCache();
		cache.addChild(circle);
		cache.addChild(rectangle);
		
		cache.invalidateCache();
		
		canvas.getLayer().addChild(cache);
		canvas.removeInputEventListener(canvas.getPanEventHandler());
		canvas.addInputEventListener(new PDragEventHandler());
	}

	public static void main(String[] args) {
		new NodeCacheExample();
	}
}
