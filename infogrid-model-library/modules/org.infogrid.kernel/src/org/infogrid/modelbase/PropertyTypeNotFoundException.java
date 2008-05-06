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

import org.infogrid.model.primitives.AttributableMeshType;
import org.infogrid.model.primitives.SubjectArea;

import org.infogrid.model.primitives.MeshTypeIdentifier;
import org.infogrid.util.StringHelper;

/**
 * This Exception indicates that a PropertyType could not be found.
 */
public class PropertyTypeNotFoundException
        extends
            MeshTypeNotFoundException
{
    /**
     * Constructor.
     *
     * @param amt the AttributableMeshType within which a PropertyType could not be found
     * @param propertyTypeName the name of the PropertyType that could not be found
     */
    public PropertyTypeNotFoundException(
            AttributableMeshType amt,
            String               propertyTypeName )
    {
        super();

        if( amt != null ) {
            theSubjectArea       = amt.getSubjectArea();
            theSubjectAreaName   = amt.getSubjectArea().getIdentifier();
            theAmtName           = amt.getIdentifier();
        } else {
            theSubjectArea       = null;
            theSubjectAreaName   = null;
            theAmtName           = null;
        }
        thePropertyTypeName  = propertyTypeName;
    }

    /**
     * Constructor.
     *
     * @param amt the AttributableMeshType within which a PropertyType could not be found
     * @param propertyTypeName the name of the PropertyType that could not be found
     * @param cause the Throwable that caused this Exception
     */
    public PropertyTypeNotFoundException(
            AttributableMeshType amt,
            String               propertyTypeName,
            Throwable            cause )
    {
        super( cause );

        if( amt != null ) {
            theSubjectArea       = amt.getSubjectArea();
            theSubjectAreaName   = amt.getSubjectArea().getIdentifier();
            theAmtName           = amt.getIdentifier();
        } else {
            theSubjectArea       = null;
            theSubjectAreaName   = null;
            theAmtName           = null;
        }
        thePropertyTypeName  = propertyTypeName;
    }

    /**
     * Convert object into string representation, mostly for debugging.
     *
     * @return string representation of this object
     */
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String [] {
                    "AttributableMeshType",
                    "PropertyType"
                },
                new Object[] {
                    theAmtName,
                    thePropertyTypeName
                });
    }

    /**
     * Obtain parameters for the internationalization.
     *
     * @return the parameters
     */
    public Object [] getLocalizationParameters()
    {
        if( theSubjectArea != null ) { // support serialization
            return new Object[]{ theSubjectArea, theAmt, thePropertyTypeName };
        } else {
            return new Object[]{ theSubjectAreaName, theAmtName, thePropertyTypeName };
        }
    }

    /**
     * The SubjectArea in which the AttributableMeshType resides in which the PropertyType could not be found.
     */
    protected volatile SubjectArea theSubjectArea;
    
    /**
     * The Identifier of the SubjectArea in which the AttributableMeshType resides in which the
     * PropertyType could not be found.
     */
    protected MeshTypeIdentifier theSubjectAreaName;
    
    /**
     * The AttributableMeshType in which the PropertyType could not be found.
     */
    protected volatile AttributableMeshType theAmt;
    
    /**
     * The Identifier of the AttributableMeshType in which the PropertyType could not be found. This
     * is stored as MeshTypeIdentifier rather than AttributableMeshType in order to support serialization.
     */
    protected MeshTypeIdentifier theAmtName;

    /**
     * The name of the PropertyType that could not be found.
     */
    protected String thePropertyTypeName;
}
