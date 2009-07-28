package edu.umd.cs.piccolox.pswing;

import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

import junit.framework.TestCase;

public class PComboBoxTest extends TestCase {
    public void testPComboInstallsItsOwnUI() {
        final PComboBox combo = new PComboBox();
        assertTrue(combo.getUI() instanceof PComboBox.PBasicComboBoxUI);
    }

    public void testConstructsWithVector() {
        final Vector items = new Vector();
        items.add("A");
        items.add("B");
        final PComboBox combo = new PComboBox(items);
        assertEquals(2, combo.getModel().getSize());
    }

    public void testConstructsWithArray() {
        final String[] items = new String[] { "A", "B" };
        final PComboBox combo = new PComboBox(items);
        assertEquals(2, combo.getModel().getSize());
    }

    public void testConstructsWithComboBoxModel() {
        final DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement("A");
        model.addElement("B");
        final PComboBox combo = new PComboBox(model);
        assertEquals(2, combo.getModel().getSize());
    }

    public void testSetEnvironmentPersists() {
        final PComboBox combo = new PComboBox();

        final PSwingCanvas canvas = new PSwingCanvas();
        final PSwing pCombo = new PSwing(combo);
        combo.setEnvironment(pCombo, canvas);

        assertEquals(pCombo, combo.getPSwing());
        assertEquals(canvas, combo.getCanvas());
    }

    public void testPopupIsRepositioned() {
        // Need a way of dispatching mock events to canvas before this can be
        // tested
    }
}
