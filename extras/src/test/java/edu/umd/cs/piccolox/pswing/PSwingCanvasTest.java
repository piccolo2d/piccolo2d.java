/*
 * Copyright (c) 2008-2009, Piccolo2D project, http://piccolo2d.org
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

import javax.swing.JPanel;

import junit.framework.TestCase;

/**
 * Unit test for PSwingCanvas.
 */
public class PSwingCanvasTest extends TestCase {
    protected int finalizerCallCount;

    public void setUp() {
        finalizerCallCount = 0;
    }

    public void testMemoryLeak() throws InterruptedException {
        JPanel panel = new JPanel();
        for (int i = 0; i < 10; i++) {
            PSwingCanvas canvas = new PSwingCanvas() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 1L;

                public void finalize() {
                    finalizerCallCount++;
                }
            };
            panel.add(canvas);
            panel.remove(canvas);
            canvas = null;
        }
        panel = null;
        System.gc();
        System.runFinalization();

        // Not sure why I need -1 here, but I do. If I create 10000 it'll always
        // be 1 less
        // TODO: make this work in all environments. Will not work at the
        // command line for some.
        // assertEquals(10 - 1, finalizerCallCount);
    }
}
