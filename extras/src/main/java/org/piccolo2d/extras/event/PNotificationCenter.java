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
/*
 * This class PNotificationCenter center is derived from the class
 * NSNotificationCenter from:
 * 
 * Wotonomy: OpenStep design patterns for pure Java
 * applications. Copyright (C) 2000 Blacksmith, Inc.
 */
package org.piccolo2d.extras.event;

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
 * Listeners of the notifications center are held by weak references. So the
 * notification center will not create garbage collection problems as standard
 * java event listeners do.
 * </p>
 * 
 * @author Jesse Grosjean
 */
public final class PNotificationCenter {
    /** Used as a place holder for null names or objects. */
    public static final Object NULL_MARKER = new Object();

    /** Singleton instance of the notification center. */
    private  static volatile PNotificationCenter DEFAULT_CENTER;

    /** A map of listeners keyed by NotificationKey objects. */
    private HashMap listenersMap;

    /** A queue of NotificationKeys that are available to be garbage collected. */
    private ReferenceQueue keyQueue;

    /**
     * Singleton accessor for the PNotificationCenter.
     * 
     * @return singleton instance of PNotificationCenter
     */
    public synchronized static PNotificationCenter defaultCenter() {
        if (DEFAULT_CENTER == null) {
            DEFAULT_CENTER = new PNotificationCenter();
        }
        return DEFAULT_CENTER;
    }

    private PNotificationCenter() {
        listenersMap = new HashMap();
        keyQueue = new ReferenceQueue();
    }

    /**
     * Registers the 'listener' to receive notifications with the name
     * 'notificationName' and/or containing 'object'. When a matching
     * notification is posted the callBackMethodName message will be sent to the
     * listener with a single PNotification argument. If notificationName is
     * null then the listener will receive all notifications with an object
     * matching 'object'. If 'object' is null the listener will receive all
     * notifications with the name 'notificationName'.
     * 
     * @param listener object to be notified of notifications
     * @param callbackMethodName method to be invoked on the listener
     * @param notificationName name of notifications to filter on
     * @param object source of notification messages that this listener is
     *            interested in
     * @return true if listener has been added
     */
    public boolean addListener(final Object listener, final String callbackMethodName, final String notificationName,
            final Object object) {
        processKeyQueue();

        final Object name = nullify(notificationName);
        final Object sanitizedObject = nullify(object);

        final Method method = extractCallbackMethod(listener, callbackMethodName);
        if (method == null) {
            return false;
        }

        final NotificationKey key = new NotificationKey(name, sanitizedObject);
        final NotificationTarget notificationTarget = new NotificationTarget(listener, method);

        List list = (List) listenersMap.get(key);
        if (list == null) {
            list = new ArrayList();
            listenersMap.put(new NotificationKey(name, sanitizedObject, keyQueue), list);
        }

        if (!list.contains(notificationTarget)) {
            list.add(notificationTarget);
        }

        return true;
    }

    private Method extractCallbackMethod(final Object listener, final String methodName) {
        Method method = null;
        try {
            Class[] classes = new Class[1];
            classes[0] = PNotification.class;
            method = listener.getClass().getMethod(methodName, classes);
        }
        catch (final NoSuchMethodException e) {
            return null;
        }

        final int modifiers = method.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            return null;
        }

        return method;
    }

    /**
     * Sanitizes the object reference by returning NULL_MARKER if the object is
     * null.
     * 
     * @param object object to sanitize
     * 
     * @return NULL_MARKER is object is null, otherwise object
     */
    private Object nullify(final Object object) {
        if (object == null) {
            return NULL_MARKER;
        }

        return object;
    }

    // ****************************************************************
    // Remove Listener Methods
    // ****************************************************************

    /**
     * Removes the listener so that it no longer receives notfications from this
     * notification center.
     * 
     * @param listener listener to be removed from this notification center
     */
    public void removeListener(final Object listener) {
        processKeyQueue();

        final Iterator i = new LinkedList(listenersMap.keySet()).iterator();
        while (i.hasNext()) {
            removeListener(listener, i.next());
        }
    }

    /**
     * Unregisters the listener as a listener for the specified kind of
     * notification.
     * 
     * If listener is null all listeners matching notificationName and object
     * are removed.
     * 
     * If notificationName is null the listener will be removed from all
     * notifications containing the object.
     * 
     * If the object is null then the listener will be removed from all
     * notifications matching notficationName.
     * 
     * @param listener listener to be removed
     * @param notificationName name of notifications or null for all
     * @param object notification source or null for all
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
     * Post a new notification with notificationName and object. The object is
     * typically the object posting the notification. The object may be null.
     * 
     * @param notificationName name of notification to post
     * @param object source of the notification, null signifies unknown
     */
    public void postNotification(final String notificationName, final Object object) {
        postNotification(notificationName, object, null);
    }

    /**
     * Creates a notification with the name notificationName, associates it with
     * the object, and posts it to this notification center. The object is
     * typically the object posting the notification. It may be null.
     * 
     * @param notificationName name of notification being posted
     * @param object source of the notification, may be null
     * @param properties properties associated with the notification
     */
    public void postNotification(final String notificationName, final Object object, final Map properties) {
        postNotification(new PNotification(notificationName, object, properties));
    }

    /**
     * Post the notification to this notification center. Most often clients
     * will instead use one of this classes convenience postNotifcations
     * methods.
     * 
     * @param notification notification to be dispatched to appropriate
     *            listeners
     */
    public void postNotification(final PNotification notification) {
        final List mergedListeners = new LinkedList();

        final Object name = notification.getName();
        final Object object = notification.getObject();

        if (name != null && object != null) {
            fillWithMatchingListeners(name, object, mergedListeners);
            fillWithMatchingListeners(null, object, mergedListeners);
            fillWithMatchingListeners(name, null, mergedListeners);
        }
        else if (name != null) {
            fillWithMatchingListeners(name, null, mergedListeners);
        }
        else if (object != null) {
            fillWithMatchingListeners(null, object, mergedListeners);
        }

        fillWithMatchingListeners(null, null, mergedListeners);

        dispatchNotifications(notification, mergedListeners);
    }

    /**
     * Adds all listeners that are registered to receive notifications to the
     * end of the list provided.
     * 
     * @param notificationName name of the notification being emitted
     * @param object source of the notification
     * @param listeners list to append listeners to
     */
    private void fillWithMatchingListeners(final Object notificationName, final Object object, final List listeners) {
        final Object key = new NotificationKey(nullify(notificationName), nullify(object));
        final List globalListeners = (List) listenersMap.get(key);
        if (globalListeners != null) {
            listeners.addAll(globalListeners);
        }
    }

    private void dispatchNotifications(final PNotification notification, final List listeners) {
        NotificationTarget listener;
        final Iterator listenerIterator = listeners.iterator();

        while (listenerIterator.hasNext()) {
            listener = (NotificationTarget) listenerIterator.next();
            if (listener.get() == null) {
                listenerIterator.remove();
            }
            else {
                notifyListener(notification, listener);
            }
        }
    }

    private void notifyListener(final PNotification notification, final NotificationTarget listener) {
        try {
            Object[] objects = new Object[1];
            objects[0] = notification;
            listener.getMethod().invoke(listener.get(), objects);
        }
        catch (final IllegalAccessException e) {
            throw new RuntimeException("Impossible Situation: invoking inaccessible method on listener", e);
        }
        catch (final InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a list of keys with the given name and object.
     * 
     * @param name name of key
     * @param object key associated with the object
     * 
     * @return list of matching keys
     */
    private List matchingKeys(final String name, final Object object) {
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

    /**
     * Removes the given listener from receiving notifications with the given
     * key.
     * 
     * @param listener the listener being unregistered
     * @param key the key that identifies the listener
     */
    private void removeListener(final Object listener, final Object key) {
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

    /**
     * Iterates over available keys in the key queue and removes the queue from
     * the listener map.
     */
    private void processKeyQueue() {
        NotificationKey key;
        while ((key = (NotificationKey) keyQueue.poll()) != null) {
            listenersMap.remove(key);
        }
    }

    /**
     * Represents a notification type from a particular object.
     */
    private static class NotificationKey extends WeakReference {
        private final Object name;
        private final int hashCode;

        /**
         * Creates a notification key with the provided name associated to the
         * object given.
         * 
         * @param name name of notification
         * @param object associated object
         */
        public NotificationKey(final Object name, final Object object) {
            super(object);
            this.name = name;
            hashCode = name.hashCode() + object.hashCode();
        }

        /**
         * Creates a notification key with the provided name associated with the
         * provided object.
         * 
         * @param name name of notification
         * @param object associated object
         * @param queue ReferenceQueue in which this NotificationKey will be
         *            appended once it has been cleared to be garbage collected
         */
        public NotificationKey(final Object name, final Object object, final ReferenceQueue queue) {
            super(object, queue);
            this.name = name;
            hashCode = name.hashCode() + object.hashCode();
        }

        /**
         * Returns name of notification this key represents.
         * 
         * @return name of notification
         */
        public Object name() {
            return name;
        }

        /** {@inheritDoc} */
        public int hashCode() {
            return hashCode;
        }

        /**
         * Two keys are equal if they have the same name and are associated with
         * the same object and conform to all other equals rules.
         * 
         * @param anObject object being tested for equivalence to this
         *            NotificationKey
         * 
         * @return true if this object is logically equivalent to the one passed
         *         in
         */
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

        /**
         * Returns a nice string representation of this notification key.
         * 
         * @return string representation of this notification key
         */
        public String toString() {
            return "[CompoundKey:" + name() + ":" + get() + "]";
        }
    }

    /**
     * A NotificationTarget is a method on a particular object that can be
     * invoked.
     */
    private static class NotificationTarget extends WeakReference {
        /** Cached hashcode value computed at construction time. */
        protected int hashCode;

        /** Method to be invoked on the object. */
        protected Method method;

        /**
         * Creates a notification target representing the method on the
         * particular object provided.
         * 
         * @param object object on which method can be invoked
         * @param method method to be invoked
         */
        public NotificationTarget(final Object object, final Method method) {
            super(object);
            hashCode = object.hashCode() + method.hashCode();
            this.method = method;
        }

        /**
         * Returns the method that will be invoked on the listener object.
         * 
         * @return method to be invoked with notification is to be dispatched
         */
        public Method getMethod() {
            return method;
        }

        /**
         * Returns hash code for this notification target.
         * 
         * @return hash code
         */
        public int hashCode() {
            return hashCode;
        }

        /**
         * Returns true if this object is logically equivalent to the one passed
         * in. For this to happen they must have the same method and object.
         * 
         * @param object object being tested for logical equivalency to this one
         * 
         * @return true if logically equivalent
         */
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

        /**
         * Returns a string representation of this NotificationTarget for
         * debugging purposes.
         * 
         * @return string representation
         */
        public String toString() {
            return "[CompoundValue:" + get() + ":" + getMethod().getName() + "]";
        }
    }
}
