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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.piccolo2d.svg.cssmini.CSSSelector.Builder;

class CSSSelectorBuilderXPath implements Builder {

    /** class Attribute start */
    private static final String A0 = "\\[@class='(?:[^']+\\s)?\\s*# <= class Attribute Start \n";
    /** Attribute end */
    private static final String AE = "(?:\\s[^']+)?'\\]# <= Attribute end\n";
    private static final String ANY_ELEMENT = "*";
    /** Inter-Attribute separator */
    private static final String AS = "(?:\\s[^']+)?\\s*# <= inter-Attribute separator\n";
    /** Element start */
    private static final String E0 = "/# <= Element Start\n";
    /** Element climber */
    private static final String EC = "\n.*# <= Element Climber\n";
    /** Element end (separator) */
    private static final String EE = "# Element End\n";

    private static final CssSelectorToXPath sel2xpath = new CssSelectorToXPath();
    private final List classes = new ArrayList();
    private final Collection selector = new ArrayList();
    private final StringBuilder selectorPattern = new StringBuilder();
    private final StringBuilder xpath = new StringBuilder();
    private final StringBuilder xpathPattern = new StringBuilder();

    CSSSelectorBuilderXPath() {
        reset();
    }

    public void addClass(final CharSequence clazz) {
        classes.add(clazz.toString());
    }

    public void combinator(final char combinator) {
        switch (combinator) {
            case ' ':
                selectorPattern.append(".*");
                xpathPattern.append("/");
                break;
            case '>':
                break;
            case '+':
                throw new UnsupportedOperationException("Combinator [" + combinator + "] not implemented yet.");
            default:
                throw new IllegalArgumentException("Combinator [" + combinator + "]");
        }
        selector.add(Character.toString(combinator));
    }

    public void endElement() {
        Collections.sort(classes);
        final String anyCssClass = "(?:\\s+\\.[^\\.\\s]+)*";
        final String cssClassPre = "\\s+\\.";
        final Iterator it = classes.iterator();
        if (it.hasNext()) {
            String clz = (String) it.next();

            selector.add("." + clz);
            selectorPattern.append(anyCssClass).append(cssClassPre).append(clz);

            xpath.append("[contains('").append(clz).append("', @class)");
            xpathPattern.append(A0).append(clz);
            while (it.hasNext()) {
                clz = (String) it.next();

                selector.add("." + clz);
                selectorPattern.append(anyCssClass).append(cssClassPre).append(clz);

                xpath.append(" and contains('").append(clz).append("', @class)");
                xpathPattern.append(AS).append(clz);
            }
            xpath.append(']');
            xpathPattern.append(AE);
        }
        else {
            // allow all attributes:
            xpathPattern.append("(?:\\[[^\\]]*\\])?");
        }
        selectorPattern.append(anyCssClass);
        xpathPattern.append(EE);

        classes.clear();
    }

    public CSSSelector finish() {
        try {
            final StringBuilder sB = new StringBuilder();
            {
                final Iterator it = selector.iterator();
                if (it.hasNext()) {
                    sB.append((String) it.next());
                    while (it.hasNext()) {
                        final String e = (String) it.next();
                        // ignore the " " (ancestry) combinator
                        if (" ".equals(e)) {
                            continue;
                        }
                        sB.append(' ').append(e);
                    }
                }
            }
            selectorPattern.append('$');
            xpathPattern.append('$');

            final String selectorStr = sB.toString();
            final Pattern selPat = Pattern.compile(selectorPattern.toString());
            final Pattern xpPat = Pattern.compile(xpathPattern.toString(), Pattern.COMMENTS);
            final String xp = xpath.toString();

            return new CSSSelector() {
                private static final long serialVersionUID = -3004114510267702903L;

                public char charAt(final int index) {
                    return selectorStr.charAt(index);
                }

                public boolean equals(final Object anObject) {
                    return selectorStr.equals(anObject);
                }

                public String getSelectorText() {
                    return selectorStr;
                }

                public String getXPathText() {
                    return xp;
                }

                public int hashCode() {
                    return selectorStr.hashCode();
                }

                public int length() {
                    return selectorStr.length();
                }

                public boolean matchesSelector(final CharSequence documentSelector) {
                    if (true) {
                        try {
                            return matchesXPath(sel2xpath.parse(documentSelector));
                        }
                        catch (final ParseException e) {
                            return false;
                        }
                    }
                    else {
                        return selPat.matcher(documentSelector).matches();
                    }
                }

                public boolean matchesXPath(final CharSequence documentXPath) {
                    return xpPat.matcher(documentXPath).matches();
                }

                public CharSequence subSequence(final int start, final int end) {
                    return selectorStr.subSequence(start, end);
                }

                public String toString() {
                    return getSelectorText();
                }
            };
        }
        finally {
            reset();
        }
    }

    public void reset() {
        classes.clear();
        selector.clear();
        selectorPattern.setLength(0);
        selectorPattern.append('^').append(".*");
        xpath.setLength(0);
        xpath.append('/');
        xpathPattern.setLength(0);
        xpathPattern.append('^').append(EC);
    }

    public void startElement(final CharSequence element) {
        selector.add(element.toString());
        selectorPattern.append("\\s+").append(ANY_ELEMENT.equals(element) ? ".*" : element);
        xpath.append('/').append(element);
        xpathPattern.append(E0).append(element);
    }
}