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

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;


/**
 * Very simple, non-DOM based css manager.
 * <p>
 * The {@link CssParser} creates {@link Pattern}s to match document xpaths from
 * the CSS selectors and {@link Map}s from the CSS attributes.
 * <p>
 * The single rules are kept in a {@link LinkedHashMap} to keep the order.
 * 
 * @see CssParser
 */
public class CssManagerImpl implements CssManager {

    // LinkedHashMap<Pattern, Map<CharSequence, CharSequence>>
    private final LinkedHashMap css = new LinkedHashMap();

    // <CharSequence, Map<CharSequence, CharSequence>>
    private final Map styleCache = new WeakHashMap();

    public void clearCache() {
        styleCache.clear();
    }

    Map computeStyle(final CharSequence xpath) {
        final Map style = new HashMap();
        for (final Iterator it = css.entrySet().iterator(); it.hasNext();) {
            final Entry elm = (Entry) it.next();
            final Pattern p = (Pattern) elm.getKey();
            if (p.matcher(xpath).matches()) {
                style.putAll((Map) elm.getValue());
            }
        }
        return style;
    }

    public Map findStyle(CharSequence xpath, final CharSequence styleAttributeValue) throws ParseException {
        xpath = xpath.toString();
        Map style = (Map) styleCache.get(xpath);
        if (style == null) {
            styleCache.put(xpath, style = computeStyle(xpath));
        }
        if (styleAttributeValue != null && styleAttributeValue.length() > 0) {
            // that's really brute force, but simple:
            style = new HashMap(style);
            final Map tmp = new CssParser().parse("dummyelem{" + styleAttributeValue + "}", null);
            final Entry en = (Entry) tmp.entrySet().iterator().next();
            style.putAll((Map) en.getValue());
        }
        return style;
    }

    public void loadStyleSheet(final CharSequence styledata) throws ParseException {
        new CssParser().parse(styledata, css);
        clearCache();
    }

    public void loadStyleSheet(final URL path) throws IOException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
