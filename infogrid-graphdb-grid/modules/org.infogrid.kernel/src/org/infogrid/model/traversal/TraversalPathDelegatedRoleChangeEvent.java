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

package org.infogrid.model.traversal;

import org.infogrid.mesh.MeshObject;

import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.transaction.AbstractMeshObjectRoleChangeEvent;
import org.infogrid.meshbase.transaction.CannotApplyChangeException;
import org.infogrid.meshbase.transaction.MeshObjectRoleAddedEvent;
import org.infogrid.meshbase.transaction.MeshObjectRoleRemovedEvent;
import org.infogrid.meshbase.transaction.TransactionException;

/**
 * This is a RoleChangeEvent, forwarded by a TraversalPath and
 * annotated with the TraversalPath.
 */
public abstract class TraversalPathDelegatedRoleChangeEvent
        extends
            AbstractMeshObjectRoleChangeEvent
        implements
            TraversalPathDelegatedEvent
{
    /**
     * Construct with the original event that we are forwarding.
     *
     * @param path the TraversalPath that forwarded this event
     * @param org the original MeshObjectRolePlayerUpdateEvent
     */
    protected TraversalPathDelegatedRoleChangeEvent(
            TraversalPath             path,
            AbstractMeshObjectRoleChangeEvent org,
            MeshBase                  base )
    {
        super(  org.getSource(),
                org.getSource().getIdentifier(),
                org.getOldValue(),
                org.getOldValueIdentifier(),
                org.getDeltaValue(),
                org.getDeltaValueIdentifier(),
                org.getNewValue(),
                org.getNewValueIdentifier(),
                org.getNeighborMeshObject(),
                org.getNeighborMeshObjectIdentifier(),
                org.getTimeEventOccurred() );
        
        thePath          = path;
        theOriginalEvent = org;
    }

    /**
     * Obtain the TraversalPath that forwarded this event.
     *
     * @return the TraversalPath that forwarded this event
     */
    public TraversalPath getTraversalPath()
    {
        return thePath;
    }

    /**
     * Obtain the underlying original AbstractMeshObjectRoleChangeEvent.
     * 
     * @return the underlying original MAbstractMeshObjectRoleChangeEvent
     */
    public AbstractMeshObjectRoleChangeEvent getOriginalEvent()
    {
        return theOriginalEvent;
    }

    /**
     * The TraversalPath that forwarded this event.
     */
    protected TraversalPath thePath;

    /**
     * The underlying original AbstractMeshObjectRoleChangeEvent.
     */
    protected AbstractMeshObjectRoleChangeEvent theOriginalEvent;

    /**
     * This indicates a TraversalPathDelegatedRoleChangeEvent in which the MeshObject plays one
     * more role.
     */
    public static class Added
            extends
                TraversalPathDelegatedRoleChangeEvent
    {
        /**
         * Constructor.
         * 
         * @param path the TraversalPath that forwarded this event
         * @param org the original MeshObjectRolePlayerUpdateEvent
         */
        Added(  TraversalPath            path,
                MeshObjectRoleAddedEvent org,
                MeshBase                 base )
        {
            super( path, org, base );
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
            MeshObject ret = theOriginalEvent.applyTo( otherMeshBase );
            return ret;
        }
    }
    
    /**
     * This indicates a TraversalPathDelegatedRoleChangeEvent in which the MeshObject plays one
     * role less.
     */
    public static class Removed
            extends
                TraversalPathDelegatedRoleChangeEvent
    {
        /**
         * Constructor.
         * 
         * @param path the TraversalPath that forwarded this event
         * @param org the original MeshObjectRolePlayerUpdateEvent
         */
        Removed(
                TraversalPath              path,
                MeshObjectRoleRemovedEvent org,
                MeshBase                   base )
        {
            super( path, org, base );
        }

        /**
         * Determine whether this is an addition or a removal.
         *
         * @return always returns false
         */
        public boolean isAdditionalRoleUpdate()
        {
            return false;
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
            MeshObject ret = theOriginalEvent.applyTo( otherMeshBase );
            return ret;
        }
    }
}
