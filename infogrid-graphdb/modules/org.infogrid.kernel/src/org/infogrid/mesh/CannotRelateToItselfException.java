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

/**
 * Thrown if an attempt is made to relate a MeshObject to itself.
 */
public class CannotRelateToItselfException
        extends
            RelatedAlreadyException
{
    /**
     * Constructor.
     */
    public CannotRelateToItselfException(
            MeshObject obj )
    {
        super( obj, obj );
    }

    /**
     * Obtain parameters for the internationalization.
     *
     * @return the parameters
     */
    @Override
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theMeshObject };
    }

}
