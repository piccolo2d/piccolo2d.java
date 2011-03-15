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

import java.awt.image.BufferedImage;

import org.piccolo2d.PCanvas;
import org.piccolo2d.PLayer;
import org.piccolo2d.PNode;
import org.piccolo2d.extras.PFrame;
import org.piccolo2d.nodes.PImage;
import org.piccolo2d.nodes.PText;




/**
 * This example demonstrates the difference between
 * the different fill strategies for {@link PNode#toImage(BufferedImage,Paint,int)}.
 */
public class ToImageExample
    extends PFrame {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;


    /**
     * Create a new toImage example.
     */
    public ToImageExample() {
        this(null);
    }

    /**
     * Create a new toImage example for the specified canvas.
     * 
     * @param canvas canvas for this toImage example
     */
    public ToImageExample(final PCanvas canvas) {
        super("ToImageExample", false, canvas);
    }


    /** {@inheritDoc} */
    public void initialize() {
        PText aspectFit = new PText("Aspect Fit");
        PText aspectCover = new PText("Aspect Cover");
        PText exactFit = new PText("Exact Fit");

        PImage aspectFit100x100 = new PImage(aspectFit.toImage(
                new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB), Color.LIGHT_GRAY,
                PNode.FILL_STRATEGY_ASPECT_FIT));
        PImage aspectFit100x200 = new PImage(aspectFit.toImage(
                new BufferedImage(100, 200, BufferedImage.TYPE_INT_ARGB), Color.LIGHT_GRAY,
                PNode.FILL_STRATEGY_ASPECT_FIT));
        PImage aspectFit200x100 = new PImage(aspectFit.toImage(
                new BufferedImage(200, 100, BufferedImage.TYPE_INT_ARGB), Color.LIGHT_GRAY,
                PNode.FILL_STRATEGY_ASPECT_FIT));
        aspectFit.setOffset(10.0, 20.0);
        aspectFit100x100.setOffset(10.0, 70.0);
        aspectFit100x200.setOffset(10.0, 174.0);
        aspectFit200x100.setOffset(10.0, 378.0);

        PImage aspectCover100x100 = new PImage(aspectCover.toImage(new BufferedImage(100, 100,
                BufferedImage.TYPE_INT_ARGB), Color.YELLOW, PNode.FILL_STRATEGY_ASPECT_COVER));
        PImage aspectCover100x200 = new PImage(aspectCover.toImage(new BufferedImage(100, 200,
                BufferedImage.TYPE_INT_ARGB), Color.YELLOW, PNode.FILL_STRATEGY_ASPECT_COVER));
        PImage aspectCover200x100 = new PImage(aspectCover.toImage(new BufferedImage(200, 100,
                BufferedImage.TYPE_INT_ARGB), Color.YELLOW, PNode.FILL_STRATEGY_ASPECT_COVER));
        aspectCover.setOffset(214.0, 20.0);
        aspectCover100x100.setOffset(214.0, 70.0);
        aspectCover100x200.setOffset(214.0, 174.0);
        aspectCover200x100.setOffset(214.0, 378.0);

        PImage exactFit100x100 = new PImage(exactFit.toImage(new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB),
                Color.PINK, PNode.FILL_STRATEGY_EXACT_FIT));
        PImage exactFit100x200 = new PImage(exactFit.toImage(new BufferedImage(100, 200, BufferedImage.TYPE_INT_ARGB),
                Color.PINK, PNode.FILL_STRATEGY_EXACT_FIT));
        PImage exactFit200x100 = new PImage(exactFit.toImage(new BufferedImage(200, 100, BufferedImage.TYPE_INT_ARGB),
                Color.PINK, PNode.FILL_STRATEGY_EXACT_FIT));
        exactFit.setOffset(410.0, 20.0);
        exactFit100x100.setOffset(418.0, 70.0);
        exactFit100x200.setOffset(418.0, 174.0);
        exactFit200x100.setOffset(418.0, 378.0);

        PImage texture = new PImage(getClass().getResource("texture.png"));

        PImage textureAspectFit100x100 = new PImage(texture.toImage(
                new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB), Color.LIGHT_GRAY,
                PNode.FILL_STRATEGY_ASPECT_FIT));
        PImage textureAspectFit100x200 = new PImage(texture.toImage(
                new BufferedImage(100, 200, BufferedImage.TYPE_INT_ARGB), Color.LIGHT_GRAY,
                PNode.FILL_STRATEGY_ASPECT_FIT));
        PImage textureAspectFit200x100 = new PImage(texture.toImage(
                new BufferedImage(200, 100, BufferedImage.TYPE_INT_ARGB), Color.LIGHT_GRAY,
                PNode.FILL_STRATEGY_ASPECT_FIT));

        textureAspectFit100x100.setOffset(10.0, 482.0);
        textureAspectFit100x200.setOffset(10.0, 586.0);
        textureAspectFit200x100.setOffset(10.0, 790.0);

        PImage textureAspectCover100x100 = new PImage(texture.toImage(new BufferedImage(100, 100,
                BufferedImage.TYPE_INT_ARGB), Color.YELLOW, PNode.FILL_STRATEGY_ASPECT_COVER));
        PImage textureAspectCover100x200 = new PImage(texture.toImage(new BufferedImage(100, 200,
                BufferedImage.TYPE_INT_ARGB), Color.YELLOW, PNode.FILL_STRATEGY_ASPECT_COVER));
        PImage textureAspectCover200x100 = new PImage(texture.toImage(new BufferedImage(200, 100,
                BufferedImage.TYPE_INT_ARGB), Color.YELLOW, PNode.FILL_STRATEGY_ASPECT_COVER));

        textureAspectCover100x100.setOffset(214.0, 482.0);
        textureAspectCover100x200.setOffset(214.0, 586.0);
        textureAspectCover200x100.setOffset(214.0, 790.0);

        PImage textureExactFit100x100 = new PImage(texture.toImage(new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB),
                Color.PINK, PNode.FILL_STRATEGY_EXACT_FIT));
        PImage textureExactFit100x200 = new PImage(texture.toImage(new BufferedImage(100, 200, BufferedImage.TYPE_INT_ARGB),
                Color.PINK, PNode.FILL_STRATEGY_EXACT_FIT));
        PImage textureExactFit200x100 = new PImage(texture.toImage(new BufferedImage(200, 100, BufferedImage.TYPE_INT_ARGB),
                Color.PINK, PNode.FILL_STRATEGY_EXACT_FIT));

        textureExactFit100x100.setOffset(418.0, 482.0);
        textureExactFit100x200.setOffset(418.0, 586.0);
        textureExactFit200x100.setOffset(418.0, 790.0);

        PLayer layer = getCanvas().getLayer();
        layer.addChild(aspectFit);
        layer.addChild(aspectCover);
        layer.addChild(exactFit);
        layer.addChild(aspectFit100x100);
        layer.addChild(aspectFit100x200);
        layer.addChild(aspectFit200x100);
        layer.addChild(aspectCover100x100);
        layer.addChild(aspectCover100x200);
        layer.addChild(aspectCover200x100);
        layer.addChild(exactFit100x100);
        layer.addChild(exactFit100x200);
        layer.addChild(exactFit200x100);
        layer.addChild(textureAspectFit100x100);
        layer.addChild(textureAspectFit100x200);
        layer.addChild(textureAspectFit200x100);
        layer.addChild(textureAspectCover100x100);
        layer.addChild(textureAspectCover100x200);
        layer.addChild(textureAspectCover200x100);
        layer.addChild(textureExactFit100x100);
        layer.addChild(textureExactFit100x200);
        layer.addChild(textureExactFit200x100);

        setSize(650, 510);
    }


    /**
     * Main.
     * 
     * @param args command line arguments, ignored
     */
    public static void main(final String[] args) {
        new ToImageExample();
    }
}
