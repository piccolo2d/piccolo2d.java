import junit.framework.Test;
import junit.framework.TestSuite;

public class RunAllUnitTests {
	
	public static Test suite() {
		TestSuite suite= new TestSuite();
		suite.addTest(new TestSuite(NodeTest.class));
		suite.addTest(new TestSuite(ImageNodeTest.class));
		suite.addTest(new TestSuite(PathNodeTest.class));
		suite.addTest(new TestSuite(TextNodeTest.class));
		suite.addTest(new TestSuite(SerializationTest.class));
		suite.addTest(new TestSuite(CameraNodeTest.class));
		suite.addTest(new TestSuite(AffineTransformTest.class));
		suite.addTest(new TestSuite(ZoomEventHandlerTest.class));
		suite.addTest(new TestSuite(TransformActivityTest.class));
		suite.addTest(new TestSuite(NotificationCenterTest.class));
		suite.addTest(new TestSuite(PickTest.class));
		suite.addTest(new TestSuite(PFrameTest.class));
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
		System.exit(0);
	}
}
