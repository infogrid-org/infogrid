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
import java.io.FileInputStream;
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
import org.infogrid.lid.model.yadis.YadisSubjectArea;
import org.infogrid.lid.model.yadis.util.YadisUtil;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.set.MeshObjectSet;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.model.primitives.EntityType;
import org.infogrid.testharness.AbstractTest;
import org.infogrid.util.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Tests parsing of Yahoo's XRDS file.
 */
@RunWith(Parameterized.class)
public class YadisTest7
        extends
            AbstractYadisTest
{
    /**
     * Test parameters.
     * 
     * @return test parameters
     */
    @Parameterized.Parameters(name = "{index}: deltay={1}")
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList( new Object[][]
            {
                {
                    AbstractTest.fileSystemFileName( YadisTest7.class, "YadisTest7.xml" ),
                    0
                },
                {
                    AbstractTest.fileSystemFileName( YadisTest7.class, "YadisTest7.xml" ),
                    2000
                }
            });
    }

    /**
     * Run the test.
     *
     * @throws Exception thrown if an Exception occurred during the test
     */
    @Test
    public void run()
        throws
            Exception
    {
        log.info( "accessing test data source" );

        NetMeshBaseIdentifier netId = theMeshBase1.getMeshBaseIdentifierFactory().fromExternalForm( "http://localhost:" + SERVER_PORT + "/" );

        NetMeshObject shadowHome = theMeshBase1.accessLocally( netId, CoherenceSpecification.ONE_TIME_ONLY );

        //

        log.info( "Checking for correct results" );

        MeshObjectSet services = YadisUtil.determineServicesFor( shadowHome );
        checkEquals( services.size(), 1, "Wrong number of services found" );

        MeshObject    only      = services.getSingleMember();
        EntityType [] onlyTypes = only.getTypes();

        checkEquals( onlyTypes.length, 1, "more than one type" );

        MeshObjectSet relatedTypeSet = only.traverse( YadisSubjectArea.XRDSSERVICE_HASTYPE_XRDSSERVICETYPE.getSource() );
        checkEquals( relatedTypeSet.size(), 4, "wrong number of XrdsServiceType instances" );
    }

    /**
     * Setup.
     * 
     * @throws IOException thrown if the HttpServer could not be started
     */
    @Before
    public void setup()
        throws
            IOException
    {
        super.setup();

        theServer.setResponseFactory( new MyResponseFactory( theXrdsFileName, theDelay ));
    }

    /**
     * Constructor with parameters.
     * 
     * @param xrdsFileName file name of the XRDS file.
     * @param delay the web server time delay
     * @throws Exception all sorts of things may happen during a test
     */
    public YadisTest7(
            String xrdsFileName,
            long   delay )
        throws
            Exception
    {
        theXrdsFileName = xrdsFileName;
        theDelay        = delay;
    }

    /**
     * Name of the file containing the XRDS.
     */
    protected String theXrdsFileName;

    /**
     * The Web server delay.
     */
    protected long theDelay;

    // Our Logger
    private static Log log = Log.getLogInstance( YadisTest7.class );

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
         * @param xrdsFileName file name of the XRDS file.
         * @param delay the delay by the web server
         */
        public MyResponseFactory(
                String xrdsFileName,
                long   delay )
        {
            theXrdsFileName = xrdsFileName;
            theDelay        = delay;
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
            HttpResponse ret;

            if( "GET".equals( request.getMethod() ) && ( "/" ).equals( request.getRelativeBaseUri() )) {
                HttpEntity entity = new HttpEntity() {
                        public boolean canRead() {
                            return true;
                        }
                        public InputStream getAsStream() {
                            try {
                                return new ByteArrayInputStream( HTML.getBytes( "UTF-8" ) );

                            } catch( UnsupportedEncodingException ex ) {
                                log.error( ex );
                                return null;
                            }
                        }
                        public String getMime() {
                            return "text/html";
                        }
                };
                ret = HttpEntityResponse.create( request, true, entity );
                ret.addHeader( "x-xrds-location", "http://localhost:" + SERVER_PORT + "/auth/xrds" );

            } else if( "GET".equals( request.getMethod() ) && ( "/auth/xrds" ).equals( request.getRelativeBaseUri() )) {
                HttpEntity entity = new HttpEntity() {
                        public boolean canRead() {
                            return true;
                        }
                        public InputStream getAsStream()
                                throws IOException
                        {
                            return new FileInputStream( theXrdsFileName );
                        }
                        public String getMime() {
                            return "application/xrds+xml";
                        }
                };
                ret = HttpEntityResponse.create( request, true, entity );

            } else if( "GET".equals( request.getMethod() ) && ( "/auth/openid_response" ).equals( request.getRelativeBaseUri() )) {
                HttpEntity entity = new HttpEntity() {
                        public boolean canRead() {
                            return true;
                        }
                        public InputStream getAsStream() {
                            try {
                                return new ByteArrayInputStream( "Content of /auth/openid_response".getBytes( "UTF-8" ) );
                            } catch( UnsupportedEncodingException ex ) {
                                log.error( ex );
                                return null;
                            }
                        }
                        public String getMime() {
                            return "text/plain";
                        }
                };
                ret = HttpEntityResponse.create( request, true, entity );

            } else if( "GET".equals( request.getMethod() ) && ( "/auth/immediate_response" ).equals( request.getRelativeBaseUri() )) {
                HttpEntity entity = new HttpEntity() {
                        public boolean canRead() {
                            return true;
                        }
                        public InputStream getAsStream() {
                            try {
                                return new ByteArrayInputStream( "Content of /auth/immediate_response".getBytes( "UTF-8" ) );
                            } catch( UnsupportedEncodingException ex ) {
                                log.error( ex );
                                return null;
                            }
                        }
                        public String getMime() {
                            return "text/plain";
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
         * The name of the XRDS file.
         */
        protected String theXrdsFileName;

        /**
         * Captures the slowness of a web server.
         */
        protected long theDelay;
    }
}
