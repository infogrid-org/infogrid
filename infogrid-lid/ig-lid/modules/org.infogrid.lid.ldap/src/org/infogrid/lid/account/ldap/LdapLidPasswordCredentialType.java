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

package org.infogrid.lid.account.ldap;

import java.util.Properties;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import org.infogrid.lid.credential.AbstractLidPasswordCredentialType;
import org.infogrid.lid.credential.LidExpiredCredentialException;
import org.infogrid.lid.credential.LidInvalidCredentialException;
import org.infogrid.lid.credential.LidWrongPasswordException;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.Identifier;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.util.logging.Log;

/**
 * A password LidCredentialType that is validated against LDAP.
 */
public class LdapLidPasswordCredentialType
    extends
        AbstractLidPasswordCredentialType
{
    private static final Log log = Log.getLogInstance( LdapLidPasswordCredentialType.class ); // our own, private logger

    /**
     * Factory method.
     *
     * @param passwordDirProps properties for directory access
     * @return the created LdapLidPasswordCredentialType
     */
    public static LdapLidPasswordCredentialType create(
            Properties passwordDirProps )
    {
        return new LdapLidPasswordCredentialType( passwordDirProps, null );
    }

    /**
     * Factory method.
     *
     * @param passwordDirProps properties for directory access
     * @param identifierSuffix to append to the identifier when attempting to check a password, if any
     * @return the created LdapLidPasswordCredentialType
     */
    public static LdapLidPasswordCredentialType create(
            Properties passwordDirProps,
            String     identifierSuffix )
    {
        return new LdapLidPasswordCredentialType( passwordDirProps, identifierSuffix );
    }

    /**
     * Constructor, for package and subclasses only.
     *
     * @param passwordDirProps properties for directory access
     * @param identifierSuffix to append to the identifier when attempting to check a password, if any.
     */
    protected LdapLidPasswordCredentialType(
            Properties passwordDirProps,
            String     identifierSuffix )
    {
        thePasswordDirProps = passwordDirProps;
        theIdentifierSuffix = identifierSuffix;
    }

    /**
     * Determine whether the request contains a valid LidCredentialType of this type
     * for the given subject at the site with the given Identifier.
     *
     * @param request the request
     * @param subject the subject
     * @param siteIdentifier identifies the site
     * @throws LidExpiredCredentialException thrown if the contained LidCdedentialType has expired
     * @throws LidInvalidCredentialException thrown if the contained LidCdedentialType is not valid for this subject
     */
    public void checkCredential(
            SaneRequest   request,
            HasIdentifier subject,
            Identifier    siteIdentifier )
        throws
            LidInvalidCredentialException
    {
        // siteIdentifier ignored
        
        String givenPassword = request.getPostedArgument( LID_CREDENTIAL_PARAMETER_NAME );

        Properties props = (Properties) thePasswordDirProps.clone();

        String identifier;
        if( theIdentifierSuffix != null ) {
            identifier = subject.getIdentifier().toExternalForm() + theIdentifierSuffix;
        } else {
            identifier = subject.getIdentifier().toExternalForm();
        }

        props.put( javax.naming.Context.SECURITY_PRINCIPAL,   identifier );
        props.put( javax.naming.Context.SECURITY_CREDENTIALS, givenPassword );

        DirContext passwordDir; // for debugging
        try {
            passwordDir = new InitialDirContext( props );
            return;

        } catch( NamingException ex ) {
            if( log.isDebugEnabled() ) {
                log.debug( ex );
            }
            throw new LidWrongPasswordException( subject.getIdentifier(), siteIdentifier, this, ex );
        }
    }

    /**
     * Determine whether this LidCredentialType is a one-time token credential, e.g.
     * a one-time password.
     *
     * @return true if this is a one-time token credential
     */
    public boolean isOneTimeToken()
    {
        return false;
    }

    /**
     * Determine equality.
     *
     * @param other the objects to compare against
     * @return true if the objects are equal
     */
    @Override
    public boolean equals(
            Object other )
    {
        if( other instanceof LdapLidPasswordCredentialType ) {
            LdapLidPasswordCredentialType realOther = (LdapLidPasswordCredentialType) other;

            if( !thePasswordDirProps.equals( realOther.thePasswordDirProps )) {
                return false;
            }

            return theIdentifierSuffix.equals( realOther.theIdentifierSuffix );
        }
        return false;
    }

    /**
     * Hash code.
     *
     * @return hash code
     */
    @Override
    public int hashCode()
    {
        return thePasswordDirProps.hashCode() ^ theIdentifierSuffix.hashCode();
    }

    /**
     * The Properties to use when attempting to check a password.
     */
    protected Properties thePasswordDirProps;

    /**
     * Suffix to append to the identifier when attempting to check a password, if any.
     */
    protected String theIdentifierSuffix;
}
