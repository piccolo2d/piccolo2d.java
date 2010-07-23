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
package org.piccolo2d.nodes;

import java.awt.Shape;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

/**
 * Area node.
 */
public class PArea extends PShape
{
    private Area area;


    public PArea() {
        super();
        area = new Area();
    }

    public PArea(final Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException("shape must not be null");
        }
        this.area = new Area(shape);
    }

    public PArea(final Area area) {
        if (area == null) {
            throw new IllegalArgumentException("area must not be null");
        }
        this.area = new Area();
        this.area.add(area);
    }


    public void add(final Area area) {
        this.area.add(area);
        updateBounds();
    }

    public void exclusiveOr(final Area area) {
        this.area.exclusiveOr(area);
        updateBounds();
    }

    public void intersect(final Area area) {
        this.area.intersect(area);
        updateBounds();
    }

    public void subtract(final Area area) {
        this.area.subtract(area);
        updateBounds();
    }

    public boolean isPolygonal() {
        return area.isPolygonal();
    }

    public boolean isRectangular() {
        return area.isRectangular();
    }

    public boolean isSingular() {
        return area.isSingular();
    }

    // todo:
    //    area property change events?
    //    static methods

    /** {@inheritDoc} */
    protected Shape getShape() {
        return area;
    }

    /** {@inheritDoc} */
    protected void transform(final AffineTransform transform) {
        area.transform(transform);
    }
}