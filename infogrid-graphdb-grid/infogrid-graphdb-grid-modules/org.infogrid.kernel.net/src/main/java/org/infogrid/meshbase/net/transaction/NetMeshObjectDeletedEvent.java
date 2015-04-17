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
// Copyright 1998-2011 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.meshbase.net.transaction;

import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.mesh.net.externalized.ExternalizedNetMeshObject;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;

/**
 * This event indicates that a NetMeshObject was semantically deleted. 
 */
public class NetMeshObjectDeletedEvent
        extends
            AbstractNetMeshObjectDeletedEvent
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor. This must be invoked with both the NetMeshObject and the canonical NetMeshObjectIdentifier,
     * because it is not possible to construct the canonical NetMeshObjectIdentifier after the NetMeshObject is dead.
     *
     * @param source the NetMeshBase that is the source of the event
     * @param sourceIdentifier the NetMeshBaseIdentifier representing the source of the event
     * @param deletedObject the NetMeshObject that was deleted
     * @param deletedObjectIdentifier the identifier of the NetMeshObject that was deleted
     * @param originIdentifier identifier of the NetMeshBase from where this NetChange arrived, if any
     * @param externalized the deleted MeshObject in externalized form as it was just prior to deletion
     * @param timeEventOccurred the time at which the event occurred, in <code>System.currentTimeMillis</code> format
     */
    public NetMeshObjectDeletedEvent(
            NetMeshBase               source,
            NetMeshBaseIdentifier     sourceIdentifier,
            NetMeshObject             deletedObject,
            NetMeshObjectIdentifier   deletedObjectIdentifier,
            NetMeshBaseIdentifier     originIdentifier,
            ExternalizedNetMeshObject externalized,
            long                      timeEventOccurred )
    {
        super( source, sourceIdentifier, deletedObject, deletedObjectIdentifier, originIdentifier, externalized, timeEventOccurred );
    }

    /**
     * <p>Create a Change that undoes this Change.</p>
     *
     * @return the inverse Change, or null if no inverse Change could be constructed.
     */
    @Override
    public NetMeshObjectCreatedEvent inverse()
    {
        return new NetMeshObjectCreatedEvent(
                (NetMeshBase) getSource(),
                (NetMeshBaseIdentifier) getSourceIdentifier(),
                (ExternalizedNetMeshObject) theExternalizedMeshObject,
                getOriginNetworkIdentifier() );
    }

}
