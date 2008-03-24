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

package org.infogrid.meshbase.net;

import org.infogrid.mesh.MeshObject;
import org.infogrid.model.primitives.BooleanValue;

import java.beans.PropertyChangeEvent;

/**
 * This event indicates the transfer of update rights from one replica in
 * one NetMeshBase to a replica of the same MeshObject in a second NetMeshBase.
 * Inner classes represent the specific event.
 */
public abstract class LockChangedEvent
        extends
            PropertyChangeEvent
{
    /**
     * Constructor.
     *
     * @param affectedObject the MeshObject between whose replicas the lock moved
     * @param oldValue the old value for lock ownership by this replica
     * @param newValue the new value for lock ownership by this replica
     */
    protected LockChangedEvent(
            MeshObject   affectedObject,
            BooleanValue oldValue,
            BooleanValue newValue )
    {
        super( affectedObject, PROPERTY_NAME, oldValue, newValue );
    }

    /**
     * Convenience method to obtain the MeshObject whose lock moved.
     *
     * @return the MeshObject
     */
    public MeshObject getMeshObject()
    {
        return (MeshObject) super.getSource();
    }

    /**
     * The name of the property as seen by the PropertyChangeEvent supertype.
     */
    public static final String PROPERTY_NAME = "_Lock";

    /**
     * This specific subclass indicates that this replica has obtained update rights.
     */
    public static class GainedLock
            extends
                LockChangedEvent
    {
        /**
         * Constructor.
         *
         * @param affectedObject the MeshObject between whose replicas the lock moved
         */
        public GainedLock(
                MeshObject affectedObject )
        {
            super( affectedObject, BooleanValue.FALSE, BooleanValue.TRUE );
        }
    }

    /**
     * This specific subclass indicates that this replica has lost update rights.
     */
    public static class LostLock
            extends
                LockChangedEvent
    {
        /**
         * Constructor.
         *
         * @param affectedObject the MeshObject between whose replicas the lock moved
         */
        public LostLock(
                MeshObject affectedObject )
        {
            super( affectedObject, BooleanValue.TRUE, BooleanValue.FALSE );
        }
    }
}
