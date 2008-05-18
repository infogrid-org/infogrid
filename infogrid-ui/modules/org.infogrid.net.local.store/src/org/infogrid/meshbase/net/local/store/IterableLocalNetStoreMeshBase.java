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
import org.infogrid.mesh.set.MeshObjectSetFactory;
import org.infogrid.mesh.set.m.ImmutableMMeshObjectSetFactory;

import org.infogrid.meshbase.Sweeper;
import org.infogrid.meshbase.net.DefaultProxyFactory;
import org.infogrid.meshbase.net.IterableNetMeshBaseDifferencer;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshObjectIdentifierFactory;
import org.infogrid.meshbase.net.Proxy;
import org.infogrid.meshbase.net.a.DefaultAnetMeshObjectIdentifierFactory;
import org.infogrid.meshbase.net.local.IterableLocalNetMeshBase;
import org.infogrid.meshbase.net.security.NetAccessManager;
import org.infogrid.meshbase.store.net.NetStoreMeshBaseEntryMapper;
import org.infogrid.meshbase.store.net.StoreProxyEntryMapper;
import org.infogrid.meshbase.store.net.StoreProxyManager;
import org.infogrid.meshbase.sweeper.SweepStep;

import org.infogrid.modelbase.ModelBase;

import org.infogrid.net.NetMessageEndpointFactory;
import org.infogrid.net.m.MPingPongNetMessageEndpointFactory;

import org.infogrid.probe.ProbeDirectory;
import org.infogrid.probe.manager.ProbeManager;
import org.infogrid.probe.manager.store.StoreScheduledExecutorProbeManager;
import org.infogrid.probe.shadow.store.StoreShadowMeshBaseFactory;

import org.infogrid.store.IterableStore;
import org.infogrid.store.prefixing.IterablePrefixingStore;
import org.infogrid.store.util.IterableStoreBackedMap;
import org.infogrid.store.util.StoreBackedMap;

import org.infogrid.util.CursorIterator;
import org.infogrid.util.logging.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;

/**
 *
 */
public class IterableLocalNetStoreMeshBase
        extends
            LocalNetStoreMeshBase
        implements
            IterableLocalNetMeshBase
{
    private static final Log log = Log.getLogInstance( IterableLocalNetStoreMeshBase.class ); // our own, private logger

    /**
      * Factory method.
      *
      * @param modelBase the ModelBase with the type definitions we use
      * @param c the Context in which this MeshBase will run
      */
    public static IterableLocalNetStoreMeshBase create(
            NetMeshBaseIdentifier    identifier,
            ModelBase                modelBase,
            NetAccessManager         accessMgr,
            IterableStore            store,
            ProbeDirectory           probeDirectory,
            long                     timeNotNeededTillExpires,
            Context                  c )
    {
        IterableStore meshStore        = IterablePrefixingStore.create( "mesh",        store );
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
    public static IterableLocalNetStoreMeshBase create(
            NetMeshBaseIdentifier    identifier,
            MeshObjectSetFactory     setFactory,
            ModelBase                modelBase,
            NetAccessManager         accessMgr,
            IterableStore            store,
            ProbeDirectory           probeDirectory,
            long                     timeNotNeededTillExpires,
            Context                  c )
    {
        IterableStore meshStore        = IterablePrefixingStore.create( "mesh",        store );
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
    public static IterableLocalNetStoreMeshBase create(
            NetMeshBaseIdentifier    identifier,
            ModelBase                modelBase,
            NetAccessManager         accessMgr,
            IterableStore            meshStore,
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
        
        IterableLocalNetStoreMeshBase ret = create(
                identifier,
                probeManager,
                modelBase,
                accessMgr,
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
    public static IterableLocalNetStoreMeshBase create(
            NetMeshBaseIdentifier    identifier,
            MeshObjectSetFactory     setFactory,
            ModelBase                modelBase,
            NetAccessManager         accessMgr,
            IterableStore            meshStore,
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
        
        IterableLocalNetStoreMeshBase ret = create(
                identifier,
                probeManager,
                setFactory,
                modelBase,
                accessMgr,
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
    public static IterableLocalNetStoreMeshBase create(
            NetMeshBaseIdentifier     identifier,
            ProbeManager              probeManager,
            ModelBase                 modelBase,
            NetAccessManager          accessMgr,
            IterableStore             meshObjectStore,
            IterableStore             proxyStore,
            NetMessageEndpointFactory endpointFactory,
            Context                   c )
    {
        ImmutableMMeshObjectSetFactory setFactory = ImmutableMMeshObjectSetFactory.create( NetMeshObject.class, NetMeshObjectIdentifier.class );

        IterableLocalNetStoreMeshBase ret = IterableLocalNetStoreMeshBase.create(
                identifier,
                probeManager,
                setFactory,
                modelBase,
                accessMgr,
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
    public static IterableLocalNetStoreMeshBase create(
            NetMeshBaseIdentifier     identifier,
            ProbeManager              probeManager,
            MeshObjectSetFactory      setFactory,
            ModelBase                 modelBase,
            NetAccessManager          accessMgr,
            IterableStore             meshObjectStore,
            IterableStore             proxyStore,
            NetMessageEndpointFactory endpointFactory,
            Context                   c )
    {
        DefaultProxyFactory proxyFactory = DefaultProxyFactory.create( endpointFactory );

        NetStoreMeshBaseEntryMapper objectMapper = new NetStoreMeshBaseEntryMapper();
        StoreProxyEntryMapper       proxyMapper  = new StoreProxyEntryMapper( proxyFactory );
        
        IterableStoreBackedMap<MeshObjectIdentifier,MeshObject> objectStorage = IterableStoreBackedMap.createWeak( objectMapper, meshObjectStore );
        IterableStoreBackedMap<NetMeshBaseIdentifier,Proxy>     proxyStorage  = IterableStoreBackedMap.createWeak( proxyMapper,  proxyStore );

        final StoreProxyManager proxyManager = StoreProxyManager.create( proxyFactory, proxyStorage );

        NetMeshObjectIdentifierFactory identifierFactory = DefaultAnetMeshObjectIdentifierFactory.create( identifier );

        IterableLocalNetStoreMeshBase ret = new IterableLocalNetStoreMeshBase(
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
    protected IterableLocalNetStoreMeshBase(
            NetMeshBaseIdentifier                           identifier,
            NetMeshObjectIdentifierFactory                  identifierFactory,
            MeshObjectSetFactory                            setFactory,
            ModelBase                                       modelBase,
            NetAccessManager                                accessMgr,
            StoreBackedMap<MeshObjectIdentifier,MeshObject> cache,
            StoreProxyManager                               proxyManager,
            ProbeManager                                    probeManager,
            Context                                         c )
    {
        super( identifier, identifierFactory, setFactory, modelBase, accessMgr, cache, proxyManager, probeManager, c );
    }

    /**
     * Obtain an Iterator over all MeshObjects in the Store.
     *
     * @return the Iterator
     */
    public CursorIterator<MeshObject> iterator()
    {
        return getCachingMap().valuesIterator( null, MeshObject.class );
    }
    
    /**
     * Obtain a CursorIterable. This performs the exact same operation as
     * @link #iterator iterator}, but is friendlier towards JSPs and other software
     * that likes to use JavaBeans conventions.
     *
     * @return the CursorIterable
     */
    public CursorIterator<MeshObject> getIterator()
    {
        return iterator();
    }

    /**
     * Determine the number of MeshObjects in this MeshBase.
     *
     * @return the number of MeshObjets in this MeshBase
     */
    public int size()
    {
        return ((IterableStore)(getCachingMap().getStore())).size();
    }

    /**
     * Factory method for a IterableMeshBaseDifferencer, with this IterableMeshBase
     * being the comparison base.
     *
     * @return the IterableMeshBaseDifferencer
     */
    public IterableNetMeshBaseDifferencer getDifferencer()
    {
        return new IterableNetMeshBaseDifferencer( this );
    }

    /**
     * Helper method for typecasting to the right subtype of CachingMap.
     *
     * @return theCache, typecast
     */
    protected StoreBackedMap<MeshObjectIdentifier,MeshObject> getCachingMap()
    {
        return (StoreBackedMap<MeshObjectIdentifier,MeshObject>) theCache;
    }

    /**
     * Continually sweep this IterableMeshBase in the background, according to
     * the configured Sweeper.
     *
     * @param scheduleVia the ScheduledExecutorService to use for scheduling
     * @throws NullPointerException thrown if no Sweeper has been set
     */
    public void startBackgroundSweeping(
            ScheduledExecutorService scheduleVia )
        throws
            NullPointerException
    {
        Sweeper sweep = theSweeper;
        if( sweep == null ) {
            throw new NullPointerException();
        }
        theSweeperScheduler = scheduleVia;

        scheduleSweepStep();
    }
    
    /**
     * Stop the background sweeping.
     */
    public void stopBackgroundSweeping()
    {
        SweepStep nextStep = theNextSweepStep;
        if( nextStep == null ) {
            return;
        }
        synchronized( nextStep ) {
            nextStep.cancel();
            theNextSweepStep = null;
        }
    }
    
    /**
     * Perform a sweep on every single MeshObject in this InterableMeshBase.
     * This may take a long time; using background sweeping is almost always
     * a better alternative.
     */
    public synchronized void sweepAllNow()
    {
        Sweeper sweep = theSweeper;
        if( sweep == null ) {
            throw new NullPointerException();
        }
        for( MeshObject candidate : this ) {
            sweep.potentiallyDelete( candidate );
        }
    }

    /**
     * Invoked by the SweepStep, schedule the next SweepStep.
     */
    public void scheduleSweepStep()
    {
        if( theNextSweepStep != null ) {
            theNextSweepStep = theNextSweepStep.nextStep();
        } else {
            theNextSweepStep = SweepStep.create( this );
        }
        theNextSweepStep.scheduleVia( theSweeperScheduler );
    }

    /**
     * The Scheduler for the Sweeper, if any.
     */
    protected ScheduledExecutorService theSweeperScheduler;
    
    /**
     * The next background Sweep task, if any.
     */
    protected SweepStep theNextSweepStep;
}
