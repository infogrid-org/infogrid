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

package org.infogrid.lid.account;

import java.util.Map;
import org.infogrid.lid.LidResourceFinder;
import org.infogrid.lid.credential.LidCredentialType;
import org.infogrid.util.CannotFindHasIdentifierException;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.HasIdentifierFinder;
import org.infogrid.util.Identifier;
import org.infogrid.util.InvalidIdentifierException;

/**
 * Manages locally provisioned accounts.
 */
public interface LidAccountManager
        extends
            HasIdentifierFinder,
            LidResourceFinder
{
    /**
     * Obtain a HasIdentifier, given its Identifier. This will either return a LidAccount
     * or not. If it returns a LidAccount, the identifier referred to that locally provisioned
     * LidAccount. If it returns something other than a LidAccount, it refers to a remote
     * persona. To determine the LidAccount that may be associated with the remote persona,
     * call determineLidAccountFromRemotePersona.
     *
     * @param identifier the Identifier for which the HasIdentifier will be retrieved
     * @return the found HasIdentifier
     * @throws CannotFindHasIdentifierException thrown if the HasIdentifier cannot be found
     * @throws InvalidIdentifierException thrown if the provided Identifier was invalid for this HasIdentifierFinder
     */
    public abstract HasIdentifier find(
            Identifier identifier )
        throws
            CannotFindHasIdentifierException,
            InvalidIdentifierException;

    /**
     * Given a remote persona and a site, determine the LidAccount that has been provisioned for
     * the remote persona at the site. May return null if none has been provisioned.
     * 
     * @param remote the remote persona
     * @param siteIdentifier identifier of the site at which the account has been provisioned
     * @return the found LidAccount, or null
     */
    public abstract LidAccount determineLidAccountFromRemotePersona(
            HasIdentifier remote,
            Identifier    siteIdentifier );

    /**
     * Provision a LidAccount.
     *
     * @param localIdentifier the Identifier for the to-be-created LidAccount. This may be null, in which case
     *        the LidAccountManager assigns a localIdentifier
     * @param remotePersonas the remote personas to be associated with the locally provisioned LidAccount
     * @param attributes the attributes for the to-be-created LidAccount
     * @param credentials the credentials for the to-be-created LidAccount
     * @param groupIdentifiers the Identifiers of the groups that this LidAccount belongs to
     * @return the LidAccount that was created
     * @throws LidAccountExistsAlreadyException thrown if a LidAccount with this Identifier exists already
     */
    public LidAccount provisionAccount(
            Identifier                    localIdentifier,
            HasIdentifier []              remotePersonas,
            Map<String,String>            attributes,
            Map<LidCredentialType,String> credentials,
            Identifier []                 groupIdentifiers )
        throws
            LidAccountExistsAlreadyException;

    /**
     * Delete a LidAccount. This overridable method always throws
     * UnsupportedOperationException.
     *
     * @param toDelete the LidAccount that will be deleted
     * @throws UnsupportedOperationException thrown if this LidAccountManager does not permit the deletion of LidAccounts
     */
    public void delete(
            LidAccount toDelete );
}
