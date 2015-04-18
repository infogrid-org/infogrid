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

import org.infogrid.lid.LidClientAuthenticationStatus;
import org.infogrid.lid.LidPipelineStageInstructions;
import org.infogrid.lid.account.LidAccount;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.Identifier;

/**
 * Instructions what to do to manage the client's session.
 */
public interface LidSessionManagementInstructions
        extends
            LidPipelineStageInstructions
{
    /**
     * Obtain the current client's authentication status.
     *
     * @return the current client's authentication status
     */
    public LidClientAuthenticationStatus getClientAuthenticationStatus();

    /**
     * Obtain the LidSessions to cancel, if any.
     *
     * @return the LidSessions to cancel, if any
     */
    public LidSession [] getSessionsToCancel();

    /**
     * Obtain the LidSessions to renew, if any.
     *
     * @return the LidSessions to renew, if any
     */
    public LidSession [] getSessionsToRenew();

    /**
     * Obtain the client for which a new session shall be created, if any.
     *
     * @return the client
     */
    public HasIdentifier getClientForNewSession();

    /**
     * Obtain the Identifier of the site for which a new session shall be created, if any.
     *
     * @return the site Identifier
     */
    public Identifier getSiteIdentifierForNewSession();

    /**
     * Obtain the account for which a new session shall be created, if any.
     * 
     * @return the account
     */
    public LidAccount getAccountForNewSession();

    /**
     * Obtain the initial token for the to-be-created new session, if any.
     *
     * @return the initial token
     */
    public String getTokenForNewSession();

    /**
     * Obtain the duration, in milliseconds, for the to-be-created new session, if any.
     *
     * @return the duration, or -1L
     */
    public long getDurationOfNewSession();
}
