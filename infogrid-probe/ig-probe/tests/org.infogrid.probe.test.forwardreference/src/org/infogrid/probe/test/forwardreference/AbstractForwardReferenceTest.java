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
// Copyright 1998-2011 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.probe.test.forwardreference;

import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Pattern;
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
import org.infogrid.util.context.Context;
import org.infogrid.util.context.SimpleContext;

/**
 * Provides functionality useful for ForwardReferenceTests.
 */
public abstract class AbstractForwardReferenceTest
        extends
            AbstractTest
{
    /**
     * Constructor.
     *
     * @param testClass the Class to be tested
     * @param fastOrSlow command-line argument indicating whether to run this test with a fast or slow CoherenceSpecification
     * @throws Exception all sorts of things may go wrong in a test
     */
    protected AbstractForwardReferenceTest(
            Class  testClass,
            String fastOrSlow )
        throws
            Exception
    {
        super( localFileName( testClass, "/ResourceHelper" ));

        here = theMeshBaseIdentifierFactory.fromExternalForm( "http://here.local/" ); // this is not going to work for communications
        base = LocalNetMMeshBase.create( here, theModelBase, null, theProbeDirectory, exec, rootContext );

        if( getLog().isDebugEnabled() ) {
            theXprisoMessageLogger = LogXprisoMessageLogger.create( getLog() );
            base.setXprisoMessageLogger( theXprisoMessageLogger );
        }

        if( "slow".equals( fastOrSlow )) {
            theCoherence = CoherenceSpecification.ONE_TIME_ONLY;
            theWait      = false;
        } else {
            theCoherence = CoherenceSpecification.ONE_TIME_ONLY_FAST;
            theWait      = true;
        }
    }

    /**
     * Clean up after the test.
     */
    @Override
    public void cleanup()
    {
        base.die();

        exec.shutdown();
    }

    /**
     * Our ThreadPool.
     */
    protected ScheduledExecutorService exec = createThreadPool( 1 );

    /**
     * The ProbeDirectory.
     */
    protected MProbeDirectory theProbeDirectory = MProbeDirectory.create();

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
    protected static ModelBase theModelBase = ModelBaseSingleton.getSingleton();

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
