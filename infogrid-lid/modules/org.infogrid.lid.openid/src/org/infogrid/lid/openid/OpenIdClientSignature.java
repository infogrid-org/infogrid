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
// Copyright 1998-2008 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.lid.openid;

import org.infogrid.lid.AbstractLidClientSignature;

import org.infogrid.context.Context;

import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.set.ByTypeMeshObjectSelector;
import org.infogrid.mesh.set.MeshObjectSet;
import org.infogrid.mesh.set.m.ImmutableMMeshObjectSet;

import org.infogrid.lid.yadis.YadisSubjectArea;

import org.infogrid.util.Base64;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.util.logging.Log;

import java.util.Map;
import java.util.StringTokenizer;

import java.io.IOException;

/**
 * An OpenId-based ClientSignature.
 */
public class OpenIdClientSignature
        extends
            AbstractLidClientSignature
{
    private static final Log log = Log.getLogInstance( OpenIdClientSignature.class ); // our own, private logger
    
    /**
     * Constructor. Use factory method.
     */
    public OpenIdClientSignature(
            SaneRequest         request,
            String             identifier,
            String             openIdSignature,
            String             lidCookieString,
            String             sessionId,
            String             target,
            String             nonce,
            String             openIdMode,
            String             openIdAssocHandle,
            String             openIdSigned,
            Map<String,String> openIdFields,
            Context            context )
    {
        super( request, identifier, openIdSignature, lidCookieString, sessionId, target, nonce, context );

        theOpenIdMode              = openIdMode;
        theOpenIdAssociationHandle = openIdAssocHandle;
        theOpenIdSigned            = openIdSigned;
        theOpenIdFields            = openIdFields;
    }

    /**
     * Obtain the credential type provided by the client, if any.
     *
     * @return the credential type provided by the client, or null
     */
    public String getCredentialType()
    {
        return OPENID_CREDENTIAL_TYPE;
    }

    /**
     * Is this Request signed. Does not imply that the signature was valid.
     *
     * @return true if the request is signed
     */
    @Override
    public boolean isSignedRequest()
    {
        return theCredential != null && "id_res".equals( theOpenIdMode );
    }

    /**
      * The internal method that determines whether or not a request was signed, and if so,
      * whether the signature is any good.
      *
      * @return true if the request was signed validly
      * @throws AbortProcessingException thrown if an error occurred
      */
    protected MeshObject determineSignedGoodRequest(
            MeshObject persona )
        throws
            IOException
    {
        MeshObjectSet openIdServices = persona.getMeshBase().getMeshObjectSetFactory().createImmutableMeshObjectSet(
                persona.traverse( YadisSubjectArea.SITE_MAKESUSEOF_SERVICE.getSource() ),
                ByTypeMeshObjectSelector.create( OpenidSubjectArea.AUTHENTICATIONSERVICE ));

        if( openIdServices.isEmpty() ) {
            return null;
        }
        MeshObjectSet endpoints = openIdServices.traverse( YadisSubjectArea.SERVICE_ISPROVIDEDATENDPOINT_SITE.getSource() );

        RelyingPartySideAssociationManager theAssociationManager = theContext.findContextObject( RelyingPartySideAssociationManager.class );
        if( theAssociationManager == null ) {
            log.error( "Cannot find RelyingPartySideAssociationManager in the context: OpenID validation not possible" );
            return null;
        }

        RelyingPartySideAssociation theAssociation = null;
        MeshObject                  foundEndpoint  = null;

        for( MeshObject currentEndpoint : endpoints ) {
            String endpointIdentifier = currentEndpoint.getIdentifier().toExternalForm();
            theAssociation = theAssociationManager.get( endpointIdentifier );
            if( theAssociation == null ) {
                continue;
            }
            if(    !theAssociation.isCurrentlyValid()
                || !theAssociation.getAssociationHandle().equals( theOpenIdAssociationHandle ) )
            {
                theAssociationManager.remove( endpointIdentifier );
                theAssociation = null;
            } else {
                foundEndpoint = currentEndpoint;
                break;
            }
        }
        if( theAssociation == null ) {
            return null; // we don't do dumb mode
        }

        // FIXME nonces
        // if( theRemotePersona != null && !theRemotePersona.isNewNonce() ) {
        //     return false;
        // }

        StringBuffer toSign1 = new StringBuffer( 256 );

        StringTokenizer tokenizer = new StringTokenizer( theOpenIdSigned, "," );
        while( tokenizer.hasMoreTokens() ) {
            String field = tokenizer.nextToken();
            String value = (String) theOpenIdFields.get( "openid." + field );

            toSign1.append( field ).append( ":" ).append( value );
            toSign1.append( "\n" );
        }
        String toSign1String = toSign1.toString();

        byte [] hmacSha1      = CryptUtils.calculateHmacSha1( theAssociation.getSharedSecret(), toSign1String.getBytes() );
        String  locallySigned = Base64.base64encodeNoCr( hmacSha1 );

        if( locallySigned.equals( theCredential )) {
            return foundEndpoint;
        } else {
            return null;
        }
    }

    /**
     * Value of the OpenIdSignature mode parameter.
     */
    protected String theOpenIdMode;

    /**
     * Value of the OpenIdSignature Association Handle parameter.
     */
    protected String theOpenIdAssociationHandle;

    /**
     * Value of the OpenIdSignature signed parameter.
     */
    protected String theOpenIdSigned;

    /**
     * Map of OpenIdSignature fields as they came in, in order to make signature validation easier.
     */
    protected Map<String,String> theOpenIdFields;

    /**
     * The name of the default OpenIdSignature credential type.
     */
    public static final String OPENID_CREDENTIAL_TYPE = "net.openid.credentialtype.HMAC-SHA1";
}
    