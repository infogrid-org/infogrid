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
import org.infogrid.mesh.MeshObjectUtils;

import org.infogrid.meshbase.MeshBase;
import org.infogrid.model.primitives.RoleType;

import org.infogrid.util.ArrayHelper;

/**
 * This event indicates that a neighbor was removed.
 */
public class MeshObjectNeighborRemovedEvent
        extends
            AbstractMeshObjectNeighborChangeEvent
{
    /**
     * Constructor.
     * 
     * @param meshObject the MeshObject whose deltaNeighbor changed
     * @param oldNeighbors the old set of neighbors
     * @param removed the deltaNeighbor that was removed
     * @param newNeighbors the new set of neighbors
     * @param updateTime the time the MeshObject was updated
     */
    public MeshObjectNeighborRemovedEvent(
            MeshObject       meshObject,
            MeshObject []    oldNeighbors,
            MeshObject       deltaNeighbor,
            MeshObject []    newNeighbors,
            long             updateTime )
    {
        this(   meshObject,
                meshObject.getIdentifier(),
                oldNeighbors,
                MeshObjectUtils.meshObjectIdentifiers( oldNeighbors ),
                new MeshObject[] { deltaNeighbor },
                new MeshObjectIdentifier[] { deltaNeighbor.getIdentifier() },
                newNeighbors,
                MeshObjectUtils.meshObjectIdentifiers( newNeighbors ),
                updateTime );
    }

    /**
     * Convenience constructor.
     */
    public MeshObjectNeighborRemovedEvent(
            MeshObject              meshObject,
            MeshObjectIdentifier [] oldNeighborIdentifiers,
            MeshObjectIdentifier    deltaNeighborIdentifier,
            MeshObjectIdentifier [] newNeighborIdentifiers,
            long                    updateTime )
    {
        this(   meshObject,
                meshObject.getIdentifier(),
                null,
                oldNeighborIdentifiers,
                null,
                new MeshObjectIdentifier[] { deltaNeighborIdentifier },
                null,
                newNeighborIdentifiers,
                updateTime );
    }

    /**
     * Constructor.
     * 
     * @param meshObject the MeshObject whose deltaNeighbor changed
     * @param oldNeighbors the old set of neighbors
     * @param removed the deltaNeighbor that was removed
     * @param newNeighbors the new set of neighbors
     * @param updateTime the time the MeshObject was updated
     */
    public MeshObjectNeighborRemovedEvent(
            MeshObjectIdentifier    meshObjectIdentifier,
            MeshObjectIdentifier [] oldNeighbors,
            MeshObjectIdentifier    deltaNeighbor,
            MeshObjectIdentifier [] newNeighbors,
            long                    updateTime )
    {
        this(   null,
                meshObjectIdentifier,
                null,
                oldNeighbors,
                null,
                new MeshObjectIdentifier[] { deltaNeighbor },
                null,
                newNeighbors,
                updateTime );
    }

    /**
     * Main constructor.
     *
     * @param meshObject the MeshObject that is the source of the event (optional)
     * @param meshObjectIdentifier Identifier of the MeshObject that is the source of the event (required)
     * @param oldNeighbors the set of neighbor MeshObjects prior to the event (optional)
     * @param oldNeighborIdentifiers the Identifiers of the neighbor MeshObjects prior to the event (required)
     * @param deltaNeighbors the set of neighbor MeshObjects affected by this event (optional)
     * @param deltaNeighborIdentifiers the Identifiers of the neighbor MeshObjects affected by this event (required)
     * @param newNeighbors the set of neighbor MeshObjects after the event (optional)
     * @param newNeighborIdentifiers the Identifiers of the neighbor MeshObjects after the event (required)
     * @param updateTime the time at which the change was made, in System.currentTimeMillis() format
     */
    protected MeshObjectNeighborRemovedEvent(
            MeshObject              meshObject,
            MeshObjectIdentifier    meshObjectIdentifier,
            MeshObject []           oldNeighbors,
            MeshObjectIdentifier [] oldNeighborIdentifiers,
            MeshObject []           deltaNeighbors,
            MeshObjectIdentifier [] deltaNeighborIdentifiers,
            MeshObject []           newNeighbors,
            MeshObjectIdentifier [] newNeighborIdentifiers,
            long                    updateTime )
    {
        super(  meshObject,
                meshObjectIdentifier,
                null,
                null,
                oldNeighbors,
                oldNeighborIdentifiers,
                deltaNeighbors,
                deltaNeighborIdentifiers,
                newNeighbors,
                newNeighborIdentifiers,
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

        MeshObject    otherObject; // declaring this out here makes debugging much easier
        MeshObject [] relatedOtherObjects;
        RoleType []   roleTypes;

        try {
            tx = otherMeshBase.createTransactionNowIfNeeded();

            otherObject         = getSource();
            relatedOtherObjects = getDeltaValue();
            roleTypes           = getProperty(); // FIXME not really needed

            for( int i=0 ; i<relatedOtherObjects.length ; ++i ) {
                otherObject.unrelate( relatedOtherObjects[i] );
            }

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
        if( !( other instanceof MeshObjectNeighborRemovedEvent )) {
            return false;
        }
        MeshObjectNeighborRemovedEvent realOther = (MeshObjectNeighborRemovedEvent) other;
        
        if( !getSourceIdentifier().equals( realOther.getSourceIdentifier() )) {
            return false;
        }
        if( !ArrayHelper.hasSameContentOutOfOrder( getPropertyIdentifier(), realOther.getPropertyIdentifier(), true )) {
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
