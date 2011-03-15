/*
 * Copyright (c) 2008-2011, Piccolo2D project, http://piccolo2d.org
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

import java.awt.Shape;
import java.awt.Stroke;

import org.piccolo2d.util.PPickPath;


/**
 * 
 * @see org.piccolo2d.nodes.PPath
 * @see Stroke
 * @version 1.3
 * @author Marcus Rohrmoser
 */
abstract class PSemanticStroke implements Stroke {
    protected static final double THRESHOLD = 1e-6;

    private transient float recentScale;
    private transient Stroke recentStroke;
    protected final Stroke stroke;

    protected PSemanticStroke(final Stroke stroke) {
        this.stroke = stroke;
        recentStroke = stroke;
        recentScale = 1.0F;
    }

    /**
     * Ask {@link #getActiveScale()}, call {@link #newStroke(float)} if
     * necessary and delegate to {@link Stroke#createStrokedShape(Shape)}.
     * 
     * @param s
     */
    public Shape createStrokedShape(final Shape s) {
        final float currentScale = getActiveScale();
        if (Math.abs(currentScale - recentScale) > THRESHOLD) {
            recentScale = currentScale;
            recentStroke = newStroke(recentScale);
        }
        return recentStroke.createStrokedShape(s);
    }

    /**
     * Returns true if this stroke is equivalent to the object provided.
     * 
     * @param obj Object being tested
     * @return true if object is equivalent
     */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PSemanticStroke other = (PSemanticStroke) obj;
        if (stroke == null) {
            if (other.stroke != null) {
                return false;
            }
        }
        else if (!stroke.equals(other.stroke)) {
            return false;
        }
        return true;
    }

    /**
     * Detect the current scale. Made protected to enable custom
     * re-implementations.
     */
    protected float getActiveScale() {
        if (PPickPath.CURRENT_PICK_PATH != null) {
            return (float) PPickPath.CURRENT_PICK_PATH.getScale();
        }

        return 1.0f;
    }

    public int hashCode() {
        final int prime = 31;
        int result = prime;

        if (stroke != null) {
            result += stroke.hashCode();
        }

        return result;
    }

    /**
     * Factory to create a new internal stroke delegate. Made protected to
     * enable custom re-implementations.
     */
    protected abstract Stroke newStroke(final float activeScale);

    public String toString() {
        return stroke.toString();
    }
}
