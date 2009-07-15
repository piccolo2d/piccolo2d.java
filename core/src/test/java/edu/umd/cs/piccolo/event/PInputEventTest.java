package edu.umd.cs.piccolo.event;

import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import junit.framework.TestCase;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPickPath;

public class PInputEventTest extends TestCase {
    private PCanvas canvas;

    public void setUp() {
        canvas = new PCanvas();
        canvas.setPreferredSize(new Dimension(100, 100));
        canvas.setBounds(0, 0, 100, 100);
    }

    public void testGetCameraUsesInputSourceIfPathIsNull() {
        InputEvent swingEvent = buildSwingClick(5, 5);

        PInputEvent event = new PInputEvent(canvas.getRoot().getDefaultInputManager(), swingEvent, canvas.getCamera());
        assertEquals(canvas.getCamera(), event.getCamera());
    }

    public void testInputManagerShouldBeSameAsGivenToConstructor() {
        InputEvent swingEvent = buildSwingClick(5, 5);

        PInputEvent event = new PInputEvent(canvas.getRoot().getDefaultInputManager(), swingEvent, canvas.getCamera());
        assertSame(canvas.getRoot().getDefaultInputManager(), event.getInputManager());
    }

    private MouseEvent buildSwingClick(int x, int y) {
        return new MouseEvent(canvas, 1, System.currentTimeMillis(), MouseEvent.MOUSE_CLICKED, x, y, 1, false);
    }
}
