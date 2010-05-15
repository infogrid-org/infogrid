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

package org.infogrid.lid.account.regex;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.infogrid.lid.account.AbstractLidAccountManager;
import org.infogrid.lid.account.LidAccount;
import org.infogrid.lid.account.SimpleLidAccount;
import org.infogrid.lid.credential.LidCredentialType;
import org.infogrid.util.CannotFindHasIdentifierException;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.Identifier;
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
     * @param passwordRegex the password regular expression
     * @return the created RegexLidAccountManager
     * @param groupIdentifiers identifiers of the groups this LidAccount is a member of
     */
    public static RegexLidAccountManager create(
            Identifier    siteIdentifier,
            String        userNameRegex,
            String        passwordRegex,
            Identifier [] groupIdentifiers )
    {
        RegexLidAccountManager ret = new RegexLidAccountManager(
                siteIdentifier,
                Pattern.compile( userNameRegex ),
                RegexLidPasswordCredentialType.create( Pattern.compile( passwordRegex )),
                groupIdentifiers );
        
        return ret;
    }

    /**
     * Factory method.
     * 
     * @param siteIdentifier identifier of the site at which the accounts are managed
     * @param userNameRegex the user name regular expression
     * @param passwordRegex the password regular expression
     * @return the created RegexLidAccountManager
     * @param groupIdentifiers identifiers of the groups this LidAccount is a member of
     */
    public static RegexLidAccountManager create(
            Identifier    siteIdentifier,
            Pattern       userNameRegex,
            Pattern       passwordRegex,
            Identifier [] groupIdentifiers )
    {
        RegexLidAccountManager ret = new RegexLidAccountManager(
                siteIdentifier,
                userNameRegex,
                RegexLidPasswordCredentialType.create( passwordRegex ),
                groupIdentifiers );
        
        return ret;
    }

    /**
     * Factory method.
     *
     * @param siteIdentifier identifier of the site at which the accounts are managed
     * @param userNameRegex the user name regular expression
     * @param credentialType the available LidCredentialType
     * @return the created RegexLidAccountManager
     * @param groupIdentifiers identifiers of the groups this LidAccount is a member of
     */
    public static RegexLidAccountManager create(
            Identifier                     siteIdentifier,
            String                         userNameRegex,
            RegexLidPasswordCredentialType credentialType,
            Identifier []                  groupIdentifiers )
    {
        RegexLidAccountManager ret = new RegexLidAccountManager(
                siteIdentifier,
                Pattern.compile( userNameRegex ),
                credentialType,
                groupIdentifiers );

        return ret;
    }

    /**
     * Factory method.
     *
     * @param siteIdentifier identifier of the site at which the accounts are managed
     * @param userNameRegex the user name regular expression
     * @param credentialType the available LidCredentialType
     * @return the created RegexLidAccountManager
     * @param groupIdentifiers identifiers of the groups this LidAccount is a member of
     */
    public static RegexLidAccountManager create(
            Identifier                     siteIdentifier,
            Pattern                        userNameRegex,
            RegexLidPasswordCredentialType credentialType,
            Identifier []                  groupIdentifiers )
    {
        RegexLidAccountManager ret = new RegexLidAccountManager(
                siteIdentifier,
                userNameRegex,
                credentialType,
                groupIdentifiers );

        return ret;
    }

    /**
     * Private constructor, use factory method.
     *
     * @param siteIdentifier identifier of the site at which the accounts are managed
     * @param userNameRegex the user name regular expression
     * @param credentialType the available LidCredentialType
     * @param groupIdentifiers identifiers of the groups this LidAccount is a member of
     */
    protected RegexLidAccountManager(
            Identifier                     siteIdentifier,
            Pattern                        userNameRegex,
            RegexLidPasswordCredentialType credentialType,
            Identifier []                  groupIdentifiers )
    {
        super( siteIdentifier );

        theUserNameRegex  = userNameRegex;

        theCredentialTypes = new LidCredentialType[] {
                credentialType
        };
        theCredentials = new String[] {
                null
        };

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
                    theCredentialTypes,
                    theCredentials,
                    theGroupIdentifiers );
            return ret;

        } else {
            throw new CannotFindHasIdentifierException( identifier );
        }
    }

    /**
     * Given a remote persona and a site, determine the LidAccount that has been provisioned for
     * the remote persona at the site. May return null if none has been provisioned.
     * Always returns null in this implementation.
     *
     * @param remote the remote persona
     * @param siteIdentifier identifier of the site at which the account has been provisioned
     * @return the found LidAccount, or null
     */
    public LidAccount determineLidAccountFromRemotePersona(
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
     * The credential types accepted.
     */
    protected LidCredentialType [] theCredentialTypes;

    /**
     * The credential values (not used).
     */
    protected String [] theCredentials;

    /**
     * Identiifers of the groups that this LidAccount is a member of.
     */
    protected Identifier [] theGroupIdentifiers;
}
