package edu.umd.cs.piccolox.swt;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class PSWTImageTest extends TestCase {
    File imageFile;
    PSWTCanvas canvas;
    PSWTImage imageNode;
    Image image;

    public void setUp() throws Exception {
        final Display display = Display.getDefault();
        final Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        canvas = new PSWTCanvas(shell, 0);
        imageNode = new PSWTImage(canvas);
        image = new Image(display, new Rectangle(0, 0, 100, 100));
    }

    public void testImageShouldDefaultToNull() {
        assertNull(imageNode.getImage());
    }

    public void testPaintShouldDoNothingWhenImageIsNull() {
        // if it tries to use the graphics context, it would throw a NPE
        imageNode.paint(null);
    }

    public void testImageInConstructorPersists() {
        imageNode = new PSWTImage(canvas, image);
        assertSame(image, imageNode.getImage());
    }

    public void testDisposingCanvasDisposesImage() {
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
