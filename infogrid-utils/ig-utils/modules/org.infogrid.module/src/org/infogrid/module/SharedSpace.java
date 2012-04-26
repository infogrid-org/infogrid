//
// This file is part of InfoGrid(tm). You may not use this file except in
// compliance with the InfoGrid license. The InfoGrid license and important
// disclaimers are contained in the file LICENSE.InfoGrid.txt that you should
// have received with InfoGrid. If you have not received LICENSE.InfoGrid.txt
// or you do not consent to all aspects of the license and the disclaimers,
// no license is granted; do not use this file.
//
// For more information about InfoGrid go to http://infogrid.org/
//
// Copyright 1998-2012 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.module;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;

/**
 * A singleton that can be used for multiple apps to share information across application boundaries.
 */
public abstract class SharedSpace
{
    /**
     * This is a collection of statics only, so there's no constructor.
     */
    private SharedSpace() {}

    /**
     * Obtain a named object from the SharedSpace. If the named object does not exist yet,
     * invoke the factory to create it.
     *
     * @param caller the Object that checks out the Object
     * @param name the name of the shared object
     * @param factory the factory to create the shared object if it does not exist yet, if any
     * @param lastReturned callback to invoke when the last caller has returned the shared object, if any
     * @return the found object, or the created object
     */
    public static synchronized Object checkoutObject(
            Object       caller,
            String       name,
            Factory      factory,
            LastReturned lastReturned )
    {
        Reference<Object> ref = theObjects.get( name );
        Object            ret = ( ref != null ) ? ref.get() : null;

        checkQueue();

        if( ret == null && factory != null ) {
            try {
                ret = factory.create();
            } catch( Throwable t ) {
                throw new RuntimeException( SharedSpace.class.getName() + ": Failed to check out (create) object named " + name, t );
            }
            if( ret != null ) { // factory may return null
                theObjects.put( name, new NamedReference( name, ret ));
            }
        }
        if( ret != null ) {
            HashMap<Object,LastReturned> callers = theNameToCallersMap.get( name );
            if( callers == null ) {
                callers = new HashMap<Object,LastReturned>();
                theNameToCallersMap.put( name, callers );
            }
            if( callers.put( caller, lastReturned ) != null ) {
                throw new IllegalStateException( "Caller " + caller + " has already checked out object named " + name + " from SharedSpace" );
            }
        }
        return ret;
    }

    /**
     * Obtain a named object from the SharedSpace. If the named object does not exist, return null.
     *
     * @param name the name of the shared object
     * @return the found object
     */
    public static synchronized Object findObject(
            String name )
    {
        checkQueue();

        Reference<Object> ref = theObjects.get( name );
        Object            ret = ( ref != null ) ? ref.get() : null;

        return ret;
    }

    /**
     * Opposite operation to checkoutObject.
     *
     * @param caller the object that releases the object. Must be the same as given during checkoutObject
     * @param name the name of the shared object
     * @return returns the named object, but only if this is the last caller returning the object
     */
    public static synchronized Object returnObject(
            Object caller,
            String name )
    {
        checkQueue();

        HashMap<Object,LastReturned> callers = theNameToCallersMap.get( name );
        if( !callers.containsKey( caller )) {
            throw new IllegalStateException( "Caller " + caller + " cannot return object named " + name + ": not checked out" );
        }
        LastReturned callback = callers.remove( caller );

        Object ret;
        if( callers.isEmpty() ) {
            Reference<Object> ref = theObjects.remove( name );
            theNameToCallersMap.remove( name );
            ret = ( ref != null ) ? ref.get() : null;
            
            if( callback != null ) {
                callback.lastReturned( name, ret );
            }

        } else {
            ret = null;
        }

        return ret;
    }

    /**
     * Internal helper to clean up entries in the SharedSpace that have been garbage-collected.
     */
    private static void checkQueue()
    {
        while( true ) {
            NamedReference current = (NamedReference) theQueue.poll();
            if( current == null ) {
                return;
            }
            String name = current.getName();

            theObjects.remove( name );
            HashMap<Object,LastReturned> callers = theNameToCallersMap.remove( name );
            if( callers != null ) {
                for( LastReturned callback : callers.values() ) {
                    if( callback != null ) {
                        callback.lastReturned( name, current.get() );
                        return; // just execute one of them
                    }
                }
            }
        }
    }

    /**
     * The internal map of names to shared objects.
     */
    private static final HashMap<String,Reference<Object>> theObjects = new HashMap<String,Reference<Object>>();

    /**
     * The ReferenceQueue into which garbage-collected References have been inserted.
     */
    private static final ReferenceQueue<Object> theQueue = new ReferenceQueue<Object>();

    /**
     * Maps the names of the checked-out objects to the callers that checked them out.
     */
    private static final HashMap<String,HashMap<Object,LastReturned>> theNameToCallersMap = new HashMap<String,HashMap<Object,LastReturned>>();

    /**
     * Helper class that allows to remove named entries from the map whose values have expired.
     */
    private static class NamedReference
            extends
                WeakReference<Object>
    {
        /**
         * Constructor.
         *
         * @param name name of the shared object
         * @param obj the shared object
         */
        public NamedReference(
                String name,
                Object obj )
        {
            super( obj, theQueue );

            theName = name;
        }

        /**
         * Obtain the name of the shared object.
         *
         * @return the name
         */
        public String getName()
        {
            return theName;
        }

        /**
         * The name of the shared object.
         */
        protected String theName;
    }

    /**
     * This interface must be implemented by users of SharedSpace that wish to create shared objects on demand if
     * they don't exist yet.
     */
    public static interface Factory
    {
        /**
         * Factory method.
         *
         * @return the created shared object
         * @throws Throwable thrown if a problem occurred
         */
        public Object create()
                throws
                    Throwable;
    }

    /**
     * This interface must be implemented by users of SharedSpace that wish to obtain a callback when a shared
     * object is returned by the last caller. This enables cleanup operations, for example.
     */
    public static interface LastReturned
    {
        /**
         * Invoked when a shared object has been returned by the last caller.
         *
         * @param name the name of the shared object
         * @param sharedObject the shared object
         */
        public void lastReturned(
                String name,
                Object sharedObject );
    }
}
