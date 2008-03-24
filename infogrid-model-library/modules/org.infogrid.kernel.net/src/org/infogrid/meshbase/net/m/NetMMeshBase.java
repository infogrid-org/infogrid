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

package org.infogrid.meshbase.net.m;

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
import org.infogrid.meshbase.net.a.AIterableNetMeshBase;
import org.infogrid.meshbase.net.a.DefaultAnetMeshObjectIdentifierFactory;
import org.infogrid.meshbase.net.security.NetAccessManager;

import org.infogrid.modelbase.ModelBase;

import org.infogrid.net.NetMessageEndpointFactory;

import org.infogrid.util.CachingMap;
import org.infogrid.util.MCachingHashMap;
import org.infogrid.util.logging.Log;

/**
 * This networked MeshBase is held only in memory. It has no persistence whatsoever.
 */
public class NetMMeshBase
        extends
            AIterableNetMeshBase
{
    private static final Log log = Log.getLogInstance(NetMMeshBase.class); // our own, private logger

    /**
      * Factory method.
      *
      * @param modelBase the ModelBase with the type definitions we use
      * @param c the Context in which this MeshBase will run
      */
    public static NetMMeshBase create(
            NetMeshBaseIdentifier     identifier,
            NetMessageEndpointFactory endpointFactory,
            ModelBase                 modelBase,
            NetAccessManager          accessMgr,
            Context                   c )
    {
        ImmutableMMeshObjectSetFactory setFactory = ImmutableMMeshObjectSetFactory.create();

        NetMMeshBase ret = NetMMeshBase.create( identifier, endpointFactory, setFactory, modelBase, accessMgr, c );

        return ret;
    }

    /**
      * Factory method.
      *
      * @param modelBase the ModelBase with the type definitions we use
      * @param c the Context in which this MeshBase will run
      */
    public static NetMMeshBase create(
            NetMeshBaseIdentifier     identifier,
            NetMessageEndpointFactory endpointFactory,
            MeshObjectSetFactory      setFactory,
            ModelBase                 modelBase,
            NetAccessManager          accessMgr,
            Context                   c )
    {
        MCachingHashMap<MeshObjectIdentifier,MeshObject> objectStorage = MCachingHashMap.create();
        MCachingHashMap<NetMeshBaseIdentifier,Proxy>     proxyStorage  = MCachingHashMap.create();

        DefaultProxyFactory proxyFactory = DefaultProxyFactory.create( endpointFactory );
        ProxyManager        proxyManager = ProxyManager.create( proxyFactory, proxyStorage );
        
        NetMeshObjectIdentifierFactory identifierFactory = DefaultAnetMeshObjectIdentifierFactory.create( identifier );

        NetMMeshBase ret = new NetMMeshBase( identifier, identifierFactory, setFactory, modelBase, accessMgr, objectStorage, proxyManager, c );

        setFactory.setMeshBase( ret );
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
     * @param identifier the NetMeshBaseIdentifier through which this NetworkedMeshBase can be reached
     * @param modelBase the ModelBase with the type definitions we use
     * @param c the Context in which this MeshBase will run
     */
    protected NetMMeshBase(
            NetMeshBaseIdentifier                       identifier,
            NetMeshObjectIdentifierFactory              identifierFactory,
            MeshObjectSetFactory                        setFactory,
            ModelBase                                   modelBase,
            NetAccessManager                            accessMgr,
            CachingMap<MeshObjectIdentifier,MeshObject> cache,
            ProxyManager                                proxyManager,
            Context                                     c )
    {
        super( identifier, identifierFactory, setFactory, modelBase, accessMgr, cache, proxyManager, c );
    }
}
