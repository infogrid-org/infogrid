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

package org.infogrid.util;

import java.util.Random;

/**
 * Generates unique String tokens.
 */
public class UniqueStringGenerator
        implements
            UniqueTokenGenerator<String>
{
    /**
     * Factory method.
     *
     * @param length the length of the generated token
     * @return the created UniqueStringGenerator
     */
    public static UniqueStringGenerator create(
            int length )
    {
        return new UniqueStringGenerator( DEFAULT_ALLOWED_FIRST_CHARS, DEFAULT_ALLOWED_CHARS, length );
    }

    /**
     * Factory method.
     *
     * @param alphabet the alphabet to use for the generated token
     * @param length the length of the generated token
     * @return the created UniqueStringGenerator
     */
    public static UniqueStringGenerator create(
            char [] alphabet,
            int     length )
    {
        return new UniqueStringGenerator( alphabet, alphabet, length );
    }

    /**
     * Factory method.
     *
     * @param firstCharAlphabet the alphabet to use for the first letter in the generated token
     * @param alphabet the alphabet to use for the generated token
     * @param length the length of the generated token, including first character
     * @return the created UniqueStringGenerator
     */
    public static UniqueStringGenerator create(
            char [] firstCharAlphabet,
            char [] alphabet,
            int     length )
    {
        return new UniqueStringGenerator( firstCharAlphabet, alphabet, length );
    }

    /**
     * Private constructor, use factory method.
     *
     * @param firstCharAlphabet the alphabet to use for the first letter in the generated token
     * @param alphabet the alphabet to use for the generated token
     * @param length the length of the generated token, including first character
     */
    protected UniqueStringGenerator(
            char [] firstCharAlphabet,
            char [] alphabet,
            int     length )
    {
        theFirstCharAlphabet = firstCharAlphabet;
        theAlphabet          = alphabet;
        theLength            = length;

        theRandom = new Random( theSeedGenerator.createUniqueToken() );
    }

    /**
     * Create a unique token.
     *
     * @return the unique token
     */
    public String createUniqueToken()
    {
        return createUniqueToken( theLength );
    }

    /**
     * Create a unique token with a given length.;
     *
     * @param length the desired length
     * @return the unique token
     */
    public synchronized String createUniqueToken(
            int length )
    {
        char [] buf = new char[ length ];

        if( length > 0 ) {
            int  value = theRandom.nextInt( theFirstCharAlphabet.length );
            char c     = theFirstCharAlphabet[ value ];

            buf[0]  = c;
        }
        for( int i=1 ; i<length ; ++i ) {
            int  value = theRandom.nextInt( theAlphabet.length );
            char c     = theAlphabet[ value ];

            buf[i]  = c;
        }
        return new String( buf );
    }

    /**
     * The alphabet to use for generating the first character in a token.
     */
    protected char [] theFirstCharAlphabet;

    /**
     * The alphabet to use for generating a token.
     */
    protected char [] theAlphabet;

    /**
     * The length of a token being generated.
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
     * The characters that are allowed in the first character in the token.
     */
    protected static final char [] DEFAULT_ALLOWED_FIRST_CHARS = theResourceHelper.getResourceStringOrDefault(
            "DefaultAllowedFirstChar",
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" ).toCharArray();

    /**
     * The characters that are allowed in the token.
     */
    protected static final char [] DEFAULT_ALLOWED_CHARS = theResourceHelper.getResourceStringOrDefault(
            "DefaultAllowedChars",
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-~" ).toCharArray();
}
