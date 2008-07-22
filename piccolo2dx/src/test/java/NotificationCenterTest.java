import edu.umd.cs.piccolox.event.PNotification;
import edu.umd.cs.piccolox.event.PNotificationCenter;

import junit.framework.TestCase;

public class NotificationCenterTest extends TestCase {

	boolean changed1;
	boolean changed2;
	boolean changed3;
	boolean changed4;

	public NotificationCenterTest(String name) {
		super(name);
	}

	public void testToString() {
		PNotificationCenter center = PNotificationCenter.defaultCenter();
		
		center.addListener(this, "changed1", "propertyChanged", this);
		center.addListener(this, "changed2", null, this);
		center.addListener(this, "changed3", "propertyChanged", null);
		center.addListener(this, "changed4", null, null);
		
		center.postNotification("propertyChanged", this);
		assertTrue(changed1 && changed2 && changed3 && changed4);
		changed1 = changed2 = changed3 = changed4 = false;
		
		center.postNotification("propertyChanged", new Object());
		assertTrue(!changed1 && !changed2 && changed3 && changed4);
		changed1 = changed2 = changed3 = changed4 = false;

		center.postNotification("otherPropertyChanged", this);
		assertTrue(!changed1 && changed2 && !changed3 && changed4);
		changed1 = changed2 = changed3 = changed4 = false;

		center.postNotification("otherPropertyChanged", new Object());
		assertTrue(!changed1 && !changed2 && !changed3 && changed4);
		changed1 = changed2 = changed3 = changed4 = false;
	}
	
	public void changed1(PNotification notification) {
		changed1 = true;
	}

	public void changed2(PNotification notification) {
		changed2 = true;
	}
	
	public void changed3(PNotification notification) {
		changed3 = true;
	}

	public void changed4(PNotification notification) {
		changed4 = true;
	}	
}
