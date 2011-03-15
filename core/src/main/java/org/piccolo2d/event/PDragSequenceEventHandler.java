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
package org.piccolo2d.event;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.piccolo2d.activities.PActivity;
import org.piccolo2d.util.PUtil;


/**
 * <b>PDragSequenceEventHandler</b> is designed to support mouse pressed,
 * dragged, and released interaction sequences. Support is also provided for
 * running a continuous activity during the drag sequence.
 * <P>
 * PDragSequenceEventHandler should be subclassed by a concrete event handler
 * that implements a particular interaction. See PPanEventHandler,
 * PZoomEventHandler, and PDragEventHandler for examples.
 * <P>
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public abstract class PDragSequenceEventHandler extends PBasicInputEventHandler {

    private double minDragStartDistance = 0;
    private transient boolean isDragging = false;
    private transient Point2D mousePressedCanvasPoint;
    private transient PActivity dragActivity;
    private transient PInputEvent dragEvent;
    private transient int sequenceInitiatedButton = MouseEvent.NOBUTTON;

    /** Constructs a drag sequence event handler instance. */
    public PDragSequenceEventHandler() {
    }

    /**
     * Returns true if this event handler is in the process of handling a drag.
     * 
     * @return true if handling a drag
     */
    public boolean isDragging() {
        return isDragging;
    }

    /**
     * Used to inform this handler that it is in the process of handling a drag.
     * 
     * @param isDragging true if handler is processing a drag
     */
    public void setIsDragging(final boolean isDragging) {
        this.isDragging = isDragging;
    }

    /**
     * Returns the minimum distance (in screen coordinates) before a pressed
     * mouse movement is registered as a drag event. The smaller this value the
     * more clicks will be incorrectly recognized as drag events.
     * 
     * @return minimum distance a pressed mouse must move before it is
     *         registered as a drag
     */
    public double getMinDragStartDistance() {
        return minDragStartDistance;
    }

    /**
     * Set the minimum distance that the mouse should be dragged (in screen
     * coordinates) before a new drag sequence is initiate.
     * 
     * @param minDistance in screen coordinates
     */
    public void setMinDragStartDistance(final double minDistance) {
        minDragStartDistance = minDistance;
    }

    /**
     * Return the point in canvas coordinates where the mouse was last pressed.
     * 
     * @return point in canvas coordinates of last mouse press
     */
    public Point2D getMousePressedCanvasPoint() {
        if (mousePressedCanvasPoint == null) {
            mousePressedCanvasPoint = new Point2D.Double();
        }
        return mousePressedCanvasPoint;
    }

    // ****************************************************************
    // Dragging - Methods to indicate the stages of the drag sequence.
    // ****************************************************************

    /**
     * Subclasses should override this method to get notified of the start of a
     * new drag sequence. Note that that overriding methods must still call
     * super.startDrag() for correct behavior.
     * 
     * @param event event that started the drag sequence
     */
    protected void startDrag(final PInputEvent event) {
        dragEvent = event;
        startDragActivity(event);
        setIsDragging(true);
        event.getComponent().setInteracting(true);
    }

    /**
     * Subclasses should override this method to get notified of the drag events
     * in a drag sequence. Note that that overriding methods must still call
     * super.startDrag() for correct behavior.
     * 
     * @param event event that caused the drag
     */
    protected void drag(final PInputEvent event) {
        dragEvent = event;
    }

    /**
     * Subclasses should override this method to get notified of the end event
     * in a drag sequence. Note that that overriding methods must still call
     * super.startDrag() for correct behavior.
     * 
     * @param event event that ended the drag sequence
     */
    protected void endDrag(final PInputEvent event) {
        stopDragActivity(event);
        dragEvent = null;
        event.getComponent().setInteracting(false);
        setIsDragging(false);
    }

    /**
     * Returns true if the provided event represents a valid start for a drag
     * sequence.
     * 
     * Subclasses should override this method to add criteria for the start of a
     * drag sequence. Subclasses are still responsible for calling
     * super.shouldStartDragInteraction()
     * 
     * @param event event being tested
     * @return true if provided event is a good start to a drag sequence
     */
    protected boolean shouldStartDragInteraction(final PInputEvent event) {
        return getMousePressedCanvasPoint().distance(event.getCanvasPosition()) >= getMinDragStartDistance();
    }

    // ****************************************************************
    // Drag Activity - Used for scheduling an activity during a drag
    // sequence. For example zooming and auto panning are implemented
    // using this.
    // ****************************************************************

    /**
     * Returns the scheduled activity that's updating the scene as a result to
     * the current drag activity (if any).
     * 
     * @return scheduled activity that's updating the scene as a result to the
     *         drag activity
     */
    protected PActivity getDragActivity() {
        return dragActivity;
    }

    /**
     * Schedules the "infinite" drag activity so that auto-panning and zooming
     * will continue to update the scene even if there are no further drag
     * events fired. For example, if the mouse is dragged to the right while
     * pressing the right mouse button and then paused for a while, the scene
     * should continue to zoom in.
     * 
     * @param event the event that's responsible for the start of the activity
     */
    protected void startDragActivity(final PInputEvent event) {
        dragActivity = new PActivity(-1, PUtil.DEFAULT_ACTIVITY_STEP_RATE);
        dragActivity.setDelegate(new PActivity.PActivityDelegate() {
            public void activityStarted(final PActivity activity) {
                dragActivityFirstStep(dragEvent);
            }

            public void activityStepped(final PActivity activity) {
                dragActivityStep(dragEvent);
            }

            public void activityFinished(final PActivity activity) {
                dragActivityFinalStep(dragEvent);
            }
        });

        event.getCamera().getRoot().addActivity(dragActivity);
    }

    /**
     * Stops the activity responsible for updating the scene.
     * 
     * @param event The event responsible for stopping the drag activity
     */
    protected void stopDragActivity(final PInputEvent event) {
        dragActivity.terminate();
        dragActivity = null;
    }

    /**
     * Subclasses override this method to get notified when the drag activity
     * starts stepping.
     * 
     * @param event the event responsible for the first step in the drag
     *            activity
     */
    protected void dragActivityFirstStep(final PInputEvent event) {
    }

    /**
     * During a drag sequence an activity is scheduled that runs continuously
     * while the drag sequence is active. This can be used to support some
     * additional behavior that is not driven directly by mouse events. For
     * example PZoomEventHandler uses it for zooming and PPanEventHandler uses
     * it for auto panning.
     * 
     * @param event the event encapsulating the callback context for the
     *            activity step
     */
    protected void dragActivityStep(final PInputEvent event) {
    }

    /**
     * Subclasses should override this method to get notified when the drag
     * activity stops stepping.
     * 
     * @param aEvent the event responsible for ending the activity
     */
    protected void dragActivityFinalStep(final PInputEvent aEvent) {
    }

    /**
     * Subclasses should not override this method, instead they should
     * override the appropriate drag callbacks.
     * 
     * @param event The event to be queried about the details of the mouse press
     */
    public void mousePressed(final PInputEvent event) {
        super.mousePressed(event);

        if (sequenceInitiatedButton == MouseEvent.NOBUTTON) {
            sequenceInitiatedButton = event.getButton();
            
            getMousePressedCanvasPoint().setLocation(event.getCanvasPosition());
            if (!isDragging() && shouldStartDragInteraction(event)) {
                startDrag(event);
            }
        }
    }

    /**
     * Subclasses should not override this method, instead they should
     * override the appropriate drag method.
     * 
     * @param event The event to be queried about the details of the mouse press
     */
    public void mouseDragged(final PInputEvent event) {
        super.mouseDragged(event);

        if (sequenceInitiatedButton != MouseEvent.NOBUTTON) {
            if (!isDragging()) {
                if (shouldStartDragInteraction(event)) {
                    startDrag(event);
                }
                return;
            }
            drag(event);
        }
    }

    /**
     * Subclasses should not override this method, instead they should
     * override the appropriate drag method.
     * 
     * @param event The event to be queried about the details of the mouse release
     */
    public void mouseReleased(final PInputEvent event) {
        super.mouseReleased(event);
        if (sequenceInitiatedButton == event.getButton()) {
            if (isDragging()) {
                endDrag(event);
            }
            sequenceInitiatedButton = MouseEvent.NOBUTTON;
        }
    }
}
