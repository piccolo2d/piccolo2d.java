package edu.umd.cs.piccolox.pswing;

import junit.framework.TestCase;

public class PSwingEventHandlerTest extends TestCase {

    public void testConstructorAcceptsNullTargetNode() {
        PSwingCanvas canvas = new PSwingCanvas();
        PSwingEventHandler handler = new PSwingEventHandler(canvas, null);
    }
}
