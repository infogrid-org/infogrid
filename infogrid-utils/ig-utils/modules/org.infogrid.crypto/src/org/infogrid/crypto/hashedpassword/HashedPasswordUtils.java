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

package org.infogrid.crypto.hashedpassword;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.infogrid.util.logging.Log;

/**
 * Utilities to store and validate passwords securely through hashing.
 */
public abstract class HashedPasswordUtils
{
    private static final Log log = Log.getLogInstance( HashedPasswordUtils.class ); // our own, private logger

    /**
     * Private constructor to keep this class abstract.
     */
    private HashedPasswordUtils()
    {
        // nothing
    }

    /**
     * Construct the hash of a password.
     *
     * @param password the password
     * @return the hash of the password
     */
    public static byte [] hash(
            String password )
    {
        try {
            MessageDigest md = MessageDigest.getInstance( DIGEST_ALGORITHM  );
            md.update( password.getBytes( "UTF-8" ));

            byte ret [] = md.digest();
            return ret;

        } catch ( NoSuchAlgorithmException ex ) {
            log.error( ex );
        } catch ( UnsupportedEncodingException ex ) {
            log.error( ex );
        }
        return null;
    }

    /**
     * Validate a password.
     *
     * @param password the password in clear text
     * @param hashed the password in hashed form
     * @return true if the password is valid
     */
    public static boolean isValid(
            String  password,
            byte [] hashed )
    {
        if( password == null ) {
            return false;
        }
        if( hashed == null || hashed.length == 0 ) {
            return false; // never permit if no password is set
        }
        byte [] found = hash( password );

        if( found == null || hashed == null ) {
            return false;
        }
        if( found.length != hashed.length ) {
            return false;
        }
        for( int i=0 ; i<found.length ; ++i ) {
            if( found[i] != hashed[i] ) {
                return false;
            }
        }
        return true;
    }

    /**
     * The Digest algorithm to use.
     */
    public static final String DIGEST_ALGORITHM = "SHA-512";
}
