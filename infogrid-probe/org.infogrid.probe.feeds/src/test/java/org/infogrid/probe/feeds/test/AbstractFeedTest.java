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

package org.infogrid.probe.feeds.test;

import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import org.diet4j.core.ModuleRegistry;
import org.diet4j.core.ModuleRequirement;
import org.diet4j.inclasspath.InClasspathModuleRegistry;
import org.infogrid.mesh.MeshObject;
import org.infogrid.meshbase.IterableMeshBase;
import org.infogrid.meshbase.net.DefaultNetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.IterableNetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.proxy.m.MPingPongNetMessageEndpointFactory;
import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.PropertyType;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.modelbase.ModelBaseSingleton;
import org.infogrid.probe.m.MProbeDirectory;
import org.infogrid.probe.manager.PassiveProbeManager;
import org.infogrid.probe.manager.m.MPassiveProbeManager;
import org.infogrid.probe.shadow.m.MShadowMeshBaseFactory;
import org.infogrid.testharness.AbstractTest;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.logging.Log;
import org.infogrid.util.context.Context;
import org.infogrid.util.context.SimpleContext;
import org.infogrid.util.logging.log4j.Log4jLog;
import org.infogrid.util.logging.log4j.Log4jLogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Factors out common behaviors of the feed tests.
 */
public abstract class AbstractFeedTest
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
        ClassLoader    cl       = AbstractFeedTest.class.getClassLoader();
        ModuleRegistry registry = InClasspathModuleRegistry.instantiateOrGet( cl );

        registry.resolve( registry.determineSingleResolutionCandidate( ModuleRequirement.create( "org.infogrid", "org.infogrid.probe.feeds" ))).activateRecursively();
        registry.resolve( registry.determineSingleResolutionCandidate( ModuleRequirement.create( "org.infogrid", "org.infogrid.model.Test" ))).activateRecursively();
        
        Log4jLog.configure( "org/infogrid/probe/feeds/test/Log.properties", cl );
        Log.setLogFactory( new Log4jLogFactory());
        
        ResourceHelper.setApplicationResourceBundle( ResourceBundle.getBundle(
                "org/infogrid/probe/feeds/test/ResourceHelper",
                Locale.getDefault(),
                cl ));

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
     * Helper method to count MeshObjects.
     *
     * @param base the MeshBase containing th MeshObjects
     * @param mylog if given, log the found objects there
     * @return the number of MeshObjects in the MeshBase
     */
    protected static int countMeshObjects(
            IterableNetMeshBase base,
            Log                 mylog )
    {
        int ret = countFromIterator( base.iterator(), mylog );
        return ret;
    }

    /**
     * Count the number of Objects found by iterating over an Iterator.
     *
     * @param iter the Iterator
     * @param mylog if given, log the found objects there
     * @return the number of Objects found
     */
    public static int countFromIterator(
            Iterator<?> iter,
            Log         mylog )
    {
        int ret = 0;
        StringBuilder buf = new StringBuilder(); // do this instead of logging directly, that way we don't changing the threading behavior
        while( iter.hasNext() ) {
            Object current = iter.next();

            ++ret;
            buf.append( "found " ).append( current );
        }

        if( mylog != null ) {
            mylog.info( "found " + ret + " elements:\n" + buf );
        }
        return ret;
    }

    /**
     * Dump the content of a MeshBase to log.debug().
     *
     * @param mb the MeshBase whose content we want to dump
     * @param prefix a string to prepend
     * @param mylog the Log to dump to
     * @throws Exception all sorts of things may go wrong during a test
     */
    protected final void dumpMeshBase(
            IterableMeshBase mb,
            String           prefix,
            Log              mylog )
        throws
            Exception
    {
        if( mylog.isDebugEnabled() ) {
            StringBuilder buf = new StringBuilder( prefix );
            for( MeshObject current : mb ) {
                buf.append( "\n  " );
                buf.append( current.getIdentifier() );
                buf.append( " (created: " );
                buf.append( current.getTimeCreated() );
                buf.append( " updated: " );
                buf.append( current.getTimeUpdated() );
                buf.append( " read: " );
                buf.append( current.getTimeRead() );
                buf.append( ")" );

                if( true ) {
                    buf.append( "\n    Types:" );
                    for( EntityType et : current.getTypes() ) {
                        buf.append( "\n        " );
                        buf.append( et.getName().value() );
                    }
                }
                if( true ) {
                    buf.append( "\n    Properties:" );
                    for( PropertyType pt : current.getAllPropertyTypes() ) {
                        buf.append( "\n        " );
                        buf.append( pt.getName().value() );
                        buf.append( ": " );
                        buf.append( current.getPropertyValue( pt ));
                    }
                }
            }
            mylog.debug( buf.toString() );
        }
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
    protected MProbeDirectory theProbeDirectory = MProbeDirectory.create();
    
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
    protected ScheduledExecutorService exec = createThreadPool( 1 );
}
