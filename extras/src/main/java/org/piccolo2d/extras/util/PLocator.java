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
import java.io.Serializable;

/**
 * <b>PLocator</b> provides an abstraction for locating points. Subclasses such
 * as PNodeLocator and PBoundsLocator specialize this behavior by locating
 * points on nodes, or on the bounds of nodes.
 * <P>
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public abstract class PLocator implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor provided for subclasses. Does nothing by itself.
     */
    public PLocator() {
    }

    /**
     * Locates the point this locator is responsible for finding, and stores it
     * in dstPoints. Should dstPoints be null, it will create a new point and
     * return it.
     * 
     * @param dstPoint output parameter to store the located point
     * @return the located point
     */
    public Point2D locatePoint(final Point2D dstPoint) {
        Point2D result;
        if (dstPoint == null) {
            result = new Point2D.Double();
        }
        else {
            result = dstPoint;
        }
        result.setLocation(locateX(), locateY());
        return result;
    }

    /**
     * Locates the X component of the position this locator finds.
     * 
     * @return x component of located point
     */
    public abstract double locateX();

    /**
     * Locates the Y component of the position this locator finds.
     * 
     * @return y component of located point
     */
    public abstract double locateY();
}
