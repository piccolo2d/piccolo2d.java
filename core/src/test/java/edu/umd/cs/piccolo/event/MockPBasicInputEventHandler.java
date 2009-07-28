package edu.umd.cs.piccolo.event;

import java.util.ArrayList;

public class MockPBasicInputEventHandler extends PBasicInputEventHandler {
    private final ArrayList methodCalls = new ArrayList();

    public String[] getMethodCalls() {
        final String[] result = new String[methodCalls.size()];
        for (int i = 0; i < methodCalls.size(); i++) {
            result[i] = (String) methodCalls.get(i);
        }
        return result;
    }

    public void keyboardFocusGained(final PInputEvent event) {
        methodCalls.add("keyboardFocusGained");
        super.keyboardFocusGained(event);
    }

    public void keyboardFocusLost(final PInputEvent event) {
        methodCalls.add("keyboardFocusLost");
        super.keyboardFocusLost(event);
    }

    public void keyPressed(final PInputEvent event) {
        methodCalls.add("keyPressed");
        super.keyPressed(event);
    }

    public void keyReleased(final PInputEvent event) {
        methodCalls.add("keyReleased");
        super.keyReleased(event);
    }

    public void keyTyped(final PInputEvent event) {
        methodCalls.add("keyTyped");
        super.keyTyped(event);
    }

    public void mouseClicked(final PInputEvent event) {
        methodCalls.add("mouseClicked");
        super.mouseClicked(event);
    }

    public void mouseDragged(final PInputEvent event) {
        methodCalls.add("mouseDragged");
        super.mouseDragged(event);
    }

    public void mouseEntered(final PInputEvent event) {
        methodCalls.add("mouseEntered");
        super.mouseEntered(event);
    }

    public void mouseExited(final PInputEvent event) {
        methodCalls.add("mouseExited");
        super.mouseExited(event);
    }

    public void mouseMoved(final PInputEvent event) {
        methodCalls.add("mouseMoved");
        super.mouseMoved(event);
    }

    public void mousePressed(final PInputEvent event) {
        methodCalls.add("mousePressed");
        super.mousePressed(event);
    }

    public void mouseReleased(final PInputEvent event) {
        methodCalls.add("mouseReleased");
        super.mouseReleased(event);
    }

    public void mouseWheelRotated(final PInputEvent event) {
        methodCalls.add("mouseReleased");
        super.mouseWheelRotated(event);
    }

    public void mouseWheelRotatedByBlock(final PInputEvent event) {
        methodCalls.add("mouseWheelRotatedByBlock");
        super.mouseWheelRotatedByBlock(event);
    }
}
