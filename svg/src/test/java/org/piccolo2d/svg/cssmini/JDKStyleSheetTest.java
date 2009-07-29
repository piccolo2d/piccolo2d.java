package org.piccolo2d.svg.cssmini;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.StyleSheet;

import junit.framework.TestCase;

import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

/**
 * Examine {@link CSSStyleSheet}, {@link CSSStyleRule} and {@link StyleSheet}.
 * 
 * @author mr0738@mro.name
 */
public class JDKStyleSheetTest extends TestCase {

    public void testSimple() throws IOException {
        final StyleSheet css = new StyleSheet();
        final Reader src = new StringReader(
                "\n"
                        + "      .rock-dark { fill: #e01010; stroke: none }\n"
                        + "      .rock-light { fill: #e0e010; stroke: none }\n"
                        + "      .rock-granite { fill: #565755; stroke: none }\n"
                        + "      .rock-number { fill: black; font-size: 0.65; alignment-baseline: central; text-anchor: middle }\n"
                        + "      .circle-12 { fill: #ff3131; stroke: none }\n"
                        + "      .circle-8 { fill: #ffffff; stroke: none }\n"
                        + "      .circle-4 { fill: #3131ff; stroke: none }\n"
                        + "      .circle-1 { fill: #ffffff; stroke: none }\n"
                        + "      .line { stroke: black; stroke-width: 0.025 /* feet, world-coordinates */ }\n"
                        + "      .ice-house { fill: #e8e8ff; stroke: none; }\n"
                        + "      .watermark { fill: black; opacity: 0.75; font-size: 0.4; }\n"
                        + "      .ghost { opacity: 0.5; }\n" + "    ");
        css.loadRules(src, null);
        final Style s = css.getRule("svg > defs > g > rect .ice-house");
        assertEquals(1, s.getAttributeCount());
        assertEquals(".ice-house", s.getAttribute(StyleConstants.NameAttribute));
    }
}
