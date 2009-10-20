package edu.umd.cs.piccolo.examples.fisheye;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

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
