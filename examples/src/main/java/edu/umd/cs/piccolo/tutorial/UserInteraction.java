package edu.umd.cs.piccolo.tutorial;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolo.util.*;
import edu.umd.cs.piccolox.*;

public class UserInteraction extends PFrame {

	public UserInteraction() {
		super();
	}

	public void initialize() {
		// Create a Camera Event Listener.

		// Remove the pan event handler that is installed by default so that it
		// does not conflict with our new squiggle handler.
		getCanvas().setPanEventHandler(null);

		// Create a squiggle handler and register it with the Canvas.
		PBasicInputEventHandler squiggleHandler = new SquiggleHandler(getCanvas());
		getCanvas().addInputEventListener(squiggleHandler);
		
		// Create a Node Event Listener.

		// Create a green rectangle node.
		PNode nodeGreen = PPath.createRectangle(0, 0, 100, 100);
		nodeGreen.setPaint(Color.GREEN);
		getCanvas().getLayer().addChild(nodeGreen);

// Attach event handler directly to the node.
nodeGreen.addInputEventListener(new PBasicInputEventHandler() {
	public void mousePressed(PInputEvent event) {
		event.getPickedNode().setPaint(Color.ORANGE);
		event.getInputManager().setKeyboardFocus(event.getPath());
		event.setHandled(true);
	}
	public void mouseDragged(PInputEvent event) {
		PNode aNode = event.getPickedNode();
		PDimension delta = event.getDeltaRelativeTo(aNode);
		aNode.translate(delta.width, delta.height);
		event.setHandled(true);
	}
	public void mouseReleased(PInputEvent event) {
		event.getPickedNode().setPaint(Color.GREEN);
		event.setHandled(true);
	}
	public void keyPressed(PInputEvent event) {
		PNode node = event.getPickedNode();
		switch (event.getKeyCode()) {
			case KeyEvent.VK_UP:
				node.translate(0, -10f);
				break;
			case KeyEvent.VK_DOWN:
				node.translate(0, 10f);
				break;
			case KeyEvent.VK_LEFT:
				node.translate(-10f, 0);
				break;
			case KeyEvent.VK_RIGHT:
				node.translate(10f, 0);
				break;
		}
	}
});
	}

	public class SquiggleHandler extends PDragSequenceEventHandler {
		protected PCanvas canvas;
		
		// The squiggle that is currently getting created.
		protected PPath squiggle;

		public SquiggleHandler(PCanvas aCanvas) {
			canvas = aCanvas;
			setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
		}
		
		public void startDrag(PInputEvent e) {
			super.startDrag(e);

			Point2D p = e.getPosition();

			// Create a new squiggle and add it to the canvas.
			squiggle = new PPath();
			squiggle.moveTo((float) p.getX(), (float) p.getY());
			squiggle.setStroke(new BasicStroke((float) (1 / e.getCamera().getViewScale())));
			canvas.getLayer().addChild(squiggle);
			
			// Reset the keydboard focus.
			e.getInputManager().setKeyboardFocus(null);
		}

		public void drag(PInputEvent e) {
			super.drag(e);
			// Update the squiggle while dragging.
			updateSquiggle(e);
		}

		public void endDrag(PInputEvent e) {
			super.endDrag(e);
			// Update the squiggle one last time.
			updateSquiggle(e);
			squiggle = null;
		}

		public void updateSquiggle(PInputEvent aEvent) {
			// Add a new segment to the squiggle from the last mouse position
			// to the current mouse position.
			Point2D p = aEvent.getPosition();
			squiggle.lineTo((float) p.getX(), (float) p.getY());
		}
	}

	public static void main(String[] args) {
		new UserInteraction();
	}
}
