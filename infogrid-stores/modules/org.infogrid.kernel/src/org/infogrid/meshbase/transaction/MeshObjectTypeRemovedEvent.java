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

package org.infogrid.meshbase.transaction;

import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;

import org.infogrid.meshbase.MeshBase;

import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.MeshTypeIdentifier;
import org.infogrid.model.primitives.MeshTypeUtils;

import org.infogrid.util.ArrayHelper;
import org.infogrid.util.logging.Log;

/**
 * A EntityType was removed from a MeshObject.
 */
public class MeshObjectTypeRemovedEvent
        extends
            AbstractMeshObjectTypeChangeEvent
{
    private static final Log log = Log.getLogInstance( MeshObjectTypeRemovedEvent.class ); // our own, private logger

    /**
     * Constructor.
     *
     * @param theMeshObject the MeshObject whose type changed
     * @param addedTypes the EntityType(s) that were added to the MeshObject
     * @param updateTime the time at which the update was performed
     */
    public MeshObjectTypeRemovedEvent(
            MeshObject        theMeshObject,
            EntityType []     oldTypes,
            EntityType []     removedTypes,
            EntityType []     newTypes,
            long              updateTime )
    {
        super(  theMeshObject,
                theMeshObject.getIdentifier(),
                oldTypes,
                MeshTypeUtils.meshTypeIdentifiers( oldTypes ),
                removedTypes,
                MeshTypeUtils.meshTypeIdentifiers( removedTypes ),
                newTypes,
                MeshTypeUtils.meshTypeIdentifiers( newTypes ),
                updateTime );
    }

    /**
     * Constructor for the case where we don't have old and new values, only the delta.
     * This perhaps should trigger some exception if it is attempted to read old or
     * new values later. (FIXME?)
     */
    public MeshObjectTypeRemovedEvent(
            MeshObjectIdentifier  meshObjectIdentifier,
            MeshTypeIdentifier [] removedTypeIdentifiers,
            long                  updateTime )
    {
        super(  null,
                meshObjectIdentifier,
                null,
                null,
                null,
                removedTypeIdentifiers,
                null,
                null,
                updateTime );        
    }
    
    /**
     * Constructor.
     *
     * @param theMeshObject the MeshObject whose type changed
     * @param addedTypes the EntityType(s) that were added to the MeshObject
     * @param updateTime the time at which the update was performed
     */
    public MeshObjectTypeRemovedEvent(
            MeshObjectIdentifier  theMeshObjectIdentifier,
            MeshTypeIdentifier [] oldTypeIdentifiers,
            MeshTypeIdentifier [] removedTypeIdentifiers,
            MeshTypeIdentifier [] newTypeIdentifiers,
            long                  updateTime )
    {
        super(  null,
                theMeshObjectIdentifier,
                null,
                oldTypeIdentifiers,
                null,
                removedTypeIdentifiers,
                null,
                newTypeIdentifiers,
                updateTime );
    }

    /**
     * Apply this Change to a MeshObject in this MeshBase. This method
     * is intended to make it easy to reproduce Changes that were made in
     * one MeshBase to MeshObjects in another MeshBase.
     *
     * This method will attempt to create a Transaction if none is present on the
     * current Thread.
     *
     * @param otherMeshBase the other MeshBase in which to apply the change
     * @throws CannotApplyChangeException thrown if the Change could not be applied, e.g because
     *         the affected MeshObject did not exist in the other MeshBase
     * @throws TransactionException thrown if a Transaction didn't exist on this Thread and could not be created
     */
    public MeshObject applyTo(
            MeshBase otherMeshBase )
        throws
            CannotApplyChangeException,
            TransactionException
    {
        setResolver( otherMeshBase );

        Transaction tx = null;
        try {
            tx = otherMeshBase.createTransactionNowIfNeeded();

            MeshObject otherObject = getSource();

            EntityType [] types = getDeltaValue();
            otherObject.unbless( types );

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
     * Determine equality.
     *
     * @param other the Object to compare with
     */
    @Override
    public boolean equals(
            Object other )
    {
        if( !( other instanceof MeshObjectTypeRemovedEvent )) {
            return false;
        }
        MeshObjectTypeRemovedEvent realOther = (MeshObjectTypeRemovedEvent) other;
        
        if( !super.getSourceIdentifier().equals( realOther.getSourceIdentifier() )) {
            return false;
        }        
        if( !ArrayHelper.hasSameContentOutOfOrder( getDeltaValueIdentifier(), realOther.getDeltaValueIdentifier(), true )) {
            return false;
        }
        if( getTimeEventOccurred() != realOther.getTimeEventOccurred() ) {
            return false;
        }
        return true;
    }
}
