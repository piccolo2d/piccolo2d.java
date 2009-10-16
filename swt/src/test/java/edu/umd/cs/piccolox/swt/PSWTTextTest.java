package edu.umd.cs.piccolox.swt;

import junit.framework.TestCase;

public class PSWTTextTest extends SWTTest {	
    private PSWTText testNode;

    public void setUp() {    	
    	if (isHeadless()) 
    		return;
    	
        testNode = new PSWTText();
    }

    public void testTextPersistsTrainingAndInternalNewlines() {
    	if (isHeadless())
    		return;

        testNode.setText("Hello\nWorld\n\n");
        assertEquals("Hello\nWorld\n\n", testNode.getText());
    }

}
