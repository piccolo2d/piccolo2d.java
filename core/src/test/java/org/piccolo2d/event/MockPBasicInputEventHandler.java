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
package org.piccolo2d.event;

import java.util.ArrayList;

import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;

/**
 * Mock PBasicInputEventHandler.
 */
public class MockPBasicInputEventHandler extends PBasicInputEventHandler {
    private final ArrayList methodCalls = new ArrayList();

    public String[] getMethodCalls() {
        final String[] result = new String[methodCalls.size()];
        for (int i = 0; i < methodCalls.size(); i++) {
            result[i] = (String) methodCalls.get(i);
        }
        return result;
    }

    public void keyboardFocusGained(final PInputEvent event) {
        methodCalls.add("keyboardFocusGained");
        super.keyboardFocusGained(event);
    }

    public void keyboardFocusLost(final PInputEvent event) {
        methodCalls.add("keyboardFocusLost");
        super.keyboardFocusLost(event);
    }

    public void keyPressed(final PInputEvent event) {
        methodCalls.add("keyPressed");
        super.keyPressed(event);
    }

    public void keyReleased(final PInputEvent event) {
        methodCalls.add("keyReleased");
        super.keyReleased(event);
    }

    public void keyTyped(final PInputEvent event) {
        methodCalls.add("keyTyped");
        super.keyTyped(event);
    }

    public void mouseClicked(final PInputEvent event) {
        methodCalls.add("mouseClicked");
        super.mouseClicked(event);
    }

    public void mouseDragged(final PInputEvent event) {
        methodCalls.add("mouseDragged");
        super.mouseDragged(event);
    }

    public void mouseEntered(final PInputEvent event) {
        methodCalls.add("mouseEntered");
        super.mouseEntered(event);
    }

    public void mouseExited(final PInputEvent event) {
        methodCalls.add("mouseExited");
        super.mouseExited(event);
    }

    public void mouseMoved(final PInputEvent event) {
        methodCalls.add("mouseMoved");
        super.mouseMoved(event);
    }

    public void mousePressed(final PInputEvent event) {
        methodCalls.add("mousePressed");
        super.mousePressed(event);
    }

    public void mouseReleased(final PInputEvent event) {
        methodCalls.add("mouseReleased");
        super.mouseReleased(event);
    }

    public void mouseWheelRotated(final PInputEvent event) {
        methodCalls.add("mouseReleased");
        super.mouseWheelRotated(event);
    }

    public void mouseWheelRotatedByBlock(final PInputEvent event) {
        methodCalls.add("mouseWheelRotatedByBlock");
        super.mouseWheelRotatedByBlock(event);
    }
}
