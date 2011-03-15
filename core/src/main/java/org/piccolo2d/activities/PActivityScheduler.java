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
package org.piccolo2d.activities;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import org.piccolo2d.PRoot;
import org.piccolo2d.util.PUtil;


/**
 * <b>PActivityScheduler</b> is responsible for maintaining a list of
 * activities. It is given a chance to process these activities from the PRoot's
 * processInputs() method. Most users will not need to use the
 * PActivityScheduler directly, instead you should look at:
 * <ul>
 * <li>PNode.addActivity - to schedule a new activity
 * <li>PActivity.terminate - to terminate a running activity
 * <li>PRoot.processInputs - already calls processActivities for you.
 * </ul>
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PActivityScheduler implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient Timer activityTimer = null;
    private final PRoot root;
    private final List activities;    
    private boolean activitiesChanged;
    private boolean animating;
    private final ArrayList processingActivities;

    /**
     * Constructs an instance of PActivityScheduler. All activities it will
     * schedule will take place on children of the rootNode provided.
     * 
     * @param rootNode root node of all activities to be performed. All nodes
     *            being animated should have this node as an ancestor.
     */
    public PActivityScheduler(final PRoot rootNode) {        
        root = rootNode;
        activities = new ArrayList();
        processingActivities = new ArrayList();
    }

    /**
     * Returns the node from which all activities will be attached.
     * 
     * @return this scheduler's associated root node
     */
    public PRoot getRoot() {
        return root;
    }

    /**
     * Adds the given activity to the scheduler if not already found.
     * 
     * @param activity activity to be scheduled
     */
    public void addActivity(final PActivity activity) {
        addActivity(activity, false);
    }

    /**
     * Add this activity to the scheduler. Sometimes it's useful to make sure
     * that an activity is run after all other activities have been run. To do
     * this set processLast to true when adding the activity.
     * 
     * @param activity activity to be scheduled
     * @param processLast whether or not this activity should be performed after
     *            all other scheduled activities
     */
    public void addActivity(final PActivity activity, final boolean processLast) {
        if (activities.contains(activity)) {
            return;
        }

        activitiesChanged = true;

        if (processLast) {
            activities.add(0, activity);
        }
        else {
            activities.add(activity);
        }

        activity.setActivityScheduler(this);

        if (!getActivityTimer().isRunning()) {
            startActivityTimer();
        }
    }

    /**
     * Removes the given activity from the scheduled activities. Does nothing if
     * it's not found.
     * 
     * @param activity the activity to be removed
     */
    public void removeActivity(final PActivity activity) {
        if (!activities.contains(activity)) {
            return;
        }

        activitiesChanged = true;
        activities.remove(activity);

        if (activities.size() == 0) {
            stopActivityTimer();
        }
    }

    /**
     * Removes all activities from the list of scheduled activities.
     */
    public void removeAllActivities() {
        activitiesChanged = true;
        activities.clear();
        stopActivityTimer();
    }

    /**
     * Returns a reference to the current activities list. Handle with care.
     * 
     * @return reference to the current activities list.
     */
    public List getActivitiesReference() {
        return activities;
    }

    /**
     * Process all scheduled activities for the given time. Each activity is
     * given one "step", equivalent to one frame of animation.
     * 
     * @param currentTime the current unix time in milliseconds.
     */
    public void processActivities(final long currentTime) {
        final int size = activities.size();
        if (size > 0) {
            processingActivities.addAll(activities);
            for (int i = size - 1; i >= 0; i--) {
                final PActivity each = (PActivity) processingActivities.get(i);
                each.processStep(currentTime);
            }
            processingActivities.clear();
        }
    }

    /**
     * Return true if any of the scheduled activities are animations.
     * 
     * @return true if any of the scheduled activities are animations.
     */
    public boolean getAnimating() {
        if (activitiesChanged) {
            animating = false;
            for (int i = 0; i < activities.size(); i++) {
                final PActivity each = (PActivity) activities.get(i);
                animating |= each.isAnimation();
            }
            activitiesChanged = false;
        }
        return animating;
    }

    /**
     * Starts the current activity timer. Multiple calls to this method are
     * ignored.
     */
    protected void startActivityTimer() {
        getActivityTimer().start();
    }

    /**
     * Stops the current activity timer.
     */
    protected void stopActivityTimer() {
        getActivityTimer().stop();
    }

    /**
     * Returns the activity timer. Creating it if necessary.
     * 
     * @return a Timer instance.
     */
    protected Timer getActivityTimer() {
        if (activityTimer == null) {
            activityTimer = root.createTimer(PUtil.ACTIVITY_SCHEDULER_FRAME_DELAY, new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    root.processInputs();
                }
            });
        }
        return activityTimer;
    }
}
