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
package edu.umd.cs.piccolox.activities;

import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.umd.cs.piccolo.activities.PInterpolatingActivity;

/**
 * <b>PPositionPathActivity</b> animates through a sequence of points.
 * 
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PPositionPathActivity extends PPathActivity {

    protected Point2D[] positions;
    protected Target target;

    public interface Target {
        public void setPosition(double x, double y);
    }

    public PPositionPathActivity(final long duration, final long stepRate, final Target aTarget) {
        this(duration, stepRate, aTarget, null, null);
    }

    public PPositionPathActivity(final long duration, final long stepRate, final Target aTarget, final float[] knots,
            final Point2D[] positions) {
        this(duration, stepRate, 1, PInterpolatingActivity.SOURCE_TO_DESTINATION, aTarget, knots, positions);
    }

    public PPositionPathActivity(final long duration, final long stepRate, final int loopCount, final int mode,
            final Target aTarget, final float[] knots, final Point2D[] positions) {
        super(duration, stepRate, loopCount, mode, knots);
        target = aTarget;        
        this.positions = (Point2D[])positions.clone();
    }

    protected boolean isAnimation() {
        return true;
    }

    public Point2D[] getPositions() {
        return (Point2D[])positions.clone();
    }

    public Point2D getPosition(final int index) {
        return positions[index];
    }

    public void setPositions(final Point2D[] positions) {
        this.positions = (Point2D[])positions.clone();
    }

    public void setPosition(final int index, final Point2D position) {
        positions[index] = position;
    }

    public void setPositions(final GeneralPath path) {
        final PathIterator pi = path.getPathIterator(null, 1);
        final ArrayList points = new ArrayList();
        final float point[] = new float[6];
        float distanceSum = 0;
        float lastMoveToX = 0;
        float lastMoveToY = 0;

        while (!pi.isDone()) {
            final int type = pi.currentSegment(point);

            switch (type) {
                case PathIterator.SEG_MOVETO:
                    points.add(new Point2D.Float(point[0], point[1]));
                    lastMoveToX = point[0];
                    lastMoveToY = point[1];
                    break;

                case PathIterator.SEG_LINETO:
                    points.add(new Point2D.Float(point[0], point[1]));
                    break;

                case PathIterator.SEG_CLOSE:
                    points.add(new Point2D.Float(lastMoveToX, lastMoveToY));
                    break;

                case PathIterator.SEG_QUADTO:
                case PathIterator.SEG_CUBICTO:
                    throw new RuntimeException();
            }

            if (points.size() > 1) {
                final Point2D last = (Point2D) points.get(points.size() - 2);
                final Point2D current = (Point2D) points.get(points.size() - 1);
                distanceSum += last.distance(current);
            }

            pi.next();
        }

        final int size = points.size();
        final Point2D newPositions[] = new Point2D[size];
        final float newKnots[] = new float[size];

        for (int i = 0; i < size; i++) {
            newPositions[i] = (Point2D) points.get(i);
            if (i > 0) {
                final float dist = (float) newPositions[i - 1].distance(newPositions[i]);
                newKnots[i] = newKnots[i - 1] + dist / distanceSum;
            }
        }

        setPositions(newPositions);
        setKnots(newKnots);
    }

    public void setRelativeTargetValue(final float zeroToOne, final int startKnot, final int endKnot) {
        final Point2D start = getPosition(startKnot);
        final Point2D end = getPosition(endKnot);
        target.setPosition(start.getX() + zeroToOne * (end.getX() - start.getX()), start.getY() + zeroToOne
                * (end.getY() - start.getY()));
    }
}
