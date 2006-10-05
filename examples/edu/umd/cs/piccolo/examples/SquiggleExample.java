
package edu.umd.cs.piccolo.examples;
import java.awt.BasicStroke;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;

public class SquiggleExample extends PFrame {
	
	private PLayer layer;

	public SquiggleExample() {
		this(null);
	}

	public SquiggleExample(PCanvas aCanvas) {
		super("SquiggleExample", false, aCanvas);
	}
	
	public void initialize() {
		super.initialize(); 	
		PBasicInputEventHandler squiggleEventHandler = createSquiggleEventHandler();
		squiggleEventHandler.setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
		getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
		getCanvas().addInputEventListener(squiggleEventHandler);
		layer = getCanvas().getLayer();
	}

	public PBasicInputEventHandler createSquiggleEventHandler() {
		return new PDragSequenceEventHandler() {

			protected PPath squiggle;
		
			public void startDrag(PInputEvent e) {
				super.startDrag(e); 		
				
				Point2D p = e.getPosition();

				squiggle = new PPath();
				squiggle.moveTo((float)p.getX(), (float)p.getY());
				squiggle.setStroke(new BasicStroke((float)(1/ e.getCamera().getViewScale())));
				layer.addChild(squiggle);
			}
			
			public void drag(PInputEvent e) {
				super.drag(e);				
				updateSquiggle(e);
			}
		
			public void endDrag(PInputEvent e) {
				super.endDrag(e);
				updateSquiggle(e);
				squiggle = null;
			}	
				
			public void updateSquiggle(PInputEvent aEvent) {
				Point2D p = aEvent.getPosition();
				squiggle.lineTo((float)p.getX(), (float)p.getY());
			}
		};
	}
		
	public static void main(String[] args) {
		new SquiggleExample();
	}
}

