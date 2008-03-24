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

import org.infogrid.util.StringHelper;

/**
 * This Exception is thrown if two MeshObjects are to become unrelated, but are not
 * currently related.
 */
public class NotRelatedException
        extends
            IllegalOperationTypeException
{
    /**
     * Constructor.
     *
     * @param obj the MeshObject that is related already
     * @param other the MeshObject to which this MeshObject is not related
     */
    public NotRelatedException(
            MeshObject obj,
            MeshObject other )
    {
        super( obj );

        theOtherObject = other;
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
                    "otherObject"
                },
                new Object[] {
                    theMeshObject,
                    theOtherObject,
                });
    }

    /**
     * Obtain parameters for the internationalization.
     *
     * @return the parameters
     */
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theMeshObject, theOtherObject };
    }

    /**
     * The MeshObject to which this MeshObject is not related to.
     */
    protected transient MeshObject theOtherObject;
}
