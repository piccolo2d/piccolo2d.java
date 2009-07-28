package edu.umd.cs.piccolo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import junit.framework.TestCase;
import edu.umd.cs.piccolo.activities.PActivity;

public class PRootTest extends TestCase {
    private PRoot root;
    private MockPropertyChangeListener mockListener;

    public void setUp() {
        root = new PRoot();
        mockListener = new MockPropertyChangeListener();
    }

    public void testActivityScheduleIsNotNullByDefault() {
        assertNotNull(root.getActivityScheduler());
    }

    public void testGetRootReturnsItself() {
        assertSame(root, root.getRoot());
    }

    public void testGetDefaultInputManagerIsNotNullByDefault() {
        assertNotNull(root.getDefaultInputManager());
    }

    public void testAddInputSourceFirePropertyChangeEvent() {
        root.addPropertyChangeListener(PRoot.PROPERTY_INPUT_SOURCES, mockListener);

        final PRoot.InputSource newSource = new PRoot.InputSource() {
            public void processInput() {

            }
        };
        root.addInputSource(newSource);

        assertEquals(1, mockListener.getPropertyChangeCount());
    }

    public void testCreateTimerReturnsATimer() {
        final Timer timer = root.createTimer(1, new ActionListener() {
            public void actionPerformed(final ActionEvent arg0) {
            }
        });
        assertNotNull(timer);
    }

    public void testCreateTimerReturnsATimerWhenDelayIs0() {
        final Timer timer = root.createTimer(0, new ActionListener() {
            public void actionPerformed(final ActionEvent arg0) {
            }
        });
        assertNotNull(timer);
    }

    public void testRemoveInputSourceDoesNothingIfStranger() {
        final PRoot.InputSource strangeSource = new PRoot.InputSource() {
            public void processInput() {

            }
        };

        root.removeInputSource(strangeSource);
    }

    public void testGlobalTimeIsNotZeroBeforeCallToProcessInputs() {
        assertFalse(0 == root.getGlobalTime());
    }

    public void testProcessInputDelegatesToInputSources() {
        final MockInputSource newSource = new MockInputSource();
        root.addInputSource(newSource);
        root.processInputs();
        assertEquals(1, newSource.getProcessInputCalls());
    }

    public void testProcessInputProcessesActivities() {
        final MockPActivity activity = new MockPActivity(100);
        root.addActivity(activity);
        root.processInputs();
        assertTrue(activity.isActivityStarted());

    }

    public void testSetFullBoundsInvalidPerists() {
        root.setFullBoundsInvalid(true);
        assertTrue(root.getFullBoundsInvalid());
    }

    public void testSetChildBoundsInvalidPerists() {
        root.setChildBoundsInvalid(true);
        assertTrue(root.getChildBoundsInvalid());
    }

    public void testSetPaintInvalidPersists() {
        root.setPaintInvalid(true);
        assertTrue(root.getPaintInvalid());
    }

    public void testSetChildPaintInvalidPersists() {
        root.setChildPaintInvalid(true);
        assertTrue(root.getChildPaintInvalid());
    }

    public void testWaitForActivitiesDoesSo() {
        final MockPActivity activity = new MockPActivity(1);
        root.addActivity(activity);
        root.waitForActivities();
        assertTrue(activity.isActivityFished());
    }

    private static final class MockInputSource implements PRoot.InputSource {
        private int processInputCalls;

        public int getProcessInputCalls() {
            return processInputCalls;
        }

        public void processInput() {
            processInputCalls++;
        }
    }

    private static final class MockPActivity extends PActivity {
        private boolean activityStarted;
        private boolean activityFinished;

        private MockPActivity(final long aDuration) {
            super(aDuration);
        }

        public boolean isActivityFished() {
            return activityFinished;
        }

        public boolean isActivityStarted() {
            return activityStarted;
        }

        protected void activityStarted() {
            activityStarted = true;
            super.activityStarted();
        }

        protected void activityFinished() {
            activityFinished = true;
            super.activityFinished();
        }
    }
}
