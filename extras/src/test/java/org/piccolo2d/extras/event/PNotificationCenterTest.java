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

import org.piccolo2d.extras.event.PNotification;
import org.piccolo2d.extras.event.PNotificationCenter;

import junit.framework.TestCase;

public class PNotificationCenterTest extends TestCase {

    boolean changed1;
    boolean changed2;
    boolean changed3;
    boolean changed4;

    public PNotificationCenterTest(final String name) {
        super(name);
    }

    public void testToString() throws SecurityException, NoSuchMethodException {
        final PNotificationCenter center = PNotificationCenter.defaultCenter();

        center.addListener(this, "changed1", "propertyChanged", this);
        center.addListener(this, "changed2", null, this);
        center.addListener(this, "changed3", "propertyChanged", null);
        center.addListener(this, "changed4", null, null);

        center.postNotification("propertyChanged", this);
        assertTrue(changed1 && changed2 && changed3 && changed4);
        changed1 = changed2 = changed3 = changed4 = false;

        center.postNotification("propertyChanged", new Object());
        assertTrue(!changed1 && !changed2 && changed3 && changed4);
        changed1 = changed2 = changed3 = changed4 = false;

        center.postNotification("otherPropertyChanged", this);
        assertTrue(!changed1 && changed2 && !changed3 && changed4);
        changed1 = changed2 = changed3 = changed4 = false;

        center.postNotification("otherPropertyChanged", new Object());
        assertTrue(!changed1 && !changed2 && !changed3 && changed4);
        changed1 = changed2 = changed3 = changed4 = false;
    }

    public void changed1(final PNotification notification) {
        changed1 = true;
    }

    public void changed2(final PNotification notification) {
        changed2 = true;
    }

    public void changed3(final PNotification notification) {
        changed3 = true;
    }

    public void changed4(final PNotification notification) {
        changed4 = true;
    }
}
