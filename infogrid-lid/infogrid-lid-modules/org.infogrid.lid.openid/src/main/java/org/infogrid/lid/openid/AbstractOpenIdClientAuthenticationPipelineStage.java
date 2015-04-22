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

package org.infogrid.lid.openid;

import org.infogrid.lid.AbstractLidClientAuthenticationPipelineStage;
import org.infogrid.lid.LidClientAuthenticationPipelineStage;
import org.infogrid.lid.account.LidAccountManager;
import org.infogrid.lid.session.LidSessionManager;
import org.infogrid.lid.credential.LidCredentialType;
import org.infogrid.lid.openid.auth.OpenId1CredentialType;
import org.infogrid.lid.openid.auth.OpenId2CredentialType;
import org.infogrid.util.http.SaneRequest;

/**
 * Augments AbstractLidClientAuthenticationPipelineStage with OpenID support.
 */
public abstract class AbstractOpenIdClientAuthenticationPipelineStage
        extends
            AbstractLidClientAuthenticationPipelineStage
{
    /**
     * Constructor.
     *
     * @param sessionManager the LidSessionManager to use
     * @param accountManager the LidAccountManager to use
     * @param availableCredentialTypes the LidCredentialTypes known by this application
     */
    protected AbstractOpenIdClientAuthenticationPipelineStage(
            LidSessionManager       sessionManager,
            LidAccountManager       accountManager,
            LidCredentialType []    availableCredentialTypes )
    {
        super( sessionManager, accountManager, availableCredentialTypes );
    }

    /**
     * Overridable method to determine the LID argument String.
     *
     * @param lidRequest the incoming request
     * @return the LID argument String, if any
     */
    @Override
    protected String determineLidArgumentString(
            SaneRequest lidRequest )
    {
        String lidArgumentString;

        if( "POST".equalsIgnoreCase( lidRequest.getMethod() )) {
            lidArgumentString = lidRequest.getPostedArgument( LidClientAuthenticationPipelineStage.LID_PARAMETER_NAME );
            if( lidArgumentString == null ) {
                lidArgumentString = lidRequest.getPostedArgument( OpenId2CredentialType.OPENID2_IDENTIFIER_PARAMETER_NAME );
            }
            if( lidArgumentString == null ) {
                lidArgumentString = lidRequest.getPostedArgument( OpenId1CredentialType.OPENID1_IDENTIFIER_PARAMETER_NAME );
            }
        } else {

            lidArgumentString = lidRequest.getUrlArgument( LidClientAuthenticationPipelineStage.LID_PARAMETER_NAME );
            if( lidArgumentString == null ) {
                lidArgumentString = lidRequest.getUrlArgument( OpenId2CredentialType.OPENID2_IDENTIFIER_PARAMETER_NAME );
            }
            if( lidArgumentString == null ) {
                lidArgumentString = lidRequest.getUrlArgument( OpenId1CredentialType.OPENID1_IDENTIFIER_PARAMETER_NAME );
            }
        }

        return lidArgumentString;
    }
}
