/*
 * Copyright (c) 2008, Piccolo2D project, http://piccolo2d.org
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
 * None of the name of the Piccolo2D project, the University of Maryland, or the names of its contributors
 * may be used to endorse or promote products derived from this software without specific prior written
 * permission.
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

package org.piccolo2d.svg.cssmini;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * A Css selector that can easily be matched with a via {@link Pattern}s.
 * 
 * @see CSSSelectorBuilderXPath
 * @see org.w3c.dom.css.CSSStyleRule#setSelectorText(String)
 * @author mr0738@mro.name
 */
interface CSSSelector extends CharSequence, Serializable {

    public interface Builder {

        void addClass(CharSequence clazz);

        void combinator(char combinator);

        void endElement();

        CSSSelector finish();

        void reset();

        void startElement(CharSequence element);
    }

    String getSelectorText();

    String getXPathText();

    boolean matchesSelector(CharSequence documentSelector);

    boolean matchesXPath(CharSequence documentXPath);
}
