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

import java.util.Collection;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.set.MeshObjectSetFactory;
import org.infogrid.meshbase.MeshBaseNameServer;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshObjectIdentifierFactory;
import org.infogrid.meshbase.net.proxy.Proxy;
import org.infogrid.meshbase.net.proxy.ProxyManager;
import org.infogrid.meshbase.net.a.AnetMeshBase;
import org.infogrid.meshbase.net.a.AnetMeshBaseLifecycleManager;
import org.infogrid.meshbase.net.local.LocalNetMeshBase;
import org.infogrid.meshbase.net.security.NetAccessManager;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.probe.manager.ProbeManager;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.util.CachingMap;
import org.infogrid.util.FactoryException;
import org.infogrid.util.context.Context;

/**
 * This NetMeshBase manages local ShadowMeshBases, ie in the same address space.
 */
public abstract class LocalAnetMeshBase
        extends
            AnetMeshBase
        implements
            LocalNetMeshBase           
{
    /**
     * Constructor for subclasses only.
     *
     * @param identifier the NetMeshBaseIdentifier of this NetMeshBase
     * @param identifierFactory the factory for NetMeshObjectIdentifiers appropriate for this NetMeshBase
     * @param setFactory the factory for MeshObjectSets appropriate for this NetMeshBase
     * @param modelBase the ModelBase containing type information
     * @param life the MeshBaseLifecycleManager to use
     * @param accessMgr the AccessManager that controls access to this NetMeshBase
     * @param cache the CachingMap that holds the NetMeshObjects in this NetMeshBase
     * @param proxyManager the ProxyManager used by this NetMeshBase
     * @param probeManager the ProbeManager for this LocalNetMeshBase
     * @param context the Context in which this NetMeshBase runs.
     */
    protected LocalAnetMeshBase(
            NetMeshBaseIdentifier                       identifier,
            NetMeshObjectIdentifierFactory              identifierFactory,
            MeshObjectSetFactory                        setFactory,
            ModelBase                                   modelBase,
            AnetMeshBaseLifecycleManager                life,
            NetAccessManager                            accessMgr,
            CachingMap<MeshObjectIdentifier,MeshObject> cache,
            ProxyManager                                proxyManager,
            ProbeManager                                probeManager,
            Context                                     context )
    {
        super( identifier, identifierFactory, setFactory, modelBase, life, accessMgr, cache, proxyManager, context );
        
        theProbeManager = probeManager;
    }

     /**
     * Obtain or create a Proxy for communication with a NetMeshBase at the specified NetMeshBaseIdentifier.
     * 
     * @param networkIdentifier the NetMeshBaseIdentifier
     * @param coherence the CoherenceSpecification to use, if any
     * @return the Proxy
     * @throws FactoryException thrown if the Proxy could not be created
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
     * Obtain an existing ShadowMeshBase operated by this LocalAnetMeshBase with the specified
     * NetMeshBaseIdentifier. Return null if no such ShadowMeshBase exists. Do not attempt to create one.
     *
     * @param networkId the NetMeshBaseIdentifier
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
    public MeshBaseNameServer getLocalNameServer()
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