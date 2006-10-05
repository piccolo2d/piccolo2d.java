package edu.umd.cs.piccolo.swtexamples;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.umd.cs.piccolox.swt.PSWTCanvas;
import edu.umd.cs.piccolox.swt.PSWTText;

/**
 * @author good
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SWTHelloWorld {

	/**
	 * Constructor for SWTBasicExample.
	 */
	public SWTHelloWorld() {
		super();
	}

	public static void main(String[] args) {
		Display display = new Display ();
		Shell shell = open (display);
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
	
	public static Shell open(Display display) {
		final Shell shell = new Shell (display);
		shell.setLayout(new FillLayout());
		PSWTCanvas canvas = new PSWTCanvas(shell,0);
		
		PSWTText text = new PSWTText("Hello World");
		canvas.getLayer().addChild(text);
		
		shell.open ();
		return shell;
	}
}
