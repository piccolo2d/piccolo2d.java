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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Map.Entry;

import org.piccolo2d.svg.css.CssManager;

/**
 * Very simple css implementation based on {@link CssParser} with full element
 * nesting and class support.
 * 
 * @author mr0738@mro.name
 */
abstract class CssManagerImpl implements CssManager {
    private static class StyImp extends CSSStyleDeclaration {

        public StyImp() {
            super();
        }

        public StyImp(final CSSStyleDeclaration properties) {
            super(properties);
        }
    }

    private final Collection css = new LinkedList();
    private final CssParser parser = new CssParser();
    private final CssSelectorToXPath selector2xpath = new CssSelectorToXPath();
    private final Map styleCacheXPath = new WeakHashMap();

    public void clearCache() {
        styleCacheXPath.clear();
    }

    StyImp computeStyleByXPath(final CharSequence xpath) {
        final StyImp style = new StyImp();
        for (final Iterator it = css.iterator(); it.hasNext();) {
            final CSSStyleRule rule = (CSSStyleRule) it.next();
            if (rule.getSelector().matchesXPath(xpath)) {
                style.putAll(rule.getStyle());
            }
        }
        return style;
    }

    public Style findStyleByCSSSelector(final CharSequence cssSelector) throws ParseException {
        return findStyleByXPath(selector2xpath.parse(cssSelector));
    }

    public Style findStyleByXPath(CharSequence xpath) throws ParseException {
        xpath = xpath.toString();
        StyImp style = (StyImp) styleCacheXPath.get(xpath);
        if (style == null) {
            styleCacheXPath.put(xpath, style = computeStyleByXPath(xpath));
        }
        return style;
    }

    /**
     * Create a new {@link CssManager.Style} instance and delegate to
     * {@link #initDefaults(org.piccolo2d.svg.css.CssManager.Style)}
     */
    public Style getDefaultStyle() {
        return initDefaults(new StyImp());
    }

    protected abstract Style initDefaults(Style style);

    public void loadStyleSheet(final CharSequence styledata) throws ParseException {
        css.addAll(parser.parse(styledata));
        clearCache();
    }

    public Style merge(final Style parent, final Style child) {
        if (parent == null) {
            return child;
        }
        if (child == null) {
            return parent;
        }
        final StyImp r = new StyImp();
        for (final Iterator it = ((StyImp) parent).entrySet().iterator(); it.hasNext();) {
            final Entry elem = (Entry) it.next();
            final String key = (String) elem.getKey();
            if (inheritProperty(key)) {
                r.put(key, elem.getValue());
            }
        }
        r.putAll((Map) child);
        return r;
    }

    public Style parseStyleAttribute(final CharSequence styleAttributeValue) throws ParseException {
        if (styleAttributeValue == null || styleAttributeValue.length() == 0) {
            return null;
        }
        // that's really brute force, but simple:
        final CSSStyleRule r = (CSSStyleRule) parser.parse("dummyelem{" + styleAttributeValue + "}").iterator().next();
        if (r == null) {
            return null;
        }
        return r.getStyle();
    }

    /** For testing purposes only. */
    int ruleCount() {
        return css.size();
    }
}
