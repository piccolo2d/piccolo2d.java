/*
 * Copyright (c) 2008-2009, Piccolo2D project, http://piccolo2d.org
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
/*
 * This class PNotificationCenter center is derived from the class
 * NSNotificationCenter from:
 * 
 * Wotonomy: OpenStep design patterns for pure Java
 * applications. Copyright (C) 2000 Blacksmith, Inc.
 */
package edu.umd.cs.piccolox.event;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <b>PNotificationCenter</b> provides a way for objects that don't know about
 * each other to communicate. It receives PNotification objects and broadcasts
 * them to all interested listeners. Unlike standard Java events, the event
 * listeners don't need to know about the event source, and the event source
 * doesn't need to maintain the list of listeners.
 * <p>
 * Listeners of the notfications center are held by weak references. So the
 * notfication center will not create garbage collection problems as standard
 * java event listeners do.
 * </p>
 * 
 * @author Jesse Grosjean
 */
public class PNotificationCenter {

    public static final Object NULL_MARKER = new Object();

    protected static PNotificationCenter DEFAULT_CENTER;

    protected HashMap listenersMap;
    protected ReferenceQueue keyQueue;

    public static PNotificationCenter defaultCenter() {
        if (DEFAULT_CENTER == null) {
            DEFAULT_CENTER = new PNotificationCenter();
        }
        return DEFAULT_CENTER;
    }

    private PNotificationCenter() {
        listenersMap = new HashMap();
        keyQueue = new ReferenceQueue();
    }

    // ****************************************************************
    // Add Listener Methods
    // ****************************************************************

    /**
     * Registers the 'listener' to receive notifications with the name
     * 'notificationName' and/or containing 'object'. When a matching
     * notification is posted the callBackMethodName message will be sent to the
     * listener with a single PNotification argument. If notificationName is
     * null then the listener will receive all notifications with an object
     * matching 'object'. If 'object' is null the listener will receive all
     * notifications with the name 'notificationName'.
     * 
     * @return whether or not the listener has been added
     * @throws SecurityException
     */
    public boolean addListener(final Object listener, final String callbackMethodName, final String notificationName,
            Object object) throws SecurityException {
        processKeyQueue();

        Object name = notificationName;
        Method method = null;

        try {
            method = listener.getClass().getMethod(callbackMethodName, new Class[] { PNotification.class });
        }
        catch (final NoSuchMethodException e) {
            return false;
        }

        final int modifiers = method.getModifiers();

        if (!Modifier.isPublic(modifiers)) {
            return false;
        }

        if (name == null) {
            name = NULL_MARKER;
        }

        if (object == null) {
            object = NULL_MARKER;
        }

        final Object key = new NotificationKey(name, object);
        final Object notificationTarget = new NotificationTarget(listener, method);

        List list = (List) listenersMap.get(key);
        if (list == null) {
            list = new ArrayList();
            listenersMap.put(new NotificationKey(name, object, keyQueue), list);
        }

        if (!list.contains(notificationTarget)) {
            list.add(notificationTarget);
        }

        return true;
    }

    // ****************************************************************
    // Remove Listener Methods
    // ****************************************************************

    /**
     * Removes the listener so that it no longer recives notfications from this
     * notfication center.
     */
    public void removeListener(final Object listener) {
        processKeyQueue();

        final Iterator i = new LinkedList(listenersMap.keySet()).iterator();
        while (i.hasNext()) {
            removeListener(listener, i.next());
        }
    }

    /**
     * Removes the listeners as the listener of notifications matching
     * notificationName and object. If listener is null all listeners matching
     * notificationName and object are removed. If notificationName is null the
     * listener will be removed from all notifications containing the object. If
     * the object is null then the listener will be removed from all
     * notifications matching notficationName.
     */
    public void removeListener(final Object listener, final String notificationName, final Object object) {
        processKeyQueue();

        final List keys = matchingKeys(notificationName, object);
        final Iterator it = keys.iterator();
        while (it.hasNext()) {
            removeListener(listener, it.next());
        }
    }

    // ****************************************************************
    // Post PNotification Methods
    // ****************************************************************

    /**
     * Post a new notfication with notificationName and object. The object is
     * typically the object posting the notification. The object may be null.
     */
    public void postNotification(final String notificationName, final Object object) {
        postNotification(notificationName, object, null);
    }

    /**
     * Creates a notification with the name notificationName, associates it with
     * the object, and posts it to this notification center. The object is
     * typically the object posting the notification. It may be nil.
     */
    public void postNotification(final String notificationName, final Object object, final Map userInfo) {
        postNotification(new PNotification(notificationName, object, userInfo));
    }

    /**
     * Post the notification to this notification center. Most often clients
     * will instead use one of this classes convenience postNotifcations
     * methods.
     */
    public void postNotification(final PNotification aNotification) {
        final List mergedListeners = new LinkedList();
        List listenersList;

        final Object name = aNotification.getName();
        final Object object = aNotification.getObject();

        if (name != null) {
            if (object == null) {// object is null
                listenersList = (List) listenersMap.get(new NotificationKey(name, NULL_MARKER));
                if (listenersList != null) {
                    mergedListeners.addAll(listenersList);
                }
            }
            else { // both are specified
                listenersList = (List) listenersMap.get(new NotificationKey(name, object));
                if (listenersList != null) {
                    mergedListeners.addAll(listenersList);
                }
                listenersList = (List) listenersMap.get(new NotificationKey(name, NULL_MARKER));
                if (listenersList != null) {
                    mergedListeners.addAll(listenersList);
                }
                listenersList = (List) listenersMap.get(new NotificationKey(NULL_MARKER, object));
                if (listenersList != null) {
                    mergedListeners.addAll(listenersList);
                }
            }
        }
        else if (object != null) { // name is null
            listenersList = (List) listenersMap.get(new NotificationKey(NULL_MARKER, object));
            if (listenersList != null) {
                mergedListeners.addAll(listenersList);
            }
        }

        final Object key = new NotificationKey(NULL_MARKER, NULL_MARKER);
        listenersList = (List) listenersMap.get(key);
        if (listenersList != null) {
            mergedListeners.addAll(listenersList);
        }

        dispatchNotifications(aNotification, mergedListeners);
    }

    private void dispatchNotifications(final PNotification aNotification, final List listeners) {
        NotificationTarget listener;
        final Iterator it = listeners.iterator();

        while (it.hasNext()) {
            listener = (NotificationTarget) it.next();
            if (listener.get() == null) {
                it.remove();
            }
            else {
                try {
                    listener.getMethod().invoke(listener.get(), new Object[] { aNotification });
                }
                catch (final IllegalAccessException e) {
                    throw new RuntimeException("Impossible Situation: invoking inaccessible method on listener", e);
                }
                catch (final InvocationTargetException e) {         
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // ****************************************************************
    // Implementation classes and methods
    // ****************************************************************

    protected List matchingKeys(final String name, final Object object) {
        final List result = new LinkedList();

        final NotificationKey searchKey = new NotificationKey(name, object);
        final Iterator it = listenersMap.keySet().iterator();
        while (it.hasNext()) {
            final NotificationKey key = (NotificationKey) it.next();
            if (searchKey.equals(key)) {
                result.add(key);
            }
        }

        return result;
    }

    protected void removeListener(final Object listener, final Object key) {
        if (listener == null) {
            listenersMap.remove(key);
            return;
        }

        final List list = (List) listenersMap.get(key);
        if (list == null) {
            return;
        }

        final Iterator it = list.iterator();
        while (it.hasNext()) {
            final Object observer = ((NotificationTarget) it.next()).get();
            if (observer == null || listener == observer) {
                it.remove();
            }
        }

        if (list.size() == 0) {
            listenersMap.remove(key);
        }
    }

    protected void processKeyQueue() {
        NotificationKey key;
        while ((key = (NotificationKey) keyQueue.poll()) != null) {
            listenersMap.remove(key);
        }
    }

    protected static class NotificationKey extends WeakReference {

        private final Object name;
        private final int hashCode;

        public NotificationKey(final Object aName, final Object anObject) {
            super(anObject);
            name = aName;
            hashCode = aName.hashCode() + anObject.hashCode();
        }

        public NotificationKey(final Object aName, final Object anObject, final ReferenceQueue aQueue) {
            super(anObject, aQueue);
            name = aName;
            hashCode = aName.hashCode() + anObject.hashCode();
        }

        public Object name() {
            return name;
        }

        public int hashCode() {
            return hashCode;
        }

        public boolean equals(final Object anObject) {
            if (this == anObject) {
                return true;
            }

            if (!(anObject instanceof NotificationKey)) {
                return false;
            }

            final NotificationKey key = (NotificationKey) anObject;

            if (name != key.name && (name == null || !name.equals(key.name))) {
                return false;
            }

            final Object object = get();

            return object != null && object == key.get();
        }

        public String toString() {
            return "[CompoundKey:" + name() + ":" + get() + "]";
        }
    }

    protected static class NotificationTarget extends WeakReference {

        protected int hashCode;
        protected Method method;

        public NotificationTarget(final Object object, final Method method) {
            super(object);
            hashCode = object.hashCode();
            this.method = method;
        }

        public Method getMethod() {
            return method;
        }

        public int hashCode() {
            return hashCode;
        }

        public boolean equals(final Object object) {
            if (this == object) {
                return true;
            }

            if (!(object instanceof NotificationTarget)) {
                return false;
            }

            final NotificationTarget target = (NotificationTarget) object;
            if (method != target.method && (method == null || !method.equals(target.method))) {
                return false;
            }

            final Object o = get();

            return o != null && o == target.get();
        }

        public String toString() {
            return "[CompoundValue:" + get() + ":" + getMethod().getName() + "]";
        }
    }
}
