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

package org.infogrid.probe.test;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.IterableNetMeshBaseDifferencer;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.transaction.ChangeSet;
import org.infogrid.meshbase.net.proxy.m.MPingPongNetMessageEndpointFactory;
import org.infogrid.meshbase.net.transaction.NetMeshObjectPropertyChangeEvent;
import org.infogrid.probe.m.MProbeDirectory;
import org.infogrid.probe.manager.PassiveProbeManager;
import org.infogrid.probe.manager.m.MPassiveProbeManager;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.probe.shadow.m.MShadowMeshBaseFactory;
import static org.infogrid.probe.test.AbstractProbeTest.theMeshBaseIdentifierFactory;
import org.infogrid.testharness.AbstractTest;
import org.infogrid.util.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Tests running Probes manually, tracking the changes of a data source correctly.
 */
@RunWith(Parameterized.class)
public class ProbeTest2
        extends
            AbstractProbeTest
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
                    AbstractTest.tempInputFileName( ProbeTest2.class, "test2-active.xml" ),
                    AbstractTest.fileSystemFileName( ProbeTest2.class, "ProbeTest2_1.xml" ),
                    AbstractTest.fileSystemFileName( ProbeTest2.class, "ProbeTest2_2.xml" )
                }
        });
    }

    /**
     * Run the test.
     *
     * @throws Exception all sorts of things may happen during a test
     */
    @Test
    public void run()
        throws
            Exception
    {
        copyFile(theTestFile1, theTestFile0 );

        log.info( "accessing test file 1 with meshBaseA" );
        
        ShadowMeshBase meshBaseA = theProbeManagerA.obtainFor(theTestFile0Id, CoherenceSpecification.ONE_TIME_ONLY );

            checkObject( meshBaseA, "could not find meshBaseA" );
            checkCondition( meshBaseA.size() > 1, "meshBaseA is empty" );
            meshBaseA.addWeakShadowListener( listenerA );
            dumpMeshBase( meshBaseA, "meshBaseA", log );

        sleepFor( 1001L ); // make sure time advances even on virtualized machines

        //

        log.info( "accessing test file 1 with meshBaseB" );
        
        ShadowMeshBase meshBaseB = theProbeManagerB.obtainFor(theTestFile0Id, CoherenceSpecification.ONE_TIME_ONLY );

            checkObject( meshBaseB, "could not find meshBaseB" );
            checkCondition( meshBaseB.size() > 1, "meshBaseB is empty" );
            meshBaseB.addWeakShadowListener( listenerB );
            dumpMeshBase( meshBaseB, "meshBaseB", log );

        //
        
        log.info( "diff'ing meshBaseA and meshBaseB -- should be the exact same, we read the same file" );

        IterableNetMeshBaseDifferencer diff_A_B       = new IterableNetMeshBaseDifferencer( meshBaseA );
        ChangeSet                      firstChangeSet = diff_A_B.determineChangeSet( meshBaseB );

            checkEquals( firstChangeSet.size(), 1, "not the same content" );
            checkCondition( firstChangeSet.getChange( 0 ) instanceof NetMeshObjectPropertyChangeEvent, "wrong change type" );
            checkEquals(
                    ((NetMeshObjectPropertyChangeEvent)firstChangeSet.getChange( 0 )).getPropertyTypeIdentifier().toExternalForm(),
                    "org.infogrid.model.Probe/ProbeUpdateSpecification_LastProbeRun",
                    "Wrong property changed" );
            if( firstChangeSet.size() > 1 ) {
                dumpChangeSet( firstChangeSet, log );
            }

        sleepFor( 1001L ); // make sure time advances even on virtualized machines

        //

        copyFile(theTestFile2, theTestFile0 );
        
        log.info( "updating meshBaseB to read file 2" );

        meshBaseB.doUpdateNow();
            checkCondition( meshBaseB.size() > 1, "meshBaseB is empty" );
            dumpMeshBase( meshBaseB, "meshBaseB", log );

        //

        log.info( "diff'ing meshBaseA and meshBaseB -- they are now different" );

        ChangeSet secondChangeSet = diff_A_B.determineChangeSet( meshBaseB );

            checkEquals( secondChangeSet.size(), 3, "there should be exactly three differences: " + secondChangeSet ); // ProbeUpdateCounter, CreatedEvent
            if( secondChangeSet.size() > 0 ) {
                dumpChangeSet( secondChangeSet, log );
            }

        sleepFor( 1001L ); // make sure time advances even on virtualized machines

        //

        copyFile(theTestFile1, theTestFile0 );

        log.info( "updating meshBaseB to read file 1 again" );

        meshBaseB.doUpdateNow();
            checkCondition( meshBaseB.size() > 1, "meshBaseB is empty" );
            dumpMeshBase( meshBaseB, "meshBaseB", log );

        //

        log.info( "diff'ing meshBaseA and meshBaseB" );

        ChangeSet thirdChangeSet = diff_A_B.determineChangeSet( meshBaseB );

            checkEquals( thirdChangeSet.size(), 2, "not the same content" );
            // ProbeUpdateCounter
            // LastProbeRun
            if( thirdChangeSet.size() > 0 ) {
                dumpChangeSet( thirdChangeSet, log );
            }

        //

        checkEquals( listenerA.size(), 0, "wrong number of events in listenerA" );
        checkEquals( listenerB.size(), 4, "wrong number of events in listenerA" );

        if( log.isDebugEnabled() ) {

            // log.traceMethodCallEntry( listenerA.toString() );
            // log.traceMethodCallEntry( listenerB.toString() );
        }
    }

    /**
     * Constructor with parameters.
     *
     * @param testFile0 test file
     * @param testFile1 test file
     * @param testFile2 test file
     * @throws Exception all sorts of things may happen during a test
     */
    public ProbeTest2(
            String testFile0,
            String testFile1,
            String testFile2 )
        throws
            Exception
    {
        theTestFile0 = testFile0;
        theTestFile1 = testFile1;
        theTestFile2 = testFile2;
        
        theTestFile0Id = theMeshBaseIdentifierFactory.obtain( new File( testFile0 ) );
        theTestFile1Id = theMeshBaseIdentifierFactory.obtain( new File( testFile1 ) );
        theTestFile2Id = theMeshBaseIdentifierFactory.obtain( new File( testFile2 ) );
    }
    
    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        theProbeDirectory = MProbeDirectory.create();
        exec = createThreadPool( 1 );
                
        listenerA = new ProbeTestShadowListener( "A" );
        listenerB = new ProbeTestShadowListener( "B" );

        MPingPongNetMessageEndpointFactory shadowEndpointFactoryA = MPingPongNetMessageEndpointFactory.create( exec );
        MPingPongNetMessageEndpointFactory shadowEndpointFactoryB = MPingPongNetMessageEndpointFactory.create( exec );

        MShadowMeshBaseFactory shadowFactoryA = MShadowMeshBaseFactory.create(
                theMeshBaseIdentifierFactory,
                shadowEndpointFactoryA,
                theModelBase,
                rootContext );
        MShadowMeshBaseFactory shadowFactoryB = MShadowMeshBaseFactory.create(
                theMeshBaseIdentifierFactory,
                shadowEndpointFactoryB,
                theModelBase,
                rootContext );
        
        theProbeManagerA = MPassiveProbeManager.create( shadowFactoryA, theProbeDirectory );
        theProbeManagerB = MPassiveProbeManager.create( shadowFactoryB, theProbeDirectory );

        shadowEndpointFactoryA.setNameServer( theProbeManagerA.getNetMeshBaseNameServer() );
        shadowEndpointFactoryB.setNameServer( theProbeManagerB.getNetMeshBaseNameServer() );

        shadowFactoryA.setProbeManager( theProbeManagerA );
        shadowFactoryB.setProbeManager( theProbeManagerB );
    }

    /**
     * Clean up after the test.
     */
    @After
    public void cleanup()
    {
        theProbeManagerA.die( true );
        theProbeManagerB.die( true );

        theProbeManagerA = null;
        theProbeManagerB = null;

        exec.shutdown();
        exec = null;
    }

    // Our Logger
    private static Log log = Log.getLogInstance( ProbeTest2.class);

    /**
     * The ProbeDirectory to use.
     */
    protected MProbeDirectory theProbeDirectory;

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
     * The ProbeManager that we use for the first Probe.
     */
    protected PassiveProbeManager theProbeManagerA;

    /**
     * The ProbeManager that we use for the second Probe.
     */
    protected PassiveProbeManager theProbeManagerB;
    
    /**
     * First listener.
     */
    protected ProbeTestShadowListener listenerA;
    
    /**
     * Second listener.
     */
    protected ProbeTestShadowListener listenerB;
}
