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
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Turn svg color strings into {@link Color}s. Maybe better use a ragel
 * micro-parser.
 * 
 * See http://www.w3.org/TR/SVG11/types.html#BasicDataTypes
 */
class SvgColor {

    /**
     * See <code>&lt;color&gt;</code> at
     * http://www.w3.org/TR/SVG11/types.html#BasicDataTypes
     */
    static final Pattern colPat = Pattern.compile("(?:#(\\p{XDigit}{3}))" + "|(?:#(\\p{XDigit}{6}))"
            + "|(?:rgb\\(\\s*(\\d{1,3})\\s*,\\s*(\\d{1,3})\\s*,\\s*(\\d{1,3})\\s*\\))"
            + "|(?:rgb\\(\\s*(\\d{1,3})\\s*%\\s*,\\s*(\\d{1,3})\\s*%\\s*,\\s*(\\d{1,3})\\s*%\\s*\\))");
    /**
     * Color names according to
     * http://www.w3.org/TR/SVG11/types.html#ColorKeywords
     */
    static final Map name2col;

    static {
        final TreeMap c = new TreeMap();
        c.put("aliceblue", rgb(240, 248, 255));
        c.put("antiquewhite", rgb(250, 235, 215));
        c.put("aqua", rgb(0, 255, 255));
        c.put("aquamarine", rgb(127, 255, 212));
        c.put("azure", rgb(240, 255, 255));
        c.put("beige", rgb(245, 245, 220));
        c.put("bisque", rgb(255, 228, 196));
        c.put("black", rgb(0, 0, 0));
        c.put("blanchedalmond", rgb(255, 235, 205));
        c.put("blue", rgb(0, 0, 255));
        c.put("blueviolet", rgb(138, 43, 226));
        c.put("brown", rgb(165, 42, 42));
        c.put("burlywood", rgb(222, 184, 135));
        c.put("cadetblue", rgb(95, 158, 160));
        c.put("chartreuse", rgb(127, 255, 0));
        c.put("chocolate", rgb(210, 105, 30));
        c.put("coral", rgb(255, 127, 80));
        c.put("cornflowerblue", rgb(100, 149, 237));
        c.put("cornsilk", rgb(255, 248, 220));
        c.put("crimson", rgb(220, 20, 60));
        c.put("cyan", rgb(0, 255, 255));
        c.put("darkblue", rgb(0, 0, 139));
        c.put("darkcyan", rgb(0, 139, 139));
        c.put("darkgoldenrod", rgb(184, 134, 11));
        c.put("darkgray", rgb(169, 169, 169));
        c.put("darkgreen", rgb(0, 100, 0));
        c.put("darkgrey", rgb(169, 169, 169));
        c.put("darkkhaki", rgb(189, 183, 107));
        c.put("darkmagenta", rgb(139, 0, 139));
        c.put("darkolivegreen", rgb(85, 107, 47));
        c.put("darkorange", rgb(255, 140, 0));
        c.put("darkorchid", rgb(153, 50, 204));
        c.put("darkred", rgb(139, 0, 0));
        c.put("darksalmon", rgb(233, 150, 122));
        c.put("darkseagreen", rgb(143, 188, 143));
        c.put("darkslateblue", rgb(72, 61, 139));
        c.put("darkslategray", rgb(47, 79, 79));
        c.put("darkslategrey", rgb(47, 79, 79));
        c.put("darkturquoise", rgb(0, 206, 209));
        c.put("darkviolet", rgb(148, 0, 211));
        c.put("deeppink", rgb(255, 20, 147));
        c.put("deepskyblue", rgb(0, 191, 255));
        c.put("dimgray", rgb(105, 105, 105));
        c.put("dimgrey", rgb(105, 105, 105));
        c.put("dodgerblue", rgb(30, 144, 255));
        c.put("firebrick", rgb(178, 34, 34));
        c.put("floralwhite", rgb(255, 250, 240));
        c.put("forestgreen", rgb(34, 139, 34));
        c.put("fuchsia", rgb(255, 0, 255));
        c.put("gainsboro", rgb(220, 220, 220));
        c.put("ghostwhite", rgb(248, 248, 255));
        c.put("gold", rgb(255, 215, 0));
        c.put("goldenrod", rgb(218, 165, 32));
        c.put("gray", rgb(128, 128, 128));
        c.put("grey", rgb(128, 128, 128));
        c.put("green", rgb(0, 128, 0));
        c.put("greenyellow", rgb(173, 255, 47));
        c.put("honeydew", rgb(240, 255, 240));
        c.put("hotpink", rgb(255, 105, 180));
        c.put("indianred", rgb(205, 92, 92));
        c.put("indigo", rgb(75, 0, 130));
        c.put("ivory", rgb(255, 255, 240));
        c.put("khaki", rgb(240, 230, 140));
        c.put("lavender", rgb(230, 230, 250));
        c.put("lavenderblush", rgb(255, 240, 245));
        c.put("lawngreen", rgb(124, 252, 0));
        c.put("lemonchiffon", rgb(255, 250, 205));
        c.put("lightblue", rgb(173, 216, 230));
        c.put("lightcoral", rgb(240, 128, 128));
        c.put("lightcyan", rgb(224, 255, 255));
        c.put("lightgoldenrodyellow", rgb(250, 250, 210));
        c.put("lightgray", rgb(211, 211, 211));
        c.put("lightgreen", rgb(144, 238, 144));
        c.put("lightgrey", rgb(211, 211, 211));
        c.put("lightpink", rgb(255, 182, 193));
        c.put("lightsalmon", rgb(255, 160, 122));
        c.put("lightseagreen", rgb(32, 178, 170));
        c.put("lightskyblue", rgb(135, 206, 250));
        c.put("lightslategray", rgb(119, 136, 153));
        c.put("lightslategrey", rgb(119, 136, 153));
        c.put("lightsteelblue", rgb(176, 196, 222));
        c.put("lightyellow", rgb(255, 255, 224));
        c.put("lime", rgb(0, 255, 0));
        c.put("limegreen", rgb(50, 205, 50));
        c.put("linen", rgb(250, 240, 230));
        c.put("magenta", rgb(255, 0, 255));
        c.put("maroon", rgb(128, 0, 0));
        c.put("mediumaquamarine", rgb(102, 205, 170));
        c.put("mediumblue", rgb(0, 0, 205));
        c.put("mediumorchid", rgb(186, 85, 211));
        c.put("mediumpurple", rgb(147, 112, 219));
        c.put("mediumseagreen", rgb(60, 179, 113));
        c.put("mediumslateblue", rgb(123, 104, 238));
        c.put("mediumspringgreen", rgb(0, 250, 154));
        c.put("mediumturquoise", rgb(72, 209, 204));
        c.put("mediumvioletred", rgb(199, 21, 133));
        c.put("midnightblue", rgb(25, 25, 112));
        c.put("mintcream", rgb(245, 255, 250));
        c.put("mistyrose", rgb(255, 228, 225));
        c.put("moccasin", rgb(255, 228, 181));
        c.put("navajowhite", rgb(255, 222, 173));
        c.put("navy", rgb(0, 0, 128));
        c.put("oldlace", rgb(253, 245, 230));
        c.put("olive", rgb(128, 128, 0));
        c.put("olivedrab", rgb(107, 142, 35));
        c.put("orange", rgb(255, 165, 0));
        c.put("orangered", rgb(255, 69, 0));
        c.put("orchid", rgb(218, 112, 214));
        c.put("palegoldenrod", rgb(238, 232, 170));
        c.put("palegreen", rgb(152, 251, 152));
        c.put("paleturquoise", rgb(175, 238, 238));
        c.put("palevioletred", rgb(219, 112, 147));
        c.put("papayawhip", rgb(255, 239, 213));
        c.put("peachpuff", rgb(255, 218, 185));
        c.put("peru", rgb(205, 133, 63));
        c.put("pink", rgb(255, 192, 203));
        c.put("plum", rgb(221, 160, 221));
        c.put("powderblue", rgb(176, 224, 230));
        c.put("purple", rgb(128, 0, 128));
        c.put("red", rgb(255, 0, 0));
        c.put("rosybrown", rgb(188, 143, 143));
        c.put("royalblue", rgb(65, 105, 225));
        c.put("saddlebrown", rgb(139, 69, 19));
        c.put("salmon", rgb(250, 128, 114));
        c.put("sandybrown", rgb(244, 164, 96));
        c.put("seagreen", rgb(46, 139, 87));
        c.put("seashell", rgb(255, 245, 238));
        c.put("sienna", rgb(160, 82, 45));
        c.put("silver", rgb(192, 192, 192));
        c.put("skyblue", rgb(135, 206, 235));
        c.put("slateblue", rgb(106, 90, 205));
        c.put("slategray", rgb(112, 128, 144));
        c.put("slategrey", rgb(112, 128, 144));
        c.put("snow", rgb(255, 250, 250));
        c.put("springgreen", rgb(0, 255, 127));
        c.put("steelblue", rgb(70, 130, 180));
        c.put("tan", rgb(210, 180, 140));
        c.put("teal", rgb(0, 128, 128));
        c.put("thistle", rgb(216, 191, 216));
        c.put("tomato", rgb(255, 99, 71));
        c.put("turquoise", rgb(64, 224, 208));
        c.put("violet", rgb(238, 130, 238));
        c.put("wheat", rgb(245, 222, 179));
        c.put("white", rgb(255, 255, 255));
        c.put("whitesmoke", rgb(245, 245, 245));
        c.put("yellow", rgb(255, 255, 0));
        c.put("yellowgreen", rgb(154, 205, 50));
        name2col = Collections.unmodifiableSortedMap(c);
    }

    static Color compute(final String txt) {
        if (txt == null || "none".equals(txt)) {
            return null;
        }
        final Color c = (Color) name2col.get(txt);
        if (c != null) {
            return c;
        }
        final Matcher m = colPat.matcher(txt);
        if (m.matches()) {
            if (m.group(1) != null) {
                return new Color(duplicate(Integer.parseInt(m.group(1), 16)));
            }
            if (m.group(2) != null) {
                return new Color(Integer.parseInt(m.group(2), 16));
            }
            if (m.group(3) != null) {
                return new Color(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)), Integer.parseInt(m
                        .group(5)));
            }
            if (m.group(7) != null) {
                final float f = 1e-2F;
                return new Color(f * Integer.parseInt(m.group(6)), f * Integer.parseInt(m.group(7)), f
                        * Integer.parseInt(m.group(8)));
            }
        }
        throw new IllegalArgumentException("Couldn't parse color [" + txt + "]");
    }

    /**
     * See <code>&lt;color&gt;</code> at
     * http://www.w3.org/TR/SVG11/types.html#BasicDataTypes
     */
    static final int duplicate(final int hex) {
        return 0x1100 * (hex & 0xF00) + 0x00110 * (hex & 0xF0) + 0x000011 * (hex & 0xF);
    }

    private static final Color rgb(final int r, final int g, final int b) {
        return new Color(r, g, b);
    }

    public static Color valueOf(final CharSequence txt) {
        return compute(txt == null ? null : txt.toString());
    }
}
