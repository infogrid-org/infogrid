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
// Copyright 1998-2010 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.util;

/**
 * An ParseException indicating that the while parsing succeeded, the wrong type of
 * object was found.
 */
public class InvalidObjectTypeFoundParseException
    extends
        LocalizedParseException
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     *
     * @param string the text that could not be parsed
     * @param rightClass the right type of object expected
     * @param found the found Objject
     * @param cause the underlying ClassCastException
     */
    public InvalidObjectTypeFoundParseException(
            String             string,
            Class<?>           rightClass,
            Object             found,
            ClassCastException cause )
    {
        super( string, null, 0, cause );

        theRightClass = rightClass;
        theFound      = found;
    }

    /**
     * Obtain the right Class that should have been found.
     *
     * @return the right Class
     */
    public Class<?> getRightClass()
    {
        return theRightClass;
    }

    /**
     * Obtain the found object.
     *
     * @return the found object
     */
    public Object getFound()
    {
        return theFound;
    }

    /**
     * Obtain resource parameters for the internationalization.
     *
     * @return the resource parameters
     */
    @Override
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theString, theRightClass, theFound, getCause() };
    }

    /**
     * The right type of object that should have been found.
     */
    protected Class<?> theRightClass;

    /**
     * The found object.
     */
    protected Object theFound;
}
