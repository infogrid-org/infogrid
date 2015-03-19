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

package org.infogrid.crypto.mains;

import java.security.SecureRandom;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.infogrid.crypto.util.SecretKeyUtil;

/**
 * Helper program to generate a secret key and print it to the console.
 */
public abstract class GenerateSecretKey
{
    /**
     * Private constructor to keep this abstract.
     */
    private GenerateSecretKey()
    {
        // nothing
    }

    /**
     * Main program, invoke from the command line.
     *
     * @param args command-line arguments
     */
    public static void main(
            String [] args )
    {
        try {
            String       algorithm = args[0];
            KeyGenerator generator = KeyGenerator.getInstance( algorithm );

            generator.init(new SecureRandom());

            SecretKey key = generator.generateKey();
            String    keyAsString = SecretKeyUtil.secretKeyToString( key );

            System.out.println( "The secret key is \"" + keyAsString + "\"" );

        } catch( Throwable t ) {
            System.err.println( "Synopsis: java " + GenerateSecretKey.class.getName() + " <algorithm e.g. DES>" );
            t.printStackTrace( System.err );
            System.exit( 1 );
        }
    }
}
