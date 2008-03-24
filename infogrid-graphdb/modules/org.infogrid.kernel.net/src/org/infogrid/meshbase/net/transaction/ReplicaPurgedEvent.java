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

import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.Proxy;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;

import org.infogrid.mesh.net.NetMeshObjectIdentifier;

/**
 *
 */
public class ReplicaPurgedEvent
        extends
            NetMeshObjectDeletedEvent
        implements
            ReplicaEvent
{
    /**
     * Constructor.
     */
    public ReplicaPurgedEvent(
            NetMeshBase             mb,
            NetMeshObjectIdentifier canonicalIdentifier,
            NetMeshObject           replica,
            NetMeshBaseIdentifier       incomingProxy,
            long                    updateTime )
    {
        super( mb, canonicalIdentifier, replica, incomingProxy, updateTime );
    }

    /**
     * Determine whether this NetChange should be forwarded through the outgoing Proxy.
     * If specified, the incomingProxy parameter specifies where the NetChange came from.
     *
     * @param incomingProxy the incoming Proxy
     * @param outgoingProxy the outgoing Proxy
     * @return true if the NetChange should be forwarded.
     */
    @Override
    public boolean shouldBeSent(
            Proxy outgoingProxy )
    {
        return true;
    }
}
