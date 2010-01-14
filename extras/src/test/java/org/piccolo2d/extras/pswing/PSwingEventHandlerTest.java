package org.piccolo2d.extras.pswing;

import org.piccolo2d.extras.pswing.PSwingCanvas;
import org.piccolo2d.extras.pswing.PSwingEventHandler;

import junit.framework.TestCase;

public class PSwingEventHandlerTest extends TestCase {

    public void testConstructorAcceptsNullTargetNode() {
        PSwingCanvas canvas = new PSwingCanvas();
        PSwingEventHandler handler = new PSwingEventHandler(canvas, null);
    }
}
