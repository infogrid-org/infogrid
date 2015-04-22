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

package org.infogrid.lid.account;

import org.infogrid.util.AbstractLocalizedException;

/**
 * Thrown if the LidAccount with this local Identifier existed already during
 * an attempt to provision it.
 */
public class LidAccountExistsAlreadyException
        extends
            AbstractLocalizedException
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     * 
     * @param account the LidAccount that existed already
     */
    public LidAccountExistsAlreadyException(
            LidAccount account )
    {
        theAccount = account;
    }
    
    /**
     * Constructor.
     * 
     * @param account the LidAccount that existed already
     * @param cause the underlying cause
     */
    public LidAccountExistsAlreadyException(
            LidAccount account,
            Throwable  cause )
    {
        super( cause );

        theAccount = account;
    }
    
    /**
     * Obtain the LidAccount that existed already.
     * 
     * @return the LidAccount
     */
    public LidAccount getAccount()
    {
        return theAccount;
    }

    /**
     * Obtain resource parameters for the internationalization.
     *
     * @return the resource parameters
     */    
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theAccount }; // FIXME? Perhaps more?
    }

    /**
     * The LidAccount that existed already.
     */
    protected LidAccount theAccount;
}
