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
package edu.umd.cs.piccolox.pswing;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
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
  will likely need to reimplement JPopupMenu to function in Piccolo with
  a transformed Graphics and to insert itself in the proper place in the
  Piccolo scenegraph.

*/


/**
 * <b>PSwing</b> is used to add Swing Components to a Piccolo canvas.
 * <p>
 * Example: adding a swing JButton to a PCanvas:
 * <pre>
 *     PSwingCanvas canvas = new PSwingCanvas();
 *     JButton button = new JButton("Button");
 *     swing = new PSwing(canvas, button);
 *     canvas.getLayer().addChild(swing);
 * <pre>
 * </p>
 * <p>
 * NOTE: PSwing has the current limitation that it does not listen for
 *       Container events.  This is only an issue if you create a PSwing
 *       and later add Swing components to the PSwing's component hierarchy
 *       that do not have double buffering turned off or have a smaller font
 *       size than the minimum font size of the original PSwing's component
 *       hierarchy.
 * </p>
 * <p>
 *       For instance, the following bit of code will give unexpected
 *       results:
 *       <pre>
 *            JPanel panel = new JPanel();
 *            PSwing swing = new PSwing(panel);
 *            JPanel newChild = new JPanel();
 *            newChild.setDoubleBuffered(true);
 *            panel.add(newChild);
 *       </pre>
 * </p>
 * <p>
 * NOTE: PSwing cannot be correctly interacted with through multiple cameras.
 * There is no support for it yet.
 * </p>
 * <p>
 * NOTE: PSwing is java.io.Serializable.
 * </p>
 * <p>
 * <b>Warning:</b> Serialized objects of this class will not be
 * compatible with future Piccolo releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running the
 * same version of Piccolo. A future release of Piccolo will provide support for long
 * term persistence.
 * </p>
 *
 * @author Sam R. Reid
 * @author Benjamin B. Bederson
 * @author Lance E. Good
 *
 * 3-23-2007 edited to automatically detect PCamera/PSwingCanvas to allow single-arg constructor usage
 */
public class PSwing extends PNode implements Serializable, PropertyChangeListener {


    /**
     * Used as a hashtable key for this object in the Swing component's
     * client properties.
     */
    public static final String PSWING_PROPERTY = "PSwing";
    private static final AffineTransform IDENTITY_TRANSFORM = new AffineTransform();
    private static PBounds TEMP_REPAINT_BOUNDS2 = new PBounds();

    /**
     * The cutoff at which the Swing component is rendered greek
     */
    private double renderCutoff = 0.3;
    private JComponent component = null;
    private double minFontSize = Double.MAX_VALUE;
    private Stroke defaultStroke = new BasicStroke();
    private Font defaultFont = new Font( "Serif", Font.PLAIN, 12 );
    private BufferedImage buffer;
    private static final Color BUFFER_BACKGROUND_COLOR = new Color( 0, 0, 0, 0 );
    private PSwingCanvas canvas;

    ////////////////////////////////////////////////////////////
    ///////Following fields are for automatic canvas/camera detection
    ////////////////////////////////////////////////////////////
    /*/keep track of which nodes we've attached listeners to since no built in support in PNode*/
    private ArrayList listeningTo = new ArrayList();

    /*The parent listener for camera/canvas changes*/
    private PropertyChangeListener parentListener = new PropertyChangeListener() {
        public void propertyChange( PropertyChangeEvent evt ) {
            PNode source = (PNode)evt.getSource();
            PNode parent = source.getParent();
            if( parent != null ) {
                listenForCanvas( parent );
            }

        }
    };

    /**
     * Constructs a new visual component wrapper for the Swing component.
     *
     * @param component The swing component to be wrapped
     */
    public PSwing(JComponent component) {
        this.component = component;
        component.putClientProperty( PSWING_PROPERTY, this );
        init( component );
        component.revalidate();
        component.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                reshape();
            }
        } );
        reshape();
        listenForCanvas( this );
    }

    /**
     * Deprecated constructor for application code still depending on this signature. 
     * @param pSwingCanvas
     * @param component
     * @deprecated 
     */
    public PSwing(PSwingCanvas pSwingCanvas, JComponent component) {
        this(component);
    }

    /**
     * Ensures the bounds of the underlying component are accurate, and sets the bounds of this PNode.
     */
    void reshape() {
        component.setBounds( 0, 0, component.getPreferredSize().width, component.getPreferredSize().height );
        setBounds( 0, 0, component.getPreferredSize().width, component.getPreferredSize().height );
    }

    /**
     * Determines if the Swing component should be rendered normally or
     * as a filled rectangle.
     * <p/>
     * The transform, clip, and composite will be set appropriately when this object
     * is rendered.  It is up to this object to restore the transform, clip, and composite of
     * the Graphics2D if this node changes any of them. However, the color, font, and stroke are
     * unspecified by Piccolo.  This object should set those things if they are used, but
     * they do not need to be restored.
     *
     * @param renderContext Contains information about current render.
     */
    public void paint( PPaintContext renderContext ) {
        Graphics2D g2 = renderContext.getGraphics();

        if( defaultStroke == null ) {
            defaultStroke = new BasicStroke();
        }
        g2.setStroke( defaultStroke );

        if( defaultFont == null ) {
            defaultFont = new Font( "Serif", Font.PLAIN, 12 );
        }

        g2.setFont( defaultFont );

        if( component.getParent() == null ) {
//            pSwingCanvas.getSwingWrapper().add( component );
            component.revalidate();
        }

        if( shouldRenderGreek( renderContext ) ) {
            paintAsGreek( g2 );
        }
        else {
            paint( g2 );
        }

    }

    protected boolean shouldRenderGreek( PPaintContext renderContext ) {
        return ( renderContext.getScale() < renderCutoff
//                 && pSwingCanvas.getInteracting()
        ) ||
          minFontSize * renderContext.getScale() < 0.5;
    }

    /**
     * Paints the Swing component as greek.
     *
     * @param g2 The graphics used to render the filled rectangle
     */
    public void paintAsGreek( Graphics2D g2 ) {
        Color background = component.getBackground();
        Color foreground = component.getForeground();
        Rectangle2D rect = getBounds();

        if( background != null ) {
            g2.setColor( background );
        }
        g2.fill( rect );

        if( foreground != null ) {
            g2.setColor( foreground );
        }
        g2.draw( rect );
    }

    /**
     * Remove from the SwingWrapper; throws an exception if no canvas is associated with this PSwing.
     */
    public void removeFromSwingWrapper() {
        if( canvas != null && Arrays.asList( this.canvas.getSwingWrapper().getComponents() ).contains( component ) ) {
            this.canvas.getSwingWrapper().remove( component );
        }
    }

    /**
     * Renders to a buffered image, then draws that image to the
     * drawing surface associated with g2 (usually the screen).
     *
     * @param g2 graphics context for rendering the JComponent
     */
    public void paint( Graphics2D g2 ) {
        if( component.getBounds().isEmpty() ) {
            // The component has not been initialized yet.
            return;
        }

        PSwingRepaintManager manager = (PSwingRepaintManager)RepaintManager.currentManager( component );
        manager.lockRepaint( component );

        Graphics2D bufferedGraphics = null;
        if( !isBufferValid() ) {
            // Get the graphics context associated with a new buffered image.
            // Use TYPE_INT_ARGB_PRE so that transparent components look good on Windows.
            buffer = new BufferedImage( component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE );
            bufferedGraphics = buffer.createGraphics();
        }
        else {
            // Use the graphics context associated with the existing buffered image
            bufferedGraphics = buffer.createGraphics();
            // Clear the buffered image to prevent artifacts on Macintosh
            bufferedGraphics.setBackground( BUFFER_BACKGROUND_COLOR );
            bufferedGraphics.clearRect( 0, 0, component.getWidth(), component.getHeight() );
        }

        // Start with the rendering hints from the provided graphics context
        bufferedGraphics.setRenderingHints( g2.getRenderingHints() );

        //PSwing sometimes causes JComponent text to render with "..." when fractional font metrics are enabled.  These are now always disabled for the offscreen buffer.
        bufferedGraphics.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF );

        // Draw the component to the buffer
        component.paint( bufferedGraphics );

        // Draw the buffer to g2's associated drawing surface
        g2.drawRenderedImage( buffer, IDENTITY_TRANSFORM );

        manager.unlockRepaint( component );
    }

    /**
     * Tells whether the buffer for the image of the Swing components
     * is currently valid.
     *
     * @return true if the buffer is currently valid
     */
    private boolean isBufferValid() {
        return !( buffer == null || buffer.getWidth() != component.getWidth() || buffer.getHeight() != component.getHeight() );
    }

    /**
     * Repaints the specified portion of this visual component
     * Note that the input parameter may be modified as a result of this call.
     *
     * @param repaintBounds
     */
    public void repaint( PBounds repaintBounds ) {
        Shape sh = getTransform().createTransformedShape( repaintBounds );
        TEMP_REPAINT_BOUNDS2.setRect( sh.getBounds2D() );
        repaintFrom( TEMP_REPAINT_BOUNDS2, this );
    }

    /**
     * Sets the Swing component's bounds to its preferred bounds
     * unless it already is set to its preferred size.  Also
     * updates the visual components copy of these bounds
     */
    public void computeBounds() {
        reshape();
//        if( !component.getBounds().isEmpty() ) {
//            Dimension d = component.getPreferredSize();
//            getBoundsReference().setRect( 0, 0, d.getWidth(), d.getHeight() );
//            if( !component.getSize().equals( d ) ) {
//                component.setBounds( 0, 0, (int)d.getWidth(), (int)d.getHeight() );
//            }
//        }
    }

    /**
     * Returns the Swing component that this visual component wraps
     *
     * @return The Swing component that this visual component wraps
     */
    public JComponent getComponent() {
        return component;
    }

    /**
     * We need to turn off double buffering of Swing components within
     * Piccolo since all components contained within a native container
     * use the same buffer for double buffering.  With normal Swing
     * widgets this is fine, but for Swing components within Piccolo this
     * causes problems.  This function recurses the component tree
     * rooted at c, and turns off any double buffering in use.  It also
     * updates the minimum font size based on the font size of c and adds
     * a property change listener to listen for changes to the font.
     *
     * @param c The Component to be recursively unDoubleBuffered
     */
    void init( Component c ) {
        Component[] children = null;
        if( c instanceof Container ) {
            children = ( (Container)c ).getComponents();
        }

        if( c.getFont() != null ) {
            minFontSize = Math.min( minFontSize, c.getFont().getSize() );
        }

        if( children != null ) {
            for( int j = 0; j < children.length; j++ ) {
                init( children[j] );
            }
        }

        if( c instanceof JComponent ) {
            ( (JComponent)c ).setDoubleBuffered( false );
            c.addPropertyChangeListener( "font", this );
            c.addComponentListener( new ComponentAdapter() {
                public void componentResized( ComponentEvent e ) {
                    computeBounds();
                }

                public void componentShown( ComponentEvent e ) {
                    computeBounds();
                }
            } );
        }
    }

    /**
     * Listens for changes in font on components rooted at this PSwing
     */
    public void propertyChange( PropertyChangeEvent evt ) {
        if( component.isAncestorOf( (Component)evt.getSource() ) &&
            ( (Component)evt.getSource() ).getFont() != null ) {
            minFontSize = Math.min( minFontSize, ( (Component)evt.getSource() ).getFont().getSize() );
        }
    }

    private void readObject( ObjectInputStream in ) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        init( component );
    }

        ////////////////////////////////////////////////////////////
    ///////Start methods for automatic canvas detection
    ////////////////////////////////////////////////////////////
    /**
     * Attaches a listener to the specified node and all its parents to listen
     * for a change in the PSwingCanvas.  Only PROPERTY_PARENT listeners are added
     * so this code wouldn't handle if a PLayer were viewed by a different PCamera
     * since that constitutes a child change.
     * @param node The child node at which to begin a parent-based traversal for adding listeners.
     */
    private void listenForCanvas( PNode node ) {
        //need to get the full tree for this node
        PNode p = node;
        while( p != null ) {
            listenToNode( p );

            PNode parent = p;
//            System.out.println( "parent = " + parent.getClass() );
            if( parent instanceof PLayer ) {
                PLayer player = (PLayer)parent;
//                System.out.println( "Found player: with " + player.getCameraCount() + " cameras" );
                for( int i = 0; i < player.getCameraCount(); i++ ) {
                    PCamera cam = player.getCamera( i );
                    if( cam.getComponent() instanceof PSwingCanvas ) {
                        updateCanvas( (PSwingCanvas)cam.getComponent() );
                        break;
                    }
                }
            }
            p = p.getParent();
        }
    }

    /**
     * Attach a listener to the specified node, if one has not already been attached.
     * @param node the node to listen to for parent/pcamera/pcanvas changes
     */
    private void listenToNode( PNode node ) {
//        System.out.println( "listeningTo.size() = " + listeningTo.size() );
        if( !listeningTo( node ) ) {
            listeningTo.add( node );
            node.addPropertyChangeListener( PNode.PROPERTY_PARENT, parentListener );
        }
    }

    /**
     * Determine whether this PSwing is already listening to the specified node for camera/canvas changes.
     * @param node the node to check
     * @return true if this PSwing is already listening to the specified node for camera/canvas changes
     */
    private boolean listeningTo( PNode node ) {
        for( int i = 0; i < listeningTo.size(); i++ ) {
            PNode pNode = (PNode)listeningTo.get( i );
            if( pNode == node ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes this PSwing from previous PSwingCanvas (if any), and ensure that this PSwing is attached to the new PSwingCanvas.
     * @param newCanvas the new PSwingCanvas (may be null)
     */
    private void updateCanvas( PSwingCanvas newCanvas ) {
        if( newCanvas != canvas ) {
            if( canvas != null ) {
                canvas.removePSwing( this );
            }
            if( newCanvas != null ) {
                canvas = newCanvas;
                canvas.addPSwing( this );
                reshape();
                repaint();
                canvas.invalidate();
                canvas.revalidate();
                canvas.repaint();
            }
        }
    }
    ////////////////////////////////////////////////////////////
    ///////End methods for automatic canvas detection
    ////////////////////////////////////////////////////////////
}
