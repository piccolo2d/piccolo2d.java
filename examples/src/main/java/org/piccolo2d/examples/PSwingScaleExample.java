package org.piccolo2d.examples;

import org.piccolo2d.PCamera;
import org.piccolo2d.PLayer;
import org.piccolo2d.PNode;
import org.piccolo2d.extras.PFrame;
import org.piccolo2d.extras.pswing.PSwing;
import org.piccolo2d.extras.pswing.PSwingCanvas;
import org.piccolo2d.util.PBounds;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Demonstrate that PSwing nodes properly receive events even when they are parented by nodes
 * with extreme scales. This is an effective regression test that previously failed before fix
 * applied to {@link org.piccolo2d.extras.pswing.PSwingEventHandler}.
 */
public class PSwingScaleExample extends PFrame {

    public static void main(String[] args) {
        new PSwingScaleExample();
    }

    public PSwingScaleExample() {
        super(PSwingScaleExample.class.getSimpleName(), false, new PSwingCanvas());
    }

    public void initialize() {
        final PSwingCanvas canvas = (PSwingCanvas) getCanvas();
        final PLayer layer = canvas.getLayer();
        final PCamera camera = canvas.getCamera();

        PNode parent = new PNode();
        parent.setPaint(Color.orange);
        parent.setBounds(0, 0, 200, 200);
        
        JButton button = new JButton("Drink Me");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(PSwingScaleExample.this, "Thank You");
            }
        });
        final PSwing ps = new PSwing(button);
        centerFullBoundsIn(ps, parent.getGlobalFullBounds());
        parent.addChild(ps);
        parent.scale(0.001);

        layer.addChild(parent);

        camera.animateViewToCenterBounds(ps.getGlobalFullBounds(), true, 0);
    }
    
    private static void centerFullBoundsIn(PNode centerMe, PBounds bounds) {
        centerMe.centerFullBoundsOnPoint(bounds.getCenterX(), bounds.getCenterY());
    }

}