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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.piccolo2d.svg.css.CssManager;
import org.piccolo2d.svg.css.CssManager.Style;
import org.piccolo2d.svg.util.FontUtil;

/**
 * @see org.w3c.dom.css.CSSStyleDeclaration
 * @author mr0738@mro.name
 */
class CSSStyleDeclaration implements Map, Style {

    public static class Builder {
        private CSSStyleDeclaration d = null;
        private CharSequence key = null;

        public void addKey(final CharSequence key) {
            this.key = key;
        }

        public void addValue(final CharSequence value) {
            if (key == null) {
                throw new IllegalStateException();
            }
            if (d == null) {
                d = newInstance();
            }
            d.put(key, value);
            key = null;
        }

        public CSSStyleDeclaration finish() {
            if (d == null) {
                d = newInstance();
            }
            try {
                return d;
            }
            finally {
                d = null;
            }
        }

        protected CSSStyleDeclaration newInstance() {
            return new CSSStyleDeclaration();
        }
    }

    private final Map properties;

    public CSSStyleDeclaration() {
        this(new TreeMap());
    }

    CSSStyleDeclaration(final Map properties) {
        this.properties = properties;
    }

    public void clear() {
        properties.clear();
    }

    public boolean containsKey(final Object key) {
        return properties.containsKey(key);
    }

    public boolean containsValue(final Object value) {
        return properties.containsValue(value);
    }

    public Set entrySet() {
        return properties.entrySet();
    }

    public boolean equals(final Object o) {
        return properties.equals(o);
    }

    public Object get(final Object key) {
        return properties.get(key);
    }

    public Color getColor(final String key) {
        return SvgColor.valueOf(getString(key));
    }

    public CharSequence getCssText() {
        final StringBuffer s = new StringBuffer();
        s.append("{");
        final Iterator it = properties.entrySet().iterator();
        if (it.hasNext()) {
            Entry elem = (Entry) it.next();
            CharSequence k = (CharSequence) elem.getKey();
            CharSequence v = (CharSequence) elem.getValue();
            s.append("\n\t").append(k).append(": ").append(v);
            while (it.hasNext()) {
                elem = (Entry) it.next();
                k = (CharSequence) elem.getKey();
                v = (CharSequence) elem.getValue();
                s.append(";\n\t").append(k).append(": ").append(v);
            }
        }
        s.append("\n}");
        return s.toString();
    }

    /**
     * @see FontUtil#findFont(CharSequence, String, CharSequence)
     */
    public Font getFont() {
        return FontUtil.findFont(getString("font-family"), getString("font-style"), getString("font-size"));
    }

    public Number getNumber(final String key) {
        final CharSequence s = getString(key);
        return s == null ? null : new Double(s.toString());
    }

    public double getNumber(final String key, final double def) {
        final CharSequence s = getString(key);
        return s == null ? def : Double.parseDouble(s.toString());
    }

    public float getNumber(final String key, final float def) {
        final CharSequence s = getString(key);
        return s == null ? def : Float.parseFloat(s.toString());
    }

    public CharSequence getString(final String key) {
        return (CharSequence) get(key);
    }

    public int hashCode() {
        return properties.hashCode();
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    public Set keySet() {
        return properties.keySet();
    }

    public Iterator propertyKeys() {
        return keySet().iterator();
    }

    public Object put(final Object arg0, final Object arg1) {
        // Check syntactic validity
        return properties.put(arg0, arg1);
    }

    public void putAll(final Map arg0) {
        // TODO Check syntactic validity
        // throw new UnsupportedOperationException("Not implemented yet.");
        properties.putAll(arg0);
    }

    public Object remove(final Object key) {
        return properties.remove(key);
    }

    /**
     * @see CssManager#parseStyleAttribute(CharSequence)
     */
    void setCssText(final String cssText) throws ParseException {
        // TODO parse
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public CharSequence setProperty(final String key, final CharSequence value) throws ParseException {
        return (String) put(key, value);
    }

    public int size() {
        return properties.size();
    }

    public String toString() {
        return getCssText().toString();
    }

    public Collection values() {
        return properties.values();
    }
}
