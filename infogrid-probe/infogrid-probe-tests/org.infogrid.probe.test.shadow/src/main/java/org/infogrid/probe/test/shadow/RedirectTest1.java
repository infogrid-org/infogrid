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

package org.infogrid.probe.test.shadow;

import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBaseAccessSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseRedirectException;
import org.infogrid.meshbase.net.NetMeshObjectAccessSpecification;
import org.infogrid.util.logging.Log;

/**
  * Tests behavior in case an HTTP data source issues a redirect.
  */
public class RedirectTest1
        extends
            AbstractRedirectTest
{
    private static final Log log = Log.getLogInstance( RedirectTest1.class ); // our own, private logger

    /**
     * Run the test.
     *
     * @throws Exception all sorts of things may happen during a test
     */
    public void run()
        throws
            Exception
    {
        int nRedirects = 2;

        // a bit complicated to get the ONE_TIME_ONLY in here
        NetMeshObjectAccessSpecification path = theMeshBase.getNetMeshObjectAccessSpecificationFactory().obtain(
                new NetMeshBaseAccessSpecification[] {
                    theMeshBase.getNetMeshObjectAccessSpecificationFactory().getNetMeshBaseAccessSpecificationFactory().obtain(
                            theMeshBase.getMeshBaseIdentifierFactory().fromExternalForm( WEB_SERVER_IDENTIFIER + REDIRECT_PREFIX + String.valueOf( nRedirects ) ),
                            CoherenceSpecification.ONE_TIME_ONLY )
                } );

        NetMeshObject                shadowHome = null;
        NetMeshBaseRedirectException realCause  = null;
        NetMeshBaseIdentifier        location   = null;

        log.debug( "Now trying", path );

        shadowHome = theMeshBase.accessLocally( path );

        checkEquals( shadowHome.getIdentifier().toExternalForm(), WEB_SERVER_IDENTIFIER + REDIRECT_PREFIX + String.valueOf( nRedirects ), "Wrong shadow home identifier" );
        checkEquals( countRemaining( theMeshBase.proxies()), 1, "Wrong number of proxies" );
        checkEquals( theMeshBase.getShadowMeshBases().size(), 1, "Wrong number of shadows" );
    }

    /**
     * Main program.
     *
     * @param args command-line arguments
     * @throws Exception all sorts of things may happen during a test
     */
    public static void main(
            String [] args )
        throws
            Exception
    {
        RedirectTest1 test = null;
        try {
            if( args.length != 0 ) {
                System.err.println( "Synopsis: <no argument>" );
                System.err.println( "aborting ..." );
                System.exit( 1 );
            }

            test = new RedirectTest1( args );
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
     * @throws Exception all sorts of things may happen during a test
     */
    public RedirectTest1(
            String [] args )
        throws
            Exception
    {
        super( RedirectTest1.class );
    }
}
