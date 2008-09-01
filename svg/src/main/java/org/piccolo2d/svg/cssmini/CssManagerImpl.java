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

import java.awt.Color;
import java.awt.Font;
import java.text.ParseException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.Map.Entry;

import org.piccolo2d.svg.css.CssManager;
import org.piccolo2d.svg.util.FontUtil;

/**
 * @see CSSSelector
 * @author mr0738@mro.name
 */
abstract class CssManagerImpl implements CssManager {
    private static class SD extends TreeMap implements Style {

        private static final long serialVersionUID = 5567683947559207117L;

        public SD() {
            super();
        }

        public SD(final Map m) {
            super(m);
        }
    }

    private final Collection css = new LinkedList();
    private final CssParser parser = new CssParser();
    private final CssSelectorToXPath selparser = new CssSelectorToXPath();
    private final Map styleCacheXPath = new WeakHashMap();

    private SD addStyle(final CharSequence styleAttributeValue, SD style) throws ParseException {
        if (styleAttributeValue != null && styleAttributeValue.length() > 0) {
            // that's really brute force, but simple:
            style = new SD(style);
            final CSSStyleRule r = (CSSStyleRule) parser.parse("dummyelem{" + styleAttributeValue + "}").iterator()
                    .next();
            style.putAll(r.getStyle());
        }
        return style;
    }

    public void clearCache() {
        styleCacheXPath.clear();
    }

    SD computeStyleByXPath(final CharSequence xpath) {
        final SD style = new SD();
        for (final Iterator it = css.iterator(); it.hasNext();) {
            final CSSStyleRule rule = (CSSStyleRule) it.next();
            if (rule.getSelector().matchesXPath(xpath)) {
                style.putAll(rule.getStyle());
            }
        }
        return style;
    }

    public Style findStyleByCSSSelector(final CharSequence cssSelector, final CharSequence styleAttributeValue)
            throws ParseException {
        return findStyleByXPath(selparser.parse(cssSelector), styleAttributeValue);
    }

    public Style findStyleByXPath(CharSequence xpath, final CharSequence styleAttributeValue) throws ParseException {
        xpath = xpath.toString();
        SD style = (SD) styleCacheXPath.get(xpath);
        if (style == null) {
            styleCacheXPath.put(xpath, style = computeStyleByXPath(xpath));
        }
        return addStyle(styleAttributeValue, style);
    }

    public Color getColor(final Style style, final String key) {
        return SvgColor.valueOf(getString(style, key));
    }

    /**
     * TODO make this a duty of {@link SvgCssManager}.
     */
    public Style getDefaultStyle() {
        return initDefaults(new SD());
    }

    public Font getFont(final Style style) {
        return FontUtil.findFont(getString(style, "font-family"), getString(style, "font-style"), getString(style,
                "font-size"));
    }

    public Number getNumber(final Style style, final String key) {
        final String s = getString(style, key);
        if (s == null) {
            return null;
        }
        return new Double(s);
    }

    public double getNumber(final Style style, final String key, final double def) {
        final String s = getString(style, key);
        if (s == null) {
            return def;
        }
        return Double.parseDouble(s);
    }

    public float getNumber(final Style style, final String key, final float def) {
        final String s = getString(style, key);
        if (s == null) {
            return def;
        }
        return Float.parseFloat(s);
    }

    public String getString(final Style style, final String key) {
        final SD sty = (SD) style;
        final CharSequence ret = (CharSequence) sty.get(key);
        if (ret == null) {
            return null;
        }
        return ret.toString();
    }

    protected abstract Style initDefaults(Style style);

    public void loadStyleSheet(final CharSequence styledata) throws ParseException {
        css.addAll(parser.parse(styledata));
        clearCache();
    }

    public Style merge(final Style parent, final CharSequence child) throws ParseException {
        final SD r = new SD((SD) parent);
        r.putAll((SD) child);
        return r;
    }

    public Style merge(final Style parent, final Style child) {
        final SD r = new SD();
        for (final Iterator it = ((SD) parent).entrySet().iterator(); it.hasNext();) {
            final Entry elem = (Entry) it.next();
            final String key = (String) elem.getKey();
            if (isInherited(key)) {
                r.put(key, elem.getValue());
            }
        }
        r.putAll((SD) child);
        return r;
    }

    public Iterator properties(final Style style) {
        final SD s = (SD) style;
        return s.keySet().iterator();
    }

    /** For testing purposes only. */
    int ruleCount() {
        return css.size();
    }

    public String setProperty(final Style style, final String key, final String value) throws ParseException {
        final SD s = (SD) style;
        return (String) s.put(key, value);
    }
}
