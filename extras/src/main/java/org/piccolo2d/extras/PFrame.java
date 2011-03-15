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
package org.piccolo2d.extras;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.piccolo2d.PCanvas;


/**
 * <b>PFrame</b> is meant to be subclassed by applications that just need a
 * PCanvas in a JFrame. It also includes full screen mode functionality when run
 * in JDK 1.4. These subclasses should override the initialize method and start
 * adding their own code there. Look in the examples package to see lots of uses
 * of PFrame.
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PFrame extends JFrame {
    private static final Dimension DEFAULT_FRAME_DIMENSION = new Dimension(400, 400);

    private static final Point DEFAULT_FRAME_POSITION = new Point(100, 100);

    /** Used to allow versioned binary streams for serializations. */
    private static final long serialVersionUID = 1L;

    /** Canvas being displayed on this PFrame. */
    private PCanvas canvas;

    /** The graphics device onto which the PFrame is being displayed. */
    private final GraphicsDevice graphicsDevice;

    /** Listener that listens for escape key. */
    private transient EventListener escapeFullScreenModeListener;

    /**
     * Creates a PFrame with no title, not full screen, and with the default
     * canvas.
     */
    public PFrame() {
        this("", false, null);
    }

    /**
     * Creates a PFrame with the given title and with the default canvas.
     * 
     * @param title title to display at the top of the frame
     * @param fullScreenMode whether to display a full screen frame or not
     * @param canvas to embed in the frame
     */
    public PFrame(final String title, final boolean fullScreenMode, final PCanvas canvas) {
        this(title, GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice(), fullScreenMode, canvas);
    }

    /**
     * Creates a PFrame with the given title and with the default canvas being
     * displayed on the provided device.
     * 
     * @param title title to display at the top of the frame
     * @param device device onto which PFrame is to be displayed
     * @param fullScreen whether to display a full screen frame or not
     * @param canvas to embed in the frame, may be null. If so, it'll create a
     *            default PCanvas
     */
    public PFrame(final String title, final GraphicsDevice device, final boolean fullScreen, final PCanvas canvas) {
        super(title, device.getDefaultConfiguration());

        graphicsDevice = device;

        setBackground(null);
        setBounds(getDefaultFrameBounds());

        try {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        catch (final SecurityException e) {
            // expected from Applets
            System.out.println("Ignoring security exception. Assuming Applet Context.");
        }

        if (canvas == null) {
            this.canvas = new PCanvas();
        }
        else {
            this.canvas = canvas;
        }

        setContentPane(this.canvas);
        validate();
        setFullScreenMode(fullScreen);
        this.canvas.requestFocus();
        beforeInitialize();

        // Manipulation of Piccolo's scene graph should be done from Swings
        // event dispatch thread since Piccolo2D is not thread safe. This code
        // calls initialize() from that thread once the PFrame is initialized,
        // so you are safe to start working with Piccolo2D in the initialize()
        // method.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PFrame.this.initialize();
                repaint();
            }
        });
    }

    /**
     * Returns the canvas being displayed on this frame.
     * 
     * @return canvas being displayed on this frame
     */
    public PCanvas getCanvas() {
        return canvas;
    }

    /**
     * Returns the default frame bounds.
     * 
     * @return default frame bounds
     */
    public Rectangle getDefaultFrameBounds() {
        return new Rectangle(DEFAULT_FRAME_POSITION, DEFAULT_FRAME_DIMENSION);
    }

    /**
     * Returns whether the frame is currently in full screen mode.
     * 
     * @return whether the frame is currently in full screen mode
     */
    public boolean isFullScreenMode() {
        return graphicsDevice.getFullScreenWindow() != null;
    }

    /**
     * Switches full screen state.
     * 
     * @param fullScreenMode whether to place the frame in full screen mode or
     *            not.
     */
    public void setFullScreenMode(final boolean fullScreenMode) {
        if (fullScreenMode != isFullScreenMode() || !isVisible()) {
            if (fullScreenMode) {
                switchToFullScreenMode();
            }
            else {
                switchToWindowedMode();
            }
        }
    }

    private void switchToFullScreenMode() {
        addEscapeFullScreenModeListener();

        if (isDisplayable()) {
            dispose();
        }

        setUndecorated(true);
        setResizable(false);
        graphicsDevice.setFullScreenWindow(this);

        if (graphicsDevice.isDisplayChangeSupported()) {
            chooseBestDisplayMode(graphicsDevice);
        }
        validate();
    }

    private void switchToWindowedMode() {
        removeEscapeFullScreenModeListener();

        if (isDisplayable()) {
            dispose();
        }

        setUndecorated(false);
        setResizable(true);
        graphicsDevice.setFullScreenWindow(null);
        validate();
        setVisible(true);
    }

    /**
     * Sets the display mode to the best device mode that can be determined.
     * 
     * Used in full screen mode.
     * 
     * @param device The graphics device being controlled.
     */
    protected void chooseBestDisplayMode(final GraphicsDevice device) {
        final DisplayMode best = getBestDisplayMode(device);
        if (best != null) {
            device.setDisplayMode(best);
        }
    }

    /**
     * Finds the best display mode the graphics device supports. Based on the
     * preferred modes.
     * 
     * @param device the device being inspected
     * 
     * @return best display mode the given device supports
     */
    protected DisplayMode getBestDisplayMode(final GraphicsDevice device) {
        final Iterator itr = getPreferredDisplayModes(device).iterator();
        while (itr.hasNext()) {
            final DisplayMode each = (DisplayMode) itr.next();
            final DisplayMode[] modes = device.getDisplayModes();
            for (int i = 0; i < modes.length; i++) {
                if (modes[i].getWidth() == each.getWidth() && modes[i].getHeight() == each.getHeight()
                        && modes[i].getBitDepth() == each.getBitDepth()) {
                    return each;
                }
            }
        }

        return null;
    }

    /**
     * By default return the current display mode. Subclasses may override this
     * method to return other modes in the collection.
     * 
     * @param device the device being inspected
     * @return preferred display mode
     */
    protected Collection getPreferredDisplayModes(final GraphicsDevice device) {
        final ArrayList result = new ArrayList();

        result.add(device.getDisplayMode());
        /*
         * result.add(new DisplayMode(640, 480, 32, 0)); result.add(new
         * DisplayMode(640, 480, 16, 0)); result.add(new DisplayMode(640, 480,
         * 8, 0));
         */

        return result;
    }

    /**
     * This method adds a key listener that will take this PFrame out of full
     * screen mode when the escape key is pressed. This is called for you
     * automatically when the frame enters full screen mode.
     */
    public void addEscapeFullScreenModeListener() {
        removeEscapeFullScreenModeListener();
        escapeFullScreenModeListener = new KeyAdapter() {
            public void keyPressed(final KeyEvent aEvent) {
                if (aEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    setFullScreenMode(false);
                }
            }
        };
        canvas.addKeyListener((KeyListener) escapeFullScreenModeListener);
    }

    /**
     * This method removes the escape full screen mode key listener. It will be
     * called for you automatically when full screen mode exits, but the method
     * has been made public for applications that wish to use other methods for
     * exiting full screen mode.
     */
    public void removeEscapeFullScreenModeListener() {
        if (escapeFullScreenModeListener != null) {
            canvas.removeKeyListener((KeyListener) escapeFullScreenModeListener);
            escapeFullScreenModeListener = null;
        }
    }

    // ****************************************************************
    // Initialize
    // ****************************************************************

    /**
     * This method will be called before the initialize() method and will be
     * called on the thread that is constructing this object.
     */
    public void beforeInitialize() {
    }

    /**
     * Subclasses should override this method and add their Piccolo2D
     * initialization code there. This method will be called on the swing event
     * dispatch thread. Note that the constructors of PFrame subclasses may not
     * be complete when this method is called. If you need to initialize some
     * things in your class before this method is called place that code in
     * beforeInitialize();
     */
    public void initialize() {
    }
}
