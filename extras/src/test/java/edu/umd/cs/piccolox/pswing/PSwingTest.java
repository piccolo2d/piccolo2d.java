package edu.umd.cs.piccolox.pswing;

import junit.framework.TestCase;

import javax.swing.*;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.PFrame;

import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;

/**
 * JUnit test class to exercise PSwing bugfixes.
 *
 * @author Stephen Chin
 */
public class PSwingTest extends TestCase {
    public void testPSwing() {
        PSwing pSwing = new PSwing(new JButton("test"));
        PFrame frame = new PFrame();
        frame.getCanvas().getLayer().addChild(pSwing);
        frame.setVisible(true);
    }

    public void testReferences() {
        WeakReference pSwing = new WeakReference(new PSwing(new JButton("test")), new ReferenceQueue());
        PFrame frame = new PFrame();
        frame.getCanvas().getLayer().addChild((PNode) pSwing.get());
        frame.setVisible(true);
        frame.getCanvas().getLayer().removeAllChildren();
        for (int i=0; i<20; i++) { // make sure garbage collection has happened
            System.gc();
        }
        assertTrue("The PSwing node should be garbage collected after removal", pSwing.isEnqueued());
    }
}
