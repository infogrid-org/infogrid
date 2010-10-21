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
// Copyright 1998-2010 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.probe.test.shadow;

import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBaseAccessSpecification;
import org.infogrid.meshbase.net.NetMeshBaseRedirectException;
import org.infogrid.meshbase.net.NetMeshObjectAccessException;
import org.infogrid.meshbase.net.NetMeshObjectAccessSpecification;
import org.infogrid.model.Web.WebSubjectArea;
import org.infogrid.model.primitives.StringValue;
import org.infogrid.probe.httpmapping.JustRecordHttpMappingPolicy;
import org.infogrid.util.logging.Log;

/**
  * Tests behavior in case an HTTP data source issues a redirect and follow redirects is set to false.
  */
public class RedirectTest2
        extends
            AbstractRedirectTest
{
    private static final Log log = Log.getLogInstance( RedirectTest2.class ); // our own, private logger

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

        theMeshBase.getProbeManager().setHttpMappingPolicy( JustRecordHttpMappingPolicy.SINGLETON );

        // a bit complicated to get the ONE_TIME_ONLY in here
        NetMeshObjectAccessSpecification path = theMeshBase.getNetMeshObjectAccessSpecificationFactory().obtain(
                new NetMeshBaseAccessSpecification[] {
                    theMeshBase.getNetMeshObjectAccessSpecificationFactory().getNetMeshBaseAccessSpecificationFactory().obtain(
                            theMeshBase.getMeshBaseIdentifierFactory().fromExternalForm( WEB_SERVER_IDENTIFIER + REDIRECT_PREFIX + String.valueOf( nRedirects ) ),
                            CoherenceSpecification.ONE_TIME_ONLY )
                } );

        NetMeshObject                shadowHome = null;
        NetMeshBaseRedirectException realCause  = null;
        StringValue                  location   = null;

        for( int i=nRedirects ; i>=0 ; --i ) {
            log.debug( "Now trying", i, path );

            shadowHome = theMeshBase.accessLocally( path );

            location = (StringValue) shadowHome.getPropertyValue( WebSubjectArea.WEBRESOURCE_HTTPHEADERLOCATION );
            if( location != null ) {
                path = theMeshBase.getNetMeshObjectAccessSpecificationFactory().fromExternalForm( location.getAsString() );
            } else {
                path = null;
            }
        }


        checkEquals( shadowHome.getIdentifier().toExternalForm(), WEB_SERVER_IDENTIFIER + RESULT, "Wrong shadow home identifier" );
        checkEquals( countRemaining( theMeshBase.proxies()), nRedirects+1, "Wrong number of proxies" );
        checkEquals( theMeshBase.getShadowMeshBases().size(), nRedirects+1, "Wrong number of shadows" );
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
        RedirectTest2 test = null;
        try {
            if( args.length != 0 ) {
                System.err.println( "Synopsis: <no argument>" );
                System.err.println( "aborting ..." );
                System.exit( 1 );
            }

            test = new RedirectTest2( args );
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
    public RedirectTest2(
            String [] args )
        throws
            Exception
    {
        super( RedirectTest2.class );
    }
}
