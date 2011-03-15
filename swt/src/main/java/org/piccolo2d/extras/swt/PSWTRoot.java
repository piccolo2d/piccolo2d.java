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

import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.eclipse.swt.widgets.Composite;
import org.piccolo2d.PRoot;


/**
 * <b>PSWTRoot</b> is a subclass of PRoot that is designed to work in the SWT
 * environment. In particular it uses SWTTimers and the SWT event dispatch
 * thread. With the current setup only a single PSWTCanvas is expected to be
 * connected to a root.
 * 
 * @version 1.1
 * @author Jesse Grosjean
 */
public class PSWTRoot extends PRoot {
    private static final long serialVersionUID = 1L;
    private final Composite composite;

    /**
     * Constructs a PSWTRoot attached to the provided composite.
     * 
     * @param composite composite PSWTRoot is responsible for
     */
    public PSWTRoot(final Composite composite) {
        this.composite = composite;
    }

    /**
     * Creates a timer that will fire the listener every delay milliseconds.
     * 
     * @param delay time in milliseconds between firings of listener
     * @param listener listener to be fired
     * 
     * @return the created timer
     */
    public Timer createTimer(final int delay, final ActionListener listener) {
        return new SWTTimer(composite.getDisplay(), delay, listener);
    }

    /**
     * Processes Inputs if any kind of IO needs to be done.
     */
    public void scheduleProcessInputsIfNeeded() {
        if (!Thread.currentThread().equals(composite.getDisplay().getThread())) {
            return;
        }

        if (!processInputsScheduled && !processingInputs
                && (getFullBoundsInvalid() || getChildBoundsInvalid() || getPaintInvalid() || getChildPaintInvalid())) {

            processInputsScheduled = true;
            composite.getDisplay().asyncExec(new Runnable() {
                public void run() {
                    processInputs();
                    processInputsScheduled = false;
                }
            });
        }
    }
}
