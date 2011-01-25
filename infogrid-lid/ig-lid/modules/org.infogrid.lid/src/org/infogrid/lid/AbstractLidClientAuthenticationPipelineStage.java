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

import java.text.ParseException;
import java.util.ArrayList;
import org.infogrid.lid.account.LidAccount;
import org.infogrid.lid.account.LidAccountManager;
import org.infogrid.lid.session.LidSession;
import org.infogrid.lid.session.LidSessionManager;
import org.infogrid.lid.credential.LidCredentialType;
import org.infogrid.lid.credential.LidExpiredCredentialException;
import org.infogrid.lid.credential.LidInvalidCredentialException;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.CannotFindHasIdentifierException;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.Identifier;
import org.infogrid.util.InvalidIdentifierException;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.util.logging.Log;

/**
 * Collects functionality common to LidClientAuthenticationPipelineStage
 * implementations.
 */
public abstract class AbstractLidClientAuthenticationPipelineStage
        implements
            LidClientAuthenticationPipelineStage
{
    private static final Log log = Log.getLogInstance( AbstractLidClientAuthenticationPipelineStage.class ); // our own, private logger

    /**
     * Constructor.
     *
     * @param sessionManager the LidSessionManager to use
     * @param accountManager the LidAccountManager to use
     * @param availableCredentialTypes the LidCredentialTypes known by this application
     */
    protected AbstractLidClientAuthenticationPipelineStage(
            LidSessionManager       sessionManager,
            LidAccountManager       accountManager,
            LidCredentialType []    availableCredentialTypes )
    {
        theSessionManager           = sessionManager;
        theAccountManager           = accountManager;
        theAvailableCredentialTypes = availableCredentialTypes;
    }

    /**
     * Determine the authentication status of the client. This acts as a factory method for LidClientAuthenticationStatus.
     *
     * @param lidRequest the incoming request
     * @param siteIdentifier identifies this site
     * @param realm the authentication realm
     * @return the LidClientAuthenticationStatus
     */
    public LidClientAuthenticationStatus determineAuthenticationStatus(
            SaneRequest        lidRequest,
            Identifier         siteIdentifier,
            String             realm )
    {
        if( log.isTraceEnabled() ) {
            log.traceMethodCallEntry( this, "determineAuthenticationStatus", lidRequest, siteIdentifier );
        }

        String lidCookieString     = null;
        String sessionCookieString = null;

        if( realm != null ) {
            lidCookieString = lidRequest.getCookieValue( determineLidCookieName( realm ));
        }
        if( lidCookieString != null ) { // make sure we pair them right
            sessionCookieString = lidRequest.getCookieValue( determineSessionCookieName( realm ));
        } else {
            lidCookieString     = lidRequest.getCookieValue( LidCookies.LID_IDENTIFIER_COOKIE_NAME );
            sessionCookieString = lidRequest.getCookieValue( LidCookies.LID_SESSION_COOKIE_NAME );
        }

        // cleanup cookie values first
        sessionCookieString = cleanupCookieValue( sessionCookieString );
        lidCookieString     = cleanupCookieValue( lidCookieString );

        // LID argument: ignore URL arguments in case of a POST
        String lidArgumentString = determineLidArgumentString( lidRequest );

        Identifier lidCookieIdentifier   = null;
        Identifier lidArgumentIdentifier = null;

        if( lidCookieString != null && lidCookieString.length() > 0 ) {
            try {
                lidCookieIdentifier = correctIdentifier( lidRequest.getAbsoluteContextUri(), lidCookieString );

            } catch( Throwable ex ) {
                log.warn( ex );
                lidCookieIdentifier = null; // We ignore the cookie if it is misformed
            }
        }

        if( lidArgumentString != null && lidArgumentString.length() > 0 ) {
            try {
                lidArgumentIdentifier = correctIdentifier( lidRequest.getAbsoluteContextUri(), lidArgumentString );

            } catch( Throwable ex ) {
                // could not be corrected
                if( log.isDebugEnabled() ) {
                    log.debug( "Cannot correct identifier", lidArgumentString, ex );
                }
            }
        }

        // Note: it is very important to distinguish between Strings that are null here,
        // and String that are empty. Do not accidentially "fix" this code by messing this up.

        Identifier clientIdentifier;
        Identifier sessionClientIdentifier = lidCookieIdentifier;

        if( lidArgumentString != null ) { // compare with string, not identifier
            clientIdentifier = lidArgumentIdentifier != null ? lidArgumentIdentifier : null;

        } else if( lidCookieIdentifier != null ) { // use identifier here
            clientIdentifier = lidCookieIdentifier;

        } else {
            clientIdentifier = null;
        }

        LidAccount    clientPersona = null;
        HasIdentifier clientRemotePersona = null;

        if( clientIdentifier != null ) {
            try {
                HasIdentifier found = theAccountManager.find( clientIdentifier );

                if( found instanceof LidAccount ) {
                    // refers to a local account
                    clientPersona = (LidAccount) found;
                } else {
                    clientRemotePersona = found;
                }
            } catch( CannotFindHasIdentifierException ex ) {
                // ignore
                if( log.isInfoEnabled() ) {
                    log.info( ex );
                }
            } catch( InvalidIdentifierException ex ) {
                // ignore
                if( log.isInfoEnabled() ) {
                    log.info( ex );
                }
            }
        }
        if( clientPersona == null && clientRemotePersona != null ) {
            // check whether there's a LidAccount for it
            clientPersona = theAccountManager.determineLidAccountFromRemotePersona( clientRemotePersona, siteIdentifier );
        }

        boolean clientLoggedOn            = false;
        boolean clientWishesToLogin       = false;
        boolean clientWishesCancelSession = false;
        boolean clientWishesToLogout      = false;
        if(    lidRequest.matchUrlArgument(    LID_ACTION_PARAMETER_NAME, LID_ACTION_CANCEL_SESSION_PARAMETER_VALUE )
            || lidRequest.matchPostedArgument( LID_ACTION_PARAMETER_NAME, LID_ACTION_CANCEL_SESSION_PARAMETER_VALUE ) )
        {
            clientWishesCancelSession = true;
        }
        if( lidArgumentString != null && lidArgumentString.length() == 0 && sessionClientIdentifier != null ) {
            clientWishesToLogout = true;
        }

        LidSession preexistingClientSession;
        if( sessionClientIdentifier != null && sessionCookieString != null ) {
            preexistingClientSession = theSessionManager.get( sessionCookieString );

            if( preexistingClientSession != null ) {
                if( clientIdentifier != null ) {
                    if( preexistingClientSession.getSessionClient() == null || !preexistingClientSession.getSessionClient().isIdentifiedBy( clientIdentifier )) {
                        preexistingClientSession = null; // this session does not belong to this client
                    } else if ( preexistingClientSession.getSiteIdentifier() == null || !preexistingClientSession.getSiteIdentifier().equals( siteIdentifier )) {
                        preexistingClientSession = null; // this session does not belong to this site
                    }
                } else if( sessionClientIdentifier != null ) {
                    // want to log out, but we still have a session
                    if( preexistingClientSession.getSessionClient() == null || !preexistingClientSession.getSessionClient().isIdentifiedBy( sessionClientIdentifier )) {
                        preexistingClientSession = null; // wrong session
                    } else if ( preexistingClientSession.getSiteIdentifier() == null || !preexistingClientSession.getSiteIdentifier().equals( siteIdentifier )) {
                        preexistingClientSession = null; // wrong session
                    }
                }
            }
        } else {
            preexistingClientSession = null;
        }

        ArrayList<LidCredentialType>             validCredentialTypes        = null;
        ArrayList<LidCredentialType>             expiredCredentialTypes      = null;
        ArrayList<LidCredentialType>             invalidCredentialTypes      = null;
        ArrayList<LidInvalidCredentialException> invalidCredentialExceptions = null;

        if( clientRemotePersona != null || clientPersona != null ) {
            // we don't know whether we have local or remote credentials

            LidCredentialType [] referencedCredentialTypes = findRecognizedCredentialTypesIn( lidRequest );

            if( referencedCredentialTypes != null && referencedCredentialTypes.length > 0 ) {
                validCredentialTypes        = new ArrayList<LidCredentialType>();
                invalidCredentialTypes      = new ArrayList<LidCredentialType>();
                expiredCredentialTypes      = new ArrayList<LidCredentialType>();
                invalidCredentialExceptions = new ArrayList<LidInvalidCredentialException>();

                for( int i=0 ; i<referencedCredentialTypes.length ; ++i ) {
                    LidCredentialType current = referencedCredentialTypes[i];

                    HasIdentifier personaToCheck = clientRemotePersona != null ? clientRemotePersona : clientPersona;
                    try {
                        current.checkCredential( lidRequest, personaToCheck );

                        validCredentialTypes.add( current );

                    } catch( LidExpiredCredentialException ex ) {
                        expiredCredentialTypes.add( current );
                        // FIXME? Should we keep the exception around?

                    } catch( LidInvalidCredentialException ex ) {
                        invalidCredentialTypes.add( current );
                        invalidCredentialExceptions.add( ex );

                        log.warn( ex );
                    }
                }
                if( !validCredentialTypes.isEmpty() && invalidCredentialTypes.isEmpty() ) {
                    if( preexistingClientSession == null ) {
                        clientLoggedOn = true;
                    }
                }
            }
        }
        if(    lidArgumentString != null
            && lidArgumentString.length() > 0
            && ( validCredentialTypes == null   || validCredentialTypes.isEmpty() )
            && ( invalidCredentialTypes == null || invalidCredentialTypes.isEmpty() ))
        {
            clientWishesToLogin = true;
        }

        LidAuthenticationService [] authServices = determineAuthenticationServices( clientRemotePersona );

        SimpleLidClientAuthenticationStatus ret = SimpleLidClientAuthenticationStatus.create(
                lidArgumentString != null && lidArgumentString.length() > 0 ? lidArgumentString : null,
                clientIdentifier,
                clientRemotePersona,
                clientPersona,
                preexistingClientSession,
                validCredentialTypes,
                expiredCredentialTypes,
                invalidCredentialTypes,
                invalidCredentialExceptions,
                clientLoggedOn,
                clientWishesToLogin,
                clientWishesCancelSession,
                clientWishesToLogout,
                authServices,
                siteIdentifier );

        if( log.isTraceEnabled() ) {
            log.traceMethodCallExit( this, "determineAuthenticationStatus", ret );
        }
        return ret;
    }

    /**
     * Overridable method to determine the LID argument String.
     *
     * @param lidRequest the incoming request
     * @return the LID argument String, if any
     */
    protected String determineLidArgumentString(
            SaneRequest lidRequest )
    {
        String lidArgumentString;

        if( "POST".equalsIgnoreCase( lidRequest.getMethod() )) {
            lidArgumentString = lidRequest.getPostedArgument( LidClientAuthenticationPipelineStage.LID_PARAMETER_NAME );

        } else {
            lidArgumentString = lidRequest.getUrlArgument( LidClientAuthenticationPipelineStage.LID_PARAMETER_NAME );
        }

        return lidArgumentString;
    }

    /**
     * Correct the entered clientIdentifier.
     *
     * @param contextUri the absolute URI of the application's context
     * @param candidate what the user typed
     * @return the corrected clientIdentifier
     * @throws ParseException thrown if the identifier was malformed
     */
    protected abstract Identifier correctIdentifier(
            String contextUri,
            String candidate )
        throws
            ParseException;

    /**
     * Determine the available authentication services for this client.
     *
     * @param remoteClient the client's remote persona
     * @return the authentication services, in sequence of preference, if any
     */
    protected LidAuthenticationService [] determineAuthenticationServices(
            HasIdentifier remoteClient )
    {
        return null;
    }

    /**
     * Determine the subset of known LidCredentialTypes given in this request.
     * The LidCredentialTypes will be returned regardless of whether the credentials are
     * valid or invalid.
     *
     * @param request the request
     * @return the recognized LidCredentialTypes
     */
    public LidCredentialType [] findRecognizedCredentialTypesIn(
            SaneRequest request )
    {
        LidCredentialType [] ret = new LidCredentialType[ theAvailableCredentialTypes.length ];
        int count = 0;

        for( LidCredentialType current : theAvailableCredentialTypes ) {
            if( current.isContainedIn( request )) {
                ret[count++] = current;
            }
        }
        if( count < ret.length ) {
            ret = ArrayHelper.copyIntoNewArray( ret, 0, count, LidCredentialType.class );
        }
        return ret;
    }

    /**
     * Given a realm, determine the LID cookie's name.
     *
     * @param realm the name of the realm
     * @return name of the LID cookie
     * @see #determineSessionCookieName
     */
    protected String determineLidCookieName(
            String realm )
    {
        if( realm == null ) {
            return LidCookies.LID_IDENTIFIER_COOKIE_NAME;
        } else {
            StringBuilder buf = new StringBuilder();
            buf.append( LidCookies.LID_IDENTIFIER_COOKIE_NAME );
            buf.append( '-' );
            buf.append( realm );
            return buf.toString();
        }
    }

    /**
     * Given a realm, determine the LID session cookie's name.
     *
     * @param realm the name of the realm
     * @return name of the LID session cookie
     * @see #determineSessionCookieName
     * @see AbstractLidClientAuthenticationPipelineStage#determineSessionCookieName
     */
    protected String determineSessionCookieName(
            String realm )
    {
        if( realm == null ) {
            return LidCookies.LID_SESSION_COOKIE_NAME;
        } else {
            StringBuilder buf = new StringBuilder();
            buf.append( LidCookies.LID_SESSION_COOKIE_NAME );
            buf.append( '-' );
            buf.append( realm );
            return buf.toString();
        }
    }

    /**
     * Overridable helper method to clean up cookie values.
     *
     * @param raw the raw cookie valkue
     * @return the cleaned-up value
     */
    protected String cleanupCookieValue(
            String raw )
    {
        String ret;

        if( raw == null ) {
            ret = null;
        } else if( raw.startsWith( "\"" ) && raw.endsWith( "\"" )) {
            ret = raw.substring( 1, raw.length()-1 );
        } else {
            ret = raw;
        }
        return ret;
    }

    /**
     * The LidSessionManager to use.
     */
    protected LidSessionManager theSessionManager;

    /**
     * The LidAccountManager to use.
     */
    protected LidAccountManager theAccountManager;

    /**
     * The credential types known by this application.
     */
    protected LidCredentialType [] theAvailableCredentialTypes;
}
