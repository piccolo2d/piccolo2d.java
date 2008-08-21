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
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;

/**
 * Create a simple graph with some random nodes and connected edges. An event
 * handler allows users to drag nodes around, keeping the edges connected.
 * 
 * ported from .NET GraphEditorExample by Sun Hongmei.
 */
public class GraphEditorExample extends PFrame {

    public GraphEditorExample() {
        this(null);
    }

    public GraphEditorExample(PCanvas aCanvas) {
        super("GraphEditorExample", false, aCanvas);
    }

    public void initialize() {
        int numNodes = 50;
        int numEdges = 50;

        // Initialize, and create a layer for the edges (always underneath the
        // nodes)
        PLayer nodeLayer = getCanvas().getLayer();
        PLayer edgeLayer = new PLayer();
        getCanvas().getCamera().addLayer(0, edgeLayer);
        Random rnd = new Random();
        ArrayList tmp;
        for (int i = 0; i < numNodes; i++) {
            float x = (float) (300. * rnd.nextDouble());
            float y = (float) (400. * rnd.nextDouble());
            PPath path = PPath.createEllipse(x, y, 20, 20);
            tmp = new ArrayList();
            path.addAttribute("edges", tmp);
            nodeLayer.addChild(path);
        }

        // Create some random edges
        // Each edge's Tag has an ArrayList used to store associated nodes
        for (int i = 0; i < numEdges; i++) {
            int n1 = rnd.nextInt(numNodes);
            int n2 = rnd.nextInt(numNodes);
            PNode node1 = nodeLayer.getChild(n1);
            PNode node2 = nodeLayer.getChild(n2);

            Point2D.Double bound1 = (Point2D.Double) node1.getBounds().getCenter2D();
            Point2D.Double bound2 = (Point2D.Double) node2.getBounds().getCenter2D();

            PPath edge = new PPath();
            edge.moveTo((float) bound1.getX(), (float) bound1.getY());
            edge.lineTo((float) bound2.getX(), (float) bound2.getY());

            tmp = (ArrayList) node1.getAttribute("edges");
            tmp.add(edge);
            tmp = (ArrayList) node2.getAttribute("edges");
            tmp.add(edge);

            tmp = new ArrayList();
            tmp.add(node1);
            tmp.add(node2);
            edge.addAttribute("nodes", tmp);

            edgeLayer.addChild(edge);
        }

        // Create event handler to move nodes and update edges
        nodeLayer.addInputEventListener(new NodeDragHandler());
    }

    public static void main(String[] args) {
        new GraphEditorExample();
    }

    // TODO eclipse formatter made this ugly
    // / <summary>
    // / Simple event handler which applies the following actions to every node
    // it is called on:
    // / * Turn node red when the mouse goes over the node
    // / * Turn node white when the mouse exits the node
    // / * Drag the node, and associated edges on mousedrag
    // / It assumes that the node's Tag references an ArrayList with a list of
    // associated
    // / edges where each edge is a PPath which each have a Tag that references
    // an ArrayList
    // / with a list of associated nodes.
    // / </summary>
    class NodeDragHandler extends PDragSequenceEventHandler {
        public NodeDragHandler() {
            getEventFilter().setMarksAcceptedEventsAsHandled(true);
        }

        public void mouseEntered(PInputEvent e) {
            if (e.getButton() == 0) {
                e.getPickedNode().setPaint(Color.red);
            }
        }

        public void mouseExited(PInputEvent e) {
            if (e.getButton() == 0) {
                e.getPickedNode().setPaint(Color.white);
            }
        }

        public void drag(PInputEvent e) {
            PNode node = e.getPickedNode();
            node.translate(e.getDelta().width, e.getDelta().height);

            ArrayList edges = (ArrayList) e.getPickedNode().getAttribute("edges");

            int i;
            for (i = 0; i < edges.size(); i++) {
                PPath edge = (PPath) edges.get(i);
                ArrayList nodes = (ArrayList) edge.getAttribute("nodes");
                PNode node1 = (PNode) nodes.get(0);
                PNode node2 = (PNode) nodes.get(1);

                edge.reset();
                // Note that the node's "FullBounds" must be used (instead of
                // just the "Bound") because the nodes have non-identity
                // transforms which must be included when determining their
                // position.
                Point2D.Double bound1 = (Point2D.Double) node1.getFullBounds().getCenter2D();
                Point2D.Double bound2 = (Point2D.Double) node2.getFullBounds().getCenter2D();

                edge.moveTo((float) bound1.getX(), (float) bound1.getY());
                edge.lineTo((float) bound2.getX(), (float) bound2.getY());
            }
        }
    }
}
