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
package org.piccolo2d.extras.swt;

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
import org.piccolo2d.PCamera;
import org.piccolo2d.PComponent;
import org.piccolo2d.PLayer;
import org.piccolo2d.PRoot;
import org.piccolo2d.event.PInputEventListener;
import org.piccolo2d.event.PPanEventHandler;
import org.piccolo2d.event.PZoomEventHandler;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PDebug;
import org.piccolo2d.util.PPaintContext;
import org.piccolo2d.util.PStack;


/**
 * <b>PSWTCanvas</b> is an SWT Composite that can be used to embed
 * Piccolo into a SWT application. Canvases view the Piccolo scene graph
 * through a camera. The canvas manages screen updates coming from this camera,
 * and forwards swing mouse and keyboard events to the camera.
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PSWTCanvas extends Composite implements PComponent {
    private static final int SWT_BUTTON1 = 1;
    private static final int SWT_BUTTON2 = 2;
    private static final int SWT_BUTTON3 = 3;
    
    /**
     * Terrible Singleton instance of the PSWTCanvas. Falsely assumes you will
     * only have one of these per application.
     */
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

    private boolean isButton1Pressed;
    private boolean isButton2Pressed;
    private boolean isButton3Pressed;

    /**
     * Construct a canvas with the basic scene graph consisting of a root,
     * camera, and layer. Event handlers for zooming and panning are
     * automatically installed.
     * 
     * @param parent component onto which the canvas is installed
     * @param style component style for the PSWTCanvas
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

        installPaintListener();
        installDisposeListener();
    }

    private void installPaintListener() {
        addPaintListener(new PaintListener() {
            public void paintControl(final PaintEvent pe) {
                paintComponent(pe.gc, pe.x, pe.y, pe.width, pe.height);
            }
        });
    }

    private void installDisposeListener() {
        SWTGraphics2D.incrementGCCount();
        addDisposeListener(new DisposeListener() {
            public void widgetDisposed(final DisposeEvent de) {
                getRoot().getActivityScheduler().removeAllActivities();
                SWTGraphics2D.decrementGCCount();
            }
        });
    }

    // ****************************************************************
    // Basic - Methods for accessing common Piccolo2D nodes.
    // ****************************************************************

    /**
     * Get the pan event handler associated with this canvas. This event handler
     * is set up to get events from the camera associated with this canvas by
     * default.
     * 
     * @return the current pan event handler, which may be null
     */
    public PPanEventHandler getPanEventHandler() {
        return panEventHandler;
    }

    /**
     * Get the zoom event handler associated with this canvas. This event
     * handler is set up to get events from the camera associated with this
     * canvas by default.
     * 
     * @return the event handler installed to handle zooming
     */
    public PZoomEventHandler getZoomEventHandler() {
        return zoomEventHandler;
    }

    /**
     * Return the camera associated with this canvas. All input events from this
     * canvas go through this camera. And this is the camera that paints this
     * canvas.
     * 
     * @return the camera associated with this canvas
     */
    public PCamera getCamera() {
        return camera;
    }

    /**
     * Set the camera associated with this canvas. All input events from this
     * canvas go through this camera. And this is the camera that paints this
     * canvas.
     * 
     * @param newCamera camera to attach to this canvas
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
     * 
     * @return root of the scene this canvas is viewing through its camera
     */
    public PRoot getRoot() {
        return camera.getRoot();
    }

    /**
     * Helper method to return the first layer attached to the camera of this
     * canvas.
     * 
     * Short form of <code>canvas.getCamera.getLayer(0)</code>
     * 
     * @return the first layer attached to the camera of this canvas
     */
    public PLayer getLayer() {
        return camera.getLayer(0);
    }

    /**
     * Add an input listener to the camera associated with this canvas.
     * 
     * @param listener listener to add to to the camera
     */
    public void addInputEventListener(final PInputEventListener listener) {
        getCamera().addInputEventListener(listener);
    }

    /**
     * Remove an input listener to the camera associated with this canvas. Does
     * nothign is the listener is not found.
     * 
     * @param listener listener to remove from the set of event listeners
     *            attached to this canvas.
     */
    public void removeInputEventListener(final PInputEventListener listener) {
        getCamera().removeInputEventListener(listener);
    }

    /**
     * Builds the basic scene graph associated with this canvas. Developers may
     * override this method to install their own layers, and cameras.
     * 
     * @return PCamera viewing the freshly created scene
     */
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
     * 
     * @return true if canvas is flagged as interacting
     */
    public boolean getInteracting() {
        return interacting > 0;
    }

    /**
     * Return true if any activities that respond with true to the method
     * isAnimating were run in the last PRoot.processInputs() loop. This values
     * is used by this canvas to determine the render quality to use for the
     * next paint.
     * 
     * @return true if there is an animating activity that is currently active
     */
    public boolean getAnimating() {
        return getRoot().getActivityScheduler().getAnimating();
    }

    /**
     * Changes the number of callers that are interacting with the canvas. Will
     * allow the scene to be rendered in a lower quality if the number is not 0.
     * 
     * @param isInteracting state the client considers the PSWTCanvas to be in
     *            with regard to interacting
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
     * Get whether this canvas should use double buffering - the default is to
     * double buffer.
     * 
     * @return true if double buffering is enabled
     */
    public boolean getDoubleBuffered() {
        return doubleBuffered;
    }

    /**
     * Set whether this canvas should use double buffering - the default is yes.
     * 
     * @param doubleBuffered value of double buffering flas
     */
    public void setDoubleBuffered(final boolean doubleBuffered) {
        this.doubleBuffered = doubleBuffered;
        if (!doubleBuffered && backBuffer != null) {
            backBuffer.dispose();
            backBuffer = null;
        }
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
     * stack. Under the hood it is mapping the java.awt.Cursor to
     * org.eclipse.swt.graphics.Cursor objects.
     * 
     * @param newCursor new cursor to push onto the cursor stack
     */
    public void pushCursor(final java.awt.Cursor newCursor) {
        Cursor swtCursor = null;
        if (newCursor.getType() == java.awt.Cursor.N_RESIZE_CURSOR) {
            swtCursor = new Cursor(getDisplay(), SWT.CURSOR_SIZEN);
        }
        else if (newCursor.getType() == java.awt.Cursor.NE_RESIZE_CURSOR) {
            swtCursor = new Cursor(getDisplay(), SWT.CURSOR_SIZENE);
        }
        else if (newCursor.getType() == java.awt.Cursor.NW_RESIZE_CURSOR) {
            swtCursor = new Cursor(getDisplay(), SWT.CURSOR_SIZENW);
        }
        else if (newCursor.getType() == java.awt.Cursor.S_RESIZE_CURSOR) {
            swtCursor = new Cursor(getDisplay(), SWT.CURSOR_SIZES);
        }
        else if (newCursor.getType() == java.awt.Cursor.SE_RESIZE_CURSOR) {
            swtCursor = new Cursor(getDisplay(), SWT.CURSOR_SIZESE);
        }
        else if (newCursor.getType() == java.awt.Cursor.SW_RESIZE_CURSOR) {
            swtCursor = new Cursor(getDisplay(), SWT.CURSOR_SIZESW);
        }
        else if (newCursor.getType() == java.awt.Cursor.E_RESIZE_CURSOR) {
            swtCursor = new Cursor(getDisplay(), SWT.CURSOR_SIZEE);
        }
        else if (newCursor.getType() == java.awt.Cursor.W_RESIZE_CURSOR) {
            swtCursor = new Cursor(getDisplay(), SWT.CURSOR_SIZEW);
        }
        else if (newCursor.getType() == java.awt.Cursor.TEXT_CURSOR) {
            swtCursor = new Cursor(getDisplay(), SWT.CURSOR_IBEAM);
        }
        else if (newCursor.getType() == java.awt.Cursor.HAND_CURSOR) {
            swtCursor = new Cursor(getDisplay(), SWT.CURSOR_HAND);
        }
        else if (newCursor.getType() == java.awt.Cursor.MOVE_CURSOR) {
            swtCursor = new Cursor(getDisplay(), SWT.CURSOR_SIZEALL);
        }
        else if (newCursor.getType() == java.awt.Cursor.CROSSHAIR_CURSOR) {
            swtCursor = new Cursor(getDisplay(), SWT.CURSOR_CROSS);
        }
        else if (newCursor.getType() == java.awt.Cursor.WAIT_CURSOR) {
            swtCursor = new Cursor(getDisplay(), SWT.CURSOR_WAIT);
        }

        if (swtCursor != null) {
            if (curCursor != null) {
                cursorStack.push(curCursor);
            }
            curCursor = swtCursor;
            setCursor(swtCursor);
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

        if (cursorStack.isEmpty()) {
            curCursor = null;
        }
        else {
            curCursor = (Cursor) cursorStack.pop();
        }

        // This sets the cursor back to default
        setCursor(curCursor);
    }

    // ****************************************************************
    // Code to manage connection to Swing. There appears to be a bug in
    // swing where it will occasionally send to many mouse pressed or mouse
    // released events. Below we attempt to filter out those cases before
    // they get delivered to the Piccolo2D framework.
    // ****************************************************************

    /**
     * This method installs mouse and key listeners on the canvas that forward
     * those events to Piccolo2D.
     */
    protected void installInputSources() {
        MouseInputSource mouseInputSource = new MouseInputSource();
        addMouseListener(mouseInputSource);
        addMouseMoveListener(mouseInputSource);

        addKeyListener(new KeyboardInputSource());
    }

    /**
     * Dispatches the given event to the default input manager for the root of
     * this canvas.
     * 
     * @param awtEvent awt event needing dispatching
     * @param type type of the event
     */
    protected void sendInputEventToInputManager(final InputEvent awtEvent, final int type) {
        getRoot().getDefaultInputManager().processEventFromCamera(awtEvent, type, getCamera());
    }

    /**
     * Changes the bounds of this PSWTCanvas. Updating the camera and the double
     * buffered image appropriately.
     * 
     * @param x left of the new bounds
     * @param y top of the new bounds
     * @param newWidth new width of the bounds
     * @param newHeight new height of the bounds
     */
    public void setBounds(final int x, final int y, final int newWidth, final int newHeight) {
        camera.setBounds(camera.getX(), camera.getY(), newWidth, newHeight);

        if (backBufferNeedsResizing(newWidth, newHeight)) {
            resizeBackBuffer(newWidth, newHeight);
        }

        super.setBounds(x, y, newWidth, newHeight);
    }

    private void resizeBackBuffer(final int newWidth, final int newHeight) {
        if (backBuffer != null) {
            backBuffer.dispose();
        }
        backBuffer = new Image(getDisplay(), newWidth, newHeight);
    }

    private boolean backBufferNeedsResizing(final int newWidth, final int newHeight) {
        if (!doubleBuffered) {
            return false;
        }

        if (backBuffer == null) {
            return true;
        }

        return backBuffer.getBounds().width < newWidth || backBuffer.getBounds().height < newHeight;
    }

    /**
     * Exists to dispatch from the Swing's repaint method to SWT's redraw
     * method.
     */
    public void repaint() {
        super.redraw();
    }

    /**
     * Flags the bounds provided as needing to be redrawn.
     * 
     * @param bounds the bounds that should be repainted
     */
    public void repaint(final PBounds bounds) {
        bounds.expandNearestIntegerDimensions();
        bounds.inset(-1, -1);

        redraw((int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height, true);
    }

    /**
     * Paints the region specified of the canvas onto the given Graphics
     * Context.
     * 
     * @param gc graphics onto within painting should occur
     * @param x left of the dirty region
     * @param y top of the dirty region
     * @param w width of the dirty region
     * @param h height of the dirty region
     */
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

        // paint Piccolo2D
        camera.fullPaint(paintContext);

        // if switched state from animating to not animating invalidate
        // the entire screen so that it will be drawn with the default instead
        // of animating render quality.
        if (animatingOnLastPaint && !getAnimating()) {
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

    /**
     * Performs an immediate repaint if no other client is currently performing
     * one.
     */
    public void paintImmediately() {
        if (paintingImmediately) {
            return;
        }

        paintingImmediately = true;
        redraw();
        update();
        paintingImmediately = false;
    }

    private final class KeyboardInputSource implements KeyListener {
        public void keyPressed(final KeyEvent ke) {
            final java.awt.event.KeyEvent inputEvent = new PSWTKeyEvent(ke, java.awt.event.KeyEvent.KEY_PRESSED);
            sendInputEventToInputManager(inputEvent, java.awt.event.KeyEvent.KEY_PRESSED);
        }

        public void keyReleased(final KeyEvent ke) {
            final java.awt.event.KeyEvent inputEvent = new PSWTKeyEvent(ke, java.awt.event.KeyEvent.KEY_RELEASED);
            sendInputEventToInputManager(inputEvent, java.awt.event.KeyEvent.KEY_RELEASED);
        }
    }

    private final class MouseInputSource implements MouseListener, MouseMoveListener {
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

        public void mouseDown(final MouseEvent mouseEvent) {
            boolean shouldBalanceEvent = false;

            switch (mouseEvent.button) {
                case SWT_BUTTON1:
                    if (isButton1Pressed) {
                        shouldBalanceEvent = true;
                    }
                    isButton1Pressed = true;
                    break;
                case SWT_BUTTON2:
                    if (isButton2Pressed) {
                        shouldBalanceEvent = true;
                    }
                    isButton2Pressed = true;
                    break;
                case SWT_BUTTON3:
                    if (isButton3Pressed) {
                        shouldBalanceEvent = true;
                    }
                    isButton3Pressed = true;
                    break;
                default:
            }

            if (shouldBalanceEvent) {
                final java.awt.event.MouseEvent balanceEvent = new PSWTMouseEvent(mouseEvent,
                        java.awt.event.MouseEvent.MOUSE_RELEASED, 1);
                sendInputEventToInputManager(balanceEvent, java.awt.event.MouseEvent.MOUSE_RELEASED);
            }

            final java.awt.event.MouseEvent balanceEvent = new PSWTMouseEvent(mouseEvent,
                    java.awt.event.MouseEvent.MOUSE_PRESSED, 1);
            sendInputEventToInputManager(balanceEvent, java.awt.event.MouseEvent.MOUSE_PRESSED);
        }

        public void mouseUp(final MouseEvent me) {
            boolean shouldBalanceEvent = false;

            switch (me.button) {
                case SWT_BUTTON1:
                    if (!isButton1Pressed) {
                        shouldBalanceEvent = true;
                    }
                    isButton1Pressed = false;
                    break;
                case SWT_BUTTON2:
                    if (!isButton2Pressed) {
                        shouldBalanceEvent = true;
                    }
                    isButton2Pressed = false;
                    break;
                case SWT_BUTTON3:
                    if (!isButton3Pressed) {
                        shouldBalanceEvent = true;
                    }
                    isButton3Pressed = false;
                    break;
                default:
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
            java.awt.event.MouseEvent inputEvent = new PSWTMouseEvent(me, java.awt.event.MouseEvent.MOUSE_PRESSED, 2);
            sendInputEventToInputManager(inputEvent, java.awt.event.MouseEvent.MOUSE_PRESSED);
            inputEvent = new PSWTMouseEvent(me, java.awt.event.MouseEvent.MOUSE_RELEASED, 2);
            sendInputEventToInputManager(inputEvent, java.awt.event.MouseEvent.MOUSE_RELEASED);
        }
    }
}
