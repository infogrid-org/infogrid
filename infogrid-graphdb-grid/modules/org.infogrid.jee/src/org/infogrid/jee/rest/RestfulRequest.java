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

package org.infogrid.jee.rest;

import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.meshbase.MeshBaseIdentifier;
import org.infogrid.meshbase.MeshObjectAccessException;

import org.infogrid.util.http.SaneRequest;

import java.net.URISyntaxException;

/**
 * Encapsulates parameter parsing according to InfoGrid REST conventions.
 * The extraction is only being performed when needed.
 */
public interface RestfulRequest
{
    /**
     * Obtain the context path of the application in the manner JEE does it,
     * ie as a relative URL.
     *
     * @return the context path
     * @see #getAbsoluteContextPath
     */
    public String getContextPath();

    /**
     * Obtain the fully-qualified context path of the application.
     * 
     * @return the context path
     * @see #getContextPath()
     */
    public String getAbsoluteContextPath();
    
    /**
     * Obtain the underlying SaneRequest.
     *
     * @return the SaneRequest
     */
    public SaneRequest getSaneRequest();

    /**
     * Determine the identifier of the requested MeshBase.
     * 
     * @return the MeshBaseIdentifier
     */
    public MeshBaseIdentifier determineRequestedMeshBaseIdentifier()
            throws
                MeshObjectAccessException,
                URISyntaxException;

    /**
     * Determine the identifier of the requested MeshObject.
     * 
     * @return the MeshObjectIdentifier
     */
    public MeshObjectIdentifier determineRequestedMeshObjectIdentifier()
            throws
                MeshObjectAccessException,
                URISyntaxException;
    /**
     * Determine the requested MeshObject.
     * 
     * @return the MeshObject, or null if not found
     */
    public MeshObject determineRequestedMeshObject()
            throws
                MeshObjectAccessException,
                URISyntaxException;

    /**
     * Obtain the name of the requested Viewlet, if any.
     *
     * @return class name of the requested Viewlet
     */
    public String getRequestedViewletClass();
}
