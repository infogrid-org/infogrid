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

import java.util.Iterator;
import org.infogrid.lid.account.LidAccountManager;
import org.infogrid.lid.session.LidSessionManagementInstructions;
import org.infogrid.lid.session.LidSessionManagementPipelineStage;
import org.infogrid.lid.yadis.YadisPipelineStage;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.Identifier;
import org.infogrid.util.context.AbstractObjectInContext;
import org.infogrid.util.context.Context;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.util.logging.Log;

/**
 * Processes LID requests in the default manner.
 */
public class DefaultLidPipeline
        extends
            AbstractObjectInContext
        implements
             LidPipeline
{
    private static final Log log = Log.getLogInstance( LidPipeline.class ); // our own, private logger

    /**
     * Factory method.
     *
     * @param c the Context in which this object operates
     * @return the created LidPipeline
     */
    public static DefaultLidPipeline create(
            Context c )
    {
        DefaultLidPipeline ret = new DefaultLidPipeline( c );
        return ret;
    }

    /**
     * Constructor for subclasses only, use factory method.
     * 
     * @param c the Context in which this object operates
     */
    protected DefaultLidPipeline(
            Context c )
    {
        super( c );

        theResourceFinder         = c.findContextObject( LidResourceFinder.class );
        theAccountManager         = c.findContextObject( LidAccountManager.class );
        theYadisStage             = c.findContextObject( YadisPipelineStage.class );
        theAuthenticationStage    = c.findContextObject( LidClientAuthenticationPipelineStage.class );
        theSessionManagementStage = c.findContextObject( LidSessionManagementPipelineStage.class );
        
        theSsoStages = new LidSsoPipelineStage[0]; // inefficient but will do
        Iterator<LidSsoPipelineStage> iter = c.contextObjectIterator( LidSsoPipelineStage.class );
        while( iter.hasNext() ) {
            LidSsoPipelineStage current = iter.next();
            theSsoStages = ArrayHelper.append( theSsoStages, current, LidSsoPipelineStage.class );
        }
    }
    
    /**
     * Process the pipeline.
     *
     * @param lidRequest the incoming request
     * @param siteIdentifier identifies this site
     * @param realm the realm of the authentication
     * @return the compound instructions
     */
    public LidPipelineInstructions processPipeline(
            SaneRequest        lidRequest,
            Identifier         siteIdentifier,
            String             realm )
    {
        HasIdentifier requestedResource = null;
        if( theResourceFinder != null ) {
            try {
                requestedResource = theResourceFinder.findFromRequest( lidRequest );

            } catch( Exception ex ) {
                if( log.isInfoEnabled() ) {
                    log.info( ex );
                }
            }
        }

        LidClientAuthenticationStatus clientAuthStatus = null;
        if( theAuthenticationStage != null ) {
            clientAuthStatus = theAuthenticationStage.determineAuthenticationStatus( lidRequest, siteIdentifier, realm );
        }

        LidPipelineInstructions instructionsSoFar = LidPipelineInstructions.create(
                siteIdentifier,
                realm,
                clientAuthStatus,
                requestedResource );
        LidPipelineStageInstructions instructionsToAdd;

        if( theYadisStage != null ) {
            // this also needs to be invoked if requestedResource is null
            instructionsToAdd = theYadisStage.processStage( lidRequest, requestedResource, instructionsSoFar );
            if( instructionsToAdd != null ) {
                instructionsSoFar.setYadisInstructions( instructionsToAdd );
            }
        }

        if( theSsoStages != null ) {
            for( int i=0 ; i<theSsoStages.length ; ++i ) {
                instructionsToAdd = theSsoStages[i].processStage( lidRequest, requestedResource, instructionsSoFar );
                if( instructionsToAdd != null ) {
                    instructionsSoFar.addSsoInstructions( (LidSsoPipelineStageInstructions) instructionsToAdd );
                }
            }
        }
        if( theSessionManagementStage != null ) {
            instructionsToAdd = theSessionManagementStage.processStage( lidRequest, requestedResource, instructionsSoFar );
            if( instructionsToAdd != null ) {
                instructionsSoFar.setSessionManagementInstructions( (LidSessionManagementInstructions) instructionsToAdd );
            }
        }
        return instructionsSoFar;
    }

    /**
     * The service that knows how to find requested resources.
     */
    protected LidResourceFinder theResourceFinder;

    /**
     * The service that knows how to find LidAccounts for incoming requests.
     */
    protected LidAccountManager theAccountManager;

    /**
     * The service that knows how to respond to Yadis requests.
     */
    protected YadisPipelineStage theYadisStage;

    /**
     * The service that knows how to determine the authentication status of the client.
     */
    protected LidClientAuthenticationPipelineStage theAuthenticationStage;

    /**
     * The service that knows how to manage sessions.
     */
    protected LidSessionManagementPipelineStage theSessionManagementStage;

    /**
     * The services that know how to handle IdP-side SSO.
     */
    protected LidSsoPipelineStage [] theSsoStages;
}
