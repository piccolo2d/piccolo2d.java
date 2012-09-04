/*
 * Copyright (c) 2008-2012, Piccolo2D project, http://piccolo2d.org
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
package org.piccolo2d.examples.issues;

import java.awt.BasicStroke;

import org.piccolo2d.PCanvas;
import org.piccolo2d.nodes.PPath;
import org.piccolo2d.extras.PFrame;

/**
 * Example that demonstrates issue with zero width strokes in Issue 221.
 * <p>
 * Cannot draw veritical and horizontal lines of 0 width stroke using PPath<br/>
 * <a href="http://code.google.com/p/piccolo2d/issues/detail?id=221">http://code.google.com/p/piccolo2d/issues/detail?id=221</a>
 * </p>
 */
public class ZeroWidthStrokeBug extends PFrame {

    public ZeroWidthStrokeBug() {
        this(null);
    }

    public ZeroWidthStrokeBug(final PCanvas aCanvas) {
        super("ZeroWidthStrokeBug", false, aCanvas);
    }

    /** {@inheritDoc} */
    public void initialize() {
        PPath line1 = PPath.createLine(5f, 10f, 5f, 100f);
        line1.setStroke(new BasicStroke(0));
        getCanvas().getLayer().addChild(line1);

        PPath line2 = new PPath.Float();
        line2.setStroke(new BasicStroke(0));
        line2.moveTo(15f, 10f);
        line2.lineTo(15f, 100f);
        getCanvas().getLayer().addChild(line2);

        PPath line3 = PPath.createLine(25f, 10f, 26f, 100f);
        line3.setStroke(new BasicStroke(0));
        getCanvas().getLayer().addChild(line3);
    }

    public static void main(final String[] args) {
        new ZeroWidthStrokeBug();
    }
}
