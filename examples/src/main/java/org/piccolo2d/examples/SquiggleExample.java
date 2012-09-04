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
package org.piccolo2d.examples;

import java.awt.BasicStroke;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;

import org.piccolo2d.PCanvas;
import org.piccolo2d.PLayer;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PDragSequenceEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PInputEventFilter;
import org.piccolo2d.extras.PFrame;
import org.piccolo2d.nodes.PPath;


public class SquiggleExample extends PFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private PLayer layer;

    public SquiggleExample() {
        this(null);
    }

    public SquiggleExample(final PCanvas aCanvas) {
        super("SquiggleExample", false, aCanvas);
    }

    public void initialize() {
        super.initialize();
        final PBasicInputEventHandler squiggleEventHandler = createSquiggleEventHandler();
        squiggleEventHandler.setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
        getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
        getCanvas().addInputEventListener(squiggleEventHandler);
        layer = getCanvas().getLayer();
    }

    public PBasicInputEventHandler createSquiggleEventHandler() {
        return new PDragSequenceEventHandler() {

            protected PPath squiggle;

            public void startDrag(final PInputEvent e) {
                super.startDrag(e);

                final Point2D p = e.getPosition();

                squiggle = new PPath.Float();
                squiggle.moveTo((float) p.getX(), (float) p.getY());
                squiggle.setStroke(new BasicStroke((float) (1 / e.getCamera().getViewScale())));
                layer.addChild(squiggle);
            }

            public void drag(final PInputEvent e) {
                super.drag(e);
                updateSquiggle(e);
            }

            public void endDrag(final PInputEvent e) {
                super.endDrag(e);
                updateSquiggle(e);
                squiggle = null;
            }

            public void updateSquiggle(final PInputEvent aEvent) {
                final Point2D p = aEvent.getPosition();
                squiggle.lineTo((float) p.getX(), (float) p.getY());
            }
        };
    }

    public static void main(final String[] args) {
        new SquiggleExample();
    }
}
