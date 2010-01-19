/*
 * Copyright (c) 2008-2010, Piccolo2D project, http://piccolo2d.org
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
package edu.umd.cs.piccolox.pswing;

import java.awt.Component;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import edu.umd.cs.piccolo.util.PBounds;

/**
 * This RepaintManager replaces the default Swing implementation, and is used to
 * intercept and repaint dirty regions of PSwing components.
 * <p>
 * This is an internal class used by Piccolo to support Swing components in
 * Piccolo. This should not be instantiated, though all the public methods of
 * javax.swing.RepaintManager may still be called and perform in the expected
 * manner.
 * </p>
 * <p>
 * PBasicRepaint Manager is an extension of RepaintManager that traps those
 * repaints called by the Swing components that have been added to the PCanvas
 * and passes these repaints to the SwingVisualComponent rather than up the
 * component hierarchy as usually happens.
 * </p>
 * <p>
 * Also traps revalidate calls made by the Swing components added to the PCanvas
 * to reshape the applicable Visual Component.
 * </p>
 * <p>
 * Also keeps a list of PSwings that are painting. This disables repaint until
 * the component has finished painting. This is to address a problem introduced
 * by Swing's CellRendererPane which is itself a work-around. The problem is
 * that JTable's, JTree's, and JList's cell renderers need to be validated
 * before repaint. Since we have to repaint the entire Swing component hierarchy
 * (in the case of a Swing component group used as a Piccolo visual component).
 * This causes an infinite loop. So we introduce the restriction that no
 * repaints can be triggered by a call to paint.
 * </p>
 * 
 * @author Benjamin B. Bederson
 * @author Lance E. Good
 * @author Sam R. Reid
 */
public class PSwingRepaintManager extends RepaintManager {

    // The components that are currently painting
    // This needs to be a vector for thread safety
    private final Vector paintingComponents = new Vector();

    /**
     * Locks repaint for a particular (Swing) component displayed by PCanvas.
     * 
     * @param c The component for which the repaint is to be locked
     */
    public void lockRepaint(final JComponent c) {
        paintingComponents.addElement(c);
    }

    /**
     * Unlocks repaint for a particular (Swing) component displayed by PCanvas.
     * 
     * @param c The component for which the repaint is to be unlocked
     */
    public void unlockRepaint(final JComponent c) {
        paintingComponents.remove(c);
    }

    /**
     * Returns true if repaint is currently locked for a component and false
     * otherwise.
     * 
     * @param c The component for which the repaint status is desired
     * @return Whether the component is currently painting
     */
    public boolean isPainting(final JComponent c) {
        return paintingComponents.contains(c);
    }

    /**
     * This is the method "repaint" now calls in the Swing components.
     * Overridden to capture repaint calls from those Swing components which are
     * being used as Piccolo visual components and to call the Piccolo repaint
     * mechanism rather than the traditional Component hierarchy repaint
     * mechanism. Otherwise, behaves like the superclass.
     * 
     * @param component Component to be repainted
     * @param x X coordinate of the dirty region in the component
     * @param y Y coordinate of the dirty region in the component
     * @param width Width of the dirty region in the component
     * @param height Height of the dirty region in the component
     */
    public synchronized void addDirtyRegion(final JComponent component, final int x, final int y, final int width,
            final int height) {
        boolean captureRepaint = false;
        JComponent childComponent = null;

        int captureX = x;
        int captureY = y;

        // We have to check to see if the PCanvas
        // (ie. the SwingWrapper) is in the components ancestry. If so,
        // we will want to capture that repaint. However, we also will
        // need to translate the repaint request since the component may
        // be offset inside another component.
        for (Component comp = component; comp != null && comp.isLightweight(); comp = comp.getParent()) {
            if (comp.getParent() instanceof PSwingCanvas.ChildWrapper) {
                captureRepaint = true;
                childComponent = (JComponent) comp;
                break;
            }
            else {
                // Adds to the offset since the component is nested
                captureX += comp.getLocation().getX();
                captureY += comp.getLocation().getY();
            }
        }

        // Now we check to see if we should capture the repaint and act
        // accordingly
        if (captureRepaint) {
            if (!isPainting(childComponent)) {
                final double repaintW = Math.min(childComponent.getWidth() - captureX, width);
                final double repaintH = Math.min(childComponent.getHeight() - captureY, height);

                dispatchRepaint(childComponent, new PBounds(captureX, captureY, repaintW, repaintH));
            }
        }
        else {
            super.addDirtyRegion(component, x, y, width, height);
        }
    }

    private void dispatchRepaint(final JComponent childComponent, final PBounds repaintBounds) {
        final PSwing pSwing = (PSwing) childComponent.getClientProperty(PSwing.PSWING_PROPERTY);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                pSwing.repaint(repaintBounds);
            }
        });
    }

    /**
     * This is the method "revalidate" calls in the Swing components. Overridden
     * to capture revalidate calls from those Swing components being used as
     * Piccolo visual components and to update Piccolo's visual component
     * wrapper bounds (these are stored separately from the Swing component).
     * Otherwise, behaves like the superclass.
     * 
     * @param invalidComponent The Swing component that needs validation
     */
    public synchronized void addInvalidComponent(final JComponent invalidComponent) {
        final JComponent capturedComponent = invalidComponent;

        if (capturedComponent.getParent() == null
                || !(capturedComponent.getParent() instanceof PSwingCanvas.ChildWrapper)) {
            super.addInvalidComponent(invalidComponent);
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    capturedComponent.validate();
                    final PSwing pSwing = (PSwing) capturedComponent.getClientProperty(PSwing.PSWING_PROPERTY);
                    pSwing.updateBounds();
                }
            });
        }
    }
}