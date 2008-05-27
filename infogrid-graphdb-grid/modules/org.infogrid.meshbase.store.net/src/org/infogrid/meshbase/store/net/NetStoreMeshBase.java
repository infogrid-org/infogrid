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
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.mesh.net.a.DefaultAnetMeshObjectIdentifier;
import org.infogrid.mesh.set.MeshObjectSetFactory;
import org.infogrid.mesh.set.m.ImmutableMMeshObjectSetFactory;

import org.infogrid.meshbase.net.DefaultProxyFactory;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshObjectIdentifierFactory;
import org.infogrid.meshbase.net.Proxy;
import org.infogrid.meshbase.net.a.AnetMeshBase;
import org.infogrid.meshbase.net.a.DefaultAnetMeshObjectIdentifierFactory;
import org.infogrid.meshbase.net.security.NetAccessManager;

import org.infogrid.modelbase.ModelBase;

import org.infogrid.net.NetMessageEndpointFactory;

import org.infogrid.store.IterableStore;
import org.infogrid.store.Store;
import org.infogrid.store.util.IterableStoreBackedMap;
import org.infogrid.store.util.StoreBackedMap;

import org.infogrid.util.logging.Log;

import java.net.URISyntaxException;
import org.infogrid.mesh.net.NetMeshObject;

/**
 * A NetMeshBase that stores its content in two Stores: one for the NetMeshObjects in
 * the NetMeshBase, and one for the Proxies associated with the NetMeshBase.
 */
public class NetStoreMeshBase
        extends
            AnetMeshBase
{
    private static final Log log = Log.getLogInstance(NetStoreMeshBase.class); // our own, private logger

    /**
     * Factory method.
     * 
     * @param identifier the NetMeshBaseIdentifierof the to-be-created NetMeshBase
     * @param modelBase the ModelBase with the type definitions we use
     * @param accessMgr the AccessManager that controls access to this MeshBase
     * @param meshObjectStore the Store in which to store the MeshObjects
     * @param proxyStore the Store in which to store the Proxies
     * @param endpointFactory the MessageEndpointFactory to use for proxy communication
     * @param c the Context in which this MeshBase will run
     * @return the created NetStoreMeshBase
     * @throws IsAbstractException thrown if the given EntityTypes for the home object are abstract and cannot be instantiated
     */
    public static NetStoreMeshBase create(
            NetMeshBaseIdentifier     identifier,
            ModelBase                 modelBase,
            NetAccessManager          accessMgr,
            Store                     meshObjectStore,
            IterableStore             proxyStore,
            NetMessageEndpointFactory endpointFactory,
            Context                   c )
        throws
            IsAbstractException
    {
        ImmutableMMeshObjectSetFactory setFactory = ImmutableMMeshObjectSetFactory.create( NetMeshObject.class, NetMeshObjectIdentifier.class );
        
        NetStoreMeshBase ret = NetStoreMeshBase.create(
                identifier,
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
     * @param identifier the NetMeshBaseIdentifier of the to-be-created NetMeshBase
     * @param modelBase the ModelBase with the type definitions we use
     * @param accessMgr the AccessManager that controls access to this MeshBase
     * @param meshObjectStore the Store in which to store the MeshObjects
     * @param proxyStore the Store in which to store the Proxies
     * @param endpointFactory the MessageEndpointFactory to use for proxy communication
     * @param c the Context in which this MeshBase will run
     * @return the created NetStoreMeshBase
     * @throws IsAbstractException thrown if the given EntityTypes for the home object are abstract and cannot be instantiated
     */
    public static NetStoreMeshBase create(
            NetMeshBaseIdentifier     identifier,
            MeshObjectSetFactory      setFactory,
            ModelBase                 modelBase,
            NetAccessManager          accessMgr,
            Store                     meshObjectStore,
            IterableStore             proxyStore,
            NetMessageEndpointFactory endpointFactory,
            Context                   c )
        throws
            IsAbstractException
    {
        DefaultProxyFactory proxyFactory = DefaultProxyFactory.create( endpointFactory );

        NetStoreMeshBaseEntryMapper objectMapper = new NetStoreMeshBaseEntryMapper();
        StoreProxyEntryMapper       proxyMapper  = new StoreProxyEntryMapper( proxyFactory );
        
        StoreBackedMap<MeshObjectIdentifier,MeshObject>     objectStorage = StoreBackedMap.createWeak( objectMapper, meshObjectStore );
        IterableStoreBackedMap<NetMeshBaseIdentifier,Proxy> proxyStorage  = IterableStoreBackedMap.createWeak( proxyMapper,  proxyStore );

        NetMeshObjectIdentifierFactory identifierFactory = DefaultAnetMeshObjectIdentifierFactory.create( identifier );
        
        StoreProxyManager proxyManager = StoreProxyManager.create( proxyFactory, proxyStorage );

        NetStoreMeshBase ret = new NetStoreMeshBase( identifier, identifierFactory, setFactory, modelBase, accessMgr, objectStorage, proxyManager, c );

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
    * Constructor. This does not initialize content.
    *
    * @param identifier the MeshBaseIdentifier of this MeshBase
    * @param identifierFactory the factory for MeshObjectIdentifiers appropriate for this MeshBase
    * @param modelBase the ModelBase containing type information
    * @param accessMgr the AccessManager that controls access to this MeshBase
    * @param cache the CachingMap that holds the MeshObjects in this MeshBase
    * @param mapper the mapper for the Store implementation
    * @param proxyManager the ProxyManager for this NetMeshBase
    * @param context the Context in which this MeshBase runs.
    */
    protected NetStoreMeshBase(
            NetMeshBaseIdentifier                           identifier,
            NetMeshObjectIdentifierFactory                  identifierFactory,
            MeshObjectSetFactory                            setFactory,
            ModelBase                                       modelBase,
            NetAccessManager                                accessMgr,
            StoreBackedMap<MeshObjectIdentifier,MeshObject> cache,
            StoreProxyManager                               proxyManager,
            Context                                         c )
    {
        super( identifier, identifierFactory, setFactory, modelBase, accessMgr, cache, proxyManager, c );

        HOME_OBJECT_IDENTIFIER = DefaultAnetMeshObjectIdentifier.create( identifier, "" );
    }

    /**
     * Create an identifier for a MeshObject at held locallt at this MeshBase.
     *
     * @param raw the identifier String
     */
    public NetMeshObjectIdentifier fromExternalForm(
            String raw )
        throws
            URISyntaxException
    {
        DefaultAnetMeshObjectIdentifier ret = DefaultAnetMeshObjectIdentifier.fromExternalForm( getIdentifier(), raw );
        return ret;
    }
    
    /**
     * Determine the Identifier of the Home Object.
     *
     * @return the Identifier
     */
    public NetMeshObjectIdentifier getHomeMeshObjectIdentifier()
    {
        return HOME_OBJECT_IDENTIFIER;
    }
    
    
    /**
     * Helper method for typecasting to the right subtype of CachingMap.
     */
    protected IterableStoreBackedMap<MeshObjectIdentifier, MeshObject> getCachingMap()
    {
        return (IterableStoreBackedMap<MeshObjectIdentifier,MeshObject>) theCache;
    }

    /**
     * The home object identifier.
     */
    protected NetMeshObjectIdentifier HOME_OBJECT_IDENTIFIER;
}
