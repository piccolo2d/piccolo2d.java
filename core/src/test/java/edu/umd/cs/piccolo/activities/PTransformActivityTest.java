package edu.umd.cs.piccolo.activities;
import edu.umd.cs.piccolo.activities.PTransformActivity;

import junit.framework.TestCase;

public class PTransformActivityTest extends TestCase {

	public PTransformActivityTest(String name) {
		super(name);
	}
	
	public void testToString() {
		PTransformActivity transformActivity = new PTransformActivity(1000, 0, null);
		assertNotNull(transformActivity.toString());
	}
}
