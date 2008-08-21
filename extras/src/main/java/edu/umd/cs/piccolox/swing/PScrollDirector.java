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
package edu.umd.cs.piccolox.swing;

import java.awt.*;
import java.awt.geom.*;

import edu.umd.cs.piccolo.PCanvas;

/**
 * The interface an application can implement to control scrolling in a
 * PScrollPane->PViewport->ZCanvas component hierarchy.
 * 
 * @see PDefaultScrollDirector
 * @author Lance Good
 */
public interface PScrollDirector {

    /**
     * Installs the scroll director
     * 
     * @param viewport The viewport on which this director directs
     * @param view The ZCanvas that the viewport looks at
     */
    public void install(PViewport viewport, PCanvas view);

    /**
     * Uninstall the scroll director
     */
    public void unInstall();

    /**
     * Get the View position given the specified camera bounds
     * 
     * @param viewBounds The bounds for which the view position will be computed
     * @return The view position
     */
    public Point getViewPosition(Rectangle2D viewBounds);

    /**
     * Set the view position
     * 
     * @param x The new x position
     * @param y The new y position
     */
    public void setViewPosition(double x, double y);

    /**
     * Get the size of the view based on the specified camera bounds
     * 
     * @param viewBounds The view bounds for which the view size will be
     *            computed
     * @return The view size
     */
    public Dimension getViewSize(Rectangle2D viewBounds);
}
