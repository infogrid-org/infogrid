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
// Copyright 1998-2015 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.store.sql;

import java.util.NoSuchElementException;
import org.infogrid.store.AbstractKeyBasedStoreCursor;
import org.infogrid.store.StoreValue;
import org.infogrid.util.logging.Log;

/**
 * Iterator implementation for the StoreValues in the AbstractSqlStore.
 * FIXME: This currently does not deal very well with moving to the very beginning or the very end of the Store.
 */
class SqlStoreIterator
        extends
            AbstractKeyBasedStoreCursor
{
    private static final Log log = Log.getLogInstance( SqlStoreIterator.class ); // our own, private logger

    /**
     * Constructor. Start at a defined place.
     *
     * @param store the AbstractSqlStore to iterate over
     * @param position the key of the current position
     * @param pattern the pattern to filter by
     */
    protected SqlStoreIterator(
            AbstractSqlStore store,
            String           position,
            String           pattern )
    {
        super( store, position, pattern );
    }

    /**
     * Find the next n StoreValues, including the StoreValue for key. This method
     * will return fewer values if only fewer values could be found.
     *
     * @param key key for the first StoreValue
     * @param n the number of StoreValues to find
     * @param pattern the pattern to filter by
     * @return the found StoreValues
     */
    @Override
    protected StoreValue [] findNextIncluding(
            String key,
            int    n,
            String pattern )
    {
        StoreValue [] ret = ((AbstractSqlStore)theStore).findNextIncluding( key, n, pattern );
        return ret;
    }

    /**
     * Find the next n keys, including key. This method
     * will return fewer values if only fewer values could be found.
     *
     * @param key the first key
     * @param n the number of keys to find
     * @param pattern the pattern to filter by
     * @return the found keys
     */
    @Override
    protected String [] findNextKeyIncluding(
            String key,
            int    n,
            String pattern )
    {
        String [] ret = ((AbstractSqlStore)theStore).findNextKeyIncluding( key, n, pattern );
        return ret;
    }

    /**
     * Find the previous n StoreValues, excluding the StoreValue for key. This method
     * will return fewer values if only fewer values could be found.
     *
     * @param key key for the first StoreValue NOT to be returned
     * @param n the number of StoreValues to find
     * @param pattern the pattern to filter by
     * @return the found StoreValues
     */
    @Override
    protected StoreValue [] findPreviousExcluding(
            String key,
            int    n,
            String pattern )
    {
        StoreValue [] ret = ((AbstractSqlStore)theStore).findPreviousExcluding( key, n, pattern );
        return ret;
    }

    /**
     * Find the previous n keys, excluding the key for key. This method
     * will return fewer values if only fewer values could be found.
     *
     * @param key the first key NOT to be returned
     * @param n the number of keys to find
     * @param pattern the pattern to filter by
     * @return the found keys
     */
    @Override
    protected String [] findPreviousKeyExcluding(
            String key,
            int    n,
            String pattern )
    {
        String [] ret = ((AbstractSqlStore)theStore).findPreviousKeyExcluding( key, n, pattern );
        return ret;
    }

    /**
     * Count the number of elements following and including the one with the key.
     *
     * @param key the key
     * @param pattern the pattern to filter by
     * @return the number of elements
     */
    @Override
    protected int hasNextIncluding(
            String key,
            String pattern )
    {
        int ret = ((AbstractSqlStore)theStore).hasNextIncluding( key, pattern );
        return ret;
    }

    /**
     * Count the number of elements preceding and excluding the one with the key.
     *
     * @param key the key
     * @param pattern the pattern to filter by
     * @return the number of elements
     */
    @Override
    protected int hasPreviousExcluding(
            String key,
            String pattern )
    {
        int ret = ((AbstractSqlStore)theStore).hasPreviousExcluding( key, pattern );
        return ret;
    }

    /**
     * Find the key N elements up or down from the current key.
     *
     * @param key the current key
     * @param delta the number of elements up (positive) or down (negative)
     * @param pattern the pattern to filter by
     * @return the found key, or null
     * @throws NoSuchElementException thrown if the delta went beyond the "after last" or "before first" element
     */
    @Override
    protected String findKeyAt(
            String key,
            int    delta,
            String pattern )
        throws
            NoSuchElementException
    {
        String ret = ((AbstractSqlStore)theStore).findKeyAt( key, delta, pattern );
        return ret;
    }

    /**
     * Helper method to determine the number of elements between two keys.
     *
     * @param from the start key
     * @param to the end key
     * @param pattern the pattern to filter by
     * @return the distance
     */
    @Override
    protected int determineDistance(
            String from,
            String to,
            String pattern )
    {
        int ret = ((AbstractSqlStore)theStore).determineDistance( from, to, pattern );
        return ret;
    }

    /**
     * Determine the key at the very beginning.
     *
     * @return the key
     * @param pattern the pattern to filter by
     * @throws NoSuchElementException thrown if the Store is empty
     */
    @Override
    protected String getBeforeFirstPosition(
            String pattern )
        throws
            NoSuchElementException
    {
        String ret = ((AbstractSqlStore)theStore).findFirstKey( pattern );
        return ret;
    }

    /**
     * Determine the key at the very end.
     *
     * @return the key
     * @param pattern the pattern to filter by
     */
    @Override
    protected String getAfterLastPosition(
            String pattern )
    {
        return null;
    }

    /**
     * Clone this position.
     *
     * @return identical new instance
     */
    @Override
    public SqlStoreIterator createCopy()
    {
        return new SqlStoreIterator( (AbstractSqlStore) theStore, thePosition, thePattern );
    }
}
