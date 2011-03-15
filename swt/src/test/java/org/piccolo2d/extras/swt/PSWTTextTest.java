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

import java.awt.Color;
import java.awt.Font;

import java.awt.geom.Point2D;

import org.piccolo2d.extras.swt.PSWTText;
import org.piccolo2d.util.PBounds;

import junit.framework.TestCase;


/**
 * Unit test for PSWTText.
 */
public class PSWTTextTest extends TestCase {
    private PSWTText textNode;

    public void setUp() {
        textNode = new PSWTText();
    }

    public void testConstructorRemembersTextValue() {
        textNode = new PSWTText("Hello World\n\n");
        assertEquals("Hello World\n\n", textNode.getText());
    }

    public void testTextPersistsTrainingAndInternalNewlines() {
        textNode.setText("Hello\nWorld\n\n");
        assertEquals("Hello\nWorld\n\n", textNode.getText());
    }

    public void testDefaultPropertiesAreCorrect() {
        assertEquals(Color.BLACK, textNode.getPenColor());
        assertEquals(Color.BLACK, textNode.getPenPaint());
        assertNull(textNode.getBackgroundColor());
        assertNull(textNode.getPaint());
        assertEquals(5.5, textNode.getGreekThreshold(), Double.MIN_VALUE);
        assertFalse(textNode.isTransparent());
    }

    public void testDefaultFontIsCorrect() {
        Font font = textNode.getFont();
        assertNotNull(font);
        assertFalse(font.isBold());
        assertEquals(12, font.getSize());
    }

    public void testPenColorPersists() {
        textNode.setPenColor(Color.RED);
        assertEquals(Color.RED, textNode.getPenColor());
    }

    public void testPenPaintPersists() {
        textNode.setPenPaint(Color.RED);
        assertEquals(Color.RED, textNode.getPenPaint());
    }

    public void testTransparencyPersists() {
        textNode.setTransparent(true);
        assertTrue(textNode.isTransparent());
    }

    public void testBackgroundColor() {
        textNode.setBackgroundColor(Color.RED);
        assertEquals(Color.RED, textNode.getBackgroundColor());
    }

    public void testPenPaintAndPenColorAreSameThing() {
        textNode.setPenColor(Color.RED);
        assertEquals(Color.RED, textNode.getPenPaint());

        textNode.setPenPaint(Color.BLUE);
        assertEquals(Color.BLUE, textNode.getPenColor());
    }

    public void testBackgroundColorAndPaintAreSameThing() {
        textNode.setBackgroundColor(Color.RED);
        assertEquals(Color.RED, textNode.getPaint());

        textNode.setPaint(Color.BLUE);
        assertEquals(Color.BLUE, textNode.getBackgroundColor());
    }

    public void testGreekThresholdPersists() {
        textNode.setGreekThreshold(0.1);
        assertEquals(0.1, textNode.getGreekThreshold(), Double.MIN_VALUE);
    }

    public void testShrinkingFontShrinksBounds() {
        textNode.setText("Hello\nWorld");

        PBounds startBounds = textNode.getBounds();
        Font startFont = textNode.getFont();
        Font newFont = new Font(startFont.getFontName(), startFont.getStyle(), 8);

        textNode.setFont(newFont);
        assertSame(newFont, textNode.getFont());

        PBounds endBounds = textNode.getBounds();
        assertTrue(startBounds.width > endBounds.width);
        assertTrue(startBounds.height > endBounds.height);
    }

    public void testTranslationsBehaveLogically() {
        textNode.setTranslation(1, 2);
        assertEquals(1, textNode.getTranslateX(), Double.MIN_VALUE);
        assertEquals(2, textNode.getTranslateY(), Double.MIN_VALUE);

        textNode.setTranslateX(3);
        assertEquals(3, textNode.getTranslateX(), Double.MIN_VALUE);

        textNode.setTranslateY(4);
        assertEquals(4, textNode.getTranslateY(), Double.MIN_VALUE);

        assertEquals(new Point2D.Double(3, 4), textNode.getTranslation());

        textNode.setTranslation(new Point2D.Double(5, 6));
        assertEquals(new Point2D.Double(5, 6), textNode.getTranslation());
    }

    public void testTranslatingDoesntAffectSize() {
        textNode.setText("Hello");
        PBounds startBounds = textNode.getBounds();
        textNode.translate(1, 2);
        PBounds endBounds = textNode.getBounds();
        assertEquals(startBounds.width, endBounds.width, Double.MIN_VALUE);
        assertEquals(startBounds.height, endBounds.height, Double.MIN_VALUE);
    }

}
