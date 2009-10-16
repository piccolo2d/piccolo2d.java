package edu.umd.cs.piccolox.swt;

import java.awt.GraphicsEnvironment;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import junit.framework.TestCase;

public abstract class SWTTest extends TestCase {	
	public final boolean isHeadless() {
		return GraphicsEnvironment.isHeadless();
	}
	
	protected final boolean hasHead() {
		return !isHeadless();
	}
	
	protected PSWTCanvas buildSimpleCanvas(Display display) {			   
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());		
		return new PSWTCanvas(shell, 0);
	}
}
