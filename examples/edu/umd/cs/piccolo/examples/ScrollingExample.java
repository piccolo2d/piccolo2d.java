package edu.umd.cs.piccolo.examples;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.swing.PDefaultScrollDirector;
import edu.umd.cs.piccolox.swing.PScrollDirector;
import edu.umd.cs.piccolox.swing.PScrollPane;
import edu.umd.cs.piccolox.swing.PViewport;

/**
 * This creates a simple scene and allows switching between
 * traditional scrolling where the scrollbars control the view
 * and alternate scrolling where the scrollbars control the
 * document.  In laymans terms - in traditional window scrolling the stuff
 * in the window moves in the opposite direction of the scroll bars and in
 * document scrolling the stuff moves in the same direction as the scrollbars.
 *
 * Toggle buttons are provided to toggle between these two types
 * of scrolling.
 *
 * @author Lance Good
 * @author Ben Bederson
 */
public class ScrollingExample extends PFrame {

	public ScrollingExample() {
		this(null);
	}

	public ScrollingExample(PCanvas aCanvas) {
		super("ScrollingExample", false, aCanvas);
	}

	public void initialize() {
		final PCanvas canvas = getCanvas();

		final PScrollPane scrollPane = new PScrollPane(canvas);
		getContentPane().add(scrollPane);
		
		final PViewport viewport = (PViewport) scrollPane.getViewport();
		final PScrollDirector windowSD = viewport.getScrollDirector();
		final PScrollDirector documentSD = new DocumentScrollDirector();

		// Make some rectangles on the surface so we can see where we are
		for (int x = 0; x < 20; x++) {
			for (int y = 0; y < 20; y++) {
				if (((x + y) % 2) == 0) {
					PPath path = PPath.createRectangle(50 * x, 50 * y, 40, 40);
					path.setPaint(Color.blue);
					path.setStrokePaint(Color.black);
					canvas.getLayer().addChild(path);
				}
				else if (((x + y) % 2) == 1) {
					PPath path = PPath.createEllipse(50 * x, 50 * y, 40, 40);
					path.setPaint(Color.blue);
					path.setStrokePaint(Color.black);
					canvas.getLayer().addChild(path);
				}
			}
		}
		
		// Now, create the toolbar
		JToolBar toolBar = new JToolBar();
		JToggleButton window = new JToggleButton("Window Scrolling");
		JToggleButton document = new JToggleButton("Document Scrolling");
		ButtonGroup bg = new ButtonGroup();
		bg.add(window);
		bg.add(document);
		toolBar.add(window);
		toolBar.add(document);
		toolBar.setFloatable(false);
		window.setSelected(true);
		window.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				viewport.setScrollDirector(windowSD);
				viewport.fireStateChanged();
				scrollPane.revalidate();
			}
		});
		document.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				viewport.setScrollDirector(documentSD);
				viewport.fireStateChanged();
				scrollPane.revalidate();
			}
		});
		getContentPane().add(toolBar, BorderLayout.NORTH);
		
		getContentPane().validate();
	}

	/**
	 * A modified scroll director that performs document based scroling
	 * rather than window based scrolling (ie. the scrollbars act in
	 * the inverse direction as normal)
	 */
	public class DocumentScrollDirector extends PDefaultScrollDirector {

		/**
		 * Get the View position given the specified camera bounds - modified
		 * such that:
		 *
		 * Rather than finding the distance from the upper left corner
		 * of the window to the upper left corner of the document -
		 * we instead find the distance from the lower right corner
		 * of the window to the upper left corner of the document
		 * THEN we subtract that value from total document width so
		 * that the position is inverted
		 *
		 * @param viewBounds The bounds for which the view position will be computed
		 * @return The view position
		 */
		public Point getViewPosition(Rectangle2D viewBounds) {
			Point pos = new Point();
			if (camera != null) {
				// First we compute the union of all the layers
				PBounds layerBounds = new PBounds();
				java.util.List layers = camera.getLayersReference();
				for (Iterator i = layers.iterator(); i.hasNext();) {
					PLayer layer = (PLayer) i.next();
					layerBounds.add(layer.getFullBoundsReference());
				}

				// Then we put the bounds into camera coordinates and
				// union the camera bounds
				camera.viewToLocal(layerBounds);
				layerBounds.add(viewBounds);

				// Rather than finding the distance from the upper left corner
				// of the window to the upper left corner of the document -
				// we instead find the distance from the lower right corner
				// of the window to the upper left corner of the document
				// THEN we measure the offset from the lower right corner
				// of the document
				pos.setLocation(
					(int) (layerBounds.getWidth() - (viewBounds.getX() + viewBounds.getWidth() - layerBounds.getX()) + 0.5),
					(int) (layerBounds.getHeight() - (viewBounds.getY() + viewBounds.getHeight() - layerBounds.getY()) + 0.5));
			}

			return pos;

		}

		/**
		 * We do the same thing we did in getViewPosition above to flip the
		 * document-window position relationship
		 *
		 * @param x The new x position
		 * @param y The new y position
		 */
		public void setViewPosition(double x, double y) {
			if (camera != null) {
				// If a scroll is in progress - we ignore new scrolls -
				// if we didn't, since the scrollbars depend on the camera location
				// we can end up with an infinite loop
				if (!scrollInProgress) {
					scrollInProgress = true;

					// Get the union of all the layers' bounds
					PBounds layerBounds = new PBounds();
					java.util.List layers = camera.getLayersReference();
					for (Iterator i = layers.iterator(); i.hasNext();) {
						PLayer layer = (PLayer) i.next();
						layerBounds.add(layer.getFullBoundsReference());
					}

					PAffineTransform at = camera.getViewTransform();
					at.transform(layerBounds,layerBounds);

					// Union the camera view bounds
					PBounds viewBounds = camera.getBoundsReference();
					layerBounds.add(viewBounds);

					// Now find the new view position in view coordinates -
					// This is basically the distance from the lower right
					// corner of the window to the upper left corner of the
					// document 
					// We then measure the offset from the lower right corner
					// of the document
					Point2D newPoint =
						new Point2D.Double(
							layerBounds.getX() + layerBounds.getWidth() - (x + viewBounds.getWidth()),
							layerBounds.getY() + layerBounds.getHeight() - (y + viewBounds.getHeight()));

					// Now transform the new view position into global coords
					camera.localToView(newPoint);

					// Compute the new matrix values to put the camera at the
					// correct location
					double newX = - (at.getScaleX() * newPoint.getX() + at.getShearX() * newPoint.getY());
					double newY = - (at.getShearY() * newPoint.getX() + at.getScaleY() * newPoint.getY());

					at.setTransform(at.getScaleX(), at.getShearY(), at.getShearX(), at.getScaleY(), newX, newY);

					// Now actually set the camera's transform
					camera.setViewTransform(at);
					scrollInProgress = false;
				}
			}
		}
	}
	
	public static void main(String[] args) {
		new ScrollingExample();
	}
}
