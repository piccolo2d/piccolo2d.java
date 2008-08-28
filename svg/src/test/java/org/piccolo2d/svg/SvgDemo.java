/*
 * Copyright (c) 2008, Piccolo2D project, http://piccolo2d.org
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
 * None of the name of the Piccolo2D project, the University of Maryland, or the names of its contributors
 * may be used to endorse or promote products derived from this software without specific prior written
 * permission.
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
package org.piccolo2d.svg;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;

public class SvgDemo {

    public static void main(final String[] args) throws IOException {
        final JFrame frame = new JFrame();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent e) {
                System.exit(0);
            }
        });
        frame.setSize(600, 800);

        final PCanvas zui = new PCanvas();
        frame.setTitle(SvgDemo.class.getName());
        frame.getContentPane().add(zui);
        {
            final PNode world = new SvgLoader().load(SvgLoadTest.findResource("/ice-plain.svg").openStream());
        }
        final long start = System.currentTimeMillis();
        final PNode world = new SvgLoader().load(SvgLoadTest.findResource("/w3c-svg/path/quad01.svg").openStream());
        System.out.println(System.currentTimeMillis() - start);

        zui.getLayer().addChild(world);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.setVisible(true);
            }
        });

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                zui.getCamera().animateViewToCenterBounds(world.getFullBounds(), true, 500);
            }
        });
    }
}
