package edu.umd.cs.piccolo.examples.fisheye;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import edu.umd.cs.piccolo.PCanvas;

public class TabularFisheye extends PCanvas {
    private CalendarNode calendarNode;

    public TabularFisheye() {
        calendarNode = new CalendarNode();
        getLayer().addChild(calendarNode);
        setMinimumSize(new Dimension(300, 300));
        setPreferredSize(new Dimension(600, 600));
        setZoomEventHandler(null);
        setPanEventHandler(null);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent arg0) {
                calendarNode.setBounds(getX(), getY(),
                        getWidth() - 1, getHeight() - 1);
                calendarNode.layoutChildren(false);
            }
        });
    }
}
