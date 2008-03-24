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

package org.infogrid.mesh.net.externalized;

import org.infogrid.mesh.externalized.ExternalizedMeshObject;

import org.infogrid.meshbase.net.NetMeshBaseIdentifier;

import org.infogrid.mesh.net.NetMeshObjectIdentifier;

/**
 * Adds Net information to ExternalizedMeshObject.
 */
public interface ExternalizedNetMeshObject
        extends
            ExternalizedMeshObject
{
    /**
     * Obtain the Identifier of the MeshObject.
     *
     * @return the Identifier of the MeshObject
     */
    public abstract NetMeshObjectIdentifier getIdentifier();

    /**
     * Obtain the Identifiers of the neighbors of this MeshObject.
     *
     * @return the Identifiers of the neighbors
     * @see #getRoleTypes
     */
    public abstract NetMeshObjectIdentifier [] getNeighbors();

    /**
     * Obtain the Identifiers of the MeshObjects that participate in an equivalence
     * set with this MeshObject.
     *
     * @return the Identifiers. May be null.
     */
    public abstract NetMeshObjectIdentifier [] getEquivalents();
    
    /**
     * Obtain the GiveUpLock property.
     *
     * @return the GiveUpLock property
     */
    public abstract boolean getGiveUpLock();

    /**
     * Obtain the NetworkIdentifiers of all Proxies.
     *
     * @return the NetworkIdentifiers, if any
     */
    public abstract NetMeshBaseIdentifier[] getProxyNames();

    /**
     * Obtain the NetMeshBaseIdentifier of the Proxy towards the home replica.
     * 
     * @return the NetMeshBaseIdentifier, if any
     */
    public abstract NetMeshBaseIdentifier getProxyTowardsHomeNetworkIdentifier();

    /**
     * Obtain the NetMeshBaseIdentifier of the Proxy towards the replica with the lock.
     * 
     * @return the NetMeshBaseIdentifier, if any
     */
    public abstract NetMeshBaseIdentifier getProxyTowardsLockNetworkIdentifier();
}
