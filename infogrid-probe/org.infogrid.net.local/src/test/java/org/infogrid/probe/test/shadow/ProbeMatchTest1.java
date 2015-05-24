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
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Pattern;
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
import org.infogrid.model.primitives.StringValue;
import org.infogrid.model.Test.TestSubjectArea;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.modelbase.ModelBaseSingleton;
import org.infogrid.module.ModuleException;
import org.infogrid.module.inclasspath.InClasspathModuleRegistry;
import org.infogrid.probe.ApiProbe;
import org.infogrid.probe.ProbeDirectory;
import org.infogrid.probe.ProbeException;
import org.infogrid.probe.StagingMeshBase;
import org.infogrid.probe.m.MProbeDirectory;
import org.infogrid.probe.manager.PassiveProbeManager;
import org.infogrid.probe.manager.m.MPassiveProbeManager;
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
 * Tests exact and pattern-based URL matching.
 */
public class ProbeMatchTest1
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
        registry.resolve( registry.getModuleMetaFor( "org.infogrid.probe" )).activateRecursively();
        registry.resolve( registry.getModuleMetaFor( "org.infogrid.model.Test" )).activateRecursively();
        
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
        log.info( "Configuring ProbeDirectory" );
        
        NetMeshBaseIdentifier id1 = theMeshBaseIdentifierFactory.fromExternalForm( PROTOCOL_NAME + "://foo.com/bar" );
        NetMeshBaseIdentifier id2 = theMeshBaseIdentifierFactory.fromExternalForm( PROTOCOL_NAME + "://foo.com/bar?abc=def" );
        NetMeshBaseIdentifier id3 = theMeshBaseIdentifierFactory.fromExternalForm( PROTOCOL_NAME + "://foo.com/bar?ghi=klm" );

        theProbeDirectory.addExactUrlMatch( new ProbeDirectory.ExactMatchDescriptor(
                id1.toExternalForm(),
                TestProbe1.class ));

        theProbeDirectory.addExactUrlMatch( new ProbeDirectory.ExactMatchDescriptor(
                id2.toExternalForm(),
                TestProbe2.class ));

        theProbeDirectory.addPatternUrlMatch( new ProbeDirectory.PatternMatchDescriptor(
                Pattern.compile( PROTOCOL_NAME + "://foo.com/bar(.*)" ),
                TestProbe3.class ));

        //
        
        log.info( "Running the Probes and checking" );

        ShadowMeshBase meshBase1 = theProbeManager1.obtainFor( id1, CoherenceSpecification.ONE_TIME_ONLY );        
        checkEquals( meshBase1.getHomeObject().getPropertyValue( TestSubjectArea.A_X ), StringValue.create( TestProbe1.class.getName()), "Wrong probe invoked for " + id1 );

        ShadowMeshBase meshBase2 = theProbeManager1.obtainFor( id2, CoherenceSpecification.ONE_TIME_ONLY );        
        checkEquals( meshBase2.getHomeObject().getPropertyValue( TestSubjectArea.A_X ), StringValue.create( TestProbe2.class.getName()), "Wrong probe invoked for " + id2 );

        ShadowMeshBase meshBase3 = theProbeManager1.obtainFor( id3, CoherenceSpecification.ONE_TIME_ONLY );        
        checkEquals( meshBase3.getHomeObject().getPropertyValue( TestSubjectArea.A_X ), StringValue.create( TestProbe3.class.getName()), "Wrong probe invoked for " + id3 );        
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
        theProbeManager1 = null;

        exec.shutdown();
        exec = null;
    }

    // Our Logger
    private static Log log = Log.getLogInstance( ProbeMatchTest1.class );

    /**
     * The root context for these tests.
     */
    protected static final Context rootContext = SimpleContext.createRoot( "root-context" );

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
     * The ProbeManager that we use for the first Probe.
     */
    protected PassiveProbeManager theProbeManager1;
    
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
     * The abstract test Probe. This needs to be declared public otherwise the Probe framework cannot access it.
     */
    public static abstract class AbstractTestProbe
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
            mb.getHomeObject().bless( TestSubjectArea.AA );
            mb.getHomeObject().setPropertyValue( TestSubjectArea.A_X, StringValue.create( getClass().getName() ));
        }
    }

    /**
     * Test Probe 1. This is just renamed from AbstractTestProbe.
     */
    public static class TestProbe1
            extends
                AbstractTestProbe
    {}
    
    /**
     * Test Probe 2. This is just renamed from AbstractTestProbe.
     */
    public static class TestProbe2
            extends
                AbstractTestProbe
    {}

    /**
     * Test Probe 3. This is just renamed from AbstractTestProbe.
     */
    public static class TestProbe3
            extends
                AbstractTestProbe
    {}
}
