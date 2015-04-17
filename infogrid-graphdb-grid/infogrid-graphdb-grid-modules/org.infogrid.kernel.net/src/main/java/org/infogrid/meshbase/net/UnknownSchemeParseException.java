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
// Copyright 1998-2011 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.meshbase.net;

import org.infogrid.meshbase.MeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.schemes.Scheme;
import org.infogrid.util.UnknownSymbolParseException;

/**
 * A ParseException due to the use of an unknown scheme in an identifier.
 */
public class UnknownSchemeParseException
    extends
        UnknownSymbolParseException
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     *
     * @param string the text that could not be parsed
     * @param factory the DefaultNetMeshBaseIdentifierFactory that threw this exception
     */
    public UnknownSchemeParseException(
            String                              string,
            DefaultNetMeshBaseIdentifierFactory factory )
    {
        super( string, -1, string );

        theFactory = factory;
    }

    /**
     * Obtain the MeshBaseIdentifierFactory that threw this exception
     *
     * @return the MeshBaseIdentifierFactory
     */
    public MeshBaseIdentifierFactory getSource()
    {
        return theFactory;
    }
    /**
     * Obtain resource parameters for the internationalization.
     *
     * @return the resource parameters
     */
    @Override
    public Object [] getLocalizationParameters()
    {
        Scheme [] schemes = theFactory.getKnownSchemes();

        String [] schemeStrings = new String[ schemes.length ];
        for( int i=0 ; i<schemes.length ; ++i ) {
            schemeStrings[i] = schemes[i].getName();
        }
        return new Object[] { theString, schemeStrings };
    }

    /**
     * The MeshBaseIdentifierFactory that threw this exception.
     */
    protected DefaultNetMeshBaseIdentifierFactory theFactory;
}
