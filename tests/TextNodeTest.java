import junit.framework.TestCase;

import edu.umd.cs.piccolo.nodes.PText;

public class TextNodeTest extends TestCase {

	public TextNodeTest(String name) {
		super(name);
	}
	
	public void testCopy() {
		PText aNode = new PText("Boo");
		aNode = (PText) aNode.clone();
		assertNotNull(aNode.getText());
		assertNotNull(aNode.getFont());
	}	
	
	public void testEmptyString() {
		PText t = new PText();
		t.setText("hello world");
		t.setText("");
		t.setText(null);
	}

	public void testBoundsOfEmptyString() {
		PText t = new PText();
		t.setText("hello world");
		assertTrue(t.getBoundsReference().getWidth() > 0);
		t.setText("");
		assertTrue(t.getBoundsReference().getWidth() == 0);
		t.setText(null);
		assertTrue(t.getBoundsReference().getWidth() == 0);
	}
	
	public void testToString() {
		PText t = new PText();
		t.setText("hello world");
		System.out.println(t.toString());
	}
}
