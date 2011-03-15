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
package org.piccolo2d.extras.event;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.piccolo2d.PCamera;
import org.piccolo2d.PCanvas;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PInputEventFilter;
import org.piccolo2d.extras.nodes.PStyledText;


/**
 * @author Lance Good
 */
public class PStyledTextEventHandler extends PBasicInputEventHandler {
    private static final int TEXT_EDIT_PADDING = 3;

    /** Canvas onto which this event handler is attached. */
    protected PCanvas canvas;

    /** Editor used to edit a PStyledText's content when it is in edit mode. */
    protected JTextComponent editor;

    /**
     * A listener that will handle programatic changes to the underlying
     * document and update the view accordingly.
     */
    protected DocumentListener docListener;

    /** The Styled text being edited. */
    protected PStyledText editedText;

    /**
     * Basic constructor for PStyledTextEventHandler.
     * 
     * @param canvas canvas to which this handler will be attached
     */
    public PStyledTextEventHandler(final PCanvas canvas) {
        final PInputEventFilter filter = new PInputEventFilter();
        filter.setOrMask(InputEvent.BUTTON1_MASK | InputEvent.BUTTON3_MASK);
        setEventFilter(filter);
        this.canvas = canvas;
        initEditor(createDefaultEditor());
    }

    /**
     * Constructor for PStyledTextEventHandler that allows an editor to be
     * specified.
     * 
     * @param canvas canvas to which this handler will be attached
     * @param editor component to display when editing a PStyledText node
     */
    public PStyledTextEventHandler(final PCanvas canvas, final JTextComponent editor) {
        super();

        this.canvas = canvas;
        initEditor(editor);
    }

    /**
     * Installs the editor onto the canvas. Making it the editor that will be
     * used whenever a PStyledText node needs editing.
     * 
     * @param newEditor component responsible for a PStyledText node while it is
     *            being edited.
     */
    protected void initEditor(final JTextComponent newEditor) {
        editor = newEditor;

        canvas.setLayout(null);
        canvas.add(editor);
        editor.setVisible(false);

        docListener = createDocumentListener();
    }

    /**
     * Creates a default editor component to be used when editing a PStyledText
     * node.
     * 
     * @return a freshly created JTextComponent subclass that can be used to
     *         edit PStyledText nodes
     */
    protected JTextComponent createDefaultEditor() {
        return new DefaultTextEditor();
    }

    /**
     * Returns a document listener that will reshape the editor whenever a
     * change occurs to its attached document.
     * 
     * @return a DocumentListener
     */
    protected DocumentListener createDocumentListener() {
        return new DocumentListener() {
            public void removeUpdate(final DocumentEvent e) {
                reshapeEditorLater();
            }

            public void insertUpdate(final DocumentEvent e) {
                reshapeEditorLater();
            }

            public void changedUpdate(final DocumentEvent e) {
                reshapeEditorLater();
            }
        };
    }

    /**
     * Creates a PStyledText instance and attaches a simple document to it. If
     * possible, it configures its font information too.
     * 
     * @return a new PStyledText instance
     */
    public PStyledText createText() {
        final PStyledText newText = new PStyledText();

        final Document doc = editor.getUI().getEditorKit(editor).createDefaultDocument();
        if (doc instanceof StyledDocument && missingFontFamilyOrSize(doc)) {
            final Font eFont = editor.getFont();
            final SimpleAttributeSet sas = new SimpleAttributeSet();
            sas.addAttribute(StyleConstants.FontFamily, eFont.getFamily());
            sas.addAttribute(StyleConstants.FontSize, new Integer(eFont.getSize()));

            ((StyledDocument) doc).setParagraphAttributes(0, doc.getLength(), sas, false);
        }
        newText.setDocument(doc);

        return newText;
    }

    private boolean missingFontFamilyOrSize(final Document doc) {
        return !doc.getDefaultRootElement().getAttributes().isDefined(StyleConstants.FontFamily)
                || !doc.getDefaultRootElement().getAttributes().isDefined(StyleConstants.FontSize);
    }

    /**
     * A callback that is invoked any time the mouse is pressed on the canvas.
     * If the press occurs directly on the canvas, it create a new PStyledText
     * instance and puts it in editing mode. If the click is on a node, it marks
     * changes it to editing mode.
     * 
     * @param event mouse click event that can be queried
     */
    public void mousePressed(final PInputEvent event) {
        final PNode pickedNode = event.getPickedNode();

        stopEditing(event);

        if (event.getButton() != MouseEvent.BUTTON1) {
            return;
        }

        if (pickedNode instanceof PStyledText) {
            startEditing(event, (PStyledText) pickedNode);
        }
        else if (pickedNode instanceof PCamera) {
            final PStyledText newText = createText();
            final Insets pInsets = newText.getInsets();
            newText.translate(event.getPosition().getX() - pInsets.left, event.getPosition().getY() - pInsets.top);
            startEditing(event, newText);
        }
    }

    /**
     * Begins editing the provided text node as a result of the provided event.
     * Will swap out the text node for an editor.
     * 
     * @param event the event responsible for starting the editing
     * @param text text node being edited
     */
    public void startEditing(final PInputEvent event, final PStyledText text) {
        // Get the node's top right hand corner
        final Insets pInsets = text.getInsets();
        final Point2D nodePt = new Point2D.Double(text.getX() + pInsets.left, text.getY() + pInsets.top);
        text.localToGlobal(nodePt);
        event.getTopCamera().viewToLocal(nodePt);

        // Update the editor to edit the specified node
        editor.setDocument(text.getDocument());
        editor.setVisible(true);

        final Insets bInsets = editor.getBorder().getBorderInsets(editor);
        editor.setLocation((int) nodePt.getX() - bInsets.left, (int) nodePt.getY() - bInsets.top);
        reshapeEditorLater();

        dispatchEventToEditor(event);
        canvas.repaint();

        text.setEditing(true);
        text.getDocument().addDocumentListener(docListener);
        editedText = text;
    }

    /**
     * Stops editing the current text node.
     * 
     * @param event the event responsible for stopping the editing
     */
    public void stopEditing(final PInputEvent event) {
        if (editedText == null) {
            return;
        }

        editedText.getDocument().removeDocumentListener(docListener);
        editedText.setEditing(false);

        if (editedText.getDocument().getLength() == 0) {
            editedText.removeFromParent();
        }
        else {
            editedText.syncWithDocument();
        }

        if (editedText.getParent() == null) {
            editedText.setScale(1.0 / event.getCamera().getViewScale());
            canvas.getLayer().addChild(editedText);
        }
        editor.setVisible(false);
        canvas.repaint();

        editedText = null;
    }

    /**
     * Intercepts Piccolo2D events and dispatches the underlying swing one to
     * the current editor.
     * 
     * @param event the swing event being intercepted
     */
    public void dispatchEventToEditor(final PInputEvent event) {
        // We have to nest the mouse press in two invoke laters so that it is
        // fired so that the component has been completely validated at the new
        // size and the mouse event has the correct offset
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        final MouseEvent me = new MouseEvent(editor, MouseEvent.MOUSE_PRESSED, event.getWhen(), event
                                .getModifiers()
                                | InputEvent.BUTTON1_MASK, (int) (event.getCanvasPosition().getX() - editor.getX()),
                                (int) (event.getCanvasPosition().getY() - editor.getY()), 1, false);
                        editor.dispatchEvent(me);
                    }
                });
            }
        });
    }

    /**
     * Adjusts the shape of the editor to fit the current document.
     */
    public void reshapeEditor() {
        if (editedText != null) {
            Dimension prefSize = editor.getPreferredSize();

            final Insets textInsets = editedText.getInsets();
            final Insets editorInsets = editor.getInsets();

            final int width;
            if (editedText.getConstrainWidthToTextWidth()) {
                width = (int) prefSize.getWidth();
            }
            else {
                width = (int) (editedText.getWidth() - textInsets.left - textInsets.right + editorInsets.left
                        + editorInsets.right + TEXT_EDIT_PADDING);
            }
            prefSize.setSize(width, prefSize.getHeight());
            editor.setSize(prefSize);

            prefSize = editor.getPreferredSize();
            final int height;
            if (editedText.getConstrainHeightToTextHeight()) {
                height = (int) prefSize.getHeight();
            }
            else {
                height = (int) (editedText.getHeight() - textInsets.top - textInsets.bottom + editorInsets.top
                        + editorInsets.bottom + TEXT_EDIT_PADDING);
            }
            prefSize.setSize(width, height);
            editor.setSize(prefSize);
        }
    }

    /**
     * Sometimes we need to invoke this later because the document events seem
     * to get fired before the text is actually incorporated into the document.
     */
    protected void reshapeEditorLater() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reshapeEditor();
            }
        });
    }

    private static final class DefaultTextEditor extends JTextPane {
        private static final long serialVersionUID = 1L;

        public DefaultTextEditor() {
            EmptyBorder padding = new EmptyBorder(TEXT_EDIT_PADDING,
                    TEXT_EDIT_PADDING, TEXT_EDIT_PADDING, TEXT_EDIT_PADDING);
            setBorder(new CompoundBorder(new LineBorder(Color.black), padding));
        }

        /**
         * Set some rendering hints - if we don't then the rendering can be
         * inconsistent. Also, Swing doesn't work correctly with fractional
         * metrics.
         */
        public void paint(final Graphics graphics) {
            if (!(graphics instanceof Graphics2D)) {
                throw new IllegalArgumentException("Provided graphics context is not a Graphics2D object");
            }
            
            final Graphics2D g2 = (Graphics2D) graphics;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);

            super.paint(graphics);
        }
    }
}
