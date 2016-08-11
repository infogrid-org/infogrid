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

package org.infogrid.probe.store.test;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;
import org.infogrid.mesh.MeshObject;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.proxy.m.MPingPongNetMessageEndpointFactory;
import org.infogrid.model.Test.TestSubjectArea;
import org.infogrid.probe.manager.store.StoreScheduledExecutorProbeManager;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.probe.shadow.store.StoreShadowMeshBaseFactory;
import org.infogrid.store.prefixing.PrefixingStore;
import org.infogrid.testharness.AbstractTest;
import org.infogrid.util.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Tests that Shadows resume correctly after they have been rebooted. This does not test
 * that the Shadows' Proxies resume correctly, only the Shadow/Probe updates.
 */
@RunWith(Parameterized.class)
public class StoreShadowMeshBaseTest6
        extends
            AbstractStoreProbeTest
{
    /**
     * Test parameters.
     *
     * @return test parameters
     */
    @Parameterized.Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList( new Object[][] {
                {
                    AbstractTest.tempInputFileName( StoreShadowMeshBaseTest6.class, "test6-1.xml" ),
                    AbstractTest.fileSystemFileName( StoreShadowMeshBaseTest6.class, "StoreShadowMeshBaseTest6_1a.xml" ),
                    AbstractTest.fileSystemFileName( StoreShadowMeshBaseTest6.class, "StoreShadowMeshBaseTest6_1b.xml" )
                }
        });
    }

    /**
     * Run the test.
     *
     * @throws Exception all sorts of things may go wrong in tests
     */
    @Test
    public void run()
        throws
            Exception
    {
        MPingPongNetMessageEndpointFactory shadowEndpointFactory = MPingPongNetMessageEndpointFactory.create( exec );

        PrefixingStore theShadowStore      = PrefixingStore.create( "Shadow",      theSqlStore );
        PrefixingStore theShadowProxyStore = PrefixingStore.create( "ShadowProxy", theSqlStore );

        StoreShadowMeshBaseFactory shadowFactory1
                = StoreShadowMeshBaseFactory.create(
                        theMeshBaseIdentifierFactory,
                        shadowEndpointFactory,
                        theModelBase,
                        theShadowStore,
                        theShadowProxyStore,
                        rootContext );
        //

        startClock();

        log.info( "Setting up ProbeManager1" );

        StoreScheduledExecutorProbeManager probeManager1 = StoreScheduledExecutorProbeManager.create( shadowFactory1, theProbeDirectory, theSqlStore );
        shadowEndpointFactory.setNameServer( probeManager1.getNetMeshBaseNameServer() );
        shadowFactory1.setProbeManager( probeManager1 );

        probeManager1.start( exec );

        //

        log.info( "accessing test files with meshBase: " + theTestFile1Id.toExternalForm() );

        copyFile(theTestFile1a, theTestFile1 );

        ShadowMeshBase meshBase1 =
                probeManager1.obtainFor(theTestFile1Id,
                    // CoherenceSpecification.ONE_TIME_ONLY );
                    new CoherenceSpecification.Periodic( 2000L ));
        checkObject( meshBase1, "MeshBase1 not created" );

        MeshObject home1 = meshBase1.getHomeObject();
        checkObject( home1, "no home object found" );
        checkCondition( !home1.isBlessedBy( TestSubjectArea.AA ), "Home object 1 incorrectly blessed" );

        //

        log.info( "Checking that ProbeManager and Shadow go away when not referenced" );

        WeakReference<StoreScheduledExecutorProbeManager> probeManager1Ref = new WeakReference<>( probeManager1 );
        WeakReference<ShadowMeshBase>                     meshBase1Ref     = new WeakReference<>( meshBase1 );

        shadowFactory1 = null;
        probeManager1  = null;
        meshBase1      = null;
        home1          = null;
        shadowEndpointFactory.setNameServer( null );

        sleepUntilIsGone( probeManager1Ref, 4000L, "ProbeManager1 still here, should have been garbage collected" );
        sleepUntilIsGone( meshBase1Ref,     4000L, "ShadowMeshBase1 still here, should have been garbage collected" );

        //

        log.info( "Creating new ShadowFactory" );

        StoreShadowMeshBaseFactory shadowFactory2
                = StoreShadowMeshBaseFactory.create(
                        theMeshBaseIdentifierFactory,
                        shadowEndpointFactory,
                        theModelBase,
                        theShadowStore,
                        theShadowProxyStore,
                        rootContext );

        //

        log.info( "Creating new ProbeManager with old data and new feed" );

        copyFile(theTestFile1b, theTestFile1 );

        StoreScheduledExecutorProbeManager probeManager2 = StoreScheduledExecutorProbeManager.create( shadowFactory2, theProbeDirectory, theSqlStore );
        shadowEndpointFactory.setNameServer( probeManager2.getNetMeshBaseNameServer() );
        shadowFactory2.setProbeManager( probeManager2 );

        probeManager2.start( exec );

        sleepUntil( 6500L );

        //

        log.info( "Accessing old object again" );

        ShadowMeshBase meshBase2 = probeManager2.get(theTestFile1Id );
        checkObject( meshBase2, "MeshBase2 not created" );

        MeshObject home2 = meshBase2.getHomeObject();
        checkObject( home2, "no home object found" );
        checkCondition( home2.isBlessedBy( TestSubjectArea.AA ), "Home object 2 not blessed" );
    }

    /**
     * Constructor with parameters.
     *
     * @param testFile1 filename of test
     * @param testFile1a filename of test
     * @param testFile1b filename of test
     */
    public StoreShadowMeshBaseTest6(
            String testFile1,
            String testFile1a,
            String testFile1b )
    {
        theTestFile1  = testFile1;
        theTestFile1a = testFile1a;
        theTestFile1b = testFile1b;
    }

    /**
     * Setup.
     *
     * @throws Exception all sorts of things may go wrong in tests
     */
    @Before
    @Override
    public void setup()
        throws
            Exception
    {
        super.setup();

        theTestFile1Id  = theMeshBaseIdentifierFactory.obtain( new File( theTestFile1 ) );

        //

        log.info( "Deleting old database and creating new database" );

        theSqlStore.initializeHard();

        exec = createThreadPool( 1 );
}

    /**
     * Clean up after the test.
     */
    @After
    public void cleanup()
    {
        exec.shutdown();
        exec = null;
    }

    // Our Logger
    private static final Log log = Log.getLogInstance( StoreShadowMeshBaseTest6.class);

    /**
     * Our ThreadPool.
     */
    protected ScheduledExecutorService exec;

    /**
     * File name of the first test file.
     */
    protected String theTestFile1;

    /**
     * File name of the first test file.
     */
    protected String theTestFile1a;

    /**
     * File name of the first test file.
     */
    protected String theTestFile1b;

    /**
     * The NetworkIdentifer of the first test file.
     */
    protected NetMeshBaseIdentifier theTestFile1Id;
}
