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
 * An ParseException indicating that the String contained one or more
 * invalid characters or character strings.
 */
public class InvalidCharacterParseException
    extends
        LocalizedParseException
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     *
     * @param string the text that could not be parsed
     * @param message error message
     * @param errorOffset the position where the error is found while parsing.
     * @param invalid the invalid character or string
     */
    public InvalidCharacterParseException(
            String string,
            String message,
            int    errorOffset,
            String invalid )
    {
        super( string, message, errorOffset, null );

        theInvalid = invalid;
    }

    /**
     * Obtain the invalid sub-String.
     *
     * @return the invalid sub-String
     */
    public String getInvalid()
    {
        return theInvalid;
    }

    /**
     * Obtain resource parameters for the internationalization.
     *
     * @return the resource parameters
     */
    @Override
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theString, getErrorOffset(), theInvalid };
    }

    /**
     * The invalid sub-String.
     */
    protected String theInvalid;
}
