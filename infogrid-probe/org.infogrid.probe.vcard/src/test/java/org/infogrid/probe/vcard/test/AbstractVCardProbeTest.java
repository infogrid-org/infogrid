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

package org.infogrid.probe.vcard.test;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import org.diet4j.core.ModuleRequirement;
import org.diet4j.inclasspath.InClasspathModuleRegistry;
import org.infogrid.meshbase.net.DefaultNetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.NetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.proxy.m.MPingPongNetMessageEndpointFactory;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.modelbase.ModelBaseSingleton;
import org.infogrid.probe.ProbeDirectory.StreamProbeDescriptor;
import org.infogrid.probe.m.MProbeDirectory;
import org.infogrid.probe.manager.PassiveProbeManager;
import org.infogrid.probe.manager.m.MPassiveProbeManager;
import org.infogrid.probe.shadow.m.MShadowMeshBaseFactory;
import org.infogrid.probe.vcard.VCardProbe;
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
 * Factors out common functionality of VCardProbeTests.
 */
public abstract class AbstractVCardProbeTest
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
        registry.resolve( registry.determineSingleResolutionCandidate( ModuleRequirement.create1( "org.infogrid.probe.vcard" ))).activateRecursively();

        Log4jLog.configure( "org/infogrid/probe/vcard/test/Log.properties", AbstractVCardProbeTest.class.getClassLoader() );
        Log.setLogFactory( new Log4jLogFactory());
        
        ResourceHelper.setApplicationResourceBundle( ResourceBundle.getBundle(
                "org/infogrid/probe/vcard/test/ResourceHelper",
                Locale.getDefault(),
                AbstractVCardProbeTest.class.getClassLoader() ));

        theModelBase = ModelBaseSingleton.getSingleton();
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
        theProbeDirectory.addStreamProbe( new StreamProbeDescriptor( "text/x-vcard", VCardProbe.class ));

        MPingPongNetMessageEndpointFactory shadowEndpointFactory = MPingPongNetMessageEndpointFactory.create( exec );

        exec = createThreadPool( 1 );

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
    }

    /**
     * The root context for these tests.
     */
    protected static final Context rootContext = SimpleContext.createRoot( "root-context" );

    /**
     * The ModelBase.
     */
    protected static ModelBase theModelBase;

    /**
     * The ProbeDirectory.
     */
    protected MProbeDirectory theProbeDirectory;
    
    /**
     * Factory for NetMeshBaseIdentifiers.
     */
    protected NetMeshBaseIdentifierFactory theMeshBaseIdentifierFactory = DefaultNetMeshBaseIdentifierFactory.create();

    /**
     * The ProbeManager that we use for the first Probe.
     */
    protected PassiveProbeManager theProbeManager1;

    /**
     * Our ThreadPool.
     */
    protected ScheduledExecutorService exec;

    private static final Log log = Log.getLogInstance( AbstractVCardProbeTest.class );
}


