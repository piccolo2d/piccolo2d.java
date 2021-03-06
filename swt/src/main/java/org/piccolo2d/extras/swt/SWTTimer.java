/*
 * Copyright (c) 2008-2019, Piccolo2D project, http://piccolo2d.org
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.eclipse.swt.widgets.Display;

/**
 * SWT timer.
 * 
 * @author Lance Good
 */
public class SWTTimer extends Timer {
    private static final long serialVersionUID = 1L;

    private boolean notify = false;

    private int initialDelay;
    private int delay;
    private boolean repeats = true;
    private boolean coalesce = true;
    private Runnable doPostEvent = null;
    private Display display = null;

    // These fields are maintained by TimerQueue.
    // eventQueued can also be reset by the TimerQueue, but will only ever
    // happen in applet case when TimerQueues thread is destroyed.
    private long expirationTime;
    private SWTTimer nextTimer;
    boolean running;

    /**
     * DoPostEvent is a runnable class that fires actionEvents to the listeners
     * on the EventDispatchThread, via invokeLater.
     * 
     * @see #post
     */
    class SWTDoPostEvent implements Runnable {
        public void run() {
            if (notify) {
                fireActionPerformed(new ActionEvent(SWTTimer.this, 0, null, System.currentTimeMillis(), 0));
                if (coalesce) {
                    cancelEventOverride();
                }
            }
        }

        SWTTimer getTimer() {
            return SWTTimer.this;
        }
    }

    /**
     * Constructor for SWTTimer.
     * 
     * @param display display associated with this timer
     * @param delay time in milliseconds between firings of this timer
     * @param listener action listener to fire when the timer fires
     */
    public SWTTimer(final Display display, final int delay, final ActionListener listener) {
        super(delay, listener);
        this.delay = delay;
        initialDelay = delay;

        doPostEvent = new SWTDoPostEvent();
        this.display = display;
    }

    /**
     * Notifies all listeners that have registered interest for notification on
     * this event type.
     * 
     * @param e the action event to fire
     */
    protected void fireActionPerformed(final ActionEvent e) {
        // Guaranteed to return a non-null array
        final Object[] listeners = listenerList.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                ((ActionListener) listeners[i + 1]).actionPerformed(e);
            }
        }
    }

    /**
     * Returns the timer queue.
     */
    SWTTimerQueue timerQueue() {
        return SWTTimerQueue.sharedInstance(display);
    }

    /**
     * Sets the <code>Timer</code>'s delay, the number of milliseconds between
     * successive action events.
     * 
     * @param delay the delay in milliseconds
     * @see #setInitialDelay
     */
    public void setDelay(final int delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("Invalid delay: " + delay);
        }
        else {
            this.delay = delay;
        }
    }

    /**
     * Returns the delay, in milliseconds, between firings of action events.
     * 
     * @see #setDelay
     * @see #getInitialDelay
     * @return delay in milliseconds between firings of this timer
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Sets the <code>Timer</code>'s initial delay, which by default is the same
     * as the between-event delay. This is used only for the first action event.
     * Subsequent action events are spaced using the delay property.
     * 
     * @param initialDelay the delay, in milliseconds, between the invocation of
     *            the <code>start</code> method and the first action event fired
     *            by this timer
     * 
     * @see #setDelay
     */
    public void setInitialDelay(final int initialDelay) {
        if (initialDelay < 0) {
            throw new IllegalArgumentException("Invalid initial delay: " + initialDelay);
        }
        else {
            this.initialDelay = initialDelay;
        }
    }

    /**
     * Returns the <code>Timer</code>'s initial delay. By default this is the
     * same as the value returned by getDelay.
     * 
     * @see #setInitialDelay
     * @see #setDelay
     * @return the initial delay of this timer
     */
    public int getInitialDelay() {
        return initialDelay;
    }

    /**
     * If <code>flag</code> is <code>false</code>, instructs the
     * <code>Timer</code> to send only one action event to its listeners.
     * 
     * @param flag specify <code>false</code> to make the timer stop after
     *            sending its first action event
     */
    public void setRepeats(final boolean flag) {
        repeats = flag;
    }

    /**
     * Returns <code>true</code> (the default) if the <code>Timer</code> will
     * send an action event to its listeners multiple times.
     * 
     * @see #setRepeats
     * @return true if this timer should repeat when completed
     */
    public boolean isRepeats() {
        return repeats;
    }

    /**
     * Sets whether the <code>Timer</code> coalesces multiple pending
     * <code>ActionEvent</code> firings. A busy application may not be able to
     * keep up with a <code>Timer</code>'s event generation, causing multiple
     * action events to be queued. When processed, the application sends these
     * events one after the other, causing the <code>Timer</code>'s listeners to
     * receive a sequence of events with no delay between them. Coalescing
     * avoids this situation by reducing multiple pending events to a single
     * event. <code>Timer</code>s coalesce events by default.
     * 
     * @param flag specify <code>false</code> to turn off coalescing
     */
    public void setCoalesce(final boolean flag) {
        final boolean old = coalesce;
        coalesce = flag;
        if (!old && coalesce) {
            // We must do this as otherwise if the Timer once notified
            // in !coalese mode notify will be stuck to true and never
            // become false.
            cancelEventOverride();
        }
    }

    /**
     * Returns <code>true</code> if the <code>Timer</code> coalesces multiple
     * pending action events.
     * 
     * @see #setCoalesce
     * @return true if this timer coalesces multiple pending action events
     */
    public boolean isCoalesce() {
        return coalesce;
    }

    /**
     * Starts the <code>Timer</code>, causing it to start sending action events
     * to its listeners.
     * 
     * @see #stop
     */
    public void start() {
        timerQueue().addTimer(this, System.currentTimeMillis() + getInitialDelay());
    }

    /**
     * Returns <code>true</code> if the <code>Timer</code> is running.
     * 
     * @see #start
     * @return true if this timer is scheduled to run
     */
    public boolean isRunning() {
        return timerQueue().containsTimer(this);
    }

    /**
     * Stops the <code>Timer</code>, causing it to stop sending action events to
     * its listeners.
     * 
     * @see #start
     */
    public void stop() {
        timerQueue().removeTimer(this);
        cancelEventOverride();
    }

    /**
     * Restarts the <code>Timer</code>, canceling any pending firings and
     * causing it to fire with its initial delay.
     */
    public void restart() {
        stop();
        start();
    }

    /**
     * Resets the internal state to indicate this Timer shouldn't notify any of
     * its listeners. This does not stop a repeatable Timer from firing again,
     * use <code>stop</code> for that.
     */
    synchronized void cancelEventOverride() {
        notify = false;
    }

    synchronized void postOverride() {
        if (!notify || !coalesce) {
            notify = true;
            display.asyncExec(doPostEvent);
        }
    }

    /**
     * @param expirationTime the expirationTime to set
     */
    public void setExpirationTime(final long expirationTime) {
        this.expirationTime = expirationTime;
    }

    /**
     * @return the expirationTime
     */
    public long getExpirationTime() {
        return expirationTime;
    }

    /**
     * @param nextTimer the nextTimer to set
     */
    void setNextTimer(final SWTTimer nextTimer) {
        this.nextTimer = nextTimer;
    }

    /**
     * @return the nextTimer
     */
    SWTTimer getNextTimer() {
        return nextTimer;
    }

    /**
     * @param running the running to set
     */
    public void setRunning(final boolean running) {
        this.running = running;
    }

}
