package org.piccolo2d.svg.cssmini;

import java.awt.Color;

class CSSValue implements CharSequence {

    public static CSSValue valueOf(final CharSequence value) {
        return new CSSValue(value);
    }

    private Color color = null;
    private final String data;

    private CSSValue(final CharSequence data) {
        this.data = data.toString();
    }

    public char charAt(final int index) {
        return data.charAt(index);
    }

    public Color getColor() {
        if (color == null) {
            try {
                color = SvgColor.compute(data);
            }
            catch (final IllegalArgumentException e) {
                color = null;
            }
        }
        return color;
    }

    public double getDouble() {
        return Double.parseDouble(data);
    }

    public float getFloat() {
        return Float.parseFloat(data);
    }

    public int length() {
        return data.length();
    }

    public CharSequence subSequence(final int start, final int end) {
        return data.subSequence(start, end);
    }

    public String toString() {
        return data.toString();
    }
}
