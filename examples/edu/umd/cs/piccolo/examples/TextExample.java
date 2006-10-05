package edu.umd.cs.piccolo.examples;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.event.PStyledTextEventHandler;

/**
 * @author Lance Good
 */
public class TextExample extends PFrame {

	public TextExample() {
		this(null);
	}

	public TextExample(PCanvas aCanvas) {
		super("TextExample", false, aCanvas);
	}
	
	public void initialize() {
		getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
		PStyledTextEventHandler textHandler = new PStyledTextEventHandler(getCanvas());
		getCanvas().addInputEventListener(textHandler);
	}

	public static void main(String[] args) {
		new TextExample();
	}
}
