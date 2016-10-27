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
// Copyright 1998-2016 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.admin.igck;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.mesh.set.MeshObjectSetFactory;
import org.infogrid.mesh.set.m.ImmutableMMeshObjectSetFactory;
import org.infogrid.meshbase.net.DefaultNetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.DefaultNetMeshObjectAccessSpecificationFactory;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.NetMeshObjectAccessSpecificationFactory;
import org.infogrid.meshbase.net.NetMeshObjectIdentifierFactory;
import org.infogrid.meshbase.net.a.AnetMeshBaseLifecycleManager;
import org.infogrid.meshbase.net.local.store.IterableLocalNetStoreMeshBase;
import org.infogrid.meshbase.net.proxy.DefaultProxyFactory;
import org.infogrid.meshbase.net.proxy.NiceAndTrustingProxyPolicyFactory;
import org.infogrid.meshbase.net.proxy.Proxy;
import org.infogrid.meshbase.net.proxy.ProxyMessageEndpointFactory;
import org.infogrid.meshbase.net.proxy.ProxyParameters;
import org.infogrid.meshbase.net.proxy.m.MPingPongNetMessageEndpointFactory;
import org.infogrid.meshbase.net.security.NetAccessManager;
import org.infogrid.meshbase.store.StoreMeshBaseSwappingHashMap;
import org.infogrid.meshbase.store.net.NetStoreMeshBaseEntryMapper;
import org.infogrid.meshbase.store.net.StoreProxyEntryMapper;
import org.infogrid.meshbase.store.net.StoreProxyManager;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.modelbase.ModelBaseSingleton;
import org.infogrid.probe.ProbeDirectory;
import org.infogrid.probe.httpmapping.HttpMappingPolicy;
import org.infogrid.probe.m.MProbeDirectory;
import org.infogrid.probe.manager.ProbeManager;
import org.infogrid.probe.manager.store.StoreScheduledExecutorProbeManager;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.probe.shadow.ShadowParameters;
import org.infogrid.probe.shadow.store.StoreShadowMeshBase;
import org.infogrid.probe.shadow.store.StoreShadowMeshBaseFactory;
import org.infogrid.store.IterableStore;
import org.infogrid.store.Store;
import org.infogrid.store.StoreEntryMapper;
import org.infogrid.store.StoreKeyDoesNotExistException;
import org.infogrid.store.StoreValue;
import org.infogrid.store.prefixing.IterablePrefixingStore;
import org.infogrid.store.util.IterableStoreBackedSwappingHashMap;
import org.infogrid.util.FactoryException;
import org.infogrid.util.context.Context;
import org.infogrid.util.context.SimpleContext;
import org.infogrid.util.logging.Log;

/**
 * An IterableNetStoreMeshBase that has been instrumented for the purposes of Igck.
 */
public class InstrumentedNetStoreMeshBase
    extends
        IterableLocalNetStoreMeshBase
    implements
        InstrumentedMeshBase
{
    private static final Log log = Log.getLogInstance( InstrumentedNetStoreMeshBase.class ); // our own, private logger

    /**
     * Factory method.
     * 
     * @param netMeshBaseId the NetMeshBaseIdentifier for the NetMeshBase
     * @param meshObjectStore the underlying IterableStore for the MeshObjects
     * @param proxyStore the underlying IterableStore for the Proxies
     * @param shadowStore the underlying IterableStore for all the MeshObjects in the Shadow MeshBases
     * @param shadowProxyStore the underlying IterableStore for all the Proxies of all the Shadow MeshBases
     * @return the created InstrumentedStoreMeshBase
     * @throws ParseException thrown if the NetMeshBaseIdentifier could not be parsed
     */
    public static InstrumentedNetStoreMeshBase create(
            String        netMeshBaseId,
            IterableStore meshObjectStore,
            IterableStore proxyStore,
            IterableStore shadowStore,
            IterableStore shadowProxyStore )
        throws
            ParseException
    {
        Context context = SimpleContext.createRoot( "root context" );
        ModelBase modelBase = ModelBaseSingleton.getSingleton();
        ScheduledExecutorService exec = new ScheduledThreadPoolExecutor( 1 );

        ProbeDirectory probeDirectory = MProbeDirectory.create();

        MPingPongNetMessageEndpointFactory endpointFactory = MPingPongNetMessageEndpointFactory.create( exec );

        NiceAndTrustingProxyPolicyFactory proxyPolicyFactory = NiceAndTrustingProxyPolicyFactory.create();

        NetMeshBaseIdentifier identifier = DefaultNetMeshBaseIdentifierFactory.create().fromExternalForm( netMeshBaseId );

        NetMeshObjectAccessSpecificationFactory netMeshObjectAccessSpecificationFactory
                = DefaultNetMeshObjectAccessSpecificationFactory.create( identifier );

        DefaultProxyFactory proxyFactory = DefaultProxyFactory.create( endpointFactory, proxyPolicyFactory );

        NetStoreMeshBaseEntryMapper objectMapper = new NetStoreMeshBaseEntryMapper();
        StoreProxyEntryMapper       proxyMapper  = new StoreProxyEntryMapper( proxyFactory );

        MyMap objectStorage = new MyMap( objectMapper, meshObjectStore );

        IterableStoreBackedSwappingHashMap<NetMeshBaseIdentifier,Proxy> proxyStorage = IterableStoreBackedSwappingHashMap.createWeak( proxyMapper, proxyStore );

        StoreProxyManager              proxyManager = StoreProxyManager.create( proxyFactory, proxyStorage );
        AnetMeshBaseLifecycleManager   life         = AnetMeshBaseLifecycleManager.create();
        ImmutableMMeshObjectSetFactory setFactory   = ImmutableMMeshObjectSetFactory.create( NetMeshObject.class, NetMeshObjectIdentifier.class );

        MPingPongNetMessageEndpointFactory shadowEndpointFactory = MPingPongNetMessageEndpointFactory.create( exec );

        StoreShadowMeshBaseFactory delegate = new MyShadowFactory(
                netMeshObjectAccessSpecificationFactory.getNetMeshBaseIdentifierFactory(),
                shadowEndpointFactory,
                modelBase,
                shadowStore,
                shadowProxyStore,
                context );

        StoreScheduledExecutorProbeManager probeManager = StoreScheduledExecutorProbeManager.create( delegate, probeDirectory, shadowStore );
        shadowEndpointFactory.setNameServer( probeManager.getNetMeshBaseNameServer() );
        delegate.setProbeManager( probeManager );

        endpointFactory.setNameServer( probeManager.getNetMeshBaseNameServer() );

        InstrumentedNetStoreMeshBase ret = new InstrumentedNetStoreMeshBase(
                identifier,
                netMeshObjectAccessSpecificationFactory.getNetMeshObjectIdentifierFactory(),
                netMeshObjectAccessSpecificationFactory.getNetMeshBaseIdentifierFactory(),
                netMeshObjectAccessSpecificationFactory,
                setFactory,
                modelBase,
                life,
                null,
                objectStorage,
                proxyManager,
                probeManager,
                context );

        setFactory.setMeshBase( ret );
        proxyFactory.setNetMeshBase( ret );
        proxyMapper.setMeshBase( ret );
        objectMapper.setMeshBase( ret );
        probeManager.setMainNetMeshBase( ret );
        // do not start it

        // do not initialize home object

        if( log.isDebugEnabled() ) {
            log.debug( "created " + ret );
        }
        return ret;
    }
    
    /**
     * Constructor.
     *
     * @param identifier the NetMeshBaseIdentifier of the to-be-created NetMeshBase
     * @param identifierFactory the factory for NetMeshObjectIdentifiers appropriate for this NetMeshBase
     * @param meshBaseIdentifierFactory the factory for NetMeshBaseIdentifiers
     * @param netMeshObjectAccessSpecificationFactory the factory for NetMeshObjectAccessSpecifications
     * @param setFactory the factory for MeshObjectSets appropriate for this NetMeshBase
     * @param modelBase the ModelBase with the type definitions we use
     * @param life the MeshBaseLifecycleManager to use
     * @param accessMgr the AccessManager that controls access to this MeshBase
     * @param cache the in-memory cache to use
     * @param proxyManager the ProxyManager used by this NetMeshBase
     * @param probeManager the ProbeManager for this LocalNetMeshBase
     * @param context the Context in which this MeshBase will run
     */
    protected InstrumentedNetStoreMeshBase(
            NetMeshBaseIdentifier                                         identifier,
            NetMeshObjectIdentifierFactory                                identifierFactory,
            NetMeshBaseIdentifierFactory                                  meshBaseIdentifierFactory,
            NetMeshObjectAccessSpecificationFactory                       netMeshObjectAccessSpecificationFactory,
            MeshObjectSetFactory                                          setFactory,
            ModelBase                                                     modelBase,
            AnetMeshBaseLifecycleManager                                  life,
            NetAccessManager                                              accessMgr,
            StoreMeshBaseSwappingHashMap<MeshObjectIdentifier,MeshObject> cache,
            StoreProxyManager                                             proxyManager,
            ProbeManager                                                  probeManager,
            Context                                                       context )
    {
        super(  identifier,
                identifierFactory,
                meshBaseIdentifierFactory,
                netMeshObjectAccessSpecificationFactory,
                setFactory,
                modelBase,
                life,
                accessMgr,
                cache,
                proxyManager,
                probeManager,
                context );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void flush(
            MeshObject obj )
    {
        MyMap map = (MyMap) theCache;
        
        map.flush( obj );
    }
    
    /**
     * Make some methods more accessible.
     */
    static class MyMap
        extends
            StoreMeshBaseSwappingHashMap<MeshObjectIdentifier,MeshObject>
    {
        /**
         * Constructor.
         * 
         * @param mapper the <code>StoreEntryMapper</code> to use
         * @param store the underlying <code>Store</code>
         */
        public MyMap(
                StoreEntryMapper<MeshObjectIdentifier,MeshObject> mapper,
                Store                                             store )
        {
            super( mapper, store );
        }
        
        /**
         * Write a particular MeshObject back to disk.
         * 
         * @param obj the MeshObject to write to disk
         */
        public void flush(
                MeshObject obj )
        {
            // the saveValueToStorage does not do anything in the superclass
            super.saveValueToStorageUponCommit( obj.getIdentifier(), obj );
        }
    }
    
    static class MyShadowFactory
        extends
            StoreShadowMeshBaseFactory
    {
        public MyShadowFactory(
                NetMeshBaseIdentifierFactory            meshBaseIdentifierFactory,
                ProxyMessageEndpointFactory             endpointFactory,
                ModelBase                               modelBase,
                IterableStore                           shadowStore,
                IterableStore                           shadowProxyStore,
                Context                                 context )
        {
            super( meshBaseIdentifierFactory, endpointFactory, modelBase, shadowStore, shadowProxyStore, context );
        }

        /**
         * Same as in superclass, except do not ever run updates.
         */
        @Override
        public ShadowMeshBase obtainFor(
                NetMeshBaseIdentifier key,
                ProxyParameters       argument )
            throws
                FactoryException
        {
            try {
                StoreValue homeValue = theShadowStore.get( key.getExternalForm() );
            } catch( StoreKeyDoesNotExistException ex ) {
                return null;
            } catch( IOException ex ) {
                log.error( ex );
            }

            HttpMappingPolicy mappingPolicy
                    = argument instanceof ShadowParameters
                    ? ((ShadowParameters)argument).getHttpMappingPolicy()
                    : theProbeManager.getProbeDirectory().getHttpMappingPolicy();

            NetMeshObjectAccessSpecificationFactory theNetMeshObjectAccessSpecificationFactory = DefaultNetMeshObjectAccessSpecificationFactory.create(
                    key,
                    theMeshBaseIdentifierFactory );

            IterablePrefixingStore thisProxyStore = IterablePrefixingStore.create( key.toExternalForm(), theShadowProxyStore );
  
            StoreShadowMeshBase ret = StoreShadowMeshBase.create(
                    key,
                    theMeshBaseIdentifierFactory,
                    theNetMeshObjectAccessSpecificationFactory,
                    theEndpointFactory,
                    theModelBase,
                    null,
                    theProbeManager.getProbeDirectory(),
                    theTimeNotNeededTillExpires,
                    mappingPolicy,
                    thisProxyStore,
                    theMeshBaseContext );

            ret.setFactory( this );

            // do update

            return ret;
        }        
    }
}
