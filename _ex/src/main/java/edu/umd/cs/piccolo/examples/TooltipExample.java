package edu.umd.cs.piccolo.examples;

import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.PFrame;

/**
 * Simple example of one way to add tooltips
 * 
 * @author jesse
 */
public class TooltipExample extends PFrame {
	
	public TooltipExample() {
		this(null);
	}

	public TooltipExample(PCanvas aCanvas) {
		super("TooltipExample", false, aCanvas);
	}

	public void initialize() {
		PNode n1 = PPath.createEllipse(0, 0, 100, 100);
		PNode n2 = PPath.createRectangle(300, 200, 100, 100);
		
		n1.addAttribute("tooltip", "node 1");
		n2.addAttribute("tooltip", "node 2");
		getCanvas().getLayer().addChild(n1);
		getCanvas().getLayer().addChild(n2);
		
		final PCamera camera = getCanvas().getCamera();
		final PText tooltipNode = new PText();
		
		tooltipNode.setPickable(false);
		camera.addChild(tooltipNode);
		
		camera.addInputEventListener(new PBasicInputEventHandler() {	
			public void mouseMoved(PInputEvent event) {
				updateToolTip(event);
			}

			public void mouseDragged(PInputEvent event) {
				updateToolTip(event);
			}

			public void updateToolTip(PInputEvent event) {
				PNode n = event.getInputManager().getMouseOver().getPickedNode();
				String tooltipString = (String) n.getAttribute("tooltip");
				Point2D p = event.getCanvasPosition();
				
				event.getPath().canvasToLocal(p, camera);
				
				tooltipNode.setText(tooltipString);
				tooltipNode.setOffset(p.getX() + 8, 
									  p.getY() - 8);
			}
		});
	}
	
	public static void main(String[] argv) {
		new TooltipExample();
	}
}