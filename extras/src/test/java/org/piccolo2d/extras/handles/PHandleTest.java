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
package org.piccolo2d.extras.handles;

import org.piccolo2d.PNode;
import org.piccolo2d.extras.handles.PHandle;
import org.piccolo2d.extras.util.PLocator;

import junit.framework.TestCase;



/**
 * Unit test for PHandle.
 */
public class PHandleTest extends TestCase {
    public void testCloneWorksAsExpected() {
        PHandle handle = new PHandle(new OriginLocator());

        PHandle cloned = (PHandle) handle.clone();
        assertNull(cloned);
    }

    public void testDragHandlerIsNotNull() {
        PHandle handle = new PHandle(new OriginLocator());
        assertNotNull(handle.getHandleDraggerHandler());
    }

    public void testLocatorIsSameAsPassedToConstructor() {
        PLocator locator = new OriginLocator();
        PHandle handle = new PHandle(locator);
        assertSame(locator, handle.getLocator());
    }

    public void testChangingLocatorWorks() {
        PLocator locator = new OriginLocator();
        PLocator locator2 = new OriginLocator();
        PHandle handle = new PHandle(locator);
        handle.setLocator(locator2);
        assertSame(locator2, handle.getLocator());
    }

    public void testChangingParentCausesRelocateHandle() {
        final int[] relocateCounts = new int[1];
        PHandle handle = new PHandle(new OriginLocator()) {
            public void relocateHandle() {
                super.relocateHandle();
                relocateCounts[0]++;
            }
        };
        relocateCounts[0] = 0;
        PNode parent = new PNode();
        handle.setParent(parent);
        assertEquals(1, relocateCounts[0]);
    }
    
    public void testResizingParentCausesRelocateHandle() {
        final int[] relocateCounts = new int[1];
        PHandle handle = new PHandle(new OriginLocator()) {
            public void relocateHandle() {
                super.relocateHandle();
                relocateCounts[0]++;
            }
        };        
        PNode parent = new PNode();
        parent.addChild(handle);
        relocateCounts[0] = 0;
        parent.setBounds(0, 0, 100, 100);
        assertEquals(1, relocateCounts[0]);
    }
    
    public void testLocatorCanBeNullWithoutAProblem() {
        PHandle handle = new PHandle(null);
        handle.relocateHandle();        
    }

    private final class OriginLocator extends PLocator {
        public double locateX() {
            return 0;
        }

        public double locateY() {
            return 0;
        }
    }
}
