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

import java.awt.Canvas;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.RepaintManager;

import org.piccolo2d.extras.pswing.PSwingCanvas;
import org.piccolo2d.extras.pswing.PSwingRepaintManager;

import junit.framework.TestCase;

/**
 * Unit test for PSwingRepaintManager.
 */
public class PSwingRepaintManagerTest extends TestCase {

    public void testConstructor() {
        final PSwingRepaintManager repaintManager = new PSwingRepaintManager();
        assertNotNull(repaintManager);
    }

    public void testCurrentManager() {
        RepaintManager currentManager = RepaintManager.currentManager(null);
        assertNotNull(currentManager);
        // TODO: this assertion is true when running this test case in isolation
        // but since PSwingCanvas may have been instantiated elsewhere in the
        // test suite
        // may not be true when running this test case as part of a test suite
        // assertFalse(currentManager instanceof PSwingRepaintManager);

        final Component awtComponent = new Canvas();
        currentManager = RepaintManager.currentManager(awtComponent);
        assertNotNull(currentManager);
        // assertFalse(currentManager instanceof PSwingRepaintManager);

        final JComponent swingComponent = new JPanel();
        currentManager = RepaintManager.currentManager(swingComponent);
        assertNotNull(currentManager);
        // assertFalse(currentManager instanceof PSwingRepaintManager);

        final PSwingCanvas pswingCanvas = new PSwingCanvas();
        currentManager = RepaintManager.currentManager(pswingCanvas);
        assertNotNull(currentManager);
        assertTrue(currentManager instanceof PSwingRepaintManager);

        // once a PSwingCanvas has been instantiated,
        // PSwingRepaintManager replaces RepaintManager everwhere
        currentManager = RepaintManager.currentManager(awtComponent);
        assertTrue(currentManager instanceof PSwingRepaintManager);

        currentManager = RepaintManager.currentManager(swingComponent);
        assertTrue(currentManager instanceof PSwingRepaintManager);

        currentManager = RepaintManager.currentManager(pswingCanvas);
        assertTrue(currentManager instanceof PSwingRepaintManager);
    }

    public void testLockRepaint() {
        final PSwingCanvas canvas = new PSwingCanvas();
        final RepaintManager currentManager = RepaintManager.currentManager(canvas);
        assertNotNull(currentManager);
        assertTrue(currentManager instanceof PSwingRepaintManager);

        final PSwingRepaintManager repaintManager = (PSwingRepaintManager) currentManager;
        // TODO: should lockRepaint allow null?
        repaintManager.lockRepaint(null);
        repaintManager.lockRepaint(canvas);
    }

    public void testUnlockRepaint() {
        final PSwingCanvas canvas = new PSwingCanvas();
        final RepaintManager currentManager = RepaintManager.currentManager(canvas);
        assertNotNull(currentManager);
        assertTrue(currentManager instanceof PSwingRepaintManager);

        final PSwingRepaintManager repaintManager = (PSwingRepaintManager) currentManager;
        repaintManager.lockRepaint(null);
        repaintManager.lockRepaint(canvas);

        repaintManager.unlockRepaint(null);
        repaintManager.unlockRepaint(canvas);

        // TODO: catch this array index out of bounds exception?
        final JComponent notLocked = new JPanel();
        try {
            repaintManager.unlockRepaint(notLocked);
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testIsPainting() {
        final PSwingCanvas canvas = new PSwingCanvas();
        final RepaintManager currentManager = RepaintManager.currentManager(canvas);
        assertNotNull(currentManager);
        assertTrue(currentManager instanceof PSwingRepaintManager);

        final PSwingRepaintManager repaintManager = (PSwingRepaintManager) currentManager;
        repaintManager.lockRepaint(null);
        repaintManager.lockRepaint(canvas);
        final JComponent notLocked = new JPanel();

        assertTrue(repaintManager.isPainting(null));
        assertTrue(repaintManager.isPainting(canvas));
        assertFalse(repaintManager.isPainting(notLocked));
    }

    public void testAddDirtyRegion() {
        final PSwingCanvas canvas = new PSwingCanvas();
        final RepaintManager currentManager = RepaintManager.currentManager(canvas);
        assertNotNull(currentManager);
        assertTrue(currentManager instanceof PSwingRepaintManager);

        final PSwingRepaintManager repaintManager = (PSwingRepaintManager) currentManager;
        repaintManager.addDirtyRegion(canvas, 0, 0, canvas.getWidth(), canvas.getHeight());

        final JComponent child = new JPanel();
        canvas.add(child);
        repaintManager.addDirtyRegion(child, 0, 0, child.getWidth(), child.getHeight());

        // TODO: will need some additional work here for full test coverage
    }

    public void testAddInvalidComponent() {
        final PSwingCanvas canvas = new PSwingCanvas();
        final RepaintManager currentManager = RepaintManager.currentManager(canvas);
        assertNotNull(currentManager);
        assertTrue(currentManager instanceof PSwingRepaintManager);

        final PSwingRepaintManager repaintManager = (PSwingRepaintManager) currentManager;
        // TODO: should check for null and throw IAE, or keep NPE?
        try {
            repaintManager.addInvalidComponent(null);
        }
        catch (final NullPointerException e) {
            // expected
        }

        final JComponent component = new JPanel();
        final JComponent child = new JPanel();
        canvas.add(child);

        repaintManager.addInvalidComponent(canvas);
        repaintManager.addInvalidComponent(component);
        repaintManager.addInvalidComponent(child);

        // TODO: will need some additional work here for full test coverage
    }
}
