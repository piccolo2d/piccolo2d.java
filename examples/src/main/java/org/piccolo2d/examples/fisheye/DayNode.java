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

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import org.piccolo2d.PNode;
import org.piccolo2d.util.PPaintContext;


class DayNode extends PNode {
    boolean hasWidthFocus;
    boolean hasHeightFocus;
    ArrayList lines;
    int week;
    int day;
    String dayOfMonthString;

    public DayNode(int week, int day) {
        lines = new ArrayList();
        lines.add("7:00 AM Walk the dog.");
        lines.add("9:30 AM Meet John for Breakfast.");
        lines.add("12:00 PM Lunch with Peter.");
        lines.add("3:00 PM Research Demo.");
        lines.add("6:00 PM Pickup Sarah from gymnastics.");
        lines.add("7:00 PM Pickup Tommy from karate.");
        this.week = week;
        this.day = day;
        this.dayOfMonthString = Integer.toString((week * 7) + day);
        setPaint(Color.BLACK);
    }

    public int getWeek() {
        return week;
    }

    public int getDay() {
        return day;
    }

    public boolean hasHeightFocus() {
        return hasHeightFocus;
    }

    public void setHasHeightFocus(boolean hasHeightFocus) {
        this.hasHeightFocus = hasHeightFocus;
    }

    public boolean hasWidthFocus() {
        return hasWidthFocus;
    }

    public void setHasWidthFocus(boolean hasWidthFocus) {
        this.hasWidthFocus = hasWidthFocus;
    }

    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();

        g2.setPaint(getPaint());
        g2.draw(getBoundsReference());
        g2.setFont(CalendarNode.DEFAULT_FONT);

        float y = (float) getY() + CalendarNode.TEXT_Y_OFFSET;
        paintContext.getGraphics().drawString(dayOfMonthString,
                (float) getX() + CalendarNode.TEXT_X_OFFSET, y);

        if (hasWidthFocus && hasHeightFocus) {
            paintContext.pushClip(getBoundsReference());
            for (int i = 0; i < lines.size(); i++) {
                y += 10;
                g2.drawString((String) lines.get(i),
                        (float) getX() + CalendarNode.TEXT_X_OFFSET, y);
            }
            paintContext.popClip(getBoundsReference());
        }
    }
}
