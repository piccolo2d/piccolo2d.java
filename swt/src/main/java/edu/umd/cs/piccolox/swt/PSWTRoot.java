/*
 * Copyright (c) 2008, Piccolo2D project, http://piccolo2d.org
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
package edu.umd.cs.piccolox.swt;

import java.awt.event.ActionListener;
import javax.swing.Timer;

import org.eclipse.swt.widgets.Composite;

import edu.umd.cs.piccolo.PRoot;

/**
 * <b>PSWTRoot</b> is a subclass of PRoot that is designed to work in the SWT
 * environment. In particular it uses SWTTimers and the SWT event dispatch
 * thread. With the current setup only a single PSWTCanvas is expected to be
 * connected to a root.
 * <P>
 * 
 * @version 1.1
 * @author Jesse Grosjean
 */
public class PSWTRoot extends PRoot {

    private Composite composite;

    public PSWTRoot(Composite composite) {
        this.composite = composite;
    }

    public Timer createTimer(int delay, ActionListener listener) {
        return new SWTTimer(composite.getDisplay(), delay, listener);
    }

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
