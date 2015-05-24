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

package org.infogrid.probe.xrd.test;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Pattern;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.DefaultNetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.DefaultNetMeshObjectAccessSpecificationFactory;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.local.LocalNetMeshBase;
import org.infogrid.meshbase.net.local.m.LocalNetMMeshBase;
import org.infogrid.meshbase.net.schemes.AcctScheme;
import org.infogrid.meshbase.net.schemes.FileScheme;
import org.infogrid.meshbase.net.schemes.HttpScheme;
import org.infogrid.meshbase.net.schemes.Scheme;
import org.infogrid.meshbase.net.schemes.StrictRegexScheme;
import org.infogrid.probe.ProbeDirectory.XmlDomProbeDescriptor;
import org.infogrid.probe.m.MProbeDirectory;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.probe.xrd.XrdProbe;
import org.infogrid.util.context.Context;
import org.infogrid.util.context.SimpleContext;
import org.infogrid.util.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests parsing an XRD file.
 */
@RunWith(Parameterized.class)
public class XrdTest1
        extends
            AbstractXrdTest
{
    /**
     * Test parameters.
     * 
     * @return test parameters
     */
    @Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList( new Object[][] {
                 { "src/test/resources/org/infogrid/probe/xrd/test/XrdTest1a.xml",  6 },
                 { "src/test/resources/org/infogrid/probe/xrd/test/XrdTest1b.xml",  6 },
                 { "src/test/resources/org/infogrid/probe/xrd/test/XrdTest1c.xml", 14 },
                 { "src/test/resources/org/infogrid/probe/xrd/test/XrdTest1d.xml",  2 }
        });
    }

    /**
     * Run one test scenario.
     *
     * @throws Exception all sorts of things may happen during a test
     */
    @Test
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
     * Setup.
     *
     * @throws Exception all sorts of things may happen during a test
     */
    @Before
    public void setup()
        throws
            Exception
    {
        theMeshBaseId = theMeshBaseIdentifierFactory.fromExternalForm( "test://one.local" );
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

    }

    /**
     * Clean up after the test.
     */
    @After
    public void cleanup()
    {
        theMeshBase.die();

        exec.shutdown();
    }

    /**
     * Constructor that takes parameters.
     * 
     * @param fileName the XRD file to read
     * @param expectedNumberMeshObjects the number of MeshObjects in the file
     * @throws Exception all sorts of things may happen during a test
     */
    public XrdTest1(
            String fileName,
            int    expectedNumberMeshObjects )
        throws
            Exception
    {
        theTestFileId = theMeshBaseIdentifierFactory.obtain( new File( fileName ));
        
        theExpectedNumberMeshObjects = expectedNumberMeshObjects;
    }

    /**
     * The root context for these tests.
     */
    protected static final Context rootContext = SimpleContext.createRoot( "root-context" );

    /**
     * Factory for NetMeshBaseIdentifiers.
     */
    protected NetMeshBaseIdentifierFactory theMeshBaseIdentifierFactory = DefaultNetMeshBaseIdentifierFactory.create(
            new Scheme [] {
                    new HttpScheme(),
                    new FileScheme(),
                    new AcctScheme(),
                    new StrictRegexScheme( "test", Pattern.compile( "test:.*" ))
             } );

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
    protected NetMeshBaseIdentifier theMeshBaseId;

    /**
     * The main NetMeshBase.
     */
    protected LocalNetMeshBase theMeshBase;

    /**
     * Our ThreadPool.
     */
    protected ScheduledExecutorService exec = createThreadPool( 1 );

    /**
     * The name of the test file.
     */
    protected NetMeshBaseIdentifier theTestFileId;

    /**
     * The expected number of MeshObjects in the test file.
     */
    protected int theExpectedNumberMeshObjects;

    // Our Logger
    private static Log log = Log.getLogInstance( XrdTest1.class);
}
