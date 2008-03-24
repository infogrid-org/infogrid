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

import org.infogrid.util.logging.Log;

import java.beans.*;
import java.lang.ref.*;
import java.util.*;

/**
 * A simple SmartFactory implementation. The obtainFor method in this
 * class assumes that object creation by the delegate Factory is fast. If it is not,
 * use {@link PatientSmartFactory PatientSmartFactory} instead of this class.
 */
public class MSmartFactory<K,V,A>
        implements
            SmartFactory<K,V,A>
{
    private static final Log log = Log.getLogInstance( MSmartFactory.class ); // our own, private logger

    /**
     * Factory method for SmartFactory that stores its objects in the provided storage object.
     *
     * @param delegateFactory the Factory that knows how to instantiate values
     * @param storage the storage to use
     * @return the created SmartFactory
     */
    public static <K,V,A> MSmartFactory<K, V, A> create(
            Factory<K,V,A>  delegateFactory,
            CachingMap<K,V> storage )
    {
        return new MSmartFactory<K,V,A>( delegateFactory, storage );
    }

    /**
     * Convenience factory method for SmartFactory that stores its objects in memory.
     *
     * @param delegateFactory the Factory that knows how to instantiate values
     * @return the created SmartFactory
     */
    public static <K,V,A> MSmartFactory<K, V, A> createDirect(
            Factory<K,V,A> delegateFactory )
    {
        MCachingHashMap<K,V> storage = MCachingHashMap.create();
        return new MSmartFactory<K,V,A>( delegateFactory, storage );
    }

    /**
     * Convenience factory method for SmartFactory that stores its objects using SoftReferences.
     *
     * @param delegateFactory the Factory that knows how to instantiate values
     * @return the created SmartFactory
     */
    public static <K,V,A> MSmartFactory<K, V, A> createSoftReference(
            Factory<K,V,A> delegateFactory )
    {
        CachingMap<K,V> storage = MSwappingHashMap.createSoft();
        return new MSmartFactory<K,V,A>( delegateFactory, storage );
    }

    /**
     * Convenience factory method for SmartFactory that stores its objects using WeakReferences.
     *
     * @param delegateFactory the Factory that knows how to instantiate values
     * @return the created SmartFactory
     */
    public static <K,V,A> MSmartFactory<K, V, A> createWeakReference(
            Factory<K,V,A> delegateFactory )
    {
        CachingMap<K,V> storage = MSwappingHashMap.createWeak();
        return new MSmartFactory<K,V,A>( delegateFactory, storage );
    }

    /**
     * Constructor.
     *
     * @param delegateFactory the Factory that knows how to instantiate values
     * @param storage the storage to use for this instance
     */
    public MSmartFactory(
            Factory<K,V,A>  delegateFactory,
            CachingMap<K,V> storage )
    {
        theDelegateFactory  = delegateFactory;
        theKeyValueMap      = storage;
    }
    
    /**
     * Obtain the delegate Factory that knows how to instantiate values.
     * 
     * @return the delegate Factory
     */
    public Factory<K,V,A> getDelegateFactory()
    {
        return theDelegateFactory;
    }

    /**
     * Obtain an already existing value for a provided key.
     *
     * @param key the key for which we want to obtain a value
     * @return the already existing value, or null
     */
    public V get(
            K key )
    {
        if( log.isDebugEnabled() ) {
            log.debug( this + ".get( " + key + " )" );
        }
        synchronized( theKeyValueMap ) {
            return theKeyValueMap.get( key );
        }
    }

    /**
     * Obtain the keys for an existing value. This is the opposite operation
     * of {@see #get}. Depending on the implementation of this interface,
     * this operation may take a long time.
     * 
     * @param v the value whose keys need to be found
     * @return an Iterator over the keys
     */
    public Iterator<K> reverseGet(
            V value )
    {
        // Default implementation
        if( log.isDebugEnabled() ) {
            log.debug( this + ".reverseGet( " + value + " )" );
        }
        ArrayList<K> ret = new ArrayList<K>();

        synchronized( theKeyValueMap ) {
            for( K key : theKeyValueMap.keySet() ) {
                V found = theKeyValueMap.get( key );
                if( value == found ) {
                    ret.add( key );
                }
            }
        }
        return ret.iterator();
    }

    /**
     * Create a new, or obtain an already existing value for a provided key.
     * This may be overridden by subclasses.
     *
     * @param key the key for which we want to obtain a value
     * @param argument optional argument to pass through to the createFor method
     * @return the found or created value for this key
     * @throws Exception catch-all Exception
     */
    @SuppressWarnings(value={"unchecked"})
    public V obtainFor(
            K key,
            A argument )
        throws
            FactoryException
    {
        if( log.isDebugEnabled() ) {
            log.debug( this + ".obtainFor( " + key + ", " + argument + " )" );
        }

        V ret;
        boolean weAreCreating = false;

        synchronized( theKeyValueMap ) {
            ret = theKeyValueMap.get( key );
            if( ret == null ) {
                weAreCreating = true;
                ret = theDelegateFactory.obtainFor( key, argument );
                
                if( ret instanceof FactoryCreatedObject ) {
                    ((FactoryCreatedObject<K,V,A>) ret).setFactory( this );
                }
                theKeyValueMap.put( key, ret );
            }
        }
        if( weAreCreating ) {
            createdHook( key, ret, argument );
        }
        return ret;
    }

    /**
     * This overridable method allows our subclasses to invoke particular functionality
     * every time this SmartFactory created a new value by invoking the delegate Factory.
     * It is not invoked for those returned values that are merely retrieved from
     * the storage in the smart factory.
     * 
     * @param key the key of the newly created value
     * @param value the newly created value
     * @param argument the argument into the creation of the newly created value
     */
    protected void createdHook(
            K key,
            V value,
            A argument )
    {
        // noop on this level
    }

    /**
     * Add a new key and a new value without going through the obtain method.
     * FIXME: This does not deal with a concurrent invocation of obtainFor for the same key
     *
     * @param key the key
     * @param value the value for the key
     * @return the old value at this key, if any
     */
    public final V put(
            K key,
            V value )
    {
        if( log.isDebugEnabled() ) {
            log.debug( this + ".put( " + key + ", " + value + " )" );
        }

        V ret;
        synchronized( theKeyValueMap ) {
            ret = theKeyValueMap.put( key, value );
        }
        return ret;
    }

    /**
     * Obtain a set of the currently known keys. This follows the pattern in
     * java.util.HashMap.
     *
     * @return a set of the currently known keys
     */
    public final Set<K> keySet()
    {
        synchronized( theKeyValueMap ) {
            return theKeyValueMap.keySet();
        }
    }

    /**
     * Obtain a Collection of the currently known values. This follows the pattern in
     * java.util.HashMap.
     *
     * @return a set of the currently known values
     */
    public final Collection<V> values()
    {
        synchronized( theKeyValueMap ) {
            return theKeyValueMap.values();
        }
    }

    /**
     * Remove a key-value pair that was previously created. This does not affect
     * values that are currently still being constructed. The semantics of
     * &quot;remove&quot; for a SmartFactory imply &quot;deletion&quot; of the
     * object as well. Correspondingly, if the removed Object supports the
     * {@link org.infogrid.util.LiveDeadObject LiveDeadObject} object, this
     * implementation will invoke the <code>die()</code> method there.
     *
     * @param key the key of the key-value pair to be removed
     * @return the value of the key-value pair to be removed, if found
     */
    public final V remove(
            K key )
    {
        Invocable<V,Void> code = new Invocable<V,Void>() {
                public Void invoke(
                        V toRemove )
                {
                    if( toRemove instanceof LiveDeadObject ) {
                        ((LiveDeadObject)toRemove).die();
                    }
                    return null;
                }
        };
        return remove( key, code );
    }

    /**
     * Remove a key-value pair that was previously created. This does not affect
     * values that are currently still being constructed. The semantics of
     * &quot;remove&quot; for a SmartFactory imply &quot;deletion&quot; of the
     * object as well. The provided cleanupCode can be used to implement those
     * semantics, e.g. in order to invoke the die() method.
     *
     * @param key the key of the key-value pair to be removed
     * @param cleanupCode the cleanup code to run, if any
     * @return the value of the key-value pair to be removed, if found
     */
    public V remove(
            K                 key,
            Invocable<V,Void> cleanupCode )
    {    
        if( log.isDebugEnabled() ) {
            log.debug( this + ".remove( " + key + ", " + cleanupCode + " )" );
        }

        V ret;
        synchronized( theKeyValueMap ) {
            ret = theKeyValueMap.remove( key );

            if( cleanupCode != null && ret != null ) {
                cleanupCode.invoke( ret );
            }
        }
        return ret;
    }

    /**
     * Determine whether the number of key-value pairs in this SmartFactory is zero.
     *
     * @return true if there are no key-value paris in this SmartFactory
     */
    public boolean isEmpty()
    {
        return theKeyValueMap.isEmpty();
    }

    /**
     * The number of known key-value pairs in this SmartFactory at this time.
     *
     * @return the number of known key-value pairs
     */
    public final int size()
    {
        synchronized( theKeyValueMap ) {
            return theKeyValueMap.size();
        }
    }

    /**
     * Invoked only by objects that have been created by this SmartFactory, this enables
     * the created objects to indicate to the SmartFactory that they have been updated.
     * Depending on the implementation of the SmartFactory, that may cause the
     * SmartFactory to write changes to disk, for example.
     *
     * @param object the FactoryCreatedObject
     */
    @SuppressWarnings(value={"unchecked"})
    public void factoryCreatedObjectUpdated(
            FactoryCreatedObject<K,V,A> object )
    {
        theKeyValueMap.valueUpdated( object.getFactoryKey(), (V) object ); // FIXME? This typecast can probably be done better
    }

    /**
     * Obtain the underlying storage CachingMap.
     *x
     * @return the underlying storage CachingMap
     */
    public CachingMap<K,V> getStorage()
    {
        return theKeyValueMap;
    }

    /**
     * The delegate Factory.
     */
    protected Factory<K,V,A> theDelegateFactory;

    /**
      * Our current values, keyed by our keys.
      */
    protected CachingMap<K,V> theKeyValueMap;
}
