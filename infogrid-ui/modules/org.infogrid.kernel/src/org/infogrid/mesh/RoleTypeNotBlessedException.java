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

import org.infogrid.model.primitives.MeshTypeUtils;
import org.infogrid.model.primitives.RoleType;

import org.infogrid.util.StringHelper;

/**
 * This Exception indicates that the relationship of two MeshObjects was not
 * blessed with this ByRoleType.
 */
public class RoleTypeNotBlessedException
        extends
            NotBlessedException
{
    /**
     * Constructor.
     * 
     * @param obj the MeshObject that is not blessed
     * @param type the ByRoleType
     * @param other the MeshObject on the other end of the relationship that was not blessed with this ByRoleType
     */
    public RoleTypeNotBlessedException(
            MeshObject obj,
            RoleType   type,
            MeshObject other )
    {
        super( obj );

        theRoleType  = type;
        theOtherSide = other;
    }

    /**
      * Obtain String representation, for debugging.
      *
      * @return String representation
      */
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "meshObject",
                    "otherSide",
                    "roleType",
                    "types"
                },
                new Object[] {
                    theMeshObject,
                    theOtherSide,
                    theRoleType.getIdentifier().toExternalForm(),
                    MeshTypeUtils.meshTypeIdentifiers( theMeshObject.getRoleTypes( theOtherSide )  )
                });
    }

    /**
     * Obtain parameters for the internationalization.
     *
     * @return the parameters
     */
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theMeshObject, theOtherSide, theRoleType };
    }

    /**
     * The other end of the relationship.
     */
    protected transient MeshObject theOtherSide;

    /**
     * The MeshType with which this MeshObject is not blessed.
     */
    protected transient RoleType theRoleType;
}
