package edu.umd.cs.piccolo.examples;


public class FullScreenNodeExample extends NodeExample {

	public void initialize() {
		super.initialize();
		setFullScreenMode(true);	
	}

	public static void main(String[] args) {
		new FullScreenNodeExample();
	}
}
