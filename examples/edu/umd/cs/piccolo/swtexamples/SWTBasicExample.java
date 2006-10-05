package edu.umd.cs.piccolo.swtexamples;

import java.awt.Color;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.umd.cs.piccolox.swt.PSWTCanvas;
import edu.umd.cs.piccolox.swt.PSWTPath;
import edu.umd.cs.piccolox.swt.PSWTText;

/**
 * @author good
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SWTBasicExample {

	/**
	 * Constructor for SWTBasicExample.
	 */
	public SWTBasicExample() {
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
		
		PSWTPath rect = PSWTPath.createRectangle(25,25,50,50);
		rect.setPaint(Color.red);
		canvas.getLayer().addChild(rect);
		
		rect = PSWTPath.createRectangle(300,25,100,50);
		rect.setPaint(Color.blue);
		canvas.getLayer().addChild(rect);		
		
		PSWTPath circle = PSWTPath.createEllipse(100,200,50,50);
		circle.setPaint(Color.green);
		canvas.getLayer().addChild(circle);

		circle = PSWTPath.createEllipse(400,400,75,150);
		circle.setPaint(Color.yellow);
		canvas.getLayer().addChild(circle);
		
		PSWTText text = new PSWTText("Hello World");
		text.translate(350,150);
		text.setPenColor(Color.gray);
		canvas.getLayer().addChild(text);
		
		text = new PSWTText("Goodbye World");
		text.translate(50,400);
		text.setPenColor(Color.magenta);
		canvas.getLayer().addChild(text);
		
		shell.open ();
		return shell;
	}
}
