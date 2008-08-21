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
package edu.umd.cs.piccolo;

import junit.framework.TestCase;

public class PCameraTest extends TestCase {

    public PCameraTest(String name) {
        super(name);
    }

    public void testCopy() {
        PNode n = new PNode();

        PLayer layer1 = new PLayer();
        PLayer layer2 = new PLayer();

        PCamera camera1 = new PCamera();
        PCamera camera2 = new PCamera();

        n.addChild(layer1);
        n.addChild(layer2);
        n.addChild(camera1);
        n.addChild(camera2);

        camera1.addLayer(layer1);
        camera1.addLayer(layer2);
        camera2.addLayer(layer1);
        camera2.addLayer(layer2);

        // no layers should be written out since they are written conditionally.
        PCamera cameraCopy = (PCamera) camera1.clone();
        assertEquals(cameraCopy.getLayerCount(), 0);

        n.clone();
        assertEquals(((PCamera) n.getChildrenReference().get(2)).getLayerCount(), 2);
        assertEquals(((PLayer) n.getChildrenReference().get(1)).getCameraCount(), 2);
    }
}
