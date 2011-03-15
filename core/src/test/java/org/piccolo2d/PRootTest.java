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
package org.piccolo2d;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.piccolo2d.PRoot;
import org.piccolo2d.activities.PActivity;

import junit.framework.TestCase;

/**
 * Unit test for PRoot.
 */
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
