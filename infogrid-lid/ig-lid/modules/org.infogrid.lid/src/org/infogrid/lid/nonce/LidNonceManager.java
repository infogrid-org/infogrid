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

package org.infogrid.lid.nonce;

import org.infogrid.lid.credential.LidCredentialType;
import org.infogrid.util.Identifier;
import org.infogrid.util.http.SaneRequest;

/**
 * Defines how to generate and validate LID nonces.
 */
public interface LidNonceManager
{
    /**
     * Generate a new nonce.
     * 
     * @return the newly generated nonce
     */
    public String generateNewNonce();

    /**
     * Validate a LID nonce contained in a request with the given URL parameter.
     *
     * @param request the request
     * @param identifier identifier of the client on whose behalf the nonce is checked
     * @param siteIdentifier the site at which the nonce is checked
     * @param type the LidCredentialType that used this nonce
     * @throws LidInvalidNonceException thrown if the nonce was unacceptable
     */
    public void validateNonce(
            SaneRequest       request,
            Identifier        identifier,
            Identifier        siteIdentifier,
            LidCredentialType type )
        throws
            LidInvalidNonceException;

    /**
     * Validate a LID nonce contained in a request with the given URL parameter.
     *
     * @param request the request
     * @param identifier identifier of the client on whose behalf the nonce is checked
     * @param siteIdentifier the site at which the nonce is checked
     * @param type the LidCredentialType that used this nonce
     * @param name the name of the URL parameter
     * @throws LidInvalidNonceException thrown if the nonce was unacceptable
     */
    public void validateNonce(
            SaneRequest       request,
            Identifier        identifier,
            Identifier        siteIdentifier,
            LidCredentialType type,
            String            name )
        throws
            LidInvalidNonceException;

    /**
     * Name of the URL parameter that indicates the LID nonce.
     */
    public static final String LID_NONCE_PARAMETER_NAME = "lid-nonce";
}
