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

package org.infogrid.lid;

import org.infogrid.util.Identifier;
import org.infogrid.util.http.SaneRequest;

/**
 * Knows how to determine the authentication status of the client from an incoming request.
 */
public interface LidClientAuthenticationPipelineStage
{
    /**
     * Determine the authentication status of the client. This acts as a factory method for LidClientAuthenticationStatus.
     * 
     * @param lidRequest the incoming request
     * @param siteIdentifier identifies this site
     * @return the LidClientAuthenticationStatus
     */
    public LidClientAuthenticationStatus determineAuthenticationStatus(
            SaneRequest        lidRequest,
            Identifier         siteIdentifier );

    /**
     * Name of the LID argument for the identifier.
     */
    public static final String LID_PARAMETER_NAME = "lid";

    /**
     * Name of the argument for the LID action.
     */
    public static final String LID_ACTION_PARAMETER_NAME = "lid-action";

    /**
     * The LID action that cancels the session.
     */
    public static final String LID_ACTION_CANCEL_SESSION_PARAMETER_VALUE = "cancel-session";
}
