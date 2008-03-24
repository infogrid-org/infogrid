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

package org.infogrid.meshbase.net.local.a;

import org.infogrid.context.Context;

import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;

import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshObjectIdentifierFactory;
import org.infogrid.meshbase.net.Proxy;
import org.infogrid.meshbase.net.ProxyManager;
import org.infogrid.meshbase.net.a.AnetMeshBase;
import org.infogrid.meshbase.net.local.LocalNetMeshBase;
import org.infogrid.meshbase.net.security.NetAccessManager;

import org.infogrid.modelbase.ModelBase;

import org.infogrid.probe.manager.ProbeManager;
import org.infogrid.probe.shadow.ShadowMeshBase;

import org.infogrid.util.CachingMap;
import org.infogrid.util.NameServer;
import org.infogrid.util.FactoryException;

import java.util.Collection;
import org.infogrid.mesh.set.MeshObjectSetFactory;

/**
 * This NetMeshBase manages local ShadowMeshBases.
 */
public abstract class LocalAnetMeshBase
        extends
            AnetMeshBase
        implements
            LocalNetMeshBase           
{
    /**
     * Constructor for subclasses only. This does not initialize content.
     *
     * @param identifier the MeshBaseIdentifier of this MeshBase
     * @param identifierFactory the factory for MeshObjectIdentifiers appropriate for this MeshBase
     * @param modelBase the ModelBase containing type information
     * @param accessMgr the AccessManager that controls access to this MeshBase
     * @param cache the CachingMap that holds the MeshObjects in this MeshBase
     * @param proxyManager the ProxyManager for this NetMeshBase
     * @param probeManager the ProbeManager for this LocalNetMeshBase
     * @param endpointFactory the MessageEndpointFactory to use for proxy communication
     * @param context the Context in which this MeshBase runs.
     */
    protected LocalAnetMeshBase(
            NetMeshBaseIdentifier                       identifier,
            NetMeshObjectIdentifierFactory              identifierFactory,
            MeshObjectSetFactory                        setFactory,
            ModelBase                                   modelBase,
            NetAccessManager                            accessMgr,
            CachingMap<MeshObjectIdentifier,MeshObject> cache,
            ProxyManager                                proxyManager,
            ProbeManager                                probeManager,
            Context                                     c )
    {
        super( identifier, identifierFactory, setFactory, modelBase, accessMgr, cache, proxyManager, c );
        
        theProbeManager = probeManager;
    }

    /**
     * Obtain or create a Proxy to the specified NetMeshBaseIdentifier.
     * 
     * @param networkIdentifier the NetMeshBaseIdentifier
     * @param coherence the CoherenceSpecification to use
     * @return the Proxy
     */
    @Override
    public Proxy obtainProxyFor(
            NetMeshBaseIdentifier  networkIdentifier,
            CoherenceSpecification coherence )
        throws
            FactoryException
    {
        // first create the shadow -- if it throws an exception, we won't create the Proxy
        ShadowMeshBase shadow = theProbeManager.obtainFor( networkIdentifier, coherence );

        Proxy ret = theProxyManager.obtainFor( networkIdentifier, coherence );
        
        return ret;
    }
    
    /**
     * Obtain a ShadowMeshBase that we are operating.
     *
     * @return the ShadowMeshBase, or null
     */
    public ShadowMeshBase getShadowMeshBaseFor(
            NetMeshBaseIdentifier networkId )
    {
        ShadowMeshBase ret = theProbeManager.get( networkId );
        return ret;
    }

    /**
     * Obtain all ShadowMeshBases that we are operating.
     *
     * @return all ShadowMeshBases
     */
    public Collection<ShadowMeshBase> getAllShadowMeshBases()
    {
        Collection<ShadowMeshBase> ret = theProbeManager.values();
        return ret;
    }

    /**
     * Obtain the NetMeshBases (this one and all shadows) as a NameServer.
     * 
     * @return NameServer
     */
    public NameServer<NetMeshBaseIdentifier,NetMeshBase> getLocalNameServer()
    {
        return theProbeManager.getNetMeshBaseNameServer();
    }

    /**
     * Obtain the ProbeManager.
     * 
     * @return the ProbeManager
     */
    public ProbeManager getProbeManager()
    {
        return theProbeManager;
    }

    /**
     * Kill off the ProbeManager upon die().
     * 
     * @param isPermanent if true, this MeshBase will go away permanently; if false, it may come alive again some time later
     */
    @Override
    protected void internalDie(
            boolean isPermanent )
    {
        theProbeManager.die( isPermanent );
        theProbeManager = null;
        
        super.internalDie( isPermanent );
    }

    /**
     * Our ProbeManager.
     */
    protected ProbeManager theProbeManager;
}
