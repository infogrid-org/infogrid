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

import org.infogrid.util.AbstractLocalizedRuntimeException;

/**
  * This Exception is thrown when an operation is attempted
  * that is illegal on this MeshObject.
  */
public abstract class IllegalOperationTypeException
        extends
            AbstractLocalizedRuntimeException
{
    /**
     * Constructor.
     *
     * @param obj the MeshObject on which the illegal operation was attempted
     */
    protected IllegalOperationTypeException(
            MeshObject obj )
    {
        theMeshObject = obj;
    }

    /**
     * The MeshObject on which the illegal operation was attempted.
     */
    protected transient MeshObject theMeshObject;
}
