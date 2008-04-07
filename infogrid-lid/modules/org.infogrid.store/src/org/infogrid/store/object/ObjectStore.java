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

package org.infogrid.store.object;

import org.infogrid.store.Store;
import org.infogrid.store.StoreEntryMapper;
import org.infogrid.store.StoreKeyDoesNotExistException;
import org.infogrid.store.StoreKeyExistsAlreadyException;
import org.infogrid.store.StoreValue;
import org.infogrid.store.StoreValueDecodingException;
import org.infogrid.store.StoreValueEncodingException;

import java.io.IOException;

/**
 * This class can store arbitrary Java objects, by delegating to an underlying,
 * configurable {@link Store} and a {@link StoreEntryMapper}.
 */
public class ObjectStore<K,V>
{
    /**
     * Constructor.
     * 
     * @param mapper the StoreEntryMapper to use
     * @param delegate the underlying Store
     */
    public ObjectStore(
            StoreEntryMapper<K,V> mapper,
            Store                 delegate )
    {
        theMapper   = mapper;
        theDelegate = delegate;
    }
    
    /**
     * Put on object into the Store for the first time.
     *
     * @param key the key under which the object will be stored
     * @param value the object to be stored
     * @throws StoreKeyExistsAlreadyException thrown if a data element is already stored in the Store using this key
     *
     * @see #update if a data element with this key exists already
     * @see #putOrUpdate if a data element with this key may exist already
     */
    public void put(
            K key,
            V value )
        throws
            ObjectStoreKeyExistsAlreadyException,
            StoreValueEncodingException,
            IOException
    {
        String  stringKey = theMapper.keyToString( key );
        byte [] data      = theMapper.asBytes( value );
        
        try {
            theDelegate.put(
                    stringKey,
                    theMapper.getPreferredEncodingId(),
                    theMapper.getTimeCreated( value ),
                    theMapper.getTimeUpdated( value ),
                    theMapper.getTimeRead( value ),
                    theMapper.getTimeExpires( value ),
                    data );

        } catch( StoreKeyExistsAlreadyException ex ) {
            throw new ObjectStoreKeyExistsAlreadyException( this, key, ex );
        }
    }

    /**
     * Update data element that already exists in the Store, by overwriting it with a new value.
     *
     * @param key the key under which the object will be stored
     * @param value the object to be stored
     * @throws StoreKeyDoesNotExistException thrown if currently there is no data element in the Store using this key
     *
     * @see #put if a data element with this key does not exist already
     * @see #putOrUpdate if a data element with this key may exist already
     */
    public void update(
            K key,
            V value )
        throws
            ObjectStoreKeyDoesNotExistException,
            StoreValueEncodingException,
            IOException
    {
        String  stringKey = theMapper.keyToString( key );
        byte [] data      = theMapper.asBytes( value );

        try {
            theDelegate.update(
                    stringKey,
                    theMapper.getPreferredEncodingId(),
                    theMapper.getTimeCreated( value ),
                    theMapper.getTimeUpdated( value ),
                    theMapper.getTimeRead( value ),
                    theMapper.getTimeExpires( value ),
                    data );
            
        } catch( StoreKeyDoesNotExistException ex ) {
            throw new ObjectStoreKeyDoesNotExistException( this, key, ex );
        }
    }

    /**
     * Put (if does not exist already) or update (if it does exist) a data element in the Store.
     *
     * @param key the key under which the data element may already, and will continue to be stored
     * @param value the object to be stored
     * @return true if the value was updated, false if it was put
     * @throws IOException thrown if an I/O error occurred
     *
     * @see #put if a data element with this key does not exist already
     * @see #update if a data element with this key exists already
     */
    public boolean putOrUpdate(
            K key,
            V value )
        throws
            StoreValueEncodingException,
            IOException
    {
        String  stringKey = theMapper.keyToString( key );
        byte [] data      = theMapper.asBytes( value );

        boolean ret = theDelegate.putOrUpdate(
                stringKey,
                theMapper.getPreferredEncodingId(),
                theMapper.getTimeCreated( value ),
                theMapper.getTimeUpdated( value ),
                theMapper.getTimeRead(    value ),
                theMapper.getTimeExpires( value ),
                data );
        
        return ret;
    }

    /**
     * Obtain an object from the Store, given a key.
     *
     * @param key the key to the object in the Store
     * @return the object
     * @throws StoreKeyDoesNotExistException thrown if currently there is no data element in the Store using this key
     */
    public V get(
            K key )
        throws
            ObjectStoreKeyDoesNotExistException,
            StoreValueDecodingException,
            IOException
    {
        String stringKey = theMapper.keyToString( key );

        try {
            StoreValue v = theDelegate.get( stringKey );

            V ret = theMapper.decodeValue( key, v );
            return ret;

        } catch( StoreKeyDoesNotExistException ex ) {
            throw new ObjectStoreKeyDoesNotExistException( this, key, ex );
        }
    }

    /**
     * Delete the object that is stored using this key.
     *
     * @param key the key to the object in the Store
     * @throws StoreKeyDoesNotExistException thrown if currently there is no data element in the Store using this key
     */
    public void delete(
            K key )
        throws
            ObjectStoreKeyDoesNotExistException,
            IOException
    {
        String stringKey = theMapper.keyToString( key );

        try {
            theDelegate.delete( stringKey );

        } catch( StoreKeyDoesNotExistException ex ) {
            throw new ObjectStoreKeyDoesNotExistException( this, key, ex );
        }
    }
    
    /**
     * The StoreEntryMapper.
     */
    protected StoreEntryMapper<K, V> theMapper;
    
    /**
     * The underlying Store.
     */
    protected Store theDelegate;
}
