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

package org.infogrid.meshbase;

import org.infogrid.util.AbstractLocalizedRuntimeException;

/**
 * Thrown if an operation is executed on MeshObjects that are contained
 * in different MeshBases. For example, it is thrown when a MeshObject from MeshBase
 * A is passed to the MeshObjectSet.contains() method, which is applied to a MeshObjectSet
 * with MeshObjects from MeshBase B.
 */
public class WrongMeshBaseException
        extends
            AbstractLocalizedRuntimeException
{
    /**
     * Constructor.
     *
     * @param one the first MeshBase
     * @param two the second, different, MeshBase
     */
    public WrongMeshBaseException(
            MeshBase one,
            MeshBase two )
    {
        this( one, two, null );
    }
    
    /**
     * Constructor.
     *
     * @param one the first MeshBase
     * @param two the second, different, MeshBase
     * @param msg the message
     */
    public WrongMeshBaseException(
            MeshBase one,
            MeshBase two,
            String   msg )
    {
        super( msg );

        theOne = one;
        theTwo = two;
    }
    
    /**
     * Obtain the first MeshBase.
     *
     * @return the first MeshBase
     */
    public MeshBase getFirstMeshBase()
    {
        return theOne;
    }
    
    /**
     * Obtain the second MeshBase.
     *
     * @return the second MeshBase
     */
    public MeshBase getSecondMeshBase()
    {
        return theOne;
    }
    
    /**
     * Obtain parameters for the internationalization.
     *
     * @return the parameters
     */
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theOne, theTwo };
    }
    
    /**
     * The first MeshBase.
     */
    protected MeshBase theOne;
    
    /**
     * The second MeshBase.
     */
    protected MeshBase theTwo;
}
