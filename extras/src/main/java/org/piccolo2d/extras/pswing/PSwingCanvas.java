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

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.RepaintManager;

import org.piccolo2d.PCanvas;


/**
 * The <b>PSwingCanvas</b> is a PCanvas that can display Swing components with
 * the PSwing adapter.
 * 
 * @author Benjamin B. Bederson
 * @author Sam R. Reid
 * @author Lance E. Good
 */
public class PSwingCanvas extends PCanvas {
    private static final long serialVersionUID = 1L;
    /** Key used to store the "Swing Wrapper" as an attribute of the PSwing node. */
    public static final String SWING_WRAPPER_KEY = "Swing Wrapper";
    private final ChildWrapper swingWrapper;

    /**
     * Construct a new PSwingCanvas.
     */
    public PSwingCanvas() {
        swingWrapper = new ChildWrapper();
        add(swingWrapper);
        initRepaintManager();
        new PSwingEventHandler(this, getCamera()).setActive(true);
    }

    private void initRepaintManager() {
        final RepaintManager repaintManager = RepaintManager.currentManager(this);
        if (!(repaintManager instanceof PSwingRepaintManager)) {
            RepaintManager.setCurrentManager(new PSwingRepaintManager());
        }
    }

    JComponent getSwingWrapper() {
        return swingWrapper;
    }

    void addPSwing(final PSwing pSwing) {
        swingWrapper.add(pSwing.getComponent());
    }

    void removePSwing(final PSwing pSwing) {
        swingWrapper.remove(pSwing.getComponent());
    }

    /**
     * JComponent wrapper for a PSwingCanvas. Used by PSwingRepaintManager.
     */
    static class ChildWrapper extends JComponent {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Create a new JComponent wrapper for the specified PSwingCanvas.
         */
        public ChildWrapper() {
            setSize(new Dimension(0, 0));
            setPreferredSize(new Dimension(0, 0));
            putClientProperty(SWING_WRAPPER_KEY, SWING_WRAPPER_KEY);
        }
    }
}