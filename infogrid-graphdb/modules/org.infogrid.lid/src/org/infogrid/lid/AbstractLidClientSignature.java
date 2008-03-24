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

package org.infogrid.lid;

import org.infogrid.context.Context;
import org.infogrid.context.ObjectInContext;

import org.infogrid.mesh.MeshObject;
import org.infogrid.meshbase.MeshBase;

import org.infogrid.util.ResourceHelper;
import org.infogrid.util.http.SaneRequest;

import org.infogrid.util.logging.Log;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Captures the information provided by the client that is relevant to authenticate a request.
 */
public abstract class AbstractLidClientSignature
        implements
            LidClientSignature,
            ObjectInContext
{
    private static final Log log = Log.getLogInstance( AbstractLidClientSignature.class ); // our own, private logger

    /**
     * Constructor for subclasses only.
     */
    protected AbstractLidClientSignature(
            SaneRequest      request,
            String          identifier,
            String          credential,
            String          lidCookieString,
            String          sessionId,
            String          target,
            String          nonce,
            Context         context )
    {
        theRequest             = request;
        theIdentifier          = identifier;
        theCredential          = credential;
        theSaneCookieString     = lidCookieString;
        theSessionId           = sessionId;
        theTarget              = target;
        theNonce               = nonce;
        theContext             = context;
    }
    
    /**
     * The Context in which we operate.
     *
     * @return the Context
     */
    public final Context getContext()
    {
        return theContext;
    }

    /**
     * Obtain the SaneRequest from which this LidClientSignature was derived.
     * 
     * @return the LSaneRequest
     */
    public final SaneRequest getSaneRequest()
    {
        return theRequest;
    }

    /**
     * Obtain the identifier of the client.
     *
     * @return the identifier of the client
     */
    public String getIdentifier()
    {
        return theIdentifier;
    }

    /**
     * Obtain the value of the LID cookie, if any.
     *
     * @return the value of the LID cookie, if any
     */
    public String getSaneCookieString()
    {
        return theSaneCookieString;
    }

    /**
     * Determine whether this request causes a logout event.
     *
     * @return true if this causes a logout event, false otherwise
     */
    public boolean isLogoutEvent()
    {
        return theSaneCookieString != null && ( theIdentifier == null );
    }
    
    /**
     * Obtain the credential provided by the client, if any.
     *
     * @return the credential provided by the client, or null
     */
    public String getCredential()
    {
        return theCredential;
    }

    /**
     * Obtain the credential type provided by the client, if any.
     *
     * @return the credential type provided by the client, or null
     */
    public abstract String getCredentialType();

    /**
     * Is this Request signed. Does not imply that the signature was valid.
     *
     * @return true if the request is signed
     */
    public boolean isSignedRequest()
    {
        return theCredential != null;
    }

    /**
     * Is this Request signed, and if so, is the signature good.
     *
     * @return the endpoint MeshObject if the Request is signed and the signature is good, null otherwise
     * @throws AbortProcessingException thrown if an error occurred
     */
    public final MeshObject isSignedGoodRequest(
            MeshObject persona )
        throws
            IOException
    {
        if( !isSignedRequest() ) {
            return null;
        }
        MeshObject endpoint = determineSignedGoodRequest( persona );
        return endpoint;
     }

     /**
      * The internal method that determines whether or not a request was signed, and if so,
      * whether the signature is any good.
      *
      * @return the endpoint MeshObject if the Request is signed and the signature is good, null otherwise
      * @throws AbortProcessingException thrown if an error occurred
      */
    protected abstract MeshObject determineSignedGoodRequest(
            MeshObject persona )
        throws
            IOException;

    /**
     * Determine the session id that was provided as part of this Request,
     * regardless of whether it is valid. This class does not know whether or not
     * the session is valid anyway.
     *
     * @return the session id
     */
    public String getSessionId()
    {
        return theSessionId;
    }

    /**
     * Obtain the target that is part of this request, if any.
     *
     * @return the target
     */
    public String getTarget()
    {
        return theTarget;
    }

    /**
     * Obtain the nonce that is part of this request, if any.
     *
     * @return the nonce
     */
    public String getNonce()
    {
        return theNonce;
    }
    
    /**
     * Obtain the MeshObject that represents the identity provider used, if any.
     *
     * @return the identity provider
     */
    public MeshObject determineIdentityProvider(
            MeshBase mb )
    {
        throw new UnsupportedOperationException();
    }

    /**
     * The incoming SaneRequest.
     */
    protected SaneRequest theRequest;

    /**
     * The provided identifier of the client.
     */
    protected String theIdentifier;

    /**
     * The value of the LID cookie.
     */
    protected String theSaneCookieString;

    /**
     * The provided session id.
     */
    protected String theSessionId;

    /**
     * The target.
     */
    protected String theTarget;

    /**
     * The provided credential.
     */
    protected String theCredential;

    /**
     * The provided nonce.
     */
    protected String theNonce;

    /**
     * The Context in which we operate.
     */
    protected Context theContext;

    /**
     * The pattern for the LID V2 nonce.
     */
    protected static final Pattern theLidNoncePattern = Pattern.compile(
            "^(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{3})Z$" );

    /**
     * Our ResourceHelper.
     */
    protected static final ResourceHelper theResourceHelper = ResourceHelper.getInstance( AbstractLidClientSignature.class );

    /**
     * The maximum age of a nonce that we tolerate.
     */
    protected static long theMaxNonceAge = theResourceHelper.getResourceLongOrDefault( "MaxNonceAge", 60L*60L*1000L ); // 1 hour
    
    /**
     * The maximum amount of time we tolerate a nonce to be in the future.
     */
    protected static long theMaxNonceFuture = theResourceHelper.getResourceLongOrDefault( "MaxNonceAge", 5L*60L*1000L ); // 5 minutes
}
