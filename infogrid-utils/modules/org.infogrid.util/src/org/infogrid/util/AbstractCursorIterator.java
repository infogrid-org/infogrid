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

import java.util.*;

/**
 * Factors out common behaviors of CursorIterators.
 */
public abstract class AbstractCursorIterator<E>
        implements
            CursorIterator<E>
{
    /**
     * Constructor.
     */
    protected AbstractCursorIterator(
            Class<E> arrayComponentType )
    {
        theArrayComponentType = arrayComponentType;
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
     * <p>Obtain the next N elements. If fewer than N elements are available, return
     * as many elements are available in a shorter array.</p>
     * 
     * @return the next no more than N elements
     * @see #previous(int)
     */
    public E [] next(
            int  n )
    {
        E [] ret = ArrayHelper.createArray( theArrayComponentType, n );
        
        int count = 0;
        while( count < n && hasNext() ) {
            ret[ count++ ] = next();
        }
        if( count < ret.length ) {
            ret = ArrayHelper.copyIntoNewArray( ret, 0, count, theArrayComponentType );
        }
        return ret;
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
    public E [] previous(
            int  n )
    {
        E [] ret = ArrayHelper.createArray( theArrayComponentType, n );
        
        int count = 0;
        while( count < n && hasPrevious() ) {
            ret[ count++ ] = previous();
        }
        if( count < ret.length ) {
            ret = ArrayHelper.copyIntoNewArray( ret, 0, count, theArrayComponentType );
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
    public void moveBy(
            int n )
        throws
            NoSuchElementException
    {
        if( n > 0 ) {
            E [] temp = next( n );
            if( temp.length < n ) {
                throw new NoSuchElementException();
            }
        } else if( n < 0 ) {
            E [] temp = previous( -n );
            if( temp.length < -n ) {
                throw new NoSuchElementException();
            }
        } // else nothing
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
            E pos )
        throws
            NoSuchElementException
    {
        // by default, we have to search.

        CursorIterator<E> currentPosition = createCopy();

        int count = 0;
        while( hasNext() ) {
            E found = peekNext();
            if( pos.equals( found )) {
                return count;
            }
            ++count;
            next();
        }
        
        setPositionTo( currentPosition );

        count = 0;
        while( hasPrevious() ) {
            E found = previous();
            --count;
            if( pos.equals( found )) {
                return count;
            }
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
            E pos )
        throws
            NoSuchElementException
    {
        // by default, we have to search.

        CursorIterator<E> currentPosition = createCopy();

        int count = 0;
        while( hasNext() ) {
            E found = next();
            ++count;
            if( pos.equals( found )) {
                return count;
            }
        }
        
        setPositionTo( currentPosition );

        count = 0;
        while( hasPrevious() ) {
            E found = peekPrevious();
            if( pos.equals( found )) {
                return count;
            }
            --count;
            previous();
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
    public final E nextElement()
    {
        return next();
    }

    /**
     * We don't know how to remove.
     *
     * @throws UnsupportedOperationException always thrown
     */
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Obtain a CursorIterable instead of an Iterator.
     *
     * @return the CursorIterable
     */
    public CursorIterator<E> iterator()
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
    public final CursorIterator<E> getIterator()
    {
        return iterator();
    }

    /**
     * The array component type for returned values.
     */
    protected Class<E> theArrayComponentType;
}
