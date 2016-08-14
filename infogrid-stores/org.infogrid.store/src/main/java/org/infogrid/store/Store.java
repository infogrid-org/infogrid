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

package org.infogrid.store;

import java.io.IOException;
import org.infogrid.util.CursorIterable;

/**
 * <p>A <code>Store</code> is an abstraction for a data store that behaves like a persistent
 * Java <code>java.util.Map</code> keyed with String, and with byte arrays as values.</p>
 *
 * <p>Classes implementing this Store interface may store information in a variety of
 * different ways, including in file systems, in databases, or even on P2P networks.
 * Application developers can usually program directly against this interface, without
 * having to be aware aware of the specific class implementing it.</p>
 *
 * <p>When storing or retrieving data element, various meta-data values are being
 * carried around:</p>
 * <dl>
 *  <dt><code>EncodingId</code></dt>
 *  <dd>An application-defined value that represents the encoder used for storing the
 *      data. This is roughly comparable to the idea of a file system descriptor
 *      in a UNIX-like operating system, but it applies on a data element-level.</dd>
 *  <dt><code>TimeCreated</code></dt>
 *  <dd>Time the data element was created, as seen by the application. This value may
 *      be different from the time at which this data element was first inserted into
 *      the <code>Store</code>.</dd>
 *  <dt><code>TimeUpdated</code></dt>
 *  <dd>Time the data element was last updated, as seen by the application. This value may
 *      be different from the time at which this data element was lasted updated in
 *      the <code>Store</code>.</dd>
 *  <dt><code>TimeRead</code></dt>
 *  <dd>Time the data element was last read, as seen by the application. This value may
 *      be different from the time at which this data element was last retrieved from
 *      the <code>Store</code>.</dd>
 *  <dt><code>TimeInvalid</code></dt>
 *  <dd>If positive, this represents the time at which this data element will become invalid.
 *      The definition of invalid is up to the application; an application may choose
 *      to treat invalid StoreValues as if they didn't exist (which is facilitated by
 *      the property <code>ignoreInvalid</code> settable by some <code>Store</code>
 *      implementations) or any other strategy that makes sense to the application. This
 *      is a core meta-data element in order to enable administration from outside of
 *      the application (e.g. by a database administrator who can delete expired rows from
 *      a SQL implementation of <code>Store</code> outside of the application.</dd>
 * </dl>
 *
 * <p><code>StoreListeners</code> may be used to listen to Store operations, e.g. for
 * logging purposes.</p>
 * <p>This interface uses the format defined by <code>System.currentTimeMillis()</code>
 * to encode all time values.</p>
 */
public interface Store
    extends
        CursorIterable<StoreValue>
{
    /**
     * Initialize the Store. If the Store was initialized earlier, this will delete all
     * contained information. This operation is similar to unconditionally formatting a hard drive.
     *
     * @throws IOException thrown if an I/O error occurred
     */
    public abstract void initializeHard()
            throws
                IOException;

    /**
     * Initialize the Store if needed. If the Store was initialized earlier, this will do
     * nothing. This operation is equivalent to {@link #initializeHard} if and only if
     * the Store had not been initialized earlier.
     *
     * @throws IOException thrown if an I/O error occurred
     */
    public abstract void initializeIfNecessary()
            throws
                IOException;

    /**
     * Put a data element into the Store for the first time. Throw a
     * {@link StoreKeyExistsAlreadyException} if a data
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
    public abstract void put(
            String  key,
            String  encodingId,
            long    timeCreated,
            long    timeUpdated,
            long    timeRead,
            long    timeExpires,
            byte [] data )
        throws
            StoreKeyExistsAlreadyException,
            IOException;

    /**
     * Put a data element into the Store for the first time. Throw a
     * {@link StoreKeyExistsAlreadyException} if a data
     * element has already been store using the same key.
     *
     * @param toStore the StoreValue to store
     * @throws StoreKeyExistsAlreadyException thrown if a data element is already stored in the Store using this key
     * @throws IOException thrown if an I/O error occurred
     *
     * @see #update if a data element with this key exists already
     * @see #putOrUpdate if a data element with this key may exist already
     */
    public abstract void put(
            StoreValue toStore )
        throws
            StoreKeyExistsAlreadyException,
            IOException;

    /**
     * Update a data element that already exists in the Store, by overwriting it with a new value. Throw a
     * {@link StoreKeyDoesNotExistException} if a data element with this key does not exist already.
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
    public abstract void update(
            String  key,
            String  encodingId,
            long    timeCreated,
            long    timeUpdated,
            long    timeRead,
            long    timeExpires,
            byte [] data )
        throws
            StoreKeyDoesNotExistException,
            IOException;

    /**
     * Update a data element that already exists in the Store, by overwriting it with a new value. Throw a
     * {@link StoreKeyDoesNotExistException} if a data element with this key does not exist already.
     *
     * @param toUpdate the StoreValue to update
     * @throws StoreKeyDoesNotExistException thrown if no data element exists in the Store using this key
     * @throws IOException thrown if an I/O error occurred
     *
     * @see #put if a data element with this key does not exist already
     * @see #putOrUpdate if a data element with this key may exist already
     */
    public abstract void update(
            StoreValue toUpdate )
        throws
            StoreKeyDoesNotExistException,
            IOException;

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
    public abstract boolean putOrUpdate(
            String  key,
            String  encodingId,
            long    timeCreated,
            long    timeUpdated,
            long    timeRead,
            long    timeExpires,
            byte [] data )
        throws
            IOException;

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
    public abstract boolean putOrUpdate(
            StoreValue toStoreOrUpdate )
        throws
            IOException;

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
    public abstract StoreValue get(
            String key )
        throws
            StoreKeyDoesNotExistException,
            IOException;

    /**
     * Delete the data element that is stored using this key.
     *
     * @param key the key to the data element in the Store
     * @throws StoreKeyDoesNotExistException thrown if currently there is no data element in the Store using this key
     * @throws IOException thrown if an I/O error occurred
     */
    public abstract void delete(
            String key )
        throws
            StoreKeyDoesNotExistException,
            IOException;

    /**
     * Remove all data elements in this Store.
     *
     * @throws IOException thrown if an I/O error occurred
     */
    public abstract void deleteAll()
        throws
            IOException;

    /**
     * Remove all data in this Store whose keys start with this string.
     *
     * @param startsWith the String the key starts with
     * @throws IOException thrown if an I/O error occurred
     */
    public abstract void deleteAll(
            String startsWith )
        throws
            IOException;

    /**
     * Return a more specific subtype of CursorIterator as an iterator over
     * the entire Store.
     *
     * @return the Iterator
     */
    @Override
    public abstract StoreCursor iterator();

    /**
     * Return a more specific subtype of CursorIterator as an iterator over
     * the entire Store.
     *
     * @return the Iterator
     */
    @Override
    public abstract StoreCursor getIterator();

    /**
     * Obtain an iterator over the subset of the elements in the Store whose
     * key starts with this String.
     *
     * @param startsWith the String the key starts with
     * @return the Iterator
     */
    public abstract StoreCursor iterator(
            String startsWith );

    /**
     * Obtain an iterator over the subset of the elements in the Store whose
     * key starts with this String.
     *
     * @param startsWith the String the key starts with
     * @return the Iterator
     */
    public abstract StoreCursor getIterator(
            String startsWith );

    /**
     * Determine the number of data elements in this Store. Some classes implementing
     * this interface may only return an approximation.
     *
     * @return the number of data elements in this Store
     * @throws IOException thrown if an I/O error occurred
     */
    public abstract int size()
            throws
                IOException;

    /**
     * Determine the number of StoreValues in this Store whose key starts with this String
     *
     * @param startsWith the String the key starts with
     * @return the number of StoreValues in this Store whose key starts with this String
     * @throws IOException thrown if an I/O error occurred
     */
    public abstract int size(
            String startsWith )
        throws
            IOException;

    /**
     * Determine whether this Store is empty.
     *
     * @return true if this Store is empty
     * @throws IOException thrown if an I/O error occurred
     */
    public abstract boolean isEmpty()
        throws
            IOException;

    /**
     * Determine whether the set of elements in this Store whose key
     * starts with this String is empty
     *
     * @param startsWith the String the key starts with
     * @return true if this Store is empty
     * @throws IOException thrown if an I/O error occurred
     */
    public abstract boolean isEmpty(
            String startsWith )
        throws
            IOException;

    /**
      * Add a listener.
      * This listener is added directly to the listener list, which prevents the
      * listener from being garbage-collected before this Object is being garbage-collected.
      *
      * @param newListener the to-be-added listener
      * @see #addSoftStoreListener
      * @see #addWeakStoreListener
      * @see #removeStoreListener
      */
    public abstract void addDirectStoreListener(
            StoreListener newListener );

    /**
      * Add a listener.
      * This listener is added to the listener list using a <code>java.lang.ref.SoftReference</code>,
      * which allows the listener to be garbage-collected before this Object is being garbage-collected
      * according to the semantics of Java references.
      *
      * @param newListener the to-be-added listener
      * @see #addDirectStoreListener
      * @see #addWeakStoreListener
      * @see #removeStoreListener
      */
    public abstract void addSoftStoreListener(
            StoreListener newListener );

    /**
      * Add a listener.
      * This listener is added to the listener list using a <code>java.lang.ref.WeakReference</code>,
      * which allows the listener to be garbage-collected before this Object is being garbage-collected
      * according to the semantics of Java references.
      *
      * @param newListener the to-be-added listener
      * @see #addDirectStoreListener
      * @see #addSoftStoreListener
      * @see #removeStoreListener
      */
    public abstract void addWeakStoreListener(
            StoreListener newListener );

    /**
      * Remove a listener.
      * This method is the same regardless how the listener was subscribed to events.
      *
      * @param oldListener the to-be-removed listener
      * @see #addDirectStoreListener
      * @see #addSoftStoreListener
      * @see #addWeakStoreListener
      */
    public abstract void removeStoreListener(
            StoreListener oldListener );
}
