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
package org.piccolo2d.extras.swt;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

/**
 * Key event overridden to wrap an SWT KeyEvent as a swing KeyEvent.
 * 
 * @author Lance Good
 */
public class PSWTKeyEvent extends KeyEvent {
    private static final long serialVersionUID = 1L;

    private static Component fakeSrc = new Component() {
    };

    private org.eclipse.swt.events.KeyEvent swtEvent;

    /**
     * Creates an object that wraps a SWT Key event. Making it queriable from
     * Piccolo2d as though it were a Swing one.
     * 
     * @param ke key event object
     * @param eventType type of key event
     */
    public PSWTKeyEvent(final org.eclipse.swt.events.KeyEvent ke, final int eventType) {
        super(fakeSrc, eventType, ke.time, 0, ke.keyCode, ke.character, KeyEvent.KEY_LOCATION_STANDARD);

        swtEvent = ke;
    }

    /** {@inheritDoc} */
    public Object getSource() {
        return swtEvent.getSource();
    }

    /** {@inheritDoc} */
    public boolean isShiftDown() {
        return (swtEvent.stateMask & SWT.SHIFT) != 0;
    }

    /** {@inheritDoc} */
    public boolean isControlDown() {
        return (swtEvent.stateMask & SWT.CONTROL) != 0;
    }

    /** {@inheritDoc} */
    public boolean isAltDown() {
        return (swtEvent.stateMask & SWT.ALT) != 0;
    }

    /** {@inheritDoc} */
    public int getModifiers() {
        int modifiers = 0;

        if (swtEvent != null) {
            if ((swtEvent.stateMask & SWT.ALT) != 0) {
                modifiers = modifiers | InputEvent.ALT_MASK;
            }
            if ((swtEvent.stateMask & SWT.CONTROL) != 0) {
                modifiers = modifiers | InputEvent.CTRL_MASK;
            }
            if ((swtEvent.stateMask & SWT.SHIFT) != 0) {
                modifiers = modifiers | InputEvent.SHIFT_MASK;
            }
        }

        return modifiers;
    }

    /** {@inheritDoc} */
    public int getModifiersEx() {
        int modifiers = 0;

        if (swtEvent != null) {
            if ((swtEvent.stateMask & SWT.ALT) != 0) {
                modifiers = modifiers | InputEvent.ALT_DOWN_MASK;
            }
            if ((swtEvent.stateMask & SWT.CONTROL) != 0) {
                modifiers = modifiers | InputEvent.CTRL_DOWN_MASK;
            }
            if ((swtEvent.stateMask & SWT.SHIFT) != 0) {
                modifiers = modifiers | InputEvent.SHIFT_DOWN_MASK;
            }
        }

        return modifiers;
    }

    /** {@inheritDoc} */
    public boolean isActionKey() {
        return false;
    }

    /**
     * Returns the widget from which the event was emitted.
     * 
     * @return source widget
     */
    public Widget getWidget() {
        return swtEvent.widget;
    }

    /**
     * Return the display on which the interaction occurred.
     * 
     * @return display on which the interaction occurred
     */
    public Display getDisplay() {
        return swtEvent.display;
    }

    /**
     * Return the associated SWT data for the event.
     * 
     * @return data associated to the SWT event
     */
    public Object getData() {
        return swtEvent.data;
    }
}
