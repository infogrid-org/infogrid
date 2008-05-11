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

package org.infogrid.lid.userpass;

import org.infogrid.context.Context;
import org.infogrid.lid.AbstractLidClientSignature;
import org.infogrid.mesh.MeshObject;

import org.infogrid.util.http.SaneRequest;

/**
 * A username/password ClientSignature.
 */
public class UserPassClientSignature
        extends
            AbstractLidClientSignature
{
    /**
     * Constructor.
     */
    public UserPassClientSignature(
            SaneRequest request,
            String     identifier,
            String     credential,
            String     lidCookieString,
            String     sessionId,
            String     target,
            String     nonce,
            Context    context )
    {
        super( request, identifier, credential, lidCookieString, sessionId, target, nonce, context );
    }

    /**
     * Obtain the credential type provided by the client, if any.
     *
     * @return the credential type provided by the client, or null
     */
    public String getCredentialType()
    {
        return USER_PASS_CREDENTIAL_TYPE;
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
    {
        throw new UnsupportedOperationException();
    }


    /**
     * The name of the simple password credential type.
     */
    public static final String USER_PASS_CREDENTIAL_TYPE = "simple-password";
}