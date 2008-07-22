package edu.umd.cs.piccolo.examples;
import java.awt.BasicStroke;
import java.awt.Color;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.handles.PBoundsHandle;
import edu.umd.cs.piccolox.handles.PHandle;
import edu.umd.cs.piccolox.util.PNodeLocator;

/**
 * This example show how to add the default handles to a node, and also how
 * to create your own custom handles.
 */
public class HandleExample extends PFrame {
	
	public HandleExample() {
		this(null);
	}
	
	public HandleExample(PCanvas aCanvas) {
		super("HandleExample", false, aCanvas);
	}
	
	public void initialize() {
		PPath n = PPath.createRectangle(0, 0, 100, 80);
		
		// add another node the the root as a reference point so that we can 
		// tell that our node is getting dragged, as opposed the the canvas 
		// view being panned.
		getCanvas().getLayer().addChild(PPath.createRectangle(0, 0, 100, 80));
		
		getCanvas().getLayer().addChild(n);
		
		// tell the node to show its default handles.
		PBoundsHandle.addBoundsHandlesTo(n);
		
		// The default PBoundsHandle implementation doesn't work well with PPaths that have strokes. The reason
		// for this is that the default PBoundsHandle modifies the bounds of an PNode, but when adding handles to
		// a PPath we really want it to be modifying the underlying geometry of the PPath, the shape without the
		// stroke. The solution is that we need to create handles specific to PPaths that locate themselves on the
		// paths internal geometry, not the external bounds geometry...
		
		n.setStroke(new BasicStroke(10));		
		n.setPaint(Color.green);
		
		// Here we create our own custom handle. This handle is located in the center of its parent
		// node and you can use it to drag the parent around. This handle also updates its color when
		// the is pressed/released in it. 
		final PHandle h = new PHandle(new PNodeLocator(n)) { // the default locator locates the center of a node.						
			public void dragHandle(PDimension aLocalDimension, PInputEvent aEvent) {
				localToParent(aLocalDimension);
				getParent().translate(aLocalDimension.getWidth(), aLocalDimension.getHeight());
			}			
		};
		
		h.addInputEventListener(new PBasicInputEventHandler() {
			public void mousePressed(PInputEvent aEvent) {
				h.setPaint(Color.YELLOW);
			}
			
			public void mouseReleased(PInputEvent aEvent) {
				h.setPaint(Color.RED);
			}
		});
		
		// make this handle appear a bit different then the default handle appearance.
		h.setPaint(Color.RED);
		h.setBounds(-10, -10, 20, 20);
		
		// also add our new custom handle to the node.
		n.addChild(h);
	}

	public static void main(String[] args) {
		new HandleExample();
	}
}
