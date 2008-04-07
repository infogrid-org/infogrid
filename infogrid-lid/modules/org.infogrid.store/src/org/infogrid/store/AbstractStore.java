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

import org.infogrid.util.FlexibleListenerSet;

/**
 * An abstract implementation of <code>Store</code> that captures the event
 * management functionality of the <code>Store</code> interface.
 */
public abstract class AbstractStore
        implements
            Store
{

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
    public void addDirectStoreListener(
            StoreListener newListener )
    {
        theStoreListeners.addDirect( newListener );
    }

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
    public void addSoftStoreListener(
            StoreListener newListener )
    {
        theStoreListeners.addSoft( newListener );
    }

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
    public void addWeakStoreListener(
            StoreListener newListener )
    {
        theStoreListeners.addWeak( newListener );
    }

    /**
      * Remove a listener.
      * This method is the same regardless how the listener was subscribed to events.
      * 
      * @param oldListener the to-be-removed listener
      * @see #addDirectStoreListener
      * @see #addSoftStoreListener
      * @see #addWeakStoreListener
      */
    public void removeStoreListener(
            StoreListener oldListener )
    {
        theStoreListeners.remove( oldListener );
    }

    /**
     * Fire a Store put event.
     *
     * @param value the value put into the Store
     */
    protected void firePutPerformed(
            StoreValue value )
    {
        theStoreListeners.fireEvent( value, 0 );
    }
    
    /**
     * Fire a Store update event.
     *
     * @param value the value updated into the Store
     */
    protected void fireUpdatePerformed(
            StoreValue value )
    {
        theStoreListeners.fireEvent( value, 1 );
    }
    
    /**
     * Fire a Store get successed event.
     *
     * @param value the obtained value
     */
    protected void fireGetPerformed(
            StoreValue value )
    {
        theStoreListeners.fireEvent( value, 2 );
    }
    
    /**
     * Fire a Store get failed event.
     *
     * @param key the key that failed
     */
    protected void fireGetFailed(
            String key )
    {
        theStoreListeners.fireEvent( key, 3 );
    }
    
    /**
     * Fire a Store delete event.
     *
     * @param key the key that was deleted
     */
    protected void fireDeletePerformed(
            String key )
    {
        theStoreListeners.fireEvent( key, 4 );
    }
    
    /**
     * Fire a Store deleteAll event.
     *
     * @param prefix the prefix if all the keys that were deleted
     */
    protected void fireDeleteAllPerformed(
            String prefix )
    {
        theStoreListeners.fireEvent( prefix, 5 );
    }
    
    /**
     * The StoreListeners.
     */
    private FlexibleListenerSet<StoreListener,Object,Integer> theStoreListeners
            = new FlexibleListenerSet<StoreListener,Object,Integer>() {
                    protected void fireEventToListener(
                            StoreListener l,
                            Object        e,
                            Integer       p )
                    {
                        switch( p.intValue() ) {
                            case 0:
                                l.putPerformed( AbstractStore.this, (StoreValue) e );
                                break;

                            case 1:
                                l.updatePerformed( AbstractStore.this, (StoreValue) e );
                                break;

                            case 2:
                                l.getPerformed( AbstractStore.this, (StoreValue) e );
                                break;

                            case 3:
                                l.getFailed( AbstractStore.this, (String) e );
                                break;

                            case 4:
                                l.deletePerformed( AbstractStore.this, (String) e );
                                break;

                            case 5:
                                l.deleteAllPerformed( AbstractStore.this, (String) e );
                                break;
                        }
                    }
    };
}
