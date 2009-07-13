package edu.umd.cs.piccolox.pswing;

import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

import junit.framework.TestCase;

public class PComboBoxTest extends TestCase {
    public void testPComboInstallsItsOwnUI() {
        PComboBox combo = new PComboBox();
        assertTrue(combo.getUI() instanceof PComboBox.PBasicComboBoxUI);
    }

    public void testConstructsWithVector() {
        Vector items = new Vector();
        items.add("A");
        items.add("B");
        PComboBox combo = new PComboBox(items);
        assertEquals(2, combo.getModel().getSize());
    }

    public void testConstructsWithArray() {
        String[] items = new String[] { "A", "B" };
        PComboBox combo = new PComboBox(items);
        assertEquals(2, combo.getModel().getSize());
    }
    
    public void testConstructsWithComboBoxModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement("A");
        model.addElement("B");        
        PComboBox combo = new PComboBox(model);
        assertEquals(2, combo.getModel().getSize());
    }
    
    public void testSetEnvironmentPersists() {
        PComboBox combo = new PComboBox();
        
        PSwingCanvas canvas = new PSwingCanvas();
        PSwing pCombo = new PSwing(combo);
        combo.setEnvironment(pCombo, canvas);
        
        assertEquals(pCombo, combo.getPSwing());
        assertEquals(canvas, combo.getCanvas());
    }
    
    public void testPopupIsRepositioned() {
       // Need a way of dispatching mock events to canvas before this can be tested
    }
}
