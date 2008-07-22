package edu.umd.cs.piccolo.examples;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;

/**
 * Simple example showing one way to create a link between two nodes.
 * 
 * @author Jesse Grosjean
 */
public class NodeLinkExample extends PFrame {
	
	PNode node1;
	PNode node2;
	PPath link;
	
	public NodeLinkExample() {
		this(null);
	}
	
	public NodeLinkExample(PCanvas aCanvas) {
		super("NodeLinkExample", false, aCanvas);
	}

	public void initialize() {	
		PCanvas canvas = getCanvas();
		
		canvas.removeInputEventListener(canvas.getPanEventHandler());
		canvas.addInputEventListener(new PDragEventHandler());
		
		PNode layer = canvas.getLayer();
		
		node1 = PPath.createEllipse(0, 0, 100, 100);
		node2 = PPath.createEllipse(0, 0, 100, 100);
		link = PPath.createLine(50, 50, 50, 50);
		link.setPickable(false);
		layer.addChild(node1);
		layer.addChild(node2);
		layer.addChild(link);
		
		node2.translate(200, 200);
		
		node1.addPropertyChangeListener(PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				updateLink();
			}
		});
		
		node2.addPropertyChangeListener(PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				updateLink();
			}
		});
	}

	public void updateLink() {
		Point2D p1 = node1.getFullBoundsReference().getCenter2D();
		Point2D p2 = node2.getFullBoundsReference().getCenter2D();
		Line2D line = new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		link.setPathTo(line);
	}
	
	public static void main(String[] args) {
		new NodeLinkExample();
	}
}