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

package org.infogrid.probe.test.writableprobe;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Pattern;
import org.diet4j.core.ModuleRequirement;
import org.diet4j.inclasspath.InClasspathModuleRegistry;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.meshbase.net.DefaultNetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.local.m.LocalNetMMeshBase;
import org.infogrid.meshbase.net.schemes.FileScheme;
import org.infogrid.meshbase.net.schemes.HttpScheme;
import org.infogrid.meshbase.net.schemes.Scheme;
import org.infogrid.meshbase.net.schemes.StrictRegexScheme;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.modelbase.ModelBaseSingleton;
import org.infogrid.probe.ApiProbe;
import org.infogrid.probe.ProbeDirectory;
import org.infogrid.probe.m.MProbeDirectory;
import org.infogrid.probe.shadow.ShadowMeshBase;
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

/**
 * Provides a test framework that makes it easier to test WritableProbes systematically.
 */
public abstract class AbstractWritableProbeTest
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
        
        Log4jLog.configure( "org/infogrid/probe/test/Log.properties", AbstractWritableProbeTest.class.getClassLoader() );
        Log.setLogFactory( new Log4jLogFactory());
        
        ResourceHelper.setApplicationResourceBundle( ResourceBundle.getBundle(
                "org/infogrid/probe/test/ResourceHelper",
                Locale.getDefault(),
                AbstractWritableProbeTest.class.getClassLoader() ));

        theModelBase = ModelBaseSingleton.getSingleton();
    }

    /**
     * Run the tests.
     *
     * @param testCases the test cases to run
     * @throws Exception all sorts of things can go wrong during a test
     */
    protected void run(
            WritableProbeTestCase [] testCases )
        throws
            Exception
    {
        Log myLog = Log.getLogInstance( getClass() ); // find right subclass logger

        for( int i=0 ; i<testCases.length ; ++i ) {

            myLog.info( "About to run TestCase " + i + ": " + testCases[i].theProbeClass.getName() );

            LocalNetMMeshBase base = null;
            try {
                // set up ProbeDirectory
                MProbeDirectory theProbeDirectory = MProbeDirectory.create();

                theProbeDirectory.addExactUrlMatch(
                        new ProbeDirectory.ExactMatchDescriptor(
                                test1_URL.toExternalForm(),
                                testCases[i].theProbeClass ));

                // create MeshBase and run Probe
                base = LocalNetMMeshBase.create( here, theModelBase, null, theProbeDirectory, exec, rootContext );

                myLog.info( "Performing accessLocally" );

                NetMeshObject shadowHomeInMain = base.accessLocally( test1_URL );
                checkObject( shadowHomeInMain, "could not find shadow's home object in main MeshBase" );

                ShadowMeshBase shadow = base.getShadowMeshBaseFor( test1_URL );
                checkObject( shadow, "could not find shadow" );
                
                testCases[i].afterFirstRun( base, shadow, shadowHomeInMain );
                
                Thread.sleep( PINGPONG_ROUNDTRIP_DURATION );

                // updating
                myLog.info( "Now doing a manual update" );

                shadow.doUpdateNow();

                testCases[i].afterSecondRun( base, shadow, shadowHomeInMain );

            } catch( Throwable ex ) {
                reportError( "Test " + i + " failed", ex );
                System.exit( 1 );

            } finally {
                if( base != null ) {
                    base.die( true );
                }
            }
        }
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
        exec = createThreadPool( 1 );

    }

    /**
     * Cleanup.
     */
    @After
    public void cleanup()
    {
        exec.shutdown();
        exec = null;
    }

    // Our Logger
    private static final Log log = Log.getLogInstance( AbstractWritableProbeTest.class  );

    /**
     * Our ThreadPool.
     */
    protected ScheduledExecutorService exec;
    
    /**
     * The root context for these tests.
     */
    protected static final Context rootContext = SimpleContext.createRoot( "root-context" );

    /**
     * The ModelBase.
     */
    protected static ModelBase theModelBase;

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
     * Expected duration within which at least one ping-pong round trip can be completed.
     * Milliseconds.
     */
    protected static final long PINGPONG_ROUNDTRIP_DURATION = 100L;

    /**
     * The identifier of the main NetMeshBase.
     */
    protected static final NetMeshBaseIdentifier here;
    static {
        NetMeshBaseIdentifier temp;
        try {
            temp = theMeshBaseIdentifierFactory.fromExternalForm( "http://here.local/" ); // this is not going to work for communications
        } catch( Exception ex ) {
            log.error( ex );
            temp = null; // make compiler happy
        }
        here = temp;
    }

    /**
     * The first URL that we are accessing.
     */
    protected static final NetMeshBaseIdentifier test1_URL;
    static {
        NetMeshBaseIdentifier temp;
        try {
            temp = theMeshBaseIdentifierFactory.fromExternalForm( PROTOCOL_NAME + "://shadow.some.where/one" );
        } catch( Exception ex ) {
            log.error( ex );
            temp = null; // make compiler happy
        }
        test1_URL = temp;
    }

    /**
     * A TestCase for WritableProbes.
     */
    public static abstract class WritableProbeTestCase
    {
        /**
         * Constructor.
         * 
         * @param clazz the Probe class to test
         */
        public WritableProbeTestCase(
                Class<? extends ApiProbe> clazz )
        {
            theProbeClass = clazz;
        }
        
        /**
         * Invoked after the first Probe run has been completed.
         * 
         * @param mainBase the main NetMeshBase
         * @param shadow the ShadowMeshBase into which the Probe has been processed
         * @param shadowHomeInMain the ShadowMeshBase's home object, as replicated in the main MeshBase
         * @throws Exception all kinds of things can go wrong in a test
         */
        public abstract void afterFirstRun(
                NetMeshBase    mainBase,
                ShadowMeshBase shadow,
                NetMeshObject  shadowHomeInMain )
            throws
                Exception;

        /**
         * Invoked after the second Probe run has been completed.
         * 
         * @param mainBase the main NetMeshBase
         * @param shadow the ShadowMeshBase into which the Probe has been processed
         * @param shadowHomeInMain the ShadowMeshBase's home object, as replicated in the main MeshBase
         * @throws Exception all kinds of things can go wrong in a test
         */
        public abstract void afterSecondRun(
                NetMeshBase    mainBase,
                ShadowMeshBase shadow,
                NetMeshObject  shadowHomeInMain )
            throws
                Exception;

   
        /**
         * The Probe class to test.
         */
        protected Class<? extends ApiProbe> theProbeClass;
    }
}
