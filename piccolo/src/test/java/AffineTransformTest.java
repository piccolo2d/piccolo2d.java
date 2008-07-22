import junit.framework.TestCase;

import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PBounds;

public class AffineTransformTest extends TestCase {
	
	public AffineTransformTest(String aName) {
		super(aName);
	}
	
	public void testRotation() {
		PAffineTransform at = new PAffineTransform();
		at.rotate(Math.toRadians(45));
		assertEquals(at.getRotation(), Math.toRadians(45), 0.000000001);
		at.setRotation(Math.toRadians(90));
		assertEquals(at.getRotation(), Math.toRadians(90), 0.000000001);
	}

	public void testScale() {
		PAffineTransform at = new PAffineTransform();
		at.scaleAboutPoint(0.45, 0, 1);
		assertEquals(at.getScale(), 0.45, 0.000000001);
		at.setScale(0.11);
		assertEquals(at.getScale(), 0.11, 0.000000001);
	}
	
	public void testTransformRect() {
		PBounds b1 = new PBounds(0, 0, 100, 80);
		PBounds b2 = new PBounds(100, 100, 100, 80);
		
		PAffineTransform at = new PAffineTransform();
		at.scale(0.5, 0.5);
		at.translate(100, 50);
				
		at.transform(b1, b1);
		at.transform(b2, b2);

		PBounds b3 = new PBounds();
		PBounds b4 = new PBounds(0, 0, 100, 100);
		
		assertTrue(at.transform(b3, b4).isEmpty());
		
		assertEquals(b1.getX(), 50, 0.000000001);
		assertEquals(b1.getY(), 25, 0.000000001);
		assertEquals(b1.getWidth(), 50, 0.000000001);
		assertEquals(b1.getHeight(), 40, 0.000000001);

		assertEquals(b2.getX(), 100, 0.000000001);
		assertEquals(b2.getY(), 75, 0.000000001);
		assertEquals(b2.getWidth(), 50, 0.000000001);
		assertEquals(b2.getHeight(), 40, 0.000000001);

		at.inverseTransform(b1, b1);
		at.inverseTransform(b2, b2);

		assertEquals(b1.getX(), 0, 0.000000001);
		assertEquals(b1.getY(), 0, 0.000000001);
		assertEquals(b1.getWidth(), 100, 0.000000001);
		assertEquals(b1.getHeight(), 80, 0.000000001);

		assertEquals(b2.getX(), 100, 0.000000001);
		assertEquals(b2.getY(), 100, 0.000000001);
		assertEquals(b2.getWidth(), 100, 0.000000001);
		assertEquals(b2.getHeight(), 80, 0.000000001);				
	}	
}
