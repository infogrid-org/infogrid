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

import java.util.ArrayList;
import org.infogrid.lid.session.LidSessionManagementInstructions;
import org.infogrid.util.ArrayListCursorIterator;
import org.infogrid.util.CursorIterator;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.Identifier;

/**
 * Aggregates the instructions of the various stages of the LidPipeline.
 */
public class LidPipelineInstructions
{
    /**
     * Factory method.
     *
     * @param siteIdentifier identifier of the site for which these instructions apply
     * @param realm the realm that the incoming request is part of
     * @param clientAuthStatus the authentication status of the client
     * @param requestedResource the resource requested by the client
     * @return the created LidPipelineInstructions
     */
    public static LidPipelineInstructions create(
            Identifier                    siteIdentifier,
            String                        realm,
            LidClientAuthenticationStatus clientAuthStatus,
            HasIdentifier                 requestedResource )
    {
        return new LidPipelineInstructions( siteIdentifier, realm, clientAuthStatus, requestedResource );
    }

    /**
     * Constructor.
     *
     * @param siteIdentifier identifier of the site for which these instructions apply
     * @param realm the realm that the incoming request is part of
     * @param clientAuthStatus the authentication status of the client
     * @param requestedResource the resource requested by the client
     */
    protected LidPipelineInstructions(
            Identifier                    siteIdentifier,
            String                        realm,
            LidClientAuthenticationStatus clientAuthStatus,
            HasIdentifier                 requestedResource )
    {
        theSiteIdentifier    = siteIdentifier;
        theRealm             = realm;
        theClientAuthStatus  = clientAuthStatus;
        theRequestedResource = requestedResource;
    }

    /**
     * Obtain the identifier of the site for which these instructions apply.
     *
     * @return Identifier
     */
    public Identifier getSiteIdentifier()
    {
        return theSiteIdentifier;
    }

    /**
     * Obtain the realm that the incoming request is part of.
     *
     * @return the realm
     */
    public String getRealm()
    {
        return theRealm;
    }

    /**
     * Obtain the status of the client as determined by the pipeline.
     *
     * @return the status
     */
    public LidClientAuthenticationStatus getClientAuthenticationStatus()
    {
        return theClientAuthStatus;
    }

    /**
     * Obtain the resource requested by the client.
     *
     * @return the resource
     */
    public HasIdentifier getRequestedResource()
    {
        return theRequestedResource;
    }

    /**
     * Set the Yadis instructions.
     *
     * @param newInstructions the new instructions
     */
    public void setYadisInstructions(
            LidPipelineStageInstructions newInstructions )
    {
        if( theYadisInstructions != null ) {
            throw new IllegalStateException();
        }
        theYadisInstructions = newInstructions;
    }

    /**
     * Get the Yadis instructions.
     *
     * @return the Yadis instructions
     */
    public LidPipelineStageInstructions getYadisInstructions()
    {
        return theYadisInstructions;
    }

    /**
     * Add SSO instructions.
     *
     * @param newInstructions the new instructions
     */
    public void addSsoInstructions(
            LidSsoPipelineStageInstructions newInstructions )
    {
        theSsoInstructionsList.add( newInstructions );
    }

    /**
     * Get the SSO instructions.
     *
     * @return the SSO instructions
     */
    public CursorIterator<LidSsoPipelineStageInstructions> getSsoInstructionsIter()
    {
        return ArrayListCursorIterator.create( theSsoInstructionsList, LidSsoPipelineStageInstructions.class );
    }

    /**
     * Set the session management instructions.
     *
     * @param newInstructions the new instructions
     */
    public void setSessionManagementInstructions(
            LidSessionManagementInstructions newInstructions )
    {
        if( theSessionManagementInstructions != null ) {
            throw new IllegalStateException();
        }
        theSessionManagementInstructions = newInstructions;
    }

    /**
     * Get the session management instructions.
     *
     * @return the session management instructions
     */
    public LidSessionManagementInstructions getSessionManagementInstructions()
    {
        return theSessionManagementInstructions;
    }

    /**
     * Authentication status of the client.
     */
    protected LidClientAuthenticationStatus theClientAuthStatus;

    /**
     * Identifier of the site for which these instructions apply.
     */
    protected Identifier theSiteIdentifier;

    /**
     * The realm.
     */
    protected String theRealm;

    /**
     * The requested resource.
     */
    protected HasIdentifier theRequestedResource;

    /**
     * The instructions from the Yadis stage.
     */
    protected LidPipelineStageInstructions theYadisInstructions;

    /**
     * The instructions from the Sso stages. As there may be more than one, there are several.
     */
    protected ArrayList<LidSsoPipelineStageInstructions> theSsoInstructionsList
            = new ArrayList<LidSsoPipelineStageInstructions>();

    /**
     * The instructions from the session processing stage.
     */
    protected LidSessionManagementInstructions theSessionManagementInstructions;
}
