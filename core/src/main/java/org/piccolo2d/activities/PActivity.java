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

import org.piccolo2d.util.PUtil;

/**
 * <b>PActivity</b> controls some time dependent aspect of Piccolo, such as
 * animation. Once created activities must be scheduled with the
 * PActivityScheduler managed by the PRoot to run. They are automatically
 * removed from the scheduler when the animation has finished.
 * <P>
 * See the PNode.animate*() methods for an example of how to set up and run
 * different activities.
 * <P>
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PActivity {
    /**
     * Parameter for terminate that signifies that activity should bail out
     * immediately without flagging activity as finished.
     */
    public static final int TERMINATE_WITHOUT_FINISHING = 0;

    /**
     * Parameter for terminate that signifies that activity should bail out
     * immediately, but flag activity as finished.
     */
    public static final int TERMINATE_AND_FINISH = 1;

    /**
     * Parameter for terminate that signifies that activity should bail out
     * immediately, if currently active.
     */
    public static final int TERMINATE_AND_FINISH_IF_STEPPING = 2;

    /** Activity scheduler that this activity is bound to. */
    private PActivityScheduler scheduler;

    /** Time at which this activity should start in PRoot global time. */
    private long startTime;

    /** Duration in milliseconds that this activity should last. */
    private long duration;

    /** How many milliseconds should pass between steps. */
    private long stepRate;

    private PActivityDelegate delegate;

    /** Whether this activity is currently active. */
    private boolean stepping;

    /** Next time at which step should occur. */
    private long nextStepTime;

    /**
     * Constructs a new PActivity.
     * 
     * @param aDuration the amount of time that this activity should take to
     *            complete, -1 for infinite.
     */
    public PActivity(final long aDuration) {
        this(aDuration, PUtil.DEFAULT_ACTIVITY_STEP_RATE);
    }

    /**
     * Constructs a new PActivity.
     * 
     * @param aDuration the amount of time that this activity should take to
     *            complete, -1 for infinite.
     * @param aStepRate the maximum rate that this activity should receive step
     *            events.
     */
    public PActivity(final long aDuration, final long aStepRate) {
        this(aDuration, aStepRate, System.currentTimeMillis());
    }

    /**
     * Constructs a new PActivity.
     * 
     * @param aDuration the amount of time that this activity should take to
     *            complete, -1 for infinite.
     * @param aStepRate the maximum rate that this activity should receive step
     *            events.
     * @param aStartTime the time (relative to System.currentTimeMillis()) that
     *            this activity should start.
     */
    public PActivity(final long aDuration, final long aStepRate, final long aStartTime) {
        duration = aDuration;
        stepRate = aStepRate;
        startTime = aStartTime;
        nextStepTime = aStartTime;
        stepping = false;
    }

    // ****************************************************************
    // Basics
    // ****************************************************************

    /**
     * Return the time that this activity should start running in PRoot global
     * time. When this time is reached (or soon after) this activity will have
     * its startStepping() method called.
     * 
     * @return time at which this activity should start in PRoot global time.
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Set the time that this activity should start running in PRoot global
     * time. When this time is reached (or soon after) this activity will have
     * its startStepping() method called.
     * 
     * @param aTriggerTime time at which you want this activity to begin in
     *            PRoot global time
     */
    public void setStartTime(final long aTriggerTime) {
        startTime = aTriggerTime;
    }

    /**
     * Return the amount of time that this activity should delay between steps.
     * 
     * @return the desired milliseconds between steps
     */
    public long getStepRate() {
        return stepRate;
    }

    /**
     * Set the amount of time that this activity should delay between steps.
     * 
     * @param aStepRate desired step rate in milliseconds between steps
     */
    public void setStepRate(final long aStepRate) {
        stepRate = aStepRate;
    }

    /**
     * Gets the next step time desired for this activity. Exists since some
     * steps might eat into the step rate otherwise.
     * 
     * @return next calculated step time
     */
    public long getNextStepTime() {
        return nextStepTime;
    }

    /**
     * Return the amount of time that this activity should take to complete,
     * after the startStepping method is called.
     * 
     * @return time that this activity should take to complete
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Set the amount of time that this activity should take to complete, after
     * the startStepping method is called.
     * 
     * @param aDuration desired duration this activity should take (-1 for
     *            infinite) once it begins stepping
     */
    public void setDuration(final long aDuration) {
        duration = aDuration;
    }

    /**
     * Returns the activity scheduler associated with this activity.
     * 
     * @return associated scheduler
     */
    public PActivityScheduler getActivityScheduler() {
        return scheduler;
    }

    /**
     * Informs the activity of the scheduler that will be responsible for
     * scheduling it.
     * 
     * @param aScheduler scheduler to associate with this activity
     */
    public void setActivityScheduler(final PActivityScheduler aScheduler) {
        scheduler = aScheduler;
    }

    // ****************************************************************
    // Stepping
    // ****************************************************************

    /**
     * Return true if this activity is stepping.
     * 
     * @return whether this activity is stepping
     */
    public boolean isStepping() {
        return stepping;
    }

    /**
     * Return true if this activity is performing an animation. This is used by
     * the PCanvas to determine if it should set the render quality to
     * PCanvas.animatingRenderQuality or not for each frame it renders.
     * 
     * @return whether this activity is an animation, subclasses can override
     *         this.
     */
    protected boolean isAnimation() {
        return false;
    }

    /**
     * This method is called right before an activity is scheduled to start
     * running. After this method is called step() will be called until the
     * activity finishes.
     */
    protected void activityStarted() {
        if (delegate != null) {
            delegate.activityStarted(this);
        }
    }

    /**
     * This is the method that most activities override to perform their
     * behavior. It will be called repeatedly when the activity is running.
     * 
     * @param elapsedTime the amount of time that has passed relative to the
     *            activities startTime.
     */
    protected void activityStep(final long elapsedTime) {
        if (delegate != null) {
            delegate.activityStepped(this);
        }
    }

    /**
     * This method is called after an activity is has finished running and the
     * activity has been removed from the PActivityScheduler queue.
     */
    protected void activityFinished() {
        if (delegate != null) {
            delegate.activityFinished(this);
        }
    }

    /**
     * Get the delegate for this activity. The delegate is notified when the
     * activity starts and stops stepping.
     * 
     * @return delegate of this activity, may be null
     */
    public PActivityDelegate getDelegate() {
        return delegate;
    }

    /**
     * Set the delegate for this activity. The delegate is notified when the
     * activity starts and stops stepping.
     * 
     * @param delegate delegate that should be informed of activity events
     */
    public void setDelegate(final PActivityDelegate delegate) {
        this.delegate = delegate;
    }

    // ****************************************************************
    // Controlling
    // ****************************************************************

    /**
     * Schedules this activity to start after the first activity has finished.
     * Note that no link is created between these activities, if the startTime
     * or duration of the first activity is later changed this activities start
     * time will not be updated to reflect that change.
     * 
     * @param first activity after which this activity should be scheduled
     */
    public void startAfter(final PActivity first) {
        setStartTime(first.getStartTime() + first.getDuration());
    }

    /**
     * Stop this activity immediately, and remove it from the activity
     * scheduler. The default termination behavior is call activityFinished if
     * the activity is currently stepping. Use terminate(terminationBehavior)
     * use a different termination behavior.
     */
    public void terminate() {
        terminate(TERMINATE_AND_FINISH_IF_STEPPING);
    }

    /**
     * Stop this activity immediately, and remove it from the activity
     * scheduler. The termination behavior determines when and if
     * activityStarted and activityFinished get called. The possible termination
     * behaviors are as follow:
     * 
     * TERMINATE_WITHOUT_FINISHING - The method activityFinished will never get
     * called and so the activity will be terminated midway.
     * TERMINATE_AND_FINISH - The method activityFinished will always get
     * called. And so the activity will always end in it's completed state. If
     * the activity has not yet started the method activityStarted will also be
     * called. TERMINATE_AND_FINISH_IF_STEPPING - The method activityFinished
     * will only be called if the activity has previously started.
     * 
     * @param terminationBehavior behavior to use regarding delegate
     *            notification and event firing
     */
    public void terminate(final int terminationBehavior) {
        if (scheduler != null) {
            scheduler.removeActivity(this);
        }

        switch (terminationBehavior) {
            case TERMINATE_WITHOUT_FINISHING:
                stepping = false;
                break;

            case TERMINATE_AND_FINISH:
                if (stepping) {
                    stepping = false;
                    activityFinished();
                }
                else {
                    activityStarted();
                    activityFinished();
                }

                break;

            case TERMINATE_AND_FINISH_IF_STEPPING:
                if (stepping) {
                    stepping = false;
                    activityFinished();
                }
                break;
            default:
                throw new RuntimeException("Invalid termination behaviour provided to PActivity.terminate");
        }
    }

    /**
     * The activity scheduler calls this method and it is here that the activity
     * decides if it should do a step or not for the given time.
     * 
     * @param currentTime in global root time
     * @return number of milliseconds in global root time before processStep
     *         should be called again, -1 if never
     */
    public long processStep(final long currentTime) {
        // if before start time
        if (currentTime < startTime) {
            return startTime - currentTime;
        }

        // if past stop time
        if (currentTime > getStopTime()) {
            if (stepping) {
                stepping = false;
                scheduler.removeActivity(this);
                activityFinished();
            }
            else {
                activityStarted();
                scheduler.removeActivity(this);
                activityFinished();
            }
            return -1;
        }

        // else should be stepping
        if (!stepping) {
            activityStarted();
            stepping = true;
        }

        if (currentTime >= nextStepTime) {
            activityStep(currentTime - startTime);
            nextStepTime = currentTime + stepRate;
        }

        return stepRate;
    }

    /**
     * Return the time when this activity should finish running. At this time
     * (or soon after) the stoppedStepping method will be called
     * 
     * @return time at which this activity should be stopped
     */
    public long getStopTime() {
        if (duration == -1) {
            return Long.MAX_VALUE;
        }
        return startTime + duration;
    }
    
    /**
     * <b>PActivityDelegate</b> is used by classes to learn about and act on the
     * different states that a PActivity goes through, such as when the activity
     * starts and stops stepping.
     */
    public interface PActivityDelegate {
        /**
         * Gets called when the activity starts.
         * 
         * @param activity activity that started
         */
        void activityStarted(PActivity activity);

        /**
         * Gets called for each step of the activity.
         * 
         * @param activity activity that is stepping
         */
        void activityStepped(PActivity activity);

        /**
         * Gets called when the activity finishes.
         * 
         * @param activity activity that finished
         */
        void activityFinished(PActivity activity);
    }

}
