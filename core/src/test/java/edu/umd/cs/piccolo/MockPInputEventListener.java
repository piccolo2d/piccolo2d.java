package edu.umd.cs.piccolo;

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;

public class MockPInputEventListener implements PInputEventListener {
    static class Notification {
        public PInputEvent event;
        public int type;

        public Notification(final PInputEvent event, final int type) {
            this.event = event;
            this.type = type;
        }
    }

    private final List notifications = new ArrayList();

    public void processEvent(final PInputEvent aEvent, final int type) {
        notifications.add(new Notification(aEvent, type));
    }

    public int getNotificationCount() {
        return notifications.size();
    }

    public Notification getNotification(final int index) {
        return (Notification) notifications.get(index);
    }

}
