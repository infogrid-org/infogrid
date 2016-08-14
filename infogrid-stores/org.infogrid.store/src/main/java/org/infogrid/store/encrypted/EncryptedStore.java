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

package org.infogrid.store.encrypted;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import org.infogrid.store.AbstractStore;
import org.infogrid.store.Store;
import org.infogrid.store.StoreKeyDoesNotExistException;
import org.infogrid.store.StoreKeyExistsAlreadyException;
import org.infogrid.store.StoreValue;
import org.infogrid.util.CursorIterator;
import org.infogrid.util.logging.Log;
import org.infogrid.store.StoreCursor;

/**
 * <p>A <code>Store</code> that encrypts its data before delegating to an
 *    underlying <code>Store</code>.</p>
 * <p>Note that the <code>encodingId</code> remains the same in this implementation; subclasses
 *    could change this.</p>
 */
public class EncryptedStore
        extends
            AbstractStore
{
    private static final Log log = Log.getLogInstance( EncryptedStore.class ); // our own, private logger

    /**
     * Factory method.
     *
     * @param transformation the name of the transformation, e.g., DES/CBC/PKCS5Padding, see <code>javax.crypto.Cipher.getInstance(String)</code>
     * @param key the secret key to be used for encryption and decryption
     * @param delegate the Store that does the actual storing
     * @return the created EncryptedStore
     * @throws InvalidKeyException thrown if the key is invalid or does not match the specified transformation
     * @throws NoSuchAlgorithmException thrown if the specified transformation is not available in the default provider package or any of the other provider packages that were searched.
     * @throws NoSuchPaddingException thrown if transformation contains a padding scheme that is not available.
     */
    public static EncryptedStore create(
            String    transformation,
            SecretKey key,
            Store     delegate )
        throws
            InvalidKeyException,
            NoSuchPaddingException,
            NoSuchAlgorithmException
    {
        Cipher encCipher = Cipher.getInstance( transformation );
        Cipher decCipher = Cipher.getInstance( transformation );

        encCipher.init( Cipher.ENCRYPT_MODE, key );
        decCipher.init( Cipher.DECRYPT_MODE, key );

        return new EncryptedStore( encCipher, decCipher, delegate );
    }

    /**
     * Constructor.
     *
     * @param encCipher the Cipher to use for encryption
     * @param decCipher the Cipher to use for decryption
     * @param delegate the Store that does the actual storing
     */
    protected EncryptedStore(
            Cipher    encCipher,
            Cipher    decCipher,
            Store     delegate )
    {
        theEncCipher   = encCipher;
        theDecCipher   = decCipher;
        theDelegate    = delegate;
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
        throw new UnsupportedOperationException( "Cannot initialize EncryptedStore; initialize underlying Store instead." );
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
        throw new UnsupportedOperationException( "Cannot initialize EncryptedStore; initialize underlying Store instead." );
    }

    /**
     * Put a data element into the Store for the first time.
     *
     * @param key the key under which the data element will be stored
     * @param encodingId the id of the encoding that was used to encode the data element. This must be 64 bytes or less.
     * @param timeCreated the time at which the data element was created originally
     * @param timeUpdated the time at which the data element was successfully updated the most recent time
     * @param timeRead the time at which the data element was last read by some client
     * @param timeExpires the time at which the data element expires
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
        try {
            byte [] encryptedData       = encrypt( data );
            String  encryptedEncodingId = constructEncodingId( encodingId );

            theDelegate.put( key, encryptedEncodingId, timeCreated, timeUpdated, timeRead, timeExpires, encryptedData );

        } finally {
            StoreValue value = new StoreValue( key, encodingId, timeCreated, timeUpdated, timeRead, timeExpires, data );

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
        put(    toStore.getKey(),
                toStore.getEncodingId(),
                toStore.getTimeCreated(),
                toStore.getTimeUpdated(),
                toStore.getTimeRead(),
                toStore.getTimeExpires(),
                toStore.getData() );
    }

    /**
     * Update a data element that already exists in the Store, by overwriting it with a new value.
     *
     * @param key the key under which the data element is already, and will continue to be stored
     * @param encodingId the id of the encoding that was used to encode the data element
     * @param timeCreated the time at which the data element was created originally
     * @param timeUpdated the time at which the data element was successfully updated the most recent time
     * @param timeRead the time at which the data element was last read by some client
     * @param timeExpires the time at which the data element expires
     * @param data the data element, expressed as a sequence of bytes
     * @throws StoreKeyDoesNotExistException thrown if currently there is no data element in the Store using this key
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
        try {
            byte [] encryptedData       = encrypt( data );
            String  encryptedEncodingId = constructEncodingId( encodingId );

            theDelegate.update( key, encryptedEncodingId, timeCreated, timeUpdated, timeRead, timeExpires, encryptedData );

        } finally {
            StoreValue value = new StoreValue( key, encodingId, timeCreated, timeUpdated, timeRead, timeExpires, data );

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
        update( toUpdate.getKey(),
                toUpdate.getEncodingId(),
                toUpdate.getTimeCreated(),
                toUpdate.getTimeUpdated(),
                toUpdate.getTimeRead(),
                toUpdate.getTimeExpires(),
                toUpdate.getData() );
    }

    /**
     * Put (if does not exist already) or update (if it does exist) a data element in the Store.
     *
     * @param key the key under which the data element may already, and will continue to be stored
     * @param encodingId the id of the encoding that was used to encode the data element. This must be 64 bytes or less.
     * @param timeCreated the time at which the data element was created originally
     * @param timeUpdated the time at which the data element was successfully updated the most recent time
     * @param timeRead the time at which the data element in the inStream was last read by some client
     * @param timeExpires the time at which the data element expires
     * @param data the data element, expressed as a sequence of bytes
     * @return true if the value was updated, false if it was put
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
        boolean ret = false;
        try {
            byte [] encryptedData       = encrypt( data );
            String  encryptedEncodingId = constructEncodingId( encodingId );

            ret = theDelegate.putOrUpdate( key, encryptedEncodingId, timeCreated, timeUpdated, timeRead, timeExpires, encryptedData );

        } finally {
            StoreValue value = new StoreValue( key, encodingId, timeCreated, timeUpdated, timeRead, timeExpires, data );

            if( ret ) {
                fireUpdatePerformed( value );
            } else {
                firePutPerformed( value );
            }
        }
        return ret;
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
        return putOrUpdate(
                toStoreOrUpdate.getKey(),
                toStoreOrUpdate.getEncodingId(),
                toStoreOrUpdate.getTimeCreated(),
                toStoreOrUpdate.getTimeUpdated(),
                toStoreOrUpdate.getTimeRead(),
                toStoreOrUpdate.getTimeExpires(),
                toStoreOrUpdate.getData() );
    }

    /**
     * Obtain a data element and associated meta-data from the Store, given a key.
     *
     * @param key the key to the data element in the Store
     * @return the StoreValue stored in the Store for this key; this encapsulates data element and meta-data
     * @throws StoreKeyDoesNotExistException thrown if currently there is no data element in the Store using this key
     * @throws IOException thrown if an I/O error occurred
     *
     * @see #put to initially store a data element
     */
    @Override
    public StoreValue get(
            String key )
        throws
            StoreKeyDoesNotExistException,
            IOException
    {
        StoreValue ret = null;
        try {
            ret = decryptStoreValue( theDelegate.get( key ));
            return ret;

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
     * @throws StoreKeyDoesNotExistException thrown if currently there is no data element in the Store using this key
     * @throws IOException thrown if an I/O error occurred
     */
    @Override
    public void delete(
            String key )
        throws
            StoreKeyDoesNotExistException,
            IOException
    {
        try {
            theDelegate.delete( key );

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
        theDelegate.deleteAll();
    }

    /**
     * Remove all data in this Store whose keys start with this string.
     *
     * @param startsWith the String the key starts with
     * @throws IOException thrown if an I/O error occurred
     */
    @Override
    public void deleteAll(
            String startsWith )
        throws
            IOException
    {
        theDelegate.deleteAll( startsWith );
    }

    /**
     * Returns an iterator over the contained StoreValues.
     *
     * @return an Iterator.
     */
    @Override
    public StoreCursor iterator()
    {
        StoreCursor delegateIter = theDelegate.iterator();

        return new MyIterator( delegateIter );
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
        StoreCursor delegateIter = theDelegate.iterator( startsWith );

        return new MyIterator( delegateIter );
    }

    /**
     * Determine the number of data elements in this Store. Some classes implementing
     * this interface may only return an approximation.
     *
     * @return the number of data elements in this Store
     * @throws IOException thrown if an I/O error occurred
     */
    @Override
    public int size()
        throws
            IOException
    {
        return theDelegate.size();
    }

    /**
     * Determine the number of StoreValues in this Store whose key starts with this String
     *
     * @param startsWith the String the key starts with
     * @return the number of StoreValues in this Store whose key starts with this String
     * @throws IOException thrown if an I/O error occurred
     */
    @Override
    public int size(
            String startsWith )
        throws
            IOException
    {
        return theDelegate.size( startsWith );
    }

    /**
     * Determine whether this Store is empty.
     *
     * @return true if this Store is empty
     * @throws IOException thrown if an I/O error occurred
     */
    @Override
    public boolean isEmpty()
        throws
            IOException
    {
        return theDelegate.isEmpty();
    }

    /**
     * Encrypt a piece of data.
     *
     * @param clear the cleartext
     * @return encrypted data
     */
    protected byte [] encrypt(
            byte [] clear )
    {
        try {
            byte [] ret = theEncCipher.doFinal( clear );
            return ret;

        } catch( Throwable t ) {
            log.error( t );

             // there isn't really anything else we can do
            if( t instanceof RuntimeException ) {
                throw ((RuntimeException) t);
            } else {
                throw new RuntimeException( t );
            }
        }
    }

    /**
     * Decrypt a piece of data.
     *
     * @param encrypted the encrypted data
     * @return decrypted data
     */
    protected byte [] decrypt(
            byte [] encrypted )
    {
        try {
            byte [] ret = theDecCipher.doFinal( encrypted );
            return ret;

        } catch( Throwable t ) {
            log.error( t );

             // there isn't really anything else we can do
            if( t instanceof RuntimeException ) {
                throw ((RuntimeException) t);
            } else {
                throw new RuntimeException( t );
            }
        }
    }

    /**
     * Helper method to encrypt / translate a StoreValue.
     *
     * @param decryptedStoreValue the decrypted StoreValue
     * @return the encrypted StoreValue
     */
    protected StoreValue encryptStoreValue(
            StoreValue decryptedStoreValue )
    {
        String  encryptedEncodingId = constructEncodingId( decryptedStoreValue.getEncodingId() );
        byte [] encryptedData       = encrypt( decryptedStoreValue.getData() );

        StoreValue ret = new StoreValue(
                decryptedStoreValue.getKey(),
                encryptedEncodingId,
                decryptedStoreValue.getTimeCreated(),
                decryptedStoreValue.getTimeUpdated(),
                decryptedStoreValue.getTimeRead(),
                decryptedStoreValue.getTimeExpires(),
                encryptedData );
        return ret;
    }

    /**
     * Helper method to decrypt / translate a StoreValue.
     *
     * @param encryptedStoreValue the encrypted StoreValue
     * @return the decrypted StoreValue
     */
    protected StoreValue decryptStoreValue(
            StoreValue encryptedStoreValue )
    {
        String  decryptedEncodingId = reconstructEncodingId( encryptedStoreValue.getEncodingId() );
        byte [] decryptedData       = decrypt( encryptedStoreValue.getData() );

        StoreValue ret = new StoreValue(
                encryptedStoreValue.getKey(),
                decryptedEncodingId,
                encryptedStoreValue.getTimeCreated(),
                encryptedStoreValue.getTimeUpdated(),
                encryptedStoreValue.getTimeRead(),
                encryptedStoreValue.getTimeExpires(),
                decryptedData );
        return ret;
    }

    /**
     * Construct the encodingId to be used for the delegate. This can be
     * overridden in subclasses.
     *
     * @param original the original encodingId
     * @return the encodingId for the delegate
     */
    protected String constructEncodingId(
            String original )
    {
        return original;
    }

    /**
     * Reconstruct the encodingId from the one used by the delegate. This can be
     * overridden in subclasses.
     *
     * @param change the encodingId used by the delegate
     * @return the original encodingId
     */
    protected String reconstructEncodingId(
            String change )
    {
        return change;
    }

    /**
     * The Cipher to use for encryption.
     */
    protected Cipher theEncCipher;

    /**
     * The Cipher to use for decryption.
     */
    protected Cipher theDecCipher;

    /**
     * The underlying Store.
     */
    protected Store theDelegate;

    /**
     * The Iterator implementation for this class.
     */
    class MyIterator
            implements
                StoreCursor
    {
        /**
         * Constructor.
         *
         * @param delegateIter the underlying Iterator
         */
        public MyIterator(
                StoreCursor delegateIter )
        {
            theDelegateIter = delegateIter;
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
            return decryptStoreValue( theDelegateIter.peekNext() );
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
            return decryptStoreValue( theDelegateIter.peekPrevious() );
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
            return theDelegateIter.hasNext();
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
            return theDelegateIter.hasPrevious();
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
            return theDelegateIter.hasNext( n );
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
            return theDelegateIter.hasPrevious( n );
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
            return decryptStoreValue( theDelegateIter.next() );
        }

        /**
         * <p>Obtain the next N elements. If fewer than N elements are available, return
         * as many elements are available in a shorter array.</p>
         *
         * @param n the number of elements to obtain
         * @return the next no more than N elements
         * @see #previous(int)
         */
        @Override
        public StoreValue [] next(
                int  n )
        {
            StoreValue [] temp = theDelegateIter.next( n );
            StoreValue [] ret  = new StoreValue[ temp.length ]; // FIXME? We might want to write in place

            for( int i=0 ; i<temp.length ; ++i ) {
                ret[i] = decryptStoreValue( temp[i] );
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
            return decryptStoreValue( theDelegateIter.previous() );
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
         * @param n the number of elements to obtain
         * @return the previous no more than N elements
         * @see #next(int)
         */
        @Override
        public StoreValue [] previous(
                int n )
        {
            StoreValue [] temp = theDelegateIter.previous( n );
            StoreValue [] ret  = new StoreValue[ temp.length ]; // FIXME? We might want to write in place

            for( int i=0 ; i<temp.length ; ++i ) {
                ret[i] = decryptStoreValue( temp[i] );
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
            theDelegateIter.moveBy( n );
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
            return theDelegateIter.moveToBefore( pos );
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
            return theDelegateIter.moveToAfter( pos );
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
            theDelegateIter.remove();
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
            return theDelegateIter.moveToBefore( key );
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
            return theDelegateIter.moveToAfter( key );
        }

        /**
         * Clone this position.
         *
         * @return identical new instance
         */
        @Override
        public MyIterator createCopy()
        {
            return new MyIterator( theDelegateIter.createCopy() );
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
            moveToAfter( encryptStoreValue( position.next() ));
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
         * Move the cursor to just before the first element, i.e. return the first element when
         * {@link #next next} is invoked right afterwards.
         *
         * @return the number of steps that were taken to move. Positive number means
         *         forward, negative backward
         */
        @Override
        public int moveToBeforeFirst()
        {
            int ret = theDelegateIter.moveToBeforeFirst();
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
            int ret = theDelegateIter.moveToAfterLast();
            return ret;
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
         * The underlying Iterator.
         */
        protected StoreCursor theDelegateIter;
    }}
