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

import org.infogrid.model.primitives.RoleType;

/**
 * Thrown if a MeshObject cannot be unblessed because a RoleType requires this
 * MeshObject to continue being of that MeshType.
 */
public class RoleTypeRequiresEntityTypeException
        extends
            IllegalOperationTypeException
{
    /**
     * Constructor.
     *
     * @param obj the MeshObject that requires the EntityType
     * @param roleType the RoleType that requires the EntityType
     */
    public RoleTypeRequiresEntityTypeException(
            MeshObject obj,
            RoleType   roleType )
    {
        super( obj );

        theRoleType      = roleType;
    }

    /**
     * Obtain the RoleType that blocked the unblessing of the MeshObject.
     *
     * @return the RoleType
     */
    public final RoleType getRoleType()
    {
        return theRoleType;
    }

    /**
     * Obtain parameters for the internationalization.
     *
     * @return the parameters
     */
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theMeshObject, theRoleType, theRoleType.getEntityType() };
    }

    /**
     * The RoleType that blocked the unblessing.
     */
    protected transient RoleType theRoleType;
}
