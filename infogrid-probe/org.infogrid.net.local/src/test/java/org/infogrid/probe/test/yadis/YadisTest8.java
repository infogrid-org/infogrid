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

package org.infogrid.probe.test.yadis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import org.infogrid.httpd.HttpEntity;
import org.infogrid.httpd.HttpEntityResponse;
import org.infogrid.httpd.HttpErrorResponse;
import org.infogrid.httpd.HttpRequest;
import org.infogrid.httpd.HttpResponse;
import org.infogrid.httpd.HttpResponseFactory;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.util.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Tests immediate XRDS discovery upon freshenNow.
 */
@RunWith(Parameterized.class)
public class YadisTest8
        extends
            AbstractYadisTest
{
    /**
     * Test parameters.
     * 
     * @return test parameters
     */
    @Parameterized.Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList( new Object[][]
            {
                {
                    0
//                },
// FIXME: currently broken                {
//                    2000
                }
            });
    }

    /**
     * Run the test.
     *
     * @throws Throwable thrown if an Exception occurred during the test
     */
    @Test
    public void run()
        throws
            Throwable
    {
        getLog().info( "Starting run with", theDelay );

        getLog().info( "Accessing test data source" );

        theWithYadis = false;

        NetMeshObject  shadowHome = theMeshBase1.accessLocally( theIdentityIdentifier, CoherenceSpecification.ONE_TIME_ONLY );
        ShadowMeshBase shadow     = theMeshBase1.getShadowMeshBaseFor( theIdentityIdentifier );

        checkNoYadisResults( shadowHome );

        //

        getLog().info(  "Freshening" );

        theWithYadis = true;
        theMeshBase1.freshenNow( new NetMeshObject[] { shadowHome } );

        checkYadisResultsIndirect( shadowHome, 3 );
    }

    /**
     * Setup.
     * 
     * @throws IOException thrown if the HttpServer could not be started
     */
    @Before
    @Override
    public void setup()
        throws
            IOException
    {
        super.setup();

        theServer.setResponseFactory( new MyResponseFactory( theDelay ));
    }

    /**
     * Constructor with parameters.
     * 
     * @param delay the web server time delay
     * @throws Exception all sorts of things may happen during a test
     */
    public YadisTest8(
            long delay )
        throws
            Exception
    {
        theDelay = delay;
    }

    /**
     * The Web server delay.
     */
    protected long theDelay;

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
        public MyResponseFactory(
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
            getLog().debug( "Incoming HTTP request:", request.getAbsoluteFullUri() );

            HttpResponse ret;
            if( "GET".equals( request.getMethod() ) && ( "/" + IDENTITY_LOCAL_IDENTIFIER ).equals( request.getRelativeBaseUri() )) {
                HttpEntity entity = new HttpEntity() {
                        public boolean canRead() {
                            return true;
                        }
                        public InputStream getAsStream() {
                            try {
                                return new ByteArrayInputStream( HTML.getBytes( "UTF-8" ));
                            } catch( UnsupportedEncodingException ex ) {
                                getLog().error( ex );
                                return null;
                            }
                        }
                        public String getMime() {
                            return "text/html";
                        }
                };
                ret = HttpEntityResponse.create( request, true, entity );
                if( theWithYadis ) {
                    ret.addHeader( "X-XRDS-Location", XRDS_IDENTIFIER );
                }

            } else if( "GET".equals( request.getMethod() ) && ( "/" + XRDS_LOCAL_IDENTIFIER ).equals( request.getRelativeBaseUri() )) {
                HttpEntity entity = new HttpEntity() {
                        public boolean canRead() {
                            return true;
                        }
                        public InputStream getAsStream() {
                            try {
                                return new ByteArrayInputStream( XRDS.getBytes( "UTF-8" ));
                            } catch( UnsupportedEncodingException ex ) {
                                getLog().error( ex );
                                return null;
                            }
                        }
                        public String getMime() {
                            return "application/xrds+xml";
                        }
                };
                ret = HttpEntityResponse.create( request, true, entity );
                if( theWithYadis ) {
                    ret.addHeader( "X-XRDS-Location", XRDS_IDENTIFIER );
                }

            } else {
                ret = HttpErrorResponse.create( request, "500", null );
            }

            if( theDelay > 0L ) {
                try {
                    Thread.sleep( theDelay );
                } catch( Throwable t ) {
                    getLog().error( t );
                }
            }
            getLog().debug( "Incoming HTTP request returns:", ret );
            return ret;
        }

        /**
         * Obtain the Log for this subclass.
         *
         * @return the Log for this subclass
         */
        protected Log getLog()
        {
            return Log.getLogInstance( YadisTest8.class );
        }
        
        /**
         * Captures the slowness of a web server.
         */
        protected long theDelay;
    }
}
