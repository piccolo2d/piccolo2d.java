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
package edu.umd.cs.piccolox.pswing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.RepaintManager;
import javax.swing.border.Border;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/*
 This message was sent to Sun on August 27, 1999

 -----------------------------------------------

 We are currently developing Piccolo, a "scenegraph" for use in 2D graphics.
 One of our ultimate goals is to support Swing lightweight components
 within Piccolo, whose graphical space supports arbitray affine transforms.
 The challenge in this pursuit is getting the components to respond and
 render properly though not actually displayed in a standard Java component
 hierarchy.


 The first issues involved making the Swing components focusable and
 showing.  This was accomplished by adding the Swing components to a 0x0
 JComponent which was in turn added to our main Piccolo application component.
 To our good fortune, a Java component is showing merely if it and its
 ancestors are showing and not based on whether it is ACTUALLY visible.
 Likewise, focus in a JComponent depends merely on the component's
 containing window having focus.


 The second issue involved capturing the repaint calls on a Swing
 component.  Normally, for a repaint and the consequent call to
 paintImmediately, a Swing component obtains the Graphics object necessary
 to render itself through the Java component heirarchy.  However, for Piccolo
 we would like the component to render using a Graphics object that Piccolo
 may have arbitrarily transformed in some way.  By capturing in the
 RepaintManager the repaint calls made on our special Swing components, we
 are able to redirect the repaint requests through the Piccolo architecture to
 put the Graphics in its proper context.  Unfortunately, this means that
 if the Swing component contains other Swing components, then any repaint
 requests made by one of these nested components must go through
 the Piccolo architecture then through the top level Swing component
 down to the nested Swing component.  This normally doesn't cause a
 problem.  However, if calling paint on one of these nested
 children causes a call to repaint then an infinite loop ensues.  This does
 in fact happen in the Swing components that use cell renderers.  Before
 the cell renderer is painted, it is invalidated and consequently
 repainted.  We solved this problem by putting a lock on repaint calls for
 a component while that component is painting.  (A similar problem faced
 the Swing team over this same issue.  They solved it by inserting a
 CellRendererPane to capture the renderer's invalidate calls.)


 Another issue arose over the forwarding of mouse events to the Swing
 components.  Since our Swing components are not actually displayed on
 screen in the standard manner, we must manually dispatch any MouseEvents
 we want the component to receive.  Hence, we needed to find the deepest
 visible component at a particular location that accepts MouseEvents.
 Finding the deepest visible component at a point was achieved with the
 "findComponentAt" method in java.awt.Container.  With the
 "getListeners(Class listenerType)" method added in JDK1.3 Beta we are able
 to determine if the component has any Mouse Listeners. However, we haven't
 yet found a way to determine if MouseEvents have been specifically enabled
 for a component. The package private method "eventEnabled" in
 java.awt.Component does exactly what we want but is, of course,
 inaccessible.  In order to dispatch events correctly we would need a
 public accessor to the method "boolean eventEnabled(AWTEvent)" in
 java.awt.Component.


 Still another issue involves the management of cursors when the mouse is
 over a Swing component in our application.  To the Java mechanisms, the
 mouse never appears to enter the bounds of the Swing components since they
 are contained by a 0x0 JComponent.  Hence, we must manually change the
 cursor when the mouse enters one of the Swing components in our
 application. This generally works but becomes a problem if the Swing
 component's cursor changes while we are over that Swing component (for
 instance, if you resize a Table Column).  In order to manage cursors
 properly, we would need setCursor to fire property change events.


 With the above fixes, most Swing components work.  The only Swing
 components that are definitely broken are ToolTips and those that rely on
 JPopupMenu. In order to implement ToolTips properly, we would need to have
 a method in ToolTipManager that allows us to set the current manager, as
 is possible with RepaintManager.  In order to implement JPopupMenu, we
 will likely need to reimplement JPopupMenu to function in Piccolo with
 a transformed Graphics and to insert itself in the proper place in the
 Piccolo scenegraph.

 */

/**
 * <b>PSwing</b> is used to add Swing Components to a Piccolo2D canvas.
 * <p>
 * Example: adding a swing JButton to a PCanvas:
 * 
 * <pre>
 * PSwingCanvas canvas = new PSwingCanvas();
 * JButton button = new JButton(&quot;Button&quot;);
 * swing = new PSwing(canvas, button);
 * canvas.getLayer().addChild(swing);
 * </pre>
 * 
 * </p>
 * <p>
 * NOTE: PSwing has the current limitation that it does not listen for Container
 * events. This is only an issue if you create a PSwing and later add Swing
 * components to the PSwing's component hierarchy that do not have double
 * buffering turned off or have a smaller font size than the minimum font size
 * of the original PSwing's component hierarchy.
 * </p>
 * <p>
 * For instance, the following bit of code will give unexpected results:
 * 
 * <pre>
 * JPanel panel = new JPanel();
 * PSwing swing = new PSwing(panel);
 * JPanel newChild = new JPanel();
 * newChild.setDoubleBuffered(true);
 * panel.add(newChild);
 * </pre>
 * 
 * </p>
 * <p>
 * NOTE: PSwing cannot be correctly interacted with through multiple cameras.
 * There is no support for it yet.
 * </p>
 * <p>
 * NOTE: PSwing is java.io.Serializable.
 * </p>
 * <p>
 * <b>Warning:</b> Serialized objects of this class will not be compatible with
 * future Piccolo2D releases. The current serialization support is appropriate
 * for short term storage or RMI between applications running the same version
 * of Piccolo2D. A future release of Piccolo2D will provide support for long
 * term persistence.
 * </p>
 * 
 * @author Sam R. Reid
 * @author Benjamin B. Bederson
 * @author Lance E. Good
 */
public class PSwing extends PNode implements Serializable, PropertyChangeListener {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Key for this object in the Swing component's client properties. */
    public static final String PSWING_PROPERTY = "PSwing";

    /** Temporary repaint bounds. */
    private static PBounds TEMP_REPAINT_BOUNDS2 = new PBounds();

    /** Default greek threshold, <code>0.3d</code>. */
    private static final double DEFAULT_GREEK_THRESHOLD = 0.3d;

    /** Swing component for this Swing node. */
    private JComponent component = null;

    /** Minimum font size. */
    private double minFontSize = Double.MAX_VALUE;

    /**
     * Default stroke, <code>new BasicStroke()</code>. Cannot be made static
     * because BasicStroke is not serializable.
     */
    private transient Stroke defaultStroke = new BasicStroke();

    /**
     * Default font, 12 point <code>"SansSerif"</code>. Will be made final in
     * version 2.0.
     */
    // public static final Font DEFAULT_FONT = new Font(Font.SANS_SERIF,
    // Font.PLAIN, 12); jdk 1.6+
    public static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 12);

    /** Greek threshold in scale. */
    private double greekThreshold = DEFAULT_GREEK_THRESHOLD;

    /** Swing canvas for this swing node. */
    private PSwingCanvas canvas;

    /**
     * Used to keep track of which nodes we've attached listeners to since no
     * built in support in PNode.
     */
    private final List listeningTo = new ArrayList();

    /* The parent listener for camera/canvas changes. */
    private final transient PropertyChangeListener parentListener = new PropertyChangeListener() {
        /** {@inheritDoc} */
        public void propertyChange(final PropertyChangeEvent evt) {
            final PNode parent = (PNode) evt.getNewValue();
            clearListeners((PNode) evt.getOldValue());
            if (parent == null) {
                updateCanvas(null);
            }
            else {
                listenForCanvas(parent);
            }

        }

        /**
         * Clear out all the listeners registered to make sure there are no
         * stray references.
         * 
         * @param fromParent Parent to start with for clearing listeners
         */
        private void clearListeners(final PNode fromParent) {
            if (fromParent != null && listeningTo(fromParent)) {
                fromParent.removePropertyChangeListener(PNode.PROPERTY_PARENT, parentListener);
                listeningTo.remove(fromParent);
                clearListeners(fromParent.getParent());
            }
        }

    };

    /**
     * Create a new visual component wrapper for the specified Swing component.
     * 
     * @param component Swing component to be wrapped
     */
    public PSwing(final JComponent component) {
        this.component = component;
        component.putClientProperty(PSWING_PROPERTY, this);
        init(component);
        component.revalidate();

        component.addPropertyChangeListener(new PropertyChangeListener() {
            /** {@inheritDoc} */
            public void propertyChange(final PropertyChangeEvent evt) {
                reshape();
            }
        });

        component.addComponentListener(new ComponentAdapter() {
            /** {@inheritDoc} */
            public void componentHidden(final ComponentEvent e) {
                setVisible(false);
            }

            /** {@inheritDoc} */
            public void componentShown(final ComponentEvent e) {
                setVisible(true);
            }
        });

        reshape();
        listenForCanvas(this);
    }

    /**
     * @deprecated by {@link PSwing(JComponent)}
     * 
     * @param swingCanvas canvas on which the PSwing node will be embedded
     * @param component not used
     */
    public PSwing(final PSwingCanvas swingCanvas, final JComponent component) {
        this(component);
    }

    /**
     * Ensures the bounds of the underlying component are accurate, and sets the
     * bounds of this PNode.
     */
    void reshape() {
        final Border border = component.getBorder();

        int width = Math.max(component.getMinimumSize().width, component.getPreferredSize().width);
        final int height = component.getPreferredSize().height;

        if (border != null) {
            final Insets borderInsets = border.getBorderInsets(component);
            width += borderInsets.left + borderInsets.right;
        }

        component.setBounds(0, 0, width, height);
        setBounds(0, 0, width, height);
    }

    /** {@inheritDoc} */
    protected void paint(final PPaintContext paintContext) {
        final Graphics2D graphics = paintContext.getGraphics();

        if (defaultStroke == null) {
            defaultStroke = new BasicStroke();
        }
        graphics.setStroke(defaultStroke);

        graphics.setFont(DEFAULT_FONT);

        if (component.getParent() == null) {
            component.revalidate();
        }

        if (component instanceof JLabel) {
            final JLabel label = (JLabel) component;
            enforceNoEllipsis(label.getText(), label.getIcon(), label.getIconTextGap(), graphics);
        }
        else if (component instanceof JButton) {
            final JButton button = (JButton) component;
            enforceNoEllipsis(button.getText(), button.getIcon(), button.getIconTextGap(), graphics);
        }

        if (shouldRenderGreek(paintContext)) {
            paintGreek(paintContext);
        }
        else {
            paintComponent(paintContext);
        }
    }

    /**
     * Workaround to prevent text-rendering Swing components from drawing an
     * ellipsis incorrectly.
     * 
     * @param text text
     * @param icon icon
     * @param iconGap icon gap
     * @param graphics graphics
     */
    private void enforceNoEllipsis(final String text, final Icon icon, final int iconGap, final Graphics2D graphics) {
        final Rectangle2D textBounds = component.getFontMetrics(component.getFont()).getStringBounds(text, graphics);
        double minAcceptableWidth = textBounds.getWidth();
        double minAcceptableHeight = textBounds.getHeight();

        if (icon != null) {
            minAcceptableWidth += icon.getIconWidth();
            minAcceptableWidth += iconGap;
            minAcceptableHeight = Math.max(icon.getIconHeight(), minAcceptableHeight);
        }

        if (component.getMinimumSize().getWidth() < minAcceptableWidth) {
            final Dimension newMinimumSize = new Dimension((int) Math.ceil(minAcceptableWidth), (int) Math
                    .ceil(minAcceptableHeight));
            component.setMinimumSize(newMinimumSize);
            reshape();
        }
    }

    /**
     * Return the greek threshold in scale. When the scale will be below this
     * threshold the Swing component is rendered as 'greek' instead of painting
     * the Swing component. Defaults to {@link DEFAULT_GREEK_THRESHOLD}.
     * 
     * @see PSwing#paintGreek(PPaintContext)
     * @return the current greek threshold in scale
     */
    public double getGreekThreshold() {
        return greekThreshold;
    }

    /**
     * Set the greek threshold in scale to <code>greekThreshold</code>. When the
     * scale will be below this threshold the Swing component is rendered as
     * 'greek' instead of painting the Swing component..
     * 
     * @see PSwing#paintGreek(PPaintContext)
     * @param greekThreshold greek threshold in scale
     */
    public void setGreekThreshold(final double greekThreshold) {
        this.greekThreshold = greekThreshold;
        invalidatePaint();
    }

    /**
     * Return true if this Swing node should render as greek given the specified
     * paint context.
     * 
     * @param paintContext paint context
     * @return true if this Swing node should render as greek given the
     *         specified paint context
     */
    protected boolean shouldRenderGreek(final PPaintContext paintContext) {
        return paintContext.getScale() < greekThreshold || minFontSize * paintContext.getScale() < 0.5;
    }

    /**
     * Paint the Swing component as greek with the specified paint context. The
     * implementation in this class paints a rectangle with the Swing
     * component's background color and paints a stroke with the Swing
     * component's foreground color.
     * 
     * @param paintContext paint context
     */
    protected void paintGreek(final PPaintContext paintContext) {
        final Graphics2D graphics = paintContext.getGraphics();
        final Color background = component.getBackground();
        final Color foreground = component.getForeground();
        final Rectangle2D rect = getBounds();

        if (background != null) {
            graphics.setColor(background);
        }
        graphics.fill(rect);

        if (foreground != null) {
            graphics.setColor(foreground);
        }
        graphics.draw(rect);
    }

    /**
     * Remove from the SwingWrapper; throws an exception if no canvas is
     * associated with this PSwing.
     */
    public void removeFromSwingWrapper() {
        if (canvas != null && Arrays.asList(canvas.getSwingWrapper().getComponents()).contains(component)) {
            canvas.getSwingWrapper().remove(component);
        }
    }

    /**
     * Paint the Swing component with the specified paint context.
     * 
     * @param paintContext paint context
     */
    protected void paintComponent(final PPaintContext paintContext) {
        if (component.getBounds().isEmpty()) {
            // The component has not been initialized yet.
            return;
        }

        final Graphics2D graphics = paintContext.getGraphics();
        final PSwingRepaintManager manager = (PSwingRepaintManager) RepaintManager.currentManager(component);
        manager.lockRepaint(component);
        component.paint(graphics);
        manager.unlockRepaint(component);
    }

    /** {@inheritDoc} */
    public void setVisible(final boolean visible) {
        super.setVisible(visible);
        component.setVisible(visible);
    }

    /**
     * Repaints the specified portion of this visual component. Note that the
     * input parameter may be modified as a result of this call.
     * 
     * @param repaintBounds bounds needing repainting
     */
    public void repaint(final PBounds repaintBounds) {
        final Shape sh = getTransform().createTransformedShape(repaintBounds);
        TEMP_REPAINT_BOUNDS2.setRect(sh.getBounds2D());
        repaintFrom(TEMP_REPAINT_BOUNDS2, this);
    }

    /**
     * Sets the Swing component's bounds to its preferred bounds unless it
     * already is set to its preferred size. Also updates the visual components
     * copy of these bounds
     */
    public void computeBounds() {
        reshape();
    }

    /**
     * Return the Swing component that this Swing node wraps.
     * 
     * @return the Swing component that this Swing node wraps
     */
    public JComponent getComponent() {
        return component;
    }

    /**
     * We need to turn off double buffering of Swing components within Piccolo
     * since all components contained within a native container use the same
     * buffer for double buffering. With normal Swing widgets this is fine, but
     * for Swing components within Piccolo this causes problems. This function
     * recurses the component tree rooted at c, and turns off any double
     * buffering in use. It also updates the minimum font size based on the font
     * size of c and adds a property change listener to listen for changes to
     * the font.
     * 
     * @param c The Component to be recursively unDoubleBuffered
     */
    void init(final Component c) {
        if (c.getFont() != null) {
            minFontSize = Math.min(minFontSize, c.getFont().getSize());
        }

        if (c instanceof Container) {
            final Component[] children = ((Container) c).getComponents();
            if (children != null) {
                for (int j = 0; j < children.length; j++) {
                    init(children[j]);
                }
            }
            ((Container) c).addContainerListener(new ContainerAdapter() {
                /** {@inheritDoc} */
                public void componentAdded(final ContainerEvent event) {
                    init(event.getChild());
                }
            });
        }
        if (c instanceof JComponent) {
            ((JComponent) c).setDoubleBuffered(false);
            c.addPropertyChangeListener("font", this);
            c.addComponentListener(new ComponentAdapter() {
                public void componentResized(final ComponentEvent e) {
                    computeBounds();
                }

                public void componentShown(final ComponentEvent e) {
                    computeBounds();
                }
            });
        }
    }

    /** {@inheritDoc} */
    public void propertyChange(final PropertyChangeEvent evt) {
        if (component.isAncestorOf((Component) evt.getSource()) && ((Component) evt.getSource()).getFont() != null) {
            minFontSize = Math.min(minFontSize, ((Component) evt.getSource()).getFont().getSize());
        }
    }

    /** {@inheritDoc} */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        init(component);
    }

    /**
     * Attaches a listener to the specified node and all its parents to listen
     * for a change in the PSwingCanvas. Only PROPERTY_PARENT listeners are
     * added so this code wouldn't handle if a PLayer were viewed by a different
     * PCamera since that constitutes a child change.
     * 
     * @param node The child node at which to begin a parent-based traversal for
     *            adding listeners.
     */
    private void listenForCanvas(final PNode node) {
        // need to get the full tree for this node
        PNode p = node;
        while (p != null) {
            listenToNode(p);

            final PNode parent = p;
            if (parent instanceof PCamera) {
                final PCamera cam = (PCamera) parent;
                if (cam.getComponent() instanceof PSwingCanvas) {
                    updateCanvas((PSwingCanvas) cam.getComponent());
                }
            }
            else if (parent instanceof PLayer) {
                final PLayer player = (PLayer) parent;
                for (int i = 0; i < player.getCameraCount(); i++) {
                    final PCamera cam = player.getCamera(i);
                    if (cam.getComponent() instanceof PSwingCanvas) {
                        updateCanvas((PSwingCanvas) cam.getComponent());
                        break;
                    }
                }
            }
            p = p.getParent();
        }
    }

    /**
     * Attach a listener to the specified node, if one has not already been
     * attached.
     * 
     * @param node the node to listen to for parent/pcamera/pcanvas changes
     */
    private void listenToNode(final PNode node) {
        if (!listeningTo(node)) {
            listeningTo.add(node);
            node.addPropertyChangeListener(PNode.PROPERTY_PARENT, parentListener);
        }
    }

    /**
     * Determine whether this PSwing is already listening to the specified node
     * for camera/canvas changes.
     * 
     * @param node the node to check
     * @return true if this PSwing is already listening to the specified node
     *         for camera/canvas changes
     */
    private boolean listeningTo(final PNode node) {
        for (int i = 0; i < listeningTo.size(); i++) {
            final PNode pNode = (PNode) listeningTo.get(i);
            if (pNode == node) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes this PSwing from previous PSwingCanvas (if any), and ensure that
     * this PSwing is attached to the new PSwingCanvas.
     * 
     * @param newCanvas the new PSwingCanvas (may be null)
     */
    private void updateCanvas(final PSwingCanvas newCanvas) {
        if (newCanvas != canvas) {
            if (canvas != null) {
                canvas.removePSwing(this);
            }
            canvas = newCanvas;
            if (newCanvas != null) {
                canvas.addPSwing(this);
                reshape();
                repaint();
            }
        }
    }
}
