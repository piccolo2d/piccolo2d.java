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
package org.piccolo2d.extras.swt.examples;

import java.awt.Color;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.piccolo2d.extras.swt.PSWTCanvas;
import org.piccolo2d.extras.swt.PSWTPath;
import org.piccolo2d.extras.swt.PSWTText;


public final class SWTBasicExample {

    /**
     * Create and open a new shell on the specified display.
     *
     * @param display display
     * @return a new shell on the specified display
     */
    public static Shell open(final Display display) {
        final Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());

        // create a new SWT canvas
        final PSWTCanvas canvas = new PSWTCanvas(shell, 0);

        // create some SWT nodes
        //    and add them as child nodes to the canvas' camera's first layer
        PSWTPath rect = PSWTPath.createRectangle(25, 25, 50, 50);
        rect.setPaint(Color.RED);
        canvas.getLayer().addChild(rect);

        rect = PSWTPath.createRectangle(300, 25, 100, 50);
        rect.setPaint(Color.BLUE);
        canvas.getLayer().addChild(rect);

        PSWTPath circle = PSWTPath.createEllipse(100, 200, 50, 50);
        circle.setPaint(Color.GREEN);
        canvas.getLayer().addChild(circle);

        circle = PSWTPath.createEllipse(120, 220, 40, 40);
        circle.setPaint(Color.GRAY);
        circle.setTransparency(0.2f);
        canvas.getLayer().addChild(circle);

        circle = PSWTPath.createEllipse(400, 400, 75, 150);
        circle.setPaint(Color.YELLOW);
        canvas.getLayer().addChild(circle);


        PSWTText text = new PSWTText("Hello World\nMultiline");        
        text.translate(350, 150);
        text.setPenColor(Color.GRAY);
        text.setBackgroundColor(Color.BLACK);
        canvas.getLayer().addChild(text);

        text = new PSWTText("Goodbye World");
        text.translate(50, 400);
        text.setPenColor(Color.MAGENTA);
        canvas.getLayer().addChild(text);

        shell.open();
        return shell;
    }
    
    public static void main(final String[] args) {
        final Display display = new Display();
        final Shell shell = open(display);
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }
}
