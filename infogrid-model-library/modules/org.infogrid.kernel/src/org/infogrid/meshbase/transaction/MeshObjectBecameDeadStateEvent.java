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

/**
 * This indicates that a MeshObject became dead, such as by being deleted.
 */
public class MeshObjectBecameDeadStateEvent
        extends
            MeshObjectStateEvent
{
    /**
     * Constructor. This must be invoked with both the MeshObject and the canonical Identifier,
     * because it is not possible to construct the canonical Identifier after the MeshObject is dead.
     * 
     * @param canonicalIdentifier the canonical Identifier of the MeshObject that became dead
     * @param theMeshObject the MeshObject whose state changed
     */
    public MeshObjectBecameDeadStateEvent(
            MeshObject           theMeshObject,
            MeshObjectIdentifier canonicalIdentifier,
            long                 updateTime )
    {
        super(  theMeshObject,
                canonicalIdentifier,
                Value.ALIVE,
                Value.DEAD,
                updateTime );
    }

    /**
     * Resolve a value of the event.
     *
     * @return a value of the event
     */
    protected MeshObjectState resolveValue(
            String vid )
    {
        if( Value.ALIVE.toString().equals( vid )) {
            return Value.ALIVE;
        } else if( Value.DEAD.toString().equals( vid )) {
            return Value.DEAD;
        } else {
            throw new IllegalArgumentException( "Do not know value " + vid );
        }
    }

    /**
     * The values for the MeshObjectState.
     */
    public static enum Value
            implements
                MeshObjectState
    {
        ALIVE,
        DEAD;
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
        if( !( other instanceof MeshObjectBecameDeadStateEvent )) {
            return false;
        }
        MeshObjectBecameDeadStateEvent realOther = (MeshObjectBecameDeadStateEvent) other;
        
        if( !getSourceIdentifier().equals( realOther.getSourceIdentifier() )) {
            return false;
        }
        if( !getDeltaValueIdentifier().equals( realOther.getDeltaValueIdentifier() )) {
            return false;
        }
        if( getTimeEventOccurred() != realOther.getTimeEventOccurred() ) {
            return false;
        }
        return true;
    }
}
