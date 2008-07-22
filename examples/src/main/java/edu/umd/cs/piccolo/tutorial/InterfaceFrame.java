package edu.umd.cs.piccolo.tutorial;

import java.awt.Color;
import java.awt.Graphics2D;

import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolo.util.*;
import edu.umd.cs.piccolox.*;

public class InterfaceFrame extends PFrame {
	
	public void initialize() {
		// Remove the Default pan event handler and add a drag event handler
		// so that we can drag the nodes around individually.
		getCanvas().setPanEventHandler(null);
		getCanvas().addInputEventListener(new PDragEventHandler());
		
		// Add Some Default Nodes

		// Create a node.
		PNode aNode = new PNode();

		// A node will not be visible until its bounds and brush are set.
		aNode.setBounds(0, 0, 100, 80);
		aNode.setPaint(Color.RED);

		// A node needs to be a descendent of the root to be displayed.
		PLayer layer = getCanvas().getLayer();
		layer.addChild(aNode);
			
		// A node can have child nodes added to it.
		PNode anotherNode = new PNode();
		anotherNode.setBounds(0, 0, 100, 80);
		anotherNode.setPaint(Color.YELLOW);
		aNode.addChild(anotherNode);
			
		// The base bounds of a node are easy to change.  Changing the bounds
		// of a node will not affect it's children.
		aNode.setBounds(-10, -10, 200, 110);
	
		// Each node has a transform that can be used to modify the position,
		// scale or rotation of a node.  Changing a node's transform, will
		// transform all of its children as well.
		aNode.translate(100, 100);
		aNode.scale(1.5f);
		aNode.rotate(45);

		// Add a couple of PPath nodes and a PText node.
		layer.addChild(PPath.createEllipse(0, 0, 100, 100));
		layer.addChild(PPath.createRectangle(0, 100, 100, 100));
		layer.addChild(new PText("Hello World"));

		// Here we create a PImage node that displays a thumbnail image
		// of the root node. Then we add the new PImage to the main layer.
		PImage image = new PImage(layer.toImage(300, 300, null));
		layer.addChild(image);

		// Create a New Node using Composition

		PNode myCompositeFace = PPath.createRectangle(0, 0, 100, 80);
	
		// Create parts for the face.
		PNode eye1 = PPath.createEllipse(0, 0, 20, 20);
		eye1.setPaint(Color.YELLOW);
		PNode eye2 = (PNode) eye1.clone();
		PNode mouth = PPath.createRectangle(0, 0, 40, 20);
		mouth.setPaint(Color.BLACK);
	
		// Add the face parts.
		myCompositeFace.addChild(eye1);
		myCompositeFace.addChild(eye2);
		myCompositeFace.addChild(mouth);
	
		// Don't want anyone grabbing out our eye's.
		myCompositeFace.setChildrenPickable(false);
	
		// Position the face parts.
		eye2.translate(25, 0);
		mouth.translate(0, 30);
	
		// Set the face bounds so that it neatly contains the face parts.
		PBounds b = myCompositeFace.getUnionOfChildrenBounds(null);
		b.inset(-5, -5);
		myCompositeFace.setBounds(b);
	
		// Opps its to small, so scale it up.
		myCompositeFace.scale(1.5);
	
		layer.addChild(myCompositeFace);

		// Create a New Node using Inheritance.
		ToggleShape ts = new ToggleShape();
		ts.setPaint(Color.ORANGE);
		layer.addChild(ts);
	}
	
	class ToggleShape extends PPath {
		
		private boolean fIsPressed = false;

		public ToggleShape() {
			setPathToEllipse(0, 0, 100, 80);
			
			addInputEventListener(new PBasicInputEventHandler() {
				public void mousePressed(PInputEvent event) {
					super.mousePressed(event);
					fIsPressed = true;
					repaint();
				}
				public void mouseReleased(PInputEvent event) {
					super.mouseReleased(event);
					fIsPressed = false;
					repaint();
				}
			});
		}
		
		protected void paint(PPaintContext paintContext) {
			if (fIsPressed) {
				Graphics2D g2 = paintContext.getGraphics();
				g2.setPaint(getPaint());
				g2.fill(getBoundsReference());
			} else {
				super.paint(paintContext);
			}
		}
	}
	
	public static void main(String[] args) {
		new InterfaceFrame();
	}
}
