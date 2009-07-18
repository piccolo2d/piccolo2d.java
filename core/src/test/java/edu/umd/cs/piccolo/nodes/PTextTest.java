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
package edu.umd.cs.piccolo.nodes;

import junit.framework.TestCase;

import edu.umd.cs.piccolo.nodes.PText;

public class PTextTest extends TestCase {

    public PTextTest(String name) {
        super(name);
    }

    public void testClone() {
        PText textNode = new PText("Boo");
        PText clonedNode = (PText) textNode.clone();        
        assertEquals("Boo", clonedNode.getText());
        assertEquals(textNode.getFont(), clonedNode.getFont());
    }

    public void testTextIsEmptyByDefault() {
    	PText textNode = new PText();
    	assertEquals("", textNode.getText());
    }

    public void testTextMayBeAssignedEmptyString() {
    	PText textNode = new PText("Before");
    	textNode.setText("");
    	assertEquals("", textNode.getText());
    }
    
    public void testTextNullGetsInterpretedAsEmptyString() {
    	PText text = new PText("Before");
    	text.setText(null);
    	assertEquals("", text.getText());
    }

 
    public void testBoundsGrowWithTextByDefault() {
    	PText text123 = new PText("123");        
    	double width123 = text123.getBounds().getWidth();
    	
    	PText text1234 = new PText("1234");        
    	
    	double width1234 = text1234.getBounds().getWidth();
    	
        assertTrue(width123 < width1234); 
    }
    
    public void testBoundsOfEmptyString() {
        PText t = new PText();
        t.setText("");
        assertEquals(0, t.getBoundsReference().getWidth(), 0.000001);
        t.setText(null);
        assertEquals(0, t.getBoundsReference().getWidth(), 0.000001);
    }

    public void testToString() {
        PText t = new PText();
        t.setText("hello world");
        assertNotNull(t.toString());
    }
}
