package edu.umd.cs.piccolox.swt;

import java.awt.GraphicsEnvironment;

import junit.framework.TestCase;

public abstract class SWTTest extends TestCase {
	public final boolean isHeadless() {
		return GraphicsEnvironment.isHeadless();
	}
}
