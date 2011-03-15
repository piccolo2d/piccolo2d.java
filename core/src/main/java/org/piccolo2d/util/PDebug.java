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
package org.piccolo2d.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.SwingUtilities;

/**
 * <b>PDebug</b> is used to set framework wide debugging flags.
 * <P>
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PDebug {
    /** Set to true to display clip bounds boxes. */
    public static boolean debugRegionManagement = false;

    /**
     * Set to true if you want to display common errors with painting and
     * threading.
     */
    public static boolean debugPaintCalls = false;

    /** Set to true to display frame rate in the console. */
    public static boolean debugPrintFrameRate = false;

    /** Set to true to display used memory in console. */
    public static boolean debugPrintUsedMemory = false;

    /** Displays bounding boxes around nodes. Used in PCamera. */
    public static boolean debugBounds = false;

    /** Displays a tint to all shapes within a bounding box. */
    public static boolean debugFullBounds = false;

    /** Whether to complain whenever common threading issues occur. */
    public static boolean debugThreads = false;

    /** How often in frames result info should be printed to the console. */
    public static int printResultsFrameRate = 10;

    private static int debugPaintColor;
    private static long framesProcessed;
    private static long startProcessingOutputTime;
    private static long startProcessingInputTime;
    private static long processOutputTime;
    private static long processInputTime;
    private static boolean processingOutput;

    private PDebug() {
        super();
    }

    /**
     * Generates a color for use while debugging.
     * 
     * @return a color for use while debugging.
     */
    public static Color getDebugPaintColor() {
        final int color = 100 + debugPaintColor++ % 10 * 10;
        return new Color(color, color, color, 150);
    }

    /**
     * Checks that process inputs is being doing from the Swing Dispatch Thread.
     */
    public static void scheduleProcessInputs() {
        if (debugThreads && !SwingUtilities.isEventDispatchThread()) {
            System.out.println("scene graph manipulated on wrong thread");
        }
    }

    /**
     * Ensures that painting is not invalidating paint regions and that it's
     * being called from the dispatch thread.
     */
    public static void processRepaint() {
        if (processingOutput && debugPaintCalls) {
            System.err
                    .println("Got repaint while painting scene. This can result in a recursive process that degrades performance.");
        }

        if (debugThreads && !SwingUtilities.isEventDispatchThread()) {
            System.out.println("repaint called on wrong thread");
        }
    }

    /**
     * Returns whether output is being processed.
     * 
     * @return whether output is being processed
     */
    public static boolean getProcessingOutput() {
        return processingOutput;
    }

    /**
     * Records that processing of ouptut has begun.
     */
    public static void startProcessingOutput() {
        processingOutput = true;
        startProcessingOutputTime = System.currentTimeMillis();
    }

    /**
     * Flags processing of output as finished. Updates all stats in the process.
     * 
     * @param g graphics context in which processing has finished
     */
    public static void endProcessingOutput(final Graphics g) {
        processOutputTime += System.currentTimeMillis() - startProcessingOutputTime;
        framesProcessed++;

        if (framesProcessed % printResultsFrameRate == 0) {
            if (PDebug.debugPrintFrameRate) {
                System.out.println("Process output frame rate: " + getOutputFPS() + " fps");
                System.out.println("Process input frame rate: " + getInputFPS() + " fps");
                System.out.println("Total frame rate: " + getTotalFPS() + " fps");
                System.out.println();
                resetFPSTiming();
            }

            if (PDebug.debugPrintUsedMemory) {
                System.out.println("Approximate used memory: " + getApproximateUsedMemory() / 1024 + " k");
            }
        }

        if (PDebug.debugRegionManagement) {
            final Graphics2D g2 = (Graphics2D) g;
            g2.setColor(PDebug.getDebugPaintColor());
            g2.fill(g.getClipBounds().getBounds2D());
        }

        processingOutput = false;
    }

    /**
     * Records that processing of input has started.
     */
    public static void startProcessingInput() {
        startProcessingInputTime = System.currentTimeMillis();
    }

    /**
     * Records that processing of input has finished.
     */
    public static void endProcessingInput() {
        processInputTime += System.currentTimeMillis() - startProcessingInputTime;
    }

    /**
     * Return how many frames are processed and painted per second. Note that
     * since piccolo doesn't paint continuously this rate will be slow unless
     * you are interacting with the system or have activities scheduled.
     * 
     * @return frame rate achieved
     */
    public static double getTotalFPS() {
        if (framesProcessed > 0) {
            return 1000.0 / ((processInputTime + processOutputTime) / (double) framesProcessed);
        }
        else {
            return 0;
        }
    }

    /**
     * Return the frames per second used to process input events and activities.
     * 
     * @return # of frames per second that were allocated to processing input
     */
    public static double getInputFPS() {
        if (processInputTime > 0 && framesProcessed > 0) {
            return 1000.0 / (processInputTime / (double) framesProcessed);
        }
        else {
            return 0;
        }
    }

    /**
     * Return the frames per seconds used to paint graphics to the screen.
     * 
     * @return # of frames per second that were used up to processing output
     */
    public static double getOutputFPS() {
        if (processOutputTime > 0 && framesProcessed > 0) {
            return 1000.0 / (processOutputTime / (double) framesProcessed);
        }
        else {
            return 0;
        }
    }

    /**
     * Return the number of frames that have been processed since the last time
     * resetFPSTiming was called.
     * 
     * @return total number of frames processed
     */
    public long getFramesProcessed() {
        return framesProcessed;
    }

    /**
     * Reset the variables used to track FPS. If you reset seldom they you will
     * get good average FPS values, if you reset more often only the frames
     * recorded after the last reset will be taken into consideration.
     */
    public static void resetFPSTiming() {
        framesProcessed = 0;
        processInputTime = 0;
        processOutputTime = 0;
    }

    /**
     * Returns an approximation of the amount of memory that is being used.
     * 
     * Not that this call might affecting timings.
     * 
     * @return approximate # of bytes of memory used
     */
    public static long getApproximateUsedMemory() {
        System.gc();
        System.runFinalization();
        final long totalMemory = Runtime.getRuntime().totalMemory();
        final long free = Runtime.getRuntime().freeMemory();
        return totalMemory - free;
    }
}
