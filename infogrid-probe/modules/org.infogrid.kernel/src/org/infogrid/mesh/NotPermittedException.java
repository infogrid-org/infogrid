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

package org.infogrid.mesh;

import org.infogrid.util.AbstractLocalizedException;

/**
 * <p>This exception is thrown if an operation is attempted on a MeshObject
 * that the caller was not permitted to perform. More specific subclasses
 * are defined in the <code>org.infogrid.mesh.security</code> package.</p>
 */
public abstract class NotPermittedException
        extends
            AbstractLocalizedException
{
    /**
     * Constructor.
     *
     * @param obj the MeshObject whose attempted modification triggered this Exception
     */
    protected NotPermittedException(
            MeshObject obj )
    {
        theMeshObject = obj;
    }
    
    /**
     * Constructor.
     *
     * @param obj the MeshObject whose attempted modification triggered this Exception
     */
    protected NotPermittedException(
            MeshObject obj,
            Throwable  cause )
    {
        super( cause );
        
        theMeshObject = obj;
    }
    
    /**
     * Obtain the MeshObject whose attemped modification triggered this Exception.
     *
     * @return the MeshObject
     */
    public final MeshObject getMeshObject()
    {
        return theMeshObject;
    }

    /**
     * Obtain resource parameters for the internationalization.
     *
     * @return the resource parameters
     */
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theMeshObject };
    }

    /**
     * The MeshObject whose attempted modification triggered this Exception.
     */
    protected transient MeshObject theMeshObject;
}
