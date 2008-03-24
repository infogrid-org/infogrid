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
import org.infogrid.mesh.net.NetMeshObjectIdentifier;

import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.Proxy;
import org.infogrid.meshbase.transaction.CannotApplyChangeException;
import org.infogrid.meshbase.transaction.MeshObjectRoleRemovedEvent;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.meshbase.transaction.TransactionException;

import org.infogrid.model.primitives.MeshTypeIdentifier;
import org.infogrid.model.primitives.MeshTypeUtils;
import org.infogrid.model.primitives.RoleType;

import org.infogrid.util.logging.Log;

/**
 *
 */
public class NetMeshObjectRoleRemovedEvent
        extends
            MeshObjectRoleRemovedEvent
        implements
            NetMeshObjectRoleChangeEvent
{
    private static final Log log = Log.getLogInstance( NetMeshObjectRoleRemovedEvent.class ); // our own, private logger

    /**
     * Constructor.
     * 
     * @param meshObject the MeshObject whose role participation changed
     * @param thisEnd the RoleTypes whose participation changed
     * @param otherSide the NetMeshObject from which the RoleTypes were removed
     * @param updateTime the time when the update occurred
     */
    public NetMeshObjectRoleRemovedEvent(
            NetMeshObject     meshObject,
            RoleType []       oldRoleTypes,
            RoleType []       removedRoleTypes,
            RoleType []       newRoleTypes,
            NetMeshObject     otherSide,
            NetMeshBaseIdentifier incomingProxy,
            long              updateTime )
    {
        this(   meshObject,
                meshObject.getIdentifier(),
                oldRoleTypes,
                MeshTypeUtils.meshTypeIdentifiers( oldRoleTypes ),
                removedRoleTypes,
                MeshTypeUtils.meshTypeIdentifiers( removedRoleTypes ),
                newRoleTypes,
                MeshTypeUtils.meshTypeIdentifiers( newRoleTypes ),
                otherSide,
                otherSide.getIdentifier(),
                incomingProxy,
                updateTime );
    }

    /**
     * Constructor for the case where we don't have old and new values, only the delta.
     * This perhaps should trigger some exception if it is attempted to read old or
     * new values later. (FIXME?)
     */
    public NetMeshObjectRoleRemovedEvent(
            NetMeshObjectIdentifier meshObjectIdentifier,
            MeshTypeIdentifier []   removedRoleTypes,
            NetMeshObjectIdentifier otherSideIdentifier,
            NetMeshBaseIdentifier   incomingProxy,
            long                    updateTime )
    {
        this(   null,
                meshObjectIdentifier,
                null,
                null,
                null,
                removedRoleTypes,
                null,
                null,
                null,
                otherSideIdentifier,
                incomingProxy,
                updateTime );
    }

    /**
     * Main constructor.
     */
    protected NetMeshObjectRoleRemovedEvent(
            NetMeshObject           meshObject,
            NetMeshObjectIdentifier meshObjectIdentifier,
            RoleType []             oldRoleTypes,
            MeshTypeIdentifier []   oldRoleTypeIdentifiers,
            RoleType []             deltaRoleTypes,
            MeshTypeIdentifier []   deltaRoleTypeIdentifiers,
            RoleType []             newRoleTypes,
            MeshTypeIdentifier []   newRoleTypeIdentifiers,
            NetMeshObject           neighbor,
            NetMeshObjectIdentifier neighborIdentifier,
            NetMeshBaseIdentifier   incomingProxy,
            long                    updateTime )
    {
        super(  meshObject,
                meshObjectIdentifier,
                oldRoleTypes,
                oldRoleTypeIdentifiers,
                deltaRoleTypes,
                deltaRoleTypeIdentifiers,
                newRoleTypes,
                newRoleTypeIdentifiers,
                neighbor,
                neighborIdentifier,
                updateTime );

        theIncomingProxy = incomingProxy;
    }
    
    /**
     * Obtain the MeshObject affected by this Change.
     *
     * @return obtain the MeshObject affected by this Change
     */
    @Override
    public NetMeshObject getAffectedMeshObject()
    {
        return (NetMeshObject) super.getAffectedMeshObject();
    }

    /**
     * Obtain the neighbor MeshObject affected by this Change.
     *
     * @return obtain the neighbor MeshObject affected by this Change
     */
    @Override
    public NetMeshObject getNeighborMeshObject()
    {
        return (NetMeshObject) super.getNeighborMeshObject();
    }

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
     * @throws CannotApplyChangeException thrown if the Change could not be applied, e.g because
     *         the affected MeshObject did not exist in the other MeshBase
     */
    public NetMeshObject applyToReplicaIn(
            NetMeshBase otherMeshBase )
        throws
            CannotApplyChangeException,
            TransactionException
    {
        setResolver( otherMeshBase );

        Transaction tx = null;
        try {
            tx = otherMeshBase.createTransactionNowIfNeeded();

            NetMeshObject otherObject        = (NetMeshObject) getSource();
            NetMeshObject relatedOtherObject = getNeighborMeshObject();
            RoleType []   roleTypes          = getDeltaValue();

            otherObject.rippleUnbless( roleTypes, relatedOtherObject.getIdentifier() );

            return otherObject;

        } catch( TransactionException ex ) {
            throw ex;

        } catch( Throwable ex ) {
            throw new CannotApplyChangeException.ExceptionOccurred( otherMeshBase, ex );

        } finally {
            if( tx != null ) {
                tx.commitTransaction();
            }
        }
    }

    /**
     * Obtain the Proxy, if any, from where this NetChange originated.
     *
     * @return the Proxy, if any
     */
    public final NetMeshBaseIdentifier getOriginNetworkIdentifier()
    {
        return theIncomingProxy;
    }

    /**
     * Determine whether this NetChange should be forwarded through the outgoing Proxy.
     * If specified, the incomingProxy parameter specifies where the NetChange came from.
     *
     * @param incomingProxy the incoming Proxy
     * @param outgoingProxy the outgoing Proxy
     * @return true if the NetChange should be forwarded.
     */
    public boolean shouldBeSent(
            Proxy outgoingProxy )
    {
        return Utils.hasReplicaInDirection( this, outgoingProxy, theIncomingProxy );
    }
    
    /**
     * The incoming Proxy, if any.
     */
    protected NetMeshBaseIdentifier theIncomingProxy;
}
