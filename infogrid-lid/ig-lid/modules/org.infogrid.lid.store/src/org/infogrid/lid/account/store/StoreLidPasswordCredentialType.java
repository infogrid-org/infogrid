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
import org.infogrid.lid.account.LidAccount;
import org.infogrid.lid.credential.AbstractLidPasswordCredentialType;
import org.infogrid.lid.credential.LidExpiredCredentialException;
import org.infogrid.lid.credential.LidInvalidCredentialException;
import org.infogrid.store.Store;
import org.infogrid.store.StoreKeyDoesNotExistException;
import org.infogrid.store.StoreValue;
import org.infogrid.util.HasIdentifier;
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
     * @param the Store that stores the passwords
     * @return the created RegexLidPasswordCredentialType
     */
    public static StoreLidPasswordCredentialType create(
            Store passwordStore )
    {
        StoreLidPasswordCredentialType ret = new StoreLidPasswordCredentialType( passwordStore );
        return ret;
    }

    /**
     * Constructor, for subclasses only, use factory method.
     *
     * @param the Store that stores the passwords
     */
    protected StoreLidPasswordCredentialType(
            Store passwordStore )
    {
        thePasswordStore = passwordStore;
    }

    /**
     * Determine whether the request contains a valid LidCredentialType of this type
     * for the given subject.
     *
     * @param request the request
     * @param subject the subject
     * @throws LidExpiredCredentialException thrown if the contained LidCdedentialType has expired
     * @throws LidInvalidCredentialException thrown if the contained LidCdedentialType is not valid for this subject
     */
    public void checkCredential(
            SaneRequest   request,
            HasIdentifier subject )
        throws
            LidExpiredCredentialException,
            LidInvalidCredentialException
    {
        StoreValue found = null;
        try {
            found = thePasswordStore.get( subject.getIdentifier().toExternalForm() );

        } catch( IOException ex ) {
            log.error( ex );

        } catch( StoreKeyDoesNotExistException ex ) {
            // ignore
        }

        if( found == null ) {
            throw new LidWrongPasswordException( subject.getIdentifier(), this );
        }
        byte [] rawHashedCredential = found.getData();

        String givenPassword = request.getPostedArgument( LID_CREDENTIAL_PARAMETER_NAME );

        if( !HashedPasswordUtils.isValid( givenPassword, rawHashedCredential )) {
            throw new LidWrongPasswordException( subject.getIdentifier(), this );
        }
        // else return without further complications
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
     * The Store for passwords.
     */
    protected Store thePasswordStore;
}
