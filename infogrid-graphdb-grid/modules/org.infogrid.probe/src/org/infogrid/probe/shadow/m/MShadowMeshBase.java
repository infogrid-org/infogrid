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

package org.infogrid.probe.shadow.m;

import org.infogrid.context.Context;

import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;

import org.infogrid.mesh.set.MeshObjectSetFactory;
import org.infogrid.mesh.set.m.ImmutableMMeshObjectSetFactory;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshObjectIdentifierFactory;
import org.infogrid.meshbase.net.Proxy;
import org.infogrid.meshbase.net.ProxyManager;
import org.infogrid.meshbase.net.a.DefaultAnetMeshObjectIdentifierFactory;
import org.infogrid.meshbase.net.security.NetAccessManager;

import org.infogrid.modelbase.ModelBase;

import org.infogrid.net.NetMessageEndpointFactory;

import org.infogrid.probe.ProbeDirectory;
import org.infogrid.probe.shadow.DefaultShadowProxyFactory;
import org.infogrid.probe.shadow.a.AShadowMeshBase;

import org.infogrid.util.CachingMap;
import org.infogrid.util.MCachingHashMap;
import org.infogrid.util.logging.Log;

/**
 * An in-memory implementation of ShadowMeshBase. It delegates most of its work
 * to ProbeDispatcher, which can also be used by other implementations of ShadowMeshBase.
 */
public class MShadowMeshBase
        extends
            AShadowMeshBase
{
    private static final Log log = Log.getLogInstance( MShadowMeshBase.class ); // our own, private logger

    /**
      * Factory method.
      *
      * @param modelBase the ModelBase with the type definitions we use
      * @param c the Context in which this MeshBase will run
      */
    public static MShadowMeshBase create(
            NetMeshBaseIdentifier     identifier,
            NetMessageEndpointFactory endpointFactory,
            ModelBase                 modelBase,
            NetAccessManager          accessMgr,
            ProbeDirectory            directory,
            long                      timeNotNeededTillExpires,
            Context                   c )
    {
        MCachingHashMap<MeshObjectIdentifier,MeshObject> objectStorage = MCachingHashMap.create();
        MCachingHashMap<NetMeshBaseIdentifier,Proxy>     proxyStorage  = MCachingHashMap.create();

        DefaultShadowProxyFactory proxyFactory = DefaultShadowProxyFactory.create( endpointFactory );
        ProxyManager              proxyManager = ProxyManager.create( proxyFactory, proxyStorage );

        NetMeshObjectIdentifierFactory identifierFactory = DefaultAnetMeshObjectIdentifierFactory.create( identifier );
        ImmutableMMeshObjectSetFactory setFactory = ImmutableMMeshObjectSetFactory.create();

        MShadowMeshBase ret = new MShadowMeshBase(
                identifier,
                identifierFactory,
                setFactory,
                modelBase,
                accessMgr,
                objectStorage,
                proxyManager,
                directory,
                System.currentTimeMillis(), // a memory Shadow could only have been created now
                timeNotNeededTillExpires,
                c );

        setFactory.setMeshBase( ret );
        proxyFactory.setNetMeshBase( ret );
        // do not initialize home object here: Shadows behave differently
        
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
     * @param directory the ProbeDirectory to use
     * @param timeCreated the time at which the data source was created (if this is a recreate operation) or -1
     * @param timeNotNeededTillExpires the time, in milliseconds, that this MShadowMeshBase will continue operating
     *         even if none of its MeshObjects are replicated to another NetMeshBase. If this is negative, it means "forever".
     *         If this is 0, it will expire immediately after the first Probe run, before the caller returns, which is probably
     *         not very useful.
     * @param endpointFactory the MessageEndpointFactory to use for proxy communication
     * @param context the Context in which this MeshBase runs.
     */
    protected MShadowMeshBase(
            NetMeshBaseIdentifier                       identifier,
            NetMeshObjectIdentifierFactory              identifierFactory,
            MeshObjectSetFactory                        setFactory,
            ModelBase                                   modelBase,
            NetAccessManager                            accessMgr,
            CachingMap<MeshObjectIdentifier,MeshObject> cache,
            ProxyManager                                proxyManager,
            ProbeDirectory                              directory,
            long                                        timeCreated,
            long                                        timeNotNeededTillExpires,
            Context                                     c )
    {
        super(  identifier,
                identifierFactory,
                setFactory,
                modelBase,
                accessMgr,
                cache,
                proxyManager,
                directory,
                timeCreated,
                timeNotNeededTillExpires,
                c );
    }

    /**
     * Allow a Proxy to tell this StagingMeshBase that it performed an operation that
     * modified data in the StagingMeshBase, and the StagingMeshBase may have to be flushed to disk.
     */
    public void flushMeshBase()
    {
        // no op, we are in memory only
    }
}
