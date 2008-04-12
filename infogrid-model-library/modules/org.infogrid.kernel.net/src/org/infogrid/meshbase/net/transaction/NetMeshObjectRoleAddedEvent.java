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

import org.infogrid.mesh.RoleTypeBlessedAlreadyException;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.Proxy;
import org.infogrid.meshbase.transaction.CannotApplyChangeException;
import org.infogrid.meshbase.transaction.MeshObjectRoleAddedEvent;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.meshbase.transaction.TransactionException;
import org.infogrid.model.primitives.MeshTypeIdentifier;
import org.infogrid.model.primitives.MeshTypeUtils;
import org.infogrid.model.primitives.RoleType;
import org.infogrid.util.logging.Log;


/**
 *
 */
public class NetMeshObjectRoleAddedEvent
        extends
            MeshObjectRoleAddedEvent
        implements
            NetMeshObjectRoleChangeEvent
{
    private static final Log log = Log.getLogInstance( NetMeshObjectRoleAddedEvent.class ); // our own, private logger

    /**
     * Constructor.
     * 
     * @param meshObject the MeshObject whose role participation changed
     * @param thisEnd the RoleTypes whose participation changed
     * @param otherSide the NetMeshObject to which the RoleTypes were added
     * @param updateTime the time when the update occurred
     */
    public NetMeshObjectRoleAddedEvent(
            NetMeshObject         meshObject,
            RoleType []           oldRoleTypes,
            RoleType []           addedRoleTypes,
            RoleType []           newRoleTypes,
            NetMeshObject         otherSide,
            NetMeshBaseIdentifier incomingProxy,
            long                  updateTime )
    {
        this(   meshObject,
                meshObject.getIdentifier(),
                oldRoleTypes,
                MeshTypeUtils.meshTypeIdentifiers( oldRoleTypes ),
                addedRoleTypes,
                MeshTypeUtils.meshTypeIdentifiers( addedRoleTypes ),
                newRoleTypes,
                MeshTypeUtils.meshTypeIdentifiers( newRoleTypes ),
                otherSide,
                otherSide.getIdentifier(),
                incomingProxy,
                updateTime );
    }

    /**
     * Constructor.
     * 
     * @param meshObject the MeshObject whose role participation changed
     * @param thisEnd the RoleTypes whose participation changed
     * @param otherSide the NetMeshObject to which the RoleTypes were added
     * @param updateTime the time when the update occurred
     */
    public NetMeshObjectRoleAddedEvent(
            NetMeshObject           meshObject,
            RoleType []             oldRoleTypes,
            RoleType []             addedRoleTypes,
            RoleType []             newRoleTypes,
            NetMeshObjectIdentifier otherSideIdentifier,
            NetMeshBaseIdentifier       incomingProxy,
            long                    updateTime )
    {
        this(   meshObject,
                meshObject.getIdentifier(),
                oldRoleTypes,
                MeshTypeUtils.meshTypeIdentifiers( oldRoleTypes ),
                addedRoleTypes,
                MeshTypeUtils.meshTypeIdentifiers( addedRoleTypes ),
                newRoleTypes,
                MeshTypeUtils.meshTypeIdentifiers( newRoleTypes ),
                null,
                otherSideIdentifier,
                incomingProxy,
                updateTime );
    }

    /**
     * Constructor for the case where we don't have old and new values, only the delta.
     * This perhaps should trigger some exception if it is attempted to read old or
     * new values later. (FIXME?)
     */
    public NetMeshObjectRoleAddedEvent(
            NetMeshObjectIdentifier meshObjectIdentifier,
            MeshTypeIdentifier []   addedRoleTypes,
            NetMeshObjectIdentifier otherSideIdentifier,
            NetMeshBaseIdentifier   incomingProxy,
            long                    updateTime )
    {
        this(   null,
                meshObjectIdentifier,
                null,
                null,
                null,
                addedRoleTypes,
                null,
                null,
                null,
                otherSideIdentifier,
                incomingProxy,
                updateTime );
    }

    /**
     * Pass-through constructor for subclasses.
     */
    protected NetMeshObjectRoleAddedEvent(
            NetMeshObject           meshObject,
            NetMeshObjectIdentifier meshObjectIdentifier,
            RoleType []             oldRoleTypes,
            MeshTypeIdentifier []   oldRoleTypeIdentifiers,
            RoleType []             addedRoleTypes,
            MeshTypeIdentifier []   addedRoleTypeIdentifiers,
            RoleType []             newRoleTypes,
            MeshTypeIdentifier []   newRoleTypesIdentifiers,
            NetMeshObject           otherSide,
            NetMeshObjectIdentifier otherSideIdentifier,
            NetMeshBaseIdentifier    incomingProxy,
            long                    updateTime )
    {
        super(  meshObject,
                meshObjectIdentifier,
                oldRoleTypes,
                oldRoleTypeIdentifiers,
                addedRoleTypes,
                addedRoleTypeIdentifiers,
                newRoleTypes,
                newRoleTypesIdentifiers,
                otherSide,
                otherSideIdentifier,
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
     * Obtain the Identifier of the neighbor MeshObject affected by this Change.
     *
     * @return the Identifier of the neighbor MeshObject affected by this Change
     */
    @Override
    public NetMeshObjectIdentifier getNeighborMeshObjectIdentifier()
    {
        return (NetMeshObjectIdentifier) super.getNeighborMeshObjectIdentifier();
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

        NetMeshObject otherObject = null; // make compiler happy

        Transaction tx = null;
        try {
            tx = otherMeshBase.createTransactionNowIfNeeded();

                                    otherObject        = (NetMeshObject) getSource();
            NetMeshObjectIdentifier relatedOtherObject = getNeighborMeshObjectIdentifier();
            RoleType []             roleTypes          = getDeltaValue();

            otherObject.rippleBless( roleTypes, relatedOtherObject );

        } catch( TransactionException ex ) {
            throw ex;

//        } catch( RoleTypeBlessedAlreadyException ex ) {
//            // that's fine
//            if( log.isDebugEnabled() ) {
//                log.debug( this + " role type blessed already" );
//            }

        } catch( Throwable ex ) {
            throw new CannotApplyChangeException.ExceptionOccurred( otherMeshBase, ex );

        } finally {
            if( tx != null ) {
                tx.commitTransaction();
            }
        }
        return otherObject;
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
