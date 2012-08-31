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
package org.piccolo2d;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.FocusManager;
import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.Timer;

import org.piccolo2d.event.PInputEventListener;
import org.piccolo2d.event.PPanEventHandler;
import org.piccolo2d.event.PZoomEventHandler;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PDebug;
import org.piccolo2d.util.PPaintContext;
import org.piccolo2d.util.PStack;
import org.piccolo2d.util.PUtil;


/**
 * <b>PCanvas</b> is a simple Swing component that can be used to embed Piccolo
 * into a Java Swing application. Canvases view the Piccolo scene graph through
 * a camera. The canvas manages screen updates coming from this camera, and
 * forwards swing mouse and keyboard events to the camera.
 * <P>
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PCanvas extends JComponent implements PComponent {
    /**
     * Allows for future serialization code to understand versioned binary
     * formats.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The property name that identifies a change in the interacting state.
     *
     * @since 1.3
     */
    public static final String PROPERTY_INTERACTING = "INTERACTING_CHANGED_NOTIFICATION";

    /** The camera though which this Canvas is viewing. */
    private PCamera camera;

    /**
     * Stack of cursors used to keep track of cursors as they change through
     * interactions.
     */
    private final PStack cursorStack;

    /**
     * Whether the canvas is considered to be interacting, will probably mean
     * worse render quality.
     */
    private int interacting;
    /**
     * The render quality to use when the scene is not being interacted or
     * animated.
     */
    private int normalRenderQuality;

    /** The quality to use while the scene is being animated. */
    private int animatingRenderQuality;

    /** The quality to use while the scene is being interacted with. */
    private int interactingRenderQuality;

    /** The one and only pan handler. */
    private transient PPanEventHandler panEventHandler;

    /** The one and only ZoomEventHandler. */
    private transient PZoomEventHandler zoomEventHandler;

    private boolean paintingImmediately;

    /** Used to track whether the last paint operation was during an animation. */
    private boolean animatingOnLastPaint;

    /** The mouse listener that is registered for large scale mouse events. */
    private transient MouseListener mouseListener;

    /** Remembers the key processor. */
    private transient KeyEventPostProcessor keyEventPostProcessor;

    /** The mouse wheel listeners that's registered to receive wheel events. */
    private transient MouseWheelListener mouseWheelListener;
    /**
     * The mouse listener that is registered to receive small scale mouse events
     * (like motion).
     */
    private transient MouseMotionListener mouseMotionListener;

    private static final int ALL_BUTTONS_MASK = InputEvent.BUTTON1_DOWN_MASK | InputEvent.BUTTON2_DOWN_MASK
            | InputEvent.BUTTON3_DOWN_MASK;

    /**
     * Construct a canvas with the basic scene graph consisting of a root,
     * camera, and layer. Zooming and panning are automatically installed.
     */
    public PCanvas() {
        cursorStack = new PStack();
        setCamera(createDefaultCamera());
        setDefaultRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
        setAnimatingRenderQuality(PPaintContext.LOW_QUALITY_RENDERING);
        setInteractingRenderQuality(PPaintContext.LOW_QUALITY_RENDERING);
        setPanEventHandler(new PPanEventHandler());
        setZoomEventHandler(new PZoomEventHandler());
        setBackground(Color.WHITE);
        setOpaque(true);

        addHierarchyListener(new HierarchyListener() {
            public void hierarchyChanged(final HierarchyEvent e) {
                if (e.getComponent() == PCanvas.this) {
                    if (getParent() == null) {
                        removeInputSources();
                    }
                    else if (isEnabled()) {
                        installInputSources();
                    }
                }
            }
        });
    }

    /**
     * Creates and returns a basic Scene Graph.
     * 
     * @return a built PCamera scene
     */
    protected PCamera createDefaultCamera() {
        return PUtil.createBasicScenegraph();
    }

    // ****************************************************************
    // Basic - Methods for accessing common piccolo nodes.
    // ****************************************************************

    /**
     * Get the pan event handler associated with this canvas. This event handler
     * is set up to get events from the camera associated with this canvas by
     * default.
     * 
     * @return the current pan event handler, may be null
     */
    public PPanEventHandler getPanEventHandler() {
        return panEventHandler;
    }

    /**
     * Set the pan event handler associated with this canvas.
     * 
     * @param handler the new zoom event handler
     */
    public void setPanEventHandler(final PPanEventHandler handler) {
        if (panEventHandler != null) {
            removeInputEventListener(panEventHandler);
        }

        panEventHandler = handler;

        if (panEventHandler != null) {
            addInputEventListener(panEventHandler);
        }
    }

    /**
     * Get the zoom event handler associated with this canvas. This event
     * handler is set up to get events from the camera associated with this
     * canvas by default.
     * 
     * @return the current zoom event handler, may be null
     */
    public PZoomEventHandler getZoomEventHandler() {
        return zoomEventHandler;
    }

    /**
     * Set the zoom event handler associated with this canvas.
     * 
     * @param handler the new zoom event handler
     */
    public void setZoomEventHandler(final PZoomEventHandler handler) {
        if (zoomEventHandler != null) {
            removeInputEventListener(zoomEventHandler);
        }

        zoomEventHandler = handler;

        if (zoomEventHandler != null) {
            addInputEventListener(zoomEventHandler);
        }
    }

    /**
     * Return the camera associated with this canvas. All input events from this
     * canvas go through this camera. And this is the camera that paints this
     * canvas.
     * 
     * @return camera through which this PCanvas views the scene
     */
    public PCamera getCamera() {
        return camera;
    }

    /**
     * Set the camera associated with this canvas. All input events from this
     * canvas go through this camera. And this is the camera that paints this
     * canvas.
     * 
     * @param newCamera the camera which this PCanvas should view the scene
     */
    public void setCamera(final PCamera newCamera) {
        if (camera != null) {
            camera.setComponent(null);
        }

        camera = newCamera;

        if (camera != null) {
            camera.setComponent(this);
            camera.setBounds(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * Return root for this canvas.
     * 
     * @return the root PNode at the "bottom" of the scene
     */
    public PRoot getRoot() {
        return camera.getRoot();
    }

    /**
     * Return layer for this canvas.
     * 
     * @return the first layer attached to this camera
     */
    public PLayer getLayer() {
        return camera.getLayer(0);
    }

    /**
     * Add an input listener to the camera associated with this canvas.
     * 
     * @param listener listener to register for event notifications
     */
    public void addInputEventListener(final PInputEventListener listener) {
        getCamera().addInputEventListener(listener);
    }

    /**
     * Remove an input listener to the camera associated with this canvas.
     * 
     * @param listener listener to unregister from event notifications
     */
    public void removeInputEventListener(final PInputEventListener listener) {
        getCamera().removeInputEventListener(listener);
    }

    // ****************************************************************
    // Painting
    // ****************************************************************

    /**
     * Return true if this canvas has been marked as interacting, or whether
     * it's root is interacting. If so the canvas will normally render at a
     * lower quality that is faster.
     * 
     * @return whether the canvas has been flagged as being interacted with
     */
    public boolean getInteracting() {
        return interacting > 0 || getRoot().getInteracting();
    }

    /**
     * Return true if any activities that respond with true to the method
     * isAnimating were run in the last PRoot.processInputs() loop. This values
     * is used by this canvas to determine the render quality to use for the
     * next paint.
     * 
     * @return whether the PCanvas is currently being animated
     */
    public boolean getAnimating() {
        return getRoot().getActivityScheduler().getAnimating();
    }

    /**
     * Set if this canvas is interacting. If so the canvas will normally render
     * at a lower quality that is faster. Also repaints the canvas if the render
     * quality should change.
     * 
     * @param isInteracting whether the PCanvas should be considered interacting
     */
    public void setInteracting(final boolean isInteracting) {
        final boolean wasInteracting = getInteracting();

        if (isInteracting) {
            interacting++;
        }
        else {
            interacting--;
        }

        if (!getInteracting()) { // determine next render quality and repaint if
            // it's greater then the old
            // interacting render quality.
            int nextRenderQuality = normalRenderQuality;
            if (getAnimating()) {
                nextRenderQuality = animatingRenderQuality;
            }
            if (nextRenderQuality > interactingRenderQuality) {
                repaint();
            }
        }

        final boolean newInteracting = getInteracting();

        if (wasInteracting != newInteracting) {
            firePropertyChange(PROPERTY_INTERACTING, wasInteracting, newInteracting);
        }
    }

    /**
     * Set the render quality that should be used when rendering this canvas
     * when it is not interacting or animating. The default value is
     * PPaintContext. HIGH_QUALITY_RENDERING.
     * 
     * @param defaultRenderQuality supports PPaintContext.HIGH_QUALITY_RENDERING
     *            or PPaintContext.LOW_QUALITY_RENDERING
     */
    public void setDefaultRenderQuality(final int defaultRenderQuality) {
        this.normalRenderQuality = defaultRenderQuality;
        repaint();
    }

    /**
     * Set the render quality that should be used when rendering this canvas
     * when it is animating. The default value is
     * PPaintContext.LOW_QUALITY_RENDERING.
     * 
     * @param animatingRenderQuality supports
     *            PPaintContext.HIGH_QUALITY_RENDERING or
     *            PPaintContext.LOW_QUALITY_RENDERING
     */
    public void setAnimatingRenderQuality(final int animatingRenderQuality) {
        this.animatingRenderQuality = animatingRenderQuality;
        if (getAnimating()) {
            repaint();
        }
    }

    /**
     * Set the render quality that should be used when rendering this canvas
     * when it is interacting. The default value is
     * PPaintContext.LOW_QUALITY_RENDERING.
     * 
     * @param interactingRenderQuality supports
     *            PPaintContext.HIGH_QUALITY_RENDERING or
     *            PPaintContext.LOW_QUALITY_RENDERING
     */
    public void setInteractingRenderQuality(final int interactingRenderQuality) {
        this.interactingRenderQuality = interactingRenderQuality;
        if (getInteracting()) {
            repaint();
        }
    }

    /**
     * Set the canvas cursor, and remember the previous cursor on the cursor
     * stack.
     * 
     * @param cursor the cursor to push onto the cursor stack
     */
    public void pushCursor(final Cursor cursor) {
        cursorStack.push(getCursor());
        setCursor(cursor);
    }

    /**
     * Pop the cursor on top of the cursorStack and set it as the canvas cursor.
     */
    public void popCursor() {
        if (!cursorStack.isEmpty()) {
            setCursor((Cursor) cursorStack.pop());
        }
    }

    // ****************************************************************
    // Code to manage connection to Swing. There appears to be a bug in
    // swing where it will occasionally send too many mouse pressed or mouse
    // released events. Below we attempt to filter out those cases before
    // they get delivered to the Piccolo framework.
    // ****************************************************************

    /**
     * Tracks whether button1 of the mouse is down.
     */
    private boolean isButton1Pressed;
    /**
     * Tracks whether button2 of the mouse is down.
     */
    private boolean isButton2Pressed;
    /**
     * Tracks whether button3 of the mouse is down.
     */
    private boolean isButton3Pressed;

    /**
     * Override setEnabled to install/remove canvas input sources as needed.
     * 
     * @param enabled new enable status of the Pcanvas
     */
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);

        if (isEnabled() && getParent() != null) {
            installInputSources();
        }
        else {
            removeInputSources();
        }
    }

    /**
     * This method installs mouse and key listeners on the canvas that forward
     * those events to piccolo.
     */
    protected void installInputSources() {
        if (mouseListener == null) {
            mouseListener = new MouseEventInputSource();
            addMouseListener(mouseListener);
        }

        if (mouseMotionListener == null) {
            mouseMotionListener = new MouseMotionInputSourceListener();
            addMouseMotionListener(mouseMotionListener);
        }

        if (mouseWheelListener == null) {
            mouseWheelListener = new MouseWheelInputSourceListener();
            addMouseWheelListener(mouseWheelListener);
        }

        if (keyEventPostProcessor == null) {
            keyEventPostProcessor = new KeyEventInputSourceListener();
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(keyEventPostProcessor);
        }
    }

    /**
     * This method removes mouse and key listeners on the canvas that forward
     * those events to piccolo.
     */
    protected void removeInputSources() {
        removeMouseListener(mouseListener);
        removeMouseMotionListener(mouseMotionListener);
        removeMouseWheelListener(mouseWheelListener);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventPostProcessor(keyEventPostProcessor);

        mouseListener = null;
        mouseMotionListener = null;
        mouseWheelListener = null;
        keyEventPostProcessor = null;
    }

    /**
     * Sends the given input event with the given type to the current
     * InputManager.
     * 
     * @param event event to dispatch
     * @param type type of event being dispatched
     */
    protected void sendInputEventToInputManager(final InputEvent event, final int type) {
        getRoot().getDefaultInputManager().processEventFromCamera(event, type, getCamera());
    }

    /**
     * Updates the bounds of the component and updates the camera accordingly.
     * 
     * @param x left of bounds
     * @param y top of bounds
     * @param width width of bounds
     * @param height height of bounds
     */
    public void setBounds(final int x, final int y, final int width, final int height) {
        camera.setBounds(camera.getX(), camera.getY(), width, height);
        super.setBounds(x, y, width, height);
    }

    /**
     * {@inheritDoc}
     */
    public void repaint(final PBounds bounds) {
        PDebug.processRepaint();

        bounds.expandNearestIntegerDimensions();
        bounds.inset(-1, -1);

        repaint((int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height);
    }

    private PBounds repaintBounds = new PBounds();

    /**
     * {@inheritDoc}
     */
    public void paintComponent(final Graphics g) {
        PDebug.startProcessingOutput();

        final Graphics2D g2 = (Graphics2D) g.create();

        // support for non-opaque canvases
        // see
        // http://groups.google.com/group/piccolo2d-dev/browse_thread/thread/134e2792d3a54cf
        if (isOpaque()) {
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        if (getAnimating()) {
            repaintBounds.add(g2.getClipBounds());
        }

        // create new paint context and set render quality to lowest common
        // denominator render quality.
        final PPaintContext paintContext = new PPaintContext(g2);
        if (getInteracting() || getAnimating()) {
            if (interactingRenderQuality < animatingRenderQuality) {
                paintContext.setRenderQuality(interactingRenderQuality);
            }
            else {
                paintContext.setRenderQuality(animatingRenderQuality);
            }
        }
        else {
            paintContext.setRenderQuality(normalRenderQuality);
        }

        camera.fullPaint(paintContext);

        // if switched state from animating to not animating invalidate the
        // repaint bounds so that it will be drawn with the default instead of
        // animating render quality.
        if (!getAnimating() && animatingOnLastPaint) {
            repaint(repaintBounds);
            repaintBounds.reset();
        }

        animatingOnLastPaint = getAnimating();

        PDebug.endProcessingOutput(g2);
    }

    /**
     * If not painting immediately, send paint notification to RepaintManager,
     * otherwise does nothing.
     */
    public void paintImmediately() {
        if (paintingImmediately) {
            return;
        }

        paintingImmediately = true;
        RepaintManager.currentManager(this).paintDirtyRegions();
        paintingImmediately = false;
    }

    /**
     * Helper for creating a timer. It's an extension point for subclasses to
     * install their own timers.
     * 
     * @param delay the number of milliseconds to wait before invoking the
     *            listener
     * @param listener the listener to invoke after the delay
     * 
     * @return the created Timer
     */
    public Timer createTimer(final int delay, final ActionListener listener) {
        return new Timer(delay, listener);
    }

    /**
     * Returns the quality to use when not animating or interacting.
     * 
     * @since 1.3
     * @return the render quality to use when not animating or interacting
     */
    public int getNormalRenderQuality() {
        return normalRenderQuality;
    }

    /**
     * Returns the quality to use when animating.
     * 
     * @since 1.3
     * @return Returns the quality to use when animating
     */
    public int getAnimatingRenderQuality() {
        return animatingRenderQuality;
    }

    /**
     * Returns the quality to use when interacting.
     * 
     * @since 1.3
     * @return Returns the quality to use when interacting
     */
    public int getInteractingRenderQuality() {
        return interactingRenderQuality;
    }

    /**
     * Returns the input event listeners registered to receive input events.
     * 
     * @since 1.3
     * @return array or input event listeners
     */
    public PInputEventListener[] getInputEventListeners() {
        return camera.getInputEventListeners();
    }

    /**
     * Prints the entire scene regardless of what the viewable area is.
     * 
     * @param graphics Graphics context onto which to paint the scene for printing
     */
    public void printAll(final Graphics graphics) {
        if (!(graphics instanceof Graphics2D)) {
            throw new IllegalArgumentException("Provided graphics context is not a Graphics2D object");
        }
        
        final Graphics2D g2 = (Graphics2D) graphics;

        final PBounds clippingRect = new PBounds(graphics.getClipBounds());
        clippingRect.expandNearestIntegerDimensions();

        final PBounds originalCameraBounds = getCamera().getBounds();
        final PBounds layerBounds = getCamera().getUnionOfLayerFullBounds();
        getCamera().setBounds(layerBounds);

        final double clipRatio = clippingRect.getWidth() / clippingRect.getHeight();
        final double nodeRatio = ((double) getWidth()) / ((double) getHeight());
        final double scale;
        if (nodeRatio <= clipRatio) {
            scale = clippingRect.getHeight() / getCamera().getHeight();
        }
        else {
            scale = clippingRect.getWidth() / getCamera().getWidth();
        }
        g2.scale(scale, scale);
        g2.translate(-clippingRect.x, -clippingRect.y);

        final PPaintContext pc = new PPaintContext(g2);
        pc.setRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
        getCamera().fullPaint(pc);

        getCamera().setBounds(originalCameraBounds);
    }

    private final class MouseMotionInputSourceListener implements MouseMotionListener {
        /** {@inheritDoc} */
        public void mouseDragged(final MouseEvent e) {
            sendInputEventToInputManager(e, MouseEvent.MOUSE_DRAGGED);
        }

        /** {@inheritDoc} */
        public void mouseMoved(final MouseEvent e) {
            sendInputEventToInputManager(e, MouseEvent.MOUSE_MOVED);
        }
    }

    private final class MouseEventInputSource implements MouseListener {
        /** {@inheritDoc} */
        public void mouseClicked(final MouseEvent e) {
            sendInputEventToInputManager(e, MouseEvent.MOUSE_CLICKED);
        }

        /** {@inheritDoc} */
        public void mouseEntered(final MouseEvent e) {
            MouseEvent simulated = null;

            if (isAnyButtonDown(e)) {
                simulated = buildRetypedMouseEvent(e, MouseEvent.MOUSE_DRAGGED);
            }
            else {
                simulated = buildRetypedMouseEvent(e, MouseEvent.MOUSE_MOVED);
            }

            sendInputEventToInputManager(e, MouseEvent.MOUSE_ENTERED);
            sendInputEventToInputManager(simulated, simulated.getID());
        }

        /** {@inheritDoc} */
        public void mouseExited(final MouseEvent e) {
            MouseEvent simulated = null;

            if (isAnyButtonDown(e)) {
                simulated = buildRetypedMouseEvent(e, MouseEvent.MOUSE_DRAGGED);
            }
            else {
                simulated = buildRetypedMouseEvent(e, MouseEvent.MOUSE_MOVED);
            }

            sendInputEventToInputManager(simulated, simulated.getID());
            sendInputEventToInputManager(e, MouseEvent.MOUSE_EXITED);
        }

        /** {@inheritDoc} */
        public void mousePressed(final MouseEvent rawEvent) {
            requestFocus();

            boolean shouldBalanceEvent = false;
            boolean shouldSendEvent = false;

            final MouseEvent event = copyButtonsFromModifiers(rawEvent, MouseEvent.MOUSE_PRESSED);

            switch (event.getButton()) {
                case MouseEvent.BUTTON1:
                    if (isButton1Pressed) {
                        shouldBalanceEvent = true;
                    }
                    isButton1Pressed = true;
                    shouldSendEvent = true;
                    break;

                case MouseEvent.BUTTON2:
                    if (isButton2Pressed) {
                        shouldBalanceEvent = true;
                    }
                    isButton2Pressed = true;
                    shouldSendEvent = true;
                    break;

                case MouseEvent.BUTTON3:
                    if (isButton3Pressed) {
                        shouldBalanceEvent = true;
                    }
                    isButton3Pressed = true;
                    shouldSendEvent = true;
                    break;

                default:
                    break;
            }

            if (shouldBalanceEvent) {
                sendRetypedMouseEventToInputManager(event, MouseEvent.MOUSE_RELEASED);
            }

            if (shouldSendEvent) {
                sendInputEventToInputManager(event, MouseEvent.MOUSE_PRESSED);
            }
        }

        /** {@inheritDoc} */
        public void mouseReleased(final MouseEvent rawEvent) {
            boolean shouldBalanceEvent = false;
            boolean shouldSendEvent = false;

            final MouseEvent event = copyButtonsFromModifiers(rawEvent, MouseEvent.MOUSE_RELEASED);

            switch (event.getButton()) {
                case MouseEvent.BUTTON1:
                    if (!isButton1Pressed) {
                        shouldBalanceEvent = true;
                    }
                    isButton1Pressed = false;
                    shouldSendEvent = true;
                    break;

                case MouseEvent.BUTTON2:
                    if (!isButton2Pressed) {
                        shouldBalanceEvent = true;
                    }
                    isButton2Pressed = false;
                    shouldSendEvent = true;
                    break;

                case MouseEvent.BUTTON3:
                    if (!isButton3Pressed) {
                        shouldBalanceEvent = true;
                    }
                    isButton3Pressed = false;
                    shouldSendEvent = true;
                    break;

                default:
                    shouldBalanceEvent = false;
                    shouldSendEvent = false;
            }

            if (shouldBalanceEvent) {
                sendRetypedMouseEventToInputManager(event, MouseEvent.MOUSE_PRESSED);
            }

            if (shouldSendEvent) {
                sendInputEventToInputManager(event, MouseEvent.MOUSE_RELEASED);
            }
        }

        private MouseEvent copyButtonsFromModifiers(final MouseEvent rawEvent, final int eventType) {
            if (rawEvent.getButton() != MouseEvent.NOBUTTON) {
                return rawEvent;
            }

            int newButton = 0;

            if (hasButtonModifier(rawEvent, InputEvent.BUTTON1_MASK)) {
                newButton = MouseEvent.BUTTON1;
            }
            else if (hasButtonModifier(rawEvent, InputEvent.BUTTON2_MASK)) {
                newButton = MouseEvent.BUTTON2;
            }
            else if (hasButtonModifier(rawEvent, InputEvent.BUTTON3_MASK)) {
                newButton = MouseEvent.BUTTON3;
            }

            return buildModifiedMouseEvent(rawEvent, eventType, newButton);
        }

        private boolean hasButtonModifier(final MouseEvent event, final int buttonMask) {
            return (event.getModifiers() & buttonMask) == buttonMask;
        }

        public MouseEvent buildRetypedMouseEvent(final MouseEvent e, final int newType) {
            return buildModifiedMouseEvent(e, newType, e.getButton());
        }

        public MouseEvent buildModifiedMouseEvent(final MouseEvent e, final int newType, final int newButton) {
            return new MouseEvent((Component) e.getSource(), newType, e.getWhen(), e.getModifiers(), e.getX(),
                    e.getY(), e.getClickCount(), e.isPopupTrigger(), newButton);
        }

        private void sendRetypedMouseEventToInputManager(final MouseEvent e, final int newType) {
            final MouseEvent retypedEvent = buildRetypedMouseEvent(e, newType);
            sendInputEventToInputManager(retypedEvent, newType);
        }
    }

    private boolean isAnyButtonDown(final MouseEvent e) {
        return (e.getModifiersEx() & ALL_BUTTONS_MASK) != 0;
    }

    /**
     * Class responsible for sending key events to the the InputManager.
     */
    private final class KeyEventInputSourceListener implements KeyEventPostProcessor {
        /** {@inheritDoc} */
        public boolean postProcessKeyEvent(final KeyEvent keyEvent) {
            Component owner = FocusManager.getCurrentManager().getFocusOwner();
            while (owner != null) {
                if (owner == PCanvas.this) {
                    sendInputEventToInputManager(keyEvent, keyEvent.getID());
                    return true;
                }
                owner = owner.getParent();
            }
            return false;
        }
    }

    /**
     * Class responsible for sending mouse events to the the InputManager.
     */
    private final class MouseWheelInputSourceListener implements MouseWheelListener {
        /** {@inheritDoc} */
        public void mouseWheelMoved(final MouseWheelEvent e) {
            sendInputEventToInputManager(e, e.getScrollType());
            if (!e.isConsumed() && getParent() != null) {
                getParent().dispatchEvent(e);
            }
        }
    }

}