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
// Copyright 1998-2008 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.util;

import java.util.EventObject;

/**
 * Event sent by SmartFactories. Specific subclasses are defined as inner classes.
 */
public abstract class SmartFactoryEvent<K,V,A>
        extends
            EventObject
{
    /**
     * Constructor.
     *
     * @param factory the sending SmartFactory
     * @param key the key for the affected value
     */
    protected SmartFactoryEvent(
            SmartFactory<K,V,A> factory,
            K                   key )
    {
        super( factory );
        
        theKey = key;
    }
    
    /**
     * Obtain the sending SmartFactory.
     *
     * @return the sending SmartFactory
     */
    @SuppressWarnings(value={"unchecked"})
    public SmartFactory<K,V,A> getSource()
    {
        return (SmartFactory<K,V,A>) super.getSource();
    }

    /**
     * Obtain the key of the value that was affected.
     *
     * @return the key
     */
    public K getKey()
    {
        return theKey;
    }
    
    /**
     * The key.
     */
    protected K theKey;
    
    /**
     * This event indicates that a value was added to the values held by the SmartFactory.
     */
    public static class Added<K,V,A>
            extends
                SmartFactoryEvent<K,V,A>
    {
        /**
         * Constructor.
         *
         * @param factory the sending SmartFactory
         * @param key the key for the affected value
         * @param value the affected value
         */
        public Added(
                SmartFactory<K,V,A> factory,
                K                   key,
                V                   value,
                A                   argument )
        {
            super( factory, key );
            
            theValue = value;
        }

        /**
         * Obtain the value that was affected.
         *
         * @return the value
         */
        public V getValue()
        {
            return theValue;
        }

        /**
         * The value.
         */
        protected V theValue;
    }
    
    /**
     * This event indicates that a value was removed from the values held by the SmartFactory.
     */
    public static class Removed<K,V,A>
            extends
                SmartFactoryEvent<K,V,A>
    {
        /**
         * Constructor.
         *
         * @param factory the sending SmartFactory
         * @param key the key for the affected value
         */
        public Removed(
                SmartFactory<K,V,A> factory,
                K                   key )
        {
            super( factory, key );
        }        
    }
}
