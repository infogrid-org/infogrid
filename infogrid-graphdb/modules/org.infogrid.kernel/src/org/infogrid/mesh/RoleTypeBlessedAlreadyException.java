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
 * A MeshObject's relationship to another MeshObject is already blessed with a particular RoleType.
 */
public class RoleTypeBlessedAlreadyException
        extends
            BlessedAlreadyException
{
    /**
     * Constructor.
     *
     * @param obj the MeshObject that is blessed already
     * @param type the RoleType with which this relationship is blessed already
     * @param otherSide the MeshObject that is at the other side of the blessed relationship
     */
    public RoleTypeBlessedAlreadyException(
            MeshObject obj,
            RoleType   type,
            MeshObject otherSide )
    {
        super( obj );

        theRoleType  = type;
        theOtherSide = otherSide;
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
                    ( theOtherSide != null ) ? MeshTypeUtils.meshTypeIdentifiers( theMeshObject.getRoleTypes( theOtherSide ))  : null 
                });
    }

    /**
     * Obtain parameters for the internationalization.
     *
     * @return the parameters
     */
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theMeshObject, theRoleType, theOtherSide };
    }

    /**
     * The other end of the relationship.
     */
    protected transient MeshObject theOtherSide;

    /**
     * The RoleType with which this relationship is blessed already.
     */
    protected transient RoleType theRoleType;
}
