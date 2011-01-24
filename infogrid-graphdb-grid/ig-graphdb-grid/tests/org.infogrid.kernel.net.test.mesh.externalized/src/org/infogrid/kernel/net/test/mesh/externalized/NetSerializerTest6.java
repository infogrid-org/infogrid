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

package org.infogrid.kernel.net.test.mesh.externalized;

import org.infogrid.meshbase.MeshBaseIdentifier;
import org.infogrid.util.Pair;
import org.infogrid.util.logging.Log;

/**
 * Tests the guessing of MeshBaseIdentifiers.
 */
public class NetSerializerTest6
        extends
            AbstractNetSerializerTest
{
    /**
     * Run the test.
     *
     * @throws Exception all sorts of things may happen during a test
     */
    public void run()
        throws
            Exception
    {
        for( int i=0 ; i<testData.length ; ++i ) {

            String input  = (String) testData[i].getName();
            String output = (String) testData[i].getValue();

            log.info( "Testing " + input + " -> " + output );

            MeshBaseIdentifier current;
            String             converted;

            current   = theMeshBaseIdentifierFactory.guessFromExternalForm( input );
            converted = current.toExternalForm();

            checkEquals( output, converted, "incorrect guessing" );
        }
    }

    /**
     * Main program.
     *
     * @param args command-line arguments
     */
    public static void main(
            String [] args )
    {
        NetSerializerTest6 test = null;
        try {
            if( args.length > 0 ) {
                System.err.println( "Synopsis: <no arguments>" );
                System.err.println( "aborting ..." );
                System.exit( 1 );
            }

            test = new NetSerializerTest6( args );
            test.run();

        } catch( Throwable ex ) {
            log.error( ex );
            System.exit(1);
        }
        if( test != null ) {
            test.cleanup();
        }
        if( errorCount == 0 ) {
            log.info( "PASS" );
        } else {
            log.info( "FAIL (" + errorCount + " errors)" );
        }
        System.exit( errorCount );
    }

    /**
     * Constructor.
     *
     * @param args command-line arguments
     * @throws Exception all sorts of things may go wrong in tests
     */
    public NetSerializerTest6(
            String [] args )
        throws
            Exception
    {
        super( NetSerializerTest6.class  );
    }

    // Our Logger
    private static final Log log = Log.getLogInstance( NetSerializerTest6.class  );

    /**
     * The test data.
     */
    protected static Pair [] testData;
    static {
        try {
            testData = new Pair [] {
                    new Pair<String,String>( "http://example.com/",      "http://example.com/" ),
                    new Pair<String,String>( "htTp://eXample.com/",      "http://example.com/" ),
                    new Pair<String,String>( "https://example.com/",     "https://example.com/" ),
                    new Pair<String,String>( "HTtps://EXAMPLE.COM/",     "https://example.com/" ),
                    new Pair<String,String>( "http://example.com/foo",   "http://example.com/foo" ),
                    new Pair<String,String>( "http://exAMple.com/fOO",   "http://example.com/fOO" ),
                    new Pair<String,String>( "https://example.com/foo",  "https://example.com/foo" ),
                    new Pair<String,String>( "http://example.com/foo/",  "http://example.com/foo/" ),
                    new Pair<String,String>( "https://example.com/foo/", "https://example.com/foo/" ),
                    new Pair<String,String>( "http://example.com",       "http://example.com/" ),
                    new Pair<String,String>( "https://example.com",      "https://example.com/" ),
                    new Pair<String,String>( "example.com",              "http://example.com/" ),
                    new Pair<String,String>( "example.com/foo",          "http://example.com/foo" ),
                    new Pair<String,String>( "example.com/foo/",         "http://example.com/foo/" ),
                    new Pair<String,String>( "eXAmple.coM/foO/",         "http://example.com/foO/" ),
                    new Pair<String,String>( "=abc",                     "=abc" ),
                    new Pair<String,String>( "=abC",                     "=abc" ),
                    new Pair<String,String>( "abc@def.com",              "acct:abc@def.com" ),
                    new Pair<String,String>( "abC@deF.cOm",              "acct:abc@def.com" ),
                    new Pair<String,String>( "test:abc",                 "test:abc" ),
                    new Pair<String,String>( "test:aBc",                 "test:aBc" ),
                    new Pair<String,String>( "file:/tmp/foo",            "file:/tmp/foo" ),
                    new Pair<String,String>( "fiLe:/tmP/foo",            "file:/tmP/foo" ),
                    new Pair<String,String>( "/tmp/foo",                 "file:/tmp/foo" ),
            };
        } catch( Throwable t ) {
            log.error( t );
        }
    }
}
