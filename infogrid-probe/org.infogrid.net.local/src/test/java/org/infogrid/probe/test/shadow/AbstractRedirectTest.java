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

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import org.infogrid.httpd.HttpEntity;
import org.infogrid.httpd.HttpEntityResponse;
import org.infogrid.httpd.HttpErrorResponse;
import org.infogrid.httpd.HttpRedirectResponse;
import org.infogrid.httpd.HttpRequest;
import org.infogrid.httpd.HttpResponse;
import org.infogrid.httpd.HttpResponseFactory;
import org.infogrid.httpd.SnapshotHttpEntity;
import org.infogrid.httpd.server.HttpServer;
import org.infogrid.meshbase.net.DefaultNetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.local.m.LocalNetMMeshBase;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.modelbase.ModelBaseSingleton;
import org.infogrid.module.inclasspath.InClasspathModuleRegistry;
import org.infogrid.probe.ProbeDirectory;
import org.infogrid.probe.blob.BlobProbe;
import org.infogrid.probe.m.MProbeDirectory;
import org.infogrid.probe.test.forwardreference.AbstractForwardReferenceTest;
import org.infogrid.testharness.AbstractTest;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.context.Context;
import org.infogrid.util.context.SimpleContext;
import org.infogrid.util.logging.Log;
import org.infogrid.util.logging.log4j.Log4jLog;
import org.infogrid.util.logging.log4j.Log4jLogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

/**
  * Collects functionality common to RedirectTests
  */
public abstract class AbstractRedirectTest
        extends
            AbstractTest
{
    private static final Log log = Log.getLogInstance( AbstractRedirectTest.class ); // our own, private logger

    /**
     * Initialize Module Framework, and initialize statics.
     * 
     * @throws Exception all sorts of things may go wrong in tests
     */
    @BeforeClass
    public static void initialize()
        throws
            Exception
    {
        InClasspathModuleRegistry registry = InClasspathModuleRegistry.getSingleton();
        registry.resolve( registry.getModuleMetaFor( "org.infogrid.probe" )).activateRecursively();
        registry.resolve( registry.getModuleMetaFor( "org.infogrid.model.Test" )).activateRecursively();
        
        Log4jLog.configure( "org/infogrid/probe/test/Log.properties", AbstractForwardReferenceTest.class.getClassLoader() );
        Log.setLogFactory( new Log4jLogFactory());
        
        ResourceHelper.setApplicationResourceBundle( ResourceBundle.getBundle(
                "org/infogrid/probe/test/ResourceHelper",
                Locale.getDefault(),
                AbstractForwardReferenceTest.class.getClassLoader() ));

        theModelBase = ModelBaseSingleton.getSingleton();
    }
    
    /**
     * Setup.
     * 
     * @throws Exception all sorts of things may go wrong in tests
     */
    @Before
    public void setup()
        throws
            Exception
    {
        theProbeDirectory = MProbeDirectory.create();
        theProbeDirectory.addStreamProbe( new ProbeDirectory.StreamProbeDescriptor( "text/html", BlobProbe.class ));

        theServer = new HttpServer( SERVER_PORT, NUMBER_THREADS );
        theServer.setResponseFactory( new MyResponseFactory( 1000L, REDIRECT_STATUS ) );

        // start the server
        theServer.start();

        // MeshBase
        exec = createThreadPool( 1 );

        theMeshBase = LocalNetMMeshBase.create( here, theModelBase, null, theProbeDirectory, exec, rootContext );
    }

    /**
     * Clean up after the test.
     */
    @After
    public void cleanup()
    {
        try {
            Thread.sleep( 200L ); // just a bit, so ripple effects can have died down
        } catch( Throwable t ) {
            log.error( t );
        }

        theMeshBase.die();
        theMeshBase = null;

        if( theServer != null ) {
            theServer.stop();
            theServer = null;
        }
        exec.shutdown();
        exec = null;
    }

    /**
     * Our HTTP Server.
     */
    protected HttpServer theServer;

    /**
     * The port on which we run our server.
     */
    protected static final int SERVER_PORT = 8081;

    /**
     * The number of threads to create in the server. FIXME? Should we run all tests
     * with different numbers of threads?
     */
    protected static final int NUMBER_THREADS = 0;

    /**
     * Our ThreadPool.
     */
    protected ScheduledExecutorService exec;

    /**
     * The ProbeDirectory to use.
     */
    protected MProbeDirectory theProbeDirectory;

    /**
     * The test NetMeshBase.
     */
    protected LocalNetMMeshBase theMeshBase;

    /**
     * The root context for these tests.
     */
    protected static final Context rootContext = SimpleContext.createRoot( "root-context" );

    /**
     * The ModelBase.
     */
    protected static ModelBase theModelBase;

    /**
     * Factory for NetMeshBaseIdentifiers.
     */
    protected static final NetMeshBaseIdentifierFactory theMeshBaseIdentifierFactory = DefaultNetMeshBaseIdentifierFactory.create();

    /**
     * Expected duration within which at least one ping-pong round trip can be completed.
     * Milliseconds.
     */
    protected static final long PINGPONG_ROUNDTRIP_DURATION = 100L;

    /**
     * The identifier of the test NetMeshBase.
     */
    protected static final NetMeshBaseIdentifier here;

    static {
        NetMeshBaseIdentifier temp1;
        try {
            temp1 = theMeshBaseIdentifierFactory.fromExternalForm( "http://here1.local/" ); // this is not going to work for communications
        } catch( Exception ex ) {
            log.error( ex );
            temp1 = null; // make compiler happy
        }
        here = temp1;
    }


    /**
     * Root identifier of the web server.
     */
    protected static final String WEB_SERVER_IDENTIFIER = "http://localhost:" + SERVER_PORT + "/";

    /**
     * The HTML data to be returned.
     */
    protected static final String HTML
            = "<html>\n"
            + " <head>\n"
            + "  <title>Test file</title>\n"
            + " </head>\n"
            + " <body>\n"
            + "  <h1>Test file</h1>\n"
            + " </body>\n"
            + "</html>\n";

    /**
     * Prefix of incoming relative URLs that indicate we should issue a redirect response.
     */
    protected final static String REDIRECT_PREFIX = "redirect";

    /**
     * Incoming relative URL that indicates we are at the final destination.
     */
    protected final static String RESULT = "result";

    /**
     * HTTP status code for the redirect.
     */
    protected final static String REDIRECT_STATUS = "302";

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
         * @param redirectCode the HTTP redirect status code to send in case of redirects
         */
        MyResponseFactory(
                long   delay,
                String redirectCode )
        {
            theDelay        = delay;
            theRedirectCode = redirectCode;
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
            if( "GET".equals( request.getMethod() ) && ( request.getRelativeBaseUri().startsWith( "/" + REDIRECT_PREFIX ))) {
                int number = Integer.parseInt( request.getRelativeBaseUri().substring( 1+REDIRECT_PREFIX.length() ) );
                if( number > 1 ) {
                    --number;
                    ret = HttpRedirectResponse.create( request, WEB_SERVER_IDENTIFIER + REDIRECT_PREFIX + String.valueOf( number), theRedirectCode );
                } else {
                    ret = HttpRedirectResponse.create( request, WEB_SERVER_IDENTIFIER + RESULT, theRedirectCode );
                }

            } else if( "GET".equals( request.getMethod() ) && ( request.getRelativeBaseUri().equals( "/" + RESULT ))) {
                HttpEntity entity = new SnapshotHttpEntity( "text/html", HTML, true );
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

        /**
         * The HTTP redirect status code to send.
         */
        protected String theRedirectCode;
    }
}
