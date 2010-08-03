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
public final class PArea extends PShape {

    /** Area for this area node. */
    private transient Area area;


    /**
     * Create a new area node with an empty area.
     */
    public PArea() {
        area = new Area();
    }

    /**
     * Create a new area node with the specified shape.
     *
     * @param shape shape, must not be null
     */
    public PArea(final Shape shape) {
        if (shape == null) {
            throw new NullPointerException("shape must not be null");
        }
        this.area = new Area(shape);
    }

    /**
     * Create a new area node with the specified area.
     *
     * @param area area, must not be null
     */
    public PArea(final Area area) {
        if (area == null) {
            throw new NullPointerException("area must not be null");
        }
        this.area = new Area();
        this.area.add(area);
    }


    /**
     * Add the shape of the specified area to the shape of this area node.
     * The resulting shape of this area node will include the union of both shapes,
     * or all areas that were contained in either this or the specified area.
     *
     * @param area area to add, must not be null
     * @throws NullPointerException if area is null
     */
    public void add(final Area area) {
        this.area.add(area);
        updateBoundsFromShape();
    }

    /**
     * Set the shape of this area node to be the combined area of its current
     * shape and the shape of the specified area, minus their intersection. The
     * resulting shape of this area node will include only areas that were contained
     * in either this area node or in the specified area, but not in both. 
     *
     * @param area area to exclusive or, must not be null
     * @throws NullPointerException if area is null
     */
    public void exclusiveOr(final Area area) {
        this.area.exclusiveOr(area);
        updateBoundsFromShape();
    }

    /**
     * Set the shape of this area node to the intersection of its current shape
     * and the shape of the specified area. The resulting shape of this area node
     * will include only areas that were contained in both this area node and also
     * in the specified area.
     *
     * @param area area to intersect, must not be null
     * @throws NullPointerException if area is null
     */
    public void intersect(final Area area) {
        this.area.intersect(area);
        updateBoundsFromShape();
    }

    /**
     * Subtract the shape of the specified area from the shape of this area node.
     * The resulting shape of this area node will include areas that were contained
     * only in this area node and not in the specified area.
     *
     * @param area area to subtract, must not be null
     * @throws NullPointerException if area is null
     */
    public void subtract(final Area area) {
        this.area.subtract(area);
        updateBoundsFromShape();
    }

    /**
     * Removes all of the geometry from this area node and restores it to an empty area.
     */
    public void reset() {
        area.reset();
        updateBoundsFromShape();
    }

    /**
     * Return true if this area node represents an empty area.
     *
     * @return true if this area node represents an empty area
     */
    public boolean isEmpty() {
        return area.isEmpty();
    }

    /**
     * Return true if this area node consists entirely of straight-edged polygonal geometry.
     *
     * @return true if this area node consists entirely of straight-edged polygonal geometry
     */
    public boolean isPolygonal() {
        return area.isPolygonal();
    }

    /**
     * Return true if this area node is rectangular in shape.
     *
     * @return true if this area node is rectangular in shape
     */
    public boolean isRectangular() {
        return area.isRectangular();
    }

    /**
     * Return true if this area node is comprised of a single closed subpath. This
     * method returns true if the path contains 0 or 1 subpaths, or false if the path
     * contains more than 1 subpath. The subpaths are counted by the number of
     * <code>SEG_MOVETO</code> segments that appear in the path. 
     *
     * @return true if this area node is comprised of a single closed subpath
     */
    public boolean isSingular() {
        return area.isSingular();
    }

    // todo:
    //    area property change events?
    //    static methods
    //    should modifiers return this to allow chaining, e.g. add(area0).intersect(area1)
    //    test serialization, may have to add custom code to serialize areas

    /** {@inheritDoc} */
    protected Shape getShape() {
        return area;
    }

    /** {@inheritDoc} */
    protected void transform(final AffineTransform transform) {
        area.transform(transform);
    }
}