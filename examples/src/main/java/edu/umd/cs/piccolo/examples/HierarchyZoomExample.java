package edu.umd.cs.piccolo.examples;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;

/**
 * This example shows how to create and zoom over a node hierarchy.
 */
public class HierarchyZoomExample extends PFrame {

	public HierarchyZoomExample() {
		this(null);
	}
	
	public HierarchyZoomExample(PCanvas aCanvas) {
		super("HierarchyZoomExample", false, aCanvas);
	}
	
	public void initialize() {
		PNode root = createHierarchy(10);
		getCanvas().getLayer().addChild(root);
		getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
		getCanvas().addInputEventListener(new PBasicInputEventHandler() {
			public void mousePressed(PInputEvent event) {
				getCanvas().getCamera().animateViewToCenterBounds(event.getPickedNode().getGlobalBounds(), true, 500);
			}
		});
	}

	public PNode createHierarchy(int level) {
		PPath result = PPath.createRectangle(0, 0, 100, 100);
		
		if (level > 0) {
			PNode child = createHierarchy(level - 1);
			child.scale(0.5);
			result.addChild(child);
			child.offset(25, 25);
		}
		
		return result;
	}

	public static void main(String[] args) {
		new HierarchyZoomExample();
	}
}