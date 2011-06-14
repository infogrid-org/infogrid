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

package org.infogrid.crypto.util;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.infogrid.util.Base64;

/**
 * Helper methods for Java Crypto functionality.
 */
public abstract class SecretKeyUtil
{
    /**
     * Private constructor to keep this abstract.
     */
    private SecretKeyUtil()
    {
        // nothing
    }

    /**
     * Convert a Java SecretKey into a printable String.
     *
     * @param key the SecretKey
     * @return the printable String
     */
    public static String secretKeyToString(
            SecretKey key )
    {
        byte [] asBytes = key.getEncoded();
        String  ret     = Base64.base64encode( asBytes );

        return ret;
    }

    /**
     * Convert a printable String into a Java SecretKey.
     *
     * @param s the String
     * @param transformation the transformation to apply
     * @return the SecretKey
     */
    public static SecretKey stringToSecretKey(
            String s,
            String transformation )
    {
        byte [] asBytes = Base64.base64decode( s );
        SecretKeySpec ret = new SecretKeySpec( asBytes, transformation );

        return ret;
    }
}
