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

package org.infogrid.store.util;

import java.io.IOException;
import java.lang.ref.Reference;
import java.text.ParseException;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import org.infogrid.store.Store;
import org.infogrid.store.StoreEntryMapper;
import org.infogrid.store.StoreKeyDoesNotExistException;
import org.infogrid.store.StoreValue;
import org.infogrid.store.StoreValueDecodingException;
import org.infogrid.store.StoreValueEncodingException;
import org.infogrid.util.CursorIterator;
import org.infogrid.util.SwappingHashMap;
import org.infogrid.util.logging.CanBeDumped;
import org.infogrid.util.logging.Dumper;
import org.infogrid.util.logging.Log;
import org.infogrid.store.StoreCursor;

/**
 * This is a <code>java.util.Map</code> that stores the values in the <code>Store</code>
 * and keeps only <code>References</code> to them in memory.
 *
 * @param <K> the type of key
 * @param <V> the type of value
 */
public abstract class StoreBackedSwappingHashMap<K,V>
        extends
            SwappingHashMap<K,V>
        implements
            CanBeDumped
{
    private static final Log log = Log.getLogInstance( StoreBackedSwappingHashMap.class  );

    /**
     * Create a <code>StoreBackedSwappingHashMap</code> that uses <code>SoftReferences</code>.
     *
     * @param mapper the <code>StoreEntryMapper</code> to use
     * @param store the underlying <code>Store</code>
     * @return the created <code>StoreBackedSwappingHashMap</code>
     * @param <K> the type of key
     * @param <V> the type of value
     */
    public static <K,V> StoreBackedSwappingHashMap<K,V> createSoft(
            StoreEntryMapper<K,V> mapper,
            Store                 store )
    {
        return createSoft( DEFAULT_INITIAL_CAPACITY, mapper, store );
    }

    /**
     * Create a <code>StoreBackedSwappingHashMap</code> that uses <code>SoftReferences</code>.
     *
     * @param initialSize the initial size of the <code>StoreBackedSwappingHashMap</code>
     * @param mapper the <code>StoreEntryMapper</code> to use
     * @param store the underlying <code>Store</code>
     * @return the created <code>StoreBackedSwappingHashMap</code>
     * @param <K> the type of key
     * @param <V> the type of value
     */
    public static <K,V> StoreBackedSwappingHashMap<K,V> createSoft(
            int                   initialSize,
            StoreEntryMapper<K,V> mapper,
            Store                 store )
    {
        if( mapper == null ) {
            throw new NullPointerException();
        }
        if( store == null ) {
            throw new NullPointerException();
        }
        return new StoreBackedSwappingHashMap<K,V>( initialSize, mapper, store ) {
                @Override
                protected Reference<V> createReference(
                        K key,
                        V value )
                {
                    return new SoftEntryReference<>( key, value, theQueue );
                }
        };
    }

    /**
     * Create a <code>StoreBackedSwappingHashMap</code> that uses <code>WeakReferences</code>.
     *
     * @param mapper the <code>StoreEntryMapper</code> to use
     * @param store the underlying <code>Store</code>
     * @return the created <code>StoreBackedSwappingHashMap</code>
     * @param <K> the type of key
     * @param <V> the type of value
     */
    public static <K,V> StoreBackedSwappingHashMap<K,V> createWeak(
            StoreEntryMapper<K,V> mapper,
            Store                 store )
    {
        return createWeak( DEFAULT_INITIAL_CAPACITY, mapper, store );
    }

    /**
     * Create a <code>StoreBackedSwappingHashMap</code> that uses <code>WeakReferences</code>.
     *
     * @param initialSize the initial size of the <code>StoreBackedSwappingHashMap</code>
     * @param mapper the <code>StoreEntryMapper</code> to use
     * @param store the underlying <code>Store</code>
     * @return the created <code>StoreBackedSwappingHashMap</code>
     * @param <K> the type of key
     * @param <V> the type of value
     */
    public static <K,V> StoreBackedSwappingHashMap<K,V> createWeak(
            int                   initialSize,
            StoreEntryMapper<K,V> mapper,
            Store                 store )
    {
        if( mapper == null ) {
            throw new NullPointerException();
        }
        if( store == null ) {
            throw new NullPointerException();
        }
        return new StoreBackedSwappingHashMap<K,V>( initialSize, mapper, store ) {
                @Override
                protected Reference<V> createReference(
                        K key,
                        V value )
                {
                    return new WeakEntryReference<>( key, value, theQueue );
                }
        };
    }

    /**
     * Constructor.
     *
     * @param initialSize the initial size of the <code>StoreBackedSwappingHashMap</code>
     * @param mapper the <code>StoreEntryMapper</code> to use
     * @param store the underlying <code>Store</code>
     */
    protected StoreBackedSwappingHashMap(
            int                   initialSize,
            StoreEntryMapper<K,V> mapper,
            Store                 store )
    {
        super( initialSize );

        theMapper = mapper;
        theStore  = store;
    }

    /**
     * Obtain the <code>Store</code> that backs this <code>StoreBackedSwappingHashMap</code>.
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
        if( log.isTraceEnabled() ) {
            log.traceMethodCallEntry( this, "get", key, storeValue );
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
                theDelegate.put( key, createReference( key, ret ));
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
        if( log.isTraceEnabled() ) {
            log.traceMethodCallEntry( this, "removeValueFromStorage", key );
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
        int ret = Integer.MAX_VALUE; // not sure what else to return, given that we don't know
        try {
            ret = theStore.size();

        } catch( IOException ex ) {
            log.error( ex );
        }
        return ret;
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return true if this StoreBackedSwappingHashMap is empty
     */
    @Override
    public boolean isEmpty()
    {
        try {
            boolean ret = theStore.isEmpty();
            return ret;

        } catch( IOException ex ) {
            log.error( ex );
            return true;
        }
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
        if( log.isTraceEnabled() ) {
            log.traceMethodCallEntry( this, "loadValueFromStorage", key );
        }

        K      realKey   = (K) key;
        String stringKey = theMapper.keyToString( realKey );

        try {
            StoreValue v = theStore.get( stringKey );

            V ret = theMapper.decodeValue( realKey, v );

            return ret;

        } catch( IOException ex ) {
            log.error( this, ".loadValueFromStorage", key, ex );

        } catch( StoreKeyDoesNotExistException ex ) {
            // no op
            if( log.isDebugEnabled() ) {
                log.debug( this, ".loadValueFromStorage", key, ex );
            }

        } catch( StoreValueDecodingException ex ) {
            log.error( this, ".loadValueFromStorage", key, ex );
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
        if( log.isTraceEnabled() ) {
            log.traceMethodCallEntry( this, "saveValueToStorage", key, newValue );
        }

        try {
            String  stringKey = theMapper.keyToString( key );
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
     * Returns a set view of the keys contained in this map.
     *
     * @return the Set of keys
     */
    @Override
    public Set<K> keySet()
    {
        cleanup();

        if( theKeySet == null ) {
            theKeySet = new MyKeySet<>( theStore, theMapper );
        }
        return theKeySet;
    }

    /**
     * Obtain a CursorIterator on the keys of this Map.
     *
     * @param keyArrayComponentType the class using which arrays of keys are allocated
     * @param valueArrayComponentType the class using which arrays of values are allocated
     * @return the CursorIterator
     */
    @Override
    public CursorIterator<K> keysIterator(
            Class<K> keyArrayComponentType,
            Class<V> valueArrayComponentType )
    {
        StoreCursor delegate = theStore.iterator();

        CursorIterator<K> ret = new StoreBackedSwappingHashMapKeysIterator<>( delegate, this, theMapper, keyArrayComponentType );
        return ret;
    }

    /**
     * Obtain a CursorIterator on the keys of this Map that start with the provided prefix.
     *
     * @param prefix the required prefix for the keys
     * @param keyArrayComponentType the class using which arrays of keys are allocated
     * @param valueArrayComponentType the class using which arrays of values are allocated
     * @return the CursorIterator
     */
    public CursorIterator<K> keysIterator(
            K        prefix,
            Class<K> keyArrayComponentType,
            Class<V> valueArrayComponentType )
    {
        StoreCursor delegate = theStore.iterator( theMapper.keyToString( prefix ));

        CursorIterator<K> ret = new StoreBackedSwappingHashMapKeysIterator<>( delegate, this, theMapper, keyArrayComponentType );
        return ret;
    }

    /**
     * Obtain a CursorIterator on the values of this Map.
     *
     * @param keyArrayComponentType the class using which arrays of keys are allocated
     * @param valueArrayComponentType the class using which arrays of values are allocated
     * @return the CursorIterator
     */
    @Override
    public CursorIterator<V> valuesIterator(
            Class<K> keyArrayComponentType,
            Class<V> valueArrayComponentType )
    {
        StoreCursor delegate = theStore.iterator();

        CursorIterator<V> ret = new StoreBackedSwappingHashMapValuesIterator<>( delegate, this, theMapper, keyArrayComponentType, valueArrayComponentType );
        return ret;
    }

    /**
     * Obtain a CursorIterator on the values of this Map whose keys start with the provided prefix.
     *
     * @param prefix the required prefix for the keys
     * @param keyArrayComponentType the class using which arrays of keys are allocated
     * @param valueArrayComponentType the class using which arrays of values are allocated
     * @return the CursorIterator
     */
    public CursorIterator<V> valuesIterator(
            K        prefix,
            Class<K> keyArrayComponentType,
            Class<V> valueArrayComponentType )
    {
        StoreCursor delegate = theStore.iterator( theMapper.keyToString( prefix ));

        CursorIterator<V> ret = new StoreBackedSwappingHashMapValuesIterator<>( delegate, this, theMapper, keyArrayComponentType, valueArrayComponentType );
        return ret;
    }

    /**
     * Invoked only by objects held in this CachingMap, this enables
     * the held objects to indicate to the CachingMap that they have been updated.
     * Depending on the implementation of the CachingMap, that may cause the
     * CachingMap to write changes to disk, for example.
     *
     * @param key the key
     * @param value the value
     */
    @Override
    public void valueUpdated(
            K key,
            V value )
    {
        saveValueToStorage( key, value );
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
                    "theStore",
                    "theMapper"
                },
                new Object[] {
                    theStore,
                    theMapper,
                } );
    }

    /**
     * The <code>Store</code> by which this <code>StoreBackedSwappingHashMap</code> is backed.
     */
    protected Store theStore;

    /**
     * The Mapper that we use.
     */
    protected StoreEntryMapper<K, V> theMapper;

    /**
     * Set of keys in this IterableStoreBackedSwappingHashMap.
     */
    protected MyKeySet<K,V> theKeySet;

    /**
     * This class is instantiated to create a "projection" of the keys in the Store.
     *
     * @param <K> the type of key
     * @param <V> the type of value
     */
    public static class MyKeySet<K,V>
            extends
                AbstractSet<K>
    {
        /**
         * Constructor.
         *
         * @param store  the underlying Store
         * @param mapper the StoreEntryMapper to use
         */
        public MyKeySet(
                Store                 store ,
                StoreEntryMapper<K,V> mapper )
        {
            theStore  = store;
            theMapper = mapper;
        }

        /**
         * Returns an Iterator over the elements contained in this collection.
         *
         * @return an Iterator over the elements contained in this collection.
         */
        @Override
        public Iterator<K> iterator()
        {
            return new MyKeyIterator<>( theStore.iterator(), theMapper );
        }

        /**
         * Returns the number of elements in this collection.
         *
         * @return the number of elements in this collection.
         */
        @Override
        public int size()
        {
            try {
                return theStore.size();

            } catch( IOException ex ) {
                log.error( ex );
                return 0;
            }
        }

        /**
         * The underlying IterableStoreBackedSwappingHashMap.
         */
        protected StoreBackedSwappingHashMap<K,V> theDelegate;

        /**
         * The underlying Store.
         */
        protected Store theStore;

        /**
         * The underlying Mapper.
         */
        protected StoreEntryMapper<K,V> theMapper;
    }

    /**
     * Iterator over the MyValueCollection.
     *
     * @param <K> the type of key
     * @param <V> the type of value
     */
    public static class MyKeyIterator<K,V>
            implements
                Iterator<K>
    {
        /**
         * Constructor.
         *
         * @param delegate the IterableStoreCursor on the underlying Store
         * @param mapper   the StoreEntryMapper to use
         */
        public MyKeyIterator(
                StoreCursor   delegate,
                StoreEntryMapper<K,V> mapper )
        {
            theDelegate = delegate;
            theMapper   = mapper;
        }

        /**
         * Are there more elements?
         *
         * @return true if there are
         */
        @Override
        public boolean hasNext()
        {
            return theDelegate.hasNext();
        }

        /**
         * Obtain the next element.
         *
         * @return the next element
         */
        @Override
        public K next()
        {
            try {
                StoreValue value = theDelegate.next();
                K          ret   = theMapper.stringToKey( value.getKey() );
                return ret;

            } catch( ParseException ex ) {
                log.error( ex );
                return null;
            }
        }

        /**
         * Remove the current element
         *
         * @throws UnsupportedOperationException
         */
        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        /**
         * The underlying Iterator.
         */
        protected StoreCursor theDelegate;

        /**
         * The underlying Mapper.
         */
        protected StoreEntryMapper<K,V> theMapper;
    }
}
