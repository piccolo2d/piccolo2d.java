/*
 * Copyright (c) 2008-2011, Piccolo2D project, http://piccolo2d.org
 * Copyright (c) 1998-2008, University of Maryland
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * None of the name of the University of Maryland, the name of the Piccolo2D project, or the names of its
 * contributors may be used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.piccolo2d.extras.pswing;

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
 * This PComboBox won't work properly if it is located in an abnormal hierarchy
 * of Cameras. Support is provided for only one (or zero) view transforms.
 * <p>
 * A ComboBox for use in Piccolo. This still has an associated JPopupMenu (which
 * is always potentially heavyweight depending on component location relative to
 * containing window borders.) However, this ComboBox places the PopupMenu
 * component of the ComboBox in the appropriate position relative to the
 * permanent part of the ComboBox. The PopupMenu is never transformed.
 * </p>
 * <p>
 * This class was not designed for subclassing. If different behavior is
 * required, it seems more appropriate to subclass JComboBox directly using this
 * class as a model.
 * </p>
 * <p>
 * NOTE: There is currently a known bug, namely, if the ComboBox receives focus
 * through 'tab' focus traversal and the keyboard is used to interact with the
 * ComboBox, there may be unexpected results.
 * </p>
 * <p>
 * <b>Warning:</b> Serialized objects of this class will not be compatible with
 * future Piccolo releases. The current serialization support is appropriate for
 * short term storage or RMI between applications running the same version of
 * Piccolo. A future release of Piccolo will provide support for long term
 * persistence.
 * </p>
 * 
 * @author Lance Good
 */
public class PComboBox extends JComboBox implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private PSwing pSwing;
    private PSwingCanvas canvas;

    /**
     * Creates a PComboBox that takes its items from an existing ComboBoxModel.
     * 
     * @param model The ComboBoxModel from which the list will be created
     */
    public PComboBox(final ComboBoxModel model) {
        super(model);
        init();
    }

    /**
     * Creates a PComboBox that contains the elements in the specified array.
     * 
     * @param items The items to populate the PComboBox list
     */
    public PComboBox(final Object[] items) {
        super(items);
        init();
    }

    /**
     * Creates a PComboBox that contains the elements in the specified Vector.
     * 
     * @param items The items to populate the PComboBox list
     */
    public PComboBox(final Vector items) {
        super(items);
        init();
    }

    /**
     * Create an empty PComboBox.
     */
    public PComboBox() {
        super();
        init();
    }

    /**
     * Substitute our UI for the default.
     */
    private void init() {
        setUI(new PBasicComboBoxUI());
    }

    /**
     * Clients must set the PSwing and PSwingCanvas environment for this
     * PComboBox to work properly.
     * 
     * @param pSwingNode node that this PComboBox is attached to
     * @param canvasEnvirnoment canvas on which the pSwing node is embedded
     */
    public void setEnvironment(final PSwing pSwingNode, final PSwingCanvas canvasEnvirnoment) {
        this.pSwing = pSwingNode;
        this.canvas = canvasEnvirnoment;
    }

    /**
     * The substitute look and feel - used to capture the mouse events on the
     * arrowButton and the component itself and to create our PopupMenu rather
     * than the default.
     */
    protected class PBasicComboBoxUI extends BasicComboBoxUI {

        /**
         * Create our Popup instead of swing's default.
         * 
         * @return a new ComboPopup
         */
        protected ComboPopup createPopup() {
            final PBasicComboPopup popup = new PBasicComboPopup(comboBox);
            popup.getAccessibleContext().setAccessibleParent(comboBox);
            return popup;
        }
    }

    /**
     * The substitute ComboPopupMenu that places itself correctly in Piccolo2d.
     */
    protected class PBasicComboPopup extends BasicComboPopup {
        private static final long serialVersionUID = 1L;

        /**
         * Creates a PBasicComboPopup that will position itself correctly in
         * relation to the provided JComboBox.
         * 
         * @param combo The associated ComboBox
         */
        public PBasicComboPopup(final JComboBox combo) {
            super(combo);
        }

        /**
         * Computes the bounds for the Popup in Piccolo2D if a PMouseEvent has
         * been received. Otherwise, it uses the default algorithm for placing
         * the popup.
         * 
         * @param px corresponds to the x coordinate of the popup
         * @param py corresponds to the y coordinate of the popup
         * @param pw corresponds to the width of the popup
         * @param ph corresponds to the height of the popup
         * @return The bounds for the PopupMenu
         */
        protected Rectangle computePopupBounds(final int px, final int py, final int pw, final int ph) {
            final Rectangle2D r = getNodeBoundsInCanvas();
            final Rectangle sup = super.computePopupBounds(px, py, pw, ph);
            return new Rectangle((int) r.getX(), (int) r.getMaxY(), (int) sup.getWidth(), (int) sup.getHeight());
        }
    }

    private Rectangle2D getNodeBoundsInCanvas() {
        if (pSwing == null || canvas == null) {
            throw new RuntimeException(
                    "PComboBox.setEnvironment( swing, pCanvas );//has to be done manually at present");
        }
        Rectangle2D r1c = new Rectangle2D.Double(pSwing.getX(), pSwing.getY(), getWidth(), getHeight());
        pSwing.localToGlobal(r1c);
        canvas.getCamera().globalToLocal(r1c);
        r1c = canvas.getCamera().getViewTransform().createTransformedShape(r1c).getBounds2D();
        return r1c;
    }

    /**
     * Returns the associated PSwing node.
     * 
     * @return associated PSwing node
     */
    public PSwing getPSwing() {
        return pSwing;
    }

    /**
     * Returns the canvas on which the PSwing node is embedded.
     * 
     * @return canvas on which the PSwing node is embedded
     */
    public PSwingCanvas getCanvas() {
        return canvas;
    }

}
