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
package org.piccolo2d.examples.fisheye;

import java.awt.Font;

import org.piccolo2d.PNode;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;


class CalendarNode extends PNode {
    static int DEFAULT_NUM_DAYS = 7;
    static int DEFAULT_NUM_WEEKS = 12;
    static int TEXT_X_OFFSET = 1;
    static int TEXT_Y_OFFSET = 10;
    static int DEFAULT_ANIMATION_MILLIS = 250;
    static float FOCUS_SIZE_PERCENT = 0.65f;
    static Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 10);

    int numDays = DEFAULT_NUM_DAYS;
    int numWeeks = DEFAULT_NUM_WEEKS;
    int daysExpanded = 0;
    int weeksExpanded = 0;

    public CalendarNode() {
        for (int week = 0; week < numWeeks; week++) {
            for (int day = 0; day < numDays; day++) {
                addChild(new DayNode(week, day));
            }
        }

        CalendarNode.this.addInputEventListener(new PBasicInputEventHandler() {
            public void mouseReleased(PInputEvent event) {
                DayNode pickedDay = (DayNode) event.getPickedNode();
                if (pickedDay.hasWidthFocus && pickedDay.hasHeightFocus) {
                    setFocusDay(null, true);
                }
                else {
                    setFocusDay(pickedDay, true);
                }
            }
        });
    }

    public void setFocusDay(DayNode focusDay, boolean animate) {
        for (int i = 0; i < getChildrenCount(); i++) {
            DayNode each = (DayNode) getChild(i);
            each.hasWidthFocus = false;
            each.hasHeightFocus = false;
        }

        if (focusDay == null) {
            daysExpanded = 0;
            weeksExpanded = 0;
        }
        else {
            focusDay.hasWidthFocus = true;
            daysExpanded = 1;
            weeksExpanded = 1;

            for (int i = 0; i < numDays; i++) {
                getDay(focusDay.week, i).hasHeightFocus = true;
            }

            for (int i = 0; i < numWeeks; i++) {
                getDay(i, focusDay.day).hasWidthFocus = true;
            }
        }

        layoutChildren(animate);
    }

    public DayNode getDay(int week, int day) {
        return (DayNode) getChild((week * numDays) + day);
    }

    protected void layoutChildren(boolean animate) {
        double focusWidth = 0;
        double focusHeight = 0;

        if (daysExpanded != 0 && weeksExpanded != 0) {
            focusWidth = (getWidth() * FOCUS_SIZE_PERCENT) / daysExpanded;
            focusHeight = (getHeight() * FOCUS_SIZE_PERCENT) / weeksExpanded;
        }

        double collapsedWidth = (getWidth() - (focusWidth * daysExpanded))
                / (numDays - daysExpanded);
        double collapsedHeight = (getHeight() - (focusHeight * weeksExpanded))
                / (numWeeks - weeksExpanded);

        double xOffset = 0;
        double yOffset = 0;
        double rowHeight = 0;
        DayNode each = null;

        for (int week = 0; week < numWeeks; week++) {
            for (int day = 0; day < numDays; day++) {
                each = getDay(week, day);
                double width = collapsedWidth;
                double height = collapsedHeight;

                if (each.hasWidthFocus())
                    width = focusWidth;
                if (each.hasHeightFocus())
                    height = focusHeight;

                if (animate) {
                    each.animateToBounds(xOffset, yOffset, width,
                            height, DEFAULT_ANIMATION_MILLIS).setStepRate(0);
                }
                else {
                    each.setBounds(xOffset, yOffset, width, height);
                }

                xOffset += width;
                rowHeight = height;
            }
            xOffset = 0;
            yOffset += rowHeight;
        }
    }
}
