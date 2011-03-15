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
package org.piccolo2d.extras.event;

import java.awt.event.InputEvent;

import org.piccolo2d.PCamera;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PInputEventFilter;
import org.piccolo2d.util.PBounds;


/**
 * <b>PZoomToEventHandler</b> is used to zoom the camera view to the node
 * clicked on with button one.
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PZoomToEventHandler extends PBasicInputEventHandler {
    private static final int ZOOM_SPEED = 500;

    /**
     * Constructs a PZoomToEventHandler that only recognizes BUTTON1 events.
     */
    public PZoomToEventHandler() {
        setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
    }

    /**
     * Zooms the camera's view to the pressed node when button 1 is pressed.
     * 
     * @param event event representing the mouse press
     */
    public void mousePressed(final PInputEvent event) {
        zoomTo(event);
    }

    /**
     * Zooms the camera to the picked node of the event.
     * @param event Event from which to extract the zoom target
     */
    protected void zoomTo(final PInputEvent event) {
        PBounds zoomToBounds;
        final PNode picked = event.getPickedNode();

        if (picked instanceof PCamera) {
            final PCamera c = (PCamera) picked;
            zoomToBounds = c.getUnionOfLayerFullBounds();
        }
        else {
            zoomToBounds = picked.getGlobalFullBounds();
        }

        event.getCamera().animateViewToCenterBounds(zoomToBounds, true, ZOOM_SPEED);
    }
}
