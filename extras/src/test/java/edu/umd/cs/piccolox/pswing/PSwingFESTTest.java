/*
 * Copyright (c) 2008-2010, Piccolo2D project, http://piccolo2d.org
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JList;

import junit.framework.TestCase;

import org.fest.swing.core.MouseButton;
import org.fest.swing.core.MouseClickInfo;
import org.fest.swing.exception.ComponentLookupException;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JListFixture;

/**
 * The <b>PSwingFESTTest</b> is a TestCase designed to ensure Piccolo2D plays
 * well with the FEST GUI Testing Framework.
 */
public class PSwingFESTTest extends TestCase {
    private FrameFixture frameFixture;

    public void setUp() {
        JFrame frame = new JFrame();
        PSwingCanvas swingCanvas = new PSwingCanvas();
        swingCanvas.setName("canvas");
        swingCanvas.setPreferredSize(new Dimension(300, 300));

        JList testList = new JList(new String[] { "One", "Two", "Three" });
        testList.setName("testList");
        swingCanvas.getLayer().addChild(new PSwing(testList));

        frame.getContentPane().add(swingCanvas);
        frame.pack();

        frameFixture = new FrameFixture(frame);
        frameFixture.show();
    }

    public void tearDown() {
        frameFixture.cleanUp();
    }

    public void testFESTThrowsExceptionWhenComponentNotFound() {
        try {
            frameFixture.list("invalidListName");
        }
        catch (ComponentLookupException expected) {
            // Expected
        }
    }

    public void testUnderlyingSwingComponentsAreAccessibleToFEST() {
        JListFixture listFixture = frameFixture.list("testList");
        listFixture.selectItem("One");
        listFixture.requireVisible();
        listFixture.click(MouseClickInfo.leftButton());

        assertFirstElementOfListSelected();
    }

    public void testClickingOnPSwingPassesThroughToComponent() {
        Component canvas = frameFixture.robot.finder().findByName("canvas");
        assertNotNull(canvas);

        Point point = canvas.getLocationOnScreen();
        Point firstElementPoint = new Point(point.x + 5, point.y + 5);
        frameFixture.robot.click(firstElementPoint, MouseButton.LEFT_BUTTON, 1);

        assertFirstElementOfListSelected();
    }

    private void assertFirstElementOfListSelected() {
        JListFixture listFixture = frameFixture.list("testList");

        String[] selection = listFixture.selection();
        assertNotNull(selection);
        assertFalse(0 == selection.length);
        assertEquals("One", selection[0]);
    }
}
