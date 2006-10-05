package edu.umd.cs.piccolo.examples;
import java.awt.BasicStroke;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * This example shows how to create and install a custom event listener that draws
 * rectangles.
 */
public class EventHandlerExample extends PFrame {

	public EventHandlerExample() {
		this(null);
	}

	public EventHandlerExample(PCanvas aCanvas) {
		super("EventHandlerExample", false, aCanvas);
	}
	
	public void initialize() {
		super.initialize();
		
		// Create a new event handler the creates new rectangles on
		// mouse pressed, dragged, release.
		PBasicInputEventHandler rectEventHandler = createRectangleEventHandler();
		
		// Make the event handler only work with BUTTON1 events, so that it does
		// not conflict with the zoom event handler that is installed by default.
		rectEventHandler.setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
		
		// Remove the pan event handler that is installed by default so that it
		// does not conflict with our new rectangle creation event handler.
		getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
		
		// Register our new event handler.
		getCanvas().addInputEventListener(rectEventHandler);		
	}

	public PBasicInputEventHandler createRectangleEventHandler() {
		
		// Create a new subclass of PBasicEventHandler that creates new PPath nodes
		// on mouse pressed, dragged, and released sequences. Not that subclassing
		// PDragSequenceEventHandler would make this class easier to implement, but
		// here you can see how to do it from scratch.
		return new PBasicInputEventHandler() {
			
			// The rectangle that is currently getting created.
			protected PPath rectangle;
			
			// The mouse press location for the current pressed, drag, release sequence.
			protected Point2D pressPoint;
			
			// The current drag location.
			protected Point2D dragPoint;
		
			public void mousePressed(PInputEvent e) {
				super.mousePressed(e);			
				
				PLayer layer = getCanvas().getLayer();

				// Initialize the locations.
				pressPoint = e.getPosition();
				dragPoint = pressPoint; 			
				
				// create a new rectangle and add it to the canvas layer so that
				// we can see it.
				rectangle = new PPath();
				rectangle.setStroke(new BasicStroke((float)(1/ e.getCamera().getViewScale())));
				layer.addChild(rectangle);
				
				// update the rectangle shape.
				updateRectangle();
			}
			
			public void mouseDragged(PInputEvent e) {
				super.mouseDragged(e);
				// update the drag point location.
				dragPoint = e.getPosition();	
				
				// update the rectangle shape.
				updateRectangle();
			}
		
			public void mouseReleased(PInputEvent e) {
				super.mouseReleased(e);
				// update the rectangle shape.
				updateRectangle();
				rectangle = null;
			}	
		
			public void updateRectangle() {
				// create a new bounds that contains both the press and current
				// drag point.
				PBounds b = new PBounds();
				b.add(pressPoint);
				b.add(dragPoint);
				
				// Set the rectangles bounds.
				rectangle.setPathTo(b);
			}
		};
	}
	
	public static void main(String[] args) {
		new EventHandlerExample();
	}
}
