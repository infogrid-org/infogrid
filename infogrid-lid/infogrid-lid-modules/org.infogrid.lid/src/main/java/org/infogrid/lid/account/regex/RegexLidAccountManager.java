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
// Copyright 1998-2015 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.lid.account.regex;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.infogrid.lid.account.AbstractLidAccountManager;
import org.infogrid.lid.account.LidAccount;
import org.infogrid.lid.account.SimpleLidAccount;
import org.infogrid.util.CannotFindHasIdentifierException;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.Identifier;
import org.infogrid.util.IdentifierFactory;
import org.infogrid.util.InvalidIdentifierException;

/**
 * A LidAccountManager that compares user name and password against regular expressions.
 * This is not likely to be useful in a production scenario, but can help during development
 * or testing.
 */
public class RegexLidAccountManager
        extends
            AbstractLidAccountManager
{
    /**
     * Factory method.
     *
     * @param siteIdentifier identifier of the site at which the accounts are managed
     * @param userNameRegex the user name regular expression
     * @param groupIdentifiers identifiers of the groups this LidAccount is a member of
     * @param identifierFactory the IdentifierFactory to use
     * @return the created RegexLidAccountManager
     */
    public static RegexLidAccountManager create(
            Identifier        siteIdentifier,
            String            userNameRegex,
            Identifier []     groupIdentifiers,
            IdentifierFactory identifierFactory )
    {
        RegexLidAccountManager ret = new RegexLidAccountManager(
                siteIdentifier,
                Pattern.compile( userNameRegex ),
                groupIdentifiers,
                identifierFactory );

        return ret;
    }

    /**
     * Factory method.
     *
     * @param siteIdentifier identifier of the site at which the accounts are managed
     * @param userNameRegex the user name regular expression
     * @param groupIdentifiers identifiers of the groups this LidAccount is a member of
     * @param identifierFactory the IdentifierFactory to use
     * @return the created RegexLidAccountManager
     */
    public static RegexLidAccountManager create(
            Identifier        siteIdentifier,
            Pattern           userNameRegex,
            Identifier []     groupIdentifiers,
            IdentifierFactory identifierFactory )
    {
        RegexLidAccountManager ret = new RegexLidAccountManager(
                siteIdentifier,
                userNameRegex,
                groupIdentifiers,
                identifierFactory );

        return ret;
    }

    /**
     * Private constructor, use factory method.
     *
     * @param siteIdentifier identifier of the site at which the accounts are managed
     * @param userNameRegex the user name regular expression
     * @param groupIdentifiers identifiers of the groups this LidAccount is a member of
     * @param identifierFactory the IdentifierFactory to use
     */
    protected RegexLidAccountManager(
            Identifier        siteIdentifier,
            Pattern           userNameRegex,
            Identifier []     groupIdentifiers,
            IdentifierFactory identifierFactory )
    {
        super( siteIdentifier, identifierFactory );

        theUserNameRegex    = userNameRegex;
        theGroupIdentifiers = groupIdentifiers;
    }

    /**
     * Obtain a HasIdentifier, given its Identifier. This implementation will always return a LidAccount.
     *
     * @param identifier the Identifier for which the HasIdentifier will be retrieved
     * @return the found HasIdentifier
     * @throws CannotFindHasIdentifierException thrown if the HasIdentifier cannot be found
     * @throws InvalidIdentifierException thrown if the provided Identifier was invalid for this HasIdentifierFinder
     */
    public LidAccount find(
            Identifier identifier )
        throws
            CannotFindHasIdentifierException,
            InvalidIdentifierException
    {
        if( isUser( identifier )) {
            HashMap<String,String> attributes  = new HashMap<String,String>();
            attributes.put( LidAccount.IDENTIFIER_ATTRIBUTE_NAME, identifier.toExternalForm() );
            attributes.put( "FirstName",  "John" );
            attributes.put( "LastName",   "Doe" );
            attributes.put( "Profession", "Mythical Man" );

            LidAccount ret = SimpleLidAccount.create(
                    identifier,
                    theSiteIdentifier,
                    LidAccount.LidAccountStatus.ACTIVE,
                    null,
                    attributes,
                    theGroupIdentifiers,
                    timeLoaded );
            return ret;

        } else {
            throw new CannotFindHasIdentifierException( identifier );
        }
    }

    /**
     * Given a remote persona and a site, determine the LidAccounts that are accessible by
     * the remote persona at the site. May return null if none has been provisioned.
     * 
     * @param remote the remote persona
     * @param siteIdentifier identifier of the site at which the account has been provisioned
     * @return the found LidAccounts, or null
     */
    public LidAccount [] determineLidAccountsFromRemotePersona(
            HasIdentifier remote,
            Identifier    siteIdentifier )
    {
        return null;
    }

    /**
     * Determine whether a record with the given username exists.
     * 
     * @param userName the user name
     * @return true if a record exists
     */
    public boolean isUser(
            Identifier userName )
    {
        if( userName == null ) {
            return false;
        }
        if( theUserNameRegex == null ) {
            return false; // no parameter, always say no
        }
        Matcher userNameMatcher = theUserNameRegex.matcher( userName.toExternalForm() );
        if( !userNameMatcher.matches() ) {
            return false;
        }
        return true;
    }
    
    /**
     * Obtain the Identifiers of the set of groups that this LidAccount is a member of.
     *
     * @return the Identifiers
     */
    public Identifier [] getGroupIdentifiers()
    {
        return theGroupIdentifiers;
    }

    /**
     * The user name regular expression.
     */
    protected Pattern theUserNameRegex;

    /**
     * Identifiers of the groups that this LidAccount is a member of.
     */
    protected Identifier [] theGroupIdentifiers;
    
    /**
     * Time when this class was loaded. We use this as the account creation time for all accounts managed by this AccountManager.
     */
    protected static final long timeLoaded = System.currentTimeMillis();
}
