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

package org.infogrid.meshbase.net.security;

import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.net.security.AccessLocallyNotPermittedException;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshObjectAccessSpecification;
import org.infogrid.meshbase.security.AccessManager;
import org.infogrid.meshbase.security.DelegatingAccessManager;

/**
 * Makes it easy to create a NetAccessManager by delegating to another AccessManager and
 * potentially overriding some methods.
 * By default, accessLocally is prohibited.
 */
public class DelegatingNetAccessManager
        extends
            DelegatingAccessManager
        implements
            NetAccessManager
{
    /**
     * Factory method.
     *
     * @param singleDelegate a single AccessManager to delegate to
     * @return the created DelegatingNetAccessManager
     */
    public static DelegatingNetAccessManager create(
            AccessManager singleDelegate )
    {
        return new DelegatingNetAccessManager( new AccessManager[] { singleDelegate } );
    }

    /**
     * Factory method.
     *
     * @param delegates the AccessManagers to delegate to
     * @return the created DelegatingNetAccessManager
     */
    public static DelegatingAccessManager create(
            AccessManager [] delegates )
    {
        return new DelegatingNetAccessManager( delegates );
    }

    /**
     * Constructor.
     *
     * @param delegates the AccessManagers to delegate to
     */
    protected DelegatingNetAccessManager(
            AccessManager [] delegates )
    {
        super( delegates );
    }

    /**
     * Check whether it is permitted to perform an accessLocally operation with the
     * provided NetMeshObjectAccessSpecification.
     *
     * @param mb the MeshBase in which the Exception occurred
     * @param failedPaths the access path that was used
     * @throws NotPermittedException thrown if it is not permitted
     */
    public void checkPermittedAccessLocally(
            NetMeshBase                         mb,
            NetMeshObjectAccessSpecification [] failedPaths )
        throws
            NotPermittedException
    {
        throw new AccessLocallyNotPermittedException( mb, mb.getIdentifier(), failedPaths );
    }
}
