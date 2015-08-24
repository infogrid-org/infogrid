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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Pattern;
import org.diet4j.core.ModuleException;
import org.diet4j.core.ModuleRequirement;
import org.diet4j.inclasspath.InClasspathModuleRegistry;
import org.infogrid.mesh.EntityBlessedAlreadyException;
import org.infogrid.mesh.EntityNotBlessedException;
import org.infogrid.mesh.IllegalPropertyTypeException;
import org.infogrid.mesh.IllegalPropertyValueException;
import org.infogrid.mesh.IsAbstractException;
import org.infogrid.mesh.MeshObjectIdentifierNotUniqueException;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.NotRelatedException;
import org.infogrid.mesh.RelatedAlreadyException;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.DefaultNetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.proxy.m.MPingPongNetMessageEndpointFactory;
import org.infogrid.meshbase.net.schemes.FileScheme;
import org.infogrid.meshbase.net.schemes.HttpScheme;
import org.infogrid.meshbase.net.schemes.Scheme;
import org.infogrid.meshbase.net.schemes.StrictRegexScheme;
import org.infogrid.meshbase.transaction.TransactionException;
import org.infogrid.model.Probe.ProbeSubjectArea;
import org.infogrid.model.Test.TestSubjectArea;
import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.FloatValue;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.modelbase.ModelBaseSingleton;
import org.infogrid.probe.ApiProbe;
import org.infogrid.probe.ProbeDirectory;
import org.infogrid.probe.ProbeException;
import org.infogrid.probe.StagingMeshBase;
import org.infogrid.probe.m.MProbeDirectory;
import org.infogrid.probe.manager.ScheduledExecutorProbeManager;
import org.infogrid.probe.manager.m.MScheduledExecutorProbeManager;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.probe.shadow.m.MShadowMeshBaseFactory;
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
import org.junit.Test;

/**
  * Tests the standard ProbeUpdateCalculator implementations.
  */
public class ProbeUpdateCalculatorTest1
    extends
        AbstractTest
{
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
        registry.resolve( registry.determineSingleResolutionCandidate( ModuleRequirement.create1( "org.infogrid.probe" ))).activateRecursively();
        registry.resolve( registry.determineSingleResolutionCandidate( ModuleRequirement.create1( "org.infogrid.model.Test" ))).activateRecursively();
        
        Log4jLog.configure( "org/infogrid/probe/test/Log.properties", AbstractForwardReferenceTest.class.getClassLoader() );
        Log.setLogFactory( new Log4jLogFactory());
        
        ResourceHelper.setApplicationResourceBundle( ResourceBundle.getBundle(
                "org/infogrid/probe/test/ResourceHelper",
                Locale.getDefault(),
                AbstractForwardReferenceTest.class.getClassLoader() ));

        theModelBase = ModelBaseSingleton.getSingleton();
    }

    /**
     * Run the test.
     *
     * @throws Exception all sorts of things can go wrong during a test
     */
    @Test
    public void run()
        throws
            Exception
    {
        testOne(
                new CoherenceSpecification.Periodic( 3000L ),
                ProbeSubjectArea.PERIODICPROBEUPDATESPECIFICATION,
                new long[] { 0, 3000L, 6000L, 9000L, 12000L, 15000L } );

        testOne(
                new CoherenceSpecification.AdaptivePeriodic( 3000L, 10000L, 1.0f ),
                ProbeSubjectArea.ADAPTIVEPERIODICPROBEUPDATESPECIFICATION,
                new long[] { 0, 3000L, 6000L, 9000L, 12000L, 15000L } );

        float ad = 1.2f;
        testOne(
                new CoherenceSpecification.AdaptivePeriodic( 3000L, 10000, ad ),
                ProbeSubjectArea.ADAPTIVEPERIODICPROBEUPDATESPECIFICATION,
                new long[] {
                    0L,
                    3000L,
                    3000L + (long) (3000f*ad),
                    3000L + (long) (3000f*ad) + (long) (3000f*ad*ad),
                    3000L + (long) (3000f*ad) + (long) (3000f*ad*ad) + (long) (3000f*ad*ad*ad),
                    3000L + (long) (3000f*ad) + (long) (3000f*ad*ad) + (long) (3000f*ad*ad*ad) + (long) (3000f*ad*ad*ad*ad),
                },
                new long[] {
                    0L,
                    3000L,
                    3000L * 2,
                    3000L * 3,
                    3000L * 4,
                    3000L * 5
                } );
    }

    /**
     * Run one test, behavior is expected to be the same for the NoChange and the WithChange cases.
     * 
     * @param coherence the CoherenceSpecification to use when creating the Probe
     * @param homeObjectType the expected EntityType of the home MeshObject
     * @param points time points when to test
     * @throws Exception all sorts of things can go wrong during a test
     */
    protected void testOne(
            CoherenceSpecification coherence,
            EntityType             homeObjectType,
            long []                points )
        throws
            Exception
    {
        testOne( coherence, homeObjectType, points, points );
    }

    /**
     * Run one test. The behavior is expected to be different for the NoChange and the WithChange cases.
     * 
     * @param coherence the CoherenceSpecification to use when creating the Probe
     * @param homeObjectType the expected EntityType of the home MeshObject
     * @param noChangePoints time points when to test
     * @param changePoints time points when to test
     * @throws Exception all sorts of things can go wrong during a test
     */
    protected void testOne(
            CoherenceSpecification coherence,
            EntityType             homeObjectType,
            long []                noChangePoints,
            long []                changePoints )
        throws
            Exception
    {
        theInvokedAt = new ArrayList<Long>();

        MPingPongNetMessageEndpointFactory theShadowEndpointFactory = MPingPongNetMessageEndpointFactory.create( exec );
        
        MShadowMeshBaseFactory noChangeShadowFactory = MShadowMeshBaseFactory.create(
                theMeshBaseIdentifierFactory,
                theShadowEndpointFactory,
                theModelBase,
                rootContext );

        ScheduledExecutorProbeManager noChangeProbeManager = MScheduledExecutorProbeManager.create( noChangeShadowFactory, theProbeDirectory );
        theShadowEndpointFactory.setNameServer( noChangeProbeManager.getNetMeshBaseNameServer() );
        noChangeShadowFactory.setProbeManager( noChangeProbeManager );
        noChangeProbeManager.start( exec );

        log.info( "Starting NoChange test for " + coherence );
        theStartTime = startClock();

        ShadowMeshBase meshBase1 = noChangeProbeManager.obtainFor( theUnchangingDataSource, coherence );

        checkEqualsOutOfSequence(
                meshBase1.getHomeObject().getTypes(),
                new EntityType[] { homeObjectType },
                "wrong home object type for unchanging probe" );

        Thread.sleep( noChangePoints[ noChangePoints.length-1 ] + 1000L ); // a bit longer than needed
        noChangeProbeManager.remove( theUnchangingDataSource );

        checkInMarginRange( copyIntoNewLongArray( theInvokedAt ), noChangePoints, 500L, 0.1f, getStartTime(), "Out of range" );

        //
        
        theInvokedAt = new ArrayList<Long>();

        MShadowMeshBaseFactory changeShadowFactory = MShadowMeshBaseFactory.create(
                theMeshBaseIdentifierFactory,
                theShadowEndpointFactory,
                theModelBase,
                rootContext );

        ScheduledExecutorProbeManager changeProbeManager = MScheduledExecutorProbeManager.create( noChangeShadowFactory, theProbeDirectory );
        theShadowEndpointFactory.setNameServer( changeProbeManager.getNetMeshBaseNameServer() );
        changeShadowFactory.setProbeManager( changeProbeManager );
        changeProbeManager.start( exec );

        log.info( "Starting WithChange test for " + coherence );
        theStartTime = startClock();

        ShadowMeshBase meshBase2 = changeProbeManager.obtainFor( theChangingDataSource, coherence );

        checkEqualsOutOfSequence(
                meshBase2.getHomeObject().getTypes(),
                new EntityType[] { homeObjectType, TestSubjectArea.AA },
                "wrong home object types for changing probe" );

        Thread.sleep( changePoints[ changePoints.length-1 ] + 1000L ); // a bit longer than needed
        changeProbeManager.remove( theChangingDataSource );

        checkInMarginRange( copyIntoNewLongArray( theInvokedAt ), changePoints, 500L, 0.1f, getStartTime(), "Out of range" );

    }

    /**
     * Helper method to copy an ArrayList of Long into a long array.
     * 
     * @param data the ArrayList of Long
     * @return the array of long
     */
    protected static long [] copyIntoNewLongArray(
            ArrayList<Long> data )
    {
        long [] ret = new long[ data.size() ];
        for( int i=0 ; i<ret.length ; ++i ) {
            ret[i] = data.get( i );
        }
        return ret;
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
        exec = createThreadPool( 1 );

        theChangingDataSource   = theMeshBaseIdentifierFactory.fromExternalForm( "test://here.local/change" );
        theUnchangingDataSource = theMeshBaseIdentifierFactory.fromExternalForm( "test://here.local/nochange" );

        theProbeDirectory.addExactUrlMatch( new ProbeDirectory.ExactMatchDescriptor(
                theUnchangingDataSource.toExternalForm(),
                UnchangingProbe.class ));

        theProbeDirectory.addExactUrlMatch( new ProbeDirectory.ExactMatchDescriptor(
                theChangingDataSource.toExternalForm(),
                ChangingProbe.class ));
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
    private static Log log = Log.getLogInstance( ProbeUpdateCalculatorTest1.class );

    protected static ArrayList<Long> theInvokedAt;
    protected static long theStartTime;

    protected NetMeshBaseIdentifier theChangingDataSource;
    protected NetMeshBaseIdentifier theUnchangingDataSource;

    /**
     * The root context for these tests.
     */
    protected static final Context rootContext = SimpleContext.createRoot( "root-context" );

    /**
     * The test protocol. In the real world this would be something like "jdbc".
     */
    protected static final String PROTOCOL_NAME = "test";

    /**
     * Factory for NetMeshBaseIdentifiers.
     */
    protected static final NetMeshBaseIdentifierFactory theMeshBaseIdentifierFactory = DefaultNetMeshBaseIdentifierFactory.create(
            new Scheme [] {
                    new HttpScheme(),
                    new FileScheme(),
                    new StrictRegexScheme( PROTOCOL_NAME, Pattern.compile( PROTOCOL_NAME + ":.*" ))
             } );

    /**
     * The ModelBase.
     */
    protected static ModelBase theModelBase;

    /**
     * Our ThreadPool.
     */
    protected ScheduledExecutorService exec;

    /**
     * The ProbeDirectory.
     */
    protected MProbeDirectory theProbeDirectory;


    /**
     * The first test Probe.
     */
    public static class UnchangingProbe
            implements
                ApiProbe
    {
        public void readFromApi(
                NetMeshBaseIdentifier  networkId,
                CoherenceSpecification coherence,
                StagingMeshBase        mb )
            throws
                IsAbstractException,
                EntityBlessedAlreadyException,
                EntityNotBlessedException,
                RelatedAlreadyException,
                NotRelatedException,
                MeshObjectIdentifierNotUniqueException,
                IllegalPropertyTypeException,
                IllegalPropertyValueException,
                TransactionException,
                NotPermittedException,
                ProbeException,
                IOException,
                ModuleException,
                URISyntaxException
        {
            long now = System.currentTimeMillis();
            if( log.isTraceEnabled() ) {
                log.traceMethodCallEntry( this, "readFromApi" );
            }
            theInvokedAt.add( now );
            return; // do nothing
        }
    }

    /**
     * The second test Probe.
     */
    public static class ChangingProbe
            implements
                ApiProbe
    {
        public void readFromApi(
                NetMeshBaseIdentifier  networkId,
                CoherenceSpecification coherence,
                StagingMeshBase        mb )
            throws
                IsAbstractException,
                EntityBlessedAlreadyException,
                EntityNotBlessedException,
                RelatedAlreadyException,
                NotRelatedException,
                MeshObjectIdentifierNotUniqueException,
                IllegalPropertyTypeException,
                IllegalPropertyValueException,
                TransactionException,
                NotPermittedException,
                ProbeException,
                IOException,
                ModuleException,
                URISyntaxException
        {
            long now = System.currentTimeMillis();
            if( log.isTraceEnabled() ) {
                log.traceMethodCallEntry( this, "readFromApi" );
            }

            theInvokedAt.add( now );

            mb.getHomeObject().bless( TestSubjectArea.AA );
            mb.getHomeObject().setPropertyValue( TestSubjectArea.AA_Y, FloatValue.create( System.currentTimeMillis() )); // simple thing that is always different
        }
    }
}
