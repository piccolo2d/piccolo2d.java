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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.piccolo2d.PNode;


/**
 * Uses Swing layout managers to position PNodes.
 * 
 * @author Sam Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SwingLayoutNode extends PNode {
    private static final long serialVersionUID = 1L;
    /*
     * How the space allocated by the Swing layout manager is used differs
     * depending on Swing component type. The behavior of a default JLabel
     * (Anchors.WEST) seems to make the most sense for PNodes.
     */
    private static final Anchor DEFAULT_ANCHOR = Anchor.WEST;

    /** Container for ProxyComponents. */
    private final Container container;

    private final PropertyChangeListener propertyChangeListener;

    /** Anchor to use when adding child nodes and they don't specify one. */
    private Anchor defaultAnchor;

    /**
     * Construct a SwingLayoutNode that uses FlowLayout.
     */
    public SwingLayoutNode() {
        this(new FlowLayout());
    }

    /**
     * Constructs a SwingLayoutNode that uses the provided LayoutManager to
     * layout its children.
     * 
     * @param layoutManager LayoutManager to use for laying out children. Must
     *            not be null.
     */
    public SwingLayoutNode(final LayoutManager layoutManager) {
        this(new JPanel(layoutManager));
    }

    /**
     * Constructs a SwingLayoutNode that lays out its children as though they
     * were children of the provided Container.
     * 
     * Whatever LayoutManager is being used by the container will be used when
     * laying out nodes.
     * 
     * @param container Container in which child nodes will effectively be laid
     *            out
     */
    public SwingLayoutNode(Container container) {
        this.container = container;
        propertyChangeListener = new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent event) {
                final String propertyName = event.getPropertyName();
                if (isLayoutProperty(propertyName)) {
                    updateContainerLayout();
                }
            }
        };
        defaultAnchor = DEFAULT_ANCHOR;
    }

    /**
     * Sets the default anchor. If no anchor is specified when a node is added,
     * then the default anchor determines where the node is positioned in the
     * space allocated by the Swing layout manager.
     * 
     * @param anchor anchor to use when a node is added but its anchor is not
     *            specified
     */
    public void setAnchor(final Anchor anchor) {
        this.defaultAnchor = anchor;
    }

    /**
     * Returns the anchor being used by this LayoutManager.
     * 
     * @return anchor currently being used when laying out children.
     */
    public Anchor getAnchor() {
        return defaultAnchor;
    }

    /**
     * Some Swing layout managers (like BoxLayout) require a reference to the
     * proxy Container.
     * 
     * For example: <code>
     * SwingLayoutNode layoutNode = new SwingLayoutNode();
     * layoutNode.setLayout( new BoxLayout( layoutNode.getContainer(), BoxLayout.Y_AXIS ) );
     * </code>
     * 
     * @return container in which children will logically be laid out in
     */
    public Container getContainer() {
        return container;
    }

    /**
     * Adds a child at the specified index. Like Swing, bad things can happen if
     * the type of the constraints isn't compatible with the layout manager.
     * 
     * @param index 0 based index at which to add the child
     * @param child child to be added
     * @param constraints constraints the layout manager uses when laying out
     *            the child
     * @param childAnchor specifies the location from which layout takes place
     */
    public void addChild(final int index, final PNode child, final Object constraints, final Anchor childAnchor) {
        /*
         * NOTE: This must be the only super.addChild call that we make in our
         * entire implementation, because all PNode.addChild methods are
         * implemented in terms of this one. Calling other variants of
         * super.addChild will incorrectly invoke our overrides, resulting in
         * StackOverflowException.
         */
        super.addChild(index, child);
        addProxyComponent(child, constraints, childAnchor);
    }

    /** {@inheritDoc} */
    public void addChild(final int index, final PNode child) {
        addChild(index, child, null, defaultAnchor);
    }

    /**
     * Adds a child at the specified index. Like Swing, bad things can happen if
     * the type of the constraints isn't compatible with the layout manager.
     * 
     * @param index 0 based index at which to add the child
     * @param child child to be added
     * @param constraints constraints the layout manager uses when laying out
     *            the child
     */
    public void addChild(final int index, final PNode child, final Object constraints) {
        addChild(index, child, constraints, defaultAnchor);
    }

    /**
     * Adds a child at the specified index.
     * 
     * @param index 0 based index at which to add the child
     * @param child child to be added
     * @param anchor specifies the location from which layout takes place
     */
    public void addChild(final int index, final PNode child, final Anchor anchor) {
        addChild(index, child, null, anchor);
    }

    /**
     * Adds a child to the end of the node list.
     * 
     * @param child child to be added
     * @param constraints constraints the layout manager uses when laying out
     *            the child
     * @param anchor specifies the location from which layout takes place
     */
    public void addChild(final PNode child, final Object constraints, final Anchor anchor) {
        // NOTE: since PNode.addChild(PNode) is implemented in terms of
        // PNode.addChild(int index), we must do the same.
        int index = getChildrenCount();
        // workaround a flaw in PNode.addChild(PNode), they should have handled
        // this in PNode.addChild(int index).
        if (child.getParent() == this) {
            index--;
        }
        addChild(index, child, constraints, anchor);
    }

    /**
     * Adds a child to the end of the node list.
     * 
     * @param child child to be added
     */
    public void addChild(final PNode child) {
        addChild(child, null, defaultAnchor);
    }

    /**
     * Adds a child to the end of the node list and specifies the given
     * constraints.
     * 
     * @param child child to be added
     * @param constraints constraints the layout manager uses when laying out
     *            the child
     */
    public void addChild(final PNode child, final Object constraints) {
        addChild(child, constraints, defaultAnchor);
    }

    /**
     * Adds a child to the end of the node list.
     * 
     * @param child child to be added
     * @param anchor specifies the location from which layout takes place
     */
    public void addChild(final PNode child, final Anchor anchor) {
        addChild(child, null, anchor);
    }

    /**
     * Adds a collection of nodes to the end of the list.
     * 
     * @param nodes nodes to add to the end of the list
     * @param constraints constraints the layout manager uses when laying out
     *            the child
     * @param anchor specifies the location from which layout takes place
     */
    public void addChildren(final Collection nodes, final Object constraints, final Anchor anchor) {
        final Iterator i = nodes.iterator();
        while (i.hasNext()) {
            final PNode each = (PNode) i.next();
            addChild(each, constraints, anchor);
        }
    }

    /** {@inheritDoc} */
    public void addChildren(final Collection nodes) {
        addChildren(nodes, null, defaultAnchor);
    }

    /**
     * Adds a collection of nodes to the end of the list.
     * 
     * @param nodes nodes to add to the end of the list
     * @param constraints constraints the layout manager uses when laying out
     *            the child
     */
    public void addChildren(final Collection nodes, final Object constraints) {
        addChildren(nodes, constraints, defaultAnchor);
    }

    /**
     * Adds a collection of nodes to the end of the list.
     * 
     * @param nodes nodes to add to the end of the list
     * @param anchor specifies the location from which layout takes place
     */
    public void addChildren(final Collection nodes, final Anchor anchor) {
        addChildren(nodes, null, anchor);
    }

    /**
     * Removes a node at a specified index.
     * 
     * @param index 0 based index of the child to be removed
     */
    public PNode removeChild(final int index) {
        /*
         * NOTE: This must be the only super.removeChild call that we make in
         * our entire implementation, because all PNode.removeChild methods are
         * implemented in terms of this one. Calling other variants of
         * super.removeChild will incorrectly invoke our overrides, resulting in
         * StackOverflowException.
         */
        final PNode node = super.removeChild(index);
        removeProxyComponent(node);
        return node;
    }

    /*
     * NOTE We don't need to override removeChild(PNode) or removeChildren,
     * because they call removeChild(int index). If their implementation ever
     * changes, then we'll need to override them.
     */

    /**
     * PNode.removeAllChildren does not call removeChild, it manipulates an
     * internal data structure. So we must override this in a more careful (and
     * less efficient) manner.
     */
    public void removeAllChildren() {
        final Iterator i = getChildrenIterator();
        while (i.hasNext()) {
            removeChild((PNode) i.next());
        }
    }

    /**
     * Adds a proxy component for a node.
     * 
     * @param node node for which to add the proxy component
     * @param constraints Constraints to apply when laying out the component
     * @param anchor relative anchor point of the underyling proxy component on
     *            its container
     */
    private void addProxyComponent(final PNode node, final Object constraints, final Anchor anchor) {
        final ProxyComponent component = new ProxyComponent(node, anchor);
        container.add(component, constraints);
        node.addPropertyChangeListener(propertyChangeListener);
        updateContainerLayout();
    }

    /**
     * Removes a proxy component for a node. Does nothing if the node is not a
     * child of the layout.
     * 
     * @param node node from which the proxy container should be removed from.
     */
    private void removeProxyComponent(final PNode node) {
        if (node != null) {
            final ProxyComponent component = getComponentForNode(node);
            if (component != null) {
                container.remove(component);
                node.removePropertyChangeListener(propertyChangeListener);
                updateContainerLayout();
            }
        }
    }

    /**
     * Finds the component that is serving as the proxy for a specific node.
     * Returns null if not found.
     */
    private ProxyComponent getComponentForNode(final PNode node) {
        ProxyComponent nodeComponent = null;
        final Component[] components = container.getComponents();
        if (components != null) {
            for (int i = 0; i < components.length && nodeComponent == null; i++) {
                if (components[i] instanceof ProxyComponent) {
                    final ProxyComponent n = (ProxyComponent) components[i];
                    if (n.getNode() == node) {
                        nodeComponent = n;
                    }
                }
            }
        }
        return nodeComponent;
    }

    /**
     * Helper to figure out if the given property name relates to layout.
     * 
     * @param propertyName name of property being tested
     * 
     * @return true property name relates to layout.
     */
    private boolean isLayoutProperty(final String propertyName) {
        return propertyName.equals(PNode.PROPERTY_VISIBLE) || propertyName.equals(PNode.PROPERTY_FULL_BOUNDS) ||
                propertyName.equals(PNode.PROPERTY_BOUNDS) || propertyName.equals(PNode.PROPERTY_TRANSFORM);
    }

    /**
     * Updates the Proxy Container's layout.
     */
    private void updateContainerLayout() {
        container.invalidate(); // necessary for layouts like BoxLayout that
        // would otherwise use stale state
        container.setSize(container.getPreferredSize());
        container.doLayout();
    }

    /**
     * JComponent that acts as a proxy for a PNode. Provides the PNode's bounds
     * info for all bounds-related requests.
     */
    private static class ProxyComponent extends JComponent {
        private static final long serialVersionUID = 1L;
        private final PNode node;
        private final Anchor anchor;

        public ProxyComponent(final PNode node, final Anchor anchor) {
            this.node = node;
            this.anchor = anchor;
        }

        /**
         * Returns the associated PNode.
         * 
         * @return associated PNode
         */
        public PNode getNode() {
            return node;
        }

        /**
         * Report the node's dimensions as the ProxyComponent's preferred size.
         */
        public Dimension getPreferredSize() {
            // Round up fractional part instead of rounding down; better to
            // include the whole node than to chop off part.
            final double w = node.getFullBoundsReference().getWidth();
            final double h = node.getFullBoundsReference().getHeight();
            return new Dimension(roundUp(w), roundUp(h));
        }

        private int roundUp(final double val) {
            return (int) Math.ceil(val);
        }

        /**
         * Return the PNode size as the minimum dimension; required by layouts
         * such as BoxLayout.
         * 
         * @return the minimum size for this component
         */
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        /**
         * Sets the bounds of the ProxyComponent and positions the node in the
         * area (x,y,w,h) allocated by the layout manager.
         */
        public void setBounds(final int x, final int y, final int w, final int h) {
            // important to check that the bounds have really changed, or we'll
            // cause StackOverflowException
            if (x != getX() || y != getY() || w != getWidth() || h != getHeight()) {
                super.setBounds(x, y, w, h);
                anchor.positionNode(node, x, y, w, h);
            }
        }
    }

    /**
     * Determines where nodes are anchored in the area allocated by the Swing
     * layout manager. Predefined anchor names are similar to GridBagConstraint
     * anchors and have the same semantics.
     */
    public interface Anchor {

        /**
         * Positions the node in the bounds defined.
         * 
         * @param node node to be laid out
         * @param x left of bounds
         * @param y top of bounds
         * @param width width of bounds
         * @param height height of bounds
         */
        void positionNode(PNode node, double x, double y, double width, double height);

        /**
         * Base class that provides utilities for computing common anchor
         * points.
         */

        /** Anchors the node's center as the point used when laying it out. */
        static final Anchor CENTER = new AbstractAnchor() {
            /** {@inheritDoc} */
            public void positionNode(final PNode node, final double x, final double y, final double w, final double h) {
                node.setOffset(centerX(node, x, w), centerY(node, y, h));
            }
        };

        /** Anchors the node's top center as the point used when laying it out. */
        static final Anchor NORTH = new AbstractAnchor() {
            /** {@inheritDoc} */
            public void positionNode(final PNode node, final double x, final double y, final double w, final double h) {
                node.setOffset(centerX(node, x, w), north(node, y, h));
            }
        };

        /** Anchors the node's top right as the point used when laying it out. */
        static final Anchor NORTHEAST = new AbstractAnchor() {
            /** {@inheritDoc} */
            public void positionNode(final PNode node, final double x, final double y, final double w, final double h) {
                node.setOffset(east(node, x, w), north(node, y, h));
            }
        };

        /**
         * Anchors the node's middle right as the point used when laying it out.
         */
        static final Anchor EAST = new AbstractAnchor() {
            /** {@inheritDoc} */
            public void positionNode(final PNode node, final double x, final double y, final double w, final double h) {
                node.setOffset(east(node, x, w), centerY(node, y, h));
            }
        };

        /**
         * Anchors the node's bottom right as the point used when laying it out.
         */
        static final Anchor SOUTHEAST = new AbstractAnchor() {
            /** {@inheritDoc} */
            public void positionNode(final PNode node, final double x, final double y, final double w, final double h) {
                node.setOffset(east(node, x, w), south(node, y, h));
            }
        };

        /**
         * Anchors the node's center bottom as the point used when laying it
         * out.
         */
        static final Anchor SOUTH = new AbstractAnchor() {
            /** {@inheritDoc} */
            public void positionNode(final PNode node, final double x, final double y, final double w, final double h) {
                node.setOffset(centerX(node, x, w), south(node, y, h));
            }
        };

        /** Anchors the node's bottom left as the point used when laying it out. */
        static final Anchor SOUTHWEST = new AbstractAnchor() {
            /** {@inheritDoc} */
            public void positionNode(final PNode node, final double x, final double y, final double w, final double h) {
                node.setOffset(west(node, x, w), south(node, y, h));
            }
        };

        /** Anchors the node's middle left as the point used when laying it out. */
        static final Anchor WEST = new AbstractAnchor() {
            /** {@inheritDoc} */
            public void positionNode(final PNode node, final double x, final double y, final double w, final double h) {
                node.setOffset(west(node, x, w), centerY(node, y, h));
            }
        };

        /** Anchors the node's top left as the point used when laying it out. */
        static final Anchor NORTHWEST = new AbstractAnchor() {
            /** {@inheritDoc} */
            public void positionNode(final PNode node, final double x, final double y, final double w, final double h) {
                node.setOffset(west(node, x, w), north(node, y, h));
            }
        };

        static abstract class AbstractAnchor implements Anchor {
            /**
             * Returns the x at which the given node would need to be placed so
             * that its center was in the middle of the horizontal segment
             * defined by x and width.
             * 
             * @param node node which is being analyzed
             * @param x x component of horizontal line segment
             * @param width width of horizontal line segment
             * @return x at which node would need to be placed so that its
             *         center matched the center of the line segment
             */
            protected static double centerX(final PNode node, final double x, final double width) {
                return x + (width - node.getFullBoundsReference().getWidth()) / 2;
            }

            /**
             * Returns the y at which the given node would need to be placed so
             * that its center was in the middle of the vertical segment defined
             * by y and h.
             * 
             * @param node node which is being analyzed
             * @param y y component of horizontal line segment
             * @param height height of vertical line segment
             * @return y at which node would need to be placed so that its
             *         center matched the center of the line segment
             */
            protected static double centerY(final PNode node, final double y, final double height) {
                return y + (height - node.getFullBoundsReference().getHeight()) / 2;
            }

            /**
             * Returns the y at which the given node would need to be placed so
             * that its top was against the top of the vertical segment defined.
             * 
             * @param node node which is being analyzed
             * @param y y component of horizontal line segment
             * @param height height of vertical line segment
             * @return y at which node would need to be placed so that its top
             *         matched the start of the line segment (y)
             */
            protected static double north(final PNode node, final double y, final double height) {
                return y;
            }

            /**
             * Returns the y at which the given node would need to be placed so
             * that its bottom was against the bottom of the vertical range
             * defined.
             * 
             * @param node node which is being analyzed
             * @param y y component of vertical range
             * @param height height of vertical range
             * @return y at which node would need to be placed so that its
             *         bottom matched the bottom of the range
             */
            protected static double south(final PNode node, final double y, final double height) {
                return y + height - node.getFullBoundsReference().getHeight();
            }

            /**
             * Returns the x at which the given node would need to be placed so
             * that its right side was against the right side of the horizontal
             * range defined.
             * 
             * @param node node which is being analyzed
             * @param x x component of horizontal range
             * @param width width of horizontal range
             * @return x at which node would need to be placed so that its right
             *         side touched the right side of the range defined.
             */
            protected static double east(final PNode node, final double x, final double width) {
                return x + width - node.getFullBoundsReference().getWidth();
            }

            /**
             * Returns the x at which the given node would need to be placed so
             * that its left side was against the left side of the horizontal
             * range defined.
             * 
             * @param node node which is being analyzed
             * @param x x component of horizontal range
             * @param width width of horizontal range
             * @return x at which node would need to be placed so that its left
             *         side touched the left side of the range defined (x)
             */
            protected static double west(final PNode node, final double x, final double width) {
                return x;
            }
        };
    }
}