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
package org.piccolo2d.svg.css;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Callbacks for {@link CssParser}.
 * 
 * @author mr0738@mro.name
 */
abstract class CssParserBase {

    /** class Attribute start */
    static final String A0 = "\\[@class='(?:[^']+\\s)?\\s*# <= class Attribute Start \n";
    /** Attribute end */
    static final String AE = "(?:\\s[^']+)?'\\]# <= Attribute end\n";
    protected static final String ANY_ELEMENT = "*";
    /** Inter-Attribute separator */
    static final String AS = "(?:\\s[^']+)?\\s*# <= inter-Attribute separator\n";
    /** Element start */
    static final String E0 = "/# <= Element Start\n";
    /** Element climber */
    static final String EC = "\n.*# <= Element Climber\n";

    /** Element end (separator) */
    static final String EE = "# Element End\n";
    // <String>
    private final SortedSet classes = new TreeSet();
    private String currentElem = null;
    private final StringBuilder pat = new StringBuilder("^");
    // <Pattern>
    private final List pats = new ArrayList();
    private String prop = null;
    // <String, String>
    private final Map prop_expr = new HashMap();

    protected void finishElement() {
        if (null == currentElem) {
            return;
        }
        pat.append(EC);
        pat.append(E0);
        if (ANY_ELEMENT.equals(currentElem)) {
            // match any element
            pat.append("[^/\\[]+# <= match any element\n");
        }
        else {
            pat.append(currentElem);
        }
        if (classes.size() > 0) {
            pat.append(A0);
            final Iterator it = classes.iterator();
            pat.append(it.next());
            while (it.hasNext()) {
                pat.append(AS).append(it.next());
            }
            pat.append(AE);
        }
        else {
            pat.append("(?:\\[[^\\]]*\\])?# <= match attribute block\n");
        }
        pat.append(EE);
        classes.clear();
        currentElem = null;
    }

    protected void finishRuleSet(final LinkedHashMap css) {
        for (final Iterator it = pats.iterator(); it.hasNext();) {
            css.put(it.next(), new HashMap(prop_expr));
        }
        prop_expr.clear();
        pats.clear();
    }

    protected Pattern finishSelector() {
        try {
            pat.append("$");
            // System.out.println(pat);
            final Pattern p = Pattern.compile(pat.toString(), Pattern.COMMENTS);
            pats.add(p);
            return p;
        }
        finally {
            pat.setLength(0);
            pat.append("^");
        }
    }

    protected void pushClass(final CharSequence s) {
        classes.add(s.toString());
    }

    protected void pushElement(final CharSequence s) {
        // pat.append("//" + s);
        // assert currentElem == null;
        // assert classes.size() == 0;
        currentElem = s.toString();
    }

    protected void pushExpr(final CharSequence s) {
        if (prop == null) {
            return;
        }
        prop_expr.put(prop, s.toString());
        prop = null;
    }

    protected void pushProperty(final CharSequence s) {
        prop = s.toString();
    }
}
