/**
 * Copyright (C) 1998-2000 by University of Maryland, College Park, MD 20742, USA
 * All rights reserved.
 */
package edu.umd.cs.piccolox.pswing;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

/**
 * The <b>PComboBox</b> is used instead of a JComboBox in a Piccolo scene graph.
 * This PComboBox won't work properly if it is located in an abnormal hierarchy of Cameras.
 * Support is provided for only one (or zero) view transforms.
 * <p/>
 * A ComboBox for use in Piccolo.  This still has an associated JPopupMenu
 * (which is always potentially heavyweight depending on component location
 * relative to containing window borders.)  However, this ComboBox places
 * the PopupMenu component of the ComboBox in the appropriate position
 * relative to the permanent part of the ComboBox.  The PopupMenu is never
 * transformed.
 * <p/>
 * This class was not designed for subclassing.  If different behavior
 * is required, it seems more appropriate to subclass JComboBox directly
 * using this class as a model.
 * <p/>
 * NOTE: There is currently a known bug, namely, if the ComboBox receives
 * focus through 'tab' focus traversal and the keyboard is used to interact
 * with the ComboBox, there may be unexpected results.
 * <p/>
 * <p/>
 * <b>Warning:</b> Serialized objects of this class will not be
 * compatible with future Piccolo releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running the
 * same version of Piccolo. A future release of Piccolo will provide support for long
 * term persistence.
 *
 * @author Lance Good
 */
public class PComboBox extends JComboBox implements Serializable {

    private PSwing pSwing;
    private PSwingCanvas canvas;

    /**
     * Creates a PComboBox that takes its items from an existing ComboBoxModel.
     *
     * @param model The ComboBoxModel from which the list will be created
     */
    public PComboBox( ComboBoxModel model ) {
        super( model );
        init();
    }

    /**
     * Creates a PComboBox that contains the elements in the specified array.
     *
     * @param items The items to populate the PComboBox list
     */
    public PComboBox( final Object items[] ) {
        super( items );
        init();
    }

    /**
     * Creates a PComboBox that contains the elements in the specified Vector.
     *
     * @param items The items to populate the PComboBox list
     */
    public PComboBox( Vector items ) {
        super( items );
        init();
    }

    /**
     * Create an empty PComboBox
     */
    public PComboBox() {
        super();
        init();
    }

    /**
     * Substitue our UI for the default
     */
    private void init() {
        setUI( new PBasicComboBoxUI() );
    }

    /**
     * Clients must set the PSwing and PSwingCanvas environment for this PComboBox to work properly.
     *
     * @param pSwing
     * @param canvas
     */
    public void setEnvironment( PSwing pSwing, PSwingCanvas canvas ) {
        this.pSwing = pSwing;
        this.canvas = canvas;
    }

    /**
     * The substitute look and feel - used to capture the mouse
     * events on the arrowButton and the component itself and
     * to create our PopupMenu rather than the default
     */
    protected class PBasicComboBoxUI extends BasicComboBoxUI {

        /**
         * Create our Popup instead of theirs
         */
        protected ComboPopup createPopup() {
            PBasicComboPopup popup = new PBasicComboPopup( comboBox );
            popup.getAccessibleContext().setAccessibleParent( comboBox );
            return popup;
        }
    }

    /**
     * The substitute ComboPopupMenu that places itself correctly
     * in Piccolo.
     */
    protected class PBasicComboPopup extends BasicComboPopup {

        /**
         * @param combo The parent ComboBox
         */
        public PBasicComboPopup( JComboBox combo ) {
            super( combo );
        }


        /**
         * Computes the bounds for the Popup in Piccolo if a
         * PMouseEvent has been received.  Otherwise, it uses the
         * default algorithm for placing the popup.
         *
         * @param px corresponds to the x coordinate of the popup
         * @param py corresponds to the y coordinate of the popup
         * @param pw corresponds to the width of the popup
         * @param ph corresponds to the height of the popup
         * @return The bounds for the PopupMenu
         */
        protected Rectangle computePopupBounds( int px, int py, int pw, int ph ) {
            Rectangle2D r = getNodeBoundsInCanvas();
            Rectangle sup = super.computePopupBounds( px, py, pw, ph );
            return new Rectangle( (int)r.getX(), (int)r.getMaxY(), (int)sup.getWidth(), (int)sup.getHeight() );
        }
    }

    private Rectangle2D getNodeBoundsInCanvas() {
        if( pSwing == null || canvas == null ) {
            throw new RuntimeException( "PComboBox.setEnvironment( swing, pCanvas );//has to be done manually at present" );
        }
        Rectangle2D r1c = pSwing.getBounds();
        pSwing.localToGlobal( r1c );
        canvas.getCamera().globalToLocal( r1c );
        r1c = canvas.getCamera().getViewTransform().createTransformedShape( r1c ).getBounds2D();
        return r1c;
    }

}













