package edu.umd.cs.piccolo.examples;

import java.awt.Color;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.nodes.P3DRect;

public class P3DRectExample extends PFrame {

    public P3DRectExample() {
        this(null);
    }

    public P3DRectExample(final PCanvas aCanvas) {
        super("P3DRect Example", false, aCanvas);
    }

    public void initialize() {
        final P3DRect rect1 = new P3DRect(50, 50, 100, 100);
        rect1.setPaint(new Color(239, 235, 222));
        getCanvas().getLayer().addChild(rect1);

        final P3DRect rect2 = new P3DRect(50, 50, 100, 100);
        rect2.setPaint(new Color(239, 235, 222));
        rect2.translate(110, 0);
        rect2.setRaised(false);
        getCanvas().getLayer().addChild(rect2);               
    }

    public static void main(String[] args) {
        new P3DRectExample();
    }

}
