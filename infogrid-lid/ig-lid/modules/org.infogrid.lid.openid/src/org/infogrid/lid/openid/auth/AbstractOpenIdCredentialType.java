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

package org.infogrid.lid.openid.auth;

import java.util.HashSet;
import java.util.StringTokenizer;
import org.infogrid.lid.nonce.LidNonceManager;
import org.infogrid.lid.credential.AbstractLidCredentialType;
import org.infogrid.lid.credential.LidExpiredCredentialException;
import org.infogrid.lid.credential.LidInvalidCredentialException;
import org.infogrid.lid.openid.CryptUtils;
import org.infogrid.lid.openid.OpenIdAssociationExpiredException;
import org.infogrid.lid.openid.OpenIdInvalidSignatureException;
import org.infogrid.lid.openid.OpenIdNoAssociationException;
import org.infogrid.lid.openid.OpenIdRpSideAssociation;
import org.infogrid.lid.openid.OpenIdRpSideAssociationManager;
import org.infogrid.lid.openid.OpenIdRpSideAssociationNegotiationParameters;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.Base64;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.http.SaneRequest;

/**
 * Common superclass for OpenID authentication credential types.
 */
public abstract class AbstractOpenIdCredentialType
        extends
            AbstractLidCredentialType
        implements
            OpenIdCredentialType
{
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
     * for the given subject.
     *
     * @param request the request
     * @param subject the subject
     * @param mandatoryFields set of fields that are mandatory
     * @param nonceParameterName name of the parameter representing the nonce
     * @throws LidExpiredCredentialException thrown if the contained LidCdedentialType has expired
     * @throws LidInvalidCredentialException thrown if the contained LidCdedentialType is not valid for this subject
     */
    protected void checkCredential(
            SaneRequest     request,
            HasIdentifier   subject,
            HashSet<String> mandatoryFields,
            String          nonceParameterName )
        throws
            LidExpiredCredentialException,
            LidInvalidCredentialException
    {
        String associationHandle = request.getUrlArgument( OPENID_ASSOC_HANDLE_PARAMETER_NAME );
        String signed            = request.getUrlArgument( OPENID_SIGNED_PARAMETER_NAME );
        String signature         = request.getUrlArgument( OPENID_SIGNATURE_PARAMETER_NAME );

        if( associationHandle == null || associationHandle.length() == 0 ) {
            // we don't do dumb mode
            throw new OpenIdNoAssociationException( subject.getIdentifier(), this );
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
            // association is expired, renegotiate. We don't do dumb mode but start again from scratch.
            
            // we don't do dumb mode
            throw new OpenIdAssociationExpiredException( subject.getIdentifier(), this );
        }
        if( !association.isCurrentlyValid() ) {
            theAssociationManager.remove( association.getServerUrl() );
            throw new OpenIdAssociationExpiredException( subject.getIdentifier(), this );
        }

        if( nonceParameterName != null ) {
            theNonceManager.validateNonce( request, subject.getIdentifier(), this, nonceParameterName ); // throws LidInvalidNonceException
        } else {
            theNonceManager.validateNonce( request, subject.getIdentifier(), this ); // throws LidInvalidNonceException
        }

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
                    this );
        }
        String toSign1String = toSign1.toString();

        byte [] hmac;
        if( OpenIdRpSideAssociationNegotiationParameters.DH_SHA256.equals( association.getSessionType() )) {
            hmac = CryptUtils.calculateHmacSha256( association.getSharedSecret(), toSign1String.getBytes() );
        } else {
            hmac = CryptUtils.calculateHmacSha1( association.getSharedSecret(), toSign1String.getBytes() );
        }
        String locallySigned = Base64.base64encodeNoCr( hmac );

        if( !locallySigned.equals( signature )) {
            throw new OpenIdInvalidSignatureException( subject.getIdentifier(), this );
        }
    }

    /**
     * Determine whether this LidCredentialType is a credential type that is about a remote persona.
     * E.g. an OpenID credential type would return true, while a password credential type would return false.
     *
     * @return true if it is about a remote persona
     */
    public boolean isRemote()
    {
        return true;
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
