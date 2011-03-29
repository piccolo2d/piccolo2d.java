package edu.umd.cs.piccolo.examples.pswing;

import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.JComponent;

/**
 * Extends {@link PSwingExample1} but uses {@link edu.umd.cs.piccolox.pswing.PSwing#setUseBufferedPainting(boolean)}
 * for {@link edu.umd.cs.piccolox.pswing.PSwing}s.
 */
public class PSwingExample4 extends PSwingExample1 {

    public static void main(final String[] args) {
        new PSwingExample4().run();
    }

    protected PSwing createPSwing(JComponent component) {
        PSwing pSwing = new PSwing(component);
        pSwing.setUseBufferedPainting(true);
        return pSwing;
    }

}
