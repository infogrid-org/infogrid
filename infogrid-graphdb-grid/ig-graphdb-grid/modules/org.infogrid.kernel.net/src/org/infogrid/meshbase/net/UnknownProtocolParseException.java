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

package org.infogrid.meshbase.net;

import org.infogrid.util.UnknownSymbolParseException;

/**
 * A ParseException due to the use of an unknown protocol in a URL.
 */
public class UnknownProtocolParseException
    extends
        UnknownSymbolParseException
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     *
     * @param string the text that could not be parsed
     * @param errorOffset the offset, into the string, where the error occurred
     * @param unknown the unknown protocol
     * @param known the known protocols
     */
    public UnknownProtocolParseException(
            String    string,
            int       errorOffset,
            String    unknown,
            String [] known )
    {
        super( string, errorOffset, unknown );

        theKnown = known;
    }

    /**
     * Obtain the known protocols.
     *
     * @return the known protocols
     */
    public String [] getKnownProtocols()
    {
        return theKnown;
    }

    /**
     * Obtain resource parameters for the internationalization.
     *
     * @return the resource parameters
     */
    @Override
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theString, theUnknown, theKnown };
    }

    /**
     * The known protocols.
     */
    protected String [] theKnown;
}
