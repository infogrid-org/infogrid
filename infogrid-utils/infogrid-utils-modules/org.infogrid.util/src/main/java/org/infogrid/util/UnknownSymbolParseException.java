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
 * A ParseException indicating that the String contained an unknown symbol.
 */
public class UnknownSymbolParseException
    extends
        LocalizedParseException
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     *
     * @param string the text that could not be parsed
     * @param errorOffset the offset, into the string, where the error occurred
     * @param unknown the unknown symbol
     */
    public UnknownSymbolParseException(
            String string,
            int    errorOffset,
            String unknown )
    {
        super( string, null, errorOffset, null );

        theUnknown = unknown;
    }

    /**
     * Obtain the unknown symbol.
     *
     * @return the unknown symbol
     */
    public String getUnknown()
    {
        return theUnknown;
    }

    /**
     * Obtain resource parameters for the internationalization.
     *
     * @return the resource parameters
     */
    @Override
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theString, theUnknown };
    }

    /**
     * The unknown symbol.
     */
    protected String theUnknown;
}
