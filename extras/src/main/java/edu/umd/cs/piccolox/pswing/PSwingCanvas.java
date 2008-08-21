/*
 * Copyright (c) 2008, Piccolo2D project, http://piccolo2d.org
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

import edu.umd.cs.piccolo.PCanvas;

import javax.swing.*;
import java.awt.*;

/**
 * The <b>PSwingCanvas</b> is a PCanvas that can display Swing components with
 * the PSwing adapter.
 * 
 * @author Benjamin B. Bederson
 * @author Sam R. Reid
 * @author Lance E. Good
 */

public class PSwingCanvas extends PCanvas {
    public static final String SWING_WRAPPER_KEY = "Swing Wrapper";
    private static PSwingRepaintManager pSwingRepaintManager = new PSwingRepaintManager();

    private SwingWrapper swingWrapper;
    private PSwingEventHandler swingEventHandler;

    /**
     * Construct a new PSwingCanvas.
     */
    public PSwingCanvas() {
        swingWrapper = new SwingWrapper(this);
        add(swingWrapper);
        RepaintManager.setCurrentManager(pSwingRepaintManager);
        pSwingRepaintManager.addPSwingCanvas(this);

        // todo or maybe getCameraLayer() or getRoot()?
        swingEventHandler = new PSwingEventHandler(this, getCamera());
        swingEventHandler.setActive(true);
    }

    JComponent getSwingWrapper() {
        return swingWrapper;
    }

    public void addPSwing(PSwing pSwing) {
        swingWrapper.add(pSwing.getComponent());
    }

    public void removePSwing(PSwing pSwing) {
        swingWrapper.remove(pSwing.getComponent());
    }

    private static class SwingWrapper extends JComponent {
        private PSwingCanvas pSwingCanvas;

        public SwingWrapper(PSwingCanvas pSwingCanvas) {
            this.pSwingCanvas = pSwingCanvas;
            setSize(new Dimension(0, 0));
            setPreferredSize(new Dimension(0, 0));
            putClientProperty(SWING_WRAPPER_KEY, SWING_WRAPPER_KEY);
        }

        public PSwingCanvas getpSwingCanvas() {
            return pSwingCanvas;
        }
    }

}