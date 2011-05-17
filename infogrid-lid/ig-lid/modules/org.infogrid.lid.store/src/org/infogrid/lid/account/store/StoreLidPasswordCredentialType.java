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

package org.infogrid.lid.account.store;

import java.io.IOException;
import org.infogrid.lid.credential.LidWrongPasswordException;
import org.infogrid.crypto.hashedpassword.HashedPasswordUtils;
import org.infogrid.lid.credential.AbstractLidPasswordCredentialType;
import org.infogrid.lid.credential.LidExpiredCredentialException;
import org.infogrid.lid.credential.LidInvalidCredentialException;
import org.infogrid.store.Store;
import org.infogrid.store.StoreKeyDoesNotExistException;
import org.infogrid.store.StoreValue;
import org.infogrid.util.FactoryException;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.Identifier;
import org.infogrid.util.NameServer;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.util.logging.Log;

/**
 * A password LidCredentialType that is validated against hashed passwords contained in a Store.
 */
public class StoreLidPasswordCredentialType
    extends
        AbstractLidPasswordCredentialType
{
    private static final Log log = Log.getLogInstance( StoreLidPasswordCredentialType.class ); // our own, private logger

    /**
     * Factory method.
     *
     * @param passwordStores gives access to the Store that stores the passwords, keyed by the site identifier
     * @return the created RegexLidPasswordCredentialType
     */
    public static StoreLidPasswordCredentialType create(
            NameServer<Identifier,Store> passwordStores )
    {
        StoreLidPasswordCredentialType ret = new StoreLidPasswordCredentialType( passwordStores );
        return ret;
    }

    /**
     * Constructor, for subclasses only, use factory method.
     *
     * @param passwordStores gives access to the Store that stores the passwords, keyed by the site identifier
     */
    protected StoreLidPasswordCredentialType(
            NameServer<Identifier,Store> passwordStores )
    {
        thePasswordStores = passwordStores;
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
            LidExpiredCredentialException,
            LidInvalidCredentialException
    {
        String givenPassword = request.getPostedArgument( LID_CREDENTIAL_PARAMETER_NAME );
        if( givenPassword == null ) {
            givenPassword = request.getUrlArgument( LID_CREDENTIAL_PARAMETER_NAME );
        }

        checkCredential( givenPassword, subject, siteIdentifier );
    }

    /**
     * Determine whether the given credential is correct.
     *
     * @param givenCredential the given credential
     * @param subject the subject
     * @param siteIdentifier identifies the site
     * @throws LidExpiredCredentialException thrown if the contained LidCdedentialType has expired
     * @throws LidInvalidCredentialException thrown if the contained LidCdedentialType is not valid for this subject
     */
    protected void checkCredential(
            String        givenCredential,
            HasIdentifier subject,
            Identifier    siteIdentifier )
        throws
            LidExpiredCredentialException,
            LidInvalidCredentialException
    {
        Store passwordStore = thePasswordStores.get( siteIdentifier );

        if( passwordStore == null ) {
            throw new LidWrongPasswordException( subject.getIdentifier(), siteIdentifier, this );
        }

        StoreValue found = null;
        try {
            found = passwordStore.get( subject.getIdentifier().toExternalForm() );

        } catch( IOException ex ) {
            log.error( ex );

        } catch( StoreKeyDoesNotExistException ex ) {
            // ignore
        }

        if( found == null ) {
            throw new LidWrongPasswordException( subject.getIdentifier(), siteIdentifier, this );
        }
        byte [] rawHashedCredential = found.getData();

        if( !HashedPasswordUtils.isValid( givenCredential, rawHashedCredential )) {
            throw new LidWrongPasswordException( subject.getIdentifier(), siteIdentifier, this );
        }
        // else return without further complications
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
     * Assign a credential for a particular subject at a particular site. This credential
     * does not ever expire.
     *
     * @param subject the subject for which the password is assigned
     * @param siteIdentifier identifies the site
     * @param newPassword the new password
     * @throws IOException thrown if the password could not be written
     * @throws FactoryException thrown if the Store could not be found or created
     */
    public void setPassword(
            HasIdentifier subject,
            Identifier    siteIdentifier,
            String        newPassword )
        throws
            IOException,
            FactoryException
    {
        setPassword( subject, siteIdentifier, newPassword, -1L );
    }

    /**
     * Assign a credential for a particular subject at a particular site. This credential
     * expires in the number of milliseconds given.
     *
     * @param subject the subject for which the password is assigned
     * @param siteIdentifier identifies the site
     * @param newPassword the new password
     * @throws IOException thrown if the password could not be written
     * @throws FactoryException thrown if the Store could not be found or created
     */
    public void setPassword(
            HasIdentifier subject,
            Identifier    siteIdentifier,
            String        newPassword,
            long          validityInMillis )
        throws
            IOException,
            FactoryException
    {
        long now = System.currentTimeMillis();

        byte [] hashedPassword = HashedPasswordUtils.hash( newPassword );

        Store passwordStore = thePasswordStores.get( siteIdentifier );
        passwordStore.putOrUpdate(
                subject.getIdentifier().toExternalForm(),
                DEFAULT_ENCODING,
                now,
                now,
                now,
                validityInMillis > 0 ? ( now + validityInMillis ) : -1L,
                hashedPassword );
    }

    /**
     * Determine whether a valid password has been set for a particular subject at a particular site.
     *
     * @param subject the subject that needs a password
     * @param siteIdentifier identifies the site
     */
    public boolean hasValidPassword(
            HasIdentifier subject,
            Identifier    siteIdentifier )
    {
        Store passwordStore = thePasswordStores.get( siteIdentifier );

        if( passwordStore == null ) {
            return false;
        }

        StoreValue found = null;
        try {
            found = passwordStore.get( subject.getIdentifier().toExternalForm() );

        } catch( IOException ex ) {
            log.error( ex );

        } catch( StoreKeyDoesNotExistException ex ) {
            // ignore
        }

        if( found == null ) {
            return false;
        }
        if( found.getTimeExpires() > 0 && found.getTimeExpires() < System.currentTimeMillis() ) {
            return false;
        }
        return true;
    }

    /**
     * Remove the password. This resets the password store to the same state as if the password
     * had never been set before.
     *
     * @param subject the subject for which the password is to be removed
     * @param siteIdentifier identifies the site
     */
    public void unsetPassword(
            HasIdentifier subject,
            Identifier    siteIdentifier )
        throws
            IOException,
            FactoryException
    {
        Store passwordStore = thePasswordStores.get( siteIdentifier );

        try {
            passwordStore.delete( subject.getIdentifier().toExternalForm() );

        } catch( StoreKeyDoesNotExistException ex ) {
            if( log.isInfoEnabled() ) {
                log.info( ex );
            }
        }
    }

    /**
     * Determine equality.
     *
     * @param other the object to compare against
     * @return true if the objects are equal
     */
    @Override
    public boolean equals(
            Object other )
    {
        if( other instanceof StoreLidPasswordCredentialType ) {
            return true;
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
        return getClass().hashCode();
    }

    /**
     * How we find the Store for passwords.
     */
    protected NameServer<Identifier,Store> thePasswordStores;

    /**
     * Name of the encoding to use in the Store.
     */
    protected static final String DEFAULT_ENCODING = "default";
}
