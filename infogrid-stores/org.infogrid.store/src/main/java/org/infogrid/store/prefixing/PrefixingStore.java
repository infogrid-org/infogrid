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

package org.infogrid.store.prefixing;

import java.io.IOException;
import java.util.NoSuchElementException;
import org.infogrid.store.AbstractStore;
import org.infogrid.store.Store;
import org.infogrid.store.StoreKeyDoesNotExistException;
import org.infogrid.store.StoreKeyExistsAlreadyException;
import org.infogrid.store.StoreValue;
import org.infogrid.util.CursorIterator;
import org.infogrid.util.FilteringCursorIterator;
import org.infogrid.util.logging.CanBeDumped;
import org.infogrid.util.logging.Dumper;
import org.infogrid.store.StoreCursor;

/**
 * This {@link Store} delegates to another <code>Store</code>, but prefixes all keys with
 * a constant prefix. This is useful to build a hierarchical namespace of <code>Store</code>
 * keys.
 */
public class PrefixingStore
        extends
            AbstractStore
        implements
            CanBeDumped
{
    /**
     * Factory method.
     *
     * @param prefix the prefix for all keys
     * @param delegate the Store that this PrefixingStore delegates to
     * @return the created PrefixingStore
     */
    public static PrefixingStore create(
            String prefix,
            Store  delegate )
    {
        if( prefix.contains( DEFAULT_SEPARATOR )) {
            throw new IllegalArgumentException( "Prefix must not contain DEFAULT_SEPARATOR: " + prefix );
        }
        return new PrefixingStore( prefix + DEFAULT_SEPARATOR, delegate );
    }

    /**
     * Constructor.
     *
     * @param prefixAndSeparator the prefixAndSeparator for all keys
     * @param delegate the Store that this PrefixingStore delegates to
     */
    protected PrefixingStore(
            String prefixAndSeparator,
            Store  delegate )
    {
        thePrefixAndSeparator = prefixAndSeparator;
        theDelegate           = delegate;
    }

    /**
     * Initialize the Store. If the Store was initialized earlier, this will delete all
     * contained information. This operation is similar to unconditionally formatting a hard drive.
     *
     * @throws IOException thrown if an I/O error occurred
     */
    @Override
    public void initializeHard()
            throws
                IOException
    {
        throw new UnsupportedOperationException( "Cannot initialize PrefixingStore; initialize underlying Store instead." );
    }

    /**
     * Initialize the Store if needed. If the Store was initialized earlier, this will do
     * nothing. This operation is equivalent to {@link #initializeHard} if and only if
     * the Store had not been initialized earlier.
     *
     * @throws IOException thrown if an I/O error occurred
     */
    @Override
    public void initializeIfNecessary()
            throws
                IOException
    {
        throw new UnsupportedOperationException( "Cannot initialize PrefixingStore; initialize underlying Store instead." );
    }

    /**
     * Put a data element into the Store for the first time. Throw an Exception if a data
     * element has already been store using the same key.
     *
     * @param key the key with which the specified data element is to be associated
     * @param encodingId the id of the encoding that was used to encode the data element.
     * @param timeCreated the time at which the data element was created
     * @param timeUpdated the time at which the data element was successfully updated the most recent time
     * @param timeRead the time at which the data element was last read by some client
     * @param timeExpires if positive, the time at which the data element expires
     * @param data the data element, expressed as a sequence of bytes
     * @throws StoreKeyExistsAlreadyException thrown if a data element is already stored in the Store using this key
     * @throws IOException thrown if an I/O error occurred
     *
     * @see #update if a data element with this key exists already
     * @see #putOrUpdate if a data element with this key may exist already
     */
    @Override
    public void put(
            String  key,
            String  encodingId,
            long    timeCreated,
            long    timeUpdated,
            long    timeRead,
            long    timeExpires,
            byte [] data )
        throws
            StoreKeyExistsAlreadyException,
            IOException
    {
        String delegatedKey = constructDelegatedKey( key );

        StoreValue value = new StoreValue( key, encodingId, timeCreated, timeUpdated, timeRead, timeExpires, data );
        try {
            theDelegate.put( delegatedKey, encodingId, timeCreated, timeUpdated, timeRead, timeExpires, data );

        } catch( StoreKeyExistsAlreadyException ex ) {
            throw new PrefixingStoreKeyExistsAlreadyException( this, key, ex );

        } finally {
            firePutPerformed( value );
        }
    }

    /**
     * Put a data element into the Store for the first time. Throw an Exception if a data
     * element has already been store using the same key.
     *
     * @param toStore the StoreValue to store
     * @throws StoreKeyExistsAlreadyException thrown if a data element is already stored in the Store using this key
     * @throws IOException thrown if an I/O error occurred
     *
     * @see #update if a data element with this key exists already
     * @see #putOrUpdate if a data element with this key may exist already
     */
    @Override
    public void put(
            StoreValue toStore )
        throws
            StoreKeyExistsAlreadyException,
            IOException
    {
        String delegatedKey = constructDelegatedKey( toStore.getKey() );

        try {
            theDelegate.put(
                    delegatedKey,
                    toStore.getEncodingId(),
                    toStore.getTimeCreated(),
                    toStore.getTimeUpdated(),
                    toStore.getTimeRead(),
                    toStore.getTimeExpires(),
                    toStore.getData() );

        } catch( StoreKeyExistsAlreadyException ex ) {
            throw new PrefixingStoreKeyExistsAlreadyException( this, toStore.getKey(), ex );

        } finally {
            firePutPerformed( toStore );
        }
    }

    /**
     * Update a data element that already exists in the Store, by overwriting it with a new value. Throw an
     * Exception if a data element with this key does not exist already.
     *
     * @param key the key with which the specified data element is already, and will continue to be stored
     * @param encodingId the id of the encoding that was used to encode the data element.
     * @param timeCreated the time at which the data element was created
     * @param timeUpdated the time at which the data element was successfully updated the most recent time
     * @param timeRead the time at which the data element was last read by some client
     * @param timeExpires if positive, the time at which the data element expires
     * @param data the data element, expressed as a sequence of bytes
     * @throws StoreKeyDoesNotExistException thrown if no data element exists in the Store using this key
     * @throws IOException thrown if an I/O error occurred
     *
     * @see #put if a data element with this key does not exist already
     * @see #putOrUpdate if a data element with this key may exist already
     */
    @Override
    public void update(
            String  key,
            String  encodingId,
            long    timeCreated,
            long    timeUpdated,
            long    timeRead,
            long    timeExpires,
            byte [] data )
        throws
            StoreKeyDoesNotExistException,
            IOException
    {
        String delegatedKey = constructDelegatedKey( key );

        StoreValue value = new StoreValue( key, encodingId, timeCreated, timeUpdated, timeRead, timeExpires, data );

        try {
            theDelegate.update( delegatedKey, encodingId, timeCreated, timeUpdated, timeRead, timeExpires, data );

        } catch( StoreKeyDoesNotExistException ex ) {
            throw new PrefixingStoreKeyDoesNotExistException( this, key, ex );

        } finally {
            fireUpdatePerformed( value );
        }
    }

    /**
     * Update a data element that already exists in the Store, by overwriting it with a new value. Throw an
     * Exception if a data element with this key does not exist already.
     *
     * @param toUpdate the StoreValue to update
     * @throws StoreKeyDoesNotExistException thrown if no data element exists in the Store using this key
     * @throws IOException thrown if an I/O error occurred
     *
     * @see #put if a data element with this key does not exist already
     * @see #putOrUpdate if a data element with this key may exist already
     */
    @Override
    public void update(
            StoreValue toUpdate )
        throws
            StoreKeyDoesNotExistException,
            IOException
    {
        String delegatedKey = constructDelegatedKey( toUpdate.getKey() );

        try {
            theDelegate.update(
                    delegatedKey,
                    toUpdate.getEncodingId(),
                    toUpdate.getTimeCreated(),
                    toUpdate.getTimeUpdated(),
                    toUpdate.getTimeRead(),
                    toUpdate.getTimeExpires(),
                    toUpdate.getData() );

        } catch( StoreKeyDoesNotExistException ex ) {
            throw new StoreKeyDoesNotExistException( this, toUpdate.getKey(), ex );

        } finally {
            fireUpdatePerformed( toUpdate );
        }
    }

    /**
     * Put (if does not exist already) or update (if it does exist) a data element in the Store.
     *
     * @param key the key with which the specified data element is already, and will continue to be stored
     * @param encodingId the id of the encoding that was used to encode the data element.
     * @param timeCreated the time at which the data element was created
     * @param timeUpdated the time at which the data element was successfully updated the most recent time
     * @param timeRead the time at which the data element was last read by some client
     * @param timeExpires if positive, the time at which the data element expires
     * @param data the data element, expressed as a sequence of bytes
     * @return true if the data element was updated, false if it was put
     * @throws IOException thrown if an I/O error occurred
     *
     * @see #put if a data element with this key does not exist already
     * @see #update if a data element with this key exists already
     */
    @Override
    public boolean putOrUpdate(
            String  key,
            String  encodingId,
            long    timeCreated,
            long    timeUpdated,
            long    timeRead,
            long    timeExpires,
            byte [] data )
        throws
            IOException
    {
        String delegatedKey = constructDelegatedKey( key );

        boolean updated = theDelegate.putOrUpdate( delegatedKey, encodingId, timeCreated, timeUpdated, timeRead, timeExpires, data );

        StoreValue value = new StoreValue( key, encodingId, timeCreated, timeUpdated, timeRead, timeExpires, data );

        if( !updated ) {
            firePutPerformed( value );
        } else {
            fireUpdatePerformed( value );
        }

        return updated;
    }

    /**
     * Put (if does not exist already) or update (if it does exist) a data element in the Store.
     *
     * @param toStoreOrUpdate the StoreValue to store or update
     * @return true if the value was updated, false if it was put
     * @throws IOException thrown if an I/O error occurred
     *
     * @see #put if a data element with this key does not exist already
     * @see #update if a data element with this key exists already
     */
    @Override
    public boolean putOrUpdate(
            StoreValue toStoreOrUpdate )
        throws
            IOException
    {
        String delegatedKey = constructDelegatedKey( toStoreOrUpdate.getKey() );

        boolean updated = theDelegate.putOrUpdate(
                    delegatedKey,
                    toStoreOrUpdate.getEncodingId(),
                    toStoreOrUpdate.getTimeCreated(),
                    toStoreOrUpdate.getTimeUpdated(),
                    toStoreOrUpdate.getTimeRead(),
                    toStoreOrUpdate.getTimeExpires(),
                    toStoreOrUpdate.getData() );

        if( !updated ) {
            firePutPerformed( toStoreOrUpdate );
        } else {
            fireUpdatePerformed( toStoreOrUpdate );
        }

        return updated;
    }


    /**
     * Obtain a data element and associated meta-data from the Store, given a key.
     *
     * @param key the key to the data element in the Store
     * @return the StoreValue stored in the Store for this key; this encapsulates data element and meta-data
     * @throws PrefixingStoreKeyDoesNotExistException thrown if currently there is no data element in the Store using this key
     * @throws IOException thrown if an I/O error occurred
     *
     * @see #put to initially store a data element
     */
    @Override
    public StoreValue get(
            String key )
        throws
            PrefixingStoreKeyDoesNotExistException,
            IOException
    {
        String delegatedKey = constructDelegatedKey( key );

        StoreValue ret = null;
        try {
            StoreValue delegateValue = theDelegate.get( delegatedKey );

            ret = translateDelegateStoreValue( delegateValue );

            return ret;

        } catch( StoreKeyDoesNotExistException ex ) {
            throw new PrefixingStoreKeyDoesNotExistException( this, key, ex );

        } finally {
            if( ret != null ) {
                fireGetPerformed( ret );
            } else {
                fireGetFailed( key );
            }
        }
    }

    /**
     * Delete the data element that is stored using this key.
     *
     * @param key the key to the data element in the Store
     * @throws PrefixingStoreKeyDoesNotExistException thrown if currently there is no data element in the Store using this key
     * @throws IOException thrown if an I/O error occurred
     */
    @Override
    public void delete(
            String key )
        throws
            PrefixingStoreKeyDoesNotExistException,
            IOException
    {
        String delegatedKey = constructDelegatedKey( key );

        try {
            theDelegate.delete( delegatedKey );

        } catch( StoreKeyDoesNotExistException ex ) {
            throw new PrefixingStoreKeyDoesNotExistException( this, key, ex );

        } finally {
            fireDeletePerformed( key );
        }
    }

    /**
     * Remove all data elements in this Store.
     *
     * @throws IOException thrown if an I/O error occurred
     */
    @Override
    public void deleteAll()
        throws
            IOException
    {
        theDelegate.deleteAll( thePrefixAndSeparator );
    }

    /**
     * Remove all data in this Store whose keys start with this string.
     *
     * @param prefix the String the key starts with
     * @throws IOException thrown if an I/O error occurred
     */
    @Override
    public void deleteAll(
            String prefix )
        throws
            IOException
    {
        theDelegate.deleteAll( thePrefixAndSeparator + prefix );
    }

    /**
     * Obtain an Iterator over the content of this Store.
     *
     * @return the Iterator
     */
    @Override
    public StoreCursor iterator()
    {
        return iterator( "" );
    }

    /**
     * Obtain an iterator over the subset of the elements in the Store whose
     * key starts with this String.
     *
     * @param startsWith the String the key starts with
     * @return the Iterator
     */
    @Override
    public StoreCursor iterator(
            String startsWith )
    {
        return new MyBridgingIterator( new MyFilteringIterator( theDelegate.iterator(), thePrefixAndSeparator, startsWith ));
    }

    /**
     * Determine the number of StoreValues in this Store with this prefix.
     *
     * @param prefix the prefix
     * @return the number of StoreValues in this Store with this prefix
     * @throws IOException thrown if an I/O error occurred
     */
    @Override
    public int size(
            String prefix )
        throws
            IOException
    {
        return theDelegate.size( thePrefixAndSeparator + prefix );
    }

    /**
     * Convert a delegate StoreValue into a StoreValue here.
     *
     * @param delegateValue the delegate StoreValue
     * @return the local StoreValue
     */
    protected StoreValue translateDelegateStoreValue(
            StoreValue delegateValue )
    {
        String newKey = delegateValue.getKey().substring( thePrefixAndSeparator.length() );
        StoreValue ret = new StoreValue(
                newKey,
                delegateValue.getEncodingId(),
                delegateValue.getTimeCreated(),
                delegateValue.getTimeUpdated(),
                delegateValue.getTimeRead(),
                delegateValue.getTimeExpires(),
                delegateValue.getData() );

        return ret;
    }

    /**
     * Convert a StoreValue into a delegate StoreValue here.
     *
     * @param value the StoreValue
     * @return the delegate StoreValue
     */
    protected StoreValue translateToDelegateStoreValue(
            StoreValue value )
    {
        String newKey = constructDelegatedKey( value.getKey() );
        StoreValue ret = new StoreValue(
                newKey,
                value.getEncodingId(),
                value.getTimeCreated(),
                value.getTimeUpdated(),
                value.getTimeRead(),
                value.getTimeExpires(),
                value.getData() );

        return ret;
    }

    /**
     * Construct the delegate key, given this key and instance data.
     *
     * @param key in key
     * @return the delegate key
     */
    protected String constructDelegatedKey(
            String key )
    {
        StringBuilder ret = new StringBuilder();
        ret.append( thePrefixAndSeparator );
        ret.append( key );
        return ret.toString();
    }

    /**
     * Dump this object.
     *
     * @param d the Dumper to dump to
     */
    @Override
    public void dump(
            Dumper d )
    {
        d.dump( this,
                new String[] {
                    "prefixAndSeparator",
                    "delegate"
                },
                new Object[] {
                    thePrefixAndSeparator,
                    theDelegate
                });
    }

    /**
     * The prefix and separator.
     */
    protected String thePrefixAndSeparator;

    /**
     * The delegate Store.
     */
    protected Store theDelegate;

    /**
     * The default separator between the prefix and the key.
     */
    public static final String DEFAULT_SEPARATOR = " ";

    /**
     * Our IterableStoreCursor implementation.
     */
    class MyBridgingIterator
            implements
                StoreCursor
    {
        /**
         * Constructor.
         *
         * @param delegateIter an Iterator over the underlying delegate IterableStore
         * @param startsWith filter by this additional prefix
         */
        public MyBridgingIterator(
                MyFilteringIterator filterIterator )
        {
            theFilterIterator = filterIterator;
        }

        /**
         * Obtain the next element, without iterating forward.
         *
         * @return the next element
         * @throws NoSuchElementException iteration has no current element (e.g. because the end of the iteration was reached)
         */
        @Override
        public StoreValue peekNext()
        {
            StoreValue ret = translateDelegateStoreValue( theFilterIterator.peekNext() );
            return ret;
        }

        /**
         * Obtain the previous element, without iterating backwards.
         *
         * @return the previous element
         * @throws NoSuchElementException iteration has no current element (e.g. because the end of the iteration was reached)
         */
        @Override
        public StoreValue peekPrevious()
        {
            StoreValue ret = translateDelegateStoreValue( theFilterIterator.peekPrevious() );
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
            return theFilterIterator.hasNext();
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
            return theFilterIterator.hasPrevious();
        }

        /**
         * Returns <tt>true</tt> if the iteration has at least N more elements in the forward direction.
         *
         * @param n the number of elements for which to check
         * @return <tt>true</tt> if the iterator has at least N more elements in the forward direction.
         * @see #hasNext()
         * @see #hasPrevious()
         * @see #hasPrevious(int)
         */
        @Override
        public boolean hasNext(
                int n )
        {
            return theFilterIterator.hasNext( n );
        }

        /**
         * Returns <tt>true</tt> if the iteration has at least N more elements in the backwards direction.
         *
         * @param n the number of elements for which to check
         * @return <tt>true</tt> if the iterator has at least N more elements in the backwards direction.
         * @see #hasNext()
         * @see #hasPrevious()
         * @see #hasNext(int)
         */
        @Override
        public boolean hasPrevious(
                int n )
        {
            return theFilterIterator.hasPrevious( n );
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration.
         * @throws NoSuchElementException iteration has no more elements.
         */
        @Override
        public StoreValue next()
        {
            StoreValue ret = translateDelegateStoreValue( theFilterIterator.next() );
            return ret;
        }

        /**
         * <p>Obtain the next N elements. If fewer than N elements are available, return
         * as many elements are available in a shorter array.</p>
         *
         * @param n the number of elements to return
         * @return the next no more than N elements
         * @see #previous(int)
         */
        @Override
        public StoreValue [] next(
                int n )
        {
            StoreValue [] found = theFilterIterator.next( n );
            StoreValue [] ret   = new StoreValue[ found.length ];
            for( int i=0 ; i<found.length ; ++i ) {
                ret[i] = translateDelegateStoreValue( found[i] );
            }
            return ret;
        }

        /**
         * Returns the previous element in the iteration.
         *
         * @return the previous element in the iteration.
         * @see #next()
         */
        @Override
        public StoreValue previous()
        {
            StoreValue ret = translateDelegateStoreValue( theFilterIterator.previous() );
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
         * @param n the number of elements to return
         * @return the previous no more than N elements
         * @see #next(int)
         */
        @Override
        public StoreValue [] previous(
                int n )
        {
            StoreValue [] found = theFilterIterator.previous( n );
            StoreValue [] ret   = new StoreValue[ found.length ];
            for( int i=0 ; i<found.length ; ++i ) {
                ret[i] = translateDelegateStoreValue( found[i] );
            }
            return ret;
        }

        /**
         * Move the cursor by N positions. Positive numbers indicate forward movemement;
         * negative numbers indicate backward movement. This can move all the way forward
         * to the position "past last" and all the way backward to the position "before first".
         *
         * @param n the number of positions to move
         * @throws NoSuchElementException thrown if the position does not exist
         */
        @Override
        public void moveBy(
                int n )
            throws
                NoSuchElementException
        {
            theFilterIterator.moveBy( n );
        }

        /**
         * Move the cursor to just before this element, i.e. return this element when {@link #next next} is invoked
         * right afterwards.
         *
         * @param pos the element to move the cursor to
         * @return the number of steps that were taken to move. Positive number means forward, negative backward
         * @throws NoSuchElementException thrown if this element is not actually part of the collection to iterate over
         */
        @Override
        public int moveToBefore(
                StoreValue pos )
            throws
                NoSuchElementException
        {
            StoreValue delegatedPos = translateToDelegateStoreValue( pos );
            int        ret          = theFilterIterator.moveToBefore( delegatedPos );

            return ret;
        }

        /**
         * Move the cursor to just after this element, i.e. return this element when {@link #previous previous} is invoked
         * right afterwards.
         *
         * @param pos the element to move the cursor to
         * @return the number of steps that were taken to move. Positive number means forward, negative backward
         * @throws NoSuchElementException thrown if this element is not actually part of the collection to iterate over
         */
        @Override
        public int moveToAfter(
                StoreValue pos )
            throws
                NoSuchElementException
        {
            StoreValue delegatedPos = translateToDelegateStoreValue( pos );
            int        ret          = theFilterIterator.moveToAfter( delegatedPos );

            return ret;
        }

        /**
         *
         * Removes from the underlying collection the last element returned by the
         * iterator (optional operation). This is the same as the current element.
         *
         * @throws UnsupportedOperationException if the <tt>remove</tt>
         *        operation is not supported by this Iterator.

         * @throws IllegalStateException if the <tt>next</tt> method has not
         *        yet been called, or the <tt>remove</tt> method has already
         *        been called after the last call to the <tt>next</tt>
         *        method.
         */
        @Override
        public void remove()
        {
            theFilterIterator.remove();
        }

        /**
         * Clone this position.
         *
         * @return identical new instance
         */
        @Override
        public MyBridgingIterator createCopy()
        {
            return new MyBridgingIterator( theFilterIterator.createCopy() );
        }

        /**
         * Set this CursorIterator to the position represented by the provided CursorIterator.
         *
         * @param position the position to set this CursorIterator to
         * @throws IllegalArgumentException thrown if the provided CursorIterator did not work on the same CursorIterable,
         *         or the implementations were incompatible.
         */
        @Override
        public void setPositionTo(
                CursorIterator<StoreValue> position )
            throws
                IllegalArgumentException
        {
            StoreValue delegatedPos = translateToDelegateStoreValue( position.next() );

            theFilterIterator.moveToAfter( delegatedPos );
        }

        /**
         * Move the cursor to this element, i.e. return this element when {@link #next next} is invoked
         * right afterwards.
         *
         * @param key the key of the element to move the cursor to
         * @return the number of steps that were taken to move. Positive number means forward, negative backward
         * @throws NoSuchElementException thrown if this element is not actually part of the collection to iterate over
         */
        @Override
        public int moveToBefore(
                String key )
            throws
                NoSuchElementException
        {
            String delegatedKey = constructDelegatedKey( key );
            int    ret          = theFilterIterator.moveToBefore( delegatedKey );

            return ret;
        }

        /**
         * Move the cursor to this element, i.e. return this element when {@link #previous previous} is invoked
         * right afterwards.
         *
         * @param key the key of the element to move the cursor to
         * @return the number of steps that were taken to move. Positive number means forward, negative backward
         * @throws NoSuchElementException thrown if this element is not actually part of the collection to iterate over
         */
        @Override
        public int moveToAfter(
                String key )
            throws
                NoSuchElementException
        {
            String delegatedKey = constructDelegatedKey( key );
            int    ret          = theFilterIterator.moveToAfter( delegatedKey );

            return ret;
        }

       /**
         * Move the cursor to just before the first element, i.e. return the first element when
         * {@link #next next} is invoked right afterwards.
         *
         * @return the number of steps that were taken to move. Positive number means
         *         forward, negative backward
         */
        @Override
        public int moveToBeforeFirst()
        {
            int ret = theFilterIterator.moveToBeforeFirst();
            return ret;
        }

        /**
         * Move the cursor to just after the last element, i.e. return the last element when
         * {@link #previous previous} is invoked right afterwards.
         *
         * @return the number of steps that were taken to move. Positive number means
         *         forward, negative backward
         */
        @Override
        public int moveToAfterLast()
        {
            int ret = theFilterIterator.moveToAfterLast();
            return ret;
        }

        /**
          * Do we have more elements?
          *
          * @return true if we have more elements
          */
        @Override
        public final boolean hasMoreElements()
        {
            return hasNext();
        }

        /**
          * Return next element and iterate.
          *
          * @return the next element
          */
        @Override
        public final StoreValue nextElement()
        {
            return next();
        }

        /**
         * Obtain a CursorIterable instead of an Iterator.
         *
         * @return the CursorIterable
         */
        @Override
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
        @Override
        public final CursorIterator<StoreValue> getIterator()
        {
            return iterator();
        }

        /**
         * Determine the type of array that is returned by the iteration methods that
         * return arrays.
         *
         * @return the type of array
         */
        @Override
        public Class<StoreValue> getArrayComponentType()
        {
            return StoreValue.class;
        }

        /**
         * The underlying FilteringIterator.
         */
        protected MyFilteringIterator theFilterIterator;
    }

    /**
     * The FilteringIterator that MyBridgingIterator delegates to. This used to be a non-static class,
     * but I ran into a compiler bug that would cause a java.lang.VerifyError at run-time!
     */
    static class MyFilteringIterator
            extends
                FilteringCursorIterator<StoreValue>
            implements
                StoreCursor
    {
        /**
         * Constructor.
         *
         * @param delegateIter the Iterator over the underlying Store
         * @param prefixAndSeparator copied from the outer class
         * @param startsWith filter by this additional prefix
         */
        public MyFilteringIterator(
                StoreCursor delegateIter,
                String      prefixAndSeparator,
                String      startsWith )
        {
            super( delegateIter,
                   ( StoreValue v ) -> {
                          String  key = v.getKey();
                          boolean ret = key.startsWith( prefixAndSeparator + startsWith );

                          return ret;
                   },
                   StoreValue.class );

            thePrefixAndSeparator = prefixAndSeparator;
            theStartsWith = startsWith;
        }

        /**
         * Move the cursor to this element, i.e. return this element when {@link #next next} is invoked
         * right afterwards.
         *
         * @param pos the element to move the cursor to
         * @return the number of steps that were taken to move. Positive number means forward, negative backward
         * @throws NoSuchElementException thrown if this element is not actually part of the collection to iterate over
         */
        @Override
        public int moveToBefore(
                String pos )
            throws
                NoSuchElementException
        {
            // see moveToBefore in superclass

            CursorIterator<StoreValue> currentPosition = createCopy();

            int count = 0;
            while( hasNext() ) {
                StoreValue found = peekNext();
                if( pos.equals( found.getKey() )) {
                    return count;
                }
                ++count;
                next();
            }

            setPositionTo( currentPosition );

            count = 0;
            while( hasPrevious() ) {
                StoreValue found = previous();
                --count;
                if( pos.equals( found.getKey() )) {
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
         * @throws NoSuchElementException thrown if this element is not actually part of the collection to iterate over
         */
        @Override
        public int moveToAfter(
                String pos )
            throws
                NoSuchElementException
        {
            // see moveToBefore in superclass

            CursorIterator<StoreValue> currentPosition = createCopy();

            int count = 0;
            while( hasNext() ) {
                StoreValue found = next();
                ++count;
                if( pos.equals( found.getKey() )) {
                    return count;
                }
            }

            setPositionTo( currentPosition );

            count = 0;
            while( hasPrevious() ) {
                StoreValue found = peekPrevious();
                if( pos.equals( found.getKey() )) {
                    return count;
                }
                --count;
                previous();
            }
            throw new NoSuchElementException();
        }

        /**
         * Clone this position.
         *
         * @return identical new instance
         */
        @Override
        public MyFilteringIterator createCopy()
        {
            return new MyFilteringIterator( (StoreCursor) theDelegate.createCopy(), thePrefixAndSeparator, theStartsWith );
        }

        /**
         * Copied from the outer class.
         */
        protected String thePrefixAndSeparator;

        /**
         * The additional key prefix to filter by.
         */
        protected String theStartsWith;
    }
}
