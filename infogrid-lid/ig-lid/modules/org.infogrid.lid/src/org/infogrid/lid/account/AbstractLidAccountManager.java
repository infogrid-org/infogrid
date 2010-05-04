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

import java.text.ParseException;
import java.util.Map;
import org.infogrid.lid.credential.LidCredentialType;
import org.infogrid.util.CannotFindHasIdentifierException;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.Identifier;
import org.infogrid.util.IdentifierFactory;
import org.infogrid.util.InvalidIdentifierException;
import org.infogrid.util.SimpleStringIdentifierFactory;
import org.infogrid.util.http.SaneRequest;

/**
 * Factors out common functionality of LidAccountManagers.
 */
public abstract class AbstractLidAccountManager
        implements
            LidAccountManager
{
    /**
     * Constructor for subclasses only.
     *
     * @param siteIdentifier identifier of the site at which the accounts are managed
     */
    protected AbstractLidAccountManager(
            Identifier siteIdentifier )
    {
        this( siteIdentifier, SimpleStringIdentifierFactory.create());
    }

    /**
     * Constructor for subclasses only.
     *
     * @param siteIdentifier identifier of the site at which the accounts are managed
     * @param identifierFactory the IdentifierFactory to use
     */
    protected AbstractLidAccountManager(
            Identifier        siteIdentifier,
            IdentifierFactory identifierFactory )
    {
        theSiteIdentifier    = siteIdentifier;
        theIdentifierFactory = identifierFactory;
    }

    /**
     * Find the resource requested by this incoming request.
     *
     * @param request the incoming request
     * @return the found resource
     * @throws ParseException thrown if the identifier in the request could not be parsed
     */
    public HasIdentifier findFromRequest(
            SaneRequest request )
        throws
            CannotFindHasIdentifierException,
            InvalidIdentifierException,
            ParseException
    {
        String        identifier = request.getAbsoluteBaseUri();
        Identifier    realId     = theIdentifierFactory.fromExternalForm( identifier );
        HasIdentifier ret        = find( realId );
        return ret;
    }

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
            LidAccountExistsAlreadyException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Delete a LidAccount. This overridable method always throws
     * UnsupportedOperationException.
     *
     * @param toDelete the LidAccount that will be deleted
     * @throws UnsupportedOperationException thrown if this LidAccountManager does not permit the deletion of LidAccounts
     */
    public void delete(
            LidAccount toDelete )
        throws
            UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Identifier of the site at which the accounts are managed.
     */
    protected Identifier theSiteIdentifier;

    /**
     * Factory for Identifiers.
     */
    protected IdentifierFactory theIdentifierFactory;
}
