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

package org.piccolo2d;

import java.awt.geom.Dimension2D;

import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PDimension;

import junit.framework.Assert;

/**
 * This class provides helper methods to help with testing.
 * 
 * It's implemented this way, as opposed to as a subclass, because when we move
 * to JUnit4, inheritance is not the preferred way of importing asserts.
 */
public final class PiccoloAsserts {
    private PiccoloAsserts() {
        // Nothing to do
    }

    public static final void assertEquals(final PBounds expected, final PBounds actual, final double errorRate) {
        assertEquals("Expected " + expected + " but was " + actual, expected, actual, errorRate);
    }

    public static final void assertEquals(final String message, final PBounds expected, final PBounds actual,
            final double errorRate) {
        Assert.assertEquals(message, expected.getX(), actual.getX(), errorRate);
        Assert.assertEquals(message, expected.getY(), actual.getY(), errorRate);
        Assert.assertEquals(message, expected.getWidth(), actual.getWidth(), errorRate);
        Assert.assertEquals(message, expected.getHeight(), actual.getHeight(), errorRate);
    }

    public static void assertEquals(final PDimension expected, final Dimension2D actual, final double errorRate) {
        assertEquals("Expected " + expected + " but was " + actual, expected, actual, errorRate);
    }

    public static void assertEquals(final String message, final PDimension expected, final Dimension2D actual,
            final double errorRate) {
        Assert.assertEquals(message, expected.getWidth(), actual.getWidth(), errorRate);
        Assert.assertEquals(message, expected.getHeight(), actual.getHeight(), errorRate);
    }

    public static void assertEquals(final String[] expected, final String[] actual) {
        Assert.assertEquals("arrays are not same size", expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            Assert.assertEquals(expected[i], expected[i]);
        }
    }
}
