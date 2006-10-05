import edu.umd.cs.piccolo.activities.PTransformActivity;

import junit.framework.TestCase;

public class TransformActivityTest extends TestCase {

	public TransformActivityTest(String name) {
		super(name);
	}
	
	public void testToString() {
		System.out.println(new PTransformActivity(1000, 0, null).toString());
	}
}
