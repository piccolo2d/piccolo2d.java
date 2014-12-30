/*
 * Copyright (c) 2008-2011, Piccolo2D project, http://piccolo2d.org
 * Copyright (c) 1998-2008, University of Maryland
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
 * None of the name of the University of Maryland, the name of the Piccolo2D project, or the names of its
 * contributors may be used to endorse or promote products derived from this software without specific
 * prior written permission.
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
package org.piccolo2d.extras.nodes;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.piccolo2d.PNode;
import org.piccolo2d.util.PPaintContext;


/**
 * @author Lance Good
 */
public class PStyledText extends PNode {

    private static final long serialVersionUID = 1L;

    /** Font rendering context used for all PStyledText instances. */
    protected static final FontRenderContext SWING_FRC = new FontRenderContext(null, true, false);

    /** Used while painting underlines. */
    protected static final Line2D paintLine = new Line2D.Double();

    /**
     * Underlying document used to handle the complexities involved with
     * arbitrary text and formatting.
     */
    protected Document document;

    /** String contents of the document. */
    protected transient ArrayList stringContents;

    /** Tracks the information about line metrics within the document. */
    protected transient LineInfo[] lines;

    /** Whether this node is currently being edited. */
    protected boolean editing;

    /** Insets represent how far away from the bounding box text will be drawn. */
    protected Insets insets = new Insets(0, 0, 0, 0);

    /** Whether width will be forced to match containing text's height. */
    protected boolean constrainHeightToTextHeight = true;

    /** Whether width will be forced to match containing text's width. */
    protected boolean constrainWidthToTextWidth = true;

    /**
     * Constructs an empty PStyledText element.
     */
    public PStyledText() {
    }

    /**
     * Controls whether this node changes its width to fit the width of its
     * text. If flag is true it does; if flag is false it doesn't
     * 
     * @param constrainWidthToTextWidth whether node's width should be
     *            constrained to the width of its text
     */
    public void setConstrainWidthToTextWidth(final boolean constrainWidthToTextWidth) {
        this.constrainWidthToTextWidth = constrainWidthToTextWidth;
        recomputeLayout();
    }

    /**
     * Controls whether this node changes its height to fit the height of its
     * text. If flag is true it does; if flag is false it doesn't
     * 
     * @param constrainHeightToTextHeight whether node's height should be
     *            constrained to the height of its text
     */
    public void setConstrainHeightToTextHeight(final boolean constrainHeightToTextHeight) {
        this.constrainHeightToTextHeight = constrainHeightToTextHeight;
        recomputeLayout();
    }

    /**
     * Controls whether this node changes its width to fit the width of its
     * text. If flag is true it does; if flag is false it doesn't
     * 
     * @return true if node is constrained to the width of its text
     */
    public boolean getConstrainWidthToTextWidth() {
        return constrainWidthToTextWidth;
    }

    /**
     * Controls whether this node changes its height to fit the height of its
     * text. If flag is true it does; if flag is false it doesn't
     * 
     * @return true if node is constrained to the height of its text
     */
    public boolean getConstrainHeightToTextHeight() {
        return constrainHeightToTextHeight;
    }

    /**
     * Get the document for this PStyledText. Document is used as the node's
     * model.
     * 
     * @return internal document used as a model of this PStyledText
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Set the document on this PStyledText. Document is used as the node's
     * model.
     * 
     * @param document to be used as the model for this PStyledText
     */
    public void setDocument(final Document document) {
        // Save the document
        this.document = document;

        syncWithDocument();
    }

    /**
     * Enforce that the current display matches the styling of the underlying
     * document as closely as possible.
     */
    public void syncWithDocument() {
        // First get the actual text and stick it in an Attributed String
        stringContents = new ArrayList();

        String documentString;
        try {
            documentString = document.getText(0, document.getLength());
        }
        catch (final BadLocationException e) {
            // Since this the location we're providing comes from directly
            // querying the document, this is impossible in a single threaded
            // model
            return;
        }

        // The paragraph start and end indices
        final ArrayList pEnds = extractParagraphRanges(documentString);

        // The default style context - which will be reused
        final StyleContext styleContext = StyleContext.getDefaultStyleContext();

        int pos;
        RunInfo paragraphRange = null;

        AttributedString attributedString;

        final Iterator contentIterator = stringContents.iterator();
        final Iterator paragraphIterator = pEnds.iterator();
        while (contentIterator.hasNext() && paragraphIterator.hasNext()) {
            paragraphRange = (RunInfo) paragraphIterator.next();
            attributedString = (AttributedString) contentIterator.next();
            pos = paragraphRange.startIndex;

            // The current element will be used as a temp variable while
            // searching for the leaf element at the current position
            Element curElement = null;

            // Small assumption here that there is one root element - can fix
            // for more general support later
            final Element rootElement = document.getDefaultRootElement();

            // If the string is length 0 then we just need to add the attributes
            // once
            if (paragraphRange.isEmpty()) {
                curElement = drillDownFromRoot(pos, rootElement);

                // These are the mandatory attributes
                final AttributeSet attributes = curElement.getAttributes();
                final Color foreground = styleContext.getForeground(attributes);

                attributedString.addAttribute(TextAttribute.FOREGROUND, foreground, Math.max(0, curElement
                        .getStartOffset()
                        - paragraphRange.startIndex), Math.min(paragraphRange.length(), curElement.getEndOffset()
                        - paragraphRange.startIndex));

                final Font font = extractFont(styleContext, pos, rootElement, attributes);
                applyFontAttribute(paragraphRange, attributedString, curElement, font);
                applyBackgroundAttribute(styleContext, paragraphRange, attributedString, curElement, attributes);
                applyUnderlineAttribute(paragraphRange, attributedString, curElement, attributes);
                applyStrikeThroughAttribute(paragraphRange, attributedString, curElement, attributes);
            }
            else {
                // OK, now we loop until we find all the leaf elements in the
                // range
                while (pos < paragraphRange.endIndex) {
                    curElement = drillDownFromRoot(pos, rootElement);

                    // These are the mandatory attributes
                    final AttributeSet attributes = curElement.getAttributes();
                    final Color foreground = styleContext.getForeground(attributes);

                    attributedString.addAttribute(TextAttribute.FOREGROUND, foreground, Math.max(0, curElement
                            .getStartOffset()
                            - paragraphRange.startIndex), Math.min(paragraphRange.length(), curElement.getEndOffset()
                            - paragraphRange.startIndex));

                    final Font font = extractFont(styleContext, pos, rootElement, attributes);
                    applyFontAttribute(paragraphRange, attributedString, curElement, font);                    
                    applyBackgroundAttribute(styleContext, paragraphRange, attributedString, curElement, attributes);
                    applyUnderlineAttribute(paragraphRange, attributedString, curElement, attributes);
                    applyStrikeThroughAttribute(paragraphRange, attributedString, curElement, attributes);          

                    // And set the position to the end of the given attribute
                    pos = curElement.getEndOffset();
                }
            }
        }

        recomputeLayout();
    }

    /**
     * Returns the first leaf encountered by drilling into the document for the
     * given position.
     * 
     * @param pos position under which we're trying to find a leaf
     * @param rootElement top most element in the document tree
     * 
     * @return Leaf element that corresponds to the position provided in the
     *         document
     */
    private Element drillDownFromRoot(final int pos, final Element rootElement) {
        // Before each pass, start at the root
        Element curElement = rootElement;

        // Now we descend the hierarchy until we get to a leaf
        while (!curElement.isLeaf()) {
            curElement = curElement.getElement(curElement.getElementIndex(pos));
        }

        return curElement;
    }

    protected void applyFontAttribute(final RunInfo paragraphRange, final AttributedString attributedString,
            final Element curElement, final Font font) {
        if (font != null) {
            attributedString.addAttribute(TextAttribute.FONT, font, Math.max(0, curElement.getStartOffset()
                    - paragraphRange.startIndex), Math.min(paragraphRange.endIndex - paragraphRange.startIndex,
                    curElement.getEndOffset() - paragraphRange.startIndex));
        }
    }

    protected void applyStrikeThroughAttribute(final RunInfo paragraphRange, final AttributedString attributedString,
            final Element curElement, final AttributeSet attributes) {
        final boolean strikethrough = StyleConstants.isStrikeThrough(attributes);
        if (strikethrough) {
            attributedString.addAttribute(TextAttribute.STRIKETHROUGH, Boolean.TRUE, Math.max(0, curElement
                    .getStartOffset()
                    - paragraphRange.startIndex), Math.min(paragraphRange.endIndex - paragraphRange.startIndex,
                    curElement.getEndOffset() - paragraphRange.startIndex));
        }
    }

    protected void applyUnderlineAttribute(final RunInfo paragraphRange, final AttributedString attributedString,
            final Element curElement, final AttributeSet attributes) {
        final boolean underline = StyleConstants.isUnderline(attributes);
        if (underline) {
            int startOffset = Math.max(0, curElement.getStartOffset() - paragraphRange.startIndex);
            int endOffset = Math.min(paragraphRange.endIndex - paragraphRange.startIndex,
                    curElement.getEndOffset() - paragraphRange.startIndex);
            attributedString.addAttribute(TextAttribute.UNDERLINE, Boolean.TRUE, startOffset, endOffset);
        }
    }

    protected void applyBackgroundAttribute(final StyleContext style, final RunInfo paragraphRange,
            final AttributedString attributedString, final Element curElement, final AttributeSet attributes) {
        if (attributes.isDefined(StyleConstants.Background)) {
            final Color background = style.getBackground(attributes);
            attributedString.addAttribute(TextAttribute.BACKGROUND, background, Math.max(0, curElement.getStartOffset()
                    - paragraphRange.startIndex), Math.min(paragraphRange.endIndex - paragraphRange.startIndex,
                    curElement.getEndOffset() - paragraphRange.startIndex));
        }
    }

    private Font extractFont(final StyleContext style, final int pos, final Element rootElement,
            final AttributeSet attributes) {
        Font font = null;
        if (attributes.isDefined(StyleConstants.FontSize) || attributes.isDefined(StyleConstants.FontFamily)) {
            font = style.getFont(attributes);
        }

        if (font == null) {
            if (document instanceof DefaultStyledDocument) {
                font = extractFontFromDefaultStyledDocument((DefaultStyledDocument) document, style, pos, rootElement);
            }
            else {
                font = style.getFont(rootElement.getAttributes());
            }
        }
        return font;
    }

    private Font extractFontFromDefaultStyledDocument(final DefaultStyledDocument styledDocument,
            final StyleContext style, final int pos, final Element rootElement) {
        Font font = style.getFont(styledDocument.getCharacterElement(pos).getAttributes());
        if (font == null) {
            font = style.getFont(styledDocument.getParagraphElement(pos).getAttributes());
            if (font == null) {
                font = style.getFont(rootElement.getAttributes());
            }
        }
        return font;
    }

    private ArrayList extractParagraphRanges(final String documentString) {
        // The paragraph start and end indices
        final ArrayList paragraphRanges = new ArrayList();

        // The current position in the specified range
        int pos = 0;

        final StringTokenizer tokenizer = new StringTokenizer(documentString, "\n", true);

        // lastNewLine is used to detect the case when two newlines follow
        // in direct succession
        // & lastNewLine should be true to start in case the first character
        // is a newline
        boolean lastNewLine = true;

        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken();

            // If the token
            if (token.equals("\n")) {
                if (lastNewLine) {
                    stringContents.add(new AttributedString(" "));
                    paragraphRanges.add(new RunInfo(pos, pos + 1));
                }

                pos = pos + 1;

                lastNewLine = true;
            }
            // If the token is empty - create an attributed string with a
            // single space since LineBreakMeasurers don't work with an empty
            // string
            // - note that this case should only arise if the document is empty
            else if (token.equals("")) {
                stringContents.add(new AttributedString(" "));
                paragraphRanges.add(new RunInfo(pos, pos));

                lastNewLine = false;
            }
            // This is the normal case - where we have some text
            else {
                stringContents.add(new AttributedString(token));
                paragraphRanges.add(new RunInfo(pos, pos + token.length()));

                // Increment the position
                pos = pos + token.length();

                lastNewLine = false;
            }
        }

        // Add one more newline if the last character was a newline
        if (lastNewLine) {
            stringContents.add(new AttributedString(" "));
            paragraphRanges.add(new RunInfo(pos, pos + 1));
        }

        return paragraphRanges;
    }

    /**
     * Compute the bounds of the text wrapped by this node. The text layout is
     * wrapped based on the bounds of this node. If the shrinkBoundsToFit
     * parameter is true then after the text has been laid out the bounds of
     * this node are shrunk to fit around those text bounds.
     */
    public void recomputeLayout() {
        if (stringContents == null) {
            return;
        }

        final ArrayList linesList = new ArrayList();

        double textWidth = 0;
        double textHeight = 0;

        final Iterator contentIterator = stringContents.iterator();

        while (contentIterator.hasNext()) {
            final AttributedString ats = (AttributedString) contentIterator.next();
            final AttributedCharacterIterator itr = ats.getIterator();

            LineBreakMeasurer measurer;
            ArrayList breakList = null;

            measurer = new LineBreakMeasurer(itr, SWING_FRC);
            breakList = extractLineBreaks(itr, measurer);

            measurer = new LineBreakMeasurer(itr, PPaintContext.RENDER_QUALITY_HIGH_FRC);

            // Need to change the lineinfo data structure to know about multiple
            // text layouts per line

            LineInfo lineInfo = null;
            boolean newLine = true;
            double lineWidth = 0;
            while (measurer.getPosition() < itr.getEndIndex()) {
                TextLayout aTextLayout = null;

                if (newLine) {
                    newLine = false;

                    final double lineHeight = calculateLineHeightFromLineInfo(lineInfo);

                    textHeight = textHeight + lineHeight;
                    textWidth = Math.max(textWidth, lineWidth);

                    // Now create a new line
                    lineInfo = new LineInfo();
                    linesList.add(lineInfo);
                }

                final int lineEnd = ((Integer) breakList.get(0)).intValue();
                if (lineEnd <= itr.getRunLimit()) {
                    breakList.remove(0);
                    newLine = true;
                }

                aTextLayout = measurer.nextLayout(Float.MAX_VALUE, Math.min(lineEnd, itr.getRunLimit()), false);

                final SegmentInfo sInfo = createSegmentInfo(itr, aTextLayout);

                final FontMetrics metrics = StyleContext.getDefaultStyleContext().getFontMetrics(
                        (Font) itr.getAttribute(TextAttribute.FONT));
                lineInfo.maxAscent = Math.max(lineInfo.maxAscent, metrics.getMaxAscent());
                lineInfo.maxDescent = Math.max(lineInfo.maxDescent, metrics.getMaxDescent());
                lineInfo.leading = Math.max(lineInfo.leading, metrics.getLeading());

                lineInfo.segments.add(sInfo);

                itr.setIndex(measurer.getPosition());
                lineWidth = lineWidth + aTextLayout.getAdvance();
            }

            final double lineHeight = calculateLineHeightFromLineInfo(lineInfo);
            textHeight = textHeight + lineHeight;
            textWidth = Math.max(textWidth, lineWidth);
        }

        lines = (LineInfo[]) linesList.toArray(new LineInfo[linesList.size()]);

        constrainDimensionsIfNeeded(textWidth, textHeight);
    }

    protected SegmentInfo createSegmentInfo(final AttributedCharacterIterator itr, TextLayout aTextLayout) {
        final SegmentInfo sInfo = newSegmentInfo();
        sInfo.font = (Font) itr.getAttribute(TextAttribute.FONT);
        sInfo.foreground = (Color) itr.getAttribute(TextAttribute.FOREGROUND);
        sInfo.background = (Color) itr.getAttribute(TextAttribute.BACKGROUND);
        sInfo.underline = (Boolean) itr.getAttribute(TextAttribute.UNDERLINE);
        sInfo.layout = aTextLayout;
        return sInfo;
    }

    protected SegmentInfo newSegmentInfo() {
        return new SegmentInfo();
    }
    
    /**
     * @param lineInfo
     * @return
     */
    private double calculateLineHeightFromLineInfo(final LineInfo lineInfo) {
        final double lineHeight;
        if (lineInfo == null) {
            lineHeight = 0;
        }
        else {
            lineHeight = lineInfo.maxAscent + lineInfo.maxDescent + lineInfo.leading;
        }
        return lineHeight;
    }

    private void constrainDimensionsIfNeeded(final double textWidth, final double textHeight) {
        if (!constrainWidthToTextWidth && !constrainHeightToTextHeight) {
            return;
        }

        double newWidth = getWidth();
        double newHeight = getHeight();

        if (constrainWidthToTextWidth) {
            newWidth = textWidth + insets.left + insets.right;
        }

        if (constrainHeightToTextHeight) {
            newHeight = Math.max(textHeight, getInitialFontHeight()) + insets.top + insets.bottom;
        }

        super.setBounds(getX(), getY(), newWidth, newHeight);
    }

    // Because swing doesn't use fractional font metrics by default, we use
    // LineBreakMeasurer to find out where Swing is going to break them
    private ArrayList extractLineBreaks(final AttributedCharacterIterator itr, final LineBreakMeasurer measurer) {
        ArrayList breakList;
        breakList = new ArrayList();
        while (measurer.getPosition() < itr.getEndIndex()) {
            if (constrainWidthToTextWidth) {
                measurer.nextLayout(Float.MAX_VALUE);
            }
            else {
                measurer.nextLayout((float) Math.ceil(getWidth() - insets.left - insets.right));
            }

            breakList.add(new Integer(measurer.getPosition()));
        }
        return breakList;
    }

    /**
     * Get the height of the font at the beginning of the document.
     * 
     * @return height of font at the start of the document.
     */
    public double getInitialFontHeight() {

        // Small assumption here that there is one root element - can fix
        // for more general support later
        final Element rootElement = document.getDefaultRootElement();
        final Element curElement = drillDownFromRoot(0, rootElement);
        final StyleContext context = StyleContext.getDefaultStyleContext();
        final Font font = context.getFont(curElement.getAttributes());

        final FontMetrics curFM = context.getFontMetrics(font);

        return curFM.getMaxAscent() + curFM.getMaxDescent() + curFM.getLeading();
    }

    /** {@inheritDoc} */
    protected void paint(final PPaintContext paintContext) {
        if (lines == null || lines.length == 0) {
            return;
        }

        final float x = (float) (getX() + insets.left);
        float y = (float) (getY() + insets.top);
        final float bottomY = (float) (getY() + getHeight() - insets.bottom);

        final Graphics2D g2 = paintContext.getGraphics();

        if (getPaint() != null) {
            g2.setPaint(getPaint());
            g2.fill(getBoundsReference());
        }

        float curX;
        LineInfo lineInfo;
        for (int i = 0; i < lines.length; i++) {
            lineInfo = lines[i];
            y += lineInfo.maxAscent;
            curX = x;

            if (bottomY < y) {
                return;
            }

            for (int j = 0; j < lineInfo.segments.size(); j++) {
                final SegmentInfo sInfo = (SegmentInfo) lineInfo.segments.get(j);
                final float width = sInfo.layout.getAdvance();

                if (sInfo.background != null) {
                    g2.setPaint(sInfo.background);
                    g2.fill(new Rectangle2D.Double(curX, y - lineInfo.maxAscent, width, lineInfo.maxAscent
                            + lineInfo.maxDescent + lineInfo.leading));
                }

                sInfo.applyFont(g2);

                // Manually set the paint - this is specified in the
                // AttributedString but seems to be
                // ignored by the TextLayout. To handle multiple colors we
                // should be breaking up the lines
                // but that functionality can be added later as needed
                g2.setPaint(sInfo.foreground);
                sInfo.layout.draw(g2, curX, y);

                // Draw the underline and the strikethrough after the text
                drawUnderlineAndStrikethroughAfterText(curX, y, g2, lineInfo, sInfo, width);

                curX = curX + width;
            }

            y += lineInfo.maxDescent + lineInfo.leading;
        }
    }

    protected void drawUnderlineAndStrikethroughAfterText
        (final float x, float y, final Graphics2D g2, LineInfo lineInfo, final SegmentInfo sInfo, final float width) {
        if (sInfo.underline != null) {
            paintLine.setLine(x, y + 1 + lineInfo.maxDescent / 2, x + width, y + 1 + lineInfo.maxDescent / 2);
            g2.draw(paintLine);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void fullPaint(final PPaintContext paintContext) {
        if (!editing) {
            super.fullPaint(paintContext);
        }
    }

    /**
     * Set whether this node is current in editing mode.
     * 
     * @param editing value to set editing flag
     */
    public void setEditing(final boolean editing) {
        this.editing = editing;
    }

    /**
     * Whether node is currently in editing state.
     * 
     * @return true if node is currently editing
     */
    public boolean isEditing() {
        return editing;
    }

    /**
     * Set the insets of the text.
     * 
     * @param insets desired insets
     */
    public void setInsets(final Insets insets) {
        if (insets != null) {
            this.insets.left = insets.left;
            this.insets.right = insets.right;
            this.insets.top = insets.top;
            this.insets.bottom = insets.bottom;

            recomputeLayout();
        }
    }

    /**
     * Get the insets of the text.
     * 
     * @return current text insets
     */
    public Insets getInsets() {
        return (Insets) insets.clone();
    }

    /** {@inheritDoc} */
    public boolean setBounds(final double x, final double y, final double width, final double height) {
        if (document == null || !super.setBounds(x, y, width, height)) {
            return false;
        }

        recomputeLayout();
        return true;
    }

    /**
     * Simple class to represent an range within the document.
     */
    protected static class RunInfo {
        private int startIndex;
        private int endIndex;

        /**
         * Constructs a RunInfo representing the range within the document from
         * runStart to runLimit.
         * 
         * @param runStart starting index of the range
         * @param runLimit ending index of the range
         */
        public RunInfo(final int runStart, final int runLimit) {
            startIndex = runStart;
            endIndex = runLimit;
        }

        /**
         * Returns whether the run is empty.
         * 
         * @return true is run is empty
         */
        public boolean isEmpty() {
            return startIndex == endIndex;
        }

        /**
         * Returns the length of the run.
         * 
         * @return length of run
         */
        public int length() {
            return endIndex - startIndex;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }
        
    }

    /**
     * The info for rendering and computing the bounds of a line.
     */
    protected static class LineInfo {
        /** Segments which make up this line's formatting segments. */
        public List segments;

        /** Maximum of the line segments' ascents. */
        public double maxAscent;

        /** Maximum of the line segments' descents. */
        public double maxDescent;

        /** Leading space at front of line segment. */
        public double leading;

        /**
         * Creates a LineInfo that contains no segments.
         */
        public LineInfo() {
            segments = new ArrayList();
        }
    }

    /**
     * Encapsulates information about a particular LineSegment.
     */
    protected static class SegmentInfo {
        /** Text Layout applied to the segment. */
        public TextLayout layout;

        /** Font being used to render the segment. */
        public Font font;

        /** Foreground (text) color of the segment. */
        public Color foreground;

        /** Background color of the segment. */
        public Color background;

        /** Whether the segment is underlined. */
        public Boolean underline;

        /** Construct a segment with null properties. */
        public SegmentInfo() {
        }

        /**
         * Applies this particular SegmentInfo's font to the graphics object
         * passed in.
         * 
         * @param g2 will have the font of this segment applied
         */
        public void applyFont(final Graphics2D g2) {
            if (font != null) {
                g2.setFont(font);
            }
        }
    }
}