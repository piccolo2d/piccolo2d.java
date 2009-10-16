package edu.umd.cs.piccolox.swt;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.util.PBounds;

public class PSWTTextTest extends SWTTest {
	private PSWTText textNode;

	public void setUp() {
		if (hasHead()) {
			textNode = new PSWTText();
		}
	}

	public void testConstructorRemembersTextValue() {
		if (hasHead()) {
			textNode = new PSWTText("Hello World\n\n");
			assertEquals("Hello World\n\n", textNode.getText());
		}
	}

	public void testTextPersistsTrainingAndInternalNewlines() {
		if (hasHead()) {
			textNode.setText("Hello\nWorld\n\n");
			assertEquals("Hello\nWorld\n\n", textNode.getText());
		}
	}

	public void testDefaultPropertiesAreCorrect() {
		if (hasHead()) {
			assertEquals(Color.BLACK, textNode.getPenColor());
			assertEquals(Color.BLACK, textNode.getPenPaint());
			assertNull(textNode.getBackgroundColor());
			assertNull(textNode.getPaint());
			assertEquals(5.5, textNode.getGreekThreshold(), Double.MIN_VALUE);
			assertFalse(textNode.isTransparent());
		}
	}

	public void testDefaultFontIsCorrect() {
		if (hasHead()) {
			Font font = textNode.getFont();
			assertNotNull(font);
			assertFalse(font.isBold());
			assertEquals(12, font.getSize());
		}
	}

	public void testPenColorPersists() {
		if (hasHead()) {
			textNode.setPenColor(Color.RED);
			assertEquals(Color.RED, textNode.getPenColor());
		}
	}

	public void testPenPaintPersists() {
		if (hasHead()) {
			textNode.setPenPaint(Color.RED);
			assertEquals(Color.RED, textNode.getPenPaint());
		}
	}

	public void testTransparencyPersists() {
		if (hasHead()) {
			textNode.setTransparent(true);
			assertTrue(textNode.isTransparent());
		}
	}

	public void testBackgroundColor() {
		if (hasHead()) {
			textNode.setBackgroundColor(Color.RED);
			assertEquals(Color.RED, textNode.getBackgroundColor());
		}
	}

	public void testPenPaintAndPenColorAreSameThing() {
		if (hasHead()) {
			textNode.setPenColor(Color.RED);
			assertEquals(Color.RED, textNode.getPenPaint());

			textNode.setPenPaint(Color.BLUE);
			assertEquals(Color.BLUE, textNode.getPenColor());
		}
	}

	public void testBackgroundColorAndPaintAreSameThing() {
		if (hasHead()) {
			textNode.setBackgroundColor(Color.RED);
			assertEquals(Color.RED, textNode.getPaint());

			textNode.setPaint(Color.BLUE);
			assertEquals(Color.BLUE, textNode.getBackgroundColor());
		}
	}

	public void testGreekThresholdPersists() {
		if (hasHead()) {
			textNode.setGreekThreshold(0.1);
			assertEquals(0.1, textNode.getGreekThreshold(), Double.MIN_VALUE);
		}
	}

	public void testShrinkingFontShrinksBounds() {
		if (hasHead()) {
			textNode.setText("Hello\nWorld");

			PBounds startBounds = textNode.getBounds();
			Font startFont = textNode.getFont();
			Font newFont = new Font(startFont.getFontName(), startFont
					.getStyle(), 8);

			textNode.setFont(newFont);
			assertSame(newFont, textNode.getFont());

			PBounds endBounds = textNode.getBounds();
			assertTrue(startBounds.width > endBounds.width);
			assertTrue(startBounds.height > endBounds.height);
		}
	}

	public void testTranslationsBehaveLogically() {
		if (hasHead()) {
			textNode.setTranslation(1, 2);
			assertEquals(1, textNode.getTranslateX(), Double.MIN_VALUE);
			assertEquals(2, textNode.getTranslateY(), Double.MIN_VALUE);

			textNode.setTranslateX(3);
			assertEquals(3, textNode.getTranslateX(), Double.MIN_VALUE);

			textNode.setTranslateY(4);
			assertEquals(4, textNode.getTranslateY(), Double.MIN_VALUE);

			assertEquals(new Point2D.Double(3, 4), textNode.getTranslation());
			
			textNode.setTranslation(new Point2D.Double(5,6));
			assertEquals(new Point2D.Double(5,6), textNode.getTranslation());
		}
	}

	public void testTranslatingDoesntAffectSize() {
		if (hasHead()) {
			textNode.setText("Hello");
			PBounds startBounds = textNode.getBounds();
			textNode.translate(1, 2);
			PBounds endBounds = textNode.getBounds();
			assertEquals(startBounds.width, endBounds.width, Double.MIN_VALUE);
			assertEquals(startBounds.height, endBounds.height, Double.MIN_VALUE);

		}
	}

}
