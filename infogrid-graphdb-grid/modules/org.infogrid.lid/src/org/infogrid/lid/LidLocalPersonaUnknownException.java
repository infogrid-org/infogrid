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

package org.infogrid.lid;

import org.infogrid.util.AbstractLocalizedException;

/**
 * Thrown if a LidLocalPersona with this identifier is required for an operation but does not exist.
 */
public class LidLocalPersonaUnknownException
        extends
            AbstractLocalizedException
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     * 
     * @param identifier the identifier that could not be resolved into a LidLocalPersona
     */
    public LidLocalPersonaUnknownException(
            String identifier )
    {
        theIdentifier = identifier;
    }
    
    /**
     * Constructor.
     * 
     * @param identifier the identifier that could not be resolved into a LidLocalPersona
     * @param cause the underlying cause, if any
     */
    public LidLocalPersonaUnknownException(
            String    identifier,
            Throwable cause )
    {
        super( cause );

        theIdentifier = identifier;
    }
    
    /**
     * Obtain the identifier that could not be resolved into a LidLocalPersona.
     * 
     * @return the identifier
     */
    public String getIdentifier()
    {
        return theIdentifier;
    }
    
    /**
     * Obtain resource parameters for the internationalization.
     *
     * @return the resource parameters
     */    
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theIdentifier };
    }

    /**
     * The identifier that could not be resolved into a LidLocalPersona.
     */
    protected String theIdentifier;        
}
