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

import org.piccolo2d.PNode;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PPickPath;

/**
 * Experimental class for detecting occlusions.
 * 
 * @author Jesse Grosjean
 */
public class POcclusionDetection {

    /**
     * Traverse from the bottom right of the scene graph (top visible node) up
     * the tree determining which parent nodes are occluded by their children
     * nodes. Note that this is only detecting a subset of occlusions (parent,
     * child), others such as overlapping siblings or cousins are not detected.
     * 
     * @param n node from which to detect occlusions
     * @param parentBounds bounds of parent node
     */
    public void detectOccusions(final PNode n, final PBounds parentBounds) {
        detectOcclusions(n, new PPickPath(null, parentBounds));
    }

    /**
     * Traverse the pick path determining which parent nodes are occluded by
     * their children nodes. Note that this is only detecting a subset of
     * occlusions (parent, child), others such as overlapping siblings or
     * cousins are not detected.
     * 
     * @param node node from which to detect occlusions
     * @param pickPath Pick Path to traverse
     */
    public void detectOcclusions(final PNode node, final PPickPath pickPath) {
        if (!node.fullIntersects(pickPath.getPickBounds())) {
            return;
        }

        pickPath.pushTransform(node.getTransformReference(false));

        final int count = node.getChildrenCount();
        for (int i = count - 1; i >= 0; i--) {
            final PNode each = node.getChild(i);
            if (node.getOccluded()) {
                // if n has been occluded by a previous descendant then
                // this child must also be occluded
                each.setOccluded(true);
            }
            else {
                // see if child each occludes n
                detectOcclusions(each, pickPath);
            }
        }

        if (nodeOccludesParents(node, pickPath)) {
            final PNode parent = node.getParent();
            while (parent != null && !parent.getOccluded()) {
                parent.setOccluded(true);
            }
        }

        pickPath.popTransform(node.getTransformReference(false));
    }

    /**
     * Calculate whether node occludes its parents.
     * 
     * @param n node to test
     * @param pickPath pickpath identifying the parents of the node
     * @return true if parents are occluded by the node
     */
    private boolean nodeOccludesParents(final PNode n, final PPickPath pickPath) {
        return !n.getOccluded() && n.intersects(pickPath.getPickBounds()) && n.isOpaque(pickPath.getPickBounds());
    }
}
