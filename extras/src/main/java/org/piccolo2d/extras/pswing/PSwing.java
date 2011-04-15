/*
 * Copyright (c) 2008, Piccolo2D project, http://piccolo2d.org
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
package org.piccolo2d.extras.pswing;

import org.piccolo2d.PCamera;
import org.piccolo2d.PLayer;
import org.piccolo2d.PNode;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PPaintContext;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

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
 will likely need to re-implement JPopupMenu to function in Piccolo2d with
 a transformed Graphics and to insert itself in the proper place in the
 Piccolo2d scenegraph.

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
 * future Piccolo releases. The current serialization support is appropriate for
 * short term storage or RMI between applications running the same version of
 * Piccolo. A future release of Piccolo will provide support for long term
 * persistence.
 * </p>
 * 
 * @author Sam R. Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author Benjamin B. Bederson
 * @author Lance E. Good
 * 
 */
public class PSwing extends PNode implements Serializable, PropertyChangeListener {
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Key for this object in the Swing component's client properties. */
    public static final String PSWING_PROPERTY = "PSwing";

    /** Temporary repaint bounds. */
    private static final PBounds TEMP_REPAINT_BOUNDS2 = new PBounds();

    /** For use when buffered painting is enabled. */
    private static final Color BUFFER_BACKGROUND_COLOR = new Color(0, 0, 0, 0);

    private static final AffineTransform IDENTITY_TRANSFORM = new AffineTransform();

    /** Default Greek threshold, <code>0.3d</code>. */
    private static final double DEFAULT_GREEK_THRESHOLD = 0.3d;

    /** The cutoff at which the Swing component is rendered greek. */
    private double greekThreshold = DEFAULT_GREEK_THRESHOLD;

    /** Swing component for this Swing node. */
    private JComponent component = null;

    /**
     * Whether or not to use buffered painting.
     * @see #paint(java.awt.Graphics2D)
     */
    private boolean useBufferedPainting = false;

    /** Used when buffered painting is enabled. */
    private BufferedImage buffer;

    /** Minimum font size. */
    private double minFontSize = Double.MAX_VALUE;

    /**
     * Default stroke, <code>new BasicStroke()</code>. Cannot be made static
     * because BasicStroke is not serializable.  Should not be null.
     */
    private Stroke defaultStroke = new BasicStroke();

    /**
     * Default font, 12 point <code>"SansSerif"</code>. Will be made final in
     * version 2.0.
     */
    // public static final Font DEFAULT_FONT = new Font(Font.SANS_SERIF,
    // Font.PLAIN, 12); jdk 1.6+
    private static final Font DEFAULT_FONT = new Font("Serif", Font.PLAIN, 12);

    /** Swing canvas for this swing node. */
    private PSwingCanvas canvas;

    /**
     * Used to keep track of which nodes we've attached listeners to since no
     * built in support in PNode.
     */
    private final ArrayList listeningTo = new ArrayList();

    /** The parent listener for camera/canvas changes. */
    private final PropertyChangeListener parentListener = new PropertyChangeListener() {
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
            if (fromParent != null && isListeningTo(fromParent)) {
                fromParent.removePropertyChangeListener(PNode.PROPERTY_PARENT, parentListener);
                listeningTo.remove(fromParent);
                clearListeners(fromParent.getParent());
            }
        }

    };

    /**
     * Listens to container nodes for changes to its contents. Any additions
     * will automatically have double buffering turned off.
     */
    private final ContainerListener doubleBufferRemover = new ContainerAdapter() {
        public void componentAdded(final ContainerEvent event) {
            Component childComponent = event.getChild();
            if (childComponent != null && childComponent instanceof JComponent) {
                disableDoubleBuffering(((JComponent) childComponent));
            }
        };

        /**
         * Disables double buffering on every component in the hierarchy of the
         * targetComponent.
         * 
         * I'm assuming that the intent of the is method is that it should be
         * called explicitly by anyone making changes to the hierarchy of the
         * Swing component graph.
         * @param targetComponent the component for which double buffering should be removed
         */
        private void disableDoubleBuffering(final JComponent targetComponent) {
            targetComponent.setDoubleBuffered( false );
            for (int i = 0; i < targetComponent.getComponentCount(); i++) {
                final Component c = targetComponent.getComponent(i);
                if (c instanceof JComponent) {
                    disableDoubleBuffering((JComponent) c);
                }
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
        initializeComponent(component);

        component.revalidate();
        updateBounds();
        listenForCanvas(this);
    }

    /**
     * @deprecated by {@link #PSwing(JComponent)}
     * 
     * @param swingCanvas canvas on which the PSwing node will be embedded
     * @param component not used
     */
    public PSwing(final PSwingCanvas swingCanvas, final JComponent component) {
        this(component);
    }

    /**
     * If true {@link PSwing} will paint the {@link JComponent} to a buffer with no graphics
     * transformations applied and then paint the buffer to the target transformed
     * graphics context. On some platforms (such as Mac OS X) rendering {@link JComponent}s to
     * a transformed context is slow. Enabling buffered painting gives a significant performance
     * boost on these platforms; however, at the expense of a lower-quality drawing result at larger
     * scales.
     * @since 1.3.1
     * @param useBufferedPainting true if this {@link PSwing} should use buffered painting
     */
    public void setUseBufferedPainting(final boolean useBufferedPainting) {
        this.useBufferedPainting = useBufferedPainting;
    }

    public boolean isUseBufferedPainting() {
        return this.useBufferedPainting;
    }

    /**
     * Ensures the bounds of the underlying component are accurate, and sets the
     * bounds of this PNode.
     */
    public void updateBounds() {
        /*
         * Need to explicitly set the component's bounds because 
         * the component's parent (PSwingCanvas.ChildWrapper) has no layout manager.
         */
        if (componentNeedsResizing()) {
            updateComponentSize();
        }
        setBounds( 0, 0, component.getPreferredSize().width, component.getPreferredSize().height );
    }

    /**
     * Since the parent ChildWrapper has no layout manager, it is the responsibility of this PSwing
     * to make sure the component has its bounds set properly, otherwise it will not be drawn properly.
     * This method sets the bounds of the component to be equal to its preferred size.
     */
    private void updateComponentSize() {
        component.setBounds( 0, 0, component.getPreferredSize().width, component.getPreferredSize().height );
    }

    /**
     * Determines whether the component should be resized, based on whether its actual width and height
     * differ from its preferred width and height.
     * @return true if the component should be resized.
     */
    private boolean componentNeedsResizing() {
        return component.getWidth() != component.getPreferredSize().width || component.getHeight() != component.getPreferredSize().height;
    }

    /**
     * Paints the PSwing on the specified renderContext.  Also determines if
     * the Swing component should be rendered normally or as a filled rectangle (greeking).
     * <p/>
     * The transform, clip, and composite will be set appropriately when this
     * object is rendered. It is up to this object to restore the transform,
     * clip, and composite of the Graphics2D if this node changes any of them.
     * However, the color, font, and stroke are unspecified by Piccolo. This
     * object should set those things if they are used, but they do not need to
     * be restored.
     * 
     * @param renderContext Contains information about current render.
     */
    public void paint(final PPaintContext renderContext) {
        if (componentNeedsResizing()) {
            updateComponentSize();
            component.validate();
        }
        final Graphics2D g2 = renderContext.getGraphics();

        //Save Stroke and Font for restoring.
        Stroke originalStroke = g2.getStroke();
        Font originalFont = g2.getFont();

        g2.setStroke(defaultStroke);
        g2.setFont(DEFAULT_FONT);
        
        if (shouldRenderGreek(renderContext)) {
            paintAsGreek(g2);
        }
        else {
            paint(g2);
        }

        //Restore the stroke and font on the Graphics2D
        g2.setStroke(originalStroke);
        g2.setFont(originalFont);
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
     * Paints the Swing component as greek.  This method assumes that the stroke has been set beforehand.
     * 
     * @param g2 The graphics used to render the filled rectangle
     */
    public void paintAsGreek(final Graphics2D g2) {
        //Save original color for restoring painting as greek.
        Color originalColor = g2.getColor();

        if (component.getBackground() != null) {
            g2.setColor(component.getBackground());
        }
        g2.fill(getBounds());

        if (component.getForeground() != null) {
            g2.setColor(component.getForeground());
        }
        g2.draw(getBounds());

        //Restore original color on the Graphics2D
        g2.setColor( originalColor );
    }

    /** {@inheritDoc} */
    public void setVisible(final boolean visible) {
        super.setVisible(visible);

        if (component.isVisible() != visible) {
            component.setVisible(visible);
        }
    }

    /**
     * Remove from the SwingWrapper; throws an exception if no canvas is
     * associated with this PSwing.
     */
    public void removeFromSwingWrapper() {
        if (canvas != null && isComponentSwingWrapped()) {
            canvas.getSwingWrapper().remove(component);
        }
    }

    private boolean isComponentSwingWrapped() {
        return Arrays.asList(canvas.getSwingWrapper().getComponents()).contains(component);
    }

    /**
     * Renders the wrapped component to the graphics context provided.
     * 
     * @param g2 graphics context for rendering the JComponent
     */
    public void paint(final Graphics2D g2) {
        if (component.getBounds().isEmpty()) {
            // The component has not been initialized yet.
            return;
        }

        final PSwingRepaintManager manager = (PSwingRepaintManager) RepaintManager.currentManager(component);
        manager.lockRepaint(component);

        final RenderingHints oldHints = g2.getRenderingHints();

        if (useBufferedPainting) {
            Graphics2D bufferedGraphics = getBufferedGraphics(g2);
            component.paint(bufferedGraphics);
            g2.drawRenderedImage(buffer, IDENTITY_TRANSFORM);
        } else {
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
            component.paint(g2);
        }

        g2.setRenderingHints(oldHints);

        manager.unlockRepaint(component);
    }

    private Graphics2D getBufferedGraphics(Graphics2D source) {
        final Graphics2D bufferedGraphics;
        if(!isBufferValid()) {
            // Get the graphics context associated with a new buffered image.
            // Use TYPE_INT_ARGB_PRE so that transparent components look good on Windows.
            buffer = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
            bufferedGraphics = buffer.createGraphics();
        }
        else {
            // Use the graphics context associated with the existing buffered image
            bufferedGraphics = buffer.createGraphics();
            // Clear the buffered image to prevent artifacts on Macintosh
            bufferedGraphics.setBackground(BUFFER_BACKGROUND_COLOR);
            bufferedGraphics.clearRect(0, 0, component.getWidth(), component.getHeight());
        }
        bufferedGraphics.setRenderingHints(source.getRenderingHints());
        return bufferedGraphics;
    }

    /**
     * Tells whether the buffer for the image of the Swing components
     * is currently valid.
     *
     * @return true if the buffer is currently valid
     */
    private boolean isBufferValid() {
        return !(buffer == null || buffer.getWidth() != component.getWidth() || buffer.getHeight() != component.getHeight());
    }

    /**
     * Repaints the specified portion of this visual component. Note that the
     * input parameter may be modified as a result of this call.
     * 
     * @param repaintBounds bounds that need repainting
     */
    public void repaint(final PBounds repaintBounds) {
        final Shape sh = getTransform().createTransformedShape(repaintBounds);
        TEMP_REPAINT_BOUNDS2.setRect(sh.getBounds2D());
        repaintFrom(TEMP_REPAINT_BOUNDS2, this);
    }

    /**
     * Returns the Swing component that this visual component wraps.
     * 
     * @return The Swing component wrapped by this PSwing node
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
    private void initializeComponent(final Component c) {

        if (c.getFont() != null) {
            minFontSize = Math.min(minFontSize, c.getFont().getSize());
        }
        c.addPropertyChangeListener( "font", this );

        if (c instanceof Container) {
            initializeChildren((Container) c);
            ((Container) c).addContainerListener(doubleBufferRemover);
        }

        if (c instanceof JComponent) {
            ((JComponent) c).setDoubleBuffered(false);
        }
    }

    private void initializeChildren(final Container c) {
        final Component[] children = c.getComponents();
        if (children != null) {
            for (int j = 0; j < children.length; j++) {
                initializeComponent(children[j]);
            }
        }
    }

    /**
     * Listens for changes in font on components rooted at this PSwing.
     * 
     * @param evt property change event representing the change in font
     */
    public void propertyChange(final PropertyChangeEvent evt) {
        final Component source = (Component) evt.getSource();
        if (source.getFont() != null && component.isAncestorOf(source)) {
            minFontSize = Math.min(minFontSize, source.getFont().getSize());
        }
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        initializeComponent(component);
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
            // System.out.println( "parent = " + parent.getClass() );
            if (parent instanceof PLayer) {
                final PLayer player = (PLayer) parent;
                // System.out.println( "Found player: with " +
                // player.getCameraCount() + " cameras" );
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
     * Attach a property change listener to the specified node, if one has not
     * already been attached.
     * 
     * @param node the node to listen to for parent/pcamera/pcanvas changes
     */
    private void listenToNode(final PNode node) {
        if (!isListeningTo(node)) {
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
    private boolean isListeningTo(final PNode node) {
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
        if (newCanvas == canvas) {
            return;
        }

        if (canvas != null) {
            canvas.removePSwing(this);
        }

        if (newCanvas == null) {
            canvas = null;
        }
        else {
            canvas = newCanvas;
            canvas.addPSwing(this);
            updateBounds();
            repaint();
            canvas.invalidate();
            canvas.revalidate();
            canvas.repaint();
        }

    }

    /**
     * Return the Greek threshold scale. When the scale will be below this
     * threshold the Swing component is rendered as 'Greek' instead of painting
     * the Swing component. Defaults to {@link #DEFAULT_GREEK_THRESHOLD}.
     * 
     * @see PSwing#paintAsGreek(Graphics2D)
     * @return the current Greek threshold scale
     */
    public double getGreekThreshold() {
        return greekThreshold;
    }

    /**
     * Set the Greek threshold in scale to <code>greekThreshold</code>. When the
     * scale will be below this threshold the Swing component is rendered as
     * 'Greek' instead of painting the Swing component..
     * 
     * @see PSwing#paintAsGreek(Graphics2D)
     * @param greekThreshold Greek threshold in scale
     */
    public void setGreekThreshold(final double greekThreshold) {
        this.greekThreshold = greekThreshold;
        invalidatePaint();
    }
}
