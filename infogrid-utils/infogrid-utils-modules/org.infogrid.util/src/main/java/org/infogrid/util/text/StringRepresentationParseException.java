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

package org.infogrid.util.text;

import org.infogrid.util.LocalizedParseException;

/**
 * Thrown if a String could not be parsed by a StringRepresentation.
 */
public class StringRepresentationParseException
    extends
        LocalizedParseException
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     *
     * @param string the text that could not be parsed
     * @param formatString the format String that defines the syntax of the String to be parsed
     * @param cause the cause of this Exception
     */
    public StringRepresentationParseException(
            String    string,
            String    formatString,
            Throwable cause )
    {
        super( string, null, 0, cause );

        theFormatString = formatString;
    }

    /**
     * Constructor for the case that no format String could be found.
     *
     * @param string the text that could not be parsed
     */
    public StringRepresentationParseException(
            String string )
    {
        super( string, null, 0, null );

        theFormatString = null;
    }

    /**
     * Obtain the format String.
     *
     * @return the format String
     */
    public String getFormatString()
    {
        return theFormatString;
    }

    /**
     * Allow subclasses to override which key to use in the Resource file for the string representation.
     *
     * @return the key
     */
    @Override
    protected String findStringRepresentationParameter()
    {
        if( theFormatString != null ) {
            return "WithFormatString";
        } else {
            return "WithoutFormatString";
        }
    }

    /**
     * Obtain resource parameters for the internationalization.
     *
     * @return the resource parameters
     */
    @Override
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theString, theFormatString };
    }

    /**
     * The format String for the String.
     */
    protected String theFormatString;
}
