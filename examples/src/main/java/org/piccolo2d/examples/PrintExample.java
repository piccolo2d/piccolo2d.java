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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.piccolo2d.PCanvas;
import org.piccolo2d.PLayer;
import org.piccolo2d.extras.PFrame;
import org.piccolo2d.extras.swing.PDefaultScrollDirector;
import org.piccolo2d.extras.swing.PScrollDirector;
import org.piccolo2d.extras.swing.PScrollPane;
import org.piccolo2d.extras.swing.PViewport;
import org.piccolo2d.nodes.PPath;
import org.piccolo2d.util.PAffineTransform;
import org.piccolo2d.util.PBounds;


/**
 * Adding print action to scrolling example.
 * 
 * @author Lance Good
 * @author Ben Bederson
 */
public class PrintExample extends PFrame {

    private static final long serialVersionUID = 1L;

    public PrintExample() {
        this(null);
    }

    public PrintExample(final PCanvas aCanvas) {
        super("ScrollingExample", false, aCanvas);
    }

    public void initialize() {
        final PCanvas canvas = getCanvas();
        final PScrollPane scrollPane = new PScrollPane(canvas);
        final PViewport viewport = (PViewport) scrollPane.getViewport();
        final PScrollDirector windowSD = viewport.getScrollDirector();
        final PScrollDirector documentSD = new DocumentScrollDirector();

        addBackgroundShapes(canvas);

        // Now, create the toolbar
        final JToolBar toolBar = new JToolBar();
        final JToggleButton window = new JToggleButton("Window Scrolling");
        final JToggleButton document = new JToggleButton("Document Scrolling");
        final JButton print = new JButton("Print");
        final ButtonGroup bg = new ButtonGroup();
        bg.add(window);
        bg.add(document);
        toolBar.add(window);
        toolBar.add(document);
        toolBar.addSeparator();
        toolBar.add(print);
        toolBar.setFloatable(false);
        window.setSelected(true);
        window.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent ae) {
                viewport.setScrollDirector(windowSD);
                viewport.fireStateChanged();
                scrollPane.revalidate();
                getContentPane().validate();
            }
        });
        document.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent ae) {
                viewport.setScrollDirector(documentSD);
                viewport.fireStateChanged();
                scrollPane.revalidate();
                getContentPane().validate();
            }
        });
        print.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent ae) {
                try {
                    print();
                }
                catch (final PrinterException e) {
                    JOptionPane.showMessageDialog(PrintExample.this, "An error occured while printing");
                }
            }
        });
        final JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add("Center", scrollPane);
        contentPane.add("North", toolBar);
        setContentPane(contentPane);
        validate();
    }

    private void addBackgroundShapes(final PCanvas canvas) {
        for (int shapeCount = 0; shapeCount < 440; shapeCount++) {
            int x = shapeCount % 21;
            int y = (shapeCount - x) / 21;

            if (shapeCount % 2 == 0) {
                final PPath path = PPath.createRectangle(50 * x, 50 * y, 40, 40);
                path.setPaint(Color.blue);
                path.setStrokePaint(Color.black);
                canvas.getLayer().addChild(path);
            }
            else if (shapeCount % 2 == 1) {
                final PPath path = PPath.createEllipse(50 * x, 50 * y, 40, 40);
                path.setPaint(Color.blue);
                path.setStrokePaint(Color.black);
                canvas.getLayer().addChild(path);
            }

        }
    }

    /**
     * A modified scroll director that performs document based scroling rather
     * than window based scrolling (ie. the scrollbars act in the inverse
     * direction as normal)
     */
    public class DocumentScrollDirector extends PDefaultScrollDirector {

        /**
         * Get the View position given the specified camera bounds - modified
         * such that:
         * 
         * Rather than finding the distance from the upper left corner of the
         * window to the upper left corner of the document - we instead find the
         * distance from the lower right corner of the window to the upper left
         * corner of the document THEN we subtract that value from total
         * document width so that the position is inverted
         * 
         * @param viewBounds The bounds for which the view position will be
         *            computed
         * @return The view position
         */
        public Point getViewPosition(final Rectangle2D viewBounds) {
            final Point pos = new Point();
            if (camera != null) {
                // First we compute the union of all the layers
                final PBounds layerBounds = new PBounds();
                final java.util.List layers = camera.getLayersReference();
                for (final Iterator i = layers.iterator(); i.hasNext();) {
                    final PLayer layer = (PLayer) i.next();
                    layerBounds.add(layer.getFullBoundsReference());
                }

                // Then we put the bounds into camera coordinates and
                // union the camera bounds
                camera.viewToLocal(layerBounds);
                layerBounds.add(viewBounds);

                // Rather than finding the distance from the upper left corner
                // of the window to the upper left corner of the document -
                // we instead find the distance from the lower right corner
                // of the window to the upper left corner of the document
                // THEN we measure the offset from the lower right corner
                // of the document
                pos.setLocation((int) (layerBounds.getWidth()
                        - (viewBounds.getX() + viewBounds.getWidth() - layerBounds.getX()) + 0.5), (int) (layerBounds
                        .getHeight()
                        - (viewBounds.getY() + viewBounds.getHeight() - layerBounds.getY()) + 0.5));
            }

            return pos;

        }

        /**
         * We do the same thing we did in getViewPosition above to flip the
         * document-window position relationship
         * 
         * @param x The new x position
         * @param y The new y position
         */
        public void setViewPosition(final double x, final double y) {
            if (camera == null)
                return;

            // If a scroll is in progress - we ignore new scrolls - if we
            // didn't, since the scrollbars depend on the camera location
            // we can end up with an infinite loop
            if (scrollInProgress)
                return;

            scrollInProgress = true;

            // Get the union of all the layers' bounds
            final PBounds layerBounds = new PBounds();
            final List layers = camera.getLayersReference();
            for (final Iterator i = layers.iterator(); i.hasNext();) {
                final PLayer layer = (PLayer) i.next();
                layerBounds.add(layer.getFullBoundsReference());
            }

            final PAffineTransform at = camera.getViewTransform();
            at.transform(layerBounds, layerBounds);

            // Union the camera view bounds
            final PBounds viewBounds = camera.getBoundsReference();
            layerBounds.add(viewBounds);

            // Now find the new view position in view coordinates -
            // This is basically the distance from the lower right
            // corner of the window to the upper left corner of the
            // document
            // We then measure the offset from the lower right corner
            // of the document
            final Point2D newPoint = new Point2D.Double(layerBounds.getX() + layerBounds.getWidth()
                    - (x + viewBounds.getWidth()), layerBounds.getY() + layerBounds.getHeight()
                    - (y + viewBounds.getHeight()));

            // Now transform the new view position into global coords
            camera.localToView(newPoint);

            // Compute the new matrix values to put the camera at the
            // correct location
            final double newX = -(at.getScaleX() * newPoint.getX() + at.getShearX() * newPoint.getY());
            final double newY = -(at.getShearY() * newPoint.getX() + at.getScaleY() * newPoint.getY());

            at.setTransform(at.getScaleX(), at.getShearY(), at.getShearX(), at.getScaleY(), newX, newY);

            // Now actually set the camera's transform
            camera.setViewTransform(at);
            scrollInProgress = false;
        }
    }

    /**
     * Print the canvas.
     * 
     * @throws PrinterException
     */
    private void print() throws PrinterException {
        final PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(new Printable() {
            public int print(final Graphics graphics, final PageFormat pageFormat, final int pageIndex)
                    throws PrinterException {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }
                else {
                    final Graphics2D g2 = (Graphics2D) graphics;
                    g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                    getCanvas().printAll(g2);
                    return PAGE_EXISTS;
                }
            }
        });
        if (printJob.printDialog()) {
            printJob.print();
        }
    }

    public static void main(final String[] args) {
        new PrintExample();
    }
}
