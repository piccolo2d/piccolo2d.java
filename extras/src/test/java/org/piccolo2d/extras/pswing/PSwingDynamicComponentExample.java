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
package org.piccolo2d.extras.pswing;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

/**
 * Demonstrates a PSwing problem with dynamic JComponents.
 * <p>
 * This example shows 2 identical JPanels.
 * The panel on the left uses PSwing.
 * The panel on the right uses pure Swing.
 * <p>
 * The JPanel contain various JComponents whose text can be updated by 
 * typing into JTextFields and pressing the "Update" button.
 * The JPanel managed by PSwing is often rendered incorrectly.
 * <p>
 * Please see piccolo2d issue 163 for more information about this problem and solution:
 * http://code.google.com/p/piccolo2d/issues/detail?id=163
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author Sam Reid
 */
public class PSwingDynamicComponentExample extends JFrame {
    
    private static final Dimension FRAME_SIZE = new Dimension( 800, 400 );
    private static final int TEXT_FIELD_COLUMNS = 30;
    
    private final ComponentPanel swingPanel, piccoloPanel;
    private final JTextField labelTextField, checkBoxTextField, radioButtonTextField;
    
    public PSwingDynamicComponentExample() {
        super( PSwingDynamicComponentExample.class.getName() );
        setSize( FRAME_SIZE );
        
        // canvas
        PSwingCanvas canvas = new PSwingCanvas();
        canvas.setBackground( Color.RED );
        canvas.removeInputEventListener( canvas.getZoomEventHandler() );
        canvas.removeInputEventListener( canvas.getPanEventHandler() );
        
        // panel that we'll display using Piccolo
        piccoloPanel = new ComponentPanel();
        final PSwing pswing = new PSwing( piccoloPanel );
        canvas.getLayer().addChild( pswing );
        pswing.setOffset( 10, 10 );
        
        // panel that we're display using pure Swing
        swingPanel = new ComponentPanel();
        JPanel jpanel = new JPanel();
        jpanel.setBorder( new LineBorder( Color.BLACK ) );
        jpanel.add( swingPanel );
        
        // text fields, for specifying dynamic text
        labelTextField = new JTextField( swingPanel.label.getText(), TEXT_FIELD_COLUMNS );
        checkBoxTextField = new JTextField( swingPanel.checkBox.getText(), TEXT_FIELD_COLUMNS );
        radioButtonTextField = new JTextField( swingPanel.radioButton.getText(), TEXT_FIELD_COLUMNS );
        
        // Update button, for applying dynamic text
        JButton updateButton = new JButton( "Update" );
        updateButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updatePanels();
            }
        } );
        
        // 
        JButton addComponentButton = new JButton( "add component" );
        addComponentButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                piccoloPanel.addComponent( new JLabel( "new" ) );
                swingPanel.addComponent( new JLabel( "new" ) );
            }
            
        });
        
        // control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder( new LineBorder( Color.BLACK ) );
        controlPanel.setLayout( new GridBagLayout() );
        GridBagConstraints c = new GridBagConstraints();
        // JLabel
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.EAST;
        controlPanel.add( new JLabel( "JLabel text:" ), c );
        c.gridx++;
        c.anchor = GridBagConstraints.WEST;
        controlPanel.add( labelTextField, c );
        // JCheckBox
        c.gridx = 0;
        c.gridy++;
        c.anchor = GridBagConstraints.EAST;
        controlPanel.add( new JLabel( "JCheckBox text:" ), c );
        c.gridx++;
        c.anchor = GridBagConstraints.WEST;
        controlPanel.add( checkBoxTextField, c );
        // JRadioButton
        c.gridx = 0;
        c.gridy++;
        c.anchor = GridBagConstraints.EAST;
        controlPanel.add( new JLabel( "JRadioButton text:" ), c );
        c.gridx++;
        c.anchor = GridBagConstraints.WEST;
        controlPanel.add( radioButtonTextField, c );
        // Update button
        c.gridx = 1;
        c.gridy++;
        c.anchor = GridBagConstraints.WEST;
        controlPanel.add( updateButton, c );
        // Add component buttons
        c.gridx = 1;
        c.gridy++;
        c.anchor = GridBagConstraints.WEST;
        controlPanel.add( addComponentButton, c );
        
       
        
        // main panel
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( canvas, BorderLayout.CENTER );
        mainPanel.add( jpanel, BorderLayout.EAST );
        mainPanel.add( controlPanel, BorderLayout.SOUTH );
        setContentPane( mainPanel );
    }
    
    // applies the text field values to the components in the panels
    private void updatePanels() {
        
        // Piccolo (PSwing) panel
        piccoloPanel.label.setText( labelTextField.getText() );
        piccoloPanel.checkBox.setText( checkBoxTextField.getText() );
        piccoloPanel.radioButton.setText( radioButtonTextField.getText() );
        
        // Swing panel
        swingPanel.label.setText( labelTextField.getText() );
        swingPanel.checkBox.setText( checkBoxTextField.getText() );
        swingPanel.radioButton.setText( radioButtonTextField.getText() );
    }
    
    // A panel with a few different types of JComponent.
    private static class ComponentPanel extends JPanel {
        
        // allow public access to keep our example code short
        public final JLabel label;
        public final JCheckBox checkBox;
        public final JRadioButton radioButton;
        public final GridBagConstraints constraints;

        public ComponentPanel() {
            setBorder( new CompoundBorder( new LineBorder( Color.BLACK, 1 ), new EmptyBorder( 5, 14, 5, 14 ) ) );
            setBackground( new Color( 180, 205, 255 ) );
            
            // components
            label = new JLabel( "JLabel" );
            checkBox = new JCheckBox( "JCheckBox" );
            radioButton = new JRadioButton( "JRadioButton" );
            
            // layout
            setLayout( new GridBagLayout() );
            constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.WEST;
            constraints.gridx = 0;
            constraints.gridy = GridBagConstraints.RELATIVE;
            addComponent( label );
            addComponent( checkBox );
            addComponent( radioButton );
        }
        
        public void addComponent( JComponent c ) {
            add( c, constraints );
            revalidate();
        }
    }
    
    public static class SleepThread extends Thread {

        public SleepThread( long millis ) {
            super( new Runnable() {
                public void run() {
                    while ( true ) {
                        try {
                            SwingUtilities.invokeAndWait( new Runnable() {
                                public void run() {
                                    try {
                                        Thread.sleep( 1000 );
                                    }
                                    catch ( InterruptedException e ) {
                                        e.printStackTrace();
                                    }
                                }
                            } );
                        }
                        catch ( InterruptedException e ) {
                            e.printStackTrace();
                        }
                        catch ( InvocationTargetException e ) {
                            e.printStackTrace();
                        }
                    }
                }
            } );
        }
    }

    public static void main( String[] args ) {
        // This thread serves to make the problem more noticeable.
//        new SleepThread( 1000 ).start();
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                JFrame frame = new PSwingDynamicComponentExample();
                frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
                frame.setVisible( true );
            }
        } );
    }
}
