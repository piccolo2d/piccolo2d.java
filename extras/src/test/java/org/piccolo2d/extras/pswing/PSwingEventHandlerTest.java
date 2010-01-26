package org.piccolo2d.extras.pswing;

import junit.framework.TestCase;

public class PSwingEventHandlerTest extends TestCase {

    public void testConstructorAcceptsNullTargetNode() {
        final PSwingCanvas canvas = new PSwingCanvas();
        new PSwingEventHandler(canvas, null);
    }
}
