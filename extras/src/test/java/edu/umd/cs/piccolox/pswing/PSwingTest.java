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

import junit.framework.TestCase;

import javax.swing.*;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.PFrame;

import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;

/**
 * JUnit test class to exercise PSwing bugfixes.
 *
 * @author Stephen Chin
 */
public class PSwingTest extends TestCase {
    public void testPSwing() {
        PSwing pSwing = new PSwing(new JButton("test"));
        PFrame frame = new PFrame();
        frame.getCanvas().getLayer().addChild(pSwing);
        frame.setVisible(true);
    }

    public void testReferences() {
        WeakReference pSwing = new WeakReference(new PSwing(new JButton("test")), new ReferenceQueue());
        PFrame frame = new PFrame();
        frame.getCanvas().getLayer().addChild((PNode) pSwing.get());
        frame.setVisible(true);
        frame.getCanvas().getLayer().removeAllChildren();
        for (int i=0; i<20; i++) { // make sure garbage collection has happened
            System.gc();
        }
        assertTrue("The PSwing node should be garbage collected after removal", pSwing.isEnqueued());
    }
}
