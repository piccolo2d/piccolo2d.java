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
import java.awt.event.MouseEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

/**
 * Mouse event overridden to wrap an SWT MouseEvent as a Swing MouseEvent.
 * 
 * @author Lance Good
 */
public class PSWTMouseEvent extends MouseEvent {
    private static final int SWT_BUTTON1 = 1;
    private static final int SWT_BUTTON2 = 2;
    private static final int SWT_BUTTON3 = 3;

    private static final long serialVersionUID = 1L;

    private static Component fakeSrc = new Component() {
    };

    /** Event being wrapped. */
    protected org.eclipse.swt.events.MouseEvent swtEvent;

    /** Number times the mouse was clicked in relation to the wrapped event. */
    protected int clickCount;

    /**
     * Constructs a PSWTMouseEvent that wraps the provided SWT MouseEvent as a
     * Swing one.
     * 
     * @param me Mouse Event being wrapped
     * @param type event type
     * @param clickCount number of times the mouse has been clicked
     */
    public PSWTMouseEvent(final org.eclipse.swt.events.MouseEvent me, final int type, final int clickCount) {
        super(fakeSrc, type, me.time, 0, me.x, me.y, clickCount, me.button == SWT_BUTTON3, me.button);

        swtEvent = me;
        this.clickCount = clickCount;
    }

    /** {@inheritDoc} */
    public Object getSource() {
        return swtEvent.getSource();
    }

    /** {@inheritDoc} */
    public int getClickCount() {
        return clickCount;
    }

    /** {@inheritDoc} */
    public int getButton() {
        switch (swtEvent.button) {
            case SWT_BUTTON1:
                return MouseEvent.BUTTON1;
            case SWT_BUTTON2:
                return MouseEvent.BUTTON2;
            case SWT_BUTTON3:
                return MouseEvent.BUTTON3;
            default:
                return MouseEvent.NOBUTTON;
        }
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
            if (swtEvent.button == SWT_BUTTON1 || (swtEvent.stateMask & SWT.BUTTON1) != 0) {
                modifiers = modifiers | InputEvent.BUTTON1_MASK;
            }
            if (swtEvent.button == SWT_BUTTON2 || (swtEvent.stateMask & SWT.BUTTON2) != 0) {
                modifiers = modifiers | InputEvent.BUTTON2_MASK;
            }
            if (swtEvent.button == SWT_BUTTON3 || (swtEvent.stateMask & SWT.BUTTON3) != 0) {
                modifiers = modifiers | InputEvent.BUTTON3_MASK;
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
            if (swtEvent.button == SWT_BUTTON1 || (swtEvent.stateMask & SWT.BUTTON1) != 0) {
                modifiers = modifiers | InputEvent.BUTTON1_DOWN_MASK;
            }
            if (swtEvent.button == SWT_BUTTON2 || (swtEvent.stateMask & SWT.BUTTON2) != 0) {
                modifiers = modifiers | InputEvent.BUTTON2_DOWN_MASK;
            }
            if (swtEvent.button == SWT_BUTTON3 || (swtEvent.stateMask & SWT.BUTTON3) != 0) {
                modifiers = modifiers | InputEvent.BUTTON3_DOWN_MASK;
            }
        }

        return modifiers;
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
