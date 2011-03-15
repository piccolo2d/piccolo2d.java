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
package org.piccolo2d.extras.util;

import java.awt.geom.Rectangle2D;

import javax.swing.SwingConstants;

import org.piccolo2d.PNode;


/**
 * <b>PBoundsLocator</b> is a locator that locates points on the bounds of a
 * node.
 * <P>
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PBoundsLocator extends PNodeLocator {
    private static final long serialVersionUID = 1L;
    private int side;

    /**
     * Creates a locator for tracking the east side of the provided node.
     * 
     * @param node node to track
     * @return a new locator
     */
    public static PBoundsLocator createEastLocator(final PNode node) {
        return new PBoundsLocator(node, SwingConstants.EAST);
    }

    /**
     * Creates a locator for tracking the north east corner of the provided
     * node.
     * 
     * @param node node to track
     * @return a new locator
     */
    public static PBoundsLocator createNorthEastLocator(final PNode node) {
        return new PBoundsLocator(node, SwingConstants.NORTH_EAST);
    }

    /**
     * Creates a locator for tracking the north west corner of the provided
     * node.
     * 
     * @param node node to track
     * @return a new locator
     */
    public static PBoundsLocator createNorthWestLocator(final PNode node) {
        return new PBoundsLocator(node, SwingConstants.NORTH_WEST);
    }

    /**
     * Creates a locator for tracking the north side of the provided node.
     * 
     * @param node node to track
     * @return a new locator
     */
    public static PBoundsLocator createNorthLocator(final PNode node) {
        return new PBoundsLocator(node, SwingConstants.NORTH);
    }

    /**
     * Creates a locator for tracking the south side of the provided node.
     * 
     * @param node node to track
     * @return a new locator
     */
    public static PBoundsLocator createSouthLocator(final PNode node) {
        return new PBoundsLocator(node, SwingConstants.SOUTH);
    }

    /**
     * Creates a locator for tracking the west side of the provided node.
     * 
     * @param node node to track
     * @return a new locator
     */
    public static PBoundsLocator createWestLocator(final PNode node) {
        return new PBoundsLocator(node, SwingConstants.WEST);
    }

    /**
     * Creates a locator for tracking the south west corner of the provided
     * node.
     * 
     * @param node node to track
     * @return a new locator
     */
    public static PBoundsLocator createSouthWestLocator(final PNode node) {
        return new PBoundsLocator(node, SwingConstants.SOUTH_WEST);
    }

    /**
     * Creates a locator for tracking the south east corner of the provided
     * node.
     * 
     * @param node node to track
     * @return a new locator
     */
    public static PBoundsLocator createSouthEastLocator(final PNode node) {
        return new PBoundsLocator(node, SwingConstants.SOUTH_EAST);
    }

    /**
     * Constructs a locator for tracking the position on the node provided.
     * 
     * @param node node to track
     * @param aSide specified the position on the node to track
     */
    public PBoundsLocator(final PNode node, final int aSide) {
        super(node);
        side = aSide;
    }

    /**
     * Returns the side of the node that's being tracked.
     * 
     * @return tracked side
     */
    public int getSide() {
        return side;
    }

    /**
     * Sets the side to track on the node.
     * 
     * @param side new side to track
     */
    public void setSide(final int side) {
        this.side = side;
    }

    /**
     * Maps the locator's side to its x position.
     * 
     * @return x position on side this locator is tracking
     */
    public double locateX() {
        final Rectangle2D aBounds = node.getBoundsReference();

        switch (side) {
            case SwingConstants.NORTH_WEST:
            case SwingConstants.SOUTH_WEST:
            case SwingConstants.WEST:
                return aBounds.getX();

            case SwingConstants.NORTH_EAST:
            case SwingConstants.SOUTH_EAST:
            case SwingConstants.EAST:
                return aBounds.getX() + aBounds.getWidth();

            case SwingConstants.NORTH:
            case SwingConstants.SOUTH:
                return aBounds.getX() + aBounds.getWidth() / 2;
            default:
                return -1;
        }
    }

    /**
     * Maps the locator's side to its y position.
     * 
     * @return y position on side this locator is tracking
     */
    public double locateY() {
        final Rectangle2D aBounds = node.getBoundsReference();

        switch (side) {
            case SwingConstants.EAST:
            case SwingConstants.WEST:
                return aBounds.getY() + aBounds.getHeight() / 2;

            case SwingConstants.SOUTH:
            case SwingConstants.SOUTH_WEST:
            case SwingConstants.SOUTH_EAST:
                return aBounds.getY() + aBounds.getHeight();

            case SwingConstants.NORTH_WEST:
            case SwingConstants.NORTH_EAST:
            case SwingConstants.NORTH:
                return aBounds.getY();
            default:
                return -1;
        }
    }
}
