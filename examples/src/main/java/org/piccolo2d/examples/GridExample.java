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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.piccolo2d.PCamera;
import org.piccolo2d.PCanvas;
import org.piccolo2d.PLayer;
import org.piccolo2d.PNode;
import org.piccolo2d.PRoot;
import org.piccolo2d.event.PDragSequenceEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.extras.PFrame;
import org.piccolo2d.util.PPaintContext;


/**
 * Example of drawing an infinite grid, and providing support for snap to grid.
 */
public class GridExample extends PFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected Line2D gridLine = new Line2D.Double();
    protected Stroke gridStroke = new BasicStroke(1);
    protected Color gridPaint = Color.BLACK;
    protected double gridSpacing = 20;

    public GridExample() {
        this(null);
    }

    public GridExample(final PCanvas aCanvas) {
        super("GridExample", false, aCanvas);
    }

    public void initialize() {
        final PRoot root = getCanvas().getRoot();
        final PCamera camera = getCanvas().getCamera();
        final PLayer gridLayer = new PLayer() {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            protected void paint(final PPaintContext paintContext) {
                // make sure grid gets drawn on snap to grid boundaries. And
                // expand a little to make sure that entire view is filled.
                final double bx = getX() - getX() % gridSpacing - gridSpacing;
                final double by = getY() - getY() % gridSpacing - gridSpacing;
                final double rightBorder = getX() + getWidth() + gridSpacing;
                final double bottomBorder = getY() + getHeight() + gridSpacing;

                final Graphics2D g2 = paintContext.getGraphics();
                final Rectangle2D clip = paintContext.getLocalClip();

                g2.setStroke(gridStroke);
                g2.setPaint(gridPaint);

                for (double x = bx; x < rightBorder; x += gridSpacing) {
                    gridLine.setLine(x, by, x, bottomBorder);
                    if (clip.intersectsLine(gridLine)) {
                        g2.draw(gridLine);
                    }
                }

                for (double y = by; y < bottomBorder; y += gridSpacing) {
                    gridLine.setLine(bx, y, rightBorder, y);
                    if (clip.intersectsLine(gridLine)) {
                        g2.draw(gridLine);
                    }
                }
            }
        };

        // replace standar layer with grid layer.
        root.removeChild(camera.getLayer(0));
        camera.removeLayer(0);
        root.addChild(gridLayer);
        camera.addLayer(gridLayer);

        // add constrains so that grid layers bounds always match cameras view
        // bounds. This makes it look like an infinite grid.
        camera.addPropertyChangeListener(PNode.PROPERTY_BOUNDS, new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent evt) {
                gridLayer.setBounds(camera.getViewBounds());
            }
        });

        camera.addPropertyChangeListener(PCamera.PROPERTY_VIEW_TRANSFORM, new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent evt) {
                gridLayer.setBounds(camera.getViewBounds());
            }
        });

        gridLayer.setBounds(camera.getViewBounds());

        final PNode n = new PNode();
        n.setPaint(Color.BLUE);
        n.setBounds(0, 0, 100, 80);

        getCanvas().getLayer().addChild(n);
        getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());

        // add a drag event handler that supports snap to grid.
        getCanvas().addInputEventListener(new PDragSequenceEventHandler() {

            protected PNode draggedNode;
            protected Point2D nodeStartPosition;

            protected boolean shouldStartDragInteraction(final PInputEvent event) {
                if (super.shouldStartDragInteraction(event)) {
                    return event.getPickedNode() != event.getTopCamera() && !(event.getPickedNode() instanceof PLayer);
                }
                return false;
            }

            protected void startDrag(final PInputEvent event) {
                super.startDrag(event);
                draggedNode = event.getPickedNode();
                draggedNode.raiseToTop();
                nodeStartPosition = draggedNode.getOffset();
            }

            protected void drag(final PInputEvent event) {
                super.drag(event);

                final Point2D start = getCanvas().getCamera().localToView(
                        (Point2D) getMousePressedCanvasPoint().clone());
                final Point2D current = event.getPositionRelativeTo(getCanvas().getLayer());
                final Point2D dest = new Point2D.Double();

                dest.setLocation(nodeStartPosition.getX() + current.getX() - start.getX(), nodeStartPosition.getY()
                        + current.getY() - start.getY());

                dest.setLocation(dest.getX() - dest.getX() % gridSpacing, dest.getY() - dest.getY() % gridSpacing);

                draggedNode.setOffset(dest.getX(), dest.getY());
            }
        });
    }

    public static void main(final String[] args) {
        new GridExample();
    }
}
