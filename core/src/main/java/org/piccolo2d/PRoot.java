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

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.piccolo2d.activities.PActivity;
import org.piccolo2d.activities.PActivityScheduler;
import org.piccolo2d.util.PDebug;
import org.piccolo2d.util.PNodeFilter;


/**
 * <b>PRoot</b> serves as the top node in Piccolo2D's runtime structure. The
 * PRoot responsible for running the main UI loop that processes input from
 * activities and external events.
 * <P>
 * 
 * @version 1.1
 * @author Jesse Grosjean
 */
public class PRoot extends PNode {

    /**
     * Allows for future serialization code to understand versioned binary
     * formats.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The property name that identifies a change in the set of this root's
     * input sources (see {@link InputSource InputSource}). In any property
     * change event the new value will be a reference to the list of this root's
     * input sources, but old value will always be null.
     */
    public static final String PROPERTY_INPUT_SOURCES = "inputSources";

    /**
     * The property code that identifies a change in the set of this root's
     * input sources (see {@link InputSource InputSource}). In any property
     * change event the new value will be a reference to the list of this root's
     * input sources, but old value will always be null.
     */
    public static final int PROPERTY_CODE_INPUT_SOURCES = 1 << 14;

    /**
     * The property name that identifies a change in this node's interacting
     * state.
     *
     * @since 1.3
     */
    public static final String PROPERTY_INTERACTING_CHANGED = "INTERACTING_CHANGED_NOTIFICATION";

    /**
     * The property code that identifies a change in this node's interacting
     * state.
     *
     * @since 1.3
     */
    public static final int PROPERTY_CODE_INTERACTING_CHANGED = 1 << 13;

    /** Whether this not is currently processing inputs. */
    protected transient boolean processingInputs;

    /** Whether this node needs to have its inputs processed. */
    protected transient boolean processInputsScheduled;

    /** The number of interactions this node is currently participating in. */
    private transient int interacting;

    /**
     * The singleton instance of the default input manager.
     */
    private transient PInputManager defaultInputManager;

    /** The Input Sources that are registered to this node. */
    private final transient List inputSources;

    /**
     * Used to provide a consistent clock time to activities as they are being
     * processed.
     * 
     * Should it happen that an activity step take longer than a millisecond,
     * the next step will be unaffected by the change in clock had it used
     * System.currentMillis().
     */
    private transient long globalTime;

    /**
     * Object responsible for scheduling activities, regardless of where in the
     * scene they take place.
     */
    private final PActivityScheduler activityScheduler;

    /**
     * Construct a new PRoot(). Note the PCanvas already creates a basic scene
     * graph for you so often you will not need to construct your own roots.
     */
    public PRoot() {
        super();
        inputSources = new ArrayList();
        globalTime = System.currentTimeMillis();
        activityScheduler = new PActivityScheduler(this);
    }

    // ****************************************************************
    // Activities
    // ****************************************************************

    /**
     * Add an activity to the activity scheduler associated with this root.
     * Activities are given a chance to run during each call to the roots
     * <code>processInputs</code> method. When the activity has finished running
     * it will automatically get removed.
     * 
     * @param activity Activity that should be scheduled
     * @return whether it has been scheduled (always true)
     */
    public boolean addActivity(final PActivity activity) {
        getActivityScheduler().addActivity(activity);
        return true;
    }

    /**
     * Get the activity scheduler associated with this root.
     * 
     * @return associated scheduler
     */
    public PActivityScheduler getActivityScheduler() {
        return activityScheduler;
    }

    /**
     * Wait for all scheduled activities to finish before returning from this
     * method. This will freeze out user input, and so it is generally
     * recommended that you use PActivities.setTriggerTime() to offset
     * activities instead of using this method.
     */
    public void waitForActivities() {
        final PNodeFilter cameraWithCanvas = new CameraWithCanvasFilter();

        while (activityScheduler.getActivitiesReference().size() > 0) {
            processInputs();
            final Iterator i = getAllNodes(cameraWithCanvas, null).iterator();
            while (i.hasNext()) {
                final PCamera each = (PCamera) i.next();
                each.getComponent().paintImmediately();
            }
        }
    }

    /**
     * Since getRoot is handled recursively, and root is the lowest point in the
     * hierarchy, simply returns itself.
     * 
     * @return itself
     */
    public PRoot getRoot() {
        return this;
    }

    /**
     * Get the default input manager to be used when processing input events.
     * PCanvas's use this method when they forward new swing input events to the
     * PInputManager.
     * 
     * @return a singleton instance of PInputManager
     */
    public PInputManager getDefaultInputManager() {
        if (defaultInputManager == null) {
            defaultInputManager = new PInputManager();
            addInputSource(defaultInputManager);
        }
        return defaultInputManager;
    }

    /**
     * Return true if this root has been marked as interacting. If so the root
     * will normally render at a lower quality that is faster.
     * 
     * @since 1.3
     * @return true if this root has user interaction taking place
     */
    public boolean getInteracting() {
        return interacting > 0;
    }

    /**
     * Set if this root is interacting. If so the root will normally render at a
     * lower quality that is faster. Also repaints the root if the the
     * interaction has ended.
     * <p/>
     * This has similar functionality to the setInteracting method on Canvas,
     * but this is the appropriate place to mark interactions that may occur in
     * multiple canvases if this Root is shared.
     * 
     * @since 1.3
     * @param isInteracting True if this root has user interaction taking place
     * @see PCanvas#setInteracting(boolean)
     */
    public void setInteracting(final boolean isInteracting) {
        final boolean wasInteracting = getInteracting();

        if (isInteracting) {
            interacting++;
        }
        else {
            interacting--;
        }

        if (!isInteracting && !getInteracting()) {
            // force all the child cameras to repaint
            for (int i = 0; i < getChildrenCount(); i++) {
                final PNode child = getChild(i);
                if (child instanceof PCamera) {
                    child.repaint();
                }
            }

        }
        if (wasInteracting != isInteracting) {
            firePropertyChange(PROPERTY_CODE_INTERACTING_CHANGED, PROPERTY_INTERACTING_CHANGED, Boolean
                    .valueOf(wasInteracting), Boolean.valueOf(isInteracting));
        }
    }

    /**
     * Advanced. If you want to add additional input sources to the roots UI
     * process you can do that here. You will seldom do this unless you are
     * making additions to the Piccolo2D framework.
     * 
     * @param inputSource An input source that should be added
     */
    public void addInputSource(final InputSource inputSource) {
        inputSources.add(inputSource);
        firePropertyChange(PROPERTY_CODE_INPUT_SOURCES, PROPERTY_INPUT_SOURCES, null, inputSources);
    }

    /**
     * Advanced. If you want to remove the default input source from the roots
     * UI process you can do that here. You will seldom do this unless you are
     * making additions to the Piccolo2D framework.
     * 
     * @param inputSource input source that should no longer be asked about
     *            input events
     */
    public void removeInputSource(final InputSource inputSource) {
        if (inputSources.remove(inputSource)) {
            firePropertyChange(PROPERTY_CODE_INPUT_SOURCES, PROPERTY_INPUT_SOURCES, null, inputSources);
        }
    }

    /**
     * Returns a new timer. This method allows subclasses, such as PSWTRoot to
     * create custom timers that will be used transparently by the Piccolo2D
     * framework.
     * 
     * @param delay # of milliseconds before action listener is invoked
     * @param listener listener to be invoked after delay
     * 
     * @return A new Timer
     */
    public Timer createTimer(final int delay, final ActionListener listener) {
        return new Timer(delay, listener);
    }

    // ****************************************************************
    // UI Loop - Methods for running the main UI loop of Piccolo2D.
    // ****************************************************************

    /**
     * Get the global Piccolo2D time. This is set to System.currentTimeMillis()
     * at the beginning of the roots <code>processInputs</code> method.
     * Activities should usually use this global time instead of System.
     * currentTimeMillis() so that multiple activities will be synchronized.
     * 
     * @return time as recorded at the beginning of activity scheduling
     */
    public long getGlobalTime() {
        return globalTime;
    }

    /**
     * This is the heartbeat of the Piccolo2D framework. Pending input events
     * are processed. Activities are given a chance to run, and the bounds
     * caches and any paint damage is validated.
     */
    public void processInputs() {
        PDebug.startProcessingInput();
        processingInputs = true;

        globalTime = System.currentTimeMillis();
        if (inputSources.size() > 0) {
            final Iterator inputSourceIterator = inputSources.iterator();
            while (inputSourceIterator.hasNext()) {
                final InputSource each = (InputSource) inputSourceIterator.next();
                each.processInput();
            }
        }

        activityScheduler.processActivities(globalTime);
        validateFullBounds();
        validateFullPaint();

        processingInputs = false;
        PDebug.endProcessingInput();
    }

    /** {@inheritDoc} */
    public void setFullBoundsInvalid(final boolean fullLayoutInvalid) {
        super.setFullBoundsInvalid(fullLayoutInvalid);
        scheduleProcessInputsIfNeeded();
    }

    /** {@inheritDoc} */
    public void setChildBoundsInvalid(final boolean childLayoutInvalid) {
        super.setChildBoundsInvalid(childLayoutInvalid);
        scheduleProcessInputsIfNeeded();
    }

    /** {@inheritDoc} */
    public void setPaintInvalid(final boolean paintInvalid) {
        super.setPaintInvalid(paintInvalid);
        scheduleProcessInputsIfNeeded();
    }

    /** {@inheritDoc} */
    public void setChildPaintInvalid(final boolean childPaintInvalid) {
        super.setChildPaintInvalid(childPaintInvalid);
        scheduleProcessInputsIfNeeded();
    }

    /**
     * Schedule process inputs if needed.
     */
    public void scheduleProcessInputsIfNeeded() {
        /*
         * The reason for the special case here (when not in the event dispatch
         * thread) is that the SwingUtilitiles.invokeLater code below only
         * invokes later with respect to the event dispatch thread, it will
         * invoke concurrently with other threads.
         */
        if (!SwingUtilities.isEventDispatchThread()) {
            /*
             * Piccolo2D is not thread safe and should almost always be called
             * from the Swing event dispatch thread. It should only reach this
             * point when a new canvas is being created.
             */
            return;
        }

        PDebug.scheduleProcessInputs();

        if (!processInputsScheduled && !processingInputs
                && (getFullBoundsInvalid() || getChildBoundsInvalid() || getPaintInvalid() || getChildPaintInvalid())) {

            processInputsScheduled = true;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    processInputs();
                    processInputsScheduled = false;
                }
            });
        }
    }

    private static final class CameraWithCanvasFilter implements PNodeFilter {
        public boolean accept(final PNode aNode) {
            return aNode instanceof PCamera && ((PCamera) aNode).getComponent() != null;
        }

        public boolean acceptChildrenOf(final PNode aNode) {
            return true;
        }
    }

    /**
     * This interfaces is for advanced use only. If you want to implement a
     * different kind of input framework then Piccolo2D provides you can hook it
     * in here.
     */
    public static interface InputSource {
        /** Causes the system to process any pending Input Events. */
        void processInput();
    }
}
