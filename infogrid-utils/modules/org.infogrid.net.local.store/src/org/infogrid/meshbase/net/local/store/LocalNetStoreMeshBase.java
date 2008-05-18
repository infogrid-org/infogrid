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

package org.infogrid.meshbase.net.local.store;

import org.infogrid.context.Context;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;

import org.infogrid.meshbase.net.DefaultProxyFactory;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshObjectIdentifierFactory;
import org.infogrid.meshbase.net.Proxy;
import org.infogrid.meshbase.net.a.DefaultAnetMeshObjectIdentifierFactory;
import org.infogrid.meshbase.net.local.a.LocalAnetMeshBase;
import org.infogrid.meshbase.net.local.LocalNetMeshBase;

import org.infogrid.meshbase.net.security.NetAccessManager;

import org.infogrid.meshbase.store.net.StoreProxyEntryMapper;
import org.infogrid.meshbase.store.net.StoreProxyManager;
import org.infogrid.meshbase.store.net.NetStoreMeshBaseEntryMapper;

import org.infogrid.modelbase.ModelBase;

import org.infogrid.net.NetMessageEndpointFactory;
import org.infogrid.net.m.MPingPongNetMessageEndpointFactory;

import org.infogrid.probe.ProbeDirectory;
import org.infogrid.probe.manager.ProbeManager;
import org.infogrid.probe.manager.store.StoreScheduledExecutorProbeManager;
import org.infogrid.probe.shadow.store.StoreShadowMeshBaseFactory;

import org.infogrid.store.IterableStore;
import org.infogrid.store.Store;
import org.infogrid.store.prefixing.IterablePrefixingStore;
import org.infogrid.store.prefixing.PrefixingStore;
import org.infogrid.store.util.IterableStoreBackedMap;
import org.infogrid.store.util.StoreBackedMap;

import org.infogrid.util.CachingMap;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.logging.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.mesh.set.MeshObjectSetFactory;
import org.infogrid.mesh.set.m.ImmutableMMeshObjectSetFactory;

/**
 * A NetStoreMeshBase that uses local (collocated, in this address space) ShadowMeshBases.
 */
public class LocalNetStoreMeshBase
        extends
            LocalAnetMeshBase
        implements
            LocalNetMeshBase
{
    private static final Log log = Log.getLogInstance( LocalNetStoreMeshBase.class ); // our own, private logger

    /**
      * Factory method.
      *
      * @param modelBase the ModelBase with the type definitions we use
      * @param c the Context in which this MeshBase will run
      */
    public static LocalNetStoreMeshBase create(
            NetMeshBaseIdentifier    identifier,
            ModelBase                modelBase,
            NetAccessManager         accessMgr,
            IterableStore            store,
            ProbeDirectory           probeDirectory,
            long                     timeNotNeededTillExpires,
            Context                  c )
    {
        Store         meshStore        = PrefixingStore.create(         "mesh",        store );
        IterableStore proxyStore       = IterablePrefixingStore.create( "proxy",       store );
        IterableStore shadowStore      = IterablePrefixingStore.create( "shadowmesh",  store );
        IterableStore shadowProxyStore = IterablePrefixingStore.create( "shadowproxy", store );

        ScheduledExecutorService exec = Executors.newScheduledThreadPool( 1 );

        return create(
                identifier,
                modelBase,
                accessMgr,
                meshStore,
                proxyStore,
                shadowStore,
                shadowProxyStore,
                probeDirectory,
                exec,
                timeNotNeededTillExpires,
                c );
    }

    /**
      * Factory method.
      *
      * @param modelBase the ModelBase with the type definitions we use
      * @param c the Context in which this MeshBase will run
      */
    public static LocalNetStoreMeshBase create(
            NetMeshBaseIdentifier    identifier,
            MeshObjectSetFactory     setFactory,
            ModelBase                modelBase,
            NetAccessManager         accessMgr,
            IterableStore            store,
            ProbeDirectory           probeDirectory,
            long                     timeNotNeededTillExpires,
            Context                  c )
    {
        Store         meshStore        = PrefixingStore.create(         "mesh",        store );
        IterableStore proxyStore       = IterablePrefixingStore.create( "proxy",       store );
        IterableStore shadowStore      = IterablePrefixingStore.create( "shadowmesh",  store );
        IterableStore shadowProxyStore = IterablePrefixingStore.create( "shadowproxy", store );

        ScheduledExecutorService exec = Executors.newScheduledThreadPool( 1 );

        return create(
                identifier,
                setFactory,
                modelBase,
                accessMgr,
                meshStore,
                proxyStore,
                shadowStore,
                shadowProxyStore,
                probeDirectory,
                exec,
                timeNotNeededTillExpires,
                c );
    }

    /**
      * Factory method.
      *
      * @param modelBase the ModelBase with the type definitions we use
      * @param c the Context in which this MeshBase will run
      */
    public static LocalNetStoreMeshBase create(
            NetMeshBaseIdentifier    identifier,
            ModelBase                modelBase,
            NetAccessManager         accessMgr,
            Store                    meshStore,
            IterableStore            proxyStore,
            IterableStore            shadowStore,
            IterableStore            shadowProxyStore,
            ProbeDirectory           probeDirectory,
            ScheduledExecutorService exec,
            long                     timeNotNeededTillExpires,
            Context                  c )
    {
        MPingPongNetMessageEndpointFactory shadowEndpointFactory = MPingPongNetMessageEndpointFactory.create( exec );

        StoreShadowMeshBaseFactory delegate = StoreShadowMeshBaseFactory.create(
                modelBase,
                shadowEndpointFactory,
                probeDirectory,
                shadowStore,
                shadowProxyStore,
                timeNotNeededTillExpires,
                c );

        StoreScheduledExecutorProbeManager probeManager = StoreScheduledExecutorProbeManager.create( delegate, shadowStore );
        shadowEndpointFactory.setNameServer( probeManager.getNetMeshBaseNameServer() );

        MPingPongNetMessageEndpointFactory endpointFactory = MPingPongNetMessageEndpointFactory.create( exec );
        endpointFactory.setNameServer( probeManager.getNetMeshBaseNameServer() );
        
        LocalNetStoreMeshBase ret = create(
                identifier,
                modelBase,
                accessMgr,
                probeManager,
                meshStore,
                proxyStore,
                endpointFactory,
                c );
        
        probeManager.setMainNetMeshBase( ret );
        probeManager.start( exec );

        return ret;
    }
    
    /**
      * Factory method.
      *
      * @param modelBase the ModelBase with the type definitions we use
      * @param c the Context in which this MeshBase will run
      */
    public static LocalNetStoreMeshBase create(
            NetMeshBaseIdentifier    identifier,
            MeshObjectSetFactory     setFactory,
            ModelBase                modelBase,
            NetAccessManager         accessMgr,
            Store                    meshStore,
            IterableStore            proxyStore,
            IterableStore            shadowStore,
            IterableStore            shadowProxyStore,
            ProbeDirectory           probeDirectory,
            ScheduledExecutorService exec,
            long                     timeNotNeededTillExpires,
            Context                  c )
    {
        MPingPongNetMessageEndpointFactory shadowEndpointFactory = MPingPongNetMessageEndpointFactory.create( exec );

        StoreShadowMeshBaseFactory delegate = StoreShadowMeshBaseFactory.create(
                modelBase,
                shadowEndpointFactory,
                probeDirectory,
                shadowStore,
                shadowProxyStore,
                timeNotNeededTillExpires,
                c );

        StoreScheduledExecutorProbeManager probeManager = StoreScheduledExecutorProbeManager.create( delegate, shadowStore );
        shadowEndpointFactory.setNameServer( probeManager.getNetMeshBaseNameServer() );

        MPingPongNetMessageEndpointFactory endpointFactory = MPingPongNetMessageEndpointFactory.create( exec );
        endpointFactory.setNameServer( probeManager.getNetMeshBaseNameServer() );
        
        LocalNetStoreMeshBase ret = create(
                identifier,
                setFactory,
                modelBase,
                accessMgr,
                probeManager,
                meshStore,
                proxyStore,
                endpointFactory,
                c );
        
        probeManager.setMainNetMeshBase( ret );
        probeManager.start( exec );

        return ret;
    }
    
    /**
      * Factory method.
      *
      * @param modelBase the ModelBase with the type definitions we use
      * @param c the Context in which this MeshBase will run
      */
    public static LocalNetStoreMeshBase create(
            NetMeshBaseIdentifier     identifier,
            ModelBase                 modelBase,
            NetAccessManager          accessMgr,
            ProbeManager              probeManager,
            Store                     meshObjectStore,
            IterableStore             proxyStore,
            NetMessageEndpointFactory endpointFactory,
            Context                   c )
    {
        ImmutableMMeshObjectSetFactory setFactory = ImmutableMMeshObjectSetFactory.create( NetMeshObject.class, NetMeshObjectIdentifier.class );

        LocalNetStoreMeshBase ret = LocalNetStoreMeshBase.create(
                identifier,
                setFactory,
                modelBase,
                accessMgr,
                probeManager,
                meshObjectStore,
                proxyStore,
                endpointFactory,
                c );
        
        return ret;
    }

    /**
      * Factory method.
      *
      * @param modelBase the ModelBase with the type definitions we use
      * @param c the Context in which this MeshBase will run
      */
    public static LocalNetStoreMeshBase create(
            NetMeshBaseIdentifier     identifier,
            MeshObjectSetFactory      setFactory,
            ModelBase                 modelBase,
            NetAccessManager          accessMgr,
            ProbeManager              probeManager,
            Store                     meshObjectStore,
            IterableStore             proxyStore,
            NetMessageEndpointFactory endpointFactory,
            Context                   c )
    {
        DefaultProxyFactory proxyFactory = DefaultProxyFactory.create( endpointFactory );

        NetStoreMeshBaseEntryMapper objectMapper = new NetStoreMeshBaseEntryMapper();
        StoreProxyEntryMapper       proxyMapper  = new StoreProxyEntryMapper( proxyFactory );

        StoreBackedMap<MeshObjectIdentifier,MeshObject>     objectStorage = StoreBackedMap.createWeak( objectMapper, meshObjectStore );
        IterableStoreBackedMap<NetMeshBaseIdentifier,Proxy> proxyStorage  = IterableStoreBackedMap.createWeak( proxyMapper,  proxyStore );

        StoreProxyManager proxyManager = StoreProxyManager.create( proxyFactory, proxyStorage );

        NetMeshObjectIdentifierFactory identifierFactory = DefaultAnetMeshObjectIdentifierFactory.create( identifier );

        LocalNetStoreMeshBase ret = new LocalNetStoreMeshBase(
                identifier,
                identifierFactory,
                setFactory,
                modelBase,
                accessMgr,
                objectStorage,
                proxyManager,
                probeManager, c );
        
        setFactory.setMeshBase( ret );
        proxyFactory.setNetMeshBase( ret );
        proxyMapper.setMeshBase( ret );
        objectMapper.setMeshBase( ret );

        ret.initializeHomeObject();
       
        if( log.isDebugEnabled() ) {
            log.debug( "created " + ret );
        }
        return ret;
    }
    
    /**
     * Constructor.
     * 
     * @param identifier the NNetMeshBaseIdentifierthrough which this NetworkedMeshBase can be reached
     * @param modelBase the ModelBase with the type definitions we use
     * @param c the Context in which this MeshBase will run
     */
    protected LocalNetStoreMeshBase(
            NetMeshBaseIdentifier                       identifier,
            NetMeshObjectIdentifierFactory              identifierFactory,
            MeshObjectSetFactory                        setFactory,
            ModelBase                                   modelBase,
            NetAccessManager                            accessMgr,
            CachingMap<MeshObjectIdentifier,MeshObject> cache,
            StoreProxyManager                           proxyManager,
            ProbeManager                                probeManager,
            Context                                     c )
    {
        super(  identifier,
                identifierFactory,
                setFactory,
                modelBase,
                accessMgr,
                cache,
                proxyManager,
                probeManager,
                c );
    }

    /**
     * Our ResourceHelper.
     */
    private static final ResourceHelper theResourceHelper = ResourceHelper.getInstance( LocalNetStoreMeshBase.class );

    /**
     * The time until unneeded Shadows expire.
     */
    protected static long theTimeNotNeededTillExpires = theResourceHelper.getResourceLongOrDefault( "theTimeNotNeededTillExpires", 10000L );
}
