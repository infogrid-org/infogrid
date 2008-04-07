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
import org.infogrid.meshbase.MeshBaseIdentifier;

/**
 * A MeshObject was semantically deleted.
 */
public class MeshObjectDeletedEvent
        extends
            AbstractMeshObjectLifecycleEvent
{
    /**
     * Construct one.
     * 
     * @param meshBase the MeshBase that sent out this event
     * @param canonicalIdentifier the canonical Identifier of the MeshObject that experienced a lifecycle event
     * @param deletedMeshObject the MeshObject that experienced a lifecycle event
     */
    public MeshObjectDeletedEvent(
            MeshBase             meshBase,
            MeshBaseIdentifier   meshBaseIdentifier,
            MeshObject           deletedMeshObject,
            MeshObjectIdentifier deletedMeshObjectIdentifier,
            long                 updateTime )
    {
        super(  meshBase,
                meshBaseIdentifier,
                deletedMeshObject,
                deletedMeshObjectIdentifier,
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

            MeshObject otherObject = getDeltaValue();

            otherMeshBase.getMeshBaseLifecycleManager().deleteMeshObject( otherObject );

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
        if( !( other instanceof MeshObjectDeletedEvent )) {
            return false;
        }
        MeshObjectDeletedEvent realOther = (MeshObjectDeletedEvent) other;
        
        if( !getDeltaValueIdentifier().equals( realOther.getDeltaValueIdentifier() )) {
            return false;
        }
        if( getTimeEventOccurred() != realOther.getTimeEventOccurred() ) {
            return false;
        }
        return true;
    }

    /**
     * Determine hash code.
     * 
     * @return hash code
     */
    @Override
    public int hashCode()
    {
        return getAffectedMeshObjectIdentifier().hashCode();
    }
}
