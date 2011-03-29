package org.piccolo2d.examples.pswing;

import org.piccolo2d.extras.pswing.PSwing;

import javax.swing.JComponent;

/**
 * Extends {@link PSwingExample1} but uses {@link org.piccolo2d.extras.pswing.PSwing#setUseBufferedPainting(boolean)}
 * for {@link org.piccolo2d.extras.pswing.PSwing}s.
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
