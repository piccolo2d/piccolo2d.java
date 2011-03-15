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
package org.piccolo2d.nodes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import org.piccolo2d.MockPropertyChangeListener;
import org.piccolo2d.nodes.PText;

import junit.framework.TestCase;

/**
 * Unit test for PText.
 */
public class PTextTest extends TestCase {

    private PText textNode;
    private MockPropertyChangeListener mockListener;

    public PTextTest(final String name) {
        super(name);
    }

    public void setUp() {
        textNode = new PText();
        mockListener = new MockPropertyChangeListener();
    }

    public void testClone() {
        textNode.setTextPaint(Color.BLUE);
        textNode.setText("Boo");
        final PText clonedNode = (PText) textNode.clone();
        assertEquals("Boo", clonedNode.getText());
        assertEquals(textNode.getFont(), clonedNode.getFont());
        assertEquals(Color.BLUE, clonedNode.getTextPaint());
    }

    public void testTextIsEmptyByDefault() {
        final PText textNode = new PText();
        assertEquals("", textNode.getText());
    }

    public void testTextMayBeAssignedEmptyString() {
        textNode.setText("");
        assertEquals("", textNode.getText());
    }

    public void testTextNullGetsInterpretedAsEmptyString() {
        textNode.setText(null);
        assertEquals("", textNode.getText());
    }

    public void testBoundsGrowWithTextByDefault() {
        final PText text123 = new PText("123");
        final double width123 = text123.getBounds().getWidth();

        final PText text1234 = new PText("1234");

        final double width1234 = text1234.getBounds().getWidth();

        assertTrue(width123 < width1234);
    }

    public void testBoundsOfEmptyString() {
        textNode.setText("");
        assertEquals(0, textNode.getBoundsReference().getWidth(), 0.000001);
        textNode.setText(null);
        assertEquals(0, textNode.getBoundsReference().getWidth(), 0.000001);
    }

    public void testToString() {
        textNode.setText("hello world");
        assertNotNull(textNode.toString());
    }

    public void testHorizontalAlignmentIsLeftByDefault() {
        assertEquals(Component.LEFT_ALIGNMENT, textNode.getHorizontalAlignment(), 0.000001);
    }

    public void testSetHorizontalAlignmentPersists() {
        textNode.setHorizontalAlignment(Component.RIGHT_ALIGNMENT);
        assertEquals(Component.RIGHT_ALIGNMENT, textNode.getHorizontalAlignment(), 0.000001);
    }

    public void testSetHorizontalAlignmentInvalidValues() {
        try {
            textNode.setHorizontalAlignment(-2.0f);
        }
        catch (final IllegalArgumentException e) {
            // expected
        }
        try {
            textNode.setHorizontalAlignment(2.0f);
        }
        catch (final IllegalArgumentException e) {
            // expected
        }
        try {
            textNode.setHorizontalAlignment(-Float.MAX_VALUE);
        }
        catch (final IllegalArgumentException e) {
            // expected
        }
        try {
            textNode.setHorizontalAlignment(Float.MAX_VALUE);
        }
        catch (final IllegalArgumentException e) {
            // expected
        }
        try {
            textNode.setHorizontalAlignment(-1.00f);
        }
        catch (final IllegalArgumentException e) {
            // expected
        }
        try {
            textNode.setHorizontalAlignment(1.00f);
        }
        catch (final IllegalArgumentException e) {
            // expected
        }
    }

    public void testTextPaintIsBlackByDefault() {
        assertEquals(Color.BLACK, textNode.getTextPaint());
    }

    public void testSetTextPaintPersists() {
        textNode.setTextPaint(Color.RED);
        assertEquals(Color.RED, textNode.getTextPaint());
    }

    public void testConstrainWidthToTextTrueByDefault() {
        assertTrue(textNode.isConstrainWidthToTextWidth());
    }

    public void testConstrainHeightToTextTrueByDefault() {
        assertTrue(textNode.isConstrainHeightToTextHeight());
    }

    public void testConstrainWidthPersists() {
        textNode.setConstrainWidthToTextWidth(true);
        assertTrue(textNode.isConstrainWidthToTextWidth());
    }

    public void testConstrainHeightPersists() {
        textNode.setConstrainHeightToTextHeight(true);
        assertTrue(textNode.isConstrainHeightToTextHeight());
    }

    public void testDefaultGreekThreshold() {
        assertEquals(PText.DEFAULT_GREEK_THRESHOLD, textNode.getGreekThreshold(), 0.000001);
    }

    public void testSetGreekThreshold() {
        textNode.setGreekThreshold(2);
        assertEquals(2, textNode.getGreekThreshold(), 0.000001);
    }

    public void testDefaultFont() {
        assertEquals(PText.DEFAULT_FONT, textNode.getFont());
    }

    public void testSetFontPersists() {
        final Font newFont = new Font("Arial", Font.BOLD, 10);
        textNode.setFont(newFont);
        assertEquals(newFont, textNode.getFont());
    }

    public void testSetFontFiresPropertyChangedEvent() {
        textNode.addPropertyChangeListener(PText.PROPERTY_FONT, mockListener);
        final Font newFont = new Font("Arial", Font.BOLD, 10);
        textNode.setFont(newFont);

        assertEquals(1, mockListener.getPropertyChangeCount());
        assertEquals(PText.PROPERTY_FONT, mockListener.getPropertyChange(0).getPropertyName());
    }
}
