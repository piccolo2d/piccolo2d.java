package edu.umd.cs.piccolo.examples.fisheye;

import javax.swing.JFrame;

public class TabularFisheyeFrame extends JFrame {
    public TabularFisheyeFrame() {
        setTitle("Piccolo2D Tabular Fisheye");

        TabularFisheye tabularFisheye = new TabularFisheye();
        getContentPane().add(tabularFisheye);
        pack();
    }

    public static void main(String args[]) {
        JFrame frame = new TabularFisheyeFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
