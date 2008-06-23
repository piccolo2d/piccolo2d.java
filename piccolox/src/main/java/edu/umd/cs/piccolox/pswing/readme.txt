This directory contains source and examples for embedding Swing components in a Piccolo hierarchy.  This code was ported from a Jazz implementation.

Example usage:

        JSlider js = new JSlider( 0, 100 );
        PSwing pSwing = new PSwing( pswingCanvas, js );
        l.addChild( pSwing );

Known Issues
o Handling cursors on Swing components is not yet supported.
o Creation of a PSwing currently requires an instance of the PSwingCanvas in which the component will appear.  Future versions could delete this requirement, so that the constructor is simply PSwing(JComponent), and the PSwing can appear in many PSwingCanvases.

This code has been tested in a variety of situations by 4 or 5 independent users, but with more users, some bugs will be most likely be exposed.  (This code comes with NO WARRANTY, etc.)

Sam Reid
reids@colorado.edu