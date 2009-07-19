package edu.umd.cs.piccolo.event;

import java.util.ArrayList;

public class MockPBasicInputEventHandler extends PBasicInputEventHandler {
    private ArrayList methodCalls = new ArrayList();
    
    public String[] getMethodCalls() {
        String[] result = new String[methodCalls.size()];
        for (int i=0; i<methodCalls.size(); i++) {
            result[i] = (String)methodCalls.get(i);
        }
        return result;
    }
    
    public void keyboardFocusGained(PInputEvent event) {
        methodCalls.add("keyboardFocusGained");
        super.keyboardFocusGained(event);
    }

    public void keyboardFocusLost(PInputEvent event) {
        methodCalls.add("keyboardFocusLost");
        super.keyboardFocusLost(event);
    }

    public void keyPressed(PInputEvent event) {
        methodCalls.add("keyPressed");
        super.keyPressed(event);
    }

    public void keyReleased(PInputEvent event) {
        methodCalls.add("keyReleased");
        super.keyReleased(event);
    }

    public void keyTyped(PInputEvent event) {
        methodCalls.add("keyTyped");
        super.keyTyped(event);
    }

    public void mouseClicked(PInputEvent event) {
        methodCalls.add("mouseClicked");
        super.mouseClicked(event);
    }

    public void mouseDragged(PInputEvent event) {
        methodCalls.add("mouseDragged");
        super.mouseDragged(event);
    }

    public void mouseEntered(PInputEvent event) {
        methodCalls.add("mouseEntered");
        super.mouseEntered(event);
    }

    public void mouseExited(PInputEvent event) {
        methodCalls.add("mouseExited");
        super.mouseExited(event);
    }

    public void mouseMoved(PInputEvent event) {
        methodCalls.add("mouseMoved");
        super.mouseMoved(event);
    }

    public void mousePressed(PInputEvent event) {
        methodCalls.add("mousePressed");
        super.mousePressed(event);
    }

    public void mouseReleased(PInputEvent event) {
        methodCalls.add("mouseReleased");
        super.mouseReleased(event);
    }

    public void mouseWheelRotated(PInputEvent event) {
        methodCalls.add("mouseReleased");
        super.mouseWheelRotated(event);
    }

    public void mouseWheelRotatedByBlock(PInputEvent event) {
        methodCalls.add("mouseWheelRotatedByBlock");
        super.mouseWheelRotatedByBlock(event);
    }
}
