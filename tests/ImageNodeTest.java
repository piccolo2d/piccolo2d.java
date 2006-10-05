import java.awt.image.BufferedImage;

import junit.framework.TestCase;

import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;

public class ImageNodeTest extends TestCase {

	public ImageNodeTest(String name) {
		super(name);
	}
	
	public void testCopy() {
		PImage aNode = new PImage(new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB));
		aNode = (PImage) aNode.clone();
		assertNotNull(aNode.getImage());
		assertEquals(aNode.getBounds(), new PBounds(0, 0, 100, 100));
	}	
	
	public void testToString() {
		PImage aNode = new PImage(new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB));
		aNode.getFullBoundsReference();
		System.out.println(aNode.toString());
	}
}
