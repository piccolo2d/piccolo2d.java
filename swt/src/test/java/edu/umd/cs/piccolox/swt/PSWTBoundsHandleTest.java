package edu.umd.cs.piccolox.swt;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolox.util.PBoundsLocator;

public class PSWTBoundsHandleTest extends SWTTest {
    private PNode node;

    public void setUp() {
        if (hasHead()) {
            node = new PNode();
            node.setBounds(0, 0, 100, 100);
        }
    }

    public void testAddBoundsHandlesToNodeAddsHandles() {
        if (hasHead()) {
            PSWTBoundsHandle.addBoundsHandlesTo(node);
            assertEquals(8, node.getChildrenCount());
            
            for (int i=0; i<8; i++) {
                PNode child = node.getChild(i);
                assertTrue(child instanceof PSWTBoundsHandle);
            }
        }
    }
    
    public void testAddStickyBoundsHandlesToNodeAddsHandles() {
        if (hasHead()) {
            PCamera camera = new PCamera();
            PSWTBoundsHandle.addStickyBoundsHandlesTo(node, camera);
            assertEquals(0, node.getChildrenCount());
            assertEquals(8, camera.getChildrenCount());
            
            for (int i=0; i<8; i++) {
                PNode child = camera.getChild(i);
                assertTrue(child instanceof PSWTBoundsHandle);
            }
        }
    }
    
    public void testRemoveBoundsHandlesRemovesOnlyHandles() {
        if (hasHead()) {
            PNode child = new PNode();
            node.addChild(child);
            PSWTBoundsHandle.addBoundsHandlesTo(node);
            PSWTBoundsHandle.removeBoundsHandlesFrom(node);
            assertEquals(1, node.getChildrenCount());  
            assertEquals(child, node.getChild(0));
        }
    }
    
    public void testRemoveBoundsHandlesDoesNothingWhenNoHandles() {
        if (hasHead()) {
            PNode child = new PNode();
            node.addChild(child);
            PSWTBoundsHandle.removeBoundsHandlesFrom(node);
            assertEquals(1, node.getChildrenCount());           
        }
    }
    
    public void testCursorHandlerIsInstalledByDefault() {
        if (hasHead()) {
            PSWTBoundsHandle handle = new PSWTBoundsHandle(PBoundsLocator.createEastLocator(node));
            PInputEventListener dragHandler = handle.getHandleDraggerHandler();
            PInputEventListener cursorHandler = handle.getHandleCursorEventHandler();
            assertNotNull(cursorHandler);
            PInputEventListener[] listeners = handle.getInputEventListeners();
            assertEquals(2, listeners.length);
            assertTrue(cursorHandler == listeners[0] || cursorHandler == listeners[1]);
            assertTrue(dragHandler == listeners[0] || dragHandler == listeners[1]);
        }
    }      
}
