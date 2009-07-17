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
package edu.umd.cs.piccolo.examples.pswing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import edu.umd.cs.piccolo.PCanvas;

import edu.umd.cs.piccolo.nodes.PText;

import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * Attempt to replicate the PSwingRepaintManager-related memory leak
 * reported in Issue 74.
 */
public final class PSwingMemoryLeakExample extends JFrame {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Active Instances Counter. */
    private final PText activeInstanceCounter;

    /** Garbage Collected Counter. */
    private final PText gcInstanceCounter;

    
    /** Memory. */
    private final PText freeMemory;

    /** Total memory. */
    private final PText totalMemory;

    /** Used memory. */
    private final PText usedMemory;

    /** Canvas. */
    private final PCanvas canvas;

    /** Main panel, container for PSwingCanvases. */
    private final JPanel mainPanel;


    /**
     * Create a new PSwing memory leak example.
     */
    public PSwingMemoryLeakExample() {
        super("PSwing memory leak example");

        PText label0 = new PText("Number of active PSwingCanvases:");
        activeInstanceCounter = new PText("0");
        PText label4 = new PText("Number of finalized PSwingCanvases:");
        gcInstanceCounter = new PText("0");
        PText label1 = new PText("Total memory:");
        totalMemory = new PText("0");
        PText label2 = new PText("Free memory:");
        freeMemory = new PText("0");
        PText label3 = new PText("Used memory:");
        usedMemory = new PText("0");

        label0.offset(20.0d, 20.0d);
        activeInstanceCounter.offset(label0.getFullBounds().getWidth() + 50.0d, 20.0d);
        label4.offset(20.0d, 40.0d);
        gcInstanceCounter.offset(label4.getFullBounds().getWidth() + 50.0d, 40.0d);
        label1.offset(20.0d, 60.0d);
        totalMemory.offset(label1.getFullBounds().getWidth() + 40.0d, 60.0d);
        label2.offset(20.0d, 80.0d);
        freeMemory.offset(label2.getFullBounds().getWidth() + 40.0d, 80.0d);
        label3.offset(freeMemory.getFullBounds().getX() + 80.0d, 80.0d);
        usedMemory.offset(label3.getFullBounds().getX() + label3.getFullBounds().getWidth() + 20.0d, 80.0d);

        canvas = new PCanvas();
        canvas.getCamera().addChild(label0);
        canvas.getCamera().addChild(activeInstanceCounter);
        canvas.getCamera().addChild(label4);
        canvas.getCamera().addChild(gcInstanceCounter);
        canvas.getCamera().addChild(label1);
        canvas.getCamera().addChild(totalMemory);
        canvas.getCamera().addChild(label2);
        canvas.getCamera().addChild(freeMemory);
        canvas.getCamera().addChild(label3);
        canvas.getCamera().addChild(usedMemory);
        canvas.setPreferredSize(new Dimension(400, 100));

        mainPanel = new JPanel();
        mainPanel.setPreferredSize(new Dimension(400, 300));
        mainPanel.setLayout(new FlowLayout());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add("North", canvas);
        getContentPane().add("Center", mainPanel);

        final Timer add = new Timer(10, new ActionListener()
        {
            int id = 0;

            /** {@inheritDoc} */
            public void actionPerformed(final ActionEvent e) {
                JLabel label = new JLabel("Label" + id);
                PSwing pswing = new PSwing(label);
                PSwingCanvas pswingCanvas = new PSwingCanvas() {
                    public void finalize() {
                        gcInstanceCounter.setText(String.valueOf(Integer.parseInt(gcInstanceCounter.getText())+1));
                    }
                };
                pswingCanvas.getLayer().addChild(pswing);
                pswingCanvas.setPreferredSize(new Dimension(60, 18));
                mainPanel.add(pswingCanvas);
                mainPanel.invalidate();
                mainPanel.validate();
                mainPanel.repaint();

                id++;
                incrementCounter();
            }
        });
        add.setDelay(5);
        add.setRepeats(true);

        final Timer remove = new Timer(20000, new ActionListener()
        {
            /** {@inheritDoc} */
            public void actionPerformed(final ActionEvent e) {
                if (add.isRunning()) {
                    add.stop();
                }
                int i = mainPanel.getComponentCount() - 1;
                if (i > 0) {
                    mainPanel.remove(mainPanel.getComponentCount() - 1);
                    mainPanel.invalidate();
                    mainPanel.validate();
                    mainPanel.repaint();
                    decrementCounter();
                    
                    System.gc();
                    System.runFinalization();
                }
            }
        });
        remove.setDelay(5);
        remove.setRepeats(true);

        final Timer updateMemory = new Timer(500, new ActionListener()
        {
            /** {@inheritDoc} */
            public void actionPerformed(final ActionEvent e) {
                updateMemory();
            }
        });
        updateMemory.setDelay(2000);
        updateMemory.setRepeats(true);

        add.start();
        remove.start();
        updateMemory.start();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 400);
        setVisible(true);
    }

    /**
     * Increment counter.
     */
    private void incrementCounter() {
        int count = Integer.parseInt(activeInstanceCounter.getText());
        count++;
        activeInstanceCounter.setText(String.valueOf(count));
    }

    /**
     * Decrement counter.
     */
    private void decrementCounter() {
        int count = Integer.parseInt(activeInstanceCounter.getText());
        count--;
        activeInstanceCounter.setText(String.valueOf(count));
    }

    /**
     * Update memory.
     */
    private void updateMemory() {
        System.gc();
        long total = Runtime.getRuntime().totalMemory();
        totalMemory.setText(String.valueOf(total));
        long free = Runtime.getRuntime().freeMemory();
        freeMemory.setText(String.valueOf(free));
        long used = (total - free);
        usedMemory.setText(String.valueOf(used));
    }


    /**
     * Main.
     *
     * @param args command line arguments, ignored
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            /** {@inheritDoc} */
            public void run() {
                new PSwingMemoryLeakExample();
            }
        });
    }
}
