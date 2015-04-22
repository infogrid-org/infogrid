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

package org.infogrid.lid.credential;

import org.infogrid.util.AbstractLocalizedException;
import org.infogrid.util.Identifier;
import org.infogrid.util.logging.CanBeDumped;
import org.infogrid.util.logging.Dumper;

/**
 * Thrown if a credential was provided that was invalid.
 */
public abstract class LidInvalidCredentialException
        extends
            AbstractLocalizedException
        implements
            CanBeDumped
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     * 
     * @param identifier the identifier for which an invalid credential was provided
     * @param siteIdentifier the site at which the invalid credential was provided
     * @param type the type of credential that was invalid
     */
    protected LidInvalidCredentialException(
            Identifier        identifier,
            Identifier        siteIdentifier,
            LidCredentialType type )
    {
        super( null, null );
        
        theIdentifier     = identifier;
        theSiteIdentifier = siteIdentifier;
    }
    
    /**
     * Constructor.
     * 
     * @param identifier the identifier for which an invalid credential was provided
     * @param siteIdentifier the site at which the invalid credential was provided
     * @param type the type of credential that was invalid
     * @param cause the underlying cause, if any
     */
    protected LidInvalidCredentialException(
            Identifier        identifier,
            Identifier        siteIdentifier,
            LidCredentialType type,
            Throwable         cause )
    {
        super( null, cause );

        theIdentifier     = identifier;
        theSiteIdentifier = siteIdentifier;
        theType           = type;
    }
    
    /**
     * Obtain the identifier for which an invalid credential was provided.
     * 
     * @return the identifier
     */
    public Identifier getIdentifier()
    {
        return theIdentifier;
    }

    /**
     * Obtain the identifier for the site at which an invalid credential was provided.
     *
     * @return the identifier
     */
    public Identifier getSiteIdentifier()
    {
        return theSiteIdentifier;
    }

    /**
     * Obtain the type of credential that was invalid.
     * 
     * @return the credential type
     */
    public LidCredentialType getCredentialType()
    {
        return theType;
    }

    /**
     * Obtain resource parameters for the internationalization.
     *
     * @return the resource parameters
     */    
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theIdentifier, theType };
    }

    /**
     * Dump this object.
     *
     * @param d the Dumper to dump to
     */
    public void dump(
            Dumper d )
    {
        d.dump( this,
                new String[] {
                        "theIdentifier",
                        "theSiteIdentifier",
                        "theType"
                },
                new Object[] {
                        theIdentifier,
                        theSiteIdentifier,
                        theType,
                } );
    }

    /**
     * The identifier for which an invalid credential was provided.
     */
    protected Identifier theIdentifier;

    /**
     * The identifier of the site at which an invalid credential was provided.
     */
    protected Identifier theSiteIdentifier;

    /**
     * The type of credential that was invalid.
     */
    protected LidCredentialType theType;
}

