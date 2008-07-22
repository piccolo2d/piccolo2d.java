package edu.umd.cs.piccolo.examples;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Random;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;

/**
 * Click on a node and the camera will pan the minimum distance to bring that node fully into
 * the cameras view.
 */
public class PanToExample extends PFrame {

	public PanToExample() {
		this(null);
	}
	
	public PanToExample(PCanvas aCanvas) {
		super("PanToExample", false, aCanvas);
	}
		
	public void initialize() {
		
		PPath eacha = PPath.createRectangle(50, 50, 300, 300);
		eacha.setPaint(Color.red);
		getCanvas().getLayer().addChild(eacha);
				
		eacha = PPath.createRectangle(-50, -50, 100, 100);
		eacha.setPaint(Color.green);
		getCanvas().getLayer().addChild(eacha);

		eacha = PPath.createRectangle(350, 350, 100, 100);
		eacha.setPaint(Color.green);
		getCanvas().getLayer().addChild(eacha);

		
		getCanvas().getCamera().addInputEventListener(new PBasicInputEventHandler() {
			public void mousePressed(PInputEvent event) {
				if (!(event.getPickedNode() instanceof PCamera)) {
					event.setHandled(true);
					getCanvas().getCamera().animateViewToPanToBounds(event.getPickedNode().getGlobalFullBounds(), 500);
				}
			}
		});
		
		PLayer layer = getCanvas().getLayer();
		
		Random random = new Random();
		for (int i = 0; i < 1000; i++) {
			PPath each = PPath.createRectangle(0, 0, 100, 80);
			each.scale(random.nextFloat() * 2);
			each.offset(random.nextFloat() * 10000, random.nextFloat() * 10000);
			each.setPaint(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()));
			each.setStroke(new BasicStroke(1 + (10 * random.nextFloat())));
			each.setStrokePaint(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()));
			layer.addChild(each);
		}
		
		
		getCanvas().removeInputEventListener(getCanvas().getZoomEventHandler());
	}
		
	public static void main(String[] args) {
		new PanToExample();
	}	
}
