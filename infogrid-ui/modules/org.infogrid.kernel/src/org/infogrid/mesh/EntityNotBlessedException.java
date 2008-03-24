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

import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.MeshTypeUtils;

import org.infogrid.util.StringHelper;

/**
 * Indicates that a MeshObject was not blessed with this EntityType.
 */
public class EntityNotBlessedException
        extends
            NotBlessedException
{
    /**
     * Constructor.
     * 
     * @param obj the MeshObject that is not blessed
     * @param type the ByEntityType with which this MeshObject is not blessed
     */
    public EntityNotBlessedException(
            MeshObject obj,
            EntityType requiredType )
    {
        super( obj );
        theRequiredType = requiredType;
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
                    "theMeshObject",
                    "theRequiredType",
                    "types"
                },
                new Object[] {
                    theMeshObject,
                    theRequiredType.getIdentifier().toExternalForm(),
                    MeshTypeUtils.meshTypeIdentifiers( theMeshObject.getTypes() )
                } );
    }

    /**
     * Obtain the EntityType with which this MeshObject is not blessed.
     *
     * @return the EntityType
     */
    public final EntityType getRequiredEntityType()
    {
        return theRequiredType;
    }

    /**
     * Obtain parameters for the internationalization.
     *
     * @return the parameters
     */
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theMeshObject, theRequiredType };
    }

    /**
     * The EntityType with which this MeshObject is not blessed.
     */
    protected transient EntityType theRequiredType;
}
