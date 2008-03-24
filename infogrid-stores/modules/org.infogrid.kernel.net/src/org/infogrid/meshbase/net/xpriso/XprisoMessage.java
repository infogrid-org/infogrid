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

package org.infogrid.meshbase.net.xpriso;

import org.infogrid.comm.CarriesInvocationId;

import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.net.externalized.ExternalizedNetMeshObject;

import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshObjectAccessSpecification;
import org.infogrid.meshbase.net.transaction.NetMeshObjectCreatedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectDeletedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectNeighborAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectNeighborRemovedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectPropertyChangeEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectRoleAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectRoleRemovedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectTypeAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectTypeRemovedEvent;

/**
 * An XprisoMessage.
 */
public interface XprisoMessage
        extends
            CarriesInvocationId
{
    /**
     * Determine whether this message contains any valid payload or is empty.
     *
     * @return true if it is empty
     */
    public boolean isEmpty();
    
    /**
     * Obtain the request ID.
     *
     * @return the request ID
     */
    public long getRequestId();

    /**
     * Obtain the response ID.
     *
     * @return the response ID
     */
    public long getResponseId();

    /**
     * Obtain the NetMeshBaseIdentifier of the sender.
     * 
     * @return the sender's NNetMeshBaseIdentifier
     */
    public NetMeshBaseIdentifier getSenderIdentifier();
    
    /**
     * Obtain the NetMeshBaseIdentifier of the receiver.
     * 
     * @return the receiver's NNetMeshBaseIdentifier
     */
    public NetMeshBaseIdentifier getReceiverIdentifier();

    /**
     * Obtain the NetworkPaths to the MeshObjects for which the sender requests
     * a lease for the first time.
     *
     * @return the NetworkPaths for the MeshObjects
     */
    public NetMeshObjectAccessSpecification[] getRequestedFirstTimeObjects();

    /**
     * Obtain the identifiers for the MeshObjects for which the sender requests
     * that a currently valid lease be chanceled.
     *
     * @return the IdentifierValues for the MeshObjects
     */
    public MeshObjectIdentifier[] getRequestedCanceledObjects();

    /**
     * Obtain the creation events that the sender needs to convey to the
     * receiver
     *
     * @return the creation events
     */
    public NetMeshObjectCreatedEvent [] getCreations();

    /**
     * Obtain the identifiers for the MeshObjects that the sender has deleted
     * semantically, and of whose deletion the receivers needs to be notified.
     *
     * @return the IdentifierValues for the MeshObjects
     */
    public NetMeshObjectDeletedEvent [] getDeleteChanges();

    /**
     * Obtain the externalized representation of the MeshObjects that are conveyed
     * by the sender to the receiver, e.g. in response to a first-time lease request.
     *
     * @return the MeshObjects
     */
    public ExternalizedNetMeshObject[] getConveyedMeshObjects();

    /**
     * Obtain the neighbor change events that the sender needs to convey to the
     * receiver.
     *
     * @return the neighbor change events
     */
    public NetMeshObjectNeighborAddedEvent [] getNeighborAdditions();

    /**
     * Obtain the neighbor change events that the sender needs to convey to the
     * receiver.
     *
     * @return the neighbor change events
     */
    public NetMeshObjectNeighborRemovedEvent [] getNeighborRemovals();

    /**
     * Obtain the property change events that the sender needs to convey to the
     * receiver.
     *
     * @return the property change events
     */
    public NetMeshObjectPropertyChangeEvent [] getPropertyChanges();

    /**
     * Obtain the role change events that the sender needs to convey to the
     * receiver.
     *
     * @return the role change events
     */
    public NetMeshObjectRoleAddedEvent [] getRoleAdditions();

    /**
     * Obtain the role change events that the sender needs to convey to the
     * receiver.
     *
     * @return the role change events
     */
    public NetMeshObjectRoleRemovedEvent [] getRoleRemovals();

    /**
     * Obtain the type addition events that the sender needs to convey to the
     * receiver.
     *
     * @return the type addition events
     */
    public NetMeshObjectTypeAddedEvent [] getTypeAdditions();

    /**
     * Obtain the type addition removed that the sender needs to convey to the
     * receiver.
     *
     * @return the type removed events
     */
    public NetMeshObjectTypeRemovedEvent [] getTypeRemovals();

    /**
     * Obtain the identifiers for the MeshObjects for which the sender requests
     * the lock from the receiver (i.e. update rights).
     *
     * @return the IdentifierValues for the MeshObjects
     */
    public MeshObjectIdentifier[] getRequestedLockObjects();

    /**
     * Obtain the identifiers for the MeshObjects for which the sender surrenders
     * the lock to the receiver (i.e. update rights).
     *
     * @return the IdentifierValues for the MeshObjects
     */
    public MeshObjectIdentifier[] getPushLockObjects();

    /**
     * Obtain the identifiers for the MeshObjects for which the sender has forcefully
     * reclaimed the lock.
     *
     * @return the IdentifierValues for the MeshObjects
     */
    public MeshObjectIdentifier[] getReclaimedLockObjects();

    /**
     * Obtain the identifiers for the MeshObjects for which the sender has a replica
     * that it wishes to resynchronize as a dependent replica.
     *
     * @return the IdentifierValues for the MeshObjects
     */
    public MeshObjectIdentifier[] getRequestedResynchronizeDependentReplicas();

    /**
     * Obtain the externalized representation of the MeshObjects that are sent
     * by the sender to the receiver in response to a resynchronizeDependent request.
     *
     * @return the MeshObjects
     */
    public ExternalizedNetMeshObject[] getResynchronizeDependentReplicas();

    /**
     * Determine whether or not to cease communications after this message.
     *
     * @return if true, cease communications
     */
    public boolean getCeaseCommunications();
}
