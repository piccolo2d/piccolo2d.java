/*
 * Copyright (c) 2008-2011, Piccolo2D project, http://piccolo2d.org
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

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.piccolo2d.PInputManager;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PInputEventFilter;

import junit.framework.TestCase;

/**
 * Unit test for PInputEventFilter.
 */
public class PInputEventFilterTest extends TestCase {
    private PInputEventFilter filter;

    public void setUp() {
        filter = new PInputEventFilter();
    }

    public void testAcceptsAlreadyHandledEventsFalseByDefault() {
        assertFalse(filter.getAcceptsAlreadyHandledEvents());
    }

    public void testDoesNotMarkEventsHandledByDefault() {
        assertFalse(filter.getMarksAcceptedEventsAsHandled());
    }

    public void testAcceptsEverythingByDefault() {
        assertAcceptsAll();
    }

    public void testMaskDoesNotAffectReportedAccepts() {
        filter = new PInputEventFilter(0);
        assertAcceptsAll();
    }

    private void assertAcceptsAll() {
        assertTrue(filter.getAcceptsFocusEvents());
        assertTrue(filter.getAcceptsKeyPressed());
        assertTrue(filter.getAcceptsKeyReleased());
        assertTrue(filter.getAcceptsKeyTyped());
        assertTrue(filter.getAcceptsMouseClicked());
        assertTrue(filter.getAcceptsMouseDragged());
        assertTrue(filter.getAcceptsMouseEntered());
        assertTrue(filter.getAcceptsMouseExited());
        assertTrue(filter.getAcceptsMouseMoved());
        assertTrue(filter.getAcceptsMousePressed());
        assertTrue(filter.getAcceptsMouseReleased());
        assertTrue(filter.getAcceptsMouseWheelRotated());
    }

    public void testRejectsEverythingAfterCallingRejectAllEventTypes() {
        filter.rejectAllEventTypes();
        assertRejectsAll();
    }

    private void assertRejectsAll() {
        assertFalse(filter.getAcceptsFocusEvents());
        assertFalse(filter.getAcceptsKeyPressed());
        assertFalse(filter.getAcceptsKeyReleased());
        assertFalse(filter.getAcceptsKeyTyped());
        assertFalse(filter.getAcceptsMouseClicked());
        assertFalse(filter.getAcceptsMouseDragged());
        assertFalse(filter.getAcceptsMouseEntered());
        assertFalse(filter.getAcceptsMouseExited());
        assertFalse(filter.getAcceptsMouseMoved());
        assertFalse(filter.getAcceptsMousePressed());
        assertFalse(filter.getAcceptsMouseReleased());
        assertFalse(filter.getAcceptsMouseWheelRotated());
    }

    public void testSetAcceptsFocusEventsPersists() {
        filter.setAcceptsFocusEvents(false);
        assertFalse(filter.getAcceptsFocusEvents());
    }

    public void testSetAcceptsKeyPressedPersists() {
        filter.setAcceptsKeyPressed(false);
        assertFalse(filter.getAcceptsKeyPressed());
    }

    public void testSetAcceptsKeyReleasedPersists() {
        filter.setAcceptsKeyReleased(false);
        assertFalse(filter.getAcceptsKeyReleased());
    }

    public void testSetAcceptsKeyTypedPersists() {
        filter.setAcceptsKeyTyped(false);
        assertFalse(filter.getAcceptsKeyTyped());
    }

    public void testSetAcceptsMouseClickedPersists() {
        filter.setAcceptsMouseClicked(false);
        assertFalse(filter.getAcceptsMouseClicked());
    }

    public void testSetAcceptsMouseEnteredPersists() {
        filter.setAcceptsMouseEntered(false);
        assertFalse(filter.getAcceptsMouseEntered());
    }

    public void testSetAcceptsMouseExitedPersists() {
        filter.setAcceptsMouseExited(false);
        assertFalse(filter.getAcceptsMouseExited());
    }

    public void testSetAcceptsMouseMovedPersists() {
        filter.setAcceptsMouseMoved(false);
        assertFalse(filter.getAcceptsMouseMoved());
    }

    public void testSetAcceptsMouseDraggedPersists() {
        filter.setAcceptsMouseDragged(false);
        assertFalse(filter.getAcceptsMouseDragged());
    }

    public void testSetAcceptsMouseMovedPressed() {
        filter.setAcceptsMousePressed(false);
        assertFalse(filter.getAcceptsMousePressed());
    }

    public void testSetAcceptsMouseMovedReleased() {
        filter.setAcceptsMouseReleased(false);
        assertFalse(filter.getAcceptsMouseReleased());
    }

    public void testSetAcceptsMouseWheelRotated() {
        filter.setAcceptsMouseWheelRotated(false);
        assertFalse(filter.getAcceptsMouseWheelRotated());
    }

    public void testAcceptsSimpleEvent() {
        final PInputEvent event = buildTestEvent();
        assertAcceptsEvent(event);
    }

    public void testRejectsAcceptedEventIfAcceptsHandledEventsIsFalse() {
        final PInputEvent event = buildTestEvent();
        event.setHandled(true);
        filter.setAcceptsAlreadyHandledEvents(false);
        assertRejectsEvent(event);
    }

    public void testRejectsEventsUnlessModifiersContainAllOfMask() {
        PInputEvent event = buildTestEvent();
        filter.setAndMask(InputEvent.CTRL_MASK | InputEvent.ALT_MASK);
        assertRejectsEvent(event);
        event = buildTestEvent(InputEvent.CTRL_MASK | InputEvent.ALT_MASK);
        assertAcceptsEvent(event);

        event = buildTestEvent(InputEvent.CTRL_MASK | InputEvent.ALT_MASK | InputEvent.META_MASK);
        assertAcceptsEvent(event);
    }

    public void testRejectsEventsUnlessModifiersContainOneOfOrMask() {
        final PInputEvent event = buildTestEvent();
        filter.setOrMask(InputEvent.CTRL_MASK | InputEvent.ALT_MASK);
        assertRejectsEvent(event);
        assertRejectsEvent(buildTestEvent(InputEvent.META_MASK));
        assertAcceptsEvent(buildTestEvent(InputEvent.CTRL_MASK));
        assertAcceptsEvent(buildTestEvent(InputEvent.ALT_MASK));
        assertAcceptsEvent(buildTestEvent(InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
    }

    public void testRejectsEventsUnlessTheyMatchOneOfNotMask() {
        final PInputEvent event = buildTestEvent();
        filter.setNotMask(InputEvent.CTRL_MASK | InputEvent.ALT_MASK);
        assertAcceptsEvent(event);

        assertAcceptsEvent(buildTestEvent(InputEvent.META_MASK));
        assertRejectsEvent(buildTestEvent(InputEvent.CTRL_MASK));
        assertRejectsEvent(buildTestEvent(InputEvent.ALT_MASK));
        assertRejectsEvent(buildTestEvent(InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
    }

    public void testRejectsMouseEventsIfMouseClickFilterSet() {
        filter.setAcceptClickCount((short) 1);
        assertRejectsEvent(buildTestEvent(0, 0));
        assertAcceptsEvent(buildTestEvent(0, 1));
        assertRejectsEvent(buildTestEvent(0, 2));
        assertRejectsEvent(buildTestEvent(0, 3));
    }

    public void testMarksEventsAsHandledIsHonnored() {
        filter.setMarksAcceptedEventsAsHandled(true);
        final PInputEvent event = buildTestEvent();
        assertAcceptsEvent(event);
        assertTrue(event.isHandled());
    }

    public void testRejectAllClickCountsIsHonoured() {
        filter.rejectAllClickCounts();
        assertRejectsEvent(buildTestEvent(0, 0));
        assertRejectsEvent(buildTestEvent(0, 1));
        assertRejectsEvent(buildTestEvent(0, 2));
        assertRejectsEvent(buildTestEvent(0, 3));

    }

    private void assertRejectsEvent(final PInputEvent event) {
        assertFalse(filter.acceptsEvent(event, MouseEvent.MOUSE_CLICKED));
    }

    private void assertAcceptsEvent(final PInputEvent event) {
        assertTrue(filter.acceptsEvent(event, MouseEvent.MOUSE_CLICKED));
    }

    private PInputEvent buildTestEvent() {
        return buildTestEvent(InputEvent.BUTTON1_MASK);
    }

    private PInputEvent buildTestEvent(final int modifiers) {
        return buildTestEvent(modifiers, 0);
    }

    private PInputEvent buildTestEvent(final int modifiers, final int clickCount) {
        final JComponent component = new JPanel();
        final PInputManager inputManager = new PInputManager();

        final MouseEvent event = new MouseEvent(component, 1, System.currentTimeMillis(), modifiers, 1, 1, clickCount,
                false);
        return new PInputEvent(inputManager, event);
    }
}
