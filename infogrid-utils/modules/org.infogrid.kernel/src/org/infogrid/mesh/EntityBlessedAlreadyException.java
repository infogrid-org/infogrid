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
 * A MeshObject is already blessed with a particular EntityType.
 */
public class EntityBlessedAlreadyException
        extends
            BlessedAlreadyException
{
    /**
     * Constructor.
     *
     * @param obj the MeshObject that is blessed already
     * @param type the EntityType with which this MeshObject is blessed already
     */
    public EntityBlessedAlreadyException(
            MeshObject obj,
            EntityType type )
    {
        super( obj );
        theMeshType = type;
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
                new String[]{
                    "meshObject",
                    "meshType",
                    "types"
                },
                new Object[] {
                    theMeshObject,
                    theMeshType.getIdentifier().toExternalForm(),
                    MeshTypeUtils.meshTypeIdentifiers( theMeshObject.getTypes() )
                });
    }

    /**
     * Obtain parameters for the internationalization.
     *
     * @return the parameters
     */
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theMeshObject, theMeshType };
    }

    /**
     * The EntityType with which this MeshObject is blessed already.
     */
    protected transient EntityType theMeshType;
}
