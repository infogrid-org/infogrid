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

package org.infogrid.util;

import java.util.Random;

/**
 * Generates unique Strings.
 */
public class UniqueStringGenerator
        implements
            UniqueTokenGenerator<String>
{
    /**
     * Factory method.
     *
     * @param length the length of the generated Strings
     * @return the created UniqueStringGenerator
     */
    public static UniqueStringGenerator create(
            int length )
    {
        return new UniqueStringGenerator( DEFAULT_ALLOWED_CHARS, length );
    }

    /**
     * Factory method.
     *
     * @param alphabet the alphabet to use for the generated Strings
     * @param length the length of the generated Strings
     * @return the created UniqueStringGenerator
     */
    public static UniqueStringGenerator create(
            char [] alphabet,
            int     length )
    {
        return new UniqueStringGenerator( alphabet, length );
    }

    /**
     * Private constructor, use factory method.
     *
     * @param alphabet the alphabet to use for the generated Strings
     * @param length the length of the generated Strings
     */
    protected UniqueStringGenerator(
            char [] alphabet,
            int     length )
    {
        theAlphabet = alphabet;
        theLength   = length;

        theRandom = new Random( theSeedGenerator.createUniqueToken() );
    }

    /**
     * Create a unique token.
     *
     * @return the unique token
     */
    public synchronized String createUniqueToken()
    {
        char [] buf = new char[ theLength ];

        for( int i=0 ; i<theLength ; ++i ) {
            int  value = theRandom.nextInt( theAlphabet.length );
            char c     = theAlphabet[ value ];

            buf[i]  = c;
        }
        return new String( buf );
    }

    /**
     * The alphabet to use for generating the Strings.
     */
    protected char [] theAlphabet;

    /**
     * The length of Strings being generated.
     */
    protected int theLength;

    /**
     * The random generator.
     */
    protected Random theRandom;

    /**
     * The generator of seeds.
     */
    protected static UniqueTokenGenerator<Long> theSeedGenerator = SimpleTimeBasedUniqueLongGenerator.create();

    /**
     * Our ResourceHelper.
     */
    private static final ResourceHelper theResourceHelper = ResourceHelper.getInstance( UniqueStringGenerator.class );

    /**
     * The characters that are allowed in the token.
     */
    protected static final char [] DEFAULT_ALLOWED_CHARS = theResourceHelper.getResourceStringOrDefault(
            "DefaultAllowedChars",
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-~" ).toCharArray();
}
