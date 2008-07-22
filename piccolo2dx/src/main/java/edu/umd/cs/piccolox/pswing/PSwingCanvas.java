/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author: samreid $
 * Revision : $Revision: 13998 $
 * Date modified : $Date: 2007-03-22 17:51:34 -0600 (Thu, 22 Mar 2007) $
 */
package edu.umd.cs.piccolox.pswing;

import edu.umd.cs.piccolo.PCanvas;

import javax.swing.*;
import java.awt.*;

/**
 * The <b>PSwingCanvas</b> is a PCanvas that can display Swing components with the PSwing adapter.
 *
 * @author Benjamin B. Bederson
 * @author Sam R. Reid
 * @author Lance E. Good
 */

public class PSwingCanvas extends PCanvas {
    public static final String SWING_WRAPPER_KEY = "Swing Wrapper";
    private static PSwingRepaintManager pSwingRepaintManager = new PSwingRepaintManager();

    private SwingWrapper swingWrapper;
    private PSwingEventHandler swingEventHandler;

    /**
     * Construct a new PSwingCanvas.
     */
    public PSwingCanvas() {
        swingWrapper = new SwingWrapper( this );
        add( swingWrapper );
        RepaintManager.setCurrentManager( pSwingRepaintManager );
        pSwingRepaintManager.addPSwingCanvas( this );

        swingEventHandler = new PSwingEventHandler( this, getCamera() );//todo or maybe getCameraLayer() or getRoot()?
        swingEventHandler.setActive( true );
    }

    JComponent getSwingWrapper() {
        return swingWrapper;
    }

    public void addPSwing( PSwing pSwing ) {
        swingWrapper.add( pSwing.getComponent() );
    }

    public void removePSwing( PSwing pSwing ) {
        swingWrapper.remove( pSwing.getComponent() );
    }

    private static class SwingWrapper extends JComponent {
        private PSwingCanvas pSwingCanvas;

        public SwingWrapper( PSwingCanvas pSwingCanvas ) {
            this.pSwingCanvas = pSwingCanvas;
            setSize( new Dimension( 0, 0 ) );
            setPreferredSize( new Dimension( 0, 0 ) );
            putClientProperty( SWING_WRAPPER_KEY, SWING_WRAPPER_KEY );
        }

        public PSwingCanvas getpSwingCanvas() {
            return pSwingCanvas;
        }
    }

}