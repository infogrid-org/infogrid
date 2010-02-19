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

/**
 * Represents the OpenID authentication credential type in OpenID Authentication V2.
 */
public interface OpenId2CredentialType
        extends
            OpenIdCredentialType
{
    /**
     * Name of the URL parameter that contains the OpenID identifier.
     */
    public static final String OPENID2_IDENTIFIER_PARAMETER_NAME = "openid.identity";

    /**
     * Name of the URL parameter that indicates the OpenID namespace as defined in the
     * OpenID Authentication V2 specification.
     */
    public static final String OPENID_NS_PARAMETER_NAME = "openid.ns";

    /**
     * NS value that indicates OpenID Authentication V2.
     */
    public static final String OPENID_AUTHV2_VALUE = "http://specs.openid.net/auth/2.0";
}
