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
import org.infogrid.mesh.net.NetMeshObjectUtils;

import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.Proxy;

import org.infogrid.meshbase.transaction.CannotApplyChangeException;
import org.infogrid.meshbase.transaction.MeshObjectNeighborAddedEvent;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.meshbase.transaction.TransactionException;

import org.infogrid.model.primitives.MeshTypeIdentifier;
import org.infogrid.model.primitives.MeshTypeUtils;
import org.infogrid.model.primitives.RoleType;

import org.infogrid.util.logging.Log;

/**
 *
 */
public class NetMeshObjectNeighborAddedEvent
        extends
            MeshObjectNeighborAddedEvent
        implements
            NetMeshObjectNeighborChangeEvent
{
    private static final Log log = Log.getLogInstance( NetMeshObjectNeighborAddedEvent.class ); // our own, private logger

    /**
     * Constructor.
     */
    public NetMeshObjectNeighborAddedEvent(
            NetMeshObject         meshObject,
            RoleType []           addedRoleTypes,
            NetMeshObject []      oldNeighbors,
            NetMeshObject         deltaNeighbor,
            NetMeshObject []      newNeighbors,
            NetMeshBaseIdentifier incomingProxy,
            long                  updateTime )
    {
        this(   meshObject,
                meshObject.getIdentifier(),
                addedRoleTypes,
                MeshTypeUtils.meshTypeIdentifiers( addedRoleTypes ),
                oldNeighbors,
                NetMeshObjectUtils.netMeshObjectIdentifiers( oldNeighbors ),
                new NetMeshObject[] { deltaNeighbor },
                new NetMeshObjectIdentifier[] { deltaNeighbor.getIdentifier() },
                newNeighbors,
                NetMeshObjectUtils.netMeshObjectIdentifiers( newNeighbors ),
                incomingProxy,
                updateTime );
    }

    /**
     * Convenience constructor.
     */
    public NetMeshObjectNeighborAddedEvent(
            NetMeshObject              meshObject,
            RoleType []                addedRoleTypes,
            NetMeshObjectIdentifier [] oldNeighborIdentifiers,
            NetMeshObjectIdentifier    deltaNeighborIdentifier,
            NetMeshObjectIdentifier [] newNeighborIdentifiers,
            NetMeshBaseIdentifier      incomingProxy,
            long                       updateTime )
    {
        this(   meshObject,
                meshObject.getIdentifier(),
                addedRoleTypes,
                MeshTypeUtils.meshTypeIdentifiers( addedRoleTypes ),
                null,
                oldNeighborIdentifiers,
                null,
                new NetMeshObjectIdentifier[] { deltaNeighborIdentifier },
                null,
                newNeighborIdentifiers,
                incomingProxy,
                updateTime );
    }

    /**
     * Constructor.
     */
    public NetMeshObjectNeighborAddedEvent(
            NetMeshObjectIdentifier    meshObjectIdentifier,
            MeshTypeIdentifier []      addedRoleTypeIdentifiers,
            NetMeshObjectIdentifier [] oldNeighbors,
            NetMeshObjectIdentifier    deltaNeighbor,
            NetMeshObjectIdentifier [] newNeighbors,
            NetMeshBaseIdentifier      incomingProxy,
            long                       updateTime )
    {
        this(   null,
                meshObjectIdentifier,
                null,
                addedRoleTypeIdentifiers,
                null,
                oldNeighbors,
                null,
                new NetMeshObjectIdentifier[] { deltaNeighbor },
                null,
                newNeighbors,
                incomingProxy,
                updateTime );
    }

    /**
     * Constructor for the case where we don't have old and new values, only the delta.
     * This perhaps should trigger some exception if it is attempted to read old or
     * new values later. (FIXME?)
     */
    public NetMeshObjectNeighborAddedEvent(
            NetMeshObjectIdentifier meshObjectIdentifier,
            MeshTypeIdentifier []   addedRoleTypeIdentifiers,
            NetMeshObjectIdentifier deltaNeighbor,
            NetMeshBaseIdentifier   incomingProxy,
            long                    updateTime )
    {
        this(   null,
                meshObjectIdentifier,
                null,
                addedRoleTypeIdentifiers,
                null,
                null,
                null,
                new NetMeshObjectIdentifier[] { deltaNeighbor },
                null,
                null,
                incomingProxy,
                updateTime );
    }
    
     /**
     * Main constructor.
     *
     * @param meshObject the MeshObject that is the source of the event (optional)
     * @param meshObjectIdentifier Identifier of the MeshObject that is the source of the event (required)
     * @param roleTypes the RoleTypes affected on the source MeshObject, with respect to the deltaNeighbors (optional)
     * @param roleTypeIdentifiers Identifiers of the RoleTypes affected on the source MeshObject, with respect to the deltaNeighbors (required)
     * @param oldNeighbors the set of neighbor MeshObjects prior to the event (optional)
     * @param oldNeighborIdentifiers the Identifiers of the neighbor MeshObjects prior to the event (required)
     * @param deltaNeighbors the set of neighbor MeshObjects affected by this event (optional)
     * @param deltaNeighborIdentifiers the Identifiers of the neighbor MeshObjects affected by this event (required)
     * @param newNeighbors the set of neighbor MeshObjects after the event (optional)
     * @param newNeighborIdentifiers the Identifiers of the neighbor MeshObjects after the event (required)
     * @param updateTime the time at which the change was made, in System.currentTimeMillis() format
     */
    protected NetMeshObjectNeighborAddedEvent(
            NetMeshObject              meshObject,
            NetMeshObjectIdentifier    meshObjectIdentifier,
            RoleType []                roleTypes,
            MeshTypeIdentifier[]       roleTypeIdentifiers,
            NetMeshObject []           oldNeighbors,
            NetMeshObjectIdentifier [] oldNeighborIdentifiers,
            NetMeshObject []           deltaNeighbors,
            NetMeshObjectIdentifier [] deltaNeighborIdentifiers,
            NetMeshObject []           newNeighbors,
            NetMeshObjectIdentifier [] newNeighborIdentifiers,
            NetMeshBaseIdentifier      incomingProxy,
            long                       updateTime )
    {
        super(  meshObject,
                meshObjectIdentifier,
                roleTypes,
                roleTypeIdentifiers,
                oldNeighbors,
                oldNeighborIdentifiers,
                deltaNeighbors,
                deltaNeighborIdentifiers,
                newNeighbors,
                newNeighborIdentifiers,
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
     * Obtain the neighbor that changed.
     *
     * @return the neighbor MeshObject
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

        NetMeshObject              otherObject = null; // make compiler happy
        NetMeshObjectIdentifier [] relatedOtherObjects;
        RoleType []                roleTypes;

        try {
            tx = otherMeshBase.createTransactionNowIfNeeded();

            otherObject         = (NetMeshObject) getSource();
            relatedOtherObjects = (NetMeshObjectIdentifier []) getDeltaValueIdentifier();
            roleTypes           = getProperty();

            for( int i=0 ; i<relatedOtherObjects.length ; ++i ) {
                otherObject.rippleRelate( relatedOtherObjects[i], otherMeshBase );
                if( roleTypes != null && roleTypes.length > 0 ) {
                    otherObject.rippleBless( roleTypes, relatedOtherObjects[i] );
                }
            }

        } catch( TransactionException ex ) {
            throw ex;

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
