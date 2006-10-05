package edu.umd.cs.piccolo.examples;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.PFrame;

/**
 * This example shows how to create a vertical and a horizontal bar which can
 * move with your graph and always stays on view.
 * 
 * @author Tao
 */
public class ChartLabelExample extends PFrame {
	final int nodeHeight = 15;
	final int nodeWidth = 30;

	//Row Bar
	PLayer rowBarLayer;

	//Colume Bar
	PLayer colBarLayer;

	public ChartLabelExample() {
		this(null);
	}

	public ChartLabelExample(PCanvas aCanvas) {
		super("ChartLabelExample", false, aCanvas);
	}

	public void initialize() {
		//create bar layers
		rowBarLayer = new PLayer();
		colBarLayer = new PLayer();

		//create bar nodes
		for (int i = 0; i < 10; i++) {
			//create row bar with node row1, row2,...row10
			PText p = new PText("Row " + i);
			p.setX(0);
			p.setY(nodeHeight * i + nodeHeight);
			p.setPaint(Color.white);
			colBarLayer.addChild(p);

			//create col bar with node col1, col2,...col10
			p = new PText("Col " + i);
			p.setX(nodeWidth * i + nodeWidth);
			p.setY(0);
			p.setPaint(Color.white);
			rowBarLayer.addChild(p);
		}

		//add bar layers to camera
		getCanvas().getCamera().addChild(rowBarLayer);
		getCanvas().getCamera().addChild(colBarLayer);

		//create matrix nodes
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				PPath path = PPath.createRectangle(nodeWidth * j + nodeWidth,
						nodeHeight * i + nodeHeight, nodeWidth - 1,
						nodeHeight - 1);
				getCanvas().getLayer().addChild(path);
			}
		}

		//catch drag event and move bars corresponding
		getCanvas().addInputEventListener(new PDragSequenceEventHandler() {
			Point2D oldP, newP;

			public void mousePressed(PInputEvent aEvent) {
				oldP = getCanvas().getCamera().getViewBounds().getCenter2D();
			}

			public void mouseReleased(PInputEvent aEvent) {
				newP = getCanvas().getCamera().getViewBounds().getCenter2D();
				colBarLayer.translate(0, (oldP.getY() - newP.getY())
						/ getCanvas().getLayer().getScale());
				rowBarLayer.translate((oldP.getX() - newP.getX())
						/ getCanvas().getLayer().getScale(), 0);
			}
		});
	}

	public static void main(String[] args) {
		new ChartLabelExample();
	}
}