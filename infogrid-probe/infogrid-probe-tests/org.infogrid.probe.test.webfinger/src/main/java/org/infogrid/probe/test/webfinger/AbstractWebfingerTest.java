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

package org.infogrid.probe.test.webfinger;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import org.infogrid.httpd.HttpResponseFactory;
import org.infogrid.httpd.server.HttpServer;
import org.infogrid.mesh.MeshObject;
import org.infogrid.meshbase.IterableMeshBase;
import org.infogrid.meshbase.net.DefaultNetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.local.m.LocalNetMMeshBase;
import org.infogrid.meshbase.net.schemes.AcctScheme;
import org.infogrid.meshbase.net.schemes.HttpScheme;
import org.infogrid.meshbase.net.schemes.Scheme;
import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.PropertyType;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.modelbase.ModelBaseSingleton;
import org.infogrid.probe.ProbeDirectory;
import org.infogrid.probe.blob.BlobProbe;
import org.infogrid.probe.m.MProbeDirectory;
import org.infogrid.probe.xrd.WebfingerAcctProbe;
import org.infogrid.probe.xrd.XrdProbe;
import org.infogrid.testharness.AbstractTest;
import org.infogrid.util.context.Context;
import org.infogrid.util.context.SimpleContext;
import org.infogrid.util.http.HTTP;
import org.infogrid.util.logging.Log;

/**
 * Factors out the commonalities of the Webfinger tests.
 */
public abstract class AbstractWebfingerTest
        extends
            AbstractTest
{
    private static final Log log = Log.getLogInstance( AbstractWebfingerTest.class ); // our own, private logger

    /**
     * Constructor.
     *
     * @param testClass the class to test
     * @param factory the factory for HttpResponses
     * @throws IOException thrown if the HttpServer could not be started
     */
    protected AbstractWebfingerTest(
            Class               testClass,
            HttpResponseFactory factory )
        throws
            IOException
    {
        super( localFileName( testClass, "/ResourceHelper" ));

        theServer = new HttpServer( SERVER_PORT, NUMBER_THREADS );
        theServer.setResponseFactory( factory );

        // start the server
        theServer.start();

        // Probe directory has Probe for plain text needed in YadisTest6

        theProbeDirectory.addStreamProbe( new ProbeDirectory.StreamProbeDescriptor( "text/plain", BlobProbe.class ));
        theProbeDirectory.addApiProbe( new ProbeDirectory.ApiProbeDescriptor( "acct", WebfingerAcctProbe.class ));
        theProbeDirectory.addXmlDomProbe( new ProbeDirectory.XmlDomProbeDescriptor( "application/xrd+xml", "http://docs.oasis-open.org/ns/xri/xrd-1.0", "XRD", XrdProbe.class ));

        // MeshBase
        exec = createThreadPool( 1 );

        theMeshBase1 = LocalNetMMeshBase.create( here1, theModelBase, null, theProbeDirectory, exec, rootContext );
        theMeshBase2 = LocalNetMMeshBase.create( here2, theModelBase, null, theProbeDirectory, exec, rootContext );
    }

    /**
     * Clean up after the test.
     */
    @Override
    public void cleanup()
    {
        try {
            Thread.sleep( 200L ); // just a bit, so ripple effects can have died down
        } catch( Throwable t ) {
            log.error( t );
        }

        theMeshBase1.die();
        theMeshBase1 = null;

        if( theServer != null ) {
            theServer.stop();
        }
        exec.shutdown();
    }

    /**
     * Dump the content of a MeshBase to log.debug().
     *
     * @param mb the MeshBase whose content we want to dump
     * @param prefix a string to prepend
     * @param mylog the Log to dump to
     * @throws Exception all sorts of things may go wrong during a test
     */
    protected final void dumpMeshBase(
            IterableMeshBase mb,
            String           prefix,
            Log              mylog )
        throws
            Exception
    {
        if( mylog.isDebugEnabled() ) {
            StringBuilder buf = new StringBuilder( prefix );
            for( MeshObject current : mb ) {
                buf.append( "\n  " );
                buf.append( current.getIdentifier() );
                buf.append( " (created: " );
                buf.append( current.getTimeCreated() );
                buf.append( " updated: " );
                buf.append( current.getTimeUpdated() );
                buf.append( " read: " );
                buf.append( current.getTimeRead() );
                buf.append( ")" );

                if( true ) {
                    buf.append( "\n    Types:" );
                    for( EntityType et : current.getTypes() ) {
                        buf.append( "\n        " );
                        buf.append( et.getName().value() );
                    }
                }
                if( true ) {
                    buf.append( "\n    Properties:" );
                    for( PropertyType pt : current.getAllPropertyTypes() ) {
                        buf.append( "\n        " );
                        buf.append( pt.getName().value() );
                        buf.append( ": " );
                        buf.append( current.getPropertyValue( pt ));
                    }
                }
            }
            mylog.debug( buf.toString() );
        }
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
    protected MProbeDirectory theProbeDirectory = MProbeDirectory.create();

    /**
     * The first test NetMeshBase.
     */
    protected LocalNetMMeshBase theMeshBase1;

    /**
     * The second test NetMeshBase.
     */
    protected LocalNetMMeshBase theMeshBase2;

    /**
     * The root context for these tests.
     */
    protected static final Context rootContext = SimpleContext.createRoot( "root-context" );

    /**
     * The ModelBase.
     */
    protected static ModelBase theModelBase = ModelBaseSingleton.getSingleton();

    /**
     * Factory for NetMeshBaseIdentifiers.
     */
    protected static final NetMeshBaseIdentifierFactory theMeshBaseIdentifierFactory = DefaultNetMeshBaseIdentifierFactory.create(
            new Scheme [] {
                    new HttpScheme(),
                    new AcctScheme()
             } );

    /**
     * Expected duration within which at least one ping-pong round trip can be completed.
     * Milliseconds.
     */
    protected static final long PINGPONG_ROUNDTRIP_DURATION = 100L;

    /**
     * The identifier of the first test NetMeshBase.
     */
    protected static final NetMeshBaseIdentifier here1;

    /**
     * The identifier of the second test NetMeshBase.
     */
    protected static final NetMeshBaseIdentifier here2;
    static {
        NetMeshBaseIdentifier temp1;
        NetMeshBaseIdentifier temp2;
        try {
            temp1 = theMeshBaseIdentifierFactory.fromExternalForm( "http://here1.local/" ); // this is not going to work for communications
            temp2 = theMeshBaseIdentifierFactory.fromExternalForm( "http://here2.local/" ); // this is not going to work for communications
        } catch( Exception ex ) {
            log.error( ex );
            temp1 = null; // make compiler happy
            temp2 = null; // make compiler happy
        }
        here1 = temp1;
        here2 = temp2;
    }

    /**
     * Root identifier of the web server.
     */
    protected static final String WEB_SERVER_IDENTIFIER = "http://localhost:" + SERVER_PORT + "/";

    /**
     * Identifier of the XRD file.
     */
    protected static final String XRD_LOCAL_IDENTIFIER = "xrd";

    /**
     * Identifier of the identity.
     */
    protected static final String IDENTITY_IDENTIFIER = "foo@localhost";

    /**
     * Identifier of the XRD file.
     */
    protected static final String XRS_IDENTIFIER = WEB_SERVER_IDENTIFIER + XRD_LOCAL_IDENTIFIER;

    /**
     * The NetMeshBaseIdentifier of the first test file.
     */
    protected static final NetMeshBaseIdentifier theIdentityIdentifier;
    static {
        NetMeshBaseIdentifier temp = null;
        try {
            temp = theMeshBaseIdentifierFactory.guessFromExternalForm( IDENTITY_IDENTIFIER );
        } catch( Throwable t ) {
            log.error( t );
        }
        theIdentityIdentifier = temp;
    }

    /**
     * The identity-specific XRD file. This is modeled after Google's.
     */
    protected static final String IDENTITY_XRD
            = "<?xml version=\"1.0\"?>\n"
            + "<XRD xmlns=\"http://docs.oasis-open.org/ns/xri/xrd-1.0\">\n"
            + "  <Subject>acct:" + IDENTITY_IDENTIFIER + "</Subject>\n"
            + "  <Alias>http://www.google.com/profiles/foo</Alias>\n"
            + "  <Link rel=\"http://portablecontacts.net/spec/1.0\" href=\"http://www-opensocial.googleusercontent.com/api/people/\"/>\n"
            + "  <Link rel=\"http://portablecontacts.net/spec/1.0#me\" href=\"http://www-opensocial.googleusercontent.com/api/people/1122334455/\"/>\n"
            + "  <Link rel=\"http://specs.openid.net/auth/2.0/provider\" href=\"http://www.google.com/profiles/foo\"/>\n"
            + "  <Link rel=\"describedby\" href=\"http://www.google.com/s2/webfinger/?q=" + HTTP.encodeToValidUrlArgument( IDENTITY_IDENTIFIER ) + "&amp;fmt=foaf\" type=\"application/rdf+xml\"/>\n"
            + "</XRD>\n";
}
