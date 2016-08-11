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
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.ScheduledExecutorService;
import org.infogrid.mesh.MeshObject;
import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.MeshBaseDifferencer;
import org.infogrid.meshbase.MeshBaseLifecycleManager;
import org.infogrid.meshbase.m.MMeshBase;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.proxy.m.MPingPongNetMessageEndpointFactory;
import org.infogrid.meshbase.transaction.ChangeSet;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.model.primitives.StringValue;
import org.infogrid.model.Test.TestSubjectArea;
import org.infogrid.probe.m.MProbeDirectory;
import org.infogrid.probe.manager.PassiveProbeManager;
import org.infogrid.probe.manager.m.MPassiveProbeManager;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.probe.shadow.m.MShadowMeshBaseFactory;
import org.infogrid.testharness.AbstractTest;
import org.infogrid.util.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Tests reading file ProbeTest1.xml via the Probe framework.
 */
@RunWith(Parameterized.class)
public class ProbeTest1
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
                    AbstractTest.fileSystemFileName( ProbeTest1.class, "ProbeTest1.xml" )
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
        log.info( "accessing test file with meshBase" );

        ShadowMeshBase meshBase1 = theProbeManager1.obtainFor( testFile1Id, CoherenceSpecification.ONE_TIME_ONLY );

        checkObject( meshBase1, "could not find meshBase1" );
        checkCondition( meshBase1.size() > 1, "meshBase1 is empty" );
        dumpMeshBase( meshBase1, "meshBase1", log );

        //

        log.info( "creating the same data independently" );

        MeshBase meshBase2 = MMeshBase.create(
                theMeshBaseIdentifierFactory.fromExternalForm( PROTOCOL_NAME + "://meshBase2" ),
                theModelBase,
                null,
                rootContext );
        MeshBaseLifecycleManager life2 = meshBase2.getMeshBaseLifecycleManager();

        Transaction tx2 = meshBase2.createTransactionNow();

        Calendar cal = GregorianCalendar.getInstance( TimeZone.getTimeZone( "GMT" ));

        cal.set( 2007, 1-1, 2, 3, 4, 5 ); // month is -1
        cal.set( Calendar.MILLISECOND, 61 );
        long ts1 = cal.getTimeInMillis();

        cal.set( 1999, 12-1, 13, 14, 15, 16 );
        cal.set( Calendar.MILLISECOND, 178 );
        long ts2 = cal.getTimeInMillis();

        cal.set( Calendar.MILLISECOND, 189 );
        long ts3 = cal.getTimeInMillis();

        cal.set( Calendar.MILLISECOND, 190 );
        long ts4 = cal.getTimeInMillis();

        cal.set( Calendar.YEAR, 2009 );
        cal.set( Calendar.MILLISECOND, 191 );
        long ts5 = cal.getTimeInMillis();

        MeshObject objAbc = life2.createMeshObject(
                meshBase2.getMeshObjectIdentifierFactory().fromExternalForm( testFile1Id.getCanonicalForm() + "#abc" ),
                ts1, ts1, ts1, -1L );
        MeshObject objDef = life2.createMeshObject(
                meshBase2.getMeshObjectIdentifierFactory().fromExternalForm( testFile1Id.getCanonicalForm() + "#def" ),
                TestSubjectArea.AA, ts2, ts3, ts4, ts5 );
        objDef.setPropertyValue( TestSubjectArea.A_X, StringValue.create( "My test String." ));
        MeshObject objGhi = life2.createMeshObject(
                meshBase2.getMeshObjectIdentifierFactory().fromExternalForm( testFile1Id.getCanonicalForm() + "#ghi" ));

        objDef.relate( objGhi );
        objGhi.bless( TestSubjectArea.B );

        objDef.blessRelationship( TestSubjectArea.RR.getSource(), objGhi );

        tx2.commitTransaction();

        dumpMeshBase( meshBase2, "meshBase2", log );

        //

        log.info( "diff'ing meshBase1 and meshBase3" );

        MeshBaseDifferencer diff = new MeshBaseDifferencer( meshBase1 );
        ChangeSet changes = diff.determineChangeSet( meshBase2 );

        checkEquals( changes.size(), 2, "more than two changes (2 Home Objects)" );
        if( changes.size() > 1 ) {
            dumpChangeSet( changes, log );
        }
    }

    /**
     * Constructor with parameters.
     *
     * @param testFile1 the test file
     * @throws Exception all sorts of things may happen during a test
     */
    public ProbeTest1(
            String testFile1 )
        throws
            Exception
    {
        testFile1Id = theMeshBaseIdentifierFactory.obtain( new File( testFile1 ) );
    }

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        theProbeDirectory = MProbeDirectory.create();
        exec = createThreadPool( 1 );

        MPingPongNetMessageEndpointFactory shadowEndpointFactory = MPingPongNetMessageEndpointFactory.create( exec );

        MShadowMeshBaseFactory theShadowFactory = MShadowMeshBaseFactory.create(
                theMeshBaseIdentifierFactory,
                shadowEndpointFactory,
                theModelBase,
                rootContext );

        theProbeManager1 = MPassiveProbeManager.create( theShadowFactory, theProbeDirectory );
        shadowEndpointFactory.setNameServer( theProbeManager1.getNetMeshBaseNameServer() );
        theShadowFactory.setProbeManager( theProbeManager1 );
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
    private static Log log = Log.getLogInstance( ProbeTest1.class);

    /**
     * Our ThreadPool.
     */
    protected ScheduledExecutorService exec;

    /**
     * The NetworkIdentifer of the first test file.
     */
    protected NetMeshBaseIdentifier testFile1Id;

    /**
     * The ProbeManager that we use for the first Probe.
     */
    protected PassiveProbeManager theProbeManager1;

    /**
     * The ProbeDirectory to use.
     */
    protected MProbeDirectory theProbeDirectory;
}
