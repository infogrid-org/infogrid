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
// Copyright 1998-2015 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.util;

/**
 * An ParseException indicating that the while parsing succeeded, the wrong number
 * of objects were found.
 */
public class InvalidObjectNumberFoundParseException
    extends
        LocalizedParseException
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     *
     * @param string the text that could not be parsed
     * @param rightNumber the number of objects expected
     * @param found the found objects
     */
    public InvalidObjectNumberFoundParseException(
            String    string,
            int       rightNumber,
            Object [] found )
    {
        super( string, null, 0, null );

        theRightNumber = rightNumber;
        theFound       = found;
    }

    /**
     * Obtain the right number of objects that should have been found.
     *
     * @return the right number of objects
     */
    public int getRightNumber()
    {
        return theRightNumber;
    }

    /**
     * Obtain the found objects.
     *
     * @return the found objects
     */
    public Object [] getFound()
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
        return new Object[] { theString, theRightNumber, theFound.length, theFound };
    }

    /**
     * The right number of objects that should have been found.
     */
    protected int theRightNumber;

    /**
     * The found objects.
     */
    protected Object [] theFound;
}
