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

package org.infogrid.store.util;

import org.infogrid.store.IterableStoreCursor;
import org.infogrid.store.StoreEntryMapper;
import org.infogrid.util.AbstractCursorIterator;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.CursorIterator;

import java.util.NoSuchElementException;

/**
 * Iterates over the values in a StoreBackedMap
 */
public class StoreBackedMapValuesIterator<K,V>
        extends
            AbstractCursorIterator<V>
{
    /**
     * Constructor.
     *
     * @param delegate the underling Iterator over the StoreValues in the Store
     * @param cache the in-memory cache in the StoreMeshBase
     * @param mapper the mapper to use
     */
    public StoreBackedMapValuesIterator(
            IterableStoreCursor   delegate,
            StoreBackedMap<K,V>   cache,
            StoreEntryMapper<K,V> mapper,
            Class<K>              keysArrayComponentClass,
            Class<V>              valuesArrayComponentClass )
    {
        super( valuesArrayComponentClass );
        theKeysIterator = new StoreBackedMapKeysIterator<K,V>( delegate, cache, mapper, keysArrayComponentClass );
    }

    /**
     * Private copy-constructor.
     */
    protected StoreBackedMapValuesIterator(
            StoreBackedMapValuesIterator<K,V> old )
    {
        super( old.theArrayComponentType );

        theKeysIterator        = old.theKeysIterator.createCopy();
    }

    /**
     * Obtain the next element, without iterating forward.
     *
     * @return the next element
     * @exception NoSuchElementException iteration has no current element (e.g. because the end of the iteration was reached)
     */
    public V peekNext()
    {
        K key = theKeysIterator.peekNext();
        V ret = theKeysIterator.getMap().get( key );

        return ret;
    }
    
    /**
     * Obtain the previous element, without iterating backwards.
     *
     * @return the previous element
     * @exception NoSuchElementException iteration has no current element (e.g. because the end of the iteration was reached)
     */
    public V peekPrevious()
    {
        K key = theKeysIterator.peekPrevious();
        V ret = theKeysIterator.getMap().get( key );

        return ret;
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements in the forward direction.
     *
     * @return <tt>true</tt> if the iterator has more elements in the forward direction.
     * @see #hasPrevious()
     * @see #hasPrevious(int)
     * @see #hasNext(int)
     */
    @Override
    public boolean hasNext()
    {
        return theKeysIterator.hasNext();
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements in the backwards direction.
     *
     * @return <tt>true</tt> if the iterator has more elements in the backwards direction.
     * @see #hasNext()
     * @see #hasPrevious(int)
     * @see #hasNext(int)
     */
    @Override
    public boolean hasPrevious()
    {
        return theKeysIterator.hasPrevious();
    }
    
    /**
     * Returns <tt>true</tt> if the iteration has at least N more elements in the forward direction.
     *
     * @return <tt>true</tt> if the iterator has at least N more elements in the forward direction.
     *
     * @param n the number of elements for which to check
     * @return true if there at least N next elements
     * @see #hasNext()
     * @see #hasPrevious()
     * @see #hasPrevious(int)
     */
    public boolean hasNext(
            int n )
    {
        return theKeysIterator.hasNext( n );
    }

    /**
     * Returns <tt>true</tt> if the iteration has at least N more elements in the backwards direction.
     *
     * @return <tt>true</tt> if the iterator has at least N more elements in the backwards direction.
     *
     * @param n the number of elements for which to check
     * @return true if there at least N previous elements
     * @see #hasNext()
     * @see #hasPrevious()
     * @see #hasNext(int)
     */
    public boolean hasPrevious(
            int n )
    {
        return theKeysIterator.hasPrevious( n );
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @exception NoSuchElementException iteration has no more elements.
     */
    public V next()
    {
        K key = theKeysIterator.next();
        V ret = theKeysIterator.getMap().get( key );

        return ret;
    }

    /**
     * <p>Obtain the next N elements. If fewer than N elements are available, return
     * as many elements are available in a shorter array.</p>
     * 
     * @return the next no more than N elements
     * @see #previous(int)
     */
    @Override
    public V [] next(
            int n )
    {
        K [] keys = theKeysIterator.next( n );
        V [] ret = ArrayHelper.createArray( theArrayComponentType, keys.length );

        StoreBackedMap<K,V> map = theKeysIterator.getMap();
        
        for( int i=0 ; i<keys.length ; ++i ) {
            ret[i] = map.get( keys[i] );
        }
        return ret;
    }

    /**
     * Returns the previous element in the iteration.
     *
     * @return the previous element in the iteration.
     * @see #next()
     */
    public V previous()
    {
        K key = theKeysIterator.previous();
        V ret = theKeysIterator.getMap().get( key );

        return ret;
    }

    /**
     * <p>Obtain the previous N elements. If fewer than N elements are available, return
     * as many elements are available in a shorter array.</p>
     * 
     * <p>Note that the elements
     * will be ordered in the opposite direction as you might expect: they are
     * returned in the sequence in which the CursorIterator visits them, not in the
     * sequence in which the underlying Iterable stores them.</p>
     *
     * @return the previous no more than N elements
     * @see #next(int)
     */
    @Override
    public V [] previous(
            int  n )
    {
        K [] keys = theKeysIterator.previous( n );
        V [] ret = ArrayHelper.createArray( theArrayComponentType, keys.length );

        StoreBackedMap<K,V> map = theKeysIterator.getMap();
        
        for( int i=0 ; i<keys.length ; ++i ) {
            ret[i] = map.get( keys[i] );
        }
        return ret;
    }

    /**
     * Move the cursor by N positions. Positive numbers indicate forward movemement;
     * negative numbers indicate backwards movement.
     * Throws NoSuchElementException if the position does not exist.
     *
     * @param n the number of positions to move
     * @exception NoSuchElementException
     */
    @Override
    public void moveBy(
            int n )
        throws
            NoSuchElementException
    {
        theKeysIterator.moveBy( n );
    }

    /**
     * 
     * Removes from the underlying collection the last element returned by the
     * iterator (optional operation). This is the same as the current element.
     *
     * @exception UnsupportedOperationException if the <tt>remove</tt>
     *		  operation is not supported by this Iterator.
     
     * @exception IllegalStateException if the <tt>next</tt> method has not
     *		  yet been called, or the <tt>remove</tt> method has already
     *		  been called after the last call to the <tt>next</tt>
     *		  method.
     */
    @Override
    public void remove()
    {
        theKeysIterator.remove();
    }
    
    /**
     * Clone this position.
     *
     * @return identical new instance
     */
    public StoreBackedMapValuesIterator<K,V> createCopy()
    {
        StoreBackedMapValuesIterator<K,V> ret = new StoreBackedMapValuesIterator<K,V>( this );

        return ret;
    }
    
    /**
     * Set this CursorIterator to the position represented by the provided CursorIterator.
     *
     * @param position the position to set this CursorIterator to
     * @throws IllegalArgumentException thrown if the provided CursorIterator did not work on the same CursorIterable,
     *         or the implementations were incompatible.
     */
    public void setPositionTo(
            CursorIterator<V> position )
        throws
            IllegalArgumentException
    {
        throw new UnsupportedOperationException(); // there is a funny typecast warning in the commented-out code which I
        // don't have time to investigate right now. FIXME.
//        if( !( position instanceof StoreBackedMapValuesIterator )) {
//            throw new IllegalArgumentException( "Wrong type of CursorIterator: " + position );
//        }
//        StoreBackedMapValuesIterator<K,V> realPosition = (StoreBackedMapValuesIterator<K,V>) position;
//
//        theKeysIterator.setPositionTo( realPosition.theKeysIterator ); // this may throw
    }
    
    /**
     * The underlying keys iterator.
     */
    protected StoreBackedMapKeysIterator<K,V> theKeysIterator;
}
