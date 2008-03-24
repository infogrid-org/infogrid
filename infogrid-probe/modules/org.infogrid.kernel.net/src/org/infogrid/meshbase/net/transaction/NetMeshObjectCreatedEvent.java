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
import org.infogrid.mesh.net.externalized.ExternalizedNetMeshObject;
import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.Proxy;
import org.infogrid.meshbase.transaction.CannotApplyChangeException;
import org.infogrid.meshbase.transaction.MeshObjectCreatedEvent;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.meshbase.transaction.TransactionException;
import org.infogrid.model.primitives.PropertyType;
import org.infogrid.modelbase.MeshTypeWithIdentifierNotFoundException;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.util.logging.Log;

/**
 *
 */
public class NetMeshObjectCreatedEvent
        extends
            MeshObjectCreatedEvent
        implements
            NetChange<MeshBase,MeshBase,MeshObject,MeshObjectIdentifier>
{
    private static final Log log = Log.getLogInstance( NetMeshObjectCreatedEvent.class ); // our own, private logger

    /**
     * Construct one.
     *
     * @param meshBase the MeshBase that sent out this event
     * @param createdObject the MeshObject that experienced a lifecycle event
     */
    public NetMeshObjectCreatedEvent(
            NetMeshBase           meshBase,
            NetMeshObject         createdObject,
            NetMeshBaseIdentifier incomingProxy )
    {
        super( meshBase, createdObject, createdObject.getTimeCreated() );
        
        theIncomingProxy = incomingProxy;
    }

    /**
     * Constructor.
     *
     * @param meshBase the MeshBase that sent out this event
     * @param createdObject the MeshObject that experienced a lifecycle event
     */
    public NetMeshObjectCreatedEvent(
            NetMeshBase               meshBase,
            ExternalizedNetMeshObject createdObject,
            NetMeshBaseIdentifier     incomingProxy )
    {
        super( meshBase, createdObject, createdObject.getTimeCreated() );
        
        theIncomingProxy = incomingProxy;
    }

    /**
     * Constructor for subclasses only.
     *
     * @param meshBase the MeshBase that sent out this event
     * @param createdObject the MeshObject that experienced a lifecycle event
     */
    protected NetMeshObjectCreatedEvent(
            NetMeshBase           meshBase,
            NetMeshObject         createdObject,
            NetMeshBaseIdentifier incomingProxy,
            long                  timeUpdated )
    {
        super( meshBase, createdObject, timeUpdated );
        
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
     * Obtain the ExternalizedMeshObject that captures the newly created MeshObject.
     *
     * @return the ExternalizedMeshObject
     */
    @Override
    public ExternalizedNetMeshObject getExternalizedMeshObject()
    {
        return (ExternalizedNetMeshObject) theExternalizedMeshObject;
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

        ModelBase modelBase = otherMeshBase.getModelBase();

        resolveEntityTypes();
        
        PropertyType [] thePropertyTypes = new PropertyType[ theExternalizedMeshObject.getPropertyTypes().length ];
        for( int i=0 ; i<thePropertyTypes.length ; ++i ) {
            try {
                thePropertyTypes[i] = modelBase.findPropertyTypeByIdentifier( theExternalizedMeshObject.getPropertyTypes()[i] );
            } catch( MeshTypeWithIdentifierNotFoundException ex ) {
                log.error( ex );
            }
        }
            
        try {
            tx = otherMeshBase.createTransactionNowIfNeeded();

            NetMeshObject newObject = otherMeshBase.getMeshBaseLifecycleManager().createMeshObject(
                        getAffectedMeshObjectIdentifier(),
                        theEntityTypes,
                        theExternalizedMeshObject.getTimeCreated(),
                        theExternalizedMeshObject.getTimeUpdated(),
                        theExternalizedMeshObject.getTimeRead(),
                        theExternalizedMeshObject.getTimeExpires() );

            newObject.rippleSetPropertyValues( thePropertyTypes, theExternalizedMeshObject.getPropertyValues() );

            return newObject;

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
        return false;
    }
    
    /**
     * The incoming NetMeshBaseIdentifier, if any.
     */
    protected NetMeshBaseIdentifier theIncomingProxy;
}
