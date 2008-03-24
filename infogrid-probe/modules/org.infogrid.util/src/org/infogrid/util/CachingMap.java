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

import java.util.Map;

/**
 * Extends the Map interface to add some methods suitable for Maps that have a local
 * cache.
 */
public interface CachingMap<K,V>
    extends
        Map<K,V>
{
    /**
     * Clear the local cache.
     */
    public abstract void clearLocalCache();

    /**
     * Determine whether this CachingMap is persistent.
     *
     * @return true if it is persistent
     */
    public abstract boolean isPersistent();

    /**
     * Obtain a CursorIterator on the keys of this Map.
     *
     * @param keyArrayComponentType the class using which arrays of keys are allocated
     * @param valueArrayComponentType the class using which arrays of values are allocated
     * @return the CursorIterator
     */
    public abstract CursorIterator<K> keysIterator(
            Class<K> keyArrayComponentType,
            Class<V> valueArrayComponentType );

    /**
     * Obtain a CursorIterator on the values of this Map.
     *
     * @param keyArrayComponentType the class using which arrays of keys are allocated
     * @param valueArrayComponentType the class using which arrays of values are allocated
     * @return the CursorIterator
     */
    public abstract CursorIterator<V> valuesIterator(
            Class<K> keyArrayComponentType,
            Class<V> valueArrayComponentType );

    /**
     * Invoked only by objects held in this CachingMap, this enables
     * the held objects to indicate to the CachingMap that they have been updated.
     * Depending on the implementation of the CachingMap, that may cause the
     * CachingMap to write changes to disk, for example.
     *
     * @param key the key
     * @param value the value
     */
    public abstract void valueUpdated(
            K key,
            V value );

    /**
      * Add a listener.
      *
      * @param newListener the to-be-added listener
      * @see #removeCachingMapListener
      */
    public void addDirectCachingMapListener(
            CachingMapListener newListener );

    /**
     * Remove a listener.
     * 
     * @param oldListener the to-be-removed listener
     * @see #addCachingMapListener
     */
    public void removeCachingMapListener(
            CachingMapListener oldListener );

    /**
     * Default capacity.
     */
    public static final int DEFAULT_INITIAL_CAPACITY = 16;
}
