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

package org.infogrid.lid;

import org.infogrid.util.AbstractLocalizedException;

/**
 * Thrown if the LidPersona with this local Identifier existed already during
 * an attempt to provision it.
 */
public class LidPersonaExistsAlreadyException
        extends
            AbstractLocalizedException
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     * 
     * @param persona the LidPersona that existed already
     */
    public LidPersonaExistsAlreadyException(
            LidPersona persona )
    {
        thePersona = persona;
    }
    
    /**
     * Constructor.
     * 
     * @param persona the LidPersona that existed already
     * @param cause the underlying cause
     */
    public LidPersonaExistsAlreadyException(
            LidPersona persona,
            Throwable  cause )
    {
        super( cause );

        thePersona = persona;
    }
    
    /**
     * Obtain the LidPersona that existed already.
     * 
     * @return the LidPersona
     */
    public LidPersona getPersona()
    {
        return thePersona;
    }

    /**
     * Obtain resource parameters for the internationalization.
     *
     * @return the resource parameters
     */    
    public Object [] getLocalizationParameters()
    {
        return new Object[] { thePersona }; // FIXME? Perhaps more?
    }

    /**
     * The LidPersona that existed already.
     */
    protected LidPersona thePersona;
}
