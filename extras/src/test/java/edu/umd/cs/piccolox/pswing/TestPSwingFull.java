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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.TableColumn;

import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid Date: Jul 11, 2005 Time: 12:15:55 PM
 */

public class TestPSwingFull extends JFrame {
    public TestPSwingFull() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ClassLoader loader;
        PSwingCanvas canvas;

        // Set up basic frame
        setBounds(50, 50, 750, 750);
        setResizable(true);
        setBackground(null);
        setVisible(true);
        canvas = new PSwingCanvas();
        canvas.setPanEventHandler(null);
        getContentPane().add(canvas);
        validate();
        loader = getClass().getClassLoader();

        ZVisualLeaf leaf;
        PNode transform;
        PSwing swing;
        PSwing swing2;

        // JButton
        JButton button = new JButton("Button");
        button.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        swing = new PSwing(button);
        leaf = new ZVisualLeaf(swing);
        transform = new PNode();
        transform.translate(-500, -500);
        transform.addChild(leaf);
        canvas.getLayer().addChild(transform);

        // JButton
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        spinner.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        swing = new PSwing(spinner);
        leaf = new ZVisualLeaf(swing);
        transform = new PNode();
        transform.translate(-800, -500);
        transform.addChild(leaf);
        canvas.getLayer().addChild(transform);

        // 2nd Copy of JButton
        leaf = new ZVisualLeaf(swing);
        transform = new PNode();
        transform.translate(-450, -450);
        transform.rotate(Math.PI / 2);
        transform.scale(0.5);
        transform.addChild(leaf);
        canvas.getLayer().addChild(transform);

        // Growable JTextArea
        JTextArea textArea = new JTextArea("This is a growable TextArea.\nTry it out!");
        textArea.setBorder(new LineBorder(Color.blue, 3));
        swing = new PSwing(textArea);
        leaf = new ZVisualLeaf(swing);
        transform = new PNode();
        transform.translate(-250, -500);
        transform.addChild(leaf);
        canvas.getLayer().addChild(transform);

        // Growable JTextField
        JTextField textField = new JTextField("A growable text field");
        swing = new PSwing(textField);
        leaf = new ZVisualLeaf(swing);
        transform = new PNode();
        transform.translate(0, -500);
        transform.addChild(leaf);
        canvas.getLayer().addChild(transform);

        // A Slider
        JSlider slider = new JSlider();
        swing = new PSwing(slider);
        leaf = new ZVisualLeaf(swing);
        transform = new PNode();
        transform.translate(250, -500);
        transform.addChild(leaf);
        canvas.getLayer().addChild(transform);

        // A Scrollable JTree
        JTree tree = new JTree();
        tree.setEditable(true);
        JScrollPane p = new JScrollPane(tree);
        p.setPreferredSize(new Dimension(150, 150));
        swing = new PSwing(p);
        leaf = new ZVisualLeaf(swing);
        transform = new PNode();
        transform.translate(-500, -250);
        transform.addChild(leaf);
        canvas.getLayer().addChild(transform);

        // A Scrollable JTextArea
        JScrollPane pane = new JScrollPane(new JTextArea("A Scrollable Text Area\nTry it out!"));
        pane.setPreferredSize(new Dimension(150, 150));
        swing = new PSwing(pane);
        leaf = new ZVisualLeaf(swing);
        transform = new PNode();
        transform.translate(-250, -250);
        transform.addChild(leaf);
        canvas.getLayer().addChild(transform);
        swing2 = swing;

        // A non-scrollable JTextField
        // A panel MUST be created with double buffering off
        JPanel panel = new JPanel(false);
        textField = new JTextField("A fixed-size text field");
        panel.setLayout(new BorderLayout());
        panel.add(textField);
        swing = new PSwing(panel);
        leaf = new ZVisualLeaf(swing);
        transform = new PNode();
        transform.translate(0, -250);
        transform.addChild(leaf);
        canvas.getLayer().addChild(transform);

        // // A JComboBox
        // String[] listItems = {"Summer Teeth", "Mermaid Avenue",
        // "Being There", "A.M."};
        // ZComboBox box = new ZComboBox( listItems );
        // swing = new PSwing( canvas, box );
        // leaf = new ZVisualLeaf( swing );
        // transform = new PNode();
        // transform.translate( 0, -150 );
        // transform.addChild( leaf );
        // canvas.getLayer().addChild( transform );

        // A panel with TitledBorder and JList
        panel = new JPanel(false);
        panel.setBackground(Color.lightGray);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED), "A JList", TitledBorder.LEFT,
                TitledBorder.TOP));
        panel.setPreferredSize(new Dimension(200, 200));
        Vector data = new Vector();
        data.addElement("Choice 1");
        data.addElement("Choice 2");
        data.addElement("Choice 3");
        data.addElement("Choice 4");
        data.addElement("Choice 5");
        JList list = new JList(data);
        list.setBackground(Color.lightGray);
        panel.add(list);
        swing = new PSwing(panel);
        leaf = new ZVisualLeaf(swing);
        transform = new PNode();
        transform.translate(250, -250);
        transform.addChild(leaf);
        canvas.getLayer().addChild(transform);

        // A JLabel
        JLabel label = new JLabel("A JLabel", SwingConstants.CENTER);

        swing = new PSwing(label);
        leaf = new ZVisualLeaf(swing);
        transform = new PNode();
        transform.translate(-500, 0);
        transform.addChild(leaf);
        canvas.getLayer().addChild(transform);

        // Rotated copy of the Scrollable JTextArea
        leaf = new ZVisualLeaf(swing2);
        transform = new PNode();
        transform.translate(-100, 0);
        transform.rotate(Math.PI / 2);
        transform.addChild(leaf);
        canvas.getLayer().addChild(transform);

        // A panel with layout
        // A panel MUST be created with double buffering off
        panel = new JPanel(false);
        panel.setLayout(new BorderLayout());
        JButton button1 = new JButton("Button 1");
        JButton button2 = new JButton("Button 2");
        label = new JLabel("A Panel with Layout");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setForeground(Color.white);
        panel.setBackground(Color.red);
        panel.setPreferredSize(new Dimension(150, 150));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(button1, "North");
        panel.add(button2, "South");
        panel.add(label, "Center");
        panel.revalidate();
        swing = new PSwing(panel);
        leaf = new ZVisualLeaf(swing);
        transform = new PNode();
        transform.translate(0, 0);
        transform.addChild(leaf);
        canvas.getLayer().addChild(transform);

        // JTable Example
        Vector columns = new Vector();
        columns.addElement("Check Number");
        columns.addElement("Description");
        columns.addElement("Amount");
        Vector rows = new Vector();
        Vector row = new Vector();
        row.addElement("101");
        row.addElement("Sandwich");
        row.addElement("$20.00");
        rows.addElement(row);
        row = new Vector();
        row.addElement("102");
        row.addElement("Monkey Wrench");
        row.addElement("$100.00");
        rows.addElement(row);
        row = new Vector();
        row.addElement("214");
        row.addElement("Ant farm");
        row.addElement("$55.00");
        rows.addElement(row);
        row = new Vector();
        row.addElement("215");
        row.addElement("Self-esteem tapes");
        row.addElement("$37.99");
        rows.addElement(row);
        row = new Vector();
        row.addElement("216");
        row.addElement("Tube Socks");
        row.addElement("$7.45");
        rows.addElement(row);
        row = new Vector();
        row.addElement("220");
        row.addElement("Ab Excerciser");
        row.addElement("$56.95");
        rows.addElement(row);
        row = new Vector();
        row.addElement("319");
        row.addElement("Y2K Supplies");
        row.addElement("$4624.33");
        rows.addElement(row);
        row = new Vector();
        row.addElement("332");
        row.addElement("Tie Rack");
        row.addElement("$15.20");
        rows.addElement(row);
        row = new Vector();
        row.addElement("344");
        row.addElement("Swing Set");
        row.addElement("$146.59");
        rows.addElement(row);
        JTable table = new JTable(rows, columns);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(30);
        TableColumn c = table.getColumn(table.getColumnName(0));
        c.setPreferredWidth(150);
        c = table.getColumn(table.getColumnName(1));
        c.setPreferredWidth(150);
        c = table.getColumn(table.getColumnName(2));
        c.setPreferredWidth(150);
        pane = new JScrollPane(table);
        pane.setPreferredSize(new Dimension(200, 200));
        table.setDoubleBuffered(false);
        swing = new PSwing(pane);
        leaf = new ZVisualLeaf(swing);
        transform = new PNode();
        transform.translate(250, 0);
        transform.addChild(leaf);
        canvas.getLayer().addChild(transform);

        // JEditorPane - HTML example
        try {

            final JEditorPane editorPane = new JEditorPane(loader.getResource("csdept.html"));
            editorPane.setDoubleBuffered(false);
            editorPane.setEditable(false);
            pane = new JScrollPane(editorPane);
            pane.setDoubleBuffered(false);
            pane.setPreferredSize(new Dimension(400, 400));
            editorPane.addHyperlinkListener(new HyperlinkListener() {
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        try {
                            editorPane.setPage(e.getURL());
                        }
                        catch (IOException ioe) {
                            System.out.println("Couldn't Load Web Page");
                        }
                    }
                }
            });
            swing = new PSwing(pane);
            leaf = new ZVisualLeaf(swing);
            transform = new PNode();
            transform.translate(-500, 250);
            transform.addChild(leaf);
            canvas.getLayer().addChild(transform);

        }
        catch (IOException ioe) {
            System.out.println("Couldn't Load Web Page");
        }

        // A JInternalFrame with a JSplitPane - a JOptionPane - and a
        // JToolBar
        JInternalFrame iframe = new JInternalFrame("JInternalFrame");
        iframe.getRootPane().setDoubleBuffered(false);
        ((JComponent) iframe.getContentPane()).setDoubleBuffered(false);
        iframe.setPreferredSize(new Dimension(500, 500));
        JTabbedPane tabby = new JTabbedPane();
        tabby.setDoubleBuffered(false);
        iframe.getContentPane().setLayout(new BorderLayout());
        JOptionPane options = new JOptionPane("This is a JOptionPane!", JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION);
        options.setDoubleBuffered(false);
        options.setMinimumSize(new Dimension(50, 50));
        options.setPreferredSize(new Dimension(225, 225));
        JPanel tools = new JPanel(false);
        tools.setMinimumSize(new Dimension(150, 150));
        tools.setPreferredSize(new Dimension(225, 225));
        JToolBar bar = new JToolBar();
        Action letter = new AbstractAction("Big A!") {

            public void actionPerformed(ActionEvent e) {
            }
        };

        Action hand = new AbstractAction("Hi!") {
            public void actionPerformed(ActionEvent e) {
            }
        };
        Action select = new AbstractAction("There!") {
            public void actionPerformed(ActionEvent e) {
            }
        };

        label = new JLabel("A Panel with a JToolBar");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        bar.add(letter);
        bar.add(hand);
        bar.add(select);
        bar.setFloatable(false);
        bar.setBorder(new LineBorder(Color.black, 2));
        tools.setLayout(new BorderLayout());
        tools.add(bar, "North");
        tools.add(label, "Center");

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, options, tools);
        split.setDoubleBuffered(false);
        iframe.getContentPane().add(split);
        swing = new PSwing(iframe);
        leaf = new ZVisualLeaf(swing);
        transform = new PNode();
        transform.translate(0, 250);
        transform.addChild(leaf);
        canvas.getLayer().addChild(transform);

        // JMenuBar menuBar = new JMenuBar();
        // ZMenu menu = new ZMenu( "File" );
        // ZMenu sub = new ZMenu( "Export" );
        // JMenuItem gif = new JMenuItem( "Funds" );
        // sub.add( gif );
        // menu.add( sub );
        // menuBar.add( menu );
        // iframe.setJMenuBar( menuBar );

        iframe.setVisible(true);

        // A JColorChooser - also demonstrates JTabbedPane
        // JColorChooser chooser = new JColorChooser();
        JCheckBox chooser = new JCheckBox("Check Box");
        swing = new PSwing(chooser);
        leaf = new ZVisualLeaf(swing);
        transform = new PNode();
        transform.translate(-250, 850);
        transform.addChild(leaf);
        canvas.getLayer().addChild(transform);

        // Revalidate and repaint
        canvas.revalidate();
        canvas.repaint();

        PSwing message = new PSwing(new JTextArea("Click-drag to zoom in and out."));
        message.translate(0, -50);
        canvas.getLayer().addChild(message);

        canvas.getCamera().animateViewToCenterBounds(message.getFullBounds(), false, 1200);
    }

    public static void main(String[] args) {
        new TestPSwingFull().setVisible(true);
    }

    public static class ZVisualLeaf extends PNode {
        public ZVisualLeaf(PNode node) {
            addChild(node);
        }
    }

}
