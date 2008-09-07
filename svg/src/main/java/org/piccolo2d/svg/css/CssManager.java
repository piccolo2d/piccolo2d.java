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

import java.awt.Color;
import java.awt.Font;
import java.text.ParseException;
import java.util.Iterator;

import javax.swing.text.html.StyleSheet;

import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

/**
 * Very simple, non-DOM based css manager facade.
 * <p>
 * http://www.w3.org/Style/CSS/SAC/ http://cssparser.sourceforge.net/
 * </p>
 * <p>
 * TODO: examine {@link CSSStyleSheet}, {@link CSSStyleRule} and
 * {@link StyleSheet}.
 * </p>
 */
public interface CssManager {

    /** Simple interface for a set of css properties. */
    public interface Style {

        /** Get a property value converted into a {@link Color} */
        Color getColor(String key);

        CharSequence getCssText();

        Font getFont();

        /**
         * Get a property value converted into a {@link Number}
         * 
         * @return <code>null</code> if not present.
         */
        Number getNumber(String key);

        /**
         * Get a property value converted into a {@link Double}
         * 
         * @param def default value if the property is not present.
         */
        double getNumber(String key, double def);

        /**
         * Get a property value converted into a {@link Float}
         * 
         * @param def default value if the property is not present.
         */
        float getNumber(String key, float def);

        /**
         * Get a property value as is.
         * 
         * @return <code>null</code> if not present.
         */
        CharSequence getString(String key);

        /**
         * List all property keys of a {@link CssManager.Style}.
         * 
         * @return Iterator of Strings.
         */
        Iterator propertyKeys();

        /**
         * Set a property value of a style.
         */
        CharSequence setProperty(String key, CharSequence value) throws ParseException;
    };

    void clearCache();

    /**
     * Get the plain style for a given element in a xml document including
     * classes.
     * <p>
     * Iterate over all css rules (-&gt; {@link #loadStyleSheet(CharSequence)})
     * and accumulate the matching styles' properties. Finally add the key-value
     * pairs from an optional style attribute value.
     * </p>
     * <p>
     * Does <b>NOT</b> deal with inherited properties. This must be done in the
     * {@link SvgSaxHandler} and cannot move here as the node ancestry in the
     * document might contain style attributes.
     * </p>
     * <p>
     * This method <b>must</b> be implemented while
     * {@link #findStyleByXPath(CharSequence)} is optional.
     * </p>
     * 
     * @param cssSelector the whole path, e.g.
     *            <code>html > body > p .myclass > em</code>
     * @return the resulting, complete style.
     * @throws ParseException the style attribute value cannot be parsed.
     */
    Style findStyleByCSSSelector(final CharSequence cssSelector) throws ParseException;

    /**
     * Cached, public access.
     * 
     * @see #findStyleByCSSSelector(CharSequence)
     */
    Style findStyleByXPath(CharSequence xpath) throws ParseException;

    /**
     * Create a new {@link Style} instance and fill it with the default style.
     * <p>
     * Document parsers should start with this style.
     * </p>
     */
    Style getDefaultStyle();

    /**
     * Should properties with this name be passed on downward in the hierarchy?
     */
    boolean inheritProperty(String key);

    /**
     * Append css declarations to the current stylesheet.
     */
    void loadStyleSheet(CharSequence styledata) throws ParseException;

    /**
     * Create an empty {@link Style} instance, add all properties from parent
     * and add all properties from child.
     * 
     * @return a new {@link Style} instance.
     * @see #inheritProperty(String)
     */
    Style merge(Style parent, Style child);

    Style parseStyleAttribute(CharSequence styleAttribute) throws ParseException;
}