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

import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;

import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;

import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.Proxy;

import org.infogrid.meshbase.transaction.CannotApplyChangeException;
import org.infogrid.meshbase.transaction.MeshObjectPropertyChangeEvent;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.meshbase.transaction.TransactionException;

import org.infogrid.model.primitives.MeshTypeIdentifier;
import org.infogrid.model.primitives.PropertyType;
import org.infogrid.model.primitives.PropertyValue;

/**
 *
 */
public class NetMeshObjectPropertyChangeEvent
        extends
            MeshObjectPropertyChangeEvent
        implements
            NetChange<MeshObject,MeshObjectIdentifier,PropertyValue,PropertyValue>
{
    /**
     * Constructor.
     */
    public NetMeshObjectPropertyChangeEvent(
            NetMeshObject         meshObject,
            PropertyType          propertyType,
            PropertyValue         oldValue,
            PropertyValue         newValue,
            NetMeshBaseIdentifier incomingProxy,
            long                  updateTime )
    {
        super(  meshObject,
                propertyType,
                oldValue,
                newValue,
                updateTime );

        theIncomingProxy = incomingProxy;
    }

    /**
     * Constructor for the case where we don't have an old value, only the new value.
     * This perhaps should trigger some exception if it is attempted to read old 
     * values later. (FIXME?)
     */
    public NetMeshObjectPropertyChangeEvent(
            NetMeshObjectIdentifier meshObjectIdentifier,
            MeshTypeIdentifier      propertyTypeIdentifier,
            PropertyValue           newValue,
            NetMeshBaseIdentifier   incomingProxy,
            long                    updateTime )
    {
        super(  meshObjectIdentifier,
                propertyTypeIdentifier,
                newValue,
                updateTime );

        theIncomingProxy = incomingProxy;
    }
    
    /**
     * Constructor.
     */
    public NetMeshObjectPropertyChangeEvent(
            NetMeshObjectIdentifier meshObjectIdentifier,
            MeshTypeIdentifier      propertyTypeIdentifier,
            PropertyValue           oldValue,
            PropertyValue           newValue,
            NetMeshBaseIdentifier       incomingProxy,
            long                    updateTime )
    {
        super(  meshObjectIdentifier,
                propertyTypeIdentifier,
                oldValue,
                newValue,
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
     * Obtain the affected PropertyType.
     *
     * @return the RoleTypes
     */
    public MeshTypeIdentifier getPropertyTypeIdentifier()
    {
        return super.getPropertyIdentifier();
    }

    /**
     * Obtain the affected PropertyType.
     *
     * @return the RoleTypes
     */
    public PropertyType getPropertyType()
    {
        return super.getProperty();
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

            NetMeshObject otherObject = (NetMeshObject) getSource();

            PropertyType  affectedProperty = getProperty();
            PropertyValue newValue         = getNewValue();
            long          updateTime       = getTimeEventOccurred();

            otherObject.rippleSetPropertyValues(
                    new PropertyType []  { affectedProperty },
                    new PropertyValue [] { newValue } );

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
        return Utils.awayFromLock( this, outgoingProxy );
    }
    
    /**
     * The incoming Proxy, if any.
     */
    protected NetMeshBaseIdentifier theIncomingProxy;
}
