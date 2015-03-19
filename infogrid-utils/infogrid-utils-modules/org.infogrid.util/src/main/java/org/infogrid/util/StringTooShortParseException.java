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
 * A ParseException indicating that the String was too short.
 */
public class StringTooShortParseException
    extends
        LocalizedParseException
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     *
     * @param string the text that could not be parsed
     * @param message error message
     * @param minLength the minimally required length
     */
    public StringTooShortParseException(
            String string,
            String message,
            int    minLength )
    {
        super( string, message, 0, null );

        theMinLength = minLength;
    }

    /**
     * Obtain the minimally required length.
     *
     * @return the minimally required length
     */
    public int getRequiredMinLength()
    {
        return theMinLength;
    }

    /**
     * Obtain resource parameters for the internationalization.
     *
     * @return the resource parameters
     */
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theString, theMinLength };
    }

    /**
     * The minimally required length.
     */
    protected int theMinLength;
}
