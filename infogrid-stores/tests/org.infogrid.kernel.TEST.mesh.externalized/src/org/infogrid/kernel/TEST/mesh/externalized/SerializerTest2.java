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
// Copyright 1998-2008 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.kernel.TEST.mesh.externalized;

import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.util.logging.Log;

/**
 * Tests NetMeshBaseIdentifier serialization and deserialization.
 */
public class SerializerTest2
        extends
            AbstractSerializerTest
{
    /**
     * Run the test.
     */
    public void run()
        throws
            Exception
    {
        for( int i=0 ; i<testData.length ; ++i ) {
            log.info( "Testing " + testData[i] );

            NetMeshBaseIdentifier original = testData[i];
            NetMeshBaseIdentifier decoded  = null;
            String            encoded  = null;

            try {
                encoded = original.toExternalForm();
                
                log.info( "value: \"" + original + "\", serialized: \"" + encoded + "\"" );

                decoded = NetMeshBaseIdentifier.fromExternalForm( encoded );

                checkEquals( original, decoded, "incorrect deserialization" );

            } catch( Throwable ex ) {
                ++errorCount;
                if( encoded == null ) {
                    reportError( "ERROR: element " + i + " of type " + original.getClass() + " threw exception of "
                            + ex.getClass() + " during "
                            + "encoding of "
                            + original,
                            ex );
                } else {
                    reportError( "ERROR: element " + i + " of type " + original.getClass() + " threw exception of "
                            + ex.getClass() + " during "
                            + "decoding of "
                            + original
                            + " from "
                            + encoded,
                            ex );
                }
                checkEquals( original, decoded, "what we received" );
            }
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
        SerializerTest2 test = null;
        try {
            if( args.length > 0 ) {
                System.err.println( "Synopsis: <no arguments>" );
                System.err.println( "aborting ..." );
                System.exit( 1 );
            }

            test = new SerializerTest2( args );
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
      */
    public SerializerTest2(
            String [] args )
        throws
            Exception
    {
        super( SerializerTest2.class );
    }

    // Our Logger
    private static Log log = Log.getLogInstance( SerializerTest2.class );


    /**
     * The test data.
     */
    protected static NetMeshBaseIdentifier[] testData;
    static {
        try {
            testData = new NetMeshBaseIdentifier [] {
                    NetMeshBaseIdentifier.create( "http://www.r-objects.com/" ),
                    NetMeshBaseIdentifier.create( "http://foo.example.com/abc.jsp&def=ghi,,/&amp;xyz." )
            };
        } catch( Throwable t ) {
            log.error( t );
        }
    }
}
