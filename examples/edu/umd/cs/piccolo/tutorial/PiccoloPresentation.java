package edu.umd.cs.piccolo.tutorial;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.ArrayList;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.PFrame;

public class PiccoloPresentation extends PFrame {
	
	protected PNode slideBar;
	protected PNode currentSlide;
	protected PBasicInputEventHandler eventHandler;
	protected ArrayList slides = new ArrayList();

	public PiccoloPresentation() {
		super();		
	}

	public void initialize() {
		setFullScreenMode(true);
		loadSlides();
		
		eventHandler = new PBasicInputEventHandler() {
			public void keyReleased(PInputEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_SPACE) {
					int newIndex = slides.indexOf(currentSlide) + 1;
					if (newIndex < slides.size()) {
						goToSlide((PNode)slides.get(newIndex));
					}
				}
			}

			public void mouseReleased(PInputEvent event) {
				PNode picked = event.getPickedNode();

				if (picked.getParent() == slideBar) {
					picked.moveToFront();
					if (picked.getScale() == 1) {
						goToSlide(null);
					} else {
						goToSlide(picked);
					}
				}
			}
		};		
		
		getCanvas().requestFocus();
		getCanvas().addInputEventListener(eventHandler);
		getCanvas().getRoot().getDefaultInputManager().setKeyboardFocus(eventHandler);
		getCanvas().removeInputEventListener(getCanvas().getZoomEventHandler());
		getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
	}

	public void goToSlide(PNode slide) {
		if (currentSlide != null) {
			currentSlide.animateToTransform((AffineTransform)currentSlide.getAttribute("small"), 1000);
		}
		
		currentSlide = slide;
		
		if (currentSlide != null) {
			currentSlide.moveToFront();
			currentSlide.animateToTransform((AffineTransform)currentSlide.getAttribute("large"), 1000);
		}
	}
		
	public void loadSlides() {
		slideBar = new PNode();
		slideBar.setPaint(Color.DARK_GRAY);
		slideBar.setBounds(0, 0, getCanvas().getWidth(), 100);
		slideBar.setOffset(0, getCanvas().getHeight() - 100);
		getCanvas().getLayer().addChild(slideBar);
		
		File[] slideFiles = new File("slides").listFiles();
		for (int i = 0; i < slideFiles.length; i++) {			
			PNode slide = new PImage(slideFiles[i].getPath());
			
			if (slide.getHeight() != (getHeight() - 100)) {
				slide = new PImage(slide.toImage(getWidth(), getHeight() - 100, null));
			}
			slide.offset((getWidth() - slide.getWidth()) / 2, - (getHeight() - 100));
			slide.addAttribute("large", slide.getTransform());

			slide.setTransform(new AffineTransform());
			slide.scale((100 - 20) / slide.getHeight());
			slide.offset(i * (slide.getFullBoundsReference().getWidth() + 10) + 10, 10);
			slide.addAttribute("small", slide.getTransform());

			slideBar.addChild(slide);
			slides.add(slide);
		}
		
		goToSlide((PNode)slides.get(0));
	}
		
	public static void main(String[] argv) {
		new PiccoloPresentation();
	}
}
