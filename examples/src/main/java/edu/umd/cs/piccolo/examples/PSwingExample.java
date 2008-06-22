package edu.umd.cs.piccolo.examples;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Demonstrates the use of PSwing in a Piccolo application.
 */

public class PSwingExample extends PFrame {

    public PSwingExample() {
        this( new PSwingCanvas() );
    }

    public PSwingExample( PCanvas aCanvas ) {
        super( "PSwingExample", false, aCanvas );
    }

    public void initialize() {
        PSwingCanvas pswingCanvas = (PSwingCanvas)getCanvas();
        PLayer l = pswingCanvas.getLayer();

        JSlider js = new JSlider( 0, 100 );
        js.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                System.out.println( "e = " + e );
            }
        } );
        js.setBorder( BorderFactory.createTitledBorder( "Test JSlider" ) );
        PSwing pSwing = new PSwing( js );
        pSwing.translate( 100, 100 );
        l.addChild( pSwing );

        pswingCanvas.setPanEventHandler( null );
    }

    public static void main( String[] args ) {
        new PSwingExample();
    }
}
