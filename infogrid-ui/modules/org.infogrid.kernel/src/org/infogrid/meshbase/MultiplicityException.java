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

package org.infogrid.meshbase;

import org.infogrid.mesh.MeshObject;
import org.infogrid.model.primitives.RoleType;

import org.infogrid.util.StringHelper;

/**
 * This Exception indicates a violation in the multiplicity of a RoleType.
 */
public class MultiplicityException
        extends
            RuntimeException
{
    /**
     * Construct one.
     *
     * @param meshObject the MeshObject where we discovered the multiplicity violation
     * @param role the RoleType for which it happened
     */
    public MultiplicityException(
            MeshObject meshObject,
            RoleType   role )
    {
        theMeshObject = meshObject;
        theRole       = role;
    }

    /**
     * Return this object in string form.
     *
     * @return string form of this object
     */
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "theMeshObject",
                    "theRole"
                },
                new Object[] {
                    theMeshObject,
                    theRole
                });
    }

    /**
     * The MeshObject for which we discovered a violation.
     */
    protected MeshObject theMeshObject;

    /**
     * The RoleType for which we discovered a violation.
     */
    protected RoleType theRole;
}
