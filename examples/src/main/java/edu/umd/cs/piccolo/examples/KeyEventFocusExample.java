package edu.umd.cs.piccolo.examples;
import java.awt.Color;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;

/**
 * This example shows how a node can get the keyboard focus.
 */
public class KeyEventFocusExample extends PFrame {

	public KeyEventFocusExample() {
		this(null);
	}

	public KeyEventFocusExample(PCanvas aCanvas) {
		super("KeyEventFocusExample", false, aCanvas);
	}
	
	public void initialize() {		
		// Create a green and red node and add them to canvas layer.
		PCanvas canvas = getCanvas();
		PNode nodeGreen = PPath.createRectangle(0, 0, 100, 100);
		PNode nodeRed = PPath.createRectangle(0, 0, 100, 100);
		nodeRed.translate(200, 0);
		nodeGreen.setPaint(Color.green);
		nodeRed.setPaint(Color.red);
		canvas.getLayer().addChild(nodeGreen);
		canvas.getLayer().addChild(nodeRed);
		
		// Add an event handler to the green node the prints "green mousepressed"
		// when the mouse is pressed on the green node, and "green keypressed" when
		// the key is pressed and the event listener has keyboard focus.
		nodeGreen.addInputEventListener(new PBasicInputEventHandler() {
			public void keyPressed(PInputEvent event) {
				System.out.println("green keypressed");
			}

			// Key board focus is managed by the PInputManager, accessible from
			// the root object, or from an incoming PInputEvent. In this case when
			// the mouse is pressed in the green node, then the event handler associated
			// with it will set the keyfocus to itself. Now it will receive key events
			// until someone else gets the focus.
			public void mousePressed(PInputEvent event) {
				event.getInputManager().setKeyboardFocus(event.getPath());
				System.out.println("green mousepressed");
			}
			
			public void keyboardFocusGained(PInputEvent event) {
				System.out.println("green focus gained");
			}

			public void keyboardFocusLost(PInputEvent event) {
				System.out.println("green focus lost");
			}
		});
		
		// do the same thing with the red node.
		nodeRed.addInputEventListener(new PBasicInputEventHandler() {
			public void keyPressed(PInputEvent event) {
				System.out.println("red keypressed");
			}

			public void mousePressed(PInputEvent event) {
				event.getInputManager().setKeyboardFocus(event.getPath());
				System.out.println("red mousepressed");
			}

			public void keyboardFocusGained(PInputEvent event) {
				System.out.println("red focus gained");
			}

			public void keyboardFocusLost(PInputEvent event) {
				System.out.println("red focus lost");
			}
		});		
	}

	public static void main(String[] args) {
		new KeyEventFocusExample();
	}
}
