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

package org.infogrid.store.sql;

import org.infogrid.store.IterableStoreCursor;
import org.infogrid.store.StoreKeyDoesNotExistException;
import org.infogrid.store.StoreValue;

import org.infogrid.util.ArrayHelper;
import org.infogrid.util.CursorIterator;
import org.infogrid.util.logging.Log;

import java.util.NoSuchElementException;

/**
 * Iterator implementation for the StoreValues in the SqlStore.
 * FIXME: This currently does not deal very well with moving to the very beginning or the very end of the Store.
 */
class SqlStoreIterator
        implements
            IterableStoreCursor
{
    private static final Log log = Log.getLogInstance( SqlStoreIterator.class ); // our own, private logger

    /**
     * Constructor. Start at the beginning.
     *
     * @param store the SqlStore to iterate over
     */
    protected SqlStoreIterator(
            SqlStore store )
    {
        this( store, "" );
    }
    
    /**
     * Constructor. Start at a defined place
     *
     * @param store the SqlStore to iterate over
     * @param position the key of the current position
     */
    protected SqlStoreIterator(
            SqlStore   store,
            String     position )
    {
        theStore    = store;
        thePosition = position;
    }

    /**
     * Obtain the next element, without iterating forward.
     *
     * @return the next element
     * @exception NoSuchElementException iteration has no current element (e.g. because the end of the iteration was reached)
     */
    public StoreValue peekNext()
    {
        StoreValue [] found = theStore.findNextIncluding( thePosition, 1 );
        
        if( found.length == 1 ) {
            return found[0];
        } else {
            throw new NoSuchElementException();
        }
    }
    
    /**
     * Obtain the previous element, without iterating backwards.
     *
     * @return the previous element
     * @exception NoSuchElementException iteration has no current element (e.g. because the end of the iteration was reached)
     */
    public StoreValue peekPrevious()
    {
        StoreValue [] found = theStore.findPreviousExcluding( thePosition, 1 );
        
        if( found.length == 1 ) {
            return found[0];
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements in the forward direction.
     *
     * @return <tt>true</tt> if the iterator has more elements in the forward direction.
     * @see #hasPrevious()
     * @see #hasPrevious(int)
     * @see #hasNext(int)
     */
    public boolean hasNext()
    {
        return hasNext( 1 );
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements in the backwards direction.
     *
     * @return <tt>true</tt> if the iterator has more elements in the backwards direction.
     * @see #hasNext()
     * @see #hasPrevious(int)
     * @see #hasNext(int)
     */
    public boolean hasPrevious()
    {
        return hasPrevious( 1 );
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
        int found = theStore.hasNextIncluding( thePosition );
        if( found >= n ) {
            return true;
        } else {
            return false;
        }
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
        int found = theStore.hasPreviousExcluding( thePosition );
        if( found >= n ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @exception NoSuchElementException iteration has no more elements.
     */
    public StoreValue next()
    {
        StoreValue [] found = next( 1 );
        if( found.length == 1 ) {
            return found[0];
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * <p>Obtain the next N elements. If fewer than N elements are available, return
     * as many elements are available in a shorter array.</p>
     * 
     * @return the next no more than N elements
     * @see #previous(int)
     */
    public StoreValue [] next(
            int n )
    {
        StoreValue [] found = theStore.findNextIncluding( thePosition, n+1 );
        if( found.length == n+1 ) {
            thePosition = found[found.length-1].getKey();
            found = ArrayHelper.copyIntoNewArray( found, 0, n, StoreValue.class );
        } else {
            thePosition = null;
        }
        return found;
    }

    /**
     * Returns the previous element in the iteration.
     *
     * @return the previous element in the iteration.
     * @see #next()
     */
    public StoreValue previous()
    {
        StoreValue [] found = previous( 1 );
        if( found.length == 1 ) {
            return found[0];
        } else {
            throw new NoSuchElementException();
        }
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
    public StoreValue [] previous(
            int n )
    {
        StoreValue [] found = theStore.findPreviousExcluding( thePosition, n );
        if( found.length > 0 ) {
            thePosition = found[found.length-1].getKey();
        }
        return found;
    }

    /**
     * Move the cursor by N positions. Positive numbers indicate forward movemement;
     * negative numbers indicate backwards movement.
     * Throws NoSuchElementException if the position does not exist.
     *
     * @param n the number of positions to move
     * @exception NoSuchElementException
     */
    public void moveBy(
            int n )
        throws
            NoSuchElementException
    {
        if( n == 0 ) {
            return;
        }
        String newPosition = theStore.findKeyAt( thePosition, n );
        if( newPosition != null ) {
            thePosition = newPosition;
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * Move the cursor to just before this element, i.e. return this element when {@link #next next} is invoked
     * right afterwards.
     *
     * @param pos the element to move the cursor to
     * @return the number of steps that were taken to move. Positive number means forward, negative backward
     * @exception NoSuchElementException thrown if this element is not actually part of the collection to iterate over
     */
    public int moveToBefore(
            StoreValue pos )
        throws
            NoSuchElementException
    {
        return moveToBefore( pos.getKey() );
    }

    /**
     * Move the cursor to just before this element, i.e. return this element when {@link #previous previous} is invoked
     * right afterwards.
     *
     * @param pos the element to move the cursor to
     * @return the number of steps that were taken to move. Positive number means forward, negative backward
     * @exception NoSuchElementException thrown if this element is not actually part of the collection to iterate over
     */
    public int moveToAfter(
            StoreValue pos )
        throws
            NoSuchElementException
    {
        return moveToAfter( pos.getKey() );
    }

    /**
     * Move the cursor to this element, i.e. return this element when {@link #next next} is invoked
     * right afterwards.
     *
     * @param pos the element to move the cursor to
     * @return the number of steps that were taken to move. Positive number means forward, negative backward
     * @exception NoSuchElementException thrown if this element is not actually part of the collection to iterate over
     */
    public int moveToBefore(
            String key )
        throws
            NoSuchElementException
    {
        // FIXME this does not look right
        int distance = theStore.determineDistance( thePosition, key );
        if( distance >= 0 ) {
            return distance;
        }
        distance = theStore.determineDistance( key, thePosition );
        if( distance >= 0 ) {
            return -distance;
        }
        throw new NoSuchElementException();
    }

    /**
     * Move the cursor to this element, i.e. return this element when {@link #previous previous} is invoked
     * right afterwards.
     *
     * @param pos the element to move the cursor to
     * @return the number of steps that were taken to move. Positive number means forward, negative backward
     * @exception NoSuchElementException thrown if this element is not actually part of the collection to iterate over
     */
    public int moveToAfter(
            String key )
        throws
            NoSuchElementException
    {
        // FIXME this does not look right
        int distance = theStore.determineDistance( thePosition, key );
        if( distance >= 0 ) {
            return distance;
        }
        distance = theStore.determineDistance( key, thePosition );
        if( distance >= 0 ) {
            return -distance;
        }
        throw new NoSuchElementException();
    }

    /**
      * Do we have more elements?
      *
      * @return true if we have more elements
      */
    public final boolean hasMoreElements()
    {
        return hasNext();
    }

    /**
      * Return next element and iterate.
      *
      * @return the next element
      */
    public final StoreValue nextElement()
    {
        return next();
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
    public void remove()
    {
        try {
            theStore.delete( thePosition );
        
            // we don't need to adjust the position
        } catch( StoreKeyDoesNotExistException ex ) {
            log.error( ex );
        } catch( SqlStoreIOException ex ) {
            log.error( ex );
        }
    }

    /**
     * Clone this position.
     *
     * @return identical new instance
     */
    public SqlStoreIterator createCopy()
    {
        return new SqlStoreIterator( theStore, thePosition );
    }
    
    /**
     * Set this CursorIterator to the position represented by the provided CursorIterator.
     *
     * @param position the position to set this CursorIterator to
     * @throws IllegalArgumentException thrown if the provided CursorIterator did not work on the same CursorIterable,
     *         or the implementations were incompatible.
     */
    public void setPositionTo(
            CursorIterator<StoreValue> position )
        throws
            IllegalArgumentException
    {
        if( !( position instanceof SqlStoreIterator )) {
            throw new IllegalArgumentException( "Wrong type of CursorIterator: " + position );
        }
        SqlStoreIterator realPosition = (SqlStoreIterator) position;

        if( theStore != realPosition.theStore ) {
            throw new IllegalArgumentException( "Not the same instance of Store" );
        }
        
        thePosition = realPosition.thePosition;
    }
    
    /**
     * Obtain a CursorIterable instead of an Iterator.
     *
     * @return the CursorIterable
     */
    public CursorIterator<StoreValue> iterator()
    {
        return this;
    }

    /**
     * Obtain a CursorIterable. This performs the exact same operation as
     * @link #iterator iterator}, but is friendlier towards JSPs and other software
     * that likes to use JavaBeans conventions.
     *
     * @return the CursorIterable
     */
    public final CursorIterator<StoreValue> getIterator()
    {
        return iterator();
    }

    /**
     * The SqlStore to iterate over.
     */
    protected SqlStore theStore;

    /**
     * The key for the current position.
     */
    protected String thePosition;
}
