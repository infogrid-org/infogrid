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
import org.infogrid.model.Test.TestSubjectArea;
import org.infogrid.meshbase.net.proxy.m.MPingPongNetMessageEndpointFactory;
import org.infogrid.probe.manager.store.StoreScheduledExecutorProbeManager;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.probe.shadow.store.StoreShadowMeshBaseFactory;
import org.infogrid.store.prefixing.IterablePrefixingStore;
import org.infogrid.testharness.AbstractTest;
import org.infogrid.util.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Tests that shadows that are not currently in memory are still being updated, in the right sequence.
 */
@RunWith(Parameterized.class)
public class StoreShadowMeshBaseTest5
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
                    AbstractTest.tempInputFileName( StoreShadowMeshBaseTest5.class, "test5-1.xml" ),
                    AbstractTest.tempInputFileName( StoreShadowMeshBaseTest5.class, "test5-2.xml" ),
                    AbstractTest.fileSystemFileName( StoreShadowMeshBaseTest5.class, "StoreShadowMeshBaseTest5_1a.xml" ),
                    AbstractTest.fileSystemFileName( StoreShadowMeshBaseTest5.class, "StoreShadowMeshBaseTest5_2a.xml" ),
                    AbstractTest.fileSystemFileName( StoreShadowMeshBaseTest5.class, "StoreShadowMeshBaseTest5_1b.xml" ),
                    AbstractTest.fileSystemFileName( StoreShadowMeshBaseTest5.class, "StoreShadowMeshBaseTest5_2b.xml" )
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
        copyFile( theTestFile1a, theTestFile1 );
        copyFile( theTestFile2a, theTestFile2 );

        //

        log.info( "accessing test files with meshBase: " + theTestFile1Id.toExternalForm() + ", " + theTestFile2Id.toExternalForm() );
        
        ShadowMeshBase meshBase1 = theProbeManager1.obtainFor(theTestFile1Id, new CoherenceSpecification.Periodic( 8000L ));
        checkObject( meshBase1, "MeshBase1 not created" );
        
        MeshObject home1 = meshBase1.getHomeObject();
        checkObject( home1, "no home object found" );
        checkCondition( !home1.isBlessedBy( TestSubjectArea.AA ), "Home object 1 incorrectly blessed" );
        
        ShadowMeshBase meshBase2 = theProbeManager1.obtainFor(theTestFile2Id, new CoherenceSpecification.Periodic( 12000L ));
        checkObject( meshBase2, "MeshBase2 not created" );
        
        MeshObject home2 = meshBase2.getHomeObject();
        checkObject( home2, "no home object found" );
        checkCondition( !home2.isBlessedBy( TestSubjectArea.AA ), "Home object 2 incorrectly blessed" );
        
        startClock();

        //
        
        log.info( "Checking that Shadow goes away when not referenced" );

        WeakReference<ShadowMeshBase> meshBase1Ref = new WeakReference<ShadowMeshBase>( meshBase1 );
        WeakReference<ShadowMeshBase> meshBase2Ref = new WeakReference<ShadowMeshBase>( meshBase2 );

        meshBase1 = null;
        home1     = null;
        meshBase2 = null;
        home2     = null;
        
        sleepUntilIsGone( meshBase1Ref, 12000L, "ShadowMeshBase1 still here, should have been garbage collected" );
        sleepUntilIsGone( meshBase2Ref, 12000L, "ShadowMeshBase2 still here, should have been garbage collected" );
        
        copyFile(theTestFile1b, theTestFile1 );
        copyFile(theTestFile2b, theTestFile2 );

        //

        log.info( "Sleeping and checking that update has been performed on meshBase1, but not meshBase2" );

        sleepUntil( 10000L );
        
        meshBase1 = theProbeManager1.get(theTestFile1Id );
        checkObject( meshBase1, "MeshBase1 not created" );
        
        home1 = meshBase1.getHomeObject();
        checkObject( home1, "no home object found" );
        checkCondition( home1.isBlessedBy( TestSubjectArea.AA ), "Home object 1 incorrectly not blessed" );

        meshBase2 = theProbeManager1.get(theTestFile2Id );
        checkObject( meshBase2, "MeshBase2 not created" );
        
        home2 = meshBase2.getHomeObject();
        checkObject( home2, "no home object found" );
        checkCondition( !home2.isBlessedBy( TestSubjectArea.AA ), "Home object 2 incorrectly blessed" );
        
        meshBase1 = null;
        home1     = null;
        meshBase2 = null;
        home2     = null;
        
        //

        log.info( "Sleeping and checking that update has been performed on meshBase2 as well" );

        sleepUntil( 14000L );

        meshBase1 = theProbeManager1.get(theTestFile1Id );
        checkObject( meshBase1, "MeshBase1 not created" );
        
        home1 = meshBase1.getHomeObject();
        checkObject( home1, "no home object found" );
        checkCondition( home1.isBlessedBy( TestSubjectArea.AA ), "Home object 1 incorrectly not blessed" );

        meshBase2 = theProbeManager1.get(theTestFile2Id );
        checkObject( meshBase2, "MeshBase2 not created" );
        
        home2 = meshBase2.getHomeObject();
        checkObject( home2, "no home object found" );
        checkCondition( home2.isBlessedBy( TestSubjectArea.AA ), "Home object 2 incorrectly not blessed" );
    }

    /**
     * Constructor with parameters.
     * 
     * @param testFile0 filename of test
     * @param testFile1 filename of test
     * @param testFile2 filename of test
     */
    public StoreShadowMeshBaseTest5(
            String testFile1,
            String testFile2,
            String testFile1a,
            String testFile2a,
            String testFile1b,
            String testFile2b )
    {
        theTestFile1    = testFile1;
        theTestFile2    = testFile2;
        theTestFile1a   = testFile1a;
        theTestFile2a   = testFile2a;
        theTestFile1b   = testFile1b;
        theTestFile2b   = testFile2b;
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

        theTestFile1Id    = theMeshBaseIdentifierFactory.obtain( new File( theTestFile1 ) );
        theTestFile2Id    = theMeshBaseIdentifierFactory.obtain( new File( theTestFile2 ) );

        //
        
        log.info( "Deleting old database and creating new database" );

        theSqlStore.initializeHard();
        
        IterablePrefixingStore theShadowStore      = IterablePrefixingStore.create( "Shadow",      theSqlStore );
        IterablePrefixingStore theShadowProxyStore = IterablePrefixingStore.create( "ShadowProxy", theSqlStore );
        
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
    private static Log log = Log.getLogInstance( StoreShadowMeshBaseTest5.class);

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
     * File name of the first test file.
     */
    protected String theTestFile2;

    /**
     * File name of the second test file.
     */
    protected String theTestFile2a;

    /**
     * File name of the second test file.
     */
    protected String theTestFile2b;

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
