/**
 * 
 */
package edu.umd.cs.piccolo;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class MockPropertyChangeListener implements PropertyChangeListener {
    private final List changes = new ArrayList();

    public void propertyChange(final PropertyChangeEvent evt) {
        changes.add(evt);
    }

    public int getPropertyChangeCount() {
        return changes.size();
    }

    public PropertyChangeEvent getPropertyChange(final int index) {
        return (PropertyChangeEvent) changes.get(index);
    }
}