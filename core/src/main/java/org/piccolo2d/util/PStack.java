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
package org.piccolo2d.util;

import java.util.ArrayList;

/**
 * <b>PStack</b> this class should be removed when a non thread safe stack is
 * added to the java class libraries.
 * <p>
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PStack extends ArrayList {
    /**
     * Allows for future serialization code to understand versioned binary
     * formats.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an empty stack.
     */
    public PStack() {
    }

    /**
     * Pushes the provided object onto the top of the stack.
     * 
     * @param o object to add to the stack
     */
    public void push(final Object o) {
        add(o);
    }

    /**
     * Returns  topmost element on the stack, or null if stack is empty.
     * 
     * @return topmost element on the stack, or null if empty
     */
    public Object peek() {
        final int s = size();
        if (s == 0) {
            return null;
        }
        else {
            return get(s - 1);
        }
    }

    /**
     * Removes top element on the stack and returns it.
     * 
     * @return topmost element on stack.
     */
    public Object pop() {
        return remove(size() - 1);
    }
}
