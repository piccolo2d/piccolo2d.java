package edu.umd.cs.piccolox.pswing.tests;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PComboBox;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 11, 2005
 * Time: 12:15:55 PM
 * Copyright (c) Jul 11, 2005 by Sam Reid
 */

public class TestPSwing {
    public static void main( String[] args ) {
        PSwingCanvas pCanvas = new PSwingCanvas();
        final PText pText = new PText( "PText" );
        pCanvas.getLayer().addChild( pText );
        JFrame frame = new JFrame( "Test Piccolo" );

        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( pCanvas );
        frame.setSize( 600, 800 );
        frame.setVisible( true );

        PText text2 = new PText( "Text2" );
        text2.setFont( new Font( "Lucida Sans", Font.BOLD, 18 ) );
        pCanvas.getLayer().addChild( text2 );
        text2.translate( 100, 100 );
        text2.addInputEventListener( new PZoomEventHandler() );

        pCanvas.removeInputEventListener( pCanvas.getPanEventHandler() );

        JButton jButton = new JButton( "MyButton!" );
        jButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "TestZSwing.actionPerformed!!!!!!!!!!!!!!*********************" );
            }
        } );
        final PSwing pSwing = new PSwing( pCanvas, jButton );
        pCanvas.getLayer().addChild( pSwing );
        pSwing.repaint();

        JSpinner jSpinner = new JSpinner();
        jSpinner.setPreferredSize( new Dimension( 100, jSpinner.getPreferredSize().height ) );
        PSwing pSpinner = new PSwing( pCanvas, jSpinner );
        pCanvas.getLayer().addChild( pSpinner );
        pSpinner.translate( 0, 150 );

        JCheckBox jcb = new JCheckBox( "CheckBox", true );
        jcb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "TestZSwing.JCheckBox.actionPerformed" );
            }
        } );
        jcb.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                System.out.println( "TestPSwing.JChekbox.stateChanged@" + System.currentTimeMillis() );
            }
        } );
        PSwing pCheckBox = new PSwing( pCanvas, jcb );
        pCanvas.getLayer().addChild( pCheckBox );
        pCheckBox.translate( 100, 0 );

        // Growable JTextArea
        JTextArea textArea = new JTextArea( "This is a growable TextArea.\nTry it out!" );
        textArea.setBorder( new LineBorder( Color.blue, 3 ) );
        PSwing swing = new PSwing( pCanvas, textArea );
        swing.translate( 150, 150 );
        pCanvas.getLayer().addChild( swing );

        // A Slider
        JSlider slider = new JSlider();
        PSwing pSlider = new PSwing( pCanvas, slider );
        pSlider.translate( 200, 200 );
        pCanvas.getLayer().addChild( pSlider );

        // A Scrollable JTree
        JTree tree = new JTree();
        tree.setEditable( true );
        JScrollPane p = new JScrollPane( tree );
        p.setPreferredSize( new Dimension( 150, 150 ) );
        PSwing pTree = new PSwing( pCanvas, p );
        pCanvas.getLayer().addChild( pTree );
        pTree.translate( 0, 250 );

        // A JColorChooser - also demonstrates JTabbedPane
        JColorChooser chooser = new JColorChooser();
        PSwing pChooser = new PSwing( pCanvas, chooser );
        pCanvas.getLayer().addChild( pChooser );
        pChooser.translate( 100, 300 );

        JPanel myPanel = new JPanel();
        myPanel.setBorder( BorderFactory.createTitledBorder( "Titled Border" ) );
        myPanel.add( new JCheckBox( "CheckBox" ) );
        PSwing panelSwing = new PSwing( pCanvas, myPanel );
        pCanvas.getLayer().addChild( panelSwing );
        panelSwing.translate( 400, 50 );

        // A Slider
        JSlider slider2 = new JSlider();
        PSwing pSlider2 = new PSwing( pCanvas, slider2 );
        pSlider2.translate( 200, 200 );
        PNode root = new PNode();
        root.addChild( pSlider2 );
        root.scale( 1.5 );
        root.rotate( Math.PI / 4 );
        root.translate( 300, 200 );
        pCanvas.getLayer().addChild( root );

        String[] listItems = {"Summer Teeth", "Mermaid Avenue", "Being There", "A.M."};
        PComboBox box = new PComboBox( listItems );
        swing = new PSwing( pCanvas, box );
        swing.translate( 200, 250 );
        pCanvas.getLayer().addChild( swing );
        box.setEnvironment( swing, pCanvas );//has to be done manually at present

        // Revalidate and repaint
        pCanvas.revalidate();
        pCanvas.repaint();
    }

}
