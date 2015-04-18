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

package org.infogrid.lid;

import java.util.Collection;
import org.infogrid.lid.account.LidAccount;
import org.infogrid.lid.credential.LidCredentialType;
import org.infogrid.lid.credential.LidInvalidCredentialException;
import org.infogrid.lid.session.LidSession;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.Identifier;

/**
 * A simple implementation of LidClientAuthenticationStatus.
 */
public class SimpleLidClientAuthenticationStatus
        extends
            AbstractLidClientAuthenticationStatus
{
    /**
     * Factory method.
     * 
     * @param clientIdentifierAsEntered String that was entered as the client identifier by the client, if any
     * @param clientIdentifier the normalized identifier provided by the client, if any
     * @param clientRemotePersona the client's remote persona, if used
     * @param clientAccounts the client's locally available LidAccounts, if any
     * @param preexistingClientSession the LidSession that existed prior to this request, if any
     * @param carriedValidCredentialTypes the credential types carried as part of this request that validated successfully, if any
     * @param carriedExpiredCredentialTypes the credential types carried as part of this request that were expired, if any
     * @param carriedInvalidCredentialTypes the credential types carried as part of this request that did not validate successfully, if any
     * @param invalidCredentialExceptions the exceptions indicating the problems with the invalid credentials, in the same sequence, if any
     * @param clientLoggedOn the client just logged on
     * @param clientWishesToLogin the client wishes to log in
     * @param wishesCancelSession the client wishes to cancel the session
     * @param clientWishesToLogout the client wishes to log out
     * @param authenticationServices the authentication services available to this client, if any
     * @param siteIdentifier identifies the site at which this status applies
     * @return the created SimpleLidClientAuthenticationStatus
     */
    public static SimpleLidClientAuthenticationStatus create(
            String                           clientIdentifierAsEntered,
            Identifier                       clientIdentifier,
            HasIdentifier                    clientRemotePersona,
            LidAccount []                    clientAccounts,
            LidSession                       preexistingClientSession,
            LidCredentialType []             carriedValidCredentialTypes,
            LidCredentialType []             carriedExpiredCredentialTypes,
            LidCredentialType []             carriedInvalidCredentialTypes,
            LidInvalidCredentialException [] invalidCredentialExceptions,
            boolean                          clientLoggedOn,
            boolean                          clientWishesToLogin,
            boolean                          wishesCancelSession,
            boolean                          clientWishesToLogout,
            LidAuthenticationService []      authenticationServices,
            Identifier                       siteIdentifier )
    {
        SimpleLidClientAuthenticationStatus ret = new SimpleLidClientAuthenticationStatus(
                clientIdentifierAsEntered,
                clientIdentifier,
                clientRemotePersona,
                clientAccounts,
                preexistingClientSession,
                carriedValidCredentialTypes,
                carriedExpiredCredentialTypes,
                carriedInvalidCredentialTypes,
                invalidCredentialExceptions,
                clientLoggedOn,
                clientWishesToLogin,
                wishesCancelSession,
                clientWishesToLogout,
                authenticationServices,
                siteIdentifier );
        
        return ret;
    }

    /**
     * Factory method.
     *
     * @param clientIdentifierAsEntered String that was entered as the client identifier by the client, if any
     * @param clientIdentifier the normalized identifier provided by the client, if any
     * @param clientRemotePersona the client's remote persona, if used
     * @param clientAccounts the client's locally available LidAccounts, if any
     * @param preexistingClientSession the LidSession that existed prior to this request, if any
     * @param carriedValidCredentialTypes the credential types carried as part of this request that validated successfully, if any
     * @param carriedExpiredCredentialTypes the credential types carried as part of this request that were expired, if any
     * @param carriedInvalidCredentialTypes the credential types carried as part of this request that did not validate successfully, if any
     * @param invalidCredentialExceptions the exceptions indicating the problems with the invalid credentials, in the same sequence, if any
     * @param clientLoggedOn the client just logged on
     * @param clientWishesToLogin the client wishes to log in
     * @param wishesCancelSession the client wishes to cancel the session
     * @param clientWishesToLogout the client wishes to log out
     * @param authenticationServices the authentication services available to this client, if any
     * @param siteIdentifier identifies the site at which this status applies
     * @return the created SimpleLidClientAuthenticationStatus
     */
    public static SimpleLidClientAuthenticationStatus create(
            String                                    clientIdentifierAsEntered,
            Identifier                                clientIdentifier,
            HasIdentifier                             clientRemotePersona,
            LidAccount []                             clientAccounts,
            LidSession                                preexistingClientSession,
            Collection<LidCredentialType>             carriedValidCredentialTypes,
            Collection<LidCredentialType>             carriedExpiredCredentialTypes,
            Collection<LidCredentialType>             carriedInvalidCredentialTypes,
            Collection<LidInvalidCredentialException> invalidCredentialExceptions,
            boolean                                   clientLoggedOn,
            boolean                                   clientWishesToLogin,
            boolean                                   wishesCancelSession,
            boolean                                   clientWishesToLogout,
            LidAuthenticationService []               authenticationServices,
            Identifier                                siteIdentifier )
    {
        SimpleLidClientAuthenticationStatus ret = SimpleLidClientAuthenticationStatus.create(
                clientIdentifierAsEntered,
                clientIdentifier,
                clientRemotePersona,
                clientAccounts,
                preexistingClientSession,
                carriedValidCredentialTypes != null
                        ? ArrayHelper.copyIntoNewArray( carriedValidCredentialTypes,   LidCredentialType.class )
                        : null,
                carriedExpiredCredentialTypes != null
                        ? ArrayHelper.copyIntoNewArray( carriedExpiredCredentialTypes,   LidCredentialType.class )
                        : null,
                carriedInvalidCredentialTypes != null
                        ? ArrayHelper.copyIntoNewArray( carriedInvalidCredentialTypes, LidCredentialType.class )
                        : null,
                invalidCredentialExceptions != null
                        ? ArrayHelper.copyIntoNewArray( invalidCredentialExceptions,   LidInvalidCredentialException.class )
                        : null,
                clientLoggedOn,
                clientWishesToLogin,
                wishesCancelSession,
                clientWishesToLogout,
                authenticationServices,
                siteIdentifier );

        return ret;
    }

    /**
     * Constructor for subclasses only.
     *
     * @param clientIdentifierAsEntered String that was entered as the client identifier by the client, if any
     * @param clientIdentifier the normalized identifier provided by the client, if any
     * @param clientRemotePersona the client's remote persona, if used
     * @param clientAccounts the client's locally available LidAccounts, if any
     * @param preexistingClientSession the LidSession that existed prior to this request, if any
     * @param carriedValidCredentialTypes the credential types carried as part of this request that validated successfully, if any
     * @param carriedExpiredCredentialTypes the credential types carried as part of this request that were expired, if any
     * @param carriedInvalidCredentialTypes the credential types carried as part of this request that did not validate successfully, if any
     * @param invalidCredentialExceptions the exceptions indicating the problems with the invalid credentials, in the same sequence, if any
     * @param clientLoggedOn the client just logged on
     * @param clientWishesToLogin the client wishes to log in
     * @param clientWishesToCancelSession the client wishes to cancel the session
     * @param clientWishesToLogout the client wishes to log out
     * @param authenticationServices the authentication services available to this client, if any
     * @param siteIdentifier identifies the site at which this status applies
     */
    protected SimpleLidClientAuthenticationStatus(
            String                           clientIdentifierAsEntered,
            Identifier                       clientIdentifier,
            HasIdentifier                    clientRemotePersona,
            LidAccount []                    clientAccounts,
            LidSession                       preexistingClientSession,
            LidCredentialType []             carriedValidCredentialTypes,
            LidCredentialType []             carriedExpiredCredentialTypes,
            LidCredentialType []             carriedInvalidCredentialTypes,
            LidInvalidCredentialException [] invalidCredentialExceptions,
            boolean                          clientLoggedOn,
            boolean                          clientWishesToLogin,
            boolean                          clientWishesToCancelSession,
            boolean                          clientWishesToLogout,
            LidAuthenticationService []      authenticationServices,
            Identifier                       siteIdentifier )
    {
        super(  clientIdentifierAsEntered,
                clientIdentifier,
                clientRemotePersona,
                clientAccounts,
                preexistingClientSession,
                carriedValidCredentialTypes,
                carriedExpiredCredentialTypes,
                carriedInvalidCredentialTypes,
                invalidCredentialExceptions,
                clientLoggedOn,
                clientWishesToLogin,
                clientWishesToCancelSession,
                clientWishesToLogout,
                authenticationServices,
                siteIdentifier );
    }
}
