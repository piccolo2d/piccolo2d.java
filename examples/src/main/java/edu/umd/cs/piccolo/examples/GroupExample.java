/*
 * Copyright (c) 2008, Piccolo2D project, http://piccolo2d.org
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
package edu.umd.cs.piccolo.examples;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.util.ArrayList;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.event.PSelectionEventHandler;

/**
 * An example of how to implement decorator groups. Decorator groups are nodes
 * that base their bounds and rendering on their children. This seems to be a
 * common type of visual node that requires some potentially non-obvious
 * subclassing to get right.
 * 
 * Both a volatile and a non-volatile implementation are shown. The volatile
 * implementation might be used in cases where you want to keep a
 * scale-independent pen width border around a group of objects. The
 * non-volatile implementation can be used in more standard cases where the
 * decorator's bounds are stable during zooming.
 * 
 * @author Lance Good
 */
public class GroupExample extends PFrame {

    public GroupExample() {
        this(null);
    }

    public GroupExample(PCanvas aCanvas) {
        super("GroupExample", false, aCanvas);
    }

    public void initialize() {
        super.initialize();

        getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());

        // Create a decorator group that is NOT volatile
        DecoratorGroup dg = new DecoratorGroup();
        dg.setPaint(Color.magenta);

        // Put some nodes under the group for it to decorate
        PPath p1 = PPath.createEllipse(25, 25, 75, 75);
        p1.setPaint(Color.red);
        PPath p2 = PPath.createRectangle(125, 75, 50, 50);
        p2.setPaint(Color.blue);

        // Add everything to the Piccolo hierarchy
        dg.addChild(p1);
        dg.addChild(p2);
        getCanvas().getLayer().addChild(dg);

        // Create a decorator group that IS volatile
        VolatileDecoratorGroup vdg = new VolatileDecoratorGroup(getCanvas().getCamera());
        vdg.setPaint(Color.cyan);

        // Put some nodes under the group for it to decorate
        PPath p3 = PPath.createEllipse(275, 175, 50, 50);
        p3.setPaint(Color.blue);
        PPath p4 = PPath.createRectangle(175, 175, 75, 75);
        p4.setPaint(Color.green);

        // Add everything to the Piccolo hierarchy
        vdg.addChild(p3);
        vdg.addChild(p4);
        getCanvas().getLayer().addChild(vdg);

        // Create a selection handler so we can see that the decorator actually
        // works
        ArrayList selectableParents = new ArrayList();
        selectableParents.add(dg);
        selectableParents.add(vdg);

        PSelectionEventHandler ps = new PSelectionEventHandler(getCanvas().getLayer(), selectableParents);
        getCanvas().addInputEventListener(ps);
    }

    public static void main(String[] args) {
        new GroupExample();
    }
}

/**
 * This is the non-volatile implementation of a decorator group that paints a
 * background rectangle based on the bounds of its children.
 */
class DecoratorGroup extends PNode {
    int INDENT = 10;

    PBounds cachedChildBounds = new PBounds();
    PBounds comparisonBounds = new PBounds();

    public DecoratorGroup() {
        super();
    }

    /**
     * Change the default paint to fill an expanded bounding box based on its
     * children's bounds
     */
    public void paint(PPaintContext ppc) {
        Paint paint = getPaint();
        if (paint != null) {
            Graphics2D g2 = ppc.getGraphics();
            g2.setPaint(paint);

            PBounds bounds = getUnionOfChildrenBounds(null);
            bounds.setRect(bounds.getX() - INDENT, bounds.getY() - INDENT, bounds.getWidth() + 2 * INDENT, bounds
                    .getHeight()
                    + 2 * INDENT);
            g2.fill(bounds);
        }
    }

    /**
     * Change the full bounds computation to take into account that we are
     * expanding the children's bounds Do this instead of overriding
     * getBoundsReference() since the node is not volatile
     */
    public PBounds computeFullBounds(PBounds dstBounds) {
        PBounds result = getUnionOfChildrenBounds(dstBounds);

        cachedChildBounds.setRect(result);
        result.setRect(result.getX() - INDENT, result.getY() - INDENT, result.getWidth() + 2 * INDENT, result
                .getHeight()
                + 2 * INDENT);
        localToParent(result);
        return result;
    }

    /**
     * This is a crucial step. We have to override this method to invalidate the
     * paint each time the bounds are changed so we repaint the correct region
     */
    public boolean validateFullBounds() {
        comparisonBounds = getUnionOfChildrenBounds(comparisonBounds);

        if (!cachedChildBounds.equals(comparisonBounds)) {
            setPaintInvalid(true);
        }
        return super.validateFullBounds();
    }
}

/**
 * This is the volatile implementation of a decorator group that paints a
 * background rectangle based on the bounds of its children.
 */
class VolatileDecoratorGroup extends PNode {
    int INDENT = 10;

    PBounds cachedChildBounds = new PBounds();
    PBounds comparisonBounds = new PBounds();
    PCamera renderCamera;

    public VolatileDecoratorGroup(PCamera camera) {
        super();
        renderCamera = camera;
    }

    /**
     * Indicate that the bounds are volatile for this group
     */
    public boolean getBoundsVolatile() {
        return true;
    }

    /**
     * Since our bounds are volatile, we can override this method to indicate
     * that we are expanding our bounds beyond our children
     */
    public PBounds getBoundsReference() {
        PBounds bds = super.getBoundsReference();
        getUnionOfChildrenBounds(bds);

        cachedChildBounds.setRect(bds);
        double scaledIndent = INDENT / renderCamera.getViewScale();
        bds.setRect(bds.getX() - scaledIndent, bds.getY() - scaledIndent, bds.getWidth() + 2 * scaledIndent, bds
                .getHeight()
                + 2 * scaledIndent);

        return bds;
    }

    /**
     * This is a crucial step. We have to override this method to invalidate the
     * paint each time the bounds are changed so we repaint the correct region
     */
    public boolean validateFullBounds() {
        comparisonBounds = getUnionOfChildrenBounds(comparisonBounds);

        if (!cachedChildBounds.equals(comparisonBounds)) {
            setPaintInvalid(true);
        }
        return super.validateFullBounds();
    }
}
