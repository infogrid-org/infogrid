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

import org.infogrid.model.primitives.MeshTypeIdentifier;
import org.infogrid.model.primitives.MeshTypeUtils;
import org.infogrid.model.primitives.RoleType;

import org.infogrid.util.ArrayHelper;

/**
 * This indicates a AbstractMeshObjectRoleChangeEvent in which the MeshObject plays one
 * or more additional RoleTypes.
 */
public class MeshObjectRoleAddedEvent
        extends
            AbstractMeshObjectRoleChangeEvent
{
    /**
     * Constructor.
     * 
     * @param theMeshObject the MeshObject whose role participation changed
     * @param thisEnd the RoleTypes whose participation changed
     * @param otherSide the other side of the relationship whose role participation changed
     * @param updateTime the time when the update occurred
     */
    public MeshObjectRoleAddedEvent(
            MeshObject        meshObject,
            RoleType []       oldRoleTypes,
            RoleType []       addedRoleTypes,
            RoleType []       newRoleTypes,
            MeshObject        otherSide,
            long              updateTime )
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
                updateTime );
    }

    /**
     * Constructor.
     * 
     * @param theMeshObject the MeshObject whose role participation changed
     * @param thisEnd the RoleTypes whose participation changed
     * @param otherSide the other side of the relationship whose role participation changed
     * @param updateTime the time when the update occurred
     */
    public MeshObjectRoleAddedEvent(
            MeshObjectIdentifier  meshObjectIdentifier,
            MeshTypeIdentifier [] oldRoleTypeIdentifier,
            MeshTypeIdentifier [] addedRoleTypeIdentifiers,
            MeshTypeIdentifier [] newRoleTypesIdentifiers,
            MeshObjectIdentifier  otherSideIdentifier,
            long                  updateTime )
    {
        this(   null,
                meshObjectIdentifier,
                null,
                oldRoleTypeIdentifier,
                null,
                addedRoleTypeIdentifiers,
                null,
                newRoleTypesIdentifiers,
                null,
                otherSideIdentifier,
                updateTime );
    }

    /**
     * Pass-through constructor for subclasses.
     */
    protected MeshObjectRoleAddedEvent(
            MeshObject            meshObject,
            MeshObjectIdentifier  meshObjectIdentifier,
            RoleType []           oldRoleTypes,
            MeshTypeIdentifier [] oldRoleTypeIdentifiers,
            RoleType []           addedRoleTypes,
            MeshTypeIdentifier [] addedRoleTypeIdentifiers,
            RoleType []           newRoleTypes,
            MeshTypeIdentifier [] newRoleTypesIdentifiers,
            MeshObject            otherSide,
            MeshObjectIdentifier  otherSideIdentifier,
            long                  updateTime )
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
    }
    
    /**
     * Determine whether this is an addition or a removal.
     *
     * @return always returns true
     */
    public boolean isAdditionalRoleUpdate()
    {
        return true;
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

            MeshObject  otherObject        = getSource();
            MeshObject  relatedOtherObject = getNeighborMeshObject();
            RoleType [] roleTypes          = getDeltaValue();

            otherObject.blessRelationship( roleTypes, relatedOtherObject );

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
        if( !( other instanceof MeshObjectRoleAddedEvent )) {
            return false;
        }
        MeshObjectRoleAddedEvent realOther = (MeshObjectRoleAddedEvent) other;
        
        if( !getSourceIdentifier().equals( realOther.getSourceIdentifier() )) {
            return false;
        }
        if( !theNeighborIdentifier.equals( realOther.theNeighborIdentifier )) {
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
