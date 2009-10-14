/*
 * Copyright (c) 2008-2009, Piccolo2D project, http://piccolo2d.org
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
package edu.umd.cs.piccolox.swt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.awt.geom.Rectangle2D;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PComponent;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.event.PPanEventHandler;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PStack;

/**
 * <b>PSWTCanvas</b> is a simple Swing component that can be used to embed Piccolo
 * into a Java Swing application. Canvas's view the Piccolo scene graph through
 * a camera. The canvas manages screen updates coming from this camera, and
 * forwards swing mouse and keyboard events to the camera.
 * <P>
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PSWTCanvas extends Composite implements PComponent {

    public static PSWTCanvas CURRENT_CANVAS = null;

    private Image backBuffer;
    private boolean doubleBuffered = true;

    private PCamera camera;
    private final PStack cursorStack;
    private Cursor curCursor;
    private int interacting;
    private int defaultRenderQuality;
    private int animatingRenderQuality;
    private int interactingRenderQuality;
    private final PPanEventHandler panEventHandler;
    private final PZoomEventHandler zoomEventHandler;
    private boolean paintingImmediately;
    private boolean animatingOnLastPaint;

    /**
     * Construct a canvas with the basic scene graph consisting of a root,
     * camera, and layer. Event handlers for zooming and panning are
     * automatically installed.
     */
    public PSWTCanvas(final Composite parent, final int style) {
        super(parent, style | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE);

        CURRENT_CANVAS = this;
        cursorStack = new PStack();
        setCamera(createBasicSceneGraph());
        installInputSources();
        setDefaultRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
        setAnimatingRenderQuality(PPaintContext.LOW_QUALITY_RENDERING);
        setInteractingRenderQuality(PPaintContext.LOW_QUALITY_RENDERING);
        panEventHandler = new PPanEventHandler();
        zoomEventHandler = new PZoomEventHandler();
        addInputEventListener(panEventHandler);
        addInputEventListener(zoomEventHandler);

        // Add a paint listener to call paint
        addPaintListener(new PaintListener() {
            public void paintControl(final PaintEvent pe) {
                paintComponent(pe.gc, pe.x, pe.y, pe.width, pe.height);
            }
        });

        // Keep track of the references so we can dispose of the Fonts and
        // Colors
        SWTGraphics2D.incrementGCCount();
        addDisposeListener(new DisposeListener() {
            public void widgetDisposed(final DisposeEvent de) {
                getRoot().getActivityScheduler().removeAllActivities();
                SWTGraphics2D.decrementGCCount();
            }
        });
    }

    // ****************************************************************
    // Basic - Methods for accessing common piccolo nodes.
    // ****************************************************************

    /**
     * Get the pan event handler associated with this canvas. This event handler
     * is set up to get events from the camera associated with this canvas by
     * default.
     */
    public PPanEventHandler getPanEventHandler() {
        return panEventHandler;
    }

    /**
     * Get the zoom event handler associated with this canvas. This event
     * handler is set up to get events from the camera associated with this
     * canvas by default.
     */
    public PZoomEventHandler getZoomEventHandler() {
        return zoomEventHandler;
    }

    /**
     * Return the camera associated with this canvas. All input events from this
     * canvas go through this camera. And this is the camera that paints this
     * canvas.
     */
    public PCamera getCamera() {
        return camera;
    }

    /**
     * Set the camera associated with this canvas. All input events from this
     * canvas go through this camera. And this is the camera that paints this
     * canvas.
     */
    public void setCamera(final PCamera newCamera) {
        if (camera != null) {
            camera.setComponent(null);
        }

        camera = newCamera;

        if (camera != null) {
            camera.setComponent(this);

            final Rectangle swtRect = getBounds();

            camera.setBounds(new Rectangle2D.Double(swtRect.x, swtRect.y, swtRect.width, swtRect.height));
        }
    }

    /**
     * Return root for this canvas.
     */
    public PRoot getRoot() {
        return camera.getRoot();
    }

    /**
     * Return layer for this canvas.
     */
    public PLayer getLayer() {
        return camera.getLayer(0);
    }

    /**
     * Add an input listener to the camera associated with this canvas.
     */
    public void addInputEventListener(final PInputEventListener listener) {
        getCamera().addInputEventListener(listener);
    }

    /**
     * Remove an input listener to the camera associated with this canvas.
     */
    public void removeInputEventListener(final PInputEventListener listener) {
        getCamera().removeInputEventListener(listener);
    }

    public PCamera createBasicSceneGraph() {
        final PRoot r = new PSWTRoot(this);
        final PLayer l = new PLayer();
        final PCamera c = new PCamera();

        r.addChild(c);
        r.addChild(l);
        c.addLayer(l);

        return c;
    }

    // ****************************************************************
    // Painting
    // ****************************************************************

    /**
     * Return true if this canvas has been marked as interacting. If so the
     * canvas will normally render at a lower quality that is faster.
     */
    public boolean getInteracting() {
        return interacting > 0;
    }

    /**
     * Return true if any activities that respond with true to the method
     * isAnimating were run in the last PRoot.processInputs() loop. This values
     * is used by this canvas to determine the render quality to use for the
     * next paint.
     */
    public boolean getAnimating() {
        return getRoot().getActivityScheduler().getAnimating();
    }

    /**
     * Set if this canvas is interacting. If so the canvas will normally render
     * at a lower quality that is faster.
     */
    public void setInteracting(final boolean isInteracting) {
        if (isInteracting) {
            interacting++;
        }
        else {
            interacting--;
        }

        if (!getInteracting()) {
            repaint();
        }
    }

    /**
     * Get whether this canvas should use double buffering - the default is no
     * double buffering
     */
    public boolean getDoubleBuffered() {
        return doubleBuffered;
    }

    /**
     * Set whether this canvas should use double buffering - the default is no
     * double buffering
     */
    public void setDoubleBuffered(final boolean dBuffered) {
        doubleBuffered = dBuffered;
    }

    /**
     * Set the render quality that should be used when rendering this canvas.
     * The default value is PPaintContext.HIGH_QUALITY_RENDERING.
     * 
     * @param requestedQuality supports PPaintContext.HIGH_QUALITY_RENDERING or
     *            PPaintContext.LOW_QUALITY_RENDERING
     */
    public void setDefaultRenderQuality(final int requestedQuality) {
        defaultRenderQuality = requestedQuality;
        repaint();
    }

    /**
     * Set the render quality that should be used when rendering this canvas
     * when it is animating. The default value is
     * PPaintContext.LOW_QUALITY_RENDERING.
     * 
     * @param requestedQuality supports PPaintContext.HIGH_QUALITY_RENDERING or
     *            PPaintContext.LOW_QUALITY_RENDERING
     */
    public void setAnimatingRenderQuality(final int requestedQuality) {
        animatingRenderQuality = requestedQuality;
        repaint();
    }

    /**
     * Set the render quality that should be used when rendering this canvas
     * when it is interacting. The default value is
     * PPaintContext.LOW_QUALITY_RENDERING.
     * 
     * @param requestedQuality supports PPaintContext.HIGH_QUALITY_RENDERING or
     *            PPaintContext.LOW_QUALITY_RENDERING
     */
    public void setInteractingRenderQuality(final int requestedQuality) {
        interactingRenderQuality = requestedQuality;
        repaint();
    }

    /**
     * Set the canvas cursor, and remember the previous cursor on the cursor
     * stack.
     */
    public void pushCursor(final java.awt.Cursor cursor) {
        Cursor aCursor = null;
        if (cursor.getType() == java.awt.Cursor.N_RESIZE_CURSOR) {
            aCursor = new Cursor(getDisplay(), SWT.CURSOR_SIZEN);
        }
        else if (cursor.getType() == java.awt.Cursor.NE_RESIZE_CURSOR) {
            aCursor = new Cursor(getDisplay(), SWT.CURSOR_SIZENE);
        }
        else if (cursor.getType() == java.awt.Cursor.NW_RESIZE_CURSOR) {
            aCursor = new Cursor(getDisplay(), SWT.CURSOR_SIZENW);
        }
        else if (cursor.getType() == java.awt.Cursor.S_RESIZE_CURSOR) {
            aCursor = new Cursor(getDisplay(), SWT.CURSOR_SIZES);
        }
        else if (cursor.getType() == java.awt.Cursor.SE_RESIZE_CURSOR) {
            aCursor = new Cursor(getDisplay(), SWT.CURSOR_SIZESE);
        }
        else if (cursor.getType() == java.awt.Cursor.SW_RESIZE_CURSOR) {
            aCursor = new Cursor(getDisplay(), SWT.CURSOR_SIZESW);
        }
        else if (cursor.getType() == java.awt.Cursor.E_RESIZE_CURSOR) {
            aCursor = new Cursor(getDisplay(), SWT.CURSOR_SIZEE);
        }
        else if (cursor.getType() == java.awt.Cursor.W_RESIZE_CURSOR) {
            aCursor = new Cursor(getDisplay(), SWT.CURSOR_SIZEW);
        }
        else if (cursor.getType() == java.awt.Cursor.TEXT_CURSOR) {
            aCursor = new Cursor(getDisplay(), SWT.CURSOR_IBEAM);
        }
        else if (cursor.getType() == java.awt.Cursor.HAND_CURSOR) {
            aCursor = new Cursor(getDisplay(), SWT.CURSOR_HAND);
        }
        else if (cursor.getType() == java.awt.Cursor.MOVE_CURSOR) {
            aCursor = new Cursor(getDisplay(), SWT.CURSOR_SIZEALL);
        }
        else if (cursor.getType() == java.awt.Cursor.CROSSHAIR_CURSOR) {
            aCursor = new Cursor(getDisplay(), SWT.CURSOR_CROSS);
        }
        else if (cursor.getType() == java.awt.Cursor.WAIT_CURSOR) {
            aCursor = new Cursor(getDisplay(), SWT.CURSOR_WAIT);
        }

        if (aCursor != null) {
            if (curCursor != null) {
                cursorStack.push(curCursor);
            }
            curCursor = aCursor;
            setCursor(aCursor);
        }
    }

    /**
     * Pop the cursor on top of the cursorStack and set it as the canvas cursor.
     */
    public void popCursor() {
        if (curCursor != null) {
            // We must manually dispose of cursors under SWT
            curCursor.dispose();
        }

        if (!cursorStack.isEmpty()) {
            curCursor = (Cursor) cursorStack.pop();
        }
        else {
            curCursor = null;
        }

        // This sets the cursor back to default
        setCursor(curCursor);
    }

    // ****************************************************************
    // Code to manage connection to Swing. There appears to be a bug in
    // swing where it will occasionally send to many mouse pressed or mouse
    // released events. Below we attempt to filter out those cases before
    // they get delivered to the Piccolo framework.
    // ****************************************************************

    private boolean isButton1Pressed;
    private boolean isButton2Pressed;
    private boolean isButton3Pressed;

    /**
     * This method installs mouse and key listeners on the canvas that forward
     * those events to piccolo.
     */
    protected void installInputSources() {
        addMouseListener(new MouseListener() {
            public void mouseDown(final MouseEvent me) {
                boolean shouldBalanceEvent = false;

                switch (me.button) {
                    case 1:
                        if (isButton1Pressed) {
                            shouldBalanceEvent = true;
                        }
                        isButton1Pressed = true;
                        break;
                    case 2:
                        if (isButton2Pressed) {
                            shouldBalanceEvent = true;
                        }
                        isButton2Pressed = true;
                        break;
                    case 3:
                        if (isButton3Pressed) {
                            shouldBalanceEvent = true;
                        }
                        isButton3Pressed = true;
                        break;
                }

                if (shouldBalanceEvent) {
                    final java.awt.event.MouseEvent balanceEvent = new PSWTMouseEvent(me,
                            java.awt.event.MouseEvent.MOUSE_RELEASED, 1);
                    sendInputEventToInputManager(balanceEvent, java.awt.event.MouseEvent.MOUSE_RELEASED);
                }

                final java.awt.event.MouseEvent balanceEvent = new PSWTMouseEvent(me,
                        java.awt.event.MouseEvent.MOUSE_PRESSED, 1);
                sendInputEventToInputManager(balanceEvent, java.awt.event.MouseEvent.MOUSE_PRESSED);
            }

            public void mouseUp(final MouseEvent me) {
                boolean shouldBalanceEvent = false;

                switch (me.button) {
                    case 1:
                        if (!isButton1Pressed) {
                            shouldBalanceEvent = true;
                        }
                        isButton1Pressed = false;
                        break;
                    case 2:
                        if (!isButton2Pressed) {
                            shouldBalanceEvent = true;
                        }
                        isButton2Pressed = false;
                        break;
                    case 3:
                        if (!isButton3Pressed) {
                            shouldBalanceEvent = true;
                        }
                        isButton3Pressed = false;
                        break;
                }

                if (shouldBalanceEvent) {
                    final java.awt.event.MouseEvent balanceEvent = new PSWTMouseEvent(me,
                            java.awt.event.MouseEvent.MOUSE_PRESSED, 1);
                    sendInputEventToInputManager(balanceEvent, java.awt.event.MouseEvent.MOUSE_PRESSED);
                }

                final java.awt.event.MouseEvent balanceEvent = new PSWTMouseEvent(me,
                        java.awt.event.MouseEvent.MOUSE_RELEASED, 1);
                sendInputEventToInputManager(balanceEvent, java.awt.event.MouseEvent.MOUSE_RELEASED);
            }

            public void mouseDoubleClick(final MouseEvent me) {
                // This doesn't work with click event types for some reason - it
                // has to do with how the click and release events are ordered,
                // I think
                java.awt.event.MouseEvent inputEvent = new PSWTMouseEvent(me, java.awt.event.MouseEvent.MOUSE_PRESSED,
                        2);
                sendInputEventToInputManager(inputEvent, java.awt.event.MouseEvent.MOUSE_PRESSED);
                inputEvent = new PSWTMouseEvent(me, java.awt.event.MouseEvent.MOUSE_RELEASED, 2);
                sendInputEventToInputManager(inputEvent, java.awt.event.MouseEvent.MOUSE_RELEASED);
            }
        });

        addMouseMoveListener(new MouseMoveListener() {
            public void mouseMove(final MouseEvent me) {
                if (isButton1Pressed || isButton2Pressed || isButton3Pressed) {
                    final java.awt.event.MouseEvent inputEvent = new PSWTMouseEvent(me,
                            java.awt.event.MouseEvent.MOUSE_DRAGGED, 1);
                    sendInputEventToInputManager(inputEvent, java.awt.event.MouseEvent.MOUSE_DRAGGED);
                }
                else {
                    final java.awt.event.MouseEvent inputEvent = new PSWTMouseEvent(me,
                            java.awt.event.MouseEvent.MOUSE_MOVED, 1);
                    sendInputEventToInputManager(inputEvent, java.awt.event.MouseEvent.MOUSE_MOVED);
                }
            }
        });

        addKeyListener(new KeyListener() {
            public void keyPressed(final KeyEvent ke) {
                final java.awt.event.KeyEvent inputEvent = new PSWTKeyEvent(ke, java.awt.event.KeyEvent.KEY_PRESSED);
                sendInputEventToInputManager(inputEvent, java.awt.event.KeyEvent.KEY_PRESSED);
            }

            public void keyReleased(final KeyEvent ke) {
                final java.awt.event.KeyEvent inputEvent = new PSWTKeyEvent(ke, java.awt.event.KeyEvent.KEY_RELEASED);
                sendInputEventToInputManager(inputEvent, java.awt.event.KeyEvent.KEY_RELEASED);
            }
        });

    }

    protected void sendInputEventToInputManager(final InputEvent e, final int type) {
        getRoot().getDefaultInputManager().processEventFromCamera(e, type, getCamera());
    }

    public void setBounds(final int x, final int y, final int w, final int h) {
        camera.setBounds(camera.getX(), camera.getY(), w, h);

        if (backBuffer == null || backBuffer.getBounds().width < w || backBuffer.getBounds().height < h) {
            backBuffer = new Image(getDisplay(), w, h);
        }

        super.setBounds(x, y, w, h);
    }

    public void repaint() {
        super.redraw();
    }

    public void repaint(final PBounds bounds) {
        bounds.expandNearestIntegerDimensions();
        bounds.inset(-1, -1);

        redraw((int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height, true);
    }

    public void paintComponent(final GC gc, final int x, final int y, final int w, final int h) {
        PDebug.startProcessingOutput();

        GC imageGC = null;
        Graphics2D g2 = null;
        if (doubleBuffered) {
            imageGC = new GC(backBuffer);
            g2 = new SWTGraphics2D(imageGC, getDisplay());
        }
        else {
            g2 = new SWTGraphics2D(gc, getDisplay());
        }

        g2.setColor(Color.white);
        g2.setBackground(Color.white);

        final Rectangle rect = getBounds();
        g2.fillRect(0, 0, rect.width, rect.height);

        // This fixes a problem with standard debugging of region management in
        // SWT
        if (PDebug.debugRegionManagement) {
            final Rectangle r = gc.getClipping();
            final Rectangle2D r2 = new Rectangle2D.Double(r.x, r.y, r.width, r.height);
            g2.setBackground(PDebug.getDebugPaintColor());
            g2.fill(r2);
        }

        // create new paint context and set render quality
        final PPaintContext paintContext = new PPaintContext(g2);
        if (getInteracting() || getAnimating()) {
            if (interactingRenderQuality > animatingRenderQuality) {
                paintContext.setRenderQuality(interactingRenderQuality);
            }
            else {
                paintContext.setRenderQuality(animatingRenderQuality);
            }
        }
        else {
            paintContext.setRenderQuality(defaultRenderQuality);
        }

        // paint piccolo
        camera.fullPaint(paintContext);

        // if switched state from animating to not animating invalidate
        // the entire screen so that it will be drawn with the default instead
        // of animating render quality.
        if (!getAnimating() && animatingOnLastPaint) {
            repaint();
        }
        animatingOnLastPaint = getAnimating();

        final boolean region = PDebug.debugRegionManagement;
        PDebug.debugRegionManagement = false;
        PDebug.endProcessingOutput(g2);
        PDebug.debugRegionManagement = region;

        if (doubleBuffered) {
            gc.drawImage(backBuffer, 0, 0);

            // Dispose of the allocated image gc
            imageGC.dispose();
        }
    }

    public void paintImmediately() {
        if (paintingImmediately) {
            return;
        }

        paintingImmediately = true;
        redraw();
        update();
        paintingImmediately = false;
    }
}
