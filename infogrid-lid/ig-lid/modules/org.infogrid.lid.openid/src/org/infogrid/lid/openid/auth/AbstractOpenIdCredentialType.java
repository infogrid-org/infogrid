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

package org.infogrid.lid.openid.auth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import org.infogrid.lid.nonce.LidNonceManager;
import org.infogrid.lid.credential.AbstractLidCredentialType;
import org.infogrid.lid.credential.LidExpiredCredentialException;
import org.infogrid.lid.credential.LidInvalidCredentialException;
import org.infogrid.lid.openid.CryptUtils;
import org.infogrid.lid.openid.OpenIdAssociationExpiredException;
import org.infogrid.lid.openid.OpenIdCannotValidateStatelessException;
import org.infogrid.lid.openid.OpenIdInvalidSignatureException;
import org.infogrid.lid.openid.OpenIdNoAssociationException;
import org.infogrid.lid.openid.OpenIdRpSideAssociation;
import org.infogrid.lid.openid.OpenIdRpSideAssociationManager;
import org.infogrid.lid.openid.OpenIdRpSideAssociationNegotiationParameters;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.Base64;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.Identifier;
import org.infogrid.util.http.HTTP;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.util.logging.Log;

/**
 * Common superclass for OpenID authentication credential types.
 */
public abstract class AbstractOpenIdCredentialType
        extends
            AbstractLidCredentialType
        implements
            OpenIdCredentialType
{
    private static final Log log = Log.getLogInstance( AbstractOpenIdCredentialType.class ); // our own, private logger

    /**
     * Constructor, for subclasses only.
     *
     * @param associationManager the relying party-side association manager to use
     * @param nonceManager the LidNonceManager to use
     */
    protected AbstractOpenIdCredentialType(
            OpenIdRpSideAssociationManager associationManager,
            LidNonceManager                nonceManager )
    {
        theAssociationManager = associationManager;
        theNonceManager       = nonceManager;
    }

    /**
     * Determine whether the request contains a valid LidCredentialType of this type
     * for the given subject at the site with the given Identifier.
     *
     * @param request the request
     * @param subject the subject
     * @param siteIdentifier identifies the site
     * @param mandatoryFields set of fields that are mandatory
     * @param nonceParameterName name of the parameter representing the nonce
     * @throws LidExpiredCredentialException thrown if the contained LidCdedentialType has expired
     * @throws LidInvalidCredentialException thrown if the contained LidCdedentialType is not valid for this subject
     */
    protected void checkCredential(
            SaneRequest     request,
            HasIdentifier   subject,
            Identifier      siteIdentifier,
            HashSet<String> mandatoryFields,
            String          nonceParameterName )
        throws
            LidExpiredCredentialException,
            LidInvalidCredentialException
    {
        String associationHandle = request.getUrlArgument( OPENID_ASSOC_HANDLE_PARAMETER_NAME );

        if( associationHandle == null || associationHandle.length() == 0 ) {
            // we don't do dumb mode
            throw new OpenIdNoAssociationException( subject.getIdentifier(), siteIdentifier, this );
        }

        if( nonceParameterName != null ) {
            theNonceManager.validateNonce( request, subject.getIdentifier(), siteIdentifier, this, nonceParameterName ); // throws LidInvalidNonceException
        } else {
            theNonceManager.validateNonce( request, subject.getIdentifier(), siteIdentifier, this ); // throws LidInvalidNonceException
        }

        String []               endpointCandidates = determineOpenIdEndpointsFor( subject );
        String                  invalidateHandle   = request.getUrlArgument( OPENID_INVALIDATE_HANDLE_PARAMETER_NAME );
        OpenIdRpSideAssociation association        = null;

        for( String epCandidate : endpointCandidates ) {

            OpenIdRpSideAssociation assocCandidate = theAssociationManager.get( epCandidate );
            if( assocCandidate == null ) {
                continue;
            }
            if( invalidateHandle != null && invalidateHandle.equals( assocCandidate.getAssociationHandle() ) ) {
                theAssociationManager.remove( assocCandidate.getServerUrl() );
                
            } else if ( assocCandidate.getAssociationHandle().equals( associationHandle )) {
                // found
                association = assocCandidate;
                break;
            }
        }

        if( association == null ) {
            LidInvalidCredentialException t = null;

            String specifiedEndpoint = request.getUrlArgument( OPENID_ENDPOINT_PARAMETER_NAME );
            if( specifiedEndpoint != null ) {
                if( !ArrayHelper.isIn( specifiedEndpoint, endpointCandidates, true )) {
                    log.error( "Cannot find specified endpoint in OpenID request", request, specifiedEndpoint, endpointCandidates );
                    throw new OpenIdInvalidSignatureException( subject.getIdentifier(), siteIdentifier, this );
                }
            } else {
                // try all endpoints
                for( String epCandidate : endpointCandidates ) {
                    try {
                        checkCredentialStatelessMode( request, subject, siteIdentifier, epCandidate ); // aka dumb

                        t = null; // we found one that worked
                        break;

                    } catch( LidInvalidCredentialException ex ) {
                        if( log.isDebugEnabled() ) {
                            log.debug( t );
                        }
                        if( t == null ) {
                            t = ex;
                        }
                    }
                }
                if( t != null ) {
                    throw t;
                }
            }

        } else {
            if( !association.isCurrentlyValid() ) {
                theAssociationManager.remove( association.getServerUrl() );
                throw new OpenIdAssociationExpiredException( subject.getIdentifier(), siteIdentifier, this );
            }
            checkCredentialStatefulMode( request, subject, siteIdentifier, mandatoryFields, association ); // aka not dumb
        }
    }

    /**
     * Determine whether the request contains a valid OpenID stateless ("dumb") assertion
     * for the given subject.
     *
     * @param request the request
     * @param subject the subject
     * @param siteIdentifier the site at which the invalid credential was provided
     * @param epUrl the endpoint URL
     * @throws LidExpiredCredentialException thrown if the contained LidCdedentialType has expired
     * @throws LidInvalidCredentialException thrown if the contained LidCdedentialType is not valid for this subject
     */
    protected void checkCredentialStatelessMode(
            SaneRequest    request,
            HasIdentifier  subject,
            Identifier     siteIdentifier,
            String         epUrl )
        throws
            LidInvalidCredentialException
    {
        StringBuilder sentContentBuf = new StringBuilder( 512 );

        sentContentBuf.append( "openid.mode=" ).append( "check_authentication" );

        for( Entry<String,String[]> arg : request.getUrlArguments().entrySet() ) {
            String key = arg.getKey();
            if( !"openid.mode".equals( key )) {
                String [] values = arg.getValue();
                for( String value : values ) {
                    sentContentBuf.append( '&' ).append( key ).append( '=' );
                    sentContentBuf.append( HTTP.encodeToValidUrlArgument( value ));
                }
            }
        }

        HTTP.Response response = null;
        String        sentContent = sentContentBuf.toString(); // this makes debugging easier

        try {
            byte [] payload = sentContent.getBytes( "US-ASCII" );
            response = HTTP.http_post( epUrl, "application/x-www-form-urlencoded", payload, false );

        } catch( IOException ex ) {
            throw new OpenIdCannotValidateStatelessException( subject.getIdentifier(), siteIdentifier, this, ex );
        }

        String  mode              = null;
        boolean is_valid          = false;
        String  invalidate_handle = null;

        String receivedContent = response.getContentAsString();
        StringTokenizer token1 = new StringTokenizer( receivedContent, "\n" );
        while( token1.hasMoreElements() ) {
            String pair = token1.nextToken();
            int    colon = pair.indexOf( ':' );
            if( colon >=0  ) {
                String key   = pair.substring( 0, colon );
                String value = pair.substring( colon+1 );

                if( "openid.mode".equals( key ) || "mode".equals( key )) {
                    mode = value;
                } else if( "is_valid".equals( key )) {
                    is_valid = "true".equals( value );
                } else if( "invalidate_handle".equals( key )) {
                    invalidate_handle = value;
                } else {
                    log.warn( "When talking to " + epUrl + ", received unknown key-value pair " + key + " -> " + value );
                }
            }
        }
        if( ( mode != null && !"id_res".equals( mode )) || !is_valid ) {
            // OpenID V1 says mode=id_res, V2 does not mention mode
            throw new OpenIdInvalidSignatureException( subject.getIdentifier(), siteIdentifier, this );
        }
        if( invalidate_handle != null ) {
            OpenIdRpSideAssociation assocCandidate = theAssociationManager.get( epUrl );
            if( invalidate_handle.equals( assocCandidate.getAssociationHandle() ) ) {
                theAssociationManager.remove( assocCandidate.getServerUrl() );
            }
        }
    }

    /**
     * Determine whether the request contains a valid OpenID stateful assertion
     * for the given subject.
     *
     * @param request the request
     * @param subject the subject
     * @param siteIdentifier the site at which the invalid credential was provided
     * @param mandatoryFields set of fields that are mandatory
     * @param association the found, valid association
     * @throws LidExpiredCredentialException thrown if the contained LidCdedentialType has expired
     * @throws LidInvalidCredentialException thrown if the contained LidCdedentialType is not valid for this subject
     */
    protected void checkCredentialStatefulMode(
            SaneRequest             request,
            HasIdentifier           subject,
            Identifier              siteIdentifier,
            HashSet<String>         mandatoryFields,
            OpenIdRpSideAssociation association )
        throws
            LidExpiredCredentialException,
            LidInvalidCredentialException
    {
        String signed    = request.getUrlArgument( OPENID_SIGNED_PARAMETER_NAME );
        String signature = request.getUrlArgument( OPENID_SIGNATURE_PARAMETER_NAME );

        @SuppressWarnings("unchecked")
        HashSet<String> mandatory = mandatoryFields != null ? (HashSet<String>) mandatoryFields.clone() : null;

        StringBuilder toSign1 = new StringBuilder( 256 );

        StringTokenizer tokenizer = new StringTokenizer( signed, "," );
        while( tokenizer.hasMoreTokens() ) {
            String field = tokenizer.nextToken();
            String value = request.getUrlArgument( "openid." + field );

            if( mandatory != null ) {
                mandatory.remove( field );
            }

            toSign1.append( field ).append( ":" ).append( value );
            toSign1.append( "\n" );
        }
        if( mandatory != null && !mandatory.isEmpty() ) {
            throw new OpenIdMandatorySignedFieldMissingException(
                    ArrayHelper.copyIntoNewArray( mandatory, String.class ),
                    subject.getIdentifier(),
                    siteIdentifier,
                    this );
        }
        String toSign1String = toSign1.toString();

        byte [] hmac;
        try {
            if( OpenIdRpSideAssociationNegotiationParameters.DH_SHA256.equals( association.getSessionType() )) {
                hmac = CryptUtils.calculateHmacSha256( association.getSharedSecret(), toSign1String.getBytes( "UTF-8" ));
            } else {
                hmac = CryptUtils.calculateHmacSha1( association.getSharedSecret(), toSign1String.getBytes( "UTF-8" ));
            }
        } catch( UnsupportedEncodingException ex ) {
            log.error( ex );
            throw new OpenIdInvalidSignatureException( subject.getIdentifier(), siteIdentifier, this ); // gotta do something
        }
        String locallySigned = Base64.base64encodeNoCr( hmac );

        if( !locallySigned.equals( signature )) {
            throw new OpenIdInvalidSignatureException( subject.getIdentifier(), siteIdentifier, this );
        }
    }

    /**
     * Determine whether this LidCredentialType is a one-time token credential, e.g.
     * a one-time password.
     *
     * @return true if this is a one-time token credential
     */
    public boolean isOneTimeToken()
    {
        return true; // because of the nonce
    }

    /**
     * Determine the endpoint URLs that support authentication for this credential type, for this subject.
     *
     * @param subject the subject
     * @return the endpoint URLs
     */
    protected abstract String [] determineOpenIdEndpointsFor(
            HasIdentifier subject );

    /**
     * The association manager to use.
     */
    protected OpenIdRpSideAssociationManager theAssociationManager;

    /**
     * The NonceManager to use.
     */
    protected LidNonceManager theNonceManager;
}
