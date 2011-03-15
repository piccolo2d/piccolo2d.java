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
package org.piccolo2d.examples;

import java.awt.Color;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;

import org.piccolo2d.PCanvas;
import org.piccolo2d.extras.PFrame;
import org.piccolo2d.extras.nodes.PStyledText;
import org.piccolo2d.nodes.PHtmlView;
import org.piccolo2d.nodes.PText;




/**
 * Example of text rendering with offset bounds.
 */
public class TextOffsetBoundsExample extends PFrame {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    public TextOffsetBoundsExample() {
        this(null);
    }

    public TextOffsetBoundsExample(final PCanvas aCanvas) {
        super("TextOffsetBoundsExample", false, aCanvas);
    }

    public void initialize() {
        String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit posuere.";
        PText ptext = new PText(text);
        ptext.setPaint(Color.GRAY);
        ptext.setBounds(0.0d, 10.0d, 600.0d, 40.0d);
        ptext.offset(0.0d, 50.0d);
        
        PHtmlView phtmlView = new PHtmlView(text);
        phtmlView.setPaint(Color.GRAY);
        phtmlView.setBounds(0.0d, 10.0d, 600.0d, 40.0d);
        phtmlView.offset(0.0d, 150.0d);

        PStyledText pstyledText = new PStyledText();
        Document document = new DefaultStyledDocument();
        try {
            document.insertString(0, text, null);
        }
        catch (BadLocationException e) {
            // ignore
        }
        pstyledText.setDocument(document);
        pstyledText.setPaint(Color.GRAY);
        pstyledText.setBounds(0.0d, 10.0d, 600.0d, 40.0d);
        pstyledText.offset(0.0d, 250.0d);

        getCanvas().getLayer().addChild(ptext);
        getCanvas().getLayer().addChild(phtmlView);
        getCanvas().getLayer().addChild(pstyledText);
    }

    public static void main(final String[] args) {
        new TextOffsetBoundsExample();
    }}
