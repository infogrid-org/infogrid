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

package org.infogrid.probe.xrd.test;

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.DefaultNetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.DefaultNetMeshObjectAccessSpecificationFactory;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.local.LocalNetMeshBase;
import org.infogrid.meshbase.net.local.m.LocalNetMMeshBase;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.modelbase.ModelBaseSingleton;
import org.infogrid.probe.ProbeDirectory.XmlDomProbeDescriptor;
import org.infogrid.probe.m.MProbeDirectory;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.probe.xrd.XrdProbe;
import org.infogrid.util.context.Context;
import org.infogrid.util.context.SimpleContext;
import org.infogrid.util.logging.Log;

/**
 * Tests parsing an XRD file.
 */
public class XrdTest1
        extends
            AbstractXrdTest
{
    /**
     * Run one test scenario.
     *
     * @throws Exception all sorts of things may happen during a test
     */
    public void run()
        throws
            Exception
    {
        log.info( "Accessing test data source" );

        NetMeshObject  shadowHome = theMeshBase.accessLocally( theTestFileId, CoherenceSpecification.ONE_TIME_ONLY );
        ShadowMeshBase shadow     = theMeshBase.getShadowMeshBaseFor( theTestFileId );

        checkEquals( shadow.size(), theExpectedNumberMeshObjects, "Wrong number of objects found" );

        dumpMeshBase( shadow, "Shadow: ", log );
    }

    /**
     * Main program.
     *
     * @param args command-line arguments
     */
    public static void main(
            String [] args )
    {
        XrdTest1 test = null;
        try {
            if( args.length != 1 ) {
                System.err.println( "Synopsis: <test file> <expected number of objects>" );
                System.err.println( "aborting ..." );
                System.exit( 1 );
            }

            test = new XrdTest1( args );
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
    public XrdTest1(
            String [] args )
        throws
            Exception
    {
        super( XrdTest1.class );

        theTestFileId = theMeshBaseIdentifierFactory.obtain( new File( args[0] ));
        theMeshBase   = LocalNetMMeshBase.create(
                theMeshBaseId,
                DefaultNetMeshObjectAccessSpecificationFactory.create(
                        theMeshBaseId,
                        theMeshBaseIdentifierFactory ),
                theModelBase,
                null,
                theProbeDirectory,
                exec,
                rootContext );

        theExpectedNumberMeshObjects = Integer.parseInt( args[1] );
    }

    /**
     * Clean up after the test.
     */
    @Override
    public void cleanup()
    {
        theMeshBase.die();

        exec.shutdown();
    }

    /**
     * The root context for these tests.
     */
    protected static final Context rootContext = SimpleContext.createRoot( "root-context" );

    /**
     * The ModelBase.
     */
    protected ModelBase theModelBase = ModelBaseSingleton.getSingleton();

    /**
     * Factory for NetMeshBaseIdentifiers.
     */
    protected NetMeshBaseIdentifierFactory theMeshBaseIdentifierFactory = DefaultNetMeshBaseIdentifierFactory.create(
            new String[] { "http", "file" },
            new String[] { "test", "acct" } );

    /**
     * The ProbeDirectory.
     */
    protected static final MProbeDirectory theProbeDirectory = MProbeDirectory.create();
    static {
        theProbeDirectory.addXmlDomProbe( new XmlDomProbeDescriptor(
                "XRD",
                "http://docs.oasis-open.org/ns/xri/xrd-1.0",
                "XRD",
                XrdProbe.class ));
    }

    /**
     * The main NetMeshBaseIdentifier.
     */
    protected NetMeshBaseIdentifier theMeshBaseId = theMeshBaseIdentifierFactory.fromExternalForm( "test://one.local" );

    /**
     * The main NetMeshBase.
     */
    protected LocalNetMeshBase theMeshBase;

    /**
     * Our ThreadPool.
     */
    protected ScheduledExecutorService exec = createThreadPool( 1 );

    /**
     * The Identifier of the test file.
     */
    protected NetMeshBaseIdentifier theTestFileId;

    /**
     * The expected number of MeshObjects in the test file.
     */
    protected int theExpectedNumberMeshObjects;

    // Our Logger
    private static Log log = Log.getLogInstance( XrdTest1.class);
}
