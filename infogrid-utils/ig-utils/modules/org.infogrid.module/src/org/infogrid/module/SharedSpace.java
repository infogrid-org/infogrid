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
     * @param name the name of the shared object
     * @param factory the factory to create the shared object if it does not exist yet
     * @return the found object, or the created object
     */
    public static synchronized Object checkoutObject(
            String  name,
            Factory factory )
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
     * Remove a named object from the SharedSpace.
     *
     * @param name the name of the shared object
     * @return the released object, if it exists
     */
    public static synchronized Object releaseObject(
            String name )
    {
        checkQueue();

        Reference<Object> ref = theObjects.remove( name );
        Object            ret = ( ref != null ) ? ref.get() : null;

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
            theObjects.remove( current.getName() );
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
}
