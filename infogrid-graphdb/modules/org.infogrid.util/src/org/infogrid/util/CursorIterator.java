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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A more fully-featured Iterator that can move backwards, and N steps at a time.
 */
public interface CursorIterator<E>
        extends
            Iterator<E>, // Iterator's methods are in-lined here as well, to show the symmetry in the API re next and previous
            Enumeration<E>,
            CursorIterable<E> // allow the CursorIterator to be used wherever a Iterable is required, e.g. in the new for loops
{
    /**
     * Obtain the next element, without iterating forward.
     *
     * @return the next element
     * @exception NoSuchElementException iteration has no current element (e.g. because the end of the iteration was reached)
     */
    public E peekNext();
    
    /**
     * Obtain the previous element, without iterating backwards.
     *
     * @return the previous element
     * @exception NoSuchElementException iteration has no current element (e.g. because the end of the iteration was reached)
     */
    public E peekPrevious();

    /**
     * Returns <tt>true</tt> if the iteration has more elements in the forward direction.
     *
     * @return <tt>true</tt> if the iterator has more elements in the forward direction.
     * @see #hasPrevious()
     * @see #hasPrevious(int)
     * @see #hasNext(int)
     */
    public boolean hasNext();

    /**
     * Returns <tt>true</tt> if the iteration has more elements in the backwards direction.
     *
     * @return <tt>true</tt> if the iterator has more elements in the backwards direction.
     * @see #hasNext()
     * @see #hasPrevious(int)
     * @see #hasNext(int)
     */
    public boolean hasPrevious();
    
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
            int n );

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
            int n );

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @exception NoSuchElementException iteration has no more elements.
     */
    public E next();

    /**
     * <p>Obtain the next N elements. If fewer than N elements are available, return
     * as many elements are available in a shorter array.</p>
     * 
     * @return the next no more than N elements
     * @see #previous(int)
     */
    public E [] next(
            int n );

    /**
     * Returns the previous element in the iteration.
     *
     * @return the previous element in the iteration.
     * @see #next()
     */
    public E previous();

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
            int  n );

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
            NoSuchElementException;

    /**
     * Move the cursor to just before this element, i.e. return this element when {@link #next next} is invoked
     * right afterwards.
     *
     * @param pos the element to move the cursor to
     * @return the number of steps that were taken to move. Positive number means forward, negative backward
     * @exception NoSuchElementException thrown if this element is not actually part of the collection to iterate over
     */
    public int moveToBefore(
            E pos )
        throws
            NoSuchElementException;

    /**
     * Move the cursor to just after this element, i.e. return this element when {@link #previous previous} is invoked
     * right afterwards.
     *
     * @param pos the element to move the cursor to
     * @return the number of steps that were taken to move. Positive number means forward, negative backward
     * @exception NoSuchElementException thrown if this element is not actually part of the collection to iterate over
     */
    public int moveToAfter(
            E pos )
        throws
            NoSuchElementException;

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
    public void remove();
    
    /**
     * Clone this position.
     *
     * @return identical new instance
     */
    public CursorIterator<E> createCopy();
    
    /**
     * Set this CursorIterator to the position represented by the provided CursorIterator.
     *
     * @param position the position to set this CursorIterator to
     * @throws IllegalArgumentException thrown if the provided CursorIterator did not work on the same CursorIterable,
     *         or the implementations were incompatible.
     */
    public void setPositionTo(
            CursorIterator<E> position )
        throws
            IllegalArgumentException;
}
