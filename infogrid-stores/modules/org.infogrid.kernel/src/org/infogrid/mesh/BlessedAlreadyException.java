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

/**
 * This Exception is thrown if an operation requires a MeshObject to be not blessed with a
 * certain type, but it is already. Two specific subclasses are defined as inner classes.
 */
public abstract class BlessedAlreadyException
        extends
            IllegalOperationTypeException
{
    /**
     * Constructor.
     *
     * @param obj the MeshObject that is blessed already
     */
    public BlessedAlreadyException(
            MeshObject obj )
    {
        super( obj );
    }
}
