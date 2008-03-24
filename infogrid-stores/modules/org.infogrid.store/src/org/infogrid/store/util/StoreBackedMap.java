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

import org.infogrid.store.IterableStore;
import org.infogrid.store.Store;
import org.infogrid.store.StoreEntryMapper;
import org.infogrid.store.StoreKeyDoesNotExistException;
import org.infogrid.store.StoreValue;
import org.infogrid.store.StoreValueDecodingException;
import org.infogrid.store.StoreValueEncodingException;

import org.infogrid.util.CursorIterator;
import org.infogrid.util.MapCursorIterator;
import org.infogrid.util.SwappingHashMap;
import org.infogrid.util.logging.Log;

import java.io.IOException;
import java.lang.ref.Reference;

/**
 * This is a <code>java.util.Map</code> that stores the values in the <code>Store</code>
 * and keeps only <code>References</code> to them in memory.
 */
public abstract class StoreBackedMap<K,V>
        extends
            SwappingHashMap<K,V>
{
    private static final Log log = Log.getLogInstance( StoreBackedMap.class );

    /**
     * Create a <code>StoreBackedMap</code> that uses <code>SoftReferences</code>.
     * 
     * @param mapper the <code>StoreEntryMapper</code> to use
     * @param store the underlying <code>Store</code>
     * @return the created <code>StoreBackedMap</code>
     */
    public static <K,V> StoreBackedMap<K,V> createSoft(
            StoreEntryMapper<K, V> mapper,
            Store            store )
    {
        return createSoft( DEFAULT_INITIAL_CAPACITY, mapper, store );
    }
    
    /**
     * Create a <code>StoreBackedMap</code> that uses <code>SoftReferences</code>.
     * 
     * @param initialSize the initial size of the <code>StoreBackedMap</code>
     * @param mapper the <code>StoreEntryMapper</code> to use
     * @param store the underlying <code>Store</code>
     * @return the created <code>StoreBackedMap</code>
     */
    public static <K,V> StoreBackedMap<K,V> createSoft(
            int              initialSize,
            StoreEntryMapper<K, V> mapper,
            Store            store )
    {
        return new StoreBackedMap<K,V>( initialSize, mapper, store ) {
                protected Reference<V> createReference(
                        K key,
                        V value )
                {
                    return new SoftEntryReference<K,V>( key, value, theQueue );
                }
        };
    }
    
    /**
     * Create a <code>StoreBackedMap</code> that uses <code>WeakReferences</code>.
     * 
     * @param mapper the <code>StoreEntryMapper</code> to use
     * @param store the underlying <code>Store</code>
     * @return the created <code>StoreBackedMap</code>
     */
    public static <K,V> StoreBackedMap<K,V> createWeak(
            StoreEntryMapper<K,V> mapper,
            Store                 store )
    {
        return createWeak( DEFAULT_INITIAL_CAPACITY, mapper, store );
    }
    
    /**
     * Create a <code>StoreBackedMap</code> that uses <code>WeakReferences</code>.
     * 
     * @param initialSize the initial size of the <code>StoreBackedMap</code>
     * @param mapper the <code>StoreEntryMapper</code> to use
     * @param store the underlying <code>Store</code>
     * @return the created <code>StoreBackedMap</code>
     */
    public static <K,V,A> StoreBackedMap<K,V> createWeak(
            int                   initialSize,
            StoreEntryMapper<K,V> mapper,
            Store                 store )
    {
        return new StoreBackedMap<K,V>( initialSize, mapper, store ) {
                protected Reference<V> createReference(
                        K key,
                        V value )
                {
                    return new WeakEntryReference<K,V>( key, value, theQueue );
                }
        };
    }
    
    /**
     * Constructor.
     * 
     * @param initialSize the initial size of the <code>StoreBackedMap</code>
     * @param mapper the <code>StoreEntryMapper</code> to use
     * @param store the underlying <code>Store</code>
     */
    protected StoreBackedMap(
            int                   initialSize,
            StoreEntryMapper<K,V> mapper,
            Store                 store )
    {
        super( initialSize );

        theMapper = mapper;
        theStore  = store;
    }
    
    /**
     * Obtain the <code>Store</code> that backs this <code>StoreBackedMap</code>.
     *
     * @return the Store
     */
    public Store getStore()
    {
        return theStore;
    }

    /**
     * Obtain a value from the cache, but instead of re-reading it from storage
     * if it isn't in the map, use this storage representation instead that
     * we have already.
     *
     * @param key the key
     * @param storeValue the store representation of the value
     * @return the value, if any
     */
    public V get(
            K          key,
            StoreValue storeValue )
    {
        if( log.isDebugEnabled() ) {
            log.debug( this + ".get( " + key + ", " + storeValue + " )" );
        }
        cleanup();
        Reference<V> found = theDelegate.get( key );
        V ret = found != null ? found.get() : null;
        
        if( ret == null ) {
            try {
                ret = theMapper.decodeValue( key, storeValue );

            } catch( StoreValueDecodingException ex ) {
                log.error( ex );
            }
            if( ret != null ) {
                theDelegate.put( key, createReference( (K) key, ret ));
            }
        }
        return ret;        
    }

    /**
     * This method is overridden, to be notified when a value has been
     * removed from this SwappingHashMap.
     *
     * @param key the key whose value has been removed
     */
    @SuppressWarnings(value={"unchecked"})
    @Override
    protected void removeValueFromStorage(
            Object key )
    {
        if( log.isDebugEnabled() ) {
            log.debug( this + ".removeValueFromStorage( " + key + " )" );
        }

        String stringKey = theMapper.keyToString( (K) key );
        
        try {
            theStore.delete( stringKey );

            super.removeValueFromStorage( key );

        } catch( IOException ex ) {
            log.error( ex );
        } catch( StoreKeyDoesNotExistException ex ) {
            log.error( ex );
        }
    }

    /**
     * Returns the number of key-value mappings in this map. This implementation returns
     * <code>Integer.MAX_VALUE</code> if the underlying <code>Store</code> is not an
     * <code>IterableStore</code>.
     *
     * @return the size of the Store
     */
    @Override
    public int size()
    {
        int ret;
        if( theStore instanceof IterableStore ) {
            ret = ((IterableStore)theStore).size();
        } else {
            ret = Integer.MAX_VALUE; // not sure what else to return, given that we don't know
        }
        return ret;
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return true if this StoreBackedMap is empty
     */
    @Override
    public boolean isEmpty()
    {
        return size() == 0;
    }

    /**
     * This method is overridden, to swap in a value that currently
     * is not contained in the local cache.
     *
     * @param key the key whose value should be loaded
     * @return the value that was loaded, or null if none.
     */
    @SuppressWarnings(value={"unchecked"})
    @Override
    protected V loadValueFromStorage(
            Object key )
    {
        if( log.isDebugEnabled() ) {
            log.debug( this + ".loadValueFromStorage( " + key + " )" );
        }

        K      realKey   = (K) key;
        String stringKey = theMapper.keyToString( realKey );
        
        try {
            StoreValue v = theStore.get( stringKey );

            V ret = theMapper.decodeValue( realKey, v );
        
            return ret;

        } catch( IOException ex ) {
            log.error( ex );

        } catch( StoreKeyDoesNotExistException ex ) {
            // no op

        } catch( StoreValueDecodingException ex ) {
            log.error( ex );
        }
        return null;
    }

    /**
     * This method may be overridden by subclasses, to update a value in the external
     * store that has been updated.
     *
     * @param key the key whose value was updated
     * @param newValue the new value
     */
    @Override
    protected void saveValueToStorage(
            K key,
            V newValue )
    {
        if( log.isDebugEnabled() ) {
            log.debug( this + ".saveValueToStorage( " + key + ", " + newValue + " )" );
        }

        try {
            String  stringKey = theMapper.keyToString( (K) key );
            byte [] data      = theMapper.asBytes( newValue ); 
        
            theStore.putOrUpdate(
                    stringKey,
                    theMapper.getPreferredEncodingId(),
                    theMapper.getTimeCreated( newValue ),
                    theMapper.getTimeUpdated( newValue ),
                    theMapper.getTimeRead( newValue ),
                    theMapper.getTimeExpires( newValue ),
                    data );

        } catch( IOException ex ) {
            log.error( ex );

        } catch( StoreValueEncodingException ex ) {
            log.error( ex );
        }
    }
    
    /**
     * This method may be overridden by subclasses, to be notified when all
     * elements have been removed from this Map.
     */
    protected void cleared()
    {
        try {
            theStore.deleteAll();

        } catch( IOException ex ) {
            log.error( ex );
        }
    }
    
    /**
     * Obtain a CursorIterator on the keys of this Map.
     *
     * @param keyArrayComponentType the class using which arrays of keys are allocated
     * @param valueArrayComponentType the class using which arrays of values are allocated
     * @return the CursorIterator
     */
    public CursorIterator<K> keysIterator(
            Class<K> keyArrayComponentType,
            Class<V> valueArrayComponentType )
    {
        CursorIterator<K> ret = MapCursorIterator.<K,V>createForKeys( this, keyArrayComponentType, valueArrayComponentType );
        return ret;
    }
    
    /**
     * Obtain a CursorIterator on the values of this Map.
     *
     * @param keyArrayComponentType the class using which arrays of keys are allocated
     * @param valueArrayComponentType the class using which arrays of values are allocated
     * @return the CursorIterator
     */
    public CursorIterator<V> valuesIterator(
            Class<K> keyArrayComponentType,
            Class<V> valueArrayComponentType )
    {
        CursorIterator<V> ret = MapCursorIterator.<K,V>createForValues( this, keyArrayComponentType, valueArrayComponentType );
        return ret;
    }

    /**
     * Invoked only by objects held in this CachingMap, this enables
     * the held objects to indicate to the CachingMap that they have been updated.
     * Depending on the implementation of the CachingMap, that may cause the
     * CachingMap to write changes to disk, for example.
     *
     * @param key the key
     * @Param value the value
     */
    public void valueUpdated(
            K key,
            V value )
    {
        saveValueToStorage( key, value );
    }

    /**
     * The <code>Store</code> by which this <code>StoreBackedMap</code> is backed.
     */
    protected Store theStore;

    /**
     * The Mapper that we use.
     */
    protected StoreEntryMapper<K, V> theMapper;
}
