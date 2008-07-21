package edu.umd.cs.piccolo.examples;

import java.awt.Color;
import java.util.Iterator;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.handles.PBoundsHandle;
import edu.umd.cs.piccolox.handles.PHandle;
import edu.umd.cs.piccolox.util.PBoundsLocator;

/**
* This example shows another way to create sticky handles. These handles are
 * not added as children to the object that they manipulate. Instead they are
 * added to the camera the views that objects. This means that they will not be
 * affected by the cameras view transform, and so will stay the same size when
 * the view is zoomed. They will also be drawn on top of all other objects, even
 * if those objects overlap the object that they manipulate. For this setup we
 * need to add and updateHandles activity that makes sure to relocate the handle
 * after any change. Another way to do this would be to add change listeners to
 * the camera and the node that they manipulate and only update them then. But
 * this method is easier and should be plenty efficient for normal use.
 * 
 * @author jesse
 */
public class StickyHandleLayerExample extends PFrame {
	
	public StickyHandleLayerExample() {
		this(null);
	}

	public StickyHandleLayerExample(PCanvas aCanvas) {
		super("StickyHandleLayerExample", false, aCanvas);
	}

	public void initialize() {
		PCanvas c = getCanvas();
		
		PActivity updateHandles = new PActivity(-1, 0) {
			protected void activityStep(long elapsedTime) {
				super.activityStep(elapsedTime);
				
				PRoot root = getActivityScheduler().getRoot();

				if (root.getPaintInvalid() || root.getChildPaintInvalid()) {	
					Iterator i = getCanvas().getCamera().getChildrenIterator();
					while (i.hasNext()) {
						PNode each = (PNode) i.next();
						if (each instanceof PHandle) {
							PHandle handle = (PHandle) each;
							handle.relocateHandle();
						}
					}
				}
			}
		};
		
		PPath rect = PPath.createRectangle(0, 0, 100, 100);
		rect.setPaint(Color.RED);
		c.getLayer().addChild(rect);

		c.getCamera().addChild(new PBoundsHandle(PBoundsLocator.createNorthEastLocator(rect)));
		c.getCamera().addChild(new PBoundsHandle(PBoundsLocator.createNorthWestLocator(rect)));
		c.getCamera().addChild(new PBoundsHandle(PBoundsLocator.createSouthEastLocator(rect)));
		c.getCamera().addChild(new PBoundsHandle(PBoundsLocator.createSouthWestLocator(rect)));
		
		c.getRoot().getActivityScheduler().addActivity(updateHandles, true);
	}
	
	public static void main(String[] args) {
		new StickyHandleLayerExample();
	}
}
