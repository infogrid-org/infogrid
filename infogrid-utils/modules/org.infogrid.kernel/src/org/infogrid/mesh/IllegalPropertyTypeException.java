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
import org.infogrid.model.primitives.PropertyType;

import org.infogrid.util.StringHelper;

/**
  * This Exception is thrown when there is an attempt to access
  * a property that cannot exist on this MeshObject because the MeshObject
  * has not been blessed with a MeshType that provides this PropertyType.
  */
public class IllegalPropertyTypeException
        extends
            IllegalOperationTypeException
{
    /**
     * Constructor.
     *
     * @param obj the MeshObject that did not have the PropertyType
     * @param pt the PropertyType that was illegal on this MeshObject
     */
    public IllegalPropertyTypeException(
            MeshObject   obj,
            PropertyType pt )
    {
        super( obj );
        
        thePropertyType = pt;
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
                    "propertyType",
                    "types"
                },
                new Object[] {
                    theMeshObject,
                    thePropertyType.getIdentifier(),
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
        return new Object[] { theMeshObject, thePropertyType };
    }

    /**
     * The PropertyType that was illegal on theMeshObject.
     */
    protected transient PropertyType thePropertyType;
}
