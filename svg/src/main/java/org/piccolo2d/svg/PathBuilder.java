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

import java.awt.geom.GeneralPath;

/**
 * Collect data from {@link PathParser}. http://www.w3.org/TR/SVG11/paths.html
 * 
 * @author mr0738@mro.name
 */
class PathBuilder {

    private final GeneralPath gp = new GeneralPath();
    // last control point for the smooth curveTo and quadTo
    private float ox = 0, oy = 0;
    // last point
    private float px = 0, py = 0;

    /**
     * http://www.w3.org/TR/SVG11/paths.html#PathDataClosePathCommand
     */
    public void closePath() {
        gp.closePath();
    }

    /**
     * http://www.w3.org/TR/SVG11/paths.html#PathDataCubicBezierCommands
     */
    public void cubicTo(final boolean abs, float x1, float y1, float x2, float y2, float x3, float y3) {
        push(abs, x1, y1);
        x1 = px;
        y1 = py;
        push(abs, x2, y2);
        x2 = px;
        y2 = py;
        push(abs, x3, y3);
        x3 = px;
        y3 = py;
        gp.curveTo(x1, y1, x2, y2, x3, y3);
        // System.out.println("CubicTo " + abs + " x1=" + x1 + " y1=" + y1 + "
        // x2=" + x2 + " y2=" + y2 + " x3=" + x3
        // + " y3=" + y3);
    }

    /**
     * http://www.w3.org/TR/SVG11/paths.html#PathDataLinetoCommands
     */
    public void hlineTo(final boolean abs, final float x) {
        lineTo(abs, x, abs ? py : 0);
    }

    /**
     * http://www.w3.org/TR/SVG11/paths.html#PathDataLinetoCommands
     */
    public void lineTo(final boolean abs, final float x, final float y) {
        push(abs, x, y);
        gp.lineTo(px, py);
    }

    /**
     * http://www.w3.org/TR/SVG11/paths.html#PathDataMovetoCommands
     */
    public void moveTo(final boolean abs, final float x, final float y) {
        push(abs, x, y);
        gp.moveTo(px, py);
    }

    private void push(final boolean abs, float x, float y) {
        if (!abs) {
            x += px;
            y += py;
        }
        ox = px;
        oy = py;
        px = x;
        py = y;
    }

    /**
     * http://www.w3.org/TR/SVG11/paths.html#PathDataQuadraticBezierCommands
     */
    public void quadTo(final boolean abs, float x1, float y1, float x2, float y2) {
        push(abs, x1, y1);
        x1 = px;
        y1 = py;
        push(abs, x2, y2);
        x2 = px;
        y2 = py;
        gp.quadTo(x1, y1, x2, y2);
        // System.out.println("QuadTo " + " x1=" + x1 + " y1=" + y1 + " x2=" +
        // x2 + " y2=" + y2);
    }

    /**
     * http://www.w3.org/TR/SVG11/paths.html#PathDataCubicBezierCommands
     */
    public void smoothCubicTo(final boolean abs, final float x2, final float y2, final float x3, final float y3) {
        float x1 = 0;
        float y1 = 0;
        if (abs) {
            x1 = 2 * px - ox;
            y1 = 2 * py - oy;
        }
        else {
            // TODO verify!
            x1 = px - ox;
            y1 = py - oy;
        }
        cubicTo(abs, x1, y1, x2, y2, x3, y3);
        // System.out.println("SmoothCubicTo " + abs + " x2=" + x2 + " y2=" + y2
        // + " x3=" + x3 + " y3=" + y3);
    }

    /**
     * http://www.w3.org/TR/SVG11/paths.html#PathDataQuadraticBezierCommands
     */
    public void smoothQuadTo(final boolean abs, final float x2, final float y2) {
        float x1 = 0;
        float y1 = 0;
        if (abs) {
            x1 = 2 * px - ox;
            y1 = 2 * py - oy;
        }
        else {
            // TODO verify!
            x1 = px - ox;
            y1 = py - oy;
        }
        quadTo(abs, x1, y1, x2, y2);
        // System.out.println("SmoothQuadTo " + abs + " x2=" + x2 + " y2=" +
        // y2);
    }

    public GeneralPath toPath() {
        return gp;
    }

    /**
     * http://www.w3.org/TR/SVG11/paths.html#PathDataLinetoCommands
     */
    public void vlineTo(final boolean abs, final float y) {
        lineTo(abs, abs ? px : 0, y);
    }
}
