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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.RepaintManager;

import junit.framework.TestCase;
import edu.umd.cs.piccolox.PFrame;

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

    public void setUp() {
        RepaintManager.setCurrentManager(new PSwingRepaintManager());
    }

    public void testConstructorFailsOnNullComponent() {
        try {
            new PSwing(null);
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void testPSwingRegistersItselfWithComponent() {
        JPanel panel = new JPanel();
        PSwing pSwing = new PSwing(panel);

        assertEquals(pSwing, panel.getClientProperty(PSwing.PSWING_PROPERTY));
    }

    public void testGetComponentReturnsValidComponent() {
        JPanel panel = new JPanel();
        PSwing pSwing = new PSwing(panel);
        assertEquals(panel, pSwing.getComponent());
    }

    public void testPSwingResizesItselfWhenComponentIsResized() {
        final boolean[] reshaped = new boolean[1];
        JPanel panel = new JPanel();

        new PSwing(panel) {
            protected void reshape() {
                super.reshape();

                reshaped[0] = true;
            }
        };
        panel.setSize(100, 100);
        assertTrue(reshaped[0]);
    }

    public void testPSwingDelegatesPaintingToItsComponent() throws IOException {
        JPanel panel = new JPanel();
        PSwing pSwing = new PSwing(panel);
        panel.setBackground(Color.RED);
        panel.setPreferredSize(new Dimension(100, 100));

        BufferedImage img = new BufferedImage(100, 100,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .createGraphics(img);

        pSwing.paint(graphics);
        assertEquals(Color.RED.getRGB(), img.getRGB(50, 50));
    }
    
    public void testHidingComponentHidesPSwing() throws InterruptedException {        
        JPanel panel = new JPanel();
        PSwing pSwing = new PSwing(panel);
        panel.setPreferredSize(new Dimension(100, 100));
        pSwing.setBounds(0, 0, 00, 100);
        panel.setVisible(false);
        
        // Wow, do I hate this next line. Turns out that the event dispatch 
        // thread needs time to push the component hidden method before this test passes
        // There has to be a way of forcing this without a sleep
        Thread.sleep(50);
        assertFalse(pSwing.getVisible());
    }
    
    public void testHidingPNodeHidesComponent() {
        JPanel panel = new JPanel();
        PSwing pSwing = new PSwing(panel);
        pSwing.setVisible(false);
        assertFalse(panel.isVisible());
    }
}
