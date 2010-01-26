/*
 * Copyright (c) 2008-2010, Piccolo2D project, http://piccolo2d.org
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
package org.piccolo2d;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import org.piccolo2d.nodes.PPath;
import org.piccolo2d.util.PAffineTransform;
import org.piccolo2d.util.PBounds;

/**
 * Performance tests.
 */
public class PerformanceTests extends TestCase {

    private static Measurements measurements = new Measurements();
    private static int NUMBER_NODES = 10000;
    private static int NUMBER_SETS = 10;

    public PerformanceTests(final String name) {
        super(name);
    }

    public void testRunPerformanceTests() throws NoninvertibleTransformException {
        for (int i = 0; i < NUMBER_SETS; i++) {
            addNodes();
            copyNodes();
            createNodes();
            createPaths();
            fullIntersectsNodes();
            memorySizeOfNodes();
            removeNodes();
            translateNodes();
            costOfNoBoundsCache();
            renderSpeed();
        }

        measurements.writeLog();
    }

    public void createNodes() {
        final PNode[] nodes = new PNode[NUMBER_NODES];

        measurements.time("Create " + NUMBER_NODES + " new nodes", new Runnable() {
            public void run() {
                for (int i = 0; i < NUMBER_NODES; i++) {
                    nodes[i] = new PNode();
                }
            }
        });
        ;
    }

    public void createPaths() {
        final PNode[] nodes = new PNode[NUMBER_NODES];
        final Random r = new Random();

        measurements.time("Create " + NUMBER_NODES + " new rect paths with random translations", new Runnable() {
            public void run() {
                for (int i = 0; i < NUMBER_NODES; i++) {
                    nodes[i] = PPath.createRectangle(0, 0, 100, 80);
                    nodes[i].translate(r.nextFloat() * 300, r.nextFloat() * 300);
                }
            }
        });
    }

    public void addNodes() {
        final PNode parent = new PNode();
        final PNode[] nodes = createNodesArray();

        measurements.time("Add " + NUMBER_NODES + " nodes to a new parent", new Runnable() {
            public void run() {
                for (int i = 0; i < NUMBER_NODES; i++) {
                    parent.addChild(nodes[i]);
                }
            }
        });
    }

    public void removeNodes() {
        final PNode parent = new PNode();
        final PNode[] nodes = createNodesArray();

        final List<PNode> list = new ArrayList<PNode>();

        for (int i = 0; i < NUMBER_NODES; i++) {
            parent.addChild(nodes[i]);
            list.add(nodes[i]);
        }

        measurements.time("Remove " + NUMBER_NODES + " nodes using removeChild() front to back", new Runnable() {
            public void run() {
                for (int i = 0; i < NUMBER_NODES; i++) {
                    parent.removeChild(nodes[i]);
                }
            }
        });

        parent.addChildren(list);
        measurements.time("Remove " + NUMBER_NODES + " nodes using removeChild() back to front by index",
                new Runnable() {
                    public void run() {
                        for (int i = NUMBER_NODES - 1; i >= 0; i--) {
                            parent.removeChild(i);
                        }
                    }
                });

        parent.addChildren(list);
        measurements.time("Remove " + NUMBER_NODES + " nodes using removeChild() back to front by object, TO_SLOW",
                new Runnable() {
                    public void run() {
                        for (int i = NUMBER_NODES - 1; i >= 0; i--) {
                            parent.removeChild(i);
                        }
                    }
                });

        parent.addChildren(list);
        measurements.time("Remove " + NUMBER_NODES + " nodes using removeChildren()", new Runnable() {

            public void run() {
                parent.removeChildren(list);
            }
        });

        parent.addChildren(list);

        measurements.time("Remove " + NUMBER_NODES + " nodes using removeAllChildren()", new Runnable() {
            public void run() {
                parent.removeAllChildren();
            }
        });
    }

    private PNode[] createNodesArray() {
        final PNode[] nodes = new PNode[NUMBER_NODES];
        for (int i = 0; i < NUMBER_NODES; i++) {
            nodes[i] = new PNode();
        }
        return nodes;
    }

    public void translateNodes() {
        final PNode parent = new PNode();
        final PNode[] nodes = new PNode[NUMBER_NODES];
        final PBounds b = new PBounds();
        final Random r = new Random();

        for (int i = 0; i < NUMBER_NODES; i++) {
            nodes[i] = new PNode();
            nodes[i].setBounds(1000 * r.nextFloat(), 1000 * r.nextFloat(), 100, 80);
            parent.addChild(nodes[i]);
            nodes[i].getFullBoundsReference();
        }

        measurements.time("Translate " + NUMBER_NODES + " nodes, not counting repaint or validate layout",
                new Runnable() {

                    public void run() {
                        for (int i = 0; i < NUMBER_NODES; i++) {
                            nodes[i].translate(1000 * r.nextFloat(), 1000 * r.nextFloat());
                            nodes[i].scale(1000 * r.nextFloat());
                            // nodes[i].translateBy(100.01, 100.2);
                            // nodes[i].scaleBy(0.9);
                        }
                    }
                });

        measurements.time("Validate Layout after translate " + NUMBER_NODES + " nodes", new Runnable() {
            public void run() {
                // Since parent.validateFullBounds(); now protected, we use
                // getFullBoundsReference since it calls validateFullBounds
                // indirectly.
                parent.getFullBoundsReference();

            }
        });

        measurements.time("Validate Paint after translate " + NUMBER_NODES + " nodes", new Runnable() {
            public void run() {
                parent.validateFullPaint();
            }
        });

        measurements.time("Parent compute bounds of " + NUMBER_NODES + " children nodes", new Runnable() {
            public void run() {
                parent.computeFullBounds(b);
            }
        });
    }

    public void fullIntersectsNodes() {
        final PNode parent = new PNode();
        final PNode[] nodes = new PNode[NUMBER_NODES];
        final PBounds b = new PBounds(0, 50, 100, 20);

        for (int i = 0; i < NUMBER_NODES; i++) {
            nodes[i] = new PNode();
            parent.addChild(nodes[i]);
        }

        // parent.validateFullBounds(); // now protected
        parent.getFullBoundsReference(); // calls validateFullBounds as a side
        // effect.

        measurements.time("fullIntersect on " + NUMBER_NODES + " nodes", new Runnable() {
            public void run() {
                for (int i = 0; i < NUMBER_NODES; i++) {
                    nodes[i].fullIntersects(b);
                }
            }
        });
    }

    public void memorySizeOfNodes() {
        final PNode[] nodes = new PNode[NUMBER_NODES];

        measurements.memory("Approximate k used by " + NUMBER_NODES + " nodes", new Runnable() {
            public void run() {
                for (int i = 0; i < NUMBER_NODES; i++) {
                    nodes[i] = new PNode();
                }
            }
        });
    }

    public void copyNodes() {
        final PNode parent = new PNode();
        final PNode[] nodes = new PNode[NUMBER_NODES];

        for (int i = 0; i < NUMBER_NODES; i++) {
            nodes[i] = new PNode();
            parent.addChild(nodes[i]);
        }

        measurements.time("Copy/Serialize " + NUMBER_NODES + " nodes", new Runnable() {
            public void run() {
                parent.clone();
            }
        });
    }

    public void costOfNoBoundsCache() {
        final PNode[] nodes = new PNode[NUMBER_NODES];
        final PBounds[] bounds = new PBounds[NUMBER_NODES];
        final PBounds pickRect = new PBounds(0, 0, 1, 1);
        final Random r = new Random();

        for (int i = 0; i < NUMBER_NODES; i++) {
            nodes[i] = new PNode();
            nodes[i].translate(1000 * r.nextFloat(), 1000 * r.nextFloat());
            nodes[i].scale(1000 * r.nextFloat());
            bounds[i] = new PBounds(1000 * r.nextFloat(), 1000 * r.nextFloat(), 100, 80);
        }

        measurements.time("Do intersects test for " + NUMBER_NODES + " bounds", new Runnable() {
            public void run() {
                for (int i = 0; i < NUMBER_NODES; i++) {
                    bounds[i].intersects(pickRect);
                }
            }
        });

        measurements.time("Transform " + NUMBER_NODES + " bounds from local to parent", new Runnable() {
            public void run() {
                for (int i = 0; i < NUMBER_NODES; i++) {
                    nodes[i].localToParent(bounds[i]);
                }
            }
        });

        measurements.time("Sum " + NUMBER_NODES + " bounds", new Runnable() {
            public void run() {
                for (int i = 0; i < NUMBER_NODES; i++) {
                    pickRect.add(bounds[i]);
                }
            }
        });

        final PBounds b = new PBounds(r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble());

        measurements.time("Clone " + NUMBER_NODES + " PBounds", new Runnable() {
            public void run() {
                for (int i = 0; i < NUMBER_NODES; i++) {
                    b.clone();
                }
            }
        });

    }

    private void renderSpeed() throws NoninvertibleTransformException {
        final Random r = new Random();
        final PAffineTransform at = new PAffineTransform();
        at.setScale(r.nextFloat());
        at.translate(r.nextDouble(), r.nextDouble());

        measurements.time("Create inverse transform " + NUMBER_NODES + " times", new Runnable() {
            public void run() {
                try {
                    for (int i = 0; i < NUMBER_NODES; i++) {
                        at.createInverse();
                    }
                }
                catch (final NoninvertibleTransformException e) {
                    e.printStackTrace();
                }
            }
        });

        final int height = 400;
        final int width = 400;

        final double scale1 = 0.5;
        final double scale2 = 2;

        final PAffineTransform transorm1 = new PAffineTransform();
        // transorm1.scale(0.5, 0.5);
        transorm1.translate(0.5, 10.1);
        final PAffineTransform transorm2 = new PAffineTransform(transorm1.createInverse());

        final GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDefaultConfiguration();
        final BufferedImage result = graphicsConfiguration.createCompatibleImage(width, height,
                Transparency.TRANSLUCENT);
        final Graphics2D g2 = result.createGraphics();

        measurements.time("Scale graphics context " + NUMBER_NODES + " times", new Runnable() {
            public void run() {
                for (int i = 0; i < NUMBER_NODES; i++) {
                    if (i % 2 == 0) {
                        g2.scale(scale2, scale2);
                    }
                    else {
                        g2.scale(scale1, scale1);

                    }
                }
            }
        });

        g2.setTransform(new AffineTransform());

        measurements.time("Translate graphics context " + NUMBER_NODES + " times", new Runnable() {
            public void run() {
                for (int i = 0; i < NUMBER_NODES; i++) {
                    g2.translate(0.5, 0.5);
                }
            }
        });

        g2.setTransform(new AffineTransform());

        measurements.time("Transform graphics context " + NUMBER_NODES + " times", new Runnable() {
            public void run() {
                for (int i = 0; i < NUMBER_NODES; i++) {
                    if (i % 2 == 0) {
                        g2.transform(transorm1);
                    }
                    else {
                        g2.transform(transorm2);
                    }
                }
            }
        });

        final Rectangle2D rect = new Rectangle2D.Double(0, 0, 100, 80);
        final GeneralPath path = new GeneralPath(rect);

        measurements.time("Fill " + NUMBER_NODES + " rects", new Runnable() {
            public void run() {
                for (int i = 0; i < NUMBER_NODES; i++) {
                    g2.fill(rect);
                }
            }
        });

        measurements.time("Call g2.getTransform() " + NUMBER_NODES + " times", new Runnable() {
            public void run() {
                for (int i = 0; i < NUMBER_NODES; i++) {
                    g2.getTransform().getScaleX();
                }
            }
        });

        measurements.time("Fill " + NUMBER_NODES + " paths", new Runnable() {
            public void run() {
                for (int i = 0; i < NUMBER_NODES; i++) {
                    g2.fill(path);
                }
            }
        });
    }
}
