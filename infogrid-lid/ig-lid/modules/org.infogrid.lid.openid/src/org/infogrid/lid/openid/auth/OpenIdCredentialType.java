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

import org.infogrid.lid.credential.LidCredentialType;

/**
 * Interface for all OpenID credential types
 */
public interface OpenIdCredentialType
        extends
            LidCredentialType
{
    /**
     * Name of the URL parameter that indicates the OpenID mode.
     */
    public static final String OPENID_MODE_PARAMETER_NAME = "openid.mode";

    /**
     * Value of the URL parameter that indicates the OpenID credential.
     */
    public static final String OPENID_MODE_IDRES_PARAMETER_VALUE = "id_res";

    /**
     * Name of the URL parameter that holds the association handle.
     */
    public static final String OPENID_ASSOC_HANDLE_PARAMETER_NAME = "openid.assoc_handle";

    /**
     * Name of the URL parameter that invalidates an association handle.
     */
    public static final String OPENID_INVALIDATE_HANDLE_PARAMETER_NAME = "openid.invalidate_handle";

    /**
     * Name of the URL parameter that holds the list of signed fields.
     */
    public static final String OPENID_SIGNED_PARAMETER_NAME = "openid.signed";

    /**
     * Name of the URL parameter that holds the signature.
     */
    public static final String OPENID_SIGNATURE_PARAMETER_NAME = "openid.sig";

    /**
     * Name of the URL parameter that contains the OpenID V2 nonce.
     */
    public static final String OPENID_NONCE_PARAMETER_NAME = "openid.response_nonce";
}
