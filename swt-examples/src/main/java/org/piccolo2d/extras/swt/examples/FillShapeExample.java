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


/**
 * Example demonstrating issue 187, general shapes are not filled
 * correctly under SWT.
 *
 * See {@link http://code.google.com/p/piccolo2d/issues/detail?id=187}
 */
public class FillShapeExample {

    /**
     * Create a new fill shape example.
     */
    public FillShapeExample() {
        super();
    }


    /**
     * Create and open a new shell on the specified display.
     *
     * @param display display
     * @return a new shell on the specified display
     */
    public static Shell open(final Display display) {
        final Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());

        final PSWTCanvas canvas = new PSWTCanvas(shell, 0);

        final PSWTPath path = PSWTPath.createPolyline(new float[] { 25.0f, 75.0f, 25.0f }, new float[] { 100.0f, 200.0f, 300.0f });
        path.setPaint(Color.YELLOW);
        path.setStrokeColor(Color.BLUE);
        //path.setStrokePaint(Color.BLUE);

        final PSWTPath pathNullPaint = PSWTPath.createPolyline(new float[] { 125.0f, 175.0f, 125.0f }, new float[] { 100.0f, 200.0f, 300.0f });
        pathNullPaint.setPaint(null);
        pathNullPaint.setStrokeColor(Color.BLUE);
        //pathNullPaint.setStrokePaint(Color.BLUE);

        //final PSWTPath pathNullStroke = PSWTPath.createPolyline(new float[] { 225.0f, 275.0f, 225.0f }, new float[] { 100.0f, 200.0f, 300.0f });
        //pathNullStroke.setPaint(Color.YELLOW);
        //pathNullStroke.setStroke(null);
        //pathNullStroke.setStrokeColor(Color.BLUE);
        //pathNullStroke.setStrokePaint(Color.BLUE);

        final PSWTPath pathNullStrokePaint = PSWTPath.createPolyline(new float[] { 325.0f, 375.0f, 325.0f }, new float[] { 100.0f, 200.0f, 300.0f });
        pathNullStrokePaint.setPaint(Color.YELLOW);
        pathNullStrokePaint.setStrokeColor(null);
        //pathNullStrokePaint.setStrokePaint(Color.BLUE);

        final PSWTPath rect = PSWTPath.createRectangle(25.0f, 400.0f, 50.0f, 100.0f);
        rect.setPaint(Color.YELLOW);
        rect.setStrokeColor(Color.BLUE);
        //rect.setStrokePaint(Color.BLUE);

        final PSWTPath rectNullPaint = PSWTPath.createRectangle(125.0f, 400.0f, 50.0f, 100.0f);
        rectNullPaint.setPaint(null);
        rectNullPaint.setStrokeColor(Color.BLUE);
        //rectNullPaint.setStrokePaint(Color.BLUE);

        //final PSWTPath rectNullStroke = PSWTPath.createRectangle(225.0f, 400.0f, 50.0f, 100.0f);
        //rectNullStroke.setPaint(Color.YELLOW);
        //rectNullStroke.setStroke(null);
        //rectNullStroke.setStrokeColor(Color.BLUE);
        //rectNullStroke.setStrokePaint(Color.BLUE);

        final PSWTPath rectNullStrokePaint = PSWTPath.createRectangle(325.0f, 400.0f, 50.0f, 100.0f);
        rectNullStrokePaint.setPaint(Color.YELLOW);
        rectNullStrokePaint.setStrokeColor(null);
        //rectNullStrokePaint.setStrokePaint(Color.BLUE);

        canvas.getLayer().addChild(path);
        canvas.getLayer().addChild(pathNullPaint);
        //canvas.getLayer().addChild(pathNullStroke);
        canvas.getLayer().addChild(pathNullStrokePaint);

        canvas.getLayer().addChild(rect);
        canvas.getLayer().addChild(rectNullPaint);
        //canvas.getLayer().addChild(rectNullStroke);
        canvas.getLayer().addChild(rectNullStrokePaint);

        shell.open();
        return shell;
    }


    /**
     * Main.
     *
     * @param args command line arguments, ignored
     */
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
