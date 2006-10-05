package edu.umd.cs.piccolo.examples;

import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;

import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.activities.PPositionPathActivity;

/**
 * This example shows how create a simple acitivty to animate a node along a
 * general path.
 */
public class PositionPathActivityExample extends PFrame {

	public PositionPathActivityExample() {
		super();
	}

	public void initialize() {
		PLayer layer = getCanvas().getLayer();
		final PNode animatedNode = PPath.createRectangle(0, 0, 100, 80);
		layer.addChild(animatedNode);
		
		// create animation path
		GeneralPath path = new GeneralPath();
		path.moveTo(0, 0);
		path.lineTo(300, 300);
		path.lineTo(300, 0);
		path.append(new Arc2D.Float(0, 0, 300, 300, 90, -90, Arc2D.OPEN), true);
		path.closePath();

		// create node to display animation path
		PPath ppath = new PPath(path);
		layer.addChild(ppath);

		// create activity to run animation.
		PPositionPathActivity positionPathActivity = new PPositionPathActivity(5000, 0, new PPositionPathActivity.Target() {
			public void setPosition(double x, double y) {
				animatedNode.setOffset(x, y);
			}
		});
//		positionPathActivity.setSlowInSlowOut(false);
		positionPathActivity.setPositions(path);
		positionPathActivity.setLoopCount(Integer.MAX_VALUE);
		
		// add the activity.
		animatedNode.addActivity(positionPathActivity);
	}

	public static void main(String[] args) {
		new PositionPathActivityExample();
	}
}