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
package edu.umd.cs.piccolo;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import junit.framework.TestCase;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

public class PNodeTest extends TestCase {

    public PNodeTest(String name) {
        super(name);
    }

    public void setUp() {
    }

    public void testCenterBaseBoundsOnPoint() {
        PNode aNode = new PNode();

        aNode.setBounds(100, 300, 100, 80);
        aNode.centerBoundsOnPoint(0, 0);
        assertEquals(-50, aNode.getBoundsReference().getX(), 0);
        assertEquals(-40, aNode.getBoundsReference().getY(), 0);
    }

    public void testClientProperties() {
        PNode n = new PNode();

        assertNull(n.getAttribute(null));
        n.addAttribute("a", "b");
        assertEquals(n.getAttribute("a"), "b");
        assertNull(n.getAttribute(null));
        n.addAttribute("a", null);
        assertNull(n.getAttribute("a"));
    }

    public void testFullScale() {
        PNode aParent = new PNode();
        PNode aNode = new PNode();

        aParent.addChild(aNode);

        aParent.scale(2.0);
        aNode.scale(0.5);

        assertEquals(1.0, aNode.getGlobalScale(), 0);

        aParent.setScale(1.0);
        assertEquals(0.5, aNode.getGlobalScale(), 0);

        aNode.setScale(.75);
        assertEquals(0.75, aNode.getGlobalScale(), 0);
    }

    public void testReparent() {
        PNode aParent = new PNode();
        PNode aNode = new PNode();

        aParent.setOffset(400, 500);
        aParent.scale(0.5);
        aNode.reparent(aParent);

        assertEquals(0, aNode.getGlobalTranslation().getX(), 0);
        assertEquals(0, aNode.getGlobalTranslation().getY(), 0);
        assertEquals(2.0, aNode.getScale(), 0);

        aNode.setGlobalScale(0.25);
        aNode.setGlobalTranslation(new Point2D.Double(10, 10));

        assertEquals(10, aNode.getGlobalTranslation().getX(), 0);
        assertEquals(10, aNode.getGlobalTranslation().getY(), 0);
        assertEquals(0.25, aNode.getGlobalScale(), 0);
    }

    public void testFindIntersectingNodes() {
        PNode n = new PNode();
        PNode c = new PNode();

        n.addChild(c);
        n.setBounds(0, 0, 100, 100);
        c.setBounds(0, 0, 100, 100);
        c.scale(200);

        ArrayList found = new ArrayList();
        Rectangle2D rect2d = new Rectangle2D.Double(50, 50, 10, 10);
        n.findIntersectingNodes(rect2d, found);

        assertEquals(found.size(), 2);
        assertEquals(rect2d.getHeight(), 10, 0);
        found = new ArrayList();

        PBounds bounds = new PBounds(50, 50, 10, 10);
        n.findIntersectingNodes(bounds, found);

        assertEquals(found.size(), 2);
        assertEquals(bounds.getHeight(), 10, 0);
    }

    public void testRemoveNonexistantListener() {
        PNode n = new PNode();
        n.removeInputEventListener(new PBasicInputEventHandler());
    }

    public void testAddChild() {
        PNode p = new PNode();
        PNode c = new PNode();

        p.addChild(c);
        p.addChild(new PNode());
        p.addChild(new PNode());

        p.addChild(c);
        assertEquals(c, p.getChild(2));

        p.addChild(0, c);
        assertEquals(c, p.getChild(0));

        p.addChild(1, c);
        assertEquals(c, p.getChild(1));

        p.addChild(2, c);
        assertEquals(c, p.getChild(2));
    }

    public void testCopy() {
        PNode aNode = new PNode();
        aNode.setPaint(Color.yellow);

        PNode aChild = new PNode();
        aNode.addChild(aChild);

        aNode = (PNode) aNode.clone();

        assertEquals(aNode.getPaint(), Color.yellow);
        assertEquals(aNode.getChildrenCount(), 1);
    }

    public void testLocalToGlobal() {
        PNode aParent = new PNode();
        PNode aChild = new PNode();

        aParent.addChild(aChild);
        aChild.scale(0.5);

        // bounds
        PBounds bnds = new PBounds(0, 0, 50, 50);

        aChild.localToGlobal(bnds);
        assertEquals(0, bnds.x, 0);
        assertEquals(0, bnds.y, 0);
        assertEquals(25, bnds.width, 0);
        assertEquals(25, bnds.height, 0);

        aChild.globalToLocal(bnds);
        assertEquals(0, bnds.x, 0);
        assertEquals(0, bnds.y, 0);
        assertEquals(50, bnds.width, 0);
        assertEquals(50, bnds.height, 0);

        aChild.getGlobalToLocalTransform(new PAffineTransform());
        aChild.getLocalToGlobalTransform(new PAffineTransform()).createTransformedShape(aChild.getBounds());

        // dimensions
        PDimension dim = new PDimension(50, 50);

        aChild.localToGlobal(dim);
        assertEquals(25, dim.getHeight(), 0);
        assertEquals(25, dim.getWidth(), 0);

        aChild.globalToLocal(dim);
        assertEquals(50, dim.getHeight(), 0);
        assertEquals(50, dim.getWidth(), 0);
    }

    public void testToString() {
        PNode a = new PNode();
        PNode b = new PNode();
        PNode c = new PNode();
        PNode d = new PNode();
        PNode e = new PNode();
        PNode f = new PNode();

        a.translate(100, 100);
        a.getFullBoundsReference();

        a.addChild(b);
        b.addChild(c);
        c.addChild(d);
        d.addChild(e);
        e.addChild(f);

        assertNotNull(a.toString());
    }

    public void testRecursiveLayout() {
        PNode layoutNode1 = new PNode() {
            protected void layoutChildren() {
                if (getChildrenCount() > 0) {
                    getChild(0).setOffset(1, 0);
                }
            }
        };

        PNode layoutNode2 = new PNode() {
            protected void layoutChildren() {
                if (getChildrenCount() > 0) {
                    getChild(0).setOffset(1, 0);
                }
            }
        };

        layoutNode1.addChild(layoutNode2);

        PNode n = new PNode();
        n.setBounds(0, 0, 100, 100);

        layoutNode2.addChild(n);

        n.setBounds(10, 10, 100, 100);

        layoutNode1.getFullBoundsReference();
    }
}
