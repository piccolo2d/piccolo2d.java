package edu.umd.cs.piccolox.swt;

import java.io.File;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

public class PSWTImageTest extends SWTTest {
	File imageFile;
	PSWTCanvas canvas;
	PSWTImage imageNode;
	Image image;

	public void setUp() throws Exception {
		if (hasHead()) {
			final Display display = Display.getDefault();
			canvas = buildSimpleCanvas(display);
			imageNode = new PSWTImage(canvas);
			image = new Image(display, new Rectangle(0, 0, 100, 100));			
		}
	}

	public void testImageShouldDefaultToNull() {
		if (hasHead()) {
			assertNull(imageNode.getImage());
		}
	}

	public void testPaintShouldDoNothingWhenImageIsNull() {
		if (hasHead()) {
			// if it tries to use the graphics context, it would throw a NPE
			imageNode.paint(null);
		}
	}

	public void testImageInConstructorPersists() {
		if (hasHead()) {
			imageNode = new PSWTImage(canvas, image);
			assertSame(image, imageNode.getImage());
		}
	}

	public void testDisposingCanvasDisposesImage() {
		if (hasHead()) {
			final boolean[] called = new boolean[1];
			called[0] = false;
			imageNode = new PSWTImage(canvas, image) {
				protected void disposeImage() {
					called[0] = true;
					super.disposeImage();
				}
			};
			canvas.dispose();
			assertTrue(called[0]);
		}
	}
}
