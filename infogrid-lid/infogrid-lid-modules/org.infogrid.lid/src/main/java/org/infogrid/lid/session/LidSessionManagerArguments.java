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
// Copyright 1998-2012 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.lid.session;

import org.infogrid.lid.account.LidAccount;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.Identifier;

/**
 * Collects the arguments to the LidSessionManager factory methods.
 */
public class LidSessionManagerArguments
{
    /**
     * Factory method.
     *
     * @param sessionDuration the session duration, in milliseconds
     * @param client the client whose session it is
     * @param siteIdentifier identifier of the site where the session takes place
     * @param account the account where the session takes place
     * @param clientIp IP address of the client that created the session
     * @return the created LidSessionManagerArguments
     */
    public static LidSessionManagerArguments create(
            long          sessionDuration,
            HasIdentifier client,
            Identifier    siteIdentifier,
            LidAccount    account,
            String        clientIp )
    {
        return new LidSessionManagerArguments( sessionDuration, client, siteIdentifier, account, clientIp );
    }

    /**
     * Constructor for subclasses only, use factory method.
     *
     * @param sessionDuration the session duration, in milliseconds
     * @param client the client whose session it is
     * @param siteIdentifier identifier of the site where the session takes place
     * @param account the account where the session takes place
     * @param clientIp IP address of the client that created the session
     */
    protected LidSessionManagerArguments(
            long          sessionDuration,
            HasIdentifier client,
            Identifier    siteIdentifier,
            LidAccount    account,
            String        clientIp )
    {
        theSessionDuration = sessionDuration;
        theClient          = client;
        theSiteIdentifier  = siteIdentifier;
        theAccount         = account;
        theClientIp        = clientIp;
    }

    /**
     * Obtain the duration of the session.
     * 
     * @return the duration of the session, in milliseconds.
     */
    public long getSessionDuration()
    {
        return theSessionDuration;
    }

    /**
     * Obtain the client whose session it is.
     *
     * @return the client
     */
    public HasIdentifier getClient()
    {
        return theClient;
    }

    /**
     * Obtain the Identifier of the site where the session takes place.
     *
     * @return the site Identifier
     */
    public Identifier getSiteIdentifier()
    {
        return theSiteIdentifier;
    }

    /**
     * Obtain the account where the session takes place.
     *
     * @return the account
     */
    public LidAccount getAccount()
    {
        return theAccount;
    }

    /**
     * Obtain the IP address of the client that originally created the session.
     *
     * @return the IP address
     */
    public String getClientIp()
    {
        return theClientIp;
    }

    /**
     * The duration of the session.
     */
    protected long theSessionDuration;

    /**
     * The client whose session it is.
     */
    protected HasIdentifier theClient;

    /**
     * Identifier of the site whose session it is.
     */
    protected Identifier theSiteIdentifier;
    
    /**
     * Account at the site whose session it is.
     */
    protected LidAccount theAccount;

    /**
     * The IP address of the client that originally created the session.
     */
    protected String theClientIp;
}
