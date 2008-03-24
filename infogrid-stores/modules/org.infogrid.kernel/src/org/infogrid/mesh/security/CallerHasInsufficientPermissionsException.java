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

package org.infogrid.mesh.security;

import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.NotPermittedException;

/**
 * This Exception indicates that a caller had insufficient permissions to perform
 * the requested operation. Other callers may have different permissions.
 */
public class CallerHasInsufficientPermissionsException
        extends
            NotPermittedException
{
    /**
     * Constructor.
     *
     * @param obj the MeshObject whose attempted modification triggered this Exception
     * @param caller identifies the caller that did not have sufficient permissions and triggered this Exception.
     *               This caller may be null, indicating an anonymous caller.
     */
    public CallerHasInsufficientPermissionsException(
            MeshObject obj,
            MeshObject caller )
    {
        super( obj );
        
        theCaller = caller;
    }
    
    /**
     * Obtain the caller who did not have sufficient permissions.
     *
     * @return the caller
     */
    public final MeshObject getCaller()
    {
        return theCaller;
    }

    /**
     * Obtain parameters for the internationalization.
     *
     * @return the parameters
     */
    @Override
    public Object [] getLocalizationParameters()
    {
        return new Object [] { theMeshObject, theCaller };
    }

    /**
     * The caller that did not have sufficient permissions and triggered this Exception.
     */
    protected transient MeshObject theCaller;
}
