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

package org.infogrid.meshbase.net.transaction;

import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.Proxy;
import org.infogrid.meshbase.transaction.CannotApplyChangeException;
import org.infogrid.meshbase.transaction.Change;
import org.infogrid.meshbase.transaction.TransactionException;

/**
 * Extends Change to cover the NetMeshBase use cases.
 */
public interface NetChange<S,SID,V,VID>
        extends
            Change<S,SID,V,VID>
{
    /**
     * Obtain the MeshObject affected by this Change.
     *
     * @return obtain the NetMeshObject affected by this Change
     */
    public abstract NetMeshObject getAffectedMeshObject();

    /**
     * Apply this NetChange to a MeshObject in this MeshBase that is a replica
     * of the NetMeshObject which caused the NetChange. This method
     * is intended to make it easy to replicate Changes that were made to a
     * replica of one NetMeshObject in one NetMeshBase to another replica
     * of the NetMeshObject in another NetMeshBase.
     *
     * <p>This method will attempt to create a Transaction if none is present on the
     * current Thread.</p>
     *
     * @param otherMeshBase the other MeshBase in which to apply the change
     * @return the replica to which the Change was applied
     * @throws CannotApplyChangeException thrown if the Change could not be applied, e.g because
     *         the affected MeshObject did not exist in the other MeshBase
     */
    public NetMeshObject applyToReplicaIn(
            NetMeshBase otherMeshBase )
        throws
            CannotApplyChangeException,
            TransactionException;

    /**
     * Obtain the NetMeshBaseIdentifier, if any, from where this NetChange originated.
     * 
     * @return the NetMeshBaseIdentifier, if any
     */
    public abstract NetMeshBaseIdentifier getOriginNetworkIdentifier();
    
    /**
     * Determine whether this NetChange should be forwarded through the outgoing Proxy.
     * If specified, the incomingProxy property specifies where the NetChange came from.
     *
     * @param outgoingProxy the outgoing Proxy
     * @return true if the NetChange should be forwarded.
     */
    public abstract boolean shouldBeSent(
            Proxy outgoingProxy );
}
