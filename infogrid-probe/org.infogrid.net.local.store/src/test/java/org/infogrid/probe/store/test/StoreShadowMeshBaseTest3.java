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
import org.infogrid.mesh.set.MeshObjectSet;
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
 * Tests that ProbeManagers resume Shadows correctly with manual trigger
 * after having been swapped out.
 */
@RunWith(Parameterized.class)
public class StoreShadowMeshBaseTest3
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
                    AbstractTest.tempInputFileName( StoreShadowMeshBaseTest3.class, "test3-active.xml" ),
                    AbstractTest.fileSystemFileName( StoreShadowMeshBaseTest3.class, "StoreProbeTest3_1.xml" ),
                    AbstractTest.fileSystemFileName( StoreShadowMeshBaseTest3.class, "StoreProbeTest3_2.xml" )
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
        copyFile( theTestFile1, theTestFile0 );

        //

        log.info( "accessing test file with meshBase" );

        ShadowMeshBase meshBase1 = theProbeManager1.obtainFor(theTestFile0Id, CoherenceSpecification.ONE_TIME_ONLY );
        checkObject( meshBase1, "MeshBase1 not created" );

        MeshObject home1 = meshBase1.getHomeObject();
        checkObject( home1, "no home object found" );
        checkCondition( home1.isBlessedBy( TestSubjectArea.AA ), "Home object not blessed" );
        checkEquals( home1.getPropertyValue( ProbeSubjectArea.PROBEUPDATESPECIFICATION_PROBERUNCOUNTER ), IntegerValue.create( 1 ), "Wrong number of probe runs" );

        MeshObject other1 = home1.traverseToNeighborMeshObjects().getSingleMember();
        checkObject( other1, "no other object found" );
        checkCondition( other1.isBlessedBy( TestSubjectArea.B ), "Other object not blessed" );

        //

        log.info( "Checking that Shadow goes away when not referenced" );

        WeakReference<ShadowMeshBase> meshBase1Ref = new WeakReference<>( meshBase1 );
        meshBase1 = null;
        home1     = null;
        other1    = null;

        sleepUntilIsGone( meshBase1Ref, 12000L, "ShadowMeshBase still here, should have been garbage collected" );

        //

        log.info( "updating file and recreating MeshBase" );

        copyFile(theTestFile2, theTestFile0 );

        ShadowMeshBase meshBase2 = theProbeManager1.obtainFor(theTestFile0Id, CoherenceSpecification.ONE_TIME_ONLY );
        meshBase2.doUpdateNow();

        //

        log.info( "Check that changes have propagated" );

        MeshObject home2 = meshBase2.getHomeObject();
        checkObject( home2, "no home object found" );
        checkCondition( home2.isBlessedBy( TestSubjectArea.B ), "Home object not blessed" );
        checkEquals( home2.getPropertyValue( ProbeSubjectArea.PROBEUPDATESPECIFICATION_PROBERUNCOUNTER ), IntegerValue.create( 2 ), "Wrong number of probe runs" );

        MeshObjectSet others2 = home2.traverseToNeighborMeshObjects();
        checkEquals( others2.size(), 2, "wrong number of neighbors found" );
    }

    /**
     * Constructor with parameters.
     *
     * @param testFile0 filename of test
     * @param testFile1 filename of test
     * @param testFile2 filename of test
     */
    public StoreShadowMeshBaseTest3(
            String testFile0,
            String testFile1,
            String testFile2 )
    {
        theTestFile0 = testFile0;
        theTestFile1 = testFile1;
        theTestFile2 = testFile2;
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

        theTestFile0Id    = theMeshBaseIdentifierFactory.obtain( new File( theTestFile0 ) );
        theTestFile1Id    = theMeshBaseIdentifierFactory.obtain( new File( theTestFile1 ) );
        theTestFile2Id    = theMeshBaseIdentifierFactory.obtain( new File( theTestFile2 ) );

        //

        log.info( "Deleting old database and creating new database" );

        theSqlStore.initializeHard();

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
    private static final Log log = Log.getLogInstance( StoreShadowMeshBaseTest3.class);

    /**
     * Our ThreadPool.
     */
    protected ScheduledExecutorService exec;

    /**
     * File name of the test file in the read position.
     */
    protected String theTestFile0;

    /**
     * File name of the first test file.
     */
    protected String theTestFile1;

    /**
     * File name of the second test file.
     */
    protected String theTestFile2;

    /**
     * The NetworkIdentifer of the test file in the read position.
     */
    protected NetMeshBaseIdentifier theTestFile0Id;

    /**
     * The NetworkIdentifer of the first test file.
     */
    protected NetMeshBaseIdentifier theTestFile1Id;

    /**
     * The NetworkIdentifer of the second test file.
     */
    protected NetMeshBaseIdentifier theTestFile2Id;

    /**
     * The ProbeManager.
     */
    protected StoreScheduledExecutorProbeManager theProbeManager1;
}
