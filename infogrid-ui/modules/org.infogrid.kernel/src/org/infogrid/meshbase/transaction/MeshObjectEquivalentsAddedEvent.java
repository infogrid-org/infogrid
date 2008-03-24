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
import org.infogrid.mesh.EquivalentAlreadyException;
import org.infogrid.meshbase.MeshBase;

import org.infogrid.util.ArrayHelper;
import org.infogrid.util.logging.Log;

/**
  * <p>This event indicates that a MeshObject has gained one or more new equivalent MeshObjects.</p>
  */
public class MeshObjectEquivalentsAddedEvent
        extends
            AbstractMeshObjectEquivalentsChangeEvent
{
    private static final Log log = Log.getLogInstance( MeshObjectEquivalentsAddedEvent.class ); // our own, private logger

    /**
     * Construct one.
     * 
     * @param meshObject the MeshObject whose set of equivalents changed
     * @param addedEquivalents the MeshObjects that were added as equivalents
     * @param newValue set of other MeshObjects equivalent to this MeshObject after the event
     * @param updateTime the time when the update occurred
     */
    public MeshObjectEquivalentsAddedEvent(
            MeshObject    meshObject,
            MeshObject [] oldEquivalents,
            MeshObject [] addedEquivalents,
            MeshObject [] newEquivalents,
            long          updateTime )
    {
        this(   meshObject,
                meshObject.getIdentifier(),
                oldEquivalents,
                MeshObjectUtils.meshObjectIdentifiers( oldEquivalents ),
                addedEquivalents,
                MeshObjectUtils.meshObjectIdentifiers( addedEquivalents ),
                newEquivalents,
                MeshObjectUtils.meshObjectIdentifiers( newEquivalents ),
                updateTime );
    }

    /**
     * Main constructor.
     * 
     * @param meshObject the MeshObject whose equivalents changed
     * @param deltaEquivalents the Identifiers of the equivalents that changed
     * @param newValue the Identifiers of the new set of equivalents
     * @param updateTime the time at which the change occurred
     */
    protected MeshObjectEquivalentsAddedEvent(
            MeshObject              meshObject,
            MeshObjectIdentifier    meshObjectIdentifier,
            MeshObject []           oldEquivalents,
            MeshObjectIdentifier [] oldEquivalentIdentifiers,
            MeshObject []           deltaEquivalents,
            MeshObjectIdentifier [] deltaEquivalentIdentifiers,
            MeshObject []           newEquivalents,
            MeshObjectIdentifier [] newEquivalentIdentifiers,
            long                    updateTime )
    {
        super(  meshObject,
                meshObjectIdentifier,
                oldEquivalents,
                oldEquivalentIdentifiers,
                deltaEquivalents,
                deltaEquivalentIdentifiers,
                newEquivalents,
                newEquivalentIdentifiers,
                updateTime );
    }

    /**
     * Determine whether this is an addition or a removal.
     *
     * @return true if this is an addition
     */
    public boolean isAdditionalEquivalentsUpdate()
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

            MeshObject    otherObject = getSource();
            MeshObject [] equivalents = getDeltaValue();

            // because we don't know which of the other MeshObjects are already equivalent, we simply try
            // all. Some will fail, in which case we ignore exceptions.
            for( MeshObject current : equivalents ) {
                try {
                    otherObject.addAsEquivalent( current );

                } catch( EquivalentAlreadyException ex ) {

                    if( log.isDebugEnabled()) {
                        log.debug( ex );
                    }
                }
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
        if( !( other instanceof MeshObjectEquivalentsAddedEvent )) {
            return false;
        }
        MeshObjectEquivalentsAddedEvent realOther = (MeshObjectEquivalentsAddedEvent) other;

        if( !getSourceIdentifier().equals( realOther.getSourceIdentifier() )) {
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
