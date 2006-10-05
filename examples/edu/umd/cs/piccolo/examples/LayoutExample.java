package edu.umd.cs.piccolo.examples;
import java.awt.Color;
import java.util.Iterator;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.handles.PBoundsHandle;

/**
 * This example shows how to create a node that will automatically 
 * layout its children.
 */
public class LayoutExample extends PFrame {

	public LayoutExample() {
		this(null);
	}
	
	public LayoutExample(PCanvas aCanvas) {
		super("LayoutExample", false, aCanvas);
	}
	
	public void initialize() {		
		// Create a new node and override its validateLayoutAfterChildren method so
		// that it lays out its children in a row from left to
		// right. 
		
		final PNode layoutNode = new PNode() {
			public void layoutChildren() {				
				double xOffset = 0;
				double yOffset = 0;

				Iterator i = getChildrenIterator(); 							
				while (i.hasNext()) {
					PNode each = (PNode) i.next();
					each.setOffset(xOffset - each.getX(), yOffset);
					xOffset += each.getWidth();
				}
			}
		};
		
		layoutNode.setPaint(Color.red);

		// add some children to the layout node.
		for (int i = 0; i < 1000; i++) {
			// create child to add to the layout node.
			PNode each = PPath.createRectangle(0, 0, 100, 80);

			// add the child to the layout node.		
			layoutNode.addChild(each);
		}
	
		PBoundsHandle.addBoundsHandlesTo(layoutNode.getChild(0));
		
		// add layoutNode to the root so it will be displayed.
		getCanvas().getLayer().addChild(layoutNode);
	}

	public static void main(String[] args) {
		new LayoutExample();
	}
}
