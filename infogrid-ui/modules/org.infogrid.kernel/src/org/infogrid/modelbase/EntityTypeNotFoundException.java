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

package org.infogrid.modelbase;

import org.infogrid.model.primitives.SubjectArea;

/**
 * This Exception indicates that an EntityType could not be found.
 */
public class EntityTypeNotFoundException
        extends
            AttributableMeshTypeNotFoundException
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     *
     * @param sa the SubjectArea within which a EntityType could not be found
     * @param entityTypeName the name of the EntityType that could not be found
     */
    public EntityTypeNotFoundException(
            SubjectArea sa,
            String      entityTypeName )
    {
        super( sa, entityTypeName );
    }

    /**
     * Constructor.
     *
     * @param sa the SubjectArea within which a EntityType could not be found
     * @param entityTypeName the name of the EntityType that could not be found
     * @param cause the Throwable that caused this Exception
     */
    public EntityTypeNotFoundException(
            SubjectArea sa,
            String      entityTypeName,
            Throwable   cause )
    {
        super( sa, entityTypeName, cause );
    }

    /**
     * Convert object into string representation, mostly for debugging.
     *
     * @return string representation of this object
     */
    @Override
    public String toString()
    {
        StringBuffer almostRet = new StringBuffer();
        almostRet.append( super.toString() );
        almostRet.append( "SubjectArea: " );
        almostRet.append( theSubjectAreaIdentifier );
        almostRet.append( ", EntityType: " );
        almostRet.append( theAmtName );
        return almostRet.toString();
    }
}
