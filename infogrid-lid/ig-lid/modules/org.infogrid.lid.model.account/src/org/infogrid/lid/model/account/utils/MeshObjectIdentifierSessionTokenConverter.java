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

package org.infogrid.lid.model.account.utils;

import java.text.ParseException;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.meshbase.MeshObjectIdentifierFactory;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.UniqueStringGenerator;

/**
 * Generates and obfuscates session IDs.
 */
public class MeshObjectIdentifierSessionTokenConverter
        extends
            UniqueStringGenerator
{
    /**
     * Singleton instance.
     */
    public static final MeshObjectIdentifierSessionTokenConverter SINGLETON = new MeshObjectIdentifierSessionTokenConverter();

    /**
     * Constructor.
     */
    private MeshObjectIdentifierSessionTokenConverter()
    {
        super( DEFAULT_ALLOWED_CHARS, 64 );
    }

    /**
     * Map the session token to the MeshObjectIdentifier of the session object.
     *
     * @param sessionToken the session token
     * @param idFact the MeshObjectIdentifierFactory to use
     * @return the MeshObjectIdentifier
     * @throws ParseException parsing problem
     */
    public static MeshObjectIdentifier tokenToMeshObjectIdentifier(
            String                      sessionToken,
            MeshObjectIdentifierFactory idFact )
        throws
            ParseException
    {
        String converted = convert( sessionToken );

        MeshObjectIdentifier ret = idFact.guessFromExternalForm( converted );
        return ret;
    }

    /**
     * Reverse map the MeshObjectIdentifier of the session object back to the session token.
     *
     * @param identifier the session object's MeshObjectIdentifier
     * @param prefix any prefix that needs to be stripped first
     * @return the token
     */
    public static String meshObjectIdentifierToToken(
            MeshObjectIdentifier identifier,
            String               prefix )
    {
        String extForm = identifier.toExternalForm();
        String local;
        if( extForm.startsWith( prefix )) {
            local = extForm.substring( prefix.length() );
        } else {
            local = extForm;
        }

        String ret = convert( local );
        return ret;
    }

    /**
     * Helper method to convert.
     *
     * @param in the input
     * @return the output
     */
    protected static String convert(
            String in )
    {
        if( !CONVERT ) {
            return in;
        }
        
        // FIXME This can be made faster
        StringBuilder converted = new StringBuilder( in.length() );

        for( int i=0 ; i<in.length() ; ++i ) {
            byte mask = MASK[ i % MASK.length ];
            char c1    = in.charAt( i );
            int  c2    = -1;

            for( int j=0 ; j<DEFAULT_ALLOWED_CHARS.length ; ++j ) {
                if( c1 == DEFAULT_ALLOWED_CHARS[j] ) {
                    c2 = j;
                    break;
                }
            }
            if( c2 == -1 ) {
                throw new IllegalArgumentException( "Cannot convert " + in + ", invalid: " + c1 );
            }

            int c3 = c2 ^ mask;

            int c4 = c3 & 63;

            char c5 = DEFAULT_ALLOWED_CHARS[c4];

            converted.append( c5 );
        }
        return converted.toString();
    }

    /**
     * Our ResourceHelper.
     */
    private static final ResourceHelper theResourceHelper = ResourceHelper.getInstance( MeshObjectIdentifierSessionTokenConverter.class );

    /**
     * Our mask. This can be configured in the ResourceHelper, so it can be customized
     * for each application.
     */
    private static final byte [] MASK;
    static {
        String found = theResourceHelper.getResourceStringOrDefault(
            "Mask",
            "a6aa47df8fa36fb1462447054ef0c0c4a85b25a2b59fc297cad573abd3d76d9f"
                    + "4245a03c4d177b925313be11cbe65a5cef21aa89c179fe3ce7231ee4b3f6d619"
                    + "b05cc56dc02190733e86f4bed6880b104acffb7e6f41a451ee0ca1da7ada86c7"
                    + "700f259d246b6df67f3060aee55e70e017f15fbb1cda4d17faf7b76f3dc669c9");

        MASK = new byte[ found.length() / 2 ];
        for( int i=0 ; i<MASK.length ; ++i ) {
            char c1 = found.charAt( 2*i );
            char c2 = found.charAt( 2*i+1 );

            byte current = (byte) (( char2nibble( c1 ) << 4) | char2nibble( c2 ));
            MASK[i] = current;
        }
    }

    /**
     * Flag that indicates whether we are converting or not. Setting this helps with debugging.
     */
    protected static final boolean CONVERT = theResourceHelper.getResourceBooleanOrDefault( "Convert", true );

    /**
     * Helper method.
     *
     * @param c the character to convert
     * @return the corresponding nibble
     */
    protected static byte char2nibble(
            char c )
    {
        if( '0' <= c && c <= '9' ) {
            return (byte) ( c-'0' );
        } else if( 'a' <= c && c <= 'z' ) {
            return (byte) (c-'a');
        } else if( 'A' <= c && c <= 'Z' ) {
            return (byte) (c-'A');
        } else {
            throw new IllegalArgumentException( "Cannot convert '" + c + "'" );
        }
    }

    /**
     * Helper main method to test.
     *
     * @param args not used
     */
    public static void main( String [] args ) {
        String test = "HHeelllloo";
        String test1 = convert( test );
        String test2 = convert( test1 );
        System.out.println( test );
        System.out.println( test1 );
        System.out.println( test2 );
    }
}
