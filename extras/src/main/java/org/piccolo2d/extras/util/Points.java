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
package org.piccolo2d.extras.util;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Interface for a sequence of points.
 */
public interface Points {
    /**
     * Returns the number of points in the sequence.
     * 
     * @return number of points in the sequence
     */
    int getPointCount();

    /**
     * Returns the x component of the point at the given index.
     * 
     * @param i index of desired point
     * 
     * @return x component of point
     */
    double getX(int i);

    /**
     * Returns the y component of the point at the given index.
     * 
     * @param i index of desired point
     * 
     * @return y component of point
     */
    double getY(int i);

    /**
     * Returns a point representation of the coordinates at the given index.
     * 
     * @param i index of desired point
     * @param dst output parameter into which the point's details will be
     *            populated, if null a new one will be created.
     * 
     * @return a point representation of the coordinates at the given index
     */
    Point2D getPoint(int i, Point2D dst);

    /**
     * Returns the bounds of all the points taken as a whole.
     * 
     * @param dst output parameter to store bounds into, if null a new rectangle
     *            will be created
     * @return rectangle containing the bounds
     */
    Rectangle2D getBounds(Rectangle2D dst);
}
