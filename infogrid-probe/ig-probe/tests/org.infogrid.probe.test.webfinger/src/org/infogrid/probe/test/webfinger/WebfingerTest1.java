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
// Copyright 1998-2012 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.probe.test.webfinger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.infogrid.httpd.HttpEntity;
import org.infogrid.httpd.HttpEntityResponse;
import org.infogrid.httpd.HttpErrorResponse;
import org.infogrid.httpd.HttpRequest;
import org.infogrid.httpd.HttpResponse;
import org.infogrid.httpd.HttpResponseFactory;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.set.MeshObjectSet;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.util.logging.Log;

/**
 * Basic test.
 */
public class WebfingerTest1
        extends
            AbstractWebfingerTest
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
        NetMeshObject  shadowHome = theMeshBase1.accessLocally( theIdentityIdentifier, CoherenceSpecification.ONE_TIME_ONLY );
        ShadowMeshBase shadow     = theMeshBase1.getShadowMeshBaseFor( theIdentityIdentifier );

        checkEquals( shadow.size(), 2, "Wrong number of objects in shadow" );
        dumpMeshBase( shadow, "shadow:", log );

        log.debug( "ShadowHome:" );
        log.debug( shadowHome );

        MeshObjectSet xrdSet = shadowHome.traverseToNeighborMeshObjects();
        checkEquals( xrdSet.size(), 1, "Wrong number XRD elements" );

        log.debug( "XrdSet:" );
        for( MeshObject found : xrdSet ) {
            log.debug( found );
        }

        MeshObjectSet linkAndOthersSet = xrdSet.traverseToNeighborMeshObjects();
        checkEquals( linkAndOthersSet.size(), 6, "Wrong number linkAndOthersSet elements" );

        log.debug( "linkAndOthersSet:" );
        for( MeshObject found : linkAndOthersSet ) {
            log.debug( found );
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
        WebfingerTest1 test = null;
        try {
            if( args.length != 1 ) {
                System.err.println( "Synopsis: <delay>" );
                System.err.println( "aborting ..." );
                System.exit( 1 );
            }

            test = new WebfingerTest1( args );
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
    public WebfingerTest1(
            String [] args )
        throws
            Exception
    {
        super( WebfingerTest1.class, new MyResponseFactory( Long.valueOf( args[0] ) ));
    }

    // Our Logger
    private static Log log = Log.getLogInstance( WebfingerTest1.class);

    /**
      * A HttpResponseFactory that acts as the RelyingParty.
      */
    static class MyResponseFactory
        implements
            HttpResponseFactory
    {
        /**
         * Constructor.
         *
         * @param delay the delay by the web server
         */
        MyResponseFactory(
                long delay )
        {
            theDelay = delay;
        }

        /**
          * Factory method for a HttpResponse.
          *
          * @param request the HttpRequest for which we create a HttpResponse
          * @return the created HttpResponse
          */
        public HttpResponse createResponse(
                HttpRequest request )
        {
            log.debug( "Incoming request", request );

            HttpResponse ret;
            if( "GET".equals( request.getMethod() ) && ("/" + XRD_LOCAL_IDENTIFIER + "/" + IDENTITY_IDENTIFIER ).equals( request.getRelativeBaseUri() )) {
                HttpEntity entity = new HttpEntity() {
                        public boolean canRead() {
                            return true;
                        }
                        public InputStream getAsStream() {
                            try {
                                return new ByteArrayInputStream( IDENTITY_XRD.getBytes( "UTF-8" ) );
                            } catch( UnsupportedEncodingException ex ) {
                                log.error( ex );
                                return null;
                            }
                        }
                        public String getMime() {
                            return "application/xml";
                        }
                };
                ret = HttpEntityResponse.create( request, true, entity );
            } else {
                ret = HttpErrorResponse.create( request, "500", null );
            }

            if( theDelay > 0L ) {
                try {
                    Thread.sleep( theDelay );
                } catch( Throwable t ) {
                    log.error( t );
                }
            }
            return ret;
        }

        /**
         * Captures the slowness of a web server.
         */
        protected long theDelay;
    }
}
