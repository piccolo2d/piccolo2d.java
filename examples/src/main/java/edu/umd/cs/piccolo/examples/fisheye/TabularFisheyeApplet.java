package edu.umd.cs.piccolo.examples.fisheye;

import javax.swing.JApplet;

public class TabularFisheyeApplet extends JApplet {

    public void init() {
        getContentPane().add(new TabularFisheye());        
    }

}
