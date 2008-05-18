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

package org.infogrid.meshbase.net.local.m;

import org.infogrid.context.Context;

import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.set.MeshObjectSetFactory;
import org.infogrid.mesh.set.m.ImmutableMMeshObjectSetFactory;

import org.infogrid.meshbase.net.DefaultProxyFactory;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshObjectIdentifierFactory;
import org.infogrid.meshbase.net.Proxy;
import org.infogrid.meshbase.net.ProxyManager;
import org.infogrid.meshbase.net.a.DefaultAnetMeshObjectIdentifierFactory;
import org.infogrid.meshbase.net.local.a.LocalAIterableNetMeshBase;
import org.infogrid.meshbase.net.security.NetAccessManager;

import org.infogrid.modelbase.ModelBase;

import org.infogrid.net.NetMessageEndpointFactory;
import org.infogrid.net.m.MPingPongNetMessageEndpointFactory;

import org.infogrid.probe.ProbeDirectory;
import org.infogrid.probe.manager.ProbeManager;
import org.infogrid.probe.manager.ScheduledExecutorProbeManager;
import org.infogrid.probe.manager.m.MScheduledExecutorProbeManager;
import org.infogrid.probe.shadow.ShadowMeshBaseFactory;
import org.infogrid.probe.shadow.m.MShadowMeshBaseFactory;

import org.infogrid.util.CachingMap;
import org.infogrid.util.CursorIterator;
import org.infogrid.util.MapCursorIterator;
import org.infogrid.util.MCachingHashMap;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.logging.Log;

import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;

/**
 * A NetMMeshBase that uses local (collocated, in this address space) ShadowMeshBases.
 */
public class LocalNetMMeshBase
        extends
            LocalAIterableNetMeshBase
{
    private static final Log log = Log.getLogInstance( LocalNetMMeshBase.class ); // our own, private logger

    /**
      * Factory method.
      *
      * @param modelBase the ModelBase with the type definitions we use
      * @param c the Context in which this MeshBase will run
      */
    public static LocalNetMMeshBase create(
            NetMeshBaseIdentifier    identifier,
            ModelBase                modelBase,
            NetAccessManager         accessMgr,
            ScheduledExecutorService exec,
            ProbeDirectory           probeDirectory,
            Context                  c )
    {
        long theTimeNotNeededTillExpires = theResourceHelper.getResourceLongOrDefault( "ShadowTimeNotNeededTillExpires", 60000L ); // 1 min

        return create(
                identifier,
                modelBase,
                accessMgr,
                exec,
                probeDirectory,
                theTimeNotNeededTillExpires,
                c );
    }

    /**
      * Factory method.
      *
      * @param modelBase the ModelBase with the type definitions we use
      * @param c the Context in which this MeshBase will run
      */
    public static LocalNetMMeshBase create(
            NetMeshBaseIdentifier    identifier,
            MeshObjectSetFactory     setFactory,
            ModelBase                modelBase,
            NetAccessManager         accessMgr,
            ScheduledExecutorService exec,
            ProbeDirectory           probeDirectory,
            Context                  c )
    {
        long theTimeNotNeededTillExpires = theResourceHelper.getResourceLongOrDefault( "ShadowTimeNotNeededTillExpires", 60000L ); // 1 min

        return create(
                identifier,
                setFactory,
                modelBase,
                accessMgr,
                exec,
                probeDirectory,
                theTimeNotNeededTillExpires,
                c );
    }

    /**
      * Factory method.
      *
      * @param modelBase the ModelBase with the type definitions we use
      * @param c the Context in which this MeshBase will run
      */
    public static LocalNetMMeshBase create(
            NetMeshBaseIdentifier    identifier,
            ModelBase                modelBase,
            NetAccessManager         accessMgr,
            ScheduledExecutorService exec,
            ProbeDirectory           probeDirectory,
            long                     timeNotNeededTillExpires,
            Context                  c )
    {
        MPingPongNetMessageEndpointFactory shadowEndpointFactory = MPingPongNetMessageEndpointFactory.create( exec );

        ShadowMeshBaseFactory delegate
                = MShadowMeshBaseFactory.create( modelBase, shadowEndpointFactory, probeDirectory, timeNotNeededTillExpires, c );

        ScheduledExecutorProbeManager probeManager = MScheduledExecutorProbeManager.create( delegate );
        shadowEndpointFactory.setNameServer( probeManager.getNetMeshBaseNameServer() );

        MPingPongNetMessageEndpointFactory endpointFactory = MPingPongNetMessageEndpointFactory.create( exec );
        endpointFactory.setNameServer( probeManager.getNetMeshBaseNameServer() );
        
        LocalNetMMeshBase ret = create(
                identifier,
                endpointFactory,
                modelBase,
                accessMgr,
                probeManager,
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
    public static LocalNetMMeshBase create(
            NetMeshBaseIdentifier    identifier,
            MeshObjectSetFactory     setFactory,
            ModelBase                modelBase,
            NetAccessManager         accessMgr,
            ScheduledExecutorService exec,
            ProbeDirectory           probeDirectory,
            long                     timeNotNeededTillExpires,
            Context                  c )
    {
        MPingPongNetMessageEndpointFactory shadowEndpointFactory = MPingPongNetMessageEndpointFactory.create( exec );

        ShadowMeshBaseFactory delegate
                = MShadowMeshBaseFactory.create( modelBase, shadowEndpointFactory, probeDirectory, timeNotNeededTillExpires, c );

        ScheduledExecutorProbeManager probeManager = MScheduledExecutorProbeManager.create( delegate );
        shadowEndpointFactory.setNameServer( probeManager.getNetMeshBaseNameServer() );

        MPingPongNetMessageEndpointFactory endpointFactory = MPingPongNetMessageEndpointFactory.create( exec );
        endpointFactory.setNameServer( probeManager.getNetMeshBaseNameServer() );
        
        LocalNetMMeshBase ret = create(
                identifier,
                endpointFactory,
                setFactory,
                modelBase,
                accessMgr,
                probeManager,
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
    public static LocalNetMMeshBase create(
            NetMeshBaseIdentifier     identifier,
            NetMessageEndpointFactory endpointFactory,
            ModelBase                 modelBase,
            NetAccessManager          accessMgr,
            ProbeManager              probeManager,
            Context                   c )
    {
        ImmutableMMeshObjectSetFactory setFactory = ImmutableMMeshObjectSetFactory.create( NetMeshObject.class, NetMeshObjectIdentifier.class );

        LocalNetMMeshBase ret = LocalNetMMeshBase.create(
                identifier,
                endpointFactory,
                setFactory,
                modelBase,
                accessMgr,
                probeManager,
                c );

        return ret;
    }
    
    /**
      * Factory method.
      *
      * @param modelBase the ModelBase with the type definitions we use
      * @param c the Context in which this MeshBase will run
      */
    public static LocalNetMMeshBase create(
            NetMeshBaseIdentifier     identifier,
            NetMessageEndpointFactory endpointFactory,
            MeshObjectSetFactory      setFactory,
            ModelBase                 modelBase,
            NetAccessManager          accessMgr,
            ProbeManager              probeManager,
            Context                   c )
    {
        MCachingHashMap<MeshObjectIdentifier,MeshObject> objectStorage = MCachingHashMap.create();
        MCachingHashMap<NetMeshBaseIdentifier,Proxy>     proxyStorage  = MCachingHashMap.create();

        DefaultProxyFactory proxyFactory = DefaultProxyFactory.create( endpointFactory );
        ProxyManager        proxyManager = ProxyManager.create( proxyFactory, proxyStorage );
        
        NetMeshObjectIdentifierFactory identifierFactory = DefaultAnetMeshObjectIdentifierFactory.create( identifier );

        LocalNetMMeshBase ret = new LocalNetMMeshBase( identifier, identifierFactory, setFactory, modelBase, accessMgr, objectStorage, proxyManager, probeManager, c );

        setFactory.setMeshBase( ret );
        proxyFactory.setNetMeshBase( ret );
        ret.initializeHomeObject();
        
        if( log.isDebugEnabled() ) {
            log.debug( "created " + ret );
        }
        return ret;
    }
    
    /**
     * Constructor. This does not initialize content.
     *
     * @param identifier the MeshBaseIdentifier of this MeshBase
     * @param identifierFactory the factory for MeshObjectIdentifiers appropriate for this MeshBase
     * @param modelBase the ModelBase containing type information
     * @param accessMgr the AccessManager that controls access to this MeshBase
     * @param cache the CachingMap that holds the MeshObjects in this MeshBase
     * @param proxyManager the ProxyManager for this NetMeshBase
     * @param probeManager the ProbeManager for this LocalNetMeshBase
     * @param context the Context in which this MeshBase runs.
     */
    protected LocalNetMMeshBase(
            NetMeshBaseIdentifier                       identifier,
            NetMeshObjectIdentifierFactory              identifierFactory,
            MeshObjectSetFactory                        setFactory,
            ModelBase                                   modelBase,
            NetAccessManager                            accessMgr,
            CachingMap<MeshObjectIdentifier,MeshObject> cache,
            ProxyManager                                proxyManager,
            ProbeManager                                probeManager,
            Context                                     context )
    {
        super( identifier, identifierFactory, setFactory, modelBase, accessMgr, cache, proxyManager, probeManager, context );
    }

    /**
     * Returns a CursorIterator over the content of this MeshBase.
     * 
     * @return a CursorIterator.
     */
    public CursorIterator<MeshObject> iterator()
    {
        // not sure why these type casts are needed, they should not be
        MapCursorIterator.Values<MeshObjectIdentifier,MeshObject> ret = MapCursorIterator.createForValues(
                (HashMap<MeshObjectIdentifier,MeshObject>) theCache,
                MeshObjectIdentifier.class,
                MeshObject.class );
        return ret;
    }
    
    /**
     * Our ResourceHelper.
     */
    private static final ResourceHelper theResourceHelper = ResourceHelper.getInstance( LocalNetMMeshBase.class );
}
