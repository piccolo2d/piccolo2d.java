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

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author mr0738@mro.name
 */
public class CssManagerImpl implements CssManager {
    private final Collection css = new LinkedList();

    private final Map styleCacheCSSSel = new WeakHashMap();
    private final Map styleCacheXPath = new WeakHashMap();

    private Map addStyle(final CharSequence styleAttributeValue, Map style) throws ParseException {
        if (styleAttributeValue != null && styleAttributeValue.length() > 0) {
            // that's really brute force, but simple:
            style = new HashMap(style);
            final CSSStyleRule r = (CSSStyleRule) new CssParser().parse("dummyelem{" + styleAttributeValue + "}")
                    .iterator().next();
            style.putAll(r.getStyle());
        }
        return style;
    }

    public void clearCache() {
        styleCacheXPath.clear();
        styleCacheCSSSel.clear();
    }

    Map computeStyleByCSSSelector(final CharSequence cssSel) {
        final Map style = new HashMap();
        for (final Iterator it = css.iterator(); it.hasNext();) {
            final CSSStyleRule rule = (CSSStyleRule) it.next();
            if (rule.getSelector().getSelectorpPattern().matcher(cssSel).matches()) {
                style.putAll(rule.getStyle());
            }
        }
        return style;
    }

    Map computeStyleByXPath(final CharSequence xpath) {
        final Map style = new HashMap();
        for (final Iterator it = css.iterator(); it.hasNext();) {
            final CSSStyleRule rule = (CSSStyleRule) it.next();
            if (rule.getSelector().getXPathPattern().matcher(xpath).matches()) {
                style.putAll(rule.getStyle());
            }
        }
        return style;
    }

    public Map findStyleByCSSSelector(CharSequence cssSelector, final CharSequence styleAttributeValue)
            throws ParseException {
        cssSelector = cssSelector.toString();
        Map style = (Map) styleCacheCSSSel.get(cssSelector);
        if (style == null) {
            styleCacheCSSSel.put(cssSelector, style = computeStyleByCSSSelector(cssSelector));
        }
        return addStyle(styleAttributeValue, style);
    }

    public Map findStyleByXPath(CharSequence xpath, final CharSequence styleAttributeValue) throws ParseException {
        xpath = xpath.toString();
        Map style = (Map) styleCacheXPath.get(xpath);
        if (style == null) {
            styleCacheXPath.put(xpath, style = computeStyleByXPath(xpath));
        }
        return addStyle(styleAttributeValue, style);
    }

    public void loadStyleSheet(final CharSequence styledata) throws ParseException {
        css.addAll(new CssParser().parse(styledata));
        clearCache();
    }

    int ruleCount() {
        return css.size();
    }
}
