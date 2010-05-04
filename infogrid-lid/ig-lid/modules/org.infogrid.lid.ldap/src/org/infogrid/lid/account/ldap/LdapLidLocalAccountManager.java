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

package org.infogrid.lid.account.ldap;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Properties;
import javax.naming.CommunicationException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import org.infogrid.lid.account.AbstractLidAccountManager;
import org.infogrid.lid.account.LidAccount;
import org.infogrid.lid.account.SimpleLidAccount;
import org.infogrid.lid.credential.LidCredentialType;
import org.infogrid.util.CannotFindHasIdentifierException;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.Identifier;
import org.infogrid.util.InvalidIdentifierException;
import org.infogrid.util.logging.Log;

/**
 * A LidAccountManager implemented using LDAP.
 * 
 * <p>Invoke similar to this:</p>
 * <pre>
 * Properties prop = new Properties();
 * prop.put( Context.PROVIDER_URL,            ldapUrl );
 * prop.put( "java.naming.ldap.version",      "3" );
 * prop.put( Context.REFERRAL,                "ignore" )
 * prop.put( Context.SECURITY_AUTHENTICATION, "simple" );
 * prop.put( Context.SECURITY_PRINCIPAL,      managerDn );
 * prop.put( Context.SECURITY_CREDENTIALS,    managerPassword );
 * prop.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );
 * DirContext dir = new InitialDirContext( prop );
 * </pre>
 * <p>When running against ActiveDirectory, an additional property may be needed:</p>
 * <pre>
 * </pre>
 * 
 */
public class LdapLidLocalAccountManager
        extends
            AbstractLidAccountManager
{
    private static final Log log = Log.getLogInstance( LdapLidLocalAccountManager.class  );

    /**
     * Factory method.
     *
     * @param siteIdentifier identifier of the site at which the accounts are managed
     * @param props Properties to use to connect to the directory
     * @param ldapContextName name of the LDAP context object in which to search, or null if default
     * @param filter the LDAP filter expression, or null if default
     * @param attributeList the list of attributes to pull out of LDAP. If null, pull out all attributes.
     * @param credentialTypes the available credential types
     * @return the created LdapLidLocalAccountManager
     * @throws NamingException something went wrong when attempting to bind to
     */
    public static LdapLidLocalAccountManager create(
            Identifier           siteIdentifier,
            Properties           props,
            String               ldapContextName,
            String               filter,
            String []            attributeList,
            LidCredentialType [] credentialTypes )
        throws
            NamingException
    {
        return create(
                siteIdentifier,
                props,
                ldapContextName,
                filter,
                null,
                attributeList,
                credentialTypes );
    }

    /**
     * Factory method.
     *
     * @param siteIdentifier identifier of the site at which the accounts are managed
     * @param props Properties to use to connect to the directory
     * @param ldapContextName name of the LDAP context object in which to search, or null if default
     * @param filter the LDAP filter expression, or null if default
     * @param controls the SearchControls to use for queries, or null if default
     * @param attributeList the list of attributes to pull out of LDAP. If null, pull out all attributes.
     * @param credentialTypes the available credential types
     * @return the created LdapLidLocalAccountManager
     * @throws NamingException something went wrong when attempting to bind to
     */
    public static LdapLidLocalAccountManager create(
            Identifier           siteIdentifier,
            Properties           props,
            String               ldapContextName,
            String               filter,
            SearchControls       controls,
            String []            attributeList,
            LidCredentialType [] credentialTypes )
        throws
            NamingException
    {
        if( ldapContextName == null ) {
            ldapContextName = props.getProperty( javax.naming.Context.PROVIDER_URL );
        }
        if( filter == null || filter.length() == 0 ) {
            filter = DEFAULT_FILTER;
        }
        if( controls == null ) {
            controls = new SearchControls();
            controls.setSearchScope( SearchControls.SUBTREE_SCOPE );
        }

        LdapLidLocalAccountManager ret = new LdapLidLocalAccountManager(
                siteIdentifier,
                props,
                ldapContextName,
                filter,
                controls,
                attributeList,
                credentialTypes );
        return ret;
    }

    /**
     * Constructor for subclasses only, use factory method.
     * 
     * @param siteIdentifier identifier of the site at which the accounts are managed
     * @param props Properties to use to connect to the directory
     * @param ldapContextName name of the LDAP context object in which to search
     * @param filter the LDAP filter expression
     * @param controls the SearchControls to use for queries
     * @param attributeList the list of attributes to pull out of LDAP. If null, pull out all attributes.
     * @param credentialTypes the LidCredentialTypes for this LidAccountManager
     */
    protected LdapLidLocalAccountManager(
            Identifier           siteIdentifier,
            Properties           props,
            String               ldapContextName,
            String               filter,
            SearchControls       controls,
            String []            attributeList,
            LidCredentialType [] credentialTypes )
    {
        super( siteIdentifier );

        theLdapProperties   = props;
        theManagerDir       = null;
        theLdapContextName  = ldapContextName;
        theFilter           = filter;
        theControls         = controls;
        theAttributeList    = attributeList;
        theCredentialTypes  = credentialTypes;
    }

    /**
     * Obtain a HasIdentifier, given its Identifier.
     * This implementation will only return instances of LidAccount; it has no concept of remote identifiers.
     *
     * @param identifier the identifier for which the LidAccount will be retrieved
     * @return the found LidAccount
     * @throws CannotFindHasIdentifierException thrown if the HasIdentifier cannot be found
     * @throws InvalidIdentifierException thrown if the provided Identifier was invalid for this HasIdentifierFinder
     */
    public LidAccount find(
            Identifier identifier )
        throws
            CannotFindHasIdentifierException,
            InvalidIdentifierException
    {
        NamingEnumeration found = null;
        String            s     = identifier.toExternalForm();

        if( s.contains( "?" ) || s.contains( "*" )) {
            throw new InvalidIdentifierException( identifier );
        }
        if( s.length() == 0 ) {
            throw new InvalidIdentifierException( identifier );
        }
        String filter = MessageFormat.format( theFilter, s );

        for( int n=0 ; n<N_CONNECTION_TRIES ; ++n ) {
            try {
                if( theManagerDir == null ) {
                    theManagerDir = new InitialDirContext( theLdapProperties );
                }
                found = theManagerDir.search( theLdapContextName, filter, theControls );

                if( found.hasMore() ) {
                    SearchResult current = (SearchResult) found.next();

                    HashMap<String,String> attributes = new HashMap<String,String>();

                    if( theAttributeList != null ) {
                        for( int i=0 ; i<theAttributeList.length ; ++i ) {
                            Attribute att = current.getAttributes().get( theAttributeList[i] );
                            if( att != null ) {

                                Object value = att.get();
                                if( value != null && value instanceof String ) {
                                    attributes.put( att.getID(), (String) value );
                                } else {
                                    attributes.put( att.getID(), null );
                                }
                            } else {
                                log.error( "Returned null attribute", theAttributeList[i] );
                            }
                        }
                    } else {
                        NamingEnumeration<? extends Attribute> currentAttributes = current.getAttributes().getAll();
                        while( currentAttributes.hasMore() ) {
                            Attribute att = currentAttributes.next();

                            Object value = att.get();
                            if( value != null && value instanceof String ) {
                                attributes.put( att.getID(), (String) value );
                            } else {
                                attributes.put( att.getID(), null );
                            }
                        }
                    }
                    LidAccount ret = SimpleLidAccount.create(
                            identifier,
                            theSiteIdentifier,
                            LidAccount.LidAccountStatus.ACTIVE,
                            null,
                            attributes,
                            theCredentialTypes,
                            new String[ theCredentialTypes.length ],
                            new Identifier[ 0 ] );

                    if( found.hasMore() ) {
                        SearchResult current2 = (SearchResult) found.next();
                        log.error( "More than one result found: ", current, current2 );
                    }
                    return ret;
                }
                throw new CannotFindHasIdentifierException( identifier );

            } catch( CommunicationException ex ) {
                // connection to directory may have timed out
                theManagerDir = null; // abandon old one

            } catch( NamingException ex ) {
                log.error( ex );
                throw new CannotFindHasIdentifierException( identifier );

            } finally {
                if( found != null ) {
                    try {
                        found.close();
                    } catch( Throwable ex2 ) {
                        // nothing
                    }
                }
            }
        }
        log.error( "Could not connect to LDAP: " + theLdapProperties );
        throw new CannotFindHasIdentifierException( identifier );
    }

    /**
     * Given a remote persona, determine the locally provisioned corresponding
     * LidAccount. May return null if none has been provisioned.
     *
     * @param remote the remote persona
     * @return the found LidAccount, or null
     */
    public LidAccount determineLidAccountFromRemotePersona(
            HasIdentifier remote )
    {
        // we don't do this, it's LDAP after all
        return null;
    }

    /**
     * The properties for the initial NamingContext.
     */
    protected Properties theLdapProperties;

    /**
     * The DirContext in which to find the identities. This is allocated when needed.
     */
    protected DirContext theManagerDir;

    /**
     * Name of the LDAP context to search.
     */
    protected String theLdapContextName;

    /**
     * The LDAP filter expression to use.
     */
    protected String theFilter;
    
    /**
     * The search controls.
     */
    protected SearchControls theControls;

    /**
     * The list of attributes to pull out of LDAP. If null, pull out all attributes.
     */
    protected String [] theAttributeList;

    /**
     * The CredentialTypes for this LidAccountManager.
     */
    protected LidCredentialType [] theCredentialTypes;

    /**
     * The default filter.
     */
    protected static final String DEFAULT_FILTER = "(sAMAccountName={0})";

    /**
     * The number of times to attempt to connect.
     */
    public static final int N_CONNECTION_TRIES = 2;
}
