package edu.umd.cs.piccolox.swt;

import junit.framework.TestCase;

public class PSWTTextTest extends TestCase {
    private PSWTText testNode;

    public void setUp() {
        testNode = new PSWTText();
    }

    
    public void testTextPersistsTrainingAndInternalNewlines() {
        testNode.setText("Hello\nWorld\n\n");
        assertEquals("Hello\nWorld\n\n", testNode.getText());
    }


}
