/**
 * 
 */
package edu.umd.cs.piccolo;

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.piccolo.util.PBounds;

class MockPCamera extends PCamera {
    private static final long serialVersionUID = 1L;
    private final List notifications = new ArrayList();

    public void repaintFromLayer(final PBounds bounds, final PLayer layer) {
        notifications.add(new Notification("repaintFromLayer", bounds, layer));
        super.repaintFromLayer(bounds, layer);
    }

    static class Notification {
        private final String type;
        private final PBounds bounds;
        // this should really be PLayer
        private final PNode layer;

        private Notification(final String type, final PBounds bounds, final PNode layer) {
            this.bounds = bounds;
            this.layer = layer;
            this.type = type;
        }

        public PNode getLayer() {
            return layer;
        }

        public PBounds getBounds() {
            return bounds;
        }
    }

    public int getNotificationCount() {
        return notifications.size();
    }

    public Notification getNotification(final int i) {
        return (Notification) notifications.get(i);
    }

}