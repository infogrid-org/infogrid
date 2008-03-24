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

import org.infogrid.util.AbstractLocalizedRuntimeException;
import org.infogrid.util.StringHelper;

/**
 * This Exception indicates that two MeshObjects are not equivalents, and thus
 * this MeshObject cannot be removed from the equivalence set.
 */
public class NotEquivalentException
        extends
            AbstractLocalizedRuntimeException
{
    /**
     * Constructor.
     *
     * @param meshObject the MeshObject where we discovered the EquivalentAlreadyException
     * @param notEquivalent theMeshObject that was not an equivalent
     */
    public NotEquivalentException(
            MeshObject meshObject,
            MeshObject notEquivalent )
    {
        theMeshObject = meshObject;
        theEquivalent = notEquivalent;
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
                    "theEquivalent"
                },
                new Object[] {
                    theMeshObject,
                    theEquivalent
                });
    }

    /**
     * Obtain parameters for the internationalization.
     *
     * @return the parameters
     */
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theMeshObject, theEquivalent };
    }

    /**
     * The MeshObject for which we discovered a violation.
     */
    protected MeshObject theMeshObject;

    /**
     * The MeshObject that was supposed to become an equivalent.
     */
    protected MeshObject theEquivalent;
}
