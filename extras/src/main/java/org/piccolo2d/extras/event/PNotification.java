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
 * This class PNotification center is derived from the class
 * NSNotification from:
 * 
 * Wotonomy: OpenStep design patterns for pure Java
 * applications. Copyright (C) 2000 Blacksmith, Inc.
 */
package org.piccolo2d.extras.event;

import java.util.Map;

/**
 * <b>PNotification</b> objects encapsulate information so that it can be
 * broadcast to other objects by a PNotificationCenter. A PNotification contains
 * a name, an object, and an optional properties map. The name is a tag
 * identifying the notification. The object is any object that the poster of the
 * notification wants to send to observers of that notification (typically, it
 * is the object that posted the notification). The properties map stores other
 * related objects, if any.
 * <p>
 * You don't usually create your own notifications directly. The
 * PNotificationCenter method postNotification() allow you to conveniently post
 * a notification without creating it first.
 * </p>
 * 
 * @author Jesse Grosjean
 */
public class PNotification {
    /** Name of the notification. */
    protected String name;
    /** The Object associated with this notification. */
    protected Object source;
    /** A free form map of properties to attach to this notification. */
    protected Map properties;

    /**
     * Creates a notification.
     * 
     * @param name Arbitrary name of the notification
     * @param source object associated with this notification
     * @param properties free form map of information about the notification
     */
    public PNotification(final String name, final Object source, final Map properties) {
        this.name = name;
        this.source = source;
        this.properties = properties;
    }

    /**
     * Return the name of the notification. This is the same as the name used to
     * register with the notification center.
     * 
     * @return name of notification
     */
    public String getName() {
        return name;
    }

    /**
     * Return the object associated with this notification. This is most often
     * the same object that posted the notification. It may be null.
     * 
     * @return object associated with this notification
     */
    public Object getObject() {
        return source;
    }

    /**
     * Return a property associated with the notification, or null if not found.
     * 
     * @param key key used for looking up the property
     * @return value associated with the key or null if not found
     */
    public Object getProperty(final Object key) {
        if (properties != null) {
            return properties.get(key);
        }
        return null;
    }
}
