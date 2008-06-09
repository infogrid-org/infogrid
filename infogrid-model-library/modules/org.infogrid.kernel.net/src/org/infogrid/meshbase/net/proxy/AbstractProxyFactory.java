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

package org.infogrid.meshbase.net.proxy;

import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.net.NetMessageEndpointFactory;

/**
 * Factors out common functionality of ProxyFactories.
 */
public abstract class AbstractProxyFactory
        implements
            ProxyFactory
{
    /**
     * Constructor.
     * 
     * @param endpointFactory the NetMessageEndpointFactory to use to communicate
     * @param proxyPolicyFactory the factory for ProxyPolicies for communications with other NetMeshBases
     */
    protected AbstractProxyFactory(
            NetMessageEndpointFactory endpointFactory,
            ProxyPolicyFactory        proxyPolicyFactory )
    {
        theEndpointFactory    = endpointFactory;
        theProxyPolicyFactory = proxyPolicyFactory;
    }

    /**
     * Set the NetMeshBase to be used with new Proxies.
     *
     * @param base the NetMeshBase to use
     */
    public void setNetMeshBase(
            NetMeshBase base )
    {
        theNetMeshBase = base;
    }
    
    /**
     * Obtain the NetMeshBase to be used with new Proxies.
     * 
     * @return the NetMeshBase
     */
    public NetMeshBase getNetMeshBase()
    {
        return theNetMeshBase;
    }

    /**
     * The NetMeshBase to be used with new Proxies.
     */
    protected NetMeshBase theNetMeshBase;

    /**
     * Factory for endpoints.
     */
    protected NetMessageEndpointFactory theEndpointFactory;
    
    /**
     * Factory for ProxyPolicies.
     */
    protected ProxyPolicyFactory theProxyPolicyFactory;
}