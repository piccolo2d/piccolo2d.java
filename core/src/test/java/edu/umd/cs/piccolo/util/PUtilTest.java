/*
 * Copyright (c) 2008-2010, Piccolo2D project, http://piccolo2d.org
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
package edu.umd.cs.piccolo.util;

import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;

import junit.framework.TestCase;

/**
 * Unit test for PUtil.
 */
public class PUtilTest extends TestCase {

    // see http://code.google.com/p/piccolo2d/issues/detail?id=116
    public void testPreventCodeCleanFinal() {
        final Enumeration ne = PUtil.NULL_ENUMERATION;
        try {
            PUtil.NULL_ENUMERATION = null;
        }
        finally {
            PUtil.NULL_ENUMERATION = ne;
        }

        final Iterator ni = PUtil.NULL_ITERATOR;
        try {
            PUtil.NULL_ITERATOR = null;
        }
        finally {
            PUtil.NULL_ITERATOR = ni;
        }

        final OutputStream no = PUtil.NULL_OUTPUT_STREAM;
        try {
            PUtil.NULL_OUTPUT_STREAM = null;
        }
        finally {
            PUtil.NULL_OUTPUT_STREAM = no;
        }
    }

}
