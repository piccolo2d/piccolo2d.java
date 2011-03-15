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
package org.piccolo2d.extras.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.plaf.ScrollPaneUI;

import org.piccolo2d.PCanvas;


/**
 * A simple extension to a standard scroll pane that uses the jazz version of
 * the viewport by default. Also uses the jazz version of ScrollPaneLayout
 * 
 * @author Lance Good
 */
public class PScrollPane extends JScrollPane {

    private static final long serialVersionUID = 1L;

    /** A reusable null action. */
    protected PNullAction nullAction = null;

    /** Controls whether key actions are disabled on this component. */
    protected boolean disableKeyActions = false;

    private final AdjustmentListener scrollAdjustmentListener = new AdjustmentListener() {
        private boolean lastAdjustingState = false;

        public void adjustmentValueChanged(final AdjustmentEvent event) {
            if (event.getSource() instanceof JScrollBar) {
                JScrollBar scrollBar = (JScrollBar) event.getSource();

                setAdjusting(scrollBar.getValueIsAdjusting());
            }
        }

        /**
         * Updates the underlying PCanvas' interacting flag depending on whether
         * scroll bar adjustments are still taking place.
         * 
         * @param isAdjusting true if the scroll bar is still being interacted
         *            with
         */
        private void setAdjusting(final boolean isAdjusting) {
            if (isAdjusting != lastAdjustingState) {
                Component c = getViewport().getView();
                if (c instanceof PCanvas) {
                    ((PCanvas) c).setInteracting(isAdjusting);
                }
                lastAdjustingState = isAdjusting;
            }
        }
    };

    /**
     * Constructs a scollpane for the provided component with the specified
     * scrollbar policies.
     * 
     * @param view component being viewed through the scrollpane
     * @param vsbPolicy vertical scroll bar policy
     * @param hsbPolicy horizontal scroll bar policy
     */
    public PScrollPane(final Component view, final int vsbPolicy, final int hsbPolicy) {
        super(view, vsbPolicy, hsbPolicy);

        // Set the layout and sync it with the scroll pane
        final PScrollPaneLayout layout = new PScrollPaneLayout.UIResource();
        setLayout(layout);
        layout.syncWithScrollPane(this);

        horizontalScrollBar.addAdjustmentListener(scrollAdjustmentListener);
        verticalScrollBar.addAdjustmentListener(scrollAdjustmentListener);
    }

    /**
     * Intercepts the vertical scroll bar setter to ensure that the adjustment
     * listener is installed appropriately.
     * 
     * @param newVerticalScrollBar the new vertical scroll bar to use with this PScrollPane
     */
    public void setVerticalScrollBar(final JScrollBar newVerticalScrollBar) {
        if (verticalScrollBar != null) {
            verticalScrollBar.removeAdjustmentListener(scrollAdjustmentListener);
        }

        super.setVerticalScrollBar(newVerticalScrollBar);
        newVerticalScrollBar.addAdjustmentListener(scrollAdjustmentListener);
    }

    /**
     * Intercepts the horizontal scroll bar setter to ensure that the adjustment
     * listener is installed appropriately.
     * 
     * @param newHorizontalScrollBar the new horizontal scroll bar to use with this PScrollPane
     */
    public void setHorizontalScrollBar(final JScrollBar newHorizontalScrollBar) {
        if (horizontalScrollBar != null) {
            horizontalScrollBar.removeAdjustmentListener(scrollAdjustmentListener);
        }

        super.setHorizontalScrollBar(newHorizontalScrollBar);
        newHorizontalScrollBar.addAdjustmentListener(scrollAdjustmentListener);
    }

    /**
     * Constructs a scroll pane for the provided component.
     * 
     * @param view component being viewed through the scroll pane
     */
    public PScrollPane(final Component view) {
        this(view, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    /**
     * Constructs a scroll pane not attached to any component with the specified
     * scroll bar policies.
     * 
     * @param vsbPolicy vertical scroll bar policy
     * @param hsbPolicy horizontal scroll bar policy
     */
    public PScrollPane(final int vsbPolicy, final int hsbPolicy) {
        this(null, vsbPolicy, hsbPolicy);
    }

    /**
     * Constructs a scroll pane not attached to any component.
     */
    public PScrollPane() {
        this(null, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    /**
     * Disable or enable key actions on this PScrollPane.
     * 
     * @param disable true disables key actions, false enables key actions
     */
    public void setKeyActionsDisabled(final boolean disable) {
        if (disable && disableKeyActions != disable) {
            disableKeyActions = disable;
            disableKeyActions();
        }
        else if (!disable && disableKeyActions != disable) {
            disableKeyActions = disable;
            installCustomKeyActions();
        }
    }

    /**
     * Sets the UI.
     * 
     * @param ui the scroll pane UI to associate with this PScollPane
     */
    public void setUI(final ScrollPaneUI ui) {
        super.setUI(ui);

        if (!disableKeyActions) {
            installCustomKeyActions();
        }
        else {
            disableKeyActions();
        }
    }

    /**
     * Install custom key actions (in place of the Swing defaults) to correctly
     * scroll the view.
     */
    protected void installCustomKeyActions() {
        final ActionMap map = getActionMap();

        map.put("scrollUp", new PScrollAction("scrollUp", SwingConstants.VERTICAL, -1, true));
        map.put("scrollDown", new PScrollAction("scrollDown", SwingConstants.VERTICAL, 1, true));
        map.put("scrollLeft", new PScrollAction("scrollLeft", SwingConstants.HORIZONTAL, -1, true));

        map.put("scrollRight", new PScrollAction("ScrollRight", SwingConstants.HORIZONTAL, 1, true));
        map.put("unitScrollRight", new PScrollAction("UnitScrollRight", SwingConstants.HORIZONTAL, 1, false));
        map.put("unitScrollLeft", new PScrollAction("UnitScrollLeft", SwingConstants.HORIZONTAL, -1, false));
        map.put("unitScrollUp", new PScrollAction("UnitScrollUp", SwingConstants.VERTICAL, -1, false));
        map.put("unitScrollDown", new PScrollAction("UnitScrollDown", SwingConstants.VERTICAL, 1, false));

        map.put("scrollEnd", new PScrollEndAction("ScrollEnd"));
        map.put("scrollHome", new PScrollHomeAction("ScrollHome"));
    }

    /**
     * Disables key actions on this PScrollPane.
     */
    protected void disableKeyActions() {
        final ActionMap map = getActionMap();

        if (nullAction == null) {
            nullAction = new PNullAction();
        }

        map.put("scrollUp", nullAction);
        map.put("scrollDown", nullAction);
        map.put("scrollLeft", nullAction);
        map.put("scrollRight", nullAction);
        map.put("unitScrollRight", nullAction);
        map.put("unitScrollLeft", nullAction);
        map.put("unitScrollUp", nullAction);
        map.put("unitScrollDown", nullAction);
        map.put("scrollEnd", nullAction);
        map.put("scrollHome", nullAction);
    }

    /**
     * Overridden to create the Piccolo2D viewport.
     * 
     * @return the Piccolo2D version of the viewport
     */
    protected JViewport createViewport() {
        return new PViewport();
    }

    /**
     * Action to scroll left/right/up/down. Modified from
     * javax.swing.plaf.basic.BasicScrollPaneUI.ScrollAction.
     * 
     * Gets the view parameters (position and size) from the Viewport rather
     * than directly from the view - also only performs its actions when the
     * relevant scrollbar is visible.
     */
    protected static class PScrollAction extends AbstractAction {
        private static final int MINIMUM_SCROLL_SIZE = 10;
        private static final long serialVersionUID = 1L;
        /** Direction to scroll. */
        protected int orientation;
        /** 1 indicates scroll down, -1 up. */
        protected int direction;
        /** True indicates a block scroll, otherwise a unit scroll. */
        private final boolean block;

        /**
         * Constructs a scroll action with the given name in the given
         * orientiation stated and in the direction provided.
         * 
         * @param name arbitrary name of action
         * @param orientation horizontal or vertical
         * @param direction 1 indicates scroll down, -1 up
         * @param block true if block scroll as opposed to unit
         */
        protected PScrollAction(final String name, final int orientation, final int direction, final boolean block) {
            super(name);
            this.orientation = orientation;
            this.direction = direction;
            this.block = block;
        }

        /**
         * Performs the scroll action if the action was performed on visible
         * scrollbars and if the viewport is valid.
         * 
         * @param event the event responsible for this action being performed
         */
        public void actionPerformed(final ActionEvent event) {
            final JScrollPane scrollpane = (JScrollPane) event.getSource();
            if (!isScrollEventOnVisibleScrollbars(scrollpane)) {
                return;
            }

            final JViewport vp = scrollpane.getViewport();
            if (vp == null) {
                return;
            }

            Component view = vp.getView();
            if (view == null) {
                return;
            }

            final Rectangle visRect = vp.getViewRect();
            // LEG: Modification to query the viewport for the
            // view size rather than going directly to the view
            final Dimension vSize = vp.getViewSize();
            final int amount;

            if (view instanceof Scrollable) {
                if (block) {
                    amount = ((Scrollable) view).getScrollableBlockIncrement(visRect, orientation, direction);
                }
                else {
                    amount = ((Scrollable) view).getScrollableUnitIncrement(visRect, orientation, direction);
                }
            }
            else {
                if (block) {
                    if (orientation == SwingConstants.VERTICAL) {
                        amount = visRect.height;
                    }
                    else {
                        amount = visRect.width;
                    }
                }
                else {
                    amount = MINIMUM_SCROLL_SIZE;
                }
            }

            if (orientation == SwingConstants.VERTICAL) {
                visRect.y += amount * direction;
                if (visRect.y + visRect.height > vSize.height) {
                    visRect.y = Math.max(0, vSize.height - visRect.height);
                }
                else if (visRect.y < 0) {
                    visRect.y = 0;
                }
            }
            else {
                visRect.x += amount * direction;
                if (visRect.x + visRect.width > vSize.width) {
                    visRect.x = Math.max(0, vSize.width - visRect.width);
                }
                else if (visRect.x < 0) {
                    visRect.x = 0;
                }
            }
            vp.setViewPosition(visRect.getLocation());
        }

        private boolean isScrollEventOnVisibleScrollbars(final JScrollPane scrollpane) {
            return orientation == SwingConstants.VERTICAL && scrollpane.getVerticalScrollBar().isShowing()
                    || orientation == SwingConstants.HORIZONTAL && scrollpane.getHorizontalScrollBar().isShowing();
        }
    }

    /**
     * Action to scroll to x,y location of 0,0. Modified from
     * javax.swing.plaf.basic.BasicScrollPaneUI.ScrollEndAction.
     * 
     * Only performs the event if a scrollbar is visible.
     */
    private static class PScrollHomeAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        protected PScrollHomeAction(final String name) {
            super(name);
        }

        public void actionPerformed(final ActionEvent e) {
            final JScrollPane scrollpane = (JScrollPane) e.getSource();
            // LEG: Modification to only perform these actions if one of the
            // scrollbars is actually showing
            if (scrollpane.getVerticalScrollBar().isShowing() || scrollpane.getHorizontalScrollBar().isShowing()) {
                final JViewport vp = scrollpane.getViewport();
                if (vp != null && vp.getView() != null) {
                    vp.setViewPosition(new Point(0, 0));
                }
            }
        }
    }

    /**
     * Action to scroll to last visible location. Modified from
     * javax.swing.plaf.basic.BasicScrollPaneUI.ScrollEndAction.
     * 
     * Gets the view size from the viewport rather than directly from the view -
     * also only performs the event if a scrollbar is visible.
     */
    protected static class PScrollEndAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        /**
         * Constructs a scroll to end action with the given name.
         * 
         * @param name name to assign to this action
         */
        protected PScrollEndAction(final String name) {
            super(name);
        }

        /**
         * Scrolls to the end of the viewport if there are visible scrollbars.
         * 
         * @param event event responsible for the scroll event
         */
        public void actionPerformed(final ActionEvent event) {
            final JScrollPane scrollpane = (JScrollPane) event.getSource();
            // LEG: Modification to only perform these actions if one of the
            // scrollbars is actually showing
            if (scrollpane.getVerticalScrollBar().isShowing() || scrollpane.getHorizontalScrollBar().isShowing()) {

                final JViewport vp = scrollpane.getViewport();
                if (vp != null && vp.getView() != null) {

                    final Rectangle visRect = vp.getViewRect();
                    // LEG: Modification to query the viewport for the
                    // view size rather than going directly to the view
                    final Dimension size = vp.getViewSize();
                    vp.setViewPosition(new Point(size.width - visRect.width, size.height - visRect.height));
                }
            }
        }
    }

    /**
     * An action to do nothing - put into an action map to keep it from looking
     * to its parent.
     */
    protected static class PNullAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        /**
         * Does nothing.
         * 
         * @param e Event responsible for this action
         */
        public void actionPerformed(final ActionEvent e) {
        }
    }
}
