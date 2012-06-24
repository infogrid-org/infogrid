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

import org.infogrid.lid.account.LidAccount;
import org.infogrid.lid.credential.LidCredentialType;
import org.infogrid.lid.credential.LidInvalidCredentialException;
import org.infogrid.lid.session.LidSession;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.Identifier;

/**
 * Collects in one place all information known about the current client and how the client authenticated.
 */
public interface LidClientAuthenticationStatus
{
    /**
     * Determine the Identifier of the site to which this LidClientAuthenticationStatus belongs.
     *
     * @return the Identifier of the site
     */
    public abstract Identifier getSiteIdentifier();

    /**
     * <p>Returns true if the client of this request did not present any kind of identification.</p>
     * 
     * @return true if the client of this request did not present any kind of identification
     */
    public abstract boolean isAnonymous();

    /**
     * <p>Returns true of the client of this request claimed an identifier that could not be resolved.</p>
     *
     * @return true if the client claimed an identifier as part of this request that could not be resolved
     */
    public abstract boolean isInvalidIdentity();

    /**
     * <p>Returns true if the client of this request merely claimed an identifier, but offered no valid credential
     *    (not even an expired cookie) to back up the claim.</p>
     * <p>Also returns true if a session id was offered (e.g. via a cookie) but the session id was unrecognized.
     *    It will return false if the session id was recognized, even if the session expired earlier.</p>
     * <p>Also returns true if an authentication attempt was made in this request, but the authentication
     *    attempted failed (e.g. wrong password). If the authentication attempt succeeded, this returns false as
     *    the identifier is not &quot;claimed only&quot; any more. To determine whether or not an authentication
     *    attempt was made, use {@link #isCarryingValidCredential}.</p>
     * 
     * @return true if client merely claimed an identifier as part of this request and no credential was offered
     */
    public abstract boolean isClaimedOnly();
    
    /**
     * <p>Returns true if the client of this request merely presented an identifier and an expired session id (e.g.
     *    via a cookie) as  credential to back up the claim.</p>
     * <p>For this to return true, the session id must have been valid in the past. If the session id is not recognized,
     *    or the session id is still valid, this will return false.</p>
     * 
     * @return true if client merely provided an expired session id as a credential for this request
     */
    public abstract boolean isExpiredSessionOnly();
    
    /**
     * <p>Returns true if the client of this request was authenticated using a still-valid session identifier only
     *    (e.g. via cookie) and no stronger valid credential was offered as part of the request.</p>
     * <p>Also returns true if a stronger credential was offered in addition to the still-valid session cookie, but the
     *    stronger credential was invalid. To determine whether or not such an attempt was made, use
     *    {@link #isCarryingValidCredential}.</p>
     * 
     * @return true if client merely provided a valid session cookie as a credential for this request
     */
    public abstract boolean isValidSessionOnly();
    
    /**
     * <p>Determine whether the client of this request offered a valid credential stronger than a session id
     *    for this request. To determine which valid credential type or types were offered, see
     *    {@link #getCarriedValidCredentialTypes}.</p>
     * 
     * @return true if the client provided a valid credential for this request that is stronger than a session identifier
     */
    public abstract boolean isCarryingValidCredential();
    
    /**
     * <p>Determine whether the client of this request offered an expired credential stronger than a session id
     *    for this request. To determine which expired credential type or types were offered, see
     *    {@link #getCarriedExpiredCredentialTypes}.</p>
     *
     * @return true if the client provided an expired credential for this request that is stronger than a session identifier
     */
    public abstract boolean isCarryingExpiredCredential();
    
    /**
     * <p>Determine whether the client of this request offered an invalid credential stronger than a session id
     *    for this request. To determine which invalid credential type or types were offered, see
     *    {@link #getCarriedValidCredentialTypes}.</p>
     * 
     * @return true if the client provided an invalid credential for this request that is stronger than a session identifier
     */
    public abstract boolean isCarryingInvalidCredential();
    
    /**
     * <p>Determine the set of credential types stronger than a session id that were offered by the
     *    client for this request and that were valid.</p>
     * <p>This returns null if none such credential type was offered, regardless of whether any were valid or not.
     *    It returns an empty array if at least one credential type was offered, but none were valid.</p>
     * <p>For example, if a request carried 5 different credential types, of which 3 validated and 2 did not, this method
     *    would return the 3 validated credential types.</p>
     *
     * @return the types of validated credentials provided by the client for this request, or null if none
     * @see #getCarriedInvalidCredentialTypes
     * @see #getCarriedExpiredCredentialTypes
     */
    public abstract LidCredentialType [] getCarriedValidCredentialTypes();

    /**
     * <p>Determine the set of credential types stronger than a session id that were offered by the
     *    client for this request and that were expired.</p>
     * <p>This returns null if none such credential type was offered, regardless of whether any were valid or not.
     *    It returns an empty array if at least one credential type was offered, but none were valid.</p>
     * <p>For example, if a request carried 5 different credential types, of which 2 validated and 1 used to be valid
     *    but is not any more, and 2 did not validate, this method would return the 1 validated credential type.</p>
     *
     * @return the types of expired credentials provided by the client for this request, or null if none
     * @see #getCarriedValidCredentialTypes
     * @see #getCarriedInvalidCredentialTypes
     */
    public abstract LidCredentialType [] getCarriedExpiredCredentialTypes();

    /**
     * <p>Determine the set of credential types stronger than a session id that  were offered by the
     *    client for this request and that were not valid.</p>
     * <p>This returns null if non such credential type was offered, regardless of whether any were valid or not.
     *    It returns an empty array if at least one credential type was offered, and all were valid.</p>
     * <p>For example, if a request carried 5 different credential types, of which 3 validated and 2 did not, this method
     *    would return the 2 invalid credential types.</p>
     * 
     * @return the types of invalid credentials provided by the client for this request, or null if none
     * @see #getCarriedValidCredentialTypes
     * @see #getCarriedExpiredCredentialTypes
     */
    public abstract LidCredentialType [] getCarriedInvalidCredentialTypes();

    /**
     * <p>Obtain the set of LidInvalidCredentialExceptions that correspond to the carried invalid credential types.
     *    By making those available, user-facing error reporting is more likely going to be more useful as
     *    different subclasses of LidInvalidCredentialException can report different error messages.</p>
     * 
     * @return the LidInvalidCredentialExceptions, in the same sequence as getCarriedInvalidCredentialTypes
     * @see #getCarriedInvalidCredentialTypes
     */
    public abstract LidInvalidCredentialException [] getInvalidCredentialExceptions();
    
    /**
     * Obtain the String that was entered by the client as the identifier as the client. If parsing this String
     * was successful, getClientIdentifier() will also return non-null.
     *
     * @return the claimed client identifier as entered by the client
     */
    public abstract String getClientIdentifierAsEntered();

    /**
     * Obtain the identifier provided by the client. To determine whether to trust that the client indeed
     * owns this identifier, other methods need to be consulted. This method makes no statement 
     * about trustworthiness. If this returns a non-null value, and {@link #getClientAccount} returns
     * <code>null</code>, this indicates that the client persona is not known or not valid.
     * 
     * @return the claimed client identifier
     * @see #getClientAccount
     */
    public abstract Identifier getClientIdentifier();

    /**
     * Obtain the client's remote persona, if the clientIdentifier refers to one. If there is none,
     * or if the remote persona could not be resolved, this will return <code>null</code>.
     *
     * @return the remote persona
     */
    public abstract HasIdentifier getClient();

    /**
     * Obtain the client's possible local LidAccounts, if there are any. If there is none, or if the LidAccount
     * could not be resolved, this will return <code>null</code>.
     *
     * @return the LidAccounts
     * @see #getClientIdentifier
     */
    public abstract LidAccount [] getClientAccounts();

    /**
     * Obtain the client's possible local LidAccounts' identifiers, if there are any. If there is no LidAccount, or the
     * LidAccount could not be resolved, this will return <code>null</code>.
     *
     * @return the LidAccount's identifier
     */
    public abstract Identifier [] getClientAccountsIdentifiers();

    /**
     * Determine whether the client has indicated its desire to log in.
     *
     * @return true if the client wishes to log in
     */
    public abstract boolean clientWishesToLogin();

    /**
     * Determine whether the client has indicated its desire to cancel the active session, if any.
     * This does not mean the client wishes to become anonymous but that the client wishes to move from authenticated
     * status to claimed only.
     * 
     * @return true if the client wishes to cancel the active session.
     */
    public abstract boolean clientWishesToCancelSession();

    /**
     * Determine whether the client just logged on.
     *
     * @return true if the client just logged on
     */
    public abstract boolean clientLoggedOn();

    /**
     * Determine whether the client has indicated its desire to log out if the active session, if any.
     * This means the client wishes to become anonymous.
     *
     * @return true of the client wishes to become anonymous again
     */
    public abstract boolean clientWishesToLogout();

    /**
     * Determine whether the client has just successfully presented a one-time token, such as one sent
     * by e-mail during a password reset.
     *
     * @return true if the client has successfully presented a one-time token
     */
    public abstract boolean clientPresentsOneTimeToken();
    
    /**
     * Get the pre-existing client session, if any. This is only set if the session is owned by a client other than
     * the current client (e.g. if a new user uses a browser with an existing valid session cookie). The
     * session may be valid or expired. It may be null.
     *
     * @return the pre-existing client session, if any
     */
    public abstract LidSession getPreexistingClientSession();

    /**
     * Obtain the authentication services available for this client, if any.
     * This will return recommended authentication services first.
     *
     * @return the authentication services
     */
    public abstract LidAuthenticationService [] getAuthenticationServices();

    /**
     * Convenience method to determine whether the client is authenticated.
     * This aggregates information from the other calls.
     *
     * @return true if the client is authenticated
     */
    public abstract boolean isAuthenticated();

    /**
     * Convenience method to determine whether the client has been authenticated within a certain time period.
     * This aggregates information from the other calls.
     *
     * @param period the time period in milliseconds
     * @return true if the client has been authenticated within the time period
     */
    public abstract boolean isAuthenticatedWithin(
            long period );
}
