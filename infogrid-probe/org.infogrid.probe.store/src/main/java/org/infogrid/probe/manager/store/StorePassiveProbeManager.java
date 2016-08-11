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

package org.infogrid.probe.manager.store;

import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.probe.ProbeDirectory;
import org.infogrid.probe.manager.AbstractProbeManager;
import org.infogrid.probe.manager.PassiveProbeManager;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.probe.shadow.store.StoreShadowMeshBaseFactory;
import org.infogrid.store.Store;
import org.infogrid.store.util.StoreBackedSwappingHashMap;
import org.infogrid.util.AbstractSwappingHashMapListener;
import org.infogrid.util.SwappingHashMap;

/**
 * Store implementation of PassiveProbeManager.
 */
public class StorePassiveProbeManager
        extends
            AbstractProbeManager
        implements
            PassiveProbeManager
{
    /**
     * Factory method.
     *
     * @param delegate the underlying factory for StoreShadowMeshBases
     * @param dir the ProbeDirectory to use
     * @param shadowStore the Store in which serialized ShadowMeshBases are kept
     * @return the created StorePassiveProbeManager
     */
    public static StorePassiveProbeManager create(
            StoreShadowMeshBaseFactory delegate,
            ProbeDirectory             dir,
            Store                      shadowStore )
    {
        StoreProbeManagerMapper theMapper = new StoreProbeManagerMapper( delegate );

        StoreBackedSwappingHashMap<NetMeshBaseIdentifier,ShadowMeshBase> storage = StoreBackedSwappingHashMap.createWeak( theMapper, shadowStore );

        StorePassiveProbeManager ret = new StorePassiveProbeManager( delegate, storage, dir );

        return ret;
    }

    /**
     * Private constructor, use factory method.
     *
     * @param delegate the underlying factory for StoreShadowMeshBases
     * @param storage the storage to use
     * @param dir the ProbeDirectory to use
     */
    protected StorePassiveProbeManager(
            StoreShadowMeshBaseFactory                                       delegate,
            StoreBackedSwappingHashMap<NetMeshBaseIdentifier,ShadowMeshBase> storage,
            ProbeDirectory                                                   dir )
    {
        super( delegate, storage, dir );

        theMapListener = new MyMapListener();
        storage.addWeakSwappingHashMapListener( theMapListener ); // this must be weak
    }

    /**
     * We are not needed any more.
     *
     * @param isPermanent true if this is permanent
     */
    @Override
    public void die(
            boolean isPermanent )
    {
        // no op
    }

    /**
     * Keep a reference to the listener to avoid it being garbage-collected.
     */
    protected MyMapListener theMapListener;

    /**
     * Patches Proxies restored from disk before they are returned.
     */
    protected class MyMapListener
            extends
                AbstractSwappingHashMapListener<NetMeshBaseIdentifier,ShadowMeshBase>
    {
        /**
         * A key-value pair has been loaded from storage.
         *
         * @param source the SwappingHashMap that emitted this event
         * @param key the key of the key-value pair that was loaded
         * @param value the value of the key-value pair that was saved
         */
        @Override
        public void loadedFromStorage(
                SwappingHashMap<NetMeshBaseIdentifier,ShadowMeshBase> source,
                NetMeshBaseIdentifier                                 key,
                ShadowMeshBase                                        value )
        {
            if( value != null ) {
                value.setFactory( StorePassiveProbeManager.this ); // patch this back in after restore from disk
            }
        }
    }
}
