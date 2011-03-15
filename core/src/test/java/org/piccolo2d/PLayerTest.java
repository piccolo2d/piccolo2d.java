/*
 * Copyright (c) 2008-2011, Piccolo2D project, http://piccolo2d.org
 * Copyright (c) 1998-2008, University of Maryland
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * None of the name of the University of Maryland, the name of the Piccolo2D project, or the names of its
 * contributors may be used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.piccolo2d;

import java.util.Collection;

import org.piccolo2d.PCamera;
import org.piccolo2d.PLayer;
import org.piccolo2d.util.PBounds;

import junit.framework.TestCase;

/**
 * Unit test for PLayer.
 */
public class PLayerTest extends TestCase {
    private PLayer layer;

    public void setUp() {
        layer = new PLayer();
    }

    public void testLayerHasEmptyCamerasCollectionByDefault() {
        final Collection cameras = layer.getCamerasReference();
        assertNotNull(cameras);
        assertTrue(cameras.isEmpty());
        assertEquals(0, layer.getCameraCount());
    }

    public void testGetCameraByIndexThrowsIndexOutOfBoundsExceptionWhenOutOfBounds() {
        final PCamera camera = new PCamera();
        layer.addCamera(camera);
        try {
            layer.getCamera(-1);
            fail("Exception should have been thrown");
        }
        catch (final IndexOutOfBoundsException e) {
            // expected;
        }

        try {
            layer.getCamera(1);
            fail("Exception should have been thrown");
        }
        catch (final IndexOutOfBoundsException e) {
            // expected;
        }
    }

    public void testGetCameraReturnsCameraAtCorrectIndex() {
        final PCamera camera1 = new PCamera();
        final PCamera camera2 = new PCamera();
        final PCamera camera3 = new PCamera();

        layer.addCamera(camera1);
        layer.addCamera(camera2);
        layer.addCamera(camera3);

        assertEquals(camera1, layer.getCamera(0));
        assertEquals(camera2, layer.getCamera(1));
        assertEquals(camera3, layer.getCamera(2));
    }

    public void testAddCameraCorrectlyHandlesIndex() {
        final PCamera camera1 = new PCamera();
        final PCamera camera2 = new PCamera();
        final PCamera camera3 = new PCamera();

        layer.addCamera(0, camera1);
        layer.addCamera(0, camera2);
        layer.addCamera(1, camera3);

        assertEquals(camera2, layer.getCamera(0));
        assertEquals(camera3, layer.getCamera(1));
        assertEquals(camera1, layer.getCamera(2));
    }

    public void testRemovingCameraByReferenceWorksWhenCameraIsFound() {
        final PCamera camera = new PCamera();
        layer.addCamera(camera);
        layer.removeCamera(camera);
        assertEquals(0, layer.getCameraCount());
    }

    public void testRemovingCameraByIndexWorksWhenIndexIsValid() {
        final PCamera camera = new PCamera();
        layer.addCamera(camera);
        layer.removeCamera(0);
        assertEquals(0, layer.getCameraCount());
    }

    public void testRemovingCameraNotAttachedToCameraShouldDoNothing() {
        final PCamera strangerCamera = new PCamera();
        layer.removeCamera(strangerCamera);
        assertEquals(0, layer.getCameraCount());
    }

    public void testRepaintFromNotifiesCameras() {
        final MockPCamera camera = new MockPCamera();
        layer.addCamera(camera);

        final PBounds bounds = new PBounds(0, 0, 100, 100);
        layer.repaintFrom(bounds, layer);

        assertEquals(1, camera.getNotificationCount());

        final MockPCamera.Notification notification = camera.getNotification(0);
        assertEquals(layer, notification.getLayer());
        assertEquals(bounds, notification.getBounds());
    }
}
