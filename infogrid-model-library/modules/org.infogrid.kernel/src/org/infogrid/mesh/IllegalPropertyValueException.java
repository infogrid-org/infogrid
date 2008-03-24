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

import org.infogrid.model.primitives.PropertyType;
import org.infogrid.model.primitives.PropertyValue;

import org.infogrid.util.StringHelper;

/**
  * This Exception is thrown when a PropertyValue with an incorrect DataType is
  * specified as the new value of a property, or when the new value of the
  * property is outside of its allowed domain.
  */
public class IllegalPropertyValueException
        extends
            IllegalOperationTypeException
{
    /**
     * Constructor.
     *
     * @param obj the MeshObject whose property we were trying to set to a new value
     * @param pt the PropertyType that we were trying to set on obj
     * @param illegalValue the value for the PropertyType that was illegal
     */
    public IllegalPropertyValueException(
            MeshObject    obj,
            PropertyType  pt,
            PropertyValue illegalValue )
    {
        super( obj );

        thePropertyType = pt;
        theIllegalValue = illegalValue;
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
                    "dataType",
                    "value"
                },
                new Object[] {
                    theMeshObject,
                    thePropertyType.getIdentifier().toExternalForm(),
                    thePropertyType.getDataType(),
                    theIllegalValue
                });
    }

    /**
     * Obtain resource parameters for the internationalization.
     *
     * @return the resource parameters
     */
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theMeshObject, thePropertyType, thePropertyType.getDataType(), theIllegalValue };
    }

    /**
     * The PropertyType we were trying to set.
     */
    protected transient PropertyType thePropertyType;

    /**
     * The illegal value that caused this exception.
     */
    protected PropertyValue theIllegalValue;
}
