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

package org.infogrid.store;

import java.util.ArrayList;

/**
 * An abstract implementation of <code>Store</code> that captures the event
 * management functionality of the <code>Store</code> interface.
 */
public abstract class AbstractStore
        implements
            Store
{
    /**
     * Add a StoreListener.
     *
     * @param newListener the new StoreListener
     */
    public synchronized void addStoreListener(
            StoreListener newListener )
    {
        if( theStoreListeners == null ) {
            theStoreListeners = new ArrayList<StoreListener>();
        }
        theStoreListeners.add( newListener );
    }
    
    /**
     * Remove a StoreListener.
     *
     * @param oldListener the old StoreListener
     */
    public synchronized void removeStoreListener(
            StoreListener oldListener )
    {
        theStoreListeners.remove( oldListener );
    }

    /**
     * Fire a Store put event.
     *
     * @param key the used key in the Store
     * @param value the corresponding value
     */
    protected void firePutPerformed(
            String     key,
            StoreValue value )
    {
        ArrayList<StoreListener> listeners;
        synchronized( this ) {
            if( theStoreListeners == null || theStoreListeners.isEmpty() ) {
                return;
            }
            listeners = cloneStoreListeners();
        }
        for( StoreListener current : listeners ) {
            current.putPerformed( this, key, value );
        }
    }
    
    /**
     * Fire a Store update event.
     *
     * @param key the used key in the Store
     * @param value the corresponding value
     */
    protected void fireUpdatePerformed(
            String     key,
            StoreValue value )
    {
        ArrayList<StoreListener> listeners;
        synchronized( this ) {
            if( theStoreListeners == null || theStoreListeners.isEmpty() ) {
                return;
            }
            listeners = cloneStoreListeners();
        }
        for( StoreListener current : listeners ) {
            current.updatePerformed( this, key, value );
        }
    }
    
    /**
     * Fire a Store get event.
     *
     * @param key the used key in the Store
     * @param value the corresponding value
     */
    protected void fireGetPerformed(
            String     key,
            StoreValue value )
    {
        ArrayList<StoreListener> listeners;
        synchronized( this ) {
            if( theStoreListeners == null || theStoreListeners.isEmpty() ) {
                return;
            }
            listeners = cloneStoreListeners();
        }
        for( StoreListener current : listeners ) {
            current.getPerformed( this, key, value );
        }
    }
    
    /**
     * Fire a Store delete event.
     *
     * @param key the used key in the Store
     */
    protected void fireDeletePerformed(
            String key )
    {
        ArrayList<StoreListener> listeners;
        synchronized( this ) {
            if( theStoreListeners == null || theStoreListeners.isEmpty() ) {
                return;
            }
            listeners = cloneStoreListeners();
        }
        for( StoreListener current : listeners ) {
            current.deletePerformed( this, key );
        }
    }
    
    /**
     * Fire a Store deleteAll event.
     *
     * @param prefix the prefix, if any, of the keys that were deleted
     */
    protected void fireDeleteAllPerformed(
            String prefix )
    {
        ArrayList<StoreListener> listeners;
        synchronized( this ) {
            if( theStoreListeners == null || theStoreListeners.isEmpty() ) {
                return;
            }
            listeners = cloneStoreListeners();
        }
        for( StoreListener current : listeners ) {
            current.deleteAllPerformed( this, prefix );
        }
    }
    
    /**
     * We factor this out to reduce the number of places where we have to suppress warnings.
     *
     * @return a cloned list of StoreListeners
     */
    @SuppressWarnings(value={"unchecked"})
    protected final ArrayList<StoreListener> cloneStoreListeners()
    {
        return (ArrayList<StoreListener>) theStoreListeners.clone();
    }

    /**
     * The StoreListeners.
     */
    protected ArrayList<StoreListener> theStoreListeners = null;
}
