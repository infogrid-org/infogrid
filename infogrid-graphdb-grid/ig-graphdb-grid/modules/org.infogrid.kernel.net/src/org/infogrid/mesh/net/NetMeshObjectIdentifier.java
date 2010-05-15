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
// Copyright 1998-2010 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.mesh.net;

import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;

/**
 * Extends MeshObjectIdentifier for NetMeshObjects. A NetMeshObjectIdentifier also carries
 * the identifier of the NetMeshBase that acts as the scope for a local ID. For example,
 * identifier 'abcd' is scoped by NetMeshBaseIdentifier 'http://example.com/'.
 */
public interface NetMeshObjectIdentifier
        extends
            MeshObjectIdentifier
{
    /**
     * Obtain the identifier of the NetMeshBase that acts as the scope for this NetMeshObjectIdentifier
     *
     * @return the identifier of the NetMeshBase
     */
    public abstract NetMeshBaseIdentifier getNetMeshBaseIdentifier();

    /**
     * Obtain the local components of the NetMeshObjectIdentifier.
     *
     * @return the local component
     */
    public abstract String getLocalId();

    /**
     * To save memory, this constant is allocated here and used wherever appropriate.
     */
    public static final NetMeshObjectIdentifier [] NET_EMPTY_ARRAY = {};
}
