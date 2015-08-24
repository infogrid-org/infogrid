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

package org.infogrid.probe.test.forwardreference;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Pattern;
import org.diet4j.core.ModuleRequirement;
import org.diet4j.inclasspath.InClasspathModuleRegistry;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.DefaultNetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.local.m.LocalNetMMeshBase;
import org.infogrid.meshbase.net.schemes.FileScheme;
import org.infogrid.meshbase.net.schemes.HttpScheme;
import org.infogrid.meshbase.net.schemes.Scheme;
import org.infogrid.meshbase.net.schemes.StrictRegexScheme;
import org.infogrid.meshbase.net.xpriso.logging.LogXprisoMessageLogger;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.modelbase.ModelBaseSingleton;
import org.infogrid.probe.m.MProbeDirectory;
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
import org.junit.runners.Parameterized;

/**
 * Provides functionality useful for ForwardReferenceTests.
 */
public abstract class AbstractForwardReferenceTest
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
     * Test parameters.
     * 
     * @return test parameters
     */
    @Parameterized.Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList( new Object[][]
            {
                {
                    true,
                },
                {
                    false,
                }
            });
    }

    /**
     * Constructor with parameters.
     *
     * @param wait if true, wait for the Probe
     * @throws Exception all sorts of things may happen during a test
     */
    public AbstractForwardReferenceTest(
            boolean wait )
        throws
            Exception
    {
        theWait = wait;
        if( wait ) {
            theCoherence = CoherenceSpecification.ONE_TIME_ONLY_FAST;
        } else {
            theCoherence = CoherenceSpecification.ONE_TIME_ONLY;
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
        theProbeDirectory = MProbeDirectory.create();
        exec = createThreadPool( 1 );

        here = theMeshBaseIdentifierFactory.fromExternalForm( "http://here.local/" ); // this is not going to work for communications
        base = LocalNetMMeshBase.create( here, theModelBase, null, theProbeDirectory, exec, rootContext );

        if( getLog().isDebugEnabled() ) {
            theXprisoMessageLogger = LogXprisoMessageLogger.create( getLog() );
            base.setXprisoMessageLogger( theXprisoMessageLogger );
        }
    }

    /**
     * Clean up after the test.
     */
    @After
    public void cleanup()
    {
        base.die();
        base = null;

        exec.shutdown();
        exec = null;
    }

    /**
     * Our ThreadPool.
     */
    protected ScheduledExecutorService exec;

    /**
     * The ProbeDirectory.
     */
    protected MProbeDirectory theProbeDirectory;

    /**
     * Identifier of the main NetMeshBase.
     */
    protected NetMeshBaseIdentifier here;

    /**
     * The main NetMeshBase.
     */
    protected LocalNetMMeshBase base;

    /**
     * The XprisoMessageLogger to use.
     */
    protected LogXprisoMessageLogger theXprisoMessageLogger;

    /**
     * The CoherenceSpecification to use.
     */
    protected CoherenceSpecification theCoherence;

    /**
     * Whether to make the calling thread wait for a while, or not.
     */
    protected boolean theWait;

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
    protected static NetMeshBaseIdentifierFactory theMeshBaseIdentifierFactory = DefaultNetMeshBaseIdentifierFactory.create(
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
}
