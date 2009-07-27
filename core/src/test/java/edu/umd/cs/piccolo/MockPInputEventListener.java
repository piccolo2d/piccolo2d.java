package edu.umd.cs.piccolo;

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;

public class MockPInputEventListener implements PInputEventListener {
	static class Notification {
		public PInputEvent event;
		public int type;
		
		public Notification(PInputEvent event, int type) {
			this.event = event;
			this.type = type;
		}
	}
	
	private List notifications = new ArrayList();
	
	public void processEvent(PInputEvent aEvent, int type) {
		notifications.add(new Notification(aEvent, type));
	}
	
	public int getNotificationCount() {
		return notifications.size();
	}
	
	public Notification getNotification(int index) {
		return (Notification) notifications.get(index);
	}

}
