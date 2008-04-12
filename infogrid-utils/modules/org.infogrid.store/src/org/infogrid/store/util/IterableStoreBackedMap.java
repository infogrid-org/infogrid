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
import org.infogrid.store.IterableStoreCursor;
import org.infogrid.store.StoreEntryMapper;
import org.infogrid.store.StoreValue;
import org.infogrid.util.CursorIterator;

import java.lang.ref.Reference;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This is a <code>java.util.Map</code> that stores the values in the
 * {@link org.infogrid.store.IterableStore IterableStore}
 * and keeps only <code>References</code> to them in memory.
 * 
 * @param K the type of key
 * @param V the type of value
 */
public abstract class IterableStoreBackedMap<K,V>
        extends
            StoreBackedMap<K,V>
{
    /**
     * Create a <code>IterableStoreBackedMap</code> that uses <code>SoftReferences</code>.
     * 
     * @param mapper the <code>SStoreEntryMapper/code> to use
     * @param store the underlying <code>IterableStore</code>
     * @return the created <code>StoreBackedMap</code>
     */
    public static <K,V> IterableStoreBackedMap<K,V> createSoft(
            StoreEntryMapper<K,V> mapper,
            IterableStore         store )
    {
        return createSoft( DEFAULT_INITIAL_CAPACITY, mapper, store );
    }
    
    /**
     * Create a <code>IterableStoreBackedMap</code> that uses <code>SoftReferences</code>.
     * 
     * @param initialSize the initial size of the <code>IterableStoreBackedMap</code>
     * @param mapper the <code>StStoreEntryMappercode> to use
     * @param store the underlying <code>IterableStore</code>
     * @return the created <code>StoreBackedMap</code>
     */
    public static <K,V> IterableStoreBackedMap<K,V> createSoft(
            int                   initialSize,
            StoreEntryMapper<K,V> mapper,
            IterableStore         store )
    {
        return new IterableStoreBackedMap<K,V>( initialSize, mapper, store ) {
                protected Reference<V> createReference(
                        K key,
                        V value )
                {
                    return new SoftEntryReference<K,V>( key, value, theQueue );
                }
        };
    }
    
    /**
     * Create a <code>IterableStoreBackedMap</code> that uses <code>WeakReferences</code>.
     * 
     * @param mapper the <code>SStoreEntryMapper/code> to use
     * @param store the underlying <code>IterableStore</code>
     * @return the created <code>IterableStoreBackedMap</code>
     */
    public static <K,V> IterableStoreBackedMap<K,V> createWeak(
            StoreEntryMapper<K,V> mapper,
            IterableStore         store )
    {
        return createWeak( DEFAULT_INITIAL_CAPACITY, mapper, store );
    }
    
    /**
     * Create a <code>IterableStoreBackedMap</code> that uses <code>WeakReferences</code>.
     * 
     * @param initialSize the initial size of the <code>IterableStoreBackedMap</code>
     * @param mapper the <code>StStoreEntryMappercode> to use
     * @param store the underlying <code>IterableStore</code>
     * @return the created <code>IterableStoreBackedMap</code>
     */
    public static <K,V,A> IterableStoreBackedMap<K,V> createWeak(
            int                   initialSize,
            StoreEntryMapper<K,V> mapper,
            IterableStore         store )
    {
        return new IterableStoreBackedMap<K,V>( initialSize, mapper, store ) {
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
     * @param initialSize the initial size of the <code>IterableStoreBackedMap</code>
     * @param mapper the <code>StStoreEntryMappercode> to use
     * @param store the underlying <code>IterableStore</code>
     */
    protected IterableStoreBackedMap(
            int                   initialSize,
            StoreEntryMapper<K,V> mapper,
            IterableStore         store )
    {
        super( initialSize, mapper, store );
    }

    /**
     * Obtain the <code>IterableStore</code> that backs this <code>IterableStoreBackedMap</code>.
     *
     * @return the IterableStore
     */
    @Override
    public IterableStore getStore()
    {
        return (IterableStore) theStore;
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
        int ret = ((IterableStore)theStore).size();
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
        boolean ret = ((IterableStore)theStore).isEmpty();
        return ret;
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
            theKeySet = new MyKeySet<K,V>( this );
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
        IterableStoreCursor delegate = ((IterableStore)theStore).iterator();
        
        CursorIterator<K> ret = new StoreBackedMapKeysIterator<K,V>( delegate, this, theMapper, keyArrayComponentType );
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
        IterableStoreCursor delegate = ((IterableStore)theStore).iterator();
        
        CursorIterator<V> ret = new StoreBackedMapValuesIterator<K,V>( delegate, this, theMapper, keyArrayComponentType, valueArrayComponentType );
        return ret;
    }

    /**
     * Set of keys in this IterableStoreBackedMap.
     */
    protected MyKeySet<K,V> theKeySet;
    
    /**
     * This class is instantiated to create a "projection" of the keys in the Store.
     * 
     * @param K the type of key
     * @param V the type of value
     */
    protected static class MyKeySet<K,V>
            extends
                AbstractSet<K>
    {
        /**
         * Constructor.
         *
         * @param map the underlying map
         */
        public MyKeySet(
                IterableStoreBackedMap<K,V> map )
        {
            theDelegate = map;
        }
        
        /**
         * Returns an Iterator over the elements contained in this collection.
         *
         * @return an Iterator over the elements contained in this collection.
         */
        public Iterator<K> iterator()
        {
            return new MyKeyIterator<K,V>( theDelegate );
        }

        /**
         * Returns the number of elements in this collection.
         *
         * @return the number of elements in this collection.
         */
        public int size()
        {
            IterableStore store = (IterableStore) theDelegate.theStore;
            return store.size();
        }

        /**
         * The underlying IterableStoreBackedMap.
         */
        protected IterableStoreBackedMap<K,V> theDelegate;
    }

    /**
     * Iterator over the MyValueCollection.
     * 
     * @param K the type of key
     * @param V the type of value
     */
    static class MyKeyIterator<K,V>
            implements
                Iterator<K>
    {
        /**
         * Constructor.
         *
         * @param map the SwappingHashMap over whose values we iterate
         */
        public MyKeyIterator(
                IterableStoreBackedMap<K,V> map )
        {
            theMap      = map;
            theDelegate = theMap.getStore().iterator();
        }

        /**
         * Are there more elements?
         *
         * @return true if there are
         */
        public boolean hasNext()
        {
            return theDelegate.hasNext();
        }
        
        /**
         * Obtain the next element.
         *
         * @return the next element
         */
        public K next()
        {
            StoreValue value = theDelegate.next();
            K          ret   = theMap.theMapper.stringToKey( value.getKey() );
            return ret;
        }

        /**
         * Remove the current element
         *
         * @throws UnsupportedOperationException
         */
        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        /**
         * The underlying Iterator.
         */
        protected IterableStoreCursor theDelegate;

        /**
         * The underlying IterableStoreBackedMap.
         */
        protected IterableStoreBackedMap<K,V> theMap;
    }
}
