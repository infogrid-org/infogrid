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

package org.infogrid.mesh.security;

import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.model.primitives.PropertyType;

/**
 * This Exception indicates that a property is read-only and could not be modified.
 * A read-only property is read-only for everybody, not just specifically for this
 * caller. For example, a calculated property may be read-only.
 */
public class PropertyReadOnlyException
        extends
            NotPermittedException
{
    /**
     * Constructor.
     *
     * @param obj the MeshObject whose attempted modification triggered this Exception
     * @param type identifies the property on this MeshObject whose attempted modification triggered this Exception
     */
    public PropertyReadOnlyException(
            MeshObject   obj,
            PropertyType type)
    {
        super( obj );
        
        thePropertyType = type;
    }
    
    /**
     * Obtain the PropertyType whose attempted modification triggered this Exception.
     *
     * @return the PropertyType
     */
    public final PropertyType getPropertyType()
    {
        return thePropertyType;
    }

    /**
     * Obtain parameters for the internationalization.
     *
     * @return the parameters
     */
    @Override
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theMeshObject, thePropertyType };
    }

    /**
     * The PropertyType whose attempted modification triggered this Exception.
     */
    protected transient final PropertyType thePropertyType;
}
