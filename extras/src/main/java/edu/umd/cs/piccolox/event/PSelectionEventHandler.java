/*
 * Copyright (c) 2008-2009, Piccolo2D project, http://piccolo2d.org
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
package edu.umd.cs.piccolox.event;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolo.util.PNodeFilter;
import edu.umd.cs.piccolox.handles.PBoundsHandle;

/**
 * <code>PSelectionEventHandler</code> provides standard interaction for
 * selection. Clicking selects the object under the cursor. Shift-clicking
 * allows multiple objects to be selected. Dragging offers marquee selection.
 * Pressing the delete key deletes the selection by default.
 * 
 * @version 1.0
 * @author Ben Bederson
 */
public class PSelectionEventHandler extends PDragSequenceEventHandler {
    /**
     * Notification name that identifies a change in the selection. Used with
     * PNotificationCenter.
     */
    public static final String SELECTION_CHANGED_NOTIFICATION = "SELECTION_CHANGED_NOTIFICATION";

    /** The default dash width when displaying selection rectangle. */
    static final int DASH_WIDTH = 5;

    static final int NUM_STROKES = 10;

    /** The current selection. */
    private HashMap selection = null;
    /** List of nodes whose children can be selected. */
    private List selectableParents = null;

    private PPath marquee = null;
    /** Node that marquee is added to as a child. */
    private PNode marqueeParent = null;

    private Point2D presspt = null;
    private Point2D canvasPressPt = null;
    private float strokeNum = 0;
    private Stroke[] strokes = null;

    /** Used within drag handler temporarily. */
    private HashMap allItems = null;

    /** Used within drag handler temporarily */
    private ArrayList unselectList = null;
    private HashMap marqueeMap = null;

    /** Node pressed on (or null if none). */
    private PNode pressNode = null;

    /** True if DELETE key should delete selection. */
    private boolean deleteKeyActive = true;

    /** Paint applied when drawing the marquee. */
    private Paint marqueePaint;

    /** How transparent the marquee should be. */
    private float marqueePaintTransparency = 1.0f;

    /**
     * Creates a selection event handler.
     * 
     * @param marqueeParent The node to which the event handler dynamically adds
     *            a marquee (temporarily) to represent the area being selected.
     * @param selectableParent The node whose children will be selected by this
     *            event handler.
     */
    public PSelectionEventHandler(final PNode marqueeParent, final PNode selectableParent) {
        this.marqueeParent = marqueeParent;
        selectableParents = new ArrayList();
        selectableParents.add(selectableParent);
        init();
    }

    /**
     * Creates a selection event handler.
     * 
     * @param marqueeParent The node to which the event handler dynamically adds
     *            a marquee (temporarily) to represent the area being selected.
     * @param selectableParents A list of nodes whose children will be selected
     *            by this event handler.
     */
    public PSelectionEventHandler(final PNode marqueeParent, final List selectableParents) {
        this.marqueeParent = marqueeParent;
        this.selectableParents = selectableParents;
        init();
    }

    protected void init() {
        final float[] dash = { DASH_WIDTH, DASH_WIDTH };
        strokes = new Stroke[NUM_STROKES];
        for (int i = 0; i < NUM_STROKES; i++) {
            strokes[i] = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, dash, i);
        }

        selection = new HashMap();
        allItems = new HashMap();
        unselectList = new ArrayList();
        marqueeMap = new HashMap();
    }

    /**
     * Marks all items as selected.
     * 
     * @param items collection of items to be selected
     */
    public void select(final Collection items) {
        boolean changes = false;
        final Iterator itemIt = items.iterator();
        while (itemIt.hasNext()) {
            final PNode node = (PNode) itemIt.next();
            changes |= internalSelect(node);
        }
        if (changes) {
            postSelectionChanged();
        }
    }

    /**
     * Marks all keys as selected.
     * 
     * @param items map where keys are to be selected
     */
    public void select(final Map items) {
        select(items.keySet());
    }

    /**
     * Select the passed node if not already selected.
     * 
     * @param node node to be selected
     * @return true if node was not already selected
     */
    private boolean internalSelect(final PNode node) {
        if (isSelected(node)) {
            return false;
        }

        selection.put(node, Boolean.TRUE);
        decorateSelectedNode(node);
        return true;
    }

    /**
     * Dispatches a selection changed notification to the PNodificationCenter.
     */
    private void postSelectionChanged() {
        PNotificationCenter.defaultCenter().postNotification(SELECTION_CHANGED_NOTIFICATION, this);
    }

    /**
     * Selected the provided node if not already selected.
     * 
     * @param node node to be selected
     */
    public void select(final PNode node) {
        if (internalSelect(node)) {
            postSelectionChanged();
        }
    }

    /**
     * Adds bound handles to the provided node.
     * 
     * @param node node to be decorated
     */
    public void decorateSelectedNode(final PNode node) {
        PBoundsHandle.addBoundsHandlesTo(node);
    }

    /**
     * Removes all nodes provided from the selection.
     * 
     * @param items items to remove form the selection
     */
    public void unselect(final Collection items) {
        boolean changes = false;
        final Iterator itemIt = items.iterator();
        while (itemIt.hasNext()) {
            final PNode node = (PNode) itemIt.next();
            changes |= internalUnselect(node);
        }
        if (changes) {
            postSelectionChanged();
        }
    }

    /**
     * Removes provided selection node if not already selected.
     * 
     * @param node node to remove from selection
     * 
     * @return true on success
     */
    private boolean internalUnselect(final PNode node) {
        if (!isSelected(node)) {
            return false;
        }

        undecorateSelectedNode(node);
        selection.remove(node);
        return true;
    }

    /**
     * Removes node from selection.
     * 
     * @param node node to be removed from selection
     */
    public void unselect(final PNode node) {
        if (internalUnselect(node)) {
            postSelectionChanged();
        }
    }

    /**
     * Removes bounds handles from node.
     * 
     * @param node to have handles removed from
     */
    public void undecorateSelectedNode(final PNode node) {
        PBoundsHandle.removeBoundsHandlesFrom(node);
    }

    /**
     * Empties the selection.
     */
    public void unselectAll() {
        // Because unselect() removes from selection, we need to
        // take a copy of it first so it isn't changed while we're iterating
        final ArrayList sel = new ArrayList(selection.keySet());
        unselect(sel);
    }

    /**
     * Returns true is provided node is selected.
     * 
     * @param node - node to be tested
     * @return true if succeeded
     */
    public boolean isSelected(final PNode node) {
        if (node != null && selection.containsKey(node)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Returns a copy of the currently selected nodes.
     * 
     * @return copy of selection
     */
    public Collection getSelection() {
        final ArrayList sel = new ArrayList(selection.keySet());
        return sel;
    }

    /**
     * Gets a reference to the currently selected nodes. You should not modify
     * or store this collection.
     * 
     * @return direct reference to selection
     */
    public Collection getSelectionReference() {
        return Collections.unmodifiableCollection(selection.keySet());
    }

    /**
     * Determine if the specified node can be selected (i.e., if it is a child
     * of the one the list of nodes that can be selected).
     * 
     * @param node node being tested
     * @return true if node can be selected
     */
    protected boolean isSelectable(final PNode node) {
        boolean selectable = false;

        final Iterator parentsIt = selectableParents.iterator();
        while (parentsIt.hasNext()) {
            final PNode parent = (PNode) parentsIt.next();
            if (parent.getChildrenReference().contains(node)) {
                selectable = true;
                break;
            }
            else if (parent instanceof PCamera) {
                for (int i = 0; i < ((PCamera) parent).getLayerCount(); i++) {
                    final PLayer layer = ((PCamera) parent).getLayer(i);
                    if (layer.getChildrenReference().contains(node)) {
                        selectable = true;
                        break;
                    }
                }
            }
        }

        return selectable;
    }

    /**
     * Flags the node provided as a selectable parent. This makes it possible to
     * select its children.
     * 
     * @param node to flag as selectable
     */
    public void addSelectableParent(final PNode node) {
        selectableParents.add(node);
    }

    /**
     * Removes the node provided from the set of selectable parents. This makes
     * its impossible to select its children.
     * 
     * @param node to remove from selectable parents
     */
    public void removeSelectableParent(final PNode node) {
        selectableParents.remove(node);
    }

    /**
     * Sets the node provided as the *only* selectable parent.
     * 
     * @param node node to become the 1 and only selectable parent
     */
    public void setSelectableParent(final PNode node) {
        selectableParents.clear();
        selectableParents.add(node);
    }

    /**
     * Sets the collection of selectable parents as the only parents that are
     * selectable.
     * 
     * @param c nodes to become selectable parents.
     */
    public void setSelectableParents(final Collection c) {
        selectableParents.clear();
        selectableParents.addAll(c);
    }

    /**
     * Returns all selectable parents.
     * 
     * @return selectable parents
     */
    public Collection getSelectableParents() {
        return new ArrayList(selectableParents);
    }

    // //////////////////////////////////////////////////////
    // The overridden methods from PDragSequenceEventHandler
    // //////////////////////////////////////////////////////

     protected void startDrag(final PInputEvent e) {
        super.startDrag(e);

        initializeSelection(e);

        if (isMarqueeSelection(e)) {
            initializeMarquee(e);

            if (!isOptionSelection(e)) {
                startMarqueeSelection(e);
            }
            else {
                startOptionMarqueeSelection(e);
            }
        }
        else {
            if (!isOptionSelection(e)) {
                startStandardSelection(e);
            }
            else {
                startStandardOptionSelection(e);
            }
        }
    }

    protected void drag(final PInputEvent e) {
        super.drag(e);

        if (isMarqueeSelection(e)) {
            updateMarquee(e);

            if (!isOptionSelection(e)) {
                computeMarqueeSelection(e);
            }
            else {
                computeOptionMarqueeSelection(e);
            }
        }
        else {
            dragStandardSelection(e);
        }
    }

    protected void endDrag(final PInputEvent e) {
        super.endDrag(e);

        if (isMarqueeSelection(e)) {
            endMarqueeSelection(e);
        }
        else {
            endStandardSelection(e);
        }
    }

    // //////////////////////////
    // Additional methods
    // //////////////////////////

    /**
     * Used to test whether the event is one that changes the selection.
     * 
     * @param pie The event under test      
     * @return true if event changes the selection
     */
    public boolean isOptionSelection(final PInputEvent pie) {
        return pie.isShiftDown();
    }

    /**
     * Tests the input event to see if it is selecting a new node.
     * 
     * @param pie event under test
     * @return true if there is no current selection
     */
    protected boolean isMarqueeSelection(final PInputEvent pie) {
        return pressNode == null;
    }

    /**
     * Starts a selection based on the provided event.
     * 
     * @param pie event used to populate the selection
     */
    protected void initializeSelection(final PInputEvent pie) {
        canvasPressPt = pie.getCanvasPosition();
        presspt = pie.getPosition();
        pressNode = pie.getPath().getPickedNode();
        if (pressNode instanceof PCamera) {
            pressNode = null;
        }
    }

    protected void initializeMarquee(final PInputEvent e) {
        marquee = PPath.createRectangle((float) presspt.getX(), (float) presspt.getY(), 0, 0);
        marquee.setPaint(marqueePaint);
        marquee.setTransparency(marqueePaintTransparency);
        marquee.setStrokePaint(Color.black);
        marquee.setStroke(strokes[0]);
        marqueeParent.addChild(marquee);

        marqueeMap.clear();
    }

    protected void startOptionMarqueeSelection(final PInputEvent e) {
    }

    protected void startMarqueeSelection(final PInputEvent e) {
        unselectAll();
    }

    protected void startStandardSelection(final PInputEvent pie) {
        // Option indicator not down - clear selection, and start fresh
        if (!isSelected(pressNode)) {
            unselectAll();

            if (isSelectable(pressNode)) {
                select(pressNode);
            }
        }
    }

    protected void startStandardOptionSelection(final PInputEvent pie) {
        // Option indicator is down, toggle selection
        if (isSelectable(pressNode)) {
            if (isSelected(pressNode)) {
                unselect(pressNode);
            }
            else {
                select(pressNode);
            }
        }
    }

    protected void updateMarquee(final PInputEvent pie) {
        final PBounds b = new PBounds();

        if (marqueeParent instanceof PCamera) {
            b.add(canvasPressPt);
            b.add(pie.getCanvasPosition());
        }
        else {
            b.add(presspt);
            b.add(pie.getPosition());
        }

        marquee.globalToLocal(b);
        marquee.setPathToRectangle((float) b.x, (float) b.y, (float) b.width, (float) b.height);
        b.reset();
        b.add(presspt);
        b.add(pie.getPosition());

        allItems.clear();
        final PNodeFilter filter = createNodeFilter(b);
        final Iterator parentsIt = selectableParents.iterator();
        while (parentsIt.hasNext()) {
            final PNode parent = (PNode) parentsIt.next();

            Collection items;
            if (parent instanceof PCamera) {
                items = new ArrayList();
                for (int i = 0; i < ((PCamera) parent).getLayerCount(); i++) {
                    ((PCamera) parent).getLayer(i).getAllNodes(filter, items);
                }
            }
            else {
                items = parent.getAllNodes(filter, null);
            }

            final Iterator itemsIt = items.iterator();
            while (itemsIt.hasNext()) {
                allItems.put(itemsIt.next(), Boolean.TRUE);
            }
        }
    }

    protected void computeMarqueeSelection(final PInputEvent pie) {
        unselectList.clear();
        // Make just the items in the list selected
        // Do this efficiently by first unselecting things not in the list
        Iterator selectionEn = selection.keySet().iterator();
        while (selectionEn.hasNext()) {
            final PNode node = (PNode) selectionEn.next();
            if (!allItems.containsKey(node)) {
                unselectList.add(node);
            }
        }
        unselect(unselectList);

        // Then select the rest
        selectionEn = allItems.keySet().iterator();
        while (selectionEn.hasNext()) {
            final PNode node = (PNode) selectionEn.next();
            if (!selection.containsKey(node) && !marqueeMap.containsKey(node) && isSelectable(node)) {
                marqueeMap.put(node, Boolean.TRUE);
            }
            else if (!isSelectable(node)) {
                selectionEn.remove();
            }
        }

        select(allItems);
    }

    protected void computeOptionMarqueeSelection(final PInputEvent pie) {
        unselectList.clear();
        Iterator selectionEn = selection.keySet().iterator();
        while (selectionEn.hasNext()) {
            final PNode node = (PNode) selectionEn.next();
            if (!allItems.containsKey(node) && marqueeMap.containsKey(node)) {
                marqueeMap.remove(node);
                unselectList.add(node);
            }
        }
        unselect(unselectList);

        // Then select the rest
        selectionEn = allItems.keySet().iterator();
        while (selectionEn.hasNext()) {
            final PNode node = (PNode) selectionEn.next();
            if (!selection.containsKey(node) && !marqueeMap.containsKey(node) && isSelectable(node)) {
                marqueeMap.put(node, Boolean.TRUE);
            }
            else if (!isSelectable(node)) {
                selectionEn.remove();
            }
        }

        select(allItems);
    }

    protected PNodeFilter createNodeFilter(final PBounds bounds) {
        return new BoundsFilter(bounds);
    }

    protected PBounds getMarqueeBounds() {
        if (marquee != null) {
            return marquee.getBounds();
        }
        return new PBounds();
    }

    protected void dragStandardSelection(final PInputEvent e) {
        // There was a press node, so drag selection
        final PDimension d = e.getCanvasDelta();
        e.getTopCamera().localToView(d);

        final PDimension gDist = new PDimension();
        final Iterator selectionEn = getSelection().iterator();
        while (selectionEn.hasNext()) {
            final PNode node = (PNode) selectionEn.next();

            gDist.setSize(d);
            node.getParent().globalToLocal(gDist);
            node.offset(gDist.getWidth(), gDist.getHeight());
        }
    }

    protected void endMarqueeSelection(final PInputEvent e) {
        // Remove marquee
        allItems.clear();
        marqueeMap.clear();
        marquee.removeFromParent();
        marquee = null;
    }

    protected void endStandardSelection(final PInputEvent e) {
        pressNode = null;
    }

    /**
     * This gets called continuously during the drag, and is used to animate the
     * marquee
     */
    protected void dragActivityStep(final PInputEvent aEvent) {
        if (marquee != null) {
            final float origStrokeNum = strokeNum;
            strokeNum = (strokeNum + 0.5f) % NUM_STROKES; // Increment by
            // partial steps to
            // slow down animation
            if ((int) strokeNum != (int) origStrokeNum) {
                marquee.setStroke(strokes[(int) strokeNum]);
            }
        }
    }

    /**
     * Delete selection when delete key is pressed (if enabled)
     */
    public void keyPressed(final PInputEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DELETE:
                if (deleteKeyActive) {
                    final Iterator selectionEn = selection.keySet().iterator();
                    while (selectionEn.hasNext()) {
                        final PNode node = (PNode) selectionEn.next();
                        node.removeFromParent();
                    }
                    selection.clear();
                }
        }
    }

    public boolean getSupportDeleteKey() {
        return deleteKeyActive;
    }

    public boolean isDeleteKeyActive() {
        return deleteKeyActive;
    }

    /**
     * Specifies if the DELETE key should delete the selection
     */
    public void setDeleteKeyActive(final boolean deleteKeyActive) {
        this.deleteKeyActive = deleteKeyActive;
    }

    // ////////////////////
    // Inner classes
    // ////////////////////

    protected class BoundsFilter implements PNodeFilter {
        PBounds localBounds = new PBounds();
        PBounds bounds;

        protected BoundsFilter(final PBounds bounds) {
            this.bounds = bounds;
        }

        public boolean accept(final PNode node) {
            localBounds.setRect(bounds);
            node.globalToLocal(localBounds);

            final boolean boundsIntersects = node.intersects(localBounds);
            final boolean isMarquee = node == marquee;
            return node.getPickable() && boundsIntersects && !isMarquee && !selectableParents.contains(node)
                    && !isCameraLayer(node);
        }

        public boolean acceptChildrenOf(final PNode node) {
            return selectableParents.contains(node) || isCameraLayer(node);
        }

        public boolean isCameraLayer(final PNode node) {
            if (node instanceof PLayer) {
                for (final Iterator i = selectableParents.iterator(); i.hasNext();) {
                    final PNode parent = (PNode) i.next();
                    if (parent instanceof PCamera && ((PCamera) parent).indexOfLayer((PLayer) node) != -1) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * Indicates the color used to paint the marquee.
     * 
     * @return the paint for interior of the marquee
     */
    public Paint getMarqueePaint() {
        return marqueePaint;
    }

    /**
     * Sets the color used to paint the marquee.
     * 
     * @param paint the paint color
     */
    public void setMarqueePaint(final Paint paint) {
        marqueePaint = paint;
    }

    /**
     * Indicates the transparency level for the interior of the marquee.
     * 
     * @return Returns the marquee paint transparency, zero to one
     */
    public float getMarqueePaintTransparency() {
        return marqueePaintTransparency;
    }

    /**
     * Sets the transparency level for the interior of the marquee.
     * 
     * @param marqueePaintTransparency The marquee paint transparency to set.
     */
    public void setMarqueePaintTransparency(final float marqueePaintTransparency) {
        this.marqueePaintTransparency = marqueePaintTransparency;
    }
}