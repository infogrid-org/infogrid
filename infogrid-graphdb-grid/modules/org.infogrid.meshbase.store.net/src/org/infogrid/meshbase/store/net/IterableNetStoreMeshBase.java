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

package org.infogrid.meshbase.store.net;

import org.infogrid.context.Context;
import org.infogrid.mesh.IsAbstractException;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.set.MeshObjectSetFactory;
import org.infogrid.mesh.set.m.ImmutableMMeshObjectSetFactory;

import org.infogrid.meshbase.IterableMeshBase;
import org.infogrid.meshbase.IterableMeshBaseDifferencer;
import org.infogrid.meshbase.Sweeper;
import org.infogrid.meshbase.sweeper.SweepStep;

import org.infogrid.meshbase.net.DefaultProxyFactory;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshObjectIdentifierFactory;
import org.infogrid.meshbase.net.NetSweeper;
import org.infogrid.meshbase.net.Proxy;
import org.infogrid.meshbase.net.a.DefaultAnetMeshObjectIdentifierFactory;
import org.infogrid.meshbase.net.security.NetAccessManager;

import org.infogrid.modelbase.ModelBase;

import org.infogrid.net.NetMessageEndpointFactory;

import org.infogrid.store.IterableStore;
import org.infogrid.store.util.IterableStoreBackedMap;

import org.infogrid.util.CursorIterator;
import org.infogrid.util.logging.Log;

import java.util.concurrent.ScheduledExecutorService;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;

/**
 *
 */
public class IterableNetStoreMeshBase
        extends
            NetStoreMeshBase
        implements
            IterableMeshBase
{
    private static final Log log = Log.getLogInstance( IterableNetStoreMeshBase.class ); // our own, private logger

    /**
     * Factory method.
     * 
     * @param identifier the NNetMeshBaseIdentifierof the to-be-created NetMeshBase
     * @param endpointFactory the MessageEndpointFactory to use for proxy communication
     * @param modelBase the ModelBase with the type definitions we use
     * @param accessMgr the AccessManager that controls access to this MeshBase
     * @param meshObjectStore the Store in which to store the MeshObjects
     * @param proxyStore the Store in which to store the Proxies
     * @param c the Context in which this MeshBase will run
     * @throws IsAbstractException thrown if the given EntityType for the home object is abstract and cannot be instantiated
     */
    public static IterableNetStoreMeshBase create(
            NetMeshBaseIdentifier     identifier,
            NetMessageEndpointFactory endpointFactory,
            ModelBase                 modelBase,
            NetAccessManager          accessMgr,
            IterableStore             meshObjectStore,
            IterableStore             proxyStore,
            Context                   c )
        throws
            IsAbstractException
    {
        ImmutableMMeshObjectSetFactory setFactory = ImmutableMMeshObjectSetFactory.create( NetMeshObject.class, NetMeshObjectIdentifier.class );

        IterableNetStoreMeshBase ret = IterableNetStoreMeshBase.create(
                identifier,
                endpointFactory,
                setFactory,
                modelBase,
                accessMgr,
                meshObjectStore,
                proxyStore,
                c );

        return ret;
    }

    /**
     * Factory method.
     * 
     * @param identifier the NNetMeshBaseIdentifierof the to-be-created NetMeshBase
     * @param endpointFactory the MessageEndpointFactory to use for proxy communication
     * @param modelBase the ModelBase with the type definitions we use
     * @param accessMgr the AccessManager that controls access to this MeshBase
     * @param meshObjectStore the Store in which to store the MeshObjects
     * @param proxyStore the Store in which to store the Proxies
     * @param c the Context in which this MeshBase will run
     * @throws IsAbstractException thrown if the given EntityType for the home object is abstract and cannot be instantiated
     */
    public static IterableNetStoreMeshBase create(
            NetMeshBaseIdentifier     identifier,
            NetMessageEndpointFactory endpointFactory,
            MeshObjectSetFactory      setFactory,
            ModelBase                 modelBase,
            NetAccessManager          accessMgr,
            IterableStore             meshObjectStore,
            IterableStore             proxyStore,
            Context                   c )
        throws
            IsAbstractException
    {
        DefaultProxyFactory proxyFactory = DefaultProxyFactory.create( endpointFactory );

        NetStoreMeshBaseEntryMapper objectMapper = new NetStoreMeshBaseEntryMapper();
        StoreProxyEntryMapper       proxyMapper  = new StoreProxyEntryMapper( proxyFactory );
        
        IterableStoreBackedMap<MeshObjectIdentifier,MeshObject> objectStorage = IterableStoreBackedMap.createWeak( objectMapper, meshObjectStore );
        IterableStoreBackedMap<NetMeshBaseIdentifier,Proxy>     proxyStorage  = IterableStoreBackedMap.createWeak( proxyMapper,  proxyStore );

        NetMeshObjectIdentifierFactory identifierFactory = DefaultAnetMeshObjectIdentifierFactory.create( identifier );

        StoreProxyManager proxyManager = StoreProxyManager.create( proxyFactory, proxyStorage );

        IterableNetStoreMeshBase ret = new IterableNetStoreMeshBase( identifier, identifierFactory, setFactory, modelBase, accessMgr, objectStorage, proxyManager, c );

        setFactory.setMeshBase( ret );
        objectMapper.setMeshBase( ret );
        proxyMapper.setMeshBase( ret );
        proxyFactory.setNetMeshBase( ret );
        ret.initializeHomeObject();
        
        if( log.isDebugEnabled() ) {
            log.debug( "created " + ret );
        }
        return ret;
    }

    /**
     * Constructor.
     * 
     * @param identifier the NNetMeshBaseIdentifierof the to-be-created NetMeshBase
     * @param modelBase the ModelBase with the type definitions we use
     * @param accessMgr the AccessManager that controls access to this MeshBase
     * @param cache the in-memory cache to use
     * @param mapper the Mapper to and from the Store
     * @param c the Context in which this MeshBase will run
     */
    protected IterableNetStoreMeshBase(
            NetMeshBaseIdentifier                                   identifier,
            NetMeshObjectIdentifierFactory                          identifierFactory,
            MeshObjectSetFactory                                    setFactory,
            ModelBase                                               modelBase,
            NetAccessManager                                        accessMgr,
            IterableStoreBackedMap<MeshObjectIdentifier,MeshObject> cache,
            StoreProxyManager                                       proxyManager,
            Context                                                 c )
    {
        super( identifier, identifierFactory, setFactory, modelBase, accessMgr, cache, proxyManager, c );
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
     * Obtain an Iterator over all MeshObjects in the Store.
     *
     * @return the Iterator
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
        return ((IterableStore) getCachingMap().getStore()).size();
    }

    /**
     * Factory method for a IterableMeshBaseDifferencer, with this IterableMeshBase
     * being the comparison base.
     *
     * @return the IterableMeshBaseDifferencer
     */
    public IterableMeshBaseDifferencer getDifferencer()
    {
        return new IterableMeshBaseDifferencer( this );
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
        Sweeper sweeper = theSweeper;
        if( sweeper == null ) {
            throw new NullPointerException();
        }
        for( MeshObject candidate : this ) {
            boolean done = sweeper.potentiallyDelete( candidate );
            if( !done && ( sweeper instanceof NetSweeper )) {
                ((NetSweeper)sweeper).potentiallyPurge( (NetMeshObject) candidate );
            }
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
