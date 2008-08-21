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
package edu.umd.cs.piccolox;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

public class PFrameTest extends TestCase {
    private static final int TEST_WIDTH = 500;
    private static final int TEST_HEIGHT = 300;

    public PFrameTest(String name) {
        super(name);
    }

    public void testComponentResized() throws InvocationTargetException, InterruptedException {
        final PFrame frame = new PFrame();
        frame.setBounds(0, 0, TEST_WIDTH, TEST_HEIGHT);
        EventQueue.invokeAndWait(new Runnable() {
            public void run() {
                // clear the event queue
            }
        });
        Rectangle bounds = frame.getCanvas().getBounds();
        assertEquals("Canvas width should match width of content pane", frame.getContentPane().getWidth(), bounds.width);
        assertEquals("Canvas height should match height of content pane", frame.getContentPane().getHeight(),
                bounds.height);
    }
}
