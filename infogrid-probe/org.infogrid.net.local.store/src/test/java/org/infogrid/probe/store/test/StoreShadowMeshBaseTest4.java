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
import org.infogrid.model.primitives.IntegerValue;
import org.infogrid.model.Probe.ProbeSubjectArea;
import org.infogrid.model.Test.TestSubjectArea;
import org.infogrid.meshbase.net.proxy.m.MPingPongNetMessageEndpointFactory;
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
 * Tests that shadows that are not currently in memory are still being updated.
 */
@RunWith(Parameterized.class)
public class StoreShadowMeshBaseTest4
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
                    AbstractTest.tempInputFileName( StoreShadowMeshBaseTest4.class, "test4-1.xml" ),
                    AbstractTest.fileSystemFileName( StoreShadowMeshBaseTest4.class, "StoreShadowMeshBaseTest4_1a.xml" ),
                    AbstractTest.fileSystemFileName( StoreShadowMeshBaseTest4.class, "StoreShadowMeshBaseTest4_1b.xml" )
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
        copyFile(theTestFile1a, theTestFile1 );

        //

        startClock();

        log.info( "accessing test files with meshBase: " + testFile1Id.toExternalForm() );

        ShadowMeshBase meshBase1 = theProbeManager1.obtainFor(
                    testFile1Id,
                    new CoherenceSpecification.Periodic( 3000L ));
        checkObject( meshBase1, "MeshBase1 not created" );

        MeshObject home1 = meshBase1.getHomeObject();
        checkObject( home1, "no home object found" );
        checkCondition( !home1.isBlessedBy( TestSubjectArea.AA ), "Home object 1 incorrectly blessed" );
        checkEquals( home1.getPropertyValue( ProbeSubjectArea.PROBEUPDATESPECIFICATION_PROBERUNCOUNTER ), IntegerValue.create( 1 ), "Wrong number of probe runs" );

        //

        log.info( "Checking that Shadow goes away when not referenced" );

        WeakReference<ShadowMeshBase> meshBase1Ref = new WeakReference<ShadowMeshBase>( meshBase1 );

        meshBase1 = null;
        home1     = null;

        sleepUntilIsGone( meshBase1Ref, 4000L, "ShadowMeshBase still here, should have been garbage collected" );

        copyFile(theTestFile1b, theTestFile1 );

        sleepUntil( 3000L * 3 + 1000L );

        //

        log.info( "Checking that update has been performed" );

        ShadowMeshBase meshBase2 = theProbeManager1.get( testFile1Id );
        checkObject( meshBase2, "MeshBase1 not recovered" );

        MeshObject home2 = meshBase2.getHomeObject();
        checkObject( home2, "no home object found" );
        checkCondition( home2.isBlessedBy( TestSubjectArea.AA ), "Home object 1 incorrectly not blessed" );
        checkEquals( home2.getPropertyValue( ProbeSubjectArea.PROBEUPDATESPECIFICATION_PROBERUNCOUNTER ), IntegerValue.create( 4 ), "Wrong number of probe runs" );
    }

    /**
     * Constructor with parameters.
     *
     * @param testFile1 filename of test
     * @param testFile1a filename of test
     * @param testFile1b filename of test
     */
    public StoreShadowMeshBaseTest4(
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

        testFile1Id    = theMeshBaseIdentifierFactory.obtain( new File( theTestFile1 ) );

        //

        log.info( "Deleting old database and creating new database" );

        theSqlStore.initializeHard();

        theSqlStore.addDirectStoreListener( theTestStoreListener );

        PrefixingStore theShadowStore      = PrefixingStore.create( "Shadow",      theSqlStore );
        PrefixingStore theShadowProxyStore = PrefixingStore.create( "ShadowProxy", theSqlStore );

        //

        exec = createThreadPool( 1 );

        MPingPongNetMessageEndpointFactory shadowEndpointFactory = MPingPongNetMessageEndpointFactory.create( exec );

        StoreShadowMeshBaseFactory shadowFactory = StoreShadowMeshBaseFactory.create(
                theMeshBaseIdentifierFactory,
                shadowEndpointFactory,
                theModelBase,
                theShadowStore,
                theShadowProxyStore,
                rootContext );

        theProbeManager1 = StoreScheduledExecutorProbeManager.create( shadowFactory, theProbeDirectory, theSqlStore );
        shadowEndpointFactory.setNameServer( theProbeManager1.getNetMeshBaseNameServer() );
        shadowFactory.setProbeManager( theProbeManager1 );

        theProbeManager1.start( exec );
    }

    /**
     * Clean up after the test.
     */
    @After
    public void cleanup()
    {
        theProbeManager1.die( true );
        theProbeManager1 = null;

        exec.shutdown();
        exec = null;
    }

    // Our Logger
    private static Log log = Log.getLogInstance( StoreShadowMeshBaseTest4.class);

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
    protected NetMeshBaseIdentifier testFile1Id;

    /**
     * The ProbeManager.
     */
    protected StoreScheduledExecutorProbeManager theProbeManager1;

    /**
     * StoreListener for debugging purposes.
     */
    protected TestStoreListener theTestStoreListener = new TestStoreListener();
}
