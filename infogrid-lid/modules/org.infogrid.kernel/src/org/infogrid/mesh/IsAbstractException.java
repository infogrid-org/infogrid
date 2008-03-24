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

import org.infogrid.model.primitives.AttributableMeshType;
import org.infogrid.model.primitives.MeshTypeIdentifier;

import org.infogrid.util.AbstractLocalizedRuntimeException;

/**
  * This Exception is thrown if we try to instantiate an EntityType or RelationshipType
  * that is declared as abstract.
  */
public class IsAbstractException
        extends
            AbstractLocalizedRuntimeException
{
    /**
      * Constructor.
      *
      * @param type the AttributableMeshType that is abstract
      */
    public IsAbstractException(
            AttributableMeshType type )
    {
        theTypeName = type.getIdentifier();
    }

    /**
      * Constructor.
      *
      * @param typeName the Identifier of the AttributableMeshType that is abstract
      */
    public IsAbstractException(
            MeshTypeIdentifier typeName )
    {
        theTypeName = typeName;
    }

    /**
      * Obtain the Identifier of the abstract AttributableMeshType.
      *
      * @return the Identifier of the abstract AttributableMeshType
      */
    public MeshTypeIdentifier getMeshTypeIdentifier()
    {
        return theTypeName;
    }

    /**
     * Convert to string, for debugging only.
     *
     * @return string form of this object
     */
    @Override
    public String toString()
    {
        return super.toString() + ": " + theTypeName;
    }

    /**
     * Obtain parameters for the internationalization.
     *
     * @return the parameters
     */
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theTypeName };
    }

    /**
      * The Identifier of the abstract AttributableMeshType.
      */
    protected MeshTypeIdentifier theTypeName;
}
