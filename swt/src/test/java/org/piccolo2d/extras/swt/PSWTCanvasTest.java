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
package org.piccolo2d.extras.swt;

import junit.framework.TestCase;

import org.eclipse.swt.layout.FillLayout;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.piccolo2d.event.PInputEventListener;
import org.piccolo2d.event.PPanEventHandler;
import org.piccolo2d.event.PZoomEventHandler;
import org.piccolo2d.extras.swt.PSWTCanvas;


/**
 * Unit test for PSWTCanvas.
 */
public class PSWTCanvasTest extends TestCase {
    private PSWTCanvas canvas;

    public void setUp() {        
        final Shell shell = new Shell(Display.getDefault());
        shell.setLayout(new FillLayout());      
        canvas = new PSWTCanvas(shell, 0);
    }

    public void testPanEventListenerIsInstalledByDefault() {
        PPanEventHandler handler = canvas.getPanEventHandler();
        assertNotNull(handler);

        int handlerIndex = getHandlerIndex(handler);
        assertFalse("Pan Event Handler not installed", handlerIndex == -1);
    }

    public void testZoomEventListenerIsInstalledByDefault() {
        PZoomEventHandler handler = canvas.getZoomEventHandler();
        assertNotNull(handler);

        int handlerIndex = getHandlerIndex(handler);
        assertFalse("Zoom Event Handler not installed", handlerIndex == -1);
    }

    private int getHandlerIndex(PInputEventListener handler) {
        PInputEventListener[] listeners = canvas.getCamera().getInputEventListeners();
        int handlerIndex = -1;
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] == handler) {
                handlerIndex = i;
            }
        }
        return handlerIndex;
    }

    public void testAnimatingDefaultsToFalse() {
        assertFalse(canvas.getAnimating());
    }

    public void testInteractingDefaultsToFalse() {
        assertFalse(canvas.getInteracting());
    }

    public void testInteractingWorksByCountingCallsToSetInteracting() {
        canvas.setInteracting(true);
        assertTrue(canvas.getInteracting());

        canvas.setInteracting(true);
        assertTrue(canvas.getInteracting());

        canvas.setInteracting(false);
        // This is terrible
        assertTrue(canvas.getInteracting());

        canvas.setInteracting(false);
        assertFalse(canvas.getInteracting());
    }

    public void testCanvasIsDoubleBufferedByDefault() {
        assertTrue(canvas.getDoubleBuffered());
    }

    public void testDoubleBufferingPersists() {
        canvas.setDoubleBuffered(false);
        assertFalse(canvas.getDoubleBuffered());
        canvas.setDoubleBuffered(true);
        assertTrue(canvas.getDoubleBuffered());
    }
}
