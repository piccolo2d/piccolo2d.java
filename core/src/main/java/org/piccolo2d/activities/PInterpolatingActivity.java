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
 * <b>PInterpolatingActivity</b> interpolates between two states (source and
 * destination) over the duration of the activity. The interpolation can be
 * either linear or slow- in, slow-out.
 * <P>
 * The mode determines how the activity interpolates between the two states. The
 * default mode interpolates from source to destination, but you can also go
 * from destination to source, and from source to destination to source.
 * <P>
 * A loopCount of greater then one will make the activity reschedule itself when
 * it has finished. This makes the activity loop between the two states.
 * <P>
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PInterpolatingActivity extends PActivity {

    /**
     * Specifies that interpolation will be from the source value to the
     * destination value.
     */
    public static final int SOURCE_TO_DESTINATION = 1;

    /**
     * Specifies that interpolation will be from the destination value to the
     * source value.
     */
    public static final int DESTINATION_TO_SOURCE = 2;

    /**
     * Specifies that interpolation proceed from the source to the destination
     * then back to the source. Can be used to perform flashes. source value.
     */
    public static final int SOURCE_TO_DESTINATION_TO_SOURCE = 3;

    private int mode;
    private boolean slowInSlowOut;
    private int loopCount;
    private boolean firstLoop;

    /**
     * Constructs an interpolating activity that will last the duration given.
     * 
     * @since 1.3
     * @param duration duration in milliseconds of the entire activity
     */
    public PInterpolatingActivity(final long duration) {
        this(duration, PUtil.DEFAULT_ACTIVITY_STEP_RATE, 1, PInterpolatingActivity.SOURCE_TO_DESTINATION);
    }

    /**
     * Constructs an interpolating activity that will last the duration given
     * and will update its target at the given rate.
     * 
     * @param duration duration in milliseconds of the entire activity
     * @param stepRate interval in milliseconds between updates to target
     */
    public PInterpolatingActivity(final long duration, final long stepRate) {
        this(duration, stepRate, 1, PInterpolatingActivity.SOURCE_TO_DESTINATION);
    }

    /**
     * Constructs an interpolating activity that will last the duration given
     * and will update its target at the given rate. Once done, it will repeat
     * the loopCount times.
     * 
     * @param duration duration in milliseconds of the entire activity
     * @param stepRate interval in milliseconds between updates to target
     * @param loopCount # of times to repeat this activity.
     * @param mode controls the direction of the interpolation (source to
     *            destination, destination to source, or source to destination
     *            back to source)
     */
    public PInterpolatingActivity(final long duration, final long stepRate, final int loopCount, final int mode) {
        this(duration, stepRate, System.currentTimeMillis(), loopCount, mode);
    }

    /**
     * Create a new PInterpolatingActivity.
     * <P>
     * 
     * @param duration the length of one loop of the activity
     * @param stepRate the amount of time between steps of the activity
     * @param startTime the time (relative to System.currentTimeMillis()) that
     *            this activity should start. This value can be in the future.
     * @param loopCount number of times the activity should reschedule itself
     * @param mode defines how the activity interpolates between states
     */
    public PInterpolatingActivity(final long duration, final long stepRate, final long startTime, final int loopCount,
            final int mode) {
        super(duration, stepRate, startTime);
        this.loopCount = loopCount;
        this.mode = mode;
        slowInSlowOut = true;
        firstLoop = true;
    }

    /**
     * Set the amount of time that this activity should take to complete, after
     * the startStepping method is called. The duration must be greater then
     * zero so that the interpolation value can be computed.
     * 
     * @param duration new duration of this activity
     */
    public void setDuration(final long duration) {
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration for PInterpolatingActivity must be greater then 0");
        }

        super.setDuration(duration);
    }

    // ****************************************************************
    // Basics.
    // ****************************************************************

    /**
     * Return the mode used for interpolation.
     * 
     * Acceptable values are: SOURCE_TO_DESTINATION, DESTINATION_TO_SOURCE and
     * SOURCE_TO_DESTINATION_TO_SOURCE
     * 
     * @return current mode of this activity
     */
    public int getMode() {
        return mode;
    }

    /**
     * Set the direction in which interpolation is going to occur.
     * 
     * Acceptable values are: SOURCE_TO_DESTINATION, DESTINATION_TO_SOURCE and
     * SOURCE_TO_DESTINATION_TO_SOURCE
     * 
     * @param mode the new mode to use when interpolating
     */
    public void setMode(final int mode) {
        this.mode = mode;
    }

    /**
     * Return the number of times the activity should automatically reschedule
     * itself after it has finished.
     * 
     * @return number of times to repeat this activity
     */
    public int getLoopCount() {
        return loopCount;
    }

    /**
     * Set the number of times the activity should automatically reschedule
     * itself after it has finished.
     * 
     * @param loopCount number of times to repeat this activity
     */
    public void setLoopCount(final int loopCount) {
        this.loopCount = loopCount;
    }

    /**
     * Return true if the activity is executing its first loop. Subclasses
     * normally initialize their source state on the first loop.
     * 
     * @return true if executing first loop
     */
    public boolean getFirstLoop() {
        return firstLoop;
    }

    /**
     * Set if the activity is executing its first loop. Subclasses normally
     * initialize their source state on the first loop. This method will rarely
     * need to be called, unless your are reusing activities.
     * 
     * @param firstLoop true if executing first loop
     */
    public void setFirstLoop(final boolean firstLoop) {
        this.firstLoop = firstLoop;
    }

    /**
     * Returns whether this interpolation accelerates and then decelerates as it
     * interpolates.
     * 
     * @return true if accelerations are being applied apply
     */
    public boolean getSlowInSlowOut() {
        return slowInSlowOut;
    }

    /**
     * Sets whether this interpolation accelerates and then decelerates as it
     * interpolates.
     * 
     * @param isSlowInSlowOut true if this interpolation inovolves some
     *            accelerations
     */
    public void setSlowInSlowOut(final boolean isSlowInSlowOut) {
        slowInSlowOut = isSlowInSlowOut;
    }

    // ****************************************************************
    // Stepping - Instead of overriding the step methods subclasses
    // of this activity will normally override setRelativeTargetValue().
    // This method will be called for every step of the activity with
    // a value ranging from 0,0 (for the first step) to 1.0 (for the
    // final step). See PTransformActivity for an example.
    // ****************************************************************

    /**
     * Called when activity is started. Makes sure target value is set properly
     * for start of activity.
     */
    protected void activityStarted() {
        super.activityStarted();
        setRelativeTargetValueAdjustingForMode(0);
    }

    /**
     * Called at each step of the activity. Sets the current position taking
     * mode into account.
     * 
     * @param elapsedTime number of milliseconds since the activity began
     */

    protected void activityStep(final long elapsedTime) {
        super.activityStep(elapsedTime);

        float t = elapsedTime / (float) getDuration();

        t = Math.min(1, t);
        t = Math.max(0, t);

        if (getSlowInSlowOut()) {
            t = computeSlowInSlowOut(t);
        }

        setRelativeTargetValueAdjustingForMode(t);
    }

    /**
     * Called whenever the activity finishes. Reschedules it if the value of
     * loopCount is > 0.
     */
    protected void activityFinished() {
        setRelativeTargetValueAdjustingForMode(1);
        super.activityFinished();

        final PActivityScheduler scheduler = getActivityScheduler();
        if (loopCount > 1) {
            if (loopCount != Integer.MAX_VALUE) {
                loopCount--;
            }
            firstLoop = false;
            setStartTime(scheduler.getRoot().getGlobalTime());
            scheduler.addActivity(this);
        }
    }

    /**
     * Stop this activity immediately, and remove it from the activity
     * scheduler. If this activity is currently running then stoppedStepping
     * will be called after it has been removed from the activity scheduler.
     */
    public void terminate() {
        loopCount = 0; // set to zero so that we don't reschedule self.
        super.terminate();
    }

    /**
     * Subclasses should override this method and set the value on their target
     * (the object that they are modifying) accordingly.
     * 
     * @param zeroToOne relative completion of task.
     */
    public void setRelativeTargetValue(final float zeroToOne) {
    }

    /**
     * Computes percent or linear interpolation to apply when taking
     * acceleration into account.
     * 
     * @param zeroToOne Percentage of activity completed
     * @return strength of acceleration
     */
    public float computeSlowInSlowOut(final float zeroToOne) {
        if (zeroToOne < 0.5f) {
            return 2.0f * zeroToOne * zeroToOne;
        }
        else {
            final float complement = 1.0f - zeroToOne;
            return 1.0f - 2.0f * complement * complement;
        }
    }

    /**
     * Assigns relative target value taking the mode into account.
     * 
     * @param zeroToOne Percentage of activity completed
     */
    protected void setRelativeTargetValueAdjustingForMode(final float zeroToOne) {
        final float adjustedZeroToOne;
        switch (mode) {
            case DESTINATION_TO_SOURCE:
                adjustedZeroToOne = 1 - zeroToOne;
                break;

            case SOURCE_TO_DESTINATION_TO_SOURCE:
                if (zeroToOne <= 0.5f) {
                    adjustedZeroToOne = zeroToOne * 2;
                }
                else {
                    adjustedZeroToOne = 2 * (1 - zeroToOne);
                }
                break;
            case SOURCE_TO_DESTINATION:
            default:
                // Just treat the zeroToOne as how far along the interpolation
                // we are.
                adjustedZeroToOne = zeroToOne;
        }

        setRelativeTargetValue(adjustedZeroToOne);
    }
}
