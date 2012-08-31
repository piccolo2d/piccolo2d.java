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
package org.piccolo2d.examples.issues;

import java.awt.Color;

import org.piccolo2d.PCanvas;
import org.piccolo2d.extras.PFrame;
import org.piccolo2d.nodes.PText;



/**
 * This example demonstrates a bug with setting the size
 * of a PFrame.  See http://code.google.com/p/piccolo2d/issues/detail?id=141.
 */
public class FrameCanvasSizeBugExample
    extends PFrame {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;


    /**
     * Create a new frame canvas size bug example.
     */
    public FrameCanvasSizeBugExample() {
        this(null);
    }

    /**
     * Create a new frame canvas size bug example for the specified canvas.
     * 
     * @param canvas canvas for this frame canvas size bug example
     */
    public FrameCanvasSizeBugExample(final PCanvas canvas) {
        super("FrameCanvasSizeBugExample", false, canvas);
        //setSize(410, 410);
        //getCanvas().setSize(410, 410);  does not help
    }


    /** {@inheritDoc} */
    public void initialize() {
        PText label = new PText("Note white at border S and E\nIt goes away when frame is resized");
        label.setOffset(200, 340);
        getCanvas().getLayer().addChild(label);
        getCanvas().setBackground(Color.PINK);
        getCanvas().setOpaque(true);
        setSize(410, 410);
        //getCanvas().setSize(410, 410);  does not help
    }


    /**
     * Main.
     * 
     * @param args command line arguments, ignored
     */
    public static void main(final String[] args) {
        new FrameCanvasSizeBugExample();
    }
}
